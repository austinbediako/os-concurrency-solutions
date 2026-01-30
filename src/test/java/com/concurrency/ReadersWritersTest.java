package com.concurrency;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ReadersWritersTest {

    @Nested
    class MonitorBehavior {
        @Test
        public void testWriterWaitsForReaders() throws Exception {
            ReadersWritersMonitor monitor = new ReadersWritersMonitor(new PerformanceMetrics());

            CountDownLatch readerEntered = new CountDownLatch(1);
            CountDownLatch readerContinue = new CountDownLatch(1);
            CountDownLatch writerAcquired = new CountDownLatch(1);

            Thread reader = new Thread(() -> {
                try {
                    monitor.startRead("R");
                    readerEntered.countDown();
                    // hold the read lock until test signals
                    readerContinue.await();
                    monitor.endRead("R");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            Thread writer = new Thread(() -> {
                try {
                    // attempt to acquire write lock (should block until reader ends)
                    monitor.startWrite("W");
                    writerAcquired.countDown();
                    monitor.endWrite("W");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            reader.start();
            // wait for reader to enter
            assertTrue(readerEntered.await(1, TimeUnit.SECONDS), "Reader did not enter in time");

            writer.start();

            // writer should NOT acquire the write lock while reader holds it
            assertFalse(writerAcquired.await(500, TimeUnit.MILLISECONDS), "Writer acquired lock while reader was active");

            // allow reader to finish
            readerContinue.countDown();

            // now writer should acquire the lock
            assertTrue(writerAcquired.await(2, TimeUnit.SECONDS), "Writer did not acquire lock after reader finished");

            // cleanup
            reader.interrupt();
            writer.interrupt();
            reader.join(1000);
            writer.join(1000);
        }
    }

    @Nested
    class ConcurrencyBehavior {
        @Test
        public void testMultipleReadersConcurrent() throws Exception {
            ReadersWritersMonitor monitor = new ReadersWritersMonitor(new PerformanceMetrics());

            final int readersCount = 3;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch enteredLatch = new CountDownLatch(readersCount);
            AtomicInteger current = new AtomicInteger(0);
            AtomicInteger max = new AtomicInteger(0);

            ExecutorService ex = Executors.newFixedThreadPool(readersCount);
            for (int i = 0; i < readersCount; i++) {
                ex.submit(() -> {
                    try {
                        startLatch.await();
                        monitor.startRead("R");
                        int c = current.incrementAndGet();
                        max.updateAndGet(prev -> Math.max(prev, c));
                        enteredLatch.countDown();
                        Thread.sleep(200);
                        current.decrementAndGet();
                        monitor.endRead("R");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            // Trigger all readers to attempt to start simultaneously
            startLatch.countDown();

            // Wait for all readers to enter their read section
            assertTrue(enteredLatch.await(2, TimeUnit.SECONDS), "Readers did not enter in time");

            // All readers should have been concurrent at peak
            assertEquals(readersCount, max.get(), "Not all readers were concurrent");

            ex.shutdownNow();
            ex.awaitTermination(1, TimeUnit.SECONDS);
        }

        @Test
        public void testNewReadersAllowedWhileWriterWaiting() throws Exception {
            ReadersWritersMonitor monitor = new ReadersWritersMonitor(new PerformanceMetrics());

            CountDownLatch readerEntered = new CountDownLatch(1);
            CountDownLatch writerTrying = new CountDownLatch(1);
            CountDownLatch secondReaderEntered = new CountDownLatch(1);

            Thread r1 = new Thread(() -> {
                try {
                    monitor.startRead("R1");
                    readerEntered.countDown();
                    Thread.sleep(500);
                    monitor.endRead("R1");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            Thread writer = new Thread(() -> {
                try {
                    // ensure writer attempts after first reader started
                    readerEntered.await();
                    writerTrying.countDown();
                    monitor.startWrite("W");
                    monitor.endWrite("W");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            Thread r2 = new Thread(() -> {
                try {
                    // wait until writer is trying to acquire lock
                    writerTrying.await();
                    monitor.startRead("R2");
                    secondReaderEntered.countDown();
                    monitor.endRead("R2");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            r1.start();
            assertTrue(readerEntered.await(1, TimeUnit.SECONDS));

            writer.start();
            assertTrue(writerTrying.await(1, TimeUnit.SECONDS));

            r2.start();

            // Current monitor allows new readers to enter even while a writer is waiting
            assertTrue(secondReaderEntered.await(1, TimeUnit.SECONDS), "Second reader could not enter while writer waiting");

            r1.interrupt();
            writer.interrupt();
            r2.interrupt();
            r1.join(1000);
            writer.join(1000);
            r2.join(1000);
        }

        @Disabled("Requires writer-priority monitor implementation")
        @Test
        public void testWriterPriorityVariant() {
            // Placeholder for a writer-priority monitor test
        }
    }
}

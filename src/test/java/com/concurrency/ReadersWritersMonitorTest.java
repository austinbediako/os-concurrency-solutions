package com.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadersWritersMonitorTest {

    @Test
    public void testWriterWaitsForReaders() throws Exception {
        ReadersWritersMonitor monitor = new ReadersWritersMonitor();

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

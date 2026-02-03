package com.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

import com.concurrency.gui.SimulationConfig;
import com.concurrency.gui.VisualizationObserver;

/**
 * Readers-Writers Problem Implementation using Monitors (synchronized +
 * wait/notify).
 * 
 * Synchronization Primitives:
 * - Intrinsic lock (synchronized): Mutual exclusion for state changes
 * - wait()/notifyAll(): Condition synchronization for readers/writers
 * 
 * Solution Strategy:
 * - Multiple readers can read concurrently (shared access)
 * - Writers require exclusive access (no readers or other writers)
 * - This implementation has no starvation prevention (readers-preference)
 * 
 * Educational Design:
 * - Delays are configurable via SimulationConfig for slow observation
 * - Active/waiting counters show thread contention clearly
 * - Total read/write counts demonstrate throughput
 * 
 * Author: Ebenezer Fuachie
 */
public class ReadersWriters {

    public static void run(long durationMillis) {
        run(durationMillis, null);
    }

    public static void run(long durationMillis, VisualizationObserver observer) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        ReadersWritersMonitor monitor = new ReadersWritersMonitor(metrics, observer);
        List<Thread> threads = new ArrayList<>();

        // Start 3 reader threads
        for (int i = 1; i <= 3; i++) {
            Thread t = new Thread(new Reader(monitor, "Reader-" + i));
            threads.add(t);
            t.start();
        }

        // Start 2 writer threads
        for (int i = 1; i <= 2; i++) {
            Thread t = new Thread(new Writer(monitor, "Writer-" + i));
            threads.add(t);
            t.start();
        }

        metrics.start();
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        monitor.stop();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        metrics.stop();
        metrics.printMetrics("Readers-Writers");
    }
}

/**
 * Monitor controlling access to shared resource.
 * 
 * Why this approach:
 * - synchronized provides mutual exclusion for state transitions
 * - wait() releases the lock while waiting, avoiding busy-wait
 * - notifyAll() wakes all waiters to re-check their conditions
 */
class ReadersWritersMonitor {
    private int activeReaders = 0;
    private boolean writerActive = false;
    private volatile int data = 0;
    private final PerformanceMetrics metrics;
    private final VisualizationObserver observer;
    private volatile boolean running = true;

    // Counters for educational visualization
    private final AtomicInteger waitingReaders = new AtomicInteger(0);
    private final AtomicInteger waitingWriters = new AtomicInteger(0);
    private final AtomicInteger totalReads = new AtomicInteger(0);
    private final AtomicInteger totalWrites = new AtomicInteger(0);

    public ReadersWritersMonitor(PerformanceMetrics metrics) {
        this(metrics, null);
    }

    public ReadersWritersMonitor(PerformanceMetrics metrics, VisualizationObserver observer) {
        this.metrics = metrics;
        this.observer = observer;
    }

    public synchronized void startRead(String name) throws InterruptedException {
        // Signal waiting state
        if (observer != null)
            observer.onReaderState(name, "WAITING");

        long startWait = System.nanoTime();

        // Increment waiting count
        waitingReaders.incrementAndGet();
        notifyReaderCounts();

        // Wait while a writer is active (readers can proceed if only other readers
        // active)
        while (writerActive && running) {
            wait();
        }

        // Decrement waiting, increment active
        waitingReaders.decrementAndGet();

        if (!running)
            throw new InterruptedException();
        metrics.recordWaitTime(System.nanoTime() - startWait);

        activeReaders++;
        notifyReaderCounts();

        if (observer != null) {
            observer.onReaderState(name, "READING");
            observer.onResourceState("READING");
        }

        System.out.println(name + " started reading. Active readers = " + activeReaders);

        // Delay for observable state transition (reads fresh)
        SimulationConfig.dynamicSleep(0.5);
    }

    public synchronized void endRead(String name) throws InterruptedException {
        activeReaders--;
        int reads = totalReads.incrementAndGet();

        notifyReaderCounts();
        if (observer != null)
            observer.onReadWriteTotals(reads, totalWrites.get());

        if (observer != null) {
            observer.onReaderState(name, "IDLE");
            if (activeReaders == 0) {
                observer.onResourceState("IDLE");
            }
        }

        System.out.println(name + " finished reading. Active readers = " + activeReaders);
        metrics.addOperation();

        if (activeReaders == 0) {
            notifyAll(); // Wake up waiting writers
        }

        // Delay for observable state transition (reads fresh)
        SimulationConfig.dynamicSleep();
    }

    public synchronized void startWrite(String name) throws InterruptedException {
        // Signal waiting state
        if (observer != null)
            observer.onWriterState(name, "WAITING");

        long startWait = System.nanoTime();

        // Increment waiting count
        waitingWriters.incrementAndGet();
        notifyWriterCounts();

        // Wait while any writer is active OR any readers are reading
        while ((writerActive || activeReaders > 0) && running) {
            wait();
        }

        // Decrement waiting
        waitingWriters.decrementAndGet();

        if (!running)
            throw new InterruptedException();
        metrics.recordWaitTime(System.nanoTime() - startWait);

        writerActive = true;
        notifyWriterCounts();

        if (observer != null) {
            observer.onWriterState(name, "WRITING");
            observer.onResourceState("WRITING");
        }

        System.out.println(name + " started writing.");

        // Delay for observable state transition (reads fresh)
        SimulationConfig.dynamicSleep(0.5);
    }

    public synchronized void endWrite(String name) throws InterruptedException {
        writerActive = false;
        int writes = totalWrites.incrementAndGet();

        notifyWriterCounts();
        if (observer != null)
            observer.onReadWriteTotals(totalReads.get(), writes);

        if (observer != null) {
            observer.onWriterState(name, "IDLE");
            observer.onResourceState("IDLE");
        }

        System.out.println(name + " finished writing.");
        metrics.addOperation();
        notifyAll(); // Wake up all waiting readers and writers

        // Delay for observable state transition (reads fresh)
        SimulationConfig.dynamicSleep();
    }

    private void notifyReaderCounts() {
        if (observer != null) {
            observer.onReaderCounts(activeReaders, waitingReaders.get());
        }
    }

    private void notifyWriterCounts() {
        if (observer != null) {
            observer.onWriterCounts(writerActive ? 1 : 0, waitingWriters.get());
        }
    }

    public int readData() {
        return data;
    }

    public void writeData(int value) {
        data = value;
    }

    public synchronized void stop() {
        running = false;
        notifyAll();
    }

    public boolean isRunning() {
        return running;
    }
}

/**
 * Reader thread: continuously reads from shared resource until stopped.
 */
class Reader implements Runnable {
    private final ReadersWritersMonitor monitor;
    private final String name;

    public Reader(ReadersWritersMonitor monitor, String name) {
        this.monitor = monitor;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (monitor.isRunning()) {
                monitor.startRead(name);
                System.out.println(name + " reads value: " + monitor.readData());
                monitor.endRead(name);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " stopped.");
        }
    }
}

/**
 * Writer thread: continuously writes to shared resource until stopped.
 */
class Writer implements Runnable {
    private final ReadersWritersMonitor monitor;
    private final String name;
    private int writeValue = 0;

    public Writer(ReadersWritersMonitor monitor, String name) {
        this.monitor = monitor;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (monitor.isRunning()) {
                monitor.startWrite(name);
                writeValue++;
                monitor.writeData(writeValue);
                System.out.println(name + " writes value: " + writeValue);
                monitor.endWrite(name);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " stopped.");
        }
    }
}

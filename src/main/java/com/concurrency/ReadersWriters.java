package com.concurrency;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

/*
  Monitor-based solution to the Readers–Writers problem.
  Multiple readers can read concurrently; writers have exclusive access.
  Console output visualizes thread activity.
 
  Author: Ebenezer Fuachie
 */
public class ReadersWriters {

    public static void run(long durationMillis) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        ReadersWritersMonitor monitor = new ReadersWritersMonitor(metrics);
        List<Thread> threads = new ArrayList<>();

        // Start reader threads
        for (int i = 1; i <= 3; i++) {
            Thread t = new Thread(new Reader(monitor, "Reader-" + i));
            threads.add(t);
            t.start();
        }

        // Start writer threads
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

/** Monitor controlling access to shared resource */
class ReadersWritersMonitor {
    private int readers = 0;
    private boolean writerActive = false;
    private volatile int data = 0; // Shared resource (volatile for visibility)
    private final PerformanceMetrics metrics;
    private volatile boolean running = true;

    public ReadersWritersMonitor(PerformanceMetrics metrics) {
        this.metrics = metrics;
    }

    public synchronized void startRead(String name) throws InterruptedException {
        long startWait = System.nanoTime();
        while (writerActive && running) wait();
        if (!running) throw new InterruptedException();
        metrics.recordWaitTime(System.nanoTime() - startWait);

        readers++;
        System.out.println(name + " started reading. Readers = " + readers);
    }

    public synchronized void endRead(String name) {
        readers--;
        System.out.println(name + " finished reading. Readers = " + readers);
        metrics.addOperation();
        if (readers == 0) notifyAll();
    }

    public synchronized void startWrite(String name) throws InterruptedException {
        long startWait = System.nanoTime();
        while ((writerActive || readers > 0) && running) wait();
        if (!running) throw new InterruptedException();
        metrics.recordWaitTime(System.nanoTime() - startWait);

        writerActive = true;
        System.out.println(name + " started writing.");
    }

    public synchronized void endWrite(String name) {
        writerActive = false;
        System.out.println(name + " finished writing.");
        metrics.addOperation();
        notifyAll();
    }

    public int readData() { return data; }
    public void writeData(int value) { data = value; }

    public synchronized void stop() {
        running = false;
        notifyAll();
    }

    public boolean isRunning() {
        return running;
    }
}

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
                Thread.sleep(ThreadLocalRandom.current().nextInt(100) + 50); // Reduced sleep for better throughput demo
                monitor.endRead(name);
                Thread.sleep(ThreadLocalRandom.current().nextInt(200));
            }
        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt(); // Don't interrupt, just exit
            System.out.println(name + " stopped.");
        }
    }
}

class Writer implements Runnable {
    private final ReadersWritersMonitor monitor;
    private final String name;
    

    public Writer(ReadersWritersMonitor monitor, String name) {
        this.monitor = monitor;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (monitor.isRunning()) {
                monitor.startWrite(name);
                int value = ThreadLocalRandom.current().nextInt(100);
                monitor.writeData(value);
                System.out.println(name + " writes value: " + value);
                Thread.sleep(ThreadLocalRandom.current().nextInt(100) + 50);
                monitor.endWrite(name);
                Thread.sleep(ThreadLocalRandom.current().nextInt(200));
            }
        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
            System.out.println(name + " stopped.");
        }
    }
}

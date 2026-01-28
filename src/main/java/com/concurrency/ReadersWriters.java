package com.concurrency;

import java.util.Random;

/*
  Monitor-based solution to the Readers–Writers problem.
  Multiple readers can read concurrently; writers have exclusive access.
  Console output visualizes thread activity.
 
  Author: Ebenezer Fuachie
 */
public class ReadersWriters {

    public static void run() {
        ReadersWritersMonitor monitor = new ReadersWritersMonitor();

        // Start reader threads
        for (int i = 1; i <= 3; i++) {
            new Thread(new Reader(monitor, "Reader-" + i)).start();
        }

        // Start writer threads
        for (int i = 1; i <= 2; i++) {
            new Thread(new Writer(monitor, "Writer-" + i)).start();
        }
    }
}

/** Monitor controlling access to shared resource */
class ReadersWritersMonitor {
    private int readers = 0;
    private boolean writerActive = false;
    private int data = 0; // Shared resource

    public synchronized void startRead(String name) throws InterruptedException {
        while (writerActive) wait();
        readers++;
        System.out.println(name + " started reading. Readers = " + readers);
    }

    public synchronized void endRead(String name) {
        readers--;
        System.out.println(name + " finished reading. Readers = " + readers);
        if (readers == 0) notifyAll();
    }

    public synchronized void startWrite(String name) throws InterruptedException {
        while (writerActive || readers > 0) wait();
        writerActive = true;
        System.out.println(name + " started writing.");
    }

    public synchronized void endWrite(String name) {
        writerActive = false;
        System.out.println(name + " finished writing.");
        notifyAll();
    }

    public int readData() { return data; }
    public void writeData(int value) { data = value; }
}

class Reader implements Runnable {
    private ReadersWritersMonitor monitor;
    private String name;
    private Random rand = new Random();

    public Reader(ReadersWritersMonitor monitor, String name) {
        this.monitor = monitor;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                monitor.startRead(name);
                System.out.println(name + " reads value: " + monitor.readData());
                Thread.sleep(rand.nextInt(500) + 100);
                monitor.endRead(name);
                Thread.sleep(rand.nextInt(1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(name + " interrupted.");
        }
    }
}

class Writer implements Runnable {
    private ReadersWritersMonitor monitor;
    private String name;
    private Random rand = new Random();

    public Writer(ReadersWritersMonitor monitor, String name) {
        this.monitor = monitor;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                monitor.startWrite(name);
                int value = rand.nextInt(100);
                monitor.writeData(value);
                System.out.println(name + " writes value: " + value);
                Thread.sleep(rand.nextInt(500) + 100);
                monitor.endWrite(name);
                Thread.sleep(rand.nextInt(1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(name + " interrupted.");
        }
    }
}


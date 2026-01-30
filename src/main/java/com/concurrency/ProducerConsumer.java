package com.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

import com.concurrency.gui.VisualizationObserver;

public class ProducerConsumer {
    public static void run(long durationMillis) {
        run(durationMillis, null);
    }

    public static void run(long durationMillis, VisualizationObserver observer) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        Buffer buffer = new Buffer(5, metrics, observer); // Capacity 5
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Thread t = new Thread(new Producer(buffer, "Producer-" + i));
            threads.add(t);
            t.start();
        }

        for (int i = 1; i <= 3; i++) {
            Thread t = new Thread(new Consumer(buffer, "Consumer-" + i));
            threads.add(t);
            t.start();
        }

        metrics.start();
        try { Thread.sleep(durationMillis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        buffer.stop();
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        metrics.stop();
        metrics.printMetrics("Producer-Consumer");
    }
}

class Buffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final PerformanceMetrics metrics;
    private final VisualizationObserver observer;
    private volatile boolean running = true;

    public Buffer(int capacity, PerformanceMetrics metrics) {
        this(capacity, metrics, null);
    }

    public Buffer(int capacity, PerformanceMetrics metrics, VisualizationObserver observer) {
        this.capacity = capacity;
        this.metrics = metrics;
        this.observer = observer;
    }

    public void produce(String name) throws InterruptedException {
        if (observer != null) observer.onProducerState(name, "WAITING");
        long startWait = System.nanoTime();
        lock.lock();
        try {
            while (queue.size() == capacity && running) {
                notFull.await();
            }
            if (!running) throw new InterruptedException();
            metrics.recordWaitTime(System.nanoTime() - startWait);

            if (observer != null) observer.onProducerState(name, "PRODUCING");
            int value = ThreadLocalRandom.current().nextInt(100);
            queue.add(value);
            System.out.println(name + " produced: " + value + " | Buffer size: " + queue.size());
            if (observer != null) observer.onBufferUpdate(queue.size(), capacity);

            metrics.addOperation();
            notEmpty.signal();
        } finally {
            lock.unlock();
            if (observer != null) observer.onProducerState(name, "IDLE");
        }
    }

    public void consume(String name) throws InterruptedException {
        if (observer != null) observer.onConsumerState(name, "WAITING");
        long startWait = System.nanoTime();
        lock.lock();
        try {
            while (queue.isEmpty() && running) {
                notEmpty.await();
            }
            if (!running) throw new InterruptedException();
            metrics.recordWaitTime(System.nanoTime() - startWait);

            if (observer != null) observer.onConsumerState(name, "CONSUMING");
            int value = queue.poll();
            System.out.println(name + " consumed: " + value + " | Buffer size: " + queue.size());
            if (observer != null) observer.onBufferUpdate(queue.size(), capacity);

            metrics.addOperation();
            notFull.signal();
        } finally {
            lock.unlock();
            if (observer != null) observer.onConsumerState(name, "IDLE");
        }
    }

    public void stop() {
        lock.lock();
        try {
            running = false;
            notFull.signalAll();
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return running;
    }
}

class Producer implements Runnable {
    private final Buffer buffer;
    private final String name;

    public Producer(Buffer buffer, String name) {
        this.buffer = buffer;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (buffer.isRunning()) {
                buffer.produce(name);
                Thread.sleep(ThreadLocalRandom.current().nextInt(100) + 50);
            }
        } catch (InterruptedException e) {
             System.out.println(name + " stopped.");
        }
    }
}

class Consumer implements Runnable {
    private final Buffer buffer;
    private final String name;

    public Consumer(Buffer buffer, String name) {
        this.buffer = buffer;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (buffer.isRunning()) {
                buffer.consume(name);
                Thread.sleep(ThreadLocalRandom.current().nextInt(100) + 50);
            }
        } catch (InterruptedException e) {
             System.out.println(name + " stopped.");
        }
    }
}

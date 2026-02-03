package com.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

import com.concurrency.gui.SimulationConfig;
import com.concurrency.gui.VisualizationObserver;

/**
 * Producer-Consumer Problem Implementation using Monitors (Locks & Conditions).
 * 
 * Synchronization Primitives:
 * - ReentrantLock: Ensures mutual exclusion when accessing the shared buffer
 * - Condition (notFull): Producers wait here when buffer is full
 * - Condition (notEmpty): Consumers wait here when buffer is empty
 * 
 * Educational Design:
 * - Delays are configurable via SimulationConfig for slow observation
 * - All state changes are reported to the VisualizationObserver
 * - Counters track produced/consumed items and waiting threads
 * 
 * Author: Ebenezer Fuachie
 */
public class ProducerConsumer {

    public static void run(long durationMillis) {
        run(durationMillis, null);
    }

    public static void run(long durationMillis, VisualizationObserver observer) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        Buffer buffer = new Buffer(5, metrics, observer);
        List<Thread> threads = new ArrayList<>();

        // Create 2 producers
        for (int i = 1; i <= 2; i++) {
            Thread t = new Thread(new Producer(buffer, "Producer-" + i));
            threads.add(t);
            t.start();
        }

        // Create 3 consumers
        for (int i = 1; i <= 3; i++) {
            Thread t = new Thread(new Consumer(buffer, "Consumer-" + i));
            threads.add(t);
            t.start();
        }

        metrics.start();
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        buffer.stop();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        metrics.stop();
        metrics.printMetrics("Producer-Consumer");
    }
}

/**
 * Shared bounded buffer with monitor-based synchronization.
 * 
 * Why this approach:
 * - Lock provides mutual exclusion (only one thread modifies buffer at a time)
 * - Conditions allow threads to wait efficiently without busy-waiting
 * - Signaling wakes up exactly one waiting thread to minimize contention
 */
class Buffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final PerformanceMetrics metrics;
    private final VisualizationObserver observer;
    private volatile boolean running = true;

    // Counters for educational visualization
    private final AtomicInteger totalProduced = new AtomicInteger(0);
    private final AtomicInteger totalConsumed = new AtomicInteger(0);
    private final AtomicInteger waitingProducers = new AtomicInteger(0);
    private final AtomicInteger waitingConsumers = new AtomicInteger(0);

    public Buffer(int capacity, PerformanceMetrics metrics) {
        this(capacity, metrics, null);
    }

    public Buffer(int capacity, PerformanceMetrics metrics, VisualizationObserver observer) {
        this.capacity = capacity;
        this.metrics = metrics;
        this.observer = observer;
    }

    public void produce(String name) throws InterruptedException {

        // Signal waiting state BEFORE acquiring lock
        if (observer != null)
            observer.onProducerState(name, "WAITING");

        long startWait = System.nanoTime();
        lock.lock();
        try {
            // Increment waiting count while we check condition
            waitingProducers.incrementAndGet();
            if (observer != null)
                observer.onWaitingProducers(waitingProducers.get());

            // Wait while buffer is full
            while (queue.size() == capacity && running) {
                notFull.await();
            }

            // Decrement waiting count now that we're proceeding
            waitingProducers.decrementAndGet();
            if (observer != null)
                observer.onWaitingProducers(waitingProducers.get());

            if (!running)
                throw new InterruptedException();
            metrics.recordWaitTime(System.nanoTime() - startWait);

            // Signal producing state
            if (observer != null)
                observer.onProducerState(name, "PRODUCING");

            // Slow delay for observable state transition (reads fresh delay)
            SimulationConfig.dynamicSleep(0.6);

            // Add item to buffer
            int value = totalProduced.incrementAndGet();
            queue.add(value);
            System.out.println(name + " produced: " + value + " | Buffer size: " + queue.size());

            // Notify observer of state changes
            if (observer != null) {
                observer.onBufferUpdate(queue.size(), capacity);
                observer.onProducedCount(totalProduced.get());
            }

            metrics.addOperation();
            notEmpty.signal();
        } finally {
            lock.unlock();
            if (observer != null)
                observer.onProducerState(name, "IDLE");
        }

        // Post-production delay for observation (reads fresh delay)
        SimulationConfig.dynamicSleep();
    }

    public void consume(String name) throws InterruptedException {

        // Signal waiting state BEFORE acquiring lock
        if (observer != null)
            observer.onConsumerState(name, "WAITING");

        long startWait = System.nanoTime();
        lock.lock();
        try {
            // Increment waiting count while we check condition
            waitingConsumers.incrementAndGet();
            if (observer != null)
                observer.onWaitingConsumers(waitingConsumers.get());

            // Wait while buffer is empty
            while (queue.isEmpty() && running) {
                notEmpty.await();
            }

            // Decrement waiting count now that we're proceeding
            waitingConsumers.decrementAndGet();
            if (observer != null)
                observer.onWaitingConsumers(waitingConsumers.get());

            if (!running)
                throw new InterruptedException();
            metrics.recordWaitTime(System.nanoTime() - startWait);

            // Signal consuming state
            if (observer != null)
                observer.onConsumerState(name, "CONSUMING");

            // Slow delay for observable state transition (reads fresh delay)
            SimulationConfig.dynamicSleep(0.6);

            // Remove item from buffer
            int value = queue.poll();
            int consumed = totalConsumed.incrementAndGet();
            System.out.println(name + " consumed: " + value + " | Buffer size: " + queue.size());

            // Notify observer of state changes
            if (observer != null) {
                observer.onBufferUpdate(queue.size(), capacity);
                observer.onConsumedCount(consumed);
            }

            metrics.addOperation();
            notFull.signal();
        } finally {
            lock.unlock();
            if (observer != null)
                observer.onConsumerState(name, "IDLE");
        }

        // Post-consumption delay for observation (reads fresh delay)
        SimulationConfig.dynamicSleep();
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

/**
 * Producer thread: continuously produces items until stopped.
 */
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
            }
        } catch (InterruptedException e) {
            System.out.println(name + " stopped.");
        }
    }
}

/**
 * Consumer thread: continuously consumes items until stopped.
 */
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
            }
        } catch (InterruptedException e) {
            System.out.println(name + " stopped.");
        }
    }
}

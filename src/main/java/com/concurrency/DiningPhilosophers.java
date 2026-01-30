package com.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophers {
    public static void run(long durationMillis) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        int numPhilosophers = 5;
        Semaphore[] forks = new Semaphore[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Semaphore(1);
        }

        List<Thread> threads = new ArrayList<>();
        SimulationState state = new SimulationState();

        for (int i = 0; i < numPhilosophers; i++) {
            Philosopher p = new Philosopher(i, forks, state, metrics);
            Thread t = new Thread(p, "Philosopher-" + (i + 1));
            threads.add(t);
            t.start();
        }

        metrics.start();
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        state.running = false;

        for (Thread t : threads) {
            // We interrupt threads to ensure they don't get stuck if they are waiting for a long think/eat
            // But strict Semaphore acquire might need interruption to wake up if deadlocked (which shouldn't happen here)
            // or if we want faster shutdown.
            // Let's rely on natural draining first, but interrupt if taking too long?
            // Actually, interruption is safer to ensure prompt exit from sleeps.
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        metrics.stop();
        metrics.printMetrics("Dining Philosophers");
    }
}

class SimulationState {
    public volatile boolean running = true;
}

class Philosopher implements Runnable {
    private final int id;
    private final Semaphore[] forks;
    private final SimulationState state;
    private final PerformanceMetrics metrics;

    public Philosopher(int id, Semaphore[] forks, SimulationState state, PerformanceMetrics metrics) {
        this.id = id;
        this.forks = forks;
        this.state = state;
        this.metrics = metrics;
    }

    @Override
    public void run() {
        int leftFork = id;
        int rightFork = (id + 1) % forks.length;

        // Resource hierarchy: pick lower index first
        int firstFork = Math.min(leftFork, rightFork);
        int secondFork = Math.max(leftFork, rightFork);

        try {
            while (state.running && !Thread.currentThread().isInterrupted()) {
                // Think
                System.out.println("Philosopher " + (id + 1) + " is thinking.");
                Thread.sleep(ThreadLocalRandom.current().nextInt(100) + 50);

                // Hungry
                long startWait = System.nanoTime();

                forks[firstFork].acquire();
                // Check interrupt/running status?
                // If interrupted during acquire, it throws InterruptedException.
                // If we proceed, we have the lock.

                // We must use try-finally to ensure release
                try {
                    forks[secondFork].acquire();
                    try {
                        long endWait = System.nanoTime();
                        metrics.recordWaitTime(endWait - startWait);

                        // Eat
                        System.out.println("Philosopher " + (id + 1) + " is eating.");
                        metrics.addOperation();
                        Thread.sleep(ThreadLocalRandom.current().nextInt(100) + 50);
                    } finally {
                        forks[secondFork].release();
                    }
                } finally {
                    forks[firstFork].release();
                }
            }
        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
            System.out.println("Philosopher " + (id + 1) + " stopped.");
        }
    }
}

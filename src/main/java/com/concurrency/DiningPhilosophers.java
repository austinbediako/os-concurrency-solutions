package com.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

import com.concurrency.gui.SimulationConfig;
import com.concurrency.gui.VisualizationObserver;

/**
 * Dining Philosophers Problem Implementation using Semaphores.
 * 
 * Synchronization Primitives:
 * - Semaphore (1 permit each): Each fork is a binary semaphore
 * - Resource Hierarchy: Philosophers always pick up lower-numbered fork first
 * to prevent circular wait (deadlock prevention)
 * 
 * Deadlock Prevention Strategy:
 * - Resource ordering: Each philosopher picks up fork with lower index first
 * - This breaks the circular-wait condition of the Coffman conditions
 * - Example: Philosopher 4 picks up fork 0 before fork 4 (not 4 then 0)
 * 
 * Educational Design:
 * - Delays are configurable via SimulationConfig for slow observation
 * - Per-philosopher eat counters show fairness of the solution
 * - State transitions are observable step-by-step
 * 
 * Author: OS Concurrency Team
 */
public class DiningPhilosophers {

    public static void run(long durationMillis) {
        run(durationMillis, null);
    }

    public static void run(long durationMillis, VisualizationObserver observer) {
        PerformanceMetrics metrics = new PerformanceMetrics();
        int numPhilosophers = 5;

        // Each fork is a semaphore with 1 permit (binary semaphore / mutex)
        Semaphore[] forks = new Semaphore[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Semaphore(1);
        }

        // Per-philosopher eat counters for visualization
        AtomicInteger[] eatCounts = new AtomicInteger[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            eatCounts[i] = new AtomicInteger(0);
        }
        AtomicInteger totalMeals = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        SimulationState state = new SimulationState();

        for (int i = 0; i < numPhilosophers; i++) {
            Philosopher p = new Philosopher(i, forks, state, metrics, observer, eatCounts[i], totalMeals);
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

/**
 * Philosopher thread: thinks, gets hungry, picks up forks, eats, puts down
 * forks.
 * 
 * Why Resource Hierarchy prevents deadlock:
 * Without ordering: All 5 philosophers could pick up their left fork
 * simultaneously
 * and then wait forever for their right fork (circular wait).
 * 
 * With ordering: Philosopher 4 needs forks 0 and 4, but must pick up 0 first.
 * This breaks the cycle because Philosopher 0 also needs fork 0.
 * One of them will get fork 0, preventing the circular wait.
 */
class Philosopher implements Runnable {
    private final int id;
    private final Semaphore[] forks;
    private final SimulationState state;
    private final PerformanceMetrics metrics;
    private final VisualizationObserver observer;
    private final AtomicInteger myEatCount;
    private final AtomicInteger totalMeals;

    public Philosopher(int id, Semaphore[] forks, SimulationState state,
            PerformanceMetrics metrics, VisualizationObserver observer,
            AtomicInteger myEatCount, AtomicInteger totalMeals) {
        this.id = id;
        this.forks = forks;
        this.state = state;
        this.metrics = metrics;
        this.observer = observer;
        this.myEatCount = myEatCount;
        this.totalMeals = totalMeals;
    }

    @Override
    public void run() {
        // Resource hierarchy: always pick up lower-indexed fork first
        int leftFork = id;
        int rightFork = (id + 1) % forks.length;
        int firstFork = Math.min(leftFork, rightFork);
        int secondFork = Math.max(leftFork, rightFork);

        try {
            while (state.running && !Thread.currentThread().isInterrupted()) {
                // ======== THINKING ========
                if (observer != null)
                    observer.onPhilosopherState(id, "THINKING");
                System.out.println("Philosopher " + (id + 1) + " is thinking.");
                SimulationConfig.dynamicSleep(0.8); // Think delay (reads fresh)

                // ======== HUNGRY (waiting for forks) ========
                if (observer != null) {
                    observer.onPhilosopherState(id, "HUNGRY");
                    observer.onWaitingPhilosopher(id, true);
                }
                long startWait = System.nanoTime();

                // Pick up first fork
                forks[firstFork].acquire();
                if (observer != null)
                    observer.onForkUpdate(firstFork, true);

                // Small delay to show fork acquisition visually
                SimulationConfig.dynamicSleep(0.5); // State transition (reads fresh)

                try {
                    // Pick up second fork
                    forks[secondFork].acquire();
                    if (observer != null)
                        observer.onForkUpdate(secondFork, true);

                    try {
                        long endWait = System.nanoTime();
                        metrics.recordWaitTime(endWait - startWait);

                        if (observer != null)
                            observer.onWaitingPhilosopher(id, false);

                        // ======== EATING ========
                        if (observer != null)
                            observer.onPhilosopherState(id, "EATING");
                        System.out.println("Philosopher " + (id + 1) + " is eating.");

                        // Update eat counters
                        int myCount = myEatCount.incrementAndGet();
                        totalMeals.incrementAndGet();
                        if (observer != null)
                            observer.onEatCount(id, myCount);

                        metrics.addOperation();
                        SimulationConfig.dynamicSleep(); // Eat delay (reads fresh)

                    } finally {
                        // Put down second fork
                        forks[secondFork].release();
                        if (observer != null)
                            observer.onForkUpdate(secondFork, false);
                    }
                } finally {
                    // Put down first fork
                    forks[firstFork].release();
                    if (observer != null)
                        observer.onForkUpdate(firstFork, false);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Philosopher " + (id + 1) + " stopped.");
        }
    }
}

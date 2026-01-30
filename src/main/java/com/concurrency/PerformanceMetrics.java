package com.concurrency;

import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMetrics {
    private final AtomicLong totalOperations = new AtomicLong(0);
    private final AtomicLong totalWaitTime = new AtomicLong(0); // in nanoseconds
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public void addOperation() {
        totalOperations.incrementAndGet();
    }

    public void recordWaitTime(long nanoSeconds) {
        totalWaitTime.addAndGet(nanoSeconds);
    }

    public long getTotalOperations() {
        return totalOperations.get();
    }

    public long getTotalWaitTime() {
        return totalWaitTime.get();
    }

    public double getDurationSeconds() {
        return (endTime - startTime) / 1_000_000_000.0;
    }

    public double getThroughput() {
        double duration = getDurationSeconds();
        return duration > 0 ? totalOperations.get() / duration : 0;
    }

    public double getAverageWaitTime() { // in milliseconds
        long ops = totalOperations.get();
        return ops > 0 ? (totalWaitTime.get() / 1_000_000.0) / ops : 0;
    }

    public void printMetrics(String simulationName) {
        System.out.println("\n--- " + simulationName + " Performance Metrics ---");
        System.out.println("Total Operations: " + getTotalOperations());
        System.out.printf("Duration: %.2f seconds\n", getDurationSeconds());
        System.out.printf("Throughput: %.2f ops/sec\n", getThroughput());
        System.out.printf("Average Wait Time: %.2f ms\n", getAverageWaitTime());
        System.out.println("-------------------------------------------");
    }
}

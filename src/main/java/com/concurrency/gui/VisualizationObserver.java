package com.concurrency.gui;

/**
 * Interface for observing state changes in concurrency simulations.
 * This allows the simulation logic to be decoupled from the GUI implementation.
 * 
 * Design Note: All methods are called from background threads. Implementations
 * MUST use Platform.runLater() to update JavaFX UI components.
 */
public interface VisualizationObserver {

    // ========== Producer-Consumer ==========

    /** Called when the buffer size changes. */
    void onBufferUpdate(int currentSize, int capacity);

    /**
     * Called when a producer's state changes. States: "PRODUCING", "WAITING",
     * "IDLE"
     */
    void onProducerState(String name, String state);

    /**
     * Called when a consumer's state changes. States: "CONSUMING", "WAITING",
     * "IDLE"
     */
    void onConsumerState(String name, String state);

    /** Called when an item is produced. Passes total produced count. */
    default void onProducedCount(int totalProduced) {
    }

    /** Called when an item is consumed. Passes total consumed count. */
    default void onConsumedCount(int totalConsumed) {
    }

    /** Called when the number of waiting producers changes. */
    default void onWaitingProducers(int count) {
    }

    /** Called when the number of waiting consumers changes. */
    default void onWaitingConsumers(int count) {
    }

    // ========== Dining Philosophers ==========

    /**
     * Called when a philosopher's state changes. States: "THINKING", "HUNGRY",
     * "EATING"
     */
    void onPhilosopherState(int id, String state);

    /** Called when a fork's ownership changes. */
    void onForkUpdate(int id, boolean taken);

    /**
     * Called when a philosopher finishes eating. Passes (philosopherId,
     * totalEatCount).
     */
    default void onEatCount(int philosopherId, int count) {
    }

    /** Called when a philosopher starts waiting for forks. */
    default void onWaitingPhilosopher(int philosopherId, boolean waiting) {
    }

    // ========== Readers-Writers ==========

    /**
     * Called when a reader's state changes. States: "READING", "WAITING", "IDLE"
     */
    void onReaderState(String name, String state);

    /**
     * Called when a writer's state changes. States: "WRITING", "WAITING", "IDLE"
     */
    void onWriterState(String name, String state);

    /**
     * Called when the shared resource state changes. States: "READING", "WRITING",
     * "IDLE"
     */
    void onResourceState(String state);

    /** Called when active/waiting reader counts change. */
    default void onReaderCounts(int activeReaders, int waitingReaders) {
    }

    /** Called when active/waiting writer counts change. */
    default void onWriterCounts(int activeWriters, int waitingWriters) {
    }

    /** Called when cumulative read/write operation counts change. */
    default void onReadWriteTotals(int totalReads, int totalWrites) {
    }
}

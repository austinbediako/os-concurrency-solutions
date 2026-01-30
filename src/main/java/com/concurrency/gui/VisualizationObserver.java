package com.concurrency.gui;

/**
 * Interface for observing state changes in concurrency simulations.
 * This allows the simulation logic to be decoupled from the GUI implementation.
 */
public interface VisualizationObserver {
    // Producer-Consumer
    void onBufferUpdate(int currentSize, int capacity);
    void onProducerState(String name, String state); // state: "PRODUCING", "WAITING", "IDLE"
    void onConsumerState(String name, String state); // state: "CONSUMING", "WAITING", "IDLE"

    // Dining Philosophers
    void onPhilosopherState(int id, String state); // state: "THINKING", "HUNGRY", "EATING"
    void onForkUpdate(int id, boolean taken);

    // Readers-Writers
    void onReaderState(String name, String state); // state: "READING", "WAITING", "IDLE"
    void onWriterState(String name, String state); // state: "WRITING", "WAITING", "IDLE"
    void onResourceState(String state); // state: "READING", "WRITING", "IDLE"
}

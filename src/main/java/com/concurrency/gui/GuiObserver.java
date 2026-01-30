package com.concurrency.gui;

import javafx.application.Platform;

/**
 * Implementation of VisualizationObserver that updates the GUI on the JavaFX Application Thread.
 */
public class GuiObserver implements VisualizationObserver {

    // We will inject View controllers here later via setters or constructor
    private ProducerConsumerView pcView;
    private DiningPhilosophersView dpView;
    private ReadersWritersView rwView;

    public void setProducerConsumerView(ProducerConsumerView view) {
        this.pcView = view;
    }

    public void setDiningPhilosophersView(DiningPhilosophersView view) {
        this.dpView = view;
    }

    public void setReadersWritersView(ReadersWritersView view) {
        this.rwView = view;
    }

    // Producer-Consumer
    @Override
    public void onBufferUpdate(int currentSize, int capacity) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateBuffer(currentSize, capacity));
        }
    }

    @Override
    public void onProducerState(String name, String state) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateProducerState(name, state));
        }
    }

    @Override
    public void onConsumerState(String name, String state) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateConsumerState(name, state));
        }
    }

    // Dining Philosophers
    @Override
    public void onPhilosopherState(int id, String state) {
        if (dpView != null) {
            Platform.runLater(() -> dpView.updatePhilosopherState(id, state));
        }
    }

    @Override
    public void onForkUpdate(int id, boolean taken) {
        if (dpView != null) {
            Platform.runLater(() -> dpView.updateForkState(id, taken));
        }
    }

    // Readers-Writers
    @Override
    public void onReaderState(String name, String state) {
        if (rwView != null) {
            Platform.runLater(() -> rwView.updateReaderState(name, state));
        }
    }

    @Override
    public void onWriterState(String name, String state) {
        if (rwView != null) {
            Platform.runLater(() -> rwView.updateWriterState(name, state));
        }
    }

    @Override
    public void onResourceState(String state) {
        if (rwView != null) {
            Platform.runLater(() -> rwView.updateResourceState(state));
        }
    }
}

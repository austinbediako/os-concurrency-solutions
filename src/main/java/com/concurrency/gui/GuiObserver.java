package com.concurrency.gui;

import javafx.application.Platform;

/**
 * Implementation of VisualizationObserver that updates the GUI on the JavaFX
 * Application Thread.
 * 
 * Design Note: All methods are called from background simulation threads.
 * Platform.runLater() ensures UI updates happen on the correct thread.
 */
public class GuiObserver implements VisualizationObserver {

    private ProducerConsumerView pcView;
    private DiningPhilosophersView dpView;
    private ReadersWritersView rwView;
    @SuppressWarnings("unused") // Reserved for future metrics integration
    private MetricsPanel metricsPanel;

    public void setProducerConsumerView(ProducerConsumerView view) {
        this.pcView = view;
    }

    public void setDiningPhilosophersView(DiningPhilosophersView view) {
        this.dpView = view;
    }

    public void setReadersWritersView(ReadersWritersView view) {
        this.rwView = view;
    }

    public void setMetricsPanel(MetricsPanel panel) {
        this.metricsPanel = panel;
    }

    // ========== Producer-Consumer ==========

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

    @Override
    public void onProducedCount(int totalProduced) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateProducedCount(totalProduced));
        }
    }

    @Override
    public void onConsumedCount(int totalConsumed) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateConsumedCount(totalConsumed));
        }
    }

    @Override
    public void onWaitingProducers(int count) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateWaitingProducers(count));
        }
    }

    @Override
    public void onWaitingConsumers(int count) {
        if (pcView != null) {
            Platform.runLater(() -> pcView.updateWaitingConsumers(count));
        }
    }

    // ========== Dining Philosophers ==========

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

    @Override
    public void onEatCount(int philosopherId, int count) {
        if (dpView != null) {
            Platform.runLater(() -> dpView.updateEatCount(philosopherId, count));
        }
    }

    @Override
    public void onWaitingPhilosopher(int philosopherId, boolean waiting) {
        if (dpView != null) {
            Platform.runLater(() -> dpView.updateWaitingState(philosopherId, waiting));
        }
    }

    // ========== Readers-Writers ==========

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

    @Override
    public void onReaderCounts(int activeReaders, int waitingReaders) {
        if (rwView != null) {
            Platform.runLater(() -> rwView.updateReaderCounts(activeReaders, waitingReaders));
        }
    }

    @Override
    public void onWriterCounts(int activeWriters, int waitingWriters) {
        if (rwView != null) {
            Platform.runLater(() -> rwView.updateWriterCounts(activeWriters, waitingWriters));
        }
    }

    @Override
    public void onReadWriteTotals(int totalReads, int totalWrites) {
        if (rwView != null) {
            Platform.runLater(() -> rwView.updateTotals(totalReads, totalWrites));
        }
    }
}

package com.concurrency.gui;

import com.concurrency.DiningPhilosophers;
import com.concurrency.ProducerConsumer;
import com.concurrency.ReadersWriters;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * Main controller for the Concurrency Visualizer application.
 * 
 * Responsibilities:
 * - Switch between different simulation views
 * - Start/stop simulations
 * - Link views with observer and metrics panel
 */
public class MainController {
    private final BorderPane root;
    private final GuiObserver observer;
    private final MetricsPanel metricsPanel;

    private Thread simulationThread;
    private volatile boolean isRunning = false;

    // Views
    private final ProducerConsumerView pcView;
    private final DiningPhilosophersView dpView;
    private final ReadersWritersView rwView;

    private String currentSimulationType = "NONE";

    public MainController(BorderPane root, GuiObserver observer, MetricsPanel metricsPanel) {
        this.root = root;
        this.observer = observer;
        this.metricsPanel = metricsPanel;

        this.pcView = new ProducerConsumerView();
        this.dpView = new DiningPhilosophersView();
        this.rwView = new ReadersWritersView();

        // Register views with observer
        observer.setProducerConsumerView(pcView);
        observer.setDiningPhilosophersView(dpView);
        observer.setReadersWritersView(rwView);
        observer.setMetricsPanel(metricsPanel);

        // Show Producer-Consumer view by default
        showProducerConsumer();
    }

    public void showProducerConsumer() {
        switchView(pcView, "PRODUCER_CONSUMER");
        if (metricsPanel != null) {
            metricsPanel.showProducerConsumerMetrics();
        }
    }

    public void showDiningPhilosophers() {
        switchView(dpView, "DINING_PHILOSOPHERS");
        if (metricsPanel != null) {
            metricsPanel.showDiningPhilosophersMetrics();
        }
    }

    public void showReadersWriters() {
        switchView(rwView, "READERS_WRITERS");
        if (metricsPanel != null) {
            metricsPanel.showReadersWritersMetrics();
        }
    }

    private void switchView(Pane view, String type) {
        if (isRunning) {
            stopSimulation();
        }
        root.setCenter(view);
        currentSimulationType = type;
        if (metricsPanel != null) {
            metricsPanel.reset();
        }
    }

    public void startSimulation() {
        if (isRunning || currentSimulationType.equals("NONE"))
            return;

        isRunning = true;
        if (metricsPanel != null) {
            metricsPanel.reset();
        }

        simulationThread = new Thread(() -> {
            try {
                // Run for a very long duration effectively "infinite" until stop is called
                long duration = Long.MAX_VALUE;

                switch (currentSimulationType) {
                    case "PRODUCER_CONSUMER":
                        ProducerConsumer.run(duration, observer);
                        break;
                    case "DINING_PHILOSOPHERS":
                        DiningPhilosophers.run(duration, observer);
                        break;
                    case "READERS_WRITERS":
                        ReadersWriters.run(duration, observer);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isRunning = false;
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    public void stopSimulation() {
        if (!isRunning)
            return;

        if (simulationThread != null) {
            simulationThread.interrupt();
            try {
                // Give it a moment to stop cleanly
                simulationThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        isRunning = false;
    }
}

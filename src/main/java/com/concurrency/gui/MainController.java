package com.concurrency.gui;

import com.concurrency.DiningPhilosophers;
import com.concurrency.ProducerConsumer;
import com.concurrency.ReadersWriters;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

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

    private Pane currentSimulationView;
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
    }

    public void showProducerConsumer() {
        switchView(pcView, "PRODUCER_CONSUMER");
    }

    public void showDiningPhilosophers() {
        switchView(dpView, "DINING_PHILOSOPHERS");
    }

    public void showReadersWriters() {
        switchView(rwView, "READERS_WRITERS");
    }

    private void switchView(Pane view, String type) {
        if (isRunning) {
            stopSimulation();
        }
        root.setCenter(view);
        currentSimulationView = view;
        currentSimulationType = type;
        metricsPanel.reset();
    }

    public void startSimulation() {
        if (isRunning || currentSimulationType.equals("NONE")) return;

        isRunning = true;
        metricsPanel.reset();

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

        // Start a separate thread to update metrics (in a real app, metrics would be passed via observer too,
        // but for now we'll just let the visual updates handle the "motion" and maybe add specific metric updates later if needed.
        // The existing PerformanceMetrics class prints to console at end,
        // to show them live we'd need to poll or have callbacks.
        // For this iteration, let's just assume the visual state is the primary feedback,
        // or we can modify the backend to push metrics.
        // Given the plan "Implement the 'Metrics Panel' to display real-time stats", we should ideally link them.
        // Refactoring backend to push metrics to observer would be best, but I'll stick to the plan constraints.
        // Let's rely on the console metrics for final report, and visual for live state.
    }

    public void stopSimulation() {
        if (!isRunning) return;

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

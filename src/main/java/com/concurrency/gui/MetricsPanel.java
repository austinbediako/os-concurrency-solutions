package com.concurrency.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Real-time metrics panel that displays problem-specific counters.
 * 
 * Design Note: This panel dynamically shows metrics relevant to the
 * currently active simulation. All updates are thread-safe via
 * Platform.runLater().
 */
public class MetricsPanel extends VBox {

    // Common metrics
    private final Label titleLabel = new Label("📊 Real-Time Metrics");
    private final Label operationsLabel = new Label("Operations: 0");
    private final Label waitTimeLabel = new Label("Avg Wait: 0 ms");
    private final Label throughputLabel = new Label("Throughput: 0 ops/s");
    private final Label durationLabel = new Label("Duration: 0 s");

    // Producer-Consumer specific
    private final Label producedLabel = new Label("Produced: 0");
    private final Label consumedLabel = new Label("Consumed: 0");
    private final Label bufferLabel = new Label("Buffer: 0/0");
    private final Label waitingProdLabel = new Label("Waiting Producers: 0");
    private final Label waitingConsLabel = new Label("Waiting Consumers: 0");

    // Dining Philosophers specific
    private final Label totalMealsLabel = new Label("Total Meals: 0");
    private final Label[] philosopherMeals = new Label[5];

    // Readers-Writers specific
    private final Label activeReadersLabel = new Label("Active Readers: 0");
    private final Label waitingReadersLabel = new Label("Waiting Readers: 0");
    private final Label activeWritersLabel = new Label("Active Writers: 0");
    private final Label waitingWritersLabel = new Label("Waiting Writers: 0");
    private final Label totalReadsLabel = new Label("Total Reads: 0");
    private final Label totalWritesLabel = new Label("Total Writes: 0");

    @SuppressWarnings("unused") // Reserved for future metrics filtering
    private String currentProblem = "NONE";

    public MetricsPanel() {
        setPadding(new Insets(15));
        setSpacing(8);
        setMinWidth(200);
        setPrefWidth(220);
        setStyle("-fx-background-color: #2c3e50; -fx-border-color: #34495e; " +
                "-fx-border-width: 0 0 0 2;");

        // Title styling
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: white;");

        // Base metric styling
        String metricStyle = "-fx-font-family: 'Courier New', monospace; -fx-text-fill: #ecf0f1;";
        operationsLabel.setStyle(metricStyle);
        waitTimeLabel.setStyle(metricStyle);
        throughputLabel.setStyle(metricStyle);
        durationLabel.setStyle(metricStyle);

        // PC metrics
        producedLabel.setStyle(metricStyle + "-fx-text-fill: #2ecc71;");
        consumedLabel.setStyle(metricStyle + "-fx-text-fill: #e74c3c;");
        bufferLabel.setStyle(metricStyle);
        waitingProdLabel.setStyle(metricStyle + "-fx-text-fill: #f39c12;");
        waitingConsLabel.setStyle(metricStyle + "-fx-text-fill: #f39c12;");

        // DP metrics
        totalMealsLabel.setStyle(metricStyle + "-fx-text-fill: #2ecc71;");
        for (int i = 0; i < 5; i++) {
            philosopherMeals[i] = new Label("P" + (i + 1) + " meals: 0");
            philosopherMeals[i].setStyle(metricStyle);
        }

        // RW metrics
        activeReadersLabel.setStyle(metricStyle + "-fx-text-fill: #3498db;");
        waitingReadersLabel.setStyle(metricStyle + "-fx-text-fill: #f39c12;");
        activeWritersLabel.setStyle(metricStyle + "-fx-text-fill: #e74c3c;");
        waitingWritersLabel.setStyle(metricStyle + "-fx-text-fill: #f39c12;");
        totalReadsLabel.setStyle(metricStyle);
        totalWritesLabel.setStyle(metricStyle);

        showDefaultView();
    }

    private void showDefaultView() {
        getChildren().clear();
        getChildren().addAll(titleLabel, new Separator());

        Label selectLabel = new Label("Select a simulation\nto view metrics");
        selectLabel.setStyle("-fx-text-fill: #95a5a6; -fx-text-alignment: center;");
        getChildren().add(selectLabel);
    }

    public void showProducerConsumerMetrics() {
        currentProblem = "PC";
        getChildren().clear();

        Label header = new Label("Producer-Consumer");
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db; -fx-font-size: 12;");

        getChildren().addAll(
                titleLabel, new Separator(), header, new Separator(),
                producedLabel, consumedLabel, bufferLabel, new Separator(),
                waitingProdLabel, waitingConsLabel,
                new Separator(), durationLabel, throughputLabel);
    }

    public void showDiningPhilosophersMetrics() {
        currentProblem = "DP";
        getChildren().clear();

        Label header = new Label("Dining Philosophers");
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #9b59b6; -fx-font-size: 12;");

        getChildren().addAll(titleLabel, new Separator(), header, new Separator(), totalMealsLabel);
        for (Label l : philosopherMeals) {
            getChildren().add(l);
        }
        getChildren().addAll(new Separator(), durationLabel, throughputLabel);
    }

    public void showReadersWritersMetrics() {
        currentProblem = "RW";
        getChildren().clear();

        Label header = new Label("Readers-Writers");
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #1abc9c; -fx-font-size: 12;");

        getChildren().addAll(
                titleLabel, new Separator(), header, new Separator(),
                activeReadersLabel, waitingReadersLabel, new Separator(),
                activeWritersLabel, waitingWritersLabel, new Separator(),
                totalReadsLabel, totalWritesLabel,
                new Separator(), durationLabel, throughputLabel);
    }

    // ========== Update Methods ==========

    public void updateMetrics(long ops, double avgWait, double throughput, double duration) {
        Platform.runLater(() -> {
            operationsLabel.setText(String.format("Operations: %d", ops));
            waitTimeLabel.setText(String.format("Avg Wait: %.2f ms", avgWait));
            throughputLabel.setText(String.format("Throughput: %.2f/s", throughput));
            durationLabel.setText(String.format("Duration: %.1f s", duration));
        });
    }

    // Producer-Consumer updates
    public void updateProducedCount(int count) {
        Platform.runLater(() -> producedLabel.setText("Produced: " + count));
    }

    public void updateConsumedCount(int count) {
        Platform.runLater(() -> consumedLabel.setText("Consumed: " + count));
    }

    public void updateBufferStatus(int current, int capacity) {
        Platform.runLater(() -> bufferLabel.setText("Buffer: " + current + "/" + capacity));
    }

    public void updateWaitingProducers(int count) {
        Platform.runLater(() -> waitingProdLabel.setText("Waiting Producers: " + count));
    }

    public void updateWaitingConsumers(int count) {
        Platform.runLater(() -> waitingConsLabel.setText("Waiting Consumers: " + count));
    }

    // Dining Philosophers updates
    public void updatePhilosopherMeals(int philosopherId, int count) {
        Platform.runLater(() -> {
            if (philosopherId >= 0 && philosopherId < 5) {
                philosopherMeals[philosopherId].setText("P" + (philosopherId + 1) + " meals: " + count);
            }
        });
    }

    public void updateTotalMeals(int total) {
        Platform.runLater(() -> totalMealsLabel.setText("Total Meals: " + total));
    }

    // Readers-Writers updates
    public void updateReaderCounts(int active, int waiting) {
        Platform.runLater(() -> {
            activeReadersLabel.setText("Active Readers: " + active);
            waitingReadersLabel.setText("Waiting Readers: " + waiting);
        });
    }

    public void updateWriterCounts(int active, int waiting) {
        Platform.runLater(() -> {
            activeWritersLabel.setText("Active Writers: " + active);
            waitingWritersLabel.setText("Waiting Writers: " + waiting);
        });
    }

    public void updateReadWriteTotals(int reads, int writes) {
        Platform.runLater(() -> {
            totalReadsLabel.setText("Total Reads: " + reads);
            totalWritesLabel.setText("Total Writes: " + writes);
        });
    }

    public void reset() {
        updateMetrics(0, 0, 0, 0);
        updateProducedCount(0);
        updateConsumedCount(0);
        updateBufferStatus(0, 0);
        updateWaitingProducers(0);
        updateWaitingConsumers(0);
        updateTotalMeals(0);
        for (int i = 0; i < 5; i++) {
            updatePhilosopherMeals(i, 0);
        }
        updateReaderCounts(0, 0);
        updateWriterCounts(0, 0);
        updateReadWriteTotals(0, 0);
    }
}

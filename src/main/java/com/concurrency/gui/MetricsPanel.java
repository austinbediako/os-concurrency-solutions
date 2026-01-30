package com.concurrency.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class MetricsPanel extends VBox {
    private final Label operationsLabel = new Label("Operations: 0");
    private final Label waitTimeLabel = new Label("Avg Wait: 0 ms");
    private final Label throughputLabel = new Label("Throughput: 0 ops/s");
    private final Label durationLabel = new Label("Duration: 0 s");

    public MetricsPanel() {
        setPadding(new Insets(10));
        setSpacing(5);
        setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: #f0f0f0;");

        Label title = new Label("Real-time Metrics");
        title.setStyle("-fx-font-weight: bold;");

        getChildren().addAll(title, durationLabel, operationsLabel, throughputLabel, waitTimeLabel);
    }

    public void updateMetrics(long ops, double avgWait, double throughput, double duration) {
        javafx.application.Platform.runLater(() -> {
            operationsLabel.setText(String.format("Operations: %d", ops));
            waitTimeLabel.setText(String.format("Avg Wait: %.2f ms", avgWait));
            throughputLabel.setText(String.format("Throughput: %.2f ops/s", throughput));
            durationLabel.setText(String.format("Duration: %.2f s", duration));
        });
    }

    public void reset() {
         updateMetrics(0, 0, 0, 0);
    }
}

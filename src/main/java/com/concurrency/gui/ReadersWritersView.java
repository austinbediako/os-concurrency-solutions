package com.concurrency.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * Visual representation of the Readers-Writers problem.
 * 
 * Layout:
 * - Left: Reader threads (circles)
 * - Center: Shared resource (large rectangle showing state)
 * - Right: Writer threads (circles)
 * 
 * Color Coding:
 * - Blue: Readers (reading)
 * - Red: Writers (writing)
 * - Yellow: Waiting
 * - Gray: Idle
 */
public class ReadersWritersView extends Pane {

    // Color constants
    private static final Color COLOR_READING = Color.web("#3498db"); // Blue
    private static final Color COLOR_WRITING = Color.web("#e74c3c"); // Red
    private static final Color COLOR_WAITING = Color.web("#f39c12"); // Yellow
    private static final Color COLOR_IDLE = Color.web("#95a5a6"); // Gray
    private static final Color COLOR_RESOURCE_IDLE = Color.web("#34495e");
    private static final Color COLOR_RESOURCE_READING = Color.web("#2980b9");
    private static final Color COLOR_RESOURCE_WRITING = Color.web("#c0392b");

    // Readers
    private final Map<String, Circle> readers = new HashMap<>();
    private final Map<String, Label> readerLabels = new HashMap<>();

    // Writers
    private final Map<String, Circle> writers = new HashMap<>();
    private final Map<String, Label> writerLabels = new HashMap<>();

    // Resource
    private final Rectangle resource;
    private final Label resourceStateLabel;

    // Counters
    private final Label activeReadersLabel = new Label("Active Readers: 0");
    private final Label waitingReadersLabel = new Label("Waiting Readers: 0");
    private final Label activeWritersLabel = new Label("Active Writers: 0");
    private final Label waitingWritersLabel = new Label("Waiting Writers: 0");
    private final Label totalReadsLabel = new Label("Total Reads: 0");
    private final Label totalWritesLabel = new Label("Total Writes: 0");

    public ReadersWritersView() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2533, #2c3e50);");
        setPrefSize(800, 600);

        resource = new Rectangle(200, 150, COLOR_RESOURCE_IDLE);
        resourceStateLabel = new Label("IDLE");

        setupLayout();
    }

    private void setupLayout() {
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.prefWidthProperty().bind(widthProperty());
        mainContainer.prefHeightProperty().bind(heightProperty());

        // Title
        Label title = new Label("Readers-Writers Problem");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        // Main visualization
        HBox visualization = new HBox(60);
        visualization.setAlignment(Pos.CENTER);

        // Readers side
        VBox readerBox = createReaderBox();

        // Shared resource in the middle
        VBox resourceBox = createResourceBox();

        // Writers side
        VBox writerBox = createWriterBox();

        visualization.getChildren().addAll(readerBox, resourceBox, writerBox);

        // Counters panel
        HBox counters = createCountersPanel();

        // Legend
        HBox legend = createLegend();

        // Explanation
        HBox explanation = createExplanationBox();

        mainContainer.getChildren().addAll(title, visualization, counters, explanation, legend);
        getChildren().add(mainContainer);
    }

    private VBox createReaderBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Label header = new Label("Readers");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #3498db;");
        box.getChildren().add(header);

        for (int i = 1; i <= 3; i++) {
            String name = "Reader-" + i;

            VBox readerContainer = new VBox(5);
            readerContainer.setAlignment(Pos.CENTER);

            Circle circle = new Circle(30, COLOR_IDLE);
            circle.setStroke(Color.web("#3498db"));
            circle.setStrokeWidth(3);
            readers.put(name, circle);

            Label label = new Label("R" + i);
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            Label stateLabel = new Label("IDLE");
            stateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #bdc3c7;");
            readerLabels.put(name, stateLabel);

            readerContainer.getChildren().addAll(circle, label, stateLabel);
            box.getChildren().add(readerContainer);
        }

        return box;
    }

    private VBox createResourceBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Label header = new Label("Shared Resource");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1abc9c;");

        // Resource rectangle
        resource.setArcWidth(20);
        resource.setArcHeight(20);
        resource.setStroke(Color.WHITE);
        resource.setStrokeWidth(3);

        resourceStateLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox resourceContainer = new VBox(10);
        resourceContainer.setAlignment(Pos.CENTER);
        resourceContainer.getChildren().addAll(resource, resourceStateLabel);

        // Access rules
        Label rulesHeader = new Label("Access Rules:");
        rulesHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #f39c12; -fx-font-size: 12;");

        Label rule1 = new Label("• Multiple readers can read together");
        rule1.setStyle("-fx-text-fill: #3498db; -fx-font-size: 10;");

        Label rule2 = new Label("• Writers need exclusive access");
        rule2.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 10;");

        box.getChildren().addAll(header, resourceContainer, rulesHeader, rule1, rule2);

        return box;
    }

    private VBox createWriterBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Label header = new Label("Writers");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        box.getChildren().add(header);

        for (int i = 1; i <= 2; i++) {
            String name = "Writer-" + i;

            VBox writerContainer = new VBox(5);
            writerContainer.setAlignment(Pos.CENTER);

            Circle circle = new Circle(30, COLOR_IDLE);
            circle.setStroke(Color.web("#e74c3c"));
            circle.setStrokeWidth(3);
            writers.put(name, circle);

            Label label = new Label("W" + i);
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            Label stateLabel = new Label("IDLE");
            stateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #bdc3c7;");
            writerLabels.put(name, stateLabel);

            writerContainer.getChildren().addAll(circle, label, stateLabel);
            box.getChildren().add(writerContainer);
        }

        return box;
    }

    private HBox createCountersPanel() {
        HBox counters = new HBox(30);
        counters.setAlignment(Pos.CENTER);
        counters.setPadding(new Insets(15));
        counters.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10;");

        String counterStyle = "-fx-font-family: 'Courier New'; -fx-font-size: 12;";

        activeReadersLabel.setStyle(counterStyle + "-fx-text-fill: #3498db;");
        waitingReadersLabel.setStyle(counterStyle + "-fx-text-fill: #f39c12;");
        activeWritersLabel.setStyle(counterStyle + "-fx-text-fill: #e74c3c;");
        waitingWritersLabel.setStyle(counterStyle + "-fx-text-fill: #f39c12;");
        totalReadsLabel.setStyle(counterStyle + "-fx-text-fill: #2ecc71;");
        totalWritesLabel.setStyle(counterStyle + "-fx-text-fill: #2ecc71;");

        VBox readerStats = new VBox(5);
        readerStats.getChildren().addAll(activeReadersLabel, waitingReadersLabel, totalReadsLabel);

        VBox writerStats = new VBox(5);
        writerStats.getChildren().addAll(activeWritersLabel, waitingWritersLabel, totalWritesLabel);

        counters.getChildren().addAll(readerStats, writerStats);

        return counters;
    }

    private HBox createExplanationBox() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 8;");

        Label syncLabel = new Label("Synchronization: synchronized + wait()/notifyAll()");
        syncLabel.setStyle("-fx-text-fill: #9b59b6; -fx-font-size: 11;");

        box.getChildren().add(syncLabel);
        return box;
    }

    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);

        legend.getChildren().addAll(
                createLegendItem("Reading", COLOR_READING),
                createLegendItem("Writing", COLOR_WRITING),
                createLegendItem("Waiting", COLOR_WAITING),
                createLegendItem("Idle", COLOR_IDLE));

        return legend;
    }

    private HBox createLegendItem(String text, Color color) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);

        Rectangle rect = new Rectangle(15, 15, color);
        rect.setArcWidth(3);
        rect.setArcHeight(3);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11; -fx-text-fill: #bdc3c7;");

        item.getChildren().addAll(rect, label);
        return item;
    }

    // ========== Update Methods ==========

    public void updateReaderState(String name, String state) {
        Circle circle = readers.get(name);
        Label label = readerLabels.get(name);
        if (circle != null) {
            switch (state.toUpperCase()) {
                case "READING":
                    circle.setFill(COLOR_READING);
                    if (label != null)
                        label.setText("READING");
                    break;
                case "WAITING":
                    circle.setFill(COLOR_WAITING);
                    if (label != null)
                        label.setText("WAITING");
                    break;
                default:
                    circle.setFill(COLOR_IDLE);
                    if (label != null)
                        label.setText("IDLE");
            }
        }
    }

    public void updateWriterState(String name, String state) {
        Circle circle = writers.get(name);
        Label label = writerLabels.get(name);
        if (circle != null) {
            switch (state.toUpperCase()) {
                case "WRITING":
                    circle.setFill(COLOR_WRITING);
                    if (label != null)
                        label.setText("WRITING");
                    break;
                case "WAITING":
                    circle.setFill(COLOR_WAITING);
                    if (label != null)
                        label.setText("WAITING");
                    break;
                default:
                    circle.setFill(COLOR_IDLE);
                    if (label != null)
                        label.setText("IDLE");
            }
        }
    }

    public void updateResourceState(String state) {
        switch (state.toUpperCase()) {
            case "READING":
                resource.setFill(COLOR_RESOURCE_READING);
                resourceStateLabel.setText("READING");
                break;
            case "WRITING":
                resource.setFill(COLOR_RESOURCE_WRITING);
                resourceStateLabel.setText("WRITING");
                break;
            default:
                resource.setFill(COLOR_RESOURCE_IDLE);
                resourceStateLabel.setText("IDLE");
        }
    }

    public void updateReaderCounts(int active, int waiting) {
        activeReadersLabel.setText("Active Readers: " + active);
        waitingReadersLabel.setText("Waiting Readers: " + waiting);
    }

    public void updateWriterCounts(int active, int waiting) {
        activeWritersLabel.setText("Active Writers: " + active);
        waitingWritersLabel.setText("Waiting Writers: " + waiting);
    }

    public void updateTotals(int reads, int writes) {
        totalReadsLabel.setText("Total Reads: " + reads);
        totalWritesLabel.setText("Total Writes: " + writes);
    }
}

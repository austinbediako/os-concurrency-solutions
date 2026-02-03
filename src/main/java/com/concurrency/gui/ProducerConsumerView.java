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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visual representation of the Producer-Consumer problem.
 * 
 * Layout:
 * - Left side: Producers (circles with state colors)
 * - Center: Buffer as horizontal slots (rectangles showing filled/empty)
 * - Right side: Consumers (circles with state colors)
 * 
 * Color Coding:
 * - Green: Actively producing/consuming
 * - Yellow: Waiting (buffer full for producers, empty for consumers)
 * - Gray: Idle
 */
public class ProducerConsumerView extends Pane {

    // Color constants following requirement spec
    private static final Color COLOR_ACTIVE = Color.web("#27ae60"); // Green - producing/consuming
    private static final Color COLOR_WAITING = Color.web("#f39c12"); // Yellow - waiting
    private static final Color COLOR_IDLE = Color.web("#95a5a6"); // Gray - idle
    private static final Color COLOR_BUFFER_EMPTY = Color.web("#ecf0f1"); // Light gray - empty slot
    private static final Color COLOR_BUFFER_FILLED = Color.web("#3498db"); // Blue - filled slot

    // Producers
    private final Map<String, Circle> producers = new HashMap<>();
    private final Map<String, Label> producerLabels = new HashMap<>();

    // Consumers
    private final Map<String, Circle> consumers = new HashMap<>();
    private final Map<String, Label> consumerLabels = new HashMap<>();

    // Buffer slots
    private final List<Rectangle> bufferSlots = new ArrayList<>();
    private final int bufferCapacity = 5;

    // Counter labels
    private final Label producedCountLabel = new Label("Produced: 0");
    private final Label consumedCountLabel = new Label("Consumed: 0");
    private final Label bufferStatusLabel = new Label("Buffer: 0/5");
    private final Label waitingProducersLabel = new Label("Waiting Producers: 0");
    private final Label waitingConsumersLabel = new Label("Waiting Consumers: 0");

    public ProducerConsumerView() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        setPrefSize(800, 600);
        setupLayout();
    }

    private void setupLayout() {
        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.prefWidthProperty().bind(widthProperty());
        mainContainer.prefHeightProperty().bind(heightProperty());

        // Title
        Label title = new Label("Producer-Consumer Problem");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        // Main visualization area
        HBox visualization = new HBox(60);
        visualization.setAlignment(Pos.CENTER);

        // Producers side
        VBox producerBox = createProducerBox();

        // Buffer in the middle
        VBox bufferBox = createBufferBox();

        // Consumers side
        VBox consumerBox = createConsumerBox();

        visualization.getChildren().addAll(producerBox, bufferBox, consumerBox);

        // Counters panel
        HBox counters = createCountersPanel();

        // Legend
        HBox legend = createLegend();

        mainContainer.getChildren().addAll(title, visualization, counters, legend);
        getChildren().add(mainContainer);
    }

    private VBox createProducerBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Label header = new Label("Producers");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #3498db;");
        box.getChildren().add(header);

        for (int i = 1; i <= 2; i++) {
            String name = "Producer-" + i;

            VBox producerContainer = new VBox(5);
            producerContainer.setAlignment(Pos.CENTER);

            Circle circle = new Circle(30, COLOR_IDLE);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            producers.put(name, circle);

            Label label = new Label("P" + i);
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            Label stateLabel = new Label("IDLE");
            stateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #bdc3c7;");
            producerLabels.put(name, stateLabel);

            producerContainer.getChildren().addAll(circle, label, stateLabel);
            box.getChildren().add(producerContainer);
        }

        return box;
    }

    private VBox createBufferBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Label header = new Label("Bounded Buffer");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #f39c12;");

        HBox slots = new HBox(8);
        slots.setAlignment(Pos.CENTER);

        for (int i = 0; i < bufferCapacity; i++) {
            VBox slotContainer = new VBox(5);
            slotContainer.setAlignment(Pos.CENTER);

            Rectangle slot = new Rectangle(50, 50, COLOR_BUFFER_EMPTY);
            slot.setArcWidth(10);
            slot.setArcHeight(10);
            slot.setStroke(Color.WHITE);
            slot.setStrokeWidth(2);
            bufferSlots.add(slot);

            Label indexLabel = new Label(String.valueOf(i + 1));
            indexLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #7f8c8d;");

            slotContainer.getChildren().addAll(slot, indexLabel);
            slots.getChildren().add(slotContainer);
        }

        box.getChildren().addAll(header, slots, bufferStatusLabel);
        bufferStatusLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: white;");

        return box;
    }

    private VBox createConsumerBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Label header = new Label("Consumers");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        box.getChildren().add(header);

        for (int i = 1; i <= 3; i++) {
            String name = "Consumer-" + i;

            VBox consumerContainer = new VBox(5);
            consumerContainer.setAlignment(Pos.CENTER);

            Circle circle = new Circle(30, COLOR_IDLE);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);
            consumers.put(name, circle);

            Label label = new Label("C" + i);
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            Label stateLabel = new Label("IDLE");
            stateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #bdc3c7;");
            consumerLabels.put(name, stateLabel);

            consumerContainer.getChildren().addAll(circle, label, stateLabel);
            box.getChildren().add(consumerContainer);
        }

        return box;
    }

    private HBox createCountersPanel() {
        HBox counters = new HBox(40);
        counters.setAlignment(Pos.CENTER);
        counters.setPadding(new Insets(20));
        counters.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10;");

        String counterStyle = "-fx-font-family: 'Courier New'; -fx-font-size: 14; -fx-text-fill: white;";
        producedCountLabel.setStyle(counterStyle + "-fx-text-fill: #2ecc71;");
        consumedCountLabel.setStyle(counterStyle + "-fx-text-fill: #e74c3c;");
        waitingProducersLabel.setStyle(counterStyle + "-fx-text-fill: #f39c12;");
        waitingConsumersLabel.setStyle(counterStyle + "-fx-text-fill: #f39c12;");

        counters.getChildren().addAll(producedCountLabel, consumedCountLabel,
                waitingProducersLabel, waitingConsumersLabel);
        return counters;
    }

    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);

        legend.getChildren().addAll(
                createLegendItem("Active", COLOR_ACTIVE),
                createLegendItem("Waiting", COLOR_WAITING),
                createLegendItem("Idle", COLOR_IDLE),
                createLegendItem("Filled Slot", COLOR_BUFFER_FILLED),
                createLegendItem("Empty Slot", COLOR_BUFFER_EMPTY));

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

    public void updateBuffer(int currentSize, int capacity) {
        bufferStatusLabel.setText("Buffer: " + currentSize + "/" + capacity);

        // Update slot colors
        for (int i = 0; i < bufferSlots.size(); i++) {
            if (i < currentSize) {
                bufferSlots.get(i).setFill(COLOR_BUFFER_FILLED);
            } else {
                bufferSlots.get(i).setFill(COLOR_BUFFER_EMPTY);
            }
        }
    }

    public void updateProducerState(String name, String state) {
        Circle circle = producers.get(name);
        Label label = producerLabels.get(name);
        if (circle != null) {
            switch (state) {
                case "PRODUCING":
                    circle.setFill(COLOR_ACTIVE);
                    if (label != null)
                        label.setText("PRODUCING");
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

    public void updateConsumerState(String name, String state) {
        Circle circle = consumers.get(name);
        Label label = consumerLabels.get(name);
        if (circle != null) {
            switch (state) {
                case "CONSUMING":
                    circle.setFill(COLOR_ACTIVE);
                    if (label != null)
                        label.setText("CONSUMING");
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

    public void updateProducedCount(int count) {
        producedCountLabel.setText("Produced: " + count);
    }

    public void updateConsumedCount(int count) {
        consumedCountLabel.setText("Consumed: " + count);
    }

    public void updateWaitingProducers(int count) {
        waitingProducersLabel.setText("Waiting Producers: " + count);
    }

    public void updateWaitingConsumers(int count) {
        waitingConsumersLabel.setText("Waiting Consumers: " + count);
    }
}

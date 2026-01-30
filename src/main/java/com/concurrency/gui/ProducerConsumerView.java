package com.concurrency.gui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerView extends Pane {
    private final List<Rectangle> bufferSlots = new ArrayList<>();
    private final List<Rectangle> producers = new ArrayList<>();
    private final List<Rectangle> consumers = new ArrayList<>();

    // Colors
    private static final Color COLOR_WORKING = Color.GREEN;
    private static final Color COLOR_WAITING = Color.ORANGE;
    private static final Color COLOR_IDLE = Color.LIGHTBLUE;
    private static final Color COLOR_EMPTY_SLOT = Color.WHITE;
    private static final Color COLOR_FULL_SLOT = Color.PURPLE;

    public ProducerConsumerView() {
        setStyle("-fx-background-color: #e0e0e0;");
        setupLayout();
    }

    private void setupLayout() {
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPrefSize(800, 600);
        mainLayout.layoutXProperty().bind(widthProperty().subtract(mainLayout.widthProperty()).divide(2));
        mainLayout.layoutYProperty().bind(heightProperty().subtract(mainLayout.heightProperty()).divide(2));

        // Producers Area
        HBox producersBox = new HBox(10);
        producersBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 2; i++) {
            VBox pContainer = new VBox(5);
            pContainer.setAlignment(Pos.CENTER);
            Rectangle p = new Rectangle(50, 50, COLOR_IDLE);
            p.setStroke(Color.BLACK);
            producers.add(p);
            pContainer.getChildren().addAll(new Label("Prod " + (i+1)), p);
            producersBox.getChildren().add(pContainer);
        }

        // Buffer Area
        HBox bufferBox = new HBox(5);
        bufferBox.setAlignment(Pos.CENTER);
        bufferBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: white;");
        for (int i = 0; i < 5; i++) {
            Rectangle slot = new Rectangle(40, 40, COLOR_EMPTY_SLOT);
            slot.setStroke(Color.BLACK);
            bufferSlots.add(slot);
            bufferBox.getChildren().add(slot);
        }

        // Consumers Area
        HBox consumersBox = new HBox(10);
        consumersBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            VBox cContainer = new VBox(5);
            cContainer.setAlignment(Pos.CENTER);
            Rectangle c = new Rectangle(50, 50, COLOR_IDLE);
            c.setStroke(Color.BLACK);
            consumers.add(c);
            cContainer.getChildren().addAll(new Label("Cons " + (i+1)), c);
            consumersBox.getChildren().add(cContainer);
        }

        mainLayout.getChildren().addAll(
            new Label("Producers"), producersBox,
            new Label("Buffer (Size: 5)"), bufferBox,
            new Label("Consumers"), consumersBox
        );
        getChildren().add(mainLayout);
    }

    public void updateBuffer(int currentSize, int capacity) {
        for (int i = 0; i < capacity; i++) {
            if (i < currentSize) {
                bufferSlots.get(i).setFill(COLOR_FULL_SLOT);
            } else {
                bufferSlots.get(i).setFill(COLOR_EMPTY_SLOT);
            }
        }
    }

    public void updateProducerState(String name, String state) {
        // name format: "Producer-1"
        int index = Integer.parseInt(name.split("-")[1]) - 1;
        if (index >= 0 && index < producers.size()) {
            Color c = COLOR_IDLE;
            switch(state) {
                case "PRODUCING": c = COLOR_WORKING; break;
                case "WAITING": c = COLOR_WAITING; break;
                case "IDLE": c = COLOR_IDLE; break;
            }
            producers.get(index).setFill(c);
        }
    }

    public void updateConsumerState(String name, String state) {
        // name format: "Consumer-1"
        int index = Integer.parseInt(name.split("-")[1]) - 1;
        if (index >= 0 && index < consumers.size()) {
            Color c = COLOR_IDLE;
            switch(state) {
                case "CONSUMING": c = COLOR_WORKING; break;
                case "WAITING": c = COLOR_WAITING; break;
                case "IDLE": c = COLOR_IDLE; break;
            }
            consumers.get(index).setFill(c);
        }
    }
}

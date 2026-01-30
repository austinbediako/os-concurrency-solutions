package com.concurrency.gui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;

public class ReadersWritersView extends Pane {
    private final List<Circle> readers = new ArrayList<>();
    private final List<Rectangle> writers = new ArrayList<>();
    private Rectangle sharedResource;

    // Colors
    private static final Color COLOR_IDLE = Color.LIGHTBLUE;
    private static final Color COLOR_READING = Color.GREEN;
    private static final Color COLOR_WRITING = Color.RED;
    private static final Color COLOR_WAITING = Color.ORANGE;
    private static final Color COLOR_RESOURCE_IDLE = Color.GRAY;

    public ReadersWritersView() {
        setStyle("-fx-background-color: #e0e0e0;");
        setupLayout();
    }

    private void setupLayout() {
        double centerX = 400;
        double centerY = 300;

        // Shared Resource
        sharedResource = new Rectangle(centerX - 50, centerY - 50, 100, 100);
        sharedResource.setFill(COLOR_RESOURCE_IDLE);
        sharedResource.setStroke(Color.BLACK);

        Label resourceLabel = new Label("DATA");
        resourceLabel.setLayoutX(centerX - 15);
        resourceLabel.setLayoutY(centerY - 10);
        resourceLabel.setTextFill(Color.WHITE);
        resourceLabel.setStyle("-fx-font-weight: bold;");

        getChildren().addAll(sharedResource, resourceLabel);

        // Readers (Top semi-circle)
        int numReaders = 3;
        double readerRadius = 150;
        for (int i = 0; i < numReaders; i++) {
            double angle = Math.PI + (i + 1) * (Math.PI / (numReaders + 1)); // distribute over top half
            double x = centerX + readerRadius * Math.cos(angle);
            double y = centerY + readerRadius * Math.sin(angle);

            Circle r = new Circle(x, y, 25, COLOR_IDLE);
            r.setStroke(Color.BLACK);
            readers.add(r);

            Label lbl = new Label("R" + (i+1));
            lbl.setLayoutX(x - 10);
            lbl.setLayoutY(y - 35);

            getChildren().addAll(r, lbl);
        }

        // Writers (Bottom semi-circle)
        int numWriters = 2;
        double writerRadius = 150;
        for (int i = 0; i < numWriters; i++) {
            double angle = (i + 1) * (Math.PI / (numWriters + 1)); // distribute over bottom half
            double x = centerX + writerRadius * Math.cos(angle);
            double y = centerY + writerRadius * Math.sin(angle);

            Rectangle w = new Rectangle(x - 25, y - 25, 50, 50);
            w.setFill(COLOR_IDLE);
            w.setStroke(Color.BLACK);
            writers.add(w);

            Label lbl = new Label("W" + (i+1));
            lbl.setLayoutX(x - 10);
            lbl.setLayoutY(y + 30);

            getChildren().addAll(w, lbl);
        }
    }

    public void updateResourceState(String state) {
        Color c = COLOR_RESOURCE_IDLE;
        switch(state) {
            case "READING": c = COLOR_READING; break;
            case "WRITING": c = COLOR_WRITING; break;
            case "IDLE": c = COLOR_RESOURCE_IDLE; break;
        }
        sharedResource.setFill(c);
    }

    public void updateReaderState(String name, String state) {
        // "Reader-1"
        int index = Integer.parseInt(name.split("-")[1]) - 1;
        if (index >= 0 && index < readers.size()) {
            Color c = COLOR_IDLE;
            switch(state) {
                case "READING": c = COLOR_READING; break;
                case "WAITING": c = COLOR_WAITING; break;
                case "IDLE": c = COLOR_IDLE; break;
            }
            readers.get(index).setFill(c);
        }
    }

    public void updateWriterState(String name, String state) {
        // "Writer-1"
        int index = Integer.parseInt(name.split("-")[1]) - 1;
        if (index >= 0 && index < writers.size()) {
            Color c = COLOR_IDLE;
            switch(state) {
                case "WRITING": c = COLOR_WRITING; break;
                case "WAITING": c = COLOR_WAITING; break;
                case "IDLE": c = COLOR_IDLE; break;
            }
            writers.get(index).setFill(c);
        }
    }
}

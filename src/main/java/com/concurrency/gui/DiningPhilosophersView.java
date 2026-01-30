package com.concurrency.gui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophersView extends Pane {
    private final List<Circle> philosophers = new ArrayList<>();
    private final List<Line> forks = new ArrayList<>();

    // Colors
    private static final Color COLOR_THINKING = Color.BLUE;
    private static final Color COLOR_HUNGRY = Color.ORANGE;
    private static final Color COLOR_EATING = Color.GREEN;
    private static final Color COLOR_FORK_FREE = Color.BLACK;
    private static final Color COLOR_FORK_TAKEN = Color.RED;

    public DiningPhilosophersView() {
        setStyle("-fx-background-color: #e0e0e0;");
        setupLayout();
    }

    private void setupLayout() {
        double centerX = 400;
        double centerY = 300;
        double tableRadius = 100;
        double philosopherRadius = 180;
        double forkRadius = 140;

        // Table
        Circle table = new Circle(centerX, centerY, tableRadius, Color.BURLYWOOD);
        table.setStroke(Color.BROWN);
        getChildren().add(table);

        int num = 5;
        double angleStep = 2 * Math.PI / num;

        // Forks (placed between philosophers)
        for (int i = 0; i < num; i++) {
            // Place fork at angle i + 0.5 step
            double angle = (i + 0.5) * angleStep - Math.PI / 2; // -PI/2 to start from top
            double x1 = centerX + (tableRadius - 20) * Math.cos(angle);
            double y1 = centerY + (tableRadius - 20) * Math.sin(angle);
            double x2 = centerX + (tableRadius + 20) * Math.cos(angle);
            double y2 = centerY + (tableRadius + 20) * Math.sin(angle);

            Line fork = new Line(x1, y1, x2, y2);
            fork.setStroke(COLOR_FORK_FREE);
            fork.setStrokeWidth(4);
            forks.add(fork);
            getChildren().add(fork);
        }

        // Philosophers
        for (int i = 0; i < num; i++) {
            double angle = i * angleStep - Math.PI / 2;
            double x = centerX + philosopherRadius * Math.cos(angle);
            double y = centerY + philosopherRadius * Math.sin(angle);

            Circle p = new Circle(x, y, 30, COLOR_THINKING);
            p.setStroke(Color.BLACK);
            philosophers.add(p);

            Label lbl = new Label("P" + (i+1));
            lbl.setLayoutX(x - 10);
            lbl.setLayoutY(y - 10);
            lbl.setTextFill(Color.WHITE);

            getChildren().addAll(p, lbl);
        }
    }

    public void updatePhilosopherState(int id, String state) {
        if (id >= 0 && id < philosophers.size()) {
            Color c = COLOR_THINKING;
            switch(state) {
                case "EATING": c = COLOR_EATING; break;
                case "HUNGRY": c = COLOR_HUNGRY; break;
                case "THINKING": c = COLOR_THINKING; break;
            }
            philosophers.get(id).setFill(c);
        }
    }

    public void updateForkState(int id, boolean taken) {
        if (id >= 0 && id < forks.size()) {
            forks.get(id).setStroke(taken ? COLOR_FORK_TAKEN : COLOR_FORK_FREE);
        }
    }
}

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

/**
 * Visual representation of the Dining Philosophers problem.
 * 
 * Layout:
 * - Circular arrangement of 5 philosophers
 * - Forks between each philosopher pair
 * - Per-philosopher eat counters
 * - Color-coded states: THINKING, HUNGRY, EATING
 * 
 * Color Coding:
 * - Purple: Thinking
 * - Orange: Hungry (waiting for forks)
 * - Green: Eating
 * - Red: Fork in use
 * - Gray: Fork available
 */
public class DiningPhilosophersView extends Pane {

    // Color constants
    private static final Color COLOR_THINKING = Color.web("#9b59b6"); // Purple
    private static final Color COLOR_HUNGRY = Color.web("#f39c12"); // Orange
    private static final Color COLOR_EATING = Color.web("#27ae60"); // Green
    private static final Color COLOR_FORK_FREE = Color.web("#95a5a6"); // Gray
    private static final Color COLOR_FORK_TAKEN = Color.web("#e74c3c"); // Red

    private static final int NUM_PHILOSOPHERS = 5;
    private static final double CENTER_X = 350;
    private static final double CENTER_Y = 280;
    private static final double TABLE_RADIUS = 120;
    private static final double PHIL_RADIUS = 180;

    private final Circle[] philosophers = new Circle[NUM_PHILOSOPHERS];
    private final Label[] stateLabels = new Label[NUM_PHILOSOPHERS];
    private final Label[] eatCountLabels = new Label[NUM_PHILOSOPHERS];
    private final Rectangle[] forks = new Rectangle[NUM_PHILOSOPHERS];
    @SuppressWarnings("unused")
    private final boolean[] waitingStates = new boolean[NUM_PHILOSOPHERS];

    public DiningPhilosophersView() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a2533, #2c3e50);");
        setPrefSize(800, 600);
        setupLayout();
    }

    private void setupLayout() {
        // Title
        Label title = new Label("Dining Philosophers Problem");
        title.setLayoutX(CENTER_X - 150);
        title.setLayoutY(20);
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        getChildren().add(title);

        // Dining table
        Circle table = new Circle(CENTER_X, CENTER_Y, TABLE_RADIUS);
        table.setFill(Color.web("#34495e"));
        table.setStroke(Color.web("#7f8c8d"));
        table.setStrokeWidth(3);
        getChildren().add(table);

        Label tableLabel = new Label("TABLE");
        tableLabel.setLayoutX(CENTER_X - 25);
        tableLabel.setLayoutY(CENTER_Y - 8);
        tableLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        getChildren().add(tableLabel);

        // Create philosophers and forks
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            double angle = 2 * Math.PI * i / NUM_PHILOSOPHERS - Math.PI / 2;

            // Philosopher position
            double philX = CENTER_X + PHIL_RADIUS * Math.cos(angle);
            double philY = CENTER_Y + PHIL_RADIUS * Math.sin(angle);

            // Philosopher circle
            Circle phil = new Circle(philX, philY, 35, COLOR_THINKING);
            phil.setStroke(Color.WHITE);
            phil.setStrokeWidth(3);
            philosophers[i] = phil;

            // Philosopher number label
            Label numLabel = new Label("P" + (i + 1));
            numLabel.setLayoutX(philX - 10);
            numLabel.setLayoutY(philY - 8);
            numLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14;");

            // State label below philosopher
            Label stateLabel = new Label("THINKING");
            stateLabel.setLayoutX(philX - 30);
            stateLabel.setLayoutY(philY + 40);
            stateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #bdc3c7;");
            stateLabels[i] = stateLabel;

            // Eat count above philosopher
            Label eatLabel = new Label("Meals: 0");
            eatLabel.setLayoutX(philX - 25);
            eatLabel.setLayoutY(philY - 55);
            eatLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11; -fx-text-fill: #2ecc71;");
            eatCountLabels[i] = eatLabel;

            getChildren().addAll(phil, numLabel, stateLabel, eatLabel);

            // Fork position (between this philosopher and the next)
            double forkAngle = angle + Math.PI / NUM_PHILOSOPHERS;
            double forkX = CENTER_X + (TABLE_RADIUS + 30) * Math.cos(forkAngle);
            double forkY = CENTER_Y + (TABLE_RADIUS + 30) * Math.sin(forkAngle);

            // Fork (rotated rectangle)
            Rectangle fork = new Rectangle(forkX - 5, forkY - 20, 10, 40);
            fork.setFill(COLOR_FORK_FREE);
            fork.setArcWidth(5);
            fork.setArcHeight(5);
            fork.setRotate(Math.toDegrees(forkAngle) + 90);
            forks[i] = fork;

            // Fork label
            Label forkLabel = new Label("F" + (i + 1));
            forkLabel.setLayoutX(forkX - 8);
            forkLabel.setLayoutY(forkY + 15);
            forkLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #7f8c8d;");

            getChildren().addAll(fork, forkLabel);
        }

        // Legend
        HBox legend = createLegend();
        legend.setLayoutX(CENTER_X - 200);
        legend.setLayoutY(520);
        getChildren().add(legend);

        // Explanation box
        VBox explanation = createExplanationBox();
        explanation.setLayoutX(20);
        explanation.setLayoutY(CENTER_Y - 80);
        getChildren().add(explanation);
    }

    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);

        legend.getChildren().addAll(
                createLegendItem("Thinking", COLOR_THINKING),
                createLegendItem("Hungry", COLOR_HUNGRY),
                createLegendItem("Eating", COLOR_EATING),
                createLegendItem("Fork Free", COLOR_FORK_FREE),
                createLegendItem("Fork Taken", COLOR_FORK_TAKEN));

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

    private VBox createExplanationBox() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 10;");
        box.setMaxWidth(140);

        Label header = new Label("Deadlock Prevention:");
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-font-size: 11;");

        Label desc = new Label("Resource Hierarchy\n" +
                "Philosophers pick up\n" +
                "lower-indexed fork first,\n" +
                "breaking circular wait.");
        desc.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 10;");
        desc.setWrapText(true);

        box.getChildren().addAll(header, desc);
        return box;
    }

    // ========== Update Methods ==========

    public void updatePhilosopherState(int id, String state) {
        if (id < 0 || id >= NUM_PHILOSOPHERS)
            return;

        Circle phil = philosophers[id];
        Label label = stateLabels[id];

        switch (state.toUpperCase()) {
            case "THINKING":
                phil.setFill(COLOR_THINKING);
                if (label != null)
                    label.setText("THINKING");
                break;
            case "HUNGRY":
                phil.setFill(COLOR_HUNGRY);
                if (label != null)
                    label.setText("HUNGRY");
                break;
            case "EATING":
                phil.setFill(COLOR_EATING);
                if (label != null)
                    label.setText("EATING");
                break;
        }
    }

    public void updateForkState(int id, boolean taken) {
        if (id < 0 || id >= NUM_PHILOSOPHERS)
            return;

        forks[id].setFill(taken ? COLOR_FORK_TAKEN : COLOR_FORK_FREE);
    }

    public void updateEatCount(int philosopherId, int count) {
        if (philosopherId < 0 || philosopherId >= NUM_PHILOSOPHERS)
            return;

        eatCountLabels[philosopherId].setText("Meals: " + count);
    }

    public void updateWaitingState(int philosopherId, boolean waiting) {
        if (philosopherId < 0 || philosopherId >= NUM_PHILOSOPHERS)
            return;

        waitingStates[philosopherId] = waiting;
        // Waiting state is already shown via HUNGRY color
    }
}

package com.concurrency.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main JavaFX Application for the Concurrency Visualizer Lab.
 * 
 * Educational Design:
 * - Speed slider allows students to slow down execution for observation
 * - Consistent color coding across all simulations
 * - Real-time metrics panel shows thread activity
 */
public class ConcurrencyVisualizerApp extends Application {

    private MainController controller;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'System'; -fx-background-color: #f5f5f5;");

        // Observer
        GuiObserver observer = new GuiObserver();

        // Controller (no metrics panel)
        controller = new MainController(root, observer, null);

        // Top Navigation Bar
        ToolBar navBar = createNavigationBar();
        root.setTop(navBar);

        // Bottom Control Panel with Speed Slider
        VBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        // Apply global CSS
        Scene scene = new Scene(root, 1100, 768);
        primaryStage.setTitle("OS Concurrency Visualizer Lab");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ToolBar createNavigationBar() {
        ToolBar navBar = new ToolBar();
        navBar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 8;");

        Button btnPC = createNavButton("Producer-Consumer");
        btnPC.setOnAction(e -> controller.showProducerConsumer());

        Button btnDP = createNavButton("Dining Philosophers");
        btnDP.setOnAction(e -> controller.showDiningPhilosophers());

        Button btnRW = createNavButton("Readers-Writers");
        btnRW.setOnAction(e -> controller.showReadersWriters());

        navBar.getItems().addAll(btnPC, new Separator(), btnDP, new Separator(), btnRW);
        return navBar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;"));
        return btn;
    }

    private VBox createBottomPanel() {
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; " +
                "-fx-border-width: 1 0 0 0;");

        // Speed Control - Direct milliseconds (lower = faster)
        HBox speedControl = new HBox(10);
        speedControl.setAlignment(Pos.CENTER);

        Label speedLabel = new Label("Delay (ms):");
        speedLabel.setStyle("-fx-font-weight: bold;");

        Label fastLabel = new Label("Fast");
        fastLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11; -fx-font-weight: bold;");

        // Slider: 50ms (fast) to 2000ms (slow)
        Slider delaySlider = new Slider(50, 2000, 500);
        delaySlider.setShowTickLabels(true);
        delaySlider.setShowTickMarks(true);
        delaySlider.setMajorTickUnit(500);
        delaySlider.setMinorTickCount(4);
        delaySlider.setBlockIncrement(100);
        delaySlider.setPrefWidth(250);
        delaySlider.setSnapToTicks(false);

        Label delayValueLabel = new Label("500ms");
        delayValueLabel
                .setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-weight: bold; -fx-min-width: 60;");

        // Bind slider to SimulationConfig - changes take effect IMMEDIATELY
        SimulationConfig config = SimulationConfig.getInstance();
        delaySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            long delay = newVal.longValue();
            config.setDelay(delay);
            delayValueLabel.setText(delay + "ms");
        });
        // Initialize from config
        delaySlider.setValue(config.getDelay());

        Label slowLabel = new Label("Slow");
        slowLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11; -fx-font-weight: bold;");

        speedControl.getChildren().addAll(speedLabel, fastLabel, delaySlider, delayValueLabel, slowLabel);

        // Start/Stop Buttons
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);

        Button btnStart = new Button("▶ Start Simulation");
        btnStart.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        btnStart.setOnAction(e -> controller.startSimulation());

        Button btnStop = new Button("■ Stop");
        btnStop.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        btnStop.setOnAction(e -> controller.stopSimulation());

        controls.getChildren().addAll(btnStart, btnStop);

        bottomPanel.getChildren().addAll(speedControl, controls);
        return bottomPanel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

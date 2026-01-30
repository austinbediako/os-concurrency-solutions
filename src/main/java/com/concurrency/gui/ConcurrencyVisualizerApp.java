package com.concurrency.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ConcurrencyVisualizerApp extends Application {

    private MainController controller;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Metrics Panel
        MetricsPanel metricsPanel = new MetricsPanel();
        root.setRight(metricsPanel);

        // Observer
        GuiObserver observer = new GuiObserver();

        // Controller
        controller = new MainController(root, observer, metricsPanel);

        // Top Navigation
        ToolBar navBar = new ToolBar();
        Button btnPC = new Button("Producer-Consumer");
        btnPC.setOnAction(e -> controller.showProducerConsumer());

        Button btnDP = new Button("Dining Philosophers");
        btnDP.setOnAction(e -> controller.showDiningPhilosophers());

        Button btnRW = new Button("Readers-Writers");
        btnRW.setOnAction(e -> controller.showReadersWriters());

        navBar.getItems().addAll(btnPC, btnDP, btnRW);
        root.setTop(navBar);

        // Bottom Control Panel
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        Button btnStart = new Button("Start");
        btnStart.setStyle("-fx-base: green; -fx-text-fill: white; -fx-font-weight: bold;");
        btnStart.setOnAction(e -> controller.startSimulation());

        Button btnStop = new Button("Stop");
        btnStop.setStyle("-fx-base: red; -fx-text-fill: white; -fx-font-weight: bold;");
        btnStop.setOnAction(e -> controller.stopSimulation());

        controls.getChildren().addAll(btnStart, btnStop);
        root.setBottom(controls);

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setTitle("Concurrency Visualizer Lab");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

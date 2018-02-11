package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class TimeSpinnerExample extends Application {
    @Override
    public void start(Stage primaryStage) {

        TimeSpinner spinner = new TimeSpinner();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        spinner.valueProperty().addListener((obs, oldTime, newTime) ->
                System.out.println(formatter.format(newTime)));

        StackPane root = new StackPane(spinner);
        Scene scene = new Scene(root, 350, 120);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.dmsoft.firefly.gui.components;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileChooserTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        Button button = new Button("Open");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Csv Choose");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
            File file = fileChooser.showOpenDialog(null);

        });
        hBox.getChildren().add(button);
        Scene scene = new Scene(hBox);
        primaryStage.setScene(scene);
        primaryStage.setWidth(150d);
        primaryStage.show();
    }
}

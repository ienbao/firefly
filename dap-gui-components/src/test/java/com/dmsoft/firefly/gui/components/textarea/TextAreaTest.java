package com.dmsoft.firefly.gui.components.textarea;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TextAreaTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        TextArea textArea = new TextArea();
        hBox.getChildren().add(textArea);
        Button button = new Button("ASFD");
        button.setOnAction(event -> {
            if (textArea.getStyleClass().contains("text-area-error")) {
                textArea.getStyleClass().remove("text-area-error");
            } else {
                textArea.getStyleClass().add("text-area-error");
            }
        });
        hBox.getChildren().add(button);
        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

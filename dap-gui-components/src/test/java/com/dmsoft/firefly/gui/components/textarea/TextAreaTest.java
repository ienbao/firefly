package com.dmsoft.firefly.gui.components.textarea;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TextAreaTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        TextArea textArea = new TextArea();
        hBox.getChildren().add(textArea);
        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

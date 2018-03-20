package com.dmsoft.firefly.gui.components.utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TextFieldDecoratorTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox();
        TextField tf = new TextField();
        ValidateRule rule = new ValidateRule();
        rule.setErrorStyle("text-field-error");
        rule.setEmptyErrorMsg("Empty!");
        rule.setRangErrorMsg("Range!");
        rule.setMaxValue(100.0d);
        rule.setMinValue(10.0d);
        rule.setPattern(ValidateUtils.DOUBLE_PATTERN);
        rule.setMaxLength(10);
        TextFieldDecorator.decorate(tf, rule);
        Button button = new Button("AA");
        hBox.getChildren().addAll(tf, button);
        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

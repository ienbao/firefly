package com.dmsoft.firefly.gui.components.validate;

import com.dmsoft.firefly.gui.components.utils.ValidateUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ValidateTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tabs");
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 250, Color.WHITE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        VBox vBox = new VBox();
        vBox.setSpacing(20);
        TextField textField = new TextField();

        ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
        textField.textProperty().addListener((obVal, oldVal, newVal) -> {
            ValidateUtil.validateIsNotEmpty(newVal, textField);
            ValidateUtil.validateSizeMinOrMax(newVal, textField, 1, 10);
        });
        textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
            ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
            ValidateUtil.validateSizeMinOrMax(textField.getText(), textField, 1, 10);
        });


        TextField textField1 = new TextField();
        ValidateUtil.validateIsNotEmpty(textField1.getText(), textField);
        textField1.textProperty().addListener((obVal, oldVal, newVal) -> {
            ValidateUtil.validateIsNotEmpty(newVal, textField1);
        });
        textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
            ValidateUtil.validateIsNotEmpty(textField1.getText(), textField1);
        });

        ObservableList<String> datas = FXCollections.observableArrayList("", "A","B");

        ComboBox comboBox = new ComboBox();
        comboBox.setItems(datas);

        ValidateUtil.validateIsNotEmpty(comboBox.getValue(), comboBox);
        comboBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
                ValidateUtil.validateIsNotEmpty(newVal, comboBox);
        });
        comboBox.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                ValidateUtil.validateIsNotEmpty(comboBox.getValue(), comboBox);
        });


        vBox.getChildren().addAll(textField, textField1, comboBox);
        root.getChildren().addAll(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

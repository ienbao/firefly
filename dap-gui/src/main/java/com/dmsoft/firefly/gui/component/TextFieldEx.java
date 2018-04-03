package com.dmsoft.firefly.gui.component;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


/**
 * Created by Julia.Zhou on 2018/4/3.
 */
public class TextFieldEx extends HBox {
    private TextField textField;
    private Label label;
    private ImageView userIcon = ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_user.png"));
    private ImageView passwordIcon = ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_password.png"));

    public TextFieldEx() {
        textField = new TextField();
        textField.setPromptText("Filter");
        textField.getStyleClass().add("text-field-filter");
        textField.setPrefHeight(26);
        label = new Label("", userIcon);
        label.setAlignment(Pos.CENTER);
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setPrefSize(26, 26);
        label.setMinSize(26, 26);
        this.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color: #DCDCDC");
        this.getChildren().add(textField);
        this.getChildren().add(label);
        HBox.setHgrow(textField, Priority.ALWAYS);
    }

    public TextField getTextField() {
        return textField;
    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        super.setPrefSize(prefWidth, prefHeight);
        label.setPrefSize(22, prefHeight);
        textField.setPrefSize(prefWidth - 22, prefHeight - 2);
    }

}

package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


/**
 * Created by GuangLi on 2018/3/2.
 */
public class TextFieldPassword extends HBox {
    private PasswordField textField;
    private Label label;
    private ImageView searchIcon = ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_password.png"));

    /**
     * constructor
     */
    public TextFieldPassword() {
        textField = new PasswordField();
        textField.getStyleClass().add("text-field-login");
        textField.setPrefHeight(24);
        label = new Label("", searchIcon);
        textField.textProperty().addListener((ov, s1, s2) -> {
            if (DAPStringUtils.isBlank(s2) && label.getGraphic() != searchIcon) {
                label.setGraphic(searchIcon);
            }
        });
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(24, 24);
        label.setMinSize(24, 24);
        this.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color: #DCDCDC");
        this.getChildren().add(label);
        this.getChildren().add(textField);
        HBox.setHgrow(textField, Priority.ALWAYS);
    }

    public TextField getTextField() {
        return textField;
    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        super.setPrefSize(prefWidth, prefHeight);
        label.setPrefSize(24, prefHeight);
        textField.setPrefSize(prefWidth - 24, prefHeight - 2);
    }

}

package com.dmsoft.firefly.gui.components.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


/**
 * Created by GuangLi on 2018/3/2.
 */
public class TextFieldFilter extends HBox {
    private TextField textField;
    private Label label;

    public TextFieldFilter() {
        textField = new TextField();
        textField.setPromptText("Filter");
//        textField.setStyle("-fx-border-width: 1 1 1 1;-fx-border-style: dotted");
        textField.getStyleClass().add("text-field-filter");
        textField.setPrefSize(160, 20);
        label = new Label("", ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(22, 22);
        label.setMinSize(22, 22);
        this.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color: #DCDCDC");
        this.getChildren().add(textField);
        this.getChildren().add(label);
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

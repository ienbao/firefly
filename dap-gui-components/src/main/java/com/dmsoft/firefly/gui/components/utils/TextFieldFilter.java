package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


/**
 * Created by GuangLi on 2018/3/2.
 */
public class TextFieldFilter extends HBox {
    private TextField textField;
    private Label label;
    private ImageView searchIcon = ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png"));
    private ImageView clearIcon = ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_click.png"));

    /**
     * constructor
     */
    public TextFieldFilter() {
        textField = new TextField();//创建文本域
        textField.setPromptText("Filter");//设置提示文字
//        textField.setStyle("-fx-border-width: 1 1 1 1;-fx-border-style: dotted");
        textField.getStyleClass().add("text-field-filter");
        textField.setPrefHeight(20);
        label = new Label("", searchIcon);
        label.setOnMouseClicked(event -> {
            if (label.getGraphic() == clearIcon) {
                textField.setText("");
            }
        });
        textField.textProperty().addListener((ov, s1, s2) -> {
            if (DAPStringUtils.isNotBlank(s2) && label.getGraphic() != clearIcon) {
                label.setGraphic(clearIcon);
            } else if (DAPStringUtils.isBlank(s2) && label.getGraphic() != searchIcon) {
                label.setGraphic(searchIcon);
            }
        });
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(20, 20);
        label.setMinSize(20, 20);
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

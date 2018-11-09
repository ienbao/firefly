package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.io.IOException;


/**
 * Author: draven.xu
 * Date: 2018/11/9 09:45
 * Description:
 */
public class TextFieldFilterDemo extends Region {

    private TextField textField;
    private ImageView imageView;
    private Label label;

    public TextFieldFilterDemo() throws IOException {

        initView();
        initEvent();

    }

    private void initView() throws IOException {
        HBox hBox = FXMLLoader.load(getClass().getClassLoader().getResource(
                "view/text_field_filter.fxml"));
        getStylesheets().add("css/redfall/main.css");
        getChildren().add(hBox);

        for(Node node : hBox.getChildren()){
            if(node instanceof TextField){
                textField = (TextField) node;
                continue;
            }
            if(node instanceof Label){
                label = (Label) node;
                imageView = (ImageView) label.getGraphic();
            }
        }
    }

    private void initEvent(){
        label.setOnMouseClicked(event -> {
            textField.setText("");
        });

        textField.textProperty().addListener((ov, s1, s2) -> {
            imageView.getStyleClass().clear();
            if (DAPStringUtils.isNotBlank(s2)) {
                imageView.getStyleClass().add("text-field-filter-clear");
            } else if (DAPStringUtils.isBlank(s2)) {
                imageView.getStyleClass().add("text-field-filter-search");
            }
        });
    }

    public TextField getTextField() {
        return textField;
    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        super.setPrefSize(prefWidth, prefHeight);
        label.setPrefSize(label.getPrefWidth(), prefHeight);
        textField.setPrefSize(prefWidth - label.getPrefWidth(), prefHeight);
    }

}

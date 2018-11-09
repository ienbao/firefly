package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Author: draven.xu
 * Date: 2018/11/9 09:45
 * Description:
 */
public class TextFieldFilterDemo extends HBox implements Initializable {

    @FXML
    private TextField textField;
    @FXML
    private ImageView imageView;
    @FXML
    private Label label;

    public TextFieldFilterDemo() throws IOException {
        initView();
    }

    private void initView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getClassLoader().getResource("view/text_field_filter.fxml"));
        getStylesheets().add("css/redfall/main.css");
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
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
    protected void setWidth(double prefWidth) {
        super.setWidth(prefWidth);
        label.setPrefWidth(label.getPrefWidth());
        textField.setPrefWidth(prefWidth - label.getPrefWidth());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initEvent();
    }
}

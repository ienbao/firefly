package com.dmsoft.firefly.gui.utils;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TipMessage extends VBox implements Initializable {
    @FXML
    private Label iconLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Button closeButton;
    @FXML
    private Label contentLabel;
    @FXML
    private Label eventLabel;

    public TipMessage() throws IOException {
        init();
    }
    private void init() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/tip_message.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        closeButton.setOnAction(event -> {
            this.setVisible(false);
        });
    }

    public void setType(String title) {
        this.clear();
        this.setVisible(true);
        if(title.equals("Success")){
            this.getStyleClass().add("tipMessage-success");
            iconLabel.getStyleClass().add("tipMessage-success-mark");
        }else{
            this.getStyleClass().add("tipMessage-warn");
            iconLabel.getStyleClass().add("tipMessage-warn-mark");
        }
        titleLabel.setText(title);
    }

    public void setMessage(String msg) {
        contentLabel.setText(msg);
    }

    public void setLinkMessage(String linkMsg) {
        eventLabel.setText(linkMsg);
    }

    public void setMouseEvent(EventHandler<MouseEvent> linkEvent) {
        eventLabel.setOnMouseClicked(linkEvent);
    }

    private void clear() {
        this.getStyleClass().clear();
        iconLabel.getStyleClass().clear();
        titleLabel.setText("");
        contentLabel.setText("");
        eventLabel.setText("");
        eventLabel.setOnMouseClicked(null);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setVisible(false);
    }

}

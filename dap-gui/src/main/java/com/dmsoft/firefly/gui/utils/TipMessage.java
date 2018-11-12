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

    public void showSuccessMsg(String title, String msg) {
        this.clear();
        this.setVisible(true);
        this.getStyleClass().add("tipMessage-success");
        iconLabel.getStyleClass().add("tipMessage-success-mark");
        titleLabel.setText(title);
        contentLabel.setText(msg);
    }

    public void showWarnMsg(String title, String msg, String linkMsg, EventHandler<MouseEvent> linkEvent) {
        this.clear();
        this.setVisible(true);
        this.getStyleClass().add("tipMessage-warn");
        iconLabel.getStyleClass().add("tipMessage-warn-mark");
        titleLabel.setText(title);
        contentLabel.setText(msg);
        eventLabel.setText(linkMsg);
        eventLabel.setOnMouseClicked(linkEvent);
    }
    public void showWarnMsg(String title, String msg) {
        this.clear();
        this.setVisible(true);
        this.getStyleClass().add("tipMessage-warn");
        iconLabel.getStyleClass().add("tipMessage-warn-mark");
        titleLabel.setText(title);
        contentLabel.setText(msg);
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

package com.dmsoft.firefly.gui.components.messagetip;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;


/**
 * Created by Ethan.Yang on 2017/3/17.
 */
public class MessageTipControl {
    private static final Logger logger = LoggerFactory.getLogger(MessageTipControl.class);

    @FXML
    private GridPane messageTip;

    @FXML
    private Button closeBtn;

    @FXML
    private Label titleLbl;

    @FXML
    private Label iconLbl;

    @FXML
    private Label contentLbl;

    @FXML
    private Button linkBtn;

    @FXML
    private void initialize() {
        linkBtn.setVisible(false);
        messageTip.getRowConstraints().get(2).setMinHeight(10);
        messageTip.getRowConstraints().get(2).setPrefHeight(10);
        messageTip.getRowConstraints().get(2).setMaxHeight(10);
    }


    public GridPane initInfo(Popup popup, String title, String msg) {
        clearAll();
        this.closeBtnEvent(popup);
        titleLbl.setText(title);
        contentLbl.setText(msg);
        messageTip.getStyleClass().add("message-tip-info");
        iconLbl.getStyleClass().add("message-tip-info-mark");
        return messageTip;
    }

    public GridPane initInfo(Popup popup, String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent) {
        initInfo(popup, title, msg);
        initLinkBtn(linkMsg, linkEvent);
        return messageTip;
    }

    public GridPane initWarn(Popup popup, String title, String msg) {
        clearAll();
        this.closeBtnEvent(popup);
        titleLbl.setText(title);
        contentLbl.setText(msg);
        messageTip.getStyleClass().add("message-tip-warn");
        iconLbl.getStyleClass().add("message-tip-warn-mark");
        return messageTip;
    }

    public GridPane initWarn(Popup popup, String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent) {
        initWarn(popup, title, msg);
        initLinkBtn(linkMsg, linkEvent);
        return messageTip;
    }

    public GridPane initNormal(Popup popup, String title, String msg) {
        this.clearAll();
        this.closeBtnEvent(popup);
        titleLbl.setText(title);
        contentLbl.setText(msg);
        messageTip.getStyleClass().add("message-tip-normal");
        iconLbl.getStyleClass().add("message-tip-normal-mark");
        return messageTip;
    }

    public GridPane initNormal(Popup popup, String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent) {
        initNormal(popup, title, msg);
        initLinkBtn(linkMsg, linkEvent);
        return messageTip;
    }

    private void initLinkBtn(String linkMsg, EventHandler<ActionEvent> linkEvent) {
        if (DAPStringUtils.isNotBlank(linkMsg)) {
            messageTip.getRowConstraints().get(2).setMinHeight(32);
            messageTip.getRowConstraints().get(2).setPrefHeight(32);
            messageTip.getRowConstraints().get(2).setMaxHeight(32);
            linkBtn.setText(linkMsg);
            linkBtn.setVisible(true);
            linkBtn.setOnAction(linkEvent);
        }
    }

    private void clearAll() {
        titleLbl.setText("");
        contentLbl.setText("");
        messageTip.getStyleClass().removeAll("message-tip-info", "message-tip-warn", "message-tip-normal");
        iconLbl.getStyleClass().removeAll("message-tip-info-mark", "message-tip-warn-mark", "message-tip-normal-mark");
    }

    private void closeBtnEvent(Popup popup) {
        closeBtn.setOnAction(event -> {
            popup.hide();
        });
    }

    public Button getLinkBtn() {
        return linkBtn;
    }

    public void setLinkBtn(Button linkBtn) {
        this.linkBtn = linkBtn;
    }
}

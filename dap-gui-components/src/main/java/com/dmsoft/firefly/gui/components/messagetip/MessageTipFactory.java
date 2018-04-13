package com.dmsoft.firefly.gui.components.messagetip;
/*
 * Copyright (C) 2017. For Intelligent Group.
 */

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;

import java.io.IOException;

/**
 * Created by Julia.Zhou on 2018/02/28.
 */
public class MessageTipFactory {
    private static MessageTipControl messageTipControl;
    private static Popup popup;
    private static GridPane messageTipPane;

    public static Popup getSuccessTip(String title, String msg) {
        init();
        messageTipPane = messageTipControl.initInfo(popup, title, msg);
        return popup;
    }

    public static Popup getSuccessTip(String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent) {
        init();
        messageTipPane = messageTipControl.initInfo(popup, title, msg, linkMsg, linkEvent);
        return popup;
    }

    public static Popup getWarnTip(String title, String msg) {
        init();
        messageTipPane = messageTipControl.initWarn(popup, title, msg);
        return popup;
    }

    public static Popup getWarnTip(String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent) {
        init();
        messageTipPane = messageTipControl.initWarn(popup, title, msg, linkMsg, linkEvent);
        return popup;
    }

    public static Popup getNormalTip(String title, String msg) {
        init();
        messageTipPane = messageTipControl.initNormal(popup, title, msg);
        return popup;
    }

    public static Popup getNormalTip(String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent) {
        init();
        messageTipPane = messageTipControl.initNormal(popup, title, msg, linkMsg, linkEvent);
        return popup;
    }

    private static void init() {
        try {
            popup = new Popup();
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/message_tip.fxml");
            Pane pane = fxmlLoader.load();
            pane.getStylesheets().addAll(WindowFactory.checkStyles());
            messageTipControl = fxmlLoader.getController();
            popup.getContent().add(pane);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static GridPane getMessageTipPane() {
        return messageTipPane;
    }
}

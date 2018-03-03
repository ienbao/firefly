/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.components.utils;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.Stage;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by Guang.Li on 2018/3/3.
 */
public class NumberTextField extends TextField {
    private Tooltip tip = new Tooltip("Input number");

    public NumberTextField() {
//        tip.setAutoHide(true);
//        tip.autoHideProperty().
        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String dif = "";
                if (newValue.length() > oldValue.length()) {
                    String text = getText();
                    Pattern pattern = Pattern.compile("^[+-]?\\d*[.]?\\d*$");
                    if (!pattern.matcher(text).matches()) {
//                        setTooltip(tip);
//                        tip.show(new Stage());
                        if (!tip.isShowing()) {
                            Bounds bounds = localToScreen(getBoundsInLocal());
                            tip.show(getParent(), bounds.getMinX(), bounds.getMinY() - 35);
                            Thread thread = new Thread(() -> {
                                try {
                                    Thread.sleep(2000);
                                    if (tip.isShowing()) {
                                        Platform.runLater(() -> tip.hide());
                                    }
                                } catch (Exception exp) {
                                    exp.printStackTrace();
                                }
                            });
                            thread.setDaemon(true);
                            thread.start();
                        }
                        setText(oldValue);
                    } else {
                        tip.hide();
                    }
                }
            }
        });
    }
}

/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.components.utils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Created by Garen.Pang on 2018/3/16.
 */
public class DecoratorTextFiled extends TextField {

    public static final int DEFAULT_TEXT_LENGTH = 255;
    private int fixedLength;
    private Tooltip tip = new Tooltip("Exceeding limit length.");

    public DecoratorTextFiled() {
        this("");
    }

    public DecoratorTextFiled(String text) {
        super(text);
        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > (fixedLength == 0 ? DEFAULT_TEXT_LENGTH : fixedLength)) {
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
                        thread.start();
                    }
                    setText(oldValue);
                } else {
//                    tip.hide();
                }
            }
        });
    }

    public void setFixedLength(int length) {
        fixedLength = length;
    }
}

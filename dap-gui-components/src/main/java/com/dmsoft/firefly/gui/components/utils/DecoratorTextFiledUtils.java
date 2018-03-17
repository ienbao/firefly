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
public class DecoratorTextFiledUtils {

    public static final int DEFAULT_TEXT_LENGTH = 255;

    public static TextField decoratorFixedLengthTextFiled(TextField textField) {
        return decoratorFixedLengthTextFiled(textField, DEFAULT_TEXT_LENGTH);
    }

    public static TextField decoratorFixedLengthTextFiled(TextField textField, int length) {

        Tooltip tip = new Tooltip("Exceeding limit length.");
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > length) {
                    if (!tip.isShowing()) {
                        Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                        tip.show(textField.getParent(), bounds.getMinX(), bounds.getMinY() - 35);
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
                    textField.setText(oldValue);
                } else {
//                    tip.hide();
                }
            }
        });
        return textField;
    }
}

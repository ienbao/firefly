package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.controller.ChangePasswordController;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class ChangePassTextFilter extends HBox {
    private PasswordField textField ;
    private static String errorStyle = "text-field-error";
    private ChangePasswordController changePasswordController;

    public ChangePassTextFilter() {
        textField = new PasswordField();
       // getStyleClass().add("text-field-error");
        textField.setPrefHeight(24);
        textField.setPromptText(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_PROMPT"));

        textField.setOnMouseEntered(event -> {
            validate(textField.getText());
        });

        textField.textProperty().addListener((obVal, oldVal, newVal) -> {
            validate(newVal);
        });
        this.getChildren().add(textField);
        HBox.setHgrow(textField, Priority.ALWAYS);

    }

    public TextField getTextField() {
        return textField;
    }

    @Override
    public void setPrefSize(double prefWidth, double prefHeight) {
        super.setPrefSize(prefWidth, prefHeight);
        textField.setPrefSize(prefWidth - 24, prefHeight - 2);
    }

    public void validate(String msg) {
        if (DAPStringUtils.isBlank(msg)) {
            getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(textField, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_EMPTY"));
        } else {
            TooltipUtil.uninstallWarnTooltip(textField);
            getStyleClass().removeAll(errorStyle);
            if (DAPStringUtils.isNotBlank(msg) && (msg.length() < 6 || msg.length() > 12)) {
                getStyleClass().add(errorStyle);
                TooltipUtil.installWarnTooltip(textField, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_LENGTH"));
            } else {
//                if(changePasswordController.isFlag()){
//                    getStyleClass().add(errorStyle);
//                    TooltipUtil.installWarnTooltip(textField, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_LENGTH_ERROR"));
//                }else{
                    getStyleClass().removeAll(errorStyle);
                    TooltipUtil.uninstallWarnTooltip(textField);
//                }


            }
        }
    }

}

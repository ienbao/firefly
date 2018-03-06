package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.utils.StringUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class ChangePasswordController {

    private static String errorStyle = "text-field-error";

    private UserService userService = RuntimeContext.getBean(UserService.class);

    @FXML
    private TextField txtOldPassword;

    @FXML
    private TextField txtNewPassword;

    @FXML
    private TextField txtConfirmPassword;

    @FXML
    private Button btnOK;

    @FXML
    private void initialize() {
        initEvent();
    }

    private void initEvent() {
        btnOK.setOnAction(event -> {
            userService.updatePassword("admin", txtOldPassword.getText(), txtNewPassword.getText());
        });

        txtOldPassword.setOnMouseEntered(event -> {
            validate(txtOldPassword.getText(), txtOldPassword);
        });

        txtOldPassword.textProperty().addListener((obVal, oldVal, newVal) -> {
            validate(newVal, txtOldPassword);
        });

        txtNewPassword.setOnMouseEntered(event -> {
            validate(txtNewPassword.getText(), txtNewPassword);
            validateConfirm(txtNewPassword.getText(), txtConfirmPassword.getText(), txtConfirmPassword);
        });
        txtNewPassword.textProperty().addListener((obVal, oldVal, newVal) -> {
            validate(newVal, txtNewPassword);
            validateConfirm(newVal, txtConfirmPassword.getText(), txtConfirmPassword);
        });

        txtConfirmPassword.setOnMouseEntered(event -> {
            validate(txtConfirmPassword.getText(), txtConfirmPassword);
            validateConfirm(txtNewPassword.getText(), txtConfirmPassword.getText(), txtConfirmPassword);

        });
        txtConfirmPassword.textProperty().addListener((obVal, oldVal, newVal) -> {
            validate(newVal, txtConfirmPassword);
            validateConfirm(txtNewPassword.getText(), newVal, txtConfirmPassword);
        });
    }

    private void validate(String msg, Node node) {
        if (StringUtils.isBlank(msg)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_EMPTY"));
        } else {
            TooltipUtil.uninstallWarnTooltip(node);
            node.getStyleClass().removeAll(errorStyle);
            if (StringUtils.isNotBlank(msg) && (msg.length() < 6 || msg.length() >= 13)) {
                node.getStyleClass().add(errorStyle);
                TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_LENGTH"));
            } else {
                node.getStyleClass().removeAll(errorStyle);
                TooltipUtil.uninstallWarnTooltip(node);
            }
        }
    }

    private void validateConfirm(String newPassword, String confirmPassword, Node node) {
        if (StringUtils.isBlank(newPassword) && StringUtils.isBlank(confirmPassword)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_EMPTY"));
        } else if (StringUtils.isNotBlank(newPassword) && StringUtils.isNotBlank(confirmPassword) && (newPassword.length() < 6 || newPassword.length() >= 13) && (confirmPassword.length() < 6 || confirmPassword.length() >= 13)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_LENGTH"));
        } else if (!newPassword.equals(confirmPassword)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_CONFIRM"));
        }  else {
            node.getStyleClass().removeAll(errorStyle);
            TooltipUtil.uninstallWarnTooltip(node);
        }
    }
}

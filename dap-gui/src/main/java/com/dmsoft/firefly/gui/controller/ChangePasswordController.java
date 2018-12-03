package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Julia.Zhou on 2018/2/11.
 */
@Component
public class ChangePasswordController {

    private static String errorStyle = "text-field-error";

    @Autowired
    private UserService userService;

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
        txtOldPassword.setPromptText(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_PROMPT"));
        txtNewPassword.setPromptText(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_PROMPT"));
        txtConfirmPassword.setPromptText(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_PROMPT"));
        initEvent();
    }

    private void initEvent() {
        UserModel userModel = UserModel.getInstance();
        btnOK.setOnAction(event -> {
            if (txtOldPassword.getStyleClass().contains(errorStyle)
                    || txtNewPassword.getStyleClass().contains(errorStyle)
                    || txtConfirmPassword.getStyleClass().contains(errorStyle)) {
                return;
            }
            try {
                if (userService.updatePassword(userModel.getUser().getUserName(), txtOldPassword.getText(), txtNewPassword.getText())) {
                    GuiFxmlAndLanguageUtils.buildChangePasswordBackDialog();
                } else {
                    WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_CHECK_PASSWORD"));
                }
            } catch (Exception e) {
                WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_FAIL"));
            }
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
        if (DAPStringUtils.isBlank(msg)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_EMPTY"));
        } else {
            TooltipUtil.uninstallWarnTooltip(node);
            node.getStyleClass().removeAll(errorStyle);
            if (DAPStringUtils.isNotBlank(msg) && (msg.length() < 6 || msg.length() >= 13)) {
                node.getStyleClass().add(errorStyle);
                TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_LENGTH"));
            } else {
                node.getStyleClass().removeAll(errorStyle);
                TooltipUtil.uninstallWarnTooltip(node);
            }
        }
    }

    private void validateConfirm(String newPassword, String confirmPassword, Node node) {
        if (DAPStringUtils.isBlank(newPassword) && DAPStringUtils.isBlank(confirmPassword)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_EMPTY"));
        } else if (DAPStringUtils.isNotBlank(newPassword) && DAPStringUtils.isNotBlank(confirmPassword) && (newPassword.length() < 6
                || newPassword.length() >= 13) && (confirmPassword.length() < 6 || confirmPassword.length() >= 13)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_LENGTH"));
        } else if (!newPassword.equals(confirmPassword)) {
            node.getStyleClass().add(errorStyle);
            TooltipUtil.installWarnTooltip(node, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_VALIDATE_CONFIRM"));
        } else {
            node.getStyleClass().removeAll(errorStyle);
            TooltipUtil.uninstallWarnTooltip(node);
        }
    }
}

package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginController {
    private UserService userService = RuntimeContext.getBean(UserService.class);
    @FXML
    private TextField userNameTxt;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    @FXML
    private void initialize() {
        loginBtn.setOnAction(event -> {
            loginBtn.getStyleClass().add("btn-primary-loading");
            if (true) {
                loginBtn.getStyleClass().removeAll("btn-primary-loading");
                StageMap.showStage(GuiConst.PLARTFORM_STAGE_MAIN);
                StageMap.getStage(GuiConst.PLARTFORM_STAGE_LOGIN).close();
            }
        });
    }

    private boolean doLogin() {
        UserDto userDto = userService.validateUser(userNameTxt.getText(), passwordField.getText());
        if (userDto != null) {
            return true;
        } else {
            loginBtn.getStyleClass().removeAll("btn-primary-loading");
        }
        return false;

    }

}

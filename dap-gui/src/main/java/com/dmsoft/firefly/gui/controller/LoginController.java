package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class LoginController {
    private UserService userService = RuntimeContext.getBean(UserService.class);

    @FXML
    private HBox loginFailHbox;

    @FXML
    private ImageView loginImageView;

    @FXML
    private TextField userNameTxt;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    @FXML
    private void initialize() {
        ImageView imageReset = new ImageView(new Image("/images/icon_choose_one_gray.png"));
        imageReset.setFitHeight(16);
        imageReset.setFitWidth(16);
/*
        userNameTxt.set(imageReset);
*/
        loginImageView.setImage(new Image("/images/top_title_logo.png"));
        loginBtn.setOnAction(event -> {
            loginBtn.getStyleClass().add("btn-primary-loading");
            if (doLogin()) {
                UserModel userModel = UserModel.getInstance();
                if (userModel != null) {
                    StageMap.getStage(GuiConst.PLARTFORM_STAGE_LOGIN).close();
                    MenuFactory.getMainController().resetMain();
                    MenuFactory.getAppController().updateLoginMenuBtn();
                }
                loginBtn.getStyleClass().removeAll("btn-primary-loading");
            }
        });
    }

    private boolean doLogin() {
        loginFailHbox.getChildren().clear();
        UserDto userDto = userService.validateUser(userNameTxt.getText(), passwordField.getText());
        if (userDto != null) {
            UserModel userModel = UserModel.getInstance();
            userModel.setUser(userDto);
            return true;
        } else {
            loginBtn.getStyleClass().removeAll("btn-primary-loading");
            addErrorTip();
        }
        return false;
    }


    private void addErrorTip() {
        Stage loginStage = StageMap.getStage(GuiConst.PLARTFORM_STAGE_LOGIN);
        loginStage.setResizable(true);
        Label warnLbl = new Label();
        warnLbl.getStyleClass().add("tooltip-warn-svg");
        warnLbl.setPadding(new Insets(0, 10, 0, 10));

        Label warnLbl1 = new Label();
        warnLbl1.setPrefWidth(350);
        warnLbl1.setPrefHeight(30);
        warnLbl1.setMinHeight(30);
        warnLbl1.setMaxHeight(30);
        warnLbl1.getStyleClass().add("tooltip-warn");
        warnLbl1.setStyle("-fx-border-color: #dcdcdc transparent; -fx-border-width: 1 0 0 0");
        warnLbl1.setText(GuiFxmlAndLanguageUtils.getString("LOGIN_FAIL"));
        warnLbl1.setGraphic(warnLbl);
        warnLbl1.setContentDisplay(ContentDisplay.LEFT);
        loginFailHbox.setPrefHeight(30);
        loginFailHbox.setMinHeight(30);
        loginFailHbox.setMaxHeight(30);
        loginFailHbox.getChildren().add(warnLbl1);
        loginStage.setHeight(288);
        loginStage.setMaxHeight(288);
        loginStage.setMinHeight(288);
        loginStage.setResizable(false);
    }

}

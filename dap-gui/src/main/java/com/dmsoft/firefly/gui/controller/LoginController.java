package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.DecoratorTextFiledUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);


    @FXML
    private void initialize() {
        DecoratorTextFiledUtils.decoratorFixedLengthTextFiled(userNameTxt, 20);
        DecoratorTextFiledUtils.decoratorFixedLengthTextFiled(passwordField, 20);
        ImageView imageReset = new ImageView(new Image("/images/icon_choose_one_gray.png"));
        imageReset.setFitHeight(16);
        imageReset.setFitWidth(16);
        loginImageView.setImage(new Image("/images/top_title_logo.png"));
        loginBtn.setOnAction(event -> {
            loginBtn.getStyleClass().add("btn-primary-loading");
            if (doLogin()) {
                UserModel userModel = UserModel.getInstance();
                if (userModel != null) {
                    StageMap.getStage(GuiConst.PLARTFORM_STAGE_LOGIN).close();
                    MenuFactory.getAppController().resetMenu();
                    MenuFactory.getMainController().resetMain();
                }
                loginBtn.getStyleClass().removeAll("btn-primary-loading");
            }
        });
    }

    private boolean doLogin() {
        loginFailHbox.getChildren().clear();
        UserDto userDto = userService.validateUser(userNameTxt.getText(), passwordField.getText());
        if (userDto != null) {
            this.initEnvData(userDto);
            return true;
        } else {
            loginBtn.getStyleClass().removeAll("btn-primary-loading");
            addErrorTip();
        }
        return false;
    }

    private void initEnvData(UserDto userDto) {
        UserModel userModel = UserModel.getInstance();
        userModel.setUser(userDto);
        envService.setUserName(userDto.getUserName());

        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        if (languageType == null) {
            envService.setLanguageType(LanguageType.EN);
        }

        TemplateSettingDto templateSettingDto =  envService.findActivatedTemplate();
        String activeTemplateName = null;
       if (templateSettingDto == null || DAPStringUtils.isBlank(templateSettingDto.getName())) {
           envService.setActivatedTemplate(GuiConst.DEFAULT_TEMPLATE_NAME);
           activeTemplateName = GuiConst.DEFAULT_TEMPLATE_NAME;
       } else {
           activeTemplateName = templateSettingDto.getName();
       }

        List<String> projectName = envService.findActivatedProjectName();
        if (projectName != null && !projectName.isEmpty()) {
            Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(projectName);
            LinkedHashMap<String, TestItemWithTypeDto> itemWithTypeDtoMap = templateService.assembleTemplate(testItemDtoMap, activeTemplateName);
            envService.setTestItems(itemWithTypeDtoMap);
            envService.setActivatedProjectName(projectName);
        }
    }


    private void addErrorTip() {
        Stage loginStage = StageMap.getStage(GuiConst.PLARTFORM_STAGE_LOGIN);
        loginStage.setResizable(true);
        Label warnLbl = new Label();
        warnLbl.getStyleClass().add("icon-warn-svg");
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

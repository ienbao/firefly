package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.DecoratorTextFiledUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldPassword;
import com.dmsoft.firefly.gui.components.utils.TextFieldUser;
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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * controller for login windows
 *
 * @author Julia
 */
public class LoginController {

    @FXML
    public VBox loginPane;

    @FXML
    private HBox loginFailHbox;

    @FXML
    private TextFieldUser userNameTxt;

    @FXML
    private TextFieldPassword passwordField;

    @FXML
    private Button loginBtn;


    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private UserService userService = RuntimeContext.getBean(UserService.class);


    @FXML
    private void initialize() {
        DecoratorTextFiledUtils.decoratorFixedLengthTextFiled(userNameTxt.getTextField(), 20);
        DecoratorTextFiledUtils.decoratorFixedLengthTextFiled(passwordField.getTextField(), 20);
        resetLoginBtn();
        initLoginEvent();
    }

    private void initLoginEvent(){
        loginBtn.setOnAction(event -> {
            loginingBtn();
            loginFailHbox.getChildren().clear();
            this.doLogin();
        });
    }

    private void resetLoginBtn() {
        loginBtn.setText(GuiFxmlAndLanguageUtils.getString("LOGIN_BTN"));
        loginBtn.getStyleClass().removeAll("btn-primary-loading");
        loginBtn.setGraphic(null);
    }

    private void loginingBtn() {

        ImageView imageReset = new ImageView();
        imageReset.getStyleClass().add("gui-logining-image");
        // TODO: 2018/11/5 重新切图 
        imageReset.setFitHeight(16);
        imageReset.setFitWidth(16);

        loginBtn.setText(GuiFxmlAndLanguageUtils.getString("LOGINING_BTN"));
        loginBtn.setGraphic(imageReset);
        loginBtn.getStyleClass().add("btn-primary-loading");
    }

    private void doLogin() {
        Thread thread = new Thread(() -> {
            UserDto userDto = userService.validateUser(userNameTxt.getTextField().getText(), passwordField.getTextField().getText());
            if (userDto != null) {
                this.initEnvData(userDto);
                Platform.runLater(() -> {
                    // TODO: 2018/11/5 事件解耦 
                    MenuFactory.getAppController().resetMenu();
                    MenuFactory.getMainController().resetMain();
                    StageMap.getStage(GuiConst.PLARTFORM_STAGE_LOGIN).close();
                });
            } else {
                Platform.runLater(() -> {
                    resetLoginBtn();
                    addErrorTip();
                });
            }
        });
        thread.start();
    }

    private void initEnvData(UserDto userDto) {
        UserModel userModel = UserModel.getInstance();
        userModel.setUser(userDto);
        envService.setUserName(userDto.getUserName());

        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        if (languageType == null) {
            envService.setLanguageType(LanguageType.EN);
        }

        // TODO: 2018/11/5 业务代码分层不清晰
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        String activeTemplateName;
        if (templateSettingDto == null || DAPStringUtils.isBlank(templateSettingDto.getName())) {
            envService.setActivatedTemplate(GuiConst.DEFAULT_TEMPLATE_NAME);
            activeTemplateName = GuiConst.DEFAULT_TEMPLATE_NAME;
        } else {
            activeTemplateName = templateSettingDto.getName();
        }

        // TODO: 2018/11/5 业务代码分层不清晰
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

        Label warnIconLbl = new Label();
        warnIconLbl.getStyleClass().addAll("icon-warn-svg","gui-login-warm-icon");

        Label warnTipLbl = new Label();
        warnTipLbl.getStyleClass().addAll("tooltip-warn","gui-login-warm-tip");
        warnTipLbl.setText(GuiFxmlAndLanguageUtils.getString("LOGIN_FAIL"));
        warnTipLbl.setGraphic(warnIconLbl);
        warnTipLbl.setContentDisplay(ContentDisplay.LEFT);

        loginFailHbox.getChildren().add(warnTipLbl);

        loginStage.setHeight(288);
        loginStage.setMaxHeight(288);
        loginStage.setMinHeight(288);
        loginStage.setResizable(false);
    }

}

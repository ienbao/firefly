package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.core.utils.DapThreadPoolExecutor;
import com.dmsoft.firefly.gui.LodingButton;
import com.dmsoft.firefly.gui.components.utils.DecoratorTextFiledUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldPassword;
import com.dmsoft.firefly.gui.components.utils.TextFieldUser;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * controller for login windows
 *
 * @author Julia
 */
@Component
public class LoginController {

    @FXML
    public VBox loginPane;

    @FXML
    private Label loginFailLbl;

    @FXML
    private TextFieldUser userNameTxt;

    @FXML
    private TextFieldPassword passwordField;

    @FXML
    private LodingButton loginBtn;

    @Autowired
    private EnvService envService ;
    @Autowired
    private UserService userService ;


    @FXML
    private void initialize() {
        DecoratorTextFiledUtils.decoratorFixedLengthTextFiled(userNameTxt.getTextField(), 20);
        DecoratorTextFiledUtils.decoratorFixedLengthTextFiled(passwordField.getTextField(), 20);
        resetLoginBtn();
        registEvent();
    }

    /**
     * 注册监听事件
     */
    private void registEvent(){
        loginBtn.setOnAction(event -> {
            loginFailLbl.setVisible(false);
            this.doLogin();
        });

        EventContext eventContext = RuntimeContext.getBean(EventContext.class);
        eventContext.addEventListener(EventType.SYSTEM_LOGIN_FAIL_ACITON, event -> {
            resetLoginBtn();
            loginFailLbl.setVisible(true);
        });


        eventContext.addEventListener(EventType.SYSTEM_LOGIN_SUCCESS_ACTION, event -> {
            UserDto userDto = (UserDto) event.getMsg();
            this.initEnvData(userDto);
            resetLoginBtn();
        });
    }


    private void doLogin() {
        DapThreadPoolExecutor.execute(() -> {
            String username = userNameTxt.getTextField().getText();
            String password = passwordField.getTextField().getText();
            UserDto userDto = userService.validateUser(username, password);

            EventContext eventContext = RuntimeContext.getBean(EventContext.class);
            if (userDto != null) {
                //发送登陆成功消息
                eventContext.pushEvent(new PlatformEvent(EventType.SYSTEM_LOGIN_SUCCESS_ACTION, userDto));
            } else {
                //发送登陆失败消息
                eventContext.pushEvent(new PlatformEvent(EventType.SYSTEM_LOGIN_FAIL_ACITON, null));
            }
        });
    }


    private void initEnvData(UserDto userDto) {
        UserModel userModel = UserModel.getInstance();
        userModel.setUser(userDto);
        this.envService.setUserName(userDto.getUserName());
        String activeTemplateName = this.envService.findActivatedTemplateName();
        this.envService.initTestItem(activeTemplateName);
    }


    private void resetLoginBtn() {
        loginBtn.change(false);
        loginBtn.setText(GuiFxmlAndLanguageUtils.getString("LOGIN_BTN"));
    }

    private void loginingBtn() {
        loginBtn.change(true);
        loginBtn.setText(GuiFxmlAndLanguageUtils.getString("LOGINING_BTN"));
    }

}

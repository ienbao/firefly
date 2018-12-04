package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.ChangePassTextFilter;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Julia.Zhou on 2018/2/11.
 */
@Component
public class ChangePasswordController {

    private static String errorStyle = "text-field-error";
    private boolean flag ;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Autowired
    private UserService userService;// =  RuntimeContext.getBean(UserService.class);

    @Autowired
    private EventContext eventContext;
    @FXML
    private ChangePassTextFilter txtOldPassword;

    @FXML
    private ChangePassTextFilter txtNewPassword;

    @FXML
    private ChangePassTextFilter txtConfirmPassword;

    @FXML
    private Button btnOK;

    @FXML
    private void initialize() {
        initEvent();
    }
    private void initEvent() {
        UserModel userModel = UserModel.getInstance();
        //old密码错误
        eventContext.addEventListener(EventType.SYSTEM_OLDPASSWORD_NOT_CORRECT, event -> {
            WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_CHECK_PASSWORD"));
        });
        //修改密码失败
        eventContext.addEventListener(EventType.SYSTEM_OLDPASSWORD_NOT_CORRECT,event -> {
            WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD_FAIL"));
        });
        //修改密码成功
        eventContext.addEventListener(EventType.SYSTEM_CHANGE_PASSWORD_SUCCESS,event -> {
            GuiFxmlAndLanguageUtils.buildChangePasswordBackDialog();
        });
        btnOK.setOnAction(event -> {
            if (txtOldPassword.getStyleClass().contains(errorStyle) || txtNewPassword.getStyleClass().contains(errorStyle) || txtConfirmPassword.getStyleClass().contains(errorStyle)) {
                return;
            }
            try {
                //校验用户名，oldPassword，newPassword
                if (userService.updatePassword(userModel.getUser().getUserName(), txtOldPassword.getTextField().getText(), txtNewPassword.getTextField().getText())) {
                    //修改密码成功
                    eventContext.pushEvent(new PlatformEvent(EventType.SYSTEM_CHANGE_PASSWORD_SUCCESS,null));

                } else {
                    //old密码错误
                    eventContext.pushEvent(new PlatformEvent(EventType.SYSTEM_OLDPASSWORD_NOT_CORRECT,null));
                }
            } catch (Exception e) {
                //修改密码失败
                eventContext.pushEvent(new PlatformEvent(EventType.SYSTEM_CHANGE_PASSWORD_FAILE,null));
            }
        });
        txtNewPassword.setOnMouseEntered(event -> {
            validateConfirm(txtNewPassword.getTextField().getText(), txtConfirmPassword.getTextField().getText(), txtConfirmPassword);

        });
        txtNewPassword.getTextField().textProperty().addListener((obVal, oldVal, newVal) -> {
            validateConfirm(newVal, txtConfirmPassword.getTextField().getText(), txtConfirmPassword);
        });
        txtConfirmPassword.setOnMouseEntered(event -> {
            setFlag(true);
            validateConfirm(txtNewPassword.getTextField().getText(), txtConfirmPassword.getTextField().getText(), txtConfirmPassword);

        });
        txtConfirmPassword.getTextField().textProperty().addListener((obVal, oldVal, newVal) -> {
            validateConfirm(txtNewPassword.getTextField().getText(), newVal, txtConfirmPassword);
        });
    }
    private void validateConfirm(String newPassword, String confirmPassword, Node node) {
       boolean newPWDIsBlank =  DAPStringUtils.isBlank(newPassword);
       boolean configPWSIsBlank = DAPStringUtils.isBlank(confirmPassword);
        if (newPWDIsBlank && configPWSIsBlank) {
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

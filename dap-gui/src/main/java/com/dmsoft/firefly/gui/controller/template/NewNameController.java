package com.dmsoft.firefly.gui.controller.template;


import com.dmsoft.firefly.gui.components.utils.*;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


/**
 * Created by GuangLi on 2018/2/28.
 */
public class NewNameController {
    @FXML
    private Button ok, cancel;
    @FXML
    private TextField name;
    private String paneName = "";
    private String initName = "";

    @FXML
    private void initialize() {
        initEvent();
        name.setText(initName);
        ValidateRule rule = new ValidateRule();
        rule.setMaxLength(255);
        rule.setErrorStyle("text-field-error");
        rule.setValidateFunc(s -> !DAPStringUtils.isSpeChars4Mongo(s));
        rule.setEmptyErrorMsg(FxmlAndLanguageUtils.getString(ValidationAnno.GLOBAL_VALIDATE_NOT_BE_EMPTY));
        TextFieldWrapper.decorate(name, rule);
    }

    private void initEvent() {
        cancel.setOnAction(event -> {
            name.setText("");
            StageMap.closeStage(paneName);
        });
    }

    public void setPaneName(String paneName) {
        this.paneName = paneName;
    }

    public Button getOk() {
        return ok;
    }

    public TextField getName() {
        return name;
    }

    public void setInitName(String initName) {
        this.initName = initName;
    }

    /**
     * method to judge is error or not
     *
     * @return true : is error; false ; is not error
     */
    public boolean isError() {
        return name.getStyleClass().contains("text-field-error");
    }
}

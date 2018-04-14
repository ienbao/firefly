package com.dmsoft.firefly.gui.controller.template;


import com.dmsoft.firefly.gui.components.utils.*;
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

    @FXML
    private void initialize() {
        initEvent();
        ValidateRule rule = new ValidateRule();
        rule.setMaxLength(255);
        rule.setPattern("[A-Za-z0-9]+");
        rule.setErrorStyle("text-field-error");
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

    /**
     * method to judge is error or not
     *
     * @return true : is error; false ; is not error
     */
    public boolean isError() {
        return name.getStyleClass().contains("text-field-error");
    }
}

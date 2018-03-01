package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by GuangLi on 2018/2/28.
 */
public class NewTemplateController {
    @FXML
    private Button ok, cancel;
    @FXML
    private TextField name;

    private TemplateController templateController;
    @FXML
    private void initialize(){
        initEvent();
    }

    private void initEvent(){
        ok.setOnAction(event -> {
            if (StringUtils.isNotEmpty(name.getText())) {
                if (!templateController.getTemplateNames().contains(name.getText())){
                    templateController.getTemplateNames().add(name.getText());
                    templateController.initData();
                }
            }
            name.setText("");
            StageMap.closeStage("newTemplate");
        });
        cancel.setOnAction(event -> {
            name.setText("");
            StageMap.closeStage("newTemplate");
        });
    }

    public void setTemplateController(TemplateController templateController){
        this.templateController = templateController;
    }
}

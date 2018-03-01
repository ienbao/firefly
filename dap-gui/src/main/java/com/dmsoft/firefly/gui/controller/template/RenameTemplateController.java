package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by GuangLi on 2018/2/28.
 */
public class RenameTemplateController {
    @FXML
    private Button ok, cancel;
    @FXML
    private TextField name;
    private ObservableList<String> names;
    private String oldName = "";

    @FXML
    private void initialize() {
        name.setText(oldName);
        initEvent();
    }

    private void initEvent() {
        ok.setOnAction(event -> {
            StageMap.closeStage("renameTemplate");
            if (StringUtils.isNotEmpty(name.getText()) && !name.getText().equals(oldName)) {
                for (int i = 0; i < names.size(); i++) {
                    if (names.get(i).equals(oldName)) {
                        names.set(i, name.getText());
                    }
                }
            }
//            name.setText("");
        });
        cancel.setOnAction(event -> {
//            name.setText("");
            StageMap.closeStage("renameTemplate");
        });
    }

    public void setName(String oldName, ObservableList<String> names) {
        this.oldName = oldName;
        this.names = names;
    }
}

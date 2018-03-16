package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Created by GuangLi on 2018/3/8.
 */
public class GrrExportSettingController {
    @FXML
    private RadioButton tolerance;
    @FXML
    private RadioButton system;
    @FXML
    private VBox pane;
    @FXML
    private GridPane chartPane;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private CheckBox ec;
    @FXML
    private CheckBox esr;
    @FXML
    private CheckBox eds;

    private ToggleGroup group = new ToggleGroup();
    private Map<String, Boolean> data;

    @FXML
    private void initialize() {
        tolerance.setToggleGroup(group);
        tolerance.setSelected(true);
        system.setToggleGroup(group);
        initData();
        initEvent();
    }

    private void initData() {
        if (data != null) {
            pane.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    if (data.get(((CheckBox) node).getText()) != null) {
                        ((CheckBox) node).setSelected(data.get(((CheckBox) node).getText()));
                    }
                }
                if (node instanceof RadioButton) {
                    if (data.get(((RadioButton) node).getText()) != null) {
                        ((RadioButton) node).setSelected(data.get(((RadioButton) node).getText()));
                    }
                }
            });
            chartPane.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    if (data.get(((CheckBox) node).getText()) != null) {
                        ((CheckBox) node).setSelected(data.get(((CheckBox) node).getText()));
                    }
                }
            });
        }
    }

    private void initEvent() {
        eds.setOnAction(event -> {
            if (eds.isSelected()) {
                esr.setDisable(false);
                ec.setDisable(false);
                chartPane.getChildren().forEach(node -> {
                    if (node instanceof CheckBox) {
                        node.setDisable(false);
                    }
                });
            } else {
                esr.setDisable(true);
                ec.setDisable(true);
                chartPane.getChildren().forEach(node -> {
                    if (node instanceof CheckBox) {
                        node.setDisable(true);
                    }
                });
            }
        });
        ec.setOnAction(event -> {
            if (ec.isSelected()) {
                chartPane.getChildren().forEach(node -> {
                    if (node instanceof CheckBox) {
                        node.setDisable(false);
                    }
                });
            } else {
                chartPane.getChildren().forEach(node -> {
                    if (node instanceof CheckBox) {
                        node.setDisable(true);
                    }
                });
            }
        });
        ok.setOnAction(event -> {
            saveData();
            StageMap.closeStage("grrExportSetting");
        });
        cancel.setOnAction(event -> StageMap.closeStage("grrExportSetting"));
    }

    private void saveData() {
        if (data != null) {
            pane.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    data.put(((CheckBox) node).getText(), ((CheckBox) node).isSelected());
                }
                if (node instanceof RadioButton) {
                    data.put(((RadioButton) node).getText(), ((RadioButton) node).isSelected());

                }
            });
            chartPane.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    data.put(((CheckBox) node).getText(), ((CheckBox) node).isSelected());
                }
            });
        }
    }

    public void setData(Map<String, Boolean> data) {
        this.data = data;
    }
}

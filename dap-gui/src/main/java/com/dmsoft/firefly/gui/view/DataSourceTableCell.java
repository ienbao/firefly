package com.dmsoft.firefly.gui.view;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.controller.template.NewNameController;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
/**
 * 创建dataSource表格中每一行为一个组件
 * @author Tod
 */
public class DataSourceTableCell extends HBox implements Initializable {

    @FXML
    private Label textField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button rename;
    @FXML
    private Button deleteOne;

    private String renameStr = GuiFxmlAndLanguageUtils.getString(ResourceMassages.RENAME_DATA_SOURCE);
    private String delStr = GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_SOURCE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public DataSourceTableCell(ChooseTableRowData item) throws IOException {
        initView(item);
        initEvent();
    }

    private void initView(ChooseTableRowData item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getClassLoader().getResource("view/data_source_cell.fxml"));
        getStylesheets().add("css/platform_app.css");
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        textField.setText(item.getValue());
        if (item.isImport() || item.isError()) {
            textField.setDisable(true);
            item.getSelector().getCheckbox().setSelected(false);
            item.getSelector().getCheckbox().setDisable(true);
        } else {
            textField.setDisable(false);
            item.getSelector().getCheckbox().setDisable(false);
        }
        progressBar.setProgress(0);
        if (item.isError()) {
            progressBar.getStyleClass().setAll("progress-bar-lg-red");
        } else {
            progressBar.getStyleClass().setAll("progress-bar-lg-green");
        }
        TooltipUtil.installNormalTooltip(rename, renameStr);
        TooltipUtil.installNormalTooltip(deleteOne, delStr);
        rename.setVisible(false);
        deleteOne.setVisible(false);
        if (!item.isError()) {
            progressBar.setVisible(item.isImport());
        } else {
            progressBar.setVisible(true);
        }
        if (item.getProgress() != 0) {
            progressBar.setProgress(item.getProgress());
        }
        this.setHgrow(textField, Priority.ALWAYS);
        this.setHgrow(progressBar, Priority.NEVER);
        this.setHgrow(rename, Priority.NEVER);
        this.setHgrow(deleteOne, Priority.NEVER);
    }

    private void initEvent(){
        this.setOnMouseExited(event -> {
            this.rename.setVisible(false);
            this.deleteOne.setVisible(false);
        });
        this.setOnMouseEntered(event -> {
            this.rename.setVisible(true);
            this.deleteOne.setVisible(true);
        });
    }

    public Button getRename() {
        return rename;
    }

    public void setRename(Button rename) {
        this.rename = rename;
    }

    public Button getDeleteOne() {
        return deleteOne;
    }

    public void setDeleteOne(Button deleteOne) {
        this.deleteOne = deleteOne;
    }
}

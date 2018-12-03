package com.dmsoft.firefly.gui.view;

import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.event.DataSourceCellEvent;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.net.URL;
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

        rename.setOnAction(event -> {
            fireEvent(new DataSourceCellEvent(DataSourceCellEvent.RENAME));
        });

        deleteOne.setOnAction(event -> {
            fireEvent(new DataSourceCellEvent(DataSourceCellEvent.DELETE));
        });
    }
}

package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.plugin.spc.charts.SelectCallBack;
import com.dmsoft.firefly.plugin.spc.charts.model.CheckTableModel;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Popup;

import java.util.List;

/**
 * Created by cherry on 2018/2/8.
 */
public class ChartOperateButton extends Button {

    private Popup popup;
    private TableViewWrapper tableViewWrapper;
    private TableView<String> tableView;
    private CheckTableModel tableModel;

    public ChartOperateButton(String[] columns, int checkBoxIndex) {
        this("", columns, checkBoxIndex, true);
    }

    public ChartOperateButton(String[] columns, int checkBoxIndex, boolean selected) {
        this("", columns, checkBoxIndex, selected);
    }

    public ChartOperateButton(String name, String[] columns, int checkBoxIndex) {
        this(name, columns, checkBoxIndex, true);
    }

    public ChartOperateButton(String name, String[] columns, int checkBoxIndex, boolean selected) {
        super(name);
        tableView = new TableView<>();
        tableView.getStyleClass().add("table-no-header");
        tableView.skinProperty().addListener((a, b, newSkin) -> {
            TableHeaderRow headerRow = ((TableViewSkinBase) newSkin).getTableHeaderRow();
            headerRow.setPrefHeight(0);
        });
        tableModel = new CheckTableModel();
        tableModel.setDefaultSelect(selected);
        tableModel.setCheckIndex(checkBoxIndex);
        tableModel.getHeaderArray().addAll(columns);
        tableViewWrapper = new TableViewWrapper(tableView, tableModel);
        popup = new Popup();
        popup.getContent().addAll(tableViewWrapper.getWrappedTable());
        popup.setAutoHide(true);
        this.setStyle("-fx-border-width: 0px");
        this.setMaxWidth(25);
        this.setMinWidth(25);
        this.setPrefWidth(25);
        this.setMaxHeight(20);
        this.setMinHeight(20);
        this.setPrefHeight(20);
        Button button = this;
        this.setOnMousePressed(event -> {
            double x = event.getSceneX();
            double y = event.getSceneY();
            popup.show(button, x, y);
        });
    }

    public void setTableRowKeys(List<String> rowKeys) {
        tableModel.getRowKeyArray().addAll(rowKeys);
    }

    public void setTableViewSize(double width, double height) {
        tableView.setPrefWidth(width);
        tableView.setPrefHeight(height);
    }

    public void setTableViewColumnWidth(int columnIndex, double width) {
        if (columnIndex < tableView.getColumns().size()) {
            TableColumn column = tableView.getColumns().get(columnIndex);
            column.setPrefWidth(width);
        }
    }

    public void setSelectCallBack(SelectCallBack selectCallBack) {
        tableModel.setSelectCallBack(selectCallBack);
    }
}

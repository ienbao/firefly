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

    public final Double MAX_HEIGHT = 200.0;

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
        tableView.setMaxHeight(MAX_HEIGHT);
        this.setMaxWidth(20);
        this.setMinWidth(20);
        this.setPrefWidth(20);
        this.setMaxHeight(20);
        this.setMinHeight(20);
        this.setPrefHeight(20);
        this.getStyleClass().add("btn-icon-b");
        Button button = this;
        this.setOnMousePressed(event -> {
            Double preHeight = tableView.getPrefHeight();
            if (preHeight >= MAX_HEIGHT) {
                preHeight = MAX_HEIGHT;
            }
            double x = button.getScene().getWindow().getX() +
                    button.getScene().getX() + button.localToScene(0, 0).getX();
            double y = button.getScene().getWindow().getY() +
                    button.getScene().getY() + button.localToScene(0, 0).getY() - preHeight - 5;
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

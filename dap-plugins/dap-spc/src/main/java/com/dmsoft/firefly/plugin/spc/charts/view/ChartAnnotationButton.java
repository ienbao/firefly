package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.plugin.spc.charts.SelectCallBack;
import com.dmsoft.firefly.plugin.spc.charts.model.CheckTableModel;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;

import java.util.List;

/**
 * Created by cherry on 2018/2/27.
 */
public class ChartAnnotationButton extends Button {

    private Popup popup;
    private TextField itemFilter;
    private GridPane gridPane;
    private TableViewWrapper tableViewWrapper;
    private TableView<String> tableView;
    private CheckTableModel tableModel;

    private Button clearBtn;
    private Button editBtn;

    public ChartAnnotationButton(String[] columns, int checkBoxIndex) {
        this("", columns, checkBoxIndex, true);
    }

    public ChartAnnotationButton(String[] columns, int checkBoxIndex, boolean selected) {
        this("", columns, checkBoxIndex, selected);
    }

    public ChartAnnotationButton(String name, String[] columns, int checkBoxIndex) {
        this(name, columns, checkBoxIndex, true);
    }

    public ChartAnnotationButton(String name, String[] columns, int checkBoxIndex, boolean selected) {
        super(name);

        itemFilter = new TextField();
        itemFilter.setPromptText("Filter");
        itemFilter.setPrefWidth(100);
        tableView = new TableView<>();
        tableModel = new CheckTableModel();
        tableModel.setDefaultSelect(selected);
        tableModel.setCheckIndex(checkBoxIndex);
        tableModel.getHeaderArray().addAll(columns);
        tableViewWrapper = new TableViewWrapper(tableView, tableModel);
        gridPane = new GridPane();
        editBtn = new Button();
        clearBtn = new Button();
        editBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_cancel_tracing_point_normal.png")));
        clearBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_remove_tracing_point_normal.png")));
        gridPane.add(itemFilter, 0, 0, 4, 1);
        gridPane.add(tableViewWrapper.getWrappedTable(), 0, 2, 4, 1);
        gridPane.add(editBtn, 2, 3, 1, 1);
        gridPane.add(clearBtn, 3, 3, 1, 1);

        popup = new Popup();
        popup.getContent().addAll(gridPane);
        popup.setAutoHide(true);
        this.setStyle("-fx-border-width: 0px");
        this.setMaxWidth(25);
        this.setMinWidth(25);
        this.setPrefWidth(25);
        this.setMaxHeight(20);
        this.setMinHeight(20);
        this.setPrefHeight(20);
        javafx.scene.control.Button button = this;
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

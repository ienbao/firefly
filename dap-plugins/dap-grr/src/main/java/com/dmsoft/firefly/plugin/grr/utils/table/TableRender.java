package com.dmsoft.firefly.plugin.grr.utils.table;

import javafx.collections.ObservableList;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Created by cherry on 2018/3/13.
 */
public class TableRender {

    private TableView tableView;

    public TableRender(TableView tableView) {
        this.tableView = tableView;
    }

    public void buildEditCellByIndex(int index, TableCellCallBack callBack) {
        ObservableList<TableColumn> tableColumns = tableView.getColumns();
        tableColumns.get(index).setCellFactory(param -> {
            EditTableCell cell = new EditTableCell(index);
            cell.setCallBack(callBack);
            return cell;
        });
    }

    public void buildRadioCellByIndex(int index, TableCellCallBack callBack) {
        ObservableList<TableColumn> tableColumns = tableView.getColumns();
        tableColumns.get(index).setCellFactory(param -> {
            final RadioButtonTableCell cell = new RadioButtonTableCell<>();
            final RadioButton radio = (RadioButton) cell.getGraphic();
            radio.setOnAction(event -> {
                if (callBack != null) {
                    callBack.execute(cell);
                }
            });
            return cell;
        });
    }

    public void buildSpecialCellByIndex(int index, TableCellCallBack callBack) {

        ObservableList<TableColumn> tableColumns = tableView.getColumns();
        tableColumns.get(index).setCellFactory(param -> {
            TableCell cell = new TableCell() {
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        callBack.execute(this, item);
                        setText(item.toString());
                    }
                }
            };
            return cell;
        });
    }
}

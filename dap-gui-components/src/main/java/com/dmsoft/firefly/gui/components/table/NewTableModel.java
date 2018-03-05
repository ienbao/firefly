package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * basic model interface for table view
 *
 * @author Can Guan
 */
public interface NewTableModel {

    /**
     * method to get header array
     *
     * @return observable string list
     */
    ObservableList<String> getHeaderArray();

    /**
     * method to get cell data by row and column name
     *
     * @param rowKey     row key
     * @param columnName column name
     * @return table view data
     */
    ObjectProperty<String> getCellData(String rowKey, String columnName);

    /**
     * method to get row key string list
     *
     * @return observable string list
     */
    ObservableList<String> getRowKeyArray();

    /**
     * method to get column is editable or not
     *
     * @param columnName column name
     * @return true : editable, false : uneditable
     */
    boolean isEditableTextField(String columnName);

    /**
     * method to get is check box or not
     *
     * @param columnName column name
     * @return true : is check box; false : is not check box
     */
    boolean isCheckBox(String columnName);

    /**
     * method to get check value
     *
     * @param rowKey     row key
     * @param columnName column name
     * @return check property
     */
    ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName);

    /**
     * method to get all check value
     *
     * @param columnName column name
     * @return selected value
     */
    ObjectProperty<Boolean> getAllCheckValue(String columnName);

    /**
     * method to get list of menu event
     *
     * @return list of menu event
     */
    List<TableMenuRowEvent> getMenuEventList();

    /**
     * method to decorate table cell
     *
     * @param <T>       any type
     * @param rowKey    row key
     * @param column    column
     * @param tableCell table cell
     * @return table cell
     */
    <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell);

    /**
     * method to set all check box
     *
     * @param checkBox check box
     */
    void setAllCheckBox(CheckBox checkBox);

    /**
     * method to set table view
     *
     * @param tableView table view
     */
    void setTableView(TableView<String> tableView);
}

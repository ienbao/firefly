package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * basic model interface for table view
 *
 * @author Can Guan
 */
public interface TableModel {

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
    ObservableValue<String> getCellData(String rowKey, String columnName);

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

    /**
     * method to judege menu event enable or not
     *
     * @param rowKey row key
     * @return is enable or not
     */
    default boolean isMenuEventEnable(String rowKey) {
        return true;
    }

    /**
     * method to  judge text input error or not
     *
     * @param textField  text field
     * @param oldText    old text
     * @param newText    new text
     * @param rowKey     row key
     * @param columnName columnName
     * @return true : is error, false : no error
     */
    default boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        return false;
    }
}

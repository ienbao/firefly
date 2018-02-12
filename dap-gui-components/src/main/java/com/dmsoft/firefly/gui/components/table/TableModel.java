package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

/**
 * basic model interface for scalable table view, familiar to swing table model
 * !important : never return new object in {@code getHeaderArray} or {@code getRowKeyArray}
 *
 * @author Can Guan
 */
public interface TableModel {
    /**
     * method to get column header string list
     *
     * @return observable string list
     */
    ObservableList<String> getHeaderArray();

    /**
     * method to get cell data by row key and column name
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
     * method to get is modified or not
     *
     * @param rowKey     row key
     * @param columnName column name
     * @param value      value
     * @return true : is modified, false : is not modified
     */
    boolean isModified(String rowKey, String columnName, String value);

    /**
     * method to get is check or not
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
     * method to set all selected or not
     *
     * @param value      new value
     * @param columnName column name
     */
    void setAllSelected(boolean value, String columnName);

    /**
     * method to get all check value or not
     *
     * @param columnName column name
     * @return all check property
     */
    ObjectProperty<Boolean> getAllCheckValue(String columnName);
}

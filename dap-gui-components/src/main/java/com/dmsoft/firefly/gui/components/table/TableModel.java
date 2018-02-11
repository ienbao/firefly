package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

/**
 * basic model interface for scalable table view
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
}

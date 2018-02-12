package com.dmsoft.firefly.gui.components.table;

import javafx.scene.control.TableView;

import java.util.List;

/**
 * interface for build table view
 * @author Can Guan
 */
public abstract class AbstractTableViewWrapper {
    protected TableView<String> tableView;
    protected TableModel tableModel;

    /**
     * constructor
     *
     * @param tableView  table view
     * @param tableModel table model
     */
    public AbstractTableViewWrapper(TableView<String> tableView, TableModel tableModel) {
        this.tableView = tableView;
        this.tableModel = tableModel;
    }

    /**
     * method to get wrapped table (and reset table)
     *
     * @return table view
     */
    public abstract TableView getWrappedTable();

    /**
     * method to set edited style class
     *
     * @param styleClass list of style class
     */
    public abstract void addEditedCellStyleClass(List<String> styleClass);

    /**
     * method to change cell style class
     *
     * @param rowKey     row key
     * @param columnName column name
     * @param styleClass list of style class
     */
    public abstract void addCustomCellStyleClass(String rowKey, String columnName, List<String> styleClass);

    /**
     * method to remove custom cell style class
     *
     * @param rowKey     row key
     * @param columnName column name
     */
    public abstract void removeCustomCellStyleClass(String rowKey, List<String> columnName);

    /**
     * method to change row style class
     *
     * @param rowKey     row key
     * @param styleClass style class
     */
    public abstract void addCustomRowStyleClass(String rowKey, List<String> styleClass);

    /**
     * method to remove custom row style class
     *
     * @param rowKey row key
     */
    public abstract void removeCustomRowStyleClass(String rowKey);

    /**
     * method to change column style class
     *
     * @param columnName column name
     * @param styleClass style class
     */
    public abstract void addCustomColumnStyleClass(String columnName, List<String> styleClass);

    /**
     * method to remove column style class
     *
     * @param columnName style class
     */
    public abstract void removeCustomColumnStyleClass(String columnName);
}

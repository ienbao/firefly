package com.dmsoft.firefly.gui.components.table;

import javafx.scene.control.TableView;

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
}

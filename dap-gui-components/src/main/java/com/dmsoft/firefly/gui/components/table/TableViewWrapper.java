package com.dmsoft.firefly.gui.components.table;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * table wrapper class
 *
 * @author Can Guan
 */
public class TableViewWrapper extends AbstractTableViewWrapper {
    private Map<String, TableColumn<String, String>> columnMap;

    /**
     * constructor
     *
     * @param tableView  table view
     * @param tableModel table model
     */
    public TableViewWrapper(TableView<String> tableView, TableModel tableModel) {
        super(tableView, tableModel);
        init();
    }

    private void init() {
        this.columnMap = new LinkedHashMap<>();
        for (String s : tableModel.getHeaderArray()) {
            TableColumn<String, String> column = new TableColumn<>(s);
            column.setCellValueFactory(cell -> tableModel.getCellData(cell.getValue(), cell.getTableColumn().getText()));
            columnMap.put(s, column);
        }
        this.tableModel.getHeaderArray().addListener((ListChangeListener<String>) c -> {
            Platform.runLater(() -> {
                        while (c.next()) {
                            if (c.wasPermutated()) {
                                Map<String, TableColumn<String, String>> newMap = new LinkedHashMap<>();
                                for (String s : c.getList()) {
                                    newMap.put(s, columnMap.get(s));
                                }
                                columnMap = newMap;
                                this.tableView.getColumns().setAll(columnMap.values());
                            } else if (c.wasAdded()) {
                                Map<String, TableColumn<String, String>> newMap = new LinkedHashMap<>();
                                for (String s : c.getAddedSubList()) {
                                    TableColumn<String, String> column = new TableColumn<>(s);
                                    column.setCellValueFactory(cell -> tableModel.getCellData(cell.getValue(), cell.getTableColumn().getText()));
                                    newMap.put(s, column);
                                }
                                this.columnMap.putAll(newMap);
                                this.tableView.getColumns().addAll(c.getFrom(), newMap.values());
                            } else if (c.wasRemoved()) {
                                List<? extends String> removedHeaders = c.getRemoved();
                                List<TableColumn> removedColumns = new ArrayList<>();
                                for (Object o : removedHeaders) {
                                    TableColumn column = this.columnMap.remove(o.toString());
                                    if (column != null) {
                                        removedColumns.add(column);
                                    }
                                }
                                this.tableView.getColumns().removeAll(removedColumns);
                            }
                        }
                    }
            );
        });

        this.tableModel.getRowKeyArray().addListener((ListChangeListener<String>) c -> {
            Platform.runLater(() -> {
                        while (c.next()) {
                            if (c.wasPermutated()) {
                                this.tableView.getItems().setAll(c.getList());
                            } else if (c.wasAdded()) {
                                this.tableView.getItems().addAll(c.getFrom(), c.getAddedSubList());
                            } else if (c.wasRemoved()) {
                                this.tableView.getItems().remove(c.getFrom(), c.getTo());
                            }
                        }
                    }
            );
        });
        this.tableView.getItems().setAll(tableModel.getRowKeyArray());
        this.tableView.getColumns().setAll(columnMap.values());
    }

    @Override
    public TableView getWrappedTable() {
        return this.tableView;
    }
}

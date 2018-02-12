package com.dmsoft.firefly.gui.components.table;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

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
    private Map<String, TableColumn<String, ?>> columnMap;

    /**
     * constructor
     *
     * @param tableView  table view
     * @param tableModel table model
     */
    public TableViewWrapper(TableView<String> tableView, TableModel tableModel) {
        super(tableView, tableModel);

    }

    private void init() {
        this.columnMap = new LinkedHashMap<>();
        for (String s : tableModel.getHeaderArray()) {
            columnMap.put(s, initColumn(s));
        }
        this.tableModel.getHeaderArray().addListener((ListChangeListener<String>) c -> {
            Platform.runLater(() -> {
                        while (c.next()) {
                            if (c.wasPermutated()) {
                                Map<String, TableColumn<String, ?>> newMap = new LinkedHashMap<>();
                                for (String s : c.getList()) {
                                    newMap.put(s, columnMap.get(s));
                                }
                                columnMap = newMap;
                                this.tableView.getColumns().setAll(columnMap.values());
                            } else if (c.wasAdded()) {
                                Map<String, TableColumn<String, ?>> newMap = new LinkedHashMap<>();
                                for (String s : c.getAddedSubList()) {
                                    newMap.put(s, initColumn(s));
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
        init();
        return this.tableView;
    }

    private TableColumn<String, ?> initColumn(String columnName) {
        if (tableModel.isEditableTextField(columnName)) {
            TableColumn<String, String> column = new TableColumn<>(columnName);
            setColumnEditable(column);
            return column;
        } else if (tableModel.isCheckBox(columnName)) {
            TableColumn<String, CheckBox> column = new TableColumn<>(columnName);
            setColumnCheckable(column);
            return column;
        } else {
            TableColumn<String, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(cell -> tableModel.getCellData(cell.getValue(), cell.getTableColumn().getText()));
            return column;
        }
    }

    private void setColumnEditable(TableColumn<String, String> column) {
        column.setCellValueFactory(cell -> tableModel.getCellData(cell.getValue(), cell.getTableColumn().getText()));
        column.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new TextFieldTableCell<String, String>(new DefaultStringConverter()) {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (getIndex() > -1 && getIndex() < getTableView().getItems().size() && tableModel.isModified(getTableView().getItems().get(getIndex()),
                                getTableColumn().getText(), item)) {
                            setStyle("-fx-background-color: yellow");
                        } else {
                            setStyle("-fx-background-color: red");
                        }
                    }
                };
            }
        });
    }

    private void setColumnCheckable(TableColumn<String, CheckBox> column) {
        CheckBox allCheckBox = new CheckBox();
        allCheckBox.selectedProperty().setValue(tableModel.getAllCheckValue(column.getText()).getValue());
        allCheckBox.setOnMouseClicked(event -> {
            tableModel.getAllCheckValue(column.getText()).setValue(allCheckBox.selectedProperty().getValue());
            tableModel.setAllSelected(allCheckBox.selectedProperty().getValue(), column.getText());
        });
        tableModel.getAllCheckValue(column.getText()).addListener((ov, b1, b2) -> {
            if (b2 != allCheckBox.selectedProperty().getValue().booleanValue()) {
                allCheckBox.selectedProperty().setValue(b2);
            }
        });
        column.setGraphic(allCheckBox);

        column.setCellValueFactory(cell -> {
            ObjectProperty<Boolean> b = tableModel.getCheckValue(cell.getValue(), cell.getTableColumn().getText());
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(b.getValue());
            checkBox.selectedProperty().addListener((ov, b1, b2) -> {
                if (b2 != b.getValue().booleanValue()) {
                    b.setValue(b2);
                }
            });
            b.addListener((ov, b1, b2) -> {
                if (checkBox.selectedProperty().getValue().booleanValue() != b2) {
                    checkBox.selectedProperty().setValue(b2);
                }
            });
            return new SimpleObjectProperty<>(checkBox);
        });

    }

}

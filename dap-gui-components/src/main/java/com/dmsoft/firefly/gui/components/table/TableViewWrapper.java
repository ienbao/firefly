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

import java.util.*;

/**
 * table wrapper class
 *
 * @author Can Guan
 */
public class TableViewWrapper extends AbstractTableViewWrapper {
    private Map<String, TableColumn<String, ?>> columnMap;
    private static final String SEPARATOR = "!@#";
    private List<String> editedStyleClass;
    private Map<String, List<String>> customStyleClassMap;
    private Set<String> addedStyleClass;

    /**
     * constructor
     *
     * @param tableView  table view
     * @param tableModel table model
     */
    public TableViewWrapper(TableView<String> tableView, TableModel tableModel) {
        super(tableView, tableModel);
        editedStyleClass = new ArrayList<>();
        this.customStyleClassMap = new HashMap<>();
        this.addedStyleClass = new HashSet<>();
    }

    private void init() {
        this.columnMap = new LinkedHashMap<>();
        for (String s : tableModel.getHeaderArray()) {
            columnMap.put(s, initColumn(s));
        }
        // add listener for column list removed, added or permuted
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

        // add listener for row key list removed, added or permuted
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

    @Override
    public void addEditedCellStyleClass(List<String> styleClass) {
        this.editedStyleClass.clear();
        this.editedStyleClass.addAll(styleClass);
        this.addedStyleClass.addAll(styleClass);
    }

    @Override
    public void addCustomCellStyleClass(String rowKey, String columnName, List<String> styleClass) {
        this.customStyleClassMap.put(rowKey + SEPARATOR + columnName, styleClass);
        this.addedStyleClass.addAll(styleClass);
    }

    @Override
    public void removeCustomCellStyleClass(String rowKey, List<String> columnName) {
        this.customStyleClassMap.remove(rowKey + SEPARATOR + columnName);
    }

    @Override
    public void addCustomRowStyleClass(String rowKey, List<String> styleClass) {
        this.customStyleClassMap.put(rowKey, styleClass);
        this.addedStyleClass.addAll(styleClass);
    }

    @Override
    public void removeCustomRowStyleClass(String rowKey) {
        this.customStyleClassMap.remove(rowKey);
    }

    @Override
    public void addCustomColumnStyleClass(String columnName, List<String> styleClass) {
        this.customStyleClassMap.put(columnName, styleClass);
        this.addedStyleClass.addAll(styleClass);
    }

    @Override
    public void removeCustomColumnStyleClass(String columnName) {
        this.customStyleClassMap.remove(columnName);
    }

    // method to init column
    private TableColumn<String, ?> initColumn(String columnName) {
        if (tableModel.isEditableTextField(columnName)) {
            TableColumn<String, String> column = new TableColumn<>(columnName);
            decorateTextFieldColumn(column);
            return column;
        } else if (tableModel.isCheckBox(columnName)) {
            TableColumn<String, CheckBox> column = new TableColumn<>(columnName);
            decorateCheckboxColumn(column);
            return column;
        } else {
            TableColumn<String, String> column = new TableColumn<>(columnName);
            decorateDefaultColumn(column);
            return column;
        }
    }

    private void decorateTextFieldColumn(TableColumn<String, String> column) {
        // init text field value and combine event and value
        column.setCellValueFactory(cell -> tableModel.getCellData(cell.getValue(), cell.getTableColumn().getText()));

        // decorate style class
        column.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
            @Override
            public TableCell<String, String> call(TableColumn<String, String> param) {
                return new TextFieldTableCell<String, String>(new DefaultStringConverter()) {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().removeAll(addedStyleClass);
                        if (getIndex() > -1 && getIndex() < getTableView().getItems().size()) {
                            getStyleClass().setAll(getNewStyleClass(getStyleClass(), getTableView().getItems().get(getIndex()),
                                    getTableColumn().getText()));
                            if (tableModel.isModified(getTableView().getItems().get(getIndex()), getTableColumn().getText(), item)) {
                                getStyleClass().addAll(editedStyleClass);
                            }
                        }
                    }
                };
            }
        });
    }

    private void decorateCheckboxColumn(TableColumn<String, CheckBox> column) {
        // set column header graphic with check box, and combine event and value
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

        // init cell check box and combine event and value
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

        // decorate style class
        column.setCellFactory(new Callback<TableColumn<String, CheckBox>, TableCell<String, CheckBox>>() {
            @Override
            public TableCell<String, CheckBox> call(TableColumn<String, CheckBox> param) {
                return new TableCell<String, CheckBox>() {
                    @Override
                    protected void updateItem(CheckBox item, boolean empty) {
                        if (item != null && item.equals(getItem())) {
                            return;
                        }
                        super.updateItem(item, empty);
                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(null);
                            super.setGraphic(item);
                        }
                        getStyleClass().removeAll(addedStyleClass);
                        if (getIndex() > -1 && getIndex() < getTableView().getItems().size()) {
                            getStyleClass().setAll(getNewStyleClass(getStyleClass(), getTableView().getItems().get(getIndex()),
                                    getTableColumn().getText()));
                        }
                    }
                };
            }
        });
    }

    private void decorateDefaultColumn(TableColumn<String, String> column) {
        // init cell value and combine event and value
        column.setCellValueFactory(cell -> tableModel.getCellData(cell.getValue(), cell.getTableColumn().getText()));

        // decorate style class
        column.setCellFactory(param -> new TableCell<String, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item != null && item.equals(getItem())) {
                    return;
                }

                super.updateItem(item, empty);

                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    super.setText(item);
                    super.setGraphic(null);
                }
                getStyleClass().removeAll(addedStyleClass);
                if (getIndex() > -1 && getIndex() < getTableView().getItems().size()) {
                    getStyleClass().setAll(getNewStyleClass(getStyleClass(), getTableView().getItems().get(getIndex()),
                            getTableColumn().getText()));
                }
            }
        });
    }

    // method to get new style class
    private List<String> getNewStyleClass(List<String> oldStyleClass, String rowKey, String columnName) {
        List<String> result = new ArrayList<>();
        if (oldStyleClass != null) {
            result.addAll(oldStyleClass);
        }
        result.addAll(customStyleClassMap.get(rowKey + SEPARATOR + columnName));
        result.addAll(customStyleClassMap.get(rowKey));
        result.addAll(customStyleClassMap.get(columnName));
        return result;
    }
}

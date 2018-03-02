package com.dmsoft.firefly.gui.components.table;

import com.google.common.collect.Lists;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.util.List;

/**
 * table view wrapper
 *
 * @author Can Guan
 */
public class NewTableViewWrapper {
    /**
     * method to decorate table view with model
     *
     * @param tableView table view
     * @param model     model
     */
    public static void decorate(TableView<String> tableView, NewTableModel model) {
        List<TableColumn<String, ?>> columns = Lists.newArrayList();
        for (String s : model.getHeaderArray()) {
            columns.add(initColumn(s, model));
        }
        tableView.getColumns().addAll(columns);
        model.getHeaderArray().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    try {
                        TableColumn<String, ?>[] columnArray = new TableColumn[tableView.getColumns().size()];
                        for (TableColumn<String, ?> tableColumn : tableView.getColumns()) {
                            columnArray[c.getList().indexOf(tableColumn.getText())] = tableColumn;
                        }
                        tableView.getColumns().setAll(columnArray);
                    } catch (Exception e) {

                    }
                } else if (c.wasAdded()) {
                    try {
                        for (String s : c.getAddedSubList()) {
                            tableView.getColumns().add(initColumn(s, model));
                        }
                    } catch (Exception e) {

                    }
                } else if (c.wasRemoved()) {
                    try {
                        List<? extends String> removedHeaders = c.getRemoved();
                        for (TableColumn column : tableView.getColumns()) {
                            if (removedHeaders.contains(column.getText())) {
                                tableView.getColumns().remove(column);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        tableView.setItems(model.getRowKeyArray());
    }

    private static TableColumn<String, ?> initColumn(String s, NewTableModel model) {
        if (model.isEditableTextField(s)) {
            TableColumn<String, String> column = new TableColumn<>(s);
            column.setCellValueFactory(cell -> model.getCellData(cell.getValue(), s));
            column.setCellFactory(tableColumn ->
                    new TextFieldTableCell<String, String>(new DefaultStringConverter()) {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (this.getIndex() > -1 && this.getIndex() < this.getTableView().getItems().size()) {
                                model.decorate(model.getRowKeyArray().get(this.getIndex()), s, this);
                            } else {
                                this.setStyle(null);
                            }
                        }
                    });
            return column;
        } else if (model.isCheckBox(s)) {
            TableColumn<String, CheckBox> column = new TableColumn<>(s);
            column.setSortable(false);
            column.setResizable(false);
            column.setPrefWidth(32);
            CheckBox allCheckBox = new CheckBox();
            allCheckBox.selectedProperty().set(model.getAllCheckValue(s).getValue());
            model.getAllCheckValue(s).addListener((ov, b1, b2) -> {
                allCheckBox.selectedProperty().set(b2);
            });
            column.setGraphic(allCheckBox);
            column.setCellValueFactory(cell -> {
                ObjectProperty<Boolean> b = model.getCheckValue(cell.getValue(), s);
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
            column.setCellFactory(tableColumn -> new TableCell<String, CheckBox>() {
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
                    if (this.getIndex() > -1 && this.getIndex() < this.getTableView().getItems().size()) {
                        model.decorate(model.getRowKeyArray().get(this.getIndex()), s, this);
                    } else {
                        this.setStyle(null);
                    }
                }
            });
            return column;
        }
        TableColumn<String, String> column = new TableColumn<>(s);
        column.setCellValueFactory(cell -> model.getCellData(cell.getValue(), s));
        column.setCellFactory(tableColumn -> new TableCell<String, String>() {
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
                if (this.getIndex() > -1 && this.getIndex() < this.getTableView().getItems().size()) {
                    model.decorate(model.getRowKeyArray().get(this.getIndex()), s, this);
                } else {
                    this.setStyle(null);
                }
            }
        });
//        column.setCellFactory(tableColumn -> {
//            TableCell<String, String> tableCell = new TableCell<>();
//            if (tableCell.getIndex() > -1 && tableCell.getIndex() < tableCell.getTableView().getItems().size()) {
//                return model.decorate(model.getRowKeyArray().get(tableCell.getIndex()), s, tableCell);
//            }
//            return tableCell;
//        });
        return column;
    }
}

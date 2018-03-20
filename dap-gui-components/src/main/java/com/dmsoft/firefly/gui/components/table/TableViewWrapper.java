package com.dmsoft.firefly.gui.components.table;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.util.Comparator;
import java.util.List;

/**
 * table view wrapper
 *
 * @author Can Guan
 */
public class TableViewWrapper {
    /**
     * method to decorate table view with model
     *
     * @param tableView table view
     * @param model     model
     */
    @SuppressWarnings("unchecked")
    public static void decorate(TableView<String> tableView, TableModel model) {
        tableView.getColumns().clear();
        if (model == null) {
            return;
        }
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
                        for (int i = 0; i < tableView.getColumns().size(); i++) {
                            columnArray[c.getList().indexOf(tableView.getColumns().get(i).getText())] = tableView.getColumns().get(i);
                        }
                        tableView.getColumns().setAll(columnArray);
                    } catch (Exception ignored) {

                    }
                } else if (c.wasAdded()) {
                    try {
                        for (String s : c.getAddedSubList()) {
                            tableView.getColumns().add(c.getFrom(), initColumn(s, model));
                        }
                    } catch (Exception ignored) {

                    }
                } else if (c.wasRemoved()) {
                    try {
                        List<? extends String> removedHeaders = c.getRemoved();
                        List<TableColumn> tableColumnList = Lists.newArrayList(tableView.getColumns());
                        for (TableColumn aTableColumnList : tableColumnList) {
                            if (removedHeaders.contains(aTableColumnList.getText())) {
                                tableView.getColumns().remove(aTableColumnList);
                            }
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        });
        SortedList<String> list = model.getRowKeyArray() instanceof SortedList ? (SortedList) model.getRowKeyArray() : new SortedList<>(model.getRowKeyArray());
        list.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(list);

        if (model.getMenuEventList() != null && !model.getMenuEventList().isEmpty()) {
            ContextMenu menu = new ContextMenu();
            for (TableMenuRowEvent event : model.getMenuEventList()) {
                MenuItem menuItem = new MenuItem(event.getMenuName(), event.getMenuNode());

                menuItem.setOnAction(event1 -> {
                    String rowKey = tableView.getSelectionModel().getSelectedItem();
                    event.handleAction(rowKey, event1);
                    tableView.impl_updatePeer();
                });
                menu.getItems().add(menuItem);
            }
            tableView.setRowFactory(tv -> {
                TableRow<String> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && model.isMenuEventEnable(row.getItem())) {
                        row.setContextMenu(menu);
                    }
                });
                return row;
            });
        }
        model.setTableView(tableView);
        if (tableView.getSkin() != null) {
            decorateSkinForSortHeader((TableViewSkin) tableView.getSkin(), tableView);
        } else {
            tableView.skinProperty().addListener((ov, s1, s2) -> {
                decorateSkinForSortHeader((TableViewSkin) s2, tableView);
            });
        }
    }

    private static TableColumn<String, ?> initColumn(String s, TableModel model) {
        if (model.isEditableTextField(s)) {
            TableColumn<String, String> column = new TableColumn<>(s);
            column.getStyleClass().add("editable-header");
            column.setCellValueFactory(cell -> model.getCellData(cell.getValue(), s));
            column.setCellFactory(tableColumn ->
                    new TextFieldTableCell<String, String>(new DefaultStringConverter()) {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (this.getIndex() > -1 && this.getIndex() < this.getTableView().getItems().size()) {
                                model.decorate(this.getTableView().getItems().get(this.getIndex()), s, this);
                            } else {
                                this.setStyle(null);
                            }
                        }
                    });
            column.setComparator(getComparator());
            return column;
        } else if (model.isCheckBox(s)) {
            TableColumn<String, CheckBox> column = new TableColumn<>(s);
            if (model.getAllCheckValue(s) != null) {
                CheckBox allCheckBox = new CheckBox();
                allCheckBox.selectedProperty().set(model.getAllCheckValue(s).getValue());
                model.getAllCheckValue(s).addListener((ov, b1, b2) -> allCheckBox.selectedProperty().set(b2));
                model.setAllCheckBox(allCheckBox);
                column.setGraphic(allCheckBox);
                column.setSortable(false);
                column.setResizable(false);
                column.setPrefWidth(32);
            }
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
                        model.decorate(this.getTableView().getItems().get(this.getIndex()), s, this);
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
                    model.decorate(this.getTableView().getItems().get(this.getIndex()), s, this);
                } else {
                    this.setStyle(null);
                }
            }
        });
        column.setComparator(getComparator());
        return column;
    }

    private static Comparator<String> getComparator() {
        return (o1, o2) -> {
            boolean o1Flag = DAPStringUtils.isNumeric(o1);
            boolean o2Flag = DAPStringUtils.isNumeric(o2);
            if (o1 != null && o2 != null) {

                if (o1Flag && o2Flag) {
                    Double delta = Double.valueOf(o2) - Double.valueOf(o1);
                    if (delta == 0) {
                        return 0;
                    } else if (delta > 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (o1Flag) {
                    return -1;
                } else if (o2Flag) {
                    return 1;
                } else {
                    return -o2.compareTo(o1);
                }
            } else {
                return 0;
            }
        };

    }

    /**
     * methdo to decorate skin
     *
     * @param skin      skin
     * @param tableView table view
     */
    @SuppressWarnings("unchecked")
    public static void decorateSkinForSortHeader(TableViewSkin skin, TableView tableView) {
        TableHeaderRow rowHeader = skin.getTableHeaderRow();
        for (int i = 0; i < tableView.getColumns().size(); i++) {
            TableColumn tableColumn = (TableColumn) tableView.getColumns().get(i);
            Button empty = new Button();
            empty.setPrefSize(0, 0);
            empty.setMinSize(0, 0);
            empty.setMaxSize(0, 0);
            tableColumn.setSortNode(empty);
            tableColumn.sortTypeProperty().addListener((ov, t1, t2) -> {
                if (TableColumn.SortType.DESCENDING.equals(t2)) {
                    rowHeader.getColumnHeaderFor(tableColumn).lookup(".label").getStyleClass().removeAll("ascending-label");
                    rowHeader.getColumnHeaderFor(tableColumn).lookup(".label").getStyleClass().add("descending-label");
                }
            });
        }
        tableView.getSortOrder().addListener((ListChangeListener<String>) c -> {
            for (Node node : tableView.lookupAll(".ascending-label")) {
                node.getStyleClass().removeAll("ascending-label");
            }
            for (Node node : tableView.lookupAll(".descending-label")) {
                node.getStyleClass().removeAll("descending-label");
            }
            if (tableView.getSortOrder() != null && !tableView.getSortOrder().isEmpty() && TableColumn.SortType.ASCENDING.equals(((TableColumn) tableView.getSortOrder().get(0)).getSortType())) {
                rowHeader.getColumnHeaderFor((TableColumn) tableView.getSortOrder().get(0)).lookup(".label").getStyleClass().add("ascending-label");
                rowHeader.getColumnHeaderFor((TableColumn) tableView.getSortOrder().get(0)).lookup(".label").getStyleClass().removeAll("descending-label");
            }
        });
    }
}

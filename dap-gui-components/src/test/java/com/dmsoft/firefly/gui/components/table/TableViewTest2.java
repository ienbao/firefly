package com.dmsoft.firefly.gui.components.table;

import com.google.common.collect.Lists;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.util.*;

public class TableViewTest2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<String> tableView = new TableView<>();
        tableView.setEditable(true);
        NewTableModel model = new NewTableModel() {
            private ObservableList<String> header = FXCollections.observableArrayList("AA", "BB", "CC", "DD", "EE", "FF");
            private SortedList<String> rowKey = new SortedList<>(FXCollections.observableArrayList("1", "2", "3", "4", "5").filtered(p -> true));
            private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
            private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
            private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
            private Set<String> falseSet = new HashSet<>();

            @Override
            public ObservableList<String> getHeaderArray() {
                return header;
            }

            @Override
            public ObjectProperty<String> getCellData(String rowKey, String columnName) {
                if (valueMap.get(rowKey + "-" + columnName) == null) {
                    if (Double.valueOf(rowKey) > 3) {
                        valueMap.put(rowKey + "-" + columnName, new SimpleObjectProperty<>(rowKey));
                    } else {
                        valueMap.put(rowKey + "-" + columnName, new SimpleObjectProperty<>(rowKey + "-" + columnName));
                    }
                }
                return valueMap.get(rowKey + "-" + columnName);
            }

            @Override
            public ObservableList<String> getRowKeyArray() {
                return rowKey;
            }

            @Override
            public boolean isEditableTextField(String columnName) {
                return "AA".equals(columnName);
            }

            @Override
            public boolean isCheckBox(String columnName) {
                return "BB".equals(columnName);
            }

            @Override
            public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
                if (checkMap.get(rowKey + "-" + columnName) == null) {
                    SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
                    if ("1".equals(rowKey)) {
                        checkMap.put(rowKey + "-" + columnName, b);
                        falseSet.add(rowKey + "-" + columnName);
                        allChecked.setValue(false);
                    } else {
                        b.setValue(true);
                        checkMap.put(rowKey + "-" + columnName, new SimpleObjectProperty<Boolean>(true));
                    }
                    b.addListener((ov, b1, b2) -> {
                        if (!b2) {
                            falseSet.add(rowKey + "-" + columnName);
                            allChecked.setValue(false);
                        } else {
                            falseSet.remove(rowKey + "-" + columnName);
                            if (falseSet.isEmpty()) {
                                allChecked.setValue(true);
                            }
                        }
                    });
                }
                return checkMap.get(rowKey + "-" + columnName);
            }

            @Override
            public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
                return allChecked;
            }

            @Override
            public List<TableMenuRowEvent> getMenuEventList() {

                TableMenuRowEvent rowEvent = new TableMenuRowEvent() {
                    @Override
                    public String getMenuName() {
                        return "Print";
                    }

                    @Override
                    public void handleAction(String rowKey, ActionEvent event) {
                        System.out.println(rowKey);
                    }
                };
                return Lists.newArrayList(rowEvent);
            }

            @Override
            public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
                if ("AA".equals(column) && "2".equals(rowKey)) {
                    tableCell.setStyle("-fx-background-color:red");
                }
                if ("CC".equals(column)) {
                    tableCell.setStyle("-fx-background-color: yellow");
                }
                return tableCell;
            }

            @Override
            public void setAllCheckBox(CheckBox checkBox) {

            }

            @Override
            public void setTableView(TableView<String> tableView) {

            }
        };

        tableView.skinProperty().addListener((ov, s1, s2) -> {
            NewTableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, tableView);
        });

        GridPane pane = new GridPane();
        HBox hBox = new HBox();
        Button addRowButton = new Button("Add Row");
        Button removeRowButton = new Button("Remove Row");
        Button addColumn = new Button("New Column");
        Button removeColumn = new Button("Remove Column");

        addRowButton.setOnAction(event -> {
            model.getRowKeyArray().add("4");
        });
        removeRowButton.setOnAction(event -> {
            model.getRowKeyArray().remove("4");
        });
        addColumn.setOnAction(event -> {
            model.getHeaderArray().add("FF");
        });
        removeColumn.setOnAction(event -> {
            model.getHeaderArray().remove("FF");
        });
        hBox.getChildren().add(addRowButton);
        hBox.getChildren().add(removeRowButton);
        hBox.getChildren().add(addColumn);
        hBox.getChildren().add(removeColumn);
        RowConstraints r0 = new RowConstraints(22, 22, 22);
        RowConstraints r1 = new RowConstraints(10, 10, 10);
        RowConstraints r2 = new RowConstraints(300, 300, 300);
        pane.getRowConstraints().addAll(r0, r1, r2);
        ColumnConstraints c0 = new ColumnConstraints(100, 300, 600);
        pane.getColumnConstraints().add(c0);
        pane.add(hBox, 0, 0);
        pane.add(tableView, 0, 2);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        NewTableViewWrapper.decorate(tableView, model);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

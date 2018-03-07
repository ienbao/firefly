package com.dmsoft.firefly.gui.components.table;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.*;


public class TableViewTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<String> tableView = new TableView<>();
        TableModel tableModel = new TableModel() {
            private ObservableList<String> list = FXCollections.observableArrayList("AA", "BB");
            private ObservableList<String> rowKey = FXCollections.observableArrayList("CC", "DD");
            private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
            private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
            private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
            private Set<String> falseSet = new HashSet<>();

            @Override
            public ObservableList<String> getHeaderArray() {
                return list;
            }

            @Override
            public ObjectProperty<String> getCellData(String rowKey, String columnName) {
                if (valueMap.get(rowKey + "-" + columnName) == null) {
                    valueMap.put(rowKey + "-" + columnName, new SimpleObjectProperty<>(rowKey + "-" + columnName));
                }
                return valueMap.get(rowKey + "-" + columnName);
            }

            @Override
            public ObservableList<String> getRowKeyArray() {
                return rowKey;
            }

            @Override
            public boolean isEditableTextField(String columnName) {
                if ("AA".equals(columnName)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean isModified(String rowKey, String columnName, String value) {
                if ("RR".equals(value)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean isCheckBox(String columnName) {
                if ("HH".equals(columnName)) {
                    return true;
                }
                return false;
            }

            @Override
            public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
                if (checkMap.get(rowKey + "-" + columnName) == null) {
                    SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
                    if ("GG".equals(rowKey)) {
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
            public void setAllSelected(boolean value, String columnName) {
                for (ObjectProperty<Boolean> b : checkMap.values()) {
                    b.setValue(value);
                }
                if (value) {
                    falseSet.clear();
                    allChecked.setValue(true);
                } else {
                    falseSet.addAll(checkMap.keySet());
                    if (!falseSet.isEmpty()) {
                        allChecked.setValue(false);
                    }
                }
            }

            @Override
            public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
                return allChecked;
            }
        };
        tableView.setEditable(true);
        tableModel.getHeaderArray().add("EE");
        tableModel.getHeaderArray().add("HH");
        tableModel.getHeaderArray().remove("BB");
        tableModel.getRowKeyArray().remove("CC");
        tableModel.getRowKeyArray().add("GG");

        TableMenuRowEvent rowEvent = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return "Print";
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {
                System.out.println(rowKey);
            }

            @Override
            public Node getMenuNode() {
                return null;
            }
        };

        TableViewWrapper wrapper = new TableViewWrapper(tableView, tableModel);
        wrapper.addTableRowEvent(rowEvent);
        List<String> addStyleClass = new ArrayList<>();
        List<String> errorStyleClass = new ArrayList<>();
        errorStyleClass.add("error");
        addStyleClass.add("edited");
        wrapper.addEditedCellStyleClass(addStyleClass);

        Scene scene = new Scene(wrapper.getWrappedTable());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        wrapper.addCustomCellStyleClass("GG", "HH", errorStyleClass);
        wrapper.update();
        System.out.println("ASF");
    }
}

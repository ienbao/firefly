package com.dmsoft.firefly.gui.components.table;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;


public class TableViewTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<String> tableView = new TableView<>();
        TableModel tableModel = new TableModel() {
            private ObservableList<String> list = FXCollections.observableArrayList("AA", "BB");
            private ObservableList<String> rowKey = FXCollections.observableArrayList("CC", "DD");

            @Override
            public ObservableList<String> getHeaderArray() {
                return list;
            }

            @Override
            public ObjectProperty<String> getCellData(String rowKey, String columnName) {
                return new SimpleObjectProperty<String>(rowKey + "-" + columnName);
            }

            @Override
            public ObservableList<String> getRowKeyArray() {
                return rowKey;
            }
        };
        tableModel.getHeaderArray().add("EE");
        tableModel.getHeaderArray().remove("BB");
        tableModel.getRowKeyArray().remove("CC");
        tableModel.getRowKeyArray().add("GG");

        TableViewWrapper wrapper = new TableViewWrapper(tableView, tableModel);
        Scene scene = new Scene(wrapper.getWrappedTable());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

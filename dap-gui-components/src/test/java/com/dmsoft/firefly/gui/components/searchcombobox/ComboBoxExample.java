package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ComboBoxExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SearchComboBox comboBox = new SearchComboBox(new ISearchComboBoxController() {
            @Override
            public ObservableList<String> getValueForTestItem(String testItem) {
                if ("AA".equals(testItem)) {
                    List<String> testList = new ArrayList<>();
                    testList.add("132");
                    testList.add("2456");
                    testList.add("<A");
                    return FXCollections.observableArrayList(testList);
                }
                return FXCollections.observableArrayList();
            }

            @Override
            public ObservableList<String> getTestItems() {
                List<String> s = new ArrayList<>();
                s.add("AA");
                s.add("BA");
                s.add("BAB!");
                s.add("<");
                return FXCollections.observableArrayList(s);
            }

            @Override
            public boolean isTimeKey(String testItem) {
                if (testItem != null && testItem.contains("A")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getTimePattern() {
                return null;
            }
        });
        Scene scene = new Scene(comboBox, 300, 300);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

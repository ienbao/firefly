/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.searchcombobox.ISearchComboBoxController;
import com.dmsoft.firefly.gui.components.searchcombobox.SearchComboBox;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guang.Li on 2018/2/27.
 */
public class BasicSearchPane extends VBox {
    private Button addSearch;

    public BasicSearchPane() {
        this.setStyle("-fx-border-color: #DCDCDC");
        this.setStyle("-fx-border-width: 0 0 1 0");

        addSearch = new Button();
        this.getChildren().add(addSearch);
        VBox.setMargin(addSearch, new Insets(10, 10, 0, 8));
        addSearch.setPrefSize(160, 22);
        addSearch.setPadding(new Insets(10, 10, 0, 8));

        addSearch.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
        addSearch.setOnAction(event -> {
            SearchComboBox basicSearchCom = new SearchComboBox(new ISearchComboBoxController() {
                @Override
                public ObservableList<String> getValueForTestItem(String testItem) {
                    if ("AA".equals(testItem)) {
                        java.util.List<String> testList = new ArrayList<>();
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
            basicSearchCom.getCloseBtn().setOnAction(e -> {
                this.getChildren().remove(basicSearchCom);
                if (this.getChildren().size() == 1) {
                    ((VBox) this.getParent()).getChildren().remove(this);
                }
            });
            if (this.getChildren().size() > 0) {
                basicSearchCom.setPadding(new Insets(10, 10, 0, 8));
                this.getChildren().add(this.getChildren().size() - 1, basicSearchCom);
                VBox.setVgrow(basicSearchCom, Priority.ALWAYS);
            }
        });
//        this.setHgrow(Priority.ALWAYS);
//        VBox.setVgrow(addSearch, Priority.ALWAYS);
    }

}

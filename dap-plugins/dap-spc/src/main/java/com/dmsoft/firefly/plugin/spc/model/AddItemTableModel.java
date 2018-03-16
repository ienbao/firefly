/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.NewTableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.google.common.collect.Lists;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.*;

/**
 * Created by Ethan.Yang on 2018/3/15.
 */
public class AddItemTableModel implements NewTableModel {
    private static final String[] HEADER = {"", "Test Item"};
    private ObservableList<String> columnKey = FXCollections.observableArrayList(Arrays.asList(HEADER));
    private ObservableList<String> rowKey = FXCollections.observableArrayList();

    private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();

    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private Set<String> falseSet = new HashSet<>();

    private CheckBox allCheckBox;
    private FilteredList<String> addItemRowDataFilteredList;

    /**
     * constructor
     */
    public AddItemTableModel() {
        addItemRowDataFilteredList = rowKey.filtered(p -> true);
        allChecked.addListener((ov, b1, b2) -> {
            if (!b2) {
                falseSet.clear();
            } else {
                for (Map.Entry<String, SimpleObjectProperty<Boolean>> entry : checkMap.entrySet()) {
                    entry.getValue().setValue(b2);
                    if (!b2) {
                        falseSet.add(entry.getKey());
                    }
                }
            }
        });
    }

    /**
     * init data
     *
     * @param testItemList      file test Item
     * @param existTestItemList exist test Item
     */
    public void initData(List<String> testItemList, List<String> existTestItemList) {
        if (testItemList == null) {
            return;
        }
        this.clearTable();
        for (String testItem : testItemList) {
            if (existTestItemList != null && existTestItemList.contains(testItem)) {
                continue;
            }
            rowKey.add(testItem);
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
            falseSet.add(testItem);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add(testItem);
                    allChecked.setValue(false);
                } else {
                    falseSet.remove(testItem);
                    if (falseSet.isEmpty()) {
                        allChecked.setValue(true);
                    }
                }
            });
            checkMap.put(testItem, b);
            valueMap.put(testItem, new SimpleObjectProperty<>(testItem));
        }
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        addItemRowDataFilteredList.setPredicate(p -> {
            String testItem = p;
            return testItem.contains(filterTf);
        });
    }

    /**
     * get select testItem
     *
     * @return the list of testItem
     */
    public List<String> getSelectTestItem() {
        List<String> testItem = Lists.newArrayList();
        for (Map.Entry<String, SimpleObjectProperty<Boolean>> entry : checkMap.entrySet()) {
            if (entry.getValue().getValue()) {
                testItem.add(entry.getKey());
            }
        }
        return testItem;
    }

    private void clearTable() {
        rowKey.clear();
        valueMap.clear();
        checkMap.clear();
        falseSet.clear();
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        return valueMap.get(rowKey);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return addItemRowDataFilteredList;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        if (columnName.equals(HEADER[0])) {
            return true;
        }
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        return checkMap.get(rowKey);
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allChecked;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return null;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        return null;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    public FilteredList<String> getAddItemRowDataFilteredList() {
        return addItemRowDataFilteredList;
    }

    public Map<String, SimpleObjectProperty<Boolean>> getCheckMap() {
        return checkMap;
    }
}

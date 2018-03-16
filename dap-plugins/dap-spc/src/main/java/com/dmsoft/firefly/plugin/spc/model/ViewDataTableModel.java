/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by Ethan.Yang on 2018/3/2.
 */
@Deprecated
public class ViewDataTableModel implements TableModel {
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allSelect = new SimpleObjectProperty<>(true);
    private Set<String> falseSet = new HashSet<>();

    private FilteredList<String> viewDataFilteredList;
    private SortedList<String> viewDataSortedList;

    private SearchDataFrame searchDataFrame;
    private List<TableMenuRowEvent> menuRowEvents;

    private Set<String> highLightRowKeys;

    /**
     * constructor
     */
    public ViewDataTableModel() {
        viewDataFilteredList = rowKey.filtered(p -> true);
        viewDataSortedList = new SortedList<>(viewDataFilteredList);
        this.highLightRowKeys = Sets.newLinkedHashSet();
        this.initTableMenuEvent();
    }

    /**
     * init data
     *
     * @param searchDataFrame data
     */
    public void initData(SearchDataFrame searchDataFrame) {
        this.searchDataFrame = searchDataFrame;
        this.clearViewDataTable();
        if (searchDataFrame != null) {
            columnKey.add("");
            columnKey.addAll(searchDataFrame.getAllTestItemName());
            rowKey.addAll(searchDataFrame.getAllRowKeys());
            allSelect.setValue(true);
        }
    }

    /**
     * clear table
     */
    public void clearViewDataTable() {
        rowKey.clear();
        columnKey.clear();
        checkMap.clear();
        falseSet.clear();
        highLightRowKeys.clear();
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        viewDataFilteredList.setPredicate(p -> {
            List<String> datas = searchDataFrame.getDataRowList(p);
            for (String s : datas) {
                if (s.contains(filterTf)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * invert select
     */
    public void invertCheckBoxRow() {
        if (rowKey != null) {
            for (String key : rowKey) {
//                checkMap.get(key).set(!checkMap.get(key).getValue());
                if (checkMap.get(key) != null) {
                    checkMap.get(key).set(!checkMap.get(key).getValue());
                } else {
                    checkMap.put(key, new SimpleObjectProperty<>(false));
                }
            }
        }
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        String value = "";
        if (searchDataFrame != null) {
            value = this.searchDataFrame.getCellValue(rowKey, columnName);
        }
        return new SimpleObjectProperty<>(value);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return viewDataSortedList;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        if (columnName.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (checkMap.get(rowKey) == null) {
            SimpleObjectProperty<Boolean> selectProperty = new SimpleObjectProperty<>(true);
            checkMap.put(rowKey, selectProperty);
            selectProperty.addListener((ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add(rowKey);
                    allSelect.setValue(false);
                } else {
                    falseSet.remove(rowKey);
                    if (falseSet.isEmpty()) {
                        allSelect.setValue(true);
                    }
                }
            });
        }
        return checkMap.get(rowKey);
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allSelect;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return menuRowEvents;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {

        if (highLightRowKeys.contains(rowKey)) {
            tableCell.setStyle("-fx-background-color:#f8d251");
        } else {
            tableCell.setStyle("-fx-background-color:#ffffff");
        }
        String value = this.searchDataFrame.getCellValue(rowKey, column);
        if (!StringUtils.isBlank(value)) {
            if (value.contains("value11")) {
                tableCell.setStyle("-fx-background-color:#ea2028;-fx-text-fill: #ffffff");
            }
            if (value.equals("value22")) {
                tableCell.setStyle("-fx-text-fill: #4B910E");
            }
        }
        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        checkBox.setOnAction(event -> {
            if (viewDataSortedList != null) {
                for (String key : viewDataSortedList) {
                    if (checkMap.get(key) != null) {
                        checkMap.get(key).set(checkBox.isSelected());
                    } else {
                        checkMap.put(key, new SimpleObjectProperty<>(checkBox.isSelected()));
                    }
                }
            }
        });
    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    private void initTableMenuEvent() {
        menuRowEvents = Lists.newArrayList();
        TableMenuRowEvent highLight = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return SpcFxmlAndLanguageUtils.getString(ResourceMassages.HIGH_LIGHT_TABLE_MENU);
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {
                if (highLightRowKeys.contains(rowKey)) {
                    highLightRowKeys.remove(rowKey);
                } else {
                    highLightRowKeys.add(rowKey);
                }
            }

            @Override
            public Node getMenuNode() {
                return null;
            }
        };

        TableMenuRowEvent detail = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return SpcFxmlAndLanguageUtils.getString(ResourceMassages.DETAIL_TABLE_MENU);
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {

            }

            @Override
            public Node getMenuNode() {
                return null;
            }
        };

        TableMenuRowEvent remove = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return SpcFxmlAndLanguageUtils.getString(ResourceMassages.REMOVE_TABLE_MENU);
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {

            }

            @Override
            public Node getMenuNode() {
                return null;
            }
        };
        this.menuRowEvents.add(highLight);
        this.menuRowEvents.add(detail);
        this.menuRowEvents.add(remove);
    }
}

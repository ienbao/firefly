package com.dmsoft.firefly.plugin.spc.charts.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.spc.charts.SelectCallBack;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cherry on 2018/2/26.
 */
public class CheckTableModel implements TableModel {

    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(true);
    private Set<String> falseSet = new HashSet<>();
    private Set<String> trueSet = new HashSet<>();
    private boolean defaultSelect = true;

    private SelectCallBack selectCallBack;

    private int checkIndex = -1;

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (valueMap.get(rowKey) == null) {
            valueMap.put(rowKey, new SimpleObjectProperty<>(rowKey));
        }
        return valueMap.get(rowKey);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return rowKey;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        return false;
    }

    @Override
    public boolean isModified(String rowKey, String columnName, String value) {
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        if (checkIndex == -1 || columnKey.size() < checkIndex || columnKey.get(checkIndex) != columnName) {
            return false;
        }
        return true;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (checkMap.get(rowKey) == null) {
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(defaultSelect);
            checkMap.put(rowKey, b);
            b.setValue(defaultSelect);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
                    trueSet.remove(rowKey);
                    falseSet.add(rowKey);
                    allChecked.setValue(false);
                } else {
                    falseSet.remove(rowKey);
                    trueSet.add(rowKey);
                    if (falseSet.isEmpty()) {
                        allChecked.setValue(true);
                    }
                }
                if (selectCallBack != null) {
                    selectCallBack.execute(rowKey, b2, trueSet);
                }
            });
        }
        return checkMap.get(rowKey);
    }

    @Override
    public void setAllSelected(boolean value, String columnName) {
        for (ObjectProperty<Boolean> b : checkMap.values()) {
            b.setValue(value);
        }
        if (value) {
            falseSet.clear();
            trueSet.addAll(checkMap.keySet());
            allChecked.setValue(true);
        } else {
            falseSet.addAll(checkMap.keySet());
            trueSet.clear();
            if (!falseSet.isEmpty()) {
                allChecked.setValue(false);
            }
        }
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allChecked;
    }

    public void setCheckIndex(int checkIndex) {
        this.checkIndex = checkIndex;
    }

    public void setSelectCallBack(SelectCallBack selectCallBack) {
        this.selectCallBack = selectCallBack;
    }

    public void setDefaultSelect(boolean defaultSelect) {
        this.allChecked.set(defaultSelect);
        this.defaultSelect = defaultSelect;
    }
}

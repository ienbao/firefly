/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.plugin.spc.dto.ControlRuleDto;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class ControlAlarmRuleTableModel implements TableModel {
    private static final String[] HEADER = UIConstant.CONTROL_ALARM_RULE_HEADER;
    private ObservableList<String> columnKey = FXCollections.observableArrayList(Arrays.asList(HEADER));
    private ObservableList<String> rowKey = FXCollections.observableArrayList();

    private List<ControlRuleDto> controlRuleDtoList;


    private Map<String, ControlRuleDto> dataMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();

    /**
     * constructor
     */
    public ControlAlarmRuleTableModel() {

    }

    /**
     * init model data
     *
     * @param controlRuleDtoList data
     */
    public void initData(List<ControlRuleDto> controlRuleDtoList) {
        this.controlRuleDtoList = controlRuleDtoList;
        this.clearTable();
        if (controlRuleDtoList != null) {
            for (ControlRuleDto controlRuleDto : controlRuleDtoList) {
                String ruleName = controlRuleDto.getRuleName();
                rowKey.add(ruleName);
                dataMap.put(ruleName, controlRuleDto);
            }
        }
    }

    private void clearTable() {
        rowKey.clear();
        dataMap.clear();
        valueMap.clear();
        checkMap.clear();
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (valueMap.get(rowKey + "-" + columnName) == null && controlRuleDtoList != null) {
            if (dataMap.get(rowKey) == null) {
                return null;
            }
            ControlRuleDto controlRuleDto = dataMap.get(rowKey);
            Object value = "";
            SimpleObjectProperty objectProperty = new SimpleObjectProperty();
            if (columnName.equals(HEADER[0])) {
                value = controlRuleDto.isUsed();
            } else if (columnName.equals(HEADER[1])) {
                value = controlRuleDto.getRuleName();
            } else if (columnName.equals(HEADER[2])) {
                value = controlRuleDto.getnValue();
                objectProperty.addListener((ov, b1, b2) -> {
                    if (!DAPStringUtils.isNumeric(String.valueOf(b2)) || DAPStringUtils.isBlank(String.valueOf(b2))) {
                        return;
                    }
                    controlRuleDto.setnValue((Integer) b2);
                });
            } else if (columnName.equals(HEADER[3])) {
                value = controlRuleDto.getmValue();
                objectProperty.addListener((ov, b1, b2) -> {
                    if (!DAPStringUtils.isNumeric(String.valueOf(b2)) || DAPStringUtils.isBlank(String.valueOf(b2))) {
                        return;
                    }
                    controlRuleDto.setmValue((Integer) b2);
                });
            } else if (columnName.equals(HEADER[4])) {
                value = controlRuleDto.getsValue();
                objectProperty.addListener((ov, b1, b2) -> {
                    if (!DAPStringUtils.isNumeric(String.valueOf(b2)) || DAPStringUtils.isBlank(String.valueOf(b2))) {
                        return;
                    }
                    controlRuleDto.setsValue((Integer) b2);
                });
            }
            objectProperty.setValue(value);
            valueMap.put(rowKey + "-" + columnName, objectProperty);
        }

        return valueMap.get(rowKey + "-" + columnName);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return rowKey;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        if (columnName.equals(HEADER[2]) || columnName.equals(HEADER[3]) || columnName.equals(HEADER[4])) {
            return true;
        }
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
        if (checkMap.get(rowKey) == null && controlRuleDtoList != null) {
            if (dataMap.get(rowKey) == null) {
                return null;
            }
            ControlRuleDto controlRuleDto = dataMap.get(rowKey);
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(controlRuleDto.isUsed());
            checkMap.put(rowKey, b);
            b.addListener((ov, b1, b2) -> {
                controlRuleDto.setUsed(b2);
            });
        }
        return checkMap.get(rowKey);
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return null;
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

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    public List<ControlRuleDto> getControlRuleDtoList() {
        return controlRuleDtoList;
    }
}

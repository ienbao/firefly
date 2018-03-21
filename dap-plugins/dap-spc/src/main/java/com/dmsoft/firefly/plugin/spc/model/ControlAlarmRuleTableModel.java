/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.plugin.spc.dto.ControlRuleDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
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
                    controlRuleDto.setnValue(Integer.parseInt((String) b2));
                });
            } else if (columnName.equals(HEADER[3])) {
                value = controlRuleDto.getmValue();
                objectProperty.addListener((ov, b1, b2) -> {
                    if (!DAPStringUtils.isNumeric(String.valueOf(b2)) || DAPStringUtils.isBlank(String.valueOf(b2))) {
                        return;
                    }
                    controlRuleDto.setmValue(Integer.parseInt((String) b2));
                });
            } else if (columnName.equals(HEADER[4])) {
                value = controlRuleDto.getsValue();
                objectProperty.addListener((ov, b1, b2) -> {
                    if (!DAPStringUtils.isNumeric(String.valueOf(b2)) || DAPStringUtils.isBlank(String.valueOf(b2))) {
                        return;
                    }
                    controlRuleDto.setsValue(Integer.parseInt((String) b2));
                });
            }
            objectProperty.setValue(value == null ? "" : String.valueOf(value));
            objectProperty.addListener((ov, b1, b2) -> {
                if (!DAPStringUtils.isNumeric((String) b2)) {
                    objectProperty.set(b1);
                    return;
                }
                if (columnName.equals(HEADER[2])) {
                    controlRuleDto.setnValue(Integer.valueOf((String) b2));
                } else if (columnName.equals(HEADER[3])) {
                    controlRuleDto.setmValue(Integer.valueOf((String) b2));
                } else if (columnName.equals(HEADER[4])) {
                    controlRuleDto.setsValue(Integer.valueOf((String) b2));
                }
            });
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
        if (column.equals(HEADER[2])) {
            if (rowKey.equals("R1") || rowKey.equals("R9")) {
                tableCell.setEditable(false);
                tableCell.setStyle("-fx-background-color: #f5f5f5");
            } else {
                tableCell.setStyle("-fx-background-color: #ffffff");
            }
            tableCell.getTableColumn().setStyle("-fx-background-color: #f2f2f2");
        } else if (column.equals(HEADER[3])) {
            if (!rowKey.equals("R5") && !rowKey.equals("R6")) {
                tableCell.setEditable(false);
                tableCell.setStyle("-fx-background-color: #f5f5f5");
            } else {
                tableCell.setStyle("-fx-background-color: #ffffff");
            }
            tableCell.getTableColumn().setStyle("-fx-background-color: #f2f2f2");
        } else if (column.equals(HEADER[4])) {
            if (rowKey.equals("R2") || rowKey.equals("R3") || rowKey.equals("R4") || rowKey.equals("R9")) {
                tableCell.setEditable(false);
                tableCell.setStyle("-fx-background-color: #f5f5f5");
            } else {
                tableCell.setStyle("-fx-background-color: #ffffff");
            }
            tableCell.getTableColumn().setStyle("-fx-background-color: #f2f2f2");
        }

        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    /**
     * get controlRule dto list
     *
     * @return
     */
    public List<ControlRuleDto> getControlRuleDtoList() {
        if (dataMap == null) {
            return null;
        }
        List<ControlRuleDto> list = Lists.newArrayList();
        for (Map.Entry<String, ControlRuleDto> entry : dataMap.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }
}

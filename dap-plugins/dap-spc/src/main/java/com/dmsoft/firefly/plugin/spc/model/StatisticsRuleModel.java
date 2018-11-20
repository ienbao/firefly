/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.plugin.spc.dto.CustomAlarmDto;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SourceObjectProperty;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.*;

/**
 * Created by Ethan.Yang on 2018/3/23.
 */
public class StatisticsRuleModel implements TableModel {
    private static final String[] HEADER = new String[]{SpcFxmlAndLanguageUtils.getString("STATISTICS"), SpcFxmlAndLanguageUtils.getString("LOWER_LIMIT"), SpcFxmlAndLanguageUtils.getString("UPPER_LIMIT")};
    private ObservableList<String> columnKey = FXCollections.observableArrayList(Arrays.asList(HEADER));
    private ObservableList<String> rowKey = FXCollections.observableArrayList();

    private Map<String, CustomAlarmDto> dataMap = new HashMap<>();
    private Map<String, SourceObjectProperty<String>> valueMap = new HashMap<>();
    private Set<String> errorEditorCell = new HashSet<>();

    /**
     * constructor
     */
    public StatisticsRuleModel() {

    }

    /**
     * init model
     * data
     *
     * @param customAlarmDtoList data
     */

    public void initData(List<CustomAlarmDto> customAlarmDtoList) {
        this.clearTable();
        if (customAlarmDtoList != null) {
            for (CustomAlarmDto customAlarmDto : customAlarmDtoList) {
                String statisticName = customAlarmDto.getStatisticName();
                rowKey.add(statisticName);
                dataMap.put(statisticName, customAlarmDto);
            }
        }
    }

    /**
     * clear table
     */
    public void clearTable() {
        rowKey.clear();
        dataMap.clear();
        valueMap.clear();
        errorEditorCell.clear();
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (valueMap.get(rowKey + "-" + columnName) == null) {
            if (dataMap.get(rowKey) == null) {
                return null;
            }
            CustomAlarmDto customAlarmDto = dataMap.get(rowKey);
            Object value = "";

            if (columnName.equals(HEADER[0])) {
                value = customAlarmDto.getStatisticName();
            } else if (columnName.equals(HEADER[1])) {
                value = customAlarmDto.getLowerLimit();
            } else if (columnName.equals(HEADER[2])) {
                value = customAlarmDto.getUpperLimit();
            }
            SourceObjectProperty objectProperty = new SourceObjectProperty(value == null ? "" : String.valueOf(value));
            objectProperty.addListener((ov, b1, b2) -> {
                if (errorEditorCell.contains(rowKey + "-" + columnName)) {
                    objectProperty.setError(true);
                    return;
                }
                if (columnName.equals(HEADER[1])) {
                    customAlarmDto.setLowerLimit(DAPStringUtils.isBlank((String) b2) ? null : Double.valueOf((String) b2));
                } else if (columnName.equals(HEADER[2])) {
                    customAlarmDto.setUpperLimit(DAPStringUtils.isBlank((String) b2) ? null : Double.valueOf((String) b2));
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
        if (columnName.equals(HEADER[1]) || columnName.equals(HEADER[2])) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        return null;
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
        if (errorEditorCell.contains(rowKey + "-" + column)) {
            tableCell.setStyle("-fx-border-color: #ea2028;-fx-border-with:1 1 1 1");
        }
        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableViewWidth(TableView<String> tableView) {

    }

    @Override
    public boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        if (newText.length() > 255) {
            textField.setText(oldText);
            return true;
        }
        if (!ValidateUtils.validatePattern(newText, ValidateUtils.DOUBLE_PATTERN)) {
            textField.setText(oldText);
            return true;
        }
        CustomAlarmDto customAlarmDto = dataMap.get(rowKey);
        if (columnName.equals(HEADER[1])) {
            SourceObjectProperty uslProperty = valueMap.get(rowKey + "-" + HEADER[2]);

            if (!DAPStringUtils.isBlank((String) uslProperty.getValue())) {
                Double usl = Double.valueOf((String) uslProperty.getValue());
                if (DAPStringUtils.isBlank(newText)) {
                    if (errorEditorCell.contains(rowKey + "-" + HEADER[2]) && uslProperty.isError()) {
                        uslProperty.setError(false);
                        customAlarmDto.setUpperLimit(usl);
                    }
                } else {
                    if (Double.valueOf(newText) >= usl) {
                        errorEditorCell.add(rowKey + "-" + columnName);
                        if (!textField.getStyleClass().contains("text-field-error")) {
                            textField.getStyleClass().add("text-field-error");
                        }
                        TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_LSL_MORE_THEN_USL));
                        return true;
                    } else if (errorEditorCell.contains(rowKey + "-" + HEADER[2])) {
                        errorEditorCell.remove(rowKey + "-" + HEADER[2]);
                        if (uslProperty.isError()) {
                            uslProperty.setError(false);
                            customAlarmDto.setUpperLimit(usl);
                        }
                    }
                }
            }
        } else if (columnName.equals(HEADER[2])) {
            SourceObjectProperty lslProperty = valueMap.get(rowKey + "-" + HEADER[1]);
            if (!DAPStringUtils.isBlank((String) lslProperty.getValue())) {
                Double lsl = Double.valueOf((String) lslProperty.getValue());
                if (DAPStringUtils.isBlank(newText)) {
                    if (lslProperty.isError() && errorEditorCell.contains(rowKey + "-" + HEADER[1])) {
                        lslProperty.setError(false);
                        customAlarmDto.setLowerLimit(lsl);
                    }
                } else {
                    if (Double.valueOf(newText) <= lsl) {
                        errorEditorCell.add(rowKey + "-" + columnName);
                        if (!textField.getStyleClass().contains("text-field-error")) {
                            textField.getStyleClass().add("text-field-error");
                        }
                        TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LESS_THEN_LSL));
                        return true;
                    } else if (errorEditorCell.contains(rowKey + "-" + HEADER[1])) {
                        errorEditorCell.remove(rowKey + "-" + HEADER[1]);
                        if (lslProperty.isError()) {
                            lslProperty.setError(false);
                            customAlarmDto.setLowerLimit(lsl);
                        }
                    }
                }
            }
        }
        if (textField.getStyleClass().contains("text-field-error")) {
            textField.getStyleClass().removeAll("text-field-error");
        }
        if (errorEditorCell.contains(rowKey + "-" + columnName)) {
            errorEditorCell.remove(rowKey + "-" + columnName);
        }
        TooltipUtil.uninstallWarnTooltip(textField);
        return false;
    }

    public boolean hasErrorEditValue() {
        return errorEditorCell.size() != 0;
    }
}

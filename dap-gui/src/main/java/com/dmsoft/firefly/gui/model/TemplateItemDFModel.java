/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.gui.utils.SourceObjectProperty;
import com.dmsoft.firefly.sdk.dai.dto.SpecificationDataDto;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.*;

/**
 * Created by Ethan.Yang on 2018/4/2.
 */
public class TemplateItemDFModel implements TableModel {
    private static final String[] HEADER = new String[]{GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_TEST_ITEM),
            GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_DATA_TYPE), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_LSL_FAIL),
            GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_USL_PASS)};
    private ObservableList<String> columnKey = FXCollections.observableArrayList(Arrays.asList(HEADER));
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private FilteredList<String> templateFilterList;

    private Map<String, SpecificationDataDto> dataMap = new HashMap<>();
    private Map<String, SourceObjectProperty<String>> valueMap = new HashMap<>();
    private Set<String> errorEditorCell = new HashSet<>();

    /**
     * constructor
     */
    public TemplateItemDFModel() {
        templateFilterList = rowKey.filtered(p -> true);
    }

    /**
     * init model
     * data
     *
     * @param dataMap data
     */
    public void initData(Map<String, SpecificationDataDto> dataMap) {
        this.clearTable();
        if (dataMap == null) {
            return;
        }
        this.dataMap = dataMap;
        rowKey.addAll(dataMap.keySet());
        dataMap.entrySet().forEach(e -> {
            this.initValueMap(e.getValue());
        });
    }

    /**
     * add new testItem
     *
     * @param specificationDataDto testItem dto
     */
    public void addTestItem(SpecificationDataDto specificationDataDto) {
        if (specificationDataDto != null) {
            dataMap.put(specificationDataDto.getTestItemName(), specificationDataDto);
            rowKey.add(0, specificationDataDto.getTestItemName());
            this.initValueMap(specificationDataDto);
        }
    }

    /**
     * clear table
     */
    public void clearTable() {
        rowKey.clear();
        dataMap = new HashMap<>();
        valueMap.clear();
        errorEditorCell.clear();
    }

    @SuppressWarnings("unchecked")
    private void initValueMap(SpecificationDataDto specificationDataDto) {
        String name = specificationDataDto.getTestItemName();
        String type = specificationDataDto.getDataType();
        SourceObjectProperty typeProperty = new SourceObjectProperty(type);
        typeProperty.addListener((ov, b1, b2) -> {
            this.updateComboxValue(name, (String) b2);
        });
        String lsl = specificationDataDto.getLslFail() == null ? "" : specificationDataDto.getLslFail();
        String usl = specificationDataDto.getUslPass() == null ? "" : specificationDataDto.getUslPass();
        SourceObjectProperty lslProperty = new SourceObjectProperty(lsl);
        lslProperty.addListener((ov, b1, b2) -> {
            if (errorEditorCell.contains(name + "-" + HEADER[2])) {
                lslProperty.setError(true);
                return;
            }
            specificationDataDto.setLslFail((String) b2);
        });
        SourceObjectProperty uslProperty = new SourceObjectProperty(usl);
        uslProperty.addListener((ov, b1, b2) -> {
            if (errorEditorCell.contains(name + "-" + HEADER[3])) {
                uslProperty.setError(true);
                return;
            }
            specificationDataDto.setUslPass((String) b2);
        });
        valueMap.put(name + "-" + HEADER[0], new SourceObjectProperty(name));
        valueMap.put(name + "-" + HEADER[1], typeProperty);
        valueMap.put(name + "-" + HEADER[2], lslProperty);
        valueMap.put(name + "-" + HEADER[3], uslProperty);
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        templateFilterList.setPredicate(p -> p.toLowerCase().contains(filterTf.toLowerCase()));
    }

    /**
     * method to update combobox value
     *
     * @param rowKey row key
     * @param value  new value
     */
    public void updateComboxValue(String rowKey, String value) {
        dataMap.get(rowKey).setDataType(value);

        if (value.equals(TestItemType.VARIABLE.getCode())) {
            valueMap.get(rowKey + "-" + HEADER[2]).setValue(null);
            valueMap.get(rowKey + "-" + HEADER[3]).setValue(null);
        }
    }

    public ObservableList<String> getComboBoxList() {
        return FXCollections.observableArrayList(TestItemType.VARIABLE.getCode(), TestItemType.ATTRIBUTE.getCode());
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        return valueMap.get(rowKey + "-" + columnName);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return templateFilterList;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        if (columnName.equals(HEADER[2]) || columnName.equals(HEADER[3])) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        return false;
    }

    /**
     * is combo box or not
     *
     * @param columnName column name
     * @return true : is comboBox; false : not
     */
    public boolean isComboBox(String columnName) {
        return columnName.equals(HEADER[1]);
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
    public void setTableView(TableView<String> tableView) {

    }

    @Override
    public boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        if (newText.length() > 255) {
            textField.setText(oldText);
            return true;
        }
        SpecificationDataDto specificationDataDto = dataMap.get(rowKey);
        if (specificationDataDto.getDataType().equals(TestItemType.VARIABLE.getCode())) {
            if (!ValidateUtils.validatePattern(newText, ValidateUtils.DOUBLE_PATTERN)) {
                textField.setText(oldText);
                return true;
            }

            if (columnName.equals(HEADER[2])) {
                SourceObjectProperty uslProperty = valueMap.get(rowKey + "-" + HEADER[3]);

                if (!DAPStringUtils.isBlank((String) uslProperty.getValue())) {
                    Double usl = Double.valueOf((String) uslProperty.getValue());
                    if (DAPStringUtils.isBlank(newText)) {
                        if (errorEditorCell.contains(rowKey + "-" + HEADER[3]) && uslProperty.isError()) {
                            uslProperty.setError(false);
                            specificationDataDto.setUslPass(usl.toString());
                        }
                    } else {
                        if (Double.valueOf(newText) >= usl) {
                            errorEditorCell.add(rowKey + "-" + columnName);
                            if (!textField.getStyleClass().contains("text-field-error")) {
                                textField.getStyleClass().add("text-field-error");
                            }
                            TooltipUtil.installWarnTooltip(textField, GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_LSL_MORE_THEN_USL));
                            return true;
                        } else if (errorEditorCell.contains(rowKey + "-" + HEADER[3])) {
                            errorEditorCell.remove(rowKey + "-" + HEADER[3]);
                            if (uslProperty.isError()) {
                                uslProperty.setError(false);
                                specificationDataDto.setUslPass(usl.toString());
                            }
                        }
                    }
                }
            } else if (columnName.equals(HEADER[3])) {
                SourceObjectProperty lslProperty = valueMap.get(rowKey + "-" + HEADER[2]);
                if (!DAPStringUtils.isBlank((String) lslProperty.getValue())) {
                    Double lsl = Double.valueOf((String) lslProperty.getValue());
                    if (DAPStringUtils.isBlank(newText)) {
                        if (lslProperty.isError() && errorEditorCell.contains(rowKey + "-" + HEADER[2])) {
                            lslProperty.setError(false);
                            specificationDataDto.setLslFail(lsl.toString());
                        }
                    } else {
                        if (Double.valueOf(newText) <= lsl) {
                            errorEditorCell.add(rowKey + "-" + columnName);
                            if (!textField.getStyleClass().contains("text-field-error")) {
                                textField.getStyleClass().add("text-field-error");
                            }
                            TooltipUtil.installWarnTooltip(textField, GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_USL_LESS_THEN_LSL));
                            return true;
                        } else if (errorEditorCell.contains(rowKey + "-" + HEADER[2])) {
                            errorEditorCell.remove(rowKey + "-" + HEADER[2]);
                            if (lslProperty.isError()) {
                                lslProperty.setError(false);
                                specificationDataDto.setLslFail(lsl.toString());
                            }
                        }
                    }
                }
            }
        }
        if (textField.getStyleClass().contains("text-field-error")) {
            textField.getStyleClass().removeAll("text-field-error");
        }
        errorEditorCell.remove(rowKey + "-" + columnName);
        TooltipUtil.uninstallWarnTooltip(textField);
        return false;
    }

    /**
     * method to judge has error or not
     *
     * @return true : has error; false : not error
     */
    public boolean hasErrorEditValue() {
        return errorEditorCell.size() != 0;
    }
}

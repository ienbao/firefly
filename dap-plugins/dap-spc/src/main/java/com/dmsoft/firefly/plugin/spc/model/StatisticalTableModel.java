/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.dto.StatisticalAlarmDto;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcKey;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcStatisticalResultKey;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/12.
 */
public class StatisticalTableModel implements TableModel {
    private static final String[] STATISTICAL_TITLE = UIConstant.SPC_SR_ALL;
    private static final String[] SPC_STATISTICAL_FIX_COLUMN = UIConstant.SPC_STATISTICAL_FIX_COLUMN;
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SourceObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private Set<String> falseSet = new HashSet<>();
    private List<SpcStatisticalResultAlarmDto> spcStatsDtoList;

    private FilteredList<String> statisticalTableRowDataFilteredList;
    private SortedList<String> statisticalTableRowDataSortedList;
    private Map<String, SpcStatisticalResultAlarmDto> keyToStatsDtoMap = Maps.newHashMap();

    private List<TableMenuRowEvent> menuRowEvents;
    private CheckBox allCheckBox;

    private TableView<String> tableView;
    private Set<String> emptyResultKeys = new HashSet<>();

    private Map<String, Color> colorCache = Maps.newHashMap();

    private Set<String> editorCell = new HashSet<>();
    private List<String> editorRowKey = Lists.newArrayList();

    private Set<String> errorEditorCell = new HashSet<>();

    /**
     * constructor
     */
    public StatisticalTableModel() {
        statisticalTableRowDataFilteredList = rowKey.filtered(p -> true);
        statisticalTableRowDataSortedList = new SortedList<>(statisticalTableRowDataFilteredList);
        this.menuRowEvents = Lists.newArrayList();
    }

    /**
     * init model data
     *
     * @param spcStatsDtoList data list
     */
    public void initData(List<SpcStatisticalResultAlarmDto> spcStatsDtoList) {
        tableView.getSortOrder().clear();
        tableView.sort();
        this.spcStatsDtoList = spcStatsDtoList;
        this.clearTableData();
        if (spcStatsDtoList != null) {
            int m = 0;
            for (SpcStatisticalResultAlarmDto dto : spcStatsDtoList) {
                rowKey.add(dto.getKey());
                keyToStatsDtoMap.put(dto.getKey(), dto);
                if (this.isEmptyResult(dto.getStatisticalAlarmDtoMap())) {
                    emptyResultKeys.add(dto.getKey());
                } else {
                    colorCache.put(dto.getKey(), ColorUtils.getTransparentColor(Colur.RAW_VALUES[m % 10], 0.8));
                    m++;
                }
            }
        }
    }

    /**
     * init column
     *
     * @param columnList column list
     */
    public void initColumn(List<String> columnList) {
        columnKey.clear();
        columnKey.addAll(Arrays.asList(SPC_STATISTICAL_FIX_COLUMN));
        columnKey.addAll(columnList);
    }

    /**
     * refresh spc statistical data
     *
     * @param spcStatsDtoList the refresh data
     */
    public void refreshData(List<SpcStatisticalResultAlarmDto> spcStatsDtoList) {
        if (spcStatsDtoList == null) {
            return;
        }
        editorCell.clear();
        editorRowKey.clear();
        errorEditorCell.clear();
        for (SpcStatisticalResultAlarmDto statisticalResultAlarmDto : spcStatsDtoList) {
            String key = statisticalResultAlarmDto.getKey();
            SpcStatisticalResultAlarmDto resultAlarmDto = keyToStatsDtoMap.get(key);
            if (resultAlarmDto != null) {
                Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = statisticalResultAlarmDto.getStatisticalAlarmDtoMap();
                resultAlarmDto.setStatisticalAlarmDtoMap(statisticalAlarmDtoMap);
            }
            this.refreshValue(resultAlarmDto);
        }
        tableView.refresh();
    }

    private void refreshValue(SpcStatisticalResultAlarmDto spcStatsDto) {
        String value = "";
        if (spcStatsDto != null) {
            String spcStatsDtoKey = spcStatsDto.getKey();
            for (int i = 0; i < STATISTICAL_TITLE.length; i++) {
                String columnName = STATISTICAL_TITLE[i];
                if (i == 0) {
                    value = spcStatsDto.getItemName();
                } else if (i == 1) {
                    value = DAPStringUtils.isBlank(spcStatsDto.getCondition()) ? "All" : spcStatsDto.getCondition();
                } else {
                    Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
                    if (statisticalAlarmDtoMap == null) {
                        value = "-";
                    } else {
                        String key = columnName;
                        if (i == 16) {
                            key = SpcStatisticalResultKey.CA.getCode();
                        }
                        value = showValue(key, statisticalAlarmDtoMap.get(key));
                    }
                }
                SourceObjectProperty valueProperty = new SourceObjectProperty<>(value);
                if (columnName.equals(STATISTICAL_TITLE[7]) || columnName.equals(STATISTICAL_TITLE[8])) {
                    valueProperty.addListener((ov, b1, b2) -> {
                        if (DAPStringUtils.isBlank((String) b2) || !DAPStringUtils.isNumeric((String) b2)) {
                            return;
                        }
                        if (!DAPStringUtils.isEqualsString((String) valueProperty.getSourceValue(), (String) b2)) {
                            editorCell.add(spcStatsDtoKey + "-" + columnName);
                            editorRowKey.add(spcStatsDtoKey);
                        } else {
                            editorCell.remove(spcStatsDtoKey + "-" + columnName);
                            editorRowKey.remove(spcStatsDtoKey);
                        }
                        if (errorEditorCell.contains(spcStatsDtoKey + "-" + columnName)) {
                            valueProperty.setError(true);
                            return;
                        }
                        spcStatsDto.getStatisticalAlarmDtoMap().get(columnName).setValue(Double.valueOf((String) b2));
                    });
                }
                valueMap.put(spcStatsDtoKey + "-" + columnName, valueProperty);
            }
        }
    }

    /**
     * clear table
     */
    public void clearTableData() {
        rowKey.clear();
        valueMap.clear();
        checkMap.clear();
        falseSet.clear();
        colorCache.clear();
        emptyResultKeys.clear();
        editorCell.clear();
        allChecked.setValue(false);
        editorRowKey.clear();
        keyToStatsDtoMap.clear();
        errorEditorCell.clear();
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        statisticalTableRowDataFilteredList.setPredicate(p -> {
            if (keyToStatsDtoMap.get(p) == null) {
                return false;
            }
            String testItem = keyToStatsDtoMap.get(p).getItemName();
            return testItem.toLowerCase().contains(filterTf.toLowerCase());
        });
    }

    /**
     * update column
     *
     * @param result column name
     */
    public void updateStatisticalResultColumn(List<String> result) {
        columnKey.remove(3, columnKey.size());
        columnKey.addAll(result);
    }

    /**
     * get select data
     *
     * @return the list of SpcStatsDto
     */
    public List<SpcStatisticalResultAlarmDto> getSelectData() {
        List<SpcStatisticalResultAlarmDto> selectStatsDtoList = Lists.newArrayList();
        for (Map.Entry<String, SimpleObjectProperty<Boolean>> entry : checkMap.entrySet()) {
            String key = entry.getKey().toString();
            boolean isSelect = entry.getValue().getValue();
            if (isSelect) {
                SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(key);
                selectStatsDtoList.add(spcStatsDto);
            }
        }
        return selectStatsDtoList;
    }

    /**
     * get select row key
     *
     * @return row key
     */
    public List<String> getSelectRowKey() {
        List<String> rowList = Lists.newArrayList();
        for (Map.Entry<String, SimpleObjectProperty<Boolean>> entry : checkMap.entrySet()) {
            String key = entry.getKey().toString();
            boolean isSelect = entry.getValue().getValue();
            if (isSelect) {
                rowList.add(key);
            }
        }
        return rowList;
    }

    /**
     * get editor row key
     *
     * @return the row keys
     */
    public List<String> getEditorRowKey() {
        if (editorRowKey == null) {
            return null;
        }
        List<String> rowKeyList = Lists.newArrayList();
        for (String key : editorRowKey) {
            if (!rowKeyList.contains(key)) {
                rowKeyList.add(key);
            }
        }
        return rowKeyList;
    }

    /**
     * get edit row data
     *
     * @return the row data
     */
    public List<SpcStatisticalResultAlarmDto> getEditRowData() {
        List<String> rowKeyList = getEditorRowKey();
        if (rowKeyList == null) {
            return null;
        }
        List<SpcStatisticalResultAlarmDto> editRowDataList = Lists.newArrayList();
        for (String key : rowKeyList) {
            editRowDataList.add(keyToStatsDtoMap.get(key));
        }
        return editRowDataList;
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (valueMap.get(rowKey + "-" + columnName) == null && spcStatsDtoList != null) {
            this.setValueMap(rowKey, columnName);
        }
        return valueMap.get(rowKey + "-" + columnName);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return statisticalTableRowDataSortedList;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        if (columnName.equals(STATISTICAL_TITLE[7]) || columnName.equals(STATISTICAL_TITLE[8])) {
            return true;
        }
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
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
            checkMap.put(rowKey, b);
            falseSet.add(rowKey);
            allChecked.setValue(false);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add(rowKey);
                    allChecked.setValue(false);
                } else {
                    falseSet.remove(rowKey);
                    if (falseSet.isEmpty()) {
                        allChecked.setValue(true);
                    }
                }
            });
        }
        return checkMap.get(rowKey);
    }


    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allChecked;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return menuRowEvents;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        tableCell.setStyle(null);
        tableCell.getStyleClass().remove("error");
        if (DAPStringUtils.isBlank(column)) {
            Color color = colorCache.get(rowKey);
            if (color != null) {
                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(color));
            }
            if (emptyResultKeys.contains(rowKey)) {
                tableCell.getStyleClass().add("error");
            }
        }
        if (editorCell.contains(rowKey + "-" + column)) {
            tableCell.setStyle("-fx-text-fill: #f38400");
        }
        if (errorEditorCell.contains(rowKey + "-" + column)) {
            tableCell.setStyle("-fx-border-color: #ea2028;-fx-border-with:1 1 1 1");
        }

        SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
        Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
        if (statisticalAlarmDtoMap != null) {
            if (column.equals(STATISTICAL_TITLE[16])) {
                column = SpcStatisticalResultKey.CA.getCode();
            }
            if (statisticalAlarmDtoMap.get(column) == null) {
                return tableCell;
            }
            String level = statisticalAlarmDtoMap.get(column).getLevel();
            if (level != null) {
                Color bgColor = getAlarmBackgroundColor(level);
                Color fgColor = Color.BLACK;
                if (Colur.isUsingLightFont(bgColor)) {
                    fgColor = Colur.WHITE;
                }
                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(bgColor) + ";-fx-text-fill:" + ColorUtils.toHexFromColor(fgColor));
            }
        }


        return tableCell;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    /**
     * add menu event
     *
     * @param tableMenuRowEvent event
     */
    public void addTableMenuEvent(TableMenuRowEvent tableMenuRowEvent) {
        menuRowEvents.add(tableMenuRowEvent);
    }

    private void setValueMap(String rowKey, String columnName) {
        String value = "";
        SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
        if (spcStatsDto != null) {
            if (columnName.equals(STATISTICAL_TITLE[0])) {
                value = spcStatsDto.getItemName();
            } else if (columnName.equals(STATISTICAL_TITLE[1])) {
                value = DAPStringUtils.isBlank(spcStatsDto.getCondition()) ? "All" : spcStatsDto.getCondition();
            } else {
                Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
                if (statisticalAlarmDtoMap == null) {
                    value = "-";
                } else {
                    String key = columnName;
                    if (columnName.equals(STATISTICAL_TITLE[16])) {
                        key = SpcStatisticalResultKey.CA.getCode();
                    }
                    value = showValue(key, statisticalAlarmDtoMap.get(key));
                }
            }
        }
        SourceObjectProperty valueProperty = new SourceObjectProperty<>(value);
        if (columnName.equals(STATISTICAL_TITLE[7]) || columnName.equals(STATISTICAL_TITLE[8])) {
            valueProperty.addListener((ov, b1, b2) -> {
                if (DAPStringUtils.isBlank((String) b2) || !DAPStringUtils.isNumeric((String) b2)) {
                    return;
                }
                if (!DAPStringUtils.isEqualsString((String) valueProperty.getSourceValue(), (String) b2)) {
                    editorCell.add(rowKey + "-" + columnName);
                    editorRowKey.add(rowKey);
                } else {
                    editorCell.remove(rowKey + "-" + columnName);
                    editorRowKey.remove(rowKey);
                }
                if (errorEditorCell.contains(rowKey + "-" + columnName)) {
                    valueProperty.setError(true);
                    return;
                }
                spcStatsDto.getStatisticalAlarmDtoMap().get(columnName).setValue(Double.valueOf((String) b2));
            });
        }
        valueMap.put(rowKey + "-" + columnName, valueProperty);
    }

    public SortedList<String> getStatisticalTableRowDataSortedList() {
        return statisticalTableRowDataSortedList;
    }

    public Map<String, SimpleObjectProperty<Boolean>> getCheckMap() {
        return checkMap;
    }

    /**
     * set row background color
     *
     * @param rowKey row key
     * @param color  color
     */
    public void setRowColor(String rowKey, Color color) {
        colorCache.put(rowKey, color);
    }

    public Set<String> getEmptyResultKeys() {
        return emptyResultKeys;
    }

    private boolean isEmptyResult(Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap) {
        StatisticalAlarmDto simpleDto = statisticalAlarmDtoMap.get(STATISTICAL_TITLE[2]);
        if (simpleDto == null || simpleDto.getValue() == null
                || simpleDto.getValue() == 0) {
            return true;
        }
        return false;
    }

    private String showValue(String key, StatisticalAlarmDto statisticalAlarmDto) {
        if (statisticalAlarmDto == null || DAPDoubleUtils.isSpecialNumber(statisticalAlarmDto.getValue())) {
            return "-";
        }
        if (!key.equals(STATISTICAL_TITLE[2]) && !key.equals(STATISTICAL_TITLE[7]) && !key.equals(STATISTICAL_TITLE[8])) {
            return DAPStringUtils.formatDouble(statisticalAlarmDto.getValue(), DigNumInstance.newInstance().getDigNum());
        }

        return statisticalAlarmDto.getValue().toString();
    }

    public Map<String, Color> getColorCache() {
        return colorCache;
    }

    private Color getAlarmBackgroundColor(String level) {
        Color color = Color.WHITE;
        if (level.equals(SpcKey.EXCELLENT.getCode())) {
            color = Colur.GREEN;
        } else if (level.equals(SpcKey.GOOD.getCode())) {
            color = Colur.LEVEL_A;
        } else if (level.equals(SpcKey.ACCEPTABLE.getCode())) {
            color = Colur.LEVEL_B;
        } else if (level.equals(SpcKey.RECTIFICATION.getCode())) {
            color = Colur.LEVEL_C;
        } else if (level.equals(SpcKey.BAD.getCode())) {
            color = Colur.LEVEL_D;
        } else if (level.equals(SpcKey.PASS.getCode())) {
            color = Colur.GREEN;
        } else if (level.equals(SpcKey.FAIL.getCode())) {
            color = Colur.LEVEL_D;
        }
        return color;
    }

    /**
     * get spc statistical data
     *
     * @return the list of data
     */
    public List<SpcStatisticalResultAlarmDto> getSpcStatsDtoList() {
        if (keyToStatsDtoMap == null) {
            return null;
        }
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = Lists.newArrayList();
        for (Map.Entry<String, SpcStatisticalResultAlarmDto> entry : keyToStatsDtoMap.entrySet()) {
            spcStatisticalResultAlarmDtoList.add(entry.getValue());
        }
        return spcStatisticalResultAlarmDtoList;
    }

    /**
     * is menu event enable
     *
     * @param rowKey row key
     * @return boolean
     */
    public boolean isMenuEventEnable(String rowKey) {
        return !emptyResultKeys.contains(rowKey);
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
        if (DAPStringUtils.isBlank(newText)) {
            errorEditorCell.add(rowKey + "-" + columnName);
            if (!textField.getStyleClass().contains("text-field-error")) {
                textField.getStyleClass().add("text-field-error");
            }
            TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LSL_EMPTY));
            return true;
        }
        SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
        Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
        if (columnName.equals(STATISTICAL_TITLE[7])) {
            SourceObjectProperty uslProperty = valueMap.get(rowKey + "-" + STATISTICAL_TITLE[8]);
            StatisticalAlarmDto statisticalAlarmDto = statisticalAlarmDtoMap.get(SpcStatisticalResultKey.USL.getCode());
            Double usl = Double.valueOf((String) uslProperty.getValue());
            if (!DAPStringUtils.isNumeric(newText) || Double.valueOf(newText) >= usl) {
                errorEditorCell.add(rowKey + "-" + columnName);
                if (!textField.getStyleClass().contains("text-field-error")) {
                    textField.getStyleClass().add("text-field-error");
                }
                TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_LSL_MORE_THEN_USL));
                return true;
            } else if (errorEditorCell.contains(rowKey + "-" + STATISTICAL_TITLE[8])) {
                errorEditorCell.remove(rowKey + "-" + STATISTICAL_TITLE[8]);
                if (uslProperty.isError()) {
                    uslProperty.setError(false);
                    statisticalAlarmDto.setValue(usl);
                }
            }
        } else if (columnName.equals(STATISTICAL_TITLE[8])) {
            SourceObjectProperty lslProperty = valueMap.get(rowKey + "-" + STATISTICAL_TITLE[7]);
            StatisticalAlarmDto statisticalAlarmDto = statisticalAlarmDtoMap.get(SpcStatisticalResultKey.LSL.getCode());
            Double lsl = Double.valueOf((String) lslProperty.getValue());
            if (Double.valueOf(newText) <= lsl) {
                errorEditorCell.add(rowKey + "-" + columnName);
                if (!textField.getStyleClass().contains("text-field-error")) {
                    textField.getStyleClass().add("text-field-error");
                }
                TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LESS_THEN_LSL));
                return true;
            } else if (errorEditorCell.contains(rowKey + "-" + STATISTICAL_TITLE[7])) {
                errorEditorCell.remove(rowKey + "-" + STATISTICAL_TITLE[7]);
                if (lslProperty.isError()) {
                    lslProperty.setError(false);
                    statisticalAlarmDto.setValue(lsl);
                }
            }
        }
        if (errorEditorCell.contains(rowKey + "-" + columnName)) {
            errorEditorCell.remove(rowKey + "-" + columnName);
            textField.getStyleClass().removeAll("text-field-error");
            TooltipUtil.uninstallWarnTooltip(textField);
            return false;
        }
        textField.getStyleClass().removeAll("text-field-error");
        TooltipUtil.uninstallWarnTooltip(textField);
        return false;
    }

    public boolean hasErrorEditValue() {
        return errorEditorCell.size() != 0;
    }
}

package com.dmsoft.firefly.plugin.yield.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldResultDto;
import com.dmsoft.firefly.plugin.yield.utils.SourceObjectProperty;
import com.dmsoft.firefly.plugin.yield.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
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
import java.util.List;
import java.util.Map;

public class OverViewTableModel  implements TableModel{
    private static final String[] YIELD_OVERVIEW_FIX_COLUMN = UIConstant.YIELD_OVERVIEW_FIX_COLUMN;

    private List<String> columnList;
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SourceObjectProperty<String>> valueMap = Maps.newLinkedHashMap();
    private FilteredList<String> overViewTableRowDataFilteredList;
    private SortedList<String> overViewTableRowDataSortedList;
    private TableView<String> tableView;
    private List<TableMenuRowEvent> menuRowEvents;


    /**
     * constructor
     */
    public OverViewTableModel() {
        this.columnKey.addAll(YIELD_OVERVIEW_FIX_COLUMN);
        overViewTableRowDataFilteredList = rowKey.filtered(p -> true);
        overViewTableRowDataSortedList = new SortedList<>(overViewTableRowDataFilteredList);
        this.menuRowEvents = Lists.newArrayList();
    }

    /**
     * init column
     *
     * @param columnList column list
     */
    public void initColumn(List<String> columnList) {
        this.columnList = columnList;
        columnKey.remove(3, columnKey.size());
//        columnKey.addAll(Arrays.asList(SPC_STATISTICAL_FIX_COLUMN));
        columnKey.addAll(columnList);
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        overViewTableRowDataFilteredList.setPredicate(p -> {
//            if (keyToStatsDtoMap.get(p) == null) {
                return false;
//            }
//            String testItem = keyToStatsDtoMap.get(p).getItemName();
//            return testItem.toLowerCase().contains(filterTf.toLowerCase());
        });
      }

    public SortedList<String> getOverViewTableRowDataSortedList() {
        return overViewTableRowDataSortedList;
    }

    /**
     * add menu event
     *
     * @param tableMenuRowEvent event
     */
    public void addTableMenuEvent(TableMenuRowEvent tableMenuRowEvent) {
        menuRowEvents.add(tableMenuRowEvent);
    }

    /**
     * init model data
     *
     * @param spcStatsDtoList data list
     */
    public void initData(List<YieldOverviewResultAlarmDto> overviewResultAlarmDtoList) {
        tableView.getSortOrder().clear();
        tableView.sort();
//        this.spcStatsDtoList = spcStatsDtoList;
//        this.clearTableData();
//        if (spcStatsDtoList != null) {
//            int m = 0;
//            for (SpcStatisticalResultAlarmDto dto : spcStatsDtoList) {
//                rowKey.add(dto.getKey());
//                keyToStatsDtoMap.put(dto.getKey(), dto);
//                if (this.isEmptyResult(dto.getStatisticalAlarmDtoMap())) {
//                    emptyResultKeys.add(dto.getKey());
//                } else {
//                    colorCache.put(dto.getKey(), ColorUtils.getTransparentColor(Colur.RAW_VALUES[m % 10], 0.8));
//                    m++;
//                }
//            }
//        }
    }


    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
//        if (valueMap.get(rowKey + "-" + columnName) == null && spcStatsDtoList != null) {
//            this.setValueMap(rowKey, columnName);
//        }
        return valueMap.get(rowKey + "-" + columnName);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return overViewTableRowDataSortedList;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
//        if (!isTimer && (columnName.equals(STATISTICAL_TITLE[7]) || columnName.equals(STATISTICAL_TITLE[8]))) {
//            return true;
//        }
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
//        if (checkMap.get(rowKey) == null) {
//            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
//            checkMap.put(rowKey, b);
//            falseSet.add(rowKey);
//            allChecked.setValue(false);
//            b.addListener((ov, b1, b2) -> {
//                if (!b2) {
//                    falseSet.add(rowKey);
//                    allChecked.setValue(false);
//                } else {
//                    falseSet.remove(rowKey);
//                    if (falseSet.isEmpty()) {
//                        allChecked.setValue(true);
//                    }
//                }
//            });
//        }
//        return checkMap.get(rowKey);
        return null;
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
//        return allChecked;
        return null;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
//        return menuRowEvents;
        return null;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        if (!this.isEditableTextField(column)) {
            tableCell.setEditable(false);
        } else {
            tableCell.setEditable(true);
        }
        tableCell.setStyle(null);
        tableCell.getStyleClass().remove("error");
//        if (DAPStringUtils.isBlank(column)) {
//            Color color = colorCache.get(rowKey);
//            if (color != null) {
//                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(color));
//            }
//            if (emptyResultKeys.contains(rowKey)) {
//                tableCell.getStyleClass().add("error");
//            }
//        }
//        if (editorCell.contains(rowKey + "-" + column)) {
//            tableCell.setStyle("-fx-text-fill: #f38400");
//        }
//        if (errorEditorCell.contains(rowKey + "-" + column)) {
//            tableCell.setStyle("-fx-border-color: #ea2028;-fx-border-with:1 1 1 1");
//        }

//        SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
//        Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
//        if (statisticalAlarmDtoMap != null) {
//            if (column.equals(STATISTICAL_TITLE[16])) {
//                column = SpcStatisticalResultKey.CA.getCode();
//            }
//            if (statisticalAlarmDtoMap.get(column) == null) {
//                return tableCell;
//            }
//            String level = statisticalAlarmDtoMap.get(column).getLevel();
//            if (level != null) {
//                Color bgColor = getAlarmBackgroundColor(level);
//                Color fgColor = Color.BLACK;
//                if (Colur.isUsingLightFont(bgColor)) {
//                    fgColor = Colur.WHITE;
//                }
//                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(bgColor) + ";-fx-text-fill:" + ColorUtils.toHexFromColor(fgColor));
//            }
//        }
//
//
//        return tableCell;
        return null;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }

    public CheckBox getAllCheckBox() {
//        return allCheckBox;
        return null;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
//        this.allCheckBox = checkBox;
    }

    @Override
    public boolean isMenuEventEnable(String rowKey) {
//        return !emptyResultKeys.contains(rowKey);
        return false;
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
//        if (DAPStringUtils.isBlank(newText)) {
//            errorEditorCell.add(rowKey + "-" + columnName);
//            if (!textField.getStyleClass().contains("text-field-error")) {
//                textField.getStyleClass().add("text-field-error");
//            }
//            TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LSL_EMPTY));
//            return true;
//        }
//        SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
//        Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
//        if (columnName.equals(STATISTICAL_TITLE[7])) {
//            SourceObjectProperty uslProperty = valueMap.get(rowKey + "-" + STATISTICAL_TITLE[8]);
//            if (!DAPStringUtils.isNumeric((String) uslProperty.getValue())) {
//                return false;
//            }
//            StatisticalAlarmDto statisticalAlarmDto = statisticalAlarmDtoMap.get(SpcStatisticalResultKey.USL.getCode());
//            Double usl = Double.valueOf((String) uslProperty.getValue());
//            if (!DAPStringUtils.isNumeric(newText) || Double.valueOf(newText) >= usl) {
//                errorEditorCell.add(rowKey + "-" + columnName);
//                if (!textField.getStyleClass().contains("text-field-error")) {
//                    textField.getStyleClass().add("text-field-error");
//                }
//                TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_LSL_MORE_THEN_USL));
//                return true;
//            } else if (errorEditorCell.contains(rowKey + "-" + STATISTICAL_TITLE[8])) {
//                errorEditorCell.remove(rowKey + "-" + STATISTICAL_TITLE[8]);
//                if (uslProperty.isError()) {
//                    uslProperty.setError(false);
//                    statisticalAlarmDto.setValue(usl);
//                }
//            }
//        } else if (columnName.equals(STATISTICAL_TITLE[8])) {
//            SourceObjectProperty lslProperty = valueMap.get(rowKey + "-" + STATISTICAL_TITLE[7]);
//            if (!DAPStringUtils.isNumeric((String) lslProperty.getValue())) {
//                return false;
//            }
//            StatisticalAlarmDto statisticalAlarmDto = statisticalAlarmDtoMap.get(SpcStatisticalResultKey.LSL.getCode());
//            Double lsl = Double.valueOf((String) lslProperty.getValue());
//            if (Double.valueOf(newText) <= lsl) {
//                errorEditorCell.add(rowKey + "-" + columnName);
//                if (!textField.getStyleClass().contains("text-field-error")) {
//                    textField.getStyleClass().add("text-field-error");
//                }
//                TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LESS_THEN_LSL));
//                return true;
//            } else if (errorEditorCell.contains(rowKey + "-" + STATISTICAL_TITLE[7])) {
//                errorEditorCell.remove(rowKey + "-" + STATISTICAL_TITLE[7]);
//                if (lslProperty.isError()) {
//                    lslProperty.setError(false);
//                    statisticalAlarmDto.setValue(lsl);
//                }
//            }
//        }
//        if (errorEditorCell.contains(rowKey + "-" + columnName)) {
//            errorEditorCell.remove(rowKey + "-" + columnName);
//            textField.getStyleClass().removeAll("text-field-error");
//            TooltipUtil.uninstallWarnTooltip(textField);
//            return false;
//        }
//        textField.getStyleClass().removeAll("text-field-error");
//        TooltipUtil.uninstallWarnTooltip(textField);
        return false;
    }

    public Map<String, SourceObjectProperty<String>> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, SourceObjectProperty<String>> valueMap) {
        this.valueMap = valueMap;
    }
}

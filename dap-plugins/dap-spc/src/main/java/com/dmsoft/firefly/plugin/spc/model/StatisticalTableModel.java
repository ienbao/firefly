/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.NewTableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.utils.Colur;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.StringUtils;
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

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/12.
 */
public class StatisticalTableModel implements NewTableModel {
    private static final String[] STATISTICAL_TITLE = UIConstant.SPC_SR_ALL;
    private ObservableList<String> columnKey = FXCollections.observableArrayList(Arrays.asList(STATISTICAL_TITLE));
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private Set<String> falseSet = new HashSet<>();
    private List<SpcStatsDto> spcStatsDtoList;

    private FilteredList<String> statisticalTableRowDataFilteredList;
    private SortedList<String> statisticalTableRowDataSortedList;
    private Map<String, String> keyToTestItemMap = Maps.newHashMap();

    private List<TableMenuRowEvent> menuRowEvents;
    private CheckBox allCheckBox;

    private TableView<String> tableView;
    private Set<String> emptyResultKeys = new HashSet<>();

    private Map<String, Color> colorCache = Maps.newHashMap();

    /**
     * constructor
     */
    public StatisticalTableModel() {
        columnKey.add(0, "");
        statisticalTableRowDataFilteredList = rowKey.filtered(p -> true);
        statisticalTableRowDataSortedList = new SortedList<>(statisticalTableRowDataFilteredList);
        this.menuRowEvents = Lists.newArrayList();
    }

    /**
     * init model data
     *
     * @param spcStatsDtoList data list
     */
    public void initData(List<SpcStatsDto> spcStatsDtoList) {
        this.spcStatsDtoList = spcStatsDtoList;
        this.clearTableData();
        if (spcStatsDtoList != null) {
            int m = 0;
            for (SpcStatsDto dto : spcStatsDtoList) {
                rowKey.add(dto.getKey());
                keyToTestItemMap.put(dto.getKey(), dto.getItemName());
                if (this.isEmptyResult(dto.getStatsResultDto())) {
                    emptyResultKeys.add(dto.getKey());
                } else {
                    colorCache.put(dto.getKey(), ColorUtils.getTransparentColor(Colur.RAW_VALUES[m % 10], 1));
                    m++;
                }
            }
            ;
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
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        statisticalTableRowDataFilteredList.setPredicate(p -> {
            String testItem = keyToTestItemMap.get(p);
            return testItem.contains(filterTf);
        });
    }

    /**
     * update column
     *
     * @param result column name
     */
    public void updateStatisticalResultColumn(List<String> result) {
        columnKey.addAll(result);
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
        if (StringUtils.isBlank(column)) {
            Color color = colorCache.get(rowKey);
            if (color != null) {
                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(color));
            }
            if (emptyResultKeys.contains(rowKey)) {
                tableCell.getGraphic().setDisable(true);
                tableCell.getStyleClass().add("error");
            }
        }
        if (column.equals("CPK")) {
            SimpleObjectProperty<String> stringSimpleObjectProperty = this.valueMap.get(rowKey + "-" + column);
            if (stringSimpleObjectProperty != null && stringSimpleObjectProperty.getValue() != null) {
                String value = stringSimpleObjectProperty.getValue();
                if (StringUtils.isNumeric(value) && Double.valueOf(value) > 60) {
                    tableCell.setStyle("-fx-background-color:#ea2028;-fx-text-fill: #ffffff");
                } else {
                    tableCell.setStyle("-fx-background-color:#51b511;-fx-text-fill: #ffffff");
                }
            }
        }


        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
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
        for (SpcStatsDto spcStatsDto : spcStatsDtoList) {
            if (spcStatsDto.getKey().equals(rowKey)) {
                if (columnName.equals(STATISTICAL_TITLE[0])) {
                    value = spcStatsDto.getItemName();
                } else if (columnName.equals(STATISTICAL_TITLE[1])) {
                    value = spcStatsDto.getCondition();
                } else {
                    SpcStatsResultDto spcStatsResultDto = spcStatsDto.getStatsResultDto();
                    if (spcStatsDto.getStatsResultDto() == null) {
                        value = "-";
                    } else {
                        if (columnName.equals(STATISTICAL_TITLE[2])) {
                            value = showValue(spcStatsResultDto.getSamples());
                        } else if (columnName.equals(STATISTICAL_TITLE[3])) {
                            value = showValue(spcStatsResultDto.getAvg());
                        } else if (columnName.equals(STATISTICAL_TITLE[4])) {
                            value = showValue(spcStatsResultDto.getMax());
                        } else if (columnName.equals(STATISTICAL_TITLE[5])) {
                            value = showValue(spcStatsResultDto.getMin());
                        } else if (columnName.equals(STATISTICAL_TITLE[6])) {
                            value = showValue(spcStatsResultDto.getStDev());
                        } else if (columnName.equals(STATISTICAL_TITLE[7])) {
                            value = showValue(spcStatsResultDto.getLsl());
                        } else if (columnName.equals(STATISTICAL_TITLE[8])) {
                            value = showValue(spcStatsResultDto.getUsl());
                        } else if (columnName.equals(STATISTICAL_TITLE[9])) {
                            value = showValue(spcStatsResultDto.getCenter());
                        } else if (columnName.equals(STATISTICAL_TITLE[10])) {
                            value = showValue(spcStatsResultDto.getRange());
                        } else if (columnName.equals(STATISTICAL_TITLE[11])) {
                            value = showValue(spcStatsResultDto.getLcl());
                        } else if (columnName.equals(STATISTICAL_TITLE[12])) {
                            value = showValue(spcStatsResultDto.getUcl());
                        } else if (columnName.equals(STATISTICAL_TITLE[13])) {
                            value = showValue(spcStatsResultDto.getKurtosis());
                        } else if (columnName.equals(STATISTICAL_TITLE[14])) {
                            value = showValue(spcStatsResultDto.getSkewness());
                        } else if (columnName.equals(STATISTICAL_TITLE[15])) {
                            value = showValue(spcStatsResultDto.getCpk());
                        } else if (columnName.equals(STATISTICAL_TITLE[16])) {
                            value = showValue(spcStatsResultDto.getCa());
                        } else if (columnName.equals(STATISTICAL_TITLE[17])) {
                            value = showValue(spcStatsResultDto.getCp());
                        } else if (columnName.equals(STATISTICAL_TITLE[18])) {
                            value = showValue(spcStatsResultDto.getCpl());
                        } else if (columnName.equals(STATISTICAL_TITLE[19])) {
                            value = showValue(spcStatsResultDto.getCpu());
                        } else if (columnName.equals(STATISTICAL_TITLE[20])) {
                            value = showValue(spcStatsResultDto.getWithinPPM());
                        } else if (columnName.equals(STATISTICAL_TITLE[21])) {
                            value = showValue(spcStatsResultDto.getOverallPPM());
                        } else if (columnName.equals(STATISTICAL_TITLE[22])) {
                            value = showValue(spcStatsResultDto.getPp());
                        } else if (columnName.equals(STATISTICAL_TITLE[23])) {
                            value = showValue(spcStatsResultDto.getPpk());
                        } else if (columnName.equals(STATISTICAL_TITLE[24])) {
                            value = showValue(spcStatsResultDto.getPpl());
                        } else if (columnName.equals(STATISTICAL_TITLE[25])) {
                            value = showValue(spcStatsResultDto.getPpu());
                        }
                    }
                }
            }
        }
        valueMap.put(rowKey + "-" + columnName, new SimpleObjectProperty<>(value));
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

    private boolean isEmptyResult(SpcStatsResultDto spcStatsResultDto) {
        if (spcStatsResultDto == null || spcStatsResultDto.getSamples() == null
                || spcStatsResultDto.getSamples() == 0) {
            return true;
        }
        return false;
    }

    private String showValue(Double value) {
        if (value == null) {
            return "-";
        }
        return value.toString();
    }
}

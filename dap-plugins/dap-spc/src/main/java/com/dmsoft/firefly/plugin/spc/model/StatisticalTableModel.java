/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.*;

/**
 * Created by Ethan.Yang on 2018/2/12.
 */
public class StatisticalTableModel implements TableModel {
    private static final String[] STATISTICAL_TITLE = UIConstant.SPC_SR_ALL;
    private ObservableList<String> columnKey = FXCollections.observableArrayList(Arrays.asList(STATISTICAL_TITLE));
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SimpleObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private Set<String> falseSet = new HashSet<>();
    private List<SpcStatsDto> spcStatsDtoList;

    private FilteredList<String> statisticalTableRowDataFilteredList;
//    private SortedList<String> rowKey;
    private Map<String, String> keyToTestItemMap = Maps.newHashMap();

    /**
     * constructor
     */
    public StatisticalTableModel() {
        columnKey.add(0, "");
//        statisticalTableRowDataObservableListrowKey = statisticalTableRowDataObservableListrowKey.filtered(p -> true);
//        rowKey = new SortedList<>(statisticalTableRowDataFilteredList);
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
            spcStatsDtoList.forEach(dto -> {
                rowKey.add(dto.getKey());
                keyToTestItemMap.put(dto.getKey(), dto.getItemName());
            });
        }
    }

    /**
     * clear table
     */
    public void clearTableData() {
        rowKey.clear();
        valueMap.clear();
    }

    public void filterTestItem(String filterTf) {
        statisticalTableRowDataFilteredList.setPredicate(p -> {
            String testItem = keyToTestItemMap.get(p);
            return testItem.contains(filterTf);
        });
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
        return rowKey;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        if (columnName.equals(STATISTICAL_TITLE[7]) || columnName.equals(STATISTICAL_TITLE[8])) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isModified(String rowKey, String columnName, String value) {
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
        if (checkMap.get(rowKey + "-" + columnName) == null) {
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
            checkMap.put(rowKey + "-" + columnName, b);
            falseSet.add(rowKey + "-" + columnName);
            allChecked.setValue(false);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add(rowKey + "-" + columnName);
                    allChecked.setValue(false);
                } else {
                    falseSet.remove(rowKey + "-" + columnName);
                    if (falseSet.isEmpty()) {
                        allChecked.setValue(true);
                    }
                }
            });
        }
        return checkMap.get(rowKey + "-" + columnName);
    }

    @Override
    public void setAllSelected(boolean value, String columnName) {
        for (ObjectProperty<Boolean> b : checkMap.values()) {
            b.setValue(value);
        }
        if (value) {
            falseSet.clear();
            allChecked.setValue(true);
        } else {
            falseSet.addAll(checkMap.keySet());
            if (!falseSet.isEmpty()) {
                allChecked.setValue(false);
            }
        }
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allChecked;
    }

    private void setValueMap(String rowKey, String columnName) {
        String value = "";
        for (SpcStatsDto spcStatsDto : spcStatsDtoList) {
            if (spcStatsDto.getKey().equals(rowKey)) {
                if (columnName.equals(STATISTICAL_TITLE[0])) {
                    value = spcStatsDto.getItemName();
                } else if (columnName.equals(STATISTICAL_TITLE[1])) {
                    value = spcStatsDto.getCondition();
                } else if (columnName.equals(STATISTICAL_TITLE[2])) {
                    value = spcStatsDto.getStatsResultDto().getSamples();
                } else if (columnName.equals(STATISTICAL_TITLE[3])) {
                    value = spcStatsDto.getStatsResultDto().getAvg();
                } else if (columnName.equals(STATISTICAL_TITLE[4])) {
                    value = spcStatsDto.getStatsResultDto().getMax();
                } else if (columnName.equals(STATISTICAL_TITLE[5])) {
                    value = spcStatsDto.getStatsResultDto().getMin();
                } else if (columnName.equals(STATISTICAL_TITLE[6])) {
                    value = spcStatsDto.getStatsResultDto().getStDev();
                } else if (columnName.equals(STATISTICAL_TITLE[7])) {
                    value = spcStatsDto.getStatsResultDto().getLsl();
                } else if (columnName.equals(STATISTICAL_TITLE[8])) {
                    value = spcStatsDto.getStatsResultDto().getUsl();
                } else if (columnName.equals(STATISTICAL_TITLE[9])) {
                    value = spcStatsDto.getStatsResultDto().getCenter();
                } else if (columnName.equals(STATISTICAL_TITLE[10])) {
                    value = spcStatsDto.getStatsResultDto().getRange();
                } else if (columnName.equals(STATISTICAL_TITLE[11])) {
                    value = spcStatsDto.getStatsResultDto().getLcl();
                } else if (columnName.equals(STATISTICAL_TITLE[12])) {
                    value = spcStatsDto.getStatsResultDto().getUcl();
                } else if (columnName.equals(STATISTICAL_TITLE[13])) {
                    value = spcStatsDto.getStatsResultDto().getKurtosis();
                } else if (columnName.equals(STATISTICAL_TITLE[14])) {
                    value = spcStatsDto.getStatsResultDto().getSkewness();
                } else if (columnName.equals(STATISTICAL_TITLE[15])) {
                    value = spcStatsDto.getStatsResultDto().getCpk();
                } else if (columnName.equals(STATISTICAL_TITLE[16])) {
                    value = spcStatsDto.getStatsResultDto().getCa();
                } else if (columnName.equals(STATISTICAL_TITLE[17])) {
                    value = spcStatsDto.getStatsResultDto().getCp();
                } else if (columnName.equals(STATISTICAL_TITLE[18])) {
                    value = spcStatsDto.getStatsResultDto().getCpl();
                } else if (columnName.equals(STATISTICAL_TITLE[19])) {
                    value = spcStatsDto.getStatsResultDto().getCpu();
                } else if (columnName.equals(STATISTICAL_TITLE[20])) {
                    value = spcStatsDto.getStatsResultDto().getWithinPPM();
                } else if (columnName.equals(STATISTICAL_TITLE[21])) {
                    value = spcStatsDto.getStatsResultDto().getOverallPPM();
                } else if (columnName.equals(STATISTICAL_TITLE[22])) {
                    value = spcStatsDto.getStatsResultDto().getPp();
                } else if (columnName.equals(STATISTICAL_TITLE[23])) {
                    value = spcStatsDto.getStatsResultDto().getPpk();
                } else if (columnName.equals(STATISTICAL_TITLE[24])) {
                    value = spcStatsDto.getStatsResultDto().getPpl();
                } else if (columnName.equals(STATISTICAL_TITLE[25])) {
                    value = spcStatsDto.getStatsResultDto().getPpu();
                }
            }
        }
        valueMap.put(rowKey + "-" + columnName, new SimpleObjectProperty<>(value));
    }

}

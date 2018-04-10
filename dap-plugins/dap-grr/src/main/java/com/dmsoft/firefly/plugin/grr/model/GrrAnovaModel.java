package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaDto;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/4/8.
 */
public class GrrAnovaModel implements TableModel {

    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, GrrAnovaDto> rowKeyDataMap;

    /**
     * Construct a new GrrAnovaModel.
     */
    public GrrAnovaModel() {
        headerArray = FXCollections.observableArrayList();
        rowKeyArray = FXCollections.observableArrayList();
        rowKeyDataMap = Maps.newLinkedHashMap();
    }

    /**
     * Set anova source table data
     *
     * @param anovaDtos anova source data
     */
    public void setData(List<GrrAnovaDto> anovaDtos) {
        this.clearTableData();
        if (anovaDtos == null) {
            return;
        }
        anovaDtos.forEach(grrAnovaDto -> {
            rowKeyDataMap.put(grrAnovaDto.getName().name(), grrAnovaDto);
            rowKeyArray.add(grrAnovaDto.getName().name());
        });
    }

    /**
     * Clear anova table data
     */
    public void clearTableData() {
        this.rowKeyArray.clear();
        this.rowKeyDataMap.clear();
    }

    /**
     * Init anova table column title
     *
     * @param columnList column names
     */
    public void initColumn(List<String> columnList) {
        headerArray.clear();
        headerArray.addAll(columnList);
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (!rowKeyDataMap.containsKey(rowKey)) {
            return null;
        }
        GrrAnovaDto grrAnovaDto = rowKeyDataMap.get(rowKey);
        int digNum = DigNumInstance.newInstance().getDigNum();
        int index = headerArray.indexOf(columnName);
        switch (index) {
            case 0:
                return new SimpleObjectProperty<>(grrAnovaDto.getName().name());
            case 1:
                return new SimpleObjectProperty<>(formatterNormalValue(grrAnovaDto.getDf(), digNum));
            case 2:
                return new SimpleObjectProperty<>(formatterNormalValue(grrAnovaDto.getSs(), digNum));
            case 3:
                return new SimpleObjectProperty<>(formatterNormalValue(grrAnovaDto.getMs(), digNum));
            case 4:
                return new SimpleObjectProperty<>(formatterNormalValue(grrAnovaDto.getF(), digNum));
            case 5:
                return new SimpleObjectProperty<>(formatterNormalValue(grrAnovaDto.getProbF(), digNum));
            default:
                return null;
        }
    }

    private String formatterNormalValue(Double value, int digNum) {
        String valueStr = DAPStringUtils.isInfinityAndNaN(value) ? "-" : DAPStringUtils.formatDouble(value, digNum);
        valueStr = DAPStringUtils.isBlankWithSpecialNumber(valueStr) ? "-" : valueStr;
        return valueStr;
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return rowKeyArray;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
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
        return null;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }
}

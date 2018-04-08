package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TableComparatorUtils;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSourceDto;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/4/8.
 */
public class GrrSourceModel implements TableModel {

    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, GrrSourceDto> rowKeyDataMap;

    /**
     * Construct a new GrrSourceModel.
     */
    public GrrSourceModel() {
        headerArray = FXCollections.observableArrayList();
        rowKeyArray = FXCollections.observableArrayList();
        rowKeyDataMap = Maps.newLinkedHashMap();
    }

    /**
     * Set grr source table data source
     *
     * @param grrSourceDtos data sources
     */
    public void setData(List<GrrSourceDto> grrSourceDtos) {
        this.clearTableData();
        if (grrSourceDtos == null) {
            return;
        }
        grrSourceDtos.forEach(grrSourceDto -> {
            this.rowKeyArray.add(grrSourceDto.getName().name());
            this.rowKeyDataMap.put(grrSourceDto.getName().name(), grrSourceDto);
        });
    }

    /**
     * Init table column title
     *
     * @param columnList column names
     */
    public void initColumn(List<String> columnList) {
        headerArray.clear();
        headerArray.addAll(columnList);
    }

    /**
     * Clear table row data
     */
    public void clearTableData() {
        this.rowKeyArray.clear();
        this.rowKeyDataMap.clear();
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
        GrrSourceDto sourceDto = rowKeyDataMap.get(rowKey);
        int digNum = DigNumInstance.newInstance().getDigNum();
        int percentDigNum = digNum - 2 >= 0 ? digNum - 2 : 0;
        int index = headerArray.indexOf(columnName);
        switch (index) {
            case 0:
                return new SimpleObjectProperty<>(sourceDto.getName().name());
            case 1:
                return new SimpleObjectProperty<>(formatterNormalValue(sourceDto.getSigma(), digNum));
            case 2:
                return new SimpleObjectProperty<>(formatterNormalValue(sourceDto.getStudyVar(), digNum));
            case 3:
                return new SimpleObjectProperty<>(formatterNormalValue(sourceDto.getVariation(), digNum));
            case 4:
                return new SimpleObjectProperty<>(formatterPercentValue(sourceDto.getTotalVariation(), percentDigNum));
            case 5:
                return new SimpleObjectProperty<>(formatterPercentValue(sourceDto.getTotalVariation(), percentDigNum));
            case 6:
                return new SimpleObjectProperty<>(formatterPercentValue(sourceDto.getTotalTolerance(), percentDigNum));
            default:
                return null;
        }
    }

    private String formatterNormalValue(Double value, int digNum) {
        String valueStr = DAPStringUtils.isInfinityAndNaN(value) ? "-" : DAPStringUtils.formatDouble(value, digNum);
        valueStr = DAPStringUtils.isBlankWithSpecialNumber(valueStr) ? "-" : valueStr;
        return valueStr;
    }

    private String formatterPercentValue(Double value, int digNum) {
        String valueStr = DAPStringUtils.isInfinityAndNaN(value) ? "-" : DAPStringUtils.formatDouble(value, digNum);
        valueStr = DAPStringUtils.isBlankWithSpecialNumber(valueStr) ? "-" : valueStr + "%";
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
        if (column.equals(UIConstant.GRR_SOURCE_TITLE[4]) || column.equals(UIConstant.GRR_SOURCE_TITLE[5]) || column.equals(UIConstant.GRR_SOURCE_TITLE[6])) {
            tableCell.getTableColumn().setComparator((Comparator<T>) TableComparatorUtils.getContainsPercentColumnComparator());
            return tableCell;
        }
        return null;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }
}

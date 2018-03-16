package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.GrrItemResultDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.utils.DataConvertUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Created by cherry on 2018/3/15.
 */
public class ItemResultModel implements TableModel {

    private SearchDataFrame dataFrame;
    private GrrItemResultDto itemResultDto;
    private List<GrrViewDataDto> grrViewDataDtos;
    private String trialKey;
    private String appraiserKey;
    private String currentItemName;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;

    /**
     * Set item result model data
     *
     * @param dataFrame       data frame
     * @param currentItemName item name
     * @param viewDataDtos    view data
     * @param itemResultDto   item result
     */
    public void setData(SearchDataFrame dataFrame,
                        String currentItemName,
                        List<GrrViewDataDto> viewDataDtos,
                        GrrItemResultDto itemResultDto) {

        this.dataFrame = dataFrame;
        this.currentItemName = currentItemName;
        this.grrViewDataDtos = viewDataDtos;
        this.itemResultDto = itemResultDto;
    }

    /**
     * Set appraiser key and trial key
     *
     * @param appraiserKey appraiser key
     * @param trialKey     trial key
     */
    public void setAppraiserAndTrialKey(String appraiserKey, String trialKey) {
        this.appraiserKey = appraiserKey;
        this.trialKey = trialKey;
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (dataFrame == null || itemResultDto == null || grrViewDataDtos == null) {
            return null;
        }
        String appraiser = rowKey.split(UIConstant.SPLIT_FLAG)[0];
        String trial = rowKey.split(UIConstant.SPLIT_FLAG)[1];
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>(appraiser);
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>(trial);
        } else {
            String viewDataRowKey = DataConvertUtils.findRowKeyFromViewData(grrViewDataDtos, appraiser, trial, columnName);
            return new SimpleObjectProperty(dataFrame.getDataRow(viewDataRowKey).getData().get(currentItemName));
        }
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

    public void setHeaderArray(ObservableList<String> headerArray) {
        this.headerArray = headerArray;
    }

    public void setRowKeyArray(ObservableList<String> rowKeyArray) {
        this.rowKeyArray = rowKeyArray;
    }
}

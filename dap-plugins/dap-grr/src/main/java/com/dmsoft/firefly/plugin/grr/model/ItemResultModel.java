package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.GrrItemResultDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.utils.DataConvertUtils;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
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
    private String currentItemName;
    private String trialKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private String appraiserKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
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

    @Override
    public ObservableList<String> getHeaderArray() {
        return headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (dataFrame == null || itemResultDto == null || grrViewDataDtos == null) {
            return null;
        }
        if (rowKey.contains(UIConstant.SPLIT_FLAG + "mean")) {
            return this.getMeanCellData(rowKey, columnName);
        } else if (rowKey.contains(UIConstant.SPLIT_FLAG + "range")) {
            return this.getRangeCellData(rowKey, columnName);
        } else if (rowKey.equals("total mean")) {
            return this.getTotalMeanCellData(columnName);
        } else if (rowKey.equals("total range")) {
            return this.getTotalRangeCellData(columnName);
        } else if (rowKey.contains(UIConstant.SPLIT_FLAG)) {
            return this.getItemCellData(rowKey, columnName);
        }
        return null;
    }

    private ObjectProperty<String> getItemCellData(String rowKey, String columnName) {
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

    private ObjectProperty<String> getTotalMeanCellData(String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>("total mean");
        } else {
            return new SimpleObjectProperty<>(itemResultDto.getTotalMeans().get(columnName));
        }
    }

    private ObjectProperty<String> getMeanCellData(String rowKey, String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>("mean");
        } else {
            return new SimpleObjectProperty<>(itemResultDto.getMeanAndRangeDtos().
                    get(rowKey.split(UIConstant.SPLIT_FLAG)[0]).getMeans().get(columnName));
        }
    }

    private ObjectProperty<String> getTotalRangeCellData(String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>("total range");
        } else {
            return new SimpleObjectProperty<>(itemResultDto.getTotalRanges().get(columnName));
        }
    }

    private ObjectProperty<String> getRangeCellData(String rowKey, String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>("range");
        } else {
            return new SimpleObjectProperty<>(itemResultDto.getMeanAndRangeDtos().
                    get(rowKey.split(UIConstant.SPLIT_FLAG)[0]).getRanges().get(columnName));
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

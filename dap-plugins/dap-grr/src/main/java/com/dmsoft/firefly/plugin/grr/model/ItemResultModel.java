package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.GrrItemResultDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.utils.DataConvertUtils;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
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
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private int digNum = -1;

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
        digNum = DigNumInstance.newInstance().getDigNum();
        if (rowKey.contains(UIConstant.SPLIT_FLAG + UIConstant.MEAN)) {
            return this.getMeanCellData(rowKey, columnName);
        } else if (rowKey.contains(UIConstant.SPLIT_FLAG + UIConstant.RANGE)) {
            return this.getRangeCellData(rowKey, columnName);
        } else if (rowKey.equals(UIConstant.TOTAL_MEAN)) {
            return this.getTotalMeanCellData(columnName);
        } else if (rowKey.equals(UIConstant.TOTAL_RANGE)) {
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
            return buildCellData(dataFrame.getDataRow(viewDataRowKey).getData().get(currentItemName));
        }
    }

    private ObjectProperty<String> getTotalMeanCellData(String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>(totalMeanKey);
        } else {
            return buildCellData(itemResultDto.getTotalMeans().get(columnName));
        }
    }

    private ObjectProperty<String> getMeanCellData(String rowKey, String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>(meanKey);
        } else {
            return buildCellData(itemResultDto.getMeanAndRangeDtos().
                    get(rowKey.split(UIConstant.SPLIT_FLAG)[0]).getMeans().get(columnName));
        }
    }

    private ObjectProperty<String> getTotalRangeCellData(String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>(totalRangeKey);
        } else {
            return buildCellData(itemResultDto.getTotalRanges().get(columnName));
        }
    }

    private ObjectProperty<String> getRangeCellData(String rowKey, String columnName) {
        if (columnName.equals(appraiserKey)) {
            return new SimpleObjectProperty<>("");
        } else if (columnName.equals(trialKey)) {
            return new SimpleObjectProperty<>(rangeKey);
        } else {
            return buildCellData(itemResultDto.getMeanAndRangeDtos().
                    get(rowKey.split(UIConstant.SPLIT_FLAG)[0]).getRanges().get(columnName));
        }
    }

    private SimpleObjectProperty buildCellData(String value) {
        if (DAPStringUtils.isBlankWithSpecialNumber(value)) {
            return new SimpleObjectProperty("-");
        } else if (DAPStringUtils.isNumeric(value)) {
            return new SimpleObjectProperty(DAPStringUtils.formatDouble(Double.valueOf(value), digNum));
        }
        return new SimpleObjectProperty(value);
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

    private String trialKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private String appraiserKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
    private String meanKey = GrrFxmlAndLanguageUtils.getString("GRR_ITEM_MEAN");
    private String rangeKey = GrrFxmlAndLanguageUtils.getString("GRR_ITEM_RANGE");
    private String totalMeanKey = GrrFxmlAndLanguageUtils.getString("GRR_ITEM_TOTAL_MEAN");
    private String totalRangeKey = GrrFxmlAndLanguageUtils.getString("GRR_ITEM_TOTAL_RANGE");
}

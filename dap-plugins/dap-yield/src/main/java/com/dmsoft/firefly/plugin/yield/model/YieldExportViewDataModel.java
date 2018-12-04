package com.dmsoft.firefly.plugin.yield.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

public class YieldExportViewDataModel implements TableModel {
    private SearchDataFrame dataFrame;
    private List<String> searchConditions;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, TestItemWithTypeDto> testItemDtoMap;

    @Autowired
    private EnvService envService;

    /**
     * constructor
     *
     * @param dataFrame        search data frame
     * @param searchConditions list of search conditions
     */
    public YieldExportViewDataModel(SearchDataFrame dataFrame, List<String> searchConditions) {
        this.dataFrame = dataFrame;
        if (searchConditions != null) {
            this.searchConditions = Lists.newArrayList(searchConditions);
        } else {
            this.searchConditions = Lists.newArrayList();
        }
        this.headerArray = FXCollections.observableArrayList(dataFrame.getAllTestItemName());
        this.rowKeyArray = FXCollections.observableArrayList();
        if (this.searchConditions.isEmpty()) {
            this.searchConditions.add("");
        }
        for (String s : this.searchConditions) {
            List<String> searchedRowKeys = dataFrame.getSearchRowKey(s);
            if (DAPStringUtils.isBlank(s)) {
                this.rowKeyArray.add("All");
            } else {
                this.rowKeyArray.add(s);
            }
            this.rowKeyArray.addAll(searchedRowKeys);
        }
        testItemDtoMap = Maps.newHashMap(this.envService.findTestItemsMap());
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return this.headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (dataFrame.isRowKeyExist(rowKey)) {
            return new SimpleObjectProperty<>(dataFrame.getCellValue(rowKey, columnName));
        } else {
            if (headerArray.get(0).equals(columnName)) {
                return new SimpleObjectProperty<>(rowKey);
            } else {
                return new SimpleObjectProperty<>("");
            }
        }
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return this.rowKeyArray;
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
        tableCell.setStyle(null);
        if (!dataFrame.isRowKeyExist(rowKey)) {
            if (tableCell.getIndex() == 0) {
                tableCell.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #DCDCDC; -fx-border-width: 0 0 1 0");
            } else {
                tableCell.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #DCDCDC; -fx-border-width: 1 0 1 0");
            }
        } else if (testItemDtoMap != null && !RangeUtils.isPass(dataFrame.getCellValue(rowKey, column), testItemDtoMap.get(column))) {
            tableCell.setStyle("-fx-background-color: #ea2028; -fx-text-fill: white");
        } else if ((dataFrame.getCellValue(rowKey, column) != null && !DAPStringUtils.isNumeric(dataFrame.getCellValue(rowKey, column))) || (testItemDtoMap != null && !testItemDtoMap.containsKey(column))) {
            tableCell.setStyle("-fx-text-fill: #aaaaaa");
        }
        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableViewWidth(TableView<String> tableView) {

    }

    @Override
    public boolean isMenuEventEnable(String rowKey) {
        return false;
    }

    @Override
    public boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        return false;
    }

    public void setTableView(TableView<String> tableView) {

    }
}

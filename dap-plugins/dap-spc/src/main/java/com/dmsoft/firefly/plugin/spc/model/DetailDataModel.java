package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.NewTableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.plugin.spc.utils.RangeUtils;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
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
 * detail data model
 *
 * @author Can Guan
 */
public class DetailDataModel implements NewTableModel {
    private RowDataDto rowDataDto;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, ObjectProperty<String>> testItemMap;
    private Map<String, ObjectProperty<String>> valueMap;
    private String testItems;
    private String values;
    private TableView<String> tableView;
    private Map<String, TestItemWithTypeDto> testItemDtoMap;

    /**
     * constructor
     *
     * @param rowDataDto  row data dto
     * @param typeDtoList list of test item with type dto
     */
    public DetailDataModel(RowDataDto rowDataDto, List<TestItemWithTypeDto> typeDtoList) {
        this.rowDataDto = rowDataDto;
        this.testItems = SpcFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEMS);
        this.values = SpcFxmlAndLanguageUtils.getString(ResourceMassages.VALUES);
        this.testItemMap = Maps.newHashMap();
        this.valueMap = Maps.newHashMap();
        this.testItemDtoMap = Maps.newHashMap();
        for (TestItemWithTypeDto testItemWithTypeDto : typeDtoList) {
            testItemDtoMap.put(testItemWithTypeDto.getTestItemName(), testItemWithTypeDto);
        }
        this.headerArray = FXCollections.observableArrayList(testItems, values);
        this.rowKeyArray = FXCollections.observableArrayList(rowDataDto.getData().keySet());
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return this.headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (testItems.equals(columnName)) {
            if (!testItemMap.containsKey(rowKey)) {
                testItemMap.put(rowKey, new SimpleObjectProperty<>(rowKey));
            }
            return testItemMap.get(rowKey);
        } else if (values.equals(columnName)) {
            if (!valueMap.containsKey(rowKey)) {
                valueMap.put(rowKey, new SimpleObjectProperty<>(rowDataDto.getData().get(rowKey)));
            }
        }
        return null;
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
        if (values.equals(column)) {
            if (!RangeUtils.isPass(valueMap.get(rowKey).get(), testItemDtoMap.get(rowKey))) {
                tableCell.setStyle("-fx-background-color: red; -fx-text-fill: white");
            }
        }
        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }
}

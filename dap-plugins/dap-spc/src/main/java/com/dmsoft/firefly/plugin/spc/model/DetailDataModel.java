package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
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

import java.util.List;
import java.util.Map;

/**
 * detail data model
 *
 * @author Can Guan
 */
public class DetailDataModel implements TableModel {
    private RowDataDto rowDataDto;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, ObjectProperty<String>> testItemMap;
    private Map<String, ObjectProperty<String>> valueMap;
    private List<String> originalRowKeys;
    private String testItems;
    private String values;
    private TableView<String> tableView;
    private Map<String, TestItemWithTypeDto> testItemDtoMap;

    /**
     * constructor
     *
     * @param rowDataDto     row data dto
     * @param testItemDtoMap list of test item with type dto
     */
    public DetailDataModel(RowDataDto rowDataDto, Map<String, TestItemWithTypeDto> testItemDtoMap) {
        this.rowDataDto = rowDataDto;
        this.testItems = SpcFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEM);
        this.values = SpcFxmlAndLanguageUtils.getString(ResourceMassages.VALUES);
        this.testItemMap = Maps.newHashMap();
        this.valueMap = Maps.newHashMap();
        this.testItemDtoMap = Maps.newHashMap(testItemDtoMap);
        this.originalRowKeys = Lists.newArrayList(rowDataDto.getData().keySet());

        this.headerArray = FXCollections.observableArrayList(testItems, values);
        this.rowKeyArray = FXCollections.observableArrayList(rowDataDto.getData().keySet());
        Map<String, TestItemWithTypeDto> itemMaps = RuntimeContext.getBean(EnvService.class).findTestItemsMap();
        for (int i = 0; i < this.rowKeyArray.size(); i++) {
            String s = this.rowKeyArray.get(i);
            if (!this.testItemDtoMap.containsKey(s)) {
                this.testItemDtoMap.put(s, itemMaps.get(s));
            }
        }
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
            return valueMap.get(rowKey);
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
            } else if ((valueMap.get(rowKey).get() != null && !DAPStringUtils.isNumeric(valueMap.get(rowKey).get())) || (testItemDtoMap != null && !testItemDtoMap.containsKey(rowKey))) {
                tableCell.setStyle("-fx-text-fill: #aaaaaa");
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

    /**
     * method to filter text
     *
     * @param s string
     */
    public void filterText(String s) {
        this.rowKeyArray.clear();
        for (String rowKey : originalRowKeys) {
            if (rowKey.toLowerCase().contains(s.toLowerCase()) || rowDataDto.getData().get(rowKey).toLowerCase().contains(s.toLowerCase())) {
                this.rowKeyArray.add(rowKey);
            }
        }
    }
}

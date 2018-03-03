package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.NewTableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.plugin.spc.utils.RangeUtils;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * view data model with data frame
 *
 * @author Can Guan
 */
public class ViewDataDFModel implements NewTableModel {
    private SearchDataFrame dataFrame;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, ObjectProperty<Boolean>> checkValueMap;
    private SimpleObjectProperty<Boolean> allCheck;
    private List<TableMenuRowEvent> menuRowEvents;
    private Set<String> highLightRowKeys;
    private CheckBox allCheckBox;
    private TableView<String> tableView;

    /**
     * constructor
     *
     * @param dataFrame search data frame
     */
    public ViewDataDFModel(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.headerArray = FXCollections.observableArrayList(dataFrame.getAllTestItemName());
        this.headerArray.add(0, "CheckBox");
        this.rowKeyArray = FXCollections.observableArrayList(dataFrame.getAllRowKeys());
        this.checkValueMap = Maps.newHashMap();
        this.allCheck = new SimpleObjectProperty<>(true);
        this.highLightRowKeys = Sets.newLinkedHashSet();
        this.menuRowEvents = Lists.newArrayList();
        TableMenuRowEvent highLight = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return "High Light";
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {
                if (highLightRowKeys.contains(rowKey)) {
                    highLightRowKeys.remove(rowKey);
                } else {
                    highLightRowKeys.add(rowKey);
                }
                tableView.refresh();
            }
        };
        this.menuRowEvents.add(highLight);
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        return new SimpleObjectProperty<>(dataFrame.getCellValue(rowKey, columnName));
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
        return "CheckBox".equals(columnName);
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (this.checkValueMap.get(rowKey) == null) {
            this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(true));
        }
        return this.checkValueMap.get(rowKey);
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allCheck;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return this.menuRowEvents;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        tableCell.setStyle(null);
        if (!RangeUtils.isPass(dataFrame.getCellValue(rowKey, column), dataFrame.getTestItemWithTypeDto(column))) {
            tableCell.setStyle("-fx-background-color: red; -fx-text-fill: white");
        } else if (dataFrame.getCellValue(rowKey, column) != null && !DAPStringUtils.isNumeric(dataFrame.getCellValue(rowKey, column)) && this.highLightRowKeys.contains(rowKey)) {
            tableCell.setStyle("-fx-background-color: yellow; -fx-text-fill: #aaaaaa");
        } else if (this.highLightRowKeys.contains(rowKey)) {
            tableCell.setStyle("-fx-background-color: yellow");
        } else if (dataFrame.getCellValue(rowKey, column) != null && !DAPStringUtils.isNumeric(dataFrame.getCellValue(rowKey, column))) {
            tableCell.setStyle("-fx-text-fill: #aaaaaa");
        }
        return tableCell;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }
}


/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDataset;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.*;
import org.apache.commons.lang.StringUtils;

/**
 *
 *
 */
public class ItemDataTableModel implements TableModel {
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private List<RowDataDto> rowDataDtoList = new ArrayList<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private List<String> trueSet = new ArrayList<>();
    private List<String> falseSet = new ArrayList<>();
    private CheckBox allCheckBox = new CheckBox();
    private SearchDataFrame searchDataFrame;


    public ItemDataTableModel(SearchDataFrame searchDataFrame){
        this.searchDataFrame = searchDataFrame;
        columnKey.clear();

        //TODO yuanwen 2018-10-27 控制表格只显示10以内的数据，原来逻辑没有做调整和验证
        if(this.searchDataFrame.getAllTestItemName().size() > 10){
            for (int i = 0; i < 10; i++) {
                columnKey.add(this.searchDataFrame.getAllTestItemName().get(i));
            }
        }else {
            columnKey.addAll(this.searchDataFrame.getAllTestItemName());
        }


        this.rowKey.addAll(this.searchDataFrame.getAllRowKeys());
    }

    /**
     * constructor.
     *
     * @param rowDataDtos list of row data dto
     * @param dataFrame   dataFrame
     */
    public ItemDataTableModel(SearchDataFrame dataFrame, List<RowDataDto> rowDataDtos) {
        rowKey.clear();
        columnKey.clear();

        List<String> headers = new LinkedList<>();
        if (dataFrame != null) {
            headers = dataFrame.getAllTestItemName();
        }
        if (headers != null && !headers.isEmpty()) {
            columnKey.add(0, "");
            if (headers.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    columnKey.add(headers.get(i));
                }
            } else {
                columnKey.addAll(headers);
            }
        }


        List<RowDataDto> rowDataDtoListContext = dataFrame == null ? Lists.newArrayList() : dataFrame.getAllDataRow();
        rowDataDtos.addAll(rowDataDtoListContext);

        int i = 0;
        int k = 3;
        if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
            for (RowDataDto rowDataDto : rowDataDtos) {
                rowKey.add(String.valueOf(i));
                checkMap.put(String.valueOf(i), new SimpleObjectProperty<>(rowDataDto.getInUsed()));
                if (i > 2 && (rowDataDto.getInUsed() == null || rowDataDto.getInUsed())) {
                    k++;
                } else {
                    falseSet.add(String.valueOf(i));
                }
                rowDataDtoList.add(rowDataDto);
                i++;
            }
        }

        if (rowDataDtos != null && !rowDataDtos.isEmpty() && k == rowDataDtos.size() && k != 3) {
            allChecked.setValue(true);
        }

    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if(StringUtils.isEmpty(columnName)){
            return null;
        }

        return new SimpleObjectProperty(this.searchDataFrame.getCellValue(rowKey, columnName));
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return rowKey;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        if(StringUtils.isEmpty(columnName)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        SimpleObjectProperty<Boolean> objectProperty = checkMap.get(rowKey);

        objectProperty.addListener((ov, b1, b2) -> {
            if (!b2) {
                falseSet.add(rowKey);
                allChecked.setValue(false);
                checkMap.put(rowKey, objectProperty);
            } else {
                if (falseSet.contains(rowKey)) {
                    falseSet.remove(rowKey);
                    checkMap.put(rowKey, objectProperty);
                }
                allChecked.setValue(true);
            }
        });

        return checkMap.get(rowKey);
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allChecked;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return null;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
//        Double dataValue = null;
//        Double usl = null;
//        Double lsl = null;

//        if (Integer.parseInt(rowKey) > 2 && falseSet.contains(rowKey)) {
//            tableCell.setStyle("-fx-text-fill: #dcdcdc");
//            return tableCell;
//        }
//
//        if (column.isEmpty() && (Integer.parseInt(rowKey) == 0 || Integer.parseInt(rowKey) == 1 || Integer.parseInt(rowKey) == 2)) {
//            tableCell.setGraphic(null);
//            return tableCell;
//        }
//
        //TODO yuanwen 暂时取消代码
//        if (DAPStringUtils.isNumeric(rowDataDtoList.get(0).getData().get(column))) {
//            usl = Double.valueOf(rowDataDtoList.get(0).getData().get(column));
//        }
//
//        if (DAPStringUtils.isNumeric(rowDataDtoList.get(1).getData().get(column))) {
//            lsl = Double.valueOf(rowDataDtoList.get(1).getData().get(column));
//        }

//        if (Integer.parseInt(rowKey) > 2) {
//            String data = rowDataDtoList.get(Integer.parseInt(rowKey)).getData().get(column);
//            if (DAPStringUtils.isNumeric(data)) {
//                dataValue = Double.valueOf(data);
//            }
//            if (null != usl && null != dataValue && dataValue > usl) {
//                tableCell.setStyle("-fx-background-color:red");
//                return tableCell;
//            }
//            if (null != lsl && null != dataValue && dataValue < lsl) {
//                tableCell.setStyle("-fx-background-color:red");
//                return tableCell;
//            }
//        }
        return null;
    }

    @Override
    public void setTableViewWidth(TableView<String> tableView) {
        if (tableView.getColumns() != null && !tableView.getColumns().isEmpty()) {
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                if (i == 0) {
                    tableView.getColumns().get(0).setPrefWidth(35);
                } else {
                    tableView.getColumns().get(i).setPrefWidth(150);
                    tableView.getColumns().get(i).setMinWidth(150);
                }
            }

        }
    }

    /**
     * get CheckMap
     *
     * @return checkMap
     */
    public Map<String, SimpleObjectProperty<Boolean>> getCheckMap() {
        return checkMap;
    }

    /**
     * get All Check Box
     *
     * @return allCheckBox
     */
    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    /**
     * get Row Key.
     *
     * @return rowKey
     */
    public ObservableList<String> getRowKey() {
        return rowKey;
    }

    /**
     * update TestItem Column.
     *
     * @param result columnKey
     */
    public void updateTestItemColumn(List<String> result) {
        columnKey.clear();
        if (result != null && !result.isEmpty()) {
            columnKey.add("");
            columnKey.addAll(result);
        }
    }

    /**
     * update RowData List.
     *
     * @param rowDataDtos rowDataDtos
     */
    public void updateRowDataList(List<RowDataDto> rowDataDtos) {
        rowDataDtoList.clear();
        rowKey.clear();
        int i = 0;
        if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
            for (RowDataDto rowDataDto : rowDataDtos) {
                rowKey.add(String.valueOf(i));
                i++;
                rowDataDtoList.add(rowDataDto);
            }
        }
    }

    /**
     * get RowDataDtoList.
     *
     * @return rowDataDtoList
     */
    public List<RowDataDto> getRowDataDtoList() {
        return rowDataDtoList;
    }

    /**
     * get FalseSet.
     *
     * @return falseSet
     */
    public List<String> getFalseSet() {
        return falseSet;
    }

    /**
     * get TrueSet.
     *
     * @return trueSet
     */
    public List<String> getTrueSet() {
        return trueSet;
    }
}

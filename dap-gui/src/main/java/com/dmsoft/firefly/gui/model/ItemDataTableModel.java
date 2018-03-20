package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by Alice on 2018/3/14.
 */
public class ItemDataTableModel implements TableModel {
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SimpleObjectProperty<String>> valueMap;
    private List<RowDataDto> rowDataDtoList = new ArrayList<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private List<String> trueSet = new ArrayList<>();
    private List<String> falseSet = new ArrayList<>();
    private CheckBox allCheckBox;

    /**
     * constructor
     */
    public ItemDataTableModel(List<String> headers, List<RowDataDto> rowDataDtos) {
        rowKey.clear();
        columnKey.clear();
        valueMap = Maps.newHashMap();
        if (headers != null && !headers.isEmpty()) {
            columnKey.add(0, "");
            if (headers.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    columnKey.add(headers.get(i));
                }
            } else {
                for (String header : headers) {
                    columnKey.add(header);
                }
            }
        }

        int i = 0;
        if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
            for (RowDataDto rowDataDto : rowDataDtos) {
                // rowKey.add( rowDataDto.getRowKey() );
                rowKey.add(String.valueOf(i));
                i++;
                rowDataDtoList.add(rowDataDto);
            }
        }
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (columnName.equals("")) {
            return null;
        } else {
            valueMap.put(rowKey, new SimpleObjectProperty<String>(rowDataDtoList.get(Integer.parseInt(rowKey)).getData().get(columnName)));
            return valueMap.get(rowKey);
        }
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
        if (columnName.equals("")) {
            return true;
        }
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (checkMap.get(rowKey) == null) {
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(true);
            checkMap.put(rowKey, b);
            trueSet.add(rowKey);
            allChecked.setValue(true);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
//                    if(trueSet.contains(rowKey )){
//                        trueSet.remove(rowKey);
//                    }
                    falseSet.add(rowKey);
                    allChecked.setValue(false);
                } else {
                    if (falseSet.contains(rowKey)) {
                        falseSet.remove(rowKey);
                    }
//                    trueSet.add( rowKey );
                    allChecked.setValue(true);
                }
            });
        }
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
        Double dataValue = null;
        Double usl = null;
        Double lsl = null;

        if (column.isEmpty() && (Integer.parseInt(rowKey) == 0 || Integer.parseInt(rowKey) == 1 || Integer.parseInt(rowKey) == 2)) {
            tableCell.setGraphic(null);
            return tableCell;
        }
        if (DAPStringUtils.isNumeric(rowDataDtoList.get(0).getData().get(column))) {
            usl = Double.valueOf(rowDataDtoList.get(0).getData().get(column));
        }

        if (DAPStringUtils.isNumeric(rowDataDtoList.get(1).getData().get(column))) {
            lsl = Double.valueOf(rowDataDtoList.get(1).getData().get(column));
        }

        if (Integer.parseInt(rowKey) > 2) {
            String data = rowDataDtoList.get(Integer.parseInt(rowKey)).getData().get(column);
            if (DAPStringUtils.isNumeric(data)) {
                dataValue = Double.valueOf(data);
            }
            if (null != usl && null != dataValue && dataValue > usl) {
                tableCell.setStyle("-fx-background-color:red");
                return tableCell;
            }
            if (null != lsl && null != dataValue && dataValue < lsl) {
                tableCell.setStyle("-fx-background-color:red");
                return tableCell;
            }
        }
        return null;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        if (tableView.getColumns() != null && !tableView.getColumns().isEmpty()) {
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                if (i == 0) {
                    tableView.getColumns().get(0).setPrefWidth(35);
                } else {
                    tableView.getColumns().get(i).setPrefWidth(75);
                    tableView.getColumns().get(i).setMinWidth(75);
                }
            }

        }

    }

    public Map<String, SimpleObjectProperty<Boolean>> getCheckMap() {
        return checkMap;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    public ObservableList<String> getRowKey() {
        return rowKey;
    }

    public void updateTestItemColumn(List<String> result) {
        columnKey.clear();
        if (result != null && !result.isEmpty()) {
            columnKey.add("");
            columnKey.addAll(result);
        }
    }

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

    public List<RowDataDto> getRowDataDtoList() {
        return rowDataDtoList;
    }

    public List<String> getFalseSet() {
        return falseSet;
    }

    public List<String> getTrueSet() {
        return trueSet;
    }
}

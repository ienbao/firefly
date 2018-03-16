package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
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
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>( false );
    private Set<String> falseSet = new HashSet<>();
    private CheckBox allCheckBox;

    /**
     * constructor
     */
    public ItemDataTableModel(List<String> headers, List<RowDataDto> rowDataDtos) {
        valueMap = Maps.newHashMap();
        if (headers != null && !headers.isEmpty()) {
            columnKey.add( 0, "" );
            if (headers.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    columnKey.add( headers.get( i ) );
                }
            } else {
                for (String header : headers) {
                    columnKey.add( header );
                }
            }
        }

        if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
            for (RowDataDto rowDataDto : rowDataDtos) {
                rowKey.add( rowDataDto.getRowKey() );
                rowDataDtoList.add( rowDataDto );
            }
        }
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (columnName.equals( "" )) {
            return null;
        } else {
            String row = rowKey.substring( rowKey.indexOf( "_!@#_" ) + 5 );
            valueMap.put( rowKey, new SimpleObjectProperty<String>( rowDataDtoList.get( Integer.parseInt( row ) - 2 ).getData().get( columnName ) ) );
            return valueMap.get( rowKey );
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
        if (columnName.equals( "" )) {
            return true;
        }
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (checkMap.get( rowKey ) == null) {
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>( false );
            checkMap.put( rowKey, b );
            falseSet.add( rowKey );
            allChecked.setValue( false );
            b.addListener( (ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add( rowKey );
                    allChecked.setValue( false );
                } else {
                    falseSet.remove( rowKey );
                    if (falseSet.isEmpty()) {
                        allChecked.setValue( true );
                    }
                }
            } );
        }
        return checkMap.get( rowKey );
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
        String row = rowKey.substring( rowKey.indexOf( "_!@#_" ) + 5 );
        String dataValue = null;
        String usl = null;
        String lsl = null;

        usl = rowDataDtoList.get( 0).getData().get( column );
        lsl = rowDataDtoList.get( 1 ).getData().get( column );

        if (Integer.parseInt( row ) > 2) {
            if (!column.equals( "" )) {
                dataValue = rowDataDtoList.get( Integer.parseInt( row ) - 2 ).getData().get( column );
            }
            if (StringUtils.isNotBlank( dataValue ) && StringUtils.isNotBlank( usl )&& !usl.equals("Upper Limited----------->") && (Double.parseDouble( dataValue ) > Double.parseDouble( usl ))) {
                tableCell.setStyle( "-fx-background-color:red" );
                return tableCell;
            }
            if (StringUtils.isNotBlank( dataValue ) && StringUtils.isNotBlank( lsl )&&!lsl.equals("Lower Limited----------->") && (Double.parseDouble( dataValue ) < Double.parseDouble( lsl ))) {
                tableCell.setStyle( "-fx-background-color:red" );
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
        columnKey.addAll(result);
    }

//    public ObservableList<String> setHeaderArray(ObservableList<String> columnKey) {
//
//        return this.columnKey = columnKey;
//    }
}

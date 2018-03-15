package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.components.table.NewTableModel;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

import java.util.*;

/**
 * Created by Alice on 2018/3/14.
 */
public class ItemDataTableModel implements NewTableModel {
    private TableCheckBox selector = new TableCheckBox();
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SimpleObjectProperty<String>> valueMap;
    private List<RowDataDto> rowDataDtoList = new ArrayList<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
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
        if(columnName.equals("")){
            return null;
        }else{
        String row = rowKey.substring( rowKey.indexOf( "_!@#_" ) + 5 );
        if (!row.equals( "0" )) {
            valueMap.put( rowKey, new SimpleObjectProperty<String>( rowDataDtoList.get( Integer.parseInt( row ) - 2 ).getData().get( columnName ) ) );
        }
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
        if(columnName.equals("")){
           return true;
        }
       return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (checkMap.get(rowKey) == null) {
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
            checkMap.put(rowKey, b);
            falseSet.add(rowKey);
            allChecked.setValue(false);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add(rowKey);
                    allChecked.setValue(false);
                } else {
                    falseSet.remove(rowKey);
                    if (falseSet.isEmpty()) {
                        allChecked.setValue(true);
                    }
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
        //todo check
//        String usl = "2.0";
//        String lsl = "1.0";
//        String row = rowKey.substring( rowKey.indexOf( "_!@#_" ) + 5 );
//        if(!column.equals("")){
//            String dataValue = rowDataDtoList.get( Integer.parseInt( row ) - 5 ).getData().get( column );
//            if (StringUtils.isNotBlank(dataValue)&&(Double.parseDouble( dataValue ) > Double.parseDouble( usl ) ||
//                    Double.parseDouble( dataValue ) < Double.parseDouble( lsl ))) {
//                tableCell.setStyle( "-fx-background-color:red" );
//            }
//        }
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

    public ObservableList<String> getRowKey(){
        return rowKey;
    }
}

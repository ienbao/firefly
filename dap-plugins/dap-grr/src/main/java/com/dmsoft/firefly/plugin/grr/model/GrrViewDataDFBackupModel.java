package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

/**
 * basic class for grr view data back up model
 *
 * @author Can Guan
 */
public class GrrViewDataDFBackupModel implements TableModel, GrrViewDataListener {
    private GrrDataFrameDto grrDataFrameDto;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private FilteredList<String> filterHeaderArray;
    private FilteredList<String> filterRowKeyArray;
    private TableView<String> tableView;
    private String partKey = GrrFxmlAndLanguageUtils.getString("PART") + " ";
    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
    private String trailKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private String radioKey = "   ";
    private Map<String, GrrViewDataDto> grrViewDataDtoMap = Maps.newHashMap();
    private Map<String, RadioButton> grrRadioButton = Maps.newHashMap();
    private ToggleGroup group = new ToggleGroup();
    private boolean isSlot;

    /**
     * constructor
     *
     * @param grrDataFrameDto grr data frame dto
     * @param isSlot          is slot or not
     */
    public GrrViewDataDFBackupModel(GrrDataFrameDto grrDataFrameDto, boolean isSlot) {
        this.grrDataFrameDto = grrDataFrameDto;
        this.isSlot = isSlot;
        this.headerArray = FXCollections.observableArrayList(grrDataFrameDto.getDataFrame().getAllTestItemName());
        this.headerArray.add(0, trailKey);
        this.headerArray.add(0, appKey);
        this.headerArray.add(0, partKey);
        this.headerArray.add(0, radioKey);
        this.filterHeaderArray = this.headerArray.filtered(p -> true);
        if (grrDataFrameDto.getBackupDatas() != null && !grrDataFrameDto.getBackupDatas().isEmpty()) {
            for (GrrViewDataDto grrViewDataDto : grrDataFrameDto.getBackupDatas()) {
                this.rowKeyArray.add(grrViewDataDto.getRowKey());
                this.grrViewDataDtoMap.put(grrViewDataDto.getRowKey(), grrViewDataDto);
                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(group);
                grrRadioButton.put(grrViewDataDto.getRowKey(), radioButton);
            }
            this.filterRowKeyArray = this.rowKeyArray.filtered(p -> true);
        }
        if (grrDataFrameDto.getIncludeDatas() != null && !grrDataFrameDto.getIncludeDatas().isEmpty()) {
            selectChange(grrDataFrameDto.getIncludeDatas().get(0));
        }
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return this.filterHeaderArray;
    }

    public List<String> getAllRowKeys() {
        return this.rowKeyArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (radioKey.equals(columnName)) {
            return null;
        } else if (partKey.equals(columnName)) {
            return new SimpleObjectProperty<>(grrViewDataDtoMap.get(rowKey).getPart());
        } else if (appKey.equals(columnName)) {
            return new SimpleObjectProperty<>(grrViewDataDtoMap.get(rowKey).getOperator());
        } else if (trailKey.equals(columnName)) {
            return new SimpleObjectProperty<>(grrViewDataDtoMap.get(rowKey).getTrial());
        }
        return new SimpleObjectProperty<>(this.grrDataFrameDto.getDataFrame().getCellValue(rowKey, columnName));
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return this.filterRowKeyArray;
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
        if (radioKey.equals(rowKey)) {
            tableCell.setText(null);
            tableCell.setGraphic(grrRadioButton.get(rowKey));
            return tableCell;
        }
        if (!partKey.equals(column) && !appKey.equals(column) && !trailKey.equals(column)) {
            if (this.grrDataFrameDto.getDataFrame().getCellValue(rowKey, column) == null) {
                tableCell.setText("NULL");
                tableCell.setStyle("-fx-background-color: #f8d251");
            } else if (tableCell.getText() != null && !DAPStringUtils.isNumeric(tableCell.getText()) && !DAPStringUtils.isSpecialBlank(tableCell.getText())) {
                tableCell.setStyle("-fx-text-fill: #aaaaaa");
            } else if (tableCell.getText() != null && DAPStringUtils.isNumeric(tableCell.getText())) {
                if (!RangeUtils.isPass(tableCell.getText(), this.grrDataFrameDto.getDataFrame().getTestItemWithTypeDto(column))) {
                    tableCell.setStyle("-fx-background-color: #ea2028; -fx-text-fill: white");
                }
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

    @Override
    public void selectChange(GrrViewDataDto grrViewDataDto) {
        this.filterHeaderArray.setPredicate(s -> grrViewDataDtoMap.get(s).getPart().equals(grrViewDataDto.getPart()) && (!isSlot || grrViewDataDtoMap.get(s).getOperator().equals(grrViewDataDto.getOperator())));
        tableView.refresh();
    }

    /**
     * method to search test item
     *
     * @param testItem test item
     */
    public void searchTestItem(String testItem) {
        this.filterHeaderArray.setPredicate(s -> radioKey.equals(s) || partKey.equals(s) || appKey.equals(s) || trailKey.equals(s) || s.toLowerCase().contains(testItem.toLowerCase()));
        this.tableView.refresh();
    }

    /**
     * method to get selected view data dto
     *
     * @return grr selected view data dto
     */
    public GrrViewDataDto getSelectedViewDataDto() {
        if (this.group.getSelectedToggle() != null) {
            RadioButton rb = (RadioButton) this.group.getSelectedToggle();
            String selectRowKey = getSelectRowKey(rb);
            if (selectRowKey != null) {
                return this.grrViewDataDtoMap.get(selectRowKey);
            }
        }
        return null;
    }


    private String getSelectRowKey(RadioButton radioButton) {
        for (String key : grrRadioButton.keySet()) {
            if (grrRadioButton.get(key).equals(radioButton)) {
                return key;
            }
        }
        return null;
    }
}

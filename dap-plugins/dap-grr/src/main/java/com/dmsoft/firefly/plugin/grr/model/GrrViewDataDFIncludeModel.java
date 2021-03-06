package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.utils.AppConstant;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

/**
 * model class for grr view data
 */
public class GrrViewDataDFIncludeModel implements TableModel {
    private GrrDataFrameDto grrDataFrameDto;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray = FXCollections.observableArrayList();
    private TableView<String> tableView;
    private String partKey = GrrFxmlAndLanguageUtils.getString("PART") + " ";
    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
    private String trailKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private String radioKey = "   ";
    private Map<String, GrrViewDataDto> grrViewDataDtoMap = Maps.newHashMap();
    private Map<String, RadioButton> grrRadioButton = Maps.newHashMap();
    private ToggleGroup group = new ToggleGroup();
    private List<GrrViewDataListener> listeners = Lists.newArrayList();
    private Map<String, TestItemWithTypeDto> typeDtoMap = Maps.newHashMap();
    private SearchConditionDto searchConditionDto;
    private List<String> originItems = Lists.newArrayList();

    /**
     * constructor
     *
     * @param grrDataFrameDto    grr data frame dto
     * @param searchConditionDto search condition dto
     */
    public GrrViewDataDFIncludeModel(GrrDataFrameDto grrDataFrameDto, SearchConditionDto searchConditionDto) {
        this.grrDataFrameDto = grrDataFrameDto;
        this.headerArray = FXCollections.observableArrayList(grrDataFrameDto.getDataFrame().getAllTestItemName());
        this.originItems = Lists.newArrayList(grrDataFrameDto.getDataFrame().getAllTestItemName());
        this.headerArray.add(0, trailKey);
        this.headerArray.add(0, appKey);
        this.headerArray.add(0, partKey);
        this.headerArray.add(0, radioKey);
        this.headerArray.remove(searchConditionDto.getPart());
        this.originItems.remove(searchConditionDto.getPart());
        this.headerArray.remove(searchConditionDto.getAppraiser());
        this.originItems.remove(searchConditionDto.getAppraiser());
        this.searchConditionDto = searchConditionDto;
        if (grrDataFrameDto.getIncludeDatas() != null && !grrDataFrameDto.getIncludeDatas().isEmpty()) {
            for (GrrViewDataDto grrViewDataDto : grrDataFrameDto.getIncludeDatas()) {
                this.rowKeyArray.add(grrViewDataDto.getRowKey());
                this.grrViewDataDtoMap.put(grrViewDataDto.getRowKey(), grrViewDataDto);
                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(group);
                grrRadioButton.put(grrViewDataDto.getRowKey(), radioButton);
            }
            grrRadioButton.get(grrDataFrameDto.getIncludeDatas().get(0).getRowKey()).setSelected(true);
        }
        this.group.selectedToggleProperty().addListener((ov, t1, t2) -> fireToggle((RadioButton) t2));
        for (TestItemWithTypeDto testItemWithTypeDto : searchConditionDto.getSelectedTestItemDtos()) {
            this.typeDtoMap.put(testItemWithTypeDto.getTestItemName(), testItemWithTypeDto);
        }

        if (this.headerArray.size() > AppConstant.MAX_COLUMN + 4) {
            this.headerArray.remove(AppConstant.MAX_COLUMN + 4, this.headerArray.size());
            this.originItems.removeAll(this.originItems.subList(AppConstant.MAX_COLUMN, this.originItems.size()));
        }
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return this.headerArray;
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
        if (radioKey.equals(column)) {
            tableCell.setText(null);
            tableCell.setGraphic(new Label("", grrRadioButton.get(rowKey)));
            return tableCell;
        }
        if (!partKey.equals(column) && !appKey.equals(column) && !trailKey.equals(column)) {
            if (this.grrDataFrameDto.getDataFrame().getCellValue(rowKey, column) == null) {
                tableCell.setText("NULL");
                tableCell.setStyle("-fx-background-color: #f8d251");
            } else if (tableCell.getText() != null && !DAPStringUtils.isNumeric(tableCell.getText()) && !DAPStringUtils.isSpecialBlank(tableCell.getText())) {
                tableCell.setStyle("-fx-text-fill: #aaaaaa");
            } else if (tableCell.getText() != null && DAPStringUtils.isNumeric(tableCell.getText())) {
                if (!RangeUtils.isPass(tableCell.getText(), this.typeDtoMap.get(column))) {
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
    public void setTableViewWidth(TableView<String> tableView) {
        this.tableView = tableView;
    }

    /**
     * method to search test item
     *
     * @param testItem test item
     */
    public void searchTestItem(String testItem) {
        Platform.runLater(() -> {
            this.headerArray.clear();
            int i = 0;
            List<String> addedList = Lists.newArrayList();
            addedList.add(0, trailKey);
            addedList.add(0, appKey);
            addedList.add(0, partKey);
            addedList.add(0, radioKey);
            for (String s : this.originItems) {
                if (s != null && s.toLowerCase().contains(testItem.toLowerCase()) && !s.equals(this.searchConditionDto.getPart()) && !s.equals(this.searchConditionDto.getAppraiser())) {
                    if (i < AppConstant.MAX_COLUMN + 4) {
                        addedList.add(s);
                    }
                    i++;
                }
            }
            this.headerArray.addAll(addedList);
        });
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

    /**
     * method to add listener for grr include data frame
     *
     * @param listener grr view data listener
     */
    public void addListener(GrrViewDataListener listener) {
        this.listeners.add(listener);
    }

    /**
     * method to replace view data dto
     *
     * @param grrViewDataDto grr view data dto to replace
     */
    public void replace(GrrViewDataDto grrViewDataDto) {
        GrrViewDataDto oldDto = getSelectedViewDataDto();
        if (oldDto != null) {
            this.grrViewDataDtoMap.remove(oldDto.getRowKey());
            this.grrViewDataDtoMap.put(grrViewDataDto.getRowKey(), grrViewDataDto);
            RadioButton rb = this.grrRadioButton.remove(oldDto.getRowKey());
            this.grrRadioButton.put(grrViewDataDto.getRowKey(), rb);
            int index = this.rowKeyArray.indexOf(oldDto.getRowKey());
            this.rowKeyArray.remove(index);
            this.rowKeyArray.add(index, grrViewDataDto.getRowKey());
        }
    }

    /**
     * method to reset headers
     *
     * @param chooseItems list of selected items
     */
    public void resetHeaders(List<String> chooseItems) {
        this.headerArray.removeAll(this.headerArray.subList(4, this.headerArray.size()));
        this.headerArray.addAll(chooseItems);
        this.originItems.clear();
        this.originItems.addAll(chooseItems);
    }

    private void fireToggle(RadioButton radioButton) {
        String selectRowKey = getSelectRowKey(radioButton);
        if (selectRowKey != null) {
            GrrViewDataDto viewDataDto = this.grrViewDataDtoMap.get(selectRowKey);
            this.listeners.forEach(grrViewDataListener -> grrViewDataListener.selectChange(viewDataDto));
        }
    }

    private String getSelectRowKey(RadioButton radioButton) {
        for (String key : grrRadioButton.keySet()) {
            if (grrRadioButton.get(key).equals(radioButton)) {
                return key;
            }
        }
        return null;
    }

    public List<String> getOriginItems() {
        return originItems;
    }

    public Map<String, GrrViewDataDto> getGrrViewDataDtoMap() {
        return grrViewDataDtoMap;
    }
}

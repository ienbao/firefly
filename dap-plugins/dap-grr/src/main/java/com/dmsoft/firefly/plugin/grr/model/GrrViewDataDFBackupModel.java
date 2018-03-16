package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
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
    private FilteredList<String> filterRowKeyArray;
    private TableView<String> tableView;
    private String partKey = GrrFxmlAndLanguageUtils.getString("PART") + " ";
    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
    private String trailKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private String radioKey = "   ";
    private Map<String, GrrViewDataDto> grrViewDataDtoMap = Maps.newHashMap();
    private Map<String, RadioButton> grrRadioButton = Maps.newHashMap();
    private ToggleGroup group = new ToggleGroup();

    /**
     * constructor
     *
     * @param grrDataFrameDto grr data frame dto
     */
    public GrrViewDataDFBackupModel(GrrDataFrameDto grrDataFrameDto) {
        this.grrDataFrameDto = grrDataFrameDto;
        if (grrDataFrameDto != null && grrDataFrameDto.getDataFrame() != null) {
            this.headerArray = FXCollections.observableArrayList(grrDataFrameDto.getDataFrame().getAllTestItemName());
            this.headerArray.add(0, trailKey);
            this.headerArray.add(0, appKey);
            this.headerArray.add(0, partKey);
            this.headerArray.add(0, radioKey);
            if (grrDataFrameDto.getIncludeDatas() != null && !grrDataFrameDto.getIncludeDatas().isEmpty()) {
                for (GrrViewDataDto grrViewDataDto : grrDataFrameDto.getIncludeDatas()) {
                    this.rowKeyArray.add(grrViewDataDto.getRowKey());
                    this.grrViewDataDtoMap.put(grrViewDataDto.getRowKey(), grrViewDataDto);
                    RadioButton radioButton = new RadioButton();
                    radioButton.setToggleGroup(group);
                    grrRadioButton.put(grrViewDataDto.getRowKey(), radioButton);
                }
                grrRadioButton.get(grrDataFrameDto.getIncludeDatas().get(0).getRowKey()).setSelected(true);
                this.filterRowKeyArray = this.rowKeyArray.filtered(p -> true);
            }
        }
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return null;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        return null;
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return null;
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
        return null;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    @Override
    public void selectChange(GrrViewDataDto grrViewDataDto) {
    }
}

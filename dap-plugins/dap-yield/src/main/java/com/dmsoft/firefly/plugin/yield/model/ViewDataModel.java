package com.dmsoft.firefly.plugin.yield.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.yield.controller.YieldMainController;
import com.dmsoft.firefly.plugin.yield.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewDataModel {

    private static Logger logger = LoggerFactory.getLogger(ViewDataModel.class);
    private SearchDataFrame dataFrame;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, ObjectProperty<Boolean>> checkValueMap;
    private SimpleObjectProperty<Boolean> allCheck;
    private CheckBox allCheckBox;
    private TableView<String> tableView;
    private YieldMainController mainController;
    private List<String> initSelectedRowKeys;
    private String initSelectedColumnKeys;
    private List<SearchConditionDto> statisticalSearchConditionDtoList;
    private Map<String, TestItemWithTypeDto> testItemDtoMap;
    private Map<String, ReadOnlyStringProperty> cellMap;
    private boolean isTimer = false;

    /**
     * constructor
     *
     * @param dataFrame       search data frame
     * @param selectedRowKeys selected row keys
     */
    public ViewDataModel(SearchDataFrame dataFrame, String selectedColumnKeys, List<String>  selectedRowKeys) {
        this.dataFrame = dataFrame;
        this.initSelectedRowKeys = selectedRowKeys;
        this.initSelectedColumnKeys = selectedColumnKeys;
        this.headerArray = FXCollections.observableArrayList(dataFrame.getAllTestItemName());
        this.headerArray.add(0, " ");
        this.rowKeyArray = FXCollections.observableArrayList(dataFrame.getAllRowKeys());
        this.checkValueMap = Maps.newLinkedHashMap();
        for (String rowKey : rowKeyArray) {
            if (this.initSelectedRowKeys != null && this.initSelectedRowKeys.contains(rowKey)) {
                this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(true));
            } else {
                this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(false));
            }
        }
        this.allCheck = new SimpleObjectProperty<>(true);
        this.cellMap = Maps.newHashMap();
    }


    public ObservableList<String> getHeaderArray() {  //获取表头
        return headerArray;
    }


    public ReadOnlyStringProperty getCellData(String rowKey, String columnName) { //获取表格中一个单元格
        if (!this.cellMap.containsKey(rowKey + " !@# " + columnName)) {
            this.cellMap.put(rowKey + " !@# " + columnName, new ReadOnlyStringWrapper(dataFrame.getCellValue(rowKey, columnName)).getReadOnlyProperty());
        }
        return this.cellMap.get(rowKey + " !@# " + columnName);
    }


    public ObservableList<String> getRowKeyArray() { //获取行key
        return rowKeyArray;
    }

    /**
     * method to get selected row keys
     *
     * @return list of selected row key
     */
    public List<String> getSelectedRowKeys() {
        List<String> result = Lists.newArrayList(dataFrame.getAllRowKeys());
        for (String s : this.checkValueMap.keySet()) {
            if (!this.checkValueMap.get(s).get()) {
                result.remove(s);
            }
        }
        return result;
    }


    public boolean isEditableTextField(String columnName) {
        return false;
    }


    public boolean isCheckBox(String columnName) {
        if (isTimer) {
            return false;
        }
        return " ".equals(columnName);
    }


    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (this.checkValueMap.get(rowKey) == null) {
            if (this.initSelectedRowKeys != null && this.initSelectedRowKeys.contains(rowKey)) {
                this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(true));
            } else {
                this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(false));
            }
        }
        return this.checkValueMap.get(rowKey);
    }


    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allCheck;
    }


    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }


    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }


    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }

    public void setMainController(YieldMainController mainController) {
        this.mainController = mainController;
    }
}

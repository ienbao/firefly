package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.plugin.spc.model.ViewDataDFModel;
import com.dmsoft.firefly.plugin.spc.utils.StringUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by GuangLi on 2018/3/12.
 */
public class ExportViewData {
    @FXML
    private TableView viewData;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    private ObservableList<String> itemData = FXCollections.observableArrayList();
    private SearchDataFrame dataFrame;
    private String csvName;
//    private ViewDataDFModel model;

    @FXML
    private void initialize() {
        csvName = RuntimeContext.getBean(EnvService.class).findActivatedProjectName().get(0).toString();
        initData();
        initEvent();
    }

    private void initData() {
        itemData.clear();
        viewData.getColumns().clear();
        List<String> searchs = dataFrame.getSearchConditionList();
        List<String> itemNames = dataFrame.getAllTestItemName();
        if (dataFrame != null) {
            for (String search : searchs) {
                List<String> rowDataDtos = dataFrame.getSearchRowKey(search);
                if (search.equals("")) {
                    search = "All";
                }
                itemData.add(search);
                itemData.addAll(rowDataDtos);
            }

            String first = itemNames.get(0);
            for (String itemName : itemNames) {
                TableColumn<String, String> column = new TableColumn<>(itemName);

                column.setCellValueFactory(cell -> {
                    String cellValue = dataFrame.getCellValue(cell.getValue(), itemName);
                    if (cellValue == null && itemName.equals(first)) {
                        return new SimpleStringProperty(cell.getValue());
                    }
                    return new SimpleStringProperty(cellValue);
                });
                column.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
                    public TableCell call(TableColumn<String, String> param) {
                        return new TableCell<String, String>() {

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (!isEmpty() && !itemData.get(getIndex()).contains(csvName)) {
                                    System.out.println(getIndex() + itemData.get(getIndex()));
                                    this.setStyle("-fx-background-color: #f8f8f8");
                                    // Get fancy and change color based on data
                                }
                                setText(item);
                            }
                        };
                    }
                });
                viewData.getColumns().add(column);
            }
        }
        viewData.setItems(itemData);
        viewData.refresh();
    }

    private void initEvent() {
        ok.setOnAction(event -> {
            StageMap.closeStage("spcExportViewData");
        });
        cancel.setOnAction(event -> StageMap.closeStage("spcExportViewData"));
    }

    public void setDataFrame(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }
}

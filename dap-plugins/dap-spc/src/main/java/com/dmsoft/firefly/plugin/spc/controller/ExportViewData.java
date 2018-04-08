package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.plugin.spc.model.SpcExportViewDataModel;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.util.List;


/**
 * Created by GuangLi on 2018/3/12.
 */
public class ExportViewData {
    @FXML
    private TableView<String> viewData;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    private SearchDataFrame dataFrame;
    private List<String> searchConditions = Lists.newArrayList();

    @FXML
    private void initialize() {
        viewData.setSkin(new ExpandableTableViewSkin(viewData));
        initData();
        initEvent();
    }

    private void initData() {
        SpcExportViewDataModel model = new SpcExportViewDataModel(dataFrame, searchConditions);
        TableViewWrapper.decorate(viewData, model);
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

    public void setSearchConditions(List<String> searchConditions) {
        this.searchConditions = searchConditions;
    }
}

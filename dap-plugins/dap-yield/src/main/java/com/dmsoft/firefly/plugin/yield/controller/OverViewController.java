package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.model.OverViewTableModel;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.UIConstant;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.yield.utils.YieldRefreshJudgeUtil;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class OverViewController implements Initializable {

    @FXML
    private TextFieldFilter filterTestItemTf;
    @FXML
    private TableView overViewResultTb;

    private OverViewTableModel overViewTableModel;
    private List<String> selectOverViewResultName = Lists.newArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTestItemTf.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
        this.initStatisticalResultTable();
        this.initComponentEvent();
    }

    private void initStatisticalResultTable() {
        overViewTableModel = new OverViewTableModel();
//        this.initTableMenuEvent();
        TableViewWrapper.decorate(overViewResultTb, overViewTableModel);

//        List<String> preDisplayResult = this.getSpcStatisticalPreference();
        List<String> displayResult = Arrays.asList(UIConstant.YIELD_CHOOSE_RESULT);
        selectOverViewResultName.addAll(displayResult);

        overViewTableModel.initColumn(selectOverViewResultName);
    }

    private void initComponentEvent() {
        filterTestItemTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());

//        statisticalTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());

        overViewResultTb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<String> statisticalSelectRowKeyListCache = YieldRefreshJudgeUtil.newInstance().getOverViewSelectRowKeyListCache();
            if(statisticalSelectRowKeyListCache == null || !statisticalSelectRowKeyListCache.contains(newValue)){
                return;
            }
//            overViewTableModel.stickChartLayer((String)newValue);
        });
    }

    private void getFilterTestItemTfEvent() {
//        overViewTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }

    private void initTableMenuEvent() {
//        TableMenuRowEvent selectColor = new ChooseColorMenuEvent();
//        statisticalTableModel.addTableMenuEvent(selectColor);
    }

    /* 表格点击事件 */

    /**
     * set statistical result table data
     *
     * @param list the data list
     * @param isTimer isTimer
     * @param selectRowKey selectRowKey
     */
    public void setTimerOverviewResultTableData(List<YieldOverviewResultAlarmDto> list, List<String> selectRowKey, boolean isTimer) {
//        List<String> columnList = statisticalTableModel.getColumnList();
//        statisticalTableModel = new StatisticalTableModel();
//        statisticalTableModel.setTimer(isTimer);
//        statisticalTableModel.initColumn(columnList);
//        this.initTableMenuEvent();
//        TableViewWrapper.decorate(statisticalResultTb, statisticalTableModel);
//
//        statisticalTableModel.initData(list);
//        statisticalTableModel.setSelect(selectRowKey);
//
//        statisticalTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());
        overViewTableModel.initData(list);
//        overViewTableModel.setSelect(selectRowKey);
//        overViewTableModel.setTimer(isTimer);
        overViewTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }


}

/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import com.dmsoft.firefly.sdk.utils.ColorUtils;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class StatisticalResultController implements Initializable {
    @FXML
    private Button chooseColumnBtn;
    @FXML
    private TextFieldFilter filterTestItemTf;
    @FXML
    private TableView statisticalResultTb;

    private SpcMainController spcMainController;

    private ChooseDialogController chooseDialogController;

    private StatisticalTableModel statisticalTableModel;

    private List<String> selectStatisticalResultName = Lists.newArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTestItemTf.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
        this.buildChooseColumnDialog();
        this.initStatisticalResultTable();
        this.initBtnIcon();
        this.initComponentEvent();
    }

    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    /**
     * set statistical result table data
     *
     * @param list the data list
     */
    public void setStatisticalResultTableData(List<SpcStatisticalResultAlarmDto> list) {
        statisticalTableModel.initData(list);
    }

    /**
     * clear statistical result data
     */
    public void clearStatisticalResultData() {
        statisticalTableModel.clearTableData();
    }

    /**
     * get select stats data
     *
     * @return the list of SpcStatsDto
     */
    public List<SpcStatisticalResultAlarmDto> getSelectStatsData() {
        return statisticalTableModel.getSelectData();
    }

    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            chooseDialogController.setValueColumnText("Statistical Result");
            this.initChooseStatisticalResultTableData();
            WindowFactory.createSimpleWindowAsModel("spcStatisticalResult", "Choose Statistical Results", root,
                    getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get Cache color
     *
     * @return color Cache
     */
    public Map<String, Color> getColorCache() {
        return statisticalTableModel.getColorCache();
    }

    private void initChooseStatisticalResultTableData() {
        List<String> value = asList(UIConstant.SPC_CHOOSE_RESULT);
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        value.forEach(v -> {
            ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, v);
            chooseTableRowDataList.add(chooseTableRowData);
        });
        chooseDialogController.setTableData(chooseTableRowDataList);
    }

    private void initStatisticalResultTable() {
        statisticalTableModel = new StatisticalTableModel();
        this.initTableMenuEvent();
        TableViewWrapper.decorate(statisticalResultTb, statisticalTableModel);
        selectStatisticalResultName.addAll(Arrays.asList(UIConstant.SPC_CHOOSE_RESULT));
    }

    private void initComponentEvent() {
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        filterTestItemTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());
        chooseDialogController.getChooseOkButton().setOnAction(event -> getChooseStatisticalResultEvent());
        statisticalTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());
    }

    private void getChooseColumnBtnEvent() {
        chooseDialogController.setSelectResultName(selectStatisticalResultName);
        StageMap.showStage("spcStatisticalResult");
    }

    private void getFilterTestItemTfEvent() {
        statisticalTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }

    private void getChooseStatisticalResultEvent() {
        selectStatisticalResultName = chooseDialogController.getSelectResultName();
        statisticalResultTb.getColumns().remove(3, statisticalResultTb.getColumns().size());
        statisticalTableModel.updateStatisticalResultColumn(selectStatisticalResultName);
        StageMap.closeStage("spcStatisticalResult");
    }

    private void getAllCheckBoxEvent() {
        if (statisticalTableModel.getStatisticalTableRowDataSortedList() != null) {
            Map<String, SimpleObjectProperty<Boolean>> checkMap = statisticalTableModel.getCheckMap();
            for (String key : statisticalTableModel.getStatisticalTableRowDataSortedList()) {
                if (statisticalTableModel.getEmptyResultKeys().contains(key)) {
                    continue;
                }
                if (checkMap.get(key) != null) {
                    checkMap.get(key).set(statisticalTableModel.getAllCheckBox().isSelected());
                } else {
                    checkMap.put(key, new SimpleObjectProperty<>(statisticalTableModel.getAllCheckBox().isSelected()));
                }
            }
        }
    }

    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }

    private void initTableMenuEvent() {
        TableMenuRowEvent selectColor = new ChooseColorMenuEvent();
        statisticalTableModel.addTableMenuEvent(selectColor);
    }

    class ChooseColorMenuEvent implements TableMenuRowEvent {
        private ColorPicker colorPicker;

        @Override
        public String getMenuName() {
            return null;
        }

        @Override
        public void handleAction(String rowKey, ActionEvent event) {
            Color color = ColorUtils.toAwtColorFromFxColor(colorPicker.getValue());
            statisticalTableModel.setRowColor(rowKey, color);
            statisticalResultTb.refresh();
        }

        @Override
        public Node getMenuNode() {
            colorPicker = new ColorPicker(javafx.scene.paint.Color.RED);
            colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);

            colorPicker.getCustomColors().addAll(
                    ColorUtils.toFxColorFromAwtColor(Colur.RAW_VALUES)
            );
            colorPicker.valueProperty().addListener((observable, oldValue, c) -> {

            });
            return colorPicker;
        }
    }

}

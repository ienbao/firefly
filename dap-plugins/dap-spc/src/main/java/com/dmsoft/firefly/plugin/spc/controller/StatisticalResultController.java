/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.colorpicker.ColorPickerMenuSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultAlarmDto;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
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
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
@Component
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
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

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
     * @param isTimer isTimer
     * @param selectRowKey selectRowKey
     */
    public void setTimerStatisticalResultTableData(List<SpcStatisticalResultAlarmDto> list,List<String> selectRowKey, boolean isTimer) {
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
        statisticalTableModel.initData(list);
        statisticalTableModel.setSelect(selectRowKey);
        statisticalTableModel.setTimer(isTimer);
        statisticalTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
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

    /**
     * get edit row data
     *
     * @return the row data
     */
    public List<SpcStatisticalResultAlarmDto> getEditRowStatsData() {
        return statisticalTableModel.getEditRowData();
    }

    /**
     * get edit row key
     *
     * @return row key
     */
    public List<String> getEidtStatisticalRowKey() {
        return statisticalTableModel.getEditorRowKey();
    }

    /**
     * get all stats data
     *
     * @return
     */
    public List<SpcStatisticalResultAlarmDto> getAllRowStatsData() {
        return statisticalTableModel.getSpcStatsDtoList();
    }

    /**
     * refresh spc statistical data
     *
     * @param spcStatsDtoList the refresh data
     */
    public void refreshStatisticalResult(List<SpcStatisticalResultAlarmDto> spcStatsDtoList) {
        statisticalTableModel.refreshData(spcStatsDtoList);
    }

    /**
     * has error edit cell.
     */
    public boolean hasErrorEditCell(){
        return statisticalTableModel.hasErrorEditValue();
    }

    /**
     * get select row key
     *
     * @return row key
     */
    public List<String> getSelectStatisticalRowKey() {
        return statisticalTableModel.getSelectRowKey();
    }

    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            chooseDialogController.setValueColumnText(SpcFxmlAndLanguageUtils.getString("STATISTICAL_RESULT"));
            chooseDialogController.setFilterTFPrompt(SpcFxmlAndLanguageUtils.getString("STATISTICAL_RESULT_PROMPT"));
            this.initChooseStatisticalResultTableData();
            Stage stage = WindowFactory.createNoManagedStage(SpcFxmlAndLanguageUtils.getString("CHOOSE_STATISTICAL_RESULTS"), root,
                    getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            chooseDialogController.setStage(stage);
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

        List<String> preDisplayResult = this.getSpcStatisticalPreference();
        List<String> displayResult = preDisplayResult == null ? Arrays.asList(UIConstant.SPC_CHOOSE_RESULT) : preDisplayResult;
        selectStatisticalResultName.addAll(displayResult);

        statisticalTableModel.initColumn(selectStatisticalResultName);
    }

    private void initComponentEvent() {
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        filterTestItemTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> getFilterTestItemTfEvent());
        chooseDialogController.getChooseOkButton().setOnAction(event -> getChooseStatisticalResultEvent());
        statisticalTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());

        statisticalResultTb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<String> statisticalSelectRowKeyListCache = SpcRefreshJudgeUtil.newInstance().getStatisticalSelectRowKeyListCache();
            if(statisticalSelectRowKeyListCache == null || !statisticalSelectRowKeyListCache.contains(newValue)){
                return;
            }
            spcMainController.stickChartLayer((String)newValue);
        });
    }

    private void getChooseColumnBtnEvent() {
        chooseDialogController.setSelectResultName(selectStatisticalResultName);
        chooseDialogController.getStage().show();
    }

    private void getFilterTestItemTfEvent() {
        statisticalTableModel.filterTestItem(filterTestItemTf.getTextField().getText());
    }

    private void getChooseStatisticalResultEvent() {
        selectStatisticalResultName = chooseDialogController.getSelectResultName();
        statisticalResultTb.getColumns().remove(3, statisticalResultTb.getColumns().size());
        statisticalTableModel.initColumn(selectStatisticalResultName);

        this.updateSpcStatisticalPreference(selectStatisticalResultName);
        chooseDialogController.getStage().close();
    }

    private void getAllCheckBoxEvent() {
        if (statisticalTableModel.getStatisticalTableRowDataSortedList() != null) {
            Map<String, SimpleObjectProperty<Boolean>> checkMap = statisticalTableModel.getCheckMap();
            for (String key : statisticalTableModel.getStatisticalTableRowDataSortedList()) {
                if (checkMap.get(key) != null) {
                    checkMap.get(key).set(statisticalTableModel.getAllCheckBox().isSelected());
                } else {
                    checkMap.put(key, new SimpleObjectProperty<>(statisticalTableModel.getAllCheckBox().isSelected()));
                }
            }
        }
    }

    private void initBtnIcon() {
        TooltipUtil.installNormalTooltip(chooseColumnBtn, SpcFxmlAndLanguageUtils.getString("CHOOSE_STATISTICAL_RESULT"));
    }

    private void initTableMenuEvent() {
        TableMenuRowEvent selectColor = new ChooseColorMenuEvent();
        statisticalTableModel.addTableMenuEvent(selectColor);
    }

    class ChooseColorMenuEvent implements TableMenuRowEvent {
        private ColorPicker colorPicker;

        /**
         * constructor
         */
        public ChooseColorMenuEvent(){
            colorPicker = new ColorPicker();
            colorPicker.getStyleClass().add(ColorPicker.STYLE_CLASS_BUTTON);
            colorPicker.setSkin(new ColorPickerMenuSkin(colorPicker));
            colorPicker.getCustomColors().addAll(
                    ColorUtils.toFxColorFromAwtColor(Colur.RAW_VALUES)
            );
        }

        @Override
        public String getMenuName() {
            return "";
        }

        @Override
        public void handleAction(String rowKey, ActionEvent event) {
            Color color = ColorUtils.toAwtColorFromFxColor(colorPicker.getValue());
            statisticalTableModel.setRowColor(rowKey, color);
            spcMainController.updateChartColor(rowKey, colorPicker.getValue());
            statisticalResultTb.refresh();
        }

        @Override
        public Node getMenuNode() {
            return colorPicker;
        }
    }

    private void updateSpcStatisticalPreference(List<String> displayResultList) {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode("spc_statistical_preference");
        userPreferenceDto.setValue(displayResultList);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private List<String> getSpcStatisticalPreference() {
        String value = userPreferenceService.findPreferenceByUserId("spc_statistical_preference", envService.getUserName());
        if (StringUtils.isNotBlank(value)) {
            return mapper.fromJson(value, List.class);
        } else {
            return null;
        }
    }

}

package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobFactory;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class YieldSettingController implements Initializable {
    @FXML
    private Label alarmSetting;
    @FXML
    private Label controlAlarmRule;
    @FXML
    private Label exportSetting;
    @FXML
    private Button apply;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;


    //alarm setting
    @FXML
    private TextFieldFilter searchTestItemTf;
    @FXML
    private Button addTestItemBtn;
  //  @FXML
  //  private TableView<CustomAlarmTestItemRowData> customAlarmTable;
 //   @FXML
   // private TableColumn<CustomAlarmTestItemRowData, String> testItemColumn;
    @FXML
    private Label testItemNameLabel;
    @FXML
    private TableView statisticalResultAlarmSetTable;

    //control Alarm Rule
    @FXML
    private TableView controlAlarmRuleTable;
  //  private ControlAlarmRuleTableModel controlAlarmRuleTableModel;

    //Export Template Setting
    @FXML
    private ComboBox exportTemplateCb;
    @FXML
    private Button exportTemplateSettingBtn;

    @FXML
    private ScrollPane settingScrollPane;
//    private ObservableList<CustomAlarmTestItemRowData> testItemRowDataObservableList;
//    private FilteredList<CustomAlarmTestItemRowData> testItemRowDataFilteredList;
//    //    private ObservableList<StatisticsResultRuleRowData> statisticsRuleRowDataObservableList;
//    private StatisticsRuleModel statisticsRuleModel;
//
//    private SpcExportSettingController spcExportSettingController;
//    private AddItemController addItemController;
    //Process Capability alarm Setting
    //CA
    @FXML
    private TextField caExcellentTf;
    @FXML
    private TextField caAcceptableTf;
    @FXML
    private TextField caRectificationTf;
    //CP
    @FXML
    private TextField cpExcellentTf;
    @FXML
    private TextField cpGoodTf;
    @FXML
    private TextField cpAcceptableTf;

    //    private void setAnalysisSettingData(int customGroupNumber, int chartIntervalNumber) {
//        subgroupSizeTf.setText(String.valueOf(customGroupNumber));
//        ndChartNumberTf.setText(String.valueOf(chartIntervalNumber));
//    }
    @FXML
    private TextField cpRectificationTf;
    //CPK
    @FXML
    private TextField cpkExcellentTf;
    @FXML
    private TextField cpkGoodTf;
    @FXML
    private TextField cpkAcceptableTf;
    @FXML
    private TextField cpkRectificationTf;
    //CPL
    @FXML
    private TextField cplExcellentTf;
    @FXML
    private TextField cplGoodTf;
    @FXML
    private TextField cplAcceptableTf;
    @FXML
    private TextField cplRectificationTf;
    //CPU
    @FXML
    private TextField cpuExcellentTf;
    @FXML
    private TextField cpuGoodTf;
    @FXML
    private TextField cpuAcceptableTf;
    @FXML
    private TextField cpuRectificationTf;
    //PP
    @FXML
    private TextField ppExcellentTf;
    @FXML
    private TextField ppGoodTf;
    @FXML
    private TextField ppAcceptableTf;
    @FXML
    private TextField ppRectificationTf;
    //PPK
    @FXML
    private TextField ppkExcellentTf;
    @FXML
    private TextField ppkGoodTf;
    @FXML
    private TextField ppkAcceptableTf;
    @FXML
    private TextField ppkRectificationTf;
    //PPL
    @FXML
    private TextField pplExcellentTf;
    @FXML
    private TextField pplGoodTf;
    @FXML
    private TextField pplAcceptableTf;
    @FXML
    private TextField pplRectificationTf;
    //PPU
    @FXML
    private TextField ppuExcellentTf;
    @FXML
    private TextField ppuGoodTf;
    @FXML
    private TextField ppuAcceptableTf;
    @FXML
    private TextField ppuRectificationTf;
    @FXML
    private VBox defaultSettingVBox, alarmSettingVBox, controlAlarmRuleVBox, exportSettingVBox;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        this.searchTestItemTf.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEXTFIELD_PROMPT));
//        this.initBtnIcon();
//        this.initComponent();
        this.initData();
//        this.initComponentEvent();
//        this.initValidate();
    }
    /**
     * init data
     */
    public void initData() {}

}

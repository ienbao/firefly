package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.controller.SpcSettingController;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class YieldSettingController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(SpcSettingController.class);
    @FXML
    private Label alarmSetting;
    @FXML
    private Label defaultSetting;
    @FXML
    private Label exportMode;
    @FXML
    private Button apply;
    @FXML
    private Button cancel;
    @FXML
    private Button ok;

    //default setting
    @FXML
    private ComboBox defaultSettingCb;

    //Export Template Setting
    @FXML
    private ComboBox exportTemplateCb;
    @FXML
    private Button exportTemplateSettingBtn;

    @FXML
    private ScrollPane settingScrollPane;

//    private SpcExportSettingController spcExportSettingController;
    //CP
    @FXML
    private TextField FTPExcellentTf;
    @FXML
    private TextField FTPGoodTf;
    @FXML
    private TextField FTPAcceptableTf;

    //CPK
    @FXML
    private TextField NTFExcellentTf;
    @FXML
    private TextField NTFGoodTf;
    @FXML
    private TextField NTFAcceptableTf;

    //CPL
    @FXML
    private TextField NGExcellentTf;
    @FXML
    private TextField NGGoodTf;
    @FXML
    private TextField NGAcceptableTf;

    @FXML
    private VBox defaultSettingVBox, alarmSettingVBox,  exportSettingVBox;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initBtnIcon();
//        this.initComponent();
        this.initData();
//        this.initComponentEvent();
//        this.initValidate();
    }
    /**
     * init data
     */
    public void initData() {}


    private void initBtnIcon() {
        exportTemplateSettingBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_setting_normal.png")));
        exportTemplateSettingBtn.setPrefSize(22, 22);
    }


}
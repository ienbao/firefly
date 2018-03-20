package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrAnalysisMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/6.
 */
public class GrrSettingController {
    @FXML
    private Label defaultSetting;
    @FXML
    private Label alarmSetting;
    @FXML
    private Label exportSetting;
    @FXML
    private RadioButton anova;
    @FXML
    private RadioButton xbar;
    @FXML
    private ComboBox<Double> coverage;
    @FXML
    private TextField sign;
    @FXML
    private ComboBox<String> sort;
    @FXML
    private TextField levelGood;
    @FXML
    private TextField levelBad;
    @FXML
    private Button exportBtn;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    @FXML
    private Button apply;
    private ToggleGroup group = new ToggleGroup();

    private GrrConfigServiceImpl grrConfigService = new GrrConfigServiceImpl();
    private Map<String, Boolean> grrExportSetting = Maps.newHashMap();

    @FXML
    private void initialize() {
        initLabelStyle();
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_setting_normal.png")));

        anova.setToggleGroup(group);
        anova.setSelected(true);
        xbar.setToggleGroup(group);
        coverage.setItems(FXCollections.observableArrayList(5.15, 6.0));
        coverage.setValue(5.15);
        sort.setItems(FXCollections.observableArrayList("Appraisers", "default"));
        sort.setValue("Appraisers");
        levelGood.setText("5");
        levelBad.setText("10");

        initConfigData();
        initEvent();
    }

    private void initConfigData() {
        GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
        if (grrConfigDto != null) {
            if (grrConfigDto.getExport() != null) {
                grrExportSetting = grrConfigDto.getExport();
            }
            if (grrConfigDto.getAnalysisMethod().equals(GrrAnalysisMethod.ANOVA)) {
                anova.setSelected(true);
                anova.requestFocus();
            }
            coverage.setValue(grrConfigDto.getCoverage());
            sign.setText(grrConfigDto.getSignLevel());
            sort.setValue(grrConfigDto.getSortMethod());
            levelGood.setText(grrConfigDto.getAlarmSetting().get(0).toString());
            levelBad.setText(grrConfigDto.getAlarmSetting().get(1).toString());
        }
    }

    private void initEvent() {
        exportBtn.setOnAction(event -> buildExportDia());
        ok.setOnAction(event -> {
            saveGrrSetting();
            StageMap.closeStage("grrSetting");
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("grrSetting");
        });
        apply.setOnAction(event -> {
            saveGrrSetting();
        });
    }

    private void initLabelStyle() {
        defaultSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.setStyle("-fx-background-color: #FFFFFF");
                alarmSetting.setStyle("-fx-background-color: #FOFOFO");
                exportSetting.setStyle("-fx-background-color: #FOFOFO");
            }
        });
        alarmSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.setStyle("-fx-background-color: #FOFOFO");
                alarmSetting.setStyle("-fx-background-color: #FFFFFF");
                exportSetting.setStyle("-fx-background-color: #FOFOFO");
            }
        });
        exportSetting.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                defaultSetting.setStyle("-fx-background-color: #FOFOFO");
                alarmSetting.setStyle("-fx-background-color: #FOFOFO");
                exportSetting.setStyle("-fx-background-color: #FFFFFF");
            }
        });
    }

    private void saveGrrSetting() {
        GrrConfigDto grrConfigDto = new GrrConfigDto();
        if (anova.isSelected()) {
            grrConfigDto.setAnalysisMethod(GrrAnalysisMethod.ANOVA);
        } else {
            grrConfigDto.setAnalysisMethod(GrrAnalysisMethod.XbarAndRange);
        }
        grrConfigDto.setCoverage(coverage.getValue());
        grrConfigDto.setSignLevel(sign.getText());
        grrConfigDto.setSortMethod(sort.getValue());
        List<Double> alarm = Lists.newArrayList();
        alarm.add(Double.valueOf(levelGood.getText()));
        alarm.add(Double.valueOf(levelBad.getText()));
        grrConfigDto.setAlarmSetting(alarm);
        grrConfigDto.setExport(grrExportSetting);
        grrConfigService.saveGrrConfig(grrConfigDto);
    }

    private void buildExportDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_export_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExportSetting", GrrFxmlAndLanguageUtils.getString(ResourceMassages.GRR_EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

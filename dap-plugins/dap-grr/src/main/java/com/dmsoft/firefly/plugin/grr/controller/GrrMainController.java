package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrDetailResultDto;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by cherry on 2018/3/11.
 */
public class GrrMainController implements Initializable {

    private GrrDataFrameDto grrDataFrame;
    private List<GrrSummaryDto> summaryDtos;
    private GrrDetailDto grrDetailDto;
    private GrrConfigDto grrConfigDto;
    private TemplateSettingDto activeTemplateSettingDto;
    private GrrParamDto grrParamDto;
    @FXML
    private GrrItemController grrItemController;
    @FXML
    private GrrResultController grrResultController;
    @FXML
    private GrrViewDataController grrViewDataController;

    private JobManager manager = RuntimeContext.getBean(JobManager.class);

    @FXML
    private Button exportBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grrItemController.init(this);
        grrResultController.init(this);
        grrViewDataController.init(this);
        initBtnIcon();
        initComponentEvents();
    }

    private void initBtnIcon() {
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
    }

    private void initComponentEvents() {
        exportBtn.setOnAction(event -> {
            getExportBtnEvent();
        });
    }

    private void getExportBtnEvent() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_export.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExport", "Grr Export", root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public GrrDataFrameDto getGrrDataFrame() {
        return grrDataFrame;
    }

    public void setGrrDataFrame(GrrDataFrameDto grrDataFrame) {
        this.grrDataFrame = grrDataFrame;
    }

    public SearchConditionDto getSearchConditionDto() {
        return grrItemController.getSearchConditionDto();
    }

    public List<GrrSummaryDto> getSummaryDtos() {
        return summaryDtos;
    }

    public void setSummaryDtos(List<GrrSummaryDto> summaryDtos) {
        this.summaryDtos = summaryDtos;
    }

    public GrrDetailDto getGrrDetailDto() {
        return grrDetailDto;
    }

    public void setGrrDetailDto(GrrDetailDto grrDetailDto) {
        this.grrDetailDto = grrDetailDto;
    }

    public void updateGrrSummaryAndDetail() {
        grrResultController.analyzeGrrResult(summaryDtos, grrDetailDto);
    }

    public void updateGrrViewData() {
        grrViewDataController.refresh();
    }

    public GrrConfigDto getGrrConfigDto() {
        return grrConfigDto;
    }

    public void setGrrConfigDto(GrrConfigDto grrConfigDto) {
        this.grrConfigDto = grrConfigDto;
    }

    public TemplateSettingDto getActiveTemplateSettingDto() {
        return activeTemplateSettingDto;
    }

    public void setActiveTemplateSettingDto(TemplateSettingDto activeTemplateSettingDto) {
        this.activeTemplateSettingDto = activeTemplateSettingDto;
    }

    public GrrParamDto getGrrParamDto() {
        return grrParamDto;
    }

    public void setGrrParamDto(GrrParamDto grrParamDto) {
        this.grrParamDto = grrParamDto;
    }
}

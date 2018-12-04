package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ListUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.stereotype.Component;

/**
 * Created by cherry on 2018/3/11.
 */
@Component
public class GrrMainController implements Initializable {
    private Logger logger = LoggerFactory.getLogger(GrrMainController.class);
    private GrrDataFrameDto grrDataFrame;
    private GrrDataFrameDto backGrrDataFrame;
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
    @FXML
    private Tab grrResultTab;
    @FXML
    private Button exportBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button printBtn;
    @FXML
    private Button resetBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grrItemController.init(this);
        grrResultController.init(this);
        grrViewDataController.init(this);
        initBtnIcon();
        initComponentEvents();
        setDisable(true);
    }

    /**
     * Clear grr result and view data
     */
    public void clearResultData() {
        this.grrDataFrame = null;
        grrResultController.removeAllResultData();
        grrResultController.disableResultOperator();
        grrResultController.toggleTickLabelsVisible(false);
        grrViewDataController.refresh();
    }

    private void initBtnIcon() {
        TooltipUtil.installNormalTooltip(resetBtn, GrrFxmlAndLanguageUtils.getString("GRR_RESET_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(printBtn, GrrFxmlAndLanguageUtils.getString("GRR_PRINT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(exportBtn, GrrFxmlAndLanguageUtils.getString("GRR_EXPORT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(refreshBtn, GrrFxmlAndLanguageUtils.getString("GRR_REFRESH_BTN_TOOLTIP"));
    }

    private void initComponentEvents() {
        exportBtn.setOnAction(event -> getExportBtnEvent());
        printBtn.setOnAction(event -> getExportBtnEvent());

        refreshBtn.setOnAction(event -> {
            logger.debug("Refresh grr start ...");
            grrResultController.refreshGrrResult();
            grrViewDataController.refresh();
            logger.debug("Refresh grr finished.");
        });

        grrResultTab.setOnSelectionChanged(event -> {
            if (grrResultTab.isSelected()) {
                if (grrViewDataController.isChanged()) {
                    logger.debug("Analyze grr result start ...");
                    grrDataFrame = grrViewDataController.getChangedGrrDFDto();
                    grrResultController.changeGrrResult();
                    grrViewDataController.setChanged(false);
                    logger.debug("Analyze grr result finished.");
                }
            }
        });

        resetBtn.setOnAction(event -> {
            logger.debug("Reset grr start ...");
            this.getSearchConditionDto().setSelectedTestItemDtos(this.grrItemController.getInitSelectTestItemDtos());
            GrrDataFrameDto newDataFrame = new GrrDataFrameDto();
            if (backGrrDataFrame == null) {
                return;
            }
            newDataFrame.setDataFrame(backGrrDataFrame.getDataFrame());
            List<GrrViewDataDto> includeViewDataDtos = Lists.newArrayList(ListUtils.deepCopy(backGrrDataFrame.getIncludeDatas()));
            List<GrrViewDataDto> backViewDataDtos = Lists.newArrayList(ListUtils.deepCopy(backGrrDataFrame.getBackupDatas()));
            newDataFrame.setIncludeDatas(includeViewDataDtos);
            newDataFrame.setBackupDatas(backViewDataDtos);
            this.grrDataFrame = newDataFrame;
            this.grrViewDataController.refresh();
            this.grrResultController.changeGrrResult();
            logger.debug("Reset grr finished.");
        });
    }

    private void getExportBtnEvent() {
        Pane root = null;
        try {
//            root = GrrFxmlAndLanguageUtils.load("view/grr_export.fxml");

            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExport", GrrFxmlAndLanguageUtils.getString("GRR_EXPORT"), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            StageMap.addStage(UIConstant.GRR_EXPORT_STAGE, stage);
//            ((GrrExportController) fxmlLoader.getController()).initGrrExportLeftConfig(grrItemController.getGrrLeftConfigDto());
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * set disable
     *
     * @param disable disable
     */
    public void setDisable(boolean disable) {
        resetBtn.setDisable(disable);
        refreshBtn.setDisable(disable);
        if (disable) {
            refreshBtn.getStyleClass().setAll("btn-primary","grr-refresh-disable-btn");
        } else {
            refreshBtn.getStyleClass().setAll("btn-primary","grr-refresh-able-btn");
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

    /**
     * Update grr summary data and grr detail result data
     */
    public void updateGrrSummaryAndDetail() {
        grrResultController.analyzeGrrResult(summaryDtos, grrDetailDto);
    }

    /**
     * Update grr view data
     */
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

    public GrrDataFrameDto getBackGrrDataFrame() {
        return backGrrDataFrame;
    }

    public void setBackGrrDataFrame(GrrDataFrameDto backGrrDataFrame) {
        this.backGrrDataFrame = backGrrDataFrame;
    }

}

package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ListUtils;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
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
    }

    private void initBtnIcon() {
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
        resetBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_reset_normal.png")));
        printBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_print_normal.png")));
        refreshBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
        TooltipUtil.installNormalTooltip(resetBtn, GrrFxmlAndLanguageUtils.getString("GRR_RESET_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(printBtn, GrrFxmlAndLanguageUtils.getString("GRR_PRINT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(exportBtn, GrrFxmlAndLanguageUtils.getString("GRR_EXPORT_BTN_TOOLTIP"));
        TooltipUtil.installNormalTooltip(refreshBtn, GrrFxmlAndLanguageUtils.getString("GRR_REFRESH_BTN_TOOLTIP"));
    }

    private void initComponentEvents() {
        exportBtn.setOnAction(event -> getExportBtnEvent());
        printBtn.setOnAction(event -> getExportBtnEvent());

        refreshBtn.setOnAction(event -> {
            grrResultController.refreshGrrResult();
            grrViewDataController.refresh();
        });

        grrResultTab.setOnSelectionChanged(event -> {
            if (grrResultTab.isSelected()) {
                if (grrViewDataController.isChanged()) {
                    grrDataFrame = grrViewDataController.getChangedGrrDFDto();
                    grrResultController.changeGrrResult();
                    grrViewDataController.setChanged(false);
                }
            }
        });

        resetBtn.setOnAction(event -> {
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
        });
    }

    private void getExportBtnEvent() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_export.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExport", GrrFxmlAndLanguageUtils.getString("GRR_EXPORT"), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            ((GrrExportController) fxmlLoader.getController()).initGrrExportLeftConfig(grrItemController.getGrrLeftConfigDto());
            stage.setResizable(false);
            stage.toFront();
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

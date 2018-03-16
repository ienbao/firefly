package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.model.GrrViewDataDFBackupModel;
import com.dmsoft.firefly.plugin.grr.model.GrrViewDataDFIncludeModel;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * grr view data controller
 *
 * @author Can Guan
 */
public class GrrViewDataController implements Initializable {
    @FXML
    private TextFieldFilter analysisFilterLB;

    @FXML
    private TextFieldFilter exchangeFilterLB;

    @FXML
    private TableView<String> analysisDataTB;

    @FXML
    private TableView<String> exchangeDataTB;

    @FXML
    private Label exchangeableLB;

    @FXML
    private Button exchangeBtn;

    private GrrMainController grrMainController;
    private GrrDataFrameDto grrDataFrameDto;
    private GrrViewDataDFIncludeModel includeModel;
    private GrrViewDataDFBackupModel backupModel;
    private String partKey = GrrFxmlAndLanguageUtils.getString("PART") + " : ";
    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " : ";
    private boolean isChanged = false;

    /**
     * Init grr main controller
     *
     * @param grrMainController grr main controller
     */
    public void init(GrrMainController grrMainController) {
        this.grrMainController = grrMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        analysisFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        exchangeFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        analysisFilterLB.setDisable(true);
        exchangeableLB.setDisable(true);
        analysisFilterLB.getTextField().textProperty().addListener((ov, t1, t2) -> {
            if (this.includeModel != null) {
                this.includeModel.searchTestItem(t2);
            }
        });
        exchangeFilterLB.getTextField().textProperty().addListener((ov, t1, t2) -> {
            if (this.backupModel != null) {
                this.backupModel.searchTestItem(t2);
            }
        });
        this.exchangeBtn.setOnAction(event -> {

        });
    }

    /**
     * method to judge is changed or not
     *
     * @return true : exchange, false : not
     */
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * method to set grr data frame
     */
    public void refresh() {
        GrrDataFrameDto dataFrame = grrMainController.getGrrDataFrame();
        boolean isSlot = true;
        if (grrMainController.getSearchConditionDto() != null && grrMainController.getSearchConditionDto().getAppraiser() == null) {
            isSlot = false;
        }
        if (dataFrame != null && dataFrame.getDataFrame() != null && dataFrame.getIncludeDatas() != null && !dataFrame.getIncludeDatas().isEmpty()) {
            analysisFilterLB.setDisable(false);
            exchangeableLB.setDisable(false);
            this.grrDataFrameDto = dataFrame;
            this.includeModel = new GrrViewDataDFIncludeModel(this.grrDataFrameDto);
            this.includeModel.addListener(grrViewDataDto -> this.exchangeableLB.setText(partKey + grrViewDataDto.getPart() + ", " + appKey + grrViewDataDto.getOperator()));
            if (dataFrame.getBackupDatas() != null && !dataFrame.getBackupDatas().isEmpty()) {
                this.backupModel = new GrrViewDataDFBackupModel(this.grrDataFrameDto, isSlot);
                this.includeModel.addListener(this.backupModel);
            } else {
                this.backupModel = null;
                if (this.exchangeDataTB.getColumns() != null) {
                    this.exchangeDataTB.getColumns().clear();
                }
            }
        } else {
            this.includeModel = null;
            this.grrDataFrameDto = null;
            if (this.analysisDataTB.getColumns() != null) {
                this.analysisDataTB.getColumns().clear();
            }
            this.backupModel = null;
            if (this.exchangeDataTB.getColumns() != null) {
                this.exchangeDataTB.getColumns().clear();
            }
        }
    }

    /**
     * method to ge grr data frame dto
     *
     * @return grr data frame dto
     */
    public GrrDataFrameDto getChangedGrrDFDto() {
        GrrDataFrameDto result = new GrrDataFrameDto();
        result.setDataFrame(this.grrDataFrameDto.getDataFrame());
        Map<String, GrrViewDataDto> viewDataDtoMap = Maps.newHashMap();
        if (this.grrDataFrameDto.getIncludeDatas() != null) {
            for (GrrViewDataDto grrViewDataDto : this.grrDataFrameDto.getIncludeDatas()) {
                viewDataDtoMap.put(grrViewDataDto.getRowKey(), grrViewDataDto);
            }
        }
        if (this.grrDataFrameDto.getBackupDatas() != null) {
            for (GrrViewDataDto grrViewDataDto : this.grrDataFrameDto.getIncludeDatas()) {
                viewDataDtoMap.put(grrViewDataDto.getRowKey(), grrViewDataDto);
            }
        }
        List<GrrViewDataDto> includeDataDto = Lists.newArrayList();
        if (analysisDataTB.getItems() != null && !analysisDataTB.getItems().isEmpty()) {
            for (String s : analysisDataTB.getItems()) {
                includeDataDto.add(viewDataDtoMap.get(s));
            }
        }
        List<GrrViewDataDto> backupDataDto = Lists.newArrayList();
        if (this.backupModel.getAllRowKeys() != null) {
            for (String s : this.backupModel.getAllRowKeys()) {
                backupDataDto.add(viewDataDtoMap.get(s));
            }
        }
        result.setIncludeDatas(includeDataDto);
        result.setBackupDatas(backupDataDto);
        return result;
    }
}

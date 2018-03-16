package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.model.GrrViewDataDFIncludeModel;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

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

    private GrrDataFrameDto grrDataFrameDto;
    private GrrViewDataDFIncludeModel model;
    private String partKey = GrrFxmlAndLanguageUtils.getString("PART") + " : ";
    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " : ";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        analysisFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        exchangeFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        analysisFilterLB.setDisable(true);
        exchangeableLB.setDisable(true);
    }

    /**
     * method to judge is changed or not
     *
     * @return true : exchange, false : not
     */
    public boolean isChanged() {
        return false;
    }

    /**
     * method to get grr data frame
     *
     * @return grr data frame
     */
    public GrrDataFrameDto getGrrDataFrame() {
        return grrDataFrameDto;
    }

    /**
     * method to set grr data frame
     *
     * @param dataFrame data frame
     */
    public void setGrrDataFrame(GrrDataFrameDto dataFrame) {
        analysisFilterLB.setDisable(false);
        exchangeableLB.setDisable(false);
        this.grrDataFrameDto = dataFrame;
        this.model = new GrrViewDataDFIncludeModel(this.grrDataFrameDto);
        this.model.addListener(grrViewDataDto -> {
            this.exchangeableLB.setText(partKey + grrViewDataDto.getPart() + ", " + appKey + grrViewDataDto.getOperator());
        });
        refresh();
    }

    private void refresh() {

    }

    public GrrDataFrameDto getOriginalGrrDataFrame() {
        return this.grrDataFrameDto;
    }
}

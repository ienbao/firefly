package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.dialog.ChooseTestItemDialog;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrViewDataDto;
import com.dmsoft.firefly.plugin.grr.model.GrrViewDataDFBackupModel;
import com.dmsoft.firefly.plugin.grr.model.GrrViewDataDFIncludeModel;
import com.dmsoft.firefly.plugin.grr.utils.AppConstant;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
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

    @FXML
    private Button chooseItemBtn;
    private ChooseTestItemDialog chooseTestItemDialog;

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
        analysisDataTB.setSkin(new ExpandableTableViewSkin(analysisDataTB));
        exchangeDataTB.setSkin(new ExpandableTableViewSkin(exchangeDataTB));
        analysisFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        exchangeFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        analysisFilterLB.setDisable(true);
        exchangeFilterLB.setDisable(true);
        chooseItemBtn.setDisable(true);
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
        chooseItemBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/choose-test-items.svg")));
        this.exchangeBtn.setOnAction(event -> {
            if (this.backupModel != null && this.includeModel != null && this.backupModel.getSelectedViewDataDto() != null && this.includeModel.getSelectedViewDataDto() != null) {
                int analsysisTBIndex = analysisDataTB.getSelectionModel().getSelectedIndex();
                int exchangeTBIndex = exchangeDataTB.getSelectionModel().getSelectedIndex();
                GrrViewDataDto toBeBackupDto = this.includeModel.getSelectedViewDataDto();
                String toBeBackUpApp = toBeBackupDto.getOperator();
                String toBeBackUpTrail = toBeBackupDto.getTrial();
                GrrViewDataDto toBeIncludeDto = this.backupModel.getSelectedViewDataDto();
                String toBeIncludeApp = toBeIncludeDto.getOperator();
                String toBeIncludeTrail = toBeIncludeDto.getTrial();
                toBeBackupDto.setOperator(toBeIncludeApp);
                toBeBackupDto.setTrial(toBeIncludeTrail);
                toBeIncludeDto.setOperator(toBeBackUpApp);
                toBeIncludeDto.setTrial(toBeBackUpTrail);
                this.includeModel.replace(toBeIncludeDto);
                this.backupModel.replace(toBeBackupDto);
                isChanged = true;
                analysisDataTB.getSelectionModel().select(analsysisTBIndex);
                exchangeDataTB.getSelectionModel().select(exchangeTBIndex);
            }
        });
        chooseTestItemDialog = new ChooseTestItemDialog(null, null, AppConstant.MAX_COLUMN);
        chooseItemBtn.setOnAction(event -> {
            chooseTestItemDialog.resetSelectedItems(this.includeModel.getOriginItems());
            chooseTestItemDialog.show();
        });
        chooseTestItemDialog.getOkBtn().setOnAction(event -> {
            chooseTestItemDialog.close();
            if (this.grrDataFrameDto == null) {
                return;
            }
            List<String> selectedTestItems = chooseTestItemDialog.getSelectedItems();
            this.includeModel.resetHeaders(selectedTestItems);
            this.backupModel.resetHeaders(selectedTestItems);
            this.analysisFilterLB.getTextField().setText("");
            this.exchangeFilterLB.getTextField().setText("");
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

    public void setChanged(boolean changed) {
        isChanged = changed;
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
        final boolean slot = isSlot;
        if (dataFrame != null && dataFrame.getDataFrame() != null && dataFrame.getIncludeDatas() != null && !dataFrame.getIncludeDatas().isEmpty()) {
            analysisFilterLB.setDisable(false);
            exchangeFilterLB.setDisable(false);
            chooseItemBtn.setDisable(false);
            this.grrDataFrameDto = dataFrame;
            this.includeModel = new GrrViewDataDFIncludeModel(this.grrDataFrameDto, grrMainController.getSearchConditionDto());
            List<String> allItemNames = Lists.newArrayList();
            allItemNames.addAll(this.grrDataFrameDto.getDataFrame().getAllTestItemName());
            chooseTestItemDialog.resetItems(allItemNames, this.includeModel.getHeaderArray());
            if (isSlot) {
                this.exchangeableLB.setText(partKey + this.grrDataFrameDto.getIncludeDatas().get(0).getPart() + ", " + appKey + this.grrDataFrameDto.getIncludeDatas().get(0).getOperator());
            } else {
                this.exchangeableLB.setText(partKey + this.grrDataFrameDto.getIncludeDatas().get(0).getPart());
            }
            this.includeModel.addListener(grrViewDataDto -> {
                if (slot) {
                    this.exchangeableLB.setText(partKey + grrViewDataDto.getPart() + ", " + appKey + grrViewDataDto.getOperator());
                } else {
                    this.exchangeableLB.setText(partKey + grrViewDataDto.getPart());
                }
            });
            if (dataFrame.getBackupDatas() != null && !dataFrame.getBackupDatas().isEmpty()) {
                this.backupModel = new GrrViewDataDFBackupModel(this.grrDataFrameDto, grrMainController.getSearchConditionDto(), isSlot);
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
            chooseTestItemDialog.resetItems(Lists.newArrayList(), Lists.newArrayList());
            if (this.analysisDataTB.getColumns() != null) {
                this.analysisDataTB.getColumns().clear();
            }
            this.backupModel = null;
            if (this.exchangeDataTB.getColumns() != null) {
                this.exchangeDataTB.getColumns().clear();
            }

            exchangeableLB.setText("");
            analysisFilterLB.getTextField().setText("");
            exchangeFilterLB.getTextField().setText("");
            analysisFilterLB.setDisable(true);
            exchangeFilterLB.setDisable(true);
            chooseItemBtn.setDisable(true);
            isChanged = false;
        }
        TableViewWrapper.decorate(analysisDataTB, this.includeModel);
        TableViewWrapper.decorate(exchangeDataTB, this.backupModel);
    }

    /**
     * method to ge grr data frame dto
     *
     * @return grr data frame dto
     */
    public GrrDataFrameDto getChangedGrrDFDto() {
        GrrDataFrameDto result = new GrrDataFrameDto();
        result.setDataFrame(this.grrDataFrameDto.getDataFrame());
        List<GrrViewDataDto> includeDataDto = Lists.newArrayList();
        List<GrrViewDataDto> backupDataDto = Lists.newArrayList();
        if (this.includeModel.getRowKeyArray() != null) {
            for (String s : includeModel.getRowKeyArray()) {
                includeDataDto.add(includeModel.getGrrViewDataDtoMap().get(s));
            }
        }
        if (this.backupModel.getRowKeyArray() != null) {
            for (String s : backupModel.getAllRowKeys()) {
                backupDataDto.add(backupModel.getGrrViewDataDtoMap().get(s));
            }
        }
        result.setIncludeDatas(includeDataDto);
        result.setBackupDatas(backupDataDto);
        return result;
    }

    public GrrDataFrameDto getGrrDataFrameDto() {
        return grrDataFrameDto;
    }
}

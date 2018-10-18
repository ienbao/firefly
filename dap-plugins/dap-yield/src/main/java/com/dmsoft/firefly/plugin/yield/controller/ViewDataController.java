package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.yield.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.yield.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.ViewResource;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewDataController implements Initializable {

    @FXML
    private Button chooseColumnBtn;  //选择按钮
    @FXML
    private TextFieldFilter filteValueTf; //搜索框
    @FXML
    private TableView<String> viewDataTable; //表格
    @FXML
    private VBox vbox;
    private YieldMainController yieldMainController;

    private SearchDataFrame dataFrame;
    private List<SearchConditionDto> searchViewDataConditionDto;
    private String selectedRowKeys;
    private String selectedClumnKey;

    private List<String> selectStatisticalResultName = Lists.newArrayList();
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
//    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filteValueTf.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_VALUE_PROMPT));
        this.initBtnIcon();
        viewDataTable.getColumns().clear();
        chooseColumnBtn.setDisable(true);
    }

    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
    }



    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
//        TooltipUtil.installNormalTooltip(chooseColumnBtn, SpcFxmlAndLanguageUtils.getString("CHOOSE_STATISTICAL_RESULT"));
    }

    /**
     * method to clear view data
     */
    public void clearViewData() {
        filteValueTf.getTextField().setText(null);
        this.setViewData(null, null, null,null);
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame                         search data frame
     * @param selectedRowKey                    selected row key
     * @param searchViewDataConditionDto statisticalSearchConditionDtoList
     */
    private void setViewData(SearchDataFrame dataFrame, String selectedRowKey, String selectedClumnKey, List<SearchConditionDto> searchViewDataConditionDto) {
        this.searchViewDataConditionDto = searchViewDataConditionDto;
        this.selectedRowKeys = selectedRowKey;
        this.selectedClumnKey = selectedClumnKey;
        this.dataFrame = dataFrame;
//        this.columnFilterSetting.clear();
//        if (dataFrame == null) {
//            filteValueTf.setDisable(true);
//            viewDataTable.getColumns().clear();
//            try {
//                if (model != null) {
//                    this.model.getRowKeyArray().clear();
//                }
//            } catch (NullPointerException ignored) {
//                ignored.printStackTrace();
//            }
//            this.model = null;
//            return;
//        }
//        filteValueTf.setDisable(false);
//
//        List<TableColumn<String, ?>> sortedColumnList = null;
//        if (isTimer && this.model != null) {
//            sortedColumnList = Lists.newArrayList(viewDataTable.getSortOrder());
//        }
//        vbox.getChildren().remove(viewDataTable);
//        this.viewDataTable = new TableView<>();
//        this.viewDataTable.setSkin(new ExpandableTableViewSkin(this.viewDataTable));
//        this.viewDataTable.setStyle("-fx-border-width: 1 0 0 0");
//        VBox.setVgrow(viewDataTable, Priority.ALWAYS);
//        this.vbox.setAlignment(Pos.CENTER);
//        this.vbox.getChildren().add(viewDataTable);
//        this.model = new ViewDataDFModel(dataFrame, selectedRowKey);
//        this.model.setStatisticalSearchConditionDtoList(statisticalSearchConditionDtoList);
//        this.model.setMainController(spcMainController);
//        if (model.getHeaderArray().size() > 51) {
//            model.getHeaderArray().remove(51, model.getHeaderArray().size());
//        }
//        unSelectedCheckBox.setDisable(false);
//        if (isTimer) {
//            this.model.setIsTimer(true);
//            unSelectedCheckBox.setDisable(true);
//        }
//        TableViewWrapper.decorate(viewDataTable, model);
//        if (isTimer) {
//            viewDataTable.getColumns().get(0).setSortable(false);
//            viewDataTable.getColumns().get(0).setResizable(false);
//            viewDataTable.getColumns().get(0).setPrefWidth(32);
//        }
//        if (model.getAllCheckBox() != null) {
//            model.getAllCheckBox().setOnMouseClicked(event -> {
//                for (String s : model.getRowKeyArray()) {
//                    model.getCheckValue(s, "").setValue(model.getAllCheckBox().selectedProperty().getValue());
//                }
//            });
//        }
//        viewDataTable.getColumns().forEach(this::decorate);
//        String filterTxt = filterTf.getTextField().getText();
//        if (DAPStringUtils.isNotBlank(filterTxt)) {
//            filterTf.getTextField().setText("");
//            filterTf.getTextField().setText(filterTxt);
//        }
//        chooseTestItemDialog.resetSelectedItems(model.getHeaderArray().subList(1, model.getHeaderArray().size()));
//        if (sortedColumnList != null && !sortedColumnList.isEmpty()) {
//            final TableColumn<String, ?> sortedColumn = sortedColumnList.get(0);
//            Platform.runLater(() -> {
//                TableColumn<String, ?> column1 = null;
//                for (TableColumn<String, ?> column : viewDataTable.getColumns()) {
//                    if (sortedColumn.getText().equals(column.getText())) {
//                        column.setSortType(sortedColumn.getSortType());
//                        column1 = column;
//                        break;
//                    }
//                }
//                if (column1 != null) {
//                    viewDataTable.getSortOrder().add(column1);
//                    viewDataTable.sort();
//                }
//            });
//        }
    }
}

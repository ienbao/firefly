package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.yield.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.ViewResource;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewDataController implements Initializable {

    @FXML
    private Button chooseColumnBtn;
    @FXML
    private TextFieldFilter filterTestItemTf;
    @FXML
    private TableView<String> viewDataTable;

    private YieldMainController yieldMainController;
//
//    private ChooseDialogController chooseDialogController;
//
//    private StatisticalTableModel statisticalTableModel;

    private List<String> selectStatisticalResultName = Lists.newArrayList();
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
//    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTestItemTf.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
        //this.buildChooseColumnDialog();
//        this.initStatisticalResultTable();
        this.initBtnIcon();
//        this.initComponentEvent();
        viewDataTable.getColumns().clear();
    }

    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
    }


    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = YieldFxmlAndLanguageUtils.getLoaderFXML(ViewResource.Yield_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
//            chooseDialogController = fxmlLoader.getController();
//            chooseDialogController.setValueColumnText(SpcFxmlAndLanguageUtils.getString("STATISTICAL_RESULT"));
//            chooseDialogController.setFilterTFPrompt(SpcFxmlAndLanguageUtils.getString("STATISTICAL_RESULT_PROMPT"));
//            this.initChooseStatisticalResultTableData();
//            Stage stage = WindowFactory.createNoManagedStage(SpcFxmlAndLanguageUtils.getString("CHOOSE_STATISTICAL_RESULTS"), root,
//                    getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
//            chooseDialogController.setStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
//        TooltipUtil.installNormalTooltip(chooseColumnBtn, SpcFxmlAndLanguageUtils.getString("CHOOSE_STATISTICAL_RESULT"));
    }
}

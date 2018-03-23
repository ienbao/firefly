package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.component.ContentStackPane;
import com.dmsoft.firefly.gui.component.CustomerTooltip;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.controller.template.DataSourceController;
import com.dmsoft.firefly.gui.model.StateBarTemplateModel;
import com.dmsoft.firefly.gui.model.UserModel;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.gui.utils.TabUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.google.common.io.Resources.getResource;

public class MainController {

    private final Logger logger = LoggerFactory.getLogger(MainController.class);
    public final Double MAX_HEIGHT = 250.0;
    public final Double MAX_WIDTH = 250.0;
    public final Double MIN_WIDTH = 160.0;

    @FXML
    private GridPane grpContent;

    @FXML
    private ToolBar tbaSystem;

    @FXML
    private GridPane stateBar;

    private Button dataSourceBtn;
    private Button templateBtn;

    private ScrollPane scrollPaneTooltip;
    private CustomerTooltip dataSourceTooltip;
    private ObservableList<String> dataSourceList = FXCollections.observableArrayList();

    private Popup templatePopup;
    private ListView<StateBarTemplateModel> templateView;
    private ObservableList<StateBarTemplateModel> templateList = FXCollections.observableArrayList();

    private ContentStackPane contentStackPane;
    private Map<String, TabPane> tabPaneMap = new LinkedHashMap<>();
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);

    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private TemplateSettingDto templateSettingDto;


    @FXML
    private void initialize() {
        scrollPaneTooltip = new ScrollPane();
        dataSourceTooltip = new CustomerTooltip();
        contentStackPane = new ContentStackPane();
        grpContent.add(contentStackPane, 0, 1);
        this.initToolBar();
        this.initStateBar();
        this.updateStateBarIcon();
        if (isLogin()) {
            templateSettingDto =  envService.findActivatedTemplate();
            this.initDataSource();
            this.initDataSourceTooltip();
            this.initTemplate();
            this.initTemplatePopup();
            this.initComponentEvent();
        }
    }

    private void initToolBar() {
        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMainBodyNames();
        final int[] i = {0};
        names.forEach(name -> {
            Button btn = new Button(name);
            btn.setId(name);
            btn.setFocusTraversable(true);
            btn.setOnAction(event -> {
                if (!tabPaneMap.containsKey(name)) {
                    Pane pane = pc.getMainBodyPane(name).getNewPane();
                    pane.setId(name);
                    initTab(name, pane);
                } else {
                    contentStackPane.navTo(name);
                }
                setActiveBtnStyle(btn);
            });
            tbaSystem.getItems().add(btn);
            if (i[0] == 0) {
                setActiveMain(name, (Button) tbaSystem.getItems().get(0), pc);
            }
            i[0]++;
        });
    }

    private void setActiveMain(String name, Button activeBtn, PluginUIContext pc) {
        if (isLogin()) {
            grpContent.setDisable(false);
            if (activeBtn.getId().equals(name)) {
                setActiveBtnStyle(activeBtn);
                Pane pane = pc.getMainBodyPane(name).getNewPane();
                pane.setId(name);
                initTab(name, pane);
            }
        } else {
            grpContent.setDisable(true);
        }
    }

    private void setActiveBtnStyle(Button btn) {
        btn.setStyle(" -fx-text-fill: #ffffff;-fx-background-color: #ea2028");
        for (Node node : tbaSystem.getItems()) {
            if (!node.getId().equals(btn.getId())) {
                node.setStyle(null);
            }
        }
    }

    public void resetMain() {
        grpContent.getChildren().remove(contentStackPane);
        contentStackPane.removeAll();
        tabPaneMap.clear();
        tbaSystem.getItems().clear();
        stateBar.getChildren().clear();
        initialize();
    }

    private void initTab(String name, Pane pane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setStyle("-fx-skin: 'com.dmsoft.firefly.gui.component.TabPaneSkin'");
        tabPane.setId(name);
        Tab tab = new Tab();
        tab.setText(name + "_1");
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
        contentStackPane.add(tabPane);
        contentStackPane.navTo(name);
        tabPaneMap.put(name, tabPane);
        TabUtils.disableCloseTab(tabPane);
        TabUtils.tabSelectedListener(tab, tabPane);
    }

    private void initStateBar() {
        Label lblFile = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_FILE"));
        lblFile.getStyleClass().add("state-bar-lbl");
        stateBar.addColumn(0, lblFile);

        ImageView imageView = new ImageView("/images/btn_edit_unable.png");
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        dataSourceBtn = new Button("--", imageView);
        dataSourceBtn.setContentDisplay(ContentDisplay.RIGHT);
        dataSourceBtn.getStyleClass().add("btn-icon-b");
        dataSourceBtn.setStyle("-fx-padding: 0 3 0 5");
        stateBar.addColumn(1, dataSourceBtn);

        Label lblAnalyze = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_ANALYZE"));
        lblAnalyze.getStyleClass().add("state-bar-lbl");
        stateBar.addColumn(2, lblAnalyze);

        ImageView imageView1 = new ImageView("/images/btn_template_unable.png");
        imageView1.setFitHeight(16);
        imageView1.setFitWidth(16);
        templateBtn = new Button("--", imageView1);
        templateBtn.setContentDisplay(ContentDisplay.RIGHT);
        templateBtn.getStyleClass().add("btn-icon-b");
        templateBtn.setStyle("-fx-padding: 0 3 0 5");
        stateBar.addColumn(3, templateBtn);

        initStateBarText();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefHeight(10);
        progressBar.setMaxHeight(10);
        progressBar.setMinHeight(10);
        progressBar.setPrefWidth(110);
        progressBar.setMaxWidth(110);
        progressBar.setMinWidth(110);
        progressBar.getStyleClass().setAll("progress-bar-lg-green");
        progressBar.setProgress(0.4);

        Label lblMemory = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_MEMORY"), progressBar);
        lblMemory.setPrefHeight(20);
        lblMemory.setMaxHeight(20);
        lblMemory.setMinHeight(20);
        lblMemory.setPrefWidth(179);
        lblMemory.setMaxWidth(179);
        lblMemory.setMinWidth(179);
        lblMemory.getStyleClass().add("state-bar-lbl");
        lblMemory.setStyle("-fx-border-width: 0 1 0 1; -fx-border-color: #ededed;");
        lblMemory.setAlignment(Pos.BASELINE_LEFT);
        lblMemory.setContentDisplay(ContentDisplay.RIGHT);
        stateBar.addColumn(4, lblMemory);

        Label lblVersion = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_VERSION"));
        lblVersion.getStyleClass().add("state-bar-lbl");
        stateBar.addColumn(5, lblVersion);
    }

    private void initStateBarText() {
        if (isLogin()) {
            List<String> activeProjectNames = envService.findActivatedProjectName();
            if (activeProjectNames != null && !activeProjectNames.isEmpty()) {
                dataSourceBtn.setText(activeProjectNames.size() +" "+ GuiFxmlAndLanguageUtils.getString("STATE_BAR_FILE_SELECTED"));
            }
            if (templateSettingDto != null) {
                templateBtn.setText(templateSettingDto.getName());
            }
        } else {
            dataSourceBtn.setText("");
            templateBtn.setText("");
        }
    }

    public void updateDataSourceText(int selectedFileNumber) {
        dataSourceBtn.setText(selectedFileNumber + GuiFxmlAndLanguageUtils.getString("STATE_BAR_FILE_SELECTED"));
    }

    public void updateTemplateText(String selecteTemplateName) {
        templateBtn.setText(selecteTemplateName);
    }

    public void updateStateBarIcon() {
        ImageView imageView = new ImageView("/images/btn_edit_normal.png");
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        dataSourceBtn.setGraphic(imageView);
        ImageView imageView1 = new ImageView("/images/btn_template_normal.png");
        imageView1.setFitHeight(16);
        imageView1.setFitWidth(16);
        templateBtn.setGraphic(imageView1);
    }

    private void initComponentEvent() {
        dataSourceBtn.setOnAction(event -> this.getDataSourceBtnEvent());
        templateBtn.setOnAction(event -> this.getTemplateBtnEvent());
        templateBtn.setOnMouseEntered(event -> this.getTemplateLblEvent());
        templateView.setOnMouseExited(event -> this.getHidePopupEvent());
    }

    private void getHidePopupEvent() {
        templatePopup.hide();
    }

    private void getDataSourceBtnEvent() {
        buildDataSourceDialog();
        logger.debug("Data source btn event.");
        getHidePopupEvent();
    }


    private void initDataSourceTooltip() {
        if (dataSourceList == null || dataSourceList.isEmpty()) {
            dataSourceTooltip.setMinHeight(0);
            dataSourceTooltip.setMinWidth(0);
            dataSourceTooltip.setPrefWidth(0);
            dataSourceTooltip.setPrefHeight(0);
        } else {
            VBox vBox = new VBox();
            dataSourceList.forEach(value->{
                Label label = new Label();
                label.setStyle("-fx-padding: 5 10 0 10");
                label.setText(value);
                label.setContentDisplay(ContentDisplay.CENTER);
                vBox.getChildren().add(label);
            });
            scrollPaneTooltip.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPaneTooltip.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPaneTooltip.setMaxHeight(MAX_HEIGHT);
            scrollPaneTooltip.setMinViewportHeight(10);
            scrollPaneTooltip.setMinViewportWidth(MIN_WIDTH);
            scrollPaneTooltip.setMaxWidth(MAX_WIDTH + 10);
            scrollPaneTooltip.setContent(vBox);
            dataSourceTooltip.setGraphic(scrollPaneTooltip);
            double preHeight =  (20 * dataSourceList.size()) + 10;
            if (preHeight >= MAX_HEIGHT) {
                preHeight = MAX_HEIGHT;
            }
            dataSourceTooltip.setPrefHeight(preHeight);
            dataSourceTooltip.getStyleClass().setAll("candlestick-tooltip");
            dataSourceTooltip.setOnShowing(event -> {
                double screenX = dataSourceBtn.getScene().getWindow().getX() + dataSourceBtn.getScene().getX() + dataSourceBtn.localToScene(0, 0).getX();
                double screenY = dataSourceBtn.getScene().getWindow().getY() + dataSourceBtn.getScene().getY() + dataSourceBtn.localToScene(0, 0).getY() - dataSourceTooltip.getPrefHeight() - 3;
                dataSourceTooltip.setX(screenX);
                dataSourceTooltip.setY(screenY);
            });
            scrollPaneTooltip.setOnMouseEntered(event -> {
                dataSourceTooltip.setHover(true);
            });
            scrollPaneTooltip.setOnMouseExited(event -> {
                dataSourceTooltip.setHover(false);
                dataSourceTooltip.hide();
            });
        }

        CustomerTooltip.install(dataSourceBtn, dataSourceTooltip);
    }

    private void initTemplatePopup() {
        templateView = new ListView<>();
        ImageView imageReset = new ImageView(new Image("/images/icon_choose_one_gray.png"));
        imageReset.setFitHeight(16);
        imageReset.setFitWidth(16);
        templateView.setItems(templateList);
        templateView.setCellFactory(e -> new ListCell<StateBarTemplateModel>() {
            @Override
            public void updateItem(StateBarTemplateModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty == true) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox cell;
                    Label label = new Label(item.getTemplateName());
                    if (item.isIsChecked()) {
                        cell = new HBox(imageReset, label);
                    } else {
                        Label label1 = new Label("");
                        label1.setPrefWidth(16);
                        label1.setPrefHeight(16);
                        cell = new HBox(label1, label);
                    }
                    setGraphic(cell);
                }

            }
        });

        templateView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        templateView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.setIsChecked(false);
            }
            if (newValue != null) {
                newValue.setIsChecked(true);
                templateBtn.setText(newValue.getTemplateName());
                envService.setActivatedTemplate(newValue.getTemplateName());
            }
            resetMain();
        });

        if (templatePopup == null) {
            templatePopup = new Popup();
            templatePopup.getContent().add(templateView);
        } else {
            templatePopup.getContent().clear();
            templatePopup.getContent().add(templateView);
        }
    }

    private void setListViewSize(ListView listView, ObservableList dataList) {
        listView.setMaxWidth(MAX_WIDTH);
        listView.setPrefWidth(MAX_WIDTH);
        listView.setMaxHeight(MAX_HEIGHT);
        if (dataList != null && !dataList.isEmpty()) {
            listView.setPrefHeight((26 * dataList.size()) + 10);
        } else {
            listView.setPrefHeight(0);
            listView.setPrefWidth(0);
            listView.setMinWidth(0);
        }
    }

    public void initDataSource() {
        List<String> projectName = envService.findActivatedProjectName();
        if (projectName != null) {
            Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(projectName);
            LinkedHashMap<String, TestItemWithTypeDto> itemWithTypeDtoMap = templateService.assembleTemplate(testItemDtoMap, GuiConst.DEFAULT_TEMPLATE_NAME);
            envService.setTestItems(itemWithTypeDtoMap);
            envService.setActivatedProjectName(projectName);
        } else {
            projectName = Lists.newArrayList();
        }
        dataSourceList = FXCollections.observableArrayList(projectName);
    }

    public void initTemplate() {

        List<StateBarTemplateModel> stateBarTemplateModels = Lists.newLinkedList();
        List<TemplateSettingDto> allTemplates = templateService.findAllTemplate();
        if (allTemplates != null) {
            allTemplates.forEach(dto -> {
                StateBarTemplateModel stateBarTemplateModel = new StateBarTemplateModel(dto.getName(), false);
                if (templateSettingDto != null) {
                    if (templateSettingDto.getName().equals(stateBarTemplateModel.getTemplateName())) {
                        stateBarTemplateModel.setIsChecked(true);
                    }
                } else {
                    if (DAPStringUtils.isNotBlank(dto.getName()) && dto.getName().equals(GuiConst.DEFAULT_TEMPLATE_NAME)) {
                        stateBarTemplateModel.setIsChecked(true);
                    }
                }
                stateBarTemplateModels.add(stateBarTemplateModel);
            });
        }
        templateList = FXCollections.observableArrayList(stateBarTemplateModels);
        if (templateSettingDto == null) {
            envService.setActivatedTemplate(GuiConst.DEFAULT_TEMPLATE_NAME);
        }
    }

    public void refreshDataSource(ObservableList<String> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    public void refreshTemplate(ObservableList<StateBarTemplateModel> templateList) {
        this.templateList = templateList;
        templateView.setItems(templateList);
        templateView.refresh();
    }

    private void getTemplateBtnEvent() {
        logger.debug("Template btn event.");
        getHidePopupEvent();
        GuiFxmlAndLanguageUtils.buildTemplateDia();
    }

    private void getTemplateLblEvent() {
        logger.debug("Template lbl event.");
        if (!templateBtn.isDisable()) {
            if (!templatePopup.isShowing()) {
                setListViewSize(templateView, templateList);

                Double preHeight = templateView.getPrefHeight();
                if (preHeight >= MAX_HEIGHT) {
                    preHeight = MAX_HEIGHT;
                }
                double screenX = templateBtn.getScene().getWindow().getX() + templateBtn.getScene().getX() + templateBtn.localToScene(0, 0).getX();
                double screenY = templateBtn.getScene().getWindow().getY() + templateBtn.getScene().getY() + templateBtn.localToScene(0, 0).getY() - preHeight - 5;
                templatePopup.show(templateBtn, screenX, screenY);
            }
        }
    }

    /**
     * disable state bar False
     */
    public void disableStateBarFalse() {
        dataSourceBtn.setDisable(false);
        templateBtn.setDisable(false);
    }

    /**
     * disable state bar True
     */
    public void disableStateBarTrue() {
        dataSourceBtn.setDisable(true);
        templateBtn.setDisable(true);
    }

    private void buildDataSourceDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/data_source.fxml");
            root = fxmlLoader.load();
            DataSourceController controller = fxmlLoader.getController();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("dataSource", GuiFxmlAndLanguageUtils.getString(ResourceMassages.DATASOURCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setOnCloseRequest(controller.getEventHandler());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isLogin() {
        UserModel userModel = UserModel.getInstance();
        if (userModel != null && userModel.getUser() != null) {
           return true;
        } else {
           return false;
        }
    }
}

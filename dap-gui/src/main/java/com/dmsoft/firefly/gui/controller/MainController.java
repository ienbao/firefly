package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.component.ContentStackPane;
import com.dmsoft.firefly.gui.component.CustomerTooltip;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.ControlMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.controller.template.DataSourceController;
import com.dmsoft.firefly.gui.model.StateBarTemplateModel;
import com.dmsoft.firefly.gui.model.UserModel;
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
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.io.Resources.getResource;

/**
 * controller for main pane
 *
 * @author Julia
 */
public class MainController {

    public static final Double MAX_HEIGHT = 250.0;
    public static final Double MAX_WIDTH = 250.0;
    public static final Double MIN_WIDTH = 160.0;
    private final Logger logger = LoggerFactory.getLogger(MainController.class);
    @FXML
    private GridPane grpContent;

    @FXML
    private ToolBar tbaSystem;

    @FXML
    private GridPane stateBar;
    private ProgressBar progressBar;

    private Button dataSourceBtn;
    private Button templateBtn;

    private ScrollPane scrollPaneTooltip;
    private CustomerTooltip dataSourceTooltip;
    private ObservableList<String> dataSourceList = FXCollections.observableArrayList();

    private Popup templatePopup;
    private ListView<StateBarTemplateModel> templateView;
    private ObservableList<StateBarTemplateModel> templateList = FXCollections.observableArrayList();
    private AtomicBoolean isShow = new AtomicBoolean(false);

    private StackPane contentStackPane;
    private Map<String, TabPane> tabPaneMap = new LinkedHashMap<>();
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);

    @FXML
    private void initialize() {
        scrollPaneTooltip = new ScrollPane();
        dataSourceTooltip = new CustomerTooltip();
        contentStackPane = new ContentStackPane();
        grpContent.add(contentStackPane, 0, 1);
        grpContent.setDisable(true);
        this.initToolBar();
        this.initStateBar();
        this.updateMemoryState();
        if (isLogin()) {
            grpContent.setDisable(false);
            this.updateStateBarIcon();
            this.initTemplate();
            this.initTemplatePopup();
            this.initDataSource();
            this.initDataSourceTooltip();
            this.setActiveFirstTab(pc);
            this.initComponentEvent();
        }
    }

    private void initToolBar() {
        Set<String> names = pc.getAllMainBodyNames();
        names.forEach(name -> {
            Button btn = new Button(name);
            btn.setId(name);
            btn.setFocusTraversable(true);
            btn.setOnAction(event -> {
                if (!tabPaneMap.containsKey(name)) {
                    Pane pane = pc.getMainBodyPane(name).getNewPane();
                    pane.setId(name);
                    initMutilyTab(name, pane);
                } else {
                    contentStackPane.getChildren().forEach(node -> {
                        node.setVisible(false);
                        if (node.getId().equals(name)) {
                            node.setVisible(true);
                        }
                    });
                }
                setActiveBtnStyle(btn);
            });
            tbaSystem.getItems().add(btn);
        });
    }

    private void setActiveBtnStyle(Button btn) {
        btn.setStyle(" -fx-text-fill: #ffffff;-fx-background-color: #ea2028");
        for (Node node : tbaSystem.getItems()) {
            if (!node.getId().equals(btn.getId())) {
                node.setStyle(null);
            }
        }
    }

    private void setActiveFirstTab(PluginUIContext pc) {
        if (tbaSystem.getItems() != null && !tbaSystem.getItems().isEmpty()) {
            Button firstTabBtn = (Button) tbaSystem.getItems().get(0);
            setActiveBtnStyle(firstTabBtn);
            Pane pane = pc.getMainBodyPane(firstTabBtn.getId()).getNewPane();
            pane.setId(firstTabBtn.getId());
            initMutilyTab(firstTabBtn.getId(), pane);
        }
    }

    /**
     * method to reset main
     */
    public void resetMain() {
        grpContent.getChildren().remove(contentStackPane);
        contentStackPane.getChildren().clear();
        tabPaneMap.clear();
        tbaSystem.getItems().clear();
        stateBar.getChildren().clear();
        initialize();
    }

    private void initSinglelTab(String name, Pane pane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setId(name);
        Tab tab = new Tab();
        tab.setText(name + "_1");
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
        contentStackPane.getChildren().add(tabPane);
        tabPaneMap.put(name, tabPane);
        TabUtils.disableCloseTab(tabPane);
        TabUtils.tabSelectedListener(tab, tabPane);
    }

    private void initMutilyTab(String name, Pane pane) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setStyle("-fx-skin: 'com.dmsoft.firefly.gui.component.TabPaneSkin'");
        tabPane.setId(name);
        Tab tab = new Tab();
        tab.setText(name + "_1");
        tab.setContent(pane);
        tabPane.getTabs().add(tab);
        contentStackPane.getChildren().add(tabPane);
        tabPaneMap.put(name, tabPane);
        TabUtils.disableCloseTab(tabPane);
        TabUtils.tabSelectedListener(tab, tabPane);
    }

    private void initStateBar() {
        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();

        Label lblFile = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_FILE"));
        lblFile.getStyleClass().add("state-bar-lbl");
        Double length = Double.valueOf(lblFile.getText().getBytes().length);
        stateBar.addColumn(0, lblFile);
        if (LanguageType.EN.equals(languageType)) {
            stateBar.getColumnConstraints().get(0).setMaxWidth(85);
        } else {
            stateBar.getColumnConstraints().get(0).setMaxWidth(55);
        }


        ImageView imageView = new ImageView("/images/btn_edit_unable.png");
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        dataSourceBtn = new Button("--", imageView);
        dataSourceBtn.setContentDisplay(ContentDisplay.RIGHT);
        dataSourceBtn.getStyleClass().add("btn-icon-b");
        dataSourceBtn.setStyle("-fx-padding: 0 3 0 5");
        stateBar.addColumn(1, dataSourceBtn);
        ControlMap.addControl(CommonResourceMassages.PLATFORM_CONTROL_DATASOURCE_BTN, dataSourceBtn);

        Label lblAnalyze = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_ANALYZE"));
        lblAnalyze.getStyleClass().add("state-bar-lbl");
        Double length1 = Double.valueOf(lblAnalyze.getText().getBytes().length);
        stateBar.addColumn(2, lblAnalyze);
        if (LanguageType.EN.equals(languageType)) {
            stateBar.getColumnConstraints().get(2).setMaxWidth(120);
        } else {
            stateBar.getColumnConstraints().get(2).setMaxWidth(70);
        }

        ImageView imageView1 = new ImageView("/images/btn_template_unable.png");
        imageView1.setFitHeight(16);
        imageView1.setFitWidth(16);
        templateBtn = new Button("--", imageView1);
        templateBtn.setContentDisplay(ContentDisplay.RIGHT);
        templateBtn.getStyleClass().add("btn-icon-b");
        templateBtn.setStyle("-fx-padding: 0 3 0 5");
        stateBar.addColumn(3, templateBtn);
        ControlMap.addControl(CommonResourceMassages.PLATFORM_CONTROL_TEMPLATE_BTN, templateBtn);

        progressBar = new ProgressBar();
        progressBar.setPrefHeight(10);
        progressBar.setMaxHeight(10);
        progressBar.setMinHeight(10);
        progressBar.setPrefWidth(110);
        progressBar.setMaxWidth(110);
        progressBar.setMinWidth(110);
        progressBar.getStyleClass().setAll("progress-bar-lg-green");
        progressBar.setProgress(0);

        Label lblMemory = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_MEMORY"), progressBar);
        lblMemory.setPrefHeight(20);
        lblMemory.setMaxHeight(20);
        lblMemory.setMinHeight(20);
        lblMemory.setPrefWidth(179);
        lblMemory.setMaxWidth(179);
        lblMemory.setMinWidth(179);
        lblMemory.getStyleClass().add("state-bar-lbl");
        lblMemory.setStyle("-fx-border-width: 0 1 0 1; -fx-border-color: #dcdcdc;");
        lblMemory.setAlignment(Pos.BASELINE_LEFT);
        lblMemory.setContentDisplay(ContentDisplay.RIGHT);
        stateBar.addColumn(4, lblMemory);

        Label lblVersion = new Label(GuiFxmlAndLanguageUtils.getString("STATE_BAR_VERSION"));
        lblVersion.getStyleClass().add("state-bar-lbl");
        stateBar.addColumn(5, lblVersion);
    }

    private void initStateBarText(List<String> activeProjectNames, String activeTemplateName) {
        if (isLogin()) {
            if (activeProjectNames != null && !activeProjectNames.isEmpty()) {
                dataSourceBtn.setText(activeProjectNames.size() + " " + GuiFxmlAndLanguageUtils.getString("STATE_BAR_FILE_SELECTED"));
            } else {
                GuiFxmlAndLanguageUtils.buildSelectDataSource();
            }

            if (DAPStringUtils.isNotBlank(activeTemplateName)) {
                templateBtn.setText(activeTemplateName);
            }
        } else {
            dataSourceBtn.setText("");
            templateBtn.setText("");
        }
    }

    /**
     * method to update source text
     *
     * @param selectedFileNumber selected file numbers
     */
    public void updateDataSourceText(int selectedFileNumber) {
        dataSourceBtn.setText(selectedFileNumber + GuiFxmlAndLanguageUtils.getString("STATE_BAR_FILE_SELECTED"));
    }

    /**
     * method to update template name
     *
     * @param selectedTemplateName selected template name
     */
    public void updateTemplateText(String selectedTemplateName) {
        templateBtn.setText(selectedTemplateName);
    }

    /**
     * method to update stats bar icon
     */
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
        templateBtn.setOnMouseExited(event -> this.timerHidePopup());
        templateView.setOnMouseExited(event -> this.getHidePopupEvent());
        templateView.setOnMouseEntered(event -> {
            isShow.set(true);
        });
        progressBar.setOnMouseClicked(event -> this.getProgressEvent());
    }

    private void getProgressEvent() {
        WindowMessageController messageController = WindowMessageFactory.createWindowMessageHasOkAndCancel(GuiFxmlAndLanguageUtils.getString("GLOBAL_TITLE_MESSAGE"),
                GuiFxmlAndLanguageUtils.getString(GuiFxmlAndLanguageUtils.getString("GLOBAL_FREE_SYTEM_MEMORY")));
        messageController.addProcessMonitorListener(new WindowCustomListener() {
            @Override
            public boolean onShowCustomEvent() {
                return false;
            }

            @Override
            public boolean onCloseAndCancelCustomEvent() {
                return false;
            }

            @Override
            public boolean onOkCustomEvent() {
                Runtime.getRuntime().gc();
                progressBar.getStyleClass().setAll("progress-bar-md-green");
                return false;
            }
        });
    }

    private void getHidePopupEvent() {
        templatePopup.hide();
        isShow.set(false);
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
            dataSourceList.forEach(value -> {
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
            double preHeight = (20 * dataSourceList.size()) + 10;
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
        ImageView imageResetWhite = new ImageView(new Image("/images/icon_choose_one_white.png"));
        imageResetWhite.setFitHeight(16);
        imageResetWhite.setFitWidth(16);
        templateView.setItems(templateList);
        templateView.setCellFactory(e -> new ListCell<StateBarTemplateModel>() {
            @Override
            public void updateItem(StateBarTemplateModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox cell;
                    Label label = new Label(item.getTemplateName());
                    label.setPadding(new Insets(0, 0, 0, 5));
                    if (item.isIsChecked()) {
                        cell = new HBox(imageReset, label);
                    } else {
                        Label label1 = new Label("");
                        label1.setPrefWidth(16);
                        label1.setPrefHeight(16);
                        cell = new HBox(label1, label);
                    }
                    setGraphic(cell);
                    cell.setOnMouseEntered(event -> {
                        if (item.isIsChecked()) {
                            cell.getChildren().clear();
                            cell.getChildren().addAll(imageResetWhite, label);
                        }
                    });
                    cell.setOnMouseExited(event -> {
                        if (item.isIsChecked()) {
                            cell.getChildren().clear();
                            cell.getChildren().addAll(imageReset, label);
                        }
                    });
                    cell.setOnMouseClicked(event -> listCellClickEvent(item));
                }

            }
        });

        templateView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        if (templatePopup == null) {
            templatePopup = new Popup();
            templatePopup.getContent().add(templateView);
        } else {
            templatePopup.getContent().clear();
            templatePopup.getContent().add(templateView);
        }
    }

    private void listCellClickEvent(StateBarTemplateModel item) {
        if (!item.isIsChecked()) {
            WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel("Message", GuiFxmlAndLanguageUtils.getString("SWITCH_TEMPLATE_CONFIRM"));
            controller.addProcessMonitorListener(new WindowCustomListener() {
                @Override
                public boolean onShowCustomEvent() {
                    return false;
                }

                @Override
                public boolean onCloseAndCancelCustomEvent() {
                    templatePopup.hide();
                    return false;
                }

                @Override
                public boolean onOkCustomEvent() {
                    templateList.forEach(template -> {
                        if (template.getTemplateName().equals(item.getTemplateName())) {
                            template.setIsChecked(true);
                        } else {
                            template.setIsChecked(false);
                        }
                    });
                    item.setIsChecked(true);
                    templateBtn.setText(item.getTemplateName());
                    envService.setActivatedTemplate(item.getTemplateName());
                    List<String> projectName = envService.findActivatedProjectName();
                    Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(projectName);
                    LinkedHashMap<String, TestItemWithTypeDto> itemWithTypeDtoMap = templateService.assembleTemplate(testItemDtoMap, item.getTemplateName());
                    envService.setTestItems(itemWithTypeDtoMap);
                    resetMain();
                    return false;
                }
            });
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

    /**
     * method to init data source
     */
    public void initDataSource() {
        List<String> projectName = envService.findActivatedProjectName();
        TemplateSettingDto activeTemplate = envService.findActivatedTemplate();
        if (projectName == null) {
            projectName = Lists.newArrayList();
        }
        String activeTemplateName = "";
        if (activeTemplate != null) {
            activeTemplateName = activeTemplate.getName();
        }
        dataSourceList = FXCollections.observableArrayList(projectName);
        initStateBarText(projectName, activeTemplateName);
    }

    /**
     * method to init template
     */
    public void initTemplate() {
        List<StateBarTemplateModel> stateBarTemplateModels = Lists.newLinkedList();
        List<TemplateSettingDto> allTemplates = templateService.findAllTemplate();
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        if (allTemplates != null) {
            allTemplates.forEach(dto -> {
                StateBarTemplateModel stateBarTemplateModel = new StateBarTemplateModel(dto.getName(), false);
                if (templateSettingDto != null && templateSettingDto.getName().equals(stateBarTemplateModel.getTemplateName())) {
                    stateBarTemplateModel.setIsChecked(true);
                }
                stateBarTemplateModels.add(stateBarTemplateModel);
            });
        }
        templateList = FXCollections.observableArrayList(stateBarTemplateModels);
    }

    /**
     * method to refresh data source
     *
     * @param dataSourceList observable string list
     */
    public void refreshDataSource(ObservableList<String> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    /**
     * method to refresh template
     *
     * @param templateList template list
     */
    public void refreshTemplate(ObservableList<StateBarTemplateModel> templateList) {
        this.templateList = templateList;
        templateView.setItems(templateList);
        templateView.refresh();
        updateTemplateText(envService.findActivatedTemplate().getName());
    }

    private void getTemplateBtnEvent() {
        logger.debug("Template btn event.");
        getHidePopupEvent();
        GuiFxmlAndLanguageUtils.buildTemplateDialog();
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
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("dataSource", GuiFxmlAndLanguageUtils.getString(ResourceMassages.DATA_SOURCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setOnCloseRequest(controller.getEventHandler());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get memory state
     */
    public void updateMemoryState() {
        boolean flag = true;
        final double memoryLimit = 2.0 * Math.pow(2, 20);
        final double warningPercent = 70;

        Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        while (flag) {
                            try {
                                Thread.sleep(1000);
                                int progress = (int) (getHeapUsed() / memoryLimit * 100);
                                updateProgress(progress, 100);
                                Platform.runLater(() -> {
                                    try {
                                        if (progress < warningPercent) {
                                            progressBar.getStyleClass().setAll("progress-bar-md-green");
                                        } else {
                                            progressBar.getStyleClass().setAll("progress-bar-md-red");
                                        }
                                        TooltipUtil.installNormalTooltip(progressBar, progress + "%");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                };
            }
        };

        progressBar.progressProperty().bind(service.progressProperty());
        service.start();
    }

    private double getHeapUsed() throws Exception {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        return heap.getUsed() / Math.pow(2, 10);
    }

    private boolean isLogin() {
        UserModel userModel = UserModel.getInstance();
        return (userModel != null && userModel.getUser() != null);
    }

    private void timerHidePopup() {
        if (templatePopup.isShowing()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!isShow.get()) {
                                templatePopup.hide();
                            }
                            timer.cancel();
                        }

                    });
                }
            }, 500);
        }
    }


    public Button getDataSourceBtn() {
        return dataSourceBtn;
    }

    public Button getTemplateBtn() {
        return templateBtn;
    }
}

package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.*;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.model.ItemTableModel;
import com.dmsoft.firefly.plugin.yield.service.impl.YieldLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.TimePatternDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

public class YieldItemController implements Initializable {
    private static final String STICKY_ON_TOP_CODE = "stick_on_top";
    private static final Double D20 = 20.0d;
    private final Logger logger = LoggerFactory.getLogger(YieldItemController.class);
    @FXML
    private TextFieldFilter itemFilter;
    @FXML
    private Button analysisBtn;
    @FXML
    private Button importBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Tab itemTab;
    @FXML
    private Tab configTab;
    @FXML
    private Tab timeTab;
//    @FXML
//    private Label helpLabel;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, TestItemWithTypeDto> item;
    @FXML
    private TableView<ItemTableModel> itemTable;

    @FXML
    private SplitPane split;
    private SearchTab searchTab;
    private CheckBox box;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);
    private YieldMainController yieldMainController;
    private ViewDataController viewDataController;
    private ContextMenu pop;
    private boolean isFilterUslOrLsl = false;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private YieldLeftConfigServiceImpl leftConfigService = new YieldLeftConfigServiceImpl();
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    // cached items for user preference
    private List<String> stickyOnTopItems = Lists.newArrayList();

    private List<String> originalItems = Lists.newArrayList();
    private SearchDataFrame dataFrame;

    @FXML
    private CheckBox enabledTimerCheckBox;
    @FXML
    private ComboBox<String> timeComboBox;
    @FXML
    private ComboBox<String> configComboBox;
    private boolean isTimer;
    private boolean startTimer;

    private Timer timer;

    /**
     * init main controller
     *
//     * @param spcMainController main controller
     */
    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBtnIcon();
        searchTab = new SearchTab();
        split.getItems().add(searchTab);
        itemFilter.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (isFilterUslOrLsl) {
                filteredList.setPredicate(this::isFilterAndHasUslOrLsl);
            } else {
                filteredList.setPredicate(this::isFilterAndAll);
            }
        });

        // test item table init
        itemTable.setOnMouseEntered(event -> {
            itemTable.focusModelProperty();
        });
        if (itemTable.getSkin() != null) {
            TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) itemTable.getSkin(), itemTable);
        } else {
            itemTable.skinProperty().addListener((ov, s1, s2) -> {
                TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, itemTable);
            });
        }
        itemTable.setRowFactory(tv -> {
            TableRow<ItemTableModel> tableRow = new TableRow<>();
            tableRow.setContextMenu(createTableRightMenu());
            return tableRow;
        });
        // select column in test item table
        box = new CheckBox();
        box.setOnAction(event -> {
            if (itemTable != null && itemTable.getItems() != null) {
                for (ItemTableModel model : itemTable.getItems()) {
                    if (isFilterUslOrLsl) {
                        if (StringUtils.isNotEmpty(model.getItemDto().getLsl()) || StringUtils.isNotEmpty(model.getItemDto().getUsl())) {
                            model.getSelector().setValue(box.isSelected());
                        }
                    } else {
                        model.getSelector().setValue(box.isSelected());
                    }
                }
            }
        });
        select.setGraphic(box);
        select.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        select.setCellFactory(new Callback<TableColumn<ItemTableModel, CheckBox>, TableCell<ItemTableModel, CheckBox>>() {
            @Override
            public TableCell<ItemTableModel, CheckBox> call(TableColumn<ItemTableModel, CheckBox> param) {
                return new TableCell<ItemTableModel, CheckBox>() {
                    @Override
                    protected void updateItem(CheckBox item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(null);
                        if (!isEmpty()) {
                            if (getTableRow() != null && getIndex() > -1) {
                                if (getTableView().getItems().get(getIndex()).getOnTop()) {
                                    this.setStyle("-fx-background-color: #dff0cf");
                                }
                            }
                        }
                        if (item == null) {
                            super.setGraphic(null);
                        } else {
                            super.setGraphic(item);
                        }
                    }
                };
            }
        });
        // test item column in test item table
        Button is = new Button();
        is.setPrefSize(22, 22);
        is.setMinSize(22, 22);
        is.setMaxSize(22, 22);
        is.setOnMousePressed(event -> createPopMenu(is, event));
        is.getStyleClass().add("filter-normal");


        item.setText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEM));
        item.setGraphic(is);
        item.getStyleClass().add("filter-header");
        item.setCellValueFactory(cellData -> cellData.getValue().itemDtoProperty());
        item.setCellFactory(new Callback<TableColumn<ItemTableModel, TestItemWithTypeDto>, TableCell<ItemTableModel, TestItemWithTypeDto>>() {
            public TableCell<ItemTableModel, TestItemWithTypeDto> call(TableColumn<ItemTableModel, TestItemWithTypeDto> param) {
                return new TableCell<ItemTableModel, TestItemWithTypeDto>() {
                    @Override
                    public void updateItem(TestItemWithTypeDto item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle(null);
                        if (!isEmpty()) {
                            if (getTableRow() != null && getIndex() > -1) {
                                if (item.getTestItemType().equals(TestItemType.ATTRIBUTE) && getTableView().getItems().get(getIndex()).getOnTop()) {
                                    this.setStyle("-fx-text-fill: #009bff; -fx-background-color: #dff0cf");
                                } else if (item.getTestItemType().equals(TestItemType.ATTRIBUTE)) {
                                    this.setStyle("-fx-text-fill: #009bff");
                                } else if (getTableView().getItems().get(getIndex()).getOnTop()) {
                                    this.setStyle("-fx-background-color: #dff0cf");
                                }
                            }
                            setText(item.getTestItemName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        item.widthProperty().addListener((ov, w1, w2) -> {
            Platform.runLater(() -> is.relocate(w2.doubleValue() - 21, 0));
        });
        item.setComparator((o1, o2) -> {
            boolean o1OnTop = stickyOnTopItems.contains(o1.getTestItemName());
            boolean o2OnTop = stickyOnTopItems.contains(o2.getTestItemName());
            if (o1OnTop == o2OnTop) {
                return -o2.getTestItemName().compareTo(o1.getTestItemName());
            } else if (o1OnTop) {
                return -1;
            } else {
                return 1;
            }
        });
        item.sortTypeProperty().addListener((ov, sort1, sort2) -> {
            if (sort2.equals(TableColumn.SortType.DESCENDING)) {
                item.setComparator((o1, o2) -> {
                    boolean o1OnTop = stickyOnTopItems.contains(o1.getTestItemName());
                    boolean o2OnTop = stickyOnTopItems.contains(o2.getTestItemName());
                    if (o1OnTop == o2OnTop) {
                        return -o2.getTestItemName().compareTo(o1.getTestItemName());
                    } else if (o1OnTop) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
            } else {
                item.setComparator((o1, o2) -> {
                    boolean o1OnTop = stickyOnTopItems.contains(o1.getTestItemName());
                    boolean o2OnTop = stickyOnTopItems.contains(o2.getTestItemName());
                    if (o1OnTop == o2OnTop) {
                        return -o2.getTestItemName().compareTo(o1.getTestItemName());
                    } else if (o1OnTop) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
            }
        });
        YieldValidateUtil.validateYield(configComboBox);
        initComponentEvent();
        initItemData();
        initYieldConfig();
        initSpcTimer();
    }

    /**
     * init spc timer tab
     */
    public void initSpcTimer() {
//        isTimer = false;
//        startTimer = false;
//        enabledTimerCheckBox.setSelected(false);
//        List<String> timerList = leftConfigService.findYieldTimerTime();
//
//        if (timerList == null) {
//            return;
//        }
//        ObservableList<String> showTimeList = FXCollections.observableArrayList();
//        for (String time : timerList) {
//            showTimeList.add(time + YieldFxmlAndLanguageUtils.getString(ResourceMassages.TIMER_MIN));
//        }
//        timeComboBox.setItems(showTimeList);
//        if (showTimeList.size() > 0) {
//            timeComboBox.setValue(showTimeList.get(0));
//        }
    }

    /**
     * method to get current config
     *
     * @return yield left config dto
     */
    public YieldLeftConfigDto getCurrentConfigData() {
        YieldLeftConfigDto leftConfigDto = new YieldLeftConfigDto();
        leftConfigDto.setItems(getSelectedItem());
        leftConfigDto.setBasicSearchs(searchTab.getBasicSearch());
        if (searchTab.getAdvanceText().getText() != null) {
            leftConfigDto.setAdvanceSearch(searchTab.getAdvanceText().getText());
        }
        leftConfigDto.setPrimaryKey(configComboBox.getValue());
//        leftConfigDto.setTopN();
        if (searchTab.getGroup1().getValue() != null) {
            leftConfigDto.setAutoGroup1(searchTab.getGroup1().getValue());
        }
        if (searchTab.getGroup2().getValue() != null) {
            leftConfigDto.setAutoGroup2(searchTab.getGroup2().getValue());
        }
        return leftConfigDto;
    }

    private void initBtnIcon() {
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        TooltipUtil.installNormalTooltip(analysisBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.ANALYSIS));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        TooltipUtil.installNormalTooltip(importBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.IMPORT_CONFIG));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
        TooltipUtil.installNormalTooltip(exportBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_CONFIG));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        itemTab.setStyle("-fx-padding: 0 5 0 5");
        itemTab.setTooltip(new Tooltip(YieldFxmlAndLanguageUtils.getString("YIELD_TEST_ITEM")));

        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        configTab.setStyle("-fx-padding: 0 5 0 5");
        configTab.setTooltip(new Tooltip(YieldFxmlAndLanguageUtils.getString("YIELD_CONFIG")));

        timeTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_timer_normal.png")));
        timeTab.setStyle("-fx-padding: 0 5 0 5");
        timeTab.setTooltip(new Tooltip(YieldFxmlAndLanguageUtils.getString("YIELD_TIMER_SETTING")));
    }
    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            RadioMenuItem all = new RadioMenuItem(YieldFxmlAndLanguageUtils.getString(ResourceMassages.ALL_TEST_ITEMS));
            all.setOnAction(event -> {
                filteredList.setPredicate(this::isFilterAndAll);
                is.getStyleClass().remove("filter-active");
                is.getStyleClass().add("filter-normal");
                is.setGraphic(null);
                isFilterUslOrLsl = false;
            });
            RadioMenuItem show = new RadioMenuItem(YieldFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEMS_WITH_USL_LSL));
            show.setOnAction(event -> {
                filteredList.setPredicate(this::isFilterAndHasUslOrLsl);
                is.getStyleClass().remove("filter-normal");
                is.getStyleClass().add("filter-active");
//                is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_filter_normal.png")));
                isFilterUslOrLsl = true;
            });
            all.setSelected(true);
            ToggleGroup toggleGroup = new ToggleGroup();
            all.setToggleGroup(toggleGroup);
            show.setToggleGroup(toggleGroup);
            pop.getItems().addAll(all, show);
        }
        Bounds bounds = is.localToScreen(is.getBoundsInLocal());
        pop.show(is, bounds.getMinX(), bounds.getMinY() + 22);
        return pop;
    }
    private ContextMenu createTableRightMenu() {
        MenuItem top = new MenuItem(YieldFxmlAndLanguageUtils.getString(ResourceMassages.STICKY_ON_TOP));
        ContextMenu right = new ContextMenu() {
            @Override
            public void show(Node anchor, double screenX, double screenY) {
                if (((TableRow) anchor).getItem() == null) {
                    return;
                }
                if (itemTable.getSelectionModel().getSelectedItem() != null && itemTable.getSelectionModel().getSelectedItem().getOnTop()) {
                    top.setText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.REMOVE_FROM_TOP));
                } else {
                    top.setText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.STICKY_ON_TOP));
                }
                super.show(anchor, screenX, screenY);
            }
        };

        top.setOnAction(event -> {
            ItemTableModel selectedItems = itemTable.getSelectionModel().getSelectedItem();
            boolean former = selectedItems.getOnTop();
            String itemName = selectedItems.getItem();
            if (former) {
                stickyOnTopItems.remove(itemName);
                items.remove(selectedItems);
                int newSite = findNewSite(items, selectedItems);
                items.add(newSite, selectedItems);
            } else {
                stickyOnTopItems.add(itemName);
                items.remove(selectedItems);
                items.add(0, selectedItems);
            }
            UserPreferenceDto<String> preferenceDto = new UserPreferenceDto<>();
            preferenceDto.setCode(STICKY_ON_TOP_CODE);
            preferenceDto.setUserName(envService.getUserName());
            preferenceDto.setValue(mapper.toJson(stickyOnTopItems));
            userPreferenceService.updatePreference(preferenceDto);
            selectedItems.setOnTop(!former);
            itemTable.refresh();
        });
        MenuItem setting = new MenuItem(YieldFxmlAndLanguageUtils.getString(ResourceMassages.SPECIFICATION_SETTING));
        setting.setOnAction(event -> RuntimeContext.getBean(EventContext.class).pushEvent(new PlatformEvent(null, "Template_Show")));
        right.getItems().addAll(top, setting);
        return right;
    }

    private void initComponentEvent() {
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
        importBtn.setOnAction(event -> importLeftConfig());
        exportBtn.setOnAction(event -> exportLeftConfig());
        enabledTimerCheckBox.selectedProperty().addListener((ov, v1, v2) -> {
            isTimer = v2;
            if (!isTimer && startTimer) {
                startTimer = false;
            }
            updateAnalysisBtnTimer();
        });
    }

    @SuppressWarnings("unchecked")
    private void initItemData() {
        items.clear();
        stickyOnTopItems.clear();
        String s = userPreferenceService.findPreferenceByUserId(STICKY_ON_TOP_CODE, envService.getUserName());
        if (DAPStringUtils.isNotBlank(s)) {
            List<String> onTopItems = mapper.fromJson(s, mapper.buildCollectionType(List.class, String.class));
            stickyOnTopItems.addAll(onTopItems);
        }
        originalItems.clear();
        List<TestItemWithTypeDto> itemDtos = envService.findTestItems();
        if (itemDtos != null) {
            List<ItemTableModel> modelList = Lists.newArrayList();
            for (TestItemWithTypeDto dto : itemDtos) {
                ItemTableModel tableModel = new ItemTableModel(dto);

                originalItems.add(dto.getTestItemName());
                if (stickyOnTopItems.contains(dto.getTestItemName())) {
                    tableModel.setOnTop(true);
                }
                modelList.add(tableModel);
            }
            modelList.sort((o1, o2) -> {
                if (o1.getOnTop() == o2.getOnTop()) {
                    return originalItems.indexOf(o1.getItem()) - originalItems.indexOf(o2.getItem());
                } else if (o1.getOnTop()) {
                    return -1;
                } else if (o2.getOnTop()) {
                    return 1;
                }
                return 0;
            });
            items.addAll(modelList);
            itemTable.setItems(personSortedList);
            personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
        }
    }

    @SuppressWarnings("unchecked")
    private void getAnalysisBtnEvent() {
        if (isTimer) {

        } else {
            if (validAnalysisCondition()) {
                this.normalAnalysisEvent(isTimer);
            }
        }
    }

//    private Timer startTimerAnalysis() {
//
//        String currentRefreshTime = (String) timeComboBox.getValue();
//        String time = currentRefreshTime.replace(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIMER_MIN), "");
//        if (!DAPStringUtils.isNumeric(time)) {
//            return null;
//        }
//        Double intervalTime = Double.valueOf(time) * 60000;
//        Timer timer1 = new Timer();
//        timer1.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> autoRefreshAnalysis());
//            }
//        }, intervalTime.longValue(), intervalTime.longValue());
//        return timer1;
//    }

//    @SuppressWarnings("unchecked")
//    private void autoRefreshAnalysis() {
//        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
//        spcMainController.clearAnalysisSubShowData();
//        List<String> projectNameList = envService.findActivatedProjectName();
//        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
//        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
//        SpcAnalysisConfigDto spcAnalysisConfigDto = this.buildSpcAnalysisConfigData();
//
//        List<SearchConditionDto> chartSearchConditionDtoList = spcMainController.getSelectSearchCondition();
//        this.updateSpcConfigPreference(spcAnalysisConfigDto);
//        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
//        windowProgressTipController.setAutoHide(false);
//        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
//        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
//        context.put(ParamKeys.STATISTICAL_SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
//        context.put(ParamKeys.CHART_SEARCH_CONDITION_DTO_LIST, chartSearchConditionDtoList);
//        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
//        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
//        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
//        windowProgressTipController.getCancelBtn().setOnAction(event -> {
//            windowProgressTipController.setCancelingText();
//            context.interruptBeforeNextJobHandler();
//            if (context.isError() || context.getCurrentProgress() == 1.0) {
//                windowProgressTipController.closeDialog();
//            }
//        });
//        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
//        WindowPane windowPane = null;
//        if (stage1.getScene().getRoot() instanceof WindowPane) {
//            windowPane = (WindowPane) stage1.getScene().getRoot();
//        }
//        if (windowPane != null) {
//            windowPane.getCloseBtn().setOnAction(event -> {
//                windowProgressTipController.setCancelingText();
//                context.interruptBeforeNextJobHandler();
//            });
//        }
//        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_TIMER_REFRESH_ANALYSIS_JOB_PIPELINE);
//        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
//            @Override
//            public void doJob(JobContext context) {
//                spcMainController.setSpcSettingDto(context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class));
//                spcMainController.setAnalysisConfigDto(spcAnalysisConfigDto);
//                spcMainController.setInitSearchConditionDtoList(searchConditionDtoList);
//                SpcRefreshJudgeUtil.newInstance().setViewDataSelectRowKeyListCache(null);
//                SpcRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
//                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.STATISTICAL_ANALYSIS_RESULT);
//                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
//                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());
//                spcMainController.timerRefreshStatisticalResultData(spcStatisticalResultAlarmDtoList);
//
//                SearchDataFrame searchDataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
//                spcMainController.setDataFrame(searchDataFrame);
//
//                //set chart data
//                List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) context.get(ParamKeys.CHART_ANALYSIS_RESULT);
//                if (spcChartDtoList != null && spcChartDtoList.size() != 0) {
//                    spcMainController.setSpcChartData(spcChartDtoList);
//                }
//                //set view data
//                spcMainController.setTimerViewData(chartSearchConditionDtoList, searchConditionDtoList);
//                windowProgressTipController.closeDialog();
//                logger.info("Spc auto refresh finish.");
//            }
//        });
//        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
//            @Override
//            public void doJob(JobContext context) {
//                logger.error(context.getError().getMessage());
//                windowProgressTipController.updateFailProgress(context.getError().toString());
//            }
//        });
//        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
//            @Override
//            public void doJob(JobContext context) {
//                windowProgressTipController.closeDialog();
//            }
//        });
//        logger.info("Start auto refresh Spc.");
//        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
//    }
//
    private boolean validAnalysisCondition() {
        if (isConfigError()) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    YieldFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    YieldFxmlAndLanguageUtils.getString(ResourceMassages.YIELD_CONFIG_ERROR_MESSAGE));
            return false;
        }
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        if (selectedItemDto.size() == 0) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    YieldFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    YieldFxmlAndLanguageUtils.getString(ResourceMassages.UI_YIELD_ANALYSIS_ITEM_EMPTY));
            return false;
        }
        return searchTab.verifySearchTextArea();
    }

    @SuppressWarnings("unchecked")
    private void normalAnalysisEvent(boolean isTimer) {
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        yieldMainController.clearAnalysisData();
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
        YieldAnalysisConfigDto yieldAnalysisConfigDto = this.buildYieldAnalysisConfigData();
        this.updateYieldConfigPreference(yieldAnalysisConfigDto);
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
            if (context.isError() || context.getCurrentProgress() == 1.0) {
                windowProgressTipController.closeDialog();
            }
        });
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> {
                context.interruptBeforeNextJobHandler();
                windowProgressTipController.setCancelingText();
            });
        }
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                yieldMainController.setYieldSettingDto(context.getParam(ParamKeys.YIELD_SETTING_DTO, YieldSettingDto.class));
                yieldMainController.setAnalysisConfigDto(yieldAnalysisConfigDto);
                yieldMainController.setInitSearchConditionDtoList(searchConditionDtoList);
                YieldRefreshJudgeUtil.newInstance().setOverViewSelectRowKeyListCache(null);
//                YieldRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
                List<YieldOverviewResultAlarmDto> YieldOverviewAlarmDtoList = (List<YieldOverviewResultAlarmDto>) context.get(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST);
                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
//                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());
                yieldMainController.setOverviewResultData(YieldOverviewAlarmDtoList, null, isTimer);
                yieldMainController.setDataFrame(context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class));
                windowProgressTipController.closeDialog();
                yieldMainController.setDisable(false);
                logger.info("Yield analysis finish.");
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().getMessage());
                windowProgressTipController.updateFailProgress(context.getError().toString());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        logger.info("Start analysis Yield.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }

    @SuppressWarnings("unchecked")
    public void normalViewDataEvent() {
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        yieldMainController.clearAnalysisData();
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
        YieldAnalysisConfigDto yieldAnalysisConfigDto = this.buildYieldAnalysisConfigData();
        this.updateYieldConfigPreference(yieldAnalysisConfigDto);

        SearchDataFrame viewDataFrame = buildSubSearchDataFrame(searchConditionDtoList);

        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
//        SearchDataFrame subDataFrame = buildSubSearchDataFrame(rowKeyList, searchConditionDtoList);
//        context.put(ParamKeys.SEARCH_DATA_FRAME, subDataFrame);
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.YIELD_ANALYSIS_CONFIG_DTO, yieldAnalysisConfigDto);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        windowProgressTipController.getCancelBtn().setOnAction(event -> {
            windowProgressTipController.setCancelingText();
            context.interruptBeforeNextJobHandler();
            if (context.isError() || context.getCurrentProgress() == 1.0) {
                windowProgressTipController.closeDialog();
            }
        });
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> {
                context.interruptBeforeNextJobHandler();
                windowProgressTipController.setCancelingText();
            });
        }
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.YIELD_OVER_VIEW_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
//                yieldMainController.setSpcSettingDto(context.getParam(ParamKeys.YIELD_SETTING_DTO, YieldSettingDto.class));
//                yieldMainController.setAnalysisConfigDto(yieldAnalysisConfigDto);
//                yieldMainController.setInitSearchConditionDtoList(searchConditionDtoList);
//                YieldRefreshJudgeUtil.newInstance().setOverViewSelectRowKeyListCache(null);
////                YieldRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
                List<YieldViewDataResultDto> YieldViewDataDtoList = (List<YieldViewDataResultDto>) context.get(ParamKeys.YIELD_RESULT_DTO_LIST);
//                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
////                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());

//                viewDataController.setViewData(viewDataFrame, subDataFrame.getAllRowKeys(), YieldViewDataDtoList, yieldItemController.isTimer());


//                yieldMainController.setDataFrame(context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class));
//                windowProgressTipController.closeDialog();
                yieldMainController.setDisable(false);
                logger.info("Yield analysis finish.");
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                logger.error(context.getError().getMessage());
                windowProgressTipController.updateFailProgress(context.getError().toString());
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        logger.info("Start analysis Yield.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }


    private List<String> getSelectedItem() {
        List<String> selectItems = Lists.newArrayList();
        if (items != null && !items.isEmpty()) {
            for (ItemTableModel model : items) {
                if (model.getSelector().isSelected()) {
                    if (isFilterUslOrLsl) {
                        if (StringUtils.isNotEmpty(model.getItemDto().getLsl()) || StringUtils.isNotEmpty(model.getItemDto().getUsl())) {
                            selectItems.add(model.getItem());
                        }
                    } else {
                        selectItems.add(model.getItem());
                    }
                }
            }
        }

        if (itemTable.lookup(".ascending-label") != null) {
            DAPStringUtils.sortListString(selectItems, false);
        } else if (itemTable.lookup(".descending-label") != null) {
            DAPStringUtils.sortListString(selectItems, true);
        }
        List<String> selectTestItemsResult = Lists.newLinkedList();
        if (stickyOnTopItems != null && !stickyOnTopItems.isEmpty()) {
            selectItems.forEach(selectedItem -> {
                if (stickyOnTopItems.contains(selectedItem)) {
                    selectTestItemsResult.add(selectedItem);
                }
            });
        }

        selectItems.forEach(selectedItem -> {
            if (!selectTestItemsResult.contains(selectedItem)) {
                selectTestItemsResult.add(selectedItem);
            }
        });
        return selectTestItemsResult;
    }

    private SearchDataFrame buildSubSearchDataFrame(List<SearchConditionDto> searchConditionDtoList) {
        if (dataFrame == null || searchConditionDtoList == null) {
            return null;
        }
        List<String> testItemNameList = Lists.newArrayList();
        List<String> searchCondition = Lists.newArrayList();
        List<String> timeKeys = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
        String timePattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        FilterUtils filterUtils = new FilterUtils(timeKeys, timePattern);
        for (SearchConditionDto searchConditionDto : searchConditionDtoList) {
            if (!testItemNameList.contains(searchConditionDto.getItemName())) {
                testItemNameList.add(searchConditionDto.getItemName());
            }
            String condition = searchConditionDto.getCondition();
            Set<String> conditionTestItemSet = filterUtils.parseItemNameFromConditions(condition);
            for (String conditionTestItem : conditionTestItemSet) {
                if (!testItemNameList.contains(conditionTestItem)) {
                    testItemNameList.add(conditionTestItem);
                }
            }

            if (!searchCondition.contains(condition)) {
                searchCondition.add(condition);
            }
        }
        return dataFrame.subDataFrame(dataFrame.getSearchRowKey(searchCondition), testItemNameList);
    }

    private List<TestItemWithTypeDto> initSelectedItemDto() {
        List<TestItemWithTypeDto> selectTestItemDtos = Lists.newLinkedList();
        if (items != null && !items.isEmpty()) {
            for (ItemTableModel model : items) {
                if (model.getSelector().isSelected()) {
                    if (isFilterUslOrLsl) {
                        if (StringUtils.isNotEmpty(model.getItemDto().getLsl()) || StringUtils.isNotEmpty(model.getItemDto().getUsl())) {
                            selectTestItemDtos.add(model.getItemDto());
                        }
                    } else {
                        selectTestItemDtos.add(model.getItemDto());
                    }
                }
            }
        }
        if (itemTable.lookup(".ascending-label") != null) {
            this.sortTestItemWithTypeDto(selectTestItemDtos, false);
        } else if (itemTable.lookup(".descending-label") != null) {
            this.sortTestItemWithTypeDto(selectTestItemDtos, true);
        }
        List<TestItemWithTypeDto> selectTestItemDtosResult = Lists.newLinkedList();
        if (stickyOnTopItems != null && !stickyOnTopItems.isEmpty()) {
            selectTestItemDtos.forEach(selectTestItemDto -> {
                if (stickyOnTopItems.contains(selectTestItemDto.getTestItemName())) {
                    selectTestItemDtosResult.add(selectTestItemDto);
                }
            });
        }

        selectTestItemDtos.forEach(selectTestItemDto -> {
            if (!selectTestItemDtosResult.contains(selectTestItemDto)) {
                selectTestItemDtosResult.add(selectTestItemDto);
            }
        });
        return selectTestItemDtosResult;
    }
    private void initYieldConfig() {
       YieldAnalysisConfigDto yieldAnalysisConfigDto = this.getYieldConfigPreference();
        if (yieldAnalysisConfigDto == null) {
            yieldAnalysisConfigDto = new YieldAnalysisConfigDto();
            yieldAnalysisConfigDto.setPrimaryKey("");
//            yieldAnalysisConfigDto.setTopN(null);
            this.updateYieldConfigPreference(yieldAnalysisConfigDto);
        }
        ObservableList<String> primaryKeyList = FXCollections.observableArrayList();
        primaryKeyList.add("");
        for (String item : originalItems) {
            primaryKeyList.add(item);
        }
        configComboBox.setItems(primaryKeyList);
        if (primaryKeyList.size() > 0) {
            configComboBox.setValue(yieldAnalysisConfigDto.getPrimaryKey());
//            set TopN
        }


    }


    private boolean isConfigError() {
        if (null==configComboBox.getValue()&&configComboBox.getValue().equals("")) {
            return true;
        }
        return false;
    }

    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(YieldFxmlAndLanguageUtils.getString(ResourceMassages.YIELD_CONFIG_IMPORT));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(StageMap.getStage(ResourceMassages.PLATFORM_STAGE_MAIN));

        if (file != null) {
            YieldLeftConfigDto yieldLeftConfigDto = leftConfigService.importSpcConfig(file);
            if (yieldLeftConfigDto != null) {
                clearLeftConfig();
                if (yieldLeftConfigDto.getItems() != null && yieldLeftConfigDto.getItems().size() > 0) {
                    items.forEach(testItem -> {
                        if (yieldLeftConfigDto.getItems().contains(testItem.getItem())) {
                            testItem.getSelector().setValue(true);
                        }
                    });
                }
                if (yieldLeftConfigDto.getBasicSearchs() != null && yieldLeftConfigDto.getBasicSearchs().size() > 0) {
                    searchTab.setBasicSearch(yieldLeftConfigDto.getBasicSearchs());
                }
                configComboBox.setValue(yieldLeftConfigDto.getPrimaryKey());
//TODO set topN
                searchTab.getAdvanceText().setText(yieldLeftConfigDto.getAdvanceSearch());
                searchTab.getGroup1().setValue(yieldLeftConfigDto.getAutoGroup1());
                searchTab.getGroup2().setValue(yieldLeftConfigDto.getAutoGroup2());
            } else {
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        YieldFxmlAndLanguageUtils.getString("IMPORT_EXCEPTION"));
            }
//
        }
    }

    private void exportLeftConfig() {
        YieldLeftConfigDto leftConfigDto = this.getCurrentConfigData();

        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(YieldFxmlAndLanguageUtils.getString(ResourceMassages.YIELD_CONFIG_EXPORT));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showSaveDialog(StageMap.getStage(ResourceMassages.PLATFORM_STAGE_MAIN));
//
        if (file != null) {
            leftConfigService.exportYieldConfig(leftConfigDto, file);
        }
    }

    private void clearLeftConfig() {
        box.setSelected(false);
        for (ItemTableModel model : items) {
            model.getSelector().setValue(false);
        }
        searchTab.clearSearchTab();
    }

    private YieldAnalysisConfigDto buildYieldAnalysisConfigData() {
        YieldAnalysisConfigDto yieldAnalysisConfigDto = new YieldAnalysisConfigDto();
        yieldAnalysisConfigDto.setPrimaryKey(configComboBox.getValue());
//        yieldAnalysisConfigDto.set;
        return yieldAnalysisConfigDto;
    }

    private List<SearchConditionDto> buildSearchConditionDataList(List<TestItemWithTypeDto> testItemWithTypeDtoList) {
        if (testItemWithTypeDtoList == null) {
            return null;
        }
        List<String> conditionList = searchTab.getSearch();
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        SearchConditionDto searchPrimaryKey = new SearchConditionDto();
        searchPrimaryKey.setItemName(configComboBox.getValue());
        searchConditionDtoList.add(searchPrimaryKey);
        int i = 0;
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            if (conditionList != null && conditionList.size() != 0) {
                for (String condition : conditionList) {
                    SearchConditionDto searchConditionDto = new SearchConditionDto();
                    searchConditionDto.setKey(ParamKeys.SPC_ANALYSIS_CONDITION_KEY + i);
                    searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                    searchConditionDto.setUslOrPass(testItemWithTypeDto.getUsl());
                    searchConditionDto.setLslOrFail(testItemWithTypeDto.getLsl());
                    searchConditionDto.setTestItemType(testItemWithTypeDto.getTestItemType());
                    searchConditionDto.setCondition(condition);
                    searchConditionDtoList.add(searchConditionDto);
                    i++;
                }
            } else {
                SearchConditionDto searchConditionDto = new SearchConditionDto();
                searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                searchConditionDto.setUslOrPass(testItemWithTypeDto.getLsl());
                searchConditionDto.setLslOrFail(testItemWithTypeDto.getUsl());
                searchConditionDto.setTestItemType(testItemWithTypeDto.getTestItemType());
                searchConditionDtoList.add(searchConditionDto);
                i++;
            }
        }
        return searchConditionDtoList;
    }
    private List<TestItemWithTypeDto> buildSelectTestItemWithTypeData(List<TestItemWithTypeDto> testItemWithTypeDtoList) {
        List<TestItemWithTypeDto> itemWithTypeDtoList = Lists.newArrayList();
        TestItemWithTypeDto searchPrimaryKey = new TestItemWithTypeDto();
        searchPrimaryKey.setTestItemName(configComboBox.getValue());
        itemWithTypeDtoList.add(searchPrimaryKey);
        itemWithTypeDtoList.addAll(testItemWithTypeDtoList);
        List<String> conditionTestItemList = getConditionTestItem();
        if (conditionTestItemList != null) {
            for (String testItem : conditionTestItemList) {
                TestItemWithTypeDto testItemWithTypeDto = envService.findTestItemNameByItemName(testItem);
                itemWithTypeDtoList.add(testItemWithTypeDto);
            }
        }
        return itemWithTypeDtoList;
    }

    private List<String> getConditionTestItem() {
        List<String> conditionList = searchTab.getSearch();
        List<String> testItemList = getSelectedItem();
        TimePatternDto timePatternDto = envService.findActivatedTemplate().getTimePatternDto();
        List<String> conditionTestItemList = Lists.newArrayList();
        for (String condition : conditionList) {
            Set<String> conditionTestItemSet = FilterUtils.parseItemNameFromConditions(condition);
            for (String conditionTestItem : conditionTestItemSet) {
                if (!testItemList.contains(conditionTestItem) && !conditionTestItemList.contains(conditionTestItem)) {
                    conditionTestItemList.add(conditionTestItem);
                }
            }
        }
        return conditionTestItemList;
    }

    private void updateYieldConfigPreference(YieldAnalysisConfigDto configDto) {
        UserPreferenceDto<YieldAnalysisConfigDto> userPreferenceDto = new UserPreferenceDto<>();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode("yield_config_preference");
        userPreferenceDto.setValue(configDto);
        userPreferenceService.updatePreference(userPreferenceDto);
    }
    private YieldAnalysisConfigDto getYieldConfigPreference() {
        String value = userPreferenceService.findPreferenceByUserId("yield_config_preference", envService.getUserName());
        if (StringUtils.isNotBlank(value)) {
            return mapper.fromJson(value, YieldAnalysisConfigDto.class);
        } else {
            return null;
        }
    }

    private int findNewSite(List<ItemTableModel> modelList, ItemTableModel model) {
        int site = originalItems.indexOf(model.getItem());
        for (int i = 0; i < modelList.size() - 1; i++) {
            if (!modelList.get(i).getOnTop()) {
                if (originalItems.indexOf(modelList.get(i).getItem()) < site && originalItems.indexOf(modelList.get(i + 1).getItem()) > site) {
                    return i + 1;
                }
            }
        }
        return site == 0 ? 0 : modelList.size();
    }

    private void updateAnalysisBtnTimer() {
        if (isTimer) {
            if (!startTimer) {
                analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_play.png")));
                analysisBtn.getStyleClass().remove("btn-primary");
                analysisBtn.getStyleClass().add("btn-timer");
            } else {
                analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_stop.png")));
                analysisBtn.getStyleClass().remove("btn-timer");
                analysisBtn.getStyleClass().add("btn-primary");
            }
        } else {
            analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
            analysisBtn.getStyleClass().remove("btn-timer");
            analysisBtn.getStyleClass().add("btn-primary");
        }
    }

    private boolean isFilterAndHasUslOrLsl(ItemTableModel itemTableModel) {
        return (StringUtils.isNotEmpty(itemTableModel.getItemDto().getLsl()) || StringUtils.isNotEmpty(itemTableModel.getItemDto().getUsl()))
                && (DAPStringUtils.isBlank(itemFilter.getTextField().getText()) || (DAPStringUtils.isNotBlank(itemFilter.getTextField().getText())
                && itemTableModel.getItem().toLowerCase().contains(itemFilter.getTextField().getText().toLowerCase())));
    }

    private boolean isFilterAndAll(ItemTableModel itemTableModel) {
        return itemTableModel.getItem().startsWith("") && (DAPStringUtils.isBlank(itemFilter.getTextField().getText())
                || (DAPStringUtils.isNotBlank(itemFilter.getTextField().getText()) && itemTableModel.getItem().toLowerCase().contains(itemFilter.getTextField().getText().toLowerCase())));
    }

    private void sortTestItemWithTypeDto(List<TestItemWithTypeDto> testItemWithTypeDtos, boolean isDES) {
        testItemWithTypeDtos.sort((o1, o2) -> {
            if (isDES) {
                return o2.getTestItemName().compareTo(o1.getTestItemName());
            } else {
                return o1.getTestItemName().compareTo(o2.getTestItemName());
            }
        });
    }

    public boolean isTimer() {
        return isTimer;
    }
    private void setStartTimerState(boolean isTimer) {
        split.setDisable(isTimer);
//        yieldMainController.setMainAnalysisTimerState(isTimer);
        importBtn.setDisable(isTimer);
        exportBtn.setDisable(isTimer);
        ControlMap.getControl(CommonResourceMassages.PLATFORM_CONTROL_DATASOURCE_BTN).setDisable(isTimer);
        ControlMap.getControl(CommonResourceMassages.PLATFORM_CONTROL_TEMPLATE_BTN).setDisable(isTimer);
    }

}

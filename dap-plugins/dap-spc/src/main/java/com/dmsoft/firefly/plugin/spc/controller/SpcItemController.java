/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.*;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
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

import javax.swing.text.html.ImageView;
import java.io.File;
import java.net.URL;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by Ethan.Yang on 2018/2/6.
 * Updated by Can Guan on 2018/3/23
 */
@Component
public class SpcItemController implements Initializable {
    private static final String STICKY_ON_TOP_CODE = "stick_on_top";
    private static final Double D20 = 20.0d;
    private final Logger logger = LoggerFactory.getLogger(SpcItemController.class);
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
    @FXML
    private Label helpLabel;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, TestItemWithTypeDto> item;
    @FXML
    private TableView<ItemTableModel> itemTable;
    @FXML
    private TextField subGroup;
    @FXML
    private TextField ndGroup;
    @FXML
    private SplitPane split;
    private SearchTab searchTab;
    private CheckBox box;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);
    private SpcMainController spcMainController;
    private ContextMenu pop;
    private boolean isFilterUslOrLsl = false;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SpcLeftConfigServiceImpl leftConfigService = new SpcLeftConfigServiceImpl();
    @Autowired
    private UserPreferenceService userPreferenceService;
    private JsonMapper mapper = JsonMapper.defaultMapper();
    // cached items for user preference
    private List<String> stickyOnTopItems = Lists.newArrayList();

    private List<String> originalItems = Lists.newArrayList();

    @FXML
    private CheckBox enabledTimerCheckBox;
    @FXML
    private ComboBox<String> timeComboBox;

    private boolean isTimer;
    private boolean startTimer;

    private Timer timer;

    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBtnIcon();
        searchTab = new SearchTab();
        split.getItems().add(searchTab);
        itemFilter.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
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
//        itemTable.setContextMenu(createTableRightMenu());

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
                                    this.getStyleClass().add("spc-item-top");
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
        is.getStyleClass().add("btn-size");
        is.setOnMousePressed(event -> createPopMenu(is, event));
        is.getStyleClass().add("filter-normal");

        item.setText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEM));
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
                                    this.getStyleClass().add("spc-item-attribute-top");
                                } else if (item.getTestItemType().equals(TestItemType.ATTRIBUTE)) {
                                    this.getStyleClass().add("spc-item-attribute");
                                } else if (getTableView().getItems().get(getIndex()).getOnTop()) {
                                    this.getStyleClass().add("spc-item-top");
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

        initComponentEvent();
        initItemData();
        initSpcConfig();
        initSpcTimer();
    }

    /**
     * init spc timer tab
     */
    public void initSpcTimer() {
        isTimer = false;
        startTimer = false;
        enabledTimerCheckBox.setSelected(false);
        List<String> timerList = leftConfigService.findSpcTimerTime();
        if (timerList == null) {
            return;
        }
        ObservableList<String> showTimeList = FXCollections.observableArrayList();
        for (String time : timerList) {
            showTimeList.add(time + SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIMER_MIN));
        }
        timeComboBox.setItems(showTimeList);
        if (showTimeList.size() > 0) {
            timeComboBox.setValue(showTimeList.get(0));
        }
    }

    /**
     * method to get current config
     *
     * @return spc left config dto
     */
    public SpcLeftConfigDto getCurrentConfigData() {
        SpcLeftConfigDto leftConfigDto = new SpcLeftConfigDto();
        leftConfigDto.setItems(getSelectedItem());
        leftConfigDto.setBasicSearchs(searchTab.getBasicSearch());
        if (searchTab.getAdvanceText().getText() != null) {
            leftConfigDto.setAdvanceSearch(searchTab.getAdvanceText().getText());
        }
        leftConfigDto.setNdNumber(ndGroup.getText());
        leftConfigDto.setSubGroup(subGroup.getText());
        if (searchTab.getGroup1().getValue() != null) {
            leftConfigDto.setAutoGroup1(searchTab.getGroup1().getValue());
        }
        if (searchTab.getGroup2().getValue() != null) {
            leftConfigDto.setAutoGroup2(searchTab.getGroup2().getValue());
        }
        return leftConfigDto;
    }

    private void initBtnIcon() {
        TooltipUtil.installNormalTooltip(analysisBtn, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ANALYSIS));
        TooltipUtil.installNormalTooltip(importBtn, SpcFxmlAndLanguageUtils.getString(ResourceMassages.IMPORT_CONFIG));
        TooltipUtil.installNormalTooltip(exportBtn, SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_CONFIG));
        itemTab.setTooltip(new Tooltip(SpcFxmlAndLanguageUtils.getString("SPC_TEST_ITEM")));
        configTab.setTooltip(new Tooltip(SpcFxmlAndLanguageUtils.getString("SPC_CONFIG")));
        timeTab.setTooltip(new Tooltip(SpcFxmlAndLanguageUtils.getString("SPC_TIMER_SETTING")));
        helpLabel.setTooltip(new Tooltip(SpcFxmlAndLanguageUtils.getString("SUBGROUP_SIZE_TIP")));

    }

    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            RadioMenuItem all = new RadioMenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.ALL_TEST_ITEMS));
            all.setOnAction(event -> {
                filteredList.setPredicate(this::isFilterAndAll);
                is.getStyleClass().remove("filter-active");
                is.getStyleClass().add("filter-normal");
                is.setGraphic(null);
                isFilterUslOrLsl = false;
            });
            RadioMenuItem show = new RadioMenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEMS_WITH_USL_LSL));
            show.setOnAction(event -> {
                filteredList.setPredicate(this::isFilterAndHasUslOrLsl);
                is.getStyleClass().remove("filter-normal");
                is.getStyleClass().add("filter-active");
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
        MenuItem top = new MenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.STICKY_ON_TOP));
        ContextMenu right = new ContextMenu() {
            @Override
            public void show(Node anchor, double screenX, double screenY) {
                if (((TableRow) anchor).getItem() == null) {
                    return;
                }
                if (itemTable.getSelectionModel().getSelectedItem() != null && itemTable.getSelectionModel().getSelectedItem().getOnTop()) {
                    top.setText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.REMOVE_FROM_TOP));
                } else {
                    top.setText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.STICKY_ON_TOP));
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
        MenuItem setting = new MenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPECIFICATION_SETTING));
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
            if (startTimer) {
                startTimer = false;
                if (timer != null) {
                    timer.cancel();
                }
                this.setStartTimerState(startTimer);
            } else {
                if (!validAnalysisCondition()) {
                    return;
                }
                startTimer = true;
                this.setStartTimerState(startTimer);
                timer = this.startTimerAnalysis();

                this.normalAnalysisEvent(isTimer);
            }
            this.updateAnalysisBtnTimer();
        } else {

            //TODO YUANWEN 添加插件测试代码
            int aa = 9901;
            System.out.println("sdfsdfsdfsdf===========");
            if (validAnalysisCondition()) {
                this.normalAnalysisEvent(isTimer);
            }
        }
    }

    private Timer startTimerAnalysis() {

        String currentRefreshTime = (String) timeComboBox.getValue();
        String time = currentRefreshTime.replace(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIMER_MIN), "");
        if (!DAPStringUtils.isNumeric(time)) {
            return null;
        }
        Double intervalTime = Double.valueOf(time) * 60000;
        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> autoRefreshAnalysis());
            }
        }, intervalTime.longValue(), intervalTime.longValue());
        return timer1;
    }

    @SuppressWarnings("unchecked")
    private void autoRefreshAnalysis() {
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        spcMainController.clearAnalysisSubShowData();
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
        SpcAnalysisConfigDto spcAnalysisConfigDto = this.buildSpcAnalysisConfigData();

        List<SearchConditionDto> chartSearchConditionDtoList = spcMainController.getSelectSearchCondition();
        this.updateSpcConfigPreference(spcAnalysisConfigDto);
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.STATISTICAL_SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.CHART_SEARCH_CONDITION_DTO_LIST, chartSearchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
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
                windowProgressTipController.setCancelingText();
                context.interruptBeforeNextJobHandler();
            });
        }
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_TIMER_REFRESH_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                spcMainController.setSpcSettingDto(context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class));
                spcMainController.setAnalysisConfigDto(spcAnalysisConfigDto);
                spcMainController.setInitSearchConditionDtoList(searchConditionDtoList);
                SpcRefreshJudgeUtil.newInstance().setViewDataSelectRowKeyListCache(null);
                SpcRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.STATISTICAL_ANALYSIS_RESULT);
                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());
                spcMainController.timerRefreshStatisticalResultData(spcStatisticalResultAlarmDtoList);

                SearchDataFrame searchDataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
                spcMainController.setDataFrame(searchDataFrame);

                //set chart data
                List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) context.get(ParamKeys.CHART_ANALYSIS_RESULT);
                if (spcChartDtoList != null && spcChartDtoList.size() != 0) {
                    spcMainController.setSpcChartData(spcChartDtoList);
                }
                //set view data
                spcMainController.setTimerViewData(chartSearchConditionDtoList, searchConditionDtoList);
                windowProgressTipController.closeDialog();
                logger.info("Spc auto refresh finish.");
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
        logger.info("Start auto refresh Spc.");
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }

    private boolean validAnalysisCondition() {
        if (isConfigError()) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_CONFIG_ERROR_MESSAGE));
            return false;
        }
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        if (selectedItemDto.size() == 0) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.UI_SPC_ANALYSIS_ITEM_EMPTY));
            return false;
        }
        return searchTab.verifySearchTextArea();
    }

    @SuppressWarnings("unchecked")
    private void normalAnalysisEvent(boolean isTimer) {
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        spcMainController.clearAnalysisData();
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
        SpcAnalysisConfigDto spcAnalysisConfigDto = this.buildSpcAnalysisConfigData();
        this.updateSpcConfigPreference(spcAnalysisConfigDto);
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
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
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                spcMainController.setSpcSettingDto(context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class));
                spcMainController.setAnalysisConfigDto(spcAnalysisConfigDto);
                spcMainController.setInitSearchConditionDtoList(searchConditionDtoList);
                SpcRefreshJudgeUtil.newInstance().setViewDataSelectRowKeyListCache(null);
                SpcRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST);
                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());
                spcMainController.setStatisticalResultData(spcStatisticalResultAlarmDtoList, null, isTimer);
                spcMainController.setDataFrame(context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class));
                windowProgressTipController.closeDialog();
                spcMainController.setDisable(false);
                logger.info("Spc analysis finish.");
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
        logger.info("Start analysis Spc.");
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

    private void initSpcConfig() {
        SpcAnalysisConfigDto spcAnalysisConfigDto = this.getSpcConfigPreference();
        if (spcAnalysisConfigDto == null) {
            spcAnalysisConfigDto = new SpcAnalysisConfigDto();
            spcAnalysisConfigDto.setIntervalNumber(PropertiesResource.SPC_CONFIG_INTERVAL_NUMBER);
            spcAnalysisConfigDto.setSubgroupSize(PropertiesResource.SPC_CONFIG_SUBGROUP_SIZE);
            this.updateSpcConfigPreference(spcAnalysisConfigDto);
        }
        String chartIntervalNumber = String.valueOf(spcAnalysisConfigDto.getIntervalNumber());
        String customGroupNumber = String.valueOf(spcAnalysisConfigDto.getSubgroupSize());

        subGroup.setText(customGroupNumber);
        ndGroup.setText(chartIntervalNumber);

        ValidateRule rule = new ValidateRule();
        rule.setMaxLength(SpcSettingValidateUtil.ANALYSIS_SETTING_MAX_INT);
        rule.setPattern("^\\+?\\d*$");
        rule.setErrorStyle("text-field-error");
        rule.setMaxValue(D20);
        rule.setMinValue(1d);
        String[] params = new String[]{rule.getMinValue().toString(), rule.getMaxValue().toString()};
        rule.setRangErrorMsg(SpcFxmlAndLanguageUtils.getString(ResourceMassages.RANGE_NUMBER_WARNING_MESSAGE, params));
        rule.setEmptyErrorMsg(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_VALIDATE_NOT_BE_EMPTY));
        TextFieldWrapper.decorate(subGroup, rule);
        TextFieldWrapper.decorate(ndGroup, rule);
    }

    private boolean isConfigError() {
        if (subGroup.getStyleClass().contains("text-field-error") || ndGroup.getStyleClass().contains("text-field-error")) {
            return true;
        }
        return false;
    }

    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_CONFIG_IMPORT));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(StageMap.getStage(ResourceMassages.PLATFORM_STAGE_MAIN));

        if (file != null) {
            SpcLeftConfigDto spcLeftConfigDto = leftConfigService.importSpcConfig(file);
            if (spcLeftConfigDto != null) {
                clearLeftConfig();
                if (spcLeftConfigDto.getItems() != null && spcLeftConfigDto.getItems().size() > 0) {
                    items.forEach(testItem -> {
                        if (spcLeftConfigDto.getItems().contains(testItem.getItem())) {
                            testItem.getSelector().setValue(true);
                        }
                    });
                }
                if (spcLeftConfigDto.getBasicSearchs() != null && spcLeftConfigDto.getBasicSearchs().size() > 0) {
                    searchTab.setBasicSearch(spcLeftConfigDto.getBasicSearchs());
                }
                ndGroup.setText(spcLeftConfigDto.getNdNumber());
                subGroup.setText(spcLeftConfigDto.getSubGroup());
                searchTab.getAdvanceText().setText(spcLeftConfigDto.getAdvanceSearch());
                searchTab.getGroup1().setValue(spcLeftConfigDto.getAutoGroup1());
                searchTab.getGroup2().setValue(spcLeftConfigDto.getAutoGroup2());
            } else {
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        SpcFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                        SpcFxmlAndLanguageUtils.getString("IMPORT_EXCEPTION"));
            }

        }
    }

    private void exportLeftConfig() {
        SpcLeftConfigDto leftConfigDto = this.getCurrentConfigData();

        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_CONFIG_EXPORT));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showSaveDialog(StageMap.getStage(ResourceMassages.PLATFORM_STAGE_MAIN));

        if (file != null) {
            leftConfigService.exportSpcConfig(leftConfigDto, file);
        }
    }

    private void clearLeftConfig() {
        box.setSelected(false);
        for (ItemTableModel model : items) {
            model.getSelector().setValue(false);
        }
        subGroup.setText(null);
        ndGroup.setText(null);
        searchTab.clearSearchTab();
    }

    private SpcAnalysisConfigDto buildSpcAnalysisConfigData() {
        SpcAnalysisConfigDto spcAnalysisConfigDto = new SpcAnalysisConfigDto();
        if (StringUtils.isNumeric(subGroup.getText())) {
            spcAnalysisConfigDto.setSubgroupSize(Integer.parseInt(subGroup.getText()));
        }
        if (StringUtils.isNumeric(ndGroup.getText())) {
            spcAnalysisConfigDto.setIntervalNumber(Integer.parseInt(ndGroup.getText()));
        }
        return spcAnalysisConfigDto;
    }

    private List<SearchConditionDto> buildSearchConditionDataList(List<TestItemWithTypeDto> testItemWithTypeDtoList) {
        if (testItemWithTypeDtoList == null) {
            return null;
        }
        List<String> conditionList = searchTab.getSearch();
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        int i = 0;
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            if (conditionList != null && conditionList.size() != 0) {
                for (String condition : conditionList) {
                    SearchConditionDto searchConditionDto = new SearchConditionDto();
                    searchConditionDto.setKey(ParamKeys.SPC_ANALYSIS_CONDITION_KEY + i);
                    searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                    searchConditionDto.setCusLsl(testItemWithTypeDto.getLsl());
                    searchConditionDto.setCusUsl(testItemWithTypeDto.getUsl());
                    searchConditionDto.setCondition(condition);
                    searchConditionDtoList.add(searchConditionDto);
                    i++;
                }
            } else {
                SearchConditionDto searchConditionDto = new SearchConditionDto();
                searchConditionDto.setKey(ParamKeys.SPC_ANALYSIS_CONDITION_KEY + i);
                searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                searchConditionDto.setCusLsl(testItemWithTypeDto.getLsl());
                searchConditionDto.setCusUsl(testItemWithTypeDto.getUsl());
                searchConditionDtoList.add(searchConditionDto);
                i++;
            }
        }
        return searchConditionDtoList;
    }

    private List<TestItemWithTypeDto> buildSelectTestItemWithTypeData(List<TestItemWithTypeDto> testItemWithTypeDtoList) {
        List<TestItemWithTypeDto> itemWithTypeDtoList = Lists.newArrayList();
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

    private void updateSpcConfigPreference(SpcAnalysisConfigDto configDto) {
        UserPreferenceDto<SpcAnalysisConfigDto> userPreferenceDto = new UserPreferenceDto<>();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode("spc_config_preference");
        userPreferenceDto.setValue(configDto);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private SpcAnalysisConfigDto getSpcConfigPreference() {
        String value = userPreferenceService.findPreferenceByUserId("spc_config_preference", envService.getUserName());
        if (StringUtils.isNotBlank(value)) {
            return mapper.fromJson(value, SpcAnalysisConfigDto.class);
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
                analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/start.svg")));
                analysisBtn.getStyleClass().remove("btn-primary");
                analysisBtn.getStyleClass().add("btn-timer");
            } else {
                analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/stop.svg")));
                analysisBtn.getStyleClass().remove("btn-timer");
                analysisBtn.getStyleClass().add("btn-primary");
            }
        } else {
            analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/analysis-white.svg")));
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
        spcMainController.setMainAnalysisTimerState(isTimer);
        importBtn.setDisable(isTimer);
        exportBtn.setDisable(isTimer);
        ControlMap.getControl(CommonResourceMassages.PLATFORM_CONTROL_DATASOURCE_BTN).setDisable(isTimer);
        ControlMap.getControl(CommonResourceMassages.PLATFORM_CONTROL_TEMPLATE_BTN).setDisable(isTimer);
    }
}

/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TextFieldWrapper;
import com.dmsoft.firefly.gui.components.utils.ValidateRule;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcSettingServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.*;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


/**
 * Created by Ethan.Yang on 2018/2/6.
 * Updated by Can Guan on 2018/3/23
 */
public class SpcItemController implements Initializable {
    private static final String STICKY_ON_TOP_CODE = "stick_on_top";
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
    private SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingServiceImpl.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    // cached items for user preference
    private List<String> stickyOnTopItems = Lists.newArrayList();

    private List<String> originalItems = Lists.newArrayList();

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
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().toLowerCase().contains(itemFilter.getTextField().getText().toLowerCase()))
        );

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
        itemTable.setContextMenu(createTableRightMenu());

        // select column in test item table
        box = new CheckBox();
        box.setOnAction(event -> {
            if (items != null) {
                for (ItemTableModel model : items) {
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
    }

    public SpcLeftConfigDto getCurrentConfigData(){
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
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        timeTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_timer_normal.png")));
    }

    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            MenuItem all = new MenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.ALL_TEST_ITEMS));
            all.setOnAction(event -> {
                filteredList.setPredicate(p -> p.getItem().startsWith(""));
                is.getStyleClass().remove("filter-active");
                is.getStyleClass().add("filter-normal");
                is.setGraphic(null);
                isFilterUslOrLsl = false;
            });
            MenuItem show = new MenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEMS_WITH_USL_LSL));
            show.setOnAction(event -> {
                filteredList.setPredicate(p -> StringUtils.isNotEmpty(p.getItemDto().getLsl()) || StringUtils.isNotEmpty(p.getItemDto().getUsl()));
                is.getStyleClass().remove("filter-normal");
                is.getStyleClass().add("filter-active");
                is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_filter_normal.png")));
                isFilterUslOrLsl = true;
            });
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
                if (itemTable.getSelectionModel().getSelectedItem().getOnTop()) {
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
        if (isConfigError()) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_CONFIG_ERROR_MESSAGE));
            return;
        }
        List<TestItemWithTypeDto> selectedItemDto = this.getSelectedItemDto();
        if (selectedItemDto.size() == 0) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER),
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.UI_SPC_ANALYSIS_ITEM_EMPTY));
            return;
        }
        if (!searchTab.verifySearchTextArea()) {
            return;
        }
        spcMainController.clearAnalysisData();
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
        SpcAnalysisConfigDto spcAnalysisConfigDto = this.buildSpcAnalysisConfigData();
        this.updateSpcConfigPreference(spcAnalysisConfigDto);
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        context.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        context.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        context.addJobEventListener(event -> {
            windowProgressTipController.getTaskProgress().setProgress(event.getProgress());
            System.out.println(event.getEventName() + " : " + event.getProgress());
        });
        windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE);
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                spcMainController.setSpcSettingDto(context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class));
                spcMainController.setAnalysisConfigDto(spcAnalysisConfigDto);
                spcMainController.setInitSearchConditionDtoList(searchConditionDtoList);
//                    spcMainController.clearAnalysisSubShowData();
                SpcRefreshJudgeUtil.newInstance().setViewDataSelectRowKeyListCache(null);
                SpcRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) context.get(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST);
                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());
                spcMainController.setStatisticalResultData(spcStatisticalResultAlarmDtoList);
                spcMainController.setDataFrame(context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class));
                windowProgressTipController.closeDialog();
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
        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
//        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
//        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
//        Service<Integer> service = new Service<Integer>() {
//            @Override
//            protected Task<Integer> createTask() {
//                return new Task<Integer>() {
//                    @Override
//                    protected Integer call() throws Exception {
//                        Thread.sleep(100);
//                        Job findSpcSettingJob = new Job(ParamKeys.FIND_SPC_SETTING_DATA_JOP_PIPELINE);
//                        Object settingDto = manager.doJobSyn(findSpcSettingJob);
//                        if (settingDto == null || settingDto instanceof Exception) {
//                            logger.debug("spc setting data is null");
//                            if (settingDto != null) {
//                                ((Exception) settingDto).printStackTrace();
//                            } else {
//                                throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_20001));
//                            }
//                        }
//                        if (!(settingDto instanceof SpcSettingDto)) {
//                            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_20001));
//                        }
//                        spcMainController.setSpcSettingDto((SpcSettingDto) settingDto);
//
//                        Job job = new Job(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE);
//                        job.addProcessMonitorListener(event -> {
//                            System.out.println("event*****" + event.getPoint());
//                            updateProgress(event.getPoint(), 100);
//                        });
//                        Map<String, Object> paramMap = Maps.newHashMap();
//                        paramMap.put(ParamKeys.SPC_SETTING_FILE_NAME, settingDto);
//                        paramMap.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
//                        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
//                        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
//                        paramMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
//
//                        spcMainController.setAnalysisConfigDto(spcAnalysisConfigDto);
//                        spcMainController.setInitSearchConditionDtoList(searchConditionDtoList);
//
//                        Object returnValue = manager.doJobSyn(job, paramMap, spcMainController);
//                        if (returnValue instanceof Exception) {
//                            //todo message tip
//                            ((Exception) returnValue).printStackTrace();
//                        } else {
//                            Platform.runLater(() -> {
//                                spcMainController.clearAnalysisSubShowData();
//                                SpcRefreshJudgeUtil.newInstance().setViewDataSelectRowKeyListCache(null);
//                                SpcRefreshJudgeUtil.newInstance().setStatisticalSelectRowKeyListCache(null);
//                                List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = (List<SpcStatisticalResultAlarmDto>) returnValue;
//                                TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
//                                DigNumInstance.newInstance().setDigNum(templateSettingDto.getDecimalDigit());
//                                spcMainController.setStatisticalResultData(spcStatisticalResultAlarmDtoList);
//                            });
//                        }
//                        return null;
//                    }
//                };
//            }
//        };
//        windowProgressTipController.getTaskProgress().progressProperty().bind(service.progressProperty());
//        service.start();
    }

    private List<String> getSelectedItem() {
        List<String> selectItems = Lists.newArrayList();
        if (itemTable.getItems() != null) {
            for (ItemTableModel model : itemTable.getItems()) {
                if (model.getSelector().isSelected()) {
                    selectItems.add(model.getItem());
                }
            }
        }
        return selectItems;
    }

    private List<TestItemWithTypeDto> getSelectedItemDto() {
        List<TestItemWithTypeDto> selectItems = Lists.newArrayList();
        if (itemTable.getItems() != null) {
            for (ItemTableModel model : itemTable.getItems()) {
                if (model.getSelector().isSelected()) {
                    selectItems.add(model.getItemDto());
                }
            }
        }
        return selectItems;
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
        rule.setMaxValue(20d);
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
        Stage fileStage = null;
        File file = fileChooser.showOpenDialog(fileStage);

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
        File file = fileChooser.showSaveDialog(null);

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
        List<String> timeKeys = Lists.newArrayList();
        String timePattern = null;
        if (timePatternDto != null) {
            timeKeys = timePatternDto.getTimeKeys();
            timePattern = timePatternDto.getPattern();
        }
        FilterUtils filterUtils = new FilterUtils(timeKeys, timePattern);
        for (String condition : conditionList) {
            Set<String> conditionTestItemSet = filterUtils.parseItemNameFromConditions(condition);
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
}

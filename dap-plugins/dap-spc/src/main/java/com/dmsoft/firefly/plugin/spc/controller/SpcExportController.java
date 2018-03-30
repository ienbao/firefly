package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TextFieldWrapper;
import com.dmsoft.firefly.gui.components.utils.ValidateRule;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcExportServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcSettingServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcExportItemKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by GuangLi on 2018/3/7.
 * Updated by Can Guan on 2018/3/23
 */
public class SpcExportController {
    private static final String STICKY_ON_TOP_CODE = "stick_on_top";
    private static final Double D100 = 100.0d;
    private static final Double D90 = 90.0d;
    private static final Double D10 = 10.0d;
    private static final Double D30 = 30.0d;
    private static final Double D70 = 70.0d;
    private static final Double D40 = 40.0d;
    private Map<String, Map<String, String>> chartPath = Maps.newHashMap();
    @FXML
    private TextFieldFilter itemFilter;
    @FXML
    private Tab itemTab;
    @FXML
    private Tab configTab;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, TestItemWithTypeDto> item;
    @FXML
    private TableView<ItemTableModel> itemTable;
    @FXML
    private Button importBtn;
    @FXML
    private Button viewData;
    @FXML
    private Button setting;
    @FXML
    private Button export;
    @FXML
    private Button print;
    @FXML
    private Button cancel;
    @FXML
    private Button browse;
    @FXML
    private RadioButton eachFile;
    @FXML
    private RadioButton allFile;
    @FXML
    private TextField locationPath;
    @FXML
    private TextField subGroup;
    @FXML
    private TextField ndGroup;
    @FXML
    private SplitPane split;
    private SearchTab searchTab;
    private CheckBox box;
    private ToggleGroup group = new ToggleGroup();
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);
    private ContextMenu pop;
    private boolean isFilterUslOrLsl = false;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private SearchDataFrame dataFrame;
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private SpcSettingServiceImpl settingService = RuntimeContext.getBean(SpcSettingServiceImpl.class);
    private SpcExportServiceImpl spcExportService = new SpcExportServiceImpl();
    private SpcLeftConfigServiceImpl leftConfigService = new SpcLeftConfigServiceImpl();
    private List<SpcStatisticalResultAlarmDto> spcStatsDtoList;
    private Map<String, Color> colorMap = Maps.newHashMap();
    // cached items for user preference
    private List<String> stickyOnTopItems = Lists.newArrayList();
    private List<String> originalItems = Lists.newArrayList();

    @FXML
    private void initialize() {
        initBtnIcon();
        eachFile.setToggleGroup(group);
        eachFile.setSelected(true);
        allFile.setToggleGroup(group);
        searchTab = new SearchTab();
        split.getItems().add(searchTab);

        itemFilter.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().contains(itemFilter.getTextField().getText()))
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
        itemTable.setContextMenu(createTableRightMenu());
        ndGroup.setText(String.valueOf(PropertiesResource.SPC_CONFIG_INTERVAL_NUMBER));
        subGroup.setText(String.valueOf(PropertiesResource.SPC_CONFIG_SUBGROUP_SIZE));
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

        initEvent();
        initItemData();
    }

    private void initBtnIcon() {
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
    }


    private void initEvent() {
        browse.setOnAction(event -> {
            String str = System.getProperty("user.home");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Spc Config export");
            directoryChooser.setInitialDirectory(new File(str));
            File file = directoryChooser.showDialog(null);

            if (file != null) {
                locationPath.setText(file.getPath());
            }
        });
        importBtn.setOnAction(event -> importLeftConfig());

        viewData.setOnAction(event -> {
            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please select export item.");
                return;
            }
            buildViewDataDia();
        });
        setting.setOnAction(event -> {
            initSpcExportSettingDialog();
        });
        export.setOnAction(event -> {
            if (StringUtils.isEmpty(locationPath.getText())) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please select export path.");
                return;
            }
            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please select export item.");
                return;
            }
            if (subGroup.getStyleClass().contains("text-field-error") || ndGroup.getStyleClass().contains("text-field-error")) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please Input correct config numbers.");
                return;
            }
            StageMap.closeStage("spcExport");
            String savePath = locationPath.getText() + "/SPC_" + getTimeString();
            export(savePath);
        });
        print.setOnAction(event -> {
            if (StringUtils.isEmpty(locationPath.getText())) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please select export path.");
                return;
            }
            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please select export item.");
                return;
            }
            StageMap.closeStage("spcExport");
            String savePath = locationPath.getText() + "/SPC_" + getTimeString();
            PdfPrintUtil.getPrintService();
            export(savePath);
            boolean isSucceed = false;
            try {
                isSucceed = new ExcelToPdfUtil().excelToPdf(savePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isSucceed) {
                PdfPrintUtil.printPdf(savePath);
            }
        });
        //TODO : change cancel text
        cancel.setOnAction(event -> {
            StageMap.closeStage("spcExport");
        });
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
        MenuItem specSetting = new MenuItem(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPECIFICATION_SETTING));
        specSetting.setOnAction(event -> RuntimeContext.getBean(EventContext.class).pushEvent(new PlatformEvent(null, "Template_Show")));
        right.getItems().addAll(top, specSetting);
        return right;
    }

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

    private synchronized void export(String savePath) {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.addJobEventListener(event -> {
            if ("Error".equals(event.getEventName())) {
                windowProgressTipController.updateFailProgress(event.getProgress(), event.getEventObject().toString());
            } else {
                windowProgressTipController.getTaskProgress().setProgress(event.getProgress());
            }
        });

        Boolean exportEachFile = false;
        if (eachFile.isSelected()) {
            exportEachFile = true;
        }
        List<String> projectNameList = envService.findActivatedProjectName();

        SpcAnalysisConfigDto spcAnalysisConfigDto = new SpcAnalysisConfigDto();

        //todo delete
        spcAnalysisConfigDto.setSubgroupSize(Integer.parseInt(subGroup.getText()));
        spcAnalysisConfigDto.setIntervalNumber(Integer.parseInt(ndGroup.getText()));

        Map<String, Boolean> exportDataItem = settingService.findSpcExportTemplateSetting();
        if (!exportDataItem.get(SpcExportItemKey.DESCRIPTIVE_STATISTICS.getCode())) {
            for (int i = 0; i < UIConstant.SPC_STATISTICAL.length; i++) {
                exportDataItem.put(UIConstant.SPC_STATISTICAL[i], false);
            }
        }
        if (!exportDataItem.get(SpcExportItemKey.PROCESS_CAPABILITY_INDEX.getCode())) {
            for (int i = 0; i < UIConstant.SPC_CAPABILITY.length; i++) {
                exportDataItem.put(UIConstant.SPC_CAPABILITY[i], false);
            }
        }
        if (!exportDataItem.get(SpcExportItemKey.PROCESS_PERFORMANCE_INDEX.getCode())) {
            for (int i = 0; i < UIConstant.SPC_PERFORMANCE.length; i++) {
                exportDataItem.put(UIConstant.SPC_PERFORMANCE[i], false);
            }
        }
        SpcExportConfigDto spcConfig = new SpcExportConfigDto();
        spcConfig.setExportPath(savePath);
        spcConfig.setPerformer(envService.getUserName());
        spcConfig.setDigNum(envService.findActivatedTemplate().getDecimalDigit());
        spcConfig.setExportDataItem(exportDataItem);
        JobPipeline jobPipeline = RuntimeContext.getBean(JobFactory.class).createJobPipeLine();
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                context.pushEvent(new JobEvent("Export done", D100, null));
                String path = context.get(ParamKeys.EXPORT_PATH).toString();
                windowProgressTipController.getCancelBtn().setText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.OPEN_EXPORT_FOLDER));
                windowProgressTipController.getCancelBtn().setOnAction(event -> {
                    try {
                        Desktop.getDesktop().open(new File(path));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.updateFailProgress(context.getError().getMessage());
            }
        });

        SpcSettingDto spcSettingDto = RuntimeContext.getBean(SpcSettingServiceImpl.class).findSpcSetting();
        if (exportEachFile) {
            int i = 0;
            for (String projectName : projectNameList) {
                jobPipeline.addLast(new AbstractBasicJobHandler(projectName + i) {
                    @Override
                    public void doJob(JobContext context) {
                        List<String> project = Lists.newArrayList(projectName);
                        List<TestItemWithTypeDto> itemDto = Lists.newArrayList();
                        List<String> allItem = dataService.findAllTestItemName(project);
                        List<TestItemWithTypeDto> selectItem = getSelectedItemDto();
                        for (TestItemWithTypeDto i : selectItem) {
                            if (allItem.contains(i.getTestItemName())) {
                                itemDto.add(i);
                            }
                        }
                        List<SearchConditionDto> searchConditionDtoList = buildSearchConditionDataList(itemDto);
                        searchTab.getConditionTestItem().forEach(item -> itemDto.add(envService.findTestItemNameByItemName(item)));
                        String result = exportFile(project, spcSettingDto, itemDto, searchConditionDtoList, spcAnalysisConfigDto, spcConfig, context);
                        context.put(ParamKeys.EXPORT_PATH, result);
                    }
                }.setWeight(D100));
                i++;
            }
        } else {
            jobPipeline.addLast(new AbstractBasicJobHandler("Export file") {
                @Override
                public void doJob(JobContext context) {

                    List<TestItemWithTypeDto> testItemWithTypeDtoList = getSelectedItemDto();
                    List<SearchConditionDto> searchConditionDtoList = buildSearchConditionDataList(testItemWithTypeDtoList);
                    searchTab.getConditionTestItem().forEach(item -> {
                        testItemWithTypeDtoList.add(envService.findTestItemNameByItemName(item));
                    });
                    String result = exportFile(projectNameList, spcSettingDto, testItemWithTypeDtoList, searchConditionDtoList, spcAnalysisConfigDto, spcConfig, context);
                    context.put(ParamKeys.EXPORT_PATH, result);
                }
            }.setWeight(D100));
        }

        RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
    }

    private String getTimeString() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }

    @SuppressWarnings("unchecked")
    private String exportFile(List<String> projectNameList,
                              SpcSettingDto spcSettingDto,
                              List<TestItemWithTypeDto> testItemWithTypeDtoList,
                              List<SearchConditionDto> searchConditionDtoList,
                              SpcAnalysisConfigDto spcAnalysisConfigDto,
                              SpcExportConfigDto spcConfig, JobContext context) {
        JobContext singleJobContext = RuntimeContext.getBean(JobFactory.class).createJobContext();
        JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_ANALYSIS_EXPORT_JOB_PIPELINE);
        jobPipeline.setErrorHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context1) {
                context.pushEvent(new JobEvent("Error", context.getCurrentProgress(), context1.getError()));
            }
        }.setWeight(D100));
        singleJobContext.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
        singleJobContext.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        singleJobContext.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        singleJobContext.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
        singleJobContext.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        RuntimeContext.getBean(JobManager.class).fireJobSyn(jobPipeline, singleJobContext);

        spcStatsDtoList = (List<SpcStatisticalResultAlarmDto>) singleJobContext.get(ParamKeys.SPC_STATISTICAL_RESULT_ALARM_DTO_LIST);
        context.pushEvent(new JobEvent("Get Stats & Alarm Result", D30, null));

        //build chart
        Map<String, String> runChartRule = Maps.newHashMap();

        if (spcConfig.getExportDataItem().get(SpcExportItemKey.EXPORT_CHARTS.getCode())) {
            JobContext singleJobContext4Chart = RuntimeContext.getBean(JobFactory.class).createJobContext();
            JobPipeline jobPipeline4Chart = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_REFRESH_CHART_EXPORT_JOB_PIPELINE);
            jobPipeline4Chart.setErrorHandler(new AbstractBasicJobHandler() {
                @Override
                public void doJob(JobContext context1) {
                    context.pushEvent(new JobEvent("Error", context.getCurrentProgress(), context1.getError()));
                }
            });
            singleJobContext4Chart.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
            singleJobContext4Chart.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
            singleJobContext4Chart.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);

//            buildViewData();
            List<String> selectItem = Lists.newArrayList();
            testItemWithTypeDtoList.forEach(dto -> selectItem.add(dto.getTestItemName()));
            List<RowDataDto> rowDataDtoList = dataService.findTestData(projectNameList, selectItem);
            dataFrame = RuntimeContext.getBean(DataFrameFactory.class).createSearchDataFrame(testItemWithTypeDtoList, rowDataDtoList);
            dataFrame.addSearchCondition(searchTab.getSearch());

            singleJobContext4Chart.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);

            RuntimeContext.getBean(JobManager.class).fireJobSyn(jobPipeline4Chart, singleJobContext4Chart);
            List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) singleJobContext4Chart.get(ParamKeys.SPC_CHART_DTO_LIST);
            CountDownLatch count = new CountDownLatch(1);
            Thread thread = new Thread(() -> {
                try {
                    chartPath = initSpcChartData(spcChartDtoList, spcConfig.getExportDataItem());
                } finally {
                    count.countDown();
                }
            });
            thread.start();

            try {
                count.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            context.pushEvent(new JobEvent("Export Chart done", D70, null));

            for (SpcChartDto dto : spcChartDtoList) {
                if (dto.getResultDto() != null && dto.getResultDto().getRunCResult() != null && dto.getResultDto().getRunCResult().getRuleResultDtoMap() != null) {
                    Map<String, RuleResultDto> rule = dto.getResultDto().getRunCResult().getRuleResultDtoMap();
                    StringBuilder s = new StringBuilder();
                    for (Map.Entry<String, RuleResultDto> entry : rule.entrySet()) {
                        if (entry.getValue() != null && entry.getValue().getX() != null && entry.getValue().getX().length > 0) {
                            if (s.length() > 0) {
                                s.append(",");
                            }
                            s.append(entry.getKey());
                        }
                    }
                    runChartRule.put(dto.getKey(), s.toString());
                }
            }
        }

        List<SpcStatisticalResultAlarmDto> spcStatisticalResultDtosToExport;
        int conditionSize = searchTab.getSearch().size();
        if (conditionSize < 2) {
            spcStatisticalResultDtosToExport = spcStatsDtoList;
        } else {
            spcStatisticalResultDtosToExport = Lists.newArrayList();
            for (int index = 0; index <= spcStatsDtoList.size() - conditionSize; index += conditionSize) {
                if (spcConfig.getExportDataItem().get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode())) {
                    SpcStatisticalResultAlarmDto spcStatisticalResultDto = new SpcStatisticalResultAlarmDto();
                    spcStatisticalResultDto.setItemName(spcStatsDtoList.get(index + conditionSize - 1).getItemName());
                    spcStatisticalResultDto.setKey(spcStatsDtoList.get(index + conditionSize - 1).getKey() + SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode());
                    spcStatisticalResultDto.setCondition(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode());
                    spcStatisticalResultDtosToExport.add(spcStatisticalResultDto);
                }
                for (int i = 0; i < conditionSize; i++) {
                    spcStatisticalResultDtosToExport.add(spcStatsDtoList.get(index + i));
                }
            }
        }

        String result = spcExportService.spcExport(spcConfig, spcStatisticalResultDtosToExport, chartPath, runChartRule);
        return result;
    }

    private List<SearchConditionDto> buildSearchConditionDataList(List<TestItemWithTypeDto> testItemWithTypeDtoList) {
        if (testItemWithTypeDtoList == null) {
            return null;
        }
        colorMap.clear();
        List<String> conditionList = searchTab.getSearch();
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        int i = 0;
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtoList) {
            if (conditionList != null) {
                for (String condition : conditionList) {
                    SearchConditionDto searchConditionDto = new SearchConditionDto();
                    searchConditionDto.setKey(ParamKeys.SPC_ANALYSIS_CONDITION_KEY + i);
                    searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                    searchConditionDto.setCusLsl(testItemWithTypeDto.getLsl());
                    searchConditionDto.setCusUsl(testItemWithTypeDto.getUsl());
                    searchConditionDto.setCondition(condition);
                    searchConditionDtoList.add(searchConditionDto);
                    colorMap.put(searchConditionDto.getKey(), ColorUtils.getTransparentColor(Colur.RAW_VALUES[i % 10], 1));
                    i++;
                }
            } else {
                SearchConditionDto searchConditionDto = new SearchConditionDto();
                searchConditionDto.setKey(ParamKeys.SPC_ANALYSIS_CONDITION_KEY + i);
                searchConditionDto.setItemName(testItemWithTypeDto.getTestItemName());
                searchConditionDto.setCusLsl(testItemWithTypeDto.getLsl());
                searchConditionDto.setCusUsl(testItemWithTypeDto.getUsl());
                searchConditionDtoList.add(searchConditionDto);
                colorMap.put(searchConditionDto.getKey(), ColorUtils.getTransparentColor(Colur.RAW_VALUES[i % 10], 1));
                i++;
            }
        }
        return searchConditionDtoList;
    }

    private Map<String, Map<String, String>> initSpcChartData(List<SpcChartDto> spcChartDtoList, Map<String, Boolean> exportParam) {
        return BuildChart.initSpcChartData(spcChartDtoList, searchTab.getSearch().size(), colorMap, exportParam);
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

    private void buildViewData() {
        List<String> selectItem = getSelectedItem();
        List<TestItemWithTypeDto> selectItemDto = Lists.newArrayList();

        if (selectItem != null) {
            List<String> conditionTestItem = searchTab.getConditionTestItem();
            if (conditionTestItem != null) {
                conditionTestItem.forEach(item -> {
                    if (!selectItem.contains(item)) {
                        selectItem.add(item);
                    }
                });
            }
            selectItem.forEach(itemName -> selectItemDto.add(envService.findTestItemNameByItemName(itemName)));
            List<RowDataDto> rowDataDtoList = dataService.findTestData(envService.findActivatedProjectName(), selectItem);
            dataFrame = RuntimeContext.getBean(DataFrameFactory.class).createSearchDataFrame(selectItemDto, rowDataDtoList);
            dataFrame.addSearchCondition(searchTab.getSearch());
        }
    }

    private void buildViewDataDia() {
        buildViewData();
        Pane root;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/export_view_data.fxml");
            ExportViewData controller = new ExportViewData();
            controller.setDataFrame(dataFrame);
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcExportViewData",
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.VIEW_DATA), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spc config import");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(null);

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
            }
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

    private void initSpcExportSettingDialog() {
        Pane root;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_export_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_EXPORT_TEMPLATE_SETTING,
                    SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();

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

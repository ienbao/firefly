package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.LinearChart;
import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.chart.*;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcExportServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcSettingServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcExportItemKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobDoComplete;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
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
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/7.
 */
public class SpcExportController {
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
    private TableView itemTable;
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
    private SplitPane split;
    private SearchTab searchTab;
    private CheckBox box;
    private ToggleGroup group = new ToggleGroup();

    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private ContextMenu pop;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private SpcExportSettingController spcExportSettingController;

    private JobManager manager = RuntimeContext.getBean(JobManager.class);
    private SearchDataFrame dataFrame;

    private SpcSettingServiceImpl settingService = new SpcSettingServiceImpl();
    private SpcExportServiceImpl spcExportService = new SpcExportServiceImpl();
    private List<SpcStatisticalResultAlarmDto> spcStatsDtoList;
    private Map<String, Color> colorMap = Maps.newHashMap();

    @FXML
    private void initialize() {
        eachFile.setToggleGroup(group);
        eachFile.setSelected(true);
        allFile.setToggleGroup(group);
        searchTab = new SearchTab();
        split.getItems().add(searchTab);
        initBtnIcon();
        initEvent();
        itemFilter.getTextField().setPromptText("Test Item");
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().contains(itemFilter.getTextField().getText()))
        );
        itemTable.setOnMouseEntered(event -> {
            itemTable.focusModelProperty();
        });
        if (itemTable.getSkin() != null) {
            NewTableViewWrapper.decorateSkinForSortHeader((TableViewSkin) itemTable.getSkin(), itemTable);
        } else {
            itemTable.skinProperty().addListener((ov, s1, s2) -> {
                NewTableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, itemTable);
            });
        }
        box = new CheckBox();
        box.setOnAction(event -> {
            if (items != null) {
                for (ItemTableModel model : items) {
                    model.getSelector().setValue(box.isSelected());
                }
            }
        });
        select.setGraphic(box);
        select.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        Button is = new Button();
        is.setPrefSize(22, 22);
        is.setMinSize(22, 22);
        is.setMaxSize(22, 22);
        is.setOnMousePressed(event -> createPopMenu(is, event));
        is.getStyleClass().add("filter-normal");

        item.setText("Test Item");
        item.setGraphic(is);
        item.getStyleClass().add("filter-header");
        item.setCellValueFactory(cellData -> cellData.getValue().itemDtoProperty());
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
            Stage fileStage = null;
            File file = directoryChooser.showDialog(fileStage);

            if (file != null) {
                locationPath.setText(file.getPath());
            }
        });

        viewData.setOnAction(event -> {
            if (getSelectedItem() != null && getSelectedItem().size() > 0)
                buildViewDataDia();
        });
        setting.setOnAction(event -> {
            initSpcExportSettingDialog();
        });
        export.setOnAction(event -> {
            if (StringUtils.isEmpty(locationPath.getText())) {
                //todo
                return;
            }
            export();
            StageMap.closeStage("spcExport");

        });
        print.setOnAction(event -> {
            StageMap.closeStage("spcExport");
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("spcExport");
        });
    }

    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            MenuItem all = new MenuItem("All Test Items");
            all.setOnAction(event -> {
                filteredList.setPredicate(p -> p.getItem().startsWith(""));
                is.getStyleClass().remove("filter-active");
                is.getStyleClass().add("filter-normal");
                is.setGraphic(null);
            });
            MenuItem show = new MenuItem("Test Items with USL/LSL");
            show.setOnAction(event -> {
                filteredList.setPredicate(p -> StringUtils.isNotEmpty(p.getItemDto().getLsl()) || StringUtils.isNotEmpty(p.getItemDto().getUsl()));
                is.getStyleClass().remove("filter-normal");
                is.getStyleClass().add("filter-active");
                is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_filter_normal.png")));
            });
            pop.getItems().addAll(all, show);
        }
        Bounds bounds = is.localToScreen(is.getBoundsInLocal());
        pop.show(is, bounds.getMinX(), bounds.getMinY() + 22);
//        pop.show(is, e.getScreenX(), e.getScreenY());
        return pop;
    }

    private void initItemData() {
        items.clear();
        List<TestItemWithTypeDto> itemDtos = envService.findTestItems();
        if (itemDtos != null) {
            for (TestItemWithTypeDto dto : itemDtos) {
                ItemTableModel tableModel = new ItemTableModel(dto);
                items.add(tableModel);
            }
            itemTable.setItems(personSortedList);
            personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
        }
    }

    private String export() {
//        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
//        Job job = new Job(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE);
//        job.addProcessMonitorListener(event -> {
//            windowProgressTipController.refreshProgress(event.getPoint());
//        });
//        windowProgressTipController.addProcessMonitorListener(new WindowCustomListener() {
//            @Override
//            public boolean onShowCustomEvent() {
//                System.out.println("show");
//
//                return false;
//            }
//
//            @Override
//            public boolean onCloseAndCancelCustomEvent() {
//                //to do
//                System.out.println("close");
//                return false;
//            }
//
//            @Override
//            public boolean onOkCustomEvent() {
//                System.out.println("ok");
//
//                return false;
//            }
//        });

        Boolean exportEachFile = false;
        if (eachFile.isSelected()) {
            exportEachFile = true;
        }
        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.getSelectedItemDto();
        searchTab.getConditionTestItem().forEach(item -> {
            testItemWithTypeDtoList.add(envService.findTestItemNameByItemName(item));
        });
        List<SearchConditionDto> searchConditionDtoList = buildSearchConditionDataList(getSelectedItemDto());
        SpcAnalysisConfigDto spcAnalysisConfigDto = new SpcAnalysisConfigDto();
//        spcAnalysisConfigDto.setSubgroupSize();
        //todo delete
        spcAnalysisConfigDto.setSubgroupSize(10);
        spcAnalysisConfigDto.setIntervalNumber(8);
        if (exportEachFile) {
            String result = "";
            for (String projectName : projectNameList) {
                result = exportFile(Lists.newArrayList(projectName), testItemWithTypeDtoList, searchConditionDtoList, spcAnalysisConfigDto);
            }
            return result;
        } else {
            return exportFile(projectNameList, testItemWithTypeDtoList, searchConditionDtoList, spcAnalysisConfigDto);
        }
    }

    private String exportFile(List<String> projectNameList, List<TestItemWithTypeDto> testItemWithTypeDtoList, List<SearchConditionDto> searchConditionDtoList, SpcAnalysisConfigDto spcAnalysisConfigDto) {
        Map<String, Boolean> exportDataItem = settingService.findSpcExportTemplateSetting();

        Job job = new Job(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE);

        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
        paramMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        spcStatsDtoList = (List<SpcStatisticalResultAlarmDto>) manager.doJobSyn(job, paramMap, null);

        //build chart
        Map<String, Map<String, String>> chartPath = Maps.newHashMap();

        if (exportDataItem.get(SpcExportItemKey.EXPORT_CHARTS.getCode())) {
            Job chartJob = new Job(ParamKeys.SPC_REFRESH_JOB_PIPELINE);
            Map chartParamMap = Maps.newHashMap();
            chartParamMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
            chartParamMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);

            buildViewData();
            chartParamMap.put(ParamKeys.SEARCH_DATA_FRAME, dataFrame);

            Object returnValue = manager.doJobSyn(chartJob, chartParamMap);
            if (returnValue == null) {
                return null;
            }
            List<SpcChartDto> spcChartDtoList = (List<SpcChartDto>) returnValue;
            chartPath = initSpcChartData(spcChartDtoList);
        }
        SpcUserActionAttributesDto spcConfig = new SpcUserActionAttributesDto();
        spcConfig.setExportPath(locationPath.getText());
        spcConfig.setPerformer(envService.getUserName());

        spcConfig.setExportDataItem(exportDataItem);

        //build statistical data
//        Platform.runLater(() -> {
//            manager.doJobASyn(job, new JobDoComplete() {
//                @Override
//                public void doComplete(Object returnValue) {
//                    if (returnValue == null) {
//                        //todo message tip
//                        return;
//                    }
//                    spcStatsDtoList = (List<SpcStatisticalResultAlarmDto>) returnValue;
//
////                    spcMainController.setStatisticalResultData(spcStatsDtoList);
//                }
//            }, paramMap, null);
//        });
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultDtosToExport = null;
        int conditionSize = searchTab.getSearch().size();
        if (conditionSize < 2) {
            spcStatisticalResultDtosToExport = spcStatsDtoList;
        } else {
            spcStatisticalResultDtosToExport = Lists.newArrayList();
            for (int index = 0; index <= spcStatsDtoList.size() - conditionSize; index += conditionSize) {
                if (exportDataItem.get(SpcExportItemKey.EXPORT_SUB_SUMMARY.getCode())) {
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
        String exportPath = spcExportService.spcExport(spcConfig, spcStatisticalResultDtosToExport, chartPath);
        return exportPath;
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
            colorMap.put(ParamKeys.SPC_ANALYSIS_CONDITION_KEY + i, ColorUtils.getTransparentColor(Colur.RAW_VALUES[i % 10], 1));
            if (conditionList != null) {
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

    private Map<String, Map<String, String>> initSpcChartData(List<SpcChartDto> spcChartDtoList) {
        Map<String, Map<String, String>> chartPath = BuildChart.initSpcChartData(spcChartDtoList, searchTab.getSearch().size(), colorMap);

        return chartPath;
    }


    /**
     * get selected test items
     *
     * @return test items
     */
    public List<String> getSelectedItem() {
        List<String> selectItems = Lists.newArrayList();
        if (items != null) {
            for (ItemTableModel model : items) {
                if (model.getSelector().isSelected()) {
                    selectItems.add(model.getItem());
                }
            }
        }
        return selectItems;
    }

    /**
     * get selected test items
     *
     * @return test items
     */
    public List<TestItemWithTypeDto> getSelectedItemDto() {
        List<TestItemWithTypeDto> selectItems = Lists.newArrayList();
        if (items != null) {
            for (ItemTableModel model : items) {
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
            selectItem.forEach(itemName -> {
                selectItemDto.add(envService.findTestItemNameByItemName(itemName));
            });
            List<RowDataDto> rowDataDtoList = dataService.findTestData(envService.findActivatedProjectName(), selectItem);
            dataFrame = RuntimeContext.getBean(DataFrameFactory.class).createSearchDataFrame(selectItemDto, rowDataDtoList);
            dataFrame.addSearchCondition(searchTab.getSearch());
//            dataFrame.subDataFrame(searchTab.getSearch(), selectItem);
        }
    }

    private void buildViewDataDia() {
        buildViewData();
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/export_view_data.fxml");
            ExportViewData controller = new ExportViewData();
            controller.setDataFrame(dataFrame);
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcExportViewData", SpcFxmlAndLanguageUtils.getString(ResourceMassages.VIEW_DATA), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initSpcExportSettingDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_export_setting.fxml");
            root = fxmlLoader.load();
            spcExportSettingController = fxmlLoader.getController();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_EXPORT_TEMPLATE_SETTING, SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}

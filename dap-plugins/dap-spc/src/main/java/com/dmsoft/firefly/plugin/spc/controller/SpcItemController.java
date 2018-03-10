/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobDoComplete;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.google.common.io.Resources.getResource;


/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcItemController implements Initializable {
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
    private TableView itemTable;
    @FXML
    private TextField subGroup;
    @FXML
    private TextField ndGroup;

    @FXML
    private SplitPane split;
    private SearchTab searchTab;

    private CheckBox box;
    private ObservableList<String> groupItem = FXCollections.observableArrayList();

    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private SpcMainController spcMainController;
    private ContextMenu pop;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private SpcLeftConfigServiceImpl leftConfigService = new SpcLeftConfigServiceImpl();
    private JobManager manager = RuntimeContext.getBean(JobManager.class);

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
        searchTab = new SearchTab();
        split.getItems().add(searchTab);
        initBtnIcon();
        itemFilter.getTextField().setPromptText("Test Item");
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().contains(itemFilter.getTextField().getText()))
        );
        this.initComponentEvent();
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

//        is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));

        item.setText("Test Item");
        item.setGraphic(is);
        item.getStyleClass().add("filter-header");
        item.setCellValueFactory(cellData -> cellData.getValue().itemDtoProperty());
        initItemData();
        item.setPrefWidth(148);

        item.widthProperty().addListener((ov, w1, w2) -> {
            Platform.runLater(() -> {
                is.relocate(w2.doubleValue() - 21, 0);
            });
        });
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
        pop.show(is, e.getScreenX(), e.getScreenY());
        return pop;
    }

    private void initComponentEvent() {
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
        importBtn.setOnAction(event -> importLeftConfig());
        exportBtn.setOnAction(event -> exportLeftConfig());
        item.setCellFactory(new Callback<TableColumn<ItemTableModel, TestItemWithTypeDto>, TableCell<ItemTableModel, TestItemWithTypeDto>>() {
            public TableCell call(TableColumn<ItemTableModel, TestItemWithTypeDto> param) {
                return new TableCell<ItemTableModel, TestItemWithTypeDto>() {
                    private ObservableValue ov;

                    @Override
                    public void updateItem(TestItemWithTypeDto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {

                            if (getTableRow() != null && item.getTestItemType().equals(TestItemType.ATTRIBUTE)) {
                                this.setStyle("-fx-text-fill: #009bff");
                            }
                            if (getTableRow() != null && StringUtils.isNotEmpty(itemFilter.getTextField().getText()) && item.getTestItemName().contains(itemFilter.getTextField().getText())) {
                                this.setStyle("-fx-text-fill: red");
                            }
                            // Get fancy and change color based on data
                            setText(item.getTestItemName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    @SuppressWarnings("unchecked")
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

    private void getAnalysisBtnEvent() {
        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        Job job = new Job(ParamKeys.SPC_ANALYSIS_JOB_PIPELINE);
        job.addProcessMonitorListener(event -> {
            windowProgressTipController.refreshProgress(event.getPoint());
        });
        windowProgressTipController.addProcessMonitorListener(new WindowCustomListener() {
            @Override
            public void onCloseAndCancelCustomEvent() {
                //to do
                System.out.println("close");
            }

            @Override
            public void onOkCustomEvent() {

            }
        });
        Map paramMap = Maps.newHashMap();

        List<String> projectNameList = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> selectedItemDto = this.getSelectedItemDto();
        List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
        List<SearchConditionDto> searchConditionDtoList = this.buildSearchConditionDataList(selectedItemDto);
        SpcAnalysisConfigDto spcAnalysisConfigDto = this.buildSpcAnalysisConfigData();

        //todo delete
        spcAnalysisConfigDto.setSubgroupSize(10);
        spcAnalysisConfigDto.setIntervalNumber(8);
        paramMap.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
        paramMap.put(ParamKeys.SEARCH_CONDITION_DTO_LIST, searchConditionDtoList);
        paramMap.put(ParamKeys.SPC_ANALYSIS_CONFIG_DTO, spcAnalysisConfigDto);
        paramMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);

        spcMainController.setAnalysisConfigDto(spcAnalysisConfigDto);
        Platform.runLater(() -> {
            manager.doJobASyn(job, new JobDoComplete() {
                @Override
                public void doComplete(Object returnValue) {
                    if (returnValue == null) {
                        //todo message tip
                        return;
                    }
                    List<SpcStatsDto> spcStatsDtoList = (List<SpcStatsDto>) returnValue;
                    spcMainController.setStatisticalResultData(spcStatsDtoList);
                }
            }, paramMap, spcMainController);
        });

    }


    @Deprecated
    private List<SpcStatsDto> initData() {
        List<SpcStatsDto> spcStatsDtoList = Lists.newArrayList();
        Random random = new Random();
        int k = random.nextInt(100);
        for (int i = 0; i < k; i++) {
            SpcStatsDto statisticalResultDto = new SpcStatsDto();
            statisticalResultDto.setKey("key" + i);
            statisticalResultDto.setItemName("itemName" + i);
            statisticalResultDto.setCondition("itemName > 22");
            spcStatsDtoList.add(statisticalResultDto);
            SpcStatsResultDto spcStatsResultDto = new SpcStatsResultDto();
            statisticalResultDto.setStatsResultDto(spcStatsResultDto);
            int m = random.nextInt(k);
            if (m > i) {
                statisticalResultDto.getStatsResultDto().setSamples(m + 2.1);
            }
            statisticalResultDto.getStatsResultDto().setAvg(m + 32.2);
            statisticalResultDto.getStatsResultDto().setMax(m + 312.7);
            statisticalResultDto.getStatsResultDto().setMin(m + 34.8);
            statisticalResultDto.getStatsResultDto().setStDev(m + 124.6);
            statisticalResultDto.getStatsResultDto().setLsl(m + 32.2);
            statisticalResultDto.getStatsResultDto().setUsl(m + 32.2);
            statisticalResultDto.getStatsResultDto().setCenter(m + 32.2);
            statisticalResultDto.getStatsResultDto().setRange(m + 32.2);
            statisticalResultDto.getStatsResultDto().setLcl(m + 32.2);
            statisticalResultDto.getStatsResultDto().setUcl(m + 32.2);
            statisticalResultDto.getStatsResultDto().setKurtosis(m + 32.2);
            statisticalResultDto.getStatsResultDto().setCpk(m + 32.2);
            statisticalResultDto.getStatsResultDto().setSkewness(m + 32.2);
            statisticalResultDto.getStatsResultDto().setCa(m + 32.2);
            statisticalResultDto.getStatsResultDto().setCp(m + 32.2);
            statisticalResultDto.getStatsResultDto().setCpl(m + 32.2);
            statisticalResultDto.getStatsResultDto().setCpu(m + 32.2);
            statisticalResultDto.getStatsResultDto().setWithinPPM(m + 32.2);
            statisticalResultDto.getStatsResultDto().setOverallPPM(m + 32.2);
            statisticalResultDto.getStatsResultDto().setPp(m + 32.2);
            statisticalResultDto.getStatsResultDto().setPpk(m + 32.2);
            statisticalResultDto.getStatsResultDto().setPpl(m + 32.2);
            statisticalResultDto.getStatsResultDto().setPpu(m + 32.2);

        }
        return spcStatsDtoList;
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

    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spc config import");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        Stage fileStage = null;
        File file = fileChooser.showOpenDialog(fileStage);

        if (file != null) {
            clearLeftConfig();
            SpcLeftConfigDto spcLeftConfigDto = leftConfigService.importSpcConfig(file);
            if (spcLeftConfigDto != null) {
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

    private void exportLeftConfig() {
        SpcLeftConfigDto leftConfigDto = new SpcLeftConfigDto();
        leftConfigDto.setItems(getSelectedItem());
            leftConfigDto.setBasicSearchs(searchTab.getBasicSearch());
        if (searchTab.getAdvanceText().getText() != null) {
            leftConfigDto.setAdvanceSearch(searchTab.getAdvanceText().getText().toString());
        }
        leftConfigDto.setNdNumber(ndGroup.getText());
        leftConfigDto.setSubGroup(subGroup.getText());
        if (searchTab.getGroup1().getValue() != null) {
            leftConfigDto.setAutoGroup1(searchTab.getGroup1().getValue().toString());
        }
        if (searchTab.getGroup2().getValue() != null) {
            leftConfigDto.setAutoGroup2(searchTab.getGroup2().getValue().toString());
        }

        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spc Config export");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        Stage fileStage = null;
        File file = fileChooser.showSaveDialog(fileStage);

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
            spcAnalysisConfigDto.setSubgroupSize(Integer.valueOf(subGroup.getText()));
        }
        if (StringUtils.isNumeric(ndGroup.getText())) {
            spcAnalysisConfigDto.setIntervalNumber(Integer.valueOf(ndGroup.getText()));
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
        List<String> conditionTestItemList = Lists.newArrayList();
        List<String> timeKeys = Lists.newArrayList();
        String timePattern = null;
        try {
            timeKeys = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
            timePattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        } catch (Exception e) {

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

}

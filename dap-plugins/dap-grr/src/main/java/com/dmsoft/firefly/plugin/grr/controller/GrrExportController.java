/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSummaryResultDto;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.model.ItemTableModel;
import com.dmsoft.firefly.plugin.grr.model.ListViewModel;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.service.GrrExportService;
import com.dmsoft.firefly.plugin.grr.service.GrrService;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrExportServiceImpl;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.message.IMessageManager;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Garen.Pang on 2018/3/13.
 */
public class GrrExportController {

    @FXML
    private TextFieldFilter itemFilter;
    @FXML
    private Tab itemTab;
    @FXML
    private TableView itemTable;
    @FXML
    private Tab configTab;
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
    private TextField locationPath;
    @FXML
    private RadioButton eachFile;
    @FXML
    private RadioButton allFile;
    @FXML
    private TextField partTxt;
    @FXML
    private TextField appraiserTxt;
    @FXML
    private TextField trialTxt;
    @FXML
    private Label partLbl;
    @FXML
    private ComboBox partCombox;
    @FXML
    private Label appraiserLbl;
    @FXML
    private ComboBox appraiserCombox;
    @FXML
    private GridPane grrConfigPane;
    @FXML
    private ListView<ListViewModel> partListView;
    private ObservableList<ListViewModel> partList = FXCollections.observableArrayList();

    @FXML
    private ListView<ListViewModel> appraiserListView;
    private ObservableList<ListViewModel> appraiserList = FXCollections.observableArrayList();

    @FXML
    private SplitPane split;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, TestItemWithTypeDto> item;

    private Label warnIconLbl;
    private Label warnIconLbl1;
    private CheckBox box;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private SearchDataFrame dataFrame;
    private SearchTab searchTab;
    private ContextMenu pop;
    private ToggleGroup group = new ToggleGroup();

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private GrrConfigServiceImpl grrConfigService = new GrrConfigServiceImpl();
    private GrrExportService grrExportService = new GrrExportServiceImpl();
    private GrrLeftConfigServiceImpl leftConfigService = new GrrLeftConfigServiceImpl();

    private JobManager manager = RuntimeContext.getBean(JobManager.class);

    @FXML
    private void initialize() {
        searchTab = new SearchTab();
        searchTab.hiddenGroupAdd();
        split.getItems().add(searchTab);
        eachFile.setToggleGroup(group);
        eachFile.setSelected(true);
        allFile.setToggleGroup(group);
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
            TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) itemTable.getSkin(), itemTable);
        } else {
            itemTable.skinProperty().addListener((ov, s1, s2) -> {
                TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, itemTable);
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
        item.widthProperty().addListener((ov, w1, w2) -> {
            Platform.runLater(() -> {
                is.relocate(w2.doubleValue() - 21, 0);
            });
        });
        initPartAndAppraiserDatas();
        GrrValidateUtil.validateGrr(partTxt, appraiserTxt, trialTxt, partCombox);
        partTxt.textProperty().addListener((obVal, oldVal, newVal) -> {
            updatePartLbl();
        });
        appraiserTxt.textProperty().addListener((obVal, oldVal, newVal) -> {
            updateAppraiserLbl();
        });
        getWarnLblIcon();
    }

    private void updatePartLbl() {
        partLbl.setVisible(true);
        partLbl.setContentDisplay(ContentDisplay.LEFT);
        partLbl.setGraphic(warnIconLbl);
        partLbl.setStyle("-fx-text-fill: red");
        int count = (int) partListView.getItems().stream().filter(ListViewModel::isIsChecked).count();
        if (StringUtils.isBlank(partTxt.getText())) {
            if (count != 0) {
                partLbl.setText(count + "/-");
                TooltipUtil.installNormalTooltip(partLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_EXPECT_WARN"));
            } else {
                clearPartLbl();
            }
        } else {
            Integer expectInt = Integer.valueOf(partTxt.getText());
            if (count != 0 && count != expectInt) {
                partLbl.setText(count + "/" + expectInt);
                String[] params = new String[]{expectInt.toString()};
                if (count < Integer.valueOf(expectInt)) {
                    TooltipUtil.installNormalTooltip(partLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_LESS_WARN", params));
                } else {
                    TooltipUtil.installNormalTooltip(partLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_MORE_WARN", params));
                }
            } else if (count != 0 && count == Integer.valueOf(partTxt.getText())) {
                partLbl.setText(count + "/" + partTxt.getText());
                partLbl.setGraphic(null);
                partLbl.setStyle("");
            } else {
                clearPartLbl();
            }
        }
    }

    private void updateAppraiserLbl() {
        appraiserLbl.setVisible(true);
        appraiserLbl.setContentDisplay(ContentDisplay.LEFT);
        appraiserLbl.setGraphic(warnIconLbl1);
        appraiserLbl.setStyle("-fx-text-fill: red");
        int count = (int) appraiserListView.getItems().stream().filter(ListViewModel::isIsChecked).count();
        if (StringUtils.isBlank(appraiserTxt.getText())) {
            if (count != 0) {
                appraiserLbl.setText(count + "/-");
            } else {
                clearAppraiserLbl();
            }
        } else {
            Integer expectInt = Integer.valueOf(appraiserTxt.getText());
            if (count != 0 && count != expectInt) {
                appraiserLbl.setText(count + "/" + expectInt);
                String[] params = new String[]{expectInt.toString()};
                if (count < Integer.valueOf(expectInt)) {
                    TooltipUtil.installNormalTooltip(appraiserLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_LESS_WARN", params));
                } else {
                    TooltipUtil.installNormalTooltip(appraiserLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_MORE_WARN", params));
                }
            } else if (count != 0 && count == Integer.valueOf(appraiserTxt.getText())) {
                appraiserLbl.setText(count + "/" + appraiserTxt.getText());
                appraiserLbl.setGraphic(null);
                appraiserLbl.setStyle("");
            } else {
                clearAppraiserLbl();
            }
        }
    }

    private void getWarnLblIcon() {
        warnIconLbl = new Label();
        warnIconLbl.getStyleClass().add("message-tip-warn-mark");
        warnIconLbl.setStyle("-fx-padding: 0 26 0 0;");
        warnIconLbl1 = new Label();
        warnIconLbl1.getStyleClass().add("message-tip-warn-mark");
        warnIconLbl1.setStyle("-fx-padding: 0 26 0 0;");
    }

    private void clearPartLbl() {
        partLbl.setText("");
        partLbl.setGraphic(null);
        partLbl.setStyle("");
        partLbl.setVisible(false);
        TooltipUtil.uninstallNormalTooltip(partLbl);
    }

    private void clearAppraiserLbl() {
        appraiserLbl.setText("");
        appraiserLbl.setStyle("");
        appraiserLbl.setGraphic(null);
        appraiserLbl.setVisible(false);
        TooltipUtil.uninstallNormalTooltip(appraiserLbl);

    }

    private void initBtnIcon() {
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
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

        ObservableList<String> datas = FXCollections.observableArrayList();
        if (items != null) {
            for (ItemTableModel model : items) {
                datas.add(model.getItem());
            }
        }
        partCombox.setItems(datas);
        appraiserCombox.setItems(datas);
        initListView(partListView);
        initListView(appraiserListView);
        this.partCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
            values.forEach(value -> {
                partList.add(new ListViewModel(value, false, ""));
            });
            partListView.setItems(partList);
        });

        this.appraiserCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
            values.forEach(value -> {
                appraiserList.add(new ListViewModel(value, false, ""));
            });
            appraiserListView.setItems(appraiserList);
        });


    }

    private void initPartAndAppraiserDatas() {
        ObservableList<String> datas = FXCollections.observableArrayList();
        if (items != null) {
            for (ItemTableModel model : items) {
                datas.add(model.getItem());
            }
        }
        partCombox.setItems(datas);
        appraiserCombox.setItems(datas);

        initListView(partListView);
        initListView(appraiserListView);

        this.partCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            partList.clear();
            clearPartLbl();
            Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
            updatePartListViewDatas(values, false);
        });

        this.appraiserCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            appraiserList.clear();
            clearAppraiserLbl();
            Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
            updateAppraiserListViewDatas(values, false);
        });
    }

    private void updatePartListViewDatas(Set<String> parts, boolean isSelected) {
        parts.forEach(value -> {
            partList.add(new ListViewModel(value, isSelected, ""));
        });
        partListView.setItems(partList);
        RowConstraints row7 = grrConfigPane.getRowConstraints().get(7);
        row7.setPrefHeight(112);
        row7.setMaxHeight(112);
        row7.setMinHeight(112);
    }

    private void updateAppraiserListViewDatas(Set<String> appraisers, boolean isSelected) {
        appraisers.forEach(value -> {
            appraiserList.add(new ListViewModel(value, isSelected, ""));
        });
        appraiserListView.setItems(appraiserList);
        RowConstraints row11 = grrConfigPane.getRowConstraints().get(11);
        row11.setPrefHeight(112);
        row11.setMaxHeight(112);
        row11.setMinHeight(112);
    }

    private void refreshPartOrAppraiserListView(GrrParamDto grrParamDto) {
        if (grrParamDto != null && grrParamDto.getErrors() == null || grrParamDto.getErrors().isEmpty()) {
            Set<String> selectedParts = grrParamDto.getParts();
            if (selectedParts != null) {
                partListView.getItems().forEach(listViewModel -> {
                    listViewModel.setErrorMsg(null);
                    if (selectedParts.contains(listViewModel.getName())) {
                        listViewModel.setIsChecked(true);
                    }
                });
                partListView.refresh();
            }

            Set<String> selectedAppraisers = grrParamDto.getAppraisers();
            if (selectedAppraisers != null) {
                appraiserListView.getItems().forEach(listViewModel -> {
                    listViewModel.setErrorMsg(null);
                    if (selectedAppraisers != null && selectedAppraisers.contains(listViewModel.getName())) {
                        listViewModel.setIsChecked(true);
                    }
                });
                appraiserListView.refresh();
            }
        } else {
            getTooltipMsg(partListView, grrParamDto, false);
            getTooltipMsg(appraiserListView, grrParamDto, true);
        }
    }

    private void getTooltipMsg(ListView<ListViewModel> listView, GrrParamDto grrParamDto, boolean isSlot) {
        Map<String, String> errorMsgs = grrParamDto.getErrors();
        listView.getItems().forEach(listViewModel -> {
            StringBuilder errorMsg = new StringBuilder();
            errorMsgs.keySet().forEach(key -> {
                String[] keys = key.split(UIConstant.SPLIT_FLAG);
                if (keys != null) {
                    if (isSlot) {
                        if (keys[1].equals(listViewModel.getName())) {
                            errorMsg.append(errorMsgs.get(key)).append("\n");
                        }
                    } else {
                        if (keys[0].equals(listViewModel.getName())) {
                            errorMsg.append(errorMsgs.get(key)).append("\n");
                        }
                    }
                }
            });
            listViewModel.setErrorMsg(errorMsg.toString());
        });
        listView.refresh();
    }

    private void initListView(ListView<ListViewModel> listView) {
        listView.setCellFactory(e -> new ListCell<ListViewModel>() {
            @Override
            public void updateItem(ListViewModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox cell;
                    CheckBox checkBox = new CheckBox();
                    checkBox.setPrefSize(12, 12);
                    if (item.isIsChecked()) {
                        checkBox.setSelected(true);
                    } else {
                        checkBox.setSelected(false);
                    }
                    if (StringUtils.isNotBlank(item.getErrorMsg())) {
                        checkBox.getStyleClass().add("error");
                    } else {
                        checkBox.getStyleClass().removeAll("error");
                    }
                    checkBox.setOnAction(event -> {
                        item.setIsChecked(checkBox.isSelected());
                        if (listView.getId().equals("partListView")) {
                            updatePartLbl();
                        } else {
                            updateAppraiserLbl();
                        }
                    });
                    if (StringUtils.isNotBlank(item.getErrorMsg())) {
                        checkBox.setOnMouseEntered(event -> {
                            TooltipUtil.installNormalTooltip(checkBox, item.getErrorMsg());
                        });
                        checkBox.setOnMouseExited(event -> {
                            TooltipUtil.uninstallNormalTooltip(checkBox);
                        });
                    }

                    Label label = new Label(item.getName());
                    cell = new HBox(checkBox, label);
                    setGraphic(cell);
                }

            }
        });

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    private void initEvent() {
        importBtn.setOnAction(event -> importLeftConfig());
        browse.setOnAction(event -> {
            String str = System.getProperty("user.home");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Grr Config export");
            directoryChooser.setInitialDirectory(new File(str));
            Stage fileStage = null;
            File file = directoryChooser.showDialog(fileStage);

            if (file != null) {
                locationPath.setText(file.getPath());
            }
        });

        viewData.setOnAction(event -> {
            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "Please select export item.");
                return;
            }
            buildViewDataDia();
        });
        setting.setOnAction(event -> {
            build();
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
            if (!checkSubmitParam(getSelectedItem().size())) {
                WindowMessageFactory.createWindowMessageHasOk("Export", "GRR Config param error.");
                return;
            }
            StageMap.closeStage("grrExport");
            Thread thread = new Thread(() -> {
                String savePath = locationPath.getText() + "/GRR_" + getTimeString();
                List<String> projectNameList = envService.findActivatedProjectName();
                if (eachFile.isSelected()) {
                    projectNameList.forEach(projectName -> export(Lists.newArrayList(projectName), savePath));
                } else {
                    export(projectNameList, savePath);
                }
            });
            thread.start();
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

            StageMap.closeStage("grrExport");

            Thread thread = new Thread(() -> {
                PdfPrintUtil.getPrintService();

                String savePath = locationPath.getText() + "/GRR_" + getTimeString();
                List<String> projectNameList = envService.findActivatedProjectName();
                if (eachFile.isSelected()) {
                    projectNameList.forEach(projectName -> export(Lists.newArrayList(projectName), savePath));
                } else {
                    export(projectNameList, savePath);
                }
                boolean isSucceed = false;
                try {
                    isSucceed = new ExcelToPdfUtil().excelToPdf(savePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isSucceed) {
//                    exportProcessDialogView.getTaProgress().append("Print success...\n");
//                    exportProcessDialogView.dispose();
                    PdfPrintUtil.printPdf(savePath);
                }
            });
            thread.start();
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("grrExport");
        });
    }

    private String getTimeString() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(d);
    }

    private List<String> getSelectedItem() {
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
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/export_view_data.fxml");
            ExportViewData controller = new ExportViewData();
            controller.setDataFrame(dataFrame);
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExportViewData", GrrFxmlAndLanguageUtils.getString(ResourceMassages.VIEW_DATA), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void build() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_export_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExportSetting", GrrFxmlAndLanguageUtils.getString(ResourceMassages.GRR_EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void export(List<String> projectNameList, String savePath) {
        List<TestItemWithTypeDto> testItemWithTypeDtoList = getSelectedItemDto();
        List<TestItemWithTypeDto> itemDto = Lists.newArrayList();
        if (projectNameList.size() == 1) {
            List<String> allItem = dataService.findAllTestItemName(projectNameList);
            for (TestItemWithTypeDto i : testItemWithTypeDtoList) {
                if (allItem.contains(i.getTestItemName())) {
                    itemDto.add(i);
                }
            }
        } else {
            itemDto = testItemWithTypeDtoList;
        }
        if (checkSubmitParam(itemDto.size())) {
            GrrConfigDto grrConfigDto = grrConfigService.findGrrConfig();
            Boolean detail = grrConfigDto.getExport().get("Export detail sheet of each selected items");
//            List<Double> level = grrConfigDto.getAlarmSetting();

            GrrExportConfigDto grrExportConfigDto = new GrrExportConfigDto();
            grrExportConfigDto.setExportPath(savePath);
            grrExportConfigDto.setUserName(envService.getUserName());
            grrExportConfigDto.setGrrConfigDto(grrConfigDto);
            grrExportConfigDto.setDigNum(envService.findActivatedTemplate().getDecimalDigit());
            searchTab.getConditionTestItem().forEach(item -> {
                testItemWithTypeDtoList.add(envService.findTestItemNameByItemName(item));
            });
            testItemWithTypeDtoList.add(envService.findTestItemNameByItemName(partCombox.getValue().toString()));
            if (appraiserCombox.getValue() != null) {
                testItemWithTypeDtoList.add(envService.findTestItemNameByItemName(appraiserCombox.getValue().toString()));
            }

            Map paramMap = Maps.newHashMap();
            paramMap.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
            paramMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
            SearchConditionDto searchConditionDto = this.initSearchConditionDto();
            searchConditionDto.setSelectedTestItemDtos(itemDto);
            paramMap.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, searchConditionDto);
            if (!detail) {
                Job job = new Job(ParamKeys.GRR_EXPORT_JOB_PIPELINE);
                Object returnValue = manager.doJobSyn(job, paramMap, null);
                if (returnValue instanceof ApplicationException) {

                    return;
                }
                if (returnValue != null && !(returnValue instanceof GrrParamDto)) {
                    List<GrrSummaryDto> grrSummaryDtoList = (List<GrrSummaryDto>) returnValue;
                    grrExportService.exportGrrSummary(grrExportConfigDto, grrSummaryDtoList);
                }
            } else {
                Job job = new Job(ParamKeys.GRR_EXPORT_DETAIL_JOB_PIPELINE);
                Object returnValue = manager.doJobSyn(job, paramMap, null);
                if (returnValue instanceof ApplicationException) {

                    return;
                }
                if (returnValue != null && !(returnValue instanceof GrrParamDto)) {
                    List<GrrExportDetailDto> grrSummaryDtoList = (List<GrrExportDetailDto>) returnValue;
//                    List<GrrExportDetailDto> grrSummaryDtoList = (List<GrrExportDetailDto>) manager.doJobSyn(job, paramMap, null);
                    List<GrrSummaryDto> summaryDtos = Lists.newArrayList();
                    List<GrrExportResultDto> grrExportResultDtos = Lists.newArrayList();
                    for (GrrExportDetailDto dto : grrSummaryDtoList) {
                        GrrSummaryDto summaryDto = new GrrSummaryDto();
                        summaryDto.setItemName(dto.getItemName());
                        GrrSummaryResultDto summaryResultDto = new GrrSummaryResultDto();
                        summaryResultDto.setUsl(dto.getExportDetailDto().getUsl());
                        summaryResultDto.setGrrOnContribution(dto.getExportDetailDto().getGrrOnContribution());
                        summaryResultDto.setGrrOnTolerance(dto.getExportDetailDto().getGrrOnTolerance());
                        summaryResultDto.setLsl(dto.getExportDetailDto().getLsl());
                        summaryResultDto.setRepeatabilityOnContribution(dto.getExportDetailDto().getRepeatabilityOnContribution());
                        summaryResultDto.setRepeatabilityOnTolerance(dto.getExportDetailDto().getRepeatabilityOnTolerance());
                        summaryResultDto.setReproducibilityOnContribution(dto.getExportDetailDto().getReproducibilityOnContribution());
                        summaryResultDto.setReproducibilityOnTolerance(dto.getExportDetailDto().getReproducibilityOnTolerance());
                        summaryResultDto.setTolerance(dto.getExportDetailDto().getTolerance());

                        summaryDto.setSummaryResultDto(summaryResultDto);
                        summaryDtos.add(summaryDto);

                        GrrExportResultDto exportResultDto = new GrrExportResultDto();
                        exportResultDto.setItemName(dto.getItemName());
                        exportResultDto.setGrrAnovaAndSourceResultDto(dto.getExportDetailDto().getAnovaAndSourceResultDto());
                        exportResultDto.setGrrImageDto(BuildChart.buildImage(dto.getExportDetailDto(), searchConditionDto.getParts(), searchConditionDto.getAppraisers()));
                        grrExportResultDtos.add(exportResultDto);
                    }
                    grrExportService.exportGrrSummaryDetail(grrExportConfigDto, summaryDtos, grrExportResultDtos);
                }
            }
        }
    }

    private SearchConditionDto initSearchConditionDto() {
        SearchConditionDto searchConditionDto = new SearchConditionDto();
        searchConditionDto.setPart(partCombox.getValue().toString());
        searchConditionDto.setPartInt(Integer.valueOf(partTxt.getText()));
        searchConditionDto.setAppraiserInt(Integer.valueOf(appraiserTxt.getText()));
        searchConditionDto.setTrialInt(Integer.valueOf(trialTxt.getText()));
        List<String> parts = Lists.newLinkedList();
        partList.forEach(listViewModel -> {
            if (listViewModel.isIsChecked()) {
                parts.add(listViewModel.getName());
            }
        });
        searchConditionDto.setParts(parts);

        if (appraiserCombox.getValue() != null) {
            searchConditionDto.setAppraiser(appraiserCombox.getValue().toString());
            List<String> appraisers = Lists.newLinkedList();
            appraiserList.forEach(listViewModel -> {
                if (listViewModel.isIsChecked()) {
                    appraisers.add(listViewModel.getName());
                }
            });
            if (!appraisers.isEmpty()) {
                searchConditionDto.setAppraisers(appraisers);
            }
        }
        List<String> conditionList = searchTab.getSearch();
        conditionList.remove("");
        searchConditionDto.setSearchCondition(conditionList);
        return searchConditionDto;
    }

    private boolean checkSubmitParam(Integer itemNumbers) {
        if (itemNumbers == null || itemNumbers <= 0) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE), GrrFxmlAndLanguageUtils.getString("UI_GRR_ANALYSIS_ITEM_EMPTY"));
            return false;
        }

        if (!GrrValidateUtil.validateResult(partTxt, appraiserTxt, trialTxt, partCombox)) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE), GrrFxmlAndLanguageUtils.getString("UI_GRR_CONFIGURATION_INVALIDATE"));
            return false;
        }

        if (appraiserLbl.getGraphic() != null || partLbl.getGraphic() != null) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE), GrrFxmlAndLanguageUtils.getString("UI_GRR_CONFIGURATION_INVALIDATE"));
            return false;
        }

        if (partListView.getItems().size() > 0 && partListView.getItems().size() < Integer.valueOf(partTxt.getText())) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE), GrrFxmlAndLanguageUtils.getString("UI_GRR_PART_MAX_NUMBER_NOT_MATCH"));
            return false;
        }

        if ((appraiserCombox.getValue() != null) && (appraiserListView.getItems().size() > 0 && appraiserListView.getItems().size() < Integer.valueOf(appraiserTxt.getText()))) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE), GrrFxmlAndLanguageUtils.getString("UI_GRR_APPRAISER_MAX_NUMBER_NOT_MATCH"));
            return false;
        }

        return true;
    }

    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Grr config import");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        Stage fileStage = null;
        File file = fileChooser.showOpenDialog(fileStage);

        if (file != null) {
            GrrLeftConfigDto grrLeftConfigDto = leftConfigService.importGrrConfig(file);
            if (grrLeftConfigDto != null) {
                clearLeftConfig();
                if (grrLeftConfigDto.getItems() != null && grrLeftConfigDto.getItems().size() > 0) {
                    items.forEach(testItem -> {
                        if (grrLeftConfigDto.getItems().contains(testItem.getItem())) {
                            testItem.getSelector().setValue(true);
                        }
                    });
                }
                if (grrLeftConfigDto.getBasicSearchs() != null && grrLeftConfigDto.getBasicSearchs().size() > 0) {
                    searchTab.setBasicSearch(grrLeftConfigDto.getBasicSearchs());
                }
                searchTab.getAdvanceText().setText(grrLeftConfigDto.getAdvanceSearch());
                if (grrLeftConfigDto.getPartInt() != null) {
                    partTxt.setText(grrLeftConfigDto.getPartInt().toString());
                }
                if (grrLeftConfigDto.getAppraiserInt() != null) {
                    appraiserTxt.setText(grrLeftConfigDto.getAppraiserInt().toString());
                }

                if (grrLeftConfigDto.getTrialInt() != null) {
                    trialTxt.setText(grrLeftConfigDto.getTrialInt().toString());
                }

                if (StringUtils.isNotBlank(grrLeftConfigDto.getPart())) {
                    partCombox.setValue(grrLeftConfigDto.getPart());
                }


                if (StringUtils.isNotBlank(grrLeftConfigDto.getAppraiser())) {
                    appraiserCombox.setValue(grrLeftConfigDto.getAppraiser());
                }

                if (grrLeftConfigDto.getParts() != null && !grrLeftConfigDto.getParts().isEmpty()) {
                    updatePartListViewDatas(new LinkedHashSet<>(grrLeftConfigDto.getParts()), true);
                }

                if (grrLeftConfigDto.getAppraisers() != null && !grrLeftConfigDto.getAppraisers().isEmpty()) {
                    updatePartListViewDatas(new LinkedHashSet<>(grrLeftConfigDto.getAppraisers()), true);
                }
            }

        }
    }

    private void clearLeftConfig() {
        box.setSelected(false);
        for (ItemTableModel model : items) {
            model.getSelector().setValue(false);
        }
        searchTab.clearSearchTab();
    }
}

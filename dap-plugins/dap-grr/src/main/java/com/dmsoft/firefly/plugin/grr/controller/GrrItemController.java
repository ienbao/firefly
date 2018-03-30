/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.model.ItemTableModel;
import com.dmsoft.firefly.plugin.grr.model.ListViewModel;
import com.dmsoft.firefly.plugin.grr.service.impl.GrrLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.grr.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.TimePatternDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by Ethan.Yang on 2018/2/6.
 * Updated by Can Guan on 2018/3/23
 */
public class GrrItemController implements Initializable {
    private static final String STICKY_ON_TOP_CODE = "stick_on_top";
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
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, TestItemWithTypeDto> item;
    @FXML
    private TableView<ItemTableModel> itemTable;
    @FXML
    private TextField partTxt;
    @FXML
    private TextField appraiserTxt;
    @FXML
    private TextField trialTxt;
    @FXML
    private ComboBox<String> partCombox;
    @FXML
    private Label partLbl;
    @FXML
    private ComboBox<String> appraiserCombox;
    @FXML
    private Label appraiserLbl;
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
    private SearchTab searchTab;
    private Label warnIconLbl;
    private Label warnIconLbl1;
    private CheckBox box;

    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private GrrMainController grrMainController;
    private ContextMenu pop;
    private boolean isFilterUslOrLsl = false;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private GrrLeftConfigServiceImpl leftConfigService = new GrrLeftConfigServiceImpl();
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private SearchConditionDto searchConditionDto = new SearchConditionDto();
    private List<TestItemWithTypeDto> initSelectTestItemDtos = Lists.newLinkedList();
    private JsonMapper mapper = JsonMapper.defaultMapper();


    // cached items for user preference
    private List<String> stickyOnTopItems = Lists.newArrayList();

    private List<String> originalItems = Lists.newArrayList();

    /**
     * init main controller
     *
     * @param grrMainController main controller
     */
    public void init(GrrMainController grrMainController) {
        this.grrMainController = grrMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchTab = new SearchTab(false);
        searchTab.hiddenGroupAdd();
        searchTab.getGroup1().setVisible(false);
        searchTab.getGroup2().setVisible(false);
        searchTab.getAutoDivideLbl().setVisible(false);
        split.getItems().add(searchTab);
        initBtnIcon();
        itemFilter.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEM));
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().contains(itemFilter.getTextField().getText()))
        );
        this.initComponentEvent();
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
        Button is = new Button();
        is.setPrefSize(22, 22);
        is.setMinSize(22, 22);
        is.setMaxSize(22, 22);
        is.setOnMousePressed(event -> createPopMenu(is, event));
        is.getStyleClass().add("filter-normal");

//        is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));

        item.setText(GrrFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEM));
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
        initPartAndAppraiserDatas();
        initGrrBasicParam();
        GrrValidateUtil.validateGrr(partTxt, appraiserTxt, trialTxt, partCombox);
        partTxt.textProperty().addListener((obVal, oldVal, newVal) -> {
            updatePartLbl();
        });
        appraiserTxt.textProperty().addListener((obVal, oldVal, newVal) -> {
            updateAppraiserLbl();
        });
        getWarnLblIcon();
    }

    private void initGrrBasicParam() {
        GrrPreferenceDto grrPreferenceDto = getGrrPreference();
        if (grrPreferenceDto != null) {
            partTxt.setText(grrPreferenceDto.getPartInt().toString());
            appraiserTxt.setText(grrPreferenceDto.getAppraiserInt().toString());
            trialTxt.setText(grrPreferenceDto.getTrialInt().toString());
            partCombox.setValue(grrPreferenceDto.getPart());
            appraiserCombox.setValue(grrPreferenceDto.getAppraiser());
        }
    }

    private void initPartAndAppraiserDatas() {
        ObservableList<String> datas = FXCollections.observableArrayList();
        datas.add("");
        datas.addAll(originalItems);
       /* if (items != null) {
            for (ItemTableModel model : items) {
                datas.add(model.getItem());
            }
        }*/

        partCombox.setItems(datas);
        appraiserCombox.setItems(datas);

        initListView(partListView);
        initListView(appraiserListView);
        Set<String> empty = new HashSet<String>();
        this.partCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            partList.clear();
            clearLbl(partLbl);
            if (DAPStringUtils.isBlank(newValue)) {
                updatePartListViewDatas(empty, false);
            } else {
                Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
                updatePartListViewDatas(values, false);
            }
        });
        this.appraiserCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            appraiserList.clear();
            clearLbl(appraiserLbl);
            if (DAPStringUtils.isBlank(newValue)) {
                updateAppraiserListViewDatas(empty, false);
            } else {
                Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
                updateAppraiserListViewDatas(values, false);
            }
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
        if (grrParamDto != null) {
            if (grrParamDto.getErrors() != null && !grrParamDto.getErrors().isEmpty()) {
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
    }

    private void getTooltipMsg(ListView<ListViewModel> listView, GrrParamDto grrParamDto, boolean isSlot) {
        if (grrParamDto.getErrors() != null && !grrParamDto.getErrors().isEmpty()) {
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
                clearLbl(partLbl);
            }
        } else {
            Integer expectInt = Integer.parseInt(partTxt.getText());
            if (count != 0 && count != expectInt) {
                partLbl.setText(count + "/" + expectInt);
                String[] params = new String[]{expectInt.toString()};
                if (count < expectInt) {
                    TooltipUtil.installNormalTooltip(partLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_LESS_WARN", params));
                } else {
                    TooltipUtil.installNormalTooltip(partLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_MORE_WARN", params));
                }
            } else if (count != 0 && count == Integer.parseInt(partTxt.getText())) {
                partLbl.setText(count + "/" + partTxt.getText());
                partLbl.setGraphic(null);
                partLbl.setStyle("");
            } else {
                clearLbl(partLbl);
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
                clearLbl(appraiserLbl);
            }
        } else {
            Integer expectInt = Integer.parseInt(appraiserTxt.getText());
            if (count != 0 && count != expectInt) {
                appraiserLbl.setText(count + "/" + expectInt);
                String[] params = new String[]{expectInt.toString()};
                if (count < expectInt) {
                    TooltipUtil.installNormalTooltip(appraiserLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_LESS_WARN", params));
                } else {
                    TooltipUtil.installNormalTooltip(appraiserLbl, GrrFxmlAndLanguageUtils.getString("UI_GRR_ITEM_VALUE_COUNT_MORE_WARN", params));
                }
            } else if (count != 0 && count == Integer.parseInt(appraiserTxt.getText())) {
                appraiserLbl.setText(count + "/" + appraiserTxt.getText());
                appraiserLbl.setGraphic(null);
                appraiserLbl.setStyle("");
            } else {
                clearLbl(appraiserLbl);
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

    private void clearLbl(Label label) {
        label.setText("");
        label.setGraphic(null);
        label.setStyle("");
        label.setVisible(false);
        TooltipUtil.uninstallNormalTooltip(label);
    }

    private void initBtnIcon() {
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
    }

    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            MenuItem all = new MenuItem(GrrFxmlAndLanguageUtils.getString(ResourceMassages.ALL_TEST_ITEMS));
            all.setOnAction(event -> {
                filteredList.setPredicate(p -> p.getItem().startsWith(""));
                is.getStyleClass().remove("filter-active");
                is.getStyleClass().add("filter-normal");
                is.setGraphic(null);
                isFilterUslOrLsl = false;
            });
            MenuItem show = new MenuItem(GrrFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEMS_WITH_USL_LSL));
            show.setOnAction(event -> {
                filteredList.setPredicate(p -> StringUtils.isNotEmpty(p.getItemDto().getLsl()) || StringUtils.isNotEmpty(p.getItemDto().getUsl()));
                is.getStyleClass().remove("filter-normal");
                is.getStyleClass().add("filter-active");
                is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_filter_normal.png")));
                isFilterUslOrLsl = true;
            });
            pop.getItems().addAll(all, show);
        }
        pop.show(is, e.getScreenX(), e.getScreenY());
        return pop;
    }


    private ContextMenu createTableRightMenu() {
        MenuItem top = new MenuItem(GrrFxmlAndLanguageUtils.getString(ResourceMassages.STICKY_ON_TOP));
        ContextMenu right = new ContextMenu() {
            @Override
            public void show(Node anchor, double screenX, double screenY) {
                if (itemTable.getSelectionModel().getSelectedItem().getOnTop()) {
                    top.setText(GrrFxmlAndLanguageUtils.getString(ResourceMassages.REMOVE_FROM_TOP));
                } else {
                    top.setText(GrrFxmlAndLanguageUtils.getString(ResourceMassages.STICKY_ON_TOP));
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
        MenuItem setting = new MenuItem(GrrFxmlAndLanguageUtils.getString(ResourceMassages.SPECIFICATION_SETTING));
        setting.setOnAction(event -> RuntimeContext.getBean(EventContext.class).pushEvent(new PlatformEvent(null, "Template_Show")));
        right.getItems().addAll(top, setting);
        return right;
    }

    private void initComponentEvent() {
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
        importBtn.setOnAction(event -> importLeftConfig());
        exportBtn.setOnAction(event -> exportLeftConfig());
        item.setCellFactory(new Callback<TableColumn<ItemTableModel, TestItemWithTypeDto>, TableCell<ItemTableModel, TestItemWithTypeDto>>() {
            public TableCell call(TableColumn<ItemTableModel, TestItemWithTypeDto> param) {
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
            personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
            itemTable.setItems(personSortedList);
            personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
        }
    }

    @SuppressWarnings("unchecked")
    private void getAnalysisBtnEvent() {
        List<TestItemWithTypeDto> selectedItemDto = this.initSelectedItemDto();
        if (checkSubmitParam(selectedItemDto.size())) {
            JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
            WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
            List<String> projectNameList = envService.findActivatedProjectName();
            List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
            context.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
            context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
            SearchConditionDto conditionDto = this.initSearchConditionDto();
            conditionDto.setSelectedTestItemDtos(selectedItemDto);
            context.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, conditionDto);
            context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
            windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
            updateGrrPreference(conditionDto);
            JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.GRR_VIEW_DATA_JOB_PIPELINE);
            jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
                @Override
                public void doJob(JobContext context) {
                    GrrParamDto grrParamDto = context.getParam(ParamKeys.GRR_PARAM_DTO, GrrParamDto.class);
                    refreshPartOrAppraiserListView(grrParamDto);
                    grrMainController.setGrrParamDto(grrParamDto);
                    grrMainController.setGrrConfigDto(context.getParam(ParamKeys.SEARCH_GRR_CONFIG_DTO, GrrConfigDto.class));
                    grrMainController.setActiveTemplateSettingDto(context.getParam(ParamKeys.SEARCH_TEMPLATE_SETTING_DTO, TemplateSettingDto.class));
                    GrrDataFrameDto grrDataFrameDto = context.getParam(ParamKeys.SEARCH_VIEW_DATA_FRAME, GrrDataFrameDto.class);
                    grrMainController.setGrrDataFrame(grrDataFrameDto);
                    GrrDataFrameDto backDataFrame = new GrrDataFrameDto();
                    backDataFrame.setDataFrame(grrDataFrameDto.getDataFrame());
                    List<GrrViewDataDto> includeViewDataDtos = Lists.newArrayList(ListUtils.deepCopy(grrDataFrameDto.getIncludeDatas()));
                    List<GrrViewDataDto> backViewDataDtos = Lists.newArrayList(ListUtils.deepCopy(grrDataFrameDto.getBackupDatas()));
                    backDataFrame.setIncludeDatas(includeViewDataDtos);
                    backDataFrame.setBackupDatas(backViewDataDtos);
                    grrMainController.setBackGrrDataFrame(backDataFrame);
                    grrMainController.setSummaryDtos((List<GrrSummaryDto>) context.get(ParamKeys.GRR_SUMMARY_DTO_LIST));
                    grrMainController.setGrrDetailDto(context.getParam(ParamKeys.GRR_DETAIL_DTO, GrrDetailDto.class));
                    if (grrParamDto != null && (grrParamDto.getErrors() == null || grrParamDto.getErrors().isEmpty())) {
                        grrMainController.updateGrrViewData();
                        grrMainController.updateGrrSummaryAndDetail();
                    }
                    windowProgressTipController.closeDialog();
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
            RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);

//            Job job = new Job(ParamKeys.GRR_VIEW_DATA_JOB_PIPELINE);
//            job.addProcessMonitorListener(event -> {
//            });
//            Map<String, Object> paramMap = Maps.newHashMap();
//            List<String> projectNameList = envService.findActivatedProjectName();
//            List<TestItemWithTypeDto> testItemWithTypeDtoList = this.buildSelectTestItemWithTypeData(selectedItemDto);
//            paramMap.put(ParamKeys.PROJECT_NAME_LIST, projectNameList);
//            paramMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
//            SearchConditionDto conditionDto = this.initSearchConditionDto();
//            conditionDto.setSelectedTestItemDtos(selectedItemDto);
//            paramMap.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, conditionDto);
//            updateGrrPreference(conditionDto);
//            manager.doJobASyn(job, new JobDoComplete() {
//                @Override
//                public void doComplete(Object returnValue) {
//                    try {
//                        Platform.runLater(() -> {
//                            if (returnValue == null) {
//                                //todo message tip
//                                return;
//                            }
//                            GrrParamDto grrParamDto = grrMainController.getGrrParamDto();
//                            refreshPartOrAppraiserListView(grrParamDto);
//                            if (grrParamDto != null && (grrParamDto.getErrors() == null || grrParamDto.getErrors().isEmpty())) {
//                                grrMainController.updateGrrViewData();
//                                grrMainController.updateGrrSummaryAndDetail();
//                            } else {
//                                System.out.println(returnValue);
//                            }
//                        });
//                    } catch (ApplicationException excption) {
//                        excption.printStackTrace();
//                    }
//
//                }
//            }, paramMap, grrMainController);
        }
    }

    private void updateGrrPreference(SearchConditionDto searchConditionDto) {
        GrrPreferenceDto grrPreferenceDto = new GrrPreferenceDto();
        grrPreferenceDto.setPart(searchConditionDto.getPart());
        grrPreferenceDto.setAppraiser(searchConditionDto.getAppraiser());
        grrPreferenceDto.setPartInt(searchConditionDto.getPartInt());
        grrPreferenceDto.setAppraiserInt(searchConditionDto.getAppraiserInt());
        grrPreferenceDto.setTrialInt(searchConditionDto.getTrialInt());

        UserPreferenceDto<GrrPreferenceDto> userPreferenceDto = new UserPreferenceDto<>();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode("grr_param_preference");
        userPreferenceDto.setValue(grrPreferenceDto);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    private GrrPreferenceDto getGrrPreference() {
        String value = userPreferenceService.findPreferenceByUserId("grr_param_preference", envService.getUserName());
        if (StringUtils.isNotBlank(value)) {
            return mapper.fromJson(value, GrrPreferenceDto.class);
        } else {
            return null;
        }
    }

    private SearchConditionDto initSearchConditionDto() {
        searchConditionDto = new SearchConditionDto();
        searchConditionDto.setPart(partCombox.getValue());
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
            searchConditionDto.setAppraiser(appraiserCombox.getValue());
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
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("UI_GRR_ANALYSIS_ITEM_EMPTY"));
            return false;
        }

        if (!GrrValidateUtil.validateResult(partTxt, appraiserTxt, trialTxt, partCombox)) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("UI_GRR_CONFIGURATION_INVALIDATE"));
            return false;
        }

        if (appraiserLbl.getGraphic() != null || partLbl.getGraphic() != null) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("UI_GRR_CONFIGURATION_INVALIDATE"));
            return false;
        }

        if (partListView.getItems().size() > 0 && partListView.getItems().size() < Integer.parseInt(partTxt.getText())) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("UI_GRR_PART_MAX_NUMBER_NOT_MATCH"));
            return false;
        }

        if ((appraiserCombox.getValue() != null) && (appraiserListView.getItems().size() > 0 && appraiserListView.getItems().size() < Integer.parseInt(appraiserTxt.getText()))) {
            RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                    GrrFxmlAndLanguageUtils.getString("UI_GRR_APPRAISER_MAX_NUMBER_NOT_MATCH"));
            return false;
        }

        return true;
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

    private List<TestItemWithTypeDto> initSelectedItemDto() {
        List<TestItemWithTypeDto> selectTestItemDtos = Lists.newLinkedList();
        initSelectTestItemDtos.clear();
        if (itemTable.getItems() != null) {
            for (ItemTableModel model : itemTable.getItems()) {
                if (model.getSelector().isSelected()) {
                    selectTestItemDtos.add(model.getItemDto());
                    initSelectTestItemDtos.add(model.getItemDto());
                }
            }
        }
        return selectTestItemDtos;
    }

    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(GrrFxmlAndLanguageUtils.getString(ResourceMassages.GRR_CONFIG_IMPORT));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(null);

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

    private void exportLeftConfig() {
        List<String> selectedItems = this.getSelectedItem();
        if (checkSubmitParam(selectedItems.size())) {
            GrrLeftConfigDto leftConfigDto = new GrrLeftConfigDto();
            leftConfigDto.setItems(selectedItems);
            leftConfigDto.setPart(searchConditionDto.getPart());
            leftConfigDto.setAppraiser(searchConditionDto.getAppraiser());
            leftConfigDto.setPartInt(searchConditionDto.getPartInt());
            leftConfigDto.setAppraiserInt(searchConditionDto.getAppraiserInt());
            leftConfigDto.setTrialInt(searchConditionDto.getTrialInt());
            leftConfigDto.setParts(searchConditionDto.getParts());
            leftConfigDto.setAppraisers(searchConditionDto.getAppraisers());
            leftConfigDto.setBasicSearchs(searchTab.getBasicSearch());
            if (searchTab.getAdvanceText().getText() != null) {
                leftConfigDto.setAdvanceSearch(searchTab.getAdvanceText().getText());
            }

            String str = System.getProperty("user.home");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(GrrFxmlAndLanguageUtils.getString(ResourceMassages.GRR_CONFIG_EXPORT));
            fileChooser.setInitialDirectory(new File(str));
            fileChooser.setInitialFileName("GrrConfig.json");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON", "*.json")
            );
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                leftConfigService.exportGrrConfig(leftConfigDto, file);
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
        conditionList.remove("");
        List<String> testItemList = getSelectedItem();
        List<String> conditionTestItemList = Lists.newArrayList();
        TimePatternDto timePatternDto = envService.findActivatedTemplate().getTimePatternDto();
        List<String> timeKeys = Lists.newArrayList();
        String timePattern = null;
        if(timePatternDto != null) {
            timeKeys = timePatternDto.getTimeKeys();
            timePattern = timePatternDto.getPattern();
        }
        if (DAPStringUtils.isNotBlank(partCombox.getValue())) {
            conditionTestItemList.add(partCombox.getValue());
        }
        if (DAPStringUtils.isNotBlank(appraiserCombox.getValue())) {
            conditionTestItemList.add(appraiserCombox.getValue());
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

    public SearchConditionDto getSearchConditionDto() {
        return searchConditionDto;
    }

    public void setSearchConditionDto(SearchConditionDto searchConditionDto) {
        this.searchConditionDto = searchConditionDto;
    }

    public List<TestItemWithTypeDto> getInitSelectTestItemDtos() {
        return initSelectTestItemDtos;
    }

    public void setInitSelectTestItemDtos(List<TestItemWithTypeDto> initSelectTestItemDtos) {
        this.initSelectTestItemDtos = initSelectTestItemDtos;
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

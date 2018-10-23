package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.yield.dto.YieldAnalysisConfigDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldLeftConfigDto;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.model.ItemTableModel;
import com.dmsoft.firefly.plugin.yield.service.impl.YieldExportServiceImpl;
import com.dmsoft.firefly.plugin.yield.service.impl.YieldLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.yield.service.impl.YieldSettingServiceImpl;
import com.dmsoft.firefly.plugin.yield.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.UIConstant;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.message.IMessageManager;
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
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class YieldExportController {
    private static final String STICKY_ON_TOP_CODE = "stick_on_top";
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
    private SplitPane split;
    @FXML
    private ComboBox<String> configComboBox;
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
    private YieldSettingServiceImpl settingService = RuntimeContext.getBean(YieldSettingServiceImpl.class);
    private YieldExportServiceImpl yieldExportService = new YieldExportServiceImpl();
    private YieldLeftConfigServiceImpl leftConfigService = new YieldLeftConfigServiceImpl();
 //   private Map<String, Color> colorMap = Maps.newHashMap();
    // cached items for user preference
    private List<String> stickyOnTopItems = Lists.newArrayList();
    private List<String> originalItems = Lists.newArrayList();
//    private SpcExportSettingController spcExportSettingController;
    @FXML
    private void initialize() {
        initBtnIcon();
        eachFile.setToggleGroup(group);
        eachFile.setSelected(true);
        allFile.setToggleGroup(group);
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
//
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
        select.setCellFactory(new Callback<TableColumn<ItemTableModel,CheckBox>, TableCell<ItemTableModel,CheckBox>>() {
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
        itemTable.setContextMenu(createTableRightMenu());
//        ndGroup.setText(String.valueOf(PropertiesResource.SPC_CONFIG_INTERVAL_NUMBER));
//        subGroup.setText(String.valueOf(PropertiesResource.SPC_CONFIG_SUBGROUP_SIZE));
//        ValidateRule rule = new ValidateRule();
//        rule.setMaxLength(SpcSettingValidateUtil.ANALYSIS_SETTING_MAX_INT);
//        rule.setPattern("^\\+?\\d*$");
//        rule.setErrorStyle("text-field-error");
//        rule.setMaxValue(D20);
//        rule.setMinValue(1d);
//        String[] params = new String[]{rule.getMinValue().toString(), rule.getMaxValue().toString()};
//        rule.setRangErrorMsg(SpcFxmlAndLanguageUtils.getString(ResourceMassages.RANGE_NUMBER_WARNING_MESSAGE, params));
//        rule.setEmptyErrorMsg(SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_VALIDATE_NOT_BE_EMPTY));
//        TextFieldWrapper.decorate(subGroup, rule);
//        TextFieldWrapper.decorate(ndGroup, rule);

        initEvent();
        initItemData();
    }
    private void initEvent() {
        browse.setOnAction(event -> {
            String str = System.getProperty("user.home");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Yield Config export");
            directoryChooser.setInitialDirectory(new File(str));
            File file = directoryChooser.showDialog(null);

            if (file != null) {
                locationPath.setText(file.getPath());
            }
        });
        importBtn.setOnAction(event -> importLeftConfig());

        viewData.setOnAction(event -> {
            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
                WindowMessageFactory.createWindowMessageHasOk(YieldFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT), YieldFxmlAndLanguageUtils.getString(ResourceMassages.EMPTY_ITEM));
                return;
            }
            buildViewData();
        });
//        setting.setOnAction(event -> {
//            initSpcExportSettingDialog();
//        });
//        export.setOnAction(event -> {
//            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
//                WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT), SpcFxmlAndLanguageUtils.getString(ResourceMassages.EMPTY_ITEM));
//                return;
//            }
//            if (subGroup.getStyleClass().contains("text-field-error") || ndGroup.getStyleClass().contains("text-field-error")) {
//                WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT), SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_ERROR_CONFIG));
//                return;
//            }
//            if (!searchTab.verifySearchTextArea()) {
//                return;
//            }
//            if (StringUtils.isEmpty(locationPath.getText())) {
//                WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT), SpcFxmlAndLanguageUtils.getString(ResourceMassages.EMPTY_PATH));
//                return;
//            }
//            StageMap.closeStage("spcExport");
//            String savePath = locationPath.getText() + "/SPC_" + getTimeString();
//            export(savePath, false);
//        });
//        print.setOnAction(event -> {
//            if (StringUtils.isEmpty(locationPath.getText())) {
//                WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT), SpcFxmlAndLanguageUtils.getString(ResourceMassages.EMPTY_PATH));
//                return;
//            }
//            if (getSelectedItem() == null || getSelectedItem().size() <= 0) {
//                WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT), SpcFxmlAndLanguageUtils.getString(ResourceMassages.EMPTY_ITEM));
//                return;
//            }
//            StageMap.closeStage("spcExport");
//            String savePath = locationPath.getText() + "/SPC_" + getTimeString();
//            export(savePath, true);
//        });
        //TODO : change cancel text
        cancel.setOnAction(event -> {
            StageMap.closeStage("yieldExport");
        });
    }
    private void initBtnIcon() {
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        TooltipUtil.installNormalTooltip(importBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.IMPORT_CONFIG));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        itemTab.setStyle("-fx-padding: 0 5 0 5");
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        configTab.setStyle("-fx-padding: 0 5 0 5");
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
    private boolean isFilterAndHasUslOrLsl(ItemTableModel itemTableModel) {
        return (StringUtils.isNotEmpty(itemTableModel.getItemDto().getLsl()) || StringUtils.isNotEmpty(itemTableModel.getItemDto().getUsl()))
                && (DAPStringUtils.isBlank(itemFilter.getTextField().getText()) || (DAPStringUtils.isNotBlank(itemFilter.getTextField().getText())
                && itemTableModel.getItem().toLowerCase().contains(itemFilter.getTextField().getText().toLowerCase())));
    }

    private boolean isFilterAndAll(ItemTableModel itemTableModel) {
        return itemTableModel.getItem().startsWith("") && (DAPStringUtils.isBlank(itemFilter.getTextField().getText())
                || (DAPStringUtils.isNotBlank(itemFilter.getTextField().getText()) && itemTableModel.getItem().toLowerCase().contains(itemFilter.getTextField().getText().toLowerCase())));
    }
    public void initYieldExportLeftConfig(YieldLeftConfigDto yieldLeftConfigDto) {
        if (yieldLeftConfigDto == null) {
            return;
        }
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
//
        ObservableList<String> primaryKeyList = FXCollections.observableArrayList();
        primaryKeyList.add("");
        for (String item : originalItems) {
            primaryKeyList.add(item);
        }
        configComboBox.setItems(primaryKeyList);
        if (primaryKeyList.size() > 0) {
            configComboBox.setValue(primaryKeyList.get(0));
        }

        searchTab.getAdvanceText().setText(yieldLeftConfigDto.getAdvanceSearch());
        searchTab.getGroup1().setValue(yieldLeftConfigDto.getAutoGroup1());
        searchTab.getGroup2().setValue(yieldLeftConfigDto.getAutoGroup2());
    }
    @SuppressWarnings("unchecked")
    private void importLeftConfig() {
        String str = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Yield config import");
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showOpenDialog(StageMap.getStage(ResourceMassages.PLATFORM_STAGE_MAIN));

        if (file != null) {
            YieldLeftConfigDto yieldLeftConfigDto = leftConfigService.importSpcConfig(file);
            if (yieldLeftConfigDto != null) {
                this.initYieldExportLeftConfig(yieldLeftConfigDto);
            } else {
                RuntimeContext.getBean(IMessageManager.class).showWarnMsg(
                        YieldFxmlAndLanguageUtils.getString(UIConstant.UI_MESSAGE_TIP_WARNING_TITLE),
                       YieldFxmlAndLanguageUtils.getString("IMPORT_EXCEPTION"));
            }
        }
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
                if (itemTable.getSelectionModel().getSelectedItem().getOnTop()) {
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
        MenuItem specSetting = new MenuItem(YieldFxmlAndLanguageUtils.getString(ResourceMassages.SPECIFICATION_SETTING));
        specSetting.setOnAction(event -> RuntimeContext.getBean(EventContext.class).pushEvent(new PlatformEvent(null, "Template_Show")));
        right.getItems().addAll(top, specSetting);
        return right;
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
    private void clearLeftConfig() {
        box.setSelected(false);
        for (ItemTableModel model : items) {
            model.getSelector().setValue(false);
        }
        configComboBox.setValue(null);
        searchTab.clearSearchTab();
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
            if (selectItemDto.size() > 50) {
                selectItemDto.removeAll(selectItemDto.subList(50, selectItemDto.size()));
            }

            JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
            context.put(ParamKeys.PROJECT_NAME_LIST, envService.findActivatedProjectName());
            context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, selectItemDto);
            JobPipeline jobPipeline = RuntimeContext.getBean(JobManager.class).getPipeLine(ParamKeys.SPC_EXPORT_VIEW_DATA);
            jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
                @Override
                public void doJob(JobContext context) {
                    dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
                    buildViewDataDialog(conditionTestItem);
                }
            });
            RuntimeContext.getBean(JobManager.class).fireJobASyn(jobPipeline, context);
        }

    }
    private void buildViewDataDialog(List<String> searchConditions) {
        Pane root;
        try {
            FXMLLoader fxmlLoader = YieldFxmlAndLanguageUtils.getLoaderFXML("view/export_view_data.fxml");
            ExportViewData controller = new ExportViewData();
            controller.setDataFrame(dataFrame);
            controller.setSearchConditions(searchConditions);
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("yieldExportViewData",
                    YieldFxmlAndLanguageUtils.getString(ResourceMassages.VIEW_DATA), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TextFieldWrapper;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.yield.model.ItemTableModel;
import com.dmsoft.firefly.plugin.yield.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
//    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
//    private JsonMapper mapper = JsonMapper.defaultMapper();
//    private SpcSettingServiceImpl settingService = RuntimeContext.getBean(SpcSettingServiceImpl.class);
//    private SpcExportServiceImpl spcExportService = new SpcExportServiceImpl();
//    private SpcLeftConfigServiceImpl leftConfigService = new SpcLeftConfigServiceImpl();
//    private Map<String, Color> colorMap = Maps.newHashMap();
//    // cached items for user preference
//    private List<String> stickyOnTopItems = Lists.newArrayList();
//    private List<String> originalItems = Lists.newArrayList();
//    private SpcExportSettingController spcExportSettingController;
    @FXML
    private void initialize() {
        initBtnIcon();
        eachFile.setToggleGroup(group);
        eachFile.setSelected(true);
        allFile.setToggleGroup(group);
        searchTab = new SearchTab();
        split.getItems().add(searchTab);

//        itemFilter.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_TEST_ITEM_PROMPT));
//        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
//            if (isFilterUslOrLsl) {
//                filteredList.setPredicate(this::isFilterAndHasUslOrLsl);
//            } else {
//                filteredList.setPredicate(this::isFilterAndAll);
//            }
//        });
//
//        // test item table init
//        itemTable.setOnMouseEntered(event -> {
//            itemTable.focusModelProperty();
//        });
//        if (itemTable.getSkin() != null) {
//            TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) itemTable.getSkin(), itemTable);
//        } else {
//            itemTable.skinProperty().addListener((ov, s1, s2) -> {
//                TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, itemTable);
//            });
//        }
//
//        // select column in test item table
//        box = new CheckBox();
//        box.setOnAction(event -> {
//            if (itemTable != null && itemTable.getItems() != null) {
//                for (ItemTableModel model : itemTable.getItems()) {
//                    if (isFilterUslOrLsl) {
//                        if (StringUtils.isNotEmpty(model.getItemDto().getLsl()) || StringUtils.isNotEmpty(model.getItemDto().getUsl())) {
//                            model.getSelector().setValue(box.isSelected());
//                        }
//                    } else {
//                        model.getSelector().setValue(box.isSelected());
//                    }
//                }
//            }
//        });
//        select.setGraphic(box);
//        select.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
//        select.setCellFactory(new Callback<TableColumn<ItemTableModel,CheckBox>, TableCell<ItemTableModel,CheckBox>>() {
//            @Override
//            public TableCell<ItemTableModel, CheckBox> call(TableColumn<ItemTableModel, CheckBox> param) {
//                return new TableCell<ItemTableModel, CheckBox>() {
//                    @Override
//                    protected void updateItem(CheckBox item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setStyle(null);
//                        if (!isEmpty()) {
//                            if (getTableRow() != null && getIndex() > -1) {
//                                if (getTableView().getItems().get(getIndex()).getOnTop()) {
//                                    this.setStyle("-fx-background-color: #dff0cf");
//                                }
//                            }
//                        }
//                        if (item == null) {
//                            super.setGraphic(null);
//                        } else {
//                            super.setGraphic(item);
//                        }
//                    }
//                };
//            }
//        });
//        Button is = new Button();
//        is.setPrefSize(22, 22);
//        is.setMinSize(22, 22);
//        is.setMaxSize(22, 22);
//        is.setOnMousePressed(event -> createPopMenu(is, event));
//        is.getStyleClass().add("filter-normal");
//
//        item.setText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.TEST_ITEM));
//        item.setGraphic(is);
//        item.getStyleClass().add("filter-header");
//        item.setCellValueFactory(cellData -> cellData.getValue().itemDtoProperty());
//        item.setCellFactory(new Callback<TableColumn<ItemTableModel, TestItemWithTypeDto>, TableCell<ItemTableModel, TestItemWithTypeDto>>() {
//            public TableCell<ItemTableModel, TestItemWithTypeDto> call(TableColumn<ItemTableModel, TestItemWithTypeDto> param) {
//                return new TableCell<ItemTableModel, TestItemWithTypeDto>() {
//                    @Override
//                    public void updateItem(TestItemWithTypeDto item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setStyle(null);
//                        if (!isEmpty()) {
//                            if (getTableRow() != null && getIndex() > -1) {
//                                if (item.getTestItemType().equals(TestItemType.ATTRIBUTE) && getTableView().getItems().get(getIndex()).getOnTop()) {
//                                    this.setStyle("-fx-text-fill: #009bff; -fx-background-color: #dff0cf");
//                                } else if (item.getTestItemType().equals(TestItemType.ATTRIBUTE)) {
//                                    this.setStyle("-fx-text-fill: #009bff");
//                                } else if (getTableView().getItems().get(getIndex()).getOnTop()) {
//                                    this.setStyle("-fx-background-color: #dff0cf");
//                                }
//                            }
//                            setText(item.getTestItemName());
//                        } else {
//                            setText(null);
//                        }
//                    }
//                };
//            }
//        });
//        item.widthProperty().addListener((ov, w1, w2) -> {
//            Platform.runLater(() -> is.relocate(w2.doubleValue() - 21, 0));
//        });
//        item.setComparator((o1, o2) -> {
//            boolean o1OnTop = stickyOnTopItems.contains(o1.getTestItemName());
//            boolean o2OnTop = stickyOnTopItems.contains(o2.getTestItemName());
//            if (o1OnTop == o2OnTop) {
//                return -o2.getTestItemName().compareTo(o1.getTestItemName());
//            } else if (o1OnTop) {
//                return -1;
//            } else {
//                return 1;
//            }
//        });
//        item.sortTypeProperty().addListener((ov, sort1, sort2) -> {
//            if (sort2.equals(TableColumn.SortType.DESCENDING)) {
//                item.setComparator((o1, o2) -> {
//                    boolean o1OnTop = stickyOnTopItems.contains(o1.getTestItemName());
//                    boolean o2OnTop = stickyOnTopItems.contains(o2.getTestItemName());
//                    if (o1OnTop == o2OnTop) {
//                        return -o2.getTestItemName().compareTo(o1.getTestItemName());
//                    } else if (o1OnTop) {
//                        return 1;
//                    } else {
//                        return -1;
//                    }
//                });
//            } else {
//                item.setComparator((o1, o2) -> {
//                    boolean o1OnTop = stickyOnTopItems.contains(o1.getTestItemName());
//                    boolean o2OnTop = stickyOnTopItems.contains(o2.getTestItemName());
//                    if (o1OnTop == o2OnTop) {
//                        return -o2.getTestItemName().compareTo(o1.getTestItemName());
//                    } else if (o1OnTop) {
//                        return -1;
//                    } else {
//                        return 1;
//                    }
//                });
//            }
//        });
//        itemTable.setContextMenu(createTableRightMenu());
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
//
//        initEvent();
//        initItemData();
    }

    private void initBtnIcon() {
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        TooltipUtil.installNormalTooltip(importBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.IMPORT_CONFIG));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        itemTab.setStyle("-fx-padding: 0 5 0 5");
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        configTab.setStyle("-fx-padding: 0 5 0 5");
    }


}

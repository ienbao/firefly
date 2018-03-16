/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.grr.model.ItemTableModel;
import com.dmsoft.firefly.plugin.grr.model.ListViewModel;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

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
    private ComboBox partCombox;
    @FXML
    private ComboBox appraiserCombox;
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

    @FXML
    private void initialize() {
        searchTab = new SearchTab();
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
                partList.add(new ListViewModel(value, false));
            });
            partListView.setItems(partList);
        });

        this.appraiserCombox.valueProperty().addListener((observable, oldValue, newValue) -> {
            Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), newValue.toString());
            values.forEach(value -> {
                appraiserList.add(new ListViewModel(value, false));
            });
            appraiserListView.setItems(appraiserList);
        });


    }

    private void initListView(ListView<ListViewModel> listView) {
        listView.setCellFactory(e -> new ListCell<ListViewModel>() {
            @Override
            public void updateItem(ListViewModel item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    HBox cell;
                    CheckBox checkBox = new CheckBox();
                    if (item.isIsChecked()) {
                        checkBox.setSelected(true);
                    } else {
                        checkBox.setSelected(false);
                    }
                    checkBox.setOnAction(event -> {
                        item.setIsChecked(checkBox.isSelected());
                    });
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
            export();
            StageMap.closeStage("grrExport");

        });
        print.setOnAction(event -> {
            StageMap.closeStage("grrExport");

        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("grrExport");
        });
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
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_setting.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrSetting", "Grr Setting", root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void export() {


    }
}

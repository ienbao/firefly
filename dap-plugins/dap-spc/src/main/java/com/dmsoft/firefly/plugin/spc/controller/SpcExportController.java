package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

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
    private TextField locationPath;

    @FXML
    private SplitPane split;
    private SearchTab searchTab;
    private CheckBox box;

    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private ContextMenu pop;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);

    @FXML
    private void initialize() {
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
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Spc Config export");
            fileChooser.setInitialDirectory(new File(str));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excl", "*.xlsl")
            );
            Stage fileStage = null;
            File file = fileChooser.showSaveDialog(fileStage);

            if (file != null) {
                locationPath.setText(file.getPath());
            }
        });

        viewData.setOnAction(event -> {

        });
        setting.setOnAction(event -> {

        });
        export.setOnAction(event -> {
            StageMap.closeStage("spcExport");

        });
    }

    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            MenuItem all = new MenuItem("All Test Items");
            all.setOnAction(event -> filteredList.setPredicate(p -> p.getItem().startsWith("")));
            MenuItem show = new MenuItem("Test Items with USL/LSL");
            show.setOnAction(event -> filteredList.setPredicate(p -> StringUtils.isNotEmpty(p.getItemDto().getLsl()) || StringUtils.isNotEmpty(p.getItemDto().getUsl())));
            pop.getItems().addAll(all, show);
        }
        pop.show(is, e.getScreenX(), e.getScreenY());
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

}

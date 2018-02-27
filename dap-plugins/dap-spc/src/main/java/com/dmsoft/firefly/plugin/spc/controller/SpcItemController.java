/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.searchcombobox.ISearchComboBoxController;
import com.dmsoft.firefly.gui.components.searchcombobox.SearchComboBox;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.SpcServiceStatsResultDto;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;


/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcItemController implements Initializable {
    @FXML
    private TextField itemFilter;
    @FXML
    private Button analysisBtn;
    @FXML
    private Button importBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Tab itemTab;
    @FXML
    private Tab configTab;
    @FXML
    private Tab timeTab;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, String> item;
    @FXML
    private TableView itemTable;
    @FXML
    private Tab basicTab;
    @FXML
    private Tab advanceTab;
    @FXML
    private Button groupAdd;
    @FXML
    private Button groupRemove;
    @FXML
    private VBox basicSearch;
    @FXML
    private TextArea advanceText;
    @FXML
    private Button help;

    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);


    private SpcMainController spcMainController;
    private ContextMenu pop;

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
        basicSearch.getChildren().add(new BasicSearchPane());
        itemFilter.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().contains(itemFilter.getText()))
        );
        this.initComponentEvent();
        itemTable.setOnMouseEntered(event -> {
            itemTable.focusModelProperty();
        });
        CheckBox box = new CheckBox();
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
        is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        is.setOnMousePressed(event -> createPopMenu(is, event));

        Label label = new Label("Test Item");
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(label);
        hBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            hBox.getChildren().add(is);
        });
        hBox.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            hBox.getChildren().remove(is);
        });
        item.setGraphic(hBox);
        item.setCellValueFactory(cellData -> cellData.getValue().itemProperty());
        initItemData();
    }

    private void initBtnIcon() {
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        saveBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        timeTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_timer_normal.png")));
        basicTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        advanceTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_advance_search_normal.png")));
        groupAdd.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_new_template_normal.png")));
        groupRemove.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_all_normal.png")));
//        addSearch.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_add_normal.png")));
    }

    private ContextMenu createPopMenu(Button is, MouseEvent e) {
        if (pop == null) {
            pop = new ContextMenu();
            MenuItem all = new MenuItem("All Test Items");
            all.setOnAction(event -> filteredList.setPredicate(p -> p.getItem().startsWith("")));
            MenuItem show = new MenuItem("Test Items with USL/LSL");
            show.setOnAction(event -> filteredList.setPredicate(p -> p.getItemDto().getLsl() != null || p.getItemDto().getUsl() != null));
            pop.getItems().addAll(all, show);
        }
        pop.show(is, e.getScreenX(), e.getScreenY());
        return pop;
    }

    private void initComponentEvent() {
        groupAdd.setOnAction(event -> basicSearch.getChildren().add(new BasicSearchPane()));
        groupRemove.setOnAction(event -> basicSearch.getChildren().clear());
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
        help.setOnAction(event -> buildAdvanceHelpDia());
    }

    private void initItemData() {
//        EnvService envService = RuntimeContext.getBean(EnvService.class);
//        List<TestItemDto> itemDtos = envService.findTestItem();
        List<TestItemDto> itemDtos = Lists.newArrayList();
        for (int i = 0; i < 40; i++) {
            TestItemDto dto = new TestItemDto();
            dto.setTestItemName("item" + i);
            itemDtos.add(dto);
        }
        if (itemDtos != null) {
            for (TestItemDto dto : itemDtos) {
                ItemTableModel tableModel = new ItemTableModel(dto);
                items.add(tableModel);
            }
            itemTable.setItems(personSortedList);
            personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
        }
    }

    private void getAnalysisBtnEvent() {
        //todo find spc statistical Result from service
//        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
//        SpcSearchConfigDto spcSearchConfigDto = new SpcSearchConfigDto();
//        List<SpcServiceStatsResultDto> spcServiceStatsResultDtoList = spcService.findStatisticalResult(searchConditionDtoList,spcSearchConfigDto);
        List<SpcServiceStatsResultDto> spcServiceStatsResultDtoList = initData();
        if (spcServiceStatsResultDtoList == null) {
            return;
        }
        spcMainController.setStatisticalResultData(spcServiceStatsResultDtoList);
    }


    @Deprecated
    private List<SpcServiceStatsResultDto> initData() {
        List<SpcServiceStatsResultDto> spcServiceStatsResultDtoList = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            SpcServiceStatsResultDto statisticalResultDto = new SpcServiceStatsResultDto();
            statisticalResultDto.setItemName("itemName" + i);
            statisticalResultDto.setCondition("itemName > 22");
            statisticalResultDto.setSamples("343.2");
            statisticalResultDto.setAvg("32.2");
            statisticalResultDto.setMax("312");
            statisticalResultDto.setMin("34");
            statisticalResultDto.setStDev("124");
            statisticalResultDto.setLsl("35");
            statisticalResultDto.setUsl("21");
            statisticalResultDto.setCenter("53");
            statisticalResultDto.setRange("13");
            statisticalResultDto.setLcl("452");
            statisticalResultDto.setUcl("323");
            statisticalResultDto.setKurtosis("234");
            statisticalResultDto.setCpk("234");
            statisticalResultDto.setSkewness("6");
            statisticalResultDto.setCa("43.5");
            statisticalResultDto.setCp("35.76");
            statisticalResultDto.setCpl("34.7");
            statisticalResultDto.setCpu("324.67");
            statisticalResultDto.setWithinPPM("324.6");
            statisticalResultDto.setOverallPPM("343.65");
            statisticalResultDto.setPp("342.76");
            statisticalResultDto.setPpk("34.5");
            statisticalResultDto.setPpl("343.5");
            statisticalResultDto.setPpu("324.87");
            spcServiceStatsResultDtoList.add(statisticalResultDto);
        }
        return spcServiceStatsResultDtoList;
    }

    private void buildAdvanceHelpDia() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_ADVANCE_SEARCH_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel(ViewResource.SPC_ADVANCE_SEARCH_VIEW_ID, ResourceBundleUtils.getString(ResourceMassages.ADVANCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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

    public List<String> getSearch() {
        List<String> search = Lists.newArrayList();
        if (basicSearch.getChildren().size() > 0) {
            for (Node node : basicSearch.getChildren()) {
                if (node instanceof SearchComboBox) {
                    search.add(((SearchComboBox) node).getCondition());
                }
            }
        }
        //todo
        String advance = advanceText.getText();
        return search;
    }
}

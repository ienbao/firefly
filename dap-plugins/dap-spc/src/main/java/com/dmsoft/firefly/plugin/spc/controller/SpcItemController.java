/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
    @FXML
    private ComboBox group1;
    @FXML
    private ComboBox group2;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private SpcMainController spcMainController;
    private ContextMenu pop;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);

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
//        List<TestItemDto> itemDtos = Lists.newArrayList();
//        for (int i = 0; i < 40; i++) {
//            TestItemDto dto = new TestItemDto();
//            dto.setTestItemName("item" + i);
//            itemDtos.add(dto);
//        }
//        List<TestItemWithTypeDto> itemDtos = envService.findTestItems();
//        if (itemDtos != null) {
//            for (TestItemWithTypeDto dto : itemDtos) {
//                ItemTableModel tableModel = new ItemTableModel(dto);
//                items.add(tableModel);
//            }
//            itemTable.setItems(personSortedList);
//            personSortedList.comparatorProperty().bind(itemTable.comparatorProperty());
//        }
    }

    private void getAnalysisBtnEvent() {
        //todo find spc statistical Result from service
//        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
//        SpcSearchConfigDto spcSearchConfigDto = new SpcSearchConfigDto();
//        List<SpcStatsDto> spcStatsDtoList = spcService.findStatisticalResult(searchConditionDtoList,spcSearchConfigDto);
        List<SpcStatsDto> spcStatsDtoList = initData();
        if (spcStatsDtoList == null) {
            return;
        }
        spcMainController.setStatisticalResultData(spcStatsDtoList);
    }


    @Deprecated
    private List<SpcStatsDto> initData() {
        List<SpcStatsDto> spcStatsDtoList = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            SpcStatsDto statisticalResultDto = new SpcStatsDto();
            statisticalResultDto.setKey("key" + i);
            SpcStatsResultDto spcStatsResultDto = new SpcStatsResultDto();
            statisticalResultDto.setStatsResultDto(spcStatsResultDto);
            statisticalResultDto.setItemName("itemName" + i);
            statisticalResultDto.setCondition("itemName > 22");
            statisticalResultDto.getStatsResultDto().setSamples("343.2");
            statisticalResultDto.getStatsResultDto().setAvg("32.2");
            statisticalResultDto.getStatsResultDto().setMax("312");
            statisticalResultDto.getStatsResultDto().setMin("34");
            statisticalResultDto.getStatsResultDto().setStDev("124");
            statisticalResultDto.getStatsResultDto().setLsl("35");
            statisticalResultDto.getStatsResultDto().setUsl("21");
            statisticalResultDto.getStatsResultDto().setCenter("53");
            statisticalResultDto.getStatsResultDto().setRange("13");
            statisticalResultDto.getStatsResultDto().setLcl("452");
            statisticalResultDto.getStatsResultDto().setUcl("323");
            statisticalResultDto.getStatsResultDto().setKurtosis("234");
            statisticalResultDto.getStatsResultDto().setCpk("234");
            statisticalResultDto.getStatsResultDto().setSkewness("6");
            statisticalResultDto.getStatsResultDto().setCa("43.5");
            statisticalResultDto.getStatsResultDto().setCp("35.76");
            statisticalResultDto.getStatsResultDto().setCpl("34.7");
            statisticalResultDto.getStatsResultDto().setCpu("324.67");
            statisticalResultDto.getStatsResultDto().setWithinPPM("324.6");
            statisticalResultDto.getStatsResultDto().setOverallPPM("343.65");
            statisticalResultDto.getStatsResultDto().setPp("342.76");
            statisticalResultDto.getStatsResultDto().setPpk("34.5");
            statisticalResultDto.getStatsResultDto().setPpl("343.5");
            statisticalResultDto.getStatsResultDto().setPpu("324.87");
            spcStatsDtoList.add(statisticalResultDto);
        }
        return spcStatsDtoList;
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
        if (basicTab.isSelected()) {
            if (basicSearch.getChildren().size() > 0) {
                for (Node node : basicSearch.getChildren()) {
                    if (node instanceof BasicSearchPane) {
                        search.add(((BasicSearchPane) node).getSearch());
                    }
                }
            }
        } else if (advanceTab.isSelected()) {
            //todo
            StringBuilder advancedInput = new StringBuilder();
            advancedInput.append(advanceText.getText());
            List<String> autoCondition1 = Lists.newArrayList();
            List<String> autoCondition2 = Lists.newArrayList();
            if (!StringUtils.isBlank(group1.getValue().toString())) {
                Set<String> valueList = dataService.findUniqueTestData(envService.findActivatedProjectName(), group1.getValue().toString());
                if (valueList != null && !valueList.isEmpty()) {
                    for (String value : valueList) {
                        String condition1 = "\"" + group1.getValue().toString() + "\"" + " = " + "\"" + value + "\"";
                        if (StringUtils.isBlank(advancedInput.toString())) {
                            autoCondition1.add(condition1);
                        } else {
                            autoCondition1.add(advancedInput.toString() + " & " + condition1);
                        }
                    }
                }
            }
            if (!StringUtils.isBlank(group2.getValue().toString())) {
                Set<String> valueList = dataService.findUniqueTestData(envService.findActivatedProjectName(), group2.getValue().toString());
                if (valueList != null && !valueList.isEmpty()) {
                    if (autoCondition1.isEmpty()) {
                        for (String value : valueList) {
                            String condition1 = "\"" + group2.getValue().toString() + "\"" + " = " + "\"" + value + "\"";
                            if (StringUtils.isBlank(advancedInput.toString())) {
                                autoCondition2.add(condition1);
                            } else {
                                autoCondition2.add(advancedInput.toString() + " & " + condition1);
                            }
                        }
                    } else {
                        for (String condition : autoCondition1) {
                            for (String value : valueList) {
                                String condition1 = "\"" + group2.getValue().toString() + "\"" + " = " + "\"" + value + "\"";
                                autoCondition2.add(condition + " & " + condition1);
                            }
                        }
                    }
                }
            }
            if (autoCondition1.isEmpty() && autoCondition2.isEmpty()) {
                search.add(advancedInput.toString());
            } else {
                if (autoCondition1.size() > autoCondition2.size() && !autoCondition1.isEmpty()) {
                    search.addAll(autoCondition1);
                } else {
                    if (!autoCondition2.isEmpty()) {
                        search.addAll(autoCondition2);
                    }
                }
            }
        }
        return search;
    }
}

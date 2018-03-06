/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.searchcombobox.SearchComboBox;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.dto.BasicSearchDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcLeftConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcLeftConfigServiceImpl;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

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
    private Tab basicTab;
    @FXML
    private Tab advanceTab;
    @FXML
    private Button groupAdd;
    @FXML
    private Button groupRemove;
    @FXML
    private ScrollPane scroll;
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
    @FXML
    private TextField subGroup;
    @FXML
    private TextField ndGroup;
    private CheckBox box;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();
    private FilteredList<ItemTableModel> filteredList = items.filtered(p -> p.getItem().startsWith(""));
    private SortedList<ItemTableModel> personSortedList = new SortedList<>(filteredList);

    private SpcMainController spcMainController;
    private ContextMenu pop;

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private SpcLeftConfigServiceImpl leftConfigService = new SpcLeftConfigServiceImpl();

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
        basicSearch.getChildren().add(new BasicSearchPane("Group1"));
        itemFilter.getTextField().setPromptText("Test Item");
        itemFilter.getTextField().textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(p -> p.getItem().contains(itemFilter.getTextField().getText()))
        );
        this.initComponentEvent();
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
//        is.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));

        item.setText("Test Item");
        item.setGraphic(is);
        item.getStyleClass().add("filter-header");
        item.setCellValueFactory(cellData -> cellData.getValue().itemDtoProperty());
        initItemData();
    }

    private void initBtnIcon() {
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
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
            show.setOnAction(event -> filteredList.setPredicate(p -> StringUtils.isNotEmpty(p.getItemDto().getLsl()) || StringUtils.isNotEmpty(p.getItemDto().getUsl())));
            pop.getItems().addAll(all, show);
        }
        pop.show(is, e.getScreenX(), e.getScreenY());
        return pop;
    }

    private void initComponentEvent() {
        groupAdd.setOnAction(event -> basicSearch.getChildren().add(new BasicSearchPane("Group" + (basicSearch.getChildren().size() + 1))));
        groupRemove.setOnAction(event -> basicSearch.getChildren().clear());
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
        help.setOnAction(event -> buildAdvanceHelpDia());
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
//        List<TestItemDto> itemDtos = Lists.newArrayList();
//        for (int i = 0; i < 40; i++) {
//            TestItemDto dto = new TestItemDto();
//            dto.setTestItemName("item" + i);
//            itemDtos.add(dto);
//        }
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

    private void buildAdvanceHelpDia() {
        FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_ADVANCE_SEARCH_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel(ViewResource.SPC_ADVANCE_SEARCH_VIEW_ID, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ADVANCE), root, getResource("css/platform_app.css").toExternalForm());
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
     * get searchs
     *
     * @return list of search
     */
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
                    for (String title : spcLeftConfigDto.getBasicSearchs().keySet()) {
                        List<BasicSearchDto> basicSearchDtos = spcLeftConfigDto.getBasicSearchs().get(title);
                        BasicSearchPane basicSearchPane = new BasicSearchPane(title);
                        if (basicSearchDtos != null && basicSearchDtos.size() > 0) {
                            basicSearchDtos.forEach(basicSearchDto -> {
                                basicSearchPane.setSearch(basicSearchDto.getTestItem(), basicSearchDto.getOperator(), basicSearchDto.getValue());
                            });
                        }
                        basicSearch.getChildren().add(basicSearchPane);
                    }
                }
                ndGroup.setText(spcLeftConfigDto.getNdNumber());
                subGroup.setText(spcLeftConfigDto.getSubGroup());
                advanceText.setText(spcLeftConfigDto.getAdvanceSearch());
                group1.setValue(spcLeftConfigDto.getAutoGroup1());
                group2.setValue(spcLeftConfigDto.getAutoGroup2());
            }

        }
    }

    private void exportLeftConfig() {
        SpcLeftConfigDto leftConfigDto = new SpcLeftConfigDto();
        leftConfigDto.setItems(getSelectedItem());
        if (basicSearch.getChildren().size() > 0) {
            LinkedHashMap<String, List<BasicSearchDto>> basicSearchDtos = Maps.newLinkedHashMap();

            for (Node node : basicSearch.getChildren()) {
                if (node instanceof BasicSearchPane) {
                    BasicSearchPane basicSearchPane = ((BasicSearchPane) node);
                    if (basicSearchPane.getChildren().size() > 0) {
                        List<BasicSearchDto> dtos = Lists.newArrayList();
                        for (Node n : basicSearchPane.getChildren()) {
                            if (n instanceof SearchComboBox) {
                                BasicSearchDto basicSearchDto = new BasicSearchDto();
                                basicSearchDto.setTestItem(((SearchComboBox) n).getTestItem());
                                basicSearchDto.setOperator(((SearchComboBox) n).getOperator());
                                basicSearchDto.setValue(((SearchComboBox) n).getValue());
                                dtos.add(basicSearchDto);
                            }
                        }
                        basicSearchDtos.put(basicSearchPane.getTitle(), dtos);
                    }
                }
            }
            leftConfigDto.setBasicSearchs(basicSearchDtos);
        }
        if (advanceText.getText() != null) {
            leftConfigDto.setAdvanceSearch(advanceText.getText().toString());
        }
        leftConfigDto.setNdNumber(ndGroup.getText());
        leftConfigDto.setSubGroup(subGroup.getText());
        if (group1.getValue() != null) {
            leftConfigDto.setAutoGroup1(group1.getValue().toString());
        }
        if (group1.getValue() != null) {
            leftConfigDto.setAutoGroup2(group2.getValue().toString());
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
        basicSearch.getChildren().clear();
        subGroup.setText(null);
        ndGroup.setText(null);
        advanceText.setText(null);
        group1.setValue(null);
        group2.setValue(null);
    }
}

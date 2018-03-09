package com.dmsoft.firefly.gui.components.searchtab;

import com.dmsoft.firefly.gui.components.searchcombobox.SearchComboBox;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.ResourceMassages;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static com.google.common.io.Resources.getResource;

/**
 * Created by GuangLi on 2018/3/9.
 */
public class SearchTabController {
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

    private ObservableList<String> groupItem = FXCollections.observableArrayList();

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);

    @FXML
    private void initialize() {
        initBtnIcon();
        basicSearch.getChildren().add(new BasicSearchPane("Group1"));
        initEvent();
        initItemData();
    }

    private void initBtnIcon() {
        basicTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        advanceTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_advance_search_normal.png")));
        groupAdd.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_new_template_normal.png")));
        groupRemove.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_all_normal.png")));
    }

    private void initEvent() {
        groupAdd.setOnAction(event -> basicSearch.getChildren().add(new BasicSearchPane("Group" + (basicSearch.getChildren().size() + 1))));
        groupRemove.setOnAction(event -> basicSearch.getChildren().clear());
        help.setOnAction(event -> buildAdvanceHelpDia());
    }

    private void initItemData() {
        groupItem.clear();
        List<TestItemWithTypeDto> itemDtos = envService.findTestItems();
        if (itemDtos != null) {
            for (TestItemWithTypeDto dto : itemDtos) {
                groupItem.add(dto.getTestItemName());
            }
        }
        group1.setItems(groupItem);
        group2.setItems(groupItem);
    }

    private void buildAdvanceHelpDia() {
        FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/advance.fxml");
        Pane root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel("advance", FxmlAndLanguageUtils.getString(ResourceMassages.ADVANCE), root, getResource("css/redfall/main.css").toExternalForm());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public List<String> getConditionTestItem() {
        List<String> conditionList = getSearch();
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
                conditionTestItemList.add(conditionTestItem);
            }
        }
        return conditionTestItemList;
    }

    public void clearSearchTab(){
        basicSearch.getChildren().clear();
        advanceText.setText(null);
        group1.setValue(null);
        group2.setValue(null);
    }

    public LinkedHashMap<String, List<BasicSearchDto>> getBasicSearch(){
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
            return basicSearchDtos;
        }
        return null;
    }

    public void setBasicSearch(LinkedHashMap<String, List<BasicSearchDto>> basicSearchDtoMaps){
        if (basicSearchDtoMaps != null && basicSearchDtoMaps.size() > 0) {
            for (String title : basicSearchDtoMaps.keySet()) {
                List<BasicSearchDto> basicSearchDtos = basicSearchDtoMaps.get(title);
                BasicSearchPane basicSearchPane = new BasicSearchPane(title);
                if (basicSearchDtos != null && basicSearchDtos.size() > 0) {
                    basicSearchDtos.forEach(basicSearchDto -> {
                        basicSearchPane.setSearch(basicSearchDto.getTestItem(), basicSearchDto.getOperator(), basicSearchDto.getValue());
                    });
                }
                basicSearch.getChildren().add(basicSearchPane);
            }
        }
    }

    public TextArea getAdvanceText() {
        return advanceText;
    }

    public ComboBox getGroup1() {
        return group1;
    }

    public ComboBox getGroup2() {
        return group2;
    }
}

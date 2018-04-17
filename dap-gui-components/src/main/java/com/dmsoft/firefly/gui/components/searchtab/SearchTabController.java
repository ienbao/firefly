package com.dmsoft.firefly.gui.components.searchtab;

import com.dmsoft.firefly.gui.components.searchcombobox.SearchComboBox;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
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
import javafx.scene.control.*;
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
 * Updated by Can Guan on 2018/3/23
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
    private ComboBox<String> group1;
    @FXML
    private ComboBox<String> group2;
    @FXML
    private Label autoDivideLbl;

    private ObservableList<String> groupItem = FXCollections.observableArrayList();

    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService dataService = RuntimeContext.getBean(SourceDataService.class);
    private boolean isMulti = true;

    @FXML
    private void initialize() {
        initBtnIcon();
        if (isMulti) {
            basicSearch.getChildren().add(new BasicSearchPane("Group1"));
        } else {
            basicSearch.getChildren().add(new BasicSearchPane(false));
        }
        initEvent();
        initItemData();
    }

    private void initBtnIcon() {
        basicTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        basicTab.setStyle("-fx-padding: 0 5 0 5");
        basicTab.setTooltip(new Tooltip(FxmlAndLanguageUtils.getString("BASIC_SEARCH")));

        advanceTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_advance_search_normal.png")));
        advanceTab.setStyle("-fx-padding: 0 5 0 5");
        advanceTab.setTooltip(new Tooltip(FxmlAndLanguageUtils.getString("ADVANCE_SEARCH")));

        groupAdd.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_new_template_normal.png")));
        TooltipUtil.installNormalTooltip(groupAdd, FxmlAndLanguageUtils.getString("ADD_GROUP"));
        groupRemove.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_all_normal.png")));
        TooltipUtil.installNormalTooltip(groupRemove, FxmlAndLanguageUtils.getString("CLEAR_GROUP"));
        help.getStyleClass().add("message-tip-question");
        help.setStyle("-fx-background-color: #0096ff");

        TooltipUtil.installNormalTooltip(help, FxmlAndLanguageUtils.getString("ADVANCE"));

    }

    private void initEvent() {
        groupAdd.setOnAction(event -> basicSearch.getChildren().add(new BasicSearchPane("Group" + (basicSearch.getChildren().size() + 1))));
        groupRemove.setOnAction(event -> {
            basicSearch.getChildren().clear();
            if (isMulti) {
                basicSearch.getChildren().add(new BasicSearchPane("Group" + (basicSearch.getChildren().size() + 1)));
            } else {
                basicSearch.getChildren().add(new BasicSearchPane(false));
            }
        });
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
        groupItem.add(0, "");
        group1.setItems(groupItem);
        group2.setItems(groupItem);
    }

    private void buildAdvanceHelpDia() {
        FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/advance.fxml");
        Pane root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("advance", FxmlAndLanguageUtils.getString(CommonResourceMassages.ADVANCE), root);
            stage.toFront();
            stage.setResizable(false);
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
            if (group1.getValue() != null && !StringUtils.isBlank(group1.getValue())) {
                Set<String> valueList = dataService.findUniqueTestData(envService.findActivatedProjectName(), group1.getValue());
                if (valueList != null && !valueList.isEmpty()) {
                    for (String value : valueList) {
                        String condition1 = "\"" + group1.getValue() + "\"" + " = " + "\"" + value + "\"";
                        if (StringUtils.isBlank(advancedInput.toString())) {
                            autoCondition1.add(condition1);
                        } else {
                            autoCondition1.add(advancedInput.toString() + " & " + condition1);
                        }
                    }
                }
            }
            if (group2.getValue() != null && !StringUtils.isBlank(group2.getValue())) {
                Set<String> valueList = dataService.findUniqueTestData(envService.findActivatedProjectName(), group2.getValue());
                if (valueList != null && !valueList.isEmpty()) {
                    if (autoCondition1.isEmpty()) {
                        for (String value : valueList) {
                            String condition1 = "\"" + group2.getValue() + "\"" + " = " + "\"" + value + "\"";
                            if (StringUtils.isBlank(advancedInput.toString())) {
                                autoCondition2.add(condition1);
                            } else {
                                autoCondition2.add(advancedInput.toString() + " & " + condition1);
                            }
                        }
                    } else {
                        for (String condition : autoCondition1) {
                            for (String value : valueList) {
                                String condition1 = "\"" + group2.getValue() + "\"" + " = " + "\"" + value + "\"";
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

    /**
     * method to get test items form condition
     *
     * @return list of test items
     */
    public List<String> getConditionTestItem() {
        List<String> conditionList = getSearch();
        List<String> conditionTestItemList = Lists.newArrayList();
        List<String> timeKeys = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
        String timePattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        FilterUtils filterUtils = new FilterUtils(timeKeys, timePattern);
        for (String condition : conditionList) {
            Set<String> conditionTestItemSet = filterUtils.parseItemNameFromConditions(condition);
            conditionTestItemList.addAll(conditionTestItemSet);
        }
        return conditionTestItemList;
    }

    /**
     * method to clear search tab
     */
    public void clearSearchTab() {
        basicSearch.getChildren().clear();
        advanceText.setText(null);
        group1.setValue(null);
        group2.setValue(null);
    }

    /**
     * method to get basic search dto
     *
     * @return map
     */
    public LinkedHashMap<String, List<BasicSearchDto>> getBasicSearch() {
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

    /**
     * method to set basic search condition dto
     *
     * @param basicSearchDtoMaps map of basic search dto
     */
    public void setBasicSearch(LinkedHashMap<String, List<BasicSearchDto>> basicSearchDtoMaps) {
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

    /**
     * method to get basic search dto
     *
     * @return map
     */
    public List<BasicSearchDto> getOneBasicSearch() {
        if (basicSearch.getChildren().size() > 0) {
            List<BasicSearchDto> basicSearchDtos = Lists.newLinkedList();

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
                        basicSearchDtos.addAll(dtos);
                    }
                }
            }
            return basicSearchDtos;
        }
        return null;
    }

    /**
     * method to set basic search condition dto
     *
     * @param basicSearchDtoMaps map of basic search dto
     */
    public void setOneBasicSearch(List<BasicSearchDto> basicSearchDtoMaps) {
        BasicSearchPane basicSearchPane = new BasicSearchPane(false);
        if (basicSearchDtoMaps != null && basicSearchDtoMaps.size() > 0) {
            basicSearchDtoMaps.forEach(basicSearchDto -> basicSearchPane.setSearch(basicSearchDto.getTestItem(), basicSearchDto.getOperator(), basicSearchDto.getValue()));
        }
        basicSearch.getChildren().add(basicSearchPane);
    }

    public TextArea getAdvanceText() {
        return advanceText;
    }

    public Tab getAdvanceTab() {
        return advanceTab;
    }

    public ComboBox getGroup1() {
        return group1;
    }

    public ComboBox getGroup2() {
        return group2;
    }

    public Label getAutoDivideLbl() {
        return autoDivideLbl;
    }

    /**
     * method to hide group add
     */
    public void hiddenGroupAdd() {
        groupAdd.setVisible(false);
    }

    /**
     * method to hide auto divided
     */
    public void hiddenAutoDivided() {
        group1.setVisible(false);
        group2.setVisible(false);
        autoDivideLbl.setVisible(false);
    }

    public void setMulti(boolean multi) {
        isMulti = multi;
    }
}

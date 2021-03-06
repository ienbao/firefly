/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.components.searchtab;

import com.dmsoft.firefly.core.utils.DapApplicationContextUtils;
import com.dmsoft.firefly.gui.components.searchcombobox.ISearchComboBoxController;
import com.dmsoft.firefly.gui.components.searchcombobox.SearchComboBox;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by Guang.Li on 2018/2/27.
 */
public class BasicSearchPane extends VBox {
    private Button addSearch;
    private Label groupTitle;
    private EnvService envService = DapApplicationContextUtils.getContext().getBean(EnvService.class);
    private SourceDataService dataService = DapApplicationContextUtils.getContext().getBean(SourceDataService.class);
    private List<String> timeKey;
    private String pattern;
    private boolean isMulti = true;

    /**
     * constructor
     */
    public BasicSearchPane(boolean isMulti) {
        this.isMulti = isMulti;
        if (envService.findActivatedTemplate() != null && envService.findActivatedTemplate().getTimePatternDto() != null) {
            timeKey = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
            pattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        }
        this.setStyle("-fx-border-color: #DCDCDC; -fx-border-width: 0 0 1 0");

        addSearch = new Button();
        this.getChildren().add(addSearch);
        VBox.setVgrow(addSearch, Priority.ALWAYS);
        VBox.setMargin(addSearch, new Insets(10, 10, 10, 8));
        addSearch.setPrefSize(160, 22);
        addSearch.setMaxWidth(Double.MAX_VALUE);

        addSearch.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/add.svg")));
        addSearch.setOnAction(event -> addBasicSearch());
    }

    /**
     * constructor
     *
     * @param title title str
     */
    public BasicSearchPane(String title) {
        if (envService.findActivatedTemplate() != null && envService.findActivatedTemplate().getTimePatternDto() != null) {
            timeKey = envService.findActivatedTemplate().getTimePatternDto().getTimeKeys();
            pattern = envService.findActivatedTemplate().getTimePatternDto().getPattern();
        }
        this.setStyle("-fx-border-color: #DCDCDC; -fx-border-width: 0 0 1 0");
        groupTitle = new Label(title);
        this.getChildren().add(groupTitle);
        VBox.setVgrow(groupTitle, Priority.ALWAYS);
        VBox.setMargin(groupTitle, new Insets(10, 10, 0, 8));
        groupTitle.setPrefSize(160, 22);
        groupTitle.setMaxWidth(Double.MAX_VALUE);

        addSearch = new Button();
        this.getChildren().add(addSearch);
        VBox.setVgrow(addSearch, Priority.ALWAYS);
        VBox.setMargin(addSearch, new Insets(10, 10, 10, 8));
        addSearch.setPrefSize(160, 22);
        addSearch.setMaxWidth(Double.MAX_VALUE);

        addSearch.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/add.svg")));
        addSearch.setOnAction(event -> addBasicSearch());

    }

    private SearchComboBox addBasicSearch() {
        SearchComboBox basicSearchCom = new SearchComboBox(new ISearchComboBoxController() {
            @Override
            public ObservableList<String> getValueForTestItem(String testItem) {
                if (StringUtils.isNotEmpty(testItem)) {
                    Set<String> values = dataService.findUniqueTestData(envService.findActivatedProjectName(), testItem);
                    if (values != null && values.size() > 0) {
                        return FXCollections.observableArrayList(values);
                    }
                }
                return FXCollections.observableArrayList();
            }

            @Override
            public ObservableList<String> getTestItems() {
                List<String> item = envService.findTestItemNames();
                return FXCollections.observableArrayList(item);
            }

            @Override
            public boolean isTimeKey(String testItem) {
                if (testItem != null && timeKey != null && timeKey.contains(testItem)) {
                    return true;
                }
                return false;
            }

            @Override
            public String getTimePattern() {
                return pattern;
            }
        });
        basicSearchCom.getCloseBtn().setOnAction(e -> {
            this.getChildren().remove(basicSearchCom);
            if (isMulti && this.getChildren().size() == 2 && ((VBox) this.getParent()).getChildren().size() > 1) {
                ((VBox) this.getParent()).getChildren().remove(this);
            }
        });
        if (this.getChildren().size() > 0) {
            basicSearchCom.setPadding(new Insets(10, 10, 0, 8));
            this.getChildren().add(this.getChildren().size() - 1, basicSearchCom);
            VBox.setVgrow(basicSearchCom, Priority.ALWAYS);
        }
        return basicSearchCom;
    }

    /**
     * get search string
     *
     * @return search
     */
    public String getSearch() {
        StringBuffer search = new StringBuffer();
        if (this.getChildren().size() > 0) {
            for (Node node : this.getChildren()) {
                if (node instanceof SearchComboBox) {
                    if (StringUtils.isNotBlank(((SearchComboBox) node).getCondition())) {
                        search.append(((SearchComboBox) node).getCondition());
                        search.append(" & ");
                    }
                }
            }
        }
        if (search.length() > 3) {
            search.delete(search.length() - 3, search.length());
            return search.toString();
        } else {
            return "";
        }
    }

    /**
     * new search
     *
     * @param item     item
     * @param operator operator
     * @param value    value
     */
    public void setSearch(String item, String operator, String value) {
        SearchComboBox searchComboBox = addBasicSearch();
        searchComboBox.setTestItem(item);
        searchComboBox.setOperator(operator);
        searchComboBox.setValue(value);
    }

    public String getTitle() {
        if (groupTitle == null) {
            return "Group1";
        }
        return groupTitle.getText();
    }

    public void setTitle(String title) {
        groupTitle.setText(title);
    }
}

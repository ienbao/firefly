package com.dmsoft.firefly.gui.components.searchtab;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * Created by GuangLi on 2018/3/9.
 */
public class SearchTab extends VBox {
    private SearchTabController controller;

    /**
     * constructor
     */
    public SearchTab() {
        this(true);
    }

    /**
     * constructor
     *
     * @param isMulti is multi or not
     */
    public SearchTab(boolean isMulti) {
        FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/search_tab.fxml");
        controller = new SearchTabController();
        controller.setMulti(isMulti);
        fxmlLoader.setController(controller);
        TabPane root = null;
        try {
            root = fxmlLoader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.getChildren().add(root);
    }

    public List<String> getSearch() {
        return controller.getSearch();
    }

    public List<String> getConditionTestItem() {
        return controller.getConditionTestItem();
    }

    public void clearSearchTab() {
        controller.clearSearchTab();
    }

    public TextArea getAdvanceText() {
        return controller.getAdvanceText();
    }

    public ComboBox<String> getGroup1() {
        return controller.getGroup1();
    }

    public ComboBox<String> getGroup2() {
        return controller.getGroup2();
    }

    public Label getAutoDivideLbl() {
        return controller.getAutoDivideLbl();
    }

    public void hiddenGroupAdd() {
        controller.hiddenGroupAdd();
    }

    public LinkedHashMap<String, List<BasicSearchDto>> getBasicSearch() {
        return controller.getBasicSearch();
    }

    public void setBasicSearch(LinkedHashMap<String, List<BasicSearchDto>> basicSearchDtoMaps) {
        controller.setBasicSearch(basicSearchDtoMaps);
    }
}

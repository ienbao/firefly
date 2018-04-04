package com.dmsoft.firefly.gui.components.searchtab;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


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

    /**
     * method to clear search tab
     */
    public void clearSearchTab() {
        controller.clearSearchTab();
    }

    public TextArea getAdvanceText() {
        return controller.getAdvanceText();
    }

    public boolean isAdvancedSelected() {
        return controller.getAdvanceTab().isSelected();
    }

    /**
     * method to verify advance text area and change style
     *
     * @return true : legal; false : illegal
     */
    public boolean verifySearchTextArea() {
        if (isAdvancedSelected()) {
            if (DAPStringUtils.isNotBlank(controller.getAdvanceText().getText())) {
                String text = controller.getAdvanceText().getText();
                boolean legal = FilterUtils.isLegal(text);
                if (!legal) {
                    if (!controller.getAdvanceText().getStyleClass().contains("text-area-error")) {
                        controller.getAdvanceText().getStyleClass().add("text-area-error");
                        TooltipUtil.installWarnTooltip(controller.getAdvanceText(),
                                FxmlAndLanguageUtils.getString(CommonResourceMassages.ILLEGAL_INPUT_SEARCH_CONDITION));
                    }
                    return false;
                } else {
                    Set<String> testItemNames = FilterUtils.parseItemNameFromConditions(text);
                    List<String> list = RuntimeContext.getBean(EnvService.class).findTestItemNames();
                    String errorTestItemName = null;
                    for (String testItemName : testItemNames) {
                        if (!list.contains(testItemName)) {
                            if (errorTestItemName == null) {
                                errorTestItemName = "" + testItemName + ", ";
                            }
                        }
                    }
                    if (errorTestItemName != null) {
                        if (!controller.getAdvanceText().getStyleClass().contains("text-area-error")) {
                            controller.getAdvanceText().getStyleClass().add("text-area-error");
                        }
                        TooltipUtil.installWarnTooltip(controller.getAdvanceText(),
                                FxmlAndLanguageUtils.getString(CommonResourceMassages.UNABLE_TO_FIND_TEST_ITEM) + errorTestItemName.substring(0, errorTestItemName.length() - 2));
                        return false;
                    }
                    controller.getAdvanceText().getStyleClass().removeAll("text-area-error");
                    TooltipUtil.uninstallWarnTooltip(controller.getAdvanceText());
                    return true;
                }
            } else {
                controller.getAdvanceText().getStyleClass().removeAll("text-area-error");
                TooltipUtil.uninstallWarnTooltip(controller.getAdvanceText());
                return true;
            }
        }
        return true;
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

    /**
     * method to hidden group add
     */
    public void hiddenGroupAdd() {
        controller.hiddenGroupAdd();
    }

    public LinkedHashMap<String, List<BasicSearchDto>> getBasicSearch() {
        return controller.getBasicSearch();
    }

    public List<BasicSearchDto> getOneBasicSearch() {
        return controller.getOneBasicSearch();
    }

    /**
     * method to search basic search
     *
     * @param basicSearchDtoMaps basic search dto map
     */
    public void setBasicSearch(LinkedHashMap<String, List<BasicSearchDto>> basicSearchDtoMaps) {
        controller.setBasicSearch(basicSearchDtoMaps);
    }

    /**
     * method to search one group basic search
     *
     * @param basicSearchDtoMaps basic search dto map
     */
    public void setOneBasicSearch(List<BasicSearchDto> basicSearchDtoMaps) {
        controller.setOneBasicSearch(basicSearchDtoMaps);
    }
}

package com.dmsoft.firefly.gui.components.dialog;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.google.common.collect.Lists;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

import static com.google.common.io.Resources.getResource;

/**
 * dialog for choose test item
 *
 * @author Can Guan
 */
public class ChooseTestItemDialog extends Stage {
    private ChooseTestItemPane mainPane;

    /**
     * constructor
     *
     * @param items         items
     * @param selectedItems selected items
     */
    public ChooseTestItemDialog(List<String> items, List<String> selectedItems) {
        this(items, selectedItems, 50);
    }

    /**
     * constructor
     *
     * @param items         items
     * @param selectedItems selected items
     * @param maxLength     maxlength
     */
    public ChooseTestItemDialog(List<String> items, List<String> selectedItems, int maxLength) {
        super(StageStyle.TRANSPARENT);
        mainPane = new ChooseTestItemPane(items, selectedItems, maxLength);
        WindowPane windowPane = new WindowPane(FxmlAndLanguageUtils.getString(CommonResourceMassages.CHOOSE_ITEM), mainPane);
        windowPane.setStage(this);
        Scene scene = new Scene(windowPane);
        scene.getStylesheets().addAll(WindowFactory.checkStyles());
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        windowPane.init();
        mainPane.getOkBtn().setOnAction(event -> this.close());
        Image image = new Image("/images/desktop_mac_logo.png");
        this.getIcons().add(image);
    }

    /**
     * method to reset items
     *
     * @param items         items
     * @param selectedItems selected items
     */
    public void resetItems(List<String> items, List<String> selectedItems) {
        mainPane.getItems().clear();
        List<String> selecteds = Lists.newArrayList();
        if (selectedItems != null) {
            selecteds.addAll(selectedItems);
            if (selecteds.size() > mainPane.getMaxLength()) {
                selecteds.removeAll(selecteds.subList(mainPane.getMaxLength(), selecteds.size()));
            }
        }
        if (items != null) {
            for (String s : items) {
                ChooseTestItemModel item = new ChooseTestItemModel(s, selecteds.contains(s));
                item.selectedProperty().addListener((ov, b1, b2) -> mainPane.handleNumberChangeEvent());
                mainPane.getItems().add(item);
            }
        }
    }

    /**
     * method to reset selected items
     *
     * @param selectedItems selected items
     */
    public void resetSelectedItems(List<String> selectedItems) {
        int j = 0;
        for (int i = 0, max = mainPane.getItems().size(); i < max; i++) {
            if (selectedItems != null && selectedItems.contains(mainPane.getItems().get(i).itemNameProperty().getValue())) {
                j++;
                if (j > mainPane.getMaxLength()) {
                    continue;
                }
                mainPane.getItems().get(i).selectedProperty().set(true);
            } else {
                mainPane.getItems().get(i).selectedProperty().set(false);
            }
        }
    }

    public Button getOkBtn() {
        return mainPane.getOkBtn();
    }

    /**
     * method to get selected items
     *
     * @return list of selected items
     */
    public List<String> getSelectedItems() {
        return mainPane.getSelectedItems();
    }
}

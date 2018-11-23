package com.dmsoft.firefly.gui.chooseDialog;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.google.common.collect.Lists;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

/**
 * dialog for choose test item
 *
 * @author Can Guan
 */
public class ChooseTestItemDialog extends Stage {
    private ChooseDialogController chooseDialogController;
//    private ChooseTestItemPane mainPane;

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
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/choose_dialog1.fxml");
            Pane root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            chooseDialogController.ChooseTestItemPane(items, selectedItems, maxLength);
//            mainPane = new ChooseTestItemPane(items, selectedItems, maxLength);
            WindowPane windowPane = new WindowPane(FxmlAndLanguageUtils.getString(CommonResourceMassages.CHOOSE_ITEM), root);
            windowPane.setStage(this);
            Scene scene = new Scene(windowPane);
            scene.getStylesheets().addAll(WindowFactory.checkStyles());
            scene.setFill(Color.TRANSPARENT);
            this.setScene(scene);
            this.initModality(Modality.APPLICATION_MODAL);
            this.setResizable(false);
            windowPane.init();
            chooseDialogController.getOkBtn().setOnAction(event -> this.close());
            Image image = new Image("/images/desktop_mac_logo.png");
            this.getIcons().add(image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to reset items
     *
     * @param items         items
     * @param selectedItems selected items
     */
    public void resetItems(List<String> items, List<String> selectedItems) {
        chooseDialogController.getItems().clear();
        List<String> selecteds = Lists.newArrayList();
        if (selectedItems != null) {
            selecteds.addAll(selectedItems);
            if (selecteds.size() > chooseDialogController.getMaxLength()) {
                selecteds.removeAll(selecteds.subList(chooseDialogController.getMaxLength(), selecteds.size()));
            }
        }
        if (items != null) {
            for (String s : items) {
                ChooseTestItemModel item = new ChooseTestItemModel(s, selecteds.contains(s));
                item.selectedProperty().addListener((ov, b1, b2) -> chooseDialogController.handleNumberChangeEvent());
                chooseDialogController.getItems().add(item);
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
        for (int i = 0, max = chooseDialogController.getItems().size(); i < max; i++) {
            if (selectedItems != null && selectedItems.contains(chooseDialogController.getItems().get(i).itemNameProperty().getValue())) {
                j++;
                if (j > chooseDialogController.getMaxLength()) {
                    continue;
                }
                chooseDialogController.getItems().get(i).selectedProperty().set(true);
            } else {
                chooseDialogController.getItems().get(i).selectedProperty().set(false);
            }
        }
    }

    public Button getOkBtn() {
        return chooseDialogController.getOkBtn();
    }

    /**
     * method to get selected items
     *
     * @return list of selected items
     */
    public List<String> getSelectedItems() {
        return chooseDialogController.getSelectedItems();
    }

    public void removeSelectedItems(List<String> selectedItems) {
        int j = 0;
        int max = chooseDialogController.getItems().size();
        for (int i = max - 1; i >= 0; i--) {
            if (selectedItems != null && selectedItems.contains(chooseDialogController.getItems().get(i).itemNameProperty().getValue())) {
                j++;
                if (j > chooseDialogController.getMaxLength()) {
                    continue;
                }
                chooseDialogController.getItems().remove(i);
            } else {
                chooseDialogController.getItems().get(i).selectedProperty().set(false);
            }
        }
    }
}

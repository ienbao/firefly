package com.dmsoft.firefly.gui.components.dialog;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
    /**
     * constructor
     *
     * @param items         items
     * @param selectedItems selected items
     * @param maxLength     maxlength
     */
    public ChooseTestItemDialog(List<String> items, List<String> selectedItems, int maxLength) {
        super(StageStyle.TRANSPARENT);
        ChooseTestItemPane mainPane = new ChooseTestItemPane(items, selectedItems, maxLength);

        WindowPane windowPane = new WindowPane(FxmlAndLanguageUtils.getString(CommonResourceMassages.CHOOSE_ITEM), mainPane);
        windowPane.setStage(this);
        Scene scene = new Scene(windowPane);
        scene.getStylesheets().addAll(WindowFactory.checkStyles(getResource("css/redfall/main.css").toExternalForm()));
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        windowPane.init();
        Image image = new Image("/images/desktop_mac_logo.png");
        this.getIcons().add(image);
    }
}

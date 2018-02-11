package com.dmsoft.firefly.gui.components.searchcombobox;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.ComboBox;

/**
 * skin combo box to set calendar
 *
 * @param <T> string
 */
public class SearchComboBoxSkin<T> extends ComboBoxListViewSkin<T> {
    /**
     * constructor
     *
     * @param comboBox combo box
     */
    public SearchComboBoxSkin(ComboBox<T> comboBox) {
        super(comboBox);
        arrowButton.setOnMouseReleased(event -> {
            if (arrowButton.getStyleClass().contains("arrow-calendar-button")) {
                comboBox.hide();
                event.consume();
            }
        });
    }
}

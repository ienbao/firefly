package com.dmsoft.firefly.gui.components.dialog;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 * choose item for choose item dialog
 */
public class ChooseTestItemModel {
    private ObservableValue<String> itemName;
    private SimpleBooleanProperty selected;

    /**
     * constructor
     *
     * @param itemName item name
     * @param selected selected or not
     */
    public ChooseTestItemModel(String itemName, boolean selected) {
        this.itemName = new SimpleStringProperty(itemName);
        this.selected = new SimpleBooleanProperty(selected);
    }

    /**
     * method to selected value
     *
     * @return
     */
    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * method to get item name property
     * @return item name property
     */
    public ObservableValue<String> itemNameProperty() {
        return itemName;
    }
}

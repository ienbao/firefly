package com.dmsoft.firefly.plugin.spc.charts.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by cherry on 2018/2/28.
 */
public class SimpleItemCheckModel {

    private SimpleStringProperty itemName;
    private SimpleBooleanProperty isChecked;

    /**
     * Constructor for SimpleItemCheckModel
     *
     * @param itemName  item name
     * @param isChecked is checked or not
     */
    public SimpleItemCheckModel(String itemName, boolean isChecked) {

        this.itemName = new SimpleStringProperty(itemName);
        this.isChecked = new SimpleBooleanProperty(isChecked);
    }

    /**
     * Get item name
     *
     * @return item name
     */
    public String getItemName() {
        return itemName.get();
    }

    /**
     * Set item name
     *
     * @param itemName item name
     */
    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    /**
     * Get check status
     *
     * @return check status
     */
    public boolean isIsChecked() {
        return isChecked.get();
    }

    /**
     * Set check value
     *
     * @param isChecked check value
     */
    public void setIsChecked(boolean isChecked) {
        this.isChecked.set(isChecked);
    }
}

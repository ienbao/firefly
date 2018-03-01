package com.dmsoft.firefly.plugin.spc.charts.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by cherry on 2018/2/28.
 */
public class SimpleItemCheckModel {

    private SimpleStringProperty itemName;
    private SimpleBooleanProperty isChecked;

    public SimpleItemCheckModel(String templateName, boolean isChecked) {

        this.itemName = new SimpleStringProperty(templateName);
        this.isChecked = new SimpleBooleanProperty(isChecked);
    }

    public String getItemName() {
        return itemName.get();
    }

    public SimpleStringProperty itemNameProperty() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public boolean isIsChecked() {
        return isChecked.get();
    }

    public SimpleBooleanProperty isCheckedProperty() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked.set(isChecked);
    }
}

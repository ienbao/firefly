package com.dmsoft.firefly.gui.model;

import com.dmsoft.firefly.gui.utils.TableCheckBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by GuangLi on 2018/3/8.
 */
public class ExportSettingModel {
    private TableCheckBox selector = new TableCheckBox();
    private StringProperty name;
    private StringProperty originalKey;


    /**
     * constructor
     *
     * @param name name
     * @param originalKey originalKey
     */
    public ExportSettingModel(String name, String originalKey) {

        this.name = new SimpleStringProperty(name);
        this.originalKey = new SimpleStringProperty(originalKey);
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }

    public String getName() {
        return name.get();
    }

    /**
     * method to set name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * method to get name property
     *
     * @return string property
     */
    public StringProperty nameProperty() {
        return name;
    }

    public String getOriginalKey() {
        return originalKey.get();
    }

    public StringProperty originalKeyProperty() {
        return originalKey;
    }

    public void setOriginalKey(String originalKey) {
        this.originalKey.set(originalKey);
    }
}

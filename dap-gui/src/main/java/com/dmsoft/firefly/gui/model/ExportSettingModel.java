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

    /**
     * constructor
     *
     * @param name name
     */
    public ExportSettingModel(String name) {

        this.name = new SimpleStringProperty(name);
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
}

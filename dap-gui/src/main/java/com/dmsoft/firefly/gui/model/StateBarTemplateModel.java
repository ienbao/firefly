package com.dmsoft.firefly.gui.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * model class for state bar
 *
 * @author Julia
 */
public class StateBarTemplateModel {
    private SimpleStringProperty templateName;
    private SimpleBooleanProperty isChecked;

    /**
     * constructor
     *
     * @param templateName template name
     * @param isChecked    is checked or not
     */
    public StateBarTemplateModel(String templateName, boolean isChecked) {
        this.templateName = new SimpleStringProperty(templateName);
        this.isChecked = new SimpleBooleanProperty(isChecked);
    }

    public String getTemplateName() {
        return templateName.get();
    }

    /**
     * method to set template name
     *
     * @param templateName template name
     */
    public void setTemplateName(String templateName) {
        this.templateName.set(templateName);
    }

    /**
     * method to get template name property
     *
     * @return string property
     */
    public SimpleStringProperty templateNameProperty() {
        return templateName;
    }

    public boolean isIsChecked() {
        return isChecked.get();
    }

    /**
     * method to set is checked or not
     *
     * @param isChecked new is checked
     */
    public void setIsChecked(boolean isChecked) {
        this.isChecked.set(isChecked);
    }

    public SimpleBooleanProperty isCheckedProperty() {
        return isChecked;
    }
}

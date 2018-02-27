package com.dmsoft.firefly.gui.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class StateBarTemplateModel {
    private SimpleStringProperty templateName;
    private SimpleBooleanProperty isChecked;

    public StateBarTemplateModel(String templateName, boolean isChecked) {
        this.templateName = new SimpleStringProperty(templateName);
        this.isChecked = new SimpleBooleanProperty(isChecked);
    }

    public String getTemplateName() {
        return templateName.get();
    }

    public SimpleStringProperty templateNameProperty() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName.set(templateName);
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

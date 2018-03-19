package com.dmsoft.firefly.plugin.grr.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ListViewModel {
    private SimpleStringProperty name;
    private SimpleBooleanProperty isChecked;
    private SimpleStringProperty errorMsg;

    public ListViewModel(String name, boolean isChecked, String errorMsg) {
        this.name = new SimpleStringProperty(name);
        this.isChecked = new SimpleBooleanProperty(isChecked);
        this.errorMsg = new SimpleStringProperty(errorMsg);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public String getErrorMsg() {
        return errorMsg.get();
    }

    public SimpleStringProperty errorMsgProperty() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg.set(errorMsg);
    }
}

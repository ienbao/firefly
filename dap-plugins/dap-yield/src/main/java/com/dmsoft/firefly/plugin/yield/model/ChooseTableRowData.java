package com.dmsoft.firefly.plugin.yield.model;

import com.dmsoft.firefly.plugin.yield.utils.TableCheckBox;
import javafx.beans.property.SimpleStringProperty;

public class ChooseTableRowData {
    private TableCheckBox selector = new TableCheckBox();
    private SimpleStringProperty value;

    public ChooseTableRowData(boolean isSelect, String value) {
        if (isSelect) {
            selector.setValue(true);
        }
        this.value = new SimpleStringProperty(value);
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public boolean isSelect(){
        return selector.isSelected();
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }
}

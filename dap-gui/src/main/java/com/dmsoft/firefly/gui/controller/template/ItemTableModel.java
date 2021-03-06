package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by GuangLi on 2018/2/8.
 */
public class ItemTableModel implements Comparable<TestItemWithTypeDto> {
    private TableCheckBox selector = new TableCheckBox();
    private StringProperty item;

    /**
     * constructor
     *
     * @param item item name
     */
    public ItemTableModel(String item) {
        this.item = new SimpleStringProperty(item);
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }

    public String getItem() {
        return item.get();
    }

    /**
     * method to set item
     *
     * @param item item name
     */
    public void setItem(String item) {
        this.item.set(item);
    }

    /**
     * method to get item property
     *
     * @return string property
     */
    public StringProperty itemProperty() {
        return item;
    }

    @Override
    public int compareTo(TestItemWithTypeDto o) {
        return item.get().compareToIgnoreCase(o.getTestItemName());
    }
}

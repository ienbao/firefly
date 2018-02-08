package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by GuangLi on 2018/2/8.
 */
public class ItemTableModel {
    private TableCheckBox selector = new TableCheckBox();
    private TestItemDto itemDto;
    private StringProperty item;

    public ItemTableModel(TestItemDto itemDto){
        this.itemDto = itemDto;
        this.item = new SimpleStringProperty(itemDto.getItemName());
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

    public StringProperty itemProperty() {
        return item;
    }

    public void setItem(String item) {
        this.item.set(item);
    }
}

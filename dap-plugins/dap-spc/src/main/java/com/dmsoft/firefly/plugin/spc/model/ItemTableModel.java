package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by GuangLi on 2018/2/8.
 */
public class ItemTableModel implements Comparable<TestItemWithTypeDto> {
    private TableCheckBox selector = new TableCheckBox();
    private TestItemWithTypeDto itemDto;
    private StringProperty item;

    public ItemTableModel(TestItemWithTypeDto itemDto) {
        this.itemDto = itemDto;
        this.item = new SimpleStringProperty(itemDto.getTestItemName());
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

    public void setItem(String item) {
        this.item.set(item);
    }

    public StringProperty itemProperty() {
        return item;
    }

    public TestItemWithTypeDto getItemDto() {
        return itemDto;
    }

    public void setItemDto(TestItemWithTypeDto itemDto) {
        this.itemDto = itemDto;
    }

    @Override
    public int compareTo(TestItemWithTypeDto o) {
        return item.get().compareToIgnoreCase(o.getTestItemName());
    }
}

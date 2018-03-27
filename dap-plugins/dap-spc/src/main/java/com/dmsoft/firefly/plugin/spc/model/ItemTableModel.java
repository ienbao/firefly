package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.utils.TableCheckBox;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by GuangLi on 2018/2/8.
 * Updated by Can Guan on 2018/3/22
 */
public class ItemTableModel {
    private TableCheckBox selector = new TableCheckBox();
    private SimpleObjectProperty<TestItemWithTypeDto> itemDto;
    private StringProperty item;
    private Boolean isOnTop = false;

    /**
     * constructor
     *
     * @param itemDto test item with type dto
     */
    public ItemTableModel(TestItemWithTypeDto itemDto) {
        this.itemDto = new SimpleObjectProperty<TestItemWithTypeDto>(itemDto);
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

    /**
     * method to set item
     *
     * @param item test item name
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

    public TestItemWithTypeDto getItemDto() {
        return itemDto.get();
    }

    /**
     * method to set test item with type dto
     *
     * @param itemDto test item dto
     */
    public void setItemDto(TestItemWithTypeDto itemDto) {
        this.itemDto.set(itemDto);
    }

    /**
     * method to get test item dto property
     *
     * @return simple object property for test item with type dto
     */
    public SimpleObjectProperty<TestItemWithTypeDto> itemDtoProperty() {
        return itemDto;
    }

    public Boolean getOnTop() {
        return isOnTop;
    }

    public void setOnTop(Boolean onTop) {
        isOnTop = onTop;
    }
}

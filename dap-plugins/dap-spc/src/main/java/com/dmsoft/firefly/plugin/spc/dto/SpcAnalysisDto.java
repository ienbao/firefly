/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;

import java.awt.*;

/**
 * Created by Ethan.Yang on 2018/2/5.
 */
public class SpcAnalysisDto extends AbstractValueObject {

    private Long id;
    private String itemName;
    private TestItemDto testItem;
    private String condition;

    private Color Background;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public TestItemDto getTestItem() {
        return testItem;
    }

    public void setTestItem(TestItemDto testItem) {
        this.testItem = testItem;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Color getBackground() {
        return Background;
    }

    public void setBackground(Color background) {
        Background = background;
    }
}

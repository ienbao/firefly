package com.dmsoft.firefly.sdk.dai.entity;

import org.bson.types.ObjectId;

/**
 * Created by cherry on 2018/1/16.
 */
public class TestItem {
    private ObjectId id;
    private String itemName;

    public TestItem() {
    }

    public TestItem(String itemName) {
        this.itemName = itemName;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}

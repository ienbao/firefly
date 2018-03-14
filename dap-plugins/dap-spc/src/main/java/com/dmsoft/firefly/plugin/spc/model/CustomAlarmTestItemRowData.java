/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.dto.CustomAlarmDto;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/3/14.
 */
public class CustomAlarmTestItemRowData {
    private SimpleStringProperty name;
    private List<CustomAlarmDto> customAlarmDtoList;
    /**
     * constructor
     * @param name testItem name
     * @param customAlarmDtoList list
     */
    public CustomAlarmTestItemRowData(String name, List<CustomAlarmDto> customAlarmDtoList){
        this.name = new SimpleStringProperty(name);
        this.customAlarmDtoList = customAlarmDtoList;
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

    public List<CustomAlarmDto> getCustomAlarmDtoList() {
        return customAlarmDtoList;
    }

    public void setCustomAlarmDtoList(List<CustomAlarmDto> customAlarmDtoList) {
        this.customAlarmDtoList = customAlarmDtoList;
    }
}

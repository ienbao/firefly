/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.dto.SpcViewDataDto;
import com.dmsoft.firefly.plugin.spc.utils.TableCheckBox;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/2/10.
 */
public class ViewDataRowData {
    private TableCheckBox selector = new TableCheckBox();
    private Map<String, SimpleStringProperty> rowDataMap = Maps.newHashMap();
    private List<String> dataList;

    public ViewDataRowData(SpcViewDataDto spcViewDataDto) {
        if (spcViewDataDto == null) {
            return;
        }
        dataList = Lists.newArrayList();
        spcViewDataDto.getTestData().forEach((name, value) -> {
            rowDataMap.put(name, new SimpleStringProperty((String) value));
            dataList.add((String) value);
        });
    }

    public Map<String, SimpleStringProperty> getRowDataMap() {
        return rowDataMap;
    }

    public void setRowDataMap(Map<String, SimpleStringProperty> rowDataMap) {
        this.rowDataMap = rowDataMap;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public boolean containsRex(String rex) {
        for (String value : dataList) {
            if (value.contains(rex)) {
                return true;
            }
        }
        return false;
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }
}

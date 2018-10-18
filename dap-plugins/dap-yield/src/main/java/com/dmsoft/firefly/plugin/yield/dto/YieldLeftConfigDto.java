package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.firefly.gui.components.searchtab.BasicSearchDto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class YieldLeftConfigDto implements Serializable {
    private List<String> items;
    private LinkedHashMap<String, List<BasicSearchDto>> basicSearchs;
    private String advanceSearch;
    private String autoGroup1;
    private String autoGroup2;
    private String primaryKey;
    private Integer topN;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public LinkedHashMap<String, List<BasicSearchDto>> getBasicSearchs() {
        return basicSearchs;
    }

    public void setBasicSearchs(LinkedHashMap<String, List<BasicSearchDto>> basicSearchs) {
        this.basicSearchs = basicSearchs;
    }

    public String getAdvanceSearch() {
        return advanceSearch;
    }

    public void setAdvanceSearch(String advanceSearch) {
        this.advanceSearch = advanceSearch;
    }

    public String getAutoGroup1() {
        return autoGroup1;
    }

    public void setAutoGroup1(String autoGroup1) {
        this.autoGroup1 = autoGroup1;
    }

    public String getAutoGroup2() {
        return autoGroup2;
    }

    public void setAutoGroup2(String autoGroup2) {
        this.autoGroup2 = autoGroup2;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getTopN() {
        return topN;
    }

    public void setTopN(Integer topN) {
        this.topN = topN;
    }
}

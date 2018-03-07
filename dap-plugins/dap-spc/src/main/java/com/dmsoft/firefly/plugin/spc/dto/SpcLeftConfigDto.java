package com.dmsoft.firefly.plugin.spc.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by GuangLi on 2018/3/5.
 */
public class SpcLeftConfigDto implements Serializable {
    private List<String> items;
    private LinkedHashMap<String, List<BasicSearchDto>> basicSearchs;
    private String advanceSearch;
    private String autoGroup1;
    private String autoGroup2;
    private String subGroup;
    private String ndNumber;

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

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public String getNdNumber() {
        return ndNumber;
    }

    public void setNdNumber(String ndNumber) {
        this.ndNumber = ndNumber;
    }
}

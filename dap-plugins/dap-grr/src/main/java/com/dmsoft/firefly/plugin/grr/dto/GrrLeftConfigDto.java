package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.firefly.gui.components.searchtab.BasicSearchDto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by GuangLi on 2018/3/5.
 */
public class GrrLeftConfigDto implements Serializable {
    private List<String> items;
    private List<BasicSearchDto> basicSearchs;
    private String advanceSearch;
    private String part;
    private String appraiser;
    private Integer partInt;
    private Integer appraiserInt;
    private Integer trialInt;
    private List<String> parts;
    private List<String> appraisers;


    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<BasicSearchDto> getBasicSearchs() {
        return basicSearchs;
    }

    public void setBasicSearchs(List<BasicSearchDto> basicSearchs) {
        this.basicSearchs = basicSearchs;
    }

    public String getAdvanceSearch() {
        return advanceSearch;
    }

    public void setAdvanceSearch(String advanceSearch) {
        this.advanceSearch = advanceSearch;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getAppraiser() {
        return appraiser;
    }

    public void setAppraiser(String appraiser) {
        this.appraiser = appraiser;
    }

    public Integer getPartInt() {
        return partInt;
    }

    public void setPartInt(Integer partInt) {
        this.partInt = partInt;
    }

    public Integer getAppraiserInt() {
        return appraiserInt;
    }

    public void setAppraiserInt(Integer appraiserInt) {
        this.appraiserInt = appraiserInt;
    }

    public Integer getTrialInt() {
        return trialInt;
    }

    public void setTrialInt(Integer trialInt) {
        this.trialInt = trialInt;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    public List<String> getAppraisers() {
        return appraisers;
    }

    public void setAppraisers(List<String> appraisers) {
        this.appraisers = appraisers;
    }
}

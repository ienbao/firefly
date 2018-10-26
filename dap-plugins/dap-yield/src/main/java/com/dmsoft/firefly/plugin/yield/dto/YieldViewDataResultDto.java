package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldViewDataResultDto extends AbstractValueObject {
    private String ItemName;
    private String primary;
    private List<String> resultlist;
    private List<String> FPYlist;
    private List<String> PASSlist;
    private List<String> Ntflist;
    private List<String> Nglist;
    private List<String> Totallist;




    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public List<String> getResultlist() {
        return resultlist;
    }

    public void setResultlist(List<String> resultlist) {
        this.resultlist = resultlist;
    }

    public List<String> getFPYlist() {
        return FPYlist;
    }

    public void setFPYlist(List<String> FPYlist) {
        this.FPYlist = FPYlist;
    }

    public List<String> getPASSlist() {
        return PASSlist;
    }

    public void setPASSlist(List<String> PASSlist) {
        this.PASSlist = PASSlist;
    }

    public List<String> getNtflist() {
        return Ntflist;
    }

    public void setNtflist(List<String> ntflist) {
        Ntflist = ntflist;
    }

    public List<String> getNglist() {
        return Nglist;
    }

    public void setNglist(List<String> nglist) {
        Nglist = nglist;
    }

    public List<String> getTotallist() {
        return Totallist;
    }

    public void setTotallist(List<String> totallist) {
        Totallist = totallist;
    }
}

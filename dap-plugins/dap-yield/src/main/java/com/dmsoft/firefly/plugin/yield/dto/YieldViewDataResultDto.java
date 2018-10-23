package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

public class YieldViewDataResultDto extends AbstractValueObject {
    private String ItemName;
    private String primary;
    private List<YieldViewDataDto> resultlist;
    private List<YieldViewDataDto> FPYlist;
    private List<YieldViewDataDto> PASSlist;
    private List<YieldViewDataDto> Ntflist;
    private List<YieldViewDataDto> Nglist;
    private List<YieldViewDataDto> Totallist;

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

    public List<YieldViewDataDto> getResultlist() {
        return resultlist;
    }

    public void setResultlist(List<YieldViewDataDto> resultlist) {
        this.resultlist = resultlist;
    }

        public List<YieldViewDataDto> getFPYlist() {
        return FPYlist;
    }

    public void setFPYlist(List<YieldViewDataDto> FPYlist) {
        this.FPYlist = FPYlist;
    }

    public List<YieldViewDataDto> getPASSlist() {
        return PASSlist;
    }

    public void setPASSlist(List<YieldViewDataDto> PASSlist) {
        this.PASSlist = PASSlist;
    }

    public List<YieldViewDataDto> getNtflist() {
        return Ntflist;
    }

    public void setNtflist(List<YieldViewDataDto> ntflist) {
        Ntflist = ntflist;
    }

    public List<YieldViewDataDto> getNglist() {
        return Nglist;
    }

    public void setNglist(List<YieldViewDataDto> nglist) {
        Nglist = nglist;
    }

    public List<YieldViewDataDto> getTotallist() {
        return Totallist;
    }

    public void setTotallist(List<YieldViewDataDto> totallist) {
        Totallist = totallist;
    }
}

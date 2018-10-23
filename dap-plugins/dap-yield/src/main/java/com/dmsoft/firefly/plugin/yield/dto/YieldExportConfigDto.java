package com.dmsoft.firefly.plugin.yield.dto;

import java.util.Map;

public class YieldExportConfigDto {
    private Map<String, Boolean> exportDataItem;
    private String performer;
    private String exportPath;
    private int digNum = 6;
    public Map<String, Boolean> getExportDataItem() {
        return exportDataItem;
    }

    public void setExportDataItem(Map<String, Boolean> exportDataItem) {
        this.exportDataItem = exportDataItem;
    }
    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public int getDigNum() {
        return digNum;
    }

    public void setDigNum(int digNum) {
        this.digNum = digNum;
    }
}

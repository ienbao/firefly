package com.dmsoft.firefly.plugin.spc.dto;

import java.util.Map;

/**
 * Created by simon.liu on 2017/7/27.
 */
public class SpcUserActionAttributesDto {
    private Map<String, Boolean> exportDataItem;
    private String performer;
    private String exportPath;
    private String exportType;

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

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
}

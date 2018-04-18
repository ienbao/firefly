package com.dmsoft.firefly.plugin.grr.utils.enums;

/**
 * Created by cherry on 2018/4/18.
 */
public enum GrrExportItemKey {

    EXPORT_DETAIL_SHEET("Export detail sheet of each selected items"),
    EXPORT_SOURCE_RESULT("Export source(and ANOVA) result"),
    EXPORT_CHART("Export charts"),
    EXPORT_R_PART_CHART("R&R Plot by Part"),
    EXPORT_R_APPRAISER_CHART("R&R Plot by Appraiser"),
    EXPORT_RANGE_CHART("Range Chart by Appraiser"),
    EXPORT_X_BAR_CHART("X-bar Chart by Appraiser"),
    EXPORT_PART_APPRAISER_CHART("Part*Appraiser Interaction"),
    EXPORT_COMPONENT_CHART("Components of Variation"),
    EXPORT_BASE_ON_TOLERANCE("Export GRR Result based on process tolerance"),
    EXPORT_BASE_ON_CONTRIBUTE("Export GRR Result based on system contribute");

    private String code;

    GrrExportItemKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}

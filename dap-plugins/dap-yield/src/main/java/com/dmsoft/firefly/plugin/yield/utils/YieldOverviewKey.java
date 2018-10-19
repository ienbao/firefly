package com.dmsoft.firefly.plugin.yield.utils;

public enum YieldOverviewKey {
    LSL("LSL"),
    USL("USL"),
    TOTALSAMPLES("TotalSamles"),
    FPYSAMPLES("FpySamples"),
    PASSSAMPLES("PassSamples"),
    NTFSAMPLES("NtfSamples"),
    NGSAMPLES("NgSamples"),
    FPYPER("FpyPercent"),
    NTFPER("NtfPercent"),
    NGPER("NgPercent"),


    EXCELLENT("Excellent"),
    ADEQUATE("Adequate"),
    MARGINAL("Marginal"),
    BAD("Bad");


    private String code;
    YieldOverviewKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static boolean isAbilityAlarmResultName(String resultName){
        switch (resultName) {
            case "FpyPercent":
                return true;
            case "NtfPercent":
                return true;
            case "NgPercent":
                return true;
            default:
                return false;
        }
    }

}

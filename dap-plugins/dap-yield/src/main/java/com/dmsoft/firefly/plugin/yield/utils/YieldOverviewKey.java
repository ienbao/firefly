package com.dmsoft.firefly.plugin.yield.utils;

public enum YieldOverviewKey {
    LSL("LSL"),
    USL("USL"),
    TOTALSAMPLES("TotalSamles"),
    FPYSAMPLES("FpySamples"),
    PASSSAMPLES("PassSamples"),
    NTFSAMPLES("NtfSamples"),
    NGSAMPLES("NgSamples"),
    FPYPER("%FPY"),
    NTFPER("%NTF"),
    NGPER("%NG"),


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
            case "%FPY":
                return true;
            case "%NTF":
                return true;
            case "%NG":
                return true;
            default:
                return false;
        }
    }

}

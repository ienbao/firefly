package com.dmsoft.firefly.plugin.yield.utils;

import com.google.common.collect.Lists;

import java.util.List;

public class YieldRefreshJudgeUtil {
    private static YieldRefreshJudgeUtil instance;
    private List<String> overViewSelectRowKeyListCache;
    private List<String> currentViewDataSelectRowKeyList = Lists.newArrayList();


    private List<String> statisticalModifyRowKeyList = Lists.newArrayList();


    /**
     * instance
     *
     * @return object
     */
    public static YieldRefreshJudgeUtil newInstance() {
        if (instance == null) {
            instance = new YieldRefreshJudgeUtil();
        }
        return instance;
    }

    public List<String> getCurrentViewDataSelectRowKeyList() {
        return currentViewDataSelectRowKeyList;
    }

    public void setCurrentViewDataSelectRowKeyList(List<String> currentViewDataSelectRowKeyList) {
        this.currentViewDataSelectRowKeyList = currentViewDataSelectRowKeyList;
    }

    public List<String> getOverViewSelectRowKeyListCache() {
        return overViewSelectRowKeyListCache;
    }

    public void setOverViewSelectRowKeyListCache(List<String> overViewSelectRowKeyListCache) {
        this.overViewSelectRowKeyListCache = overViewSelectRowKeyListCache;
    }

    /**
     * is need refresh
     *
     * @param statisticalModifyRowKeyList        statistical Modify row key
     * @return id need
     */
    public RefreshType refreshJudge(List<String> statisticalModifyRowKeyList) {
        this.statisticalModifyRowKeyList = statisticalModifyRowKeyList;
        if (statisticalModifyRowKeyList.size() == 0){
            return RefreshType.NOT_NEED_REFRESH;
        } else{
            return RefreshType.REFRESH_STATISTICAL_RESULT;
        }
    }

    public enum RefreshType {
        NOT_NEED_REFRESH("notNeedRefresh"),
        REFRESH_STATISTICAL_RESULT("refreshStatisticalResult"),
        REFRESH_CHART_RESULT("refreshChartResult"),
        REFRESH_ALL_ANALYSIS_RESULT("refreshAllAnalysisResult");

        private String code;

        RefreshType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        /**
         * Get judgeRuleType by code name.
         *
         * @param codeName the name of code
         * @return judgeRuleType
         */
        public static RefreshType getByCode(String codeName) {
            switch (codeName) {
                case "notNeedRefresh":
                    return NOT_NEED_REFRESH;
                case "refreshStatisticalResult":
                    return REFRESH_STATISTICAL_RESULT;
                case "refreshChartResult":
                    return REFRESH_CHART_RESULT;
                case "refreshAllAnalysisResult":
                    return REFRESH_ALL_ANALYSIS_RESULT;
                default:
                    return null;
            }
        }

    }


}

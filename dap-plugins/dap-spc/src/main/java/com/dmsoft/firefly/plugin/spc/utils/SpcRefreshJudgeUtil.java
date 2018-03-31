/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/3/18.
 */
public class SpcRefreshJudgeUtil {
    private static SpcRefreshJudgeUtil instance;
    private List<String> statisticalSelectRowKeyListCache;
    private List<String> viewDataSelectRowKeyListCache;

    private List<String> currentStatisticalSelectRowKeyList = Lists.newArrayList();
    private List<String> currentViewDataSelectRowKeyList = Lists.newArrayList();

    private List<String> statisticalModifyRowKeyList = Lists.newArrayList();

    private boolean viewDataIsBlank = true;

    /**
     * instance
     *
     * @return object
     */
    public static SpcRefreshJudgeUtil newInstance() {
        if (instance == null) {
            instance = new SpcRefreshJudgeUtil();
        }
        return instance;
    }


    /**
     * is need refresh
     *
     * @param currentStatisticalSelectRowKeyList current statistical select row key
     * @param currentViewDataSelectRowKeyList    current view data select row key
     * @param statisticalModifyRowKeyList        statistical Modify row key
     * @return id need
     */
    public RefreshType refreshJudge(List<String> currentStatisticalSelectRowKeyList, List<String> currentViewDataSelectRowKeyList,
                                    List<String> statisticalModifyRowKeyList) {
        this.currentStatisticalSelectRowKeyList = currentStatisticalSelectRowKeyList;
        this.currentViewDataSelectRowKeyList = currentViewDataSelectRowKeyList;
        this.statisticalModifyRowKeyList = statisticalModifyRowKeyList;


        if (!this.isStatisticalSelectRowChange() && statisticalModifyRowKeyList.size() == 0 && !this.isViewDataSelectRowChange()) {
            return RefreshType.NOT_NEED_REFRESH;
        } else if (currentStatisticalSelectRowKeyList.size() == 0 || (!this.resultSelectIsChange(currentStatisticalSelectRowKeyList, statisticalSelectRowKeyListCache)
                && !this.modifyRowContainSelectRow(statisticalModifyRowKeyList, currentStatisticalSelectRowKeyList) && !this.isViewDataSelectRowChange())) {
            return RefreshType.REFRESH_STATISTICAL_RESULT;
        } else if ((currentStatisticalSelectRowKeyList.size() != 0 && this.resultSelectIsChange(currentStatisticalSelectRowKeyList, statisticalSelectRowKeyListCache))
                && statisticalModifyRowKeyList.size() == 0 && (viewDataIsBlank || !this.isViewDataSelectRowChange())) {
            return RefreshType.REFRESH_CHART_RESULT;
        } else {
            return RefreshType.REFRESH_ALL_ANALYSIS_RESULT;
        }
    }

    private boolean isStatisticalSelectRowChange() {
        if ((currentStatisticalSelectRowKeyList.size() == 0
                || !this.resultSelectIsChange(currentStatisticalSelectRowKeyList, statisticalSelectRowKeyListCache))) {
            return false;
        }
        return true;
    }

    private boolean isViewDataSelectRowChange() {
        if (viewDataSelectRowKeyListCache == null || (currentViewDataSelectRowKeyList != null && viewDataSelectRowKeyListCache != null && !this.resultSelectIsChange(currentViewDataSelectRowKeyList, viewDataSelectRowKeyListCache))) {
            return false;
        }
        return true;
    }

    private boolean modifyRowContainSelectRow(List<String> modifyRowList, List<String> selectRowList) {
        for (String row : selectRowList) {
            if (modifyRowList.contains(row)) {
                return true;
            }
        }
        return false;
    }

    private boolean resultSelectIsChange(List<String> newList, List<String> oldList) {
        if (oldList == null) {
            if (newList.size() == 0) {
                return false;
            } else {
                return true;
            }
        }
        if (newList.size() != oldList.size()) {
            return true;
        }
        for (String oldRowKey : oldList) {
            if (!newList.contains(oldRowKey)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getStatisticalSelectRowKeyListCache() {
        return statisticalSelectRowKeyListCache;
    }

    public void setStatisticalSelectRowKeyListCache(List<String> statisticalSelectRowKeyListCache) {
        this.statisticalSelectRowKeyListCache = statisticalSelectRowKeyListCache;
    }

    public List<String> getViewDataSelectRowKeyListCache() {
        return viewDataSelectRowKeyListCache;
    }

    public void setViewDataSelectRowKeyListCache(List<String> viewDataSelectRowKeyListCache) {
        this.viewDataSelectRowKeyListCache = viewDataSelectRowKeyListCache;
    }

    public List<String> getCurrentStatisticalSelectRowKeyList() {
        return currentStatisticalSelectRowKeyList;
    }

    public void setCurrentStatisticalSelectRowKeyList(List<String> currentStatisticalSelectRowKeyList) {
        this.currentStatisticalSelectRowKeyList = currentStatisticalSelectRowKeyList;
    }

    public List<String> getCurrentViewDataSelectRowKeyList() {
        return currentViewDataSelectRowKeyList;
    }

    public void setCurrentViewDataSelectRowKeyList(List<String> currentViewDataSelectRowKeyList) {
        this.currentViewDataSelectRowKeyList = currentViewDataSelectRowKeyList;
    }

    public List<String> getStatisticalModifyRowKeyList() {
        return statisticalModifyRowKeyList;
    }

    public void setStatisticalModifyRowKeyList(List<String> statisticalModifyRowKeyList) {
        this.statisticalModifyRowKeyList = statisticalModifyRowKeyList;
    }

    public void setViewDataIsBlank(boolean viewDataIsBlank) {
        this.viewDataIsBlank = viewDataIsBlank;
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

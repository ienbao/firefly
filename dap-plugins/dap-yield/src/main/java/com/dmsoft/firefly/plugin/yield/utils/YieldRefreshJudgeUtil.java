package com.dmsoft.firefly.plugin.yield.utils;

import com.google.common.collect.Lists;

import java.util.List;

public class YieldRefreshJudgeUtil {
    private static YieldRefreshJudgeUtil instance;
    private List<String> overViewSelectRowKeyListCache;
    private List<String> currentViewDataSelectRowKeyList = Lists.newArrayList();

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
}

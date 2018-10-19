package com.dmsoft.firefly.plugin.yield.utils;

import java.util.List;

public class YieldRefreshJudgeUtil {
    private static YieldRefreshJudgeUtil instance;
    private List<String> overViewSelectRowKeyListCache;

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

    public List<String> getOverViewSelectRowKeyListCache() {
        return overViewSelectRowKeyListCache;
    }

    public void setOverViewSelectRowKeyListCache(List<String> overViewSelectRowKeyListCache) {
        this.overViewSelectRowKeyListCache = overViewSelectRowKeyListCache;
    }
}

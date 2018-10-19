package com.dmsoft.firefly.plugin.yield.utils;

public class DigNumInstance {
    private static DigNumInstance instance = null;
    private int digNum = -1;

    private DigNumInstance() {
    }

    /**
     * Get DigNumInstance instance
     *
     * @return DigNumInstance instance
     */
    public static DigNumInstance newInstance() {
        if (null == instance) {
            instance = new DigNumInstance();
        }
        return instance;
    }

    public int getDigNum() {
        return digNum;
    }

    public void setDigNum(int digNum) {
        this.digNum = digNum;
    }
}

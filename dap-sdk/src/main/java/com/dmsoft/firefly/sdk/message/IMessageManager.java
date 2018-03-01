package com.dmsoft.firefly.sdk.message;


public interface IMessageManager {
    /**
     * init all chart data
     *
     * @param title title
     * @param msg information content
     * @param type information type
     */
    void addMsg(String title, String msg, MessageTipTyp type);
}

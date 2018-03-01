package com.dmsoft.firefly.sdk.message;


public interface IMessageManager {
    /**
     * show information
     *
     * @param title title
     * @param msg information content
     * @param type information type
     */
    void showMsg(String title, String msg, MessageTipTyp type);

    /**
     * show success info
     *
     * @param title title
     * @param msg information content
     */
    void showSuccessMsg(String title, String msg);

    /**
     * show war info
     *
     * @param title title
     * @param msg information content
     */
    void showWarnMsg(String title, String msg);

    /**
     * show normal info
     *
     * @param title title
     * @param msg information content
     */
    void showInfoMsg(String title, String msg);



}

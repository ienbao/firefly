package com.dmsoft.firefly.sdk.message;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * interface class for message manager
 *
 * @author Julia
 */
public interface IMessageManager {
    /**
     * show information
     *
     * @param title title
     * @param msg   information content
     * @param type  information type
     */
    void showMsg(String title, String msg, MessageTipType type);

    /**
     * show success info
     *
     * @param title title
     * @param msg   information content
     */
    void showSuccessMsg(String title, String msg);

    /**
     * show success info
     *
     * @param title title
     * @param msg   information content
     * @param linkMsg
     * @param linkEvent
     */
    void showSuccessMsg(String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent);

    /**
     * show war info
     *
     * @param title title
     * @param msg   information content
     */
    void showWarnMsg(String title, String msg);

    /**
     * show war info
     *
     * @param title title
     * @param msg   information content
     * @param linkMsg
     * @param linkEvent
     */
    void showWarnMsg(String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent);

    /**
     * show normal info
     *
     * @param title title
     * @param msg   information content
     */
    void showInfoMsg(String title, String msg);

    /**
     * show war info
     *
     * @param title title
     * @param msg   information content
     * @param linkMsg
     * @param linkEvent
     */
    void showInfoMsg(String title, String msg, String linkMsg, EventHandler<ActionEvent> linkEvent);


}

package com.dmsoft.firefly.gui.components.table;

import javafx.event.ActionEvent;

/**
 * interface for table row event
 *
 * @author Can Guan
 */
public interface TableMenuRowEvent {
    /**
     * method to get menu name
     * if the row name is null, it is illegal
     *
     * @return menu name
     */
    String getMenuName();

    /**
     * method to handle event
     *
     * @param rowKey row key
     * @param event  mouse event
     */
    void handleAction(String rowKey, ActionEvent event);
}

package com.dmsoft.firefly.gui.components.table;

import javafx.event.ActionEvent;
import javafx.scene.Node;

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
     * method to ge menu node
     *
     * @return node is not null, the menuItem will be create by {@code new MenuItem(Node node)}
     */
    Node getMenuNode();

    /**
     * method to handle event
     *
     * @param rowKey row key
     * @param event  mouse event
     */
    void handleAction(String rowKey, ActionEvent event);
}

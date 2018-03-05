package com.dmsoft.firefly.gui.components.utils;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Cache node to HashMap
 *
 * @author Julia
 */
public final class NodeMap {
    private static final Logger logger = LoggerFactory.getLogger(NodeMap.class);

    //For storing Node objects
    private static HashMap<String, Node> nodes = new HashMap<String, Node>();

    /**
     * method to add node
     *
     * @param name name
     * @param pane node
     */
    public static void addNode(String name, Pane pane) {
        nodes.put(name, pane);
    }

    /**
     * method to get node by name
     *
     * @param name name
     * @@return node
     */
    public static Node getNode(String name) {
        return nodes.get(name);
    }

    /**
     * method to show node by name
     *
     * @param name name
     * @@return boolean
     */
    public static boolean showNode(String name) {
        getNode(name).setVisible(true);
        return true;
    }

    /**
     * method to Show one node hidden another
     *
     * @param show  name
     * @param close name
     * @@return boolean
     */
    public static boolean showNode(String show, String close) {
        getNode(close).setVisible(false);
        showNode(show);
        return true;
    }

    /**
     * method to remove node by name
     *
     * @param name name
     * @@return Whether to remove success or not， true:success
     */
    public static boolean removeNode(String name) {
        if (nodes.remove(name) == null) {
            logger.error("Node does not exist, please check the name");
            return false;
        } else {
            logger.debug("Node removal success.");
            return true;
        }
    }
}

package com.dmsoft.firefly.sdk.utils;


import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class UiUtils {
    private static final Logger logger = LoggerFactory.getLogger(UiUtils.class);

    //For storing Pane objects
    private static HashMap<String, Pane> panes = new HashMap<String, Pane>();

    /**
     * method to add pane
     *
     * @param name name
     * @param pane pane
     */
    public static void addPane(String name, Pane pane) {
        panes.put(name, pane);
    }

    /**
     * method to get pane by name
     *
     * @param name name
     * @@return  pane
     */
    public static Pane getPane(String name) {
        return panes.get(name);
    }

    /**
     * method to show pane by name
     *
     * @param name name
     * @@return  boolean
     */
    public static boolean showPane(String name) {
        getPane(name).setVisible(true);
        return true;
    }

    /**
     * method to Show one pane hidden another
     *
     * @param show name
     * @param close name
     * @@return  boolean
     */
    public static boolean showPane(String show, String close) {
        getPane(close).setVisible(false);
        showPane(show);
        return true;
    }

    /**
     * method to remove pane by name
     *
     * @param name name
     * @@return Whether to remove success or not， true:success

     */
    public static boolean removePane(String name) {
        if (panes.remove(name) == null) {
            logger.error("Pane does not exist, please check the name");
            return false;
        } else {
            logger.debug("Panel removal success.");
            return true;
        }
    }
}

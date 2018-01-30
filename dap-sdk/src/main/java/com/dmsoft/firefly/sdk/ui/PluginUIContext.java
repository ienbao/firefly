package com.dmsoft.firefly.sdk.ui;

import javafx.scene.layout.Pane;

import java.util.List;

/**
 * method for caching uis registered by plugins
 *
 * @author Can Guan
 */
public interface PluginUIContext {
    /**
     * method to get all menu locations
     *
     * @return list of menu location
     */
    List<String> getAllMenuLocations();

    /**
     * method to get menu action
     *
     * @param menuLocation menu location
     * @return action
     */
    Action getMenuAction(String menuLocation);

    /**
     * method to get all main body names
     *
     * @return list of context name
     */
    List<String> getAllMainBodyNames();

    /**
     * method to get main body pane
     *
     * @param name main body name
     * @return main body pane
     */
    Pane getMainBodyPane(String name);
}

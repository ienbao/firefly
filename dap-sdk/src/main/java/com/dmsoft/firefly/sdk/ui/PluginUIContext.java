package com.dmsoft.firefly.sdk.ui;

import java.util.Set;

/**
 * method for caching uis registered by plugins
 *
 * @author Can Guan
 */
public interface PluginUIContext {

    /**
     * method to register menu
     *
     * @param menu menu
     */
    void registerMenu(IMenu menu);

    /**
     * method to get all menu locations
     *
     * @return list of menu location
     */
    Set<String> getAllMenuLocations();

    /**
     * method to get menu
     *
     * @param menuLocation menu location
     * @return menu
     */
    IMenu getMenu(String menuLocation);

    /**
     * method to register main body
     *
     * @param name name
     * @param pane pane
     */
    void registerMainBody(String name, IMainBodyPane pane);

    /**
     * method to get all main body names
     *
     * @return list of context name
     */
    Set<String> getAllMainBodyNames();

    /**
     * method to get main body pane
     *
     * @param name main body name
     * @return main body pane
     */
    IMainBodyPane getMainBodyPane(String name);
}

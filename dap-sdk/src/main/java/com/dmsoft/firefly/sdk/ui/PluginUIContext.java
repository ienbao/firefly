package com.dmsoft.firefly.sdk.ui;

import javafx.scene.layout.Pane;

import java.util.List;
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
     * @param menuLocation ui location
     * @param menu         menu
     */
    void registerMenu(String menuLocation, MenuComponent menu);

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
    MenuComponent getMenu(String menuLocation);

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

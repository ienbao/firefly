package com.dmsoft.firefly.sdk.ui;


import javafx.scene.layout.Pane;

/**
 * class for plugin ui
 *
 * @author Can Guan
 */
public interface PluginUI {
    /**
     * method to register menu
     *
     * @param menuLocation ui location
     * @param name         name
     * @param action       action
     */
    void registerMenu(String menuLocation, String name, Action action);

    /**
     * method to register main body
     *
     * @param name name
     * @param pane pane
     */
    void registerMainBody(String name, Pane pane);
}

package com.dmsoft.firefly.sdk.ui;

import static com.dmsoft.firefly.sdk.ui.MenuBuilder.MenuType;

/**
 * menu component for menu
 */
public interface IMenu {

    /**
     * method to get plugin id
     *
     * @return pluginId
     */
    String getPluginId();

    /**
     * method to get menu type
     *
     * @return menu or menu item
     */
    MenuType getMenuType();

    /**
     * method to get location
     *
     * @return location
     */
    String getLocation();

    /**
     * method to get parent location
     *
     * @return parent location
     */
    String getParentLocation();

    /**
     * method to get menu
     *
     * @param <T> any node
     * @return instance
     */
    <T> T getMenu();
}

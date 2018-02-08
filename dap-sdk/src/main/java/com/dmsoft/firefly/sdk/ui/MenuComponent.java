package com.dmsoft.firefly.sdk.ui;

import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * menu component for menu
 */
public interface MenuComponent {
    /**
     * method to get menu type
     * @return menu or menu item
     */
    MenuType getMenuType();

    /**
     * method to get location
     * @return location
     */
    String getLocation();

    /**
     * method to get menu
     * @return menu
     */
    Menu getMenu();

    /**
     * method to get menuItem
     *
     * @return menuItem
     */
    MenuItem getMenuItem();
}

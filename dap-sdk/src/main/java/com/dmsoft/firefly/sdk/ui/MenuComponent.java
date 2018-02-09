package com.dmsoft.firefly.sdk.ui;

/**
 * menu component for menu
 */
public interface MenuComponent {

    /**
     * method to get location
     * @return location
     */
    String getLocation();

    /**
     *method to get menu
     * @param menuClass {@link javafx.scene.control.Menu}.class or {@link javafx.scene.control.MenuItem}.class
     * @param <T> menu class
     * @return instance
     */
    <T> T getMenu(Class<T> menuClass);
}

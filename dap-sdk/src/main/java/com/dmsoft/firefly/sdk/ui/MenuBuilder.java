package com.dmsoft.firefly.sdk.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.commons.lang3.Validate;

/**
 * menu component for menu
 *
 * @param <T> any type
 * @author Julia
 */
public class MenuBuilder<T> implements IMenu {
    public static final String MENU_FILE = "file";
    public static final String MENU_DATASOURCE = "dataSource";
    public static final String MENU_DATASOURCE_RESOLVER = "dataSourceResolver";
    public static final String MENU_ANALYSE = "analyse";
    public static final String MENU_PREFERENCE = "preference";
    public static final String MENU_HELP = "help";

    private MenuType menuType;
    private String pluginId;
    private String location;
    private String parentLocation;
    private T menu;

    /**
     * constructor
     *
     * @param pluginId plugin id
     * @param menuType menu type
     */
    public MenuBuilder(String pluginId, MenuType menuType) {
        this.menuType = menuType;
        this.pluginId = pluginId;
    }

    /**
     * constructor
     *
     * @param pluginId       plugin id
     * @param menuType       menu type
     * @param location       location
     * @param parentLocation parent location
     */
    public MenuBuilder(String pluginId, MenuType menuType, String location, String parentLocation) {
        Validate.notBlank(pluginId);
        Validate.notBlank(location);
        Validate.notBlank(parentLocation);
        this.pluginId = pluginId;
        this.menuType = menuType;
        this.location = location;
        this.parentLocation = parentLocation;
    }

    @Override
    public String getPluginId() {
        return this.pluginId;
    }

    @Override
    public MenuType getMenuType() {
        return menuType;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getParentLocation() {
        return parentLocation;
    }

    /**
     * method to set parent location
     *
     * @param parentLocation parent location
     * @return menu builder
     */
    public MenuBuilder setParentLocation(String parentLocation) {
        this.parentLocation = parentLocation;
        return this;
    }

    @Override
    public <T> T getMenu() {
        return (T) this.menu;
    }

    /**
     * method to add menu
     *
     * @param menu menu
     * @return menu builder
     */
    public MenuBuilder addMenu(T menu) {
        this.menu = menu;
        if (menu instanceof Menu) {
            this.location = pluginId + "_" + ((Menu) menu).getId();
            ((Menu) this.menu).setId(location);
        } else if (menu instanceof MenuItem) {
            this.location = pluginId + "_" + ((MenuItem) menu).getId();
            ((MenuItem) this.menu).setId(location);
        }

        return this;
    }

    public enum MenuType {
        MENU, MENU_ITEM;
    }
}

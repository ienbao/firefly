package com.dmsoft.firefly.sdk.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.commons.lang3.Validate;

/**
 * menu component for menu
 */
public class MenuBuilder<T> implements IMenu {
    public final static String MENU_FILE = "file";
    public final static String MENU_DATASOURCE = "dataSource";
    public final static String MENU_DATASOURCE_RESOLVER = "dataSourceResolver";
    public final static String MENU_ANALYSE = "analyse";
    public final static String MENU_PREFERENCE = "preference";
    public final static String MENU_HELP = "help";

    private MenuType menuType;
    private String pluginId;
    private String location;
    private String parentLocation;
    private T menu;

    public MenuBuilder(String pluginId, MenuType menuType) {
        this.menuType = menuType;
        this.pluginId = pluginId;
    }

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

    public MenuBuilder setParentLocation(String parentLocation) {
        this.parentLocation = parentLocation;
        return this;
    }

    @Override
    public <T> T getMenu() {
        return (T) this.menu;
    }

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

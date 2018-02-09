package com.dmsoft.firefly.gui.component.Menu;

import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuDataSourceResolverImpl implements MenuComponent {

    @Override
    public MenuType getMenuType() {
        return MenuType.MENU;
    }

    @Override
    public String getLocation() {
        return "resolver";
    }

    @Override
    public String getParentLocation() {
        return "dataSource";
    }

    @Override
    public <T> T getMenu() {
        Menu menu = new Menu("Resolver");
        menu.setId("resolver");
        return (T) menu;
    }
}

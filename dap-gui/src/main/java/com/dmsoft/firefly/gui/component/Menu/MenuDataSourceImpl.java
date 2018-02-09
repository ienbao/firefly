package com.dmsoft.firefly.gui.component.Menu;

import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuDataSourceImpl implements MenuComponent {

    @Override
    public MenuType getMenuType() {
        return MenuType.MENU;
    }

    @Override
    public String getLocation() {
        return "dataSource";
    }

    @Override
    public String getParentLocation() {
        return null;
    }

    @Override
    public <T> T getMenu() {
        Menu menu = new Menu("Data Source");
        menu.setId("dataSource");
        MenuItem menuItem = new MenuItem("Select Data Source(S)");
        menu.getItems().add(menuItem);
        return (T) menu;
    }
}

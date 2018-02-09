package com.dmsoft.firefly.gui.component.Menu;

import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuHelpmpl implements MenuComponent {

    @Override
    public MenuType getMenuType() {
        return MenuType.MENU;
    }

    @Override
    public String getLocation() {
        return "help";
    }

    @Override
    public String getParentLocation() {
        return null;
    }

    @Override
    public <T> T getMenu() {
        Menu menu = new Menu("Help(H)");
        menu.setId("help");
        MenuItem legalMenuItem = new MenuItem("Legal Notice");
        MenuItem dapMenuItem = new MenuItem("About DAP");
        MenuItem updateMenuItem = new MenuItem("Check Update");

        menu.getItems().add(legalMenuItem);
        menu.getItems().add(dapMenuItem);
        menu.getItems().add(updateMenuItem);
        return (T) menu;
    }
}

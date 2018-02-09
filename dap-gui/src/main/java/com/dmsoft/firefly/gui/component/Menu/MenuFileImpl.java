package com.dmsoft.firefly.gui.component.Menu;

import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuFileImpl implements MenuComponent {

    @Override
    public MenuType getMenuType() {
        return MenuType.MENU;
    }

    @Override
    public String getLocation() {
        return "file";
    }

    @Override
    public String getParentLocation() {
        return null;
    }

    @Override
    public <T> T getMenu() {
        Menu menu = new Menu("File");
        menu.setId("file");
        MenuItem importMenuItem = new MenuItem("Import Settings(I)");
        MenuItem exportMenuItem = new MenuItem("Export Setting(E)");
        MenuItem restoreMenuItem = new MenuItem("Restore Setting(R)");
        MenuItem exitMenuItem = new MenuItem("Exit(X)");

        menu.getItems().add(importMenuItem);
        menu.getItems().add(exportMenuItem);
        menu.getItems().add(restoreMenuItem);
        menu.getItems().add(exitMenuItem);
        return (T) menu;
    }
}

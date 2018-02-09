package com.dmsoft.firefly.gui.component.Menu;

import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.utils.enums.MenuType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;

public class MenuPreferencempl implements MenuComponent {

    @Override
    public MenuType getMenuType() {
        return MenuType.MENU;
    }

    @Override
    public String getLocation() {
        return "preference";
    }

    @Override
    public String getParentLocation() {
        return null;
    }

    @Override
    public <T> T getMenu() {
        Menu menu = new Menu("Preference(P)");
        menu.setId("preference");
        MenuItem importMenuItem = new MenuItem("Plugin-Manager(P)");
        Menu language = new Menu("Language");
        RadioMenuItem ch = new RadioMenuItem("中文");
        RadioMenuItem en = new RadioMenuItem("English");
        language.getItems().addAll(ch, en);

        menu.getItems().add(importMenuItem);
        menu.getItems().add(language);
        return (T) menu;
    }
}

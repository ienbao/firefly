//package com.dmsoft.firefly.gui.component.Menu;
//
//import com.dmsoft.firefly.sdk.ui.IMenu;
//import javafx.scene.control.Menu;
//
//public class MenuDataSourceResolverImpl implements IMenu {
//
//    @Override
//    public MenuType getMenuType() {
//        return MenuType.MENU;
//    }
//
//    @Override
//    public String getLocation() {
//        return "resolver";
//    }
//
//    @Override
//    public String getParentLocation() {
//        return "dataSource";
//    }
//
//    @Override
//    public <T> T getMenu() {
//        Menu menu = new Menu("Resolver");
//        menu.setId("resolver");
//        return (T) menu;
//    }
//}

//package com.dmsoft.firefly.gui.component.Menu;
//
//import com.dmsoft.firefly.sdk.ui.IMenu;
//import javafx.scene.control.Menu;
//import javafx.scene.control.MenuItem;
//
//public class MenuAnalyzeImpl implements IMenu {
//    @Override
//    public MenuType getMenuType() {
//        return MenuType.MENU;
//    }
//
//    @Override
//    public String getLocation() {
//        return "analyse";
//    }
////
//    @Override
//    public String getParentLocation() {
//        return null;
//    }
//
//    @Override
//    public <T> T getMenu() {
//        Menu menu = new Menu("Analyze(A)");
//        menu.setId("analyse");
//        MenuItem analysisMenuItem = new MenuItem("Analysis Templete(A)");
//        menu.getItems().add(analysisMenuItem);
//        return (T) menu;
//    }
//}

package com.dmsoft.firefly.gui.view;

import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.IMenu;
import com.dmsoft.firefly.sdk.ui.MenuBuilder;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.google.common.collect.Lists;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 创建菜单
 * @author Tod
 */
public class MenuCreation {
    private MenuBar menuSystem;
    private Logger logger;

    public MenuCreation(MenuBar menuSystem, Logger logger){
        this.menuSystem = menuSystem;
        this.logger = logger;
    }

    //TODO td 测试创建菜单
    public void createMenu(PluginUIContext pc){
        Set<String> names = pc.getAllMenuLocations();
        List<String> secondNames = Lists.newLinkedList();
        List<String> thirdNames = Lists.newLinkedList();

        names.forEach(name -> {
            IMenu menu = pc.getMenu(name);
            String pluginId = menu.getPluginId();
            String parentLocation = pluginId + "_" + menu.getParentLocation();
            if (parentLocation.equals(MenuFactory.getParentMenuId())) {
                if (MenuBuilder.MenuType.MENU.equals(menu.getMenuType())) {
                    menuSystem.getMenus().add(menu.getMenu());
                } else {
                    logger.debug("TMenu bar can not set menu item, only set menu.");
                }
            }else{
                List<Menu> menus = menuSystem.getMenus();
                for (Menu menu1 : menus){
                    updateMenu(menu,menu1);
                }
            }
        });


//        names.forEach(name -> {
//            IMenu menu = pc.getMenu(name);
//            String pluginId = menu.getPluginId();
//            String parentLocation = pluginId + "_" + menu.getParentLocation();
//            if (parentLocation.equals(MenuFactory.getParentMenuId())) {
//                if (MenuBuilder.MenuType.MENU.equals(menu.getMenuType())) {
//                    menuSystem.getMenus().add(menu.getMenu());
//                } else {
//                    logger.debug("TMenu bar can not set menu item, only set menu.");
//                }
//            } else {
//                if (MenuBuilder.MenuType.MENU.equals(menu.getMenuType())) {
//                    List<Menu> menus = menuSystem.getMenus();
//                    boolean result1 = false;
//                    for (Menu menu1 : menus) {
//                        boolean result = updateMenu(menu, menu1);
//                        if (result) {
//                            result1 = true;
//                            break;
//                        }
//                    }
//                    if (!result1) {
//                        secondNames.add(name);
//                    }
//                } else {
//                    boolean isExist = false;
//                    for (Menu tempMenu : menuSystem.getMenus()) {
//                        if (isExist) {
//                            break;
//                        }
//                        for (MenuItem menuItem : tempMenu.getItems()) {
//                            MenuItem menuItem1 = (MenuItem) menu.getMenu();
//                            if (menuItem.getText().equals(menuItem1.getText())) {
//                                isExist = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (!isExist) {
//                        secondNames.add(name);
//                    }
//                }
//            }
//        });
//        secondNames.forEach(name -> {
//            IMenu menu = pc.getMenu(name);
//            if (isHasParentMenu(menu, pc)) {
//                if (MenuBuilder.MenuType.MENU.equals(menu.getMenuType())) {
//                    List<Menu> menus = menuSystem.getMenus();
//                    for (Menu menu1 : menus) {
//                        boolean result = updateMenu(menu, menu1);
//                        if (result) {
//                            break;
//                        }
//                    }
//                } else {
//                    thirdNames.add(name);
//                }
//            }
//        });
//
//        thirdNames.forEach(name -> {
//            IMenu menu = pc.getMenu(name);
//            if (isHasParentMenu(menu, pc)) {
//                List<Menu> menus = menuSystem.getMenus();
//                for (Menu menu1 : menus) {
//                    boolean result = updateMenu(menu, menu1);
//                    if (result) {
//                        break;
//                    }
//                }
//            }
//        });
    }


    private boolean updateMenu(IMenu menuComponent, Menu menu) {
        String parentLocation = menuComponent.getPluginId() + "_" + menuComponent.getParentLocation();
        String platformParentLocation = MenuFactory.PLATFORM_ID + "_" + menuComponent.getParentLocation();

        AtomicBoolean result = new AtomicBoolean(false);
        if (StringUtils.isNotBlank(menu.getId()) && (menu.getId().equals(parentLocation) || (menu.getId().equals(platformParentLocation)))) {
            menu.getItems().add(menuComponent.getMenu());
            result.set(true);
        } else {
            ObservableList<MenuItem> dd = menu.getItems();
            for (MenuItem menuItem : dd) {
                if (menuItem instanceof Menu) {
                    Menu menu2 = (Menu) menuItem;
                    this.updateMenu(menuComponent, menu2);
                }
            }
        }
        return result.get();
    }

//    private boolean isHasParentMenu(IMenu menu, PluginUIContext pc) {
//        String parentLocation = menu.getPluginId() + "_" + menu.getParentLocation();
//        IMenu parentMenuComponent = pc.getMenu(parentLocation);
//        if (parentMenuComponent == null) {
//            parentLocation = MenuFactory.PLATFORM_ID + "_" + menu.getParentLocation();
//            parentMenuComponent = pc.getMenu(parentLocation);
//            if (parentMenuComponent == null) {
//                logger.debug(" The parent menu does not exist. parentLocation={}", parentLocation);
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            return true;
//        }
//    }
}

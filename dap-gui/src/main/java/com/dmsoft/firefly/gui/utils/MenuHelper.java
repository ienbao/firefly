package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.component.Menu.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuHelper {

    public static void initMenu() {
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuFileImpl());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuDataSourceImpl());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuDataSourceResolverImpl());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuAnalyzeImpl());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuPreferencempl());
        RuntimeContext.getBean(PluginUIContext.class).registerMenu(new MenuHelpmpl());
    }
}

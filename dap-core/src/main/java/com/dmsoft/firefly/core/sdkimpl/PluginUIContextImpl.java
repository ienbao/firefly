package com.dmsoft.firefly.core.sdkimpl;

import com.dmsoft.firefly.sdk.ui.IMainBodyPane;
import com.dmsoft.firefly.sdk.ui.MenuComponent;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * impl class for plugin ui context impl
 *
 * @author Can Guan
 */
public class PluginUIContextImpl implements PluginUIContext {
    private Map<String, MenuComponent> menuMap;
    private Map<String, IMainBodyPane> paneMap;

    /**
     * constructor
     */
    public PluginUIContextImpl() {
        this.menuMap = Maps.newLinkedHashMap();
        this.paneMap = Maps.newLinkedHashMap();
    }

    @Override
    public void registerMenu(MenuComponent menu) {
        this.menuMap.put(menu.getLocation(), menu);
    }

    @Override
    public Set<String> getAllMenuLocations() {
        return this.menuMap.keySet();
    }

    @Override
    public MenuComponent getMenu(String menuLocation) {
        return this.menuMap.get(menuLocation);
    }

    @Override
    public void registerMainBody(String name, IMainBodyPane pane) {
        this.paneMap.put(name, pane);
    }

    @Override
    public Set<String> getAllMainBodyNames() {
        return this.paneMap.keySet();
    }

    @Override
    public IMainBodyPane getMainBodyPane(String name) {
        return this.paneMap.get(name);
    }
}

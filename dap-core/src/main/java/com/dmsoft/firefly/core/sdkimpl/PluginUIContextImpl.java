package com.dmsoft.firefly.core.sdkimpl;

import com.dmsoft.firefly.sdk.ui.Action;
import com.dmsoft.firefly.sdk.ui.IMainBodyPane;
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
    private Map<String, Action> menuMap;
    private Map<String, IMainBodyPane> paneMap;

    /**
     * constructor
     */
    public PluginUIContextImpl() {
        this.menuMap = Maps.newHashMap();
        this.paneMap = Maps.newHashMap();
    }

    @Override
    public void registerMenu(String menuLocation, Action action) {
        this.menuMap.put(menuLocation, action);
    }

    @Override
    public Set<String> getAllMenuLocations() {
        return this.menuMap.keySet();
    }

    @Override
    public Action getMenuAction(String menuLocation) {
        return menuMap.get(menuLocation);
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

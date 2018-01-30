package com.dmsoft.firefly.core.sdkimpl;

import com.dmsoft.firefly.sdk.ui.Action;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import com.google.common.collect.Maps;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * impl class for plugin ui context impl
 *
 * @author Can Guan
 */
public class PluginUIContextImpl implements PluginUIContext {
    private Map<String, Action> menuMap;

    /**
     * constructor
     */
    public PluginUIContextImpl() {
        this.menuMap = Maps.newHashMap();
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
    public void registerMainBody(String name, Pane pane) {

    }

    @Override
    public List<String> getAllMainBodyNames() {
        return null;
    }

    @Override
    public Pane getMainBodyPane(String name) {
        return null;
    }
}

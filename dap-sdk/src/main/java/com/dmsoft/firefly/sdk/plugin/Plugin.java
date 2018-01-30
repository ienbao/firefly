package com.dmsoft.firefly.sdk.plugin;

import com.dmsoft.firefly.sdk.utils.enums.InitModel;

/**
 * Interface for plugin entity. Extensions should implement this interface and application will manager the class loader
 * by calling the methods.
 *
 * @author Can Guan
 */
public abstract class Plugin {
    /**
     * constructor
     */
    public Plugin() {
    }

    /**
     * initialize method is important for preparing the env.
     *
     * @param model start model
     */
    public abstract void initialize(InitModel model);

    /**
     * start method will be called after all initialize method is done.
     */
    public abstract void start();

    /**
     * destroy method will be called when this plugin is disable.
     */
    public abstract void destroy();
}

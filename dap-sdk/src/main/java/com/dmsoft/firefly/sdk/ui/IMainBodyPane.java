package com.dmsoft.firefly.sdk.ui;

import javafx.scene.layout.Pane;

/**
 * interface for main body pane
 */
public interface IMainBodyPane {
    /**
     * method to get new pane
     *
     * @return pane
     */
    Pane getNewPane();

    /**
     * method to reset ui when the env is changed
     */
    void reset();
}

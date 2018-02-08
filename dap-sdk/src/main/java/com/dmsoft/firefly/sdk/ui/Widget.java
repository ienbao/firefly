package com.dmsoft.firefly.sdk.ui;

import javafx.scene.layout.Pane;

/**
 * widget for request window or message
 */
public interface Widget {
    /**
     * method to request window
     *
     * @param title       title
     * @param bodyPane    body pane
     * @param windowClass window class
     * @param <T>         class
     * @return T instance
     */
    <T> T requestWindow(String title, Pane bodyPane, Class<T> windowClass);

    /**
     * method to request window and glass other pane
     *
     * @param title       tile
     * @param bodyPane    body pane
     * @param windowClass window class
     * @param otherPaneId other pane id
     * @param <T>         class
     * @return T instance
     */
    <T> T requestWindowAndGlassOtherPane(String title, Pane bodyPane, Class<T> windowClass, String... otherPaneId);
}

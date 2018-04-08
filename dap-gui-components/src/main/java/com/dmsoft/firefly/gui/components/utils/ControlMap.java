package com.dmsoft.firefly.gui.components.utils;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Cache control to HashMap
 *
 * @author Julia
 */
public final class ControlMap {
    private static final Logger logger = LoggerFactory.getLogger(ControlMap.class);

    //For storing Node objects
    private static HashMap<String, Control> controls = new HashMap<String, Control>();

    /**
     * method to add node
     *
     * @param name name
     * @param control control
     */
    public static void addControl(String name, Control control) {
        controls.put(name, control);
    }

    /**
     * method to get node by name
     *
     * @param name name
     * @@return node
     */
    public static Control getControl(String name) {
        return controls.get(name);
    }

}

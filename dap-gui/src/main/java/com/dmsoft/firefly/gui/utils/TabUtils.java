package com.dmsoft.firefly.gui.utils;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Created by Julia.Zhou on 2018/3/20.
 */
public class TabUtils {

    /**
     * listener
     *
     * @param tab     tab
     * @param tabPane tab pane
     */
    public static void tabSelectedListener(Tab tab, TabPane tabPane) {
        tab.setOnCloseRequest(event -> {
            if (tabPane.getTabs().size() == 2) {
                tabPane.getTabs().forEach(tab1 -> tab1.setClosable(false));
            } else {
                tabPane.getTabs().forEach(tab1 -> tab1.setClosable(true));
            }
        });
    }

    /**
     * method to disable close tab
     *
     * @param tabPane tab pane
     */
    public static void disableCloseTab(TabPane tabPane) {
        if (tabPane.getTabs().size() == 1) {
            tabPane.getTabs().forEach(tab -> tab.setClosable(false));
        } else {
            tabPane.getTabs().forEach(tab -> tab.setClosable(true));
        }
    }
}

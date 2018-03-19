package com.dmsoft.firefly.plugin.grr.utils.table;

import javafx.scene.control.TableCell;

/**
 * Created by cherry on 2018/3/13.
 */
public interface TableCellCallBack {

    default void execute(TableCell cell) {

    }

    default boolean execute(TableCell cell, int index) {
        return true;
    }

    default void execute(TableCell cell, Object item) {

    }
}

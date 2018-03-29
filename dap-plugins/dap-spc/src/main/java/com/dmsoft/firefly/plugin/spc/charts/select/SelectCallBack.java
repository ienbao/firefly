package com.dmsoft.firefly.plugin.spc.charts.select;

import java.util.Set;

/**
 * Created by cherry on 2018/2/26.
 */
public interface SelectCallBack {

    /**
     * List item select call
     *
     * @param name          current operated item
     * @param selected      is selected or not
     * @param selectedNames all selected items
     */
    void execute(String name, boolean selected, Set<String> selectedNames);
}

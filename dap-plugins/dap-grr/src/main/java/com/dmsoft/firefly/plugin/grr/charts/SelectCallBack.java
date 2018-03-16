package com.dmsoft.firefly.plugin.grr.charts;

import java.util.Set;

/**
 * Created by cherry on 2018/2/26.
 */
public interface SelectCallBack {

    /**
     * Select call back execute function
     *
     * @param name          current operated item
     * @param selected      selected status
     * @param selectedNames current selected items
     */
    void execute(String name, boolean selected, Set<String> selectedNames);
}

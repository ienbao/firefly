package com.dmsoft.firefly.plugin.grr.charts;

import java.util.Set;

/**
 * Created by cherry on 2018/2/26.
 */
public interface SelectCallBack {

    void execute(String name, boolean selected, Set<String> selectedNames);
}

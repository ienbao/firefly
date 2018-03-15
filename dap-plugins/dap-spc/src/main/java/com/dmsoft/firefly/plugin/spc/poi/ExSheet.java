/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.spc.poi;

import java.util.List;

/**
 * Created by Peter on 2016/4/18.
 */
public class ExSheet {
    private String name;
    private List<ExCell> exCells;
    private int index;

    public String getName() {
        return formatName(index, name);
    }

    /**
     * format sheet name
     * @param index   sheet name index
     * @param name    sheet name
     * @return  format sheet name
     */
    public static String formatName(int index, String name) {
        if (index == 0) {
            return name;
        }
        if (name != null && name.length() > 26) {
            return name.substring(0, 25) + "_" + index;
        } else {
            return name + "_" + index;
        }
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<ExCell> getExCells() {
        return exCells;
    }

    public void setExCells(List<ExCell> exCells) {
        this.exCells = exCells;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

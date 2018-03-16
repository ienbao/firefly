
/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export;

/**
 * Created by Peter on 2016/4/18.
 */
public interface ExcelBuilder {
    /**
     *  draw excel
     * @param path     export file path
     * @param factory  class of fill data to excel
     */
    void drawExcel(String path, ExWorker factory);
}

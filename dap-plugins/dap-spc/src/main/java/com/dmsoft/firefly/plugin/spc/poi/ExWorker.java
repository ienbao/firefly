/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.spc.poi;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;

/**
 * Created by cherry on 2016/4/20.
 */
public interface ExWorker {
    /**
     *  get Current Workbook
     * @return workbook
     */
    SXSSFWorkbook getCurrentWorkbook();

    /**
     *  get sheets
     * @return sheets
     */
    List<ExSheet> getSheets();
}

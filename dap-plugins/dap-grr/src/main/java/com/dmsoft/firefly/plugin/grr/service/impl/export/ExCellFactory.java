
/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * Created by cherry on 2016/4/20.
 */
public interface ExCellFactory {
    /**
     * get current workbook
     *
     * @return current workbook
     */
    XSSFWorkbook getCurrentWorkbook();

    /**
     * get sheets
     *
     * @return sheets
     */
    List<ExSheet> getSheets();
}

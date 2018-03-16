/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export;

import org.apache.poi.ss.usermodel.CellStyle;


/**
 * Created by cherry on 2016/4/20.
 */
public final class ExUtil {
    /**
     * fill date to cell
     * @param coordinate coordinate
     * @param value  cell value
     * @param type   include text、image、hyperlink
     * @param style  cell style
     * @return  cell
     */
    public static ExCell fillToCell(Integer[] coordinate, String value, ExCellType type, CellStyle style) {
        ExCell exCell = new ExCell();
        exCell.setCellType(type);
        if (ExCellType.HYPERLINK.equals(type)) {
            exCell.setHyperLinkCode(type.getCode());
        }

        exCell.setStyle(style);
        exCell.setCoordinate(coordinate);
        exCell.setValue(value);
        return exCell;
    }

    /**
     * fill date to cell
     * @param coordinate coordinate
     * @param value      cell value
     * @param type       include text、image、hyperlink
     * @return  cell
     */
    public static ExCell fillToCell(Integer[] coordinate, String value, ExCellType type) {
        return ExUtil.fillToCell(coordinate, value, type, null);
    }
}


/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service.impl.export;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * Created by Peter on 2016/4/18.
 */
public class ExCell {

    private Integer[] coordinate;
    private String value;
    private ExCellType cellType;
    private CellStyle style;
    private XSSFFont font;
    private String hyperLinkCode;

    public Integer[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Integer[] coordinate) {
        this.coordinate = coordinate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExCellType getCellType() {
        return cellType;
    }

    public void setCellType(ExCellType cellType) {
        this.cellType = cellType;
    }

    public CellStyle getStyle() {
        return style;
    }

    public void setStyle(CellStyle style) {
        this.style = style;
    }

    public XSSFFont getFont() {
        return font;
    }

    public void setFont(XSSFFont font) {
        this.font = font;
    }

    public String getHyperLinkCode() {
        return hyperLinkCode;
    }

    public void setHyperLinkCode(String hyperLinkCode) {
        this.hyperLinkCode = hyperLinkCode;
    }
}

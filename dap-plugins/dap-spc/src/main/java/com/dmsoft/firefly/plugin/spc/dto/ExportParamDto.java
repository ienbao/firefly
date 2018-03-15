package com.dmsoft.firefly.plugin.spc.dto;

/**
 * Created by simon.liu on 2017/8/8.
 */
public class ExportParamDto {
    private int excelCapacity;
    private String dirSavePath;
    private String dirName;
    private int digitNum;

    public ExportParamDto(int excelCapacity, String dirSavePath, String dirName, int digitNum) {
        this.excelCapacity = excelCapacity;
        this.dirSavePath = dirSavePath;
        this.dirName = dirName;
        this.digitNum = digitNum;
    }

    public int getExcelCapacity() {
        return excelCapacity;
    }

    public void setExcelCapacity(int excelCapacity) {
        this.excelCapacity = excelCapacity;
    }

    public String getDirSavePath() {
        return dirSavePath;
    }

    public void setDirSavePath(String dirSavePath) {
        this.dirSavePath = dirSavePath;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getDigitNum() {
        return digitNum;
    }

    public void setDigitNum(int digitNum) {
        this.digitNum = digitNum;
    }
}

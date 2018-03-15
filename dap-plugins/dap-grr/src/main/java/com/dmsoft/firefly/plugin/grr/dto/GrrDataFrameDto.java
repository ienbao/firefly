package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;

import java.util.List;

/**
 * Created by Julia.Zhou on 2016/08/17.
 */
public class GrrDataFrameDto extends AbstractValueObject {
    private SearchDataFrame dataFrame;
    private List<GrrViewDataDto> includeDatas;
    private List<GrrViewDataDto> backupDatas;

    public SearchDataFrame getDataFrame() {
        return dataFrame;
    }

    public void setDataFrame(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public List<GrrViewDataDto> getIncludeDatas() {
        return includeDatas;
    }

    public void setIncludeDatas(List<GrrViewDataDto> includeDatas) {
        this.includeDatas = includeDatas;
    }

    public List<GrrViewDataDto> getBackupDatas() {
        return backupDatas;
    }

    public void setBackupDatas(List<GrrViewDataDto> backupDatas) {
        this.backupDatas = backupDatas;
    }
}

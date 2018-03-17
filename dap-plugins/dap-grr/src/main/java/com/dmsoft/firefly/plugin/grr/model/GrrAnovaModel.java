package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaDto;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by cherry on 2018/3/14.
 */
public class GrrAnovaModel {

    private ObservableList<GrrSingleAnova> anovas = FXCollections.observableArrayList();
    private List<GrrAnovaDto> data = Lists.newArrayList();

    public GrrAnovaModel() {
    }

    public void setData(List<GrrAnovaDto> data) {
        this.data.clear();
        anovas.setAll(FXCollections.observableArrayList());
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        int digNum = DigNumInstance.newInstance().getDigNum();
        data.forEach(oneData -> {
            String dfStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getDf()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getDf(), digNum) : String.valueOf(oneData.getDf());
            String ssStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getSs()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getSs(), digNum) : String.valueOf(oneData.getSs());
            String msStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getMs()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getMs(), digNum) : String.valueOf(oneData.getMs());
            String fStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getF()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getF(), digNum) : String.valueOf(oneData.getF());
            String probFStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getProbF()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getProbF(), digNum) : String.valueOf(oneData.getProbF());
            anovas.add(new GrrSingleAnova(oneData.getName().name(), dfStr, ssStr, msStr, fStr, probFStr));
        });
    }

    public ObservableList<GrrSingleAnova> getAnovas() {
        return anovas;
    }
}

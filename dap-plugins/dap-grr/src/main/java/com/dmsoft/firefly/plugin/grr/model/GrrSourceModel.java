package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSourceDto;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by cherry on 2018/3/14.
 */
public class GrrSourceModel {

    private ObservableList<GrrSingleSource> sources = FXCollections.observableArrayList();
    private List<GrrSourceDto> data = Lists.newArrayList();

    public GrrSourceModel() {

    }

    public void setData(List<GrrSourceDto> sourceDtos) {
        this.data.clear();
        sources.setAll(FXCollections.observableArrayList());
        if (sourceDtos == null) {
            return;
        }
        this.data.addAll(sourceDtos);
        int digNum = DigNumInstance.newInstance().getDigNum();
        sourceDtos.forEach(oneData -> {
            String sigmaStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getSigma()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getSigma(), digNum) : String.valueOf(oneData.getSigma());
            String studyValStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getStudyVar()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getStudyVar(), digNum) : String.valueOf(oneData.getStudyVar());
            String variationStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getVariation()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getVariation(), digNum) : String.valueOf(oneData.getVariation());
            String totalVariationStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getTotalVariation())? "-" :
                    DAPStringUtils.formatDouble(oneData.getTotalVariation(), digNum) : String.valueOf(oneData.getTotalVariation());
            String contributionStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getContribution()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getContribution(), digNum) : String.valueOf(oneData.getContribution());
            String totalToleranceStr = digNum >= 0 ? DAPStringUtils.isInfinityAndNaN(oneData.getTotalTolerance()) ? "-" :
                    DAPStringUtils.formatDouble(oneData.getTotalTolerance(), digNum) : String.valueOf(oneData.getTotalTolerance());
            sources.add(new GrrSingleSource(
                    oneData.getName().name(), sigmaStr, studyValStr, variationStr, totalVariationStr, contributionStr, totalToleranceStr));
        });
    }

    public ObservableList<GrrSingleSource> getSources() {
        return sources;
    }
}

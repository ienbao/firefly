package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSourceDto;
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
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        sourceDtos.forEach(oneData -> sources.add(new GrrSingleSource(
                oneData.getName().name(),
                String.valueOf(oneData.getSigma()),
                String.valueOf(oneData.getStudyVar()),
                String.valueOf(oneData.getVariation()),
                String.valueOf(oneData.getTotalVariation()),
                String.valueOf(oneData.getContribution()),
                String.valueOf(oneData.getTotalTolerance()))
        ));
    }

    public ObservableList<GrrSingleSource> getSouces() {
        return sources;
    }
}

package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaDto;
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
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        data.forEach(oneData -> anovas.add(new GrrSingleAnova(
                String.valueOf(oneData.getName()),
                String.valueOf(oneData.getDf()),
                String.valueOf(oneData.getSs()),
                String.valueOf(oneData.getMs()),
                String.valueOf(oneData.getF()),
                String.valueOf(oneData.getProbF())
        )));
    }

    public ObservableList<GrrSingleAnova> getAnovas() {
        return anovas;
    }
}

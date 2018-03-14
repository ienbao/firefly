package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrSummaryModel {

    private ObservableList<GrrSingleSummary> summaries = FXCollections.observableArrayList();
    private List<GrrSummaryDto> data = Lists.newArrayList();

    public GrrSummaryModel() {
    }

    public void setData(List<GrrSummaryDto> summaryDtos, String resultType) {
        data.clear();
        if (summaryDtos == null) {
            return;
        }
        data.addAll(summaryDtos);
        for (int i = 0; i < summaryDtos.size(); i++) {
            boolean selected = (i == 0) ? true : false;
            double repeatability = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    summaryDtos.get(i).getSummaryResultDto().getRepeatabilityOnTolerance() :
                    summaryDtos.get(i).getSummaryResultDto().getRepeatabilityOnContribution();
            double reproducibility = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    summaryDtos.get(i).getSummaryResultDto().getReproducibilityOnTolerance() :
                    summaryDtos.get(i).getSummaryResultDto().getReproducibilityOnContribution();
            double grr = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    summaryDtos.get(i).getSummaryResultDto().getGrrOnTolerance() :
                    summaryDtos.get(i).getSummaryResultDto().getGrrOnContribution();

            summaries.set(i, new GrrSingleSummary(selected,
                    summaryDtos.get(i).getItemName(),
                    String.valueOf(summaryDtos.get(i).getSummaryResultDto().getLsl()),
                    String.valueOf(summaryDtos.get(i).getSummaryResultDto().getUsl()),
                    String.valueOf(summaryDtos.get(i).getSummaryResultDto().getTolerance()),
                    String.valueOf(repeatability),
                    String.valueOf(reproducibility),
                    String.valueOf(grr)));
        }
    }

    public void updateDataByResultType(String resultType) {

        for (int i = 0; i < summaries.size(); i++) {
            double repeatability = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    data.get(i).getSummaryResultDto().getRepeatabilityOnTolerance() :
                    data.get(i).getSummaryResultDto().getRepeatabilityOnContribution();
            double reproducibility = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    data.get(i).getSummaryResultDto().getReproducibilityOnTolerance() :
                    data.get(i).getSummaryResultDto().getReproducibilityOnContribution();
            double grr = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    data.get(i).getSummaryResultDto().getGrrOnTolerance() :
                    data.get(i).getSummaryResultDto().getGrrOnContribution();
            summaries.get(i).updateData(String.valueOf(repeatability), String.valueOf(reproducibility), String.valueOf(grr));
        }
    }

    public ObservableList<GrrSingleSummary> getSummaries() {
        return summaries;
    }
}

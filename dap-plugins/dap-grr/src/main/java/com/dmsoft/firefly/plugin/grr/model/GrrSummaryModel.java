package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
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
    private List<TestItemWithTypeDto> editTestItem = Lists.newArrayList();
    private String selectedItemName;

    public GrrSummaryModel() {
    }

    public void setData(List<GrrSummaryDto> summaryDtos, String resultType, int selectedIndex) {
        data.clear();
        summaries.setAll(FXCollections.observableArrayList());
        if (summaryDtos == null) {
            return;
        }
        data.addAll(summaryDtos);
        for (int i = 0; i < summaryDtos.size(); i++) {
            boolean selected = (selectedIndex == i) ? true : false;
            summaries.add(buildGrrSingleSummary(summaryDtos.get(i), resultType, selected));
        }
    }

    private GrrSingleSummary buildGrrSingleSummary(GrrSummaryDto summaryDto, String resultType, boolean selected) {
        int digNum = DigNumInstance.newInstance().getDigNum() - 2;
        double repeatability = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                summaryDto.getSummaryResultDto().getRepeatabilityOnTolerance() :
                summaryDto.getSummaryResultDto().getRepeatabilityOnContribution();
        double reproducibility = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                summaryDto.getSummaryResultDto().getReproducibilityOnTolerance() :
                summaryDto.getSummaryResultDto().getReproducibilityOnContribution();
        double grr = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                summaryDto.getSummaryResultDto().getGrrOnTolerance() :
                summaryDto.getSummaryResultDto().getGrrOnContribution();
        double lsl = summaryDto.getSummaryResultDto().getLsl();
        double usl = summaryDto.getSummaryResultDto().getUsl();
        double tolerance = summaryDto.getSummaryResultDto().getTolerance();
        String lslStr = String.valueOf(lsl);
        String uslStr = String.valueOf(usl);
        String toleranceStr = digNum >= 0 ? DAPStringUtils.formatDouble(tolerance, digNum + 2) : String.valueOf(tolerance);
        String repeatabilityStr = digNum >= 0 ? DAPStringUtils.formatDouble(repeatability, digNum) : String.valueOf(repeatability);
        String reproducibilityStr = digNum >= 0 ? DAPStringUtils.formatDouble(reproducibility, digNum) : String.valueOf(reproducibility);
        String grrStr = digNum >= 0 ? DAPStringUtils.formatDouble(grr, digNum) : String.valueOf(grr);
        return new GrrSingleSummary(selected,
                summaryDto.getItemName(),
                DAPStringUtils.isBlankWithSpecialNumber(lslStr) ? "-" : lslStr,
                DAPStringUtils.isBlankWithSpecialNumber(uslStr) ? "-" : uslStr,
                DAPStringUtils.isBlankWithSpecialNumber(toleranceStr) ? "-" : toleranceStr,
                DAPStringUtils.isBlankWithSpecialNumber(repeatabilityStr) ? "-" : repeatabilityStr + "%",
                DAPStringUtils.isBlankWithSpecialNumber(reproducibilityStr) ? "-" : reproducibilityStr + "%",
                DAPStringUtils.isBlankWithSpecialNumber(grrStr) ? "-" : grrStr + "%");
    }

    public void updateDataByResultType(String resultType) {
        int digNum = DigNumInstance.newInstance().getDigNum() - 2;
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
            String repeatabilityStr = digNum >= 0 ? DAPStringUtils.formatDouble(repeatability, digNum) : String.valueOf(repeatability);
            String reproducibilityStr = digNum >= 0 ? DAPStringUtils.formatDouble(reproducibility, digNum) : String.valueOf(reproducibility);
            String grrStr = digNum >= 0 ? DAPStringUtils.formatDouble(grr, digNum) : String.valueOf(grr);
            summaries.get(i).updateData(DAPStringUtils.isSpecialBlank(repeatabilityStr) ? "-" : repeatabilityStr + "%",
                    DAPStringUtils.isBlankWithSpecialNumber(reproducibilityStr) ? "-" : reproducibilityStr + "%",
                    DAPStringUtils.isBlankWithSpecialNumber(grrStr) ? "-" : grrStr + "%");
        }
    }

    public void updateSummaryModelData(List<GrrSummaryDto> summaryDtos, String resultType, String selectedItem) {
        if (summaryDtos == null) {
            return;
        }
        summaryDtos.forEach(summaryDto -> {
            for (int i = 0; i < summaries.size(); i++) {
                if (summaryDto.getItemName().equals(summaries.get(i).getItemName())) {
                    boolean selected = selectedItem.equals(summaryDto.getItemName());
                    summaries.set(i, buildGrrSingleSummary(summaryDto, resultType, selected));
                } else {
                    summaries.get(i).setSelect(false);
                }
            }
        });
    }

    public ObservableList<GrrSingleSummary> getSummaries() {
        return summaries;
    }

    public void setEditTestItem(List<TestItemWithTypeDto> editTestItem) {
        this.editTestItem = editTestItem;
    }

    public void addEditTestItem(TestItemWithTypeDto editTestItem) {
        this.editTestItem.add(editTestItem);
    }

    public void removeEditTestItem(TestItemWithTypeDto itemWithTypeDto) {
        this.editTestItem.forEach(testItemWithTypeDto -> {
            if (itemWithTypeDto.getTestItemName().equals(testItemWithTypeDto.getTestItemName())) {
                this.editTestItem.remove(testItemWithTypeDto);
            }
        });
    }

    public void clearEditTestItem() {
        this.editTestItem.clear();
    }

    public void setSelectedItemName(String selectedItemName) {
        this.selectedItemName = selectedItemName;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public List<TestItemWithTypeDto> getEditTestItem() {
        return editTestItem;
    }
}

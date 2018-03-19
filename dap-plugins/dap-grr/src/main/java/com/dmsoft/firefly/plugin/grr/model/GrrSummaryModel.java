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
        Double repeatability = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                summaryDto.getSummaryResultDto().getRepeatabilityOnTolerance() :
                summaryDto.getSummaryResultDto().getRepeatabilityOnContribution();
        Double reproducibility = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                summaryDto.getSummaryResultDto().getReproducibilityOnTolerance() :
                summaryDto.getSummaryResultDto().getReproducibilityOnContribution();
        Double grr = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                summaryDto.getSummaryResultDto().getGrrOnTolerance() :
                summaryDto.getSummaryResultDto().getGrrOnContribution();
        Double lsl = summaryDto.getSummaryResultDto().getLsl();
        Double usl = summaryDto.getSummaryResultDto().getUsl();
        Double tolerance = summaryDto.getSummaryResultDto().getTolerance();
        String lslStr = lsl == null ? "-" : String.valueOf(lsl);
        String uslStr = usl == null ? "-" : String.valueOf(usl);
        String toleranceStr = tolerance == null ? "-" : (digNum >= 0 ? DAPStringUtils.formatDouble(tolerance, digNum + 2) : String.valueOf(tolerance));
        String repeatabilityStr = repeatability == null ? "-" : (digNum >= 0 ? DAPStringUtils.formatDouble(repeatability, digNum) : String.valueOf(repeatability));
        String reproducibilityStr = reproducibility == null ? "-" : (digNum >= 0 ? DAPStringUtils.formatDouble(reproducibility, digNum) : String.valueOf(reproducibility));
        String grrStr = grr == null ? "-" : (digNum >= 0 ? DAPStringUtils.formatDouble(grr, digNum) : String.valueOf(grr));
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
            Double repeatability = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    data.get(i).getSummaryResultDto().getRepeatabilityOnTolerance() :
                    data.get(i).getSummaryResultDto().getRepeatabilityOnContribution();
            Double reproducibility = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    data.get(i).getSummaryResultDto().getReproducibilityOnTolerance() :
                    data.get(i).getSummaryResultDto().getReproducibilityOnContribution();
            Double grr = UIConstant.GRR_RESULT_TYPE[0].equals(resultType) ?
                    data.get(i).getSummaryResultDto().getGrrOnTolerance() :
                    data.get(i).getSummaryResultDto().getGrrOnContribution();
            String repeatabilityStr = repeatability == null ? "-" : (digNum >= 0 ? DAPStringUtils.formatDouble(repeatability, digNum) : String.valueOf(repeatability));
            String reproducibilityStr = reproducibility == null ? "-" : (digNum >= 0 ? DAPStringUtils.formatDouble(reproducibility, digNum) : String.valueOf(reproducibility));
            String grrStr = grr == null ? "-" : digNum >= 0 ? DAPStringUtils.formatDouble(grr, digNum) : String.valueOf(grr);
            summaries.get(i).updateData(DAPStringUtils.isSpecialBlank(repeatabilityStr) ? "-" : repeatabilityStr + "%",
                    DAPStringUtils.isBlankWithSpecialNumber(reproducibilityStr) ? "-" : reproducibilityStr + "%",
                    DAPStringUtils.isBlankWithSpecialNumber(grrStr) ? "-" : grrStr + "%");
        }
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

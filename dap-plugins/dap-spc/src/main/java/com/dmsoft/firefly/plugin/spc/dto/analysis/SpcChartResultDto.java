package com.dmsoft.firefly.plugin.spc.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * spc chart result dto
 *
 * @author Can Guan
 */
public class SpcChartResultDto extends AbstractValueObject {
    private NDCResultDto ndcResult;
    private RunCResultDto runCResult;
    private ControlChartDto xbarCResult;
    private ControlChartDto rangeCResult;
    private ControlChartDto sdCResult;
    private ControlChartDto medianCResult;
    private ControlChartDto mrCResult;
    private BoxCResultDto boxCResult;

    public NDCResultDto getNdcResult() {
        return ndcResult;
    }

    public void setNdcResult(NDCResultDto ndcResult) {
        this.ndcResult = ndcResult;
    }

    public RunCResultDto getRunCResult() {
        return runCResult;
    }

    public void setRunCResult(RunCResultDto runCResult) {
        this.runCResult = runCResult;
    }

    public ControlChartDto getXbarCResult() {
        return xbarCResult;
    }

    public void setXbarCResult(ControlChartDto xbarCResult) {
        this.xbarCResult = xbarCResult;
    }

    public ControlChartDto getRangeCResult() {
        return rangeCResult;
    }

    public void setRangeCResult(ControlChartDto rangeCResult) {
        this.rangeCResult = rangeCResult;
    }

    public ControlChartDto getSdCResult() {
        return sdCResult;
    }

    public void setSdCResult(ControlChartDto sdCResult) {
        this.sdCResult = sdCResult;
    }

    public ControlChartDto getMedianCResult() {
        return medianCResult;
    }

    public void setMedianCResult(ControlChartDto medianCResult) {
        this.medianCResult = medianCResult;
    }

    public ControlChartDto getMrCResult() {
        return mrCResult;
    }

    public void setMrCResult(ControlChartDto mrCResult) {
        this.mrCResult = mrCResult;
    }

    public BoxCResultDto getBoxCResult() {
        return boxCResult;
    }

    public void setBoxCResult(BoxCResultDto boxCResult) {
        this.boxCResult = boxCResult;
    }
}

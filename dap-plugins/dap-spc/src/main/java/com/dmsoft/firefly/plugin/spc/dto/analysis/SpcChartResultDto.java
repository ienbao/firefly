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
    private SpcControlChartDto xbarCResult;
    private SpcControlChartDto rangeCResult;
    private SpcControlChartDto sdCResult;
    private SpcControlChartDto medianCResult;
    private SpcControlChartDto mrCResult;
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

    public SpcControlChartDto getXbarCResult() {
        return xbarCResult;
    }

    public void setXbarCResult(SpcControlChartDto xbarCResult) {
        this.xbarCResult = xbarCResult;
    }

    public SpcControlChartDto getRangeCResult() {
        return rangeCResult;
    }

    public void setRangeCResult(SpcControlChartDto rangeCResult) {
        this.rangeCResult = rangeCResult;
    }

    public SpcControlChartDto getSdCResult() {
        return sdCResult;
    }

    public void setSdCResult(SpcControlChartDto sdCResult) {
        this.sdCResult = sdCResult;
    }

    public SpcControlChartDto getMedianCResult() {
        return medianCResult;
    }

    public void setMedianCResult(SpcControlChartDto medianCResult) {
        this.medianCResult = medianCResult;
    }

    public SpcControlChartDto getMrCResult() {
        return mrCResult;
    }

    public void setMrCResult(SpcControlChartDto mrCResult) {
        this.mrCResult = mrCResult;
    }

    public BoxCResultDto getBoxCResult() {
        return boxCResult;
    }

    public void setBoxCResult(BoxCResultDto boxCResult) {
        this.boxCResult = boxCResult;
    }
}

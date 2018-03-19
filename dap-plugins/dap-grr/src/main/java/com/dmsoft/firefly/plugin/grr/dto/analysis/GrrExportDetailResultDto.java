package com.dmsoft.firefly.plugin.grr.dto.analysis;

public class GrrExportDetailResultDto extends GrrDetailResultDto {
    private Double lsl;
    private Double usl;
    private Double tolerance;
    private Double repeatabilityOnTolerance;
    private Double reproducibilityOnTolerance;
    private Double grrOnTolerance;

    private Double repeatabilityOnContribution;
    private Double reproducibilityOnContribution;
    private Double grrOnContribution;

    public Double getLsl() {
        return lsl;
    }

    public void setLsl(Double lsl) {
        this.lsl = lsl;
    }

    public Double getUsl() {
        return usl;
    }

    public void setUsl(Double usl) {
        this.usl = usl;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public void setTolerance(Double tolerance) {
        this.tolerance = tolerance;
    }

    public Double getRepeatabilityOnTolerance() {
        return repeatabilityOnTolerance;
    }

    public void setRepeatabilityOnTolerance(Double repeatabilityOnTolerance) {
        this.repeatabilityOnTolerance = repeatabilityOnTolerance;
    }

    public Double getReproducibilityOnTolerance() {
        return reproducibilityOnTolerance;
    }

    public void setReproducibilityOnTolerance(Double reproducibilityOnTolerance) {
        this.reproducibilityOnTolerance = reproducibilityOnTolerance;
    }

    public Double getGrrOnTolerance() {
        return grrOnTolerance;
    }

    public void setGrrOnTolerance(Double grrOnTolerance) {
        this.grrOnTolerance = grrOnTolerance;
    }

    public Double getRepeatabilityOnContribution() {
        return repeatabilityOnContribution;
    }

    public void setRepeatabilityOnContribution(Double repeatabilityOnContribution) {
        this.repeatabilityOnContribution = repeatabilityOnContribution;
    }

    public Double getReproducibilityOnContribution() {
        return reproducibilityOnContribution;
    }

    public void setReproducibilityOnContribution(Double reproducibilityOnContribution) {
        this.reproducibilityOnContribution = reproducibilityOnContribution;
    }

    public Double getGrrOnContribution() {
        return grrOnContribution;
    }

    public void setGrrOnContribution(Double grrOnContribution) {
        this.grrOnContribution = grrOnContribution;
    }
}

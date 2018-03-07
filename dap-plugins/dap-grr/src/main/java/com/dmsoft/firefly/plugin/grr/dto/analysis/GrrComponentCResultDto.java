package com.dmsoft.firefly.plugin.grr.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * component chart result dto
 *
 * @author Can Guan
 */
public class GrrComponentCResultDto extends AbstractValueObject {
    private Double grrContri;
    private Double grrVar;
    private Double grrTol;
    private Double repeatContri;
    private Double repeatVar;
    private Double repeatTol;
    private Double reprodContri;
    private Double reprodVar;
    private Double reprodTol;
    private Double partContri;
    private Double partVar;
    private Double partTol;

    public Double getGrrContri() {
        return grrContri;
    }

    public void setGrrContri(Double grrContri) {
        this.grrContri = grrContri;
    }

    public Double getGrrVar() {
        return grrVar;
    }

    public void setGrrVar(Double grrVar) {
        this.grrVar = grrVar;
    }

    public Double getGrrTol() {
        return grrTol;
    }

    public void setGrrTol(Double grrTol) {
        this.grrTol = grrTol;
    }

    public Double getRepeatContri() {
        return repeatContri;
    }

    public void setRepeatContri(Double repeatContri) {
        this.repeatContri = repeatContri;
    }

    public Double getRepeatVar() {
        return repeatVar;
    }

    public void setRepeatVar(Double repeatVar) {
        this.repeatVar = repeatVar;
    }

    public Double getRepeatTol() {
        return repeatTol;
    }

    public void setRepeatTol(Double repeatTol) {
        this.repeatTol = repeatTol;
    }

    public Double getReprodContri() {
        return reprodContri;
    }

    public void setReprodContri(Double reprodContri) {
        this.reprodContri = reprodContri;
    }

    public Double getReprodVar() {
        return reprodVar;
    }

    public void setReprodVar(Double reprodVar) {
        this.reprodVar = reprodVar;
    }

    public Double getReprodTol() {
        return reprodTol;
    }

    public void setReprodTol(Double reprodTol) {
        this.reprodTol = reprodTol;
    }

    public Double getPartContri() {
        return partContri;
    }

    public void setPartContri(Double partContri) {
        this.partContri = partContri;
    }

    public Double getPartVar() {
        return partVar;
    }

    public void setPartVar(Double partVar) {
        this.partVar = partVar;
    }

    public Double getPartTol() {
        return partTol;
    }

    public void setPartTol(Double partTol) {
        this.partTol = partTol;
    }
}

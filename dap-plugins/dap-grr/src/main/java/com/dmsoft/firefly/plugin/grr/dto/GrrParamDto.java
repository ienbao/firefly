package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.Map;
import java.util.Set;

/**
 * get auto parameter or error dto for grr
 *
 * @author Julia
 */
public class GrrParamDto extends AbstractValueObject {
    private Map<String, String> errors;
    private Set<String> parts;
    private Set<String> appraisers;

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public Set<String> getParts() {
        return parts;
    }

    public void setParts(Set<String> parts) {
        this.parts = parts;
    }

    public Set<String> getAppraisers() {
        return appraisers;
    }

    public void setAppraisers(Set<String> appraisers) {
        this.appraisers = appraisers;
    }
}

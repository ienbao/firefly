package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

/**
 * Created by cherry on 2018/3/14.
 */
public class GrrSingleSource {

    private StringProperty name;
    private StringProperty sigma;
    private StringProperty studyVar;
    private StringProperty variation;
    private StringProperty totalVariation;
    private StringProperty contribution;
    private StringProperty totalTolerance;

    public final static Map<String, String> propertyKeys = Maps.newHashMap();
    public final static String nameKey = "name";
    public final static String sigmaKey = "sigma";
    public final static String studyVarKey = "studyVar";
    public final static String variationKey = "variation";
    public final static String totalVariationKey = "totalVariation";
    public final static String contributionKey = "contribution";
    public final static String totalToleranceKey = "totalTolerance";

    private static String[] GRR_SOURCE_TITLE = new String[] {
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_SOURCE_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_SIGMA"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_STUDY_VAR"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_SIGMA"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_VARIATION"),
            GrrFxmlAndLanguageUtils.getString("GRR_SOURCE_TITLE_TOTAL_TOLERANCE")};

    static {
        propertyKeys.put(GRR_SOURCE_TITLE[0], nameKey);
        propertyKeys.put(GRR_SOURCE_TITLE[1], sigmaKey);
        propertyKeys.put(GRR_SOURCE_TITLE[2], studyVarKey);
        propertyKeys.put(GRR_SOURCE_TITLE[3], variationKey);
        propertyKeys.put(GRR_SOURCE_TITLE[4], totalVariationKey);
        propertyKeys.put(GRR_SOURCE_TITLE[5], contributionKey);
        propertyKeys.put(GRR_SOURCE_TITLE[6], totalToleranceKey);
    }

    public GrrSingleSource(String name,
                           String sigma,
                           String studyVar,
                           String variation,
                           String totalVariation,
                           String contribution,
                           String totalTolerance) {
        this.name = new SimpleStringProperty(name);
        this.sigma = new SimpleStringProperty(sigma);
        this.studyVar = new SimpleStringProperty(studyVar);
        this.variation = new SimpleStringProperty(variation);
        this.totalVariation = new SimpleStringProperty(totalVariation);
        this.contribution = new SimpleStringProperty(contribution);
        this.totalTolerance = new SimpleStringProperty(totalTolerance);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSigma() {
        return sigma.get();
    }

    public void setSigma(String sigma) {
        this.sigma.set(sigma);
    }

    public String getStudyVar() {
        return studyVar.get();
    }

    public void setStudyVar(String studyVar) {
        this.studyVar.set(studyVar);
    }

    public String getVariation() {
        return variation.get();
    }

    public void setVariation(String variation) {
        this.variation.set(variation);
    }

    public String getTotalVariation() {
        return totalVariation.get();
    }

    public void setTotalVariation(String totalVariation) {
        this.totalVariation.set(totalVariation);
    }

    public String getContribution() {
        return contribution.get();
    }

    public void setContribution(String contribution) {
        this.contribution.set(contribution);
    }

    public String getTotalTolerance() {
        return totalTolerance.get();
    }

    public void setTotalTolerance(String totalTolerance) {
        this.totalTolerance.set(totalTolerance);
    }
}

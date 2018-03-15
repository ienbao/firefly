package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

/**
 * Created by cherry on 2018/3/14.
 */
public class GrrSingleAnova {

    private StringProperty name;
    private StringProperty df;
    private StringProperty ss;
    private StringProperty ms;
    private StringProperty f;
    private StringProperty probF;

    public final static Map<String, String> propertyKeys = Maps.newHashMap();
    public final static String nameKey = "name";
    public final static String dfKey = "df";
    public final static String ssKey = "ss";
    public final static String msKey = "ms";
    public final static String fKey = "f";
    public final static String probFKey = "probF";

    static {
        propertyKeys.put(UIConstant.GRR_ANOVA_TITLE[0], nameKey);
        propertyKeys.put(UIConstant.GRR_ANOVA_TITLE[1], dfKey);
        propertyKeys.put(UIConstant.GRR_ANOVA_TITLE[2], ssKey);
        propertyKeys.put(UIConstant.GRR_ANOVA_TITLE[3], msKey);
        propertyKeys.put(UIConstant.GRR_ANOVA_TITLE[4], fKey);
        propertyKeys.put(UIConstant.GRR_ANOVA_TITLE[5], probFKey);
    }

    public GrrSingleAnova(String name, String df, String ss, String ms, String f, String probF) {
        this.name = new SimpleStringProperty(name);
        this.df = new SimpleStringProperty(df);
        this.ss = new SimpleStringProperty(ss);
        this.ms = new SimpleStringProperty(ms);
        this.f = new SimpleStringProperty(f);
        this.probF = new SimpleStringProperty(probF);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDf() {
        return df.get();
    }

    public void setDf(String df) {
        this.df.set(df);
    }

    public String getSs() {
        return ss.get();
    }

    public void setSs(String ss) {
        this.ss.set(ss);
    }

    public String getMs() {
        return ms.get();
    }

    public void setMs(String ms) {
        this.ms.set(ms);
    }

    public String getF() {
        return f.get();
    }

    public void setF(String f) {
        this.f.set(f);
    }

    public String getProbF() {
        return probF.get();
    }

    public void setProbF(String probF) {
        this.probF.set(probF);
    }
}

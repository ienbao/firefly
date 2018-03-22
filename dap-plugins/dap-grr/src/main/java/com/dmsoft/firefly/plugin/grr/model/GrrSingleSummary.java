package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.beans.property.*;

import java.util.Map;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrSingleSummary {

    private String originalUsl;
    private String originalLsl;
    private BooleanProperty selected;
    private StringProperty itemName;
    private SimpleStringProperty lsl;
    private SimpleStringProperty usl;
    private StringProperty tolerance = new SimpleStringProperty("");
    private StringProperty repeatability = new SimpleStringProperty("");
    private StringProperty reproducibility = new SimpleStringProperty("");
    private StringProperty grr = new SimpleStringProperty("");

    public final static Map<String, String> propertyKeys = Maps.newHashMap();

    public final static String grrKey = "grr";
    public final static String lslKey = "lsl";
    public final static String uslKey = "usl";
    public final static String selectedKey = "selected";
    public final static String itemNameKey = "itemName";
    public final static String toleranceKey = "tolerance";
    public final static String repeatabilityKey = "repeatability";
    public final static String reproducibilityKey = "reproducibility";

    private static String[] GRR_SUMMARY_TITLE = new String[] {
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_TESTITEM"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_LSL"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_USL"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_TOLERANCE"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_REPEATABILITY"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_REPRODUCIBILITY"),
            GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_TITLE_GAUGE")
    };

    static {
        propertyKeys.put(GRR_SUMMARY_TITLE[0], itemNameKey);
        propertyKeys.put(GRR_SUMMARY_TITLE[1], lslKey);
        propertyKeys.put(GRR_SUMMARY_TITLE[2], uslKey);
        propertyKeys.put(GRR_SUMMARY_TITLE[3], toleranceKey);
        propertyKeys.put(GRR_SUMMARY_TITLE[4], repeatabilityKey);
        propertyKeys.put(GRR_SUMMARY_TITLE[5], reproducibilityKey);
        propertyKeys.put(GRR_SUMMARY_TITLE[6], grrKey);
    }

    public GrrSingleSummary(boolean selected,
                            String itemName,
                            String lsl,
                            String usl,
                            String tolerance,
                            String repeatability,
                            String reproducibility,
                            String grr) {

        this.selected = new SimpleBooleanProperty(selected);
        this.itemName = new SimpleStringProperty(itemName);
        this.lsl = new SimpleStringProperty(lsl);
        this.usl = new SimpleStringProperty(usl);
        this.originalLsl = lsl;
        this.originalUsl = usl;
        this.tolerance.set(tolerance);
        this.updateData(repeatability, reproducibility, grr);
    }

    public void updateData(String repeatability,
                        String reproducibility,
                        String grr) {
        this.repeatability.set(repeatability);
        this.reproducibility.set(reproducibility);
        this.grr.set(grr);
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setSelect(boolean selected) {
        this.selected.set(selected);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public String getLsl() {
        return lsl.get();
    }

    public String getUsl() {
        return usl.get();
    }

    public String getTolerance() {
        return tolerance.get();
    }

    public String getRepeatability() {
        return repeatability.get();
    }

    public String getReproducibility() {
        return reproducibility.get();
    }

    public String getGrr() {
        return grr.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public void setLsl(String lsl) {
        this.lsl.set(lsl);
    }

    public void setUsl(String usl) {
        this.usl.set(usl);
    }

    public String getOriginalUsl() {
        return originalUsl;
    }

    public String getOriginalLsl() {
        return originalLsl;
    }

}

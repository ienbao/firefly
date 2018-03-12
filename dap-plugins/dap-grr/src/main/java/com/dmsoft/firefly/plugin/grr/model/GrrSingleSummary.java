package com.dmsoft.firefly.plugin.grr.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrSingleSummary {

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final StringProperty itemName = new SimpleStringProperty("");
    private final StringProperty lsl = new SimpleStringProperty("");
    private final StringProperty usl = new SimpleStringProperty("");
    private final StringProperty tolerance = new SimpleStringProperty("");
    private final StringProperty repeatability = new SimpleStringProperty("");
    private final StringProperty reproducibility = new SimpleStringProperty("");
    private final StringProperty grr = new SimpleStringProperty("");

    public final static String grrKey = "grr";
    public final static String lslKey = "lsl";
    public final static String uslKey = "usl";
    public final static String selectedKey = "selected";
    public final static String itemNameKey = "itemName";
    public final static String toleranceKey = "tolerance";
    public final static String repeatabilityKey = "repeatability";
    public final static String reproducibilityKey = "reproducibility";

    public GrrSingleSummary() {
    }

    public GrrSingleSummary(boolean selected,
                            String itemName,
                            String lsl,
                            String usl,
                            String tolerance,
                            String repeatability,
                            String reproducibility,
                            String grr) {

        this.selected.setValue(selected);
        this.itemName.set(itemName);
        this.lsl.setValue(lsl);
        this.usl.setValue(usl);
        this.tolerance.setValue(tolerance);
        this.updateData(repeatability, reproducibility, grr);
    }

    public void updateData(String repeatability,
                        String reproducibility,
                        String grr) {
        this.repeatability.setValue(repeatability);
        this.reproducibility.setValue(reproducibility);
        this.grr.setValue(grr);
    }
}

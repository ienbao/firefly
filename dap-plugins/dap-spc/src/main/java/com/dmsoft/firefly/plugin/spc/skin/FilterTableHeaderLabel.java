package com.dmsoft.firefly.plugin.spc.skin;

import com.sun.javafx.scene.control.skin.LabelSkin;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * skin class for table header label with filter header
 */
public class FilterTableHeaderLabel extends LabelSkin {
    private Label label;

    /**
     * constructor
     *
     * @param label label
     */
    public FilterTableHeaderLabel(Label label) {
        super(label);
        this.label = label;
    }

    @Override
    protected void layoutLabelInArea(double x, double y, double w, double h, Pos alignment) {
        super.layoutLabelInArea(x, y, w, h, alignment);
        label.getGraphic().relocate(w - 15, 0);
    }
}

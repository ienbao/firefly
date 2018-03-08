/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.components.utils;

import com.sun.javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;

/**
 * Created by Ethan.Yang on 2018/3/6.
 */
public class ColorPickerMenuSkin extends ColorPickerSkin {
    private Label displayNode;
    /**
     * constructor
     *
     * @param colorPicker color picker
     */
    public ColorPickerMenuSkin(ColorPicker colorPicker) {
        super(colorPicker);
        displayNode = new Label("Select Color");
    }

    @Override public Node getDisplayNode() {
        return displayNode;
    }
}

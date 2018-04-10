/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.components.colorpicker;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.sun.javafx.scene.control.skin.ColorPalette;
import com.sun.javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.Locale;

/**
 * Created by Ethan.Yang on 2018/3/6.
 */
public class ColorPickerMenuSkin extends ColorPickerSkin {
    private Label displayNode;
    private DColorPalette popupContent;
    /**
     * constructor
     *
     * @param colorPicker color picker
     */
    public ColorPickerMenuSkin(ColorPicker colorPicker) {
        super(colorPicker);
        displayNode = new Label(FxmlAndLanguageUtils.getString("SELECT_COLOR"));
    }

    @Override public Node getDisplayNode() {
        return displayNode;
    }

    @Override protected Node getPopupContent() {
        super.getPopupContent();
        if (popupContent == null) {
//            popupContent = new ColorPalette(colorPicker.getValue(), colorPicker);
            popupContent = new DColorPalette((ColorPicker)getSkinnable());
            popupContent.setPopupControl(getPopup());
            popupContent.setStyle("-fx-background-color: white");
        }
        return popupContent;
    }

    @Override public void show() {
        super.show();
        final ColorPicker colorPicker = (ColorPicker)getSkinnable();
        popupContent.updateSelection(colorPicker.getValue());
    }

    @Override protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);

        if ("SHOWING".equals(p)) {
            if (getSkinnable().isShowing()) {
                show();
            } else {
                if (!popupContent.isCustomColorDialogShowing()) hide();
            }
        } else if ("VALUE".equals(p)) {
//            updateColor();
            // Change the current selected color in the grid if ColorPicker value changes
            if (popupContent != null) {
//                popupContent.updateSelection(getSkinnable().getValue());
            }
        }
    }

    public static String formatHexString(Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                    Math.round(c.getRed() * 255),
                    Math.round(c.getGreen() * 255),
                    Math.round(c.getBlue() * 255));
        } else {
            return null;
        }
    }

}

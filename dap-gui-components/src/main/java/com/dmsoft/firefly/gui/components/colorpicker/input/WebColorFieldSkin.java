/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.components.colorpicker.input;

import com.dmsoft.firefly.gui.components.colorpicker.ColorPickerMenuSkin;
import javafx.beans.InvalidationListener;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.Locale;

/**
 * Created by Ethan.Yang on 2018/4/9.
 */
public class WebColorFieldSkin extends InputFieldSkin {
    private InvalidationListener integerFieldValueListener;
    private boolean noChangeInValue = false;

    /**
     * Create a new WebColorFieldSkin.
     * @param control The WebColorField
     */
    public WebColorFieldSkin(WebColorField control) {
        super(control);

        // Whenever the value changes on the control, we need to update the text
        // in the TextField. The only time this is not the case is when the update
        // to the control happened as a result of an update in the text textField.
        control.valueProperty().addListener(integerFieldValueListener = observable -> {
            updateText();
        });

        // RT-37494: Force the major text direction to LTR, so that '#' is always
        // on the left side of the text. A special style is used in CSS to keep
        // the text right-aligned when in RTL mode.
        getTextField().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    }

    @Override public WebColorField getSkinnable() {
        return (WebColorField) control;
    }

    @Override public Node getNode() {
        return getTextField();
    }

    /**
     * Called by a Skinnable when the Skin is replaced on the Skinnable. This method
     * allows a Skin to implement any logic necessary to clean up itself after
     * the Skin is no longer needed. It may be used to release native resources.
     * The methods {@link #getSkinnable()} and {@link #getNode()}
     * should return null following a call to dispose. Calling dispose twice
     * has no effect.
     */
    @Override public void dispose() {
        ((WebColorField) control).valueProperty().removeListener(integerFieldValueListener);
        super.dispose();
    }

    //  "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    protected boolean accept(String text) {
        if (text.length() == 0) return true;
        if (text.matches("#[a-fA-F0-9]{0,6}") || text.matches("[a-fA-F0-9]{0,6}")) {
            return true;
        }
        return false;
    }

    protected void updateText() {
        Color color = ((WebColorField) control).getValue();
        if (color == null) color = Color.BLACK;
        getTextField().setText(ColorPickerMenuSkin.formatHexString(color));
    }

    protected void updateValue() {
        if (noChangeInValue) return;
        Color value = ((WebColorField) control).getValue();
        String text = getTextField().getText() == null ? "" : getTextField().getText().trim().toUpperCase(Locale.ROOT);
        if (text.matches("#[A-F0-9]{6}") || text.matches("[A-F0-9]{6}")) {
            try {
                Color newValue = (text.charAt(0) == '#')? Color.web(text) : Color.web("#"+text);
                if (!newValue.equals(value)) {
                    ((WebColorField) control).setValue(newValue);
                } else {
                    // calling setText results in updateValue - so we set this flag to true
                    // so that when this is true updateValue simply returns.
                    noChangeInValue = true;
                    getTextField().setText(ColorPickerMenuSkin.formatHexString(newValue));
                    noChangeInValue = false;
                }
            } catch (java.lang.IllegalArgumentException ex) {
                System.out.println("Failed to parse ["+text+"]");
            }
        }
    }
}
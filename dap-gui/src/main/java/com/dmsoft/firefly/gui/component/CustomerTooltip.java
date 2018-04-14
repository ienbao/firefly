package com.dmsoft.firefly.gui.component;

import javafx.scene.control.Tooltip;

/**
 * expanded tool tip
 * @author Julia
 */
public class CustomerTooltip extends Tooltip {
    private boolean isHover = false;
    @Override
    public void hide() {
        if (!isHover) {
            super.hide();
        }
    }

    public boolean isHover() {
        return isHover;
    }

    public void setHover(boolean hover) {
        isHover = hover;
    }
}

package com.dmsoft.firefly.gui.components.window;

public interface WindowCustomListener {
    /**
     * An event implemented before closing or cancel
     *
     */
    void onCloseAndCancelCustomEvent();

    /**
     * An event implemented before ok
     *
     */
    void onOkCustomEvent();
}

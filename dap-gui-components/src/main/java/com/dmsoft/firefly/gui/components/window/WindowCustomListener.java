package com.dmsoft.firefly.gui.components.window;

public interface WindowCustomListener {

    /**
     * An event implemented before show
     *
     */
    boolean onShowCustomEvent();

    /**
     * An event implemented before closing or cancel
     *
     */
    boolean onCloseAndCancelCustomEvent();

    /**
     * An event implemented before ok
     *
     */
    boolean onOkCustomEvent();
}

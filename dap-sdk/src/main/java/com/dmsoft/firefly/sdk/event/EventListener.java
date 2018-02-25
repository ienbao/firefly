package com.dmsoft.firefly.sdk.event;

/**
 * interface class for event listener
 *
 * @author Can Guan
 */
@FunctionalInterface
public interface EventListener {
    /**
     * @param event platform event
     */
    void eventChanged(PlatformEvent event);
}

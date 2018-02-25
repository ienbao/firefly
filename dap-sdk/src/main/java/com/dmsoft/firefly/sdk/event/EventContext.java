package com.dmsoft.firefly.sdk.event;

import java.util.List;

/**
 * interface for event context
 *
 * @author Can Guan
 */
public interface EventContext {
    /**
     * method to push event.
     *
     * @param event platform event
     */
    void pushEvent(PlatformEvent event);

    /**
     * method to add event listener.
     *
     * @param eventListener event listener
     */
    void addEventListener(EventListener eventListener);

    /**
     * method to remove event listener.
     *
     * @param eventListener event listener
     */
    void removeEventListener(EventListener eventListener);

    /**
     * method to get all listeners.
     *
     * @return list of event listeners
     */
    List<EventListener> getAllListeners();
}

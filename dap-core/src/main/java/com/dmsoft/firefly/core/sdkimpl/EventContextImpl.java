package com.dmsoft.firefly.core.sdkimpl;

import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventListener;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Impl class for event context
 *
 * @author Can Guan
 */
public class EventContextImpl implements EventContext {
    private List<EventListener> listeners = Lists.newArrayList();

    @Override
    public void pushEvent(PlatformEvent event) {
        for (EventListener listener : listeners) {
            listener.eventChanged(event);
        }
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        this.listeners.add(eventListener);
    }

    @Override
    public void removeEventListener(EventListener eventListener) {
        this.listeners.remove(eventListener);
    }

    @Override
    public List<EventListener> getAllListeners() {
        return Lists.newArrayList(listeners);
    }
}

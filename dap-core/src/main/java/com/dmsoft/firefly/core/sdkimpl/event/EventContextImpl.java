package com.dmsoft.firefly.core.sdkimpl.event;

import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventListener;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.CollectionUtils;

/**
 * 合局事件模型简实现
 * TODO yuanwen 简单做一个全局同步
 *
 * @author Can Guan
 * @author yuanwen
 *
 */
public class EventContextImpl implements EventContext {
    private Map<EventType, List<EventListener>> map = new HashMap<EventType, List<EventListener>>();

    @Override
    public void pushEvent(PlatformEvent event) {
        synchronized (map) {
            List<EventListener> targetList = map.get(event.getEventType());
            if (CollectionUtils.isEmpty(targetList)) {
                return;
            }

            for (EventListener listener : targetList) {
                listener.eventNotify(event);
            }
        }
    }

    @Override
    public void addEventListener(EventType eventType, EventListener eventListener) {
        synchronized (map) {
            List<EventListener> targetList = this.map.get(eventType);

            if (targetList == null) {
                targetList = new ArrayList<EventListener>();
                this.map.put(eventType, targetList);
            }

            targetList.add(eventListener);
        }
    }

    @Override
    public void removeEventListener(EventType eventType, EventListener eventListener) {
        synchronized (map) {
            List<EventListener> targetList = this.map.get(eventType);
            if (targetList == null) {
                return;
            }

            targetList.remove(eventListener);
        }
    }



    /**
     * 移除所有事件处理器
     *
     * @param eventType 事件类型
     */
    @Override
    public void removeEventListener(EventType eventType) {
        synchronized (map) {
            this.map.remove(eventType);
        }
    }

//    @Override
//    public List<EventListener> getAllListeners() {
//
//        return Lists.newArrayList(listeners);
//    }
}

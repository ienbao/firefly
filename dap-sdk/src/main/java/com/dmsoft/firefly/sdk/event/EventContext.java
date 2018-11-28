package com.dmsoft.firefly.sdk.event;

import java.util.List;

/**
 * 修改监听事件总线，处理新事件模型
 *
 * @author Can Guan
 * @author yuanwen
 */
public interface EventContext {
    /**
     * 发布事件
     *
     * @param event 事件
     */
    void pushEvent(PlatformEvent event);

    /**
     * 添加事件监听器
     *
     * @param eventType 事件类型
     * @param eventListener 事件处理器
     *
     */
    void addEventListener(EventType eventType, EventListener eventListener);

    /**
     * 移除单个事件处理器
     *
     * @param eventType 事件
     * @param eventListener 事件处理器
     */
    void removeEventListener(EventType eventType, EventListener eventListener);


    /**
     * 移除所有事件处理器
     *
     * @param eventType 事件
     */
    void removeEventListener(EventType eventType);

//    /**
//     * method to get all listeners.
//     *
//     * @return list of event listeners
//     */
//    List<EventListener> getAllListeners();
}

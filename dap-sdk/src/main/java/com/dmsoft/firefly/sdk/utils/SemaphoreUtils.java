package com.dmsoft.firefly.sdk.utils;

import com.dmsoft.firefly.sdk.RuntimeContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * semaphore utils class
 *
 * @author Can Guan
 */
public class SemaphoreUtils {
    private static final Map<String, Semaphore> SEMAPHORE_MAP = new HashMap<>();

    /**
     * method to acquire sempahore
     *
     * @param lockName lock name
     * @return semaphore
     */
    public static synchronized Semaphore acquireSemaphore(String lockName) {
        if (!SEMAPHORE_MAP.containsKey(lockName)) {
            SEMAPHORE_MAP.put(lockName, new Semaphore(1));
        }
        return SEMAPHORE_MAP.get(lockName);
    }

    /**
     * method to acquire sempahore
     *
     * @param o instance
     * @return semaphore
     */
    public static synchronized Semaphore acquireSemaphore(Object o) {
        String lockName = RuntimeContext.getSimpleBeanName(o.getClass().getName());
        return acquireSemaphore(lockName);
    }

    /**
     * method to lock sempahore
     *
     * @param lockName lock name
     * @return semaphore
     */
    public static synchronized boolean lockSemaphore(String lockName) {
        Semaphore semaphore = acquireSemaphore(lockName);
        try {
            semaphore.acquire();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * method to lock sempahore
     *
     * @param o instance
     * @return semaphore
     */
    public static synchronized boolean lockSemaphore(Object o) {
        String lockName = RuntimeContext.getSimpleBeanName(o.getClass().getName());
        return lockSemaphore(lockName);
    }

    /**
     * method to release semaphore
     *
     * @param lockName lock name
     */
    public static synchronized void releaseSemaphore(String lockName) {
        acquireSemaphore(lockName).release();
    }

    /**
     * method to release semaphore
     *
     * @param o instance
     */
    public static synchronized void releaseSemaphore(Object o) {
        String lockName = RuntimeContext.getSimpleBeanName(o.getClass().getName());
        acquireSemaphore(lockName).release();
    }
}

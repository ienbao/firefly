/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.spc.utils;

import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * Declare the R Utils tools.
 */
public final class RUtils {
    private Logger logger = LoggerFactory.getLogger(RUtils.class);

    private REnConnector connector = null;
    private static Semaphore semaphore = new Semaphore(1);

    private RUtils() {
        logger.info("RUtils Create!");
        connector = new REnConnector();
        connector.connect();
    }

    public Rengine getREngine() {
        return connector.getREngine();
    }

    public REnConnector getConnector() {
        return connector;
    }

    /**
     * Class single holder.
     */
    private static class SingletonHolder {
        private static final RUtils INSTANCE = new RUtils();
    }

    /**
     * Return RUtils single instance.
     *
     * @return RUtils
     */
    public static RUtils getInstance() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SingletonHolder.INSTANCE;
    }

    public static Semaphore getSemaphore() {
        return semaphore;
    }
}

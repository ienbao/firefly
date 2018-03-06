/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.utils;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Declare the R engine connector.
 */
public class REnConnector {
    private Logger logger = LoggerFactory.getLogger(REnConnector.class);

    private Rengine re = null;
    private boolean active = false;

    protected void destroy() {
        connect();
    }

    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    /**
     * Connect R server with engine.
     */
    public void connect() {
        if (re == null) {
            logger.info("R Create!");
            re = new Rengine(new String[]{"--slave"}, false, null);

            active = re.waitForR();

            if (!active) {
                logger.error("R Create Fail", "");
            }
        }
    }

    public Rengine getREngine() {
        return re;
    }

    /**
     * Disconnect R server.
     */
    public void disconnect() {
        if (isActive()) {
            logger.info("R end!");

            if (re != null) {
                re.end();
            }
        }
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Set value to R context.
     *
     * @param name  variable name.
     * @param value variable value for double.
     * @return REXP
     */
    public REXP setInput(String name, double value) {
        return re.eval(name + "<-" + String.valueOf(value));
    }

    /**
     * Set value to R context.
     *
     * @param name  variable name.
     * @param value variable value for double[].
     * @return boolean
     */
    public boolean setInput(String name, double[] value) {
        return re.assign(name, value);
    }

    /**
     * Set value to R context.
     *
     * @param name  variable name.
     * @param value variable value for int[].
     * @return boolean
     */
    public boolean setInput(String name, int[] value) {
        return re.assign(name, value);
    }

    /**
     * Set value to R context.
     *
     * @param name  variable name.
     * @param value variable value for String[].
     * @return boolean
     */
    public boolean setInput(String name, String[] value) {
        return re.assign(name, value);
    }

    /**
     * Set value to R context.
     *
     * @param name  variable name.
     * @param value variable value for String.
     * @return boolean
     */
    public boolean setInput(String name, String value) {
        return re.assign(name, value);
    }

    /**
     * Execute command to R.
     *
     * @param name   variable name.
     * @param cmdStr command.
     */
    public void execEval(String name, String cmdStr) {
        execEval(String.format("%s <- %s", name, cmdStr));
    }

    /**
     * Return the result of command.
     *
     * @param cmdStr command.
     * @return return execute result from r
     */
    public REXP getEval(String cmdStr) {
        try {
            return re.eval(cmdStr);
        } catch (Exception ex) {
            logger.error(String.format("getEval-%s", cmdStr), ex);
            return null;
        }
    }

    /**
     * Return the result of command.
     *
     * @param cmdStr command.
     */
    public void execEval(String cmdStr) {
        getEval(cmdStr);
    }

    /**
     * Get output double.
     *
     * @param cmdStr command.
     * @return result of double
     */
    public double getOutputDouble(String cmdStr) {
        if (getEval(cmdStr) == null) {
            return 0.0;
        }
        return getEval(cmdStr).asDouble();
    }

    /**
     * Get output int.
     *
     * @param cmdStr command.
     * @return result of int
     */
    public int getOutputInt(String cmdStr) {
        if (getEval(cmdStr) == null) {
            return 0;
        }
        return getEval(cmdStr).asInt();
    }

    /**
     * Get output string.
     *
     * @param cmdStr command.
     * @return result of string
     */
    public String getOutputString(String cmdStr) {
        if (getEval(cmdStr) == null) {
            return null;
        }
        return getEval(cmdStr).asString();
    }

    /**
     * Get output with string array.
     *
     * @param cmdStr command.
     * @return result of string array
     */
    public String[] getOutputStringArray(String cmdStr) {
        if (getEval(cmdStr) == null) {
            return null;
        }
        return getEval(cmdStr).asStringArray();
    }

    /**
     * Get output with int array.
     *
     * @param cmdStr command.
     * @return result of int array
     */
    public int[] getOutputIntArray(String cmdStr) {
        if (getEval(cmdStr) == null) {
            return null;
        }
        return getEval(cmdStr).asIntArray();
    }

    /**
     * Get output with double array.
     *
     * @param cmdStr command.
     * @return result of double array
     */
    public double[] getOutputDoubleArray(String cmdStr) {
        if (getEval(cmdStr) == null) {
            return null;
        }
        return getEval(cmdStr).asDoubleArray();
    }
}

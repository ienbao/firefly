/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;

import com.dmsoft.firefly.sdk.plugin.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.plugin.annotation.OpenService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

/**
 * spc service
 */
@OpenService
public class SpcService {
    /**
     * say method
     *
     * @param s string
     */
    public void say(String s) {
        System.out.println("Hello " + s + "!");
        JFreeChart chart = ChartFactory.createBarChart(null, null, null, null);
    }

    /**
     * sdfas
     */
    @ExcludeMethod
    public void excludedMethod() {
        System.out.println("Private method");
    }

    /**
     * main
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        System.out.println("ASFDA");
    }
}

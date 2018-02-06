/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr;

import com.dmsoft.firefly.sdk.plugin.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.plugin.annotation.OpenService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

/**
 * grr service
 */
@OpenService
public class GrrService {
    /**
     * say method
     *
     * @param s string
     */
    public void say(String s) {
        System.out.println("Hello " + s + "!");
        JFreeChart chart = ChartFactory.createBarChart("Chart", null, null, null);
        System.out.println(chart.getTitle());
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

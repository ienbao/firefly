/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.restart.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by julia on 18/4/9.
 */
public class PropertyConfig {
    private static Properties properties = new Properties();

    /**
     * get properties
     *
     * @return properties
     */
    public static Properties getProperties(String filePath){
        try {
            InputStream fis = null;
            try {
                fis = new FileInputStream(new File(filePath));
            } catch (Exception e) {
                System.out.println("can't find application.properties");
            }
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}

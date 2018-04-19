/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ethan.Yang on 2018/4/19.
 */
public class CloseMongoDBUtil {

    /**
     * close mongodb
     */
    public static void closeMongoDB(){

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            String run = "." + File.separator + "installmongo.bat stop";
            File dirFile = new File(run);
            dirFile.setReadable(true, false);
            dirFile.setExecutable(true, false);
            dirFile.setWritable(true, false);
            try {
                Runtime.getRuntime().exec(run);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

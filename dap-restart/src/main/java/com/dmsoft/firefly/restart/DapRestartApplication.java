/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.restart;


import com.dmsoft.firefly.restart.utils.ApplicationPathUtil;
import com.dmsoft.firefly.restart.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public class DapRestartApplication {

    public static void main(String[] args) throws IOException, InterruptedException {

        Thread.sleep(1000);

        Arrays.stream(args).forEach(v -> {
            if (v.contains("delete:")) {
                FileUtils.deleteFolder(v.replace("delete:", ""));
            }
            if (v.contains("cover:")) {
                try {
                    String coverAll = v.replace("cover:", "");
                    String filePath = coverAll.split(":coverPath:")[0];
                    String coverPath = coverAll.split(":coverPath:")[1];
                    FileUtils.unZipFilesNoFileName(new File(filePath), coverPath + "/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Properties pro = new Properties();
        try {
            String file = ApplicationPathUtil.getCanonicalPath() + "application.properties";
            InputStream fis = null;
            try {
                fis = new FileInputStream(new File(file));
            } catch (Exception e) {
                System.out.println("can't find application.properties");
            }
            pro.load(fis);
            Runtime.getRuntime().exec(pro.getProperty("restart_command"));
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

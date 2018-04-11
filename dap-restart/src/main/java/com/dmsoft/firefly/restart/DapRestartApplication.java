/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.restart;


import com.dmsoft.firefly.restart.utils.FileUtils;
import com.dmsoft.firefly.restart.utils.PropertyConfig;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public class DapRestartApplication {

    public static void main(String[] args) throws IOException, InterruptedException {

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
        try {
            String run = "." + File.separator + "startup.sh";
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                run = "." + File.separator + "startup.bat";
            }
            File dirFile = new File(run);
            dirFile.setReadable(true, false);
            dirFile.setExecutable(true, false);
            dirFile.setWritable(true, false);
            Runtime.getRuntime().exec(run);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.restart;


import com.dmsoft.firefly.restart.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public class DapRestartApplication {
    private final static Logger logger = LoggerFactory.getLogger(DapRestartApplication.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("start dap-restart.jar..");
//        Thread.sleep(2000);
        Arrays.stream(args).forEach(v -> {
//            System.out.println(v);
            if (v.contains("delete:")) {
                logger.info("start delete plugin folder" + v);
                String path = v.replace("#!@$", " ");
                logger.info("start delete plugin folder:" + path);
                boolean isDelete = FileUtils.deleteFolder(path.replace("delete:", ""));
                if (isDelete) {
                    logger.info("delete plugin folder success");
                } else {
                    logger.info("delete plugin folder fail");
                }
            }
            if (v.contains("cover:")) {
                try {
                    logger.info("start cover original plugin" + v);
                    String path = v.replace("#!@$", " ");
                    logger.info("start cover original plugin:" + path);
                    String coverAll = path.replace("cover:", "");
                    String filePath = coverAll.split(":coverPath:")[0];
                    String coverPath = coverAll.split(":coverPath:")[1];
                    FileUtils.unZipFilesNoFileName(new File(filePath), coverPath + "/");
                    logger.info("end cover original plugin");
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
            logger.info("restart new application");
            Runtime.getRuntime().exec(run);
            Thread.sleep(3000);
            logger.info("restart new application finish");
            logger.info("close old application");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

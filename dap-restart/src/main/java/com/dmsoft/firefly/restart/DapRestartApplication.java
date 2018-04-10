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

      /*  Arrays.stream(args).forEach(v -> {
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
        });*/
       // try {
          /*  String file = System.getProperty("user.dir") + File.separator + "application.properties";
            Properties properties = PropertyConfig.getProperties(file);
            System.out.println("start_command:" + properties.getProperty("start_command"));
            String run = System.getProperty("user.dir") + File.separator + "startup.sh";

            File dirFile = new File(run);
            dirFile.setReadable(true, false);
            dirFile.setExecutable(true, false);
            dirFile.setWritable(true, false);
           // String run = "." + File.separator + "startup.sh";
            Runtime.getRuntime().exec(run);
            System.exit(0);*/


            String run = "/Users/julia/projects/spc2.5/deploy/dap/" + "restart.sh";
            File dirFile = new File(run);
            dirFile.setReadable(true, false);
            dirFile.setExecutable(true, false);
            dirFile.setWritable(true, false);
            ProcessBuilder pBuilder = new ProcessBuilder();
            Map<String, String> penv = pBuilder.environment(); //获得进程的环境
            penv.put("APP_JAR", "dap-restart-2.5.0-SNAPSHOT.jar pluginFolderPath:/Users/julia/projects/spc2.5/deploy/dap/./Plugins/ delete:/Users/julia/projects/spc2.5/deploy/dap/./Plugins/dap-csv-resolver");
            //String run = "." + File.separator + "restart.sh";
            System.out.println(run);
           /* pBuilder.command(run);
            pBuilder.start();*/
        Process d = Runtime.getRuntime().exec(run);
        d.getErrorStream();
        d.getInputStream();
        d.getOutputStream();
            //System.exit(0);
       /* } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}

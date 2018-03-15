/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.restart;


import com.dmsoft.firefly.restart.utils.FileUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public class DapRestartApplication {

    public static void main(String[] args) throws IOException, InterruptedException {

        Thread.sleep(1000);

        Arrays.stream(args).forEach(v -> {
            FileUtils.deleteFolder(v);
        });
        Runtime.getRuntime().exec("java -jar dap-gui-1.0.0.jar");
        System.exit(0);

    }
}

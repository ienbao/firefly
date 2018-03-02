package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.handler.importcsv.CsvImportHandler;
import com.dmsoft.firefly.gui.handler.importcsv.ResolverSelectHandler;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.utils.MessageManagerFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GuiApplication extends Application {

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DAPApplication.run(Lists.newArrayList("com.dmsoft.dap.SpcPlugin", "com.dmsoft.dap.GrrPlugin", "com.dmsoft.dap.CsvResolverPlugin"));
        RuntimeContext.registerBean(IMessageManager.class, new MessageManagerFactory());

        MenuFactory.initMenu();

        Pane root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/app_menu.fxml"));
        Pane main = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/main.fxml"));
        primaryStage = WindowFactory.createFullWindow("platform_gui", root, main, getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm());
        primaryStage.show();
        initJob();
        NodeMap.addNode("platform_main", main);
    }

    private static void initJob() {
        JobManager manager = RuntimeContext.getBean(JobManager.class);
        manager.initializeJob("import", new InitJobPipeline() {
            @Override
            public void initJobPipeline(JobPipeline pipeline) {
                pipeline.addLast("resolver", new ResolverSelectHandler());
                pipeline.addLast("import", new CsvImportHandler());
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}

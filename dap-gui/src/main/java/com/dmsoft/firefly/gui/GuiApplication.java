package com.dmsoft.firefly.gui;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.handler.importcsv.CsvImportHandler;
import com.dmsoft.firefly.gui.handler.importcsv.ResolverSelectHandler;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.KeyValueDto;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.utils.MessageManagerFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class GuiApplication extends Application {

    private final String parentPath = this.getClass().getResource("/").getPath() + "config";
    private JsonMapper mapper = JsonMapper.defaultMapper();

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        String json = JsonFileUtil.readJsonFile(parentPath, "activePlugin");
        List<KeyValueDto> activePlugin = Lists.newArrayList();
        if (DAPStringUtils.isNotBlank(json)) {
            activePlugin = mapper.fromJson(json, mapper.buildCollectionType(List.class, KeyValueDto.class));
        }
        List<String> plugins = Lists.newArrayList();
        if (activePlugin != null) {
            activePlugin.forEach(v -> {
                plugins.add(v.getKey());
            });
        }
        DAPApplication.run(Lists.newArrayList(plugins));
        RuntimeContext.registerBean(IMessageManager.class, new MessageManagerFactory());

        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        if (languageType == null) {
            RuntimeContext.getBean(EnvService.class).setLanguageType(LanguageType.EN);
        } else {
            RuntimeContext.getBean(EnvService.class).setLanguageType(RuntimeContext.getBean(EnvService.class).getLanguageType());
        }

        MenuFactory.initMenu();

        FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/app_menu.fxml");
        FXMLLoader fxmlLoader1 = GuiFxmlAndLanguageUtils.getLoaderFXML("view/main.fxml");

        Pane root = fxmlLoader.load();
        Pane main = fxmlLoader1.load();

        MenuFactory.setMainController(fxmlLoader1.getController());
        MenuFactory.setAppController(fxmlLoader.getController());

        primaryStage = WindowFactory.createFullWindow("platform_gui", root, main, getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm());
        primaryStage.show();
        initJob();
        NodeMap.addNode("platform_main", main);
    }

    private void initJob() {
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

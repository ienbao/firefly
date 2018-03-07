package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.handler.importcsv.CsvImportHandler;
import com.dmsoft.firefly.gui.handler.importcsv.ResolverSelectHandler;
import com.dmsoft.firefly.gui.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.job.core.InitJobPipeline;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

import static com.google.common.io.Resources.getResource;

public class GuiApplication extends Application {
    public static final int TOTAL_LOAD_CLASS = 6261;
    private SystemStartUpProcessorBarController systemStartUpProcessorBarController;

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DAPApplication.initEnv();
        buildProcessorBarDialog();
        DAPApplication.startPlugin(Lists.newArrayList("com.dmsoft.dap.SpcPlugin", "com.dmsoft.dap.GrrPlugin", "com.dmsoft.dap.CsvResolverPlugin"));
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

        WindowFactory.createFullWindow(GuiConst.PLARTFORM_STAGE_MAIN, root, main, getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm());

        initJob();
        NodeMap.addNode(GuiConst.PLARTFORM_NODE_MAIN, main);
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

    private void buildProcessorBarDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/system_processor_bar.fxml");
            root = fxmlLoader.load();
            systemStartUpProcessorBarController = fxmlLoader.getController();
            Effect shadowEffect = new DropShadow(BlurType.TWO_PASS_BOX, new Color(0, 0, 0, 0.2),
                    10, 0, 0, 0);
            root.setEffect(shadowEffect);
            Scene tempScene = new Scene(root);
            tempScene.getStylesheets().add(getResource("css/platform_app.css").toExternalForm());
            tempScene.setFill(Color.TRANSPARENT);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(tempScene);
            stage.setResizable(false);
            stage.show();
            StageMap.addStage(GuiConst.PLARTFORM_STAGE_PROCESS, stage);
            if (stage.isShowing()) {
                updateProcessorBar();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildLoginDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/login.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_LOGIN, GuiFxmlAndLanguageUtils.getString(ResourceMassages.DATASOURCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateProcessorBar() {
        Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        while (true) {
                            Thread.sleep(100);
                            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
                            int process = (int) (classLoadingMXBean.getLoadedClassCount() * 1.0 / TOTAL_LOAD_CLASS * 100);
                            updateProgress(process, 100);
                            if (process >= 100) {
                                Platform.runLater(() -> {
                                    StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).close();
                                    buildLoginDialog();
                                });
                                break;
                            }
                        }
                        return null;
                    }
                };
            }

        };
        systemStartUpProcessorBarController.getProgressBar().progressProperty().bind(service.progressProperty());
        service.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

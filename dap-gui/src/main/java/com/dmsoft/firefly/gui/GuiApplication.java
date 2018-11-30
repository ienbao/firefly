package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.job.BasicJobFactory;
import com.dmsoft.firefly.gui.job.BasicJobManager;
import com.dmsoft.firefly.gui.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.io.Resources.getResource;

/**
 * GUI Application
 */
public class GuiApplication extends Application {

    private Logger logger = LoggerFactory.getLogger(GuiApplication.class);
    public static final int TOTAL_LOAD_CLASS = 4800;

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    private UserService userService;

    /**
     * main method
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SvgImageLoaderFactory.install();
        DapUtils.registDockIcon();

        this.userService = DapApplictionContext.getInstance().getBean(UserService.class);
        DAPApplication.initEnv();
        registEvent();

        BasicJobManager jobManager1 = new BasicJobManager();
        BasicJobFactory jobFactory = new BasicJobFactory();
        RuntimeContext.registerBean(BasicJobFactory.class, jobFactory);
        RuntimeContext.registerBean(JobManager.class, jobManager1);

        buildProcessorBarDialog();
        if (StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).isShowing()) {
            updateProcessorBar();
        }

        MenuFactory.initMenu();

        RuntimeContext.registerBean(IMessageManager.class, new MessageManagerFactory());
        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        if (languageType != null) {
            RuntimeContext.getBean(EnvService.class).setLanguageType(RuntimeContext.getBean(EnvService.class).getLanguageType());
        }



        Pane root = DapUtils.loadFxml("/view/app_menu.fxml");
        Pane main = DapUtils.loadFxml("/view/main.fxml");

        StageMap.setPrimaryStage(GuiConst.PLARTFORM_STAGE_MAIN, WindowFactory.createFullWindow(GuiConst.PLARTFORM_STAGE_MAIN, root, main,
                getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm()));
        NodeMap.addNode(GuiConst.PLARTFORM_NODE_MAIN, main);

        RuntimeContext.getBean(EventContext.class).addEventListener(
            EventType.PLATFORM_TEMPLATE_SHOW,
            event -> { GuiFxmlAndLanguageUtils.buildTemplateDialog(); });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                CloseMongoDBUtil.closeMongoDB();
            }
        });
    }

    private void registEvent() {

        EventContext eventContext = RuntimeContext.getBean(EventContext.class);
        eventContext.addEventListener(EventType.PLATFORM_PROCESS_CLOSE, event -> {
            Platform.runLater(() -> {
                StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).close();
                showMain();
            });
        });
    }


    private void showMain(){
        if (!userService.findLegal()) {
            GuiFxmlAndLanguageUtils.buildLegalDialog();
        } else {
            Stage stage = StageMap.getPrimaryStage(GuiConst.PLARTFORM_STAGE_MAIN);
            stage.show();
            if (stage.getScene().getRoot() instanceof WindowPane) {
                WindowPane windowPane = (WindowPane) stage.getScene().getRoot();
                windowPane.getController().maximizePropertyProperty().set(true);
            }
            GuiFxmlAndLanguageUtils.buildLoginDialog();
        }
    }

    /**
     * method to update process bar
     */
    public void updateProcessorBar() {
        Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        while (true) {
                            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
                            int process = (int) (classLoadingMXBean.getLoadedClassCount() * 1.0 / TOTAL_LOAD_CLASS * 100);
                            logger.info("count: " + classLoadingMXBean.getLoadedClassCount());
                            if (process >= 100) {
                                for (int i = 10; i <= 100; i++) {
                                    Thread.sleep(20);
                                    updateProgress(i, 100);
                                }

                                EventContext eventContext = RuntimeContext.getBean(EventContext.class);
                                eventContext.pushEvent(new PlatformEvent(EventType.PLATFORM_PROCESS_CLOSE, null));

                                break;
                            }
                        }
                        return null;
                    }
                };
            }

        };

//        systemProcessorController.getProgressBar().progressProperty().bind(service.progressProperty());
        service.start();
    }

    private void buildProcessorBarDialog() {
        Pane root = DapUtils.loadFxml("/view/system_processor_bar.fxml");
        Stage stage = new Stage();
        Scene tempScene = new Scene(root);
        tempScene.getStylesheets().addAll(getResource("css/platform_app.css").toExternalForm(),getResource("css/redfall/main.css").toExternalForm());
        stage.setScene(tempScene);
        stage.show();
        StageMap.addStage(GuiConst.PLARTFORM_STAGE_PROCESS, stage);
    }
}

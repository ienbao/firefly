package com.dmsoft.firefly.gui;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.core.utils.ResourceFinder;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.job.BasicJobFactory;
import com.dmsoft.firefly.gui.job.BasicJobManager;
import com.dmsoft.firefly.gui.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
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

import javax.swing.*;
import java.awt.*;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.io.Resources.getResource;

/**
 * GUI Application
 */
public class GuiApplication extends Application {

    public static final int TOTAL_LOAD_CLASS = 5700;

    static {
        System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
    }

    private final String parentPath = ApplicationPathUtil.getPath(GuiConst.CONFIG_PATH);
    private UserService userService;
    private SystemProcessorController systemProcessorController;
    private JsonMapper mapper = JsonMapper.defaultMapper();

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

        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            Class cla = Class.forName("com.apple.eawt.Application");
            Method method1 = cla.getMethod("getApplication");
            Object o = method1.invoke(cla);
            Method method = cla.getMethod("setDockIconImage", Image.class);
            ResourceFinder finder = new ResourceFinder();
            method.invoke(o, new ImageIcon(finder.findResource("images/desktop_mac_logo.png")).getImage());
        } else {
        }
        String json = JsonFileUtil.readJsonFile(parentPath, GuiConst.ACTIVE_PLUGIN);
        List<KeyValueDto> activePlugin = Lists.newArrayList();
        if (DAPStringUtils.isNotBlank(json)) {
            activePlugin = mapper.fromJson(json, mapper.buildCollectionType(List.class, KeyValueDto.class));
        }
        List<String> plugins = Lists.newArrayList();
        if (activePlugin != null) {
            activePlugin.forEach(v -> {
                if ((boolean) v.getValue()) {
                    plugins.add(v.getKey());
                }
            });
        }
//        DAPApplication.run(Lists.newArrayList(plugins));
        DAPApplication.initEnv();
        BasicJobManager jobManager1 = new BasicJobManager();
        BasicJobFactory jobFactory = new BasicJobFactory();
        RuntimeContext.registerBean(JobManager.class, jobManager1);
        RuntimeContext.registerBean(BasicJobFactory.class, jobFactory);
        userService = RuntimeContext.getBean(UserService.class);

        buildProcessorBarDialog();
        if (StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).isShowing()) {
            updateProcessorBar();
        }

        MenuFactory.initMenu();

        DAPApplication.startPlugin(Lists.newArrayList(plugins));
        RuntimeContext.registerBean(IMessageManager.class, new MessageManagerFactory());
        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        if (languageType != null) {
            RuntimeContext.getBean(EnvService.class).setLanguageType(RuntimeContext.getBean(EnvService.class).getLanguageType());
        }

        FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/app_menu.fxml");
        FXMLLoader fxmlLoader1 = GuiFxmlAndLanguageUtils.getLoaderFXML("view/main.fxml");

        Pane root = fxmlLoader.load();
        Pane main = fxmlLoader1.load();
        MenuFactory.setMainController(fxmlLoader1.getController());
        MenuFactory.setAppController(fxmlLoader.getController());

        WindowFactory.createFullWindow(GuiConst.PLARTFORM_STAGE_MAIN, root, main, getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm());

        NodeMap.addNode(GuiConst.PLARTFORM_NODE_MAIN, main);

        RuntimeContext.getBean(EventContext.class).addEventListener(event -> {
            if (event.getMessage().equals("Template_Show")) {
                GuiFxmlAndLanguageUtils.buildTemplateDia();
            }
        });
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
                            Thread.sleep(100);
                            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
                            int process = (int) (classLoadingMXBean.getLoadedClassCount() * 1.0 / TOTAL_LOAD_CLASS * 100);
                            updateProgress(process, 100);
                            if (process >= 100) {
                                Platform.runLater(() -> {
                                    StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).close();
                                    if (!userService.findLegal()) {
                                        GuiFxmlAndLanguageUtils.buildLegalDialog();
                                    } else {
                                        StageMap.showStage(GuiConst.PLARTFORM_STAGE_MAIN);
                                        GuiFxmlAndLanguageUtils.buildLoginDialog();
                                    }
                                });
                                break;
                            }
                        }
                        return null;
                    }
                };
            }

        };

        systemProcessorController.getProgressBar().progressProperty().bind(service.progressProperty());
        service.start();
    }

    private void buildProcessorBarDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/system_processor_bar.fxml");
            root = fxmlLoader.load();
            systemProcessorController = fxmlLoader.getController();
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
            stage.toFront();
            stage.show();
            StageMap.addStage(GuiConst.PLARTFORM_STAGE_PROCESS, stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

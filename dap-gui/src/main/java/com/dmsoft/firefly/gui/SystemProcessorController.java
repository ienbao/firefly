package com.dmsoft.firefly.gui;

import static com.google.common.io.Resources.getResource;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.utils.DapUtils;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;


/**
 * Created by QiangChen on 2017/4/8.
 */
@Component
public class SystemProcessorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemProcessorController.class);
    public static final int TOTAL_LOAD_CLASS = 4800;
    @FXML
    private ProgressBar progressBar;

    @Autowired
    private EventContext eventContext;

    @FXML
    private void initialize() {
        LOGGER.debug("The processor bar is start.");
        progressBar.setProgress(0);

        eventContext.addEventListener(EventType.UPDATA_PROGRESS, event -> {
            Platform.runLater(() -> {
                updateProcessorBar();
            });
        });
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }


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
                            LOGGER.info("count: " + classLoadingMXBean.getLoadedClassCount());
                            if (process >= 100) {
                                for (int i = 10; i <= 100; i++) {
                                    Thread.sleep(20);
                                    updateProgress(i, 100);
                                }
                                eventContext.pushEvent(new PlatformEvent(EventType.PLATFORM_PROCESS_CLOSE, null));
                                break;
                            }
                        }
                        return null;
                    }
                };
            }

        };

        getProgressBar().progressProperty().bind(service.progressProperty());
        service.start();
    }
}

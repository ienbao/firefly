package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.SystemStartUpProcessorBarController;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Peter on 2017/3/27.
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    private SystemStartUpProcessorBarController systemStartUpProcessorBarController;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("Initialized system.");
        if (systemStartUpProcessorBarController.getProgrossBar().getProgress() == 100) {
            StageMap.closeStage("platform_gui_load");
        }
        //contextRefreshedEvent.getApplicationContext().getBean(MainController.class).init();
    }

    private void buildChangePasswordDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/change_password.fxml");
            root = fxmlLoader.load();
            systemStartUpProcessorBarController = fxmlLoader.getController();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("platform_gui_load", GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

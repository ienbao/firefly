package com.dmsoft.firefly.gui;

import static com.google.common.io.Resources.getResource;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.utils.DapUtils;
import com.dmsoft.firefly.gui.utils.GuiConst;
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
import org.springframework.stereotype.Component;


/**
 * Created by QiangChen on 2017/4/8.
 */
@Component
public class SystemProcessorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemProcessorController.class);
    @FXML
    private ProgressBar progressBar;

    @FXML
    private void initialize() {
        LOGGER.debug("The processor bar is start.");

        final Double d02 = 0.2d;
        try {

//            Effect shadowEffect = new DropShadow(BlurType.TWO_PASS_BOX, new Color(0, 0, 0, d02),
//                10, 0, 0, 0);
//            setEffect(shadowEffect);
//            Scene tempScene = new Scene(root);
//            tempScene.getStylesheets().addAll(getResource("css/platform_app.css").toExternalForm(),getResource("css/redfall/main.css").toExternalForm());
//            tempScene.setFill(Color.TRANSPARENT);
//            Stage stage = new Stage();
//            javafx.scene.image.Image image = new javafx.scene.image.Image("/images/desktop_mac_logo.png");
//            stage.getIcons().addAll(image);
//            stage.initStyle(StageStyle.TRANSPARENT);
//            stage.setScene(tempScene);
//            stage.setResizable(false);
//            stage.toFront();
//            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        progressBar.setProgress(0);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}

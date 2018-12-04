package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Zeng Liyun on 2018/11/30.
 */
public class StageSwitchDialog {

    public static void buildProcessorBarDialog() {
        Pane root = DapUtils.loadFxml("view/system_processor_bar.fxml");
        Stage stage = new Stage();
        Scene tempScene = new Scene(root);
        tempScene.setFill(Color.TRANSPARENT);//设置阴影
        tempScene.getStylesheets().addAll(getResource("css/platform_app.css").toExternalForm(),getResource("css/redfall/main.css").toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(tempScene);
        stage.show();
        StageMap.addStage(GuiConst.PLARTFORM_STAGE_PROCESS, stage);
    }
}

package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ResourceMassages;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Julia on 2018/03/09.
 */
public class WindowMessageFactory {

    public static WindowMessageController createWindowMessageNoBtn(String title, String msg) {
        return buildWindowMessageDialogNoBtn(title, msg);
    }

    public static WindowMessageController createWindowMessage(String title, String msg) {
        return buildWindowMessageDialog(title, msg);
    }

    public static WindowProgressTipController createWindowProgressTip(String title) {
        return buildWindowProgressTipDialog(title);
    }

    public static WindowProgressTipController createWindowProgressTip() {
        return buildWindowProgressTipDialog(FxmlAndLanguageUtils.getString("GLOBAL_RUNNING_TASK"));
    }


    private static WindowMessageController buildWindowMessageDialogNoBtn(String title, String msg) {
        Pane root = null;
        WindowMessageController windowMessageController = new WindowMessageController();
        try {
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/window_message.fxml");
            fxmlLoader.setController(windowMessageController);
            root = fxmlLoader.load();
            windowMessageController.updateSmLbl(msg);
            Stage newStage = WindowFactory.createOrUpdateWindowAsModelNoBtn(ResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE, title, root);
            newStage.setResizable(false);
            newStage.show();
            newStage.setOnCloseRequest(event -> {
                windowMessageController.closeDialog();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windowMessageController;

    }

    private static WindowMessageController buildWindowMessageDialog(String title, String msg) {
        Pane root = null;
        WindowMessageController windowMessageController = new WindowMessageController();
        try {
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/window_message.fxml");
            fxmlLoader.setController(windowMessageController);
            root = fxmlLoader.load();
            windowMessageController.updateSmLbl(msg);
            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE, title, root);
            newStage.setResizable(false);
            newStage.show();
            newStage.setOnCloseRequest(event -> {
                windowMessageController.closeDialog();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windowMessageController;

    }

    private static WindowProgressTipController buildWindowProgressTipDialog(String title) {
        Pane root = null;
        WindowProgressTipController windowProgressTipController = new WindowProgressTipController();
        try {
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/window_progress_tip.fxml");
            fxmlLoader.setController(windowProgressTipController);
            root = fxmlLoader.load();
            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP, title, root);
            newStage.setResizable(false);
            newStage.show();
            newStage.setOnCloseRequest(event -> {
                windowProgressTipController.closeDialog();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windowProgressTipController;

    }
}
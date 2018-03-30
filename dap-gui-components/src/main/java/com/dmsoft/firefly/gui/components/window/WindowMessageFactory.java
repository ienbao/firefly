package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ResourceMassages;
import javafx.application.Platform;
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

    public static WindowMessageController createWindowMessageNoBtnHasOkAndCancel(String title, String msg) {
        WindowMessageController windowMessageController = buildWindowMessageDialogNoBtn(title, msg);
        windowMessageController.showOKAndCancel();
        return windowMessageController;
    }

    public static WindowMessageController createWindowMessageNoBtnHasCancel(String title, String msg) {
        WindowMessageController windowMessageController = buildWindowMessageDialogNoBtn(title, msg);
        windowMessageController.showCancel();
        return windowMessageController;
    }

    public static WindowMessageController createWindowMessageNoBtnHasOk(String title, String msg) {
        WindowMessageController windowMessageController = buildWindowMessageDialogNoBtn(title, msg);
        windowMessageController.showOk();
        return windowMessageController;
    }

    public static WindowMessageController createWindowMessage(String title, String msg) {
        return buildWindowMessageDialog(title, msg);
    }


    public static WindowMessageController createWindowMessageHasOkAndCancel(String title, String msg) {
        WindowMessageController windowMessageController = buildWindowMessageDialog(title, msg);
        windowMessageController.showOKAndCancel();
        return windowMessageController;
    }

    public static WindowMessageController createWindowMessageHasCancel(String title, String msg) {
        WindowMessageController windowMessageController = buildWindowMessageDialog(title, msg);
        windowMessageController.showCancel();
        return windowMessageController;
    }

    public static WindowMessageController createWindowMessageHasOk(String title, String msg) {
        WindowMessageController windowMessageController = buildWindowMessageDialog(title, msg);
        windowMessageController.showOk();
        return windowMessageController;
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
            windowMessageController.updateMsgLbl(msg);
            Stage newStage = WindowFactory.createOrUpdateWindowAsModelNoBtn(ResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE, title, root);
            newStage.setResizable(false);
            newStage.toFront();
            newStage.show();
            newStage.setOnShowing(event -> {
                windowMessageController.onShowingRequest();
            });
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
            windowMessageController.updateMsgLbl(msg);
            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE, title, root);
            newStage.setResizable(false);
            newStage.setOnShowing(event -> {
                windowMessageController.onShowingRequest();
            });
            newStage.toFront();
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
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/windows_progress.fxml");
            fxmlLoader.setController(windowProgressTipController);
            root = fxmlLoader.load();
            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP, title, root);
            newStage.setResizable(false);
            newStage.setOnShowing(event -> {
                windowProgressTipController.onShowingRequest();
            });
            newStage.setHeight(140);
            newStage.setWidth(430);
            newStage.toFront();
            newStage.show();

            newStage.setOnCloseRequest(event -> {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        newStage.setMinHeight(150);
                        newStage.setMaxHeight(150);
                    }
                });
                windowProgressTipController.closeDialog();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windowProgressTipController;

    }
}

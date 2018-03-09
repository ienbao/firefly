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
public class SimpleWindowFactory {

    public static SimpleMessageController createSimpleMessageNoBtn(String title, String msg) {
        return buildSimpleTemplateDialogNoBtn(title, msg);
    }

    public static SimpleMessageController createSimpleMessage(String title, String msg) {
        return buildSimpleTemplateDialog(title, msg);
    }


    private static SimpleMessageController buildSimpleTemplateDialogNoBtn(String title, String msg) {
        Pane root = null;
        SimpleMessageController simpleMessageController = new SimpleMessageController();
        try {
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/simple_message_template.fxml");
            fxmlLoader.setController(simpleMessageController);
            root = fxmlLoader.load();
            simpleMessageController.updateSmLbl(msg);
            Stage newStage = WindowFactory.createOrUpdateWindowAsModelNoBtn(ResourceMassages.COMPONENT_STAGE_SIMPLE_MESSAGE_TEMPLATE, title, root);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return simpleMessageController;

    }

    private static SimpleMessageController buildSimpleTemplateDialog(String title, String msg) {
        Pane root = null;
        SimpleMessageController simpleMessageController = new SimpleMessageController();
        try {
            FXMLLoader fxmlLoader = FxmlAndLanguageUtils.getLoaderFXML("view/simple_message_template.fxml");
            fxmlLoader.setController(simpleMessageController);
            root = fxmlLoader.load();
            simpleMessageController.updateSmLbl(msg);
            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(ResourceMassages.COMPONENT_STAGE_SIMPLE_MESSAGE_TEMPLATE, title, root);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return simpleMessageController;

    }
}

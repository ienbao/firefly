package com.dmsoft.firefly.gui.components.service.impl;

import com.dmsoft.firefly.core.sdkimpl.dai.ModuleType;
import com.dmsoft.firefly.core.sdkimpl.dai.SpringFxmlLoader;
import com.dmsoft.firefly.gui.components.service.GuiComponentFxmlLoadService;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GuiComponentFxmlLoadServiceImpl implements GuiComponentFxmlLoadService {

  @Autowired
  private ApplicationContext context;
  @Autowired
  private LanguageService languageService;


  @Override
  public <T extends Node> T loadFxml(String fxmlFile) {
    return new SpringFxmlLoader().load(this.languageService.getBundle(ModuleType.COM), context, this.getClass().getClassLoader(), fxmlFile);
  }

    @Override
    public WindowMessageController loadWindowMessage(String title, String msg) {
        WindowMessageController windowMessageController = null;
        try {
            FXMLLoader loader = this.fxmlLoadService.getFxmlLoader("view/window_message.fxml");
            Pane root = loader.load();
            windowMessageController = loader.getController();
            windowMessageController.updateMsgLbl(msg);
            windowMessageController.showOKAndCancel();
            Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(CommonResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE, title, root);
            newStage.setResizable(false);
            WindowMessageController finalWindowMessageController = windowMessageController;
            newStage.setOnShowing(event -> {
                finalWindowMessageController.onShowingRequest();
            });
            newStage.toFront();
            newStage.show();
            WindowMessageController finalWindowMessageController1 = windowMessageController;
            newStage.setOnCloseRequest(event -> {
                finalWindowMessageController1.closeDialog();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return windowMessageController;
//        root.fireEvent(new WindowMessageEvent(new EventType<>("UPDATE_MESSAGE")));
//
//        windowMessageController.updateMsgLbl(msg);
//        Stage newStage = WindowFactory.createOrUpdateSimpleWindowAsModel(CommonResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE, title, root);
//        newStage.setResizable(false);
//        newStage.setOnShowing(event -> {
////            root.fireEvent(new WindowMessageEvent(new EventType<>("SHOW_REQUEST")));
////            windowMessageController.onShowingRequest();
//        });
//        newStage.toFront();
//        newStage.show();
//        newStage.setOnCloseRequest(event -> {
////            root.fireEvent(new WindowMessageEvent(new EventType<>("CLOSE_DIALOG")));
////            windowMessageController.closeDialog();
//        });
//        return root;
    }
}

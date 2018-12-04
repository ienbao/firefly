package com.dmsoft.firefly.gui.components.service;

import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import javafx.scene.Node;

/**
 * fxml加载处理类
 *
 */
public interface GuiComponentFxmlLoadService {

    <T extends Node> T loadFxml(String fxmlFile);

    WindowMessageController loadWindowMessage(String title, String msg);

}

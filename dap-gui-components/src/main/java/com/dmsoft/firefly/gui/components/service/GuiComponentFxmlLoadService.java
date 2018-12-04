package com.dmsoft.firefly.gui.components.service;

import javafx.scene.Node;

/**
 * fxml加载处理类
 *
 */
public interface GuiComponentFxmlLoadService {

  <T extends Node> T loadFxml(String fxmlFile);

}

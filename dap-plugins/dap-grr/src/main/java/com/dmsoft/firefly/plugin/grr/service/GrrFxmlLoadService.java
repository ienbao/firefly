package com.dmsoft.firefly.plugin.grr.service;

import javafx.scene.Node;

/**
 * fxml加载处理类
 *
 */
public interface GrrFxmlLoadService {

  <T extends Node> T loadFxml(String fxmlFile);


}

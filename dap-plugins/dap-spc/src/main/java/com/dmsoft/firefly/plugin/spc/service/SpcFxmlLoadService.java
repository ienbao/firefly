package com.dmsoft.firefly.plugin.spc.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * fxml加载处理类
 *
 */
public interface SpcFxmlLoadService {

  <T extends Node> T loadFxml(String fxmlFile);

  FXMLLoader getFxmlLoader(String fxmlFile);
}

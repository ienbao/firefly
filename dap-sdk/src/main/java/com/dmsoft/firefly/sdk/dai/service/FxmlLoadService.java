package com.dmsoft.firefly.sdk.dai.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * 加载fxml服务
 *
 * @author yuanwen
 *
 */
public interface FxmlLoadService {


    <T extends Node> T loadFxml(String fxmlFile);

    FXMLLoader getFxmlLoader(String fxmlFile);
}

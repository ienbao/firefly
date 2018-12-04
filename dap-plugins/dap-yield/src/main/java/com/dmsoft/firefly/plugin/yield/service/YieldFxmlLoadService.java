package com.dmsoft.firefly.plugin.yield.service;

import javafx.scene.Node;

public interface YieldFxmlLoadService {
    <T extends Node> T loadFxml(String fxmlFile);
}

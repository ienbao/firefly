package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.core.sdkimpl.dai.SpringFxmlLoader;
import com.dmsoft.firefly.sdk.dai.service.FxmlLoadService;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpcFxmlLoadServiceImpl implements FxmlLoadService {

  @Autowired
  private ApplicationContext context;
  @Autowired
  private LanguageService languageService;

  @Override
  public <T extends Node> T loadFxml(String fxmlFile) {
    return new SpringFxmlLoader().load(languageService, context, fxmlFile);
  }
}

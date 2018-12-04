package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.core.sdkimpl.dai.ModuleType;
import com.dmsoft.firefly.core.sdkimpl.dai.SpringFxmlLoader;
import com.dmsoft.firefly.plugin.spc.service.SpcFxmlLoadService;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpcFxmlLoadServiceImpl implements SpcFxmlLoadService {

  @Autowired
  private ApplicationContext context;
  @Autowired
  private LanguageService languageService;

  @Override
  public <T extends Node> T loadFxml(String fxmlFile) {
    return new SpringFxmlLoader().load(this.languageService.getBundle(ModuleType.SPC), context, this.getClass().getClassLoader(), fxmlFile);
  }

  @Override
  public FXMLLoader getFxmlLoader(String fxmlFile) {
    return new SpringFxmlLoader(context,languageService,fxmlFile);
  }

}

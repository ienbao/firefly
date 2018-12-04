package com.dmsoft.firefly.gui.service;


import com.dmsoft.firefly.core.sdkimpl.dai.SpringFxmlLoader;
import com.dmsoft.firefly.sdk.dai.service.FxmlLoadService;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 加载fxml文件
 *
 * @author yuanwen
 */
@Component
public class GuiFxmlLoadServiceImpl implements FxmlLoadService {

  @Autowired
  private ApplicationContext context;
  @Autowired
  private LanguageService languageService;

  @Override
  public <T extends Node> T loadFxml(String fxmlFile) {
    return new SpringFxmlLoader().load(languageService.getResourceBundle(), context, this.getClass().getClassLoader(), fxmlFile);
  }

  @Override
  public FXMLLoader getFxmlLoader(String fxmlFile) {
    return new SpringFxmlLoader(context,languageService,fxmlFile);
  }
}

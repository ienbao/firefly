package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 获取当前环境的语言设置
 *
 */
@Service
public class LanguageServiceImpl implements LanguageService {

  @Autowired
  private EnvService envService;

  /**
   * 获取当前多语言设置
   */
  @Override
  public ResourceBundle getResourceBundle() {
    LanguageType languageType = this.envService.getLanguageType();
    if (languageType == null) {
      languageType = LanguageType.EN;
    }
    String bundleKey = "i18n.message_en_US_";
    if (LanguageType.ZH.equals(languageType)) {
      bundleKey = "i18n.message_zh_CN_";
    }
    bundleKey = bundleKey + ModuleType.GUI.name();
    return ResourceBundle.getBundle(bundleKey);
  }
}

package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 获取当前环境的语言设置
 *
 */
@Service
public class LanguageServiceImpl implements LanguageService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LanguageServiceImpl.class);

  private  boolean IS_DEBUG = false;

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
    bundleKey = bundleKey + com.dmsoft.firefly.core.sdkimpl.dai.ModuleType.GUI.name();
    return ResourceBundle.getBundle(bundleKey);
  }


  @Override
  public ResourceBundle getBundle(com.dmsoft.firefly.core.sdkimpl.dai.ModuleType moduleKey) {
    LanguageType languageType = null;
    if (IS_DEBUG == false) {
      languageType = this.envService.getLanguageType();
    }
    if (languageType == null) {
      languageType = LanguageType.EN;
    }
    String bundleKey = "i18n.message_en_US_";
    if (languageType.equals(LanguageType.ZH)) {
      bundleKey = "i18n.message_zh_CN_";
    }
    if (StringUtils.isNotBlank(moduleKey.name())) {
      bundleKey = bundleKey + moduleKey.name();
    } else {
      LOGGER.error("The module key is null.");
      return null;
    }
    return ResourceBundle.getBundle(bundleKey);
  }



}

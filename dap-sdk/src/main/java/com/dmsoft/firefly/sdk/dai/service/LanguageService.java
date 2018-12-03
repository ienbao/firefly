package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.core.sdkimpl.dai.ModuleType;
import java.util.ResourceBundle;

/**
 * 多语言处理类
 *
 * @author yuanwen
 */
public interface LanguageService {

  /**
   * 获取当前多语言设置
   *
   * @return
   */
  ResourceBundle getResourceBundle();

  /**
   * 获取当前多语言设置
   *
   * @param moduleKey
   * @return
   */
  ResourceBundle getBundle(ModuleType moduleKey);
}

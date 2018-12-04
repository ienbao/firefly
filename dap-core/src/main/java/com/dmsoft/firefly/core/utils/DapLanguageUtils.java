package com.dmsoft.firefly.core.utils;

import com.dmsoft.firefly.sdk.utils.enums.LanguageType;

/**
 * 管理程序全局多语言
 *
 * @author yuanwen
 */
public class DapLanguageUtils {

  private static LanguageType languageType;

  public static LanguageType getLanguageType() {
    return languageType;
  }

  public static void setLanguageType(LanguageType languageType) {
    DapLanguageUtils.languageType = languageType;
  }
}

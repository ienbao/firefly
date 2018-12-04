package com.dmsoft.firefly.core.utils;

import org.springframework.context.ApplicationContext;

/**
 * 存储当前Spring上下文
 *
 * @author yuanwen
 */
public class DapApplicationContextUtils {

  private static ApplicationContext context;

  public static ApplicationContext getContext() {
    return context;
  }

  public static void setContext(ApplicationContext context) {
    DapApplicationContextUtils.context = context;
  }
}

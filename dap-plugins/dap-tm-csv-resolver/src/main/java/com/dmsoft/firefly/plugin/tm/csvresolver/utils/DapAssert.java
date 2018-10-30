package com.dmsoft.firefly.plugin.tm.csvresolver.utils;

public class DapAssert {


  /**
   *
   * 检测对象是否为空，
   *
   * @param obj
   */
  public static void isNotNull(Object obj, String errMsg){
    if(obj == null){
        throw new RuntimeException(errMsg);
    }
  }

}

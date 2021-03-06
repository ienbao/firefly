package com.dmsoft.firefly.gui.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * DAP spring加载全局配置文件。
 * 通过两种方式引用，直接方法调用和动态注入
 *
 * @author yuanwen
 */
public class DapApplictionContext {
  private ApplicationContext context;
  private static DapApplictionContext instance;

  private DapApplictionContext() {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/spring-config-gui.xml");

    this.context = applicationContext;
  }

  public ApplicationContext getContext() {
    return context;
  }

  public static synchronized DapApplictionContext getInstance(){
    if(instance == null){
      instance = new DapApplictionContext();
    }

    return instance;
  }

  /**
   * 通过类型来获取Bean
   *
   * @param tClass
   * @return
   */
  public <T> T getBean(Class<T> tClass){
    return this.context.getBean(tClass);
  }


  /**
   * 通过名称来获取Bean
   *
   * @param beanName
   * @return
   */
  public Object getBean(String beanName){

    return this.context.getBean(beanName);
  }


}

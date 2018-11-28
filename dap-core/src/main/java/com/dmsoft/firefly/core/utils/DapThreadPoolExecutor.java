package com.dmsoft.firefly.core.utils;

import ch.qos.logback.core.util.TimeUtil;
import java.sql.Time;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 添加全局线程池管理，免得在界面再创建线程
 *
 * @author yuanwen
 *
 */
public class DapThreadPoolExecutor {

  private static ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 20, 2, TimeUnit.MINUTES,
      new LinkedBlockingDeque<Runnable>());


  /**
   * 立即持行线程方法
   *
   * @param command
   */
  public static void execute(Runnable command){
    executor.execute(command);
  }

}

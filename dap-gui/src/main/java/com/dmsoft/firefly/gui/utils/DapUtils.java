package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.core.utils.ResourceFinder;
import com.dmsoft.firefly.gui.controller.SpringFxmlLoader;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.scene.Node;
import javax.swing.ImageIcon;

/**
 * Dap 公用方法集
 *
 */
public class DapUtils {

  /**
   * 注册MAC系统下面图标
   *
   */
  public static void registDockIcon()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String os = System.getProperty("os.name");
    if (!os.toLowerCase().startsWith("win")) {
      Class cla = Class.forName("com.apple.eawt.Application");
      Method method1 = cla.getMethod("getApplication");
      Object o = method1.invoke(cla);
      Method method = cla.getMethod("setDockIconImage", Image.class);
      ResourceFinder finder = new ResourceFinder();
      method.invoke(o, new ImageIcon(finder.findResource("images/desktop_mac_logo.png")).getImage());
    }else{

    }
  }

  /**
   * 加载fxml文件
   *
   * @param fxmlFile
   * @return
   */
  public static <T extends Node> T loadFxml(String fxmlFile){

    return new SpringFxmlLoader().load(fxmlFile);
  }
}

package com.dmsoft.firefly.gui.utils;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.sdkimpl.dai.SpringFxmlLoader;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.core.utils.ResourceFinder;
import com.dmsoft.firefly.gui.service.GuiFxmlLoadServiceImpl;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javafx.scene.Node;
import javax.swing.ImageIcon;
import org.springframework.context.ApplicationContext;

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

    return DapApplictionContext.getInstance().getBean(GuiFxmlLoadServiceImpl.class).loadFxml(fxmlFile);
  }

  /**
   * 获取当前可用plugin 列表
   *
   * @return
   */
  public static List<String> loadActivePluginList(){
    String parentPath = ApplicationPathUtil.getPath(GuiConst.CONFIG_PATH);
    JsonMapper mapper = JsonMapper.defaultMapper();
    String json = JsonFileUtil.readJsonFile(parentPath, GuiConst.ACTIVE_PLUGIN);/* "activePlugin" 路径：gui-resources-config */
    java.util.List<KeyValueDto> activePlugin = Lists.newArrayList();
    if (DAPStringUtils.isNotBlank(json)) {
      activePlugin = mapper.fromJson(json, mapper.buildCollectionType(java.util.List.class, KeyValueDto.class));/* 文件中读取到的信息 */
    }
    List<String> plugins = Lists.newArrayList(); /* "com.dmsoft.dap.SpcPlugin" */
    if (activePlugin != null) {
      activePlugin.forEach(v -> {
        if ((boolean) v.getValue()) {
          plugins.add(v.getKey());
        }
      });
    }

    return plugins;
  }
}

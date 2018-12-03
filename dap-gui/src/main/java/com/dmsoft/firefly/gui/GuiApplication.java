package com.dmsoft.firefly.gui;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.DAPApplication;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.core.utils.PluginScanner;
import com.dmsoft.firefly.core.utils.PluginXMLParser;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.utils.*;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI Application
 *
 */
public class GuiApplication extends Application {
  private Logger logger = LoggerFactory.getLogger(GuiApplication.class);

  static {
    System.getProperties().put("javafx.pseudoClassOverrideEnabled", "true");
  }

  private UserService userService;
  private EventContext eventContext;
  private EnvService envService;
  private PluginContext pluginContext;


  /**
   * main method
   *
   * @param args arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    SvgImageLoaderFactory.install();
    DapUtils.registDockIcon();

    DapApplictionContext applictionContext = DapApplictionContext.getInstance();
    this.userService = applictionContext.getBean(UserService.class);
    this.eventContext = applictionContext.getBean(EventContext.class);
    this.envService = applictionContext.getBean(EnvService.class);
    this.pluginContext = applictionContext.getBean(PluginContext.class);

    String parentPath = ApplicationPathUtil.getPath(GuiConst.CONFIG_PATH);
    JsonMapper mapper = JsonMapper.defaultMapper();
    String json = JsonFileUtil.readJsonFile(parentPath, GuiConst.ACTIVE_PLUGIN);/* "activePlugin" 路径：gui-resources-config */
    List<KeyValueDto> activePlugin = Lists.newArrayList();
    if (DAPStringUtils.isNotBlank(json)) {
      activePlugin = mapper.fromJson(json, mapper.buildCollectionType(List.class, KeyValueDto.class));/* 文件中读取到的信息 */
    }
    List<String> plugins = Lists.newArrayList(); /* "com.dmsoft.dap.SpcPlugin" */
    if (activePlugin != null) {
      activePlugin.forEach(v -> {
        if ((boolean) v.getValue()) {
          plugins.add(v.getKey());
        }
      });
    }

    this.loadingPlugin(plugins);

    DAPApplication.initEnv();
    registEvent();

    StageSwitchDialog.buildProcessorBarDialog();

    if (StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).isShowing()) {
      this.eventContext.pushEvent(new PlatformEvent(EventType.UPDATA_PROGRESS, null));
    }

    MenuFactory.initMenu();

    LanguageType languageType = this.envService.getLanguageType();
    if (languageType != null) {
      this.envService.setLanguageType(languageType);
    }

    Pane root = DapUtils.loadFxml("/view/app_menu.fxml");
    Pane main = DapUtils.loadFxml("/view/main.fxml");

    StageMap.setPrimaryStage(GuiConst.PLARTFORM_STAGE_MAIN, WindowFactory.createFullWindow(GuiConst.PLARTFORM_STAGE_MAIN, root, main,
        getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm()));
    NodeMap.addNode(GuiConst.PLARTFORM_NODE_MAIN, main);


    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        CloseMongoDBUtil.closeMongoDB();
      }
    });
  }

  private void registEvent() {
    eventContext.addEventListener(EventType.PLATFORM_PROCESS_CLOSE, event -> {
      Platform.runLater(() -> {
        StageMap.getStage(GuiConst.PLARTFORM_STAGE_PROCESS).close();
        showMain();
      });
    });

    eventContext.addEventListener(
        EventType.PLATFORM_TEMPLATE_SHOW,
        event -> { GuiFxmlAndLanguageUtils.buildTemplateDialog(); });
  }


  private void loadingPlugin(List<String> activePluginList){
    List<String> urlList = new ArrayList<>();
    urlList.add(this.getClass().getClassLoader().getResource("plugins/am-plugin.xml").getFile());
    urlList.add(this.getClass().getClassLoader().getResource("plugins/grr-plugin.xml").getFile());
    urlList.add(this.getClass().getClassLoader().getResource("plugins/spc-plugin.xml").getFile());
    urlList.add(this.getClass().getClassLoader().getResource("plugins/tm-plugin.xml").getFile());
    urlList.add(this.getClass().getClassLoader().getResource("plugins/yeild-plugin.xml").getFile());

    List<PluginInfo> scannedPlugins = PluginXMLParser.parseXML(urlList);
    this.pluginContext.installPlugin(scannedPlugins);
    this.pluginContext.enablePlugin(activePluginList);
    this.pluginContext.startPlugin(activePluginList);
  }


  private void showMain(){
    if (!userService.findLegal()) {
      GuiFxmlAndLanguageUtils.buildLegalDialog();
    } else {
      Stage stage = StageMap.getPrimaryStage(GuiConst.PLARTFORM_STAGE_MAIN);
      stage.show();
      if (stage.getScene().getRoot() instanceof WindowPane) {
        WindowPane windowPane = (WindowPane) stage.getScene().getRoot();
        windowPane.getController().maximizePropertyProperty().set(true);
      }
      GuiFxmlAndLanguageUtils.buildLoginDialog();
    }
  }

}

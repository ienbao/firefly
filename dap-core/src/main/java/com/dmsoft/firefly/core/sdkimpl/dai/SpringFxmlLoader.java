package com.dmsoft.firefly.core.sdkimpl.dai;


import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;


/**
 * 重写fxml加载机制
 *
 * 引用相关地址：https://blog.csdn.net/weixin_40080972/article/details/80869853
 *
 * @author yuanwen
 *
 */
public class SpringFxmlLoader extends FXMLLoader {

  public SpringFxmlLoader(){
    super();
  }

  public  <T extends Node> T  load(ResourceBundle resourceBundle, ApplicationContext context, ClassLoader classLoader, String url) {
    try{
      InputStream fxmlStream = classLoader.getResourceAsStream(url);
      super.setResources(resourceBundle);
      super.setControllerFactory(new Callback<Class<?>, Object>() {
        @Override
        public Object call(Class<?> clazz) {
            return context.getBean(clazz);
        }});
          return this.load(fxmlStream);
        } catch (IOException ioException) {
          throw new RuntimeException(ioException);
        }
  }
}

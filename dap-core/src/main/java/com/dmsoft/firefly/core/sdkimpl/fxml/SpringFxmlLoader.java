package com.dmsoft.firefly.core.sdkimpl.fxml;


import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.springframework.context.ApplicationContext;

import java.net.URL;


/**
 * 重写fxml加载机制
 *
 * 引用相关地址：https://blog.csdn.net/weixin_40080972/article/details/80869853
 *
 * @author yuanwen
 *
 */
public class SpringFxmlLoader extends FXMLLoader {

    public SpringFxmlLoader(ApplicationContext context, LanguageService languageService, String url) {
        super();
        URL fileUrl = getClassLoader().getResource(url);
        super.setLocation(fileUrl);
        super.setResources(languageService.getResourceBundle());
        super.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                return context.getBean(clazz);
            }});
    }

}

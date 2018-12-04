package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.core.sdkimpl.fxml.SpringFxmlLoader;
import com.dmsoft.firefly.sdk.dai.service.FxmlLoadService;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * gui-core加载页面业务
 * author:Tod
 */
@Service
public class FxmlLoadServiceImpl implements FxmlLoadService {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private LanguageService languageService;

    @Override
    public SpringFxmlLoader loadFxml(String fxmlFile) {
        return new SpringFxmlLoader(context, languageService, fxmlFile);
    }
}

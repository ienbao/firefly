package com.dmsoft.firefly.gui.components.service;

import com.dmsoft.firefly.gui.components.window.WindowMessageController;
/**
 * gui-component加载页面业务接口
 * author:Tod
 */
public interface GuiComponentFxmlLoadService {

    WindowMessageController loadWindowMessage(String title,String msg);

}

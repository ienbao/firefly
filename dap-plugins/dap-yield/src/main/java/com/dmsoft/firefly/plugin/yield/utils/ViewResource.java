package com.dmsoft.firefly.plugin.yield.utils;

public class ViewResource {
    public static final String Yield_CSS_PATH = ViewResource.class.getClassLoader().getResource("css/spc_app.css").toExternalForm();

    public static final String PRE_VIEW = "view/";
    public static final String Yield_VIEW_RES = PRE_VIEW + "yield.fxml";
}

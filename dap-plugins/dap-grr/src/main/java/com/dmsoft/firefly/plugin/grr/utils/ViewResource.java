package com.dmsoft.firefly.plugin.grr.utils;


public final class ViewResource {
    public static final String GRR_CSS_PATH = ViewResource.class.getClassLoader().getResource("css/grr_app.css").toExternalForm();

    public static final String PRE_VIEW = "view/";

    public static final String GRR_VIEW_DATA = PRE_VIEW + "grr_view_data.fxml";
}


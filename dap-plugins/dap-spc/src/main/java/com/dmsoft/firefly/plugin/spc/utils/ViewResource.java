package com.dmsoft.firefly.plugin.spc.utils;


public final class ViewResource {
    public static final String SPC_CSS_PATH = ViewResource.class.getClassLoader().getResource("css/spc_app.css").toExternalForm();

    public static final String PRE_VIEW = "view/";

    public static final String SPC_VIEW_ID = "spc";
    public static final String SPC_VIEW_RES = PRE_VIEW + "spc.fxml";

    public static final String SPC_ITEM_VIEW_ID = "spc_item";
    public static final String SPC_ITEM_VIEW_RES = PRE_VIEW + "spc_item.fxml";

    public static final String SPC_STATISTICAL_VIEW_ID = "spc_statistical";
    public static final String SPC_STATISTICAL_VIEW_RES = PRE_VIEW + "statistical_result.fxml";

    public static final String SPC_VIEW_DATA_VIEW_ID = "spc_view_data";
    public static final String SPC_VIEW_DATA_VIEW_RES = PRE_VIEW + "view_data.fxml";

    public static final String SPC_CHART_VIEW_ID = "spc_chart";
    public static final String SPC_CHART_VIEW_RES = PRE_VIEW + "chart_result.fxml";


    public static final String SPC_CHOOSE_STATISTICAL_VIEW_ID = "spc_choose_statistical";
    public static final String SPC_CHOOSE_STATISTICAL_VIEW_RES = PRE_VIEW + "choose_dialog.fxml";


    public static final String SPC_QUICK_SEARCH_VIEW_ID = "spc_quick_search";
    public static final String SPC_QUICK_SEARCH_VIEW_RES = PRE_VIEW + "quick_search.fxml";

    public static final String SPC_ADVANCE_SEARCH_VIEW_ID = "advance";
    public static final String SPC_ADVANCE_SEARCH_VIEW_RES = PRE_VIEW + "advance.fxml";

    public static final String SPC_BASIC_SEARCH_VIEW_RES = PRE_VIEW + "basic_search.fxml";
    public static final String SPC_VIEW_DATA_DETAIL = PRE_VIEW + "spc_detail.fxml";
}


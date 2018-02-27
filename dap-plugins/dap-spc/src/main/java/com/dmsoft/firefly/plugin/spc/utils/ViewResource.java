package com.dmsoft.firefly.plugin.spc.utils;


public final class ViewResource {
    public static final String SPC_CSS_PATH = ViewResource.class.getClassLoader().getResource("css/spc_app.css").toExternalForm();

    public final static String PRE_VIEW = "view/";

    public final static String  SPC_VIEW_ID = "spc";
    public final static String  SPC_VIEW_RES = PRE_VIEW + "spc.fxml";

    public final static String _SPC_ITEM_VIEW_ID = "spc_item";
    public final static String  SPC_ITEM__VIEW_RES = PRE_VIEW + "spc_item.fxml";

    public final static String  SPC_STATISTICAL_VIEW_ID = "spc_statistical";
    public final static String  SPC_STATISTICAL_VIEW_RES = PRE_VIEW + "statistical_result.fxml";

    public final static String  SPC_VIEW_DATA_VIEW_ID = "spc_view_data";
    public final static String  SPC_VIEW_DATA_VIEW_RES = PRE_VIEW + "view_data.fxml";

    public final static String  SPC_CHART_VIEW_ID = "spc_chart";
    public final static String  SPC_CHART_VIEW_RES = PRE_VIEW + "chart_result.fxml";


    public final static String  SPC_CHOOSE_STATISTICAL_VIEW_ID = "spc_choose_statistical";
    public final static String  SPC_CHOOSE_STATISTICAL_VIEW_RES = PRE_VIEW + "choose_dialog.fxml";


    public final static String  SPC_QUICK_SEARCH_VIEW_ID = "spc_quick_search";
    public final static String  SPC_QUICK_SEARCH_VIEW_RES = PRE_VIEW + "quick_search.fxml";

    public final static String  SPC_ADVANCE_SEARCH_VIEW_ID = "advance";
    public final static String  SPC_ADVANCE_SEARCH_VIEW_RES = PRE_VIEW + "advance.fxml";

    public final static String  SPC_BASIC_SEARCH_VIEW_RES = PRE_VIEW + "basic_search.fxml";
}


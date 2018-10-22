package com.dmsoft.firefly.plugin.chart;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/28.
 */
public class GlobalMenuApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

//        MenuButton menuButton = new MenuButton("ssss");
//        Button node = new Button("ssss");
//        GlobalMenu.getInstance().show(menuButton, Side.BOTTOM,0,0);

//        node.setOnMouseClicked(event -> {
//
//            GlobalMenu.getInstance().show(node, Side.BOTTOM, 0, 0);
//        });

        Menu menu = new Menu("menu");
        MenuItem menuItem1 = new MenuItem("aaaaa");
        MenuItem menuItem2 = new MenuItem("bbbbb");
        MenuItem menuItem3 = new MenuItem("ccccc");
        MenuItem menuItem4 = new MenuItem("ddddd");
        MenuItem menuItem5 = new MenuItem("eeeee");
        menu.getItems().setAll(menuItem1, menuItem2, menuItem3, menuItem4, menuItem5);
        MenuBar menuBar = new MenuBar(menu);
        menuItem1.setOnAction(event -> {
            System.out.println("ssssss");
        });

        Scene scene = new Scene(menuBar, 200, 100);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        double maxX = bounds.getMaxX();
        double maxY = bounds.getMaxY();

        System.out.println("minX" + minX);
        System.out.println("minY" + minY);
        System.out.println("maxX" + maxX);
        System.out.println("maxY" + maxY);


//        System.out.println("window" + node.getScene().getWindow().getX());
//        System.out.println("window" + node.getScene().getX());
//        System.out.println("window" + node.getScene().getWindow().getY());
//        System.out.println("window" + node.getScene().getY());
//
//        System.out.println("window" + node.getScene().getWidth());
//        System.out.println("window" + node.getScene().getHeight());
//
//
//        System.out.println("minX***" + node.getBoundsInLocal().getMinX());
//        System.out.println("minY***" + node.getBoundsInLocal().getMaxX());
//        System.out.println("maxX***" + node.getBoundsInLocal().getMinY());
//        System.out.println("maxY***" + node.getBoundsInLocal().getMaxY());

//        System.out.println("messageTip" + messageTip.getBoundsInParent().getMaxY());
//        System.out.println("messageTip" + messageTip.getBoundsInParent().getMinY());
//        System.out.println("messageTip" + messageTip.getBoundsInParent().getHeight());

    }
}

class GlobalMenu extends ContextMenu {
    /**
     * 单例
     */
    private static GlobalMenu INSTANCE = null;

    /**
     * 私有构造函数
     */
    private GlobalMenu() {
        MenuItem settingMenuItem = new MenuItem("设置");
        MenuItem updateMenuItem = new MenuItem("检查更新");
        MenuItem feedbackMenuItem = new MenuItem("官方论坛");
        MenuItem aboutMenuItem = new MenuItem("问题与建议");
        MenuItem companyMenuItem = new MenuItem("关于");
        MenuItem companyMenuItem1 = new MenuItem("关于");
        MenuItem companyMenuItem2 = new MenuItem("关于");
        MenuItem companyMenuItem3 = new MenuItem("关于");
        MenuItem companyMenuItem4 = new MenuItem("关于");


        getItems().add(settingMenuItem);
        getItems().add(updateMenuItem);
        getItems().add(companyMenuItem);
        getItems().add(feedbackMenuItem);
        getItems().add(aboutMenuItem);
        getItems().add(companyMenuItem1);
        getItems().add(companyMenuItem2);
        getItems().add(companyMenuItem3);
        getItems().add(companyMenuItem4);

        this.setPrefHeight(10);
        this.setMinHeight(10);
        this.setMaxHeight(10);
    }

    /**
     * 获取实例
     *
     * @return GlobalMenu
     */
    public static GlobalMenu getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GlobalMenu();
        }

        return INSTANCE;
    }
}

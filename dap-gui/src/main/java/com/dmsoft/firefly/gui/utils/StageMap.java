package com.dmsoft.firefly.gui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;

public final class StageMap {

//    private static UiUtils instance;
//
//    public static UiUtils getInstance() {
//        if (instance == null) {
//            instance = new UiUtils();
//        }
//        return instance;
//    }

    //建立一个专门存储Stage的Map，全部用于存放Stage对象
    private static HashMap<String, Stage> stages = new HashMap<String, Stage>();

    //建立一个专门存储Pane的Map，全部用于存放Pane对象
    private static HashMap<String, Node> nodes = new HashMap<>();

    /**
     * 将加载好的Pane放到Map中进行管理
     *
     * @param name  设定Pane的名称
     * @param pane Pane
     */
    public static void addNode(String name, Node node) {
        nodes.put(name, node);
    }


    /**
     * 通过Pane名称获取Pane对象
     *
     * @param name Pane的名称
     * @return 对应的Pane对象
     */
    public static Node getNode(String name) {
        return nodes.get(name);
    }

    /**
     * 将加载好的Stage放到Map中进行管理
     *
     * @param name  设定Stage的名称
     * @param stage Stage的对象
     */
    public static void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }


    /**
     * 通过Stage名称获取Stage对象
     *
     * @param name Stage的名称
     * @return 对应的Stage对象
     */
    public static Stage getStage(String name) {
        return stages.get(name);
    }


    /**
     * 将主舞台的对象保存起来，这里只是为了以后可能需要用，目前还不知道用不用得上
     *
     * @param primaryStageName 设置主舞台的名称
     * @param primaryStage     主舞台对象，在Start()方法中由JavaFx的API建立
     */
    public static void setPrimaryStage(String primaryStageName, Stage primaryStage) {
        addStage(primaryStageName, primaryStage);
    }


    /**
     * 加载窗口地址，需要fxml资源文件属于独立的窗口并用Pane容器或其子类继承
     *
     * @param name      注册好的fxml窗口的文件
     * @param resources fxml资源地址
     * @param styles    可变参数，init使用的初始化样式资源设置
     * @return 是否加载成功
     */
    public static boolean loadStage(String name, String resources, String s, StageStyle... styles) {
        try {
            if (stages.containsKey(name)) {
                return true;
            }
            System.out.println("load");
            //加载FXML资源文件
            FXMLLoader loader = new FXMLLoader(StageMap.class.getClassLoader().getResource(resources));
            Pane tempPane = (Pane) loader.load();
            tempPane.getStylesheets().add(s);
            //通过Loader获取FXML对应的ViewCtr，并将本StageController注入到ViewCtr中
//            ControlledStage controlledStage = (ControlledStage) loader.getController();
//            controlledStage.setStageMap(this);

            //构造对应的Stage
            Scene tempScene = new Scene(tempPane);
            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);

            //配置initStyle
            for (StageStyle style : styles) {
                tempStage.initStyle(style);
            }

            //将设置好的Stage放到HashMap中
            addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 显示Stage但不隐藏任何Stage
     *
     * @param name 需要显示的窗口的名称
     * @return 是否显示成功
     */
    public static boolean showStage(String name) {
        getStage(name).show();
        return true;
    }


    /**
     * 显示Stage并隐藏对应的窗口
     *
     * @param show  需要显示的窗口
     * @param close 需要删除的窗口
     * @return
     */
    public static boolean showStage(String show, String close) {
        getStage(close).close();
        showStage(show);
        return true;
    }


    /**
     * 在Map中删除Stage加载对象
     *
     * @param name 需要删除的fxml窗口文件名
     * @return 是否删除成功
     */
    public static boolean unloadStage(String name) {
        if (stages.remove(name) == null) {
            System.out.println("窗口不存在，请检查名称");
            return false;
        } else {
            System.out.println("窗口移除成功");
            return true;
        }
    }

    /**
     * 加载FXML资源文件到Pane容器
     *
     * @param name      注册好的pane窗口的文件
     * @param node      node
     * @return Node
     */
    public static Node loadPane(String name, Node node) {
        try {
            if (nodes.containsKey(name)) {
                return nodes.get(name);
            }
            System.out.println("load");
            //将设置好的pane放到HashMap中
            addNode(name, node);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes.get(name);
    }
}

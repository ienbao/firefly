/*
 * Copyright (c) 2017. For Intelligent Group.
 */

import com.dmsoft.firefly.core.sdkimpl.dai.EnvServiceImpl;
import com.dmsoft.firefly.core.sdkimpl.dataframe.BasicDataFrameFactoryImpl;
import com.dmsoft.firefly.plugin.spc.utils.FXMLLoaderUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/1/29.
 */
public class TestApplication extends Application {

    static {
        FXMLLoaderUtils.isDebug = true;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        EnvService envService = new EnvServiceImpl();
        DataFrameFactory dataFrameFactory = new BasicDataFrameFactoryImpl();
        RuntimeContext.registerBean(EnvService.class, envService);
        RuntimeContext.registerBean(DataFrameFactory.class, dataFrameFactory);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/spc.fxml"));
        loader.setResources(ResourceBundle.getBundle("i18n.message_en_US"));
        Parent root = loader.load();


        Scene scene = new Scene(root, 1280, 704);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
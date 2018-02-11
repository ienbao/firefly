package com.dmsoft.firefly.gui.components.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class StageMap {
    private static final Logger logger = LoggerFactory.getLogger(StageMap.class);

    //For storing Stage objects
    private static HashMap<String, Stage> stages = new HashMap<String, Stage>();

    /**
     * method to add stage
     *
     * @param name name
     * @param stage stage
     */
    public static void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }

    /**
     * method to get stage by name
     *
     * @param name name
     * @return  stage stage
     */
    public static Stage getStage(String name) {
        return stages.get(name);
    }

    /**
     * method to set primary stage by name
     *
     * @param primaryStageName name
     * @param primaryStage primaryStage
     */
    public static void setPrimaryStage(String primaryStageName, Stage primaryStage) {
        addStage(primaryStageName, primaryStage);
    }



    /**
     * method to set primary stage by name
     *
     * @param name name
     * @param resources primaryStage
     * @param style style
     * @param styles styles
     * @return Whether to load stage success or not
     */
    public static boolean loadStage(String name, String resources, String style, StageStyle... styles) {
        try {
            if (stages.containsKey(name)) {
                return true;
            }
            //load fxml
            FXMLLoader loader = new FXMLLoader(StageMap.class.getClassLoader().getResource(resources));
            Pane tempPane = (Pane) loader.load();
            tempPane.getStylesheets().add(style);

            //The corresponding Stage
            Scene tempScene = new Scene(tempPane);
            tempScene.setFill(Color.TRANSPARENT);

            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);

            //set initStyle
            for (StageStyle s : styles) {
                tempStage.initStyle(s);
            }

            //add stage to HashMap
            addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * method to set primary stage by name
     *
     * @param name name
     * @param resources pane
     * @param modality true:modality
     * @param style style
     * @param styles styles
     * @return Whether to load stage success or not
     */
    public static boolean loadStage(String name, Pane resources, boolean modality, String style, StageStyle... styles) {
        try {
            if (stages.containsKey(name)) {
                return true;
            }
            resources.getStylesheets().add(style);

            //The corresponding Stage
            Scene tempScene = new Scene(resources);
            tempScene.setFill(Color.TRANSPARENT);

            Stage tempStage = new Stage();
            if (modality) {
                tempStage.initModality(Modality.APPLICATION_MODAL);
            }
            tempStage.setScene(tempScene);

            //set initStyle
            for (StageStyle s : styles) {
                tempStage.initStyle(s);
            }

            //add stage to HashMap
            addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * method to show stage by name
     *
     * @param name name
     * @@return  Whether to show success or not
     */
    public static boolean showStage(String name) {
        getStage(name).show();
        return true;
    }

    /**
     * method to close one stage hidden another
     *
     * @param name name
     * @@return  Whether to close success or not， true:success
     */
    public static boolean closeStage(String name) {
        getStage(name).close();
        return true;
    }

    /**
     * method to Show one stage hidden another
     *
     * @param show name
     * @param close name
     * @@return  boolean
     */
    public static boolean showStage(String show, String close) {
        getStage(close).close();
        showStage(show);
        return true;
    }

    /**
     * method to unload stage by name
     *
     * @param name name
     * @@return Whether to unload success or not， true:success
     */
    public static boolean unloadStage(String name) {
        if (stages.remove(name) == null) {
            logger.error("Stage does not exist, please check the name");
            return false;
        } else {
            logger.debug("Stage removal success.");
            return true;
        }
    }
}

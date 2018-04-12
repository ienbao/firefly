package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.gui.components.window.WindowPane;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class StageMap {
    private static final Logger logger = LoggerFactory.getLogger(StageMap.class);

    //For storing Stage objects
    private static HashMap<String, Stage> stages = new HashMap<String, Stage>();
    private static HashMap<String, Stage> primaryStages = new HashMap<String, Stage>();


    /**
     * method to add stage
     *
     * @param name  name
     * @param stage stage
     */
    public static void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }

    /**
     * method to get stage by name
     *
     * @param name name
     * @return stage stage
     */
    public static Stage getStage(String name) {
        return stages.get(name);
    }

    /**
     * method to set primary stage by name
     *
     * @param name  name
     * @param primaryStage     primaryStage
     */
    public static void setPrimaryStage(String name, Stage primaryStage) {
        primaryStages.put(name, primaryStage);
    }

    /**
     * method to get primary stage by name
     *
     * @param name name
     * @return stage stage
     */
    public static Stage getPrimaryStage(String name) {
        return primaryStages.get(name);
    }

    /**
     * method to set primary stage by name
     *
     * @param name      name
     * @param resources primaryStage
     * @param style     style
     * @param styles    styles
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
            Image image = new Image("/images/desktop_mac_logo.png");
            tempStage.getIcons().addAll(image);
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
     * @param name      name
     * @param resources pane
     * @param modality  true:modality
     * @param cusStyles style
     * @param styles    styles
     * @return Whether to load stage success or not
     */
    public static boolean loadStage(String name, Pane resources, boolean modality, List<String> cusStyles, StageStyle... styles) {
        try {
            if (stages.containsKey(name)) {
                return true;
            }
            //The corresponding Stage
            Scene tempScene = new Scene(resources);
            if (cusStyles != null && !cusStyles.isEmpty()) {
                cusStyles.forEach(s -> {
                    tempScene.getStylesheets().add(s);
                });
            }

            tempScene.setFill(Color.TRANSPARENT);


            Stage tempStage = new Stage();
            Image image = new Image("/images/desktop_mac_logo.png");
            tempStage.getIcons().addAll(image);
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
     * method to set primary stage by name
     *
     * @param name      name
     * @param resources pane
     * @param modality  true:modality
     * @param cusStyles style
     * @param styles    styles
     * @return Whether to load stage success or not
     */
    public static Stage loadAndRefreshStage(String name, Object title, Pane resources, boolean modality, int windowModel, List<String> cusStyles, StageStyle... styles) {
        try {
            WindowPane windowPane = null;
            if (title instanceof String) {
                windowPane = new WindowPane((String) title, resources);

            } else if (title instanceof Pane) {
                windowPane = new WindowPane((Pane) title, resources);
            } else {
                windowPane = new WindowPane("", resources);
            }

            windowPane.setWindowsModel(windowModel);
            Stage tempStage = null;

            if (StageMap.getStage(name) == null) {
                //The corresponding Stage
                Scene tempScene = new Scene(windowPane);
                if (cusStyles != null && !cusStyles.isEmpty()) {
                    cusStyles.forEach(s -> {
                        tempScene.getStylesheets().add(s);
                    });
                }

                tempScene.setFill(Color.TRANSPARENT);

                tempStage = new Stage();
                tempStage.setResizable(false);
                if (modality) {
                    tempStage.initModality(Modality.APPLICATION_MODAL);
                }
                tempStage.setScene(tempScene);
                Image image = new Image("/images/desktop_mac_logo.png");
                tempStage.getIcons().addAll(image);

                //set initStyle
                for (StageStyle s : styles) {
                    tempStage.initStyle(s);
                }

                //add stage to HashMap
                addStage(name, tempStage);
            } else {
                tempStage = StageMap.getStage(name);
                Scene tempScene = tempStage.getScene();
                tempScene.setRoot(windowPane);
                tempScene.getStylesheets().clear();

                tempScene.setFill(Color.TRANSPARENT);

                if (cusStyles != null && !cusStyles.isEmpty()) {
                    cusStyles.forEach(s -> {
                        tempScene.getStylesheets().add(s);
                    });
                }
                //Cannot set style and modality once stage has been set visible
                /*for (StageStyle s : styles) {
                    tempStage.initStyle(s);
                }*/
            }

            windowPane.setStage(tempStage);
            windowPane.init();
            return StageMap.getStage(name);
        } catch (Exception e) {
            e.printStackTrace();
            return StageMap.getStage(name);
        }
    }

    /**
     * method to show stage by name
     *
     * @param name name
     * @@return Whether to show success or not
     */
    public static boolean showStage(String name) {
        getStage(name).toFront();
        getStage(name).show();
        return true;
    }

    /**
     * method to close one stage hidden another
     *
     * @param name name
     * @@return Whether to close success or not， true:success
     */
    public static boolean closeStage(String name) {
        getStage(name).close();
        return true;
    }

    /**
     * method to Show one stage hidden another
     *
     * @param show  name
     * @param close name
     * @@return boolean
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

    /**
     * method to get all stage
     *
     * @return all stage name
     */
    public static Set<String> getAllStage() {
        return stages.keySet();
    }

    public static Stage createNoManagedStage(Object title, Pane resources, boolean modality, List<String> cusStyles, StageStyle... styles) {
        try {
            WindowPane windowPane = null;
            if (title instanceof String) {
                windowPane = new WindowPane((String) title, resources);

            } else if (title instanceof Pane) {
                windowPane = new WindowPane((Pane) title, resources);
            } else {
                windowPane = new WindowPane("", resources);
            }

            windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_X);

            //The corresponding Stage
            Scene tempScene = new Scene(windowPane);
            if (cusStyles != null && !cusStyles.isEmpty()) {
                cusStyles.forEach(s -> {
                    tempScene.getStylesheets().add(s);
                });
            }

            tempScene.setFill(Color.TRANSPARENT);


            Stage tempStage = new Stage();
            Image image = new Image("/images/desktop_mac_logo.png");
            tempStage.getIcons().addAll(image);
            if (modality) {
                tempStage.initModality(Modality.APPLICATION_MODAL);
            }
            tempStage.setScene(tempScene);

            //set initStyle
            for (StageStyle s : styles) {
                tempStage.initStyle(s);
            }
            tempStage.setResizable(false);

            windowPane.setStage(tempStage);
            windowPane.init();

            return tempStage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

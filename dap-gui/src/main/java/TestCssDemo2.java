import com.dmsoft.firefly.gui.component.ContentStackPane;
import com.dmsoft.firefly.gui.utils.TabUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TestCssDemo2 extends Application {
    private GridPane gridPane;
    private StackPane contentStackPane;
    private Map<String, TabPane> tabPaneMap = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        //通过fxml的UI布局文件加载实际的界面内容
        gridPane = initTabPane();
        gridPane.setMinWidth(1400);
        Scene scene = new Scene(gridPane);

        scene.getStylesheets().add("css/redfall/testdemo2.css");
        stage.setScene(scene);
        stage.show();
    }
    private GridPane initTabPane() {
        String name = "name_1";
        GridPane gridPane = new GridPane();
        StackPane contentStackPane = new StackPane();
        contentStackPane.setMaxWidth(1400);
        contentStackPane.setMinWidth(1400);
        TabPane tabPane = new TabPane();
        tabPane.setTabMinWidth(70);
        Tab tab = null;
        tabPane.setId(name);
        //for (int i = 0; i < 10; i++) {
            tab = new Tab();
            String [] namee = name.split("_");
            tab.setText(namee[0]+"_"+(1));
            tabPane.getTabs().add(tab);
            tabPaneMap.put(name,tabPane);
       // }
        contentStackPane.getChildren().add(tabPane);
        tabPane.setStyle("-fx-skin: 'com.dmsoft.firefly.gui.component.TabPaneSkinDemo'");
        gridPane.add(contentStackPane, 0, 0);
        System.out.println(tabPane.getStyleClass());
        System.out.println(tab.getStyleClass());
        TabUtils.tabSelectedListener(tab, tabPane);
        TabUtils.disableCloseTab(tabPane);
        return gridPane;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

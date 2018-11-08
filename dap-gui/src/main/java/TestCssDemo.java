import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestCssDemo extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    //通过fxml的UI布局文件加载实际的界面内容
    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/test_css_demo.fxml"));
    root.getStylesheets().add("css/test.css");

    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}

import com.dmsoft.firefly.gui.LodingButton;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TestButtonDemo extends Application {

  int count = 0;

  @Override
  public void start(Stage stage) throws IOException {
    //通过fxml的UI布局文件加载实际的界面内容
//    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/test-button.fxml"));
//    root.getStylesheets().add("css/test.css");
    LodingButton lodingButton = new LodingButton();
    lodingButton.getStylesheets().add("css/test.css");

    lodingButton.setOnMouseClicked(event -> {
      count ++;
      if (count%2 == 0){
        lodingButton.change(false);
      }else{
        lodingButton.change(true);
      }
    });


    Scene scene = new Scene(lodingButton);

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

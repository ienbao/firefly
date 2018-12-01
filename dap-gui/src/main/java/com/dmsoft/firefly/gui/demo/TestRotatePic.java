package com.dmsoft.firefly.gui.demo;

import com.dmsoft.firefly.gui.RotatePic;
import com.dmsoft.firefly.gui.demo.LodingButton;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class TestRotatePic extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    AnchorPane anchorPane = new AnchorPane();
    Image image = new Image(getClass().getResourceAsStream("/images/icon_loading_gray.png"));
    RotatePic rotatePic = new RotatePic(image);
    LodingButton button = new LodingButton();
    anchorPane.getStylesheets().add("css/test.css");
    button.setGraphic(rotatePic);
    anchorPane.getChildren().add(button);
    Scene scene = new Scene(anchorPane);

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

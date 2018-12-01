package com.dmsoft.firefly.gui.demo;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomEventDemo extends Application {

  /**
   * The main entry point for all JavaFX applications. The start method is called after the init
   * method has returned, and after the system is ready for the application to begin running.
   *
   * <p>
   * NOTE: This method is called on the JavaFX Application Thread.
   * </p>
   *
   * @param primaryStage the primary stage for this application, onto which the application scene
   * can be set. The primary stage will be embedded in the browser if the application was launched
   * as an applet. Applications may create other stages, if needed, but they will not be primary
   * stages and will not be embedded in the browser.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    CustomTextField textField = new CustomTextField();

    textField.addEventHandler(ActionEvent.ACTION, event -> {
      System.out.println("ok is input.");
    });

    Scene scene = new Scene(textField);

    primaryStage.setScene(scene);
    primaryStage.show();

  }


  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}

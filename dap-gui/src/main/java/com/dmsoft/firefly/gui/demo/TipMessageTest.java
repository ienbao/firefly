package com.dmsoft.firefly.gui.demo;

import com.dmsoft.firefly.gui.utils.TipMessage;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class TipMessageTest extends Application {
    Label label=new Label("Event");
    int i=1;
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        root.getStylesheets().add("css/test.css");
        TipMessage tipMessage=new TipMessage();
        Button button1=new Button("Success");
        Button button2=new Button("Warn");
        Button button3=new Button("Warn Event");
        button1.setOnAction(event ->{
            tipMessage.setType(TipMessage.TipMessageType.Success);
            tipMessage.setMessage("Success message");

        } );
        button2.setOnAction(event ->{
            tipMessage.setType(TipMessage.TipMessageType.Warn);
            tipMessage.setMessage("Warn message");
        } );

        button3.setOnAction(event ->{
            tipMessage.setType(TipMessage.TipMessageType.WarnEvent);
            tipMessage.setMessage("Warn message");
            tipMessage.setLinkText("Event");
            tipMessage.setMouseEvent(gotoEvent());
        } );

        root.getChildren().addAll(tipMessage,button1,button2,button3,label);
        Scene scene = new Scene(root,500,500);
        stage.setScene(scene);
        stage.show();
    }

    private EventHandler<MouseEvent> gotoEvent() {
        return event-> {
            label.setText("event"+i);
            i=i+1;
        };
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

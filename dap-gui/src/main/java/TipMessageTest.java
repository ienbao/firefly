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
    Label label=new Label("success");
    int i=1;
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        root.getStylesheets().add("css/test.css");
        TipMessage tipMessage=new TipMessage();
        Button button1=new Button("success");
        Button button2=new Button("warn");
        Button button3=new Button("warn event");
        button1.setOnAction(event ->{tipMessage.showSuccessMsg("Success", "Success message");} );
        button2.setOnAction(event ->{tipMessage.showWarnMsg("Warn", "Warn message");} );
        button3.setOnAction(event ->{tipMessage.showWarnMsg("Warn", "Warn message","Event",gotoEvent());} );
        root.getChildren().addAll(tipMessage,button1,button2,button3,label);
        Scene scene = new Scene(root,500,500);
        stage.setScene(scene);
        stage.show();
    }
    private EventHandler<MouseEvent> gotoEvent() {
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                label.setText("event"+i);
                i=i+1;
            }
        };
        return eventHandler;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

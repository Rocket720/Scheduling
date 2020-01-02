import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.util.Scanner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.StageStyle;
import javafx.scene.control.Label;

public class ScheGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Scanner input = new Scanner(System.in);
        primaryStage.setTitle("ScheGUI");
        primaryStage.setWidth(1366); primaryStage.setHeight(768);
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        //Sets up Splash Screen
        VBox parent = new VBox();
        Scene test = new Scene(parent);

//        //Background
//        Rectangle bg = new Rectangle(0,0,primaryStage.getWidth(), primaryStage.getHeight());//bg.setFill(Color.web("2E2E2E"));
//        parent.getChildren().add(bg);
        test.setFill(Color.web("525252"));


        //ScheGUI Label
        Label l = new Label("kill me");
        Label label1 = new Label("ScheGUI");
        label1.setScaleX(4);label1.setScaleY(4); label1.setTextFill(Color.BLACK);
        label1.setTranslateX(/*primaryStage.getWidth()/2*/80);label1.setTranslateY(/*primaryStage.getHeight()/2*/20);
        parent.getChildren().add(label1);








        //Shows Window
        primaryStage.setScene(test);
        primaryStage.show();


    }
}

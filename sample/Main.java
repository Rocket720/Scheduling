package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("ScheGUI");

        //Scene test = new Scene(parent, 1366, 768);
        //primaryStage.setScene(test);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

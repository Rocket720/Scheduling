package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFXTester extends Application {

    @Override
    public void start(Stage stage) {
        /*
         *
         * my css file content:
         *
         * .progress-indicator .indicator { -fx-background-color: transparent;
         * -fx-background-insets: 0; -fx-background-radius: 0;
         *
         * } .progress-indicator { -fx-progress-color: green ; }
         *
         *
         *
         */
        Stage initStage = new Stage();

        initStage.initStyle(StageStyle.TRANSPARENT);
        ProgressIndicator loadProgress = new ProgressIndicator();
        loadProgress.setSkin(null);
        loadProgress.setPrefWidth(50);
        VBox box = new VBox();
        box.getChildren().add(loadProgress);
        final Scene scene = new Scene(box, 150, 150);

        scene.setFill(Color.TRANSPARENT);

        initStage.setScene(scene);
        scene.getStylesheets().add("application.css");

        initStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
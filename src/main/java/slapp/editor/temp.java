package slapp.editor;

import javafx.application.Application;
import javafx.stage.Stage;

public class temp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println(System.getProperties().get("javafx.runtime.version"));

    }
}

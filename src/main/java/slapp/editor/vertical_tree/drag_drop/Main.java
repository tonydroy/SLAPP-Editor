package slapp.editor.vertical_tree.drag_drop;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        RootLayout rootLayout = new RootLayout();
        root.setCenter(rootLayout);

        try {
            Scene scene = new Scene(root,640,480);
            scene.getStylesheets().add(getClass().getResource("/drag_drop.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }


}
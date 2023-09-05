package slapp.editor;

import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.application.*;
import slapp.editor.view.SimpleEditorView;

public class EditorMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon32x32.png")));
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        SimpleEditorView simpleView = new SimpleEditorView(stage); simpleView.start(stage);
    }



}

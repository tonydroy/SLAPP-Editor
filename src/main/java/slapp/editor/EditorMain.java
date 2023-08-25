package slapp.editor;

import javafx.stage.*;
import javafx.application.*;
import slapp.editor.view.SimpleEditorView;

public class EditorMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SimpleEditorView simpleView = new SimpleEditorView(); simpleView.start(stage);
    }



}

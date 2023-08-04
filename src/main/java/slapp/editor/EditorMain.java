package slapp.editor;

import javafx.stage.*;
import javafx.application.*;
import slapp.editor.demos.*;

public class EditorMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

//        BasicDemoCopy demo = new BasicDemoCopy(); demo.demoCopy(stage);
//        ActionsDemoCopy demo = new ActionsDemoCopy(); demo.actionsDemo(stage);
//        HighlightDemoCopy demo = new HighlightDemoCopy(); demo.highlightDemo(stage);
//        ListsDemoCopy demo = new ListsDemoCopy(); demo.listsDemo(stage);
//        EmojiDemoCopy demo = new EmojiDemoCopy(); demo.emojiDemo(stage);
        EmojiPopupDemoCopy demo = new EmojiPopupDemoCopy(); demo.emojiPopupDemo(stage);

//        ** FullFeatureDemoCopy has its own main method **

/*
        RichTextArea editor = new RichTextArea();
        BorderPane root = new BorderPane(editor);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

 */
    }



}

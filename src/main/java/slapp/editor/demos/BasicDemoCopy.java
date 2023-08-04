package slapp.editor.demos;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * Basic sample with the RichTextArea control, showing a prompt message.
 * <p>
 * While all the control features are available, but there are no menus or
 * toolbars included, so user interaction is limited to shorcuts or context
 * menu.
 * <p>
 * For instance, after typing some text, select all (Ctrl/Cmd + A) or part of it
 * (with mouse or keyboard) and press Ctrl/Cmd + I for italic or Ctrl/Cmd + B for bold.
 * <p>
 * Undo/Redo, Cut/Copy/Paste options work as usual.
 * You can copy text with emoji unicode, and paste it on the editor.
 * For instance, while running this sample, copy this text:
 * <pre>
 *     {@code Hello 👋🏼}
 * </pre>
 * and paste it, you should see the waving hand emoji and some text. Also copying from
 * the control and pasting it on the control itself or on any other application will work
 * too, keeping the rich content when possible.
 * <p>
 * Right click to display a context menu with different options, like inserting a
 * 2x1 table.
 * <p>
 * To apply the rest of the control features, some UI is needed for user interaction. See
 * the {@link FullFeaturedDemo} sample for a complete and advanced showcase.
 */
public class BasicDemoCopy {


    /**
     * Defines the text and paragraph decorations, based on the default presets,
     * but with Arial font
     */
    private static final List<DecorationModel> decorations = List.of(
            new DecorationModel(0, 0,
                    TextDecoration.builder().presets().fontFamily("Arial").build(),
                    ParagraphDecoration.builder().presets().build()));

    /**
     * Creates an empty document with the new decorations
     */
    private static final Document emptyDocument =
            new Document("", decorations, 0);


    public void demoCopy(Stage stage) {
        RichTextArea editor = new RichTextArea();
        editor.setDocument(emptyDocument);
        editor.setPromptText("Type something!");
        editor.setPadding(new Insets(20));

        BorderPane root = new BorderPane(editor);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("RichTextArea");
        stage.show();
    }


}

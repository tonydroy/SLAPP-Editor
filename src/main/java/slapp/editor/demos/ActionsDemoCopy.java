package slapp.editor.demos;


import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.Selection;
import com.gluonhq.richtextarea.action.TextDecorateAction;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.text.FontPosture.ITALIC;
import static javafx.scene.text.FontPosture.REGULAR;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;

/**
 * This sample shows how to use the RichTextArea control to render some text and
 * interact with it in a basic way via three toggle buttons.
 * <p>
 * Run the sample and select some or all text, via mouse or keyboard, and then
 * press the toggles to see how the decoration of the selection changes accordingly.
 * <p>
 * Note that when you move the caret over the text, the toggles update their state
 * (enabled means bold/italic/underline active), showing at any time the current
 * decoration at the caret.
 */
public class ActionsDemoCopy {


    private static final String text =
            "Document is the basic model that contains all the information required for the RichTextArea control, " +
                    "in order to render all the rich content, including decorated text, images and other non-text objects.\n" +
                    "A document is basically a string with the full text, and a list of DecorationModel that contain the text and paragraph decorations for one or more fragments of the text, " +
                    "where a fragment can be defined as the longest substring of the text that shares the same text and paragraph decorations.\n" +
                    "Any change to the document invalidates the undo/redo stack, forces the RichTextAreaSkin to recreate the PieceTable and sets it on the RichTextAreaViewModel.";

    private static final TextDecoration preset =
            TextDecoration.builder().presets().fontFamily("Arial").fontSize(14).build();
    private static final ParagraphDecoration parPreset =
            ParagraphDecoration.builder().presets().build();


    public void actionsDemo(Stage stage) {
        List<DecorationModel> decorationList = getDecorations();
        Document document = new Document(text, decorationList, text.length());

        RichTextArea editor = new RichTextArea();
        editor.setDocument(document);

        BorderPane root = new BorderPane(editor);

        // decorate actions
        ToggleButton fontBoldToggle = new ToggleButton("Bold");
        new TextDecorateAction<>(editor, fontBoldToggle.selectedProperty().asObject(),
                d -> d.getFontWeight() == BOLD,
                (builder, a) -> builder.fontWeight(a ? BOLD : NORMAL).build());
        ToggleButton fontItalicToggle = new ToggleButton("Italic");
        new TextDecorateAction<>(editor, fontItalicToggle.selectedProperty().asObject(),
                d -> d.getFontPosture() == ITALIC,
                (builder, a) -> builder.fontPosture(a ? ITALIC : REGULAR).build());
        ToggleButton fontUnderlinedToggle = new ToggleButton("Underline");
        new TextDecorateAction<>(editor, fontUnderlinedToggle.selectedProperty().asObject(),
                TextDecoration::isUnderline, (builder, a) -> builder.underline(a).build());
        HBox actionsBox = new HBox(fontBoldToggle, fontItalicToggle, fontUnderlinedToggle);
        actionsBox.getStyleClass().add("actions-box");
        root.setTop(actionsBox);

        Scene scene = new Scene(root, 800, 300);
        scene.getStylesheets().add(ActionsDemoCopy.class.getClassLoader().getResource("actionsDemo.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RTA: Actions");
        stage.show();

        // select some text and change its decoration;
        editor.getActionFactory().selectAndDecorate(new Selection(12, 27),
                TextDecoration.builder().presets().fontFamily("Arial")
                        .fontWeight(BOLD).underline(true)
                        .build()).execute(new ActionEvent());
    }

    private List<DecorationModel> getDecorations() {
        List<DecorationModel> decorations = new ArrayList<>();
        // decoration for text
        decorations.add(new DecorationModel(0, text.length(), preset, parPreset));
        return decorations;
    }

}

package slapp.editor.demos;


import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiSkinTone;
import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import slapp.editor.demos.popup.EmojiPopup;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.util.List;



/**
 * This basic sample shows how to use the RichTextArea control to render text and
 * emojis, including a popup control to select interactively emojis.
 */
public class EmojiPopupDemoCopy {


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


    public void emojiPopupDemo(Stage stage) {
        RichTextArea editor = new RichTextArea();
        editor.setDocument(emptyDocument);
        editor.setPromptText("Type something or insert emojis!");
        editor.setSkinTone(EmojiSkinTone.MEDIUM_SKIN_TONE);
        editor.setPadding(new Insets(20));

        Region region = new Region();
        region.getStyleClass().addAll("icon", "emoji-outline");
        Button emojiButton = new Button(null, region);
        emojiButton.getStyleClass().add("emoji-button");
        emojiButton.setOnAction(e -> {
            EmojiPopup emojiPopup = new EmojiPopup();
            emojiPopup.setSkinTone(editor.getSkinTone());
            editor.skinToneProperty().bindBidirectional(emojiPopup.skinToneProperty());
            emojiPopup.setOnAction(ev -> {
                Emoji emoji = (Emoji) ev.getSource();
                editor.getActionFactory().insertEmoji(emoji).execute(new ActionEvent());
            });
            emojiPopup.show(emojiButton);
        });

        HBox root = new HBox(20, editor, emojiButton);
        HBox.setHgrow(editor, Priority.ALWAYS);
        root.getStyleClass().add("root-box");

        Scene scene = new Scene(root, 600, 300);
        scene.getStylesheets().add(EmojiPopupDemoCopy.class.getClassLoader().getResource("emojiPopupDemo.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RTA: Text and emoji popup");
        stage.show();

        editor.requestFocus();
    }

}

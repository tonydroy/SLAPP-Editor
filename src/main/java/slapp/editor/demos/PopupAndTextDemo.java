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

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.text.FontPosture.ITALIC;


/**
 * This basic sample shows how to use the RichTextArea control to render text and
 * emojis, including a popup control to select interactively emojis.
 */
public class PopupAndTextDemo {

    private static final String text = "Overline: a\u035eb\u035ec; \u231c\u035e\ud835\uddc7\u035e+\u035e2; underline a\u035fb\u035fc. Here is a regular symbol: '\u03f4' a low range emoji number '\u2194' a couple logic symbols: '\u2192' and '\u2200' and 2-member symbols: '\ud835\udc9c' and '\ud835\udd04'.\n";

    private static final TextDecoration preset =
            TextDecoration.builder().presets().fontFamily("Ariel").fontSize(14).build();

    private static final ParagraphDecoration parPreset =
            ParagraphDecoration.builder().presets().build();

    public void popupAndTextDemo(Stage stage) {

        List<DecorationModel> decorationList = getDecorations();
        Document document = new Document(text, decorationList, text.length());


        RichTextArea editor = new RichTextArea();
        editor.setDocument(document);
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

    private List<DecorationModel> getDecorations() {
        List<DecorationModel> decorations = new ArrayList<>();
        // decoration for text
        decorations.add(new DecorationModel(0, text.length(), preset, parPreset));
        return decorations;
    }

}

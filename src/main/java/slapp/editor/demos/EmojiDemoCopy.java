package slapp.editor.demos;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.EmojiSkinTone;
import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gluonhq.emoji.EmojiSkinTone.*;

/**
 * This basic sample shows how to use the RichTextArea control to render text and
 * emojis.
 * <p>
 * This sample doesn't include a control to select interactively emojis (See
 * {@link EmojiPopupDemo} for that).
 */
public class EmojiDemoCopy {


    private static final String text =
            "Here is a regular symbol: '\u03f4' a low range emoji number '\u2194' a couple logic symbols: '\u2192' and '\u2200' and 2-member symbols: '\ud835\udc9c' and '\ud835\udd04'.\n  And now emojis \ud83d\ude03!\n" +
                    "\uD83D\uDC4B\uD83C\uDFFC, this is some random text with some emojis " +
                    "like \uD83E\uDDD1\uD83C\uDFFC\u200D\uD83E\uDD1D\u200D\uD83E\uDDD1\uD83C\uDFFD or " +
                    "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC73\uDB40\uDC63\uDB40\uDC74\uDB40\uDC7F.\n" +
                    "These are emojis with skin tone or hair style, like:\n";

    private static final StringBuilder fullText = new StringBuilder(text);
    private static final TextDecoration preset =
            TextDecoration.builder().presets().fontFamily("Arial").fontSize(14).build();
    private static final ParagraphDecoration parPreset =
            ParagraphDecoration.builder().presets().build();


    public void emojiDemo(Stage stage) {
        String personText = EmojiData.search("person").stream()
                .limit(10)
                .map(Emoji::character)
                .collect(Collectors.joining(", "));
        fullText.append(personText).append(".\nAnd this is another emoji with skin tone: ");

        List<DecorationModel> decorationList = getDecorations();
        Document document = new Document(fullText.toString(), decorationList, fullText.length());

        RichTextArea editor = new RichTextArea();
        editor.setDocument(document);

        BorderPane root = new BorderPane(editor);
        Scene scene = new Scene(root, 800, 300);
        scene.getStylesheets().add(EmojiDemoCopy.class.getClassLoader().getResource("emojiDemo.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("RTA: Text and emojis");
        stage.show();

        editor.setSkinTone(MEDIUM_SKIN_TONE);

        // Use ActionFactory to insert emojis
        EmojiData.emojiFromShortName("runner")
                .ifPresent(emoji -> {
                    Emoji emojiWithTone = emoji.getSkinVariationMap().get(editor.getSkinTone().getUnicode());
                    editor.getActionFactory().insertEmoji(emojiWithTone).execute(new ActionEvent());
                });
    }

    private List<DecorationModel> getDecorations() {
        List<DecorationModel> decorations = new ArrayList<>();
        // decoration for text
        decorations.add(new DecorationModel(0, fullText.length(), preset, parPreset));
        return decorations;
    }

}

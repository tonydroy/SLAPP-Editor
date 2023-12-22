package slapp.editor.decorated_rta;

import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.TextBuffer;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.List;
import java.util.Objects;


public class ExtractSubText {

    public ExtractSubText() {}

    public static TextFlow getTextFromDoc(int start, int length, Document doc) {
        TextFlow flow = new TextFlow();
        String docString = doc.getText();
        List<DecorationModel> decorations = doc.getDecorations();
        StringBuilder sb = new StringBuilder();
        DecorationModel currentDec = getDecorationAtIndex(start, decorations);

        for (int i = start; i < start + length; i++ ) {
            DecorationModel decorationAtIndex = getDecorationAtIndex(i, decorations);
            if (decorationAtIndex != currentDec) {
                Text text = buildText(sb.toString(), (TextDecoration) currentDec.getDecoration());
                flow.getChildren().add(text);
                sb.delete(0, sb.length());
                currentDec = decorationAtIndex;
                sb.append(docString.charAt(i));
            } else {
                if (!Character.isWhitespace(docString.charAt(i))) sb.append(docString.charAt(i));
            }
        }
        if (sb.length() != 0) {
            Text text = buildText(sb.toString(), (TextDecoration) currentDec.getDecoration());
            flow.getChildren().add(text);
        }
        return flow;
    }

    private static DecorationModel getDecorationAtIndex(int index, List<DecorationModel> decorations) {
        DecorationModel decorationAtIndex = null;
        for (DecorationModel decoration : decorations) {
            if (decoration.getStart() <= index && index < decoration.getStart() + decoration.getLength()) {
                decorationAtIndex = decoration;
                break;
            }
        }
        return decorationAtIndex;
    }

    private static Text buildText(String content, TextDecoration decoration) {

        if ("\n".equals(content)) {
            Text lfText = new Text(TextBuffer.ZERO_WIDTH_TEXT);
            return lfText;
        }
        Objects.requireNonNull(decoration);
        Text text = new Text(Objects.requireNonNull(content));
        text.setFill(decoration.getForeground());
        text.setStrikethrough(decoration.isStrikethrough());
        text.setUnderline(decoration.isUnderline());

        if (decoration.isSubscript() || decoration.isTransSubscript()) text.setTranslateY(decoration.getFontSize() * .17);
        else if (decoration.isSuperscript() || decoration.isTransSuperscript()) text.setTranslateY(decoration.getFontSize() * -.4);
        else text.setTranslateY(0.0);

        if (decoration.isTransSubscript() || decoration.isTransSuperscript()) text.setTranslateX(decoration.getFontSize() * -.3);
        else text.setTranslateX(0.0);

        double actualFontSize = (decoration.isSuperscript() || decoration.isSubscript() || decoration.isTransSuperscript() || decoration.isTransSubscript() ) ? decoration.getFontSize() * .72 : decoration.getFontSize();

        Font font = Font.font(
                decoration.getFontFamily(),
                decoration.getFontWeight(),
                decoration.getFontPosture(),
                actualFontSize);
        text.setFont(font);

        return text;
    }

}

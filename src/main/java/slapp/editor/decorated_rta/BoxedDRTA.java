package slapp.editor.decorated_rta;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class BoxedDRTA {

    DecoratedRTA drta;
    HBox boxedRTA;

    public BoxedDRTA() {
        drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        rta.setContentAreaWidth(2000);
        boxedRTA = new HBox(rta);


        EventHandler<MouseEvent> mouseEventHandler = e -> {
            rta.requestFocus();
            e.consume();
        };
        boxedRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
    }



    public DecoratedRTA getDRTA() {
        return drta;
    }

    public HBox getBoxedRTA() {
        return boxedRTA;
    }

    public RichTextArea getRTA() {return drta.getEditor(); }
}

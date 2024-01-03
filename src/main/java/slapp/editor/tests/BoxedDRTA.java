package slapp.editor.tests;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import slapp.editor.decorated_rta.DecoratedRTA;

public class BoxedDRTA {

    DecoratedRTA drta;
    HBox boxedRTA;

    BoxedDRTA() {
        drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        boxedRTA = new HBox(rta);


        EventHandler<MouseEvent> mouseEventHandler = e -> {
            rta.requestFocus();
            e.consume();
        };
        boxedRTA.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventHandler);






    }



    public DecoratedRTA getDrta() {
        return drta;
    }

    public HBox getBoxedRTA() {
        return boxedRTA;
    }
}

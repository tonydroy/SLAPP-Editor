/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.
If not, see <https://www.gnu.org/licenses/>.
 */

package slapp.editor.decorated_rta;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

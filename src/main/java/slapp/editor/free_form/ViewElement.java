/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.free_form;

import javafx.scene.Node;

public class ViewElement {

    private Node node;
    private int indentLevel = 0;


    public ViewElement(Node node, int indentLevel) {
        this.node = node;
        this.indentLevel = indentLevel;
    }

    public Node getNode() {     return node;  }

    public void setNode(Node node) {     this.node = node;   }

    public int getIndentLevel() {     return indentLevel;   }

    public void setIndentLevel(int indentLevel) {     this.indentLevel = indentLevel;   }

}

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

package slapp.editor.derivation;

import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;

import java.util.ArrayList;
import java.util.List;

public class ViewLine {

    private Label lineNumberLabel = null;
    private int depth = 0;
    private LineType lineType = null;
    private BoxedDRTA lineContentBoxedDRTA = null;
    private TextFlow justificationFlow = null;
    private List<Label> clientLabels = new ArrayList<Label>();

    public ViewLine(){}
    public ViewLine(Label lineNumberLabel, int depth, LineType lineType, BoxedDRTA bdrta, TextFlow justificationFlow, List<Label> clientLabels) {
        this.lineNumberLabel = lineNumberLabel;
        this.depth = depth;
        this.lineType = lineType;
        this.lineContentBoxedDRTA = bdrta;
        this.justificationFlow = justificationFlow;
        this.clientLabels = clientLabels;
    }

    public void setLineNumberLabel(Label lineNumberLabel) {
        this.lineNumberLabel = lineNumberLabel;
    }

    public void setDepth(int depth) { this.depth = depth; }

    public void setLineType(LineType type) {
        this.lineType = type;
    }

    public void setLineContentBoxedDRTA(BoxedDRTA lineContentBoxedDRTA) {
        this.lineContentBoxedDRTA = lineContentBoxedDRTA;
    }

    public void setJustificationFlow(TextFlow justificationFlow) {this.justificationFlow = justificationFlow; }

    public Label getLineNumberLabel() {
        return lineNumberLabel;
    }

    public int getDepth() {
        return depth;
    }

    public LineType getLineType() {
        return lineType;
    }

    public BoxedDRTA getLineContentBoxedDRTA() {
        return lineContentBoxedDRTA;
    }

    public TextFlow getJustificationFlow() {
        return justificationFlow;
    }

    public List<Label> getClientLabels() {
        return clientLabels;
    }

}

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

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;

public class ModelLine implements Serializable {

    private int gridLineIndex;
    private int depth;
    private Document lineContentDoc;
    private String justification = "";
    private LineType lineType = LineType.MAIN_CONTENT_LINE;

    public ModelLine(int depth, Document lineContent, String justification, LineType type) {
        this.depth = depth;
        this.lineContentDoc = lineContent;
        this.justification = justification;
        this.lineType = type;
    }


    public int getDepth() {
        return depth;
    }

    public Document getLineContentDoc() {
        return lineContentDoc;
    }

    public String getJustification() {
        return justification;
    }

    public LineType getLineType() {
        return lineType;
    }


}

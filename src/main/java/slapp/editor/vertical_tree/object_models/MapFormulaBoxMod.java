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

package slapp.editor.vertical_tree.object_models;

import com.gluonhq.richtextarea.model.Document;

import java.io.Serializable;
import java.util.List;

public class MapFormulaBoxMod implements Serializable {
    private static final long serialVersionUID = 100L;
    private String idString;
    private double layoutX;
    private double layoutY;
//    private double width;
    private Document text;
    private List<String> linkIdStrings;



    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
    }

 //   public double getWidth() {
   //     return width;
//    }

//    public void setWidth(double width) {
//        this.width = width;
//    }

    public Document getText() {
        return text;
    }

    public void setText(Document text) {
        this.text = text;
    }

    public List<String> getLinkIdStrings() {
        return linkIdStrings;
    }

    public void setLinkIdStrings(List<String> linkIdStrings) {
        this.linkIdStrings = linkIdStrings;
    }
}

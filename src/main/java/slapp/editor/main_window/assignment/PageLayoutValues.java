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

package slapp.editor.main_window.assignment;

import javafx.print.PageOrientation;

import java.io.Serializable;

public class PageLayoutValues implements Serializable {

    PageOrientation orientation;
    double leftMargin;
    double rightMargin;
    double topMargin;
    double bottomMargin;

    public PageLayoutValues(PageOrientation orientation, double leftMargin, double rightMargin, double topMargin, double bottomMargin) {
        this.orientation = orientation;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    public PageOrientation getOrientation() {
        return orientation;
    }



    public double getLeftMargin() {
        return leftMargin;
    }



    public double getRightMargin() {
        return rightMargin;
    }


    public double getTopMargin() {
        return topMargin;
    }



    public double getBottomMargin() {
        return bottomMargin;
    }


}

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

import java.io.Serializable;

public class UnderlineMod implements Serializable {
    private static final long serialVersionUID = 100L;
    double startX;
    double length;
    double yPos;

    public UnderlineMod(double startX, double length, double yPos) {
        this.startX = startX;
        this.length = length;
        this.yPos = yPos;
    }

    public double getStartX() {return startX; }
    public double getLength() { return length;  }
    public double getyPos() { return yPos; }
}

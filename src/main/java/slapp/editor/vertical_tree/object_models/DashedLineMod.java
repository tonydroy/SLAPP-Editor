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

public class DashedLineMod implements Serializable {

    private double layoutX;
    private double layoutY;
    private double width;

    public DashedLineMod(double layoutX, double layoutY, double width) {
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.width = width;
    }

    public double getLayoutX() {  return layoutX;  }

    public double getLayoutY() { return layoutY;   }

    public double getWidth() { return width;  }
}

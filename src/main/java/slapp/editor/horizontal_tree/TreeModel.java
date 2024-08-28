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

package slapp.editor.horizontal_tree;

import java.io.Serializable;

public class TreeModel implements Serializable {
    private static final long serialVersionUID = 100L;

    private double paneXlayout;
    private double paneYlayout;
    private double rootXlayout;
    private double rootYlayout;
    private BranchModel root;



    public void setPaneXlayout(double paneXlayout) {    this.paneXlayout = paneXlayout;   }

    public void setPaneYlayout(double paneYlayout) {   this.paneYlayout = paneYlayout;    }

    public double getPaneXlayout() {  return paneXlayout;  }

    public double getPaneYlayout() { return paneYlayout;  }

    public void setRoot(BranchModel root) {     this.root = root;  }

    public BranchModel getRoot() {   return root;  }

    public double getRootXlayout() {
        return rootXlayout;
    }

    public void setRootXlayout(double rootXlayout) {
        this.rootXlayout = rootXlayout;
    }

    public double getRootYlayout() {
        return rootYlayout;
    }

    public void setRootYlayout(double rootYlayout) {
        this.rootYlayout = rootYlayout;
    }
}

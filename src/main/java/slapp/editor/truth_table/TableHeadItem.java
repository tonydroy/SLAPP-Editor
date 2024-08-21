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

package slapp.editor.truth_table;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class TableHeadItem {

    private TextFlow expression;
    private ColumnConstraints columnConstraints;
    boolean blankColumn = false;
    boolean dividerColumn = false;

    public TableHeadItem(TextFlow expression, ColumnConstraints columnConstraints) {
        this.expression = expression;
        this.columnConstraints = columnConstraints;
    }

    public void setDividerColumn(boolean dividerColumn) { this.dividerColumn = dividerColumn; }

    public TextFlow getExpression() {
        return expression;
    }

    public ColumnConstraints getColumnConstraints() {
        return columnConstraints;
    }

    public boolean isBlankColumn() { return blankColumn; }

    public boolean isDividerColumn() { return dividerColumn; }

    public void setBlankColumn(boolean blankColumn) {this.blankColumn = blankColumn;  }

//    public String toString() {
//        return expression.getChildren().toString();    }

}

package slapp.editor.truth_table;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.TextFlow;

public class TableHeadItem {

    private TextFlow expression;
    private ColumnConstraints columnConstraints;
    boolean blankColumn = false;

    TableHeadItem(TextFlow expression, ColumnConstraints columnConstraints) {
        this.expression = expression;
        this.columnConstraints = columnConstraints;
    }

    public TextFlow getExpression() {
        return expression;
    }

    public ColumnConstraints getColumnConstraints() {
        return columnConstraints;
    }

    public boolean isBlankColumn() { return blankColumn; }

    public void setBlankColumn(boolean blankColumn) {this.blankColumn = blankColumn;  }

}

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

    TableHeadItem(TextFlow expression, ColumnConstraints columnConstraints) {
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

package slapp.editor.truth_table;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.TextFlow;

public class TableHeadItem {

    private TextFlow expression;
    private ColumnConstraints columnConstraints;

    TableHeadItem(TextFlow expression, ColumnConstraints columnConstraints) {
        this.expression = expression;
        this.columnConstraints = columnConstraints;
    }

    public TextFlow getExpression() {
        return expression;
    }

    public void setExpression(TextFlow expression) {
        this.expression = expression;
    }

    public ColumnConstraints getColumnConstraints() {
        return columnConstraints;
    }

    public void setColumnConstraints(ColumnConstraints columnConstraints) {
        this.columnConstraints = columnConstraints;
    }
}

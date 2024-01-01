package slapp.editor.truth_table;

import com.gluonhq.richtextarea.model.Document;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.ExtractSubText;

import java.util.ArrayList;
import java.util.List;

public class ParseDocForTTable {
    private static List<TableHeadItem> headItems = new ArrayList<>();
    private List<String> unaryOperators;
    private List<String> binaryOperators;
    Document doc;
    int start = 0;
    int span = 0;
    String formulaString;
    int formulaLength;

    public ParseDocForTTable(List unaryOperators, List binaryOperators ) {
        this.unaryOperators = unaryOperators;
        this.binaryOperators = binaryOperators;

    }

    public List<TableHeadItem> generateHeadItems(Document document) {
        headItems.clear();
        start = 0;
        span = 0;
        this.doc = document;
        formulaString = doc.getText();
        formulaLength = formulaString.length();

        while (start < formulaLength) {
            span = 1;
            char c = formulaString.charAt(start);
            if (Character.isWhitespace(c)) start++;
            if (isOpenBracket(c)) openBracketSequence();
            else if (isOperator(c)) operatorSequence(c);
            else if (isRelationChar(c)) relationSequence();
            else if (isCloseBracket(c)) {
                TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
                ColumnConstraints constraints = new ColumnConstraints();
                constraints.setHalignment(HPos.CENTER);
                TableHeadItem headItem = new TableHeadItem(flow, constraints);
                headItems.add(headItem);
                EditorAlerts.fleetingPopup("Unexpected close bracket.");
                return headItems;
            }
        }
        return headItems;
    }

    private void operatorSequence(char c) {
        TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
        flow.setTextAlignment(TextAlignment.CENTER);


        if (isBinaryOperator(c)) {
            flow.getChildren().add(0, new Text(" "));
            flow.getChildren().add(new Text(" "));
        }
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        TableHeadItem headItem = new TableHeadItem(flow, constraints);
        headItems.add(headItem);
        start = start + span;
    }

    private void openBracketSequence() {

        while (start + span < formulaLength && isOpenBracket(formulaString.charAt(start + span))) {
            span++;
        }
        if (start + span < formulaLength && isOperator(formulaString.charAt(start + span))) {
            span++;
            TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
            flow.setTextAlignment(TextAlignment.CENTER);

            ColumnConstraints constraints = new ColumnConstraints();
 //           constraints.setHalignment(HPos.RIGHT);
            constraints.setHalignment(HPos.CENTER);

            TableHeadItem headItem = new TableHeadItem(flow, constraints);
            headItems.add(headItem);
            start = start + span;
            return;
        }
        while (start + span < formulaLength && isRelationChar(formulaString.charAt(start + span))) {
            span++;
            if (start + span < formulaLength) bracketMatch(formulaString.charAt(start + span));
        }
        TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
        flow.setTextAlignment(TextAlignment.CENTER);

        ColumnConstraints constraints = new ColumnConstraints();
 //       constraints.setHalignment(HPos.RIGHT);
        constraints.setHalignment(HPos.CENTER);
        TableHeadItem headItem = new TableHeadItem(flow, constraints);
        headItems.add(headItem);
        start = start + span;
    }

    private void relationSequence() {
        while (start + span < formulaLength && isRelationChar(formulaString.charAt(start + span))) {
            span++;
            if (start + span < formulaLength) bracketMatch(formulaString.charAt(start + span));
        }
        if (start + span < formulaLength && isOperator(formulaString.charAt(start + span))) {
            TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
            flow.setTextAlignment(TextAlignment.CENTER);

            ColumnConstraints constraints = new ColumnConstraints();
  //          constraints.setHalignment(HPos.RIGHT);
            constraints.setHalignment(HPos.CENTER);
            TableHeadItem headItem = new TableHeadItem(flow, constraints);
            headItems.add(headItem);
            start = start + span;
            return;
        }
        while (start + span < formulaLength && isCloseBracket(formulaString.charAt(start + span))) {
            span++;
        }
        TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
        flow.setTextAlignment(TextAlignment.CENTER);

        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
  //      constraints.setHalignment(HPos.LEFT);
        TableHeadItem headItem = new TableHeadItem(flow, constraints);
        headItems.add(headItem);
        start = start + span;
    }

    private boolean isOpenBracket(char c) {
        return (c == '(' || c == '[');
    }
    private boolean isCloseBracket(char c) {
        return (c == ')' || c == ']');
    }
    private boolean isUnaryOperator(char c) {
        boolean isUnaryOperator = false;
        for (String ops : unaryOperators) {
            char op = ops.charAt(0);
            if (c == op) isUnaryOperator = true;
        }
        return isUnaryOperator;
    }
    private boolean isBinaryOperator(char c) {
        boolean isBinaryOperator = false;
        for (String ops : binaryOperators) {
            char op = ops.charAt(0);
            if (c == op) isBinaryOperator = true;
        }
        return isBinaryOperator;
    }
    private boolean isOperator(char c) {
        return (isBinaryOperator(c) || isUnaryOperator(c));
    }
    private boolean isRelationChar(char c) {
        return (!isOpenBracket(c) && !isCloseBracket(c) && !isOperator(c)  );
    }
    private boolean isMatchingBracket(char open, char test) {
        boolean match = false;
        if (open == '(' && test == ')') match = true;
        if (open == '[' && test == ']') match = true;
        return match;
    }
    private void bracketMatch(char c) {
        if (isOpenBracket(c)) {
            int count = 1;
            while (start + span < formulaLength && count != 0) {
                span++;
                if (start + span < formulaLength && formulaString.charAt(start + span) == c) count++;
                if (start + span < formulaLength && isMatchingBracket(c, formulaString.charAt(start + span)))  count--;
            }
            span++;
        }
    }

}

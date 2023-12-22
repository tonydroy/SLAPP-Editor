package slapp.editor.truth_table;

import com.gluonhq.richtextarea.model.Document;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.ExtractSubText;

import java.util.ArrayList;
import java.util.List;

public class ParseDocForTTable {

    private static List<TableHeadItem> headItems = new ArrayList<>();
    private char[] unaryOperators;
    private char[] binaryOperators;

    Document doc;

    int start = 0;
    int span = 0;

    String formulaString;
    int formulaLength;




    public ParseDocForTTable(char[] unaryOperators, char[] binaryOperators, Document doc) {
        this.unaryOperators = unaryOperators;
        this.binaryOperators = binaryOperators;
        this.doc = doc;
    }

    public List<TableHeadItem> generateHeadItems(Document doc) {
        formulaString = doc.getText();
        formulaLength = formulaString.length();
        for (int i = 0; i < formulaString.length(); i++) {
            System.out.println((int) formulaString.charAt(i));
        }



        while (start < formulaLength) {
            span = 1;
            char c = formulaString.charAt(start);
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
        System.out.println(headItems.toString());
        return headItems;
    }

    private void operatorSequence(char c) {
        TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
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
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHalignment(HPos.RIGHT);
            TableHeadItem headItem = new TableHeadItem(flow, constraints);
            headItems.add(headItem);
            start = start + span;
            return;
        }
        while (start + span < formulaLength && isRelationChar(formulaString.charAt(start + span))) {
            span++;
        }
        TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.RIGHT);
        TableHeadItem headItem = new TableHeadItem(flow, constraints);
        headItems.add(headItem);
        start = start + span;
    }

    private void relationSequence() {
        while (start + span < formulaLength && isRelationChar(formulaString.charAt(start + span))) {
            span++;
        }
        if (start + span < formulaLength && isOperator(formulaString.charAt(start + span))) {
            TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHalignment(HPos.RIGHT);
            TableHeadItem headItem = new TableHeadItem(flow, constraints);
            headItems.add(headItem);
            start = start + span;
            return;
        }
        while (start + span < formulaLength && isCloseBracket(formulaString.charAt(start + span))) {
            span++;
        }
        TextFlow flow = ExtractSubText.getTextFromDoc(start, span, doc);
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.RIGHT);
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
        for (char op : unaryOperators) {
            if (c == op) isUnaryOperator = true;
        }
        return isUnaryOperator;
    }
    private boolean isBinaryOperator(char c) {
        boolean isBinaryOperator = false;
        for (char op : binaryOperators) {
            if (c == op) isBinaryOperator = true;
        }
        return isBinaryOperator;
    }
    private boolean isOperator(char c) {
        return (isBinaryOperator(c) || isUnaryOperator(c));
    }
    private boolean isRelationChar(char c) {
        return (!isOpenBracket(c) && !isCloseBracket(c) && !isOperator(c) );
    }




}

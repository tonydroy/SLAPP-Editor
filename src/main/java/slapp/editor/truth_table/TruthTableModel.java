package slapp.editor.truth_table;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TruthTableModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private boolean started = false;
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();
    private List<Document> mainFormulas = new ArrayList<>();
    private List<Document> basicFormulas = new ArrayList<>();
    private List<Character> unaryOperators = new ArrayList<>();
    private List<Character> binaryOperators = new ArrayList<>();
    private List<List<Character>> tableValues = new ArrayList<>();   //[w][h]
    private List<Document> rowComments = new ArrayList<>(); //[h]
    private List<Boolean> columnHighlights = new ArrayList<>(); //[w]
    private boolean conclusionDivider;
    private boolean showChoiceArea = false;
    private String choiceLead = new String("");
    private String aPrompt = new String("");
    private boolean aSelected = false;
    private String bPrompt = new String("");
    private boolean bSelected = false;
    private Document explainDocument = new Document();


    //dummy model
    public TruthTableModel() {
        exerciseName = "Test Truth Table";
        exerciseStatement = new Document("This is a dummy truth table exercise.");
        mainFormulas.addAll(new ArrayList<>(Arrays.asList(new Document("A\u2192B"), new Document("\u223cB"), new Document("\u223cA"))));
        basicFormulas.addAll(new ArrayList<>(Arrays.asList(new Document("A"), new Document("B"))));
        unaryOperators.add((char) 0x223c);
        binaryOperators.addAll(new ArrayList<>(Arrays.asList((char) 0x2227, (char) 0x2228, (char) 0x2192, (char) 0x2194)));

        List<Character> col1 = new ArrayList<>(Arrays.asList( 'T', 'T', 'F', 'F' ));
        tableValues.add(col1);
        List<Character> col2 = new ArrayList<>(Arrays.asList( 'T', 'F', 'T', 'F' ));
        tableValues.add(col2);
        List<Character> col3 = new ArrayList<>(Arrays.asList( 'T', 'T', 'F', 'F'));
        tableValues.add(col3);
        List<Character> col4 = new ArrayList<>(Arrays.asList('T', 'F', 'T', 'T'));
        tableValues.add(col4);
        List<Character> col5 = new ArrayList<>(Arrays.asList('T', 'F', 'T', 'F'));
        tableValues.add(col5);
        List<Character> col6 = new ArrayList<>(Arrays.asList('F', 'T', 'F', 'T'));
        tableValues.add(col6);
        List<Character> col7 = new ArrayList<>(Arrays.asList('T', 'F', 'T', 'T'));
        tableValues.add(col7);
        List<Character> col8 = new ArrayList<>(Arrays.asList('F', 'F', 'T', 'T'));
        tableValues.add(col8);
        List<Character> col9 = new ArrayList<>(Arrays.asList('T', 'T', 'F', 'F'));
        tableValues.add(col9);

        columnHighlights.addAll(new ArrayList<>(Arrays.asList(false, false, false, false, false, false, false, false, false)));
        if (mainFormulas.size() > 1) conclusionDivider = true;
        showChoiceArea = true;
        choiceLead = "This argument is,";
        aPrompt = "Valid";
        bPrompt = "Invalid";
    }

    public List<Document> getRowComments() { return rowComments;}

    public List<Boolean> getColumnHighlights() { return columnHighlights; }

    public List<List<Character>> getTableValues() { return tableValues; }

    public boolean isConclusionDivider() { return conclusionDivider; }
    public List<Document> getMainFormulas() { return mainFormulas; }
    public List<Character> getUnaryOperators() { return unaryOperators; }

    public List<Character> getBinaryOperators() { return binaryOperators; }

    public List<Document> getBasicFormulas() { return basicFormulas; }

    public Document getExplainDocument() { return explainDocument; }

    public String getChoiceLead() {return choiceLead; }

    public String getaPrompt() { return aPrompt; }

    public boolean isaSelected() { return aSelected; }

    public String getbPrompt() { return bPrompt; }

    public boolean isbSelected() { return bSelected; }

    public boolean isShowChoiceArea() { return showChoiceArea; }

    @Override
    public String getExerciseName() {
        return exerciseName;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void setStarted(boolean started) { this.started = started; }

    @Override
    public Document getExerciseComment() {
        return exerciseComment;
    }

    @Override
    public Document getExerciseStatement() {
        return exerciseStatement;
    }

    @Override
    public void setExerciseComment(Document comment) { exerciseComment = comment;    }

    @Override
    public double getStatementPrefHeight() {
        return statementPrefHeight;
    }

    @Override
    public void setStatementPrefHeight(double height) { statementPrefHeight = height;  }

}

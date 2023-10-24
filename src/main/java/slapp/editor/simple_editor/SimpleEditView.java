package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;

public class SimpleEditView implements ExerciseView<DecoratedRTA, ArrayList<DecoratedRTA>> {

    private String exerciseName = new String();
    private String contentPrompt = new String();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private ArrayList<DecoratedRTA> exerciseContent = new ArrayList<>();
    private double statementPrefHeight = 80;

    private Node exerciseControlNode = new VBox();
    private Pagination pagination;
    private Button addPageButton;
    private Button removePageButton;
    private MainWindowView mainView;




    public SimpleEditView(MainWindowView mainView) {
        this.mainView = mainView;
        this.pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory((index) -> {
            DecoratedRTA DRTApage = exerciseContent.get(index);
            mainView.setContentHeightProperty(DRTApage.getEditor().prefHeightProperty());
            mainView.setCenterVgrow();
            return DRTApage.getEditor();
        });


        this.addPageButton = new Button("Insert Page");
        addPageButton.setTooltip(new Tooltip("Add after current page"));
        addPageButton.setPrefWidth(90.0);

        this.removePageButton = new Button("Remove Page");
        removePageButton.setTooltip(new Tooltip("Remove current page"));
        removePageButton.setPrefWidth(90.0);

        VBox controlBox = (VBox) exerciseControlNode;        ;
        controlBox.setSpacing(30.0);
        controlBox.setPadding(new Insets(200,20,200,30));



        controlBox.getChildren().addAll(addPageButton, removePageButton);

    }

    public void initializeViewDetails() {
        exerciseStatement.getEditor().setPrefHeight(statementPrefHeight);
        exerciseComment.getEditor().setPrefHeight(70.0);
        exerciseComment.getEditor().setPromptText("Comment:");
        if (!exerciseContent.isEmpty()) {
            exerciseContent.get(0).getEditor().setPromptText(contentPrompt);
        }
        pagination.setPageCount(exerciseContent.size());
    }

    public void addBlankContentPage(int index, DecoratedRTA drta) {
        exerciseContent.add(index, drta);
        pagination.setPageCount(exerciseContent.size());
        pagination.setCurrentPageIndex(index);
    }
    public void removeContentPage(int index) {
        exerciseContent.remove(index);
        int newSize = exerciseContent.size();
        pagination.setPageCount(newSize);
        if (newSize >= index) {
            pagination.setCurrentPageIndex(index);
        }
        else {
            pagination.setCurrentPageIndex(Math.max(0, index - 1));
        }
    }

    public int getContentPageIndex() {
        return pagination.getCurrentPageIndex();
    }
    public Button getAddPageButton() { return addPageButton; }
    public Button getRemovePageButton() { return removePageButton; }

    @Override
    public double getStatementHeight() { return exerciseStatement.getEditor().getHeight(); }
    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }
    @Override
    public String getExerciseName() {return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }
    @Override
    public ArrayList<DecoratedRTA> getExerciseContent() {
        return exerciseContent;
    }
    @Override
    public void setExerciseContent(ArrayList<DecoratedRTA> exerciseContent) { this.exerciseContent = exerciseContent; }
    @Override
    public DecoratedRTA getExerciseComment() {
        return exerciseComment;
    }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }
    public void setExerciseControl(Node control) { this.exerciseControlNode = control; }
    public Node getExerciseStatementNode() {
        return exerciseStatement.getEditor();
    }
    public Node getExerciseContentNode() {
        return pagination;
    }
    public DoubleProperty getContentHeightProperty() {
        return exerciseContent.get(pagination.getCurrentPageIndex()).getEditor().prefHeightProperty();
    }
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }
    public void setContentPrompt(String prompt) {
        contentPrompt = prompt;
    }





}


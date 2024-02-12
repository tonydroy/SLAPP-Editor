package slapp.editor.simple_editor;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;
import java.util.ArrayList;
import java.util.List;

public class SimpleEditView implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = new String();
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private List<DecoratedRTA> contentPageList = new ArrayList<>();
    private String contentPrompt = new String();
    private Pagination pagination;
    private Button addPageButton;
    private Button removePageButton;
    private Node exerciseControlNode = new VBox();

    public SimpleEditView(MainWindowView mainView) {
        this.mainView = mainView;
        this.pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory((index) -> {
            DecoratedRTA drtaPage = contentPageList.get(index);
            RichTextArea rtaPage = drtaPage.getEditor();
            rtaPage.getStylesheets().add("slappTextArea.css");
            mainView.setContentHeightProperty(rtaPage.prefHeightProperty());
            mainView.setCenterVgrow();
            return rtaPage;
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

    void initializeViewDetails() {
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.setPrefHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);
        RichTextArea commentRTA = exerciseComment.getEditor();
        exerciseComment.getEditor().getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setPromptText("Comment:");

        if (!contentPageList.isEmpty()) {
            contentPageList.get(0).getEditor().setPromptText(contentPrompt);
        }
        pagination.setPageCount(contentPageList.size());
    }

    void addBlankContentPage(int index, DecoratedRTA drta) {
        contentPageList.add(index, drta);
        pagination.setPageCount(contentPageList.size());
        pagination.setCurrentPageIndex(index);
    }
    void removeContentPage(int index) {
        contentPageList.remove(index);
        int newSize = contentPageList.size();
        pagination.setPageCount(newSize);
        if (newSize >= index) {
            pagination.setCurrentPageIndex(index);
        }
        else {
            pagination.setCurrentPageIndex(Math.max(0, index - 1));
        }
    }
    int getContentPageIndex() { return pagination.getCurrentPageIndex();  }
    Button getAddPageButton() { return addPageButton; }
    Button getRemovePageButton() { return removePageButton; }
    public List<DecoratedRTA> getContentPageList() { return contentPageList; }
    public void setContentPageList(List<DecoratedRTA> contentPageList) { this.contentPageList = contentPageList; }
    public void setContentPrompt(String prompt) {
        contentPrompt = prompt;
    }

    @Override
    public String getExerciseName() {return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) {  this.exerciseStatement = exerciseStatement;  }
    @Override
    public Node getExerciseStatementNode() {  return exerciseStatement.getEditor();  }
    @Override
    public double getStatementHeight() { return exerciseStatement.getEditor().getHeight(); }
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }
    @Override
    public Node getExerciseContentNode() {
        return pagination;
    }
    @Override
    public DoubleProperty getContentHeightProperty() { return contentPageList.get(pagination.getCurrentPageIndex()).getEditor().prefHeightProperty(); }
    @Override
    public DoubleProperty getContentWidthProperty() { return contentPageList.get(pagination.getCurrentPageIndex()).getEditor().prefWidthProperty(); }


    @Override
    public double getContentFixedHeight() { return 0.0; }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }
    @Override
    public double getContentWidth() {
        return 0;
    }
}


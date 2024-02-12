package slapp.editor.abefg_explain;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;

public class ABEFGview implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = new String();
    private Label leaderLabelAB = new Label("");
    private Label leaderLabelEFG = new Label("");
    private CheckBox checkBoxA = new CheckBox("");
    private CheckBox checkBoxB = new CheckBox("");
    private CheckBox checkBoxE = new CheckBox("");
    private CheckBox checkBoxF = new CheckBox("");
    private CheckBox checkBoxG = new CheckBox("");
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private ArrayList<DecoratedRTA> contentPageList = new ArrayList<>();
    private String contentPrompt = new String();
    private Pagination pagination;
    private Button addPageButton;
    private Button removePageButton;
    private Node exerciseControlNode = new VBox();
    private HBox abBox = new HBox();
    private HBox efgBox = new HBox();
    private VBox checksBox = new VBox();
    private Font labelFont = new Font("Noto Serif Combo", 11);

    public ABEFGview(MainWindowView mainView) {
        this.mainView = mainView;
        leaderLabelAB.setFont(labelFont); checkBoxA.setFont(labelFont); checkBoxB.setFont(labelFont);
        abBox.getChildren().addAll(leaderLabelAB, checkBoxA, checkBoxB);
        abBox.setSpacing(20);

        leaderLabelEFG.setFont(labelFont); checkBoxE.setFont(labelFont); checkBoxF.setFont(labelFont); checkBoxG.setFont(labelFont);
        efgBox.getChildren().addAll(leaderLabelEFG, checkBoxE, checkBoxF, checkBoxG);
        efgBox.setSpacing(20);

        checksBox.getChildren().addAll(abBox, efgBox);
        checksBox.setStyle("-fx-border-color: gainsboro; -fx-border-width: 2.2; -fx-background-color: white");
        checksBox.setSpacing(10);
        checksBox.setPadding(new Insets(10));


        this.pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory((index) -> {
            Node page;
            if (index == 0) {
                DecoratedRTA drtaPage0 = contentPageList.get(index);
                RichTextArea rtaPage0 = drtaPage0.getEditor();
                rtaPage0.getStylesheets().add("slappTextArea.css");
                mainView.setContentHeightProperty(rtaPage0.prefHeightProperty());
                VBox topContentPage = new VBox(3, checksBox, rtaPage0);
                topContentPage.setMargin(checksBox, new Insets(5,0,0,0));
                page = topContentPage;
            } else {
                DecoratedRTA drtaPage = contentPageList.get(index);
                RichTextArea rtaPage = drtaPage.getEditor();
                rtaPage.getStylesheets().add("slappTextArea.css");
                mainView.setContentHeightProperty(rtaPage.prefHeightProperty());
                mainView.setCenterVgrow();
                page = rtaPage;
            }
            return page;
        });

        pagination.currentPageIndexProperty().addListener(e -> {
            Platform.runLater(() -> {
                getContentHeightProperty().unbind();
                getContentWidthProperty().unbind();
                mainView.updateContentWidthProperty();
                mainView.updateContentHeightProperty();
            });

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
        statementRTA.setMinHeight(statementPrefHeight);
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPrefHeight(70.0);
        commentRTA.setMinHeight(70.0);
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
        if (index == 0) {
            EditorAlerts.showSimpleAlert("Cannot Remove", "Cannot remove top page with selection boxes.");
        } else {
            contentPageList.remove(index);
            int newSize = contentPageList.size();
            pagination.setPageCount(newSize);
            if (newSize >= index) {
                pagination.setCurrentPageIndex(index);
            } else {
                pagination.setCurrentPageIndex(Math.max(0, index - 1));
            }
        }
    }


    public Label getLeaderLabelAB() {
        return leaderLabelAB;
    }
    public Label getLeaderLabelEFG() { return leaderLabelEFG; }
    public CheckBox getCheckBoxA() {
        return checkBoxA;
    }
    public CheckBox getCheckBoxB() {
        return checkBoxB;
    }
    public CheckBox getCheckBoxE() { return checkBoxE; }
    public CheckBox getCheckBoxF() { return checkBoxF; }
    public CheckBox getCheckBoxG() { return checkBoxG; }
    int getContentPageIndex() {return pagination.getCurrentPageIndex();  }
    Button getAddPageButton() { return addPageButton; }
    Button getRemovePageButton() { return removePageButton; }
    public ArrayList<DecoratedRTA> getContentPageList() {
        return contentPageList;
    }
    public void setContentPageList(ArrayList<DecoratedRTA> contentPageList) { this.contentPageList = contentPageList; }
    public void setContentPrompt(String prompt) {
        contentPrompt = prompt;
    }

    @Override
    public String getExerciseName() {return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment;  }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement; }
    @Override
    public Node getExerciseStatementNode() {return exerciseStatement.getEditor();    }
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
    public DoubleProperty getContentWidthProperty() { return contentPageList.get(pagination.getCurrentPageIndex()).getEditor().prefWidthProperty();  }
    @Override
    public double getContentFixedHeight() { return 55; }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }

    @Override
    public double getContentWidth() { return 200.0; }
    @Override
    public double getContentHeight() { return 200.0; }
}



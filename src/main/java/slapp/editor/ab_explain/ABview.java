package slapp.editor.ab_explain;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;
import java.util.ArrayList;
import java.util.List;

public class ABview implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = new String();
    private Label leaderLabel = new Label("");
    private CheckBox checkBoxA = new CheckBox("");
    private CheckBox checkBoxB = new CheckBox("");
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private double statementPrefHeight = 80;
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private ArrayList<DecoratedRTA> contentPageList = new ArrayList<>();
    private String contentPrompt = new String();
    private Pagination pagination;
    private Button addPageButton;
    private Button removePageButton;
    private Node exerciseControlNode = new VBox();
    private HBox abBbox = new HBox();
    private Font labelFont = new Font("Noto Serif Combo", 11);

    public ABview(MainWindowView mainView) {
        this.mainView = mainView;
        leaderLabel.setFont(labelFont); checkBoxA.setFont(labelFont); checkBoxB.setFont(labelFont);
        abBbox.getChildren().addAll(leaderLabel, checkBoxA, checkBoxB);
        abBbox.setSpacing(20);
        abBbox.setPadding(new Insets(10,10,10,10));
        abBbox.setStyle("-fx-border-color: gainsboro; -fx-border-width: 2.2; -fx-background-color: white");




        this.pagination = new Pagination();
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory((index) -> {
            Node page;
            if (index == 0) {
                DecoratedRTA drtaPage0 = contentPageList.get(index);
                RichTextArea rtaPage0 = drtaPage0.getEditor();
                rtaPage0.getStylesheets().add("slappTextArea.css");
                mainView.setContentHeightProperty(rtaPage0.prefHeightProperty());
                VBox topContentPage = new VBox(3, abBbox, drtaPage0.getEditor());
                topContentPage.setMargin(abBbox, new Insets(5,0,0,0));
                page = topContentPage;
            } else {
                DecoratedRTA DRTApage = contentPageList.get(index);
                RichTextArea RTApage = DRTApage.getEditor();
                RTApage.getStylesheets().add("slappTextArea.css");
                mainView.setContentHeightProperty(RTApage.prefHeightProperty());
                mainView.setCenterVgrow();
                page = DRTApage.getEditor();
            }
            return page;
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
        exerciseStatement.getEditor().setPrefHeight(statementPrefHeight);
        exerciseStatement.getEditor().setEditable(false);
        exerciseStatement.getEditor().getStylesheets().add("slappTextArea.css");

        exerciseComment.getEditor().setPrefHeight(70.0);
        exerciseComment.getEditor().setPromptText("Comment:");
        exerciseComment.getEditor().getStylesheets().add("slappTextArea.css");
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

    public Label getLeaderLabel() {
        return leaderLabel;
    }
    public CheckBox getCheckBoxA() {
        return checkBoxA;
    }
    public CheckBox getCheckBoxB() {
        return checkBoxB;
    }
    int getContentPageIndex() { return pagination.getCurrentPageIndex(); }
    Button getAddPageButton() { return addPageButton; }
    Button getRemovePageButton() { return removePageButton; }
    public List<DecoratedRTA> getContentPageList() { return contentPageList; }
    public void setContentPageList(ArrayList<DecoratedRTA> contentPageList) { this.contentPageList = contentPageList; }
    public void setContentPrompt(String prompt) {
        contentPrompt = prompt;
    }

    @Override
    public String getExerciseName() {return exerciseName; }
    @Override
    public void setExerciseName(String name) { this.exerciseName = name; }
    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment;     }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public double getCommentHeight() { return exerciseComment.getEditor().getHeight(); }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { this.exerciseStatement = exerciseStatement; }
    @Override
    public Node getExerciseStatementNode() { return exerciseStatement.getEditor(); }
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
    public DoubleProperty getContentWidthProperty() {return null; }
    @Override
    public double getContentFixedHeight() { return 55; }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }



}



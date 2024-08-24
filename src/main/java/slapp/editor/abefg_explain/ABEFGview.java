/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.
If not, see <https://www.gnu.org/licenses/>.
 */

package slapp.editor.abefg_explain;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

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
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private List<DecoratedRTA> contentPageList = new ArrayList<>();
    private String contentPrompt = new String();
    private Pagination pagination;
    private Button addPageButton;
    private Button removePageButton;
    private Node exerciseControlNode = new VBox();
    private HBox abBox = new HBox();
    private HBox efgBox = new HBox();
    private VBox checksBox = new VBox();
    private Font labelFont = new Font("Noto Serif Combo", 11);
    private double statementPrefHeight = 0;
    private double commentPrefHeight = 0;
    private double paginationPrefHeight = 0;
    private Spinner<Double> statementHeightSpinner;
    private Spinner<Double> statementWidthSpinner;
    private Spinner<Double> commentHeightSpinner;
    private Spinner<Double> commentWidthSpinner;
    private Spinner<Double> choicesHeightSpinner;
    private Spinner<Double> choicesWidthSpinner;
    private Spinner<Double> paginationHeightSpinner;
    private Spinner<Double> paginationWidthSpinner;
    private Node currentSpinnerNode;


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
                VBox topContentPage = new VBox(3, checksBox, rtaPage0);
                topContentPage.setVgrow(rtaPage0, Priority.ALWAYS);
                topContentPage.setMargin(checksBox, new Insets(5,0,0,0));
                page = topContentPage;
            } else {
                DecoratedRTA drtaPage = contentPageList.get(index);
                RichTextArea rtaPage = drtaPage.getEditor();
                rtaPage.getStylesheets().add("slappTextArea.css");
                page = rtaPage;
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
        controlBox.setPadding(new Insets(200,20,0,30));
        controlBox.setMinWidth(150); controlBox.setMaxWidth(150);
        controlBox.getChildren().addAll(addPageButton, removePageButton);
    }

    void initializeViewDetails() {

        //statement
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        double statementInitialHeight = Math.round(statementPrefHeight / mainView.getScalePageHeight() * 100.0 );
        statementHeightSpinner = new Spinner<>(0.0, 999.0, statementInitialHeight, 1.0);
        statementHeightSpinner.setPrefWidth(60);
        statementHeightSpinner.setDisable(false);
        statementHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        statementHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = statementHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = statementHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });


        statementRTA.prefWidthProperty().bind(mainView.scalePageWidthProperty());
        statementWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        statementWidthSpinner.setPrefWidth(60);
        statementWidthSpinner.setDisable(true);
        statementWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        statementRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != statementRTA) {
                currentSpinnerNode = statementRTA;
                mainView.updateSizeSpinners(statementHeightSpinner, statementWidthSpinner);
            }
        });

        //comment
        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPromptText("Comment:");

        double commentInitialHeight = Math.round(commentPrefHeight / mainView.getScalePageHeight() * 100.0 );
        commentHeightSpinner = new Spinner<>(0.0, 999.0, commentInitialHeight, 1.0);
        commentHeightSpinner.setPrefWidth(60);
        commentHeightSpinner.setDisable(false);
        commentHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        commentHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = commentHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = commentHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });


        commentRTA.prefWidthProperty().bind(mainView.scalePageWidthProperty());
        commentWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        commentWidthSpinner.setPrefWidth(60);
        commentWidthSpinner.setDisable(true);
        commentWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        commentRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != commentRTA) {
                currentSpinnerNode = commentRTA;
                mainView.updateSizeSpinners(commentHeightSpinner, commentWidthSpinner);
            }
        });

        //pagination
        double paginationInitialHeight = Math.round(paginationPrefHeight / mainView.getScalePageHeight() * 20.0 ) * 5.0 ;
        paginationHeightSpinner = new Spinner<>(0.0, 999.0, paginationInitialHeight, 5.0);
        paginationHeightSpinner.setPrefWidth(60);
        paginationHeightSpinner.setDisable(false);
        paginationHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        pagination.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(paginationHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        paginationHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = paginationHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = paginationHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });


        pagination.prefWidthProperty().bind(mainView.scalePageWidthProperty());
        paginationWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        paginationWidthSpinner.setPrefWidth(60);
        paginationWidthSpinner.setDisable(true);
        paginationWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        pagination.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != pagination) {
                currentSpinnerNode = pagination;
                mainView.updateSizeSpinners(paginationHeightSpinner, paginationWidthSpinner);
            }
        });

        if (!contentPageList.isEmpty()) {
            contentPageList.get(0).getEditor().setPromptText(contentPrompt);
        }
        pagination.setPageCount(contentPageList.size());

        //choices (null spinners)
        choicesHeightSpinner = new Spinner<>(0.0, 999.0, 0, 1.0);
        choicesHeightSpinner.setPrefWidth(60);
        choicesHeightSpinner.setDisable(true);
        choicesHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));

        choicesWidthSpinner = new Spinner<>(0.0, 999.0, 100.0, 1.0);
        choicesWidthSpinner.setPrefWidth(60);
        choicesWidthSpinner.setDisable(true);
        choicesWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        checksBox.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != checksBox) {
                currentSpinnerNode = checksBox;
                double choicesHeightValue = Math.round(checksBox.getHeight() / mainView.getScalePageHeight() * 100);
                choicesHeightSpinner.getValueFactory().setValue(choicesHeightValue);
                mainView.updateSizeSpinners(choicesHeightSpinner, choicesWidthSpinner);
            }
        });

        //page height listener
        mainView.scalePageHeightProperty().addListener((ob, ov, nv) -> {

            statementRTA.prefHeightProperty().unbind();
            statementHeightSpinner.getValueFactory().setValue((double) Math.round(statementHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            commentRTA.prefHeightProperty().unbind();
            commentHeightSpinner.getValueFactory().setValue((double) Math.round(commentHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            pagination.prefHeightProperty().unbind();
            paginationHeightSpinner.getValueFactory().setValue((double) Math.round(paginationHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue() / 5.0) * 5.0);
            pagination.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(paginationHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            double choicesHeightValue = Math.round(checksBox.getHeight() / mainView.getScalePageHeight() * 100);
            choicesHeightSpinner.getValueFactory().setValue(choicesHeightValue);

        });


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
    public List<DecoratedRTA> getContentPageList() {
        return contentPageList;
    }
    public void setContentPageList(List<DecoratedRTA> contentPageList) { this.contentPageList = contentPageList; }
    public void setContentPrompt(String prompt) {
        contentPrompt = prompt;
    }
    public double getCommentPrefHeight() { return exerciseComment.getEditor().getPrefHeight();  }
    public void setCommentPrefHeight(double commentPrefHeight) { this.commentPrefHeight = commentPrefHeight; }
    public double getPaginationPrefHeight() {    return pagination.getPrefHeight(); }
    public void setPaginationPrefHeight(double paginationPrefHeight) { this.paginationPrefHeight = paginationPrefHeight;   }

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
    public Node getExerciseControl() { return exerciseControlNode; }
    @Override
    public Node getRightControl() { return null; }


}



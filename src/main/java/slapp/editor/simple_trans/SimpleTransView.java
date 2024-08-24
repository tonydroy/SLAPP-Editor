/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.  If not, see
<https://www.gnu.org/licenses/>.
 */

package slapp.editor.simple_trans;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

public class SimpleTransView implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private DecoratedRTA exerciseStatement = new DecoratedRTA();
    private DecoratedRTA exerciseComment = new DecoratedRTA();
    private DecoratedRTA exerciseResponse = new DecoratedRTA();
    private DecoratedRTA exerciseInterpretation = new DecoratedRTA();

    private Node exerciseControlNode = new VBox();
    private double statementPrefHeight = 0;
    private double commentPrefHeight = 0;
    private double responsePrefHeight = 0;
    private double interpretationPrefHeight = 0;
    private Spinner<Double> statementHeightSpinner;
    private Spinner<Double> statementWidthSpinner;
    private Spinner<Double> commentHeightSpinner;
    private Spinner<Double> commentWidthSpinner;
    private Spinner<Double> responseHeightSpinner;
    private Spinner<Double> responseWidthSpinner;
    private Spinner<Double> interpretationHeightSpinner;
    private Spinner<Double> interpretationWidthSpinner;
    private Node currentSpinnerNode;


    public SimpleTransView(MainWindowView mainView) {
        this.mainView = mainView;

        Region spacer = new Region();
        spacer.setPrefWidth(150);
        VBox controlBox = (VBox) exerciseControlNode;
        controlBox.getChildren().add(spacer);
    }

    void initializeViewDetails() {

        //statement rta
        RichTextArea statementRTA = exerciseStatement.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        double statementInitialHeight = Math.round(statementPrefHeight / mainView.getScalePageHeight() * 100.0);
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

        //comment rta
        RichTextArea commentRTA = exerciseComment.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPromptText("Commment:");

        double commentInitialHeight = Math.round(commentPrefHeight / mainView.getScalePageHeight() * 100.0);
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

        //interpretation rta

        RichTextArea interpretationRTA = exerciseInterpretation.getEditor();
        interpretationRTA.getStylesheets().add("slappTextArea.css");
        interpretationRTA.setPromptText("Interpretation Function");
        exerciseInterpretation.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.BASE_AND_SANS);

        double interpretationInitialHeight = Math.round(interpretationPrefHeight / mainView.getScalePageHeight() * 20.0 ) * 5;


        interpretationHeightSpinner = new Spinner<>(0.0, 999.0, interpretationInitialHeight, 5.0);
        interpretationHeightSpinner.setPrefWidth(60);
        interpretationHeightSpinner.setDisable(false);
        interpretationHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        interpretationRTA.prefHeightProperty().bind(Bindings.max(15.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(interpretationHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        interpretationHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = interpretationHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = interpretationHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        interpretationRTA.prefWidthProperty().bind(mainView.scalePageWidthProperty());
        interpretationWidthSpinner = new Spinner<>(0.0, 999.0, 100, 5.0);
        interpretationWidthSpinner.setPrefWidth(60);
        interpretationWidthSpinner.setDisable(true);
        interpretationWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        interpretationRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            currentSpinnerNode = interpretationRTA;
            mainView.updateSizeSpinners(interpretationHeightSpinner, interpretationWidthSpinner);
        });


        //response rta
        RichTextArea responseRTA = exerciseResponse.getEditor();
        responseRTA.getStylesheets().add("slappTextArea.css");
        responseRTA.setPromptText("Formal Translation");
        exerciseResponse.getKeyboardSelector().valueProperty().setValue(RichTextAreaSkin.KeyMapValue.ITALIC_AND_SANS);

        double responseInitialHeight = Math.round(responsePrefHeight / mainView.getScalePageHeight() * 20.0 ) * 5;


        responseHeightSpinner = new Spinner<>(0.0, 999.0, responseInitialHeight, 5.0);
        responseHeightSpinner.setPrefWidth(60);
        responseHeightSpinner.setDisable(false);
        responseHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        responseRTA.prefHeightProperty().bind(Bindings.max(15.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(responseHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        responseHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = responseHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = responseHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        responseRTA.prefWidthProperty().bind(mainView.scalePageWidthProperty());
        responseWidthSpinner = new Spinner<>(0.0, 999.0, 100, 5.0);
        responseWidthSpinner.setPrefWidth(60);
        responseWidthSpinner.setDisable(true);
        responseWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        responseRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                currentSpinnerNode = responseRTA;
                mainView.updateSizeSpinners(responseHeightSpinner, responseWidthSpinner);
        });

        //page height listener
        mainView.scalePageHeightProperty().addListener((ob, ov, nv) -> {

            statementRTA.prefHeightProperty().unbind();
            statementHeightSpinner.getValueFactory().setValue((double) Math.round(statementHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            commentRTA.prefHeightProperty().unbind();
            commentHeightSpinner.getValueFactory().setValue((double) Math.round(commentHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            responseRTA.prefHeightProperty().unbind();
            responseHeightSpinner.getValueFactory().setValue((double) Math.round(responseHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue() / 5.0) * 5.0);
            responseRTA.prefHeightProperty().bind(Bindings.max(15.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(responseHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            interpretationRTA.prefHeightProperty().unbind();
            interpretationHeightSpinner.getValueFactory().setValue((double) Math.round(interpretationHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue() / 5.0) * 5.0);
            interpretationRTA.prefHeightProperty().bind(Bindings.max(15.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(interpretationHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        });
    }


    public double getCommentPrefHeight() { return exerciseComment.getEditor().getPrefHeight();     }
    public void setCommentPrefHeight(double commentPrefHeight) {     this.commentPrefHeight = commentPrefHeight;   }

    public double getResponsePrefHeight() {    return exerciseResponse.getEditor().getPrefHeight();   }
    public void setResponsePrefHeight(double responsePrefHeight) {     this.responsePrefHeight = responsePrefHeight;  }
    public DecoratedRTA getExerciseResponse() {    return exerciseResponse;   }
    public void setExerciseResponse(DecoratedRTA exerciseResponse) {   this.exerciseResponse = exerciseResponse;  }
    public DecoratedRTA getExerciseInterpretation() {  return exerciseInterpretation;   }
    public void setExerciseInterpretation(DecoratedRTA exerciseInterpretation) {     this.exerciseInterpretation = exerciseInterpretation;  }
    public double getInterpretationPrefHeight() {   return exerciseInterpretation.getEditor().getPrefHeight();  }
    public void setInterpretationPrefHeight(double interpretationPrefHeight) {   this.interpretationPrefHeight = interpretationPrefHeight;   }



    @Override
    public DecoratedRTA getExerciseComment() { return exerciseComment; }
    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { this.exerciseComment = exerciseComment; }
    @Override
    public DecoratedRTA getExerciseStatement() { return exerciseStatement; }
    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) {  this.exerciseStatement = exerciseStatement;  }
    @Override
    public Node getExerciseStatementNode() {  return exerciseStatement.getEditor();  }
     public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        exerciseStatement.getEditor().setPrefHeight(height);
    }
    @Override
    public Node getExerciseContentNode() {     return new VBox(2, exerciseInterpretation.getEditor(), exerciseResponse.getEditor());  }
    @Override
    public Node getExerciseControl() { return exerciseControlNode; }
    @Override
    public Node getRightControl() { return null; }


}



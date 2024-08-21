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
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import slapp.editor.DiskUtilities;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.simple_trans.SimpleTransModel;
import slapp.editor.simple_trans.SimpleTransView;

import java.util.ArrayList;
import java.util.List;

public class SimpleTransExercise implements Exercise<SimpleTransModel, SimpleTransView> {

    private MainWindow mainWindow;
    private SimpleTransModel transModel;
    private SimpleTransView transView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;


    public SimpleTransExercise(SimpleTransModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.transModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        mainView = mainWindow.getMainView();
        transView = new SimpleTransView(mainView);

        setTransView();
    }

    private void setTransView() {

        transView.setExerciseName(transModel.getExerciseName());
        transView.setStatementPrefHeight(transModel.getStatementPrefHeight());
        transView.setCommentPrefHeight(transModel.getCommentPrefHeight());
        transView.setResponsePrefHeight(transModel.getResponsePrefHeight());
        transView.setInterpretationPrefHeight(transModel.getInterpretationPrefHeight());

        //statement
        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.getActionFactory().open(transModel.getExerciseStatement()).execute(new ActionEvent());

        mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        transView.setExerciseStatement(statementDRTA);

        //comment
        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();

        commentEditor.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            exerciseModified = true;
            double commentTextHeight = mainView.getRTATextHeight(commentEditor);
            transModel.setCommentTextHeight(commentTextHeight);
        });
        commentEditor.getActionFactory().open(transModel.getExerciseComment()).execute(new ActionEvent());

        mainView.editorInFocus(commentDRTA, ControlType.AREA);
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        transView.setExerciseComment(commentDRTA);

        //interpretation
        DecoratedRTA interpretationDRTA = new DecoratedRTA();
        RichTextArea interpretationEditor = interpretationDRTA.getEditor();

        interpretationEditor.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            exerciseModified = true;
            double interpretationTextHeight = mainView.getRTATextHeight(interpretationEditor);
            transModel.setInterpretationTextHeight(interpretationTextHeight);
        });



        interpretationEditor.getActionFactory().open(transModel.getExerciseInterpretation()).execute(new ActionEvent());

        mainView.editorInFocus(interpretationDRTA, ControlType.AREA);
        interpretationEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(interpretationDRTA, ControlType.AREA);
            }
        });
        transView.setExerciseInterpretation(interpretationDRTA);

        //response
        DecoratedRTA responseDRTA = new DecoratedRTA();
        RichTextArea responseEditor = responseDRTA.getEditor();

        responseEditor.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            exerciseModified = true;
            double responseTextHeight = mainView.getRTATextHeight(responseEditor);
            transModel.setResponseTextHeight(responseTextHeight);
        });
        responseEditor.getActionFactory().open(transModel.getExerciseResponse()).execute(new ActionEvent());

        mainView.editorInFocus(responseDRTA, ControlType.AREA);
        responseEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(responseDRTA, ControlType.AREA);
            }
        });
        transView.setExerciseResponse(responseDRTA);

        //cleanup
        transView.initializeViewDetails();
    }

    @Override
    public SimpleTransModel getExerciseModel() {
        return transModel;
    }
    @Override
    public SimpleTransView getExerciseView() {
        return transView;
    }
    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getSimpleTransModelFromView());
        if (success) exerciseModified = false;
    }

    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        SimpleTransModel printModel = transModel;
        SimpleTransExercise printExercise = this;
        double nodeWidth = PrintUtilities.getPageWidth() / mainWindow.getBaseScale();

        //header node
        Label exerciseName = new Label(printModel.getExerciseName());
        exerciseName.setStyle("-fx-font-weight: bold;");
        HBox hbox = new HBox(exerciseName);
        hbox.setPadding(new Insets(0,0,10,0));

        Group headerRoot = new Group();
        Scene headerScene = new Scene(headerRoot);
        headerRoot.getChildren().add(hbox);
        headerRoot.applyCss();
        headerRoot.layout();
        double boxHeight = hbox.getHeight();
        hbox.setPrefHeight(boxHeight);
        nodeList.add(hbox);
        Separator headerSeparator = new Separator(Orientation.HORIZONTAL);
        headerSeparator.setPrefWidth(nodeWidth);
        nodeList.add(headerSeparator);

        //statement node
        RichTextArea statementRTA = printExercise.getExerciseView().getExerciseStatement().getEditor();
        statementRTA.prefHeightProperty().unbind();
        statementRTA.minWidthProperty().unbind();
        double statementHeight = printModel.getStatementTextHeight();
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(nodeWidth);
        statementRTA.setMinWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setMinWidth(nodeWidth);
        statementSepBox.setAlignment(Pos.CENTER);
        nodeList.add(statementSepBox);

        //interpretation node
        RichTextArea interpretationRTA = printExercise.getExerciseView().getExerciseInterpretation().getEditor();
        interpretationRTA.prefHeightProperty().unbind();
        interpretationRTA.minWidthProperty().unbind();
        interpretationRTA.setPrefHeight(printModel.getInterpretationTextHeight() + 35.0);
        interpretationRTA.setContentAreaWidth(nodeWidth);
        interpretationRTA.setMinWidth(nodeWidth);
        interpretationRTA.getStylesheets().clear(); interpretationRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(interpretationRTA);

        Separator interpretationSeparator = new Separator(Orientation.HORIZONTAL);
        interpretationSeparator.setPrefWidth(100);
        HBox interpretationSepBox = new HBox(interpretationSeparator);
        interpretationSepBox.setMinWidth(nodeWidth);
        interpretationSepBox.setAlignment(Pos.CENTER);
        nodeList.add(interpretationSepBox);

        //response node
        RichTextArea responseRTA = printExercise.getExerciseView().getExerciseResponse().getEditor();
        responseRTA.prefHeightProperty().unbind();
        responseRTA.minWidthProperty().unbind();
        responseRTA.setPrefHeight(printModel.getResponseTextHeight() + 35.0);
        responseRTA.setContentAreaWidth(nodeWidth);
        responseRTA.setMinWidth(nodeWidth);
        responseRTA.getStylesheets().clear(); responseRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(responseRTA);

        Separator responseSeparator = new Separator(Orientation.HORIZONTAL);
        responseSeparator.setPrefWidth(100);
        HBox responseSepBox = new HBox(responseSeparator);
        responseSepBox.setMinWidth(nodeWidth);
        responseSepBox.setAlignment(Pos.CENTER);
        nodeList.add(responseSepBox);

        //comment node
        RichTextArea commentRTA = printExercise.getExerciseView().getExerciseComment().getEditor();
        commentRTA.prefHeightProperty().unbind();
        commentRTA.minWidthProperty().unbind();
        commentRTA.setPrefHeight(printModel.getCommentTextHeight() + 35.0);
        commentRTA.setContentAreaWidth(nodeWidth);
        commentRTA.setMinWidth(nodeWidth);
        commentRTA.getStylesheets().clear(); commentRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(commentRTA);

        return nodeList;
    }

    @Override
    public SimpleTransExercise resetExercise() {
        RichTextArea commentRTA = transView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        SimpleTransModel originalModel = (SimpleTransModel) (transModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        SimpleTransExercise clearExercise = new SimpleTransExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {
        RichTextArea commentEditor = transView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;
        RichTextArea responseEditor = transView.getExerciseResponse().getEditor();
        if (responseEditor.isModified()) exerciseModified = true;
        RichTextArea interpretationEditor = transView.getExerciseInterpretation().getEditor();
        if (interpretationEditor.isModified()) exerciseModified = true;

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) {
        this.exerciseModified = modified;
    }
    @Override
    public Node getFFViewNode() {   return null;}
    @Override
    public Node getFFPrintNode() { return null;   }

    @Override
    public ExerciseModel getExerciseModelFromView() {
        return (ExerciseModel) getSimpleTransModelFromView();
    }

    private SimpleTransModel getSimpleTransModelFromView() {
        SimpleTransModel model = new SimpleTransModel(transModel.getExerciseName());
        model.setOriginalModel(transModel.getOriginalModel());

        model.setExerciseStatement(transModel.getExerciseStatement());
        model.setStatementPrefHeight(transView.getExerciseStatement().getEditor().getPrefHeight());
        model.setStatementTextHeight(transModel.getStatementTextHeight());

        RichTextArea commentRTA = transView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        model.setExerciseComment(commentDocument);
        model.setCommentPrefHeight(transView.getCommentPrefHeight());
        model.setCommentTextHeight(transModel.getCommentTextHeight());

        RichTextArea responseRTA = transView.getExerciseResponse().getEditor();
        responseRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document responseDocument = responseRTA.getDocument();
        model.setExerciseResponse(responseDocument);
        model.setResponsePrefHeight(transView.getResponsePrefHeight());
        model.setResponseTextHeight(transModel.getResponseTextHeight());

        RichTextArea interpretationRTA = transView.getExerciseInterpretation().getEditor();
        interpretationRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document interpretationDocument = interpretationRTA.getDocument();
        model.setExerciseInterpretation(interpretationDocument);
        model.setInterpretationPrefHeight(transView.getInterpretationPrefHeight());
        model.setInterpretationTextHeight(transModel.getInterpretationTextHeight());


        boolean started = (transModel.isStarted() || exerciseModified);
        model.setStarted(started); transModel.setStarted(started);

        return model;
    }



}

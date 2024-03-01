package slapp.editor.vertical_tree;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;
import slapp.editor.vertical_tree.drag_drop.DragIconType;
import slapp.editor.vertical_tree.object_models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerticalTreeModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.VERTICAL_TREE;
    private boolean started = false;
    private double statementPrefHeight = 80;
    private Document exerciseStatement = new Document();
    private Document exerciseComment = new Document();

    private List<DragIconType> dragIconList = new ArrayList<>();
    private List<ObjectControlType> objectControlList = new ArrayList<>();
    private List<TreeFormulaBoxMod> treeFormulaBoxes = new ArrayList<>();
    private List<MapFormulaBoxMod> mapFormulaBoxes = new ArrayList<>();
    private List<VerticalBracketMod> verticalBrackets = new ArrayList<>();
    private List<DashedLineMod> dashedLineMods = new ArrayList<>();
    private List<ClickableNodeLinkMod> clickableNodeLinks = new ArrayList<>();
    private List<ClickableMapLinkMod> clickableMapLinks = new ArrayList<>();
    private List <MapQuestionMarkerMod> mapQuestionMarkers = new ArrayList<>();




    public VerticalTreeModel(){}

    public VerticalTreeModel(boolean test) {
        this();
        exerciseName = "VTexer";
        exerciseStatement = new Document("test exercise");
        exerciseComment = new Document("my comment");


        dragIconList.addAll(Arrays.asList(DragIconType.text_field, DragIconType.bracket, DragIconType.dashed_line, DragIconType.mapping_text_field));
        objectControlList.addAll(Arrays.asList(ObjectControlType.FORMULA_BOX, ObjectControlType.OPERATOR_CIRCLE, ObjectControlType.STAR, ObjectControlType.ANNOTATION, ObjectControlType.UNDERLINE, ObjectControlType.MAPPING));
    }




    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public void setExerciseStatement(Document exerciseStatement) {
        this.exerciseStatement = exerciseStatement;
    }

    public List<DragIconType> getDragIconList() {
        return dragIconList;
    }

    public void setDragIconList(List<DragIconType> dragIconList) {
        this.dragIconList = dragIconList;
    }

    public List<ObjectControlType> getObjectControlList() {
        return objectControlList;
    }

    public void setObjectControlList(List<ObjectControlType> objectControlList) {
        this.objectControlList = objectControlList;
    }

    public List<TreeFormulaBoxMod> getTreeFormulaBoxes() {
        return treeFormulaBoxes;
    }

    public void setTreeFormulaBoxes(List<TreeFormulaBoxMod> treeFormulaBoxes) {
        this.treeFormulaBoxes = treeFormulaBoxes;
    }

    public List<MapFormulaBoxMod> getMapFormulaBoxes() {
        return mapFormulaBoxes;
    }

    public void setMapFormulaBoxes(List<MapFormulaBoxMod> mapFormulaBoxes) {
        this.mapFormulaBoxes = mapFormulaBoxes;
    }

    public List<VerticalBracketMod> getVerticalBrackets() {
        return verticalBrackets;
    }

    public void setVerticalBrackets(List<VerticalBracketMod> verticalBrackets) {
        this.verticalBrackets = verticalBrackets;
    }

    public List<DashedLineMod> getDashedLineMods() {
        return dashedLineMods;
    }

    public void setDashedLineMods(List<DashedLineMod> dashedLineMods) {
        this.dashedLineMods = dashedLineMods;
    }

    public List<ClickableNodeLinkMod> getClickableNodeLinks() {
        return clickableNodeLinks;
    }

    public void setClickableNodeLinks(List<ClickableNodeLinkMod> clickableNodeLinks) {
        this.clickableNodeLinks = clickableNodeLinks;
    }

    public List<ClickableMapLinkMod> getClickableMapLinks() {
        return clickableMapLinks;
    }

    public void setClickableMapLinks(List<ClickableMapLinkMod> clickableMapLinks) {
        this.clickableMapLinks = clickableMapLinks;
    }

    public List<MapQuestionMarkerMod> getMapQuestionMarkers() {
        return mapQuestionMarkers;
    }

    public void setMapQuestionMarkers(List<MapQuestionMarkerMod> mapQuestionMarkers) {
        this.mapQuestionMarkers = mapQuestionMarkers;
    }

    @Override
    public String getExerciseName() {
        return exerciseName;
    }
    @Override
    public ExerciseType getExerciseType() { return exerciseType; }
    @Override
    public boolean isStarted() {
        return started;
    }
    @Override
    public void setStarted(boolean started) { }
    @Override
    public Document getExerciseComment() {
        return exerciseComment;
    }
    @Override
    public Document getExerciseStatement() {
        return exerciseStatement;
    }
    @Override
    public void setExerciseComment(Document document) { this.exerciseComment = document;   }
    @Override
    public double getStatementPrefHeight() {
        return statementPrefHeight;
    }
    @Override
    public void setStatementPrefHeight(double height) { this.statementPrefHeight = height; }

}

package slapp.editor.vert_tree_abexplain;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;
import slapp.editor.vertical_tree.drag_drop.DragIconType;
import slapp.editor.vertical_tree.object_models.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VerticalTreeABExpModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = new String("");
    private ExerciseType exerciseType = ExerciseType.VERTICAL_TREE;
    private ExerciseModel<Document> originalModel = null;
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
    private String choiceLead = new String("");
    private String aPrompt = new String("");
    private boolean aSelected = false;
    private String bPrompt = new String("");
    private boolean bSelected = false;
    private Document explainDocument = new Document();


    public VerticalTreeABExpModel(){}


    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName;    }

    public void setExerciseStatement(Document exerciseStatement) { this.exerciseStatement = exerciseStatement;    }

    public void setExplainDocument(Document explainDocument) {this.explainDocument = explainDocument; }

    public Document getExplainDocument() {  return explainDocument;  }

    public void setChoiceLead(String choiceLead) {
        this.choiceLead = choiceLead;
    }

    public void setaPrompt(String aPrompt) {
        this.aPrompt = aPrompt;
    }

    public void setaSelected(boolean aSelected) {
        this.aSelected = aSelected;
    }

    public void setbPrompt(String bPrompt) {
        this.bPrompt = bPrompt;
    }

    public void setbSelected(boolean bSelected) {
        this.bSelected = bSelected;
    }


    public List<DragIconType> getDragIconList() { return dragIconList;    }

    public void setDragIconList(List<DragIconType> dragIconList) { this.dragIconList = dragIconList;    }

    public List<ObjectControlType> getObjectControlList() {  return objectControlList;    }

    public void setObjectControlList(List<ObjectControlType> objectControlList) { this.objectControlList = objectControlList;    }

    public List<TreeFormulaBoxMod> getTreeFormulaBoxes() {  return treeFormulaBoxes;   }

    public List<MapFormulaBoxMod> getMapFormulaBoxes() {  return mapFormulaBoxes;    }

    public List<VerticalBracketMod> getVerticalBrackets() {  return verticalBrackets;    }

    public List<DashedLineMod> getDashedLineMods() {  return dashedLineMods;   }

    public List<ClickableNodeLinkMod> getClickableNodeLinks() {  return clickableNodeLinks;    }

    public List<ClickableMapLinkMod> getClickableMapLinks() {  return clickableMapLinks;   }

    public List<MapQuestionMarkerMod> getMapQuestionMarkers() {  return mapQuestionMarkers;    }
    public String getChoiceLead() {return choiceLead; }
    public String getaPrompt() { return aPrompt; }
    public boolean isaSelected() { return aSelected; }
    public String getbPrompt() { return bPrompt; }
    public boolean isbSelected() { return bSelected; }

    @Override
    public String getExerciseName() { return exerciseName;    }
    @Override
    public ExerciseType getExerciseType() { return exerciseType; }
    @Override
    public boolean isStarted() {  return started;    }
    @Override
    public void setStarted(boolean started) {this.started = started; }
    @Override
    public Document getExerciseComment() { return exerciseComment;    }
    @Override
    public Document getExerciseStatement() {  return exerciseStatement;    }
    @Override
    public void setExerciseComment(Document document) { this.exerciseComment = document;   }
    @Override
    public double getStatementPrefHeight() {    return statementPrefHeight;  }
    @Override
    public void setStatementPrefHeight(double height) { this.statementPrefHeight = height; }
    @Override
    public ExerciseModel<Document> getOriginalModel() {  return originalModel;  }
    public void setOriginalModel(ExerciseModel<Document> originalModel) {  this.originalModel = originalModel; }

}
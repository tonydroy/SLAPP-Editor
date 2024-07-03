package slapp.editor.vertical_tree;

import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import slapp.editor.PrintUtilities;
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
    private RichTextAreaSkin.KeyMapValue defaultKeyboardType;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;
    private Document exerciseStatement = new Document();
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document exerciseComment = new Document();
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private double mainPanePrefHeight = 350;
    private double mainPanePrefWidth = 100;



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


    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName;    }

    public void setExerciseStatement(Document exerciseStatement) { this.exerciseStatement = exerciseStatement;    }

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

    public RichTextAreaSkin.KeyMapValue getDefaultKeyboardType() {    return defaultKeyboardType;  }

    public void setDefaultKeyboardType(RichTextAreaSkin.KeyMapValue defaultKeyboardType) {   this.defaultKeyboardType = defaultKeyboardType;  }

    public double getCommentPrefHeight() {     return commentPrefHeight;  }

    public void setCommentPrefHeight(double commentPrefHeight) {      this.commentPrefHeight = commentPrefHeight;  }

    public double getMainPanePrefHeight() { return mainPanePrefHeight;  }

    public void setMainPanePrefHeight(double mainPanePrefHeight) {  this.mainPanePrefHeight = mainPanePrefHeight;  }

    public double getMainPanePrefWidth() {     return mainPanePrefWidth;  }

    public void setMainPanePrefWidth(double mainPanePrefWidth) {    this.mainPanePrefWidth = mainPanePrefWidth; }

    public double getStatementTextHeight() {  return statementTextHeight;  }

    public void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }

    public double getCommentTextHeight() {    return commentTextHeight; }

    public void setCommentTextHeight(double commentTextHeight) {    this.commentTextHeight = commentTextHeight;  }

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

    @Override
    public String toString() { return exerciseName; }

}

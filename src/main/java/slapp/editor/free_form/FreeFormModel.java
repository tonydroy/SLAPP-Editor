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

package slapp.editor.free_form;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.ExerciseType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FreeFormModel implements ExerciseModel<Document>, Serializable {

    private String exerciseName = "";
    private ExerciseType exerciseType = ExerciseType.FREE_FORM;
    private ExerciseModel<Document> originalModel = null;
    private boolean started = false;
    private Document statementDocument = new Document("");
    private double statementPrefHeight = 80;
    private double statementTextHeight = 0;
    private Document commentDocument = new Document("");
    private double commentPrefHeight = 60;
    private double commentTextHeight = 0;
    private List<ModelElement> modelElements = new ArrayList<>();
    private List<ElementTypes> elementTypes = new ArrayList<>();


    public FreeFormModel(String name, List<ElementTypes> types) {
        exerciseName = name;
        elementTypes = types;
    }


    public void setExerciseStatement(Document exerciseStatement) {  statementDocument = exerciseStatement;  }
    public double getStatementTextHeight() {
        return statementTextHeight;
    }
    public void setStatementTextHeight(double statementTextHeight) {    this.statementTextHeight = statementTextHeight;  }
    public double getCommentPrefHeight() {     return commentPrefHeight;  }
    public void setCommentPrefHeight(double commentPrefHeight) {    this.commentPrefHeight = commentPrefHeight;  }
    public double getCommentTextHeight() {
        return commentTextHeight;
    }
    public void setCommentTextHeight(double commentTextHeight) {
        this.commentTextHeight = commentTextHeight;
    }
    public List<ModelElement> getModelElements() {    return modelElements;  }
    public List<ElementTypes> getElementTypes() {     return elementTypes;  }

    public void setModelElements(List<ModelElement> modelElements) {    this.modelElements = modelElements; }

    @Override
    public String getExerciseName() {  return exerciseName;  }

    @Override
    public ExerciseType getExerciseType() {  return exerciseType;  }

    @Override
    public boolean isStarted() { return started;   }

    @Override
    public void setStarted(boolean started) { this.started = started;   }

    @Override
    public Document getExerciseComment() { return commentDocument;   }

    @Override
    public Document getExerciseStatement() { return statementDocument;  }

    @Override
    public void setExerciseComment(Document comment) { commentDocument = comment;  }

    @Override
    public double getStatementPrefHeight() { return statementPrefHeight;   }

    @Override
    public void setStatementPrefHeight(double height) { statementPrefHeight = height;    }

    @Override
    public ExerciseModel<Document> getOriginalModel() { return (ExerciseModel) originalModel;  }

    @Override
    public void setOriginalModel(ExerciseModel<Document> exerciseModel) { originalModel = exerciseModel;   }

    @Override
    public String toString() { return exerciseName;  }

}

package slapp.editor.free_form;

import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;

public class ModelElement implements Serializable {

    private ExerciseModel model = null;
    private int indentLevel = 0;

    public ModelElement(ExerciseModel model, int indentLevel) {
        this.model = model;
        this.indentLevel = indentLevel;
    }

    public ExerciseModel getModel() {      return model;  }

    public void setModel(ExerciseModel model) {     this.model = model;  }

    public int getIndentLevel() {     return indentLevel;  }

    public void setIndent(int indent) {     this.indentLevel = indent;  }

}

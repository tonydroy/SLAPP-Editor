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

ackage slapp.editor.free_form;

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

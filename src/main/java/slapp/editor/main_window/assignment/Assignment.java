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

package slapp.editor.main_window.assignment;

import com.gluonhq.richtextarea.model.Document;
import javafx.print.PageLayout;
import javafx.print.Paper;
import javafx.print.Printer;
import slapp.editor.PrintUtilities;
import slapp.editor.main_window.ExerciseModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Assignment implements Serializable {
    private AssignmentHeader header = new AssignmentHeader();
    private Document comment = new Document();
    private List<ExerciseModel> exerciseModels = new ArrayList<>();

    private double baseScale = 1.0;
    private boolean fitToPage = false;
    private PageLayoutValues pageLayoutValues;
    private transient PageLayout pageLayout;


    public Assignment(){
        this.pageLayout = PrintUtilities.getPageLayout();
    }

    private void writeObject (ObjectOutputStream stream) throws IOException, ClassNotFoundException {
        if (pageLayout != null)
            pageLayoutValues = new PageLayoutValues(pageLayout.getPageOrientation(), pageLayout.getLeftMargin(), pageLayout.getRightMargin(), pageLayout.getTopMargin(), pageLayout.getBottomMargin());
        stream.defaultWriteObject();
    }

    private void readObject (ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        Printer printer = PrintUtilities.getPrinter();
        if (printer != null && pageLayoutValues != null) {
            Paper paper = printer.getPrinterAttributes().getDefaultPaper();
            pageLayout = printer.createPageLayout(paper, pageLayoutValues.getOrientation(), pageLayoutValues.getLeftMargin(), pageLayoutValues.getRightMargin(), pageLayoutValues.getTopMargin(), pageLayoutValues.getBottomMargin());
        }
    }


    public ExerciseModel getExercise(int index) {
        return exerciseModels.get(index);
    }

    public void replaceExerciseModel(int index, ExerciseModel exerciseModel) { exerciseModels.set(index, exerciseModel); }
    public void addExerciseModel(int index, ExerciseModel exerciseModel) {
        exerciseModels.add(index, exerciseModel);
    }






    public boolean hasCompletedHeader() {
        return !getHeader().getStudentName().isEmpty();
    }

    public AssignmentHeader getHeader() { return header; }

    public void setHeader(AssignmentHeader header) { this.header = header; }

    public Document getComment() { return comment; }

    public void setComment(Document comment) { this.comment = comment; }

    public List<ExerciseModel> getExerciseModels() {
        return exerciseModels;
    }

    public void setExerciseModels(List<ExerciseModel> exerciseModels) {
        this.exerciseModels = exerciseModels;
    }

    public double getBaseScale() { return baseScale; }

    public void setBaseScale(double baseScale) { this.baseScale = baseScale; }

    public boolean isFitToPage() {    return fitToPage;  }

    public void setFitToPage(boolean fitToPage) {   this.fitToPage = fitToPage;  }

    public PageLayout getPageLayout() {     return pageLayout;  }

    public void setPageLayout(PageLayout pageLayout) {     this.pageLayout = pageLayout;  }
}

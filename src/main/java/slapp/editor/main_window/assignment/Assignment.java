package slapp.editor.main_window.assignment;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Assignment implements Serializable {

    private Header header = new Header();
    private Document comment = new Document();
    private List<ExerciseModel> exerciseModels = new ArrayList<>();

    public Assignment(){  }


    public ExerciseModel getExercise(int index) {
        return exerciseModels.get(index);
    }

    public void replaceExerciseModel(int index, ExerciseModel exerciseModel) { exerciseModels.set(index, exerciseModel); }
    public void addExerciseModel(int index, ExerciseModel exerciseModel) {
        exerciseModels.add(index, exerciseModel);
    }

    public void addComment(){}


    public void save() { System.out.println("save assignment"); }
    public void saveAs() { System.out.println("save assignment as"); }
    public void print(){ System.out.println("print assignment"); }
    public void exportToPdf() {System.out.println("export assignment to pdf"); }

    public boolean isStarted() {
        boolean isStarted = false;
        for (ExerciseModel mod : exerciseModels) {
            if (mod.isStarted()) isStarted = true;
        }
        return isStarted;
    }

    public Header getHeader() { return header; }

    public void setHeader(Header header) { this.header = header; }

    public Document getComment() { return comment; }

    public void setComment(Document comment) { this.comment = comment; }

    public List<ExerciseModel> getExerciseModels() {
        return exerciseModels;
    }

    public void setExerciseModels(List<ExerciseModel> exerciseModels) {
        this.exerciseModels = exerciseModels;
    }
}

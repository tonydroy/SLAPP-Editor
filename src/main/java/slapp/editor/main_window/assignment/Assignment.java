package slapp.editor.main_window.assignment;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.ExerciseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Assignment implements Serializable {

    private AssignmentHeader header = new AssignmentHeader();
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
}

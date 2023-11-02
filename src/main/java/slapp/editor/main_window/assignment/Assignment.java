package slapp.editor.main_window.assignment;

import com.gluonhq.richtextarea.model.Document;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;

import java.util.ArrayList;

public class Assignment {

    private Header header;
    private Document comment;
    private ArrayList<ExerciseModel> exerciseModels;


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
    public Header getHeader() { return header; }

    public void setHeader(Header header) { this.header = header; }

    public Document getComment() { return comment; }

    public void setComment(Document comment) { this.comment = comment; }
}

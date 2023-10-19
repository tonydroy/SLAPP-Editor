package slapp.editor.main_window;

import java.util.ArrayList;

public class Assignment {
    private String assignmentName = new String();
    ArrayList<Exercise> exerciseList;

    public Exercise getExercise(int index) {
        return exerciseList.get(index);
    }
    public void setExercise(int index, Exercise exercise) {
        exerciseList.add(index, exercise);
    }

    public String getAssignmentName(){
        return assignmentName;
    }
    public void print(){}

    public void save(){}

}

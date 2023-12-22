package slapp.editor.truth_table;

import javafx.scene.Node;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;

import java.util.List;

public class TruthTableExercise implements Exercise<TruthTableModel, TruthTableView> {

    @Override
    public TruthTableModel getExerciseModel() {
        return null;
    }

    @Override
    public TruthTableView getExerciseView() {
        return null;
    }

    @Override
    public void saveExercise(boolean saveAs) {

    }

    @Override
    public void printExercise() {

    }

    @Override
    public void exportExerciseToPDF() {

    }

    @Override
    public List<Node> getPrintNodes() {
        return null;
    }

    @Override
    public Exercise<TruthTableModel, TruthTableView> getContentClearExercise() {
        return null;
    }

    @Override
    public boolean isExerciseModified() {
        return false;
    }

    @Override
    public void setExerciseModified(boolean modified) {

    }

    @Override
    public void updateContentHeight(Node focusedNode, boolean isRequired) {

    }

    @Override
    public void updateCommentHeight(boolean isRequired) {

    }

    @Override
    public void updateStatementHeight(boolean isRequired) {

    }

    @Override
    public ExerciseModel<TruthTableModel> getExerciseModelFromView() {
        return null;
    }
}

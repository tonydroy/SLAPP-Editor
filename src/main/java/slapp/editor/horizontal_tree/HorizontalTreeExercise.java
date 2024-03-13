package slapp.editor.horizontal_tree;

import javafx.scene.Node;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;

import java.util.List;

public class HorizontalTreeExercise implements Exercise<HorizontalTreeModel, HorizontalTreeView> {

    @Override
    public HorizontalTreeModel getExerciseModel() {
        return null;
    }

    @Override
    public HorizontalTreeView getExerciseView() {
        return null;
    }

    @Override
    public void saveExercise(boolean saveAs) {

    }

    @Override
    public List<Node> getPrintNodes() {
        return null;
    }

    @Override
    public Exercise<HorizontalTreeModel, HorizontalTreeView> resetExercise() {
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
    public ExerciseModel<HorizontalTreeModel> getExerciseModelFromView() {
        return null;
    }
}

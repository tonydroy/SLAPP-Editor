package slapp.editor.vertical_tree;

import javafx.scene.Node;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.MainWindow;
import slapp.editor.main_window.MainWindowView;

import java.util.List;

public class VerticalTreeExercise implements Exercise<VerticalTreeModel, VerticalTreeView> {

    MainWindow mainWindow;
    MainWindowView mainView;
    VerticalTreeModel verticalTreeModel;
    VerticalTreeView verticalTreeView;



    public VerticalTreeExercise(VerticalTreeModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.verticalTreeModel = model;
        this.mainView = mainWindow.getMainView();
        this.verticalTreeView = new VerticalTreeView(mainView);
    }


    @Override
    public VerticalTreeModel getExerciseModel() {
        return null;
    }

    @Override
    public VerticalTreeView getExerciseView() {
        return verticalTreeView;
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
    public Exercise<VerticalTreeModel, VerticalTreeView> resetExercise() {
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
    public ExerciseModel<VerticalTreeModel> getExerciseModelFromView() {
        return null;
    }
}

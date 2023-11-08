package slapp.editor.front_page;

import javafx.scene.Node;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.MainWindow;
import java.util.List;

/**
 * Dummy exercise to fill the empty opening screen.
 */
public class FrontPageExercise implements Exercise<FrontPageModel, FrontPageView> {
    private FrontPageView view;
    private FrontPageModel model;
    private MainWindow mainWindow;

    public FrontPageExercise(MainWindow mainWindow) {
        this.view = new FrontPageView(mainWindow.getMainView());
        this.model = new FrontPageModel();
        this.mainWindow = mainWindow;
    }

    @Override
    public FrontPageModel getExerciseModel() {return model; }
    @Override
    public FrontPageView getExerciseView() { return view; }
    @Override
    public void saveExercise(boolean saveAs) { }
    @Override
    public void printExercise() { }
    @Override
    public void exportExerciseToPDF(){}
    @Override
    public List<Node> getPrintNodes() { return null; }
    @Override
    public Exercise<FrontPageModel, FrontPageView> getContentClearExercise() { return new FrontPageExercise(mainWindow); }
    @Override
    public boolean isExerciseModified() { return false; }
    @Override
    public void setExerciseModified(boolean modified) { }
    @Override
    public void updateContentHeight(boolean isRequired) { }
    @Override
    public void updateCommentHeight(boolean isRequired) { }
    @Override
    public void updateStatementHeight(boolean isRequired) {    }
    @Override
    public ExerciseModel<FrontPageModel, FrontPageView> getExerciseModelFromView() { return (ExerciseModel) new FrontPageModel(); }


}

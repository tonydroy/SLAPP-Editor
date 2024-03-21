package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import java.util.List;

public class HorizontalTreeExercise implements Exercise<HorizontalTreeModel, HorizontalTreeView> {

    private MainWindow mainWindow;
    private MainWindowView mainView;
    private HorizontalTreeModel horizontalTreeModel;
    private HorizontalTreeView horizontalTreeView;
    private boolean exerciseModified = false;

    public HorizontalTreeExercise(HorizontalTreeModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.horizontalTreeModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.horizontalTreeView = new HorizontalTreeView(mainView);

        setHorizontalTreeView();
    }

    private void setHorizontalTreeView() {
        horizontalTreeView.setExerciseName(horizontalTreeModel.getExerciseName());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(horizontalTreeModel.getExerciseStatement());
        horizontalTreeView.setStatementPrefHeight(horizontalTreeModel.getStatementPrefHeight());
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        horizontalTreeView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setPromptText("Comment: ");
        commentEditor.setDocument(horizontalTreeModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        horizontalTreeView.setExerciseComment(commentDRTA);

        DecoratedRTA explainDRTA = horizontalTreeView.getExplainDRTA();
        RichTextArea explainEditor = explainDRTA.getEditor();
        explainEditor.setDocument(horizontalTreeModel.getExplainDocument());
        explainEditor.setPromptText("Explain:");
        explainEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });

        horizontalTreeView.initializeViewDetails();
    }


    @Override
    public HorizontalTreeModel getExerciseModel() { return horizontalTreeModel;  }

    @Override
    public HorizontalTreeView getExerciseView() { return horizontalTreeView;  }

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
    public void setExerciseModified(boolean modified) { exerciseModified = modified;   }

    @Override
    public ExerciseModel<HorizontalTreeModel> getExerciseModelFromView() {
        return null;
    }
}

package slapp.editor.truth_table;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.MainWindow;

public class TruthTableCreate {
    private MainWindow mainWindow;
    private TextField nameField;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;

    private boolean fieldModified = false;



    public TruthTableCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public TruthTableCreate(MainWindow mainWindow, TruthTableModel originalModel) {
        this(mainWindow);

        nameField.setText(originalModel.getExerciseName());
        statementRTA.setDocument(originalModel.getExerciseStatement());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());



    }

    private void setupWindow(){

    }

}

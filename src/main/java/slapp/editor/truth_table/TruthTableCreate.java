package slapp.editor.truth_table;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.MainWindow;

public class TruthTableCreate {
    private MainWindow mainWindow;
    private TextField nameField;
    private RichTextArea statementRTA;
    private DecoratedRTA statementDRTA;
    private boolean fieldModified = false;
    private double scale = 1.0;
    private Scene scene;
    private Stage stage;
    private SimpleDoubleProperty centerHeightProperty;
    private TextArea helpArea;
    private VBox centerBox;
    private CheckBox conclusionDividerCheck;
    private CheckBox showChoiceAreaCheck;





    public TruthTableCreate(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupWindow();
    }

    public TruthTableCreate(MainWindow mainWindow, TruthTableModel originalModel) {
        this(mainWindow);

        nameField.setText(originalModel.getExerciseName());
        statementRTA.setDocument(originalModel.getExerciseStatement());
        statementRTA.getActionFactory().saveNow().execute(new ActionEvent());
        updateOperatorFieldsFromModel();
        updateMainFormulasFromModel();
        updateChoiceFieldsFromModel();
    }

    private void setupWindow(){

    }

   private void updateOperatorFieldsFromModel(){}
    private void updateMainFormulasFromModel(){}
    private void updateChoiceFieldsFromModel(){}

}

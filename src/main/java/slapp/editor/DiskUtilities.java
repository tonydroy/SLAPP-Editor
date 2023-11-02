package slapp.editor;

import javafx.stage.FileChooser;
import slapp.editor.main_window.assignment.Assignment;
import slapp.editor.main_window.ExerciseModel;

import java.io.*;

public class DiskUtilities {

    private static File exerciseDirectory = null;
    private static File assignmentDirectory = null;
    private static File userHomeFile = new File(System.getProperty("user.home"));


    public static void saveExercise(boolean saveAs, ExerciseModel exerciseModel) {
        File fileToSave;
        if (saveAs || exerciseDirectory == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SLAPP exercise (*.sle)", "*.sle"));
            if (exerciseDirectory != null)
                fileChooser.setInitialDirectory(exerciseDirectory);
            else
                fileChooser.setInitialDirectory(userHomeFile);
            fileChooser.setInitialFileName(exerciseModel.getExerciseName() + ".sle");
            fileToSave = fileChooser.showSaveDialog(EditorMain.mainStage);
            if (fileToSave == null)
                return;
        }
        else fileToSave = new File(exerciseDirectory, exerciseModel.getExerciseName() + ".sle");

        try {
            fileToSave.createNewFile();
        }
        catch (IOException e) {
            EditorAlerts.showSimpleAlert("Cannot Save", "No access to save " + fileToSave.getPath());
            return;
        }
        //legit fileToSave
        exerciseDirectory = fileToSave.getParentFile();

        try (FileOutputStream fs = new FileOutputStream(fileToSave, false); ObjectOutputStream os = new ObjectOutputStream(fs);) {
            os.writeObject(exerciseModel);
            EditorAlerts.fleetingPopup(fileToSave.getName() + " saved.");
        }
        catch (IOException e) {
//            e.printStackTrace();
            EditorAlerts.showSimpleAlert("Error Saving", e.getClass().getCanonicalName());
        }
    }

    public static void saveAssignment(boolean saveAs, Assignment assignment) {}

    public static Object openExerciseModelObject() {
        Object exerciseModelObject = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SLAPP exercise (*.sle)", "*.sle"));
        if (exerciseDirectory != null)
            fileChooser.setInitialDirectory(exerciseDirectory);
        else
            fileChooser.setInitialDirectory(userHomeFile);
        File fileToOpen = fileChooser.showOpenDialog(EditorMain.mainStage);
        if (fileToOpen != null) {
            exerciseDirectory = fileToOpen.getParentFile();
            try (FileInputStream fi = new FileInputStream(fileToOpen); ObjectInputStream oi = new ObjectInputStream(fi);) {
                exerciseModelObject = oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                EditorAlerts.showSimpleAlert("Error opening file", e.getClass().getCanonicalName());
            }
        }
        return exerciseModelObject;
    }




}

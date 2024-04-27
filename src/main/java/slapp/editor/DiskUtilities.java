package slapp.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import slapp.editor.main_window.assignment.Assignment;
import slapp.editor.main_window.ExerciseModel;

import java.io.*;
import java.util.Collections;

public class DiskUtilities {

    private static File exerciseDirectory = null;
    private static File assignmentDirectory = null;
    private static File userHomeFile = new File(System.getProperty("user.home"));


    public static boolean saveExercise(boolean saveAs, ExerciseModel exerciseModel) {
        boolean success = false;
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
                return success;
        }
        else fileToSave = new File(exerciseDirectory, exerciseModel.getExerciseName() + ".sle");

        try {
            fileToSave.createNewFile();
        }
        catch (IOException e) {
            EditorAlerts.showSimpleAlert("Cannot Save", "No access to save " + fileToSave.getPath());
            return success;
        }
        //legit fileToSave
        exerciseDirectory = fileToSave.getParentFile();

        try (FileOutputStream fs = new FileOutputStream(fileToSave, false); ObjectOutputStream os = new ObjectOutputStream(fs);) {
            os.writeObject(exerciseModel);

            String locationString = ".";
            if (fileToSave.getParent() != null) locationString = "\n\nin " + fileToSave.getParent() +".";
            EditorAlerts.fleetingPopup(fileToSave.getName() + " saved" + locationString);
            success = true;
        }
        catch (IOException e) {
//            e.printStackTrace();
            EditorAlerts.showSimpleAlert("Error Saving", e.getClass().getCanonicalName());
            return success;
        }
        return success;
    }

    public static boolean saveAssignment(boolean saveAs, Assignment assignment) {
        boolean success = false;
        File fileToSave;
        if (saveAs || assignmentDirectory == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SLAPP assignment (*.sla)", "*.sla"));
            if (assignmentDirectory != null)
                fileChooser.setInitialDirectory(assignmentDirectory);
            else
                fileChooser.setInitialDirectory(userHomeFile);
            fileChooser.setInitialFileName(assignment.getHeader().getAssignmentName() + ".sla");
            fileToSave = fileChooser.showSaveDialog(EditorMain.mainStage);
            if (fileToSave == null)
                return success;
        }
        else fileToSave = new File(assignmentDirectory, assignment.getHeader().getAssignmentName() + ".sla");

        try {
            fileToSave.createNewFile();
        }
        catch (IOException e) {
            EditorAlerts.showSimpleAlert("Cannot Save", "No access to save " + fileToSave.getPath());
            return success;
        }
        //legit fileToSave
        assignmentDirectory = fileToSave.getParentFile();

        try (FileOutputStream fs = new FileOutputStream(fileToSave, false); ObjectOutputStream os = new ObjectOutputStream(fs);) {
            os.writeObject(assignment);

            String locationString = ".";
            if (fileToSave.getParent() != null) locationString = "in\n\n" + fileToSave.getParent() +".";
            EditorAlerts.fleetingPopup(fileToSave.getName() + " saved" + locationString);
            success = true;
        }
        catch (IOException e) {
            e.printStackTrace();
            EditorAlerts.showSimpleAlert("Error Saving", e.getClass().getCanonicalName());
            return success;
        }
        return success;
    }


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
            } catch (IOException | ClassNotFoundException  e) {
//                e.printStackTrace();
                EditorAlerts.showSimpleAlert("Error opening file", fileToOpen.getName() + " is not compatible with this version of SLAPP.\nCannot open.");
            }
        }
        return exerciseModelObject;
    }

    public static Assignment openAssignment() {
        Assignment assignment = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SLAPP assignment (*.sla)", "*.sla"));
        if (assignmentDirectory != null)
            fileChooser.setInitialDirectory(assignmentDirectory);
        else
            fileChooser.setInitialDirectory(userHomeFile);
        File fileToOpen = fileChooser.showOpenDialog(EditorMain.mainStage);
        if (fileToOpen != null) {
            assignmentDirectory = fileToOpen.getParentFile();
            try (FileInputStream fi = new FileInputStream(fileToOpen); ObjectInputStream oi = new ObjectInputStream(fi);) {
                assignment = (Assignment) oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
                EditorAlerts.showSimpleAlert("Error opening file", fileToOpen.getName() + " is not compatible with this version of SLAPP.\nCannot open.");
            }
        }
        return assignment;
    }

    public static ExerciseModel getExerciseModelFromFile(File fileToOpen) {
        ExerciseModel exerciseModel = null;
        if (fileToOpen != null) {
            try (FileInputStream fi = new FileInputStream(fileToOpen); ObjectInputStream oi = new ObjectInputStream(fi);) {
                exerciseModel = (ExerciseModel) oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                EditorAlerts.showSimpleAlert("Error opening file", e.getClass().getCanonicalName());
            }
        }
        return exerciseModel;
    }

    public static File getDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(userHomeFile);
        File directory = directoryChooser.showDialog(EditorMain.mainStage);
        return directory;
    }


    public static ObservableList<File> getFileListFromDir(File directory, String extension) {
        ObservableList list = null;
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(extension);
            }
        };
        File[] files = directory.listFiles(filenameFilter);
        list = FXCollections.observableArrayList(files);

        Collections.sort(list, new AlphanumFileComparator());

        return list;
    }





}

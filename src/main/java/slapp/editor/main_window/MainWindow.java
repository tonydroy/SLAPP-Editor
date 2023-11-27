package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Callback;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;
import slapp.editor.PrintUtilities;
import slapp.editor.front_page.FrontPageExercise;
import slapp.editor.main_window.assignment.*;
import slapp.editor.simple_editor.SimpleEditExercise;
import slapp.editor.simple_editor.SimpleEditModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafx.scene.control.ButtonType.OK;


public class MainWindow {
    MainWindow mainWindow;
    MainWindowView mainView;
    Exercise currentExercise;
    Assignment currentAssignment = null;
    int assignmentIndex = 0;
    boolean isAssignmentContentModified = false;
    ChangeListener<Node> focusListener;
    Node lastFocusOwner;

    boolean isExerciseOpen = false;


    public MainWindow() {
        mainWindow = this;
        mainView = new MainWindowView(this);
        setupMainWindow();

        focusListener = (ob, ov, nv) ->  {
            if (nv != null) {
                if (nv.focusedProperty().get() == true) {
                    lastFocusOwner = ov;
                    updateNodeContainerHeight(nv, false);
                }
            }
        };
        setUpExercise(new FrontPageExercise(this));


        mainView.getMainScene().focusOwnerProperty().addListener(focusListener);
    }

    /* Comment:
    I do not understand how the focusOwnerProperty listener works.  In particular, a single listener responds to
    focus changes on buttons and such, but not to focus changes on the comment, statement or content.  With a
    second assignment of the same (!) listener as in setUpExercise below it fires twice but on all nodes -- a
    remove command prevents adding another fire each time the exercise is changed.  ??
     */

    private void setupMainWindow() {

        mainView.getCreateNewExerciseItem().setOnAction(e -> createNewExercise());
        mainView.getCreateRevisedExerciseItem().setOnAction(e -> createRevisedExercise());
        mainView.getSaveExerciseItem().setOnAction(e -> saveExercise(false));
        mainView.getSaveAsExerciseItem().setOnAction(e -> saveExercise(true));
        mainView.getOpenExerciseItem().setOnAction(e -> openExercise());
        mainView.getClearExerciseItem().setOnAction(e -> clearExercise());
        mainView.getCloseExerciseItem().setOnAction(e -> closeExercise());
        mainView.getPrintExerciseItem().setOnAction(e -> printExercise());
        mainView.getExportToPDFExerciseItem().setOnAction(e -> exportExerciseToPDF());

        mainView.getSaveAssignmentItem().setOnAction(e -> saveAssignment(false));
        mainView.getSaveAsAssignmentItem().setOnAction(e -> saveAssignment(true));
        mainView.getOpenAssignmentItem().setOnAction(e -> openAssignment());
        mainView.getCloseAssignmentItem().setOnAction(e -> closeAssignment());
        mainView.getPrintAssignmentItem().setOnAction(e -> printAssignment());
        mainView.getExportAssignmentToPDFItem().setOnAction(e -> exportAssignment());
        mainView.getCreateRevisedAssignmentItem().setOnAction(e -> createRevisedAssignment());
        mainView.getCreateNewAssignmentItem().setOnAction(e -> createNewAssignment());

        mainView.getPrintExerciseItemPM().setOnAction(e -> printExercise());
        mainView.getExportExerciseToPDFItemPM().setOnAction(e -> exportExerciseToPDF());
        mainView.getPrintAssignmentItemPM().setOnAction(e -> printAssignment());
        mainView.getExportAssignmentToPDFItemPM().setOnAction(e -> exportAssignment());
        mainView.getPageSetupItem().setOnAction(e -> pageSetup());
        mainView.getExportSetupItem().setOnAction(e -> exportSetup());

        mainView.getUpdateHeightButton().setOnAction(e -> {
            updateNodeContainerHeight(lastFocusOwner, true);
        });
        mainView.getPreviousExerciseMenu().onShownProperty().setValue(e -> {mainView.getPreviousExerciseMenu().hide(); previousExercise();});
        mainView.getNextExerciseMenu().onShownProperty().setValue(e -> {mainView.getNextExerciseMenu().hide(); nextExercise();});
        mainView.getGoToExerciseMenu().onShownProperty().setValue(e -> {mainView.getGoToExerciseMenu().hide(); goToExercise();});
        mainView.getAssignmentCommentMenu().onShownProperty().setValue(e -> {mainView.getAssignmentCommentMenu().hide(); assignmentComment();});
    }


    public void setUpExercise(Exercise exercise) {
        mainView.getMainScene().focusOwnerProperty().removeListener(focusListener);
        currentExercise = exercise;
        mainView.setupExercise();
        mainView.getSaveButton().setOnAction(e -> saveAction());
        mainView.getMainScene().focusOwnerProperty().get();
        mainView.getMainScene().focusOwnerProperty().addListener(focusListener);
    }


    private void createNewExercise() {
        if (checkContinueAssignment("Confirm Create", "This assignment appears to have been changed, and will be overwritten in the create process.\n\nContinue to create exercise?")) {
            if (checkContinueExercise("Confirm Create", "This exercise appears to have been changed, and will be overwritten by the new one.\n\nContinue to create exercise?")) {
                ExerciseType exerciseType = ExerciseTypePopup.getType();
                if (exerciseType != null) {
                    TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
                    typeFactories.createExerciseOfType(exerciseType);
                    isExerciseOpen = false;
                    currentAssignment = null;
                }
            }
        }
    }

    private void createRevisedExercise() {
        if (checkContinueAssignment("Confirm Create", "This assignment appears to have been changed, and will be overwritten in the create process.\n\nContinue to create exercise?")) {
            if (checkContinueExercise("Confirm Create", "This exercise appears to have been changed, and will be overwritten by the new one.\n\nContinue to create exercise?")) {
                Object exerciseModelObject = DiskUtilities.openExerciseModelObject();
                if (exerciseModelObject != null) {
                    TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
                    typeFactories.createRevisedExerciseFromModelObject(exerciseModelObject);
                    isExerciseOpen = false;
                    currentAssignment = null;
                }
            }
        }
    }

    public void saveAction(){
        if (currentAssignment != null) {
            if (!currentAssignment.getHeader().getAssignmentName().isEmpty()) {
                saveAssignment(false);
            }
        }
        else if (isExerciseOpen && currentExercise != null) {
            if (!((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
                currentExercise.saveExercise(false);
            }
        }
        else EditorAlerts.fleetingPopup("No named assignment or exercise to save.");
    }
    public void saveExercise(boolean saveAs) {
        if (currentExercise != null) {
            if (!((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
                currentExercise.saveExercise(saveAs);
            }
        }
        else EditorAlerts.fleetingPopup("No named exercise to save.");
    }
    private void clearExercise() {
        boolean okContinue = true;
        if (currentExercise.isExerciseModified()) {
            Alert confirm = EditorAlerts.confirmationAlert("Confirm Clear", "This exercise appears to have been modified.  Continue to clear content?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        if (okContinue) {
            Exercise clearExercise = currentExercise.getContentClearExercise();
            setUpExercise(clearExercise);
        }
    }

    private Exercise getEmptyExercise() {
        //revert to empty simple edit exercise
        SimpleEditModel emptyModel = new SimpleEditModel("",false,"",80,new Document(), new Document(), new ArrayList<>());
        SimpleEditExercise emptyExercise = new SimpleEditExercise(emptyModel, mainWindow);
        return emptyExercise;
    }

    public void closeExercise() {
        if (currentAssignment == null) {
            if (checkContinueExercise("Confirm Close", "This exercise appears to have been changed.\n\nContinue to close exercise?")) {

                setUpExercise(getEmptyExercise());
                isExerciseOpen = false;
            }
        } else {
            EditorAlerts.showSimpleAlert("Cannot Close", "There is an open assignment.  Closing the assignment closes its member exercises.");
        }
    }

    private void openExercise(){

        if (checkContinueAssignment("Confirm Open", "This assignment appears to have been changed, and will be overwritten by the new exercise.  Continue to open exercise?")) {
            if (checkContinueExercise("Confirm Open", "This exercise appears to have been changed, and will be overwritten by the new one.  Continue to open exercise?")) {
                Object exerciseModelObject = DiskUtilities.openExerciseModelObject();
                if (exerciseModelObject != null) {
                    TypeSelectorFactories typeFactories = new TypeSelectorFactories(this);
                    Exercise exercise = typeFactories.getExerciseFromModelObject(exerciseModelObject);
                    if (exercise != null) {
                        setUpExercise(exercise);
                        isExerciseOpen = true;
                        currentAssignment = null;
                    }
                }
            }
        }
    }

    private void exportExerciseToPDF() {
        if (currentExercise != null) {
            if (!((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
                currentExercise.exportExerciseToPDF();
            }
        }
        else EditorAlerts.fleetingPopup("Cannot find exercise to export.");
    }

    private void printExercise() {
        if (currentExercise != null) {
            if (!((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
                currentExercise.printExercise();
            }
        }
        else EditorAlerts.fleetingPopup("Cannot find exercise to print.");
    }

    private boolean checkContinueExercise(String title, String content) {
        boolean okContinue = true;
        if (isExerciseOpen && currentExercise.isExerciseModified()) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        return okContinue;
    }

    private boolean checkContinueAssignment(String title, String content) {
        boolean okContinue = true;
        if (currentAssignment != null && (currentExercise.isExerciseModified() || isAssignmentContentModified)) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        return okContinue;
    }

    private void updateNodeContainerHeight(Node element, boolean isRequired) {
        if (isContainer(mainView.getContentNode(), element)) currentExercise.updateContentHeight(isRequired);
        else if (isContainer(mainView.getCommentNode(), element)) currentExercise.updateCommentHeight(isRequired);
        else if (isContainer(mainView.getStatementNode(), element)) currentExercise.updateStatementHeight(isRequired);
    }

    private boolean isContainer(Node container, Node element) {
        if (element == null)
            return false;
        Node current = element;
        while (current != null) {
            if (current == container)
                return true;
            current = current.getParent();
        }
        return false;
    }

    private void exportSetup() { PrintUtilities.exportSetup(); }

    private void pageSetup() {
        PrintUtilities.updatePageLayout();
        mainView.setupExercise();
        mainView.updateZoom(mainView.getZoomSpinner().getValue());
        mainView.updatePageHeightLabel(PrintUtilities.getPageHeight());
    }


    public void assignmentComment() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment on which to comment.");
        } else {
            AssignmentCommentWindow commentWindow = new AssignmentCommentWindow(currentAssignment.getHeader());
            AssignmentHeader header = commentWindow.getHeaderComment();
            if (!(header.getComment().equals(currentAssignment.getComment()))) isAssignmentContentModified = true;
            currentAssignment.setHeader(header);
        }
    }
    public void saveAssignment(boolean saveAs){
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to save.");
        } else {
            if (currentExercise.isExerciseModified()) {
                ExerciseModel model = currentExercise.getExerciseModelFromView();
                currentAssignment.replaceExerciseModel(assignmentIndex, model);
                currentExercise.setExerciseModified(false);
            }
            boolean saved = DiskUtilities.saveAssignment(saveAs, currentAssignment);
            if (saved) isAssignmentContentModified = false;
        }
    }

    public void openAssignment(){

        if (checkContinueAssignment("Confirm Open", "The current assignment appears to have been changed, and will be overwritten by the new one.\n\nContinue to open assignment?")) {
            if (checkContinueExercise("Confirm Open", "The current exercise appears to have been changed, and will be overwritten by the new assignment.\n\nContinue to open assignment?")) {
                isExerciseOpen = false;
                Assignment assignment = DiskUtilities.openAssignment();
                if (assignment != null) {
                    if (!assignment.hasCompletedHeader()) {
                        UpdateAssignmentHeader headerUpdater = new UpdateAssignmentHeader(assignment.getHeader());
                        assignment.setHeader(headerUpdater.updateHeader());
                    }
                    if (assignment.hasCompletedHeader()) {
                        currentAssignment = assignment;
                        TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);
                        assignmentIndex = 0;
                        currentExercise = typeFactory.getExerciseFromModelObject(currentAssignment.getExerciseModels().get(assignmentIndex));
                        mainView.setupExercise();
                        mainView.setUpLowerAssignmentBar();
                        isAssignmentContentModified = false;
                    }
                }
            }
        }
    }
    public void closeAssignment() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to close.");
        } else {
            if (checkContinueAssignment("Confirm Close", "This assignment appears to have been changed.\n\nContinue to close assignment?")) {

                setUpExercise(getEmptyExercise());
                isExerciseOpen = false;
                currentAssignment = null;
                isAssignmentContentModified = false;
            }
        }
    }
    public void printAssignment() {

        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to print.");
        } else {
            List<Node> printNodes = new ArrayList<>();
            printNodes.add(mainView.getAssignmentHeader());

            if (currentExercise.isExerciseModified()) isAssignmentContentModified = true;
            ExerciseModel currentModel = currentExercise.getExerciseModelFromView();
            currentAssignment.replaceExerciseModel(assignmentIndex, currentModel);

            List<ExerciseModel> exerciseModelList = currentAssignment.getExerciseModels();
            for (int i = 0; i < exerciseModelList.size(); i++) {
                ExerciseModel model = exerciseModelList.get(i);
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);
                Exercise exercise = typeFactory.getExerciseFromModelObject(model);
                List<Node> exerciseNodes = exercise.getPrintNodes();
                if (PrintUtilities.checkExerciseNodeHeights(exerciseNodes, model.getExerciseName())) {
                    printNodes.addAll(exerciseNodes);
                } else return;
            }
            String infoString = currentAssignment.getHeader().getCreationID() + "-" + currentAssignment.getHeader().getWorkingID();
            PrintUtilities.printAssignment(printNodes, infoString);
        }
    }



    public void exportAssignment() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to print.");
        } else {
            List<Node> printNodes = new ArrayList<>();
            printNodes.add(mainView.getAssignmentHeader());

            if (currentExercise.isExerciseModified()) isAssignmentContentModified = true;
            ExerciseModel currentModel = currentExercise.getExerciseModelFromView();
            currentAssignment.replaceExerciseModel(assignmentIndex, currentModel);

            List<ExerciseModel> exerciseModelList = currentAssignment.getExerciseModels();
            for (int i = 0; i < exerciseModelList.size(); i++) {
                ExerciseModel model = exerciseModelList.get(i);
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(mainWindow);
                Exercise exercise = typeFactory.getExerciseFromModelObject(model);
                List<Node> exerciseNodes = exercise.getPrintNodes();
                if (PrintUtilities.checkExerciseNodeHeights(exerciseNodes, model.getExerciseName())) {
                    printNodes.addAll(exerciseNodes);
                } else return;
            }
            String infoString = currentAssignment.getHeader().getCreationID() + "-" + currentAssignment.getHeader().getWorkingID();
            PrintUtilities.exportAssignmentToPDF (printNodes, infoString);
        }
    }
    public void createRevisedAssignment() {
        if (checkContinueAssignment("Confirm Create", "The current assignment appears to have been changed, and will be overwritten in the creation process.\n\nContinue to create assignment?")) {
 //           currentAssignment = null;
            if (checkContinueExercise("Confirm Create", "The current exercise appears to have been changed, and will be overwritten in the creation process.\n\nContinue to create assignment?")) {
                isExerciseOpen = false;

                Assignment assignment = DiskUtilities.openAssignment();
                if (assignment != null) {
                    if (!assignment.hasCompletedHeader()) {
                        currentAssignment = null;
                        new CreateAssignment(assignment, this);
                    } else {
                        EditorAlerts.showSimpleAlert("Cannot Modify", "This assignment appears to have been started.  Cannot open in create window.");
                    }
                }
            }
        }
    }
    public void createNewAssignment(){
        if (checkContinueAssignment("Confirm Create", "The current assignment appears to have been changed, and will be overwritten in the creation process.\n\nContinue to create assignment?")) {
            currentAssignment = null;
            if (checkContinueExercise("Confirm Create", "The current exercise appears to have been changed, and will be overwritten in the creation process.\n\n Continue to create assignment?")) {
                isExerciseOpen = false;
                new CreateAssignment(new Assignment(), this);
            }
        }
    }

    public void previousExercise() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("Cannot Advance.  There is no open assignment.");
        } else {
            if (currentExercise.isExerciseModified()) isAssignmentContentModified = true;
            int prevIndex = assignmentIndex - 1;
            if (prevIndex >= 0) {
                ExerciseModel model = currentExercise.getExerciseModelFromView();
                currentAssignment.replaceExerciseModel(assignmentIndex, model);

                assignmentIndex = prevIndex;
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);
                currentExercise = typeFactory.getExerciseFromModelObject(currentAssignment.getExerciseModels().get(assignmentIndex));
                mainView.setupExercise();
                mainView.setUpLowerAssignmentBar();
            }
        }
    }
    public void nextExercise() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("Cannot Advance.  There is no open assignment.");
        } else {
            if (currentExercise.isExerciseModified()) isAssignmentContentModified = true;
            int nextIndex = assignmentIndex + 1;
            if (nextIndex < currentAssignment.getExerciseModels().size()) {
                ExerciseModel model = currentExercise.getExerciseModelFromView();
                currentAssignment.replaceExerciseModel(assignmentIndex, model);

                assignmentIndex = nextIndex;
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);
                currentExercise = typeFactory.getExerciseFromModelObject(currentAssignment.getExerciseModels().get(assignmentIndex));
                mainView.setupExercise();
                mainView.setUpLowerAssignmentBar();
            }
        }
     }
    public void goToExercise() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("Cannot Jump.  There is no open assignment.");
        } else {
            if (currentExercise.isExerciseModified()) isAssignmentContentModified = true;

            Popup exercisePopup = new Popup();
            ListView exerciseList = new ListView();
            exerciseList.setPadding(new Insets(5));
            exerciseList.setStyle("-fx-background-color: gainsboro");
            exerciseList.setCellFactory(new Callback<ListView<ExerciseModel>, ListCell<ExerciseModel>>() {
                public ListCell<ExerciseModel> call(ListView<ExerciseModel> param) {
                    return new ListCell<ExerciseModel>() {
                        @Override
                        protected void updateItem(ExerciseModel item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(item == null || empty ? null : (exerciseList.getItems().indexOf(item) + 1) + ".  " + item);
                        }
                    };
                }
            });
            exerciseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ExerciseModel>() {
                @Override
                public void changed(ObservableValue ob, ExerciseModel ov, ExerciseModel nv) {
                    if (nv != null) {
                        ExerciseModel model = currentExercise.getExerciseModelFromView();
                        currentAssignment.replaceExerciseModel(assignmentIndex, model);

                        assignmentIndex = exerciseList.getItems().indexOf(nv);
                        TypeSelectorFactories typeFactory = new TypeSelectorFactories(mainWindow);
                        currentExercise = typeFactory.getExerciseFromModelObject(currentAssignment.getExerciseModels().get(assignmentIndex));
                        mainView.setupExercise();
                        mainView.setUpLowerAssignmentBar();
                        exercisePopup.hide();
                    }
                }
            });

            exerciseList.getItems().addAll(currentAssignment.getExerciseModels());
            exercisePopup.getContent().add(exerciseList);
            exercisePopup.show(EditorMain.mainStage);
        }
    }

    public boolean checkCloseWindow() {
        boolean continueClose = false;
        if (checkContinueAssignment("Confirm Close", "The current assignment appears to have been changed.\n\nContinue to close?")) {
            if (checkContinueExercise("Confirm Close", "The current exercise appears to have been changed.\n\nContinue to close exercise?")) {
                continueClose = true;
            }
        }
        return continueClose;
    }

    public MainWindowView getMainView() { return mainView; }
    public Assignment getCurrentAssignment() { return currentAssignment; }

    public int getAssignmentIndex() {
        return assignmentIndex;
    }
}

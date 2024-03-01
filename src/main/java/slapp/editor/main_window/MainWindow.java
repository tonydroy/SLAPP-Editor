package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
    boolean assignmentContentModified = false;
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
 //                   updateNodeContainerHeight(nv, false);
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
        mainView.getClearExerciseItem().setOnAction(e -> resetExercise());
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

        mainView.getCommonElementsTextItem().setOnAction(e -> generalTextHelp());
        mainView.getAboutItem().setOnAction(e -> aboutTextHelp());
        mainView.getContextualTextItem().setOnAction(e -> contextualTextHelp());


        Label previousExerciseLabel = new Label("Previous");
        previousExerciseLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                previousExercise();
            }
        });
        mainView.getPreviousExerciseMenu().setGraphic(previousExerciseLabel);


        Label nextExerciseLabel = new Label("Next");
        nextExerciseLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                nextExercise();
            }
        });
        mainView.getNextExerciseMenu().setGraphic(nextExerciseLabel);


        Label goToExerciseLabel = new Label("Jump");
        goToExerciseLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                goToExercise();
            }
        });
        mainView.getGoToExerciseMenu().setGraphic(goToExerciseLabel);


        Label assignmentCommentLabel = new Label("Comment");
        assignmentCommentLabel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                assignmentComment();
            }
        });
        mainView.getAssignmentCommentMenu().setGraphic(assignmentCommentLabel);


    }


    public void setUpExercise(Exercise exercise) {
        if (currentExercise != null) {
            mainView.contentHeightProperty().unbind();
            mainView.contentWidthProperty().unbind();
            mainView.contentHeightProperty().removeListener(mainView.getVerticalListener());
            mainView.contentWidthProperty().removeListener(mainView.getHorizontalListener());
        }


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
    private void resetExercise() {
        boolean okContinue = true;
        if (currentExercise.isExerciseModified()) {
            Alert confirm = EditorAlerts.confirmationAlert("Confirm Reset", "This exercise appears to have been modified.  Continue to reset content?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        if (okContinue) {
            Exercise clearExercise = currentExercise.resetExercise();
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
        if (currentExercise != null && !((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
            boolean heightGood = true;
            List<Node> printNodes = currentExercise.getPrintNodes();
            PrintUtilities.resetPrintBuffer();
            for (Node node : printNodes) {
                if (!PrintUtilities.processPrintNode(node) && !mainView.isFitToPageSelected()) {
                    heightGood = false;
                }
            }
            if (!heightGood && !mainView.isFitToPageSelected()) {
                String message = "Fit page not selected and exercise " + ((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName() + "includes at least on block that takes up more than a page.  Content exceeding page bounds will be cropped.\n\n Continue export?";
                Alert confirm = EditorAlerts.confirmationAlert("Page Problem:", message);
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == OK) heightGood = true;
            }
            if (heightGood) {
                if (!mainView.isFitToPageSelected()) PrintUtilities.resetScale();
                PrintUtilities.sendBufferToPDF(null);
            }
        }
        else EditorAlerts.fleetingPopup("Cannot find exercise to export.");

    }

    private void printExercise() {
        if (currentExercise != null && !((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName().isEmpty()) {
            boolean heightGood = true;
            List<Node> printNodes = currentExercise.getPrintNodes();
            PrintUtilities.resetPrintBuffer();
            for (Node node : printNodes) {
                if (!PrintUtilities.processPrintNode(node) && !mainView.isFitToPageSelected()) {
                    heightGood = false;
                }
            }
            if (!heightGood && !mainView.isFitToPageSelected()) {
                String message = "Fit page not selected and exercise " + ((ExerciseModel) currentExercise.getExerciseModel()).getExerciseName() + " includes at least one block that takes up more than a page.  Content exceeding page bounds will be cropped.\n\n Continue to print?";
                Alert confirm = EditorAlerts.confirmationAlert("Page Problem:", message);
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == OK) heightGood = true;
            }
            if (heightGood) {
                if (!mainView.isFitToPageSelected()) PrintUtilities.resetScale();
                PrintUtilities.sendBufferToPrint(null);
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
        if (currentAssignment != null && (currentExercise.isExerciseModified() || assignmentContentModified)) {
            Alert confirm = EditorAlerts.confirmationAlert(title, content);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() != OK) okContinue = false;
        }
        return okContinue;
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
        if (currentExercise != null) {
            mainView.contentHeightProperty().unbind();
            mainView.contentWidthProperty().unbind();
            mainView.contentHeightProperty().removeListener(mainView.getVerticalListener());
            mainView.contentWidthProperty().removeListener(mainView.getHorizontalListener());
        }
        mainView.updateContentHeightProperty();
        mainView.updateContentWidthProperty();

 //       mainView.updateZoom(mainView.getZoomSpinner().getValue());

    }


    public void assignmentComment() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment on which to comment.");
        } else {
            AssignmentCommentWindow commentWindow = new AssignmentCommentWindow(currentAssignment.getHeader());
            AssignmentHeader header = commentWindow.getHeaderComment();
            if (!(header.getComment().equals(currentAssignment.getComment()))) assignmentContentModified = true;
            currentAssignment.setHeader(header);
        }
    }
    public void saveAssignment(boolean saveAs){
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to save.");
        } else {
            if (currentExercise.isExerciseModified()) {
                assignmentContentModified = true;
                ExerciseModel model = currentExercise.getExerciseModelFromView();
                currentAssignment.replaceExerciseModel(assignmentIndex, model);
                currentExercise.setExerciseModified(false);
            }
            boolean saved = DiskUtilities.saveAssignment(saveAs, currentAssignment);

            if (saved) assignmentContentModified = false;
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

                        if (currentExercise != null) {
                            mainView.contentHeightProperty().unbind();
                            mainView.contentWidthProperty().unbind();
                            mainView.contentHeightProperty().removeListener(mainView.getVerticalListener());
                            mainView.contentWidthProperty().removeListener(mainView.getHorizontalListener());
                        }

                        currentExercise = typeFactory.getExerciseFromModelObject(currentAssignment.getExerciseModels().get(assignmentIndex));
                        mainView.setupExercise();
                        mainView.setUpLowerAssignmentBar();
                        assignmentContentModified = false;
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
                assignmentContentModified = false;
            }
        }
    }
    public void printAssignment() {

        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to print.");
        } else {
            boolean heightGood = true;
            List<String> badExerciseList = new ArrayList<>();
            PrintUtilities.resetPrintBuffer();
            PrintUtilities.setTopBox(mainView.getAssignmentHeader());

            if (currentExercise.isExerciseModified()) assignmentContentModified = true;
            ExerciseModel currentModel = currentExercise.getExerciseModelFromView();
            currentAssignment.replaceExerciseModel(assignmentIndex, currentModel);

            List<ExerciseModel> exerciseModelList = currentAssignment.getExerciseModels();
            for (int i = 0; i < exerciseModelList.size(); i++) {
                ExerciseModel model = exerciseModelList.get(i);
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);
                Exercise exercise = typeFactory.getExerciseFromModelObject(model);
                List<Node> exerciseNodes = exercise.getPrintNodes();

                for (Node node : exerciseNodes) {
                    if (!PrintUtilities.processPrintNode(node) && !mainView.isFitToPageSelected()) {
                        heightGood = false;
                        badExerciseList.add(((ExerciseModel) exercise.getExerciseModel()).getExerciseName());
                    }
                }
            }

            if (!badExerciseList.isEmpty()) {
                StringBuilder sb = new StringBuilder(badExerciseList.get(0));
                for (int i = 1; i < badExerciseList.size(); i++) {
                    sb.append(", ");
                    sb.append(badExerciseList.get(i));
                }
                String message = "Fit page not selected and " + sb.toString() + " include(s) at least one block that takes up more than a page.  Content exceeding page bounds will be cropped.\n\n Continue to print?";
                Alert confirm = EditorAlerts.confirmationAlert("Page Problem:", message);
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == OK) heightGood = true;
            }

            if (heightGood) {
                if (!mainView.isFitToPageSelected()) PrintUtilities.resetScale();
                String infoString = currentAssignment.getHeader().getCreationID() + "-" + currentAssignment.getHeader().getWorkingID();
                PrintUtilities.sendBufferToPrint(infoString);
            }
        }
    }



    public void exportAssignment() {
        if (currentAssignment == null) {
            EditorAlerts.fleetingPopup("There is no open assignment to export.");
        } else {
            boolean heightGood = true;
            List<String> badExerciseList = new ArrayList<>();
            PrintUtilities.resetPrintBuffer();
            PrintUtilities.setTopBox(mainView.getAssignmentHeader());


            if (currentExercise.isExerciseModified()) assignmentContentModified = true;
            ExerciseModel currentModel = currentExercise.getExerciseModelFromView();
            currentAssignment.replaceExerciseModel(assignmentIndex, currentModel);

            List<ExerciseModel> exerciseModelList = currentAssignment.getExerciseModels();
            for (int i = 0; i < exerciseModelList.size(); i++) {
                ExerciseModel model = exerciseModelList.get(i);
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);
                Exercise exercise = typeFactory.getExerciseFromModelObject(model);
                List<Node> exerciseNodes = exercise.getPrintNodes();

                for (Node node : exerciseNodes) {
                    if (!PrintUtilities.processPrintNode(node) && !mainView.isFitToPageSelected()) {
                        heightGood = false;
                        badExerciseList.add(((ExerciseModel) exercise.getExerciseModel()).getExerciseName());
                    }
                }
            }

            if (!badExerciseList.isEmpty()) {
                StringBuilder sb = new StringBuilder(badExerciseList.get(0));
                for (int i = 1; i < badExerciseList.size(); i++) {
                    sb.append(", ");
                    sb.append(badExerciseList.get(i));
                }
                String message = "Fit page not selected and " + sb.toString() + " include(s) at least one block that takes up more than a page.  Content exceeding page bounds will be cropped.\n\n Continue export?";
                Alert confirm = EditorAlerts.confirmationAlert("Page Problem:", message);
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == OK) heightGood = true;
            }

            if (heightGood) {
                if (!mainView.isFitToPageSelected()) PrintUtilities.resetScale();
                String infoString = currentAssignment.getHeader().getCreationID() + "-" + currentAssignment.getHeader().getWorkingID();
                PrintUtilities.sendBufferToPDF(infoString);
            }
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
            if (currentExercise.isExerciseModified()) assignmentContentModified = true;
            int prevIndex = assignmentIndex - 1;
            if (prevIndex >= 0) {
                ExerciseModel model = currentExercise.getExerciseModelFromView();
                currentAssignment.replaceExerciseModel(assignmentIndex, model);

                assignmentIndex = prevIndex;
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);

                if (currentExercise != null) {
                    mainView.contentHeightProperty().unbind();
                    mainView.contentWidthProperty().unbind();
                    mainView.contentHeightProperty().removeListener(mainView.getVerticalListener());
                    mainView.contentWidthProperty().removeListener(mainView.getHorizontalListener());
                }

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
            if (currentExercise.isExerciseModified()) {assignmentContentModified = true; System.out.println("current: " + currentExercise.isExerciseModified() + " assignment: " + assignmentContentModified);}
            int nextIndex = assignmentIndex + 1;
            if (nextIndex < currentAssignment.getExerciseModels().size()) {
                ExerciseModel model = currentExercise.getExerciseModelFromView();
                currentAssignment.replaceExerciseModel(assignmentIndex, model);

                assignmentIndex = nextIndex;
                TypeSelectorFactories typeFactory = new TypeSelectorFactories(this);

                if (currentExercise != null) {
                    mainView.contentWidthProperty().unbind();
                    mainView.contentWidthProperty().unbind();
                    mainView.contentHeightProperty().removeListener(mainView.getVerticalListener());
                    mainView.contentWidthProperty().removeListener(mainView.getHorizontalListener());
                }

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
            if (currentExercise.isExerciseModified()) assignmentContentModified = true;

            Popup exercisePopup = new Popup();
            ListView exerciseList = new ListView();
            exerciseList.setPadding(new Insets(5));
            exerciseList.setStyle("-fx-border-color: white; -fx-background-color: white; -fx-border-width: 0");

            exerciseList.getItems().addAll(currentAssignment.getExerciseModels());

            exerciseList.getSelectionModel().select(assignmentIndex);

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

                        if (currentExercise != null) {
                            mainView.contentHeightProperty().unbind();
                            mainView.contentWidthProperty().unbind();
                            mainView.contentHeightProperty().removeListener(mainView.getVerticalListener());
                            mainView.contentWidthProperty().removeListener(mainView.getHorizontalListener());
                        }

                        currentExercise = typeFactory.getExerciseFromModelObject(currentAssignment.getExerciseModels().get(assignmentIndex));
                        mainView.setupExercise();
                        mainView.setUpLowerAssignmentBar();
                        exercisePopup.hide();
                    }
                }
            });



            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> exercisePopup.hide());

            HBox buttonBox = new HBox(closeButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setStyle("-fx-background-color: white; -fx-border-width: 1 0 0 0; -fx-border-color: lightblue;");


            VBox jumpBox = new VBox(0, exerciseList, buttonBox);


            exercisePopup.getContent().add(jumpBox);

            jumpBox.setStyle("-fx-border-color: lightblue; -fx-border-width: 5; -fx-opacity: 1.0");



//            exercisePopup.getContent().addAll(exerciseList, closeButton);
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

    private void aboutTextHelp() {
        TextHelpPopup.helpAbout();
    }
    private void generalTextHelp() {
        TextHelpPopup.helpCommonElements();
    }
    private void contextualTextHelp() {
        TextHelpPopup.helpContextual(((ExerciseModel) (currentExercise.getExerciseModel())).getExerciseType());
    }


    public MainWindowView getMainView() { return mainView; }
    public Assignment getCurrentAssignment() { return currentAssignment; }

    public int getAssignmentIndex() {
        return assignmentIndex;
    }

    public Node getLastFocusOwner() {
        return lastFocusOwner;
    }

    public void setLastFocusOwner(Node lastFocusOwner) {
        this.lastFocusOwner = lastFocusOwner;
    }
}

package slapp.editor.vertical_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import slapp.editor.DiskUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.vertical_tree.drag_drop.*;
import slapp.editor.vertical_tree.object_models.*;

import java.util.List;

public class VerticalTreeExercise implements Exercise<VerticalTreeModel, VerticalTreeView> {

    MainWindow mainWindow;
    MainWindowView mainView;
    VerticalTreeModel verticalTreeModel;
    VerticalTreeModel originalModel;
    VerticalTreeView verticalTreeView;
    private boolean exerciseModified = false;



    public VerticalTreeExercise(VerticalTreeModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.verticalTreeModel = model;
        this.originalModel = model;
        this.mainView = mainWindow.getMainView();
        this.verticalTreeView = new VerticalTreeView(mainView);

        setVerticalTreeView();
    }

    private void setVerticalTreeView() {
        verticalTreeView.setExerciseName(verticalTreeModel.getExerciseName());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(verticalTreeModel.getExerciseStatement());
        verticalTreeView.setStatementPrefHeight(verticalTreeModel.getStatementPrefHeight());
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        verticalTreeView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(verticalTreeModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        verticalTreeView.setExerciseComment(commentDRTA);


        populateControlBox();
        for (DragIconType type : verticalTreeModel.getDragIconList()) {
            verticalTreeView.getRootLayout().addDragIcon(type);
        }



        populateMainPaneNodes();



        verticalTreeView.initializeViewDetails();


    }


    private void populateMainPaneNodes() {
        AnchorPane mainPane = verticalTreeView.getRootLayout().getMain_pane();



        for (VerticalBracketMod bracketMod : verticalTreeModel.getVerticalBrackets()) {
            VerticalBracket bracket = new VerticalBracket();
            mainPane.getChildren().add(bracket);
            bracket.setLayoutX(bracketMod.getLayoutX());
            bracket.setLayoutY(bracketMod.getLayoutY());
            bracket.setPrefHeight(bracketMod.getHeight());
        }

        for (DashedLineMod dlMod : verticalTreeModel.getDashedLineMods()) {
            DashedLine dashedLine = new DashedLine();
            mainPane.getChildren().add(dashedLine);
            dashedLine.setLayoutX(dlMod.getLayoutX());
            dashedLine.setLayoutY(dlMod.getLayoutY());
            dashedLine.setPrefWidth(dlMod.getWidth());
        }

        for (MapFormulaBoxMod mapBoxMod : verticalTreeModel.getMapFormulaBoxes()) {
            MapFormulaBox mapFormulaBox = new MapFormulaBox(verticalTreeView);
            mainPane.getChildren().add(mapFormulaBox);
            mapFormulaBox.setLayoutX(mapBoxMod.getLayoutX());
            mapFormulaBox.setLayoutY(mapBoxMod.getLayoutY());
            mapFormulaBox.setIdString(mapBoxMod.getIdString());
            mapFormulaBox.setmLinkIds(mapBoxMod.getLinkIdStrings());

            BoxedDRTA formulaBox = mapFormulaBox.getFormulaBox();
            RichTextArea mapBoxRTA = formulaBox.getRTA();
            mapBoxRTA.setPrefWidth(mapBoxMod.getWidth());
            mapBoxRTA.setDocument(mapBoxMod.getText());
            mapBoxRTA.getActionFactory().saveNow().execute(new ActionEvent());
        }

        for (TreeFormulaBoxMod treeBoxMod : verticalTreeModel.getTreeFormulaBoxes()) {
            TreeFormulaBox treeFormulaBox = new TreeFormulaBox(verticalTreeView);
            mainPane.getChildren().add(treeFormulaBox);
            treeFormulaBox.setLayoutX(treeBoxMod.getLayoutX());
            treeFormulaBox.setLayoutY(treeBoxMod.getLayoutY());
            treeFormulaBox.setIdString(treeBoxMod.getIdString());
            treeFormulaBox.setmLinkIds(treeBoxMod.getLinkIdStrings());

            BoxedDRTA treeFormulaDRTA = treeFormulaBox.getFormulaBox();
            RichTextArea treeBoxRTA = treeFormulaDRTA.getRTA();
            treeBoxRTA.setPrefWidth(treeBoxMod.getWidth());
            treeBoxRTA.setDocument(treeBoxMod.getText());
            treeBoxRTA.getActionFactory().saveNow().execute(new ActionEvent());



        }



    }

    private void populateControlBox() {
        VBox controlBox = verticalTreeView.getControlBox();
        RootLayout layout = verticalTreeView.getRootLayout();
        ToggleGroup buttonGroup = verticalTreeView.getRootLayout().getButtonGroup();

        for (ObjectControlType type : verticalTreeModel.getObjectControlList()) {
            switch(type) {

                case FORMULA_BOX: {
                    ToggleButton boxButton = layout.getBoxToggle();
                    controlBox.getChildren().add(boxButton);
                    boxButton.setToggleGroup(buttonGroup);
                    break;
                }
                case OPERATOR_CIRCLE: {
                    ToggleButton circleButton = layout.getCircleToggle();
                    controlBox.getChildren().add(circleButton);
                    circleButton.setToggleGroup(buttonGroup);
                    break;
                }
                case STAR: {
                    ToggleButton starButton = layout.getStarToggle();
                    controlBox.getChildren().add(starButton);
                    starButton.setToggleGroup(buttonGroup);
                    break;
                }
                case ANNOTATION: {
                    controlBox.getChildren().add(layout.getAnnotationBox());
                    layout.getAnnotationToggle().setToggleGroup(buttonGroup);
                    controlBox.setMargin(layout.getAnnotationBox(), new Insets(20, 0, -15, 0));  //don't understand weird spacing this "papers over"
                    break;
                }
                case UNDERLINE: {
                    ToggleButton underlineButton = layout.getUnderlineToggle();
                    controlBox.getChildren().add(underlineButton);
                    underlineButton.setToggleGroup(buttonGroup);
                    break;
                }
                case MAPPING: {
                    ToggleButton mappingButton = layout.getMappingToggle();
                    controlBox.getChildren().add(mappingButton);
                    mappingButton.setToggleGroup(buttonGroup);
                    controlBox.setMargin(mappingButton, new Insets(10, 0, 0, 0));  //don't understand spacing this "papers over"
                }
            }
        }
    }

    @Override
    public VerticalTreeModel getExerciseModel() {
        return verticalTreeModel;
    }
    @Override
    public VerticalTreeView getExerciseView() {
        return verticalTreeView;
    }

    @Override
    public void saveExercise(boolean saveAs) {

        boolean success = DiskUtilities.saveExercise(saveAs, getVerticalTreeModelFromView());


        if (success) exerciseModified = false;
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
    public void setExerciseModified(boolean modified) { exerciseModified = modified; }

    @Override
    public ExerciseModel<VerticalTreeModel> getExerciseModelFromView() {
        return (ExerciseModel) getVerticalTreeModelFromView();
    }

    private VerticalTreeModel getVerticalTreeModelFromView() {
        VerticalTreeModel model = new VerticalTreeModel();

        model.setExerciseName(verticalTreeModel.getExerciseName());
        model.setDragIconList(verticalTreeModel.getDragIconList());
        model.setObjectControlList(verticalTreeModel.getObjectControlList());
        model.setStarted(verticalTreeModel.isStarted() || exerciseModified);
        model.setStatementPrefHeight(verticalTreeView.getExerciseStatement().getEditor().getPrefHeight());
        model.setExerciseStatement(verticalTreeModel.getExerciseStatement());

        RichTextArea commentRTA = verticalTreeView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());

        RootLayout rootLayout = verticalTreeView.getRootLayout();
        AnchorPane mainPane = rootLayout.getMain_pane();
        ObservableList<Node> nodesList = mainPane.getChildren();

        for (Node node : nodesList) {

            if (node instanceof TreeFormulaBox) {
                TreeFormulaBox originalTreeBox = (TreeFormulaBox) node;
                TreeFormulaBoxMod newTreeMod = new TreeFormulaBoxMod();
                newTreeMod.setIdString(originalTreeBox.getIdString());
                newTreeMod.setLayoutX(originalTreeBox.getLayoutX());
                newTreeMod.setLayoutY(originalTreeBox.getLayoutY());
                newTreeMod.setLinkIdStrings(originalTreeBox.getmLinkIds());

                BoxedDRTA treeFormulaBox = originalTreeBox.getFormulaBox();
                newTreeMod.setWidth(treeFormulaBox.getRTA().getWidth());
                RichTextArea treeRTA = treeFormulaBox.getRTA();
                treeRTA.getActionFactory().saveNow().execute(new ActionEvent());
                newTreeMod.setText(treeRTA.getDocument());




                model.getTreeFormulaBoxes().add(newTreeMod);

            } else if (node instanceof MapFormulaBox) {
                MapFormulaBox originalMapBox = (MapFormulaBox) node;
                MapFormulaBoxMod newMapMod = new MapFormulaBoxMod();
                newMapMod.setIdString(originalMapBox.getIdString());
                newMapMod.setLayoutX(originalMapBox.getLayoutX());
                newMapMod.setLayoutY(originalMapBox.getLayoutY());
                newMapMod.setLinkIdStrings(originalMapBox.getmLinkIds());

                BoxedDRTA formulaBox = originalMapBox.getFormulaBox();
                newMapMod.setWidth(formulaBox.getRTA().getWidth());
                RichTextArea mapRTA = formulaBox.getRTA();
                mapRTA.getActionFactory().saveNow().execute(new ActionEvent());
                newMapMod.setText(mapRTA.getDocument());

                model.getMapFormulaBoxes().add(newMapMod);

            } else if (node instanceof VerticalBracket) {
                VerticalBracketMod brack = new VerticalBracketMod(node.getLayoutX(), node.getLayoutY(), node.getLayoutBounds().getHeight());
                model.getVerticalBrackets().add(brack);

            } else if (node instanceof DashedLine) {
                DashedLineMod dlMod = new DashedLineMod(node.getLayoutX(), node.getLayoutY(), node.getLayoutBounds().getWidth());
                model.getDashedLineMods().add(dlMod);

            } else if (node instanceof ClickableNodeLink) {



            } else if (node instanceof ClickableMapLink) {



            } else if (node instanceof MapQuestionMarker) {


            }  
        }

        return model;
    }

}

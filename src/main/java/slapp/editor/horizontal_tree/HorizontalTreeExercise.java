package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.SerializationUtils;
import slapp.editor.DiskUtilities;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.vertical_tree.VerticalTreeExercise;

import java.util.ArrayList;
import java.util.List;

public class HorizontalTreeExercise implements Exercise<HorizontalTreeModel, HorizontalTreeView> {

    private MainWindow mainWindow;
    private MainWindowView mainView;
    private HorizontalTreeModel horizontalTreeModel;
    private HorizontalTreeView horizontalTreeView;
    private boolean exerciseModified = false;
    private UndoRedoList<HorizontalTreeModel> undoRedoList = new UndoRedoList<>(100);

    public BooleanProperty undoRedoFlag = new SimpleBooleanProperty();

    public HorizontalTreeExercise(HorizontalTreeModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.horizontalTreeModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.horizontalTreeView = new HorizontalTreeView(mainView);
        setHorizontalTreeView();
        undoRedoFlag.set(false);
        undoRedoFlag.bind(horizontalTreeView.undoRedoFlagProperty());
        undoRedoFlag.addListener((ob, ov, nv) -> {
            if (nv) {
                exerciseModified = true;
                pushUndoRedo();
            }
        });
        pushUndoRedo();
    }

    private void setHorizontalTreeView() {
        horizontalTreeView.setExerciseName(horizontalTreeModel.getExerciseName());
        horizontalTreeView.setStatementPrefHeight(horizontalTreeModel.getStatementPrefHeight());
        horizontalTreeView.setCommentPrefHeight(horizontalTreeModel.getCommentPrefHeight());
        horizontalTreeView.setExplainPrefHeight(horizontalTreeModel.getExplainPrefHeight());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(horizontalTreeModel.getExerciseStatement());

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
        horizontalTreeView.setExplainPrompt(horizontalTreeModel.getExplainPrompt());

        explainEditor.getActionFactory().saveNow().execute(new ActionEvent());
        mainView.editorInFocus(explainDRTA, ControlType.AREA);
        explainEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explainDRTA, ControlType.AREA);
            }
        });



        horizontalTreeView.getUndoButton().setOnAction(e -> undoAction());
        horizontalTreeView.getRedoButton().setOnAction(e -> redoAction());

        horizontalTreeView.initializeViewDetails();

        refreshViewFromModel();
        horizontalTreeView.setAnnotationModified(false);
    }

    private void refreshViewFromModel() {

        populateTreePanes();
        horizontalTreeView.refreshTreePanes();

        if (horizontalTreeModel.isAxis()) {
            horizontalTreeView.simpleAddAxis();
            horizontalTreeView.getRulerButton().setSelected(true);
        }
        else {
            horizontalTreeView.simpleRemoveAxis();
            horizontalTreeView.getRulerButton().setSelected(false);
        }
    }


    private void populateTreePanes() {
        horizontalTreeView.getTreePanes().clear();

        for (TreeModel treeModel : horizontalTreeModel.getTreeModels()) {
            TreePane treePane = new TreePane(horizontalTreeView);
            treePane.setLayoutX(treeModel.getPaneXlayout());
            treePane.setLayoutY(treeModel.getPaneYlayout());
            BranchNode rootNode = treePane.getRootBranchNode();
            rootNode.setLayoutX(treeModel.getRootXlayout());
            rootNode.setLayoutY(treeModel.getRootYlayout());
            setTreeNodes(rootNode, treeModel.getRoot());
            horizontalTreeView.getTreePanes().add(treePane);
        }
    }

    private void setTreeNodes(BranchNode branchNode, BranchModel branchModel) {
        if (branchModel.isAnnotation()) {
            branchNode.addAnnotation();
            branchNode.setAnnBump(branchNode.getAnnotationWidth());
            branchNode.setAnnotation(true);
        }
        branchNode.setFormulaNode(branchModel.isFormulaBranch());
        branchNode.setIndefiniteNode(branchModel.isIndefiniteNumBranch());
        branchNode.setDotDivider(branchModel.isDotDivider());
        branchNode.setRoot(branchModel.isRootBranch());
        branchNode.getAnnotationField().setText(branchModel.getAnnotationText());

        RichTextArea formulaRTA = branchNode.getFormulaBoxedDRTA().getRTA();
        formulaRTA.setDocument(branchModel.getFormulaDoc());

        formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());

//        formulaRTA.setPrefWidth(branchModel.getFormulaPrefWidth());

        RichTextArea connectRTA = branchNode.getConnectorBoxedDRTA().getRTA();
        connectRTA.setDocument(branchModel.getConnectorDoc());

        connectRTA.getActionFactory().saveNow().execute(new ActionEvent());

//        connectRTA.setPrefWidth(branchModel.getConnectorPrefWidth());
        if (!branchModel.isFormulaBranch()) branchNode.setStyle("-fx-border-width: 0 0 0 0");
        if (branchModel.isIndefiniteNumBranch()) branchNode.setStyle("-fx-border-width: 0 0 0 0");


        for (BranchModel dependentMod : branchModel.getDependents()) {
            BranchNode dependentNode = new BranchNode(branchNode, horizontalTreeView);
            branchNode.getDependents().add(dependentNode);
            setTreeNodes(dependentNode, dependentMod);

        }
    }

    private void undoAction() {
        HorizontalTreeModel undoElement = undoRedoList.getUndoElement();
        if (undoElement != null) {
            horizontalTreeModel = (HorizontalTreeModel) SerializationUtils.clone(undoElement);
            refreshViewFromModel();
            updateUndoRedoButtons();
            horizontalTreeView.deselectToggles();
        }
    }
    private void redoAction() {
        HorizontalTreeModel redoElement = undoRedoList.getRedoElement();
        if (redoElement != null) {
            horizontalTreeModel = (HorizontalTreeModel) SerializationUtils.clone(redoElement);
            refreshViewFromModel();
            updateUndoRedoButtons();
            horizontalTreeView.deselectToggles();
        }
    }
    private void updateUndoRedoButtons() {
        horizontalTreeView.getUndoButton().setDisable(!undoRedoList.canUndo());
        horizontalTreeView.getRedoButton().setDisable(!undoRedoList.canRedo());
    }
    private void pushUndoRedo() {
        HorizontalTreeModel model = getHorizontalTreeModelFromView();
        HorizontalTreeModel deepCopy = (HorizontalTreeModel) SerializationUtils.clone(model);
        undoRedoList.push(deepCopy);
        updateUndoRedoButtons();
    }


    @Override
    public HorizontalTreeModel getExerciseModel() { return horizontalTreeModel;  }

    @Override
    public HorizontalTreeView getExerciseView() { return horizontalTreeView;  }

    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getHorizontalTreeModelFromView());
        if (success) {
            exerciseModified = false;
            horizontalTreeView.setAnnotationModified(false);
        }
    }

    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        horizontalTreeModel = getHorizontalTreeModelFromView();
        HorizontalTreeExercise exercise = new HorizontalTreeExercise(horizontalTreeModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth() / mainWindow.getBaseScale();

        //header node
        Label exerciseName = new Label(horizontalTreeModel.getExerciseName());
        exerciseName.setStyle("-fx-font-weight: bold;");
        HBox hbox = new HBox(exerciseName);
        hbox.setPadding(new Insets(0,0,10,0));

        Group headerRoot = new Group();
        Scene headerScene = new Scene(headerRoot);
        headerRoot.getChildren().add(hbox);
        headerRoot.applyCss();
        headerRoot.layout();
        double boxHeight = hbox.getHeight();
        hbox.setPrefHeight(boxHeight);
        nodeList.add(hbox);
        Separator headerSeparator = new Separator(Orientation.HORIZONTAL);
        headerSeparator.setPrefWidth(nodeWidth);
        nodeList.add(headerSeparator);

        //statement node
        RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
        statementRTA.prefHeightProperty().unbind();
        statementRTA.minWidthProperty().unbind();
        double statementHeight = mainView.getRTATextHeight(statementRTA);
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(nodeWidth);
        statementRTA.setMinWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setMinWidth(nodeWidth);
        statementSepBox.setAlignment(Pos.CENTER);
        nodeList.add(statementSepBox);

        //content
        AnchorPane mainPane = exercise.getExerciseView().getMainPane();

        Group root = new Group();
        Scene scene = new Scene(root);
        root.getChildren().add(mainPane);
        root.applyCss();
        root.layout();

        mainPane.setStyle("-fx-background-color: transparent");

        ObservableList<Node> mainNodes = mainPane.getChildren();
        for (Node mainNode : mainNodes) {
            if (mainNode instanceof TreePane) {
                TreePane treePane = (TreePane) mainNode;
                ObservableList<Node> paneNodes = treePane.getChildren();
                for (Node paneNode : paneNodes) {
                    if (paneNode instanceof BranchNode) {
                        BranchNode branchNode = (BranchNode) paneNode;
                        branchNode.getFormulaBoxedDRTA().getRTA().setStyle("-fx-border-color: transparent");
                        branchNode.getConnectorBoxedDRTA().getRTA().setStyle("-fx-border-color: transparent");
                        branchNode.getAnnotationField().setStyle("-fx-background-color: transparent");
                    }
                }
            }
        }

        HBox contentHBox = new HBox(mainPane);
        contentHBox.setAlignment(Pos.CENTER);
        contentHBox.setPadding(new Insets(0,0,20, 0));



        nodeList.add(contentHBox);


        //explain node
        RichTextArea explainRTA = exercise.getExerciseView().getExplainDRTA().getEditor();
        explainRTA.prefHeightProperty().unbind();
        explainRTA.minWidthProperty().unbind();
        double explainHeight = mainView.getRTATextHeight(explainRTA);
        explainRTA.setPrefHeight(explainHeight + 35.0);
        explainRTA.setContentAreaWidth(nodeWidth);
        explainRTA.setMinWidth(nodeWidth);
        explainRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(explainRTA);

        Separator explainSeparator = new Separator(Orientation.HORIZONTAL);
        explainSeparator.setPrefWidth(100);
        HBox explainSepBox = new HBox(explainSeparator);
        explainSepBox.setMinWidth(nodeWidth);
        explainSepBox.setAlignment(Pos.CENTER);
        nodeList.add(explainSepBox);


        //comment node
        RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
        commentRTA.prefHeightProperty().unbind();
        commentRTA.minWidthProperty().unbind();
        double commentHeight = mainView.getRTATextHeight(commentRTA);
        commentRTA.setPrefHeight(commentHeight + 35.0);
        commentRTA.setContentAreaWidth(nodeWidth);
        commentRTA.setMinWidth(nodeWidth);
        commentRTA.getStylesheets().clear(); commentRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(commentRTA);

        return nodeList;
    }

    @Override
    public Exercise<HorizontalTreeModel, HorizontalTreeView> resetExercise() {
        RichTextArea commentRTA = horizontalTreeView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        HorizontalTreeModel originalModel = (HorizontalTreeModel) (horizontalTreeModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        HorizontalTreeExercise clearExercise = new HorizontalTreeExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {
        return exerciseModified || horizontalTreeView.isAnnotationModified();
    }

    @Override
    public void setExerciseModified(boolean modified) { exerciseModified = modified;   }

    @Override
    public ExerciseModel<HorizontalTreeModel> getExerciseModelFromView() {
        return (ExerciseModel) getHorizontalTreeModelFromView();
    }

    private HorizontalTreeModel getHorizontalTreeModelFromView() {
        HorizontalTreeModel model = new HorizontalTreeModel();

        model.setExerciseName(horizontalTreeModel.getExerciseName());
        model.setExplainPrompt(horizontalTreeModel.getExplainPrompt());
        model.setOriginalModel(horizontalTreeModel.getOriginalModel());
        model.setStarted(horizontalTreeModel.isStarted() || exerciseModified);
        model.setStatementPrefHeight(horizontalTreeView.getExerciseStatement().getEditor().getPrefHeight());
        model.setCommentPrefHeight(horizontalTreeView.getCommentPrefHeight());
        model.setExplainPrefHeight(horizontalTreeView.getExplainPrefHeight());


        model.setExerciseStatement(horizontalTreeModel.getExerciseStatement());

        RichTextArea commentRTA = horizontalTreeView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());

        RichTextArea explainRTA = horizontalTreeView.getExplainDRTA().getEditor();
        explainRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExplainDocument(explainRTA.getDocument());

        model.setAxis(horizontalTreeView.isAxis());

        List<TreePane> treePanes = horizontalTreeView.getTreePanes();
        List<TreeModel> treeModels = model.getTreeModels();
        for (TreePane treePane : treePanes) {
            TreeModel treeModel = new TreeModel();
            treeModel.setPaneXlayout(treePane.getLayoutX());
            treeModel.setPaneYlayout(treePane.getLayoutY());

            BranchNode rootNode = treePane.getRootBranchNode();
            BranchModel rootModel = new BranchModel();
            setBranchModel(rootModel, rootNode);
            treeModel.setRoot(rootModel);

            treeModel.setRootXlayout(rootNode.getLayoutX());
            treeModel.setRootYlayout(rootNode.getLayoutY());

            treeModels.add(treeModel);
        }
        return model;
    }

    void setBranchModel(BranchModel model, BranchNode node) {
        model.setAnnotation(node.isAnnotation());
        model.setFormulaBranch(node.isFormulaNode());
        model.setIndefiniteNumBranch(node.isIndefiniteNode());
        model.setDotDivider(node.isDotDivider());
        model.setRootBranch(node.isRoot());
        model.setAnnotation(node.isAnnotation());
        model.setAnnotationText(node.getAnnotationField().getText());

        RichTextArea formulaRTA = node.getFormulaBoxedDRTA().getRTA();
        formulaRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setFormulaDoc(formulaRTA.getDocument());
//        model.setFormulaPrefWidth(formulaRTA.getPrefWidth());

        RichTextArea connectRTA = node.getConnectorBoxedDRTA().getRTA();
        connectRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setConnectorDoc(connectRTA.getDocument());
//        model.setConnectorPrefWidth(connectRTA.getPrefWidth());

        for (BranchNode dependentNode : node.getDependents()) {
            BranchModel dependentMod = new BranchModel();
            model.getDependents().add(dependentMod);
            setBranchModel(dependentMod, dependentNode);
        }
    }

}

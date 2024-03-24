package slapp.editor.horizontal_tree;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import slapp.editor.DiskUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
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
        if (horizontalTreeModel.isAxis()) {
            horizontalTreeView.getRulerButton().setSelected(true);
        }

        populateTreePanes();
        horizontalTreeView.refreshTreePanes();

    }

    private void populateTreePanes() {
        for (TreeModel treeModel : horizontalTreeModel.getTreeModels()) {
            TreePane treePane = new TreePane(horizontalTreeView);
            AnchorPane mainPane = horizontalTreeView.getMainPane();
            mainPane.getChildren().add(treePane);
            treePane.setLayoutX(treeModel.getPaneXlayout());
            treePane.setLayoutY(treeModel.getPaneYlayout());
            setTreeNodes(treePane.getRootTreeNode(), treeModel.getRoot());
        }
    }

    private void setTreeNodes(BranchNode branchNode, BranchModel branchModel) {
        branchNode.setAnnotation(branchModel.isAnnotation());
        branchNode.setFormulaNode(branchModel.isFormulaBranch());
        branchNode.setIndefiniteNode(branchModel.isIndefiniteNumBranch());
        branchNode.setDotDivider(branchModel.isDotDivider());
        branchNode.setRoot(branchModel.isRootBranch());
        branchNode.getAnnotationField().setText(branchModel.getAnnotationText());

        RichTextArea formulaRTA = branchNode.getFormulaBoxedDRTA().getRTA();
        formulaRTA.setDocument(branchModel.getFormulaDoc());
        formulaRTA.setPrefWidth(branchModel.getFormulaPrefWidth());

        RichTextArea connectRTA = branchNode.getConnectorBoxedDRTA().getRTA();
        connectRTA.setDocument(branchModel.getConnectorDoc());
        connectRTA.setPrefWidth(branchModel.getConnectorPrefWidth());

        for (BranchModel dependentMod : branchModel.getDependents()) {
            BranchNode dependentNode = new BranchNode(branchNode, horizontalTreeView);
            branchNode.getChildren().add(dependentNode);
            setTreeNodes(dependentNode, dependentMod);
        }
    }




    @Override
    public HorizontalTreeModel getExerciseModel() { return horizontalTreeModel;  }

    @Override
    public HorizontalTreeView getExerciseView() { return horizontalTreeView;  }

    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getHorizontalTreeModelFromView());
        if (success) exerciseModified = false;
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
        return (ExerciseModel) getHorizontalTreeModelFromView();
    }

    private HorizontalTreeModel getHorizontalTreeModelFromView() {
        HorizontalTreeModel model = new HorizontalTreeModel();

        model.setExerciseName(horizontalTreeModel.getExerciseName());
        model.setOriginalModel(horizontalTreeModel.getOriginalModel());
        model.setStarted(horizontalTreeModel.isStarted() || exerciseModified);
        model.setStatementPrefHeight(horizontalTreeView.getExerciseStatement().getEditor().getPrefHeight());
        model.setExerciseStatement(horizontalTreeModel.getExerciseStatement());

        RichTextArea commentRTA = horizontalTreeView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExerciseComment(commentRTA.getDocument());

        RichTextArea explainRTA = horizontalTreeView.getExplainDRTA().getEditor();
        explainRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setExplainDocument(explainRTA.getDocument());

        Region axis = horizontalTreeView.getAxis();
        model.setAxis(horizontalTreeView.getMainPane().getChildren().contains(axis));

        List<TreePane> treePanes = horizontalTreeView.getTreePanes();
        List<TreeModel> treeModels = horizontalTreeModel.getTreeModels();
        for (TreePane treePane : treePanes) {
            TreeModel treeModel = new TreeModel();
            treeModel.setPaneXlayout(treePane.getLayoutX());
            treeModel.setPaneYlayout(treePane.getLayoutY());

            BranchNode rootNode = treePane.getRootTreeNode();
            BranchModel rootModel = new BranchModel();
            setBranchModel(rootModel, rootNode);
            treeModel.setRoot(rootModel);
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
        model.setFormulaPrefWidth(formulaRTA.getPrefWidth());

        RichTextArea connectRTA = node.getConnectorBoxedDRTA().getRTA();
        connectRTA.getActionFactory().saveNow().execute(new ActionEvent());
        model.setConnectorDoc(connectRTA.getDocument());
        model.setConnectorPrefWidth(connectRTA.getPrefWidth());

        for (BranchNode dependentNode : node.getDependents()) {
            BranchModel dependentMod = new BranchModel();
            model.getDependents().add(dependentMod);
            setBranchModel(dependentMod, dependentNode);
        }
    }

}

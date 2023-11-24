package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import slapp.editor.EditorAlerts;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.Exercise;
import slapp.editor.main_window.ExerciseModel;
import slapp.editor.main_window.MainWindow;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

public class DerivationExercise implements Exercise<DerivationModel, DerivationView> {
    private MainWindow mainWindow;
    private DerivationModel derivationModel;
    private DerivationView derivationView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;

    Node lastFocusedNode;
    Font labelFont = new Font("Noto Serif Combo", 11);

    Boolean editJustification;
    EventHandler justificationClickFilter;



    public DerivationExercise(DerivationModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.derivationModel = model;
        this.mainView = mainWindow.getMainView();
        this.derivationView = new DerivationView(mainView);

        mainView.getMainScene().focusOwnerProperty().addListener((ob, ov, nv) -> {
            lastFocusedNode = ov;
        });




        setDerivationView();
    }

    private void setDerivationView() {

        derivationView.setExerciseName(derivationModel.getExerciseName());
        derivationView.setContentPrompt(derivationModel.getContentPrompt());
        derivationView.setLeftmostScopeLine(derivationModel.isLeftmostScopeLine());

        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(derivationModel.getExerciseStatement());
        derivationView.setStatementPrefHeight(derivationModel.getStatementPrefHeight());
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA);
            }
        });
        derivationView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(derivationModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA);
            }
        });
        derivationView.setExerciseComment(commentDRTA);

        //
        derivationView.getInsertButton().setOnAction(e -> insertLineAction());
        derivationView.getDeleteButton().setOnAction(e -> deleteLineAction());
        //

        derivationView.initializeViewDetails();
        setViewLinesFromModel();
        derivationView.setGridFromViewLines();
    }

    private void setViewLinesFromModel() {

        List<ModelLine> modelLines = derivationModel.getExerciseContent();
        List<ViewLine> viewLines = new ArrayList<>();
        int lineNumber = 1;
        for (int i = 0; i < modelLines.size(); i++) {
            ModelLine modelLine = modelLines.get(i);
            ViewLine viewLine = new ViewLine();

            viewLine.setDepth(modelLine.getDepth());
            LineType lineType = modelLine.getLineType();
            viewLine.setLineType(lineType);
            viewLine.setSetupLine(modelLine.isSetupLine());

            if (lineType == LineType.CONTENT_LINE) {
                Label numLabel = new Label();
                numLabel.setText(Integer.toString(lineNumber++));
                numLabel.setFont(labelFont);
                viewLine.setLineNumberLabel(numLabel);

                DecoratedRTA drta = new DecoratedRTA();
                RichTextArea rta = drta.getEditor();
                rta.setDocument(modelLine.getLineContentDoc());
                rta.focusedProperty().addListener((o, ov, nv) -> {
                    if (nv) {
                        mainView.editorInFocus(drta);
                    }
                });






                viewLine.setLineContentDRTA(drta);

                TextFlow justificationFlow = getJustificationFlow(modelLine.getJustification(), viewLines);
                viewLine.setJustificationFlow(justificationFlow);

            } else {
                viewLine.setLineNumberLabel(null);
                viewLine.setLineContentDRTA(null);
                viewLine.setJustificationFlow(null);
            }
            viewLines.add(viewLine);
        }
        derivationView.setViewLines(viewLines);
    }

    private void focusNext(DecoratedRTA drta) {
        System.out.println("next");


    }


    private void focusPrior(DecoratedRTA drta) {
        System.out.println("prior");
    }
    private void focusAbove(DecoratedRTA drta) {
        System.out.println("above");
    }
    private void focusBelow(DecoratedRTA drta) {
        System.out.println("below");
    }

    private TextFlow getJustificationFlow(String justificationString, List<ViewLine> viewLines) {
        justificationString = justificationString.trim();
        TextFlow flow = getStyledJustificationFlow(new TextFlow());

        if (!justificationString.isEmpty()) {

            //get List of alternating digit and non-digit sequences
            List<String> splitList = new ArrayList();
            boolean startsDigit = false;
            if (charIsDigit(justificationString.charAt(0))) startsDigit = true;
            StringBuilder builder = new StringBuilder();
            boolean buildingDigit = startsDigit;

            int j = 0;
            while (j < justificationString.length()) {
                if (charIsDigit(justificationString.charAt(j)) == buildingDigit) {
                    builder.append(justificationString.charAt(j));
                    j++;
                }
                else {
                    splitList.add(builder.toString());
                    builder.delete(0, builder.length());
                    buildingDigit = charIsDigit(justificationString.charAt(j));
                }
            }
            splitList.add(builder.toString());

            //get flow of labels and texts with labels bound to line numbers
            boolean buildingNum = startsDigit;
            for (int i = 0; i < splitList.size(); i++) {
                if (buildingNum) {
                    Label label = new Label(splitList.get(i));
                    label.setFont(labelFont);

                    for (int k = 0; k < viewLines.size(); k++) {
                        ViewLine line = viewLines.get(k);
                        if (line.getLineNumberLabel() != null) {
                            String lineLabel = line.getLineNumberLabel().getText();
                            if ((!lineLabel.isEmpty()) && (lineLabel.equals(label.getText()))) {
                                label.textProperty().bind(line.getLineNumberLabel().textProperty());
                                line.getClientLabels().add(label);
                                break;
                            }
                        }
                    }
                    flow.getChildren().add(label);

                } else {
                    Text text = new Text(splitList.get(i));
                    text.setFont(Font.font("Noto Serif Combo", 11));
                    flow.getChildren().add(text);
                }
                buildingNum = !buildingNum;
            }
        }
        return flow;
    }

    TextFlow getStyledJustificationFlow(TextFlow flow) {
        flow.setFocusTraversable(true);
        flow.setMouseTransparent(false);

        flow.setMinWidth(100);
        flow.setMaxWidth(100);
        flow.setMaxHeight(20);
  //      flow.setPrefHeight(20);
        flow.setPadding(new Insets(0,0,0,3));
 //       flow.setStyle("-fx-border-color: red; -fx-border-width: 1 1 1 1; ");
        flow.setOnMouseClicked(e -> flow.requestFocus());
        flow.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editJustificationField(flow);
            }
        });
        return flow;
    }

    private void editJustificationField(TextFlow flow) {
        int rowIndex = derivationView.getGrid().getRowIndex(flow);
        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        rta.setContentAreaWidth(110);
        rta.setPrefHeight(20);
        rta.setPrefWidth(100);
        rta.setMaxWidth(100);
        rta.setMinWidth(100);
        rta.getStylesheets().add("slappDerivation.css");

        rta.setDocument(new Document(getStringFromJustificationFlow(flow)));

        rta.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {

                mainView.editorInFocus(drta);                         //commenting this out stops the "jump" when rta gets focu; with in, fixes keyboard dropdown  why such a jump anyway??

                editJustification = true;
                justificationClickFilter = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!inHierarchy(event.getPickResult().getIntersectedNode(), rta)) {

                            if (editJustification) {
                                editJustification = false;
                                saveJustificationRTA(rta, rowIndex);
                            }
                        }
                    }
                };
                mainView.getMainScene().addEventFilter(MouseEvent.MOUSE_PRESSED, justificationClickFilter);
            }
            else {
                if (editJustification) {
                    editJustification = false;
                    saveJustificationRTA(rta, rowIndex);
                }
            }
        });

        derivationView.getGrid().add(rta, 22, rowIndex);
    }

    public static boolean inHierarchy(Node node, Node potentialHierarchyElement) {
        if (potentialHierarchyElement == null) {
            return true;
        }
        while (node != null) {
            if (node == potentialHierarchyElement) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    private void saveJustificationRTA(RichTextArea rta, int rowIndex) {
        mainView.getMainScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, justificationClickFilter);
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        String justificationString = rta.getDocument().getText();
        TextFlow justificationFlow = getJustificationFlow(justificationString, derivationView.getViewLines());
        derivationView.getViewLines().get(rowIndex).setJustificationFlow(justificationFlow);
        derivationView.setGridFromViewLines();
    }

    private String getStringFromJustificationFlow(TextFlow flow) {
        StringBuilder sb = new StringBuilder();
        ObservableList<Node> list = flow.getChildren();
        for (Node node : flow.getChildren()) {
            if (node instanceof Label) sb.append(((Label) node).getText());
            else if (node instanceof Text) sb.append(((Text) node).getText());
        }
        return sb.toString();
    }

    private boolean charIsDigit(char character) {
        boolean result = false;
        if ('0' <= character && character <= '9') result = true;
        return result;
    }

    private void setEmptyViewContentRow(int row, int depth) {
        Label numLabel = new Label();
        numLabel.setFont(labelFont);
        TextFlow flow = new TextFlow();
        TextFlow justificationFlow = getStyledJustificationFlow(flow);
        ViewLine viewLine = new ViewLine(numLabel, depth, LineType.CONTENT_LINE, false, new DecoratedRTA(), justificationFlow, new ArrayList<>());
        derivationView.getViewLines().add(row, viewLine);
    }

    private void insertLineAction() {
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) {
            int row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
            int depth = derivationView.getViewLines().get(row).getDepth();
            if (!derivationView.getViewLines().get(row).isSetupLine() || row + 1 == derivationView.getViewLines().size()) {
                setEmptyViewContentRow(row, depth);
                derivationView.setGridFromViewLines();
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup lines.");
            }
        }
        else {
            EditorAlerts.fleetingPopup("Select derivation row for insert above.");
        }
    }

    private void deleteLineAction() {
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) {
            int row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
            List<ViewLine> viewLines = derivationView.getViewLines();
            if (!viewLines.get(row).isSetupLine()) {
                List<Label> clients = viewLines.get(row).getClientLabels();
                for (Label label : clients) {
                    label.textProperty().unbind();
                    label.setText("??");
                }
                viewLines.remove(row);

                LineType type = viewLines.get(row).getLineType();
                if ((type == LineType.SHELF_LINE) || (type == LineType.GAP_LINE)) viewLines.remove(row);
                derivationView.setGridFromViewLines();
                for (int i = row; i < viewLines.size(); i++) {
                    if (viewLines.get(i).getLineContentDRTA() != null) {
                        viewLines.get(i).getLineContentDRTA().getEditor().requestFocus();
                        break;
                    }
                }
            }
            else {
                EditorAlerts.fleetingPopup("Cannot modify setup line.");
            }
        }
        else {
            EditorAlerts.fleetingPopup("Select derivation row to delete.");
        }
    }





    @Override
    public DerivationModel getExerciseModel() { return derivationModel; }
    @Override
    public DerivationView getExerciseView() { return derivationView; }
    @Override
    public void saveExercise(boolean saveAs) {

    }

    @Override
    public void printExercise() {

    }

    @Override
    public void exportExerciseToPDF() {

    }

    @Override
    public List<Node> getPrintNodes() {
        return null;
    }

    @Override
    public Exercise<DerivationModel, DerivationView> getContentClearExercise() {
        return null;
    }

    @Override
    public boolean isExerciseModified() {
        return false;
    }

    @Override
    public void setExerciseModified(boolean modified) { this.exerciseModified = modified; }

    @Override
    public void updateContentHeight(boolean isRequired) {

    }

    @Override
    public void updateCommentHeight(boolean isRequired) {

    }

    @Override
    public void updateStatementHeight(boolean isRequired) {

    }

    @Override
    public ExerciseModel<DerivationModel, DerivationView> getExerciseModelFromView() {
        return null;
    }

}

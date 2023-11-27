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

    private Node lastFocusedNode;
    private Font labelFont = new Font("Noto Serif Combo", 11);

    private Boolean editJustification;
    private EventHandler justificationClickFilter;
    private RichTextArea lastJustificationRTA;
    private int lastJustificationRow;




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
        derivationView.getInsertLineButton().setOnAction(e -> insertLineAction());
        derivationView.getDeleteLineButton().setOnAction(e -> deleteLineAction());
        derivationView.getIndentButton().setOnAction(e -> indentLineAction());
        derivationView.getOutdentButton().setOnAction(e -> outdentLineAction());
        derivationView.getAddShelfButton().setOnAction(e -> addShelfLineAction());
        derivationView.getAddGapButton().setOnAction(e -> addGapLineAction());
        derivationView.getInsertSubButton().setOnAction(e -> insertSubAction());
        derivationView.getInsertSubsButton().setOnAction(e -> insertSubsAction());

        //

        derivationView.initializeViewDetails();
        setViewLinesFromModel();
        derivationView.setGridFromViewLines();
    }

    private void setViewLinesFromModel() {

        List<ModelLine> modelLines = derivationModel.getExerciseContent();
        List<ViewLine> viewLines = new ArrayList<>();
        int lineNumber = 1;
        for (int rowIndex = 0; rowIndex < modelLines.size(); rowIndex++) {
            ModelLine modelLine = modelLines.get(rowIndex);
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

                rta.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    KeyCode code = e.getCode();
                    int row = derivationView.getGrid().getRowIndex(rta.getParent());

                    if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShiftDown())) {
                        viewLine.getJustificationFlow().requestFocus();
                        e.consume();
                    } else if (code == KeyCode.LEFT && e.isShiftDown()) {
                        if (getContentLineAbove(row) != null) getContentLineAbove(row).getJustificationFlow().requestFocus();
                        e.consume();
                    } else if (code == KeyCode.UP ) {
                        if (getContentLineAbove(row) != null) getContentLineAbove(row).getLineContentDRTA().getEditor().requestFocus();
                        e.consume();
                    } else if (code == KeyCode.DOWN) {
                        if (getContentLineBelow(row) != null) getContentLineBelow(row).getLineContentDRTA().getEditor().requestFocus();
                        e.consume();
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

    private ViewLine getContentLineBelow(int row) {
        ViewLine line = null;
        row++;
        for (int i = row; i < derivationView.getViewLines().size(); i++) {
            ViewLine temp = derivationView.getViewLines().get(i);
            if (temp.getLineType() == LineType.CONTENT_LINE) {
                line = temp;
                break;
            }
        }
        return line;
    }

    private ViewLine getContentLineAbove(int row) {
        ViewLine line = null;
        row--;
        for (int i = row; i >= 0; i--) {
            ViewLine temp = derivationView.getViewLines().get(i);
            if (temp.getLineType() == LineType.CONTENT_LINE) {
                line = temp;
                break;
            }
        }
        return line;
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
        lastJustificationRTA = rta;
        lastJustificationRow = rowIndex;

        rta.applyCss();
        rta.layout();

        rta.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
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

        rta.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            int row = derivationView.getGrid().getRowIndex(rta);


            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShiftDown())) {
                if (getContentLineBelow(row) != null) getContentLineBelow(row).getLineContentDRTA().getEditor().requestFocus();
                e.consume();
            } else if (code == KeyCode.LEFT && e.isShiftDown()) {
                derivationView.getViewLines().get(row).getLineContentDRTA().getEditor().requestFocus();
                e.consume();
            } else if (code == KeyCode.UP ) {
                if (getContentLineAbove(row) != null) getContentLineAbove(row).getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.DOWN ) {
                if (getContentLineBelow(row) != null) getContentLineBelow(row).getJustificationFlow().requestFocus();
                e.consume();
            }

        });

        derivationView.getGrid().add(rta, 22, rowIndex);
        mainView.editorInFocus(drta);
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

    private void addEmptyViewContentRow(int newRow, int depth) {
        Label numLabel = new Label();
        numLabel.setFont(labelFont);

        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        rta.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            int row = derivationView.getGrid().getRowIndex(rta.getParent());

            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShiftDown())) {
                derivationView.getViewLines().get(row).getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.LEFT && e.isShiftDown()) {
                if (getContentLineAbove(row) != null) getContentLineAbove(row).getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.UP ) {
                if (getContentLineAbove(row) != null) getContentLineAbove(row).getLineContentDRTA().getEditor().requestFocus();
                e.consume();
            } else if (code == KeyCode.DOWN) {
                if (getContentLineBelow(row) != null) getContentLineBelow(row).getLineContentDRTA().getEditor().requestFocus();
                e.consume();
            }
        });

        TextFlow flow = new TextFlow();
        TextFlow justificationFlow = getStyledJustificationFlow(flow);
        ViewLine viewLine = new ViewLine(numLabel, depth, LineType.CONTENT_LINE, false, drta, justificationFlow, new ArrayList<>());
        derivationView.getViewLines().add(newRow, viewLine);
    }



    private void insertLineAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            int depth = derivationView.getViewLines().get(row).getDepth();
            if (!derivationView.getViewLines().get(row).isSetupLine() || row + 1 == derivationView.getViewLines().size()) {
                addEmptyViewContentRow(row, depth);
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
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
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

    private void indentLineAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            if (!viewLine.isSetupLine()) {

                int depth = viewLine.getDepth();
                if (depth < 19) {
                    viewLine.setDepth(++depth);
                    viewLines.set(row, viewLine);
                    if (++row < viewLines.size()) {
                        ViewLine nextLine = viewLines.get(row);
                        if (nextLine.getLineType() == LineType.SHELF_LINE || nextLine.getLineType() == LineType.GAP_LINE) {
                            nextLine.setDepth(depth);
                            viewLines.set(row, nextLine);
                        }
                    }
                    derivationView.setGridFromViewLines();
                } else {
                    EditorAlerts.fleetingPopup("19 is the maximum scope depth.");
                }
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup line.");
            }
        } else {
            EditorAlerts.fleetingPopup("Select derivation row to indent.");
        }
    }
    private void outdentLineAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            if (!viewLine.isSetupLine()) {
                int depth = viewLine.getDepth();
                if (depth > 1) {
                    viewLine.setDepth(--depth);
                    viewLines.set(row, viewLine);
                    if (++row < viewLines.size()) {
                        ViewLine nextLine = viewLines.get(row);
                        if (nextLine.getLineType() == LineType.SHELF_LINE || nextLine.getLineType() == LineType.GAP_LINE) {
                            if (depth > 1) {
                                nextLine.setDepth(depth);
                                viewLines.set(row, nextLine);
                            } else {
                                viewLines.remove(row);
                            }
                        }
                    }
                    derivationView.setGridFromViewLines();
                } else {
                    EditorAlerts.fleetingPopup("1 is the mininum scope depth.");
                }
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup line.");
            }
        } else {
            EditorAlerts.fleetingPopup("Select derivation row to outdent.");
        }
    }
    private void addShelfLineAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            int depth = viewLine.getDepth();
            if (!viewLine.isSetupLine()) {
                if (depth > 1) {
                    if (++row < viewLines.size()) {
                        if (!(viewLines.get(row).getLineType() == LineType.SHELF_LINE || viewLines.get(row).getLineType() == LineType.GAP_LINE)) {
                            ViewLine shelfLine = new ViewLine(null, depth, LineType.SHELF_LINE, false, null, null, null);
                            viewLines.add(row, shelfLine);
                            derivationView.setGridFromViewLines();
                        } else {
                            EditorAlerts.fleetingPopup("No shelf on top of shelf or gap.");
                        }
                    } else {
                        EditorAlerts.fleetingPopup("No shelf under last line.");
                    }
                } else {
                    EditorAlerts.fleetingPopup("Cannot modify at leftmost scope depth.");
                }
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup line.");
            }
        } else {
            EditorAlerts.fleetingPopup("Select row to have shelf.");
        }
    }
    private void addGapLineAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            int depth = viewLine.getDepth();
            if (!viewLine.isSetupLine()) {
                if (depth > 1) {
                    if (++row < viewLines.size()) {
                        if (!(viewLines.get(row).getLineType() == LineType.SHELF_LINE || viewLines.get(row).getLineType() == LineType.GAP_LINE)) {
                            ViewLine gapLine = new ViewLine(null, depth, LineType.GAP_LINE, false, null, null, null);
                            viewLines.add(row, gapLine);
                            derivationView.setGridFromViewLines();
                        } else {
                            EditorAlerts.fleetingPopup("No gap on top of shelf or gap.");
                        }
                    } else {
                        EditorAlerts.fleetingPopup("No gap under last line.");
                    }
                } else {
                    EditorAlerts.fleetingPopup("Cannot modify at leftmost scope depth.");
                }
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup line.");
            }
        } else {
            EditorAlerts.fleetingPopup("Select row to have gap.");
        }
    }
    private void insertSubAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            int depth = derivationView.getViewLines().get(row).getDepth();
            depth++;
            if (!derivationView.getViewLines().get(row).isSetupLine() || row + 1 == derivationView.getViewLines().size()) {
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                ViewLine shelfLine = new ViewLine(null, depth, LineType.SHELF_LINE, false, null, null, null);
                derivationView.getViewLines().add(row, shelfLine);
                addEmptyViewContentRow(row, depth);
                derivationView.setGridFromViewLines();
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup lines.");
            }
        }
        else {
            EditorAlerts.fleetingPopup("Select derivation row for insert above.");
        }
    }
    private void insertSubsAction() {
        int row = -1;
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            int depth = derivationView.getViewLines().get(row).getDepth();
            depth++;
            if (!derivationView.getViewLines().get(row).isSetupLine() || row + 1 == derivationView.getViewLines().size()) {
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                ViewLine shelfLine1 = new ViewLine(null, depth, LineType.SHELF_LINE, false, null, null, null);
                derivationView.getViewLines().add(row, shelfLine1);
                addEmptyViewContentRow(row, depth);

                ViewLine gapLine = new ViewLine(null, depth, LineType.GAP_LINE, false, null, null, null);
                derivationView.getViewLines().add(row, gapLine);

                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                ViewLine shelfLine2 = new ViewLine(null, depth, LineType.SHELF_LINE, false, null, null, null);
                derivationView.getViewLines().add(row, shelfLine2);
                addEmptyViewContentRow(row, depth);

                derivationView.setGridFromViewLines();
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup lines.");
            }
        }
        else {
            EditorAlerts.fleetingPopup("Select derivation row for insert above.");
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

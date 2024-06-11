package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.SerializationUtils;
import slapp.editor.DiskUtilities;
import slapp.editor.EditorAlerts;
import slapp.editor.PrintUtilities;
import slapp.editor.decorated_rta.BoxedDRTA;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.*;
import slapp.editor.vertical_tree.VerticalTreeExercise;
import slapp.editor.vertical_tree.VerticalTreeModel;

import java.util.ArrayList;
import java.util.List;


public class DerivationExercise implements Exercise<DerivationModel, DerivationView> {
    private MainWindow mainWindow;
    private DerivationModel derivationModel;
    private DerivationView derivationView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;
    private Font labelFont = new Font("Noto Serif Combo", 11);
    private boolean editJustification;
    private EventHandler justificationClickFilter;
    private RichTextArea lastJustificationRTA;
    private int lastJustificationRow;
    private UndoRedoList<DerivationModel> undoRedoList = new UndoRedoList<>(20);


    public DerivationExercise(DerivationModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.derivationModel = model;
        if (model.getOriginalModel() == null) {model.setOriginalModel(model); }
        this.mainView = mainWindow.getMainView();
        this.derivationView = new DerivationView(mainView);
        setDerivationView();
        pushUndoRedo();
    }

    private void setDerivationView() {

        derivationView.setExerciseName(derivationModel.getExerciseName());
        derivationView.setLeftmostScopeLine(derivationModel.isLeftmostScopeLine());
        derivationView.setKeyboardSelector(derivationModel.getKeyboardSelector());
        derivationView.setStatementPrefHeight(derivationModel.getStatementPrefHeight());
        derivationView.setCommentPrefHeight(derivationModel.getCommentPrefHeight());
        derivationView.setSplitPanePrefWidth(derivationModel.getSplitPanePrefWidth());

        //statement
        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(derivationModel.getExerciseStatement());

        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        derivationView.setExerciseStatement(statementDRTA);

        //comment
        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(derivationModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        derivationView.setExerciseComment(commentDRTA);

        //buttons
        derivationView.getInsertLineButton().setOnAction(e -> insertLineAction());
        derivationView.getDeleteLineButton().setOnAction(e -> deleteLineAction());
        derivationView.getIndentButton().setOnAction(e -> indentLineAction());
        derivationView.getOutdentButton().setOnAction(e -> outdentLineAction());
        derivationView.getAddShelfButton().setOnAction(e -> addShelfLineAction());
        derivationView.getAddGapButton().setOnAction(e -> addGapLineAction());
        derivationView.getInsertSubButton().setOnAction(e -> insertSubAction());
        derivationView.getInsertSubsButton().setOnAction(e -> insertSubsAction());
        derivationView.getUndoButton().setOnAction(e -> undoAction());
        derivationView.getRedoButton().setOnAction(e -> redoAction());

        //cleanup
        derivationView.initializeViewDetails();
        derivationView.getContentSplitPane().setDividerPosition(0, derivationModel.getGridWidth());
        derivationView.getContentSplitPane().getDividers().get(0).positionProperty().addListener((ob, ov, nv) -> {
            double diff = (double) nv - (double) ov;
            if (Math.abs(diff) >= .07) exerciseModified = true;
        });

        setViewLinesFromModel();
        derivationView.setGridFromViewLines();
        setContentFocusListeners();
    }

    private void setViewLinesFromModel() {

        List<ModelLine> modelLines = derivationModel.getDerivationLines();
        List<ViewLine> viewLines = new ArrayList<>();
        int lineNumber = 1;
        for (int rowIndex = 0; rowIndex < modelLines.size(); rowIndex++) {
            ModelLine modelLine = modelLines.get(rowIndex);
            ViewLine viewLine = new ViewLine();

            viewLine.setDepth(modelLine.getDepth());
            LineType lineType = modelLine.getLineType();
            viewLine.setLineType(lineType);

            if (LineType.isContentLine(lineType)) {
                Label numLabel = new Label();
                numLabel.setText(Integer.toString(lineNumber++));
                numLabel.setFont(labelFont);
                viewLine.setLineNumberLabel(numLabel);

                BoxedDRTA bdrta = new BoxedDRTA();
                RichTextArea rta = bdrta.getRTA();
                rta.setDocument(modelLine.getLineContentDoc());
                rta.getActionFactory().saveNow().execute(new ActionEvent());


                if (LineType.isSetupLine(lineType)) {
                    rta.setEditable(false);
                }

                rta.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                    KeyCode code = e.getCode();
                    int row = derivationView.getGrid().getRowIndex(rta.getParent());

                    if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                        viewLine.getJustificationFlow().requestFocus();
                        e.consume();
                    } else if (code == KeyCode.LEFT && e.isShortcutDown()) {
                        ViewLine contentLineAbove = getContentLineAbove(row);
                        if (contentLineAbove != null) contentLineAbove.getJustificationFlow().requestFocus();
                        e.consume();
                    } else if (code == KeyCode.UP) {
                        ViewLine contentLineAbove = getContentLineAbove(row);
                        if (contentLineAbove != null) contentLineAbove.getLineContentBoxedDRTA().getRTA().requestFocus();
                        e.consume();
                    } else if (code == KeyCode.DOWN) {
                        ViewLine contentLineBelow = getContentLineBelow(row);
                        if (contentLineBelow != null) contentLineBelow.getLineContentBoxedDRTA().getRTA().requestFocus();
                        e.consume();
                    }
                });
                viewLine.setLineContentBoxedDRTA(bdrta);

                TextFlow justificationFlow = getJustificationFlow(modelLine.getJustification(), viewLines);
                viewLine.setJustificationFlow(justificationFlow);

            } else {
                viewLine.setLineNumberLabel(null);
                viewLine.setLineContentBoxedDRTA(null);
                viewLine.setJustificationFlow(null);
            }
            viewLines.add(viewLine);
        }
        derivationView.setViewLines(viewLines);
    }

    private void setContentFocusListeners() {
        List<ViewLine> viewLines = derivationView.getViewLines();
        for (ViewLine viewLine : viewLines) {
            if (LineType.isContentLine(viewLine.getLineType())) {
                BoxedDRTA bdrta = viewLine.getLineContentBoxedDRTA();
                DecoratedRTA drta = bdrta.getDRTA();
                RichTextArea rta = bdrta.getRTA();
                mainView.editorInFocus(drta, ControlType.FIELD);
                rta.focusedProperty().addListener((o, ov, nv) -> {
                    if (nv) {
                        mainView.editorInFocus(drta, ControlType.FIELD);
                    } else {
                        if (rta.isModified()) {
                            pushUndoRedo();
                            exerciseModified = true;
                        }
                    }
                });
            }
        }
    }

    private ViewLine getContentLineBelow(int row) {
        ViewLine line = derivationView.getViewLines().get(row);
//        ViewLine line = null;
        row++;
        for (int i = row; i < derivationView.getViewLines().size(); i++) {
            ViewLine temp = derivationView.getViewLines().get(i);
            if (LineType.isContentLine(temp.getLineType())) {
                line = temp;
                break;
            }
        }
        return line;
    }

    private ViewLine getContentLineAbove(int row) {
        ViewLine line = derivationView.getViewLines().get(row);
//        ViewLine line = null;
        row--;
        for (int i = row; i >= 0; i--) {
            ViewLine temp = derivationView.getViewLines().get(i);
            if (LineType.isContentLine(temp.getLineType())) {
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
//        flow.setMaxWidth(100);
        flow.setMaxHeight(20);
        flow.setPadding(new Insets(0,0,0,3));
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
        rta.setContentAreaWidth(200);
        rta.setPrefHeight(20);
        rta.setPrefWidth(100);
        rta.getStylesheets().add("slappDerivation.css");
        rta.setDocument(new Document(getStringFromJustificationFlow(flow)));
        rta.getActionFactory().saveNow().execute(new ActionEvent());

        if (derivationView.getViewLines().get(rowIndex).getLineType() == LineType.PREMISE_LINE) rta.setEditable(false);
        lastJustificationRTA = rta;
        lastJustificationRow = rowIndex;
        rta.applyCss();
        rta.layout();

        justificationClickFilter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!inHierarchy(event.getPickResult().getIntersectedNode(), rta)) {
                    if (editJustification) {
                        editJustification = false;
                        saveJustificationRTA(rta, rowIndex);

                        //this stops (strange) scroll pane behavior on mouse click out of justification;
                        //jumps to top of scroll.  Don't understand.
                        derivationView.getGrid().requestFocus();
                    }
                }
            }
        };

        rta.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                editJustification = true;
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

            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                derivationView.setGridFromViewLines();
                ViewLine contentLineBelow = getContentLineBelow(row);
                if (contentLineBelow != null) contentLineBelow.getLineContentBoxedDRTA().getRTA().requestFocus();
                e.consume();
            } else if (code == KeyCode.LEFT && e.isShortcutDown()) {
                derivationView.setGridFromViewLines();
                ViewLine currentLine = derivationView.getViewLines().get(row);
                currentLine.getLineContentBoxedDRTA().getRTA().requestFocus();
                e.consume();
            } else if (code == KeyCode.UP ) {
                derivationView.setGridFromViewLines();
                ViewLine contentLineAbove = getContentLineAbove(row);
                if (contentLineAbove != null) contentLineAbove.getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.DOWN ) {
                derivationView.setGridFromViewLines();
                ViewLine contentLineBelow = getContentLineBelow(row);
                if (contentLineBelow != null)  contentLineBelow.getJustificationFlow().requestFocus();
                e.consume();
            }

        });

        derivationView.getGrid().add(rta, 22, rowIndex);
        mainView.editorInFocus(drta, ControlType.STATEMENT);
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

        rta.setEditable(true);
        boolean modified = rta.isModified();
        rta.getActionFactory().saveNow().execute(new ActionEvent());
        String justificationString = rta.getDocument().getText();

        TextFlow justificationFlow = getJustificationFlow(justificationString, derivationView.getViewLines());
        derivationView.getViewLines().get(rowIndex).setJustificationFlow(justificationFlow);



        derivationView.setGridFromViewLines();

        if (modified) {
            pushUndoRedo();
            exerciseModified = true;
        }
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

        BoxedDRTA bdrta = new BoxedDRTA();
        RichTextArea rta = bdrta.getRTA();
        rta.getStylesheets().add("slappDerivation.css");

        rta.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode code = e.getCode();
            int row = derivationView.getGrid().getRowIndex(rta.getParent());

            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                derivationView.getViewLines().get(row).getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.LEFT && e.isShortcutDown()) {
                if (getContentLineAbove(row) != null) getContentLineAbove(row).getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.UP ) {
                if (getContentLineAbove(row) != null) getContentLineAbove(row).getLineContentBoxedDRTA().getRTA().requestFocus();
                e.consume();
            } else if (code == KeyCode.DOWN) {
                if (getContentLineBelow(row) != null) getContentLineBelow(row).getLineContentBoxedDRTA().getRTA().requestFocus();
                e.consume();
            }
        });

        TextFlow flow = new TextFlow();
        TextFlow justificationFlow = getStyledJustificationFlow(flow);
        ViewLine viewLine = new ViewLine(numLabel, depth, LineType.MAIN_CONTENT_LINE, bdrta, justificationFlow, new ArrayList<Label>());

        derivationView.getViewLines().add(newRow, viewLine);

    }

    private void undoAction() {
        DerivationModel undoElement = undoRedoList.getUndoElement();
        if (undoElement != null) {
            derivationModel = (DerivationModel) SerializationUtils.clone(undoElement);
            setViewLinesFromModel();
            derivationView.setGridFromViewLines();
            updateUndoRedoButtons();
            setContentFocusListeners();

        }
    }

    private void redoAction() {
        DerivationModel redoElement = undoRedoList.getRedoElement();
        if (redoElement != null) {
            derivationModel = (DerivationModel) SerializationUtils.clone(redoElement);
            setViewLinesFromModel();
            derivationView.setGridFromViewLines();
            updateUndoRedoButtons();
            setContentFocusListeners();

        }
    }

    private void updateUndoRedoButtons() {
        derivationView.getUndoButton().setDisable(!undoRedoList.canUndo());
        derivationView.getRedoButton().setDisable(!undoRedoList.canRedo());
    }

    private void pushUndoRedo() {
        DerivationModel model = getDerivationModelFromView();
        DerivationModel deepCopy = (DerivationModel) SerializationUtils.clone(model);
        undoRedoList.push(deepCopy);
        updateUndoRedoButtons();
    }

    private void insertLineAction() {
        int row = -1;
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;

        if (row >= 0) {
            ViewLine viewLine = derivationView.getViewLines().get(row);
            int depth = viewLine.getDepth();
            if (!LineType.isSetupLine(viewLine.getLineType()) || viewLine.getLineType() == LineType.CONCLUSION_LINE) {
                addEmptyViewContentRow(row, depth);
                derivationView.setGridFromViewLines();
                pushUndoRedo();

                BoxedDRTA bdrta = derivationView.getViewLines().get(row).getLineContentBoxedDRTA();
                DecoratedRTA drta = bdrta.getDRTA();
                RichTextArea rta = bdrta.getRTA();
                rta.applyCss();
                rta.layout();
                exerciseModified = true;

                mainView.editorInFocus(drta, ControlType.FIELD);
                rta.focusedProperty().addListener((o, ov, nv) -> {
                    if (nv) {
                        mainView.editorInFocus(drta, ControlType.FIELD);
                    } else {
                        if (rta.isModified()) {
                            pushUndoRedo();
                            exerciseModified = true;
                        }
                    }
                });
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
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        List<ViewLine> viewLines = derivationView.getViewLines();
        if (row >= 0 && row < viewLines.size()) {
            if (viewLines.size() > 1) {

                if (!LineType.isSetupLine(viewLines.get(row).getLineType())) {
                    List<Label> clients = viewLines.get(row).getClientLabels();
                    for (Label label : clients) {
                        label.textProperty().unbind();
                        label.setText("??");
                    }
                    viewLines.remove(row);
                    exerciseModified = true;

                    if (row < viewLines.size()) {
                        LineType type = viewLines.get(row).getLineType();
                        if (LineType.isShelfLine(type) || LineType.isGapLine(type)) viewLines.remove(row);
                    }

                    derivationView.setGridFromViewLines();

                    pushUndoRedo();

                    for (int i = row; i < viewLines.size(); i++) {
                        if (viewLines.get(i).getLineContentBoxedDRTA() != null) {
                            viewLines.get(i).getLineContentBoxedDRTA().getRTA().requestFocus();
                            break;
                        }
                    }
                }
                else {
                    EditorAlerts.fleetingPopup("Cannot modify setup line.");
                }
            }
            else {
                EditorAlerts.fleetingPopup("A derivation must contain at least one line.");
            }
        }
        else {
            EditorAlerts.fleetingPopup("Select derivation line to delete.");
        }
    }

    private void indentLineAction() {
        int row = -1;
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            if (!LineType.isSetupLine(viewLine.getLineType())) {
                int depth = viewLine.getDepth();
                if (depth < 19) {
                    viewLine.setDepth(++depth);
                    viewLines.set(row, viewLine);
                    if (++row < viewLines.size()) {
                        ViewLine nextLine = viewLines.get(row);
                        if (LineType.isShelfLine(nextLine.getLineType())  || LineType.isGapLine(nextLine.getLineType())) {
                            nextLine.setDepth(depth);
                            viewLines.set(row, nextLine);
                        }
                    }
                    derivationView.setGridFromViewLines();
                    pushUndoRedo();
                    exerciseModified = true;


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
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            if (!LineType.isSetupLine(viewLine.getLineType())) {
                int depth = viewLine.getDepth();
                if (depth > 1) {
                    viewLine.setDepth(--depth);
                    viewLines.set(row, viewLine);
                    if (++row < viewLines.size()) {
                        ViewLine nextLine = viewLines.get(row);
                        if (LineType.isShelfLine(nextLine.getLineType()) || LineType.isGapLine(nextLine.getLineType())) {
                            if (depth > 1) {
                                nextLine.setDepth(depth);
                                viewLines.set(row, nextLine);
                            } else {
                                viewLines.remove(row);
                            }
                        }
                    }

                    derivationView.setGridFromViewLines();
                    pushUndoRedo();
                    exerciseModified = true;
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
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            int depth = viewLine.getDepth();
            if (!LineType.isSetupLine(viewLine.getLineType())) {
//                if (depth > 1) {
                    if (++row < viewLines.size()) {
                        if (!(LineType.isShelfLine(viewLines.get(row).getLineType()) || LineType.isGapLine(viewLines.get(row).getLineType()))) {
                            ViewLine shelfLine = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                            viewLines.add(row, shelfLine);
                            viewLine.getLineContentBoxedDRTA().getRTA().requestFocus();
                            derivationView.setGridFromViewLines();
                            pushUndoRedo();
                            exerciseModified = true;

                        } else {
                            EditorAlerts.fleetingPopup("No shelf on top of shelf or gap.");
                        }
                    } else {
                        EditorAlerts.fleetingPopup("No shelf under last line.");
                    }
 //               } else {
 //                   EditorAlerts.fleetingPopup("Cannot modify at leftmost scope depth.");
 //               }
            } else {
                EditorAlerts.fleetingPopup("Cannot modify setup line.");
            }
        } else {
            EditorAlerts.fleetingPopup("Select row to have shelf.");
        }
    }
    private void addGapLineAction() {
        int row = -1;
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = derivationView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            int depth = viewLine.getDepth();
            if (!LineType.isSetupLine(viewLine.getLineType())) {
                if (depth > 1) {
                    if (++row < viewLines.size()) {
                        if (!(LineType.isShelfLine(viewLines.get(row).getLineType())|| LineType.isGapLine(viewLines.get(row).getLineType()))) {
                            ViewLine gapLine = new ViewLine(null, depth, LineType.GAP_LINE, null, null, null);
                            viewLines.add(row, gapLine);
                            viewLine.getLineContentBoxedDRTA().getRTA().requestFocus();
                            derivationView.setGridFromViewLines();
                            pushUndoRedo();
                            exerciseModified = true;


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
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            ViewLine viewLine = derivationView.getViewLines().get(row);
            int depth = viewLine.getDepth();
            depth++;
            if (!LineType.isSetupLine(viewLine.getLineType()) || viewLine.getLineType() == LineType.CONCLUSION_LINE) {
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
//                addEmptyViewContentRow(row, depth);
                if (derivationModel.isDefaultShelf()) {
                    ViewLine shelfLine = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                    derivationView.getViewLines().add(row, shelfLine);
                }
                addEmptyViewContentRow(row, depth);

                derivationView.setGridFromViewLines();
                pushUndoRedo();
                exerciseModified = true;

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
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (derivationView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = derivationView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            ViewLine viewLine = derivationView.getViewLines().get(row);
            int depth = viewLine.getDepth();
            depth++;
            if (!LineType.isSetupLine(viewLine.getLineType()) || viewLine.getLineType() == LineType.CONCLUSION_LINE) {
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
//                addEmptyViewContentRow(row, depth);
                if (derivationModel.isDefaultShelf()) {
                    ViewLine shelfLine1 = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                    derivationView.getViewLines().add(row, shelfLine1);
                }
                addEmptyViewContentRow(row, depth);

                ViewLine gapLine = new ViewLine(null, depth, LineType.GAP_LINE,  null, null, null);
                derivationView.getViewLines().add(row, gapLine);

                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
 //               addEmptyViewContentRow(row, depth);
                if (derivationModel.isDefaultShelf()) {
                    ViewLine shelfLine2 = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                    derivationView.getViewLines().add(row, shelfLine2);
                }
                addEmptyViewContentRow(row, depth);
                derivationView.setGridFromViewLines();
                pushUndoRedo();
                exerciseModified = true;


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
        boolean success = DiskUtilities.saveExercise(saveAs, getDerivationModelFromView());
        if (success) exerciseModified = false;
    }


    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        derivationModel = getDerivationModelFromView();
        DerivationExercise exercise = new DerivationExercise(derivationModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth() / mainWindow.getBaseScale();

        //header node
        Label exerciseName = new Label(derivationModel.getExerciseName());
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

        //content node
        GridPane derivationPane = exercise.getExerciseView().getGrid();
        derivationPane.setPadding(new Insets(15,0,15,0));

        double width = derivationModel.getGridWidth() * nodeWidth;
        derivationPane.setMaxWidth(width);
        derivationPane.setMinWidth(width);
        HBox gridBox = new HBox(derivationPane);
        gridBox.setAlignment(Pos.CENTER);
        nodeList.add(gridBox);


        Separator contentSeparator = new Separator(Orientation.HORIZONTAL);
        contentSeparator.setStyle("-fx-stroke-dash-array:0.1 5.0");
        contentSeparator.setPrefWidth(100);
        HBox contentSepBox = new HBox(contentSeparator);
        contentSepBox.setMinWidth(nodeWidth);
        contentSepBox.setAlignment(Pos.CENTER);
        nodeList.add(contentSepBox);

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
    public Exercise<DerivationModel, DerivationView> resetExercise() {
        RichTextArea commentRTA = derivationView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        DerivationModel originalModel = (DerivationModel) (derivationModel.getOriginalModel());
        originalModel.setExerciseComment(commentDocument);
        DerivationExercise clearExercise = new DerivationExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {


        RichTextArea commentEditor = derivationView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) {
            exerciseModified = true;
        }

        List<ViewLine> viewLines = derivationView.getViewLines();
        for (ViewLine viewLine : viewLines) {
            if (LineType.isContentLine(viewLine.getLineType())) {
                RichTextArea rta = viewLine.getLineContentBoxedDRTA().getRTA();
                if (rta.isModified()) {
                    exerciseModified = true;
                }
            }
        }

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) { this.exerciseModified = modified; }


    @Override
    public ExerciseModel<DerivationModel> getExerciseModelFromView() {
        return (ExerciseModel) getDerivationModelFromView();
    }

    private DerivationModel getDerivationModelFromView() {
        String name = derivationView.getExerciseName();

        Boolean started = (derivationModel.isStarted() || exerciseModified);
        double statementHeight = derivationView.getExerciseStatement().getEditor().getPrefHeight();
        double gridWidth = derivationView.getContentSplitPane().getDividerPositions()[0];


        boolean leftmostScopeLine = derivationModel.isLeftmostScopeLine();
        boolean defaultShelf = derivationModel.isDefaultShelf();
        RichTextAreaSkin.KeyMapValue keyboardSelector = derivationModel.getKeyboardSelector();
        Document statementDocument = derivationModel.getExerciseStatement();

        RichTextArea commentRTA = derivationView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();

        List<ModelLine> modelLines = new ArrayList<>();
        List<ViewLine> viewLines = derivationView.getViewLines();
        for (int i = 0; i < viewLines.size(); i++) {
            ViewLine viewLine = viewLines.get(i);
            int depth = viewLine.getDepth();

            LineType lineType = viewLine.getLineType();
            Document lineContentDocument = null;
            String justification = "";
            if (LineType.isContentLine(lineType)) {

                RichTextArea lineContentRTA = viewLine.getLineContentBoxedDRTA().getRTA();

                boolean editable = lineContentRTA.isEditable();
                lineContentRTA.setEditable(true);

                lineContentRTA.setDisable(true);
                lineContentRTA.getActionFactory().saveNow().execute(new ActionEvent());
                lineContentRTA.setDisable(false);

                lineContentDocument = lineContentRTA.getDocument();
                lineContentRTA.setEditable(editable);

                justification = getStringFromJustificationFlow(viewLine.getJustificationFlow());
            }

            ModelLine modelLine = new ModelLine(depth, lineContentDocument, justification, lineType);
            modelLines.add(modelLine);
        }
        DerivationModel model = new DerivationModel(name, started, statementHeight,gridWidth, leftmostScopeLine, defaultShelf, keyboardSelector, statementDocument, commentDocument, modelLines);
        model.setOriginalModel(derivationModel.getOriginalModel());
        model.setCommentPrefHeight(derivationView.getCommentPrefHeight());
        model.setSplitPanePrefWidth(derivationView.getSplitPanePrefWidth());

        return model;
    }


}

package slapp.editor.derivation_explain;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.RichTextAreaSkin;
import com.gluonhq.richtextarea.model.Document;
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
import slapp.editor.derivation.LineType;
import slapp.editor.derivation.ModelLine;
import slapp.editor.derivation.ViewLine;
import slapp.editor.main_window.*;

import java.util.ArrayList;
import java.util.List;


public class DrvtnExpExercise implements Exercise<DrvtnExpModel, DrvtnExpView> {
    private MainWindow mainWindow;
    private DrvtnExpModel drvtnExpModel;
    private DrvtnExpModel originalModel;
    private DrvtnExpView drvtnExpView;
    private MainWindowView mainView;
    private boolean exerciseModified = false;

    private Font labelFont = new Font("Noto Serif Combo", 11);
    private boolean editJustification;
    private EventHandler justificationClickFilter;
    private RichTextArea lastJustificationRTA;
    private int lastJustificationRow;
    private UndoRedoList<DrvtnExpModel> undoRedoList = new UndoRedoList<>(20);


    public DrvtnExpExercise(DrvtnExpModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.drvtnExpModel = model;
        this.originalModel = model;
        this.mainView = mainWindow.getMainView();
        this.drvtnExpView = new DrvtnExpView(mainView);
        setDrvtnExpView();
        pushUndoRedo();
    }

    private void setDrvtnExpView() {

        drvtnExpView.setExerciseName(drvtnExpModel.getExerciseName());
        drvtnExpView.setContentPrompt(drvtnExpModel.getContentPrompt());
        drvtnExpView.setLeftmostScopeLine(drvtnExpModel.isLeftmostScopeLine());



        DecoratedRTA statementDRTA = new DecoratedRTA();
        RichTextArea statementEditor = statementDRTA.getEditor();
        statementEditor.setDocument(drvtnExpModel.getExerciseStatement());
        drvtnExpView.setStatementPrefHeight(drvtnExpModel.getStatementPrefHeight());
        statementEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(statementDRTA, ControlType.STATEMENT);
            }
        });
        drvtnExpView.setExerciseStatement(statementDRTA);

        DecoratedRTA commentDRTA = new DecoratedRTA();
        RichTextArea commentEditor = commentDRTA.getEditor();
        commentEditor.setDocument(drvtnExpModel.getExerciseComment());
        commentEditor.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(commentDRTA, ControlType.AREA);
            }
        });
        drvtnExpView.setExerciseComment(commentDRTA);

        DecoratedRTA explanationDRTA = new DecoratedRTA();
        RichTextArea explanationEditor = explanationDRTA.getEditor();

        explanationEditor.setDocument(drvtnExpModel.getExplanationDocument());
        explanationEditor.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(explanationDRTA, ControlType.AREA);
            }
        });
        drvtnExpView.setExplanationDRTA(explanationDRTA);


        drvtnExpView.getInsertLineButton().setOnAction(e -> insertLineAction());
        drvtnExpView.getDeleteLineButton().setOnAction(e -> deleteLineAction());
        drvtnExpView.getIndentButton().setOnAction(e -> indentLineAction());
        drvtnExpView.getOutdentButton().setOnAction(e -> outdentLineAction());
        drvtnExpView.getAddShelfButton().setOnAction(e -> addShelfLineAction());
        drvtnExpView.getAddGapButton().setOnAction(e -> addGapLineAction());
        drvtnExpView.getInsertSubButton().setOnAction(e -> insertSubAction());
        drvtnExpView.getInsertSubsButton().setOnAction(e -> insertSubsAction());
        drvtnExpView.getUndoButton().setOnAction(e -> undoAction());
        drvtnExpView.getRedoButton().setOnAction(e -> redoAction());

        drvtnExpView.initializeViewDetails();
        drvtnExpView.getSplitPane().setDividerPosition(0, drvtnExpModel.getGridWidth());
        drvtnExpView.getSplitPane().getDividers().get(0).positionProperty().addListener((ob, ov, nv) -> {
            double diff = (double) nv - (double) ov;
            if (Math.abs(diff) >= .02) exerciseModified = true;
        });

        setViewLinesFromModel();
        drvtnExpView.setGridFromViewLines();

        setContentFocusListeners();
    }

    private void setViewLinesFromModel() {

        List<ModelLine> modelLines = drvtnExpModel.getDerivationLines();
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
                    int row = drvtnExpView.getGrid().getRowIndex(rta.getParent());

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
        drvtnExpView.setViewLines(viewLines);
    }

    private void setContentFocusListeners() {
        List<ViewLine> viewLines = drvtnExpView.getViewLines();
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
        ViewLine line = null;
        row++;
        for (int i = row; i < drvtnExpView.getViewLines().size(); i++) {
            ViewLine temp = drvtnExpView.getViewLines().get(i);
            if (LineType.isContentLine(temp.getLineType())) {
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
            ViewLine temp = drvtnExpView.getViewLines().get(i);
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
        flow.setMaxWidth(100);
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
        int rowIndex = drvtnExpView.getGrid().getRowIndex(flow);
        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        rta.setContentAreaWidth(110);
        rta.setPrefHeight(20);
        rta.setMaxWidth(100);
        rta.setMinWidth(100);
        rta.getStylesheets().add("slappDerivation.css");
        rta.setDocument(new Document(getStringFromJustificationFlow(flow)));
        rta.getActionFactory().saveNow().execute(new ActionEvent());

        if (drvtnExpView.getViewLines().get(rowIndex).getLineType() == LineType.PREMISE_LINE) rta.setEditable(false);
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


            int row = drvtnExpView.getGrid().getRowIndex(rta);

            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                drvtnExpView.setGridFromViewLines();
                ViewLine contentLineBelow = getContentLineBelow(row);
                if (contentLineBelow != null) contentLineBelow.getLineContentBoxedDRTA().getRTA().requestFocus();
                e.consume();
            } else if (code == KeyCode.LEFT && e.isShortcutDown()) {
                drvtnExpView.setGridFromViewLines();
                ViewLine currentLine = drvtnExpView.getViewLines().get(row);
                currentLine.getLineContentBoxedDRTA().getRTA().requestFocus();
                e.consume();
            } else if (code == KeyCode.UP ) {
                drvtnExpView.setGridFromViewLines();
                ViewLine contentLineAbove = getContentLineAbove(row);
                if (contentLineAbove != null) contentLineAbove.getJustificationFlow().requestFocus();
                e.consume();
            } else if (code == KeyCode.DOWN ) {
                drvtnExpView.setGridFromViewLines();
                ViewLine contentLineBelow = getContentLineBelow(row);
                if (contentLineBelow != null)  contentLineBelow.getJustificationFlow().requestFocus();
                e.consume();
            }

        });

        drvtnExpView.getGrid().add(rta, 22, rowIndex);
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

        TextFlow justificationFlow = getJustificationFlow(justificationString, drvtnExpView.getViewLines());
        drvtnExpView.getViewLines().get(rowIndex).setJustificationFlow(justificationFlow);

        drvtnExpView.setGridFromViewLines();

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
            int row = drvtnExpView.getGrid().getRowIndex(rta.getParent());

            if (code == KeyCode.ENTER || (code == KeyCode.RIGHT && e.isShortcutDown())) {
                drvtnExpView.getViewLines().get(row).getJustificationFlow().requestFocus();
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

        drvtnExpView.getViewLines().add(newRow, viewLine);
    }

    private void undoAction() {
        DrvtnExpModel undoElement = undoRedoList.getUndoElement();
        if (undoElement != null) {
            drvtnExpModel = (DrvtnExpModel) SerializationUtils.clone(undoElement);
            setViewLinesFromModel();
            drvtnExpView.setGridFromViewLines();
            updateUndoRedoButtons();
            setContentFocusListeners();
        }
    }

    private void redoAction() {
        DrvtnExpModel redoElement = undoRedoList.getRedoElement();
        if (redoElement != null) {
            drvtnExpModel = (DrvtnExpModel) SerializationUtils.clone(redoElement);
            setViewLinesFromModel();
            drvtnExpView.setGridFromViewLines();
            updateUndoRedoButtons();
            setContentFocusListeners();
        }
    }

    private void updateUndoRedoButtons() {
        drvtnExpView.getUndoButton().setDisable(!undoRedoList.canUndo());
        drvtnExpView.getRedoButton().setDisable(!undoRedoList.canRedo());
    }

    private void pushUndoRedo() {
        DrvtnExpModel model = getDrvtnExpModelFromView();
        DrvtnExpModel deepCopy = (DrvtnExpModel) SerializationUtils.clone(model);
        undoRedoList.push(deepCopy);
        updateUndoRedoButtons();
    }

    private void insertLineAction() {
        int row = -1;
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;

        if (row >= 0) {
            ViewLine viewLine = drvtnExpView.getViewLines().get(row);
            int depth = viewLine.getDepth();
            if (!LineType.isSetupLine(viewLine.getLineType()) || viewLine.getLineType() == LineType.CONCLUSION_LINE) {
                addEmptyViewContentRow(row, depth);
                drvtnExpView.setGridFromViewLines();
                pushUndoRedo();

                BoxedDRTA bdrta = drvtnExpView.getViewLines().get(row).getLineContentBoxedDRTA();
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
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        List<ViewLine> viewLines = drvtnExpView.getViewLines();
        if (row >= 0 && row < viewLines.size()) {

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

                drvtnExpView.setGridFromViewLines();

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
            EditorAlerts.fleetingPopup("Select derivation row to delete.");
        }
    }

    private void indentLineAction() {
        int row = -1;
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = drvtnExpView.getViewLines();
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
                    drvtnExpView.setGridFromViewLines();
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
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = drvtnExpView.getViewLines();
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

                    drvtnExpView.setGridFromViewLines();
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
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = drvtnExpView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            int depth = viewLine.getDepth();
            if (!LineType.isSetupLine(viewLine.getLineType())) {
                if (depth > 1) {
                    if (++row < viewLines.size()) {
                        if (!(LineType.isShelfLine(viewLines.get(row).getLineType()) || LineType.isGapLine(viewLines.get(row).getLineType()))) {
                            ViewLine shelfLine = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                            viewLines.add(row, shelfLine);
                            viewLine.getLineContentBoxedDRTA().getRTA().requestFocus();
                            drvtnExpView.setGridFromViewLines();
                            pushUndoRedo();
                            exerciseModified = true;
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
        Node lastFocusedNode = mainWindow.getLastFocusOwner();
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            List<ViewLine> viewLines = drvtnExpView.getViewLines();
            ViewLine viewLine = viewLines.get(row);
            int depth = viewLine.getDepth();
            if (!LineType.isSetupLine(viewLine.getLineType())) {
                if (depth > 1) {
                    if (++row < viewLines.size()) {
                        if (!(LineType.isShelfLine(viewLines.get(row).getLineType())|| LineType.isGapLine(viewLines.get(row).getLineType()))) {
                            ViewLine gapLine = new ViewLine(null, depth, LineType.GAP_LINE, null, null, null);
                            viewLines.add(row, gapLine);
                            viewLine.getLineContentBoxedDRTA().getRTA().requestFocus();
                            drvtnExpView.setGridFromViewLines();
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
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            ViewLine viewLine = drvtnExpView.getViewLines().get(row);
            int depth = viewLine.getDepth();
            depth++;
            if (!LineType.isSetupLine(viewLine.getLineType()) || viewLine.getLineType() == LineType.CONCLUSION_LINE) {
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                if (drvtnExpModel.isDefaultShelf()) {
                    ViewLine shelfLine = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                    drvtnExpView.getViewLines().add(row, shelfLine);
                }
                addEmptyViewContentRow(row, depth);

                drvtnExpView.setGridFromViewLines();
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
        if (drvtnExpView.getGrid().getChildren().contains(lastFocusedNode.getParent())) row = drvtnExpView.getGrid().getRowIndex(lastFocusedNode.getParent());
        else if (lastJustificationRTA == lastFocusedNode) row = lastJustificationRow;
        if (row >= 0) {
            ViewLine viewLine = drvtnExpView.getViewLines().get(row);
            int depth = viewLine.getDepth();
            depth++;
            if (!LineType.isSetupLine(viewLine.getLineType()) || viewLine.getLineType() == LineType.CONCLUSION_LINE) {
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                if (drvtnExpModel.isDefaultShelf()) {
                    ViewLine shelfLine1 = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                    drvtnExpView.getViewLines().add(row, shelfLine1);
                }
                addEmptyViewContentRow(row, depth);

                ViewLine gapLine = new ViewLine(null, depth, LineType.GAP_LINE,  null, null, null);
                drvtnExpView.getViewLines().add(row, gapLine);

                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                addEmptyViewContentRow(row, depth);
                if (drvtnExpModel.isDefaultShelf()) {
                    ViewLine shelfLine2 = new ViewLine(null, depth, LineType.SHELF_LINE, null, null, null);
                    drvtnExpView.getViewLines().add(row, shelfLine2);
                }
                addEmptyViewContentRow(row, depth);
                drvtnExpView.setGridFromViewLines();
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
    public DrvtnExpModel getExerciseModel() { return drvtnExpModel; }
    @Override
    public DrvtnExpView getExerciseView() { return drvtnExpView; }
    @Override
    public void saveExercise(boolean saveAs) {
        boolean success = DiskUtilities.saveExercise(saveAs, getDrvtnExpModelFromView());
        if (success) exerciseModified = false;
    }
    @Override
    public void printExercise() { PrintUtilities.printExercise(getPrintNodes(), drvtnExpModel.getExerciseName()); }

    @Override
    public void exportExerciseToPDF() { PrintUtilities.exportExerciseToPDF(getPrintNodes(), drvtnExpModel.getExerciseName()); }

    @Override
    public List<Node> getPrintNodes() {
        List<Node> nodeList = new ArrayList<>();
        drvtnExpModel = getDrvtnExpModelFromView();
        DrvtnExpExercise exercise = new DrvtnExpExercise(drvtnExpModel, mainWindow);
        double nodeWidth = PrintUtilities.getPageWidth();

        //header node
        Label exerciseName = new Label(drvtnExpModel.getExerciseName());
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
        nodeList.add(new Separator(Orientation.HORIZONTAL));

        //statement node
        RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
        statementRTA.setEditable(true);
        RichTextAreaSkin statementRTASkin = ((RichTextAreaSkin) statementRTA.getSkin());
        double statementHeight = statementRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        statementRTA.setPrefHeight(statementHeight + 35.0);
        statementRTA.setContentAreaWidth(nodeWidth);
        statementRTA.setPrefWidth(nodeWidth);
        statementRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(statementRTA);

        Separator statementSeparator = new Separator(Orientation.HORIZONTAL);
        statementSeparator.setPrefWidth(100);
        HBox statementSepBox = new HBox(statementSeparator);
        statementSepBox.setAlignment(Pos.CENTER);
        nodeList.add(statementSepBox);

        //content node
        GridPane derivationPane = exercise.getExerciseView().getGrid();
        derivationPane.setPadding(new Insets(15,0,15,0));

        double width = drvtnExpModel.getGridWidth() * PrintUtilities.getPageWidth();
        derivationPane.setMaxWidth(width);
        derivationPane.setMinWidth(width);
        HBox gridBox = new HBox(derivationPane);
        gridBox.setAlignment(Pos.CENTER);
        nodeList.add(gridBox);

        RichTextArea explanationRTA = exercise.getExerciseView().getExplanationDRTA().getEditor();
        RichTextAreaSkin explanationRTASkin = ((RichTextAreaSkin) explanationRTA.getSkin());
        double explanationHeight = explanationRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        explanationRTA.setPrefHeight(explanationHeight + 35.0);
        explanationRTA.setContentAreaWidth(nodeWidth);
        explanationRTA.setPrefWidth(nodeWidth);
        explanationRTA.getStylesheets().clear(); statementRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(explanationRTA);

        Separator contentSeparator = new Separator(Orientation.HORIZONTAL);
        contentSeparator.setStyle("-fx-stroke-dash-array:0.1 5.0");
        contentSeparator.setPrefWidth(100);
        HBox contentSepBox = new HBox(contentSeparator);
        contentSepBox.setAlignment(Pos.CENTER);
        nodeList.add(contentSepBox);

        //comment node
        RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
        RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentRTA.getSkin());
        double commentHeight = commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
        commentRTA.setPrefHeight(Math.max(70, commentHeight + 35.0));
        commentRTA.setContentAreaWidth(PrintUtilities.getPageWidth());
        commentRTA.setPrefWidth(nodeWidth);
        commentRTA.getStylesheets().clear(); commentRTA.getStylesheets().add("richTextAreaPrinter.css");
        nodeList.add(commentRTA);

        return nodeList;
    }

    @Override
    public Exercise<DrvtnExpModel, DrvtnExpView> resetExercise() {
        RichTextArea commentRTA = drvtnExpView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();
        originalModel.setExerciseComment(commentDocument);

        DrvtnExpExercise clearExercise = new DrvtnExpExercise(originalModel, mainWindow);
        return clearExercise;
    }

    @Override
    public boolean isExerciseModified() {


        RichTextArea commentEditor = drvtnExpView.getExerciseComment().getEditor();
        if (commentEditor.isModified()) exerciseModified = true;


        RichTextArea explanationEditor = drvtnExpView.getExplanationDRTA().getEditor();
        if (explanationEditor.isModified()) exerciseModified = true;


        List<ViewLine> viewLines = drvtnExpView.getViewLines();
        for (ViewLine viewLine : viewLines) {
            if (LineType.isContentLine(viewLine.getLineType())) {
                RichTextArea rta = viewLine.getLineContentBoxedDRTA().getRTA();
                if (rta.isModified()) exerciseModified = true;
            }
        }

        return exerciseModified;
    }

    @Override
    public void setExerciseModified(boolean modified) { this.exerciseModified = modified; }

    @Override
    public void updateContentHeight(Node focusedNode, boolean isRequired) {

        if (isContainer(drvtnExpView.getSplitPane(), focusedNode)) {
            double gridHeight = drvtnExpView.getGridHeight();
            mainWindow.getMainView().updatePageSizeLabels(gridHeight + 20);
            mainWindow.getLastFocusOwner().requestFocus();

        } else if (isContainer(drvtnExpView.getExplanationDRTA().getEditor(), focusedNode)) {
            DrvtnExpModel model = getDrvtnExpModelFromView();
            DrvtnExpExercise exercise = new DrvtnExpExercise(model, mainWindow);
            RichTextArea explanationRTA = exercise.getExerciseView().getExplanationDRTA().getEditor();
            RichTextAreaSkin explanationRTASkin = ((RichTextAreaSkin) explanationRTA.getSkin());
            double explanationHeight = explanationRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(explanationHeight + 20);
            mainWindow.getLastFocusOwner().requestFocus();
        }
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

    @Override
    public void updateCommentHeight(boolean isRequired) {
        if (isRequired || mainWindow.getLastFocusOwner() != drvtnExpView.getExerciseComment().getEditor()) {
            mainWindow.setLastFocusOwner(drvtnExpView.getExerciseComment().getEditor());

            DrvtnExpModel model = getDrvtnExpModelFromView();
            DrvtnExpExercise exercise = new DrvtnExpExercise(model, mainWindow);
            RichTextArea commentRTA = exercise.getExerciseView().getExerciseComment().getEditor();
            RichTextAreaSkin commentRTASkin = ((RichTextAreaSkin) commentRTA.getSkin());
            double commentHeight = commentRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(Math.max(70, commentHeight + 35));
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }

    @Override
    public void updateStatementHeight(boolean isRequired) {
        if (isRequired || mainWindow.getLastFocusOwner() != drvtnExpView.getExerciseStatementNode()) {
            mainWindow.setLastFocusOwner(drvtnExpView.getExerciseStatementNode());

            drvtnExpModel = getDrvtnExpModelFromView();
            DrvtnExpExercise exercise = new DrvtnExpExercise(drvtnExpModel, mainWindow);
            RichTextArea statementRTA = exercise.getExerciseView().getExerciseStatement().getEditor();
            statementRTA.setEditable(true);
            RichTextAreaSkin statementRTASkin = ((RichTextAreaSkin) statementRTA.getSkin());
            double statementHeight = statementRTASkin.getContentAreaHeight(PrintUtilities.getPageWidth(), PrintUtilities.getPageHeight());
            mainWindow.getMainView().updatePageSizeLabels(statementHeight + 35);
            mainWindow.getLastFocusOwner().requestFocus();
        }
    }

    @Override
    public ExerciseModel<DrvtnExpModel> getExerciseModelFromView() {
        return (ExerciseModel) getDrvtnExpModelFromView();
    }

    private DrvtnExpModel getDrvtnExpModelFromView() {
        String name = drvtnExpView.getExerciseName();
        String prompt = drvtnExpView.getContentPrompt();
        Boolean started = (drvtnExpModel.isStarted() || exerciseModified);
        double statementHeight = drvtnExpView.getExerciseStatement().getEditor().getPrefHeight();
        double gridWidth = drvtnExpView.getSplitPane().getDividerPositions()[0];


        boolean leftmostScopeLine = drvtnExpModel.isLeftmostScopeLine();
        boolean defaultShelf = drvtnExpModel.isDefaultShelf();
        Document statementDocument = drvtnExpModel.getExerciseStatement();

        RichTextArea commentRTA = drvtnExpView.getExerciseComment().getEditor();
        commentRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document commentDocument = commentRTA.getDocument();

        RichTextArea explanationRTA = drvtnExpView.getExplanationDRTA().getEditor();
        explanationRTA.getActionFactory().saveNow().execute(new ActionEvent());
        Document explanationDocument = explanationRTA.getDocument();


        List<ModelLine> modelLines = new ArrayList<>();
        List<ViewLine> viewLines = drvtnExpView.getViewLines();
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


        DrvtnExpModel model = new DrvtnExpModel(name, started, statementHeight, gridWidth, prompt, leftmostScopeLine, defaultShelf, statementDocument, commentDocument, explanationDocument, modelLines);

        return model;
    }


}

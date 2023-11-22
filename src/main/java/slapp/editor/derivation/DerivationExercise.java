package slapp.editor.derivation;

import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

    public DerivationExercise(DerivationModel model, MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.derivationModel = model;
        this.mainView = mainWindow.getMainView();
        this.derivationView = new DerivationView(mainView);

        mainView.getMainScene().focusOwnerProperty().addListener((ob, ov, nv) -> {
            lastFocusedNode = ov;
//            System.out.println(nv.toString());
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

    private void setEmptyViewContentRow(int row, int depth) {
        Label numLabel = new Label();
        numLabel.setFont(labelFont);
        TextFlow flow = new TextFlow();
        TextFlow justificationFlow = getStyledJustificationFlow(flow);
        ViewLine viewLine = new ViewLine(numLabel, depth, LineType.CONTENT_LINE, false, new DecoratedRTA(), justificationFlow, new ArrayList<>());
        derivationView.getViewLines().add(row, viewLine);
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

                rta.setStyle("-fx-border-color: green; -fx-border-width: 1 1 1 1");

                rta.focusedProperty().addListener((o, ov, nv) -> {
                    if (nv) {
                        mainView.editorInFocus(drta);
                    }
                });
                viewLine.setLineContentDRTA(drta);

                TextFlow justificationFlow = getJustificationFlow(modelLine.getJustification(), viewLines, i);
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

    private TextFlow getJustificationFlow(String justification, List<ViewLine> viewLines, int gridRow) {
        justification = justification.trim();
        TextFlow flow = getStyledJustificationFlow(new TextFlow());

        if (!justification.isEmpty()) {

            //get List of alternating digit and non-digit sequences
            List<String> split = new ArrayList();
            boolean startsDigit = false;
            if (charIsDigit(justification.charAt(0))) startsDigit = true;
            StringBuilder builder = new StringBuilder();
            boolean buildingDigit = startsDigit;
            int j = 0;
            while (j < justification.length()) {

                if (charIsDigit(justification.charAt(j)) == buildingDigit) {
                    builder.append(justification.charAt(j));
                    j++;
                }
                else {
                    split.add(builder.toString());
                    builder.delete(0, builder.length());
                    buildingDigit = charIsDigit(justification.charAt(j));
                }
            }
            split.add(builder.toString());

            //get flow of labels and texts with labels bound to line numbers
            boolean buildingNum = startsDigit;
            for (int i = 0; i < split.size(); i++) {
                if (buildingNum) {
                    Label label = new Label(split.get(i));
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
                    Text text = new Text(split.get(i));
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
        flow.setPrefWidth(100);
        flow.setMaxHeight(20);
        flow.setPrefHeight(20);
        flow.setStyle("-fx-border-color: red; -fx-border-width: 1 1 1 1; ");

        flow.setFocusTraversable(true);
        flow.setOnMouseClicked(e -> flow.requestFocus());
        flow.focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                editJustificationField(flow);
            }
        });
        return flow;
    }

    private void editJustificationField(TextFlow flow) {
        int r = derivationView.getGrid().getRowIndex(flow);
        DecoratedRTA drta = new DecoratedRTA();
        RichTextArea rta = drta.getEditor();
        rta.setContentAreaWidth(300);
        rta.setPrefHeight(19);
        rta.setPrefWidth(100);
        rta.getStylesheets().add("slappDerivation.css");

        rta.setStyle("-fx-border-color: green; -fx-border-width: 1 1 1 1");

        rta.setDocument(new Document(getStringFromJustificationFlow(flow)));



        rta.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                mainView.editorInFocus(drta);
            } else {
                rta.getActionFactory().saveNow().execute(new ActionEvent());
                String justificationString = rta.getDocument().getText();
                TextFlow justificationFlow = getJustificationFlow(justificationString, derivationView.getViewLines(), r);
                derivationView.getViewLines().get(r).setJustificationFlow(justificationFlow);
                derivationView.setGridFromViewLines();
            }
        });
        derivationView.getGrid().add(rta, 22, r);
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

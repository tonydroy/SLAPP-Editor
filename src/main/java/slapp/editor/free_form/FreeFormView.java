package slapp.editor.free_form;

import com.gluonhq.richtextarea.RichTextArea;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import slapp.editor.decorated_rta.DecoratedRTA;
import slapp.editor.main_window.ExerciseView;
import slapp.editor.main_window.MainWindowView;

import java.util.ArrayList;
import java.util.List;

public class FreeFormView implements ExerciseView<DecoratedRTA> {

    private MainWindowView mainView;
    private String exerciseName = "";
    private DecoratedRTA statementDRTA = new DecoratedRTA();
    private DecoratedRTA commentDRTA = new DecoratedRTA();
    private double statementPrefHeight = 0;
    private double commentPrefHeight = 0;
    private Node exerciseControlNode;
    private Node freeFormControlNode;
    private List<ViewElement> viewElements = new ArrayList<>();
    private VBox contentBox = new VBox();

    private Spinner<Double> statementHeightSpinner;
    private Spinner<Double> statementWidthSpinner;
    private Spinner<Double> commentHeightSpinner;
    private Spinner<Double> commentWidthSpinner;
    private Node currentSpinnerNode;

    private Button indentButton;
    private Button outdentButton;
    private Button removeButton;
    private Button restoreButton;
    private Button addEditButton;
    private Button addHTreeButton;
    private Button addTTableButton;
    private Button addVTreeBaseItalButton;
    private Button addVTreeItalSansButton;
    private Button addVTreeScriptItalButton;
    private Button addNDrvtnItalSansButton;
    private Button addNDrvtnScriptItalButton;
    private Button addNDrvtnScriptSansButton;
    private Button addNDrvtnItalBBButton;
    private Button addADrvtnItalSansButton;
    private Button addADrvtnScriptItalButton;
    private Button addADrvtnScriptSansButton;
    private Button addADrvtnItalBBButton;




    public FreeFormView(MainWindowView mainView) {
        this.mainView = mainView;

        indentButton = newControlsButton("Indent", "Indent current window.");
        outdentButton = newControlsButton("Outdent", "Outdent current window.");
        removeButton = newControlsButton("Remove", "Remove current window.");
        restoreButton = newControlsButton ("Restore", "Restore last removed item.");
        addEditButton = newControlsButton("Simple Edit", "Insert simple editor.");
        addTTableButton = newControlsButton("Truth Table", "Insert truth table.");
        addHTreeButton = newControlsButton("Horiz Tree", "Insert horizontal tree.");
        addVTreeBaseItalButton = newControlsButton("Vert Tree", "Insert vertical tree.");
        addVTreeItalSansButton = newControlsButton("Vert Tree", "Insert vertical tree.");
        addVTreeScriptItalButton = newControlsButton("Vert Tree", "Insert vertical tree.");
        addNDrvtnItalSansButton = newControlsButton("Nat Drvtn", "Insert natural derivation.");
        addNDrvtnScriptItalButton = newControlsButton("Nat Drvtn", "Insert natural derivation.");
        addNDrvtnScriptSansButton = newControlsButton("Nat Drvtn", "Insert natural derivation.");
        addNDrvtnItalBBButton = newControlsButton("Nat Drvtn", "Insert natural derivation.");
        addADrvtnItalSansButton = newControlsButton("Ax Drvtn", "Insert axiomatic derivation.");
        addADrvtnScriptItalButton = newControlsButton("Ax Drvtn", "Insert axiomatic derivation.");
        addADrvtnScriptSansButton = newControlsButton("Ax Drvtn", "Insert axiomatic derivation.");
        addADrvtnItalBBButton = newControlsButton("Ax Drvtn", "Insert axiomatic derivation.");


        VBox rightControlBox = new VBox(20);
        rightControlBox.setAlignment(Pos.BASELINE_LEFT);
        rightControlBox.setPadding(new Insets(40,20,0,20));
        freeFormControlNode = rightControlBox;


    }

    private Button newControlsButton(String name, String tip) {
        Button button = new Button(name);
        button.setPrefWidth(100);
        button.setPrefHeight(28);
        button.setTooltip(new Tooltip(tip));
        return button;
    }

    void initializeViewDetails() {

        RichTextArea statementRTA = statementDRTA.getEditor();
        statementRTA.getStylesheets().add("slappTextArea.css");
        statementRTA.setEditable(false);

        double statementInitialHeight = Math.round(statementPrefHeight / mainView.getScalePageHeight() * 100.0);
        statementHeightSpinner = new Spinner<>(0.0, 999.0, statementInitialHeight, 1.0);
        statementHeightSpinner.setPrefWidth(60);
        statementHeightSpinner.setDisable(false);
        statementHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        statementHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = statementHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = statementHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        statementRTA.maxWidthProperty().bind(mainView.scalePageWidthProperty());
        statementWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        statementWidthSpinner.setPrefWidth(60);
        statementWidthSpinner.setDisable(true);
        statementWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        statementRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != statementRTA) {
                currentSpinnerNode = statementRTA;
                mainView.updateSizeSpinners(statementHeightSpinner, statementWidthSpinner);
            }
        });

        //comment rta
        RichTextArea commentRTA = commentDRTA.getEditor();
        commentRTA.getStylesheets().add("slappTextArea.css");
        commentRTA.setPromptText("Commment:");

        double commentInitialHeight = Math.round(commentPrefHeight / mainView.getScalePageHeight() * 100.0);
        commentHeightSpinner = new Spinner<>(0.0, 999.0, commentInitialHeight, 1.0);
        commentHeightSpinner.setPrefWidth(60);
        commentHeightSpinner.setDisable(false);
        commentHeightSpinner.setTooltip(new Tooltip("Height as % of selected paper"));
        commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(mainView.scalePageHeightProperty(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        commentHeightSpinner.valueProperty().addListener((obs, ov, nv) -> {
            Node increment = commentHeightSpinner.lookup(".increment-arrow-button");
            if (increment != null) increment.getOnMouseReleased().handle(null);
            Node decrement = commentHeightSpinner.lookup(".decrement-arrow-button");
            if (decrement != null) decrement.getOnMouseReleased().handle(null);
        });

        commentRTA.maxWidthProperty().bind(mainView.scalePageWidthProperty());
        commentWidthSpinner = new Spinner<>(0.0, 999.0, 100, 1.0);
        commentWidthSpinner.setPrefWidth(60);
        commentWidthSpinner.setDisable(true);
        commentWidthSpinner.setTooltip(new Tooltip("Width as % of selected paper"));

        commentRTA.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (currentSpinnerNode != commentRTA) {
                currentSpinnerNode = commentRTA;
                mainView.updateSizeSpinners(commentHeightSpinner, commentWidthSpinner);
            }
        });


        //page height listener
        mainView.scalePageHeightProperty().addListener((ob, ov, nv) -> {

            statementRTA.prefHeightProperty().unbind();
            statementHeightSpinner.getValueFactory().setValue((double) Math.round(statementHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            statementRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(statementHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));

            commentRTA.prefHeightProperty().unbind();
            commentHeightSpinner.getValueFactory().setValue((double) Math.round(commentHeightSpinner.getValue() * ov.doubleValue() / nv.doubleValue()));
            commentRTA.prefHeightProperty().bind(Bindings.max(45.0, Bindings.multiply(nv.doubleValue(), DoubleProperty.doubleProperty(commentHeightSpinner.getValueFactory().valueProperty()).divide(100.0))));
        });
    }

    void updateContentFromViewElements() {
        contentBox.getChildren().clear();
        for (ViewElement element : viewElements) {
            Node node = element.getNode();
            Region spacer = new Region();
            spacer.setPrefWidth(20.0 * element.getIndentLevel());
            HBox windowBox = new HBox(spacer, node);
            contentBox.getChildren().add(windowBox);
        }
    }


    public List<ViewElement> getViewElements() {      return viewElements;  }
    public Button getIndentButton() {     return indentButton;  }

    public Button getOutdentButton() {     return outdentButton;   }

    public Button getRemoveButton() {     return removeButton;   }

    public Button getRestoreButton() {     return restoreButton;  }

    public Button getAddEditButton() {     return addEditButton;  }

    public Button getAddHTreeButton() {     return addHTreeButton;  }

    public Button getAddTTableButton() {      return addTTableButton;  }

    public Button getAddVTreeBaseItalButton() {      return addVTreeBaseItalButton;   }

    public Button getAddVTreeItalSansButton() {     return addVTreeItalSansButton;   }
    public Button getAddVTreeScriptItalButton() { return addVTreeScriptItalButton; }

    public Button getAddNDrvtnItalSansButton() {     return addNDrvtnItalSansButton;   }

    public Button getAddNDrvtnScriptItalButton() {    return addNDrvtnScriptItalButton;   }
    public Button getAddNDrvtnScriptSansButton() {    return addNDrvtnScriptSansButton;   }

    public Button getAddNDrvtnItalBBButton() {     return addNDrvtnItalBBButton;  }

    public Button getAddADrvtnItalSansButton() {     return addADrvtnItalSansButton;   }

    public Button getAddADrvtnScriptItalButton() {     return addADrvtnScriptItalButton;  }
    public Button getAddADrvtnScriptSansButton() {     return addADrvtnScriptSansButton;  }

    public Button getAddADrvtnItalBBButton() {     return addADrvtnItalBBButton;   }

    public double getCommentPrefHeight() {    return commentDRTA.getEditor().getPrefHeight(); }
    public void setCommentPrefHeight(double commentPrefHeight) {     this.commentPrefHeight = commentPrefHeight;   }

    public void setExerciseControlNode(Node exerciseControlNode) {   this.exerciseControlNode = exerciseControlNode;  }

    @Override
    public String getExerciseName() { return exerciseName;  }

    @Override
    public void setExerciseName(String name) { exerciseName = name; }

    @Override
    public DecoratedRTA getExerciseComment() { return commentDRTA;  }

    @Override
    public void setExerciseComment(DecoratedRTA exerciseComment) { commentDRTA = exerciseComment;   }

    @Override
    public double getCommentHeight() { return commentDRTA.getEditor().getHeight();   }

    @Override
    public DecoratedRTA getExerciseStatement() {  return statementDRTA;   }

    @Override
    public void setExerciseStatement(DecoratedRTA exerciseStatement) { statementDRTA = exerciseStatement;   }

    @Override
    public Node getExerciseStatementNode() { return statementDRTA.getEditor();  }

    @Override
    public double getStatementHeight() { return statementDRTA.getEditor().getHeight();   }

    @Override
    public void setStatementPrefHeight(double height) {
        statementPrefHeight = height;
        statementDRTA.getEditor().setPrefHeight(height);
    }

    @Override
    public Node getExerciseContentNode() {   return contentBox;   }

    @Override
    public Node getExerciseControl() {  return exerciseControlNode;   }
    @Override
    public Node getRightControl() { return freeFormControlNode; }


    //vesitigal
    @Override
    public DoubleProperty getContentHeightProperty() {       return null;  }
    @Override
    public DoubleProperty getContentWidthProperty() {     return null;  }
    @Override
    public double getContentFixedHeight() {      return 0;   }
    @Override
    public double getContentWidth() {      return 0;  }
    @Override
    public double getContentHeight() {      return 0;  }
}

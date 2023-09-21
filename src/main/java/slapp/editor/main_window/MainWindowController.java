package slapp.editor.main_window;

import com.gluonhq.richtextarea.model.DecorationModel;
import com.gluonhq.richtextarea.model.Document;
import com.gluonhq.richtextarea.model.ParagraphDecoration;
import com.gluonhq.richtextarea.model.TextDecoration;
import javafx.stage.Stage;

import java.util.List;

public class MainWindowController {

    MainWindowController mainController;
    MainWindowView mainView;
    MainWindowModel mainModel;

    public MainWindowController(Stage stage) {
        mainController = this;
        mainView = new MainWindowView(stage, mainController);
        mainModel = new MainWindowModel();
    }

    //stub
    public Document getAssignmentInfo() {
        Document assignmentInfo = new Document("Assignment Info:\n More info:",
               List.of(new DecorationModel(0, 27,
                   TextDecoration.builder().presets().fontSize(11).build(),
                   ParagraphDecoration.builder().presets().build())), 0);
        return assignmentInfo;
    }

}

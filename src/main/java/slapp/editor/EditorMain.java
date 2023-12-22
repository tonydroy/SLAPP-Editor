package slapp.editor;

import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.application.*;
import slapp.editor.main_window.MainWindow;
import slapp.editor.tests.*;

public class EditorMain extends Application {

    public static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon32x32.png")));
        stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
//        MainWindow mainWindowController = new MainWindow();

//        ExtendedDemo demo = new ExtendedDemo(stage); demo.start();
//        DerivationTest test = new DerivationTest(); test.testGrid(stage);
//        ExtractFromDoc test = new ExtractFromDoc(); test.rtaInsertTest(stage);
        ParseTableDocTest test = new ParseTableDocTest(); test.parseTableDocTest(stage);
    }


}


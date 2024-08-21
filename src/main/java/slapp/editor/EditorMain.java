/*
Copyright (c) 2024 Tony Roy

This file is part of the Symbolic Logic APPlication (SLAPP).

SLAPP is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

SLAPP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with SLAPP.
If not, see <https://www.gnu.org/licenses/>.
 */

package slapp.editor;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.application.*;
import slapp.editor.main_window.MainWindow;
import slapp.editor.decorated_rta.*;
import slapp.editor.tests.*;
import slapp.editor.decorated_rta.*;

public class EditorMain extends Application {

    public static Stage mainStage;
    public static ObservableList<Image> icons;
    public static boolean secondaryCopy = false;
    public static String os;
    public static Image emptyImage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;

        secondaryCopy = JustOneLock.isAppActive();
        os = System.getProperty("os.name");

        if (!secondaryCopy) {
            stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon32x32.png")));
            stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        }
        else {
            stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon_purple32x32.png")));
            stage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon_purple16x16.png")));
        }
        icons = stage.getIcons();

        emptyImage = new Image(EditorMain.class.getResourceAsStream("/emptyImage.png"));

        Font.loadFont(EditorMain.class.getResource("/fonts/la-solid-900.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSans-Bold.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSans-BoldItalic.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSans-Italic.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSans-Regular.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSerifCombo-Bold.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSerifCombo-BoldItalic.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSerifCombo-Italic.ttf").toExternalForm(),11);
        Font.loadFont(EditorMain.class.getResource("/fonts/NotoSerifCombo-Regular.ttf").toExternalForm(),11);







        MainWindow mainWindow = new MainWindow();


//        ExtendedDemo demo = new ExtendedDemo(stage); demo.start();
//        DerivationTest test = new DerivationTest(); test.testGrid(stage);
//        ExtractFromDoc test = new ExtractFromDoc(); test.rtaInsertTest(stage);
//        ParseTableDocTest test = new ParseTableDocTest(); test.parseTableDocTest(stage);
//        BoxedRTATest test = new BoxedRTATest(); test.testBoxedDRTA(stage);
//        TextWidthTest test = new TextWidthTest(); test.testTextWidth(stage);
//        ScrollPaneTest test = new ScrollPaneTest(); test.testScrollPane(stage);
//        RoundingTest test = new RoundingTest();
//        ClipTest test = new ClipTest(); test.testClip(stage);

    }


}


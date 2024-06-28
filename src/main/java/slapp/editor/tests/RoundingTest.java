package slapp.editor.tests;

import java.io.*;
import com.gluonhq.richtextarea.RichTextArea;
import com.gluonhq.richtextarea.model.Document;
import javafx.event.ActionEvent;
import slapp.editor.EditorMain;


public class RoundingTest {
    RichTextArea rta = new RichTextArea(EditorMain.mainStage);
    Document doc = new Document();

    public RoundingTest() {
        System.out.println("before: " + rta.isModified());

       rta.getActionFactory().open(doc).execute(new ActionEvent());
//        rta.getActionFactory().newDocument().execute(new ActionEvent());


        System.out.println("after: " + rta.isModified());

        System.out.println("doc: " + rta.getDocument());
    }

}

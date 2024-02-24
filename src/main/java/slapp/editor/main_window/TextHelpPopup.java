package slapp.editor.main_window;

import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import slapp.editor.EditorAlerts;
import slapp.editor.EditorMain;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;



public class TextHelpPopup {

    private static String about;
    private static String commonElements;
    private static String simpleEdit;
    private static String verticalTrees;
    private static String horizontalTrees;
    private static String truthTables;
    private static String derivations;

static {
    about = "<h2><p style=\"text-align: center;\">Symbolic Logic Application (SLAPP)</p></h2>" +
            "<p style = \"text-align: center;\">Version 1.0&#x03b1</p>" +
            "<p style=\"text-align: center;\">Copyright (c) 2024, Tony Roy</p>" +
            "<p>This program (SLAPP) is free software.  An executable install package may be downloaded from <a href=\"https://tonyroyphilosophy.net\">tonyroyphilosophy.net/xxx</a>.  Source code is available at <a href=\"https://github.com\">github.com/xxx</a>.  You may redistribute the software and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.  For the license, see <a href=\"https://www.gnu.org/licenses/licenses.html\">www.gnu.org/licenses.html</a>.</p>" +
            "<p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. </p>" +
            "In this preliminary version, every user becomes an evaluator and tester.  All comments are much appreciated.  You may submit comments and error reports through the SLAPP help menu item.  Reviews and other items that deserve public discussion may be submitted through the <a href=\"https://tonyroyphilosophy.net/textbook-blog/\">Symbolic Logic Blog</a>.</p>"
            ;

    commonElements = "<p><h3>Common Functions</h3></p>"+
            "<p>The SLAPP file structure consists of exercises, and then assignments made up of exercises.  You can open and work an exercise as such.  However, in the usual case, you will begin with an assignment.</p>"+
            "<ol><li><h4>Top Menu Bar:</li>"+
            "<ul><li>The assignment dropdown has the usual operations: save, save as, open, close, print, and export to PDF.  The latter works by a 'PDF printer', and the export option is a convenience to setup both physical and PDF printers.  (Recent versions of the Mac OS block PDF printers as such -- to generate a PDF, select the regular SLAPP print option and then the PDF dropdown from the Mac printer window.)  The 'create' options are of special interest to instructors.<br><br></li>"+
            "<li>The first time you open an assignment you will be asked to provide header information.  The name field is required.  Then the '+' and '-' buttons add and remove fields for optional header items (as student number or course section).  You will not be able to return to this screen (without restarting the assignment from scratch).  So be sure that the information is complete and correct as you enter it.  <br><br></li>"+
            "<li>Once you have an open assignment, move among exercises by the 'previous', 'next' and 'jump' items from the menu bar.  The 'comment' item opens a window to add a comment on the assignment as a whole; the comment appears at the top of a printed assignment.  The 'print' item duplicates print and export commands from the assignment and exercise items.  It also has options for printer and page setup. <br><br></li></ul>"+
            "<li><h4>Edit Commands:</li>"+
            "<ul><li>The button with the small keyboard icon pops up a keyboard diagram.  The diagram lists normal, shift, alt, shift-alt, and ctrl-shift-alt boards. The content of these boards is modified by the keyboard selector dropdown (and, more conveniently, by the F1-F5 keys).  The keyboard selector shifts base alphabet characters.  Logic characters remain the same for each selection (except for the few symbols on the top row of Base/Italic alt-board).  The keyboard selector is meant to group together characters you will need in a given context.<br><br>"+
            "<i>Note:</i> You will not need all the characters from all the boards at once!  You should be able to go through Part I of <i>Symbolic Logic</i> using nothing but the few logic characters sprinkled into the alt- and shift-alt boards, and just the Base/Italic and Italic/Sans keyboard selections.<br><br></li>"+
            "<li>In the unlikely event that you need a symbol not on the keyboard diagrams, you can insert any Unicode character by the unicode field.  You may enter either a decimal (preceded by '#') or hexadecimal (preceded by 'x') code.  Unicode includes over 100,000 character codes. You will be able to display a Unicode character so long as that character exists in a font on your computer.  If you enter a code for which there is no representation, you usually see an empty box.<br><br></li> "+
            "<li>In Java (the computer language in which SLAPP is written), the ordinary representation for a character is a single 16-bit word.  But Unicode has more characters than can be represented in 16 bits.  The Java solution is to use just one 16-bit word in the ordinary case, but two words for character codes that fall outside of the 16-bit range.  This manifests itself in SLAPP especially when deleting characters -- the delete takes out one word at a time, where the second word typically does not represent anything, and so shows as an empty box.  If you see this, the program is not broken -- it is only that you have half the representation of a character in the underlying file and you can just delete again.<br><br></li>"+
            "<li>Many of the edit buttons: cut, copy, paste, and such are what you expect.  A few call for special comment:<br><br></li>"+
            "<ul><li>Superscript and subscript have 'regular' and green 'shifted' versions (and work also by shifted or unshifted F7 and F8 keys).  The regular versions work in the usual way.  Sometimes, however, you want a superscript over a subscript.  The shifted versions back up one space before adding the character.  This is not a complete solution to putting superscripts over subscripts, as it only moves back one space.  But in many cases this is just what you need.  Note that the shift applies to any text after it is applied and text reverts to its normal position after the shift is removed -- a result is that you always end up with a space after a shifted super- or sub-script -- as the text reverts to the place it would have been without the shift.<br><br></li>"+
            "<li>Overline does not work by the usual mechanisms for strikethrough and underline.  It rather inserts an overline 'combining character' before the one you type -- where the combining character effectively overlaps the typed character.  Again, this mechanism manifests itself when deleting characters -- one step deletes the base character, and another the overline. And this is not a complete solution to overlining text.  It should, however, be sufficient for our purposes.<br><br></li>"+
            "<li>The save button saves an open exercise in the current (most recent) exercise directory, or an assignment in the current assignment directory.  If an exercise is open, it saves the exercise.  If an assignment is open, it saves the assignment.<br><br></li></ul></ul>"+
            "<li><h4>Page Management:</h4>"+
            "Many SLAPP exercises involve graphical elements.  While there is no problem in the vast majority of cases, it is possible for such elements to extend beyond regular page boundaries.  SLAPP breaks printed pages only at the boundaries of its boxes.  If the content of a box extends beyond one page, it will clip whatever is beyond the page border.  This means that you have to manage page breaks yourself.<br><br> </li>"+
            "<ul><li> The 'V Size' and 'H Size' items manage the size of the main content area.  The counters reflect the horizontal or vertical size of the content area as a percentage of the selected paper size.  With 'Win' checked, the content area sizes with the size of the SLAPP window.  With 'Win' unchecked the counter controls the size of the content area directly.<br><br></li>"+
            "<li>In case content exceeds the selected paper size, there are different options:<br><br></li>"+
            "<ul><li>In many cases, you can solve the problem by messing with the page setup -- changing the margins or changing the page orientation.   Note that page setup options apply to an entire print  job, and so to all the exercises in a printed assignment and not just to a member that is giving you trouble.<br><br></li>"+
            "<li>Another option is to change the selected paper size.  This is sufficient to accommodate most any exercise, but the larger paper may not be accommodated by your printer.  If the size of your object is not too different from the size of the printer paper, you can select 'fit to page' from the print options and the printed version will squish itself onto the page.  Alternatively, if a PDF file is sufficient for your purposes, the PDF printer will accommodate any selected page size.<br><br></li>"+
            "<li>If an electronic copy is sufficient, though, it may be simplest just to use the SLAPP file itself, with any review in SLAPP.  Then there are no worries about paper size.  And the comment fields on assignments and exercises remain live.<br><br></li></ul></ul>"+
            "<li><h4>Additional Help:</h4>"+
            "The 'Help' menu item includes help videos on different exercise types.  In addition, the 'Contextual' help item pops up help relevant to the exercise that is currently open.</li></ol>"
            ;

    simpleEdit = "<p><h3>Edit Exercises</h3></p>"+
            "<p>Beyond information from the 'Common' help item, there is very little to say about exercises in this category.  Beyond the common controls, commands for edit exercises are straightforward:</p>"+
            "<ul><li>In case of an extended response, add and delete pages by the buttons on the left.  'Insert' adds a page after the current page.  'Remove' deletes the current page.  You will not be able to have less than one page.<br><br></li>"+
            "<li>Some edit exercises begin with simple choice checkboxes that are followed by the main edit field. <br><br></li></ul>"
            ;

    verticalTrees = "VerticalTrees";
    horizontalTrees = "HorizontalTrees";

    truthTables = "<p><h3>Truth Table Exercises</h3></p>"+
            "<p>Constructing truth tables is straightforward:</p>"+
            "<ul><li>To start, enter the setup information on the right:  '+' and '-' add and remove fields for basic sentences.  Enter the basic sentences, and then the number of table rows.  Press 'setup'.  This creates the table.<br><br></li>"+
            "<li>Then it is a straightforward matter to enter table values. A given cell holds a single character (usually 'T' or 'F').  Automatic cursor movement is down.<br><br></li>"+
            "<li>Each row includes a short comment field at the right.  In most cases the comment will either be blank or a simple marker on the row.  Circular buttons highlight their columns.  Highlighting may be helpful to 'direct the eye' as you work the table and, in the end, to mark main columns.<br><br></li>"+
            "<li>Some truth table exercises include a checkbox choice and then a short explanation field at the bottom.<br><br></li>"+
            "<li>Though there is no problem in most cases, some truth table exercises overrun a standard page size.  The problem is usually solved by modifying page settings.<br><br></li></ul>"
            ;

    derivations = "<p><h3>Derivation Exercises</h3></p>"+
            "<p>A derivation exercise typically begins with premise(s) at the top, and a conclusion at the bottom.  You will not be able to modify the concluding formula and, in the ordinary case, you will not be able to modify either a premise or its justification.  There is a slider bar to adjust the length of formula fields.  After that, buttons on the left are reasonably straightforward:</p>"+
            "<ul><li>The 'undo' and 'redo' buttons apply to actions taken on the derivation as a whole -- actions by commands on the left, and the entry of formulas or justifications.  This contrasts with the undo/redo edit controls, whose application is always to characters typed in the current formula field.<br><br></li>"+
            "<li>The 'insert line' button inserts a line above the current line (at current scope depth), and 'delete line' removes the current line.<br><br></li>"+
            "<li>The 'insert subder' button adds lines for a new subderivation just above the current line, and 'insert subders' adds lines for a pair of subderivations above the current line (as for &#x2194;I or &#x2228;E)." +
            "<br><br></li>"+
            "<li>It is possible to modify or generate suberivations 'by hand' using the 'indent line', 'outdent line', 'add shelf' and 'add gap' buttons.  The indent button increases the scope of the current line by one; outdent reduces it by one.  Add shelf inserts a small shelf beneath the current line, and add gap inserts a gap just below the current line.  Removing a line removes a shelf or gap beneath it.<br><br></li>"+
            "<li>Once you enter line numbers for a justification, the numbers in the justification are automatically adjusted as you insert and delete lines above.<br><br>"+
            "<i>*For this reason, it is good practice to enter justifications of lines that depend on ones above as soon as the relevant lines exist with numbers.</i> For example, if a line <i>L</i> is to be justified by a suberivation, you can enter the justification for <i>L</i> when that suberivation is first set up, rather than waiting until the suberivation is complete -- and let SLAPP manage justification numbers as lines are inserted or deleted.\n" +
            "<br><br></li>" +
            "<li>In some cases, derivation exercises will be followed by an edit field to explain some aspect(s) of the derivation.</li></ul>"
            ;
}

    public static void helpAbout() {
       showHelp(about);
    }

    public static void helpCommonElements() {
        showHelp(commonElements);
    }

    public static void helpContextual(ExerciseType type) {

        if (type == null) {
            EditorAlerts.fleetingPopup("Open exercise to obtain contextual help.");
            return;
        }

        switch(type) {
            case AB_EXPLAIN: { }
            case ABEFG_EXPLAIN: { }
            case SIMPLE_EDITOR: {
                showHelp(simpleEdit);
                break;
            }

            case DRVTN_EXP: {}
            case DERIVATION: {
                showHelp(derivations);
                break;
            }

            case TRUTH_TABLE_EXPLAIN: { }
            case TRUTH_TABLE: {
                showHelp(truthTables);
                break;
            }

            case VERTICAL_TREE: {
                showHelp(verticalTrees);
                break;
            }

            default: {
            }
        }

    }


    private static void showHelp(String helpString) {

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setUserStyleSheetLocation("data:, body {font: 16px Noto Serif Combo; }");
        webEngine.loadContent(helpString);

        //open links in native browser
        webEngine.getLoadWorker().stateProperty().addListener((ob, ov, nv) -> {
            if (nv == Worker.State.SUCCEEDED) {
                Document document = webEngine.getDocument();
                NodeList nodeList = document.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++)
                {
                    Node node= nodeList.item(i);
                    EventTarget eventTarget = (EventTarget) node;
                    eventTarget.addEventListener("click", new EventListener()
                    {
                        @Override
                        public void handleEvent(Event evt)
                        {
                            EventTarget target = evt.getCurrentTarget();
                            HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                            String href = anchorElement.getHref();
                            //handle opening URL outside JavaFX WebView
                            System.out.println(href);

                            try {
                                Desktop.getDesktop().browse(new URI(href));
                            } catch (URISyntaxException e) {
                                System.out.println("URISyntaxException (textHelpPopup)");
                            } catch (IOException e) {
                                System.out.println("IOException (textHelpPopup)");
                            }
                            evt.preventDefault();
                        }
                    }, false);
                }
            }
        });

        VBox root = new VBox(webView);
        root.setVgrow(webView, Priority.ALWAYS);
        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Text Help");
        stage.initModality(Modality.NONE);
        stage.getIcons().addAll(EditorMain.icons);
        stage.initOwner(EditorMain.mainStage);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);

        stage.show();
    }

}

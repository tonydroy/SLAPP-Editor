package slapp.editor.main_window;

import javafx.concurrent.Worker;
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

import static slapp.editor.main_window.ExerciseType.HORIZONTAL_TREE;


public class TextHelpPopup {

    private static String about;
    private static String commonElements;
    private static String simpleEdit;
    private static String verticalTrees;
    private static String horizontalTrees;
    private static String truthTables;
    private static String derivations;

static {

    about = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<h2><p style=\"text-align: center;\">Symbolic Logic Application (SLAPP)</p></h2>" +
            "<p style = \"text-align: center;\">Version 1.00&#x03b1</p>" +
            "<p style=\"text-align: center;\">Copyright (c) 2024, Tony Roy</p>" +
            "<p>This program (SLAPP) is open-source and free software.  An executable install package may be downloaded from <a href=\"https://tonyroyphilosophy.net\">tonyroyphilosophy.net/xxx</a>.  Source code is available at <a href=\"https://github.com\">github.com/xxx</a>.  You may redistribute the software and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.  For the license, see <a href=\"https://www.gnu.org/licenses/licenses.html\">www.gnu.org/licenses.html</a>.</p>" +
            "<p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. </p>" +
            "In this preliminary version, every user becomes an evaluator and tester.  Comments are much appreciated.  You may submit comments and error reports by email <a href=\"mailto:messaging@slappservices.net?subject=SLAPP: (your issue)&body=Please be as specific as you can about your concern; if you are reporting an error, include information about the version of SLAPP and of your operating system, and (if possible) whether and how the problem may be repeated (ok to delete this line).\"> here </a>and by the 'comment / report' help menu item.  Reviews and other items that deserve public discussion may be submitted through the <a href=\"https://tonyroyphilosophy.net/textbook-blog/\">Symbolic Logic Blog</a>.</p>"
            ;

    commonElements = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<p><h3>Common Functions</h3></p>"+
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

    simpleEdit = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<p><h3>Edit Exercises</h3></p>"+
            "<p>Beyond information from the 'General Intro' help item -- with which you want to be familiar -- there is very little to say about exercises in this category.  Beyond the common controls, commands for edit exercises are straightforward:</p>"+
            "<ul><li>In case of an extended response, add and delete pages by the buttons on the left.  'Insert' adds a page after the current page.  'Remove' deletes the current page.  You will not be able to have less than one page.<br><br></li>"+
            "<li>Some edit exercises begin with simple choice checkboxes that are followed by the main edit field. <br><br></li></ul>"
            ;

    verticalTrees = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<p><h3>VerticalTrees</h3></p>"+
            "<p>Vertical trees appear in a variety of contexts and in a variety of forms, as sprinkled through chapters 2, 4, and 5 of <i>Symbolic Logic</i>.  Different exercises may include different combinations of the controls here described.  Also, though they are not strictly vertical trees, the 'map' problems of chapter 2 work within the same overall framework.</p>"+
            "<ol><li>A vertical tree always  has a bar over the main work area containing items that may be dragged into the work area.  Among the possibilities are a blue formula field, a green formula field, a grey vertical bracket, and/or a grey dotted horizontal line.    Once placed in the work area, these may be sized from the far end (the right or right or bottom) by the mouse.  Each has small popup buttons at the start (the left or top) -- grey to drag the object around in the work area, black to delete.  It may be necessary to modify the size of your window in order to create space for items in the work area.  <br><br></li>"+
            "<li>The 'undo' and 'redo' buttons apply to actions taken on the work area as a whole -- drag/drop actions, actions by commands on the left, and the content of text fields from stages where the fields (are completed and) lose focus.  This contrasts with the undo/redo edit controls, whose application is to individual characters that have been typed in the current formula field.  Other controls down the left apply just to formula fields.<br><br></li>"+
            "<li>In addition to the usual sizing and movement controls, the blue formula field has popup grey buttons above and below the center of the field.  Grabbing a lower (upper) button of one field and dragging to an upper (lower) button of another results in  a line between the two. This line is linked to the formula fields and moves with them as the fields move or grow. It is possible to 'grab' the center of a line with the mouse; then right-click deletes it.  Removing a box removes any lines to which it is connected. In addition, the box may have any of the following controls: <br><br></li>"+
            "<ul><li>With 'box' selected, left-click adds a solid outline around the formula space.  Right-click removes the outline.<br><br></li>"+
            "<li>With 'star' selected, left-click adds a solid star at the upper right-hand corner of the formula space.  Right-click removes the star.<br><br></li>"+
            "<li>With 'annotation' (the stacked boxes) selected, left-click adds a small annotation field at the upper right-hand corner of the formula space.  Right-click removes the field.  The small '+' button adds annotation fields to each formula space, and '-' removes them all.  The annotation fields are regular text-entry fields.  Star and annotation options exclude one another. <br><br></li>"+
            "<li> With 'circle' selected, F9 adds a marker at the current cursor position.  Move the cursor, and F9 again adds a second marker.  Pressing F9 a third time removes markers, and the cycle begins again.  With two markers, F10 adds a circle whose leftmost point is at the left marker and rightmost point is at the right marker.  Right-click removes a circle and any markers.  In order for the circle not to 'walk' on characters in the formula space, you can insert a space on either side of the circled item(s), and the markers just before the spaces.<br><br></li>"+
            "<li> Underlines work very much like circle.  With 'underlines' (horizontal bars) selected, F9 adds a marker at the current cursor position.  Move the cursor, and F9 again adds a second marker.  Pressing F9 a third time removes markers and the cycle begins again.  With two markers, F10 adds an underline whose leftmost point is at the left marker, and rightmost point at the right marker.  Right-click removes all underlines and any markers on the field.  A new underline always rests just above any underlines beneath it.  This will be what you want so long as you begin with longer lines (for main operators) first, and come with shorter ones 'contained' within them after.<br><br></li></ul>"+
            "<li>A 'mapping' function applies to green formula boxes.  This function is related to circle and underline.  It applies just to the green formula fields.  With 'mapping' (down arrow) selected, F9 adds a marker at the current cursor position.  Move the cursor, and F9 again adds a second marker.  Pressing F9 a third time removes markers, and the cycle begins again. And similarly for a separate formula field.  With mapping selected, right click on a formula box removes any markers.<br><br></li>"+
            "<ul><li>With at least one marker in separate formula fields, F10 connects them by a line.  If there are two markers the line reaches to a bracket whose leftmost end is at the left marker, and rightmost point at the right marker.  If there is a single marker, the line reaches to the marked character.  It is possible to 'grab' the center of a line with the mouse; then right-click deletes it.   <br><br></li> "+
            "<li>With at least one marker in a selected formula field, F11 adds '?' above.  If there are two markers, the question mark attaches to a bracket whose leftmost end is at the left marker, and rightmost point at the right marker.  If there is a single marker, the question mark attaches to the marked character.  It is possible to grab the question mark with the mouse; then right-click deletes it.<br><br></li></ol>"+
            "<ul><li>Elements from categories (3) and (4) attach to the <i>formula box</i>.  This is just what you want for the box, star, and annotation options.  However, although circle, underline, and map are first set relative to formula characters, they too are linked to the boxes.  A result is that changes to the formula may leave these incorrectly positioned.  So it is best to add circle, underline, and map only after the formula is finalized.   </li></ul>"
            ;

    horizontalTrees = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<p><h3>HorizontalTrees</h3></p>"+
            "<p>Horizontal trees appear especially in the quantificational portion of chapter 4 of <i>Symbolic Logic</i>.  Though there are a number of controls, their application is reasonably intuitive.</p>"+
            "<ol><li>The 'undo' and 'redo' buttons apply to actions taken on the work area as a whole -- actions by commands on the left, drag/drop actions, and the content of text fields from stages where the fields (are completed and) lose focus.  This contrasts with the undo/redo edit controls, whose application is to individual characters that have been typed in the current formula field.<br><br></li>"+
            "<li>Other controls down the left apply directly to the construction of trees.<br><br></li>"+
            "<ul><li>A horizontal tree always begins by placing a 'root' formula box in the main work area.  Select the box control, and click on the work area to add the box.  With the box control selected, right click on a box removes it together with any branches it may have.  As for vertical trees, a formula box may be sized from the right.  As you are sizing a box, it may appear misaligned with its branches; however when you stop the drag, the tree will align itself properly. A root formula box, together with its branches, may be moved in the work area by the grey popup button to its right. <br><br></li>"+
            "<li>One, two, or three branches may be added to a formula node by selecting the relevant control, and clicking on the formula box.  In fact, there is no limit to the number of branches a node may have -- as another click adds one, two, or three branches again (but it would be unusual to require more than two or three branches).  For a node with one or more branches, it is possible to indicate that the branches continue indefinitely, by adding a 'dots' branch with the control showing dots underneath a bar.<br><br></li>"+
            "<li>If a break is required to separate a formula from its terms, a vertical dotted line may be added by selecting the relevant control and clicking on the formula box.  With the control selected, right-click removes the break.<br><br></li>"+
            "<li>Branching for terms works by selecting the relevant control and clicking on a node.  Such branching looks like that for vertical trees except rotated clockwise by 90&#176;. Again, there is no limit to the number of branches a node may have -- as another click adds one or two branches again (but it would be unusual to require more than one or two branches).  It will not be possible to add a formula branch to a term node; and it is not possible for the immediate branches of any node to include both formula and term nodes.<br><br></li>"+
            "<li>By the control with vertical tick marks across a line, it is possible to add a 'ruler' over the main work area.  There is no special scale to these marks.  They are meant merely to locate positions in the work area, in order to aid references to one portion of a tree or another.<br><br></li>"+
            "<li>As for vertical trees, selecting the control with the stacked boxes adds (left click) or removes (right click) an annotation box in the upper right corner of formula and term boxes.  The small '+' button adds annotation fields to them all; the small '-' button removes them from all.</li></ull></ol>"+
            "<ul><li>It is likely that some horizontal will overrun the right margin of a standard page.  Very often it will make sense to switch to landscape orientation by the 'page setup' print option.</li></ul>"
            ;

    truthTables = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<p><h3>Truth Table Exercises</h3></p>"+
            "<p>Constructing truth tables is straightforward:</p>"+
            "<ul><li>To start, enter the setup information on the right:  '+' and '-' add and remove fields for basic sentences.  Enter the basic sentences, and then the number of table rows.  Press 'setup'.  This creates the table.<br><br></li>"+
            "<li>Then it is a straightforward matter to enter table values. A given cell holds a single character (usually 'T' or 'F').  Automatic cursor movement is down.<br><br></li>"+
            "<li>Each row includes a short comment field at the right.  In most cases the comment will either be blank or a simple marker on the row.  Circular buttons highlight their columns.  Highlighting may be helpful to 'direct the eye' as you work the table and, in the end, to mark main columns.<br><br></li>"+
            "<li>Some truth table exercises include a checkbox choice and then a short explanation field at the bottom.<br><br></li>"+
            "<li>Though there is no problem in most cases, some truth table exercises overrun a standard page size.  The problem is usually solved by modifying page settings.<br><br></li></ul>"
            ;

    derivations = "<body style=\"margin-left:10; margin-right: 20\">"+
            "<p><h3>Derivation Exercises</h3></p>"+
            "<p>A derivation exercise typically begins with premise(s) at the top, and a conclusion at the bottom.  You will not be able to modify the concluding formula and, in the ordinary case, you will not be able to modify either a premise or its justification.</p>" +
            "<p>There is a slider bar to adjust the width of formula fields.  Move within a derivation by the mouse and/or Ctrl-up, -down, -right, -left (Ctrl optional for -up, -down).  After that, buttons on the left are reasonably straightforward:</p>"+
            "<ul><li>The 'undo' and 'redo' buttons apply to actions taken on the work area as a whole --  actions by commands on the left, and the content of text fields from stages where the fields (are completed and) lose focus.  This contrasts with the undo/redo edit controls, whose application is to individual characters that have been typed in the current formula field.<br><br></li>"+
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

            case TRUTH_TABLE_ABEXP: { }
            case TRUTH_TABLE: {
                showHelp(truthTables);
                break;
            }

            case VERTICAL_TREE: {}
            case VERTICAL_TREE_EXP: {}
            case VERTICAL_TREE_ABEXP: {}
            case VERTICAL_TREE_ABEFEXP: {
                showHelp(verticalTrees);
                break;
            }

           case HORIZONTAL_TREE: {
                showHelp(horizontalTrees);
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
        stage.setTitle("SLAPP Text Help");
        stage.initModality(Modality.NONE);
        stage.getIcons().addAll(EditorMain.icons);
        stage.initOwner(EditorMain.mainStage);
        stage.setX(EditorMain.mainStage.getX() + EditorMain.mainStage.getWidth());
        stage.setY(EditorMain.mainStage.getY() + 200);

        stage.show();
    }

}

package slapp.editor.decorated_rta;

import com.gluonhq.richtextarea.RichTextAreaSkin;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import slapp.editor.EditorMain;
import java.util.HashMap;
import java.util.Map;


public class ExDemKeybdDiagram {
    private ExtendedDemo editorView;
    private Stage keyboardDiagramStage;
    private BooleanProperty keyboardDiagramButtonSelected;
    private Map<Character, Text> keyTypedTextMap = new HashMap<>();
    private Map<KeyCombination, Text> keyPressedTextMap = new HashMap<>();
    private double primaryFontSize;
    private Font textFont;
    private Font symbolFont;
    private Font titleFont = Font.font("Noto Sans", FontWeight.BOLD, FontPosture.REGULAR, 20);
    private int baseKeyWidth = 10;  //the width of a standard key is 4 of these units

       ExDemKeybdDiagram(Stage mainStage, ExtendedDemo editorView, BooleanProperty keyboardDiagramButtonSelected) {
        this.editorView = editorView;
        this.keyboardDiagramButtonSelected = keyboardDiagramButtonSelected;
        this.primaryFontSize = editorView.getPrimaryFontSize();
        textFont = Font.font("Noto Sans", FontWeight.NORMAL, FontPosture.REGULAR, primaryFontSize);
        symbolFont = Font.font("Noto Sans Math", FontWeight.NORMAL, FontPosture.REGULAR, primaryFontSize);

        //initialize text maps
        Map<Character, String> keyTypedCharMap = ((RichTextAreaSkin) editorView.getEditor().getSkin()).getKeyTypedCharMap();
        Map<KeyCodeCombination, String> keyPressedCharMap = ((RichTextAreaSkin) editorView.getEditor().getSkin()).getKeyPressedCharMap();
        for (char key : keyTypedCharMap.keySet()) {
            keyTypedTextMap.put(key, new Text());
        }
        for (KeyCombination key : keyPressedCharMap.keySet()) {
            keyPressedTextMap.put(key, new Text());
        }
        updateTextMaps();

        Text title1 = new Text("Normal");
        title1.setFont(titleFont);

        GridPane normalBoard = new GridPane();
        normalBoard.add(getTypedKey('`',4),0,0, 4, 1);
        normalBoard.add(getTypedKey('1',4),4,0,4,1);
        normalBoard.add(getTypedKey('2',4),8,0,4,1);
        normalBoard.add(getTypedKey('3',4),12,0,4,1);
        normalBoard.add(getTypedKey('4',4),16,0,4,1);
        normalBoard.add(getTypedKey('5',4),20,0,4,1);
        normalBoard.add(getTypedKey('6',4),24,0,4,1);
        normalBoard.add(getTypedKey('7',4),28,0,4,1);
        normalBoard.add(getTypedKey('8',4),32,0,4,1);
        normalBoard.add(getTypedKey('9',4),36,0,4,1);
        normalBoard.add(getTypedKey('0',4),40,0,4,1);
        normalBoard.add(getTypedKey('-',4),44,0,4,1);
        normalBoard.add(getTypedKey('=',4),48,0,4,1);
        normalBoard.add(getControlKey("BackDel", 8),52,0,8,1);

        normalBoard.add(getControlKey("Tab",6),0,1,6,1);
        normalBoard.add(getTypedKey('q',4),6,1,4,1);
        normalBoard.add(getTypedKey('w',4),10,1,4,1);
        normalBoard.add(getTypedKey('e',4),14,1,4,1);
        normalBoard.add(getTypedKey('r',4),18,1,4,1);
        normalBoard.add(getTypedKey('t',4),22,1,4,1);
        normalBoard.add(getTypedKey('y',4),26,1,4,1);
        normalBoard.add(getTypedKey('u',4),30,1,4,1);
        normalBoard.add(getTypedKey('i',4),34,1,4,1);
        normalBoard.add(getTypedKey('o',4),38,1,4,1);
        normalBoard.add(getTypedKey('p',4),42,1,4,1);
        normalBoard.add(getTypedKey('[',4),46,1,4,1);
        normalBoard.add(getTypedKey(']',4),50,1,4,1);
        normalBoard.add(getTypedKey('\\',6),54,1,6,1);

        normalBoard.add(getControlKey("Cap Lock",7),0,2,7,1);
        normalBoard.add(getTypedKey('a',4),7,2,4,1);
        normalBoard.add(getTypedKey('s',4),11,2,4,1);
        normalBoard.add(getTypedKey('d',4),15,2,4,1);
        normalBoard.add(getTypedKey('f',4),19,2,4,1);
        normalBoard.add(getTypedKey('g',4),23,2,4,1);
        normalBoard.add(getTypedKey('h',4),27,2,4,1);
        normalBoard.add(getTypedKey('j',4),31,2,4,1);
        normalBoard.add(getTypedKey('k',4),35,2,4,1);
        normalBoard.add(getTypedKey('l',4),39,2,4,1);
        normalBoard.add(getTypedKey(';',4),43,2,4,1);
        normalBoard.add(getTypedKey('\'',4),47,2,4,1);
        normalBoard.add(getControlKey("Enter",9),51,2,9,1);

        normalBoard.add(getControlKey("Shift",9),0,3,9,1);
        normalBoard.add(getTypedKey('z',4),9,3,4,1);
        normalBoard.add(getTypedKey('x',4),13,3,4,1);
        normalBoard.add(getTypedKey('c',4),17,3,4,1);
        normalBoard.add(getTypedKey('v',4),21,3,4,1);
        normalBoard.add(getTypedKey('b',4),25,3,4,1);
        normalBoard.add(getTypedKey('n',4),29,3,4,1);
        normalBoard.add(getTypedKey('m',4),33,3,4,1);
        normalBoard.add(getTypedKey(',',4),37,3,4,1);
        normalBoard.add(getTypedKey('.',4),41,3,4,1);
        normalBoard.add(getTypedKey('/',4),45,3,4,1);
        normalBoard.add(getControlKey("Shift",11),49,3,11,1);

        normalBoard.add(getControlKey("control/\n command",10),0,4,10,1);
        normalBoard.add(getControlKey("alternate/\n option",9),10,4,9,1);
        normalBoard.add(getControlKey("",24),19,4,24,1);
        normalBoard.add(getControlKey("alternate/\n option",8),43,4,8,1);
        normalBoard.add(getControlKey("control/\n command",9),51,4,9,1);

        Text title2 = new Text("Shift");
        title2.setFont(titleFont);

        GridPane shiftBoard = new GridPane();
        shiftBoard.add(getTypedKey('~',4),0,0, 4, 1);
        shiftBoard.add(getTypedKey('!',4),4,0,4,1);
        shiftBoard.add(getTypedKey('@',4),8,0,4,1);
        shiftBoard.add(getTypedKey('#',4),12,0,4,1);
        shiftBoard.add(getTypedKey('$',4),16,0,4,1);
        shiftBoard.add(getTypedKey('%',4),20,0,4,1);
        shiftBoard.add(getTypedKey('^',4),24,0,4,1);
        shiftBoard.add(getTypedKey('&',4),28,0,4,1);
        shiftBoard.add(getTypedKey('*',4),32,0,4,1);
        shiftBoard.add(getTypedKey('(',4),36,0,4,1);
        shiftBoard.add(getTypedKey(')',4),40,0,4,1);
        shiftBoard.add(getTypedKey('_',4),44,0,4,1);
        shiftBoard.add(getTypedKey('+',4),48,0,4,1);
        shiftBoard.add(getControlKey("BackDel", 8),52,0,8,1);

        shiftBoard.add(getControlKey("Tab",6),0,1,6,1);
        shiftBoard.add(getTypedKey('Q',4),6,1,4,1);
        shiftBoard.add(getTypedKey('W',4),10,1,4,1);
        shiftBoard.add(getTypedKey('E',4),14,1,4,1);
        shiftBoard.add(getTypedKey('R',4),18,1,4,1);
        shiftBoard.add(getTypedKey('T',4),22,1,4,1);
        shiftBoard.add(getTypedKey('Y',4),26,1,4,1);
        shiftBoard.add(getTypedKey('U',4),30,1,4,1);
        shiftBoard.add(getTypedKey('I',4),34,1,4,1);
        shiftBoard.add(getTypedKey('O',4),38,1,4,1);
        shiftBoard.add(getTypedKey('P',4),42,1,4,1);
        shiftBoard.add(getTypedKey('{',4),46,1,4,1);
        shiftBoard.add(getTypedKey('}',4),50,1,4,1);
        shiftBoard.add(getTypedKey('|',6),54,1,6,1);

        shiftBoard.add(getControlKey("Cap Lock",7),0,2,7,1);
        shiftBoard.add(getTypedKey('A',4),7,2,4,1);
        shiftBoard.add(getTypedKey('S',4),11,2,4,1);
        shiftBoard.add(getTypedKey('D',4),15,2,4,1);
        shiftBoard.add(getTypedKey('F',4),19,2,4,1);
        shiftBoard.add(getTypedKey('G',4),23,2,4,1);
        shiftBoard.add(getTypedKey('H',4),27,2,4,1);
        shiftBoard.add(getTypedKey('J',4),31,2,4,1);
        shiftBoard.add(getTypedKey('K',4),35,2,4,1);
        shiftBoard.add(getTypedKey('L',4),39,2,4,1);
        shiftBoard.add(getTypedKey(':',4),43,2,4,1);
        shiftBoard.add(getTypedKey('\"',4),47,2,4,1);
        shiftBoard.add(getControlKey("Enter",9),51,2,9,1);

        shiftBoard.add(getControlKey("Shift",9),0,3,9,1);
        shiftBoard.add(getTypedKey('Z',4),9,3,4,1);
        shiftBoard.add(getTypedKey('X',4),13,3,4,1);
        shiftBoard.add(getTypedKey('C',4),17,3,4,1);
        shiftBoard.add(getTypedKey('V',4),21,3,4,1);
        shiftBoard.add(getTypedKey('B',4),25,3,4,1);
        shiftBoard.add(getTypedKey('N',4),29,3,4,1);
        shiftBoard.add(getTypedKey('M',4),33,3,4,1);
        shiftBoard.add(getTypedKey('<',4),37,3,4,1);
        shiftBoard.add(getTypedKey('>',4),41,3,4,1);
        shiftBoard.add(getTypedKey('?',4),45,3,4,1);
        shiftBoard.add(getControlKey("Shift",11),49,3,11,1);

        shiftBoard.add(getControlKey("control/\n command",10),0,4,10,1);
        shiftBoard.add(getControlKey("alternate/\n option",9),10,4,9,1);
        shiftBoard.add(getControlKey("",24),19,4,24,1);
        shiftBoard.add(getControlKey("alternate/\n option",8),43,4,8,1);
        shiftBoard.add(getControlKey("control/\n command",9),51,4,9,1);

        Text title3 = new Text("Alt");
        title3.setFont(titleFont);

        GridPane altBoard = new GridPane();
        altBoard.add(getAltKey(KeyCode.BACK_QUOTE,4),0,0, 4, 1);
        altBoard.add(getAltKey(KeyCode.DIGIT1,4),4,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT2,4),8,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT3,4),12,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT4,4),16,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT5,4),20,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT6,4),24,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT7,4),28,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT8,4),32,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT9,4),36,0,4,1);
        altBoard.add(getAltKey(KeyCode.DIGIT0,4),40,0,4,1);
        altBoard.add(getAltKey(KeyCode.MINUS,4),44,0,4,1);
        altBoard.add(getAltKey(KeyCode.EQUALS,4),48,0,4,1);
        altBoard.add(getControlKey("BackDel", 8),52,0,8,1);

        altBoard.add(getControlKey("Tab",6),0,1,6,1);
        altBoard.add(getAltKey(KeyCode.Q,4),6,1,4,1);
        altBoard.add(getAltKey(KeyCode.W,4),10,1,4,1);
        altBoard.add(getAltKey(KeyCode.E,4),14,1,4,1);
        altBoard.add(getAltKey(KeyCode.R,4),18,1,4,1);
        altBoard.add(getAltKey(KeyCode.T,4),22,1,4,1);
        altBoard.add(getAltKey(KeyCode.Y,4),26,1,4,1);
        altBoard.add(getAltKey(KeyCode.U,4),30,1,4,1);
        altBoard.add(getAltKey(KeyCode.I,4),34,1,4,1);
        altBoard.add(getAltKey(KeyCode.O,4),38,1,4,1);
        altBoard.add(getAltKey(KeyCode.P,4),42,1,4,1);
        altBoard.add(getAltKey(KeyCode.OPEN_BRACKET,4),46,1,4,1);
        altBoard.add(getAltKey(KeyCode.CLOSE_BRACKET,4),50,1,4,1);
        altBoard.add(getAltKey(KeyCode.BACK_SLASH,6),54,1,6,1);

        altBoard.add(getControlKey("Cap Lock",7),0,2,7,1);
        altBoard.add(getAltKey(KeyCode.A,4),7,2,4,1);
        altBoard.add(getAltKey(KeyCode.S,4),11,2,4,1);
        altBoard.add(getAltKey(KeyCode.D,4),15,2,4,1);
        altBoard.add(getAltKey(KeyCode.F,4),19,2,4,1);
        altBoard.add(getAltKey(KeyCode.G,4),23,2,4,1);
        altBoard.add(getAltKey(KeyCode.H,4),27,2,4,1);
        altBoard.add(getAltKey(KeyCode.J,4),31,2,4,1);
        altBoard.add(getAltKey(KeyCode.K,4),35,2,4,1);
        altBoard.add(getAltKey(KeyCode.L,4),39,2,4,1);
        altBoard.add(getAltKey(KeyCode.SEMICOLON,4),43,2,4,1);
        altBoard.add(getAltKey(KeyCode.QUOTE,4),47,2,4,1);
        altBoard.add(getControlKey("Enter",9),51,2,9,1);

        altBoard.add(getControlKey("Shift",9),0,3,9,1);
        altBoard.add(getAltKey(KeyCode.Z,4),9,3,4,1);
        altBoard.add(getAltKey(KeyCode.X,4),13,3,4,1);
        altBoard.add(getAltKey(KeyCode.C,4),17,3,4,1);
        altBoard.add(getAltKey(KeyCode.V,4),21,3,4,1);
        altBoard.add(getAltKey(KeyCode.B,4),25,3,4,1);
        altBoard.add(getAltKey(KeyCode.N,4),29,3,4,1);
        altBoard.add(getAltKey(KeyCode.M,4),33,3,4,1);
        altBoard.add(getAltKey(KeyCode.COMMA,4),37,3,4,1);
        altBoard.add(getAltKey(KeyCode.PERIOD,4),41,3,4,1);
        altBoard.add(getAltKey(KeyCode.SLASH,4),45,3,4,1);
        altBoard.add(getControlKey("Shift",11),49,3,11,1);

        altBoard.add(getControlKey("control/\n command",10),0,4,10,1);
        altBoard.add(getControlKey("alternate/\n option",9),10,4,9,1);
        altBoard.add(getControlKey("",24),19,4,24,1);
        altBoard.add(getControlKey("alternate/\n option",8),43,4,8,1);
        altBoard.add(getControlKey("control/\n command",9),51,4,9,1);

        Text title4 = new Text("Shift-Alt");
        title4.setFont(titleFont);

        GridPane shiftAltBoard = new GridPane();
        shiftAltBoard.add(getShiftAltKey(KeyCode.BACK_QUOTE,4),0,0, 4, 1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT1,4),4,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT2,4),8,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT3,4),12,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT4,4),16,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT5,4),20,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT6,4),24,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT7,4),28,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT8,4),32,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT9,4),36,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.DIGIT0,4),40,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.MINUS,4),44,0,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.EQUALS,4),48,0,4,1);
        shiftAltBoard.add(getControlKey("BackDel", 8),52,0,8,1);

        shiftAltBoard.add(getControlKey("Tab",6),0,1,6,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.Q,4),6,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.W,4),10,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.E,4),14,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.R,4),18,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.T,4),22,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.Y,4),26,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.U,4),30,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.I,4),34,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.O,4),38,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.P,4),42,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.OPEN_BRACKET,4),46,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.CLOSE_BRACKET,4),50,1,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.BACK_SLASH,6),54,1,6,1);

        shiftAltBoard.add(getControlKey("Cap Lock",7),0,2,7,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.A,4),7,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.S,4),11,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.D,4),15,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.F,4),19,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.G,4),23,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.H,4),27,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.J,4),31,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.K,4),35,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.L,4),39,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.SEMICOLON,4),43,2,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.QUOTE,4),47,2,4,1);
        shiftAltBoard.add(getControlKey("Enter",9),51,2,9,1);

        shiftAltBoard.add(getControlKey("Shift",9),0,3,9,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.Z,4),9,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.X,4),13,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.C,4),17,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.V,4),21,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.B,4),25,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.N,4),29,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.M,4),33,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.COMMA,4),37,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.PERIOD,4),41,3,4,1);
        shiftAltBoard.add(getShiftAltKey(KeyCode.SLASH,4),45,3,4,1);
        shiftAltBoard.add(getControlKey("Shift",11),49,3,11,1);

        shiftAltBoard.add(getControlKey("control/\n command",10),0,4,10,1);
        shiftAltBoard.add(getControlKey("alternate/\n option",9),10,4,9,1);
        shiftAltBoard.add(getControlKey("",24),19,4,24,1);
        shiftAltBoard.add(getControlKey("alternate/\n option",8),43,4,8,1);
        shiftAltBoard.add(getControlKey("control/\n command",9),51,4,9,1);

        Text title5 = new Text("Ctrl-Shift-Alt");
        title5.setFont(titleFont);

        GridPane ctrlShiftAltBoard = new GridPane();
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.BACK_QUOTE,4),0,0, 4, 1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT1,4),4,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT2,4),8,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT3,4),12,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT4,4),16,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT5,4),20,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT6,4),24,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT7,4),28,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT8,4),32,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT9,4),36,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.DIGIT0,4),40,0,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.MINUS,4),44,0,4,1);
//        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.EQUALS,4),48,0,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u035e\ud835\udc5c\u035e\ud835\udc5c",4),48,0,4,1);
        ctrlShiftAltBoard.add(getControlKey("BackDel", 8),52,0,8,1);

        ctrlShiftAltBoard.add(getControlKey("Tab",6),0,1,6,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.Q,4),6,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.W,4),10,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.E,4),14,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.R,4),18,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.T,4),22,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.Y,4),26,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.U,4),30,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.I,4),34,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.O,4),38,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.P,4),42,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.OPEN_BRACKET,4),46,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.CLOSE_BRACKET,4),50,1,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.BACK_SLASH,6),54,1,6,1);

        ctrlShiftAltBoard.add(getControlKey("Cap Lock",7),0,2,7,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.A,4),7,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.S,4),11,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.D,4),15,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.F,4),19,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.G,4),23,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.H,4),27,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.J,4),31,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.K,4),35,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.L,4),39,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.SEMICOLON,4),43,2,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.QUOTE,4),47,2,4,1);
        ctrlShiftAltBoard.add(getControlKey("Enter",9),51,2,9,1);

        ctrlShiftAltBoard.add(getControlKey("Shift",9),0,3,9,1);
        //these are "hard coded" - need to fix for modified assignments.
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u0305",4),9,3,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u033f",4),13,3,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u20d7",4),17,3,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u0302",4),21,3,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u0338",4),25,3,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u030a",4),29,3,4,1);
        ctrlShiftAltBoard.add(getFixedCharKey("\u25cc\u0303",4),33,3,4,1);
        //
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.COMMA,4),37,3,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.PERIOD,4),41,3,4,1);
        ctrlShiftAltBoard.add(getCtrlShiftAltKey(KeyCode.SLASH,4),45,3,4,1);
        ctrlShiftAltBoard.add(getControlKey("Shift",11),49,3,11,1);

        ctrlShiftAltBoard.add(getControlKey("control/\n command",10),0,4,10,1);
        ctrlShiftAltBoard.add(getControlKey("alternate/\n option",9),10,4,9,1);
        ctrlShiftAltBoard.add(getControlKey("",24),19,4,24,1);
        ctrlShiftAltBoard.add(getControlKey("alternate/\n option",8),43,4,8,1);
        ctrlShiftAltBoard.add(getControlKey("control/\n command",9),51,4,9,1);

        Text ctrlChars = new Text("\ud83e\udc46 Ctrl:5, :6, :7, :8, :9 select keyboards (like keyboard dropdown).\n" +
                "    Ctrl:B bold, :I italic, :U underline, :0 (zero) overline\n" +
                "    Ctrl:= subscript, :=(+shift) superscript, :- back subscript, :-(+shift) back superscript\n" +
                "    Ctrl-A select all, :C copy, :X cut, :V paste, :Z undo, :Z(+shift) redo\n\n");

        ctrlChars.setFont(textFont);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(title1, normalBoard, title2, shiftBoard, title3, altBoard, title4, shiftAltBoard, title5, ctrlShiftAltBoard, ctrlChars);
        vBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(normalBoard, new Insets(0,0,20,0));
        vBox.setMargin(shiftBoard, new Insets(0,0,20,0));
        vBox.setMargin(altBoard, new Insets(0,0,20,0));
        vBox.setMargin(shiftAltBoard, new Insets(0,0,20,0));
        vBox.setMargin(ctrlShiftAltBoard, new Insets(0,0,20,0));
        vBox.setMargin(ctrlChars, new Insets(0,0,20,0));

        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setPadding(new Insets(10,10,10,10));
        //why does scroll pane think keyboards are so wide??

        Scene scene = new Scene(scrollPane);
        keyboardDiagramStage = new Stage();
        keyboardDiagramStage.setOnCloseRequest(e -> {
           e.consume();
           closeKeyboardDiagram();
        });
        keyboardDiagramStage.getIcons().add(new Image(EditorMain.class.getResourceAsStream("/icon16x16.png")));
        keyboardDiagramStage.setScene(scene);
        keyboardDiagramStage.setTitle("Keyboard Diagrams");
        keyboardDiagramStage.initModality(Modality.NONE);
        keyboardDiagramStage.setX(editorView.getKeyboardWindowX());
        keyboardDiagramStage.setY(editorView.getKeyboardWindowY());
        keyboardDiagramStage.setWidth(editorView.getKeyboardWindowWidth());
        keyboardDiagramStage.setHeight(editorView.getKeyboardWindowHeight());
        keyboardDiagramStage.show();
    }
    StackPane getControlKey(String name, int width) {
        Text text = new Text(name);
        text.setFont(new Font("Noto Sans",primaryFontSize * 2/3));
        TextFlow flow = new TextFlow(text);
        flow.setTextAlignment(TextAlignment.CENTER);
        StackPane pane = new StackPane(flow);
        pane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3),new BorderWidths(2.0))));
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(baseKeyWidth * width);
        return pane;
    }

    //used for combining characters
    //the regular c/s/a pathway shows the character
    //but I could not get it to work when appended to the dotted circle
    StackPane getFixedCharKey(String name, int width) {
        Text text = new Text(name);
        text.setFont(symbolFont);
        TextFlow flow = new TextFlow(text);
        flow.setTextAlignment(TextAlignment.CENTER);
        StackPane pane = new StackPane(flow);
        pane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3),new BorderWidths(2.0))));
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(baseKeyWidth * width);
        return pane;
    }
    StackPane getTypedKey(Character key, int width) {
        Text text = keyTypedTextMap.get(key);
        text.setFont(textFont);
        TextFlow flow = new TextFlow(text);
        flow.setTextAlignment(TextAlignment.CENTER);
        StackPane pane = new StackPane(flow);
        pane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3),new BorderWidths(2.0))));
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(baseKeyWidth * width);
        return pane;
    }

    StackPane getAltKey(KeyCode code, int width) {
        Text text = keyPressedTextMap.get(new KeyCodeCombination(code, KeyCombination.ALT_DOWN));
        text.setFont(symbolFont);
        TextFlow flow = new TextFlow(text);
        flow.setTextAlignment(TextAlignment.CENTER);
        StackPane pane = new StackPane(flow);
        pane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3),new BorderWidths(2.0))));
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(baseKeyWidth * width);
        return pane;
    }

    StackPane getShiftAltKey(KeyCode code, int width) {
        Text text = keyPressedTextMap.get(new KeyCodeCombination(code, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN));
        text.setFont(symbolFont);
        TextFlow flow = new TextFlow(text);
        flow.setTextAlignment(TextAlignment.CENTER);
        StackPane pane = new StackPane(flow);
        pane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3),new BorderWidths(2.0))));
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(baseKeyWidth * width);
        return pane;
    }
    StackPane getCtrlShiftAltKey(KeyCode code, int width) {
        Text text = keyPressedTextMap.get(new KeyCodeCombination(code, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN,KeyCombination.SHORTCUT_DOWN));
        text.setFont(symbolFont);
        TextFlow flow = new TextFlow(text);
        flow.setTextAlignment(TextAlignment.CENTER);
        StackPane pane = new StackPane(flow);
        pane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(3),new BorderWidths(2.0))));
        pane.setAlignment(Pos.CENTER);
        pane.setMinWidth(baseKeyWidth * width);
        return pane;
    }

    public void updateTextMaps() {
        Map<Character, String> keyTypedCharMap = ((RichTextAreaSkin) editorView.getEditor().getSkin()).getKeyTypedCharMap();
        Map<KeyCodeCombination, String> keyPressedCharMap = ((RichTextAreaSkin) editorView.getEditor().getSkin()).getKeyPressedCharMap();

        for (Map.Entry<Character, Text> entry : keyTypedTextMap.entrySet()) {
            entry.getValue().setText(keyTypedCharMap.get(entry.getKey()));
        }
        for (Map.Entry<KeyCombination, Text> entry : keyPressedTextMap.entrySet()) {
            entry.getValue().setText(keyPressedCharMap.get(entry.getKey()));
        }
    }

    public void closeKeyboardDiagram() {
        editorView.setKeyboardWindowX(keyboardDiagramStage.getX());
        editorView.setKeyboardWindowY(keyboardDiagramStage.getY());
        editorView.setKeyboardWindowWidth(keyboardDiagramStage.getWidth());
        editorView.setKeyboardWindowHeight(keyboardDiagramStage.getHeight());
        keyboardDiagramButtonSelected.setValue(false);
        keyboardDiagramStage.close();
    }
}






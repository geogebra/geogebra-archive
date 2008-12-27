package geogebra.gui.inputbar;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;
import geogebra.util.AutoCompleteDictionary;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AutoCompleteTextField extends JTextField implements 
AutoComplete, KeyListener, GeoElementSelectionListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private StringBuffer curWord;       
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	protected boolean autoComplete;
	private int historyIndex;
	private ArrayList history;  
	private boolean handleEscapeKey = false;

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up.
	 * A default model is created and the number of columns is 0.
	 *
	 */
	public AutoCompleteTextField(int columns, Application app) {
		this(columns, app, true); 
	}    

	public AutoCompleteTextField(int columns, Application app, boolean handleEscapeKey) {
		setColumns(columns);

		this.app = app;
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuffer();

		historyIndex = 0;
		history = new ArrayList(50);

		addKeyListener(this);
		setDictionary(app.getCommandDictionary());   
	}   

	/**
	 * Set the dictionary that autocomplete lookup should be performed by.
	 *
	 * @param dict The dictionary that will be used for the autocomplete lookups.
	 */
	public void setDictionary(AutoCompleteDictionary dict) {
		this.dict = dict;   
	}

	/**
	 * Gets the dictionary currently used for lookups.
	 *
	 * @return dict The dictionary that will be used for the autocomplete lookups.
	 */
	public AutoCompleteDictionary getDictionary() {
		return dict;
	}

	/**
	 * Sets whether the component is currently performing autocomplete lookups as
	 * keystrokes are performed.
	 *
	 * @param val True or false.
	 */
	public void setAutoComplete(boolean val) {
		autoComplete = val && app.isAutoCompletePossible();
	}

	/**
	 * Gets whether the component is currently performing autocomplete lookups as
	 * keystrokes are performed.
	 *
	 * @return True or false.
	 */
	public boolean getAutoComplete() {
		return autoComplete && app.isAutoCompletePossible();
	}

	public String getCurrentWord() {
		return curWord.toString();
	}  

	public void geoElementSelected(GeoElement geo, boolean add) {

		switch (app.getMode()) {

		case EuclidianView.MODE_ALGEBRA_INPUT:   	
			replaceSelection(geo.getLabel());   
			requestFocus();
			break;

		default:
			if (geo == null) {
				setText("");
				return;
			}
		// copy definition into input bar
		//((AlgebraInput)(app.getGuiManager().getAlgebraInput())).setString(geo);		
		}
	}

	//----------------------------------------------------------------------------
	// Protected methods
	//----------------------------------------------------------------------------

	boolean ctrlC = false;

	public void keyPressed(KeyEvent e) {        
		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (Application.MAC_OS && e.isControlDown())
			e.consume();

		
		int keyCode = e.getKeyCode();  

		ctrlC = false;

		switch (keyCode) {

		// process input
		case KeyEvent.VK_C:
			if (Application.isControlDown(e)) //workaround for MAC_OS
			{
				ctrlC = true;
			}

			break;

		case KeyEvent.VK_ENTER:                             
			// processEnterKey accepts a selection if there is one 
			// in this case the ENTER key event is consumed 
			// so that it is not processed by other objects (e.g. AlgebraInput)
			if (getAutoComplete() && processAutoCompletionKey()) {
				e.consume();                
			}                 
			break;

			// clear textfield
		case KeyEvent.VK_ESCAPE:   
			if (!handleEscapeKey) {
				break;
			}

			Component comp = SwingUtilities.getRoot(this);
			if (comp instanceof JDialog) {
				((JDialog) comp).setVisible(false);
				return;
			}            	            	

			if ("".equals(getText())) {
				app.setMoveMode();
				requestFocus();
			} else {
				setText(null);
			}
			break;

		case KeyEvent.VK_LEFT_PARENTHESIS:
			break;


		case KeyEvent.VK_UP:
			if (!handleEscapeKey) {
				break;
			}
			String text = getPreviousInput();
			if (text != null) setText(text);
			break;

		case KeyEvent.VK_DOWN:
			if (!handleEscapeKey) {
				break;
			}
			setText(getNextInput());
			break; 

		case KeyEvent.VK_F1:            	
			updateCurrentWord();
			showCommandHelp(false);
			break;          
			/* experimental - jump striaght to manual page for current command
			 * could replace F1 if it works - problems with accents/asian languages
            case KeyEvent.VK_F4:
                updateCurrentWord();
                showCommandHelp(true);
                break;          
			 */
		default:                                
		}                                   
	}


	public void keyReleased(KeyEvent e) {

		//Application.debug(e+"");

		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = Application.MAC_OS ? e.isControlDown() : e.isAltDown();


		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			modifierKeyPressed = false;

		if (!getAutoComplete()) return;

		char charPressed = e.getKeyChar();  

		if ( (!Character.isLetterOrDigit(charPressed) && !modifierKeyPressed) || 
				(ctrlC && Application.MAC_OS) // don't want selection cleared
		) return;        


		int start = getSelectionStart();
		int end = getSelectionEnd();        
		//    clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuffer sb = new StringBuffer();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));            
			setText(sb.toString());
			if (pos < sb.length()) setCaretPosition(pos);
		}

		//Application.debug(e.isAltDown()+"");

		if (modifierKeyPressed) {

			String insertStr = "";


			// works nicely for alt or ctrl pressed (Windows/Mac)  
			String keyString = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase(Locale.US);
			
			//Application.debug(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().charAt(0)+"");
			//Application.debug(e+"");
			//Application.debug(keyString);
			
			
			// Numeric keypad numbers eg NumPad-8, NumPad *
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
				keyString = e.getKeyChar() + "";
			
			// workaround for different Java versions!!
			if (keyString.equals("minus"))
				keyString = "-";
			else if (keyString.equals("plus"))
				keyString = "+";
			else if (keyString.equals("comma"))
				keyString = ",";
			else if (keyString.equals("period"))
				keyString = ".";
			else if (keyString.equals("equals"))
				keyString = "=";
			
			// workaround for shifted characters:
			// (different in different locales)
			if (e.getKeyChar() == '+')
				keyString = "+";
			else if (e.getKeyChar() == '*')
				keyString = "*";
			else if (e.getKeyChar() == '=')
				keyString = "=";
			else if (e.getKeyChar() == '-')
				keyString = "-";
			else if (e.getKeyChar() == '>')
				keyString = ">";
			else if (e.getKeyChar() == '<')
				keyString = "<";
			

			
			//Application.debug(keyString);


			// don't want to act on eg "Shift"
			if (keyString.length() == 1)
				switch (keyString.charAt(0)) {

				case '*' :
					insertStr = ExpressionNode.strCOMPLEXMULTIPLY; //  alt-m -> complex multiply "\u2297"
					break;
				case '=' :
					insertStr = "\u2260"; // alt-= -> notEqualTo
					break;
				case '+' :
					insertStr = "\u00b1"; // alt-+ -> minusOrPlus
					break;
				case '-' :
					insertStr = "\u2213"; // alt-- -> minusOrPlus
					break;
				case ',' : 
				case '<' : 
					insertStr = "\u2264"; // alt-< -> lessThanOrEqual
					break;
				case '.' : 
				case '>' : 
					insertStr = "\u2265"; // alt-> -> greaterThanOrEqual
					break;
				case 'a' :
					if (e.isShiftDown())
						insertStr = "\u0391"; // alt-A -> unicode alpha (upper case)
					else
						insertStr = "\u03b1"; // alt-a -> unicode alpha
					break;
				case 'b' :
					if (e.isShiftDown())
						insertStr = "\u0392"; // alt-B -> unicode beta (upper case)
					else
						insertStr = "\u03b2"; // alt-b -> unicode beta
					break;
				case 'd' :
					if (e.isShiftDown())
						insertStr = "\u0394"; // alt-D -> unicode delta (upper case)
					else
						insertStr = "\u03b4"; // alt-d -> unicode delta
					break;
				case 'e' :
					insertStr = Kernel.EULER_STRING; // alt-e -> unicode e
					break;
				case 'f' :
					if (e.isShiftDown())
						insertStr = "\u03a6"; // alt-F -> unicode phi (upper case)
					else
						insertStr = "\u03c6"; // alt-f -> unicode phi
					break;
				case 'g' :
					if (e.isShiftDown())
						insertStr = "\u0393"; // alt-G -> unicode gamma (upper case)
					else
						insertStr = "\u03b3"; // alt-g -> unicode gamma
					break;
				case 'l' :
					if (e.isShiftDown())
						insertStr = "\u039b"; // alt-L -> unicode lambda (upper case)
					else
						insertStr = "\u03bb"; // alt-l -> unicode lambda
					break;
				case 'm' :
					if (e.isShiftDown())
						insertStr = "\u039c"; // alt-P -> unicode pi (upper case)
					else
						insertStr = "\u03bc"; // alt-p -> unicode pi
					break;
				case 'o' :
					insertStr = "\u00b0"; // alt-o -> unicode degree sign
					break;
				case 'p' :
					if (e.isShiftDown())
						insertStr = "\u03a0"; // alt-P -> unicode pi (upper case)
					else
						insertStr = "\u03c0"; // alt-p -> unicode pi
					break;
				case 's' :
					if (e.isShiftDown())
						insertStr = "\u03a3"; // alt-S -> unicode theta (upper case)
					else
						insertStr = "\u03c3"; // alt-s -> unicode theta
					break;
				case 't' :
					if (e.isShiftDown())
						insertStr = "\u0398"; // alt-T -> unicode theta (upper case)
					else
						insertStr = "\u03b8"; // alt-t -> unicode theta
					break;
				case '0' :
					insertStr = "\u2070"; // alt-0 -> unicode superscript 0
					break;
				case '1' :
					insertStr = "\u00b9"; // alt-1 -> unicode superscript 1
					break;
				case '2' :
					insertStr = "\u00b2"; // alt-2 -> unicode superscript 2
					break;
				case '3' :
					insertStr = "\u00b3"; // alt-3 -> unicode superscript 3
					break;
				case '4' :
					insertStr = "\u2074"; // alt-4 -> unicode superscript 4
					break;
				case '5' :
					insertStr = "\u2075"; // alt-5 -> unicode superscript 5
					break;
				case '6' :
					insertStr = "\u2076"; // alt-6 -> unicode superscript 6
					break;
				case '7' :
					insertStr = "\u2077"; // alt-7 -> unicode superscript 7
					break;
/*	care needed
On pc-keyboard:    7   8   9    0
AltGraph
or Ctrl Alt        {   [   ]    }

On Apple:          7   8   9    0
Alt                    [   ]
Ctrl Alt               {   }
 */
				case '8' :
					insertStr = "\u2078"; // alt-8 -> unicode superscript 8
					break;
				case '9' :
					insertStr = "\u2079"; // alt-9 -> unicode superscript 9
					break;
				}

			if (!insertStr.equals("")) {
				int pos = getCaretPosition();
				String oldText = getText();
				StringBuffer sb = new StringBuffer();
				sb.append(oldText.substring(0, pos));
				sb.append(insertStr);
				sb.append(oldText.substring(pos));            
				setText(sb.toString());
				setCaretPosition(pos + insertStr.length());
				e.consume();
			}
		}


		updateCurrentWord();
		updateAutoCompletion();

		/*
        if (charCodePressed == KeyEvent.VK_BACK_SPACE &&
          isTextSelected && input.length() > 0) {
            setText(input.substring(0, input.length()));
        }*/
	}      

	public void keyTyped(KeyEvent e) {      
		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (Application.MAC_OS && e.isControlDown())
			e.consume();

	}   



	protected String lookup(String s) {
		if(dict != null)
			return dict.lookup(s);
		return null;
	}

	/**
	 * Updates curWord to word at current caret position.
	 * curWordStart, curWordEnd are set to this word's start and end position
	 */
	private void updateCurrentWord() {                    
		String text = getText();  
		if (text == null) return;
		int caretPos = getCaretPosition();          

		// search to the left
		curWordStart = caretPos - 1;
		while (  curWordStart >= 0 &&
				Character.isLetter( text.charAt(curWordStart))) --curWordStart;     
		curWordStart++;

		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while ( curWordEnd <  length &&
				Character.isLetter( text.charAt(curWordEnd) )) ++curWordEnd;        

		curWord.setLength(0);
		curWord.append(text.substring(curWordStart, curWordEnd));
	}

	// returns the word at position pos in text
	public static String getWordAtPos(String text, int pos) {
		// search to the left
		int wordStart = pos - 1;
		while (  wordStart >= 0 &&
				isLetterOrDigit( text.charAt(wordStart)))   --wordStart;
		wordStart++;

		// search to the right
		int wordEnd= pos;
		int length = text.length();
		while (   wordEnd < length &&
				isLetterOrDigit( text.charAt(wordEnd) ))    ++wordEnd;

		if (wordStart >= 0 && wordEnd <= length)
			return text.substring(wordStart, wordEnd);
		else 
			return null;
	}

	private static boolean isLetterOrDigit(char character) {
		switch (character) {
		case '_':  // allow underscore as a valid letter in an autocompletion word
			return true;

		default:
			return Character.isLetterOrDigit(character);
		}
	}

	/**
	 * returns wheter the input field's text was changed due to auotcompletion
	 */ 
	private void updateAutoCompletion() { 
		//    start autocompletion only for words with at least two characters                
		if (curWord.length() < 2)  return;
		int caretPos = getCaretPosition();
		String text = getText();

		// make first letter of word uppercase as every command starts
		// with an upper case letter
		curWord.setCharAt(0, Character.toUpperCase(curWord.charAt(0)));

		// lookup command that starts with current word
		String cmd = lookup(curWord.toString());  
		if (cmd == null)
			return;     

		// build new autocompletion text
		StringBuffer sb = new StringBuffer();
		sb.append(cmd);
		sb.append("[]"); // add brackets
		cmd = sb.toString();            

		// insert the command into current text   
		sb.setLength(0);
		sb.append(text.substring(0, caretPos));
		String cmdTail = cmd.substring(caretPos - curWordStart);
		sb.append(cmdTail);
		sb.append(text.substring(caretPos));

		setText(sb.toString());

		//Application.debug("set selection: " + caretPos + ", " + end);


		//setSelectionEnd(caretPos + cmdTail.length());
		//setSelectionStart(caretPos);    
		//setCaretPosition(caretPos);

		setCaretPosition(caretPos + cmdTail.length());
		moveCaretPosition(caretPos);


		// change current word
		curWord.setLength(0);
		curWord.append(cmd);
		curWordStart = caretPos - curWordStart;
	}

	private boolean processAutoCompletionKey() {  	  	  	
		String selText = getSelectedText();

		// if the selection is a command name remove the
		// selection and set the right case of the command name
		if (selText != null && selText.endsWith("[]")) {
			int pos = getSelectionEnd();  
			String text = getText();
			String selWord = getWordAtPos(text, pos-2);
			if (selWord == null) return false;

			String cmdWord = dict.lookup(selWord);
			if (cmdWord == null ||
					cmdWord.length() != selWord.length()) return false;

			StringBuffer sb = new StringBuffer();
			int startPos = pos - selWord.length() - 2;
			if (startPos > 0)
				sb.append(text.substring(0, startPos));
			sb.append(cmdWord); 
			sb.append("[]");    	
			if (pos < text.length())
				sb.append(text.substring(pos, text.length()));
			setText(sb.toString());

			// move caret left to get inside the bracket
			setCaretPosition(pos - 1);          
			return true;                                
		}
		return false;
	}

	/**
	 * Adds string to input textfield's history
	 * @param str
	 */          
	public void addToHistory(String str) {
		history.add(str);
		historyIndex = history.size();
	}

	/**
	 * @return previous input from input textfield's history
	 */       
	private String getPreviousInput() {
		if (history.size() == 0) return null;
		if (historyIndex > 0) --historyIndex;
		return (String) history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {       
		if (historyIndex < history.size()) ++historyIndex;
		if (historyIndex == history.size()) 
			return null;          
		else 
			return (String) history.get(historyIndex);
	}

	/**
	 * shows dialog with syntax info for current command
	 */
	private void showCommandHelp(boolean goToWebManual) {   
		// show help for current command (current word)
		String cmd = getCurrentWord();
		String help = getCmdSyntax(cmd);

		// maybe this didn't work because we are between the parentheses [ ... ]
		// try harder and get the word left to the last "["
		if (help == null) {    
			int oldCaretPos = getCaretPosition();
			String leftText = getText().substring(0, oldCaretPos);

			int pos = leftText.lastIndexOf("[");
			if (pos > -1) {
				cmd = AutoCompleteTextField.getWordAtPos(leftText, pos);               
				help = getCmdSyntax(cmd);             
			}
		}

		// show help if available
		if (help != null) {
			if (goToWebManual) app.getGuiManager().openHelp(cmd); // TEST CODE
			else app.showHelp(help); // ORIGINAL CODE
		} else {
			app.getGuiManager().openHelp(null);
		}
	}

	/**
	 * @param command: command name in local language
	 * @return syntax description of command as html text or null
	 */
	private String getCmdSyntax(String command) {
		if (command == null || command.length() == 0) return null;

		// try macro first
		Macro macro = app.getKernel().getMacro(command);
		if (macro != null) {
			return macro.toString();
		}

		// translate command to internal name and get syntax description
		// note: the translation ignores the case of command
		String internalCmd = app.translateCommand(command);
		String key = internalCmd + "Syntax";
		String syntax = app.getCommand(key);

		// check if we really found syntax information
		if (key.equals(syntax)) return null;

		// build html tooltip
		syntax = syntax.replaceAll("<", "&lt;");
		syntax = syntax.replaceAll(">", "&gt;");
		syntax = syntax.replaceAll("\n", "<br>");
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append(syntax);
		sb.append("</html>");
		return sb.toString();
	}





}

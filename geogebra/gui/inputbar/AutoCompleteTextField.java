package geogebra.gui.inputbar;
import geogebra.gui.MathTextField;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;
import geogebra.util.AutoCompleteDictionary;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

public class AutoCompleteTextField extends MathTextField implements 
AutoComplete, KeyListener, GeoElementSelectionListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private StringBuilder curWord;       
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	protected boolean autoComplete;
	private int historyIndex;
	private ArrayList history;  
	private boolean handleEscapeKey = false;

	// vars for the symbol table popup (G.Sturr 2010-11-15) 
	private JPopupMenu popup;
	private AutoCompleteTextField myAutoField = this;
	private SelectionTable symbolTable;
	
	
	
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
		super(app.getGuiManager());
		setColumns(columns);

		this.app = app;
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList(50);

		//addKeyListener(this); now in MathTextField
		setDictionary(app.getCommandDictionary());   
		
		//create popup with symbol table
		createPopup();
		
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
		
		if (autoComplete) app.initTranslatedCommands();

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
		if (geo != null) {				
			replaceSelection(" " + geo.getLabel() + " ");
			requestFocusInWindow();	
		}
	}

	
	
	
	/** Gets the pixel location of the caret. Used to locate the popup. */
	private Point getCaretPixelPosition(){
		int width = this.getSize().width;  
		int position = this.getCaretPosition();  
		Rectangle r;
		try {
			r = this.modelToView(position);
		} catch (BadLocationException e) {
			r = null;
		}  
		return new Point(r.x, r.y - popup.getPreferredSize().height-10);
	}
	
	
	/** 
	 * Creates an instance of JPopupMenu and adds a symbol table to it.
	 */
	private void createPopup(){
		popup = new JPopupMenu();
		symbolTable = new SelectionTable(app, InputPanel.symbols, -1,6, new Dimension(20,16), SelectionTable.MODE_TEXT);
		symbolTable.setShowGrid(true);
		symbolTable.setHorizontalAlignment(SwingConstants.CENTER);
		symbolTable.setBorder(BorderFactory.createEtchedBorder());
		symbolTable.setSelectedIndex(1);
		symbolTable.addKeyListener(this);
		popup.add(symbolTable);
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
		
		//=============================
		// popup handling
		
		// show popup on ctrl-up_arrow
		if ((e.isControlDown()||Application.isControlDown(e)) && keyCode == KeyEvent.VK_UP){
			popup.show(myAutoField, getCaretPixelPosition().x, getCaretPixelPosition().y);
			symbolTable.requestFocus();
			return;
		}
		
		// handle key presses from the popup symbol table
		if(e.getSource()==symbolTable){
			if(keyCode == KeyEvent.VK_ENTER){
				insertString((String) symbolTable.getSelectedValue());
				popup.setVisible(false);
			}
			if(keyCode == KeyEvent.VK_ESCAPE){
				popup.setVisible(false);
			}
			return;
		}
		// END popup handling
		//=============================
		
		
		ctrlC = false;

		switch (keyCode) {
		
		case KeyEvent.VK_0:
		case KeyEvent.VK_1:
		case KeyEvent.VK_2:
		case KeyEvent.VK_3:
		case KeyEvent.VK_4:
		case KeyEvent.VK_5:
		case KeyEvent.VK_6:
		case KeyEvent.VK_7:
		case KeyEvent.VK_8:
		case KeyEvent.VK_9:
			if (Application.isControlDown(e) && e.isShiftDown())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;

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

			setText(null);
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

		case KeyEvent.VK_F9: 
			// needed for applets
			if (app.isApplet())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;

			/*
		case KeyEvent.VK_F1:            	
			updateCurrentWord();
			showCommandHelp(false);
			break;          */
			/* experimental - jump striaght to manual page for current command
			 * could replace F1 if it works - check with accents/asian languages */
            case KeyEvent.VK_F1:
                updateCurrentWord();
                showCommandHelp(true);
                break;          
			 
		default:                                
		}                                   
	}


	public void keyReleased(KeyEvent e) {

		//Application.debug(e+"");
		
		/* test code to generate unicode strings for Virtual Keyboard
		String text = getText();
		String outStr = "";
		for (int i = 0 ; i < text.length() ; i++) {
			int ch = text.charAt(i);
			if (ch < 128) outStr += text.charAt(i);
			else {
				String unicode = Integer.toHexString(ch);
				if (unicode.length() < 4) unicode = "\\u0"+unicode;
				else unicode = "\\u"+unicode;
				outStr += unicode;
			}
		}
		Application.debug(outStr);
		//*/

		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = Application.MAC_OS ? e.isControlDown() : e.isAltDown();
		
		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			modifierKeyPressed = false;




		char charPressed = e.getKeyChar();  

		if ( (!Character.isLetterOrDigit(charPressed) && !modifierKeyPressed) || 
				(ctrlC && Application.MAC_OS) // don't want selection cleared
		) return;        


		clearSelection();

		//Application.debug(e.isAltDown()+"");

		
		// handle alt-p etc
		super.keyReleased(e);

		if (getAutoComplete()) {
			updateCurrentWord();
			updateAutoCompletion();
		}

		/*
        if (charCodePressed == KeyEvent.VK_BACK_SPACE &&
          isTextSelected && input.length() > 0) {
            setText(input.substring(0, input.length()));
        }*/	
	}      
	
	private void clearSelection() {
		int start = getSelectionStart();
		int end = getSelectionEnd();        
		//    clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));            
			setText(sb.toString());
			if (pos < sb.length()) setCaretPosition(pos);
		}
	}
	
	/**
	 * Automatically closes parentheses (, {, [ when next sign
	 * is a space or end of input text.
	 * and ignores ] }, ) if the brackets already match (simple check)
	 */
	public void keyTyped(KeyEvent e) {
		// only handle parentheses
		char ch = e.getKeyChar();
		if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')' || ch == ']')) {
			super.keyTyped(e);
			return;
		}
		
		clearSelection();
		int caretPos = getCaretPosition();
		String text = getText();
		
		if (ch == '}' || ch == ')' || ch == ']') {
			
			// simple check if brackets match
			if (text.length() > caretPos && text.charAt(caretPos)==ch) {
				int count = 0;
				for (int i = 0 ; i < text.length() ; i++) {
					char c = text.charAt(i);
					if (c == '{') count++;
					else if (c == '}') count--;
					else if (c == '(') count+=1E3;
					else if (c == ')') count-=1E3;
					else if (c == '[') count+=1E6;
					else if (c == ']') count-=1E6;
				}
			
				if (count == 0) { 
					// if brackets match, just move the cursor forwards one
					e.consume();
					caretPos++;
				} 
			}
			
		}

		
		// auto-close parentheses
		if (caretPos == text.length() || Character.isWhitespace(text.charAt(caretPos))) {		
			switch (ch){
				case '(':
					// opening parentheses: insert closing parenthesis automatically
					insertString(")");
					break;	
					
				case '{':
					// opening braces: insert closing parenthesis automatically
					insertString("}");
					break;
					
				case '[':
					// opening bracket: insert closing parenthesis automatically
					insertString("]");
					break;
			}
		}
		
		// make sure we keep the previous caret position
		setCaretPosition(Math.min(text.length(), caretPos));
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
	public void updateCurrentWord() {                    
		String text = getText();  
		if (text == null) return;
		int caretPos = getCaretPosition();          

		// search to the left
		curWordStart = caretPos - 1;
		while (  curWordStart >= 0 &&
				Character.isLetterOrDigit( text.charAt(curWordStart))) --curWordStart;     
		curWordStart++;

		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while ( curWordEnd <  length &&
				Character.isLetterOrDigit( text.charAt(curWordEnd) )) ++curWordEnd;        

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
	public void updateAutoCompletion() { 
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
		StringBuilder sb = new StringBuilder();
		sb.append(cmd);
		sb.append("[]"); // add brackets
		cmd = sb.toString();            

		// insert the command into current text   
		sb.setLength(0);
		sb.append(text.substring(0, caretPos));
		String cmdTail = cmd.substring(caretPos - curWordStart);
		sb.append(cmdTail);
		String afterCaret = text.substring(caretPos);
		if (afterCaret.startsWith("[]"))
			sb.append(afterCaret.substring(2));
		else
			sb.append(afterCaret);

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

			StringBuilder sb = new StringBuilder();
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
		
		if (goToWebManual) {
			app.getGuiManager().openHelp(cmd); // TEST CODE
			return;
		}

		// show help if available
		if (help != null) {
			app.showHelp(help); // ORIGINAL CODE
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
		//String key = internalCmd + "Syntax";
		//String syntax = app.getCommand(key);
		String syntax = app.getCommandSyntax(internalCmd);

		// check if we really found syntax information
		//if (key.equals(syntax)) return null;
		if (syntax.indexOf("Syntax") == -1) return null;

		// build html tooltip
		syntax = syntax.replaceAll("<", "&lt;");
		syntax = syntax.replaceAll(">", "&gt;");
		syntax = syntax.replaceAll("\n", "<br>");
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(syntax);
		sb.append("</html>");
		return sb.toString();
	}





}

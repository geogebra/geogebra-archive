package geogebra.gui.inputbar;
import geogebra.gui.MathTextField;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;
import geogebra.util.AutoCompleteDictionary;
import geogebra.util.Util;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class AutoCompleteTextField extends MathTextField implements 
AutoComplete, KeyListener, GeoElementSelectionListener {

	private static final long serialVersionUID = 1L;

	private Application app;
	private StringBuilder curWord;       
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	protected boolean autoComplete;
	private int historyIndex;
	private ArrayList<String> history;  
	private boolean handleEscapeKey = false;
	
	
	/**
	 * Flag to determine if text must start with "=" to activate autoComplete;
	 * used with spreadsheet cells
	 */
	private boolean isEqualsRequired = false; 
	

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up.
	 * A default model is created and the number of columns is 0.
	 *
	 */
	public AutoCompleteTextField(int columns, Application app) {
		this(columns, app, true); 
	}    

	public AutoCompleteTextField(int columns, Application app, boolean handleEscapeKey, AutoCompleteDictionary dict) {
		super(app);
		setColumns(columns);

		this.app = app;
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList<String>(50);

		//addKeyListener(this); now in MathTextField
		setDictionary(dict);
		
	}   
	
	
	public AutoCompleteTextField(int columns, Application app, boolean handleEscapeKey){
		this(columns, app, handleEscapeKey, app.getCommandDictionary());
	}

	
	public void showPopupSymbolButton(boolean showPopupSymbolButton){
		((MyTextField)this).setShowSymbolTableIcon(showPopupSymbolButton);
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

	
	/** returns if text must start with "=" to activate autocomplete */
	public boolean isEqualsRequired() {
		return isEqualsRequired;
	}
	
	/** sets flag to require text starts with "=" to activate autocomplete */
	public void setEqualsRequired(boolean isEqualsRequired) {
		this.isEqualsRequired = isEqualsRequired;
	}
	
	


	

	//----------------------------------------------------------------------------
	// Protected methods
	//----------------------------------------------------------------------------

	boolean ctrlC = false;

	
	

	public void keyPressed(KeyEvent e) {        
		int keyCode = e.getKeyCode(); 
				
		// special processing for Korean
		// don't check for language (may have changed & this still needs clearing!)
		if (lastTyped != null) {
			if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_BACK_SPACE) {
				koreanRawText.setLength(0);
				lastTyped = null;
				Application.debug("clearing Korean buffer");
			}
		}
		
		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (Application.MAC_OS && e.isControlDown())
			e.consume();

		
		
		ctrlC = false;

		switch (keyCode) {
		
		case KeyEvent.VK_Z:
		case KeyEvent.VK_Y:
			if (Application.isControlDown(e)) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
				e.consume();
			}
			break;

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
                e.consume();
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

		if ( (!isLetterOrDigit(charPressed) && !modifierKeyPressed) || 
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
	
	private StringBuilder koreanRawText = new StringBuilder();;
	
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
		
		String text = getText();
		
		clearSelection();
		int caretPos = getCaretPosition();
		
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
		
		if (dict == null) return null;
			
		String sKorean = flattenKorean(s);
		
		Iterator<String> it = dict.getIterator();
		while (it.hasNext()) {
			String str = it.next();
			
			if (flattenKorean(str).startsWith(sKorean)){
				return str;
			}
		}
		
		String ret = dict.lookup(s);
		
		// make sure when we type eg CA we get CAuchy not Cauchy, in case we want CA=33
		if (ret != null && !ret.startsWith(s)) {
			ret = s + ret.substring(s.length());
		}
		
		return ret;
	}
	
	StringBuilder koreanSB;
	
	HashMap<Character, Character> koreanLeadToTail = new HashMap<Character, Character>();
	{
		koreanLeadToTail.put(new Character('\u1100'), new Character('\u11a8'));
		koreanLeadToTail.put(new Character('\u1101'), new Character('\u11a9'));
		koreanLeadToTail.put(new Character('\u1102'), new Character('\u11ab'));
		koreanLeadToTail.put(new Character('\u1103'), new Character('\u11ae'));
		koreanLeadToTail.put(new Character('\u1104'), new Character('\u1104')); // map to itself
		koreanLeadToTail.put(new Character('\u1105'), new Character('\u11af'));
		koreanLeadToTail.put(new Character('\u1106'), new Character('\u11b7'));
		koreanLeadToTail.put(new Character('\u1107'), new Character('\u11b8'));
		koreanLeadToTail.put(new Character('\u1108'), new Character('\u1108')); // map to itself
		koreanLeadToTail.put(new Character('\u1109'), new Character('\u11ba'));
		koreanLeadToTail.put(new Character('\u110a'), new Character('\u11bb'));
		koreanLeadToTail.put(new Character('\u110b'), new Character('\u11bc'));
		koreanLeadToTail.put(new Character('\u110c'), new Character('\u11bd'));
		koreanLeadToTail.put(new Character('\u110d'), new Character('\u110d')); // map to itself
		koreanLeadToTail.put(new Character('\u110e'), new Character('\u11be'));
		koreanLeadToTail.put(new Character('\u110f'), new Character('\u11bf'));
		koreanLeadToTail.put(new Character('\u1110'), new Character('\u11c0'));
		koreanLeadToTail.put(new Character('\u1111'), new Character('\u11c1'));
		koreanLeadToTail.put(new Character('\u1112'), new Character('\u11c2'));
	}
	
	/*
	 * convert eg \uB458 to \u1103\u116e\u11af
	 */
	private String flattenKorean(String s) {
		if (koreanSB == null) koreanSB = new StringBuilder();
		else koreanSB.setLength(0);
		
		boolean lastWasVowel = false;
		
		for (int i = 0 ; i < s.length() ; i++) {
			char c = s.charAt(i);
			if (isKoreanMultiChar(c)) appendKoreanMultiChar(koreanSB, c);
			else {
				// if a "lead char" follows a vowel, turn into a "tail char"
				if (lastWasVowel && isKoreanLeadChar(c))
					koreanSB.append(koreanLeadToTail.get(new Character(c)).charValue());
				else
					koreanSB.append(c);
			}
			lastWasVowel = isKoreanVowelChar(koreanSB.charAt(koreanSB.length() - 1));
		}
		
		return koreanSB.toString();
	}
	
	private boolean isKoreanMultiChar(char c) {
		if (c >= 0xac00 && c <= 0xd7af) return true;
		
		return false;
	}
	
	private boolean isKoreanLeadChar(char c) {
		if (c >= 0x1100 && c <= 0x1112) return true;
		
		return false;
	}
	
	private boolean isKoreanVowelChar(char c) {
		if (c >= 0x1161 && c <= 0x1175) return true;
		
		return false;
	}
	
	private boolean isKoreanTailChar(char c) {
		if (c >= 0x11a8 && c <= 0x11c2) return true;
		
		return false;
	}
	
	/*
	 * 	http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html
	 */
	private void appendKoreanMultiChar(StringBuilder sb, char c) {
		char tail = (char) (0x11a7 + (c - 44032) % 28) ;
		char vowel = (char)(0x1161 + ( (c - 44032 - (tail - 0x11a7)) % 588) / 28 );
		char lead = (char)(0x1100  + (c - 44032) / 588);
		//Application.debug(Util.toHexString(c)+" decoded to "+Util.toHexString(lead)+Util.toHexString(vowel)+Util.toHexString(tail));
		sb.append(lead);
		sb.append(vowel);
		sb.append(tail);
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
				isLetterOrDigit( text.charAt(curWordStart))) --curWordStart;     
		curWordStart++;

		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while ( curWordEnd <  length &&
				isLetterOrDigit( text.charAt(curWordEnd) )) ++curWordEnd;        

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
	
	static String lastTyped = null;

	/**
	 * returns whether the input field's text was changed due to autocompletion
	 */ 
	public void updateAutoCompletion() { 
		String text = getText();
		
		if(isEqualsRequired && !text.startsWith("="))
			return;
		
		// Autocompletion for Korean enabled only for the Virtual Keyboard at present
		if (app.getLocale().getLanguage().equals("ko") && !virtualKeyboardInUse)
			return;
		
		//    start autocompletion only for words with at least two characters                
		 if (curWord.length() < 2 && !isKoreanMultiChar(curWord.length() > 0 ? curWord.charAt(0) : ' '))  
			 return;
		 int caretPos = getCaretPosition();
		
		if (lastTyped != null) {
			char lastCh = curWord.charAt(curWord.length() - 1);
			curWord.setLength(0);
			curWord.append(lastTyped);
			curWord.append(lastCh);
			//Application.debug(curWord.toString()+" "+Util.toHexString(curWord.toString()));
			lastTyped += (lastCh+"");
		}

		// make first letter of word uppercase as every command starts
		// with an upper case letter
		//curWord.setCharAt(0, Character.toUpperCase(curWord.charAt(0)));

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
		sb.append(text.substring(0, curWordStart));
		if (virtualKeyboardInUse && app.getLocale().getLanguage().equals("ko") && lastTyped == null) {
			lastTyped = text.substring(curWordStart, caretPos);
			//Application.debug("lastTyped="+lastTyped+" "+Util.toHexString(lastTyped));
			//Application.debug("text="+text+" "+Util.toHexString(text));
		}
		//Application.debug(Util.toHexString(lastTyped));
		//String cmdTail = cmd.substring(caretPos - curWordStart);
		sb.append(cmd);
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

		if (lastTyped == null) {
			setCaretPosition( cmd.length() + curWordStart);
			// highlight auto-inserted text
			moveCaretPosition(caretPos);
		} else {
			setCaretPosition(sb.toString().indexOf("[]", curWordStart) );
		}


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

			String cmdWord = lookup(selWord);
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
		return history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {       
		if (historyIndex < history.size()) ++historyIndex;
		if (historyIndex == history.size()) 
			return null;          
		else 
			return history.get(historyIndex);
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
			app.getGuiManager().openCommandHelp(cmd); // TEST CODE
			return;
		}

		// show help if available
		if (help != null) {
			app.showHelp(help); // ORIGINAL CODE
		} else {
			app.getGuiManager().openCommandHelp(null);
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
	
	private static boolean virtualKeyboardInUse = false;

	/*
	 * Autocompletion for Korean enabled only for the Virtual Keyboard at present
	 */
	public static void setVirtualKeyboardInUse(boolean b) {
		virtualKeyboardInUse = b;
		if (!b) lastTyped = null;		
		//Application.debug("using VK:"+b);
	}





}

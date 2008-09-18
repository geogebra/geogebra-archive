package geogebra.algebra.autocomplete;
import geogebra.Application;
import geogebra.GeoElementSelectionListener;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Macro;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

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
    protected boolean autoComplete = true;
    private int historyIndex;
    private ArrayList history;  
    
    /**
   * Constructs a new AutoCompleteTextField that uses the dictionary of the
   * given Application for autocomplete look up.
   * A default model is created and the number of columns is 0.
   *
   */
  public AutoCompleteTextField(int columns, Application app) {
    setColumns(columns);
    
	this.app = app;
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
    autoComplete = val;
  }

  /**
   * Gets whether the component is currently performing autocomplete lookups as
   * keystrokes are performed.
   *
   * @return True or false.
   */
  public boolean getAutoComplete() {
    return autoComplete;
  }
  
  public String getCurrentWord() {
    return curWord.toString();
  }  
    
    public void geoElementSelected(GeoElement geo, boolean add) {
        replaceSelection(geo.getLabel());   
        requestFocus();
    }

  //----------------------------------------------------------------------------
  // Protected methods
  //----------------------------------------------------------------------------


    public void keyPressed(KeyEvent e) {        
        int keyCode = e.getKeyCode();    
        Application.debug(keyCode+"");
        switch (keyCode) {
        
        // MacOS: cmd-C does a cut rather than a copy, so do it ourselves
        case KeyEvent.VK_C:
        	if (Application.MAC_OS) {
	            int start = getSelectionStart();
	            int end = getSelectionEnd();        
	            
	            if (start != end) {
	                String textToCopy = getText().substring(start, end);
		    		Toolkit toolkit = Toolkit.getDefaultToolkit();
		    		Clipboard clipboard = toolkit.getSystemClipboard();
		    		StringSelection stringSelection = new StringSelection(textToCopy);
		    		clipboard.setContents(stringSelection, null);
	            }
	        	e.consume();
        	}
            break;
            
            /*
             * this code works, but doesn't seem to be needed on Mac
        case KeyEvent.VK_V:
            start = getSelectionStart();
            end = getSelectionEnd();        
            String stringToPaste = app.getStringFromClipboard();
            if (stringToPaste.length() != 0) {
                  int pos = getCaretPosition();
                  String oldText = getText();
                  StringBuffer sb = new StringBuffer();
                  sb.append(oldText.substring(0, start));
                  sb.append(stringToPaste);
                  sb.append(oldText.substring(end));            
                  setText(sb.toString());
                  setCaretPosition(pos + stringToPaste.length());
            }
        	e.consume();
        break;*/
        
        
            // process input
            case KeyEvent.VK_ENTER:                             
                // processEnterKey accepts a selection if there is one 
                // in this case the ENTER key event is consumed 
                // so that it is not processed by other objects (e.g. AlgebraInput)
                if (autoComplete && processAutoCompletionKey()) {
                    e.consume();                
                }                 
                break;

             // clear textfield
            case KeyEvent.VK_ESCAPE:   
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
                 String text = getPreviousInput();
                 if (text != null) setText(text);
                 break;

            case KeyEvent.VK_DOWN:
                 setText(getNextInput());
                 break; 
     
            case KeyEvent.VK_F1:
                updateCurrentWord();
                showCommandHelp(false);
                break;          

            case KeyEvent.VK_F4:
                updateCurrentWord();
                showCommandHelp(true);
                break;          

            default:                                
        }                                   
  }
  
  
  public void keyReleased(KeyEvent e) {
    if (!autoComplete) return;
    
    char charPressed = e.getKeyChar();        

    if (!Character.isLetter(charPressed)) return;        
    
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
          setCaretPosition(pos);
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
              Character.isLetterOrDigit( text.charAt(wordStart)))   --wordStart;
      wordStart++;
    
      // search to the right
      int wordEnd= pos;
      int length = text.length();
      while (   wordEnd < length &&
                  Character.isLetterOrDigit( text.charAt(wordEnd) ))    ++wordEnd;
        
      if (wordStart >= 0 && wordEnd <= length)
      	return text.substring(wordStart, wordEnd);
      else 
      	return null;
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
        	 if (goToWebManual) app.openHelp(cmd); // TEST CODE
        	 else app.showHelp(help); // ORIGINAL CODE
         } else {
             app.openHelp(null);
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

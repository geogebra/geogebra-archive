package geogebra.gui.virtualkeyboard;

import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.main.GuiManager;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class MyTextField extends JTextField implements FocusListener, VirtualKeyboardListener {
	
	GuiManager guiManager;
	
	public MyTextField(GuiManager guiManager) {
		super();
		this.guiManager = guiManager;
		addFocusListener(this);

		
	}

	public void focusGained(FocusEvent e) {
		guiManager.setCurrentTextfield((VirtualKeyboardListener)this);
		
	}

	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null);
		
	}
	
	public void insertString(String text) {

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

		
		int pos = getCaretPosition();
		String oldText = getText();
		StringBuffer sb = new StringBuffer();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));            
		setText(sb.toString());

		setCaretPosition(pos + text.length());
		
		
		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField)this;
			tf.updateCurrentWord();
			tf.updateAutoCompletion();
		}


	}

}

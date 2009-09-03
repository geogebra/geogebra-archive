package geogebra.gui;

import geogebra.main.GuiManager;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class MyTextField extends JTextField implements FocusListener {
	
	GuiManager guiManager;
	
	public MyTextField(GuiManager guiManager) {
		super();
		this.guiManager = guiManager;
		addFocusListener(this);

		
	}

	public void focusGained(FocusEvent e) {
		guiManager.setCurrentTextfield(this);
		
	}

	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null);
		
	}
	
	public void insertString(String text) {
		int pos = getCaretPosition();
		String oldText = getText();
		StringBuffer sb = new StringBuffer();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));            
		setText(sb.toString());

		setCaretPosition(pos + text.length());

	}

}

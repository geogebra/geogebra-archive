package geogebra.gui;

import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

import javax.swing.JTextField;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * Alt-o inserts a degree sign at the end (only one allowed)
 * Alt-p inserts pi at the end (only one allowed)
 * Ctrl-o Ctrl-p on Mac OSX
 */

public class AngleTextField extends JTextField implements KeyListener {

	public AngleTextField(int i) {
		super(i);
		this.addKeyListener(this);
	}

	public void keyPressed(KeyEvent e) {   
	}

	public void keyTyped(KeyEvent e) {    
	}

	public void keyReleased(KeyEvent e) {   
		
		boolean modifierKeyPressed = Application.MAC_OS ? e.isControlDown() : e.isAltDown();

		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			modifierKeyPressed = false;

		//Application.debug(e+"");
		
		String insertString = "";
		
		switch (KeyEvent.getKeyText(e.getKeyCode()).toLowerCase(Locale.US).charAt(0)) {
		case 'o':
			insertString = "\u00b0"; // degree symbol
			break;
		case 'p':
			insertString = "\u03c0"; // pi
			break;
		}

		if (modifierKeyPressed 
				&& !insertString.equals(""))
		{
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

			String oldText = getText();
			
			// don't insert more than one degree sign or pi *in total*
			if (oldText.indexOf('\u00b0') == -1 && oldText.indexOf('\u03c0') == -1) {
				int pos = oldText.length(); // getCaretPosition();
				StringBuffer sb = new StringBuffer();
				sb.append(oldText.substring(0, pos));
				sb.append(insertString);
				sb.append(oldText.substring(pos));            
				setText(sb.toString());
				setCaretPosition(pos + insertString.length());
			}

			e.consume();
		}
	}


}

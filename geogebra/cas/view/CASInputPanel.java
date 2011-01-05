/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class CASInputPanel extends JPanel {

	private AutoCompleteTextField inputArea;

	private Application app;
	
	public CASInputPanel(Application app) {
		this.app = app;
		
		setBackground(Color.white);		
		setLayout(new BorderLayout(0,0));
		
		// use autocomplete text field from input bar 
		// but ignore Escape, Up, Down keys
		inputArea = new AutoCompleteTextField(1, app, false);	
		inputArea.setAutoComplete(true);
		inputArea.showPopupSymbolButton(true);
		inputArea.setBorder(BorderFactory.createEmptyBorder());						
		add(inputArea, BorderLayout.CENTER);
		
//		inputArea.addFocusListener(new FocusListener() {
//
//			public void focusGained(FocusEvent e) {
//				String text = inputArea.getText();
//				int pos = text != null ? text.length() : 0;
//				inputArea.setCaretPosition(pos);
//				inputArea.setSelectionStart(pos);
//				inputArea.setSelectionEnd(pos);
//			}
//
//			public void focusLost(FocusEvent e) {
//				
//			}
//			
//		});
	}

	public void setInput(String inValue) {
		inputArea.setText(inValue);
	}

	public String getInput() {
		return inputArea.getText();
	}

	public JTextComponent getInputArea() {
		return inputArea;
	}

	public void setInputAreaFocused() {
		inputArea.requestFocusInWindow();
	}

	final public void setFont(Font ft) {
		super.setFont(ft);

		if (inputArea != null)
			inputArea.setFont(ft);
	}
	
}

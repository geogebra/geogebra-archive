/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class CASInputPanel extends JPanel {


	private JTextField inputArea;

	public CASInputPanel() {
		setBackground(Color.white);		
		setLayout(new BorderLayout(0,0));
		
		inputArea = new JTextField(20);	
		inputArea.setBorder(BorderFactory.createEmptyBorder());						
		add(inputArea, BorderLayout.CENTER);
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
		inputArea.requestFocus();
		String text = inputArea.getText();
		if (text != null)
			inputArea.setCaretPosition(text.length());
	}

	final public void setFont(Font ft) {
		super.setFont(ft);

		if (inputArea != null)
			inputArea.setFont(ft);
	}
	
}

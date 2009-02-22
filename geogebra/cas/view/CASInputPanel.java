/**
 * This panel is for the input.
 */

package geogebra.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class CASInputPanel extends JPanel {

	private JLabel inputSign;
	private JTextField inputArea;

	public CASInputPanel() {
		inputSign = new JLabel(" >>");
		inputArea = new JTextField(20);
		inputArea.setBorder(BorderFactory.createEmptyBorder());

		setLayout(new BorderLayout(5,5));
		add(inputSign, BorderLayout.WEST);
		add(inputArea, BorderLayout.CENTER);				
		setBackground(Color.white);
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
		if (inputSign != null)
			inputSign.setFont(ft);
	}
	
}

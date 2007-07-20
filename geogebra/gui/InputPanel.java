package geogebra.gui;

import geogebra.Application;
import geogebra.algebra.autocomplete.AutoCompleteTextField;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 * @author Markus Hohenwarter
 */
public class InputPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private static String [] specialChars = { 	
		"\u00b0", // degree	
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u03c0", // pi	
		Kernel.EULER_STRING, // e
		"sqrt(x)",
		"cbrt(x)",
		"abs(x)",
		"ln(x)",
		"sin(x)",
		"asin(x)",
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR
	};
	
	public final static String [] greekLowerCase = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9"
	};
	
	public static String [] greekUpperCase = {
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9"  // Omega
	};
	
	private JTextComponent textComponent;	
	private JComboBox cbSpecialChars, cbGreekLetters;
	
	public InputPanel(String initText, Application app, int columns, boolean autoComplete) {
		this(initText, app, 1, columns, true, true);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(autoComplete);
	}		
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSpecialChars,
						boolean showGreekLetters) {
		if (rows > 1) 
			textComponent = new JTextArea(rows, columns);
		else
			textComponent = new AutoCompleteTextField(columns, app);		
		
		
		if (initText != null) textComponent.setText(initText);		
		cbSpecialChars = new JComboBox();
		cbGreekLetters  = new JComboBox();		
		textComponent.setFocusable(true);
		cbGreekLetters.setFocusable(false);
		cbSpecialChars.setFocusable(false);		
		
		for (int i=0; i < specialChars.length; i++) {
			cbSpecialChars.addItem(specialChars[i]);
		}
		
		// set up greek letter combo box
		for (int i=0; i < greekLowerCase.length; i++) {
			cbGreekLetters.addItem(greekLowerCase[i]);
		}
		for (int i=0; i < greekUpperCase.length; i++) {
			cbGreekLetters.addItem(greekUpperCase[i]);
		}
		
		ComboBoxListener cbl = new ComboBoxListener();
		cbSpecialChars.addActionListener(cbl);			
		cbSpecialChars.addMouseListener(cbl);
		cbGreekLetters.addActionListener(cbl);	
		cbGreekLetters.addMouseListener(cbl);	
		
		if (rows > 1) { // JTextArea
			setLayout(new BorderLayout(5, 5));	
			JScrollPane sp = new JScrollPane(textComponent); 
			//sp.setPreferredSize(new Dimension(300, 200));
			sp.setAutoscrolls(true);
			add(sp, BorderLayout.CENTER);
			
			JPanel cbPanel = new JPanel(new BorderLayout());
			JPanel tempPanel = new JPanel(new BorderLayout(0, 3));			
			tempPanel.add(cbSpecialChars, BorderLayout.NORTH);						
			tempPanel.add(cbGreekLetters, BorderLayout.SOUTH);		
			cbPanel.add(tempPanel, BorderLayout.NORTH);						
			add(cbPanel, BorderLayout.EAST);			
		} 
		else { // JTextField
			setLayout(new BorderLayout(5,5));
			add(textComponent, BorderLayout.CENTER);
			
			JPanel cbPanel = new JPanel(new BorderLayout(2,0));			
			cbPanel.add(cbSpecialChars, BorderLayout.WEST);
			cbPanel.add(cbGreekLetters, BorderLayout.EAST);
			add(cbPanel, BorderLayout.EAST);	
		}		
		
		cbSpecialChars.setVisible(showSpecialChars);
		cbGreekLetters.setVisible(showGreekLetters);
	}
	
	public void showSpecialChars(boolean flag) {
		cbSpecialChars.setVisible(flag);
	}
	
	public void showGreekLetters(boolean flag) {
		cbGreekLetters.setVisible(flag);
	}
	
	public JTextComponent getTextComponent() {
		return textComponent;
	}
	
	public String getText() {
		return textComponent.getText();
	}
	
	public String getSelectedText() {
		return textComponent.getSelectedText();
	}
	
	public void selectText() { 					
		textComponent.setSelectionStart(0);
		textComponent.moveCaretPosition(textComponent.getText().length());
	}
	
	public void setText(String text) {
		textComponent.setText(text);
	}
	
	private class ComboBoxListener extends MyComboBoxListener {
		
		public void doActionPerformed(Object source) {			
			if (source == cbSpecialChars) {				
				insertString(cbSpecialChars.getSelectedItem().toString());								
			}
			else if (source == cbGreekLetters) {
				insertString(cbGreekLetters.getSelectedItem().toString());		
			}
		}
	}

	
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str: inserted string
	 */
	public void insertString(String str) {	
		textComponent.replaceSelection(str);	
	}		
}

package geogebra.gui.view.algebra;

import geogebra.gui.VirtualKeyboardListener;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.inputbar.MyComboBox;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 * @author Markus Hohenwarter
 */
public class InputPanel extends JPanel implements FocusListener, VirtualKeyboardListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Application app;

	public final static String [] displayChars = { 	
		"\u2245", // congruent	
		"\u2261",  // equivalent
		"\u2221",  // angle
		"\u2206"  // triangle
	};

	public final static String [] specialChars = { 	
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree	
		"\u03c0", // pi	
		Kernel.EULER_STRING, // e
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		"sqrt(x)",
		"cbrt(x)",
		"abs(x)",
		"sgn(x)",
		"ln(x)",
		"lg(x)",
		"ld(x)",
		"sin(x)",
		"cos(x)",
		"tan(x)",
		"asin(x)",
		"acos(x)",
		"atan(x)",
		"sinh(x)",
		"cosh(x)",
		"tanh(x)",
		"asinh(x)",
		"acosh(x)",
		"atanh(x)",
		"floor(x)",
		"ceil(x)",
		"round(x)",
		"gamma(x)",
		"random()",
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
	};

	
	
	public final static String [] functions = { 	
		"sqrt(x)",
		"cbrt(x)",
		"abs(x)",
		"sgn(x)",
		"ln(x)",
		"lg(x)",
		"ld(x)",
		"sin(x)",
		"cos(x)",
		"tan(x)",
		"asin(x)",
		"acos(x)",
		"atan(x)",
		"sinh(x)",
		"cosh(x)",
		"tanh(x)",
		"asinh(x)",
		"acosh(x)",
		"atanh(x)",
		"floor(x)",
		"ceil(x)",
		"round(x)",
		"gamma(x)",
		"random()",
	};

	
	public final static String [] symbols = { 	
		"\u03c0", // pi	
		Kernel.EULER_STRING, // e
		
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree			
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
	};

	
	
	
	
	public final static String [] greekLowerCase = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9"
	};

	public final static String [] greekUpperCase = {
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
	
	
	public final static String [] greek = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", "\u03c0", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9",
	
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
	
	
	
	public final static String [] tableSymbols = {
		"\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8",
		"\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd", "\u03be", "\u03bf", 
		"\u03c1", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8",
		"\u03c9",
	
		"\u0393", // Gamma
		"\u0394", // Delta
		"\u0398", // Theta
		"\u039b", // Lambda
		"\u039e", // Xi
		"\u03a0", // Pi
		"\u03a3", // Sigma
		"\u03a6", // Phi
		"\u03a8", // Psi
		"\u03a9",  // Omega
		
		
		"\u03c0", // pi	
		Kernel.EULER_STRING, // e
		
		"\u00b2",  // exponents ^2 
		"\u00b3",  // exponents ^3 
		"\u00b0", // degree			
		"\u221e", // infinity
		ExpressionNode.strVECTORPRODUCT, //  "\u2297", // vector product (circled times)
		ExpressionNode.strEQUAL_BOOLEAN,
		ExpressionNode.strNOT_EQUAL,
		ExpressionNode.strLESS_EQUAL,
		ExpressionNode.strGREATER_EQUAL,
		ExpressionNode.strNOT,
		ExpressionNode.strAND,
		ExpressionNode.strOR, 
		ExpressionNode.strPARALLEL,
		ExpressionNode.strPERPENDICULAR,
		ExpressionNode.strIS_ELEMENT_OF,
		ExpressionNode.strCONTAINS,
		ExpressionNode.strCONTAINS_STRICT,
		
		
		
	};
	
	
	
	
	
	
	private JTextComponent textComponent;	
	private MyComboBox cbSpecialChars, cbGreekLetters;

	private PopupMenuButton symbolButton;

	public PopupMenuButton getSymbolButton() {
		return symbolButton;
	}

	private PopupMenuButton functionButton;
	
	public InputPanel(String initText, Application app, int columns, boolean autoComplete) {
		this(initText, app, 1, columns, true, true, false, null);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(autoComplete);
	}		

	public InputPanel(String initText, Application app, int columns) {
		this(initText, app, 1, columns, false, false, false, null);
		AutoCompleteTextField atf = (AutoCompleteTextField) textComponent;
		atf.setAutoComplete(true);
	}		

	
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSpecialChars,
			boolean showGreekLetters, boolean showDisplayChars) {
		this(initText, app, rows, columns, showSpecialChars,
			showGreekLetters, showDisplayChars, null);
	}
	
	public InputPanel(String initText, Application app, int rows, int columns, boolean showSpecialChars,
						boolean showGreekLetters, boolean showDisplayChars, KeyListener keyListener) {
		
		this.app = app;
		
		if (rows > 1) 
			textComponent = new JTextArea(rows, columns);
		else
			textComponent = new AutoCompleteTextField(columns, app);		
		
		textComponent.addFocusListener(this);
		
		if (keyListener != null)
		textComponent.addKeyListener(keyListener);
		
		if (initText != null) textComponent.setText(initText);		
		cbSpecialChars = new MyComboBox();
		cbGreekLetters  = new MyComboBox();
		
		this.createPopupButton();
		
		// make sure we use a font that can display special characters
		cbSpecialChars.setFont(app.getFontCanDisplay(Kernel.EULER_STRING));
		cbGreekLetters.setFont(app.getFontCanDisplay("\u03b1")); // alpha
			
		textComponent.setFocusable(true);
		cbGreekLetters.setFocusable(false);
		cbSpecialChars.setFocusable(false);		
		
		for (int i=0; i < specialChars.length; i++) {
			cbSpecialChars.addItem(specialChars[i]);
		}
		
		if (showDisplayChars)
			for (int i=0; i < displayChars.length; i++) {
				cbSpecialChars.addItem(displayChars[i]);
			}

		// set up greek letter combo box
		for (int i=0; i < greekLowerCase.length; i++) {
			cbGreekLetters.addItem(greekLowerCase[i]);
		}
		for (int i=0; i < greekUpperCase.length; i++) {
			cbGreekLetters.addItem(greekUpperCase[i]);
		}
		
		// set widths of combo boxes
		cbSpecialChars.setPrototypeDisplayValue("Wa");
		cbGreekLetters.setPrototypeDisplayValue("Wa");
		
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
			//cbPanel.add(cbSpecialChars, BorderLayout.WEST);
			//cbPanel.add(cbGreekLetters, BorderLayout.EAST);
			cbPanel.add(symbolButton,BorderLayout.WEST);
			//cbPanel.add(functionButton,BorderLayout.EAST);
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
				String str = cbSpecialChars.getSelectedItem().toString();
				insertString(str);	
				if (str.length() > 1)
					cbSpecialChars.setSelectedIndex(0);
			}
			else if (source == cbGreekLetters) {
				insertString(cbGreekLetters.getSelectedItem().toString());		
			}
		}
	}

	
	private void createPopupButton(){
		
		symbolButton = new PopupMenuButton(app, tableSymbols, 5,11,new Dimension(-1,-1), SelectionTable.MODE_TEXT);
		symbolButton.setFocusable(false);
		symbolButton.setSelectedIndex(0);
		symbolButton.setKeepVisible(false);
		symbolButton.getMyTable().setShowGrid(true);
		symbolButton.getMyTable().setBorder(BorderFactory.createLineBorder(symbolButton.getMyTable().getGridColor()));
		symbolButton.getMyPopup().setBorder(BorderFactory.createEmptyBorder());
		symbolButton.addActionListener(this);
		
		functionButton = new PopupMenuButton(app, this.functions, -1,3,new Dimension(-1,16), SelectionTable.MODE_TEXT);
		functionButton.setFocusable(false);
		functionButton.setSelectedIndex(0);
		functionButton.setKeepVisible(false);
		functionButton.getMyTable().setShowGrid(true);
		functionButton.addActionListener(this);
		
	}
	
	
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str: inserted string
	 */
	public void insertString(String str) {	
		textComponent.replaceSelection(str);	
		textComponent.requestFocus();
	}		
	
	public void focusGained(FocusEvent e) {
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void focusLost(FocusEvent e) {
		app.getGuiManager().setCurrentTextfield(null, !(e.getOppositeComponent() instanceof VirtualKeyboard));
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == symbolButton){
			insertString((String) symbolButton.getSelectedValue());		
		}
			
		
	}

}

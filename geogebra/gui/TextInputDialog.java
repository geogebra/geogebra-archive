/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/
package geogebra.gui;

import geogebra.Application;
import geogebra.kernel.GeoElement;
import geogebra.util.InputDialog;
import geogebra.util.LaTeXinputHandler;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class TextInputDialog extends InputDialog {
	
	private static final long serialVersionUID = 1L;

	protected JCheckBox cbLaTeX;
	private JComboBox cbLaTeXshortcuts;
	
	/**
	 * Input Dialog for a GeoText object
	 */
	public TextInputDialog(Application app, String message, String title,
								String initText, boolean isLaTeX, boolean autoComplete,
								LaTeXinputHandler handler) {	
		super(app.getFrame(), false);
		this.app = app;
		inputHandler = handler;
		initString = initText;

		// create LaTeX checkbox
		cbLaTeX = new JCheckBox(app.getPlain("LaTeXFormula"));
		cbLaTeX.setSelected(isLaTeX);
		cbLaTeX.addActionListener(this);
		
		// add LaTeX shortcuts
		cbLaTeXshortcuts = new JComboBox();								
		cbLaTeXshortcuts.addItem("\u221a"); 											// 0 square root
		cbLaTeXshortcuts.addItem("\u221b"); 											// 1 cubic root
		cbLaTeXshortcuts.addItem("a / b");  											// 2 fraction
		cbLaTeXshortcuts.addItem(app.getPlain("Vector")); 								// 3 vector
		cbLaTeXshortcuts.addItem(app.getPlain("Segment") + " AB"); 						// 4 overline			
		cbLaTeXshortcuts.addItem("\u2211"); 											// 5 sum		
		cbLaTeXshortcuts.addItem("\u222b"); 											// 6 int
		cbLaTeXshortcuts.setFocusable(false);		
		cbLaTeXshortcuts.setEnabled(isLaTeX);		
		cbLaTeXshortcuts.addActionListener(this);
		
		createGUI(title, message, autoComplete, 30, DEFAULT_ROWS);		
		JPanel centerPanel = new JPanel(new BorderLayout());
		
		JPanel latexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		latexPanel.add(cbLaTeX);
		latexPanel.add(cbLaTeXshortcuts);							
		
		
		centerPanel.add(inputPanel, BorderLayout.CENTER);		
		centerPanel.add(latexPanel, BorderLayout.SOUTH);	
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();		
	}
	
	/**
	 * Returns state of LaTeX Formula checkbox. 
	 */
	public boolean isLaTeX() {
		return cbLaTeX.isSelected();		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		boolean finished = false;
		try {
			if (source == btApply || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();
				((LaTeXinputHandler) inputHandler).
						setLaTeX(cbLaTeX.isSelected());
				
				finished = inputHandler.processInput(inputText);				
			} 
			else if (source == btCancel) {
				finished = true;
			}
			else if (source == cbLaTeX) {
				cbLaTeXshortcuts.setEnabled(cbLaTeX.isSelected());
			}
			else if (source == cbLaTeXshortcuts) {		
				String selText = inputPanel.getSelectedText();				
				if (selText == null) selText = "";
				
				switch (cbLaTeXshortcuts.getSelectedIndex()) {
					case 0: // square root
						insertString(" \\sqrt{ " + selText + " } ");
						setRelativeCaretPosition(-3);
						break;
						
					case 1: // cubic root
						insertString(" \\sqrt[3]{ " + selText + " } ");
						setRelativeCaretPosition(-3);
						break;
						
					case 2: // fraction
						insertString(" \\frac{ " + selText + " }{ } ");
						setRelativeCaretPosition(-6);
						break;
						
					case 3: // fraction
						insertString(" \\vec{ " + selText + " } ");
						setRelativeCaretPosition(-3);
						break;
						
					case 4: // segment
						insertString(" \\overline{ " + selText + " } ");
						setRelativeCaretPosition(-3);
						break;
						
					case 5: // sum
						insertString(" \\sum_{ }^{ } " + selText);
						setRelativeCaretPosition(-7 - selText.length());
						break;
						
					case 6: // integral
						insertString(" \\int_{ }^{ } " + selText);
						setRelativeCaretPosition(-7 - selText.length());
						break;
						
					default:
				}
				
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
		}
		setVisible(!finished);
	}
	
	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * @param geo
	 */
	public void insertGeoElement(GeoElement geo) {
		if (geo == null) return;		
		
		JTextComponent textComp = inputPanel.getTextComponent();	
		textComp.replaceSelection(""); // insert empty string to get rid of a possible selection
		String text = inputPanel.getText();
		int caretPos = textComp.getCaretPosition();		
		String leftText = text.substring(0, caretPos).trim();				
		String rightText = text.substring(caretPos).trim();		
		
		StringBuffer insertedText = new StringBuffer();
		int leftQuotesAdded = 0;
		int rightQuotesAdded = 0;
		
		// check left side for quote at its end
		if (leftText.length() > 0) {
			if (!leftText.endsWith("\"")) {
				insertedText.append('"');
				leftQuotesAdded = 1;			
			}
			insertedText.append(" + ");
		}				

		// insert GeoElement's label
		insertedText.append(geo.getLabel());
		
		// 	check right side for quote at its beginning
		if (rightText.length() > 0) {			
			insertedText.append(" + ");
			if (!rightText.startsWith("\"")) {
				insertedText.append('"');
				rightQuotesAdded = 1;
			}
		}	
		
		// now really do the insertion
		textComp.replaceSelection(insertedText.toString());		
						
		// make a simple check if the quotes are ok:
		// there should be an even number of quotes to the left and to the right of the inserted label	
		text = inputPanel.getText();
		int leftQuotes = leftQuotesAdded + countChar('"', text.substring(0,caretPos));		
	
		caretPos += insertedText.length();
		int rightQuotes = rightQuotesAdded + countChar('"', text.substring(caretPos));
		
		if (leftQuotes % 2 == 1) {// try to fix the number of quotes by adding one at the beginning of text
			text = "\"" + text;
			caretPos++;
		}		
		if (rightQuotes % 2 == 1) // try to fix the number of quotes by adding one at the end of text
			text =  text + "\"";								
		
		textComp.setText(text);
		if (caretPos <= text.length())
			textComp.setCaretPosition(caretPos);
		textComp.requestFocusInWindow();
	}
	
	/**
	 * Returns how often the given char is in the given String
	 * @param str
	 * @param ch
	 * @return
	 */
	private int countChar(char ch, String str) {
		if (str == null) return 0;
		
		int count = 0;
		for (int i=0; i < str.length(); i++) {
			if (str.charAt(i) == ch) {
				
				count ++;
			}
		}
		return count;		
	}
}

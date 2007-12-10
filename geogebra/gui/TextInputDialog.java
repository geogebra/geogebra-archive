/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.Application;
import geogebra.MyError;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
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
	private JPanel latexPanel;
	private GeoText text;
	private boolean isLaTeX;
	private GeoPoint startPoint;
	
	/**
	 * Input Dialog for a GeoText object
	 */
	public TextInputDialog(Application app,  String title, GeoText text, GeoPoint startPoint,
								int cols, int rows) {	
		super(app.getFrame(), false);
		this.app = app;
		this.startPoint = startPoint;
		inputHandler = new TextInputHandler();
				
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
		cbLaTeXshortcuts.addItem(" "); 	// space
		cbLaTeXshortcuts.setFocusable(false);		
		cbLaTeXshortcuts.setEnabled(isLaTeX);	
		ComboBoxListener cbl = new ComboBoxListener();
		cbLaTeXshortcuts.addActionListener(cbl);
		cbLaTeXshortcuts.addMouseListener(cbl);
				
		createGUI(title, "", false, cols, rows, true, true, false);		
		
		// init dialog using text
		setGeoText(text);
		
		JPanel centerPanel = new JPanel(new BorderLayout());		
		latexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		latexPanel.add(cbLaTeX);
		latexPanel.add(cbLaTeXshortcuts);							
			
		centerPanel.add(inputPanel, BorderLayout.CENTER);		
		centerPanel.add(latexPanel, BorderLayout.SOUTH);	
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();		
	}
	
	private class ComboBoxListener extends MyComboBoxListener {
				
		
		public void doActionPerformed(Object source) {			
			if (source == cbLaTeXshortcuts) {		
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
						
					case 7: // space
						insertString(" \\; ");						
						break;
						
					default:
				}
				
			}
		}
	}
	
	public void setGeoText(GeoText text) {
		this.text = text;
        boolean createText = text == null;   
        isLaTeX = text == null ? false: text.isLaTeX();
        //String label = null;
        
        String descString;
        
        if (createText) {
            //initString = "\"\"";
        	initString = null;
            descString = app.getPlain("Text");
            isLaTeX = false;
        }           
        else {                                
        	//label = text.getLabel();
          
            initString = text.isIndependent() ? 
                           // "\"" + text.toValueString() + "\"" :
            		 		text.getTextString() :
                            text.getCommandDescription(); 
            descString = text.getNameDescription();
            isLaTeX = text.isLaTeX();
        }           
        
        msgLabel.setText(descString);
        inputPanel.setText(initString);
        cbLaTeX.setSelected(isLaTeX);
        cbLaTeXshortcuts.setEnabled(isLaTeX);
	}
	
	public JPanel getLaTeXPanel() {
		return latexPanel;
	}
	
	public JPanel getInputPanel() {
		return inputPanel;
	}
	
	public JButton getApplyButton() {
		return btApply;
	}
	
	
	/**
	 * Returns state of LaTeX Formula checkbox. 
	 */
	public boolean isLaTeX() {
		return cbLaTeX.isSelected();		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		try {
			if (source == btApply || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();
				isLaTeX = cbLaTeX.isSelected();
				
				boolean finished = inputHandler.processInput(inputText);	
				if (isShowing()) {				
					setVisible(!finished);
				} else {			
					text.setLaTeX(isLaTeX, true);
					setGeoText(text);
				}
			} 
			else if (source == btCancel) {
				if (isShowing())
					setVisible(false);		
				else {
					setGeoText(text);
				}
			}
			else if (source == cbLaTeX) {
				isLaTeX = cbLaTeX.isSelected();
				cbLaTeXshortcuts.setEnabled(isLaTeX);
			}			
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
		}			
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
	
	private class TextInputHandler implements InputHandler {
		
		private Kernel kernel;
		private EuclidianView euclidianView;
       
        private TextInputHandler() { 
        	kernel = app.getKernel();
        	euclidianView = app.getEuclidianView();
        }        
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;                        
          
            // no quotes?
        	if (inputValue.indexOf('"') < 0) {
            	// this should become either
            	// (1) a + "" where a is an object label or
            	// (2) "text", a plain text 
        	
        		// ad (1) OBJECT LABEL 
        		// add empty string to end to make sure
        		// that this will become a text object
        		if (kernel.lookupLabel(inputValue.trim()) != null) {
        			inputValue = "(" + inputValue + ") + \"\"";
        		} 
        		// ad (2) PLAIN TEXT
        		// add quotes to string
        		else {
        			inputValue = "\"" + inputValue + "\"";
        		}        			
        	} 
        	else {
        	   // replace \n\" by \"\n, this is useful for e.g.:
        	  //    "a = " + a + 
        	  //	"b = " + b 
        		inputValue = inputValue.replaceAll("\n\"", "\"\n");
        	}
            
            if (inputValue.equals("\"\"")) return false;
            
            // create new text
            boolean createText = text == null;
            if (createText) {
                GeoElement [] ret = 
                	kernel.getAlgebraProcessor().processAlgebraCommand(inputValue, false);
                if (ret != null && ret[0].isTextValue()) {
                    GeoText t = (GeoText) ret[0];
                    t.setLaTeX(isLaTeX, true);                 
                    
                    if (startPoint.isLabelSet()) {
                    	  try { t.setStartPoint(startPoint); }catch(Exception e){};                          
                    } else {
                    	// startpoint contains mouse coords
                    	t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX), 
                    			euclidianView.toScreenCoordY(startPoint.inhomY));
                    	t.setAbsoluteScreenLocActive(true);
                    }
                    t.updateRepaint();
                    app.storeUndoInfo();                    
                    return true;                
                }
                return false;
            }
                    
            // change existing text
            try {           
                text.setLaTeX(isLaTeX, true);
                GeoText newText = (GeoText) kernel.getAlgebraProcessor().changeGeoElement(text, inputValue, true);
                app.doAfterRedefine(newText);
                return newText != null;
			} catch (Exception e) {
                app.showError("ReplaceFailed");
                return false;
            } catch (MyError err) {
                app.showError(err);
                return false;
            } 
        }   
    }

}

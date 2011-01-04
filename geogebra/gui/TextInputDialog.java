/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.LatexTable;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.util.SymbolTable;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class TextInputDialog extends InputDialog implements DocumentListener {
	
	private static final long serialVersionUID = 1L;

	private JCheckBox cbLaTeX;
	private JToolBar toolBar;

	private JPanel previewPanel, editPanel;
	private GeoText text;
	private boolean isLaTeX;
	private GeoPoint startPoint;
	private boolean isTextMode = false;
	private LaTeXPreviewerPanel latexPreviewer;
	private TextPreviewPanel textPreviewer;

	//private JToolBar tb;
	private PopupMenuButton btInsertLaTeX;
	private PopupMenuButton btInsertUnicode;
	private PopupMenuButton btInsertGeo;

	private JLabel previewHeader, editHeader;

	
	
	/**
	 * Input Dialog for a GeoText object
	 * @param app 
	 * @param title 
	 * @param text 
	 * @param startPoint 
	 * @param cols 
	 * @param rows 
	 * @param isTextMode 
	 */
	public TextInputDialog(Application app,  String title, GeoText text, GeoPoint startPoint,
								int cols, int rows, boolean isTextMode) {	
		super(app.getFrame(), false);
		this.app = app;
		this.startPoint = startPoint;
		this.isTextMode = isTextMode;
		inputHandler = new TextInputHandler();
		
		// create LaTeX checkbox
		cbLaTeX = new JCheckBox();
		cbLaTeX.setSelected(isLaTeX);
		cbLaTeX.addActionListener(this);

		// create insertion buttons
		buildInsertLaTeXButton();
		buildInsertUnicodeButton();		
		buildInsertGeoButton();	
				
		// build input dialog GUI
		createGUI(title, "", false, cols, rows, false, false, false, false, false, false);		
		setGeoText(text);  // init dialog using text
		this.showSpecialCharacters(false);
		this.setResizable(true);
		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
		
		
		// build toolbar
		toolBar = new JToolBar();
		toolBar.add(cbLaTeX);
		toolBar.add(btInsertLaTeX);
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.addSeparator();
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.add(btInsertUnicode);
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.addSeparator();
		toolBar.add(Box.createRigidArea(new Dimension(5,1)));
		toolBar.add(btInsertGeo);
		toolBar.setFloatable(false);
		
		
		// create edit panel to contain both the input panel and toolbar
		editHeader = new JLabel();
		editHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
		editPanel = new JPanel(new BorderLayout(2,2));
		editPanel.add(editHeader, BorderLayout.NORTH);
		editPanel.add(inputPanel, BorderLayout.CENTER);
		editPanel.add(toolBar, BorderLayout.SOUTH);		
		editPanel.setBorder(BorderFactory.createEtchedBorder());
		
		
		// create preview panel
		previewPanel = new JPanel(new BorderLayout());
		previewPanel.setPreferredSize(inputPanel.getPreferredSize());
			
		/*
		if (latexPreviewer == null) 
			latexPreviewer = new LaTeXPreviewerPanel();
		latexPreviewer.setOpaque(true);
		latexPreviewer.setBackground(Color.WHITE);
		latexPreviewer.setOpaque(true);
		latexPreviewer.setBackground(Color.WHITE);
		*/
		
		if (textPreviewer == null) {		
			textPreviewer = new TextPreviewPanel(app.getKernel());
		}
		textPreviewer.updatePreviewText(text, inputPanel.getText(), isLaTeX);		
		JPanel p = new JPanel(new BorderLayout());
		p.add(textPreviewer, BorderLayout.CENTER);
		JScrollPane scroller = new JScrollPane(p);
		previewHeader = new JLabel();
		previewHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		previewPanel.add(previewHeader, BorderLayout.NORTH);	
		previewPanel.add(scroller, BorderLayout.CENTER);		
		textPreviewer.setPreferredSize(p.getPreferredSize());
		
		
		// put the preview and edit panels into a split pane
		// and make sure they are given equal weight
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, previewPanel, editPanel);
		previewPanel.setPreferredSize(editPanel.getPreferredSize());
		sp.setResizeWeight(0.5);
		sp.setBorder(BorderFactory.createEmptyBorder());
		
		// put all the sub-panels together
		JPanel centerPanel = new JPanel(new BorderLayout());		
		centerPanel.add(sp, BorderLayout.CENTER);			
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();
		
		// update the labels
		setLabels(title);
	}
	

	
	/** 
	 * builds unicode insertion button and drop down tables
	 */
	private void buildInsertUnicodeButton(){
		
		btInsertUnicode = new PopupMenuButton();
		btInsertUnicode.setKeepVisible(false);
		btInsertUnicode.setStandardButton(true);
		btInsertUnicode.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));
		//btInsertUnicode.setText("Symbols");
		
		JMenu menu = new JMenu(app.getMenu("Properties.Basic"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, SymbolTable.tableSymbols, -1, 11,SelectionTable.MODE_TEXT));
		btInsertUnicode.addPopupMenuItem(menu);
		//btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.math_ops,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.operators,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.greekUpperCaseFull,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.analysis,-1,8));
		
		
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.sets,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.logical,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.sub_superscripts,-1,10));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.basic_arrows,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.otherArrows,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.geometricShapes,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.games_music,-1,7));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.currency,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.UNICODEpointers,-1,6));
		
		/*
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.letterLikeSymbols,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.other_arrows,0,1,2));
		
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.misc,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.bullets,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.writing,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.geometricShapes,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.weather_astrology,0,1,2));
		
		btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.games_music,0,1,2));
		
		*/
		
		menu = new JMenu("JLatex");
		menu.add(new LatexTable(app, this, btInsertLaTeX, SymbolTable.JLatex, -1, 16,SelectionTable.MODE_TEXT));
	//	btInsertUnicode.addPopupMenuItem(menu);
		
		
	}

	/**
	 * creates a sub-menu for the unicode insert button
	 */
	private JMenu createMenuItem(String[] table, int rows, int columns ){
		JMenu menu = new JMenu(table[0] + " " + table[1] + " " + table[2] + "  ");
		menu.add(new LatexTable(app, this, btInsertLaTeX, table, rows, columns, SelectionTable.MODE_TEXT));
		return menu;
	}
	
	
	
	/** 
	 * builds LaTeX insertion button and drop down tables
	 */
	private void buildInsertLaTeXButton(){

		btInsertLaTeX = new PopupMenuButton();
		btInsertLaTeX.setKeepVisible(false);
		btInsertLaTeX.setStandardButton(true);
		btInsertLaTeX.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));
		btInsertLaTeX.setText("LaTeX");
		btInsertLaTeX.setEnabled(false);
		
		JMenu menu;
		menu = new JMenu(app.getMenu("RootsAndFractions"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.roots_fractions, 1, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);
		
		menu = new JMenu(app.getMenu("SumsAndIntegrals"));	
		LatexTable table = new LatexTable(app, this, btInsertLaTeX, LatexTable.sums, 1, -1,SelectionTable.MODE_LATEX);
		//table.setCaretPosition(-3);
		menu.add(table);
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Accents"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.accents, 2, -1, SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("AccentsExt"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.accentsExtended, 2, -1, SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Brackets"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.brackets, 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);
		
		menu = new JMenu(app.getMenu("Matrices"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.matrices, 1, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);
		
		menu = new JMenu(app.getMenu("FrakturLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.mathfrak(), 4, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);
		
		menu = new JMenu(app.getMenu("CalligraphicLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.mathcal(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);
		
		menu = new JMenu(app.getMenu("BlackboardLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.mathbb(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);
		
		menu = new JMenu(app.getMenu("CursiveLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, LatexTable.mathscr(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

	}
	
	
	/** 
	 * builds GeoElement insertion button 
	 */
	private void buildInsertGeoButton(){
		
		TreeSet ts = app.getKernel().getConstruction().getGeoSetLabelOrder();
		ArrayList<String> list = new ArrayList<String>(); 
		Iterator<GeoElement> iter = ts.iterator();
		while (iter.hasNext()) {
			GeoElement g = iter.next();
			if (g.isLabelSet()) {
				list.add(g.getLabel());
			}
		}
		
		final JList geoList = new JList(list.toArray());
		geoList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				
				if(!e.getValueIsAdjusting()){		
					String label = (String) geoList.getSelectedValue();
					insertGeoElement(app.getKernel().lookupLabel(label));
					btInsertGeo.handlePopupActionEvent();
					geoList.getSelectionModel().clearSelection();
				}
			}
			
		});
		
		geoList.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
		JScrollPane scroller = new JScrollPane(geoList);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		
		btInsertGeo = new PopupMenuButton();
		btInsertGeo.addPopupMenuItem(scroller);
		btInsertGeo.setKeepVisible(false);
		btInsertGeo.setStandardButton(true);
		btInsertGeo.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));
		
	};
	
	
	public void setLabels(String title) {
		super.setLabels(title);
		
		if(editHeader != null)
			editHeader.setText(app.getPlain("Edit"));
		if(previewHeader != null)
			previewHeader.setText(app.getMenu("Preview"));

		btInsertLaTeX.setText(app.getPlain("LaTeXFormula"));	
		btInsertUnicode.setText(app.getMenu("Symbols"));
		btInsertGeo.setText(app.getMenu("Objects"));	
		
	}
	
	
	/**
	 * Set content of the textarea and 
	 * LaTex checkbox from the text
	 * @param text GeoText text to be edited
	 */
	public void setGeoText(GeoText text) {
		this.text = text;
        boolean createText = text == null;   
        isLaTeX = text == null ? false: text.isLaTeX();
        //String label = null;
                       
        if (createText) {
            //initString = "\"\"";
        	initString = null;            
            isLaTeX = false;
        }           
        else {                                
        	//label = text.getLabel();
          
            initString = "";
            if(text.isIndependent()){ 
            	initString = text.getTextString();
            	if(text.getKernel().lookupLabel(initString) != null)
            		initString = "\"" + initString + "\"";            		 		
            }
            else
            	initString = text.getCommandDescription();            
            isLaTeX = text.isLaTeX();
        }           
                
        inputPanel.setText(initString);
        cbLaTeX.setSelected(false);
        if (isLaTeX) {
        	cbLaTeX.doClick();
        }
        
	}
	
	/**
	 * @return toolbar with buttons for inserting text symbols and LaTeX formulas
	 */
	public JToolBar getToolBar() {
		return toolBar;
	}
	
	/**
	 * @return panel with textarea
	 */
	public JPanel getInputPanel() {
		return inputPanel;
	}
	
	/**
	 * @return preview panel
	 */
	public JPanel getPreviewPanel() {
		return previewPanel;
	}
	
	
	/**
	 * @return apply button
	 */
	public JButton getApplyButton() {
		return btApply;
	}
	
	
	/**
	 * Returns state of LaTeX Formula checkbox. 
	 * @return true if switched to LaTeX mode
	 */
	public boolean isLaTeX() {
		return cbLaTeX.isSelected();		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		try {
			
			if (source == btOK || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();
				isLaTeX = cbLaTeX.isSelected();
				
				boolean finished = inputHandler.processInput(inputText);	
				if (isShowing()) {	
					// text dialog window is used and open
					setVisible(!finished);
					if(isTextMode)
						app.setMode(EuclidianConstants.MODE_TEXT);
				} else {		
					// text input field embedded in properties window
					
					// removed - causes text to be erased on an error
					// (and not needed)
					//text.setLaTeX(isLaTeX, true);
					//setGeoText(text);

				}
			} 
			
			else if (source == btCancel) {
				if (isShowing())
					setVisible(false);		
				else {
					setGeoText(text);
				}
				
				if(isTextMode)
					app.setMode(EuclidianConstants.MODE_TEXT);
			}
			
			else if (source == cbLaTeX) {
				
				btInsertLaTeX.setEnabled(cbLaTeX.isSelected());
				isLaTeX = cbLaTeX.isSelected();
				textPreviewer.updatePreviewText(text, inputPanel.getText(), isLaTeX);
				
				if (isLaTeX) {
					((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit("latex");
					inputPanel.getTextComponent().getDocument().addDocumentListener(this);
					if (latexPreviewer == null) 
						latexPreviewer = new LaTeXPreviewerPanel();
					//inputPanel.getTextComponent().add(latexPreviewer);
					
					latexPreviewer.setLaTeX(app, inputPanel.getText());
				} else {
					((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit("geogebra");
					inputPanel.getTextComponent().getDocument().addDocumentListener(this);
					//latexPreviewer = null;	
				}
				if(isLaTeX && inputPanel.getText().length() == 0) {
					insertString("$  $");
					setRelativeCaretPosition(-2);
				}
				
				if (latexPreviewer != null) {
					latexPreviewer.setVisible(isLaTeX);
				}
			}	
			
			
			
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
		}			
	}
	
	/**
     * Nothing !
     * @param e the event
     */
    public void changedUpdate(DocumentEvent e) { }

    /**
     * Called when an insertion is made in the textarea
     * @param e the event
     */
    public void insertUpdate(DocumentEvent e) {
        handleDocumentEvent(e);
    }

    /**
     * Called when a remove is made in the textarea
     * @param e the event
     */
    public void removeUpdate(DocumentEvent e) {
        handleDocumentEvent(e);
    }

    protected void handleDocumentEvent(DocumentEvent e) {
    	Document doc = e.getDocument();
    	try {
    		//inputHandler.processInput(inputPanel.getText());
    		//latexPreviewer.setLaTeX(app, doc.getText(0, doc.getLength()));
    		textPreviewer.updatePreviewText(text, doc.getText(0, doc.getLength()), isLaTeX);
    	} catch (BadLocationException ex) { }
    }
    
	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * @param geo
	 */
	public void insertGeoElement(GeoElement geo) {
		if (geo == null) return;		
		inputPanel.getTextComponent().getDocument().removeDocumentListener(this);
		JTextComponent textComp = inputPanel.getTextComponent();	
		textComp.replaceSelection(""); // insert empty string to get rid of a possible selection
		String text = inputPanel.getText();
		int caretPos = textComp.getCaretPosition();		
		String leftText = text.substring(0, caretPos).trim();				
		String rightText = text.substring(caretPos).trim();		
		
		StringBuilder insertedText = new StringBuilder();
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
		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
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
       
        private TextInputHandler() { 
        	kernel = app.getKernel();        	
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
                    
                    // make sure for new LaTeX texts we get nice "x"s
                    if (isLaTeX) t.setSerifFont(true);
                    
                    if (startPoint.isLabelSet()) {
                    	  try { t.setStartPoint(startPoint); }catch(Exception e){};                          
                    } else {
                    	
//                    	// Michael Borcherds 2008-04-27 changed to RealWorld not absolute
                    	// startpoint contains mouse coords
                    	//t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX), 
                    	//		euclidianView.toScreenCoordY(startPoint.inhomY));
                    	//t.setAbsoluteScreenLocActive(true); 
                    	t.setRealWorldLoc(startPoint.inhomX, startPoint.inhomY);
                    	t.setAbsoluteScreenLocActive(false); 
                    }
                    t.updateRepaint();
                    app.storeUndoInfo();                    
                    return true;                
                }
                return false;
            }
                    
            // change existing text
            try {           
                GeoText newText = (GeoText) kernel.getAlgebraProcessor().changeGeoElement(text, inputValue, true);                         
                
                // make sure newText is using correct LaTeX setting
                newText.setLaTeX(isLaTeX, true);
                newText.updateRepaint();
                
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

	
	public void setVisible(boolean isVisible) {	
		if(!isVisible ){
			textPreviewer.removePreviewGeoText();
		}
		super.setVisible(isVisible);
	}
	
	
	
	
	
}


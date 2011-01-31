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
import geogebra.gui.util.TableSymbols;
import geogebra.gui.util.TableSymbolsLaTeX;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class TextInputDialog extends InputDialog implements DocumentListener, CaretListener, KeyListener {

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


	private SelectionTable recentSymbolTable;
	private ArrayList<String> recentSymbolList;

	private JPanel toolPanel;



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
		createGUI(title, "", false, cols, rows, false, false, false, false, false, false, true);		
		setGeoText(text);  // init dialog using text
		this.showSymbolTablePopup(false);
		this.setResizable(true);
		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
		inputPanel.getTextComponent().addCaretListener(this);
		inputPanel.getTextComponent().addKeyListener(this);

		// add key listener to the editor
		inputPanel.getTextComponent().addKeyListener(new MyKeyListener());

		// build toolbar

		toolPanel = new JPanel(new BorderLayout());

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

		toolPanel.add(toolBar, BorderLayout.NORTH);
		toolPanel.add(createRecentSymbolTable(),BorderLayout.SOUTH);

		// create edit panel to contain both the input panel and toolbar
		editHeader = new JLabel();
		editHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
		
		editPanel = new JPanel(new BorderLayout(2,2));
		
		editPanel.add(editHeader, BorderLayout.NORTH);
		
		
		// testing dynamic input panel
		/*
		DynamicTextInputPanel d = new DynamicTextInputPanel(app);
		editPanel.add(d, BorderLayout.NORTH);
		*/
		
		editPanel.add(inputPanel, BorderLayout.CENTER);
		editPanel.add(toolPanel, BorderLayout.SOUTH);		
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
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPanel, previewPanel );
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
		menu.add(new LatexTable(app, this, btInsertUnicode, TableSymbols.basicSymbols, -1, 11,SelectionTable.MODE_TEXT));
		btInsertUnicode.addPopupMenuItem(menu);
		//btInsertUnicode.addPopupMenuItem(createMenuItem(SymbolTable.math_ops,0,1,2));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.operators,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.greekUpperCaseFull,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.analysis,-1,8));

		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.sets,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.logical,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.sub_superscripts,-1,10));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.basic_arrows,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.otherArrows,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.geometricShapes,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.games_music,-1,7));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.currency,-1,8));
		btInsertUnicode.addPopupMenuItem(createMenuItem(TableSymbols.handPointers,-1,6));

	}

	/**
	 * creates a sub-menu for the unicode insert button
	 */
	private JMenu createMenuItem(String[] table, int rows, int columns ){
		JMenu menu = new JMenu(table[0] + " " + table[1] + " " + table[2] + "  ");
		menu.add(new LatexTable(app, this, btInsertUnicode, table, rows, columns, SelectionTable.MODE_TEXT));
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
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.roots_fractions, 1, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("SumsAndIntegrals"));	
		LatexTable table = new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.sums, 1, -1,SelectionTable.MODE_LATEX);
		//table.setCaretPosition(-3);
		menu.add(table);
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Accents"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.accents, 2, -1, SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("AccentsExt"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.accentsExtended, 2, -1, SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Brackets"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.brackets, 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("Matrices"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.matrices, 1, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("FrakturLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathfrak(), 4, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("CalligraphicLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathcal(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("BlackboardLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathbb(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		menu = new JMenu(app.getMenu("CursiveLetters"));
		menu.add(new LatexTable(app, this, btInsertLaTeX, TableSymbolsLaTeX.mathscr(), 2, -1,SelectionTable.MODE_LATEX));
		btInsertLaTeX.addPopupMenuItem(menu);

		JMenuItem menuItem = new JMenuItem(app.getMenu("Space"));
		menuItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				insertString(" \\; ");
			}

		});
		btInsertLaTeX.addPopupMenuItem(menuItem);

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


	public JToolBar createRecentSymbolTable(){


		recentSymbolList = app.getGuiManager().getRecentSymbolList();

		recentSymbolTable = new SelectionTable(app, recentSymbolList.toArray(), 1, recentSymbolList.size(), 
				new Dimension(24,24), SelectionTable.MODE_TEXT);

		recentSymbolTable.setHorizontalAlignment(SwingConstants.CENTER);
		recentSymbolTable.setSelectedIndex(0);
		//	this.setUseColorSwatchBorder(true);
		recentSymbolTable.setShowGrid(true);
		recentSymbolTable.setGridColor(MyTable.TABLE_GRID_COLOR);
		recentSymbolTable.setBorder(BorderFactory.createLoweredBevelBorder());  
		recentSymbolTable.setShowSelection(false);

		recentSymbolTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				insertString(recentSymbolList.get(recentSymbolTable.getSelectedIndex()));
			}
		});



		JToolBar p = new JToolBar();
		p.setFloatable(false);
		//p.add(new JLabel("Recent: "));
		p.add(recentSymbolTable);
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		return p;




		/*


		JToolBar tb = new JToolBar();
		tb.setFloatable(false);


		symbolButton = new JButton[symbolButtonCount];
		for(int i=0; i < symbolButton.length; i++){
			symbolButton[i] = new JButton();
			symbolButton[i].setFocusable(false);
			symbolButton[i].addActionListener(this);
			symbolButton[i].setIcon(
					GeoGebraIcon.createStringIcon(symbolList.get(i), app.getPlainFont(), new Dimension(18,18)));
			tb.add(symbolButton[i]);
		}

		 */

	}

	public void addRecentSymbol(String newSymbol){

		this.recentSymbolList.add(0,newSymbol);
		this.recentSymbolList.remove(recentSymbolList.size()-1);
		updateRecentSymbolTable();

	}


	public void updateRecentSymbolTable(){
		recentSymbolTable.populateModel(recentSymbolList.toArray());
	}



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
	public JPanel getToolBar() {
		return toolPanel;
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
				//inputText = inputPanel.getText();
				isLaTeX = cbLaTeX.isSelected();

				JTextComponent textComp = inputPanel.getTextComponent();
					
				String html = textComp.getText();
				
				dth.parseHTMLString(html);
				
				boolean finished = inputHandler.processInput(dth.toGeoGebraString());	
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
					//((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit("latex");
					inputPanel.getTextComponent().getDocument().addDocumentListener(this);
					if (latexPreviewer == null) 
						latexPreviewer = new LaTeXPreviewerPanel();
					//inputPanel.getTextComponent().add(latexPreviewer);

					latexPreviewer.setLaTeX(app, inputPanel.getText());
				} else {
					//((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit("geogebra");
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
		//try {
			//inputHandler.processInput(inputPanel.getText());
			//latexPreviewer.setLaTeX(app, doc.getText(0, doc.getLength()));
			
			JTextComponent textComp = inputPanel.getTextComponent();
			String html = textComp.getText();
				
			dth.parseHTMLString(html);

			
			//textPreviewer.updatePreviewText(text, doc.getText(0, doc.getLength()), isLaTeX);
			textPreviewer.updatePreviewText(text, dth.toGeoGebraString(), isLaTeX);
		//} catch (BadLocationException ex) { }
	}
	
	public void insertGeoElement(GeoElement geo) {
		if (geo == null) return;
		inputPanel.getTextComponent().getDocument().removeDocumentListener(this);
		inputPanel.getTextComponent().removeCaretListener(this);
		
		JTextComponent textComp = inputPanel.getTextComponent();
		int caretPos = textComp.getCaretPosition();	
		
		
		JTextPane tp = (JTextPane)inputPanel.getTextComponent();
		HTMLEditorKit kit = (HTMLEditorKit)tp.getEditorKit();
		try {
			
			String html = textComp.getText();
			textComp.setText("");
			
			dth.parseHTMLString(html);
			
			dth.insertGeoElement(geo.getLabel(), caretPos);
					
			kit.read(((Reader)(new StringReader(dth.toHTMLString()))), tp.getDocument(), 0);
			

			
			/*
			textComp.setText("");
			
			text = text.substring(0,caretPos) + " <font color=\"red\">&nbsp;"+geo.getLabel()+"&nbsp;</font> " + text.substring(caretPos);

			text = text.replaceAll("<head>", "");
			text = text.replaceAll("</head>", "");
			text = text.replaceAll("<html>", "");
			text = text.replaceAll("</html>", "");
			text = text.replaceAll("<body>", "");
			text = text.replaceAll("</body>", "");*/
			
		
			//kit.insertHTML((HTMLDocument)tp.getDocument(),0,text,0,0, null);
			//kit.insertHTML((HTMLDocument)tp.getDocument(),caretPos," <font color=\"red\">"+geo.getLabel()+"</font> ",0,0, HTML.Tag.P);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Application.debug(textComp.getText());
		//Application.debug(kit.get);

		
		
		inputPanel.getTextComponent().addCaretListener(this);
		inputPanel.getTextComponent().getDocument().addDocumentListener(this);
	}

	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * @param geo
	 */
	public void insertGeoElementx(GeoElement geo) {
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
			if(textPreviewer != null){
				textPreviewer.removePreviewGeoText();
				//textPreviewer.detachView();
			}
		}
		super.setVisible(isVisible);
	}

	private class MyKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if((e.isControlDown()||Application.isControlDown(e)) && e.getKeyCode() == KeyEvent.VK_SPACE){
				if(isLaTeX)
					inputPanel.insertString("\\:");
			}
		}
	}


	int lastCaretPos = -1;
	
	public void caretUpdate(CaretEvent e) {
		inputPanel.getTextComponent().removeCaretListener(this);
		
		String html = inputPanel.getTextComponent().getText();
		//int caretPos = inputPanel.getTextComponent().getCaretPosition();

		dth.parseHTMLString(html);
		int caretPos = e.getDot();
		
		caretPos = dth.moveCaret(caretPos, lastCaretPos);
		
		try {
			inputPanel.getTextComponent().setCaretPosition(caretPos);
		} catch (Exception ee) {ee.printStackTrace();}
		
		
		JTextPane tp = (JTextPane)inputPanel.getTextComponent();
		HTMLEditorKit kit = (HTMLEditorKit)tp.getEditorKit();
		
		StringWriter sw = new StringWriter();
		
		try {
			kit.write(sw, inputPanel.getTextComponent().getDocument(), 0, 100);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Application.debug(sw.getBuffer().toString());
		
		lastCaretPos = e.getDot();
		inputPanel.getTextComponent().addCaretListener(this);

		
	}
	
	private static void parseHTMLx(String str, final StringBuilder sbHTML, final StringBuilder dataSB) {

		sbHTML.setLength(0);
		dataSB.setLength(0);
		
		HTMLEditorKit.ParserCallback callback = 
			new HTMLEditorKit.ParserCallback () {
			boolean dynamic = false;
			boolean changeNextChar = false;
			public void handleText(char[] data, int pos) {

				sbHTML.append(data);
				
				char c = dynamic ? ' ' : 'x';
				for (int i = 0 ; i < data.length ; i++) {
					dataSB.append(changeNextChar ? ' ' : c);
					changeNextChar = false;
				}
				

				//System.err.println(data);
			}
			public void handleStartTag(HTML.Tag tag, 
					MutableAttributeSet attrSet, int pos) {
				if (tag == HTML.Tag.FONT) {
					dynamic = true;
					//Application.debug("XXX DIV");
				}  
					

			}

			public void handleEndTag(HTML.Tag tag, int pos) {
				if (tag == HTML.Tag.FONT) {
					dynamic = false;
					changeNextChar = true;
					//Application.debug("YYY DIV");
				} else if (tag == HTML.Tag.P) {
					sbHTML.append('\n');
					dataSB.append(' ');
					
				}
					

			}

		};
		Reader reader = new StringReader(str);
		try {
			new ParserDelegator().parse(reader, callback, true);
		} catch (IOException e) {
			
			e.printStackTrace();
			sbHTML.setLength(0);
			dataSB.setLength(0);
			return;
		}
		
		//Application.debug(sbHTML);
		//Application.debug(dataSB);

	}



	public void keyTyped(KeyEvent e) {
		//Application.debug(e.getKeyCode()==KeyEvent.VK_BACK_SPACE);
		
	}


	DynamicTextHolder dth = new DynamicTextHolder();

	public void keyPressed(KeyEvent e) {

		//Application.debug(e.getKeyCode()==KeyEvent.VK_BACK_SPACE);
		
		if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE && e.getKeyCode() != KeyEvent.VK_DELETE) return;
		
		String html = inputPanel.getTextComponent().getText();
		int caretPos = inputPanel.getTextComponent().getCaretPosition();

		inputPanel.getTextComponent().setText("");

		
		

		dth.parseHTMLString(html);
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_BACK_SPACE:
			caretPos = dth.backSpacePressed(caretPos);
			e.consume();
			break;
		case KeyEvent.VK_DELETE:
			caretPos = dth.deletePressed(e, caretPos);
			e.consume();
			break;
		}
		
		// only try to change caret position if necessary
		if (e.isConsumed()) {
		
			JTextPane tp = (JTextPane)inputPanel.getTextComponent();
			HTMLEditorKit kit = (HTMLEditorKit)tp.getEditorKit();
	
			try {
				kit.read(((Reader)(new StringReader(dth.toHTMLString()))), tp.getDocument(), 0);
				inputPanel.getTextComponent().removeCaretListener(this);
				try {
				inputPanel.getTextComponent().setCaretPosition(caretPos);
				} catch (Exception ee) { ee.printStackTrace(); }
				inputPanel.getTextComponent().addCaretListener(this);
	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		}
		


	}



	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}



}


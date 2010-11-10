/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.inputbar;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.LowerCaseDictionary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInput extends  JPanel implements ActionListener, KeyListener, MouseListener, FocusListener {
	private static final long serialVersionUID = 1L;
	
	
	private boolean showCommandButton = false;	  //<=========== set this flag to test the command button
	
	
	private Application app;

	private JLabel inputLabel, helpIcon;
 	
	private MyComboBox cmdCB; // for command list
	
	private CommandPopupMenuButton cmdButton; // alternate button for command list
	
	// autocompletion text field
	private AutoCompleteTextField inputField;

	

	/**
	 * creates new AlgebraInput
	 */
	public AlgebraInput(Application app) {		
		this.app = app;		
		 		
		app.removeTraversableKeys(this);
		
		//initGUI();
	}
	
	public void initGUI() {
		removeAll();
		helpIcon = new JLabel(app.getImageIcon("help.png")); 
		helpIcon.addMouseListener(this);
		inputLabel = new JLabel(); 
		inputLabel.addMouseListener(this);

		InputPanel inputPanel = new InputPanel(null, app, 30, true);
		inputField = (AutoCompleteTextField) inputPanel.getTextComponent();		
		
		// set up input field		
		inputField.setEditable(true);						
		inputField.addKeyListener(this);
		
		// set up command combo box
		cmdCB = new MyComboBox();
		if (app.showCmdList()) {
			cmdCB.setMaximumSize(new Dimension(200, 200));
			// set to approx half screen height
			//cmdCB.setMaximumRowCount(app.getScreenSize().height/app.getFontSize()/3);
			cmdCB.addActionListener(this);
		}
		
		
		// set up command popup button
		cmdButton = new CommandPopupMenuButton(app);
		cmdButton.setDownwardPopup(false);
		cmdButton.addActionListener(this);	
		
		updateFonts();
				
		// add to panel				 		
		setLayout(new BorderLayout(2, 2));	
		JPanel iconLabelPanel = new JPanel();
		iconLabelPanel.add(helpIcon);
		
		if (app.showCmdList() && showCommandButton ) {
			JPanel cbPanel = new JPanel(new BorderLayout());
			cbPanel.add(cmdButton, BorderLayout.CENTER);
			iconLabelPanel.add(cbPanel);
		}
		
		iconLabelPanel.add(inputLabel);
		add(iconLabelPanel, BorderLayout.WEST);   
		add(inputPanel, BorderLayout.CENTER);
		if (app.showCmdList()) {
			JPanel p = new JPanel(new BorderLayout(5,5));
			p.add(cmdCB, BorderLayout.CENTER);		
			add(p, BorderLayout.EAST);
		}
		
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow),  
			BorderFactory.createEmptyBorder(2,2,2,2) )
		);
			
		setLabels();
		inputField.addFocusListener(this);
	}
	
	public boolean requestFocusInWindow() { 
		return inputField.requestFocusInWindow();
	}
	
	public void requestFocus() {
		requestFocusInWindow();
	}
	
	public boolean hasFocus() {
		return inputField.hasFocus();
	}
	
	public void clear() {
		inputField.setText(null);
	}		
	
	public AutoCompleteTextField getTextField() {
		return inputField;
	}
	
	/**
	 * updates labels according to current locale
	 */
	public void setLabels() {
		if (inputLabel != null)
			inputLabel.setText( app.getPlain("InputLabel") + ":");
		//inputButton.setToolTipText(app.getMenu("Mode") + " " + app.getMenu("InputField"));   
		if (helpIcon != null)
			helpIcon.setToolTipText(app.getMenu("FastHelp"));		
		setCommandNames();				
	}	
	
	public void updateFonts() {
		inputField.setFont(app.getBoldFont());		
		inputLabel.setFont(app.getPlainFont());
		if (app.showCmdList()) {	
			cmdCB.setFont(app.getPlainFont());
			// set to approx half screen height
			cmdCB.setMaximumRowCount(app.getScreenSize().height/app.getFontSize()/3);
		}
	}    
	
//	/**
//	 * Inserts string at current position of the input textfield and gives focus
//	 * to the input textfield.
//	 * @param str: inserted string
//	 */
//	public void insertString(String str) {
//		inputField.replaceSelection(str);
//	}
	
	/**
	 * Sets the content of the input textfield and gives focus
	 * to the input textfield.
	 */
	public void replaceString(String str) {
		inputField.setText(str);
	}

	
	// see actionPerformed
	private void insertCommand(String cmd) {
		if (cmd == null) return;
		
		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = 
			oldText.substring(0, pos) + 
			cmd + "[]" +
			oldText.substring(pos);			 			
		
		inputField.setText(newText);
		inputField.setCaretPosition(pos + cmd.length() + 1);		
		inputField.requestFocus();
	}
	

	
	
	/**
	 * fill command list with command names of current locale
	 */	
	public void setCommandNames() {	
		app.initTranslatedCommands();
		LowerCaseDictionary dict = app.getCommandDictionary();
		if (dict == null || cmdCB == null) return;
		
		// localize the  cmdButton 
		cmdButton.setCommands();
		
		ActionListener [] listeners = cmdCB.getActionListeners();
		for (int i=0; i < listeners.length; i++) 
			cmdCB.removeActionListener(listeners[i]);
		
		if (cmdCB.getItemCount() > 0) cmdCB.removeAllItems();		
		String commandString = app.getCommand("Command") + " ...";
		cmdCB.addItem(commandString);		
		
		Iterator<?> it = dict.getLowerCaseIterator();
		while (it.hasNext()) {
			// combobox
			String cmdName = (String) dict.get(it.next());
			if (cmdName != null && cmdName.length() > 0)
				cmdCB.addItem(cmdName);
		}	 
		
		// set width of combo box to fit "Command ..."
		cmdCB.setPrototypeDisplayValue(commandString + "Wa");
				
		for (int i=0; i < listeners.length; i++) 
			cmdCB.addActionListener(listeners[i]);
	}
	

	
   
	/**
	* action listener implementation for command combobox
	*/
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
	
		// command combobox
		if (source == cmdCB) { 			
			if (cmdCB.getSelectedIndex() != 0) { // not title
				String cmd = (String) cmdCB.getSelectedItem();
				
				// copy command into input bar
				insertCommand(cmd);	
				
				// show command's syntax
				StringBuilder sb = new StringBuilder();
				cmd = app.translateCommand(cmd); // internal name
				CommandProcessor.getCommandSyntax(sb, app, cmd, -1);
				app.showError(new MyError(app, sb.toString(), cmd));
				
				//cmdCB.setSelectedIndex(0);
			}					
		}			
		
		if (source == cmdButton) { 		
			String cmd = cmdButton.getSelectedCommand();
			insertCommand(cmd);	
		}
	}

	public void keyPressed(KeyEvent e) {
		// the input field may have consumed this event
		// for auto completion
		if (e.isConsumed()) return;

		int keyCode = e.getKeyCode();    
		if (keyCode == KeyEvent.VK_ENTER) {	
			  String input = inputField.getText();					   
			  if (input == null || input.length() == 0)
			  {
				  app.getEuclidianView().requestFocus(); // Michael Borcherds 2008-05-12
				  return;
			  }
			  
			  app.getGuiManager().setScrollToShow(true);
			  
			  GeoElement[] geos = app.getKernel().getAlgebraProcessor().processAlgebraCommand( input, true );
			  
			  boolean success = null != geos;
			  
			  // create texts in the middle of the visible view
			  if (success && geos[0].isGeoText()) {
				  GeoText text = (GeoText)geos[0];
				  if (!text.isTextCommand() && text.getStartPoint() == null) {
					  
					  Construction cons = text.getConstruction();
					  EuclidianView ev = app.getEuclidianView();
					  
					  boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
					  cons.setSuppressLabelCreation(true);
					  GeoPoint p = new GeoPoint(text.getConstruction(), null, ( ev.getXmin() + ev.getXmax() ) / 2, ( ev.getYmin() + ev.getYmax() ) / 2, 1.0);
					  cons.setSuppressLabelCreation(oldSuppressLabelsStatus);
					  
					  try {
						text.setStartPoint(p);
						text.update();
					} catch (CircularDefinitionException e1) {
						e1.printStackTrace();
					}
				  }
			  }
				 				
			  
			  app.getGuiManager().setScrollToShow(false);

			  if (success) {						   
				 inputField.addToHistory(input);
				inputField.setText(null);  							  			   
			  }			  
		} else app.getGlobalKeyDispatcher().handleGeneralKeys(e); // handle eg ctrl-tab
	}

	public void keyReleased(KeyEvent e) {

	}


	public void keyTyped(KeyEvent e) {	
	}
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent e) {	
		Object src = e.getSource();
				
		// click on help icon: open input bar help dialog
		if (src == helpIcon || src == inputLabel) {
			app.showHelp(app.getPlain("InputFieldHelp"));
		}
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void focusGained(FocusEvent arg0) {
		app.clearSelectedGeos();
	}

	public void focusLost(FocusEvent arg0) {
	
	}	 

}
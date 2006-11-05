/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.algebra;

import geogebra.Application;
import geogebra.MySmallJButton;
import geogebra.algebra.autocomplete.AutoCompleteTextField;
import geogebra.util.InputPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInput extends  JPanel 
implements ActionListener, MouseListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private Application app;

	private MySmallJButton helpButton;
	private JToggleButton inputButton;	
 	
	private JComboBox cmdCB; // for command list
	private Locale cmdLocale;
	
	// autocompletion text field
	private AutoCompleteTextField inputField;	

	/**
	 * creates new AlgebraInput
	 */
	public AlgebraInput(Application app) {		
		this.app = app;		
		cmdCB = new JComboBox(); 		
		initGUI();
	}
	
	public void initGUI() {
		removeAll();
		helpButton = new MySmallJButton(app.getImageIcon("help.gif"), 5); 
		inputButton = new JToggleButton(); // label text
		InputPanel inputPanel = new InputPanel(null, app, 30);
		inputField = (AutoCompleteTextField) inputPanel.getTextComponent();		
		
		// set up input field		
		inputField.setEditable(true);					
		inputField.addMouseListener(this);			
		inputField.addKeyListener(this);
		
		// set up command combo box
		if (app.showCmdList()) {			
			cmdCB.setMaximumSize(new Dimension(200, 200));
			cmdCB.addActionListener(this);
		}
			
		helpButton.addActionListener(this);		
		inputButton.addMouseListener(this);
				
		// add to panel				 		
		setLayout(new BorderLayout(5, 5));	
		add(inputButton, BorderLayout.WEST);   
		add(inputPanel, BorderLayout.CENTER);
							 	
		JPanel p = new JPanel(new BorderLayout(5,5));				
		if (app.showCmdList()) {			
			p.add(cmdCB, BorderLayout.CENTER);					
		}
		p.add(helpButton, BorderLayout.EAST);
		add(p, BorderLayout.EAST);
		
		setBorder(BorderFactory.createCompoundBorder(
				   BorderFactory.createEtchedBorder(),  
				   BorderFactory.createEmptyBorder(2,2,2,2) )
			   );    	
		
		setLabels();
	}		
	
	public JToggleButton getInputButton() {
		return inputButton;
	}
	
	public void setFocus() {
		inputField.requestFocus();
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
	 * updates labesl according to current locale
	 */
	public void setLabels() {
		inputButton.setText( app.getPlain("InputLabel") + ":");
		//inputButton.setToolTipText(app.getMenu("Mode") + " " + app.getMenu("InputField"));   
		helpButton.setToolTipText(app.getMenu("FastHelp"));		
		setCommandNames();				
	}	
	
	public void updateFonts() {
		inputField.setFont(app.getBoldFont());		
		cmdCB.setFont(app.getPlainFont());
		inputButton.setFont(app.getPlainFont());
	}    
	
	/**
	 * Inserts string at current position of the input textfield and gives focus
	 * to the input textfield.
	 * @param str: inserted string
	 */
	public void insertString(String str) {
		inputField.replaceSelection(str);
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
	private void setCommandNames() {	
		// only do something if the locale has changed
		if (app.getLocale() == cmdLocale) return;
		cmdLocale = app.getLocale();
		
		cmdCB.removeActionListener(this);
		if (cmdCB.getItemCount() > 0) cmdCB.removeAllItems();
		
		cmdCB.addItem(app.getCommand("Command") + " ...");		
		String [] cmds = app.getCommandNames();
		for (int i=0; i < cmds.length; i++) {
			// combobox
			cmdCB.addItem(cmds[i]);
		}	 
		cmdCB.addActionListener(this);
	}
	

	
   
	/**
	* action listener implementation for command combobox
	*/
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
	
		// command combobox
		if (source == cmdCB) { 			
			if (cmdCB.getSelectedIndex() != 0) { // not title
				insertCommand((String) cmdCB.getSelectedItem());				
				cmdCB.setSelectedIndex(0);
			}					
		}
		// help button
		else if (source == helpButton) {
			// show help dialog
			app.showHelp("InputFieldHelp");
		}		
	}
	
      


	/**
	 * sets AlgebraInput mode when focus is gained
	 *
	public void focusGained(FocusEvent e) {
		app.setAlgebraInputMode();	
	}

	public void focusLost(FocusEvent e) {
	}*/


	/**
	 * sets AlgebraInput mode on double click
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == inputField) {		
			if (e.getClickCount() == 2) {
				app.setAglebraInputMode();
				inputField.requestFocus();
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}


	public void mouseExited(MouseEvent arg0) {
	}


	public void mousePressed(MouseEvent e) {
		if (e.getSource() == inputButton) {	
			if (!inputButton.isSelected()) {
				inputButton.setSelected(true);
				app.setAglebraInputMode();
				inputField.requestFocusInWindow();
			} else {
				inputButton.setSelected(false);
				app.setMoveMode();
			}
		} 		
	}

	public void mouseReleased(MouseEvent arg0) {
	}


	public void keyPressed(KeyEvent e) {
		// the input field may have consumed this event
		// for auto completion
		if (e.isConsumed()) return;

		int keyCode = e.getKeyCode();    
		if (keyCode == KeyEvent.VK_ENTER) {	
			  String input = inputField.getText();					   
			  if (input == null || input.length() == 0) return;
			  boolean success = null != 
				 app.getAlgebraController().processAlgebraCommand( input, true );
			  if (success) {						   
				 inputField.addToHistory(input);
				inputField.setText(null);  							  			   
			  }			  
		}
	}

	public void keyReleased(KeyEvent e) {

	}


	public void keyTyped(KeyEvent e) {	
	}	 

}
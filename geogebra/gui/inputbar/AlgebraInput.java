/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.inputbar;

import geogebra.Application;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.util.LowerCaseDictionary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInput extends  JPanel 
implements ActionListener, MouseListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private Application app;

	private JLabel helpButton;
	private JToggleButton inputButton;	
 	
	private JComboBox cmdCB; // for command list
	
	// autocompletion text field
	private AutoCompleteTextField inputField;	

	/**
	 * creates new AlgebraInput
	 */
	public AlgebraInput(Application app) {		
		this.app = app;		
		 		
		initGUI();
	}
	
	public void initGUI() {
		removeAll();
		helpButton = new JLabel(app.getImageIcon("help.png")); 
		inputButton = new JToggleButton(); // label text
		InputPanel inputPanel = new InputPanel(null, app, 30, true);
		inputField = (AutoCompleteTextField) inputPanel.getTextComponent();		
		
		// set up input field		
		inputField.setEditable(true);					
		inputField.addMouseListener(this);			
		inputField.addKeyListener(this);
		
		// set up command combo box
		cmdCB = new JComboBox();
		if (app.showCmdList()) {			
			cmdCB.setMaximumSize(new Dimension(200, 200));
			cmdCB.addActionListener(this);
		}
			
		helpButton.addMouseListener(this);		
		inputButton.addMouseListener(this);
				
		// add to panel				 		
		JPanel p = new JPanel(new BorderLayout(5,5));
		p.add(helpButton, BorderLayout.WEST);	
		p.add(inputButton, BorderLayout.CENTER);    
		
		setLayout(new BorderLayout(5, 5));	
		add(p, BorderLayout.WEST);   
		add(inputPanel, BorderLayout.CENTER);
		if (app.showCmdList()) {
			p = new JPanel(new BorderLayout(5,5));
			p.add(cmdCB, BorderLayout.CENTER);		
			add(p, BorderLayout.EAST);
		}
		
		setBorder(BorderFactory.createCompoundBorder(
				   BorderFactory.createEtchedBorder(),  
				   BorderFactory.createEmptyBorder(2,2,2,2) )
			   );    	
		
		setLabels();
	}		
	
	public JToggleButton getInputButton() {
		return inputButton;
	}
	
	public void requestFocus() {
		super.requestFocus();
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
	public void setCommandNames() {		
		LowerCaseDictionary dict = app.getCommandDictionary();
		if (dict == null) return;
		
		ActionListener [] listeners = cmdCB.getActionListeners();
		for (int i=0; i < listeners.length; i++) 
			cmdCB.removeActionListener(listeners[i]);
		
		if (cmdCB.getItemCount() > 0) cmdCB.removeAllItems();		
		cmdCB.addItem(app.getCommand("Command") + " ...");		
		
		
		Iterator it = dict.getLowerCaseIterator();
		while (it.hasNext()) {
			// combobox
			String cmdName = (String) dict.get(it.next());
			if (cmdName != null && cmdName.length() > 0)
				cmdCB.addItem(cmdName);
		}	 
				
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
				insertCommand((String) cmdCB.getSelectedItem());				
				cmdCB.setSelectedIndex(0);
			}					
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
				app.setAlgebraInputMode();
				inputField.requestFocus();
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}


	public void mouseExited(MouseEvent arg0) {
	}


	public void mousePressed(MouseEvent e) {
		Object src = e.getSource();
		if (src == inputButton) {	
			if (!inputButton.isSelected()) {
				inputButton.setSelected(true);
				app.setAlgebraInputMode();
				inputField.requestFocusInWindow();
			} else {
				inputButton.setSelected(false);
				app.setMoveMode();
			}
		} 	
		else if (src == helpButton) {
			// show help dialog
			app.showHelp("InputFieldHelp");
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
			  if (input == null || input.length() == 0)
			  {
				  app.getEuclidianView().requestFocus(); // Michael Borcherds 2008-05-12
				  return;
			  }
			  boolean success = null != 
				 app.getKernel().getAlgebraProcessor().processAlgebraCommand( input, true );
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
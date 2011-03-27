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
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInput extends  JPanel implements ActionListener, KeyListener, MouseListener, FocusListener {
	private static final long serialVersionUID = 1L;


	private Application app;

	// autocompletion text field
	private AutoCompleteTextField inputField;

	private JLabel inputLabel;
	private JToggleButton btnToggleInputPanel;
	private InputPanel inputPanel;


	/***********************************************************
	 * creates new AlgebraInput
	 */
	public AlgebraInput(Application app) {		
		this.app = app;		

		app.removeTraversableKeys(this);

		//initGUI();
	}


	public void initGUI() {
		removeAll();
		inputLabel = new JLabel(); 
		inputPanel = new InputPanel(null, app, 30, true);

		// create and set up the input field
		inputField = (AutoCompleteTextField) inputPanel.getTextComponent();			
		inputField.setEditable(true);						
		inputField.addKeyListener(this);
		inputField.addFocusListener(this);
		updateFonts();

		// show the history popup
		inputPanel.setShowHistoryButton(true);
		inputPanel.getSymbolButton().setDownwardPopup(false);

		// create toggle button to hide/show the input help panel
		btnToggleInputPanel = new JToggleButton();
		btnToggleInputPanel.setSelectedIcon(GeoGebraIcon.listRightIcon());
		btnToggleInputPanel.setIcon(GeoGebraIcon.listLeftIcon());
		btnToggleInputPanel.addActionListener(this);
		btnToggleInputPanel.setFocusable(false);
		btnToggleInputPanel.setContentAreaFilled(false);   
		btnToggleInputPanel.setBorderPainted(false);


		// create sub-panels				 		
		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0,10, 0, 2));
		labelPanel.add(inputLabel, BorderLayout.EAST);

		JPanel eastPanel = new JPanel(new BorderLayout());
		if (app.showCmdList()) {
			eastPanel.add(btnToggleInputPanel, BorderLayout.WEST);
			eastPanel.add(Box.createRigidArea(new Dimension(10,1)), BorderLayout.EAST);
		}

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.ipady = 0; 
		c.insets = new Insets(2,0,2,0);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(labelPanel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.ipady = 2; // adds extra height to the input field
		this.add(inputPanel,c);

		if (app.showCmdList()){
			c.ipady = 0; 
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			c.anchor = GridBagConstraints.LINE_END;
			this.add(eastPanel,c);
		}

		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow));
		setLabels();

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

		// update the help panel
		app.getInputHelpPanel().setLabels();
		app.getInputHelpPanel().setCommands();
	}	


	public void updateFonts() {
		inputField.setFont(app.getBoldFont());		
		inputLabel.setFont(app.getPlainFont());

		//update the help panel
		app.getInputHelpPanel().updateFonts();

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
	public void insertCommand(String cmd) {
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


	public void insertString(String str) {
		if (str == null) return;

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = 
			oldText.substring(0, pos) + str +
			oldText.substring(pos);			 			

		inputField.setText(newText);
		inputField.setCaretPosition(pos + str.length());		
		inputField.requestFocus();
	}




	/**
	 * action listener implementation for input help panel toggle button
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnToggleInputPanel) { 
			if(btnToggleInputPanel.isSelected()){
				app.getInputHelpPanel().updateFonts();
				app.setShowInputHelpPanel(true);
			}else{
				app.setShowInputHelpPanel(false);
			}
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
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (success && geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
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
				inputPanel.updateHistoryPopup(input);
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

	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void focusGained(FocusEvent arg0) {
		app.clearSelectedGeos();
	}

	public void focusLost(FocusEvent arg0) {

	}	 

}
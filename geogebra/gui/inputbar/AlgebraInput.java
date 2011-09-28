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
import geogebra.gui.SetLabels;
import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.gui.view.algebra.AlgebraInputDropTargetListener;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInput extends  JPanel implements ActionListener, KeyListener, FocusListener, SetLabels {
	private static final long serialVersionUID = 1L;


	private Application app;

	// autocompletion text field
	private AutoCompleteTextField inputField;

	private JLabel inputLabel;
	private JToggleButton btnHelpToggle;
	private InputPanel inputPanel;


	/***********************************************************
	 * creates new AlgebraInput
	 * @param app 
	 */
	public AlgebraInput(Application app) {		
		this.app = app;		

		app.removeTraversableKeys(this);

		initGUI();
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
		
		// enable a history popup and embedded button 
		inputField.addHistoryPopup(app.showInputTop());
		
		// enable drops
		inputField.setDragEnabled(true);
		inputField.setDropTarget(new DropTarget(this,
				new AlgebraInputDropTargetListener(app, inputField)));
		
		updateFonts();


		// create toggle button to hide/show the input help panel
		btnHelpToggle = new JToggleButton();
		
		//btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_16x16.png"));
		//btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_16x16.png"));
		
		btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_18x18.png"));
		btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_18x18.png"));
		
		//btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_20x20.png"));
		//btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_20x20.png"));
		
		btnHelpToggle.addActionListener(this);
		btnHelpToggle.setFocusable(false);
		btnHelpToggle.setContentAreaFilled(false);   
		btnHelpToggle.setBorderPainted(false);


		// create sub-panels				 		
		JPanel labelPanel = new JPanel(new BorderLayout());
		
		labelPanel.add(inputLabel, BorderLayout.EAST);

		JPanel eastPanel = new JPanel(new BorderLayout());
		if (app.showInputHelpToggle()) {
			eastPanel.add(btnHelpToggle, BorderLayout.WEST);
		}
		
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0,10, 0, 2));
		eastPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(2,0,2,0));
		
		setLayout(new BorderLayout(0,0));
		add(labelPanel, BorderLayout.WEST);
		add(inputPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
		
		

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

		if(btnHelpToggle!=null)
			btnHelpToggle.setToolTipText(app.getMenu("InputHelp"));
	
		inputField.setDictionary(app.getCommandDictionary());
	}	


	public void updateFonts() {
		inputField.setFont(app.getBoldFont());		
		inputLabel.setFont(app.getPlainFont());

		//update the help panel
		if (app.getGuiManager().hasInputHelpPanel())
		{
			InputBarHelpPanel helpPanel = (InputBarHelpPanel) app.getGuiManager().getInputHelpPanel();
			helpPanel.updateFonts();
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
	 * @param str 
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

		if (source == btnHelpToggle) { 
			if(btnHelpToggle.isSelected()){
				InputBarHelpPanel helpPanel = (InputBarHelpPanel) app.getGuiManager().getInputHelpPanel();
				helpPanel.setLabels();
				helpPanel.setCommands();
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
			app.getKernel().clearJustCreatedGeosInViews();
			String input = inputField.getText();					   
			if (input == null || input.length() == 0)
			{
				app.getEuclidianView().requestFocus(); // Michael Borcherds 2008-05-12
				return;
			}

			app.setScrollToShow(true);
			GeoElement[] geos;
			try {
				geos = app.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling( input, true, false, true );
			} catch (Exception ee) {
				inputField.showError(ee);
				return;
			}
		 catch (MyError ee) {
			inputField.showError(ee);
			return;
		}
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


			app.setScrollToShow(false);

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

	public void focusGained(FocusEvent arg0) {
		app.clearSelectedGeos();
	}

	public void focusLost(FocusEvent arg0) {

	}	 

}
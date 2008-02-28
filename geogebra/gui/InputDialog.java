/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.GeoGebraApplicationBase;
import geogebra.GeoElementSelectionListener;
import geogebra.algebra.autocomplete.AutoCompleteTextField;
import geogebra.kernel.GeoElement;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

public class InputDialog extends JDialog implements ActionListener,
								WindowFocusListener {
	
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;
	
	protected String inputText = null;
	protected InputPanel inputPanel;	
	protected JButton btApply, btCancel;
	private JPanel optionPane, btPanel;
	protected GeoElementSelectionListener sl;
	protected JLabel msgLabel; 
		
	protected String initString;
	protected GeoGebraApplicationBase app;
	protected InputHandler inputHandler;
	
	/**
	 * Creates a non-modal standard input dialog.
	 */
	public InputDialog(GeoGebraApplicationBase app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler) {
		this(app, message,title, initString, autoComplete, handler, false, false);
	}
	
	public InputDialog(GeoGebraApplicationBase app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler, boolean modal, boolean selectInitText) {
		this(app.getFrame(), modal);
		this.app = app;		
		inputHandler = handler;
		this.initString = initString;			

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, false, true, selectInitText);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
		
		if (initString != null && selectInitText)
			inputPanel.selectText();
	}	
	
	protected InputDialog(JFrame frame, boolean modal) {
		super(frame, modal);
	}
	
	public JPanel getButtonPanel() {
		return btPanel;
	}
	
	protected void createGUI(String title, String message, boolean autoComplete, int columns, int rows,
			boolean specialChars, boolean greekLetters, boolean selectInitText) {
		setTitle(title);
		setResizable(false);		

		//Create components to be displayed
		inputPanel = new InputPanel(initString, app, rows, columns, specialChars, greekLetters);	
				
		sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo, boolean addToSelection) {
				insertGeoElement(geo);
			}
		};
		
		// add listeners to textfield
		JTextComponent textComp = inputPanel.getTextComponent();	
		if (textComp instanceof AutoCompleteTextField) {
			AutoCompleteTextField tf = (AutoCompleteTextField) textComp;	
			tf.setAutoComplete(autoComplete);
			tf.addActionListener(this);	
		}			
		
		// buttons
		btApply = new JButton(app.getPlain("Apply"));
		btApply.setActionCommand("Apply");
		btApply.addActionListener(this);
		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btApply);
		btPanel.add(btCancel);
	
		//Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5,5));
		msgLabel = new JLabel(message);
		optionPane.add(msgLabel, BorderLayout.NORTH);	
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);				
	}
	
	public void showSpecialCharacters(boolean flag) {
		inputPanel.showSpecialChars(flag);
	}
	
	public void insertGeoElement(GeoElement geo) {
		if (geo != null)
			inputPanel.insertString(geo.getLabel());
	}
	
	public void insertString(String str) {
		if (str != null)
			inputPanel.insertString(str);
	}
	
	protected void centerOnScreen() {
		pack();
		// center on screen
		setLocationRelativeTo(app.getMainComponent());
	}
	
	public String getInputString() {
		return inputText;
	}	
	
	public void setCaretPosition(int pos) { 	
		JTextComponent tc = inputPanel.getTextComponent();
		tc.setCaretPosition(pos);
		tc.requestFocusInWindow();
	}
	
	public void setRelativeCaretPosition(int pos) { 	
		JTextComponent tc = inputPanel.getTextComponent();
		try {tc.setCaretPosition(tc.getCaretPosition() + pos);}
		catch (Exception e) {}
		tc.requestFocusInWindow();
	}
	
	public void selectText() { 		
		inputPanel.selectText(); 
	}
	
	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		boolean finished = false;
		try {
			if (source == btApply || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();				
				finished = inputHandler.processInput(inputText);
			} else if (source == btCancel) {
				finished = true;
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
		}
		setVisible(!finished);
	}
	
	public String getText() {
		return inputPanel.getText();
	}
	
	public void setText(String text) {
		inputPanel.setText(text);
	}
	
	public void setVisible(boolean flag) {	
		if (!isModal()) {
			if (flag) { // set old mode again			
				addWindowFocusListener(this);			
			} else {		
				removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}
		super.setVisible(flag);
	}
	
	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setSelectionListenerMode(sl);
		}
	}

	public void windowLostFocus(WindowEvent arg0) {
	}

}
/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.util;

import geogebra.Application;
import geogebra.GeoElementSelectionListener;
import geogebra.algebra.autocomplete.AutoCompleteTextField;
import geogebra.kernel.GeoElement;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
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
	private JPanel optionPane;
	protected GeoElementSelectionListener sl;
		
	protected String initString;
	protected Application app;
	protected InputHandler inputHandler;
	
	/**
	 * Creates a non-modal standard input dialog.
	 */
	public InputDialog(Application app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler) {
		this(app, message,title, initString, autoComplete, handler, false);
	}
	
	public InputDialog(Application app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler, boolean modal) {
		this(app.getFrame(), modal);
		this.app = app;		
		inputHandler = handler;
		this.initString = initString;			

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
	}	
	
	protected InputDialog(JFrame frame, boolean modal) {
		super(frame, modal);
	}
	
	protected void createGUI(String title, String message, boolean autoComplete, int columns, int rows) {
		setTitle(title);
		setResizable(false);		

		//Create components to be displayed
		inputPanel = new InputPanel(initString, app, rows, columns, true);		
		
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
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btApply);
		btPanel.add(btCancel);
	
		//Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5,5));
		optionPane.add(new JLabel(message), BorderLayout.NORTH);	
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);				
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
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension d = getSize();
		setLocation((dim.width - d.width) / 2, (dim.height - d.height) / 2);
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
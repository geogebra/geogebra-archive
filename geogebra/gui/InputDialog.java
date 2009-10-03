/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class InputDialog extends JDialog implements ActionListener,
								WindowFocusListener, VirtualKeyboardListener {
	
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;
	
	protected String inputText = null;
	protected InputPanel inputPanel;	
	protected JButton btApply, btCancel, btProperties, btOK;
	private JPanel optionPane, buttonsPanel, btPanel, btPanel2;
	protected GeoElementSelectionListener sl;
	protected JLabel msgLabel; 
		
	protected String initString;
	protected Application app;
	protected InputHandler inputHandler;
	
	protected GeoElement geo;
	
	/**
	 * Creates a non-modal standard input dialog.
	 */
	public InputDialog(Application app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler, GeoElement geo) {
		this(app, message,title, initString, autoComplete, handler, false, false, geo);
	}
	
	public InputDialog(Application app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler) {
		this(app, message,title, initString, autoComplete, handler, false, false, null);
	}
	
	public InputDialog(Application app,  String message, String title, String initString,
			boolean autoComplete, InputHandler handler, boolean modal, boolean selectInitText, GeoElement geo) {
		this(app.getFrame(), modal);
		this.app = app;	
		this.geo = geo;
		inputHandler = handler;
		this.initString = initString;		

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, false, true, selectInitText, false, geo!=null, geo!=null);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
		
		if (initString != null && selectInitText)
			inputPanel.selectText();
		else
            // workaround for Mac OS X 10.5 problem (first character typed deleted)
			// TODO [UNTESTED]
            if (Application.MAC_OS)
	            SwingUtilities.invokeLater( new Runnable(){ public void
	            	run() { inputPanel.getTextComponent().setSelectionStart(1);
	            	inputPanel.getTextComponent().setSelectionEnd(1);} });

	}	
	
	protected InputDialog(JFrame frame, boolean modal) {
		super(frame, modal);
	}
	
	public JPanel getButtonPanel() {
		return btPanel;
	}
	
	protected void createGUI(String title, String message, boolean autoComplete, int columns, int rows,
			boolean specialChars, boolean greekLetters, boolean selectInitText, boolean showDisplayChars,
			boolean showProperties, boolean showApply) {
		setResizable(false);		
		
		//Create components to be displayed
		inputPanel = new InputPanel(initString, app, rows, columns, specialChars, greekLetters, showDisplayChars );	
				
		sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo, boolean addToSelection) {
				insertGeoElement(geo);
				inputPanel.getTextComponent().requestFocusInWindow();
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
		btPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btProperties = new JButton();
		btProperties.setActionCommand("OpenProperties");
		btProperties.addActionListener(this);
		btOK = new JButton();
		btOK.setActionCommand("OK");
		btOK.addActionListener(this);
		btCancel = new JButton();
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btApply = new JButton();
		btApply.setActionCommand("Apply");
		btApply.addActionListener(this);
		
		
		optionPane = new JPanel(new BorderLayout(5,5));
		buttonsPanel = new JPanel(new BorderLayout(5,5));
		msgLabel = new JLabel(message);
		
		if (showProperties) {
			btPanel2.add(btProperties);
			buttonsPanel.add(btPanel2, BorderLayout.EAST);	
			buttonsPanel.add(btPanel, BorderLayout.WEST);	
		}
		else
		{
			buttonsPanel.add(btPanel, BorderLayout.EAST);	
		}
		
		btPanel.add(btOK);
		btPanel.add(btCancel);		
		if (showApply) btPanel.add(btApply);
		
		optionPane.add(msgLabel, BorderLayout.NORTH);	
		optionPane.add(buttonsPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);
		
		setLabels(title);
	}
	
	/**
	 * Update the labels of this component (applied if the language was changed).
	 * 
	 * @param The title of the dialog which is customized for every dialog
	 */
	public void setLabels(String title) {
		setTitle(title);
		
		btOK.setText(app.getPlain("OK"));
		btCancel.setText(app.getPlain("Cancel"));
		btApply.setText(app.getPlain("Apply"));
		
		btProperties.setText(app.getPlain("Properties")+"...");
	}
	
	public void showSpecialCharacters(boolean flag) {
		inputPanel.showSpecialChars(flag);
	}
	
	public void showGreekLetters(boolean flag) {
		inputPanel.showGreekLetters(flag);
	}
	
	public void insertGeoElement(GeoElement geo) {
		if (geo != null)
			insertString(" " + geo.getLabel() + " ");
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
		
		//boolean finished = false;
		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();				
				setVisible(!inputHandler.processInput(inputText));
			} else if (source == btApply) {
				inputText = inputPanel.getText();				
				inputHandler.processInput(inputText);
			} else if (source == btCancel) {
				setVisible(false);
			} else if (source == btProperties && geo != null) {
				setVisible(false);
            	tempArrayList.clear();
            	tempArrayList.add(geo);
                app.getGuiManager().showPropertiesDialog(tempArrayList);
                
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisible(false);
		}
		//setVisible(!finished);
	}
    protected ArrayList tempArrayList = new ArrayList();
	
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
		app.getGuiManager().setCurrentTextfield(this);
	}

	public void windowLostFocus(WindowEvent arg0) {
		app.getGuiManager().setCurrentTextfield(null);
	}

}
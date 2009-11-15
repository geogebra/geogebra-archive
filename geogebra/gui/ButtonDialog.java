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
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoButton;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.JTextComponent;

public class ButtonDialog extends JDialog 
			implements ActionListener, KeyListener, WindowListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextComponent tfCaption, tfScript, tfScript2;
	private JPanel btPanel;
	private DefaultListModel listModel;
	private DefaultComboBoxModel comboModel;
	
	private Point location;
	private JButton btApply, btCancel;
	private JRadioButton rbNumber, rbAngle;
	private InputPanel tfLabel;
	private JPanel optionPane;
	
	private Application app;
	
	private GeoElement geoResult = null;
	private GeoButton button = null;
	
	InputPanel inputPanel, inputPanel2;
	
	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * @param x, y: location of slider in screen coords
	 */
	public ButtonDialog(Application app, int x, int y) {
		super(app.getFrame(), false);
		this.app = app;		
		addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		button = new GeoButton(cons);
		button.setEuclidianVisible(true);
		button.setAbsoluteScreenLoc(x, y);
		
		createGUI();	
		pack();
		setLocationRelativeTo(app.getMainComponent());		
	}			
	
	private void createGUI() {
		setTitle(app.getPlain("Button"));
		setResizable(false);		
		
		// create caption panel
		JLabel captionLabel = new JLabel(app.getMenu("Button.Caption")+":");
		String initString = button == null ? "" : button.getCaption();
		InputPanel ip = new InputPanel(initString, app, 1, 15, true, true, false);				
		tfCaption = ip.getTextComponent();
		if (tfCaption instanceof AutoCompleteTextField) {
			AutoCompleteTextField atf = (AutoCompleteTextField) tfCaption;
			atf.setAutoComplete(false);
		}
		
		captionLabel.setLabelFor(tfCaption);
		JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		captionPanel.add(captionLabel);
		captionPanel.add(ip);
		
		// create script panel
		JLabel scriptLabel = new JLabel(app.getPlain("JavaScript")+":");
		initString = (button == null || button.getJavaScript().equals("")) ? "ggbApplet.evalCommand('A=(3,4)');\n" : button.getJavaScript();
		InputPanel ip2 = new InputPanel(initString, app, 10, 40, false, false, false);				
		tfScript = ip2.getTextComponent();
		if (tfScript instanceof AutoCompleteTextField) {
			AutoCompleteTextField atf = (AutoCompleteTextField) tfScript;
			atf.setAutoComplete(false);
		}
		
		scriptLabel.setLabelFor(tfScript);
		JPanel scriptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		scriptPanel.add(scriptLabel);
		scriptPanel.add(ip2);
		
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
		
		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		optionPane.add(scriptPanel, BorderLayout.CENTER);	
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);			
		
		/*
		
		inputPanel = new InputPanel("ggbApplet.evalCommand('A=(3,4)');", app, 10, 50, false, true, false );	
		inputPanel2 = new InputPanel("function func() {\n}", app, 10, 50, false, true, false );	

		JPanel centerPanel = new JPanel(new BorderLayout());		
			
		centerPanel.add(inputPanel, BorderLayout.CENTER);		
		centerPanel.add(inputPanel2, BorderLayout.SOUTH);	
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		//centerOnScreen();		

		setContentPane(centerPanel);			
		pack();	
		setLocationRelativeTo(app.getFrame());	*/
	}
	
	public GeoElement getResult() {
		if (geoResult != null) {		
			// set label of geoResult
			String strLabel;
			try {								
				strLabel = app.getKernel().getAlgebraProcessor().
								parseLabel(tfLabel.getText());
			} catch (Exception e) {
				strLabel = null;
			}			
			geoResult.setLabel(strLabel);
		}
		
		return geoResult;
	}
		
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		Application.debug(tfScript.getText());				
		if (source == btApply) {				
			
			button.setLabel(null);	
			button.setJavaScript(tfScript.getText());
			
			// set caption text
			String strCaption = tfCaption.getText().trim();
			if (strCaption.length() > 0) {
				button.setCaption(strCaption);			
			}
			
			button.setEuclidianVisible(true);
			button.setLabelVisible(true);
			button.updateRepaint();


			geoResult = button;		
			setVisible(false);
		} 
		else if (source == btCancel) {		
			geoResult = null;
			setVisible(false);
		} 
	}

	private void setLabelFieldFocus() {	
		tfLabel.getTextComponent().requestFocus();
		tfLabel.selectText();	
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ENTER:		
				btApply.doClick();
				break;
				
			case KeyEvent.VK_ESCAPE:
				btCancel.doClick();
				e.consume();
				break;				
		}					
	}

	public void keyReleased(KeyEvent arg0) {		
	}

	public void keyTyped(KeyEvent arg0) {		
	}

	public void windowActivated(WindowEvent arg0) {		
	}

	public void windowClosed(WindowEvent arg0) {		
	}

	public void windowClosing(WindowEvent arg0) {		
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {		
		//setLabelFieldFocus();
	}

	
			
}
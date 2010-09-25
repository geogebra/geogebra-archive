/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.gui.view.algebra.MyComboBoxListener;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoTextField;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicComboPopup;
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
	//private DefaultListModel listModel;
	private DefaultComboBoxModel comboModel;
	
	private GeoElement linkedGeo = null;
	private boolean textField = false;
	
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
	public ButtonDialog(Application app, int x, int y, boolean textField) {
		super(app.getFrame(), false);
		this.app = app;		
		this.textField = textField;
		addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		button = textField ? new GeoTextField(cons) : new GeoButton(cons);
		button.setEuclidianVisible(true);
		button.setAbsoluteScreenLoc(x, y);
		
		createGUI();	
		pack();
		setLocationRelativeTo(app.getMainComponent());		
	}			
	
	private void createGUI() {
		setTitle(textField ? app.getPlain("TextField") : app.getPlain("Button") );
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
		
		
		// combo box to link GeoElement to TextField
		comboModel = new DefaultComboBoxModel();
		TreeSet sortedSet = app.getKernel().getConstruction().
									getGeoSetNameDescriptionOrder();			
		
		final JComboBox cbAdd = new JComboBox(comboModel);

		
		
		if (textField) {
			// lists for combo boxes to select input and output objects
			// fill combobox models
			Iterator it = sortedSet.iterator();
			comboModel.addElement(null);
			FontMetrics fm = getFontMetrics(getFont());
			int width = (int)cbAdd.getPreferredSize().getWidth();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();				
				if (!geo.isGeoImage() && !(geo.isGeoButton()) && !(geo.isGeoBoolean())) {				
					comboModel.addElement(geo);
					String str = geo.toString();
					if (width < fm.stringWidth(str))
						width = fm.stringWidth(str);
				}
			}	
			
			// make sure it's not too wide (eg long GeoList)
			Dimension size = new Dimension(Math.min(app.getScreenSize().width/2, width), cbAdd.getPreferredSize().height);
			cbAdd.setMaximumSize(size);
			cbAdd.setPreferredSize(size);


			
			if (comboModel.getSize() > 1) {
		
				// listener for the combobox
				MyComboBoxListener ac = new MyComboBoxListener() {
					public void doActionPerformed(Object source) {				
						GeoElement geo = (GeoElement) cbAdd.getSelectedItem();		
						//if (geo == null)
						//{
						//	
						//	return;
						//}
						
						linkedGeo = geo;	
						((GeoTextField)button).setLinkedGeo(geo);
						
						cbAdd.removeActionListener(this);		
						
						//cbAdd.setSelectedItem(null);
						cbAdd.addActionListener(this);
					}
				};
				cbAdd.addActionListener(ac);
				cbAdd.addMouseListener(ac);
				
				captionPanel.add(cbAdd);
			}
		}

		
		// create script panel
		JLabel scriptLabel = new JLabel(app.getPlain("Script")+":");
		initString = (button == null || button.getScript().equals("")) ? "A=(3,4)\n" : button.getScript();
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
		
		JPanel linkedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel linkedLabel = new JLabel(app.getPlain("LinkedObject")+":");
		linkedPanel.add(linkedLabel);
		linkedPanel.add(cbAdd);
		
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
		if (textField)
			optionPane.add(linkedPanel, BorderLayout.CENTER);	
		else
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
			button.setScript(tfScript.getText());
			
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
			
			app.getKernel().storeUndoInfo();
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
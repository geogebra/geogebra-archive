/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/
package geogebra.gui;

import geogebra.Application;
import geogebra.GeoElementSelectionListener;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class BooleanCheckboxCreationDialog extends JDialog 
implements WindowFocusListener, ActionListener, GeoElementSelectionListener {
	
	private static final long serialVersionUID = 1L;

	private JTextComponent tfCaption;
	private JButton btApply, btCancel;
	private JPanel optionPane, btPanel;
	private DefaultListModel listModel;
	private DefaultComboBoxModel comboModel;
	
	private Point location;
	private Application app;
	private GeoBoolean geoBoolean;
	
	/**
	 * Input Dialog for a GeoText object
	 */
	public BooleanCheckboxCreationDialog(Application app, Point location, GeoBoolean geoBoolean) {	
		super(app.getFrame(), false);
		this.app = app;
		this.location = location;
		this.geoBoolean = geoBoolean;
		
		initLists();
		createGUI(app.getMenu("ShowCheckBox"));
		pack();
		setLocationRelativeTo(app.getMainComponent());		
	}
	
	private void initLists() {
		// fill combo box with all geos
		comboModel = new DefaultComboBoxModel();		
		TreeSet sortedSet = app.getKernel().getConstruction().getGeoSetNameDescriptionOrder();			
		
		// lists for combo boxes to select input and output objects
		// fill combobox models
		Iterator it = sortedSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();				
			if (geo.isEuclidianShowable()) {				
				comboModel.addElement(geo);
			}
		}	
		
		// fill list with all selected geos
		listModel = new DefaultListModel() {
			public void addElement(Object ob) {
				if (contains(ob)) return;
				
				if (ob instanceof GeoElement) {
					GeoElement geo = (GeoElement) ob;
					if (geo.isEuclidianShowable()) {
						super.addElement(geo);						
						comboModel.removeElement(geo);
					}
				}	
			}
		};
		
	    for (int i=0; i < app.getSelectedGeos().size(); i++) {
		  GeoElement geo = (GeoElement) app.getSelectedGeos().get(i);
		  listModel.addElement(geo);
	    }	
	}
	
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {				
		listModel.addElement(geo);				
	}
			
	protected void createGUI(String title) {
		setTitle(title);
		setResizable(false);		
		
		// create caption panel
		JLabel captionLabel = new JLabel(app.getMenu("Button.Caption")+":");
		String initString = geoBoolean == null ? "" : geoBoolean.getCaption();
		InputPanel ip = new InputPanel(initString, app, 1, 30, true, true);		
		tfCaption = ip.getTextComponent();
		captionLabel.setLabelFor(tfCaption);
		JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		captionPanel.add(captionLabel);
		captionPanel.add(tfCaption);
		
		// list panel
		JPanel listPanel = ToolCreationDialog.
			createInputOutputPanel(app, listModel, comboModel, false);
		
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
		optionPane.add(listPanel, BorderLayout.CENTER);	
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);				
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		
		if (src == btCancel) {
			setVisible(false);
		}
		else if (src == btApply) {
			apply();
			setVisible(false);
		}

		
	}
	
	private void apply() {
		// 	create new GeoBoolean
		if (geoBoolean == null) {
			geoBoolean = new GeoBoolean(app.getKernel().getConstruction());	
			geoBoolean.setValue(true);
			geoBoolean.setAbsoluteScreenLoc(location.x, location.y);
			geoBoolean.setLabel(null);			
		}

		// set visibility condition for all GeoElements in list
		for (int i=0; i < listModel.size(); i++) {
			GeoElement geo = (GeoElement) listModel.get(i);
			geo.setShowObjectCondition(geoBoolean);
		}
		
		// set caption text
		String strCaption = tfCaption.getText().trim();
		if (strCaption.length() > 0) {
			geoBoolean.setCaption(strCaption);			
		}
		
		// update boolean (updates visibility of geos from list too)
		geoBoolean.updateRepaint();
	}
	
	public void windowGainedFocus(WindowEvent arg0) {		
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianView.MODE_ALGEBRA_INPUT ||
			app.getCurrentSelectionListener() != this) 
		{
			app.setSelectionListenerMode(this);
		}	
	}

	public void windowLostFocus(WindowEvent arg0) {
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

}

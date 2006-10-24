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
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SliderDialog extends JDialog implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btApply, btCancel;
	private JRadioButton rbNumber, rbAngle;
	private JPanel optionPane;
	
	private Application app;
	private SliderPanel sliderPanel;
	private AnimationStepPanel stepPanel;
	
	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
	
	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * @param x, y: location of slider in screen coords
	 */
	public SliderDialog(Application app, int x, int y) {
		super(app.getFrame(), true);
		this.app = app;		
		
		// create temp geos that may returned as result
		Construction cons = app.getKernel().getConstruction();
		number = new GeoNumeric(cons);
		number.setEuclidianVisible(true);
		number.setSliderLocation(x, y);
		number.setAbsoluteScreenLocActive(true);
		
		angle = new GeoAngle(cons);
		angle.setEuclidianVisible(true);
		angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		angle.setSliderLocation(x, y);
		angle.setAbsoluteScreenLocActive(true);
		
		geoResult = null;

		createGUI();	
		centerOnScreen();
	}	
	
	private void createGUI() {
		setTitle(app.getPlain("Slider"));
		setResizable(false);		

		//Create components to be displayed
		
		// radio buttons for number or angle
		ButtonGroup bg = new ButtonGroup();
		rbNumber = new JRadioButton(app.getPlain("Numeric"));
		rbAngle = new JRadioButton(app.getPlain("Angle"));		
		rbNumber.addActionListener(this);
		rbAngle.addActionListener(this);
		rbNumber.setSelected(true);
		bg.add(rbNumber);
		bg.add(rbAngle);						
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		radioPanel.add(rbNumber);
		radioPanel.add(rbAngle);
		
		// slider panels		
		sliderPanel = new SliderPanel(app, null);
		stepPanel = new AnimationStepPanel(app);		
		JPanel slPanel = new JPanel(new BorderLayout());		
		GeoElement [] geos = { number };
		slPanel.add(sliderPanel.update(geos), BorderLayout.CENTER);
		slPanel.add(stepPanel.update(geos), BorderLayout.SOUTH);
		
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
		optionPane.add(radioPanel, BorderLayout.NORTH);
		optionPane.add(slPanel, BorderLayout.CENTER);
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);
		pack();
	}
	
	private void centerOnScreen() {		
		// center on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension d = getSize();
		setLocation( (dim.width - d.width) / 2, 
					 (dim.height - d.height) / 2);
	}
	
	public GeoElement getResult() {
		return geoResult;
	}
		
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
				
		if (source == btApply) {				
			geoResult = rbNumber.isSelected() ? number : angle; 			
			setVisible(false);
		} 
		else if (source == btCancel) {						
			setVisible(false);
		} 
		else if (source == rbNumber || source == rbAngle) {
			GeoElement selGeo = rbNumber.isSelected() ? number : angle;
			GeoElement [] geos = { selGeo };
			sliderPanel.update(geos);
			stepPanel.update(geos);
		}		
	}
			
}
/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

public class SliderDialog extends JDialog 
			implements ActionListener, KeyListener, WindowListener
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btApply, btCancel;
	private JRadioButton rbNumber, rbAngle, rbInteger;
	private InputPanel tfLabel;
	private JPanel optionPane;
	private JCheckBox cbRandom;
	
	private Application app;
	private SliderPanel sliderPanel;
	
	private GeoElement geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
	
	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * @param x, y: location of slider in screen coords
	 */
	public SliderDialog(Application app, int x, int y) {
		super(app.getFrame(), false);
		this.app = app;		
		addWindowListener(this);
		
		// create temp geos that may be returned as result
		Construction cons = app.getKernel().getConstruction();
		number = new GeoNumeric(cons);
		number.setEuclidianVisible(true);
		number.setSliderLocation(x, y);
		number.setAbsoluteScreenLocActive(true);
		number.setValue(1);
		
		angle = new GeoAngle(cons);
		angle.setEuclidianVisible(true);
		angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		angle.setSliderLocation(x, y);
		angle.setAnimationType(GeoElement.ANIMATION_INCREASING);
		angle.setAbsoluteScreenLocActive(true);
		angle.setValue(45 * Math.PI/180);
		
		geoResult = null;

		createGUI();	
	}			
	
	private void createGUI() {
		setTitle(app.getPlain("Slider"));
		setResizable(false);		

		//Create components to be displayed			
		
		// radio buttons for number or angle
		ButtonGroup bg = new ButtonGroup();
		rbNumber = new JRadioButton(app.getPlain("Numeric"));		
		rbAngle = new JRadioButton(app.getPlain("Angle"));		
		rbInteger = new JRadioButton(app.getPlain("Integer"));		
		rbNumber.addActionListener(this);
		rbAngle.addActionListener(this);		
		rbInteger.addActionListener(this);		
		bg.add(rbNumber);
		bg.add(rbAngle);			
		bg.add(rbInteger);			
		rbNumber.setSelected(true);
		//JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
		radioPanel.add(rbNumber);		
		radioPanel.add(rbAngle);			
		radioPanel.add(rbInteger);			
		
		// label textfield
		tfLabel = new InputPanel(number.getDefaultLabel(), app, 1, 10, false, true, false);				
		tfLabel.getTextComponent().addKeyListener(this);				
		Border border =
			BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(app.getPlain("Name")),
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tfLabel.setBorder(border);
		
		cbRandom = new JCheckBox("Random");
		
		// put together label textfield and radioPanel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0,0));
		JPanel labelPanel = new JPanel(new BorderLayout(0,0));
		labelPanel.add(tfLabel, BorderLayout.NORTH);
		labelPanel.add(cbRandom, BorderLayout.SOUTH);
		topPanel.add(labelPanel, BorderLayout.CENTER);
		topPanel.add(radioPanel, BorderLayout.WEST);

		// slider panels		
		sliderPanel = new SliderPanel(app, null, true);			
		JPanel slPanel = new JPanel(new BorderLayout(0,0));		
		GeoElement [] geos = { number };
		slPanel.add(sliderPanel.update(geos), BorderLayout.CENTER);		
		
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
		optionPane.add(topPanel, BorderLayout.NORTH);
		optionPane.add(slPanel, BorderLayout.CENTER);
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));				
		
		//Make this dialog display it.
		setContentPane(optionPane);			
		pack();	
		setLocationRelativeTo(app.getFrame());	
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
				
		if (source == btApply) {				
			geoResult = rbAngle.isSelected() ? angle : number; 		
			getResult();
			geoResult.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			geoResult.setLabelVisible(true);
			geoResult.update();
			((GeoNumeric)geoResult).setRandom(cbRandom.isSelected());

			setVisible(false);
			
			app.getKernel().storeUndoInfo();
		} 
		else if (source == btCancel) {						
			setVisible(false);
		} 
		else if (source == rbNumber || source == rbAngle || source == rbInteger) {
			GeoElement selGeo = rbAngle.isSelected() ? angle : number;
			
			if (source == rbInteger) {
				number.setAnimationStep(1);
				number.setIntervalMin(1);
				number.setIntervalMax(30);
			} else if (source == rbNumber) {
				number.setAnimationStep(GeoNumeric.DEFAULT_SLIDER_INCREMENT);
				number.setIntervalMin(GeoNumeric.DEFAULT_SLIDER_MIN);
				number.setIntervalMax(GeoNumeric.DEFAULT_SLIDER_MAX);
			}
			GeoElement [] geos = { selGeo };			
			sliderPanel.update(geos);			
			
			// update label text field
			tfLabel.setText(selGeo.getDefaultLabel());	
			setLabelFieldFocus();
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
		setLabelFieldFocus();
	}

	
			
}
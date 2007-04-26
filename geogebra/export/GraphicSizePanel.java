/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

package geogebra.export;

import geogebra.Application;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel with fields to enter width and height of graphic file to be exported.
 * The ratio of init width and heigth is kept constant.
 * Use methods getSelectedWidth() and getSelectedHeight() to retrieve
 * the searched values.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class GraphicSizePanel extends JPanel implements ActionListener,
	FocusListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final int MIN = 10;

    private static final int MAX = 5000;

    private int width, height;

    private double ratio;

    private JTextField tfWidth, tfHeight;

    private boolean keepRatio;

    public GraphicSizePanel(Application app, int width, int height) {
	this(app, width, height, true);
    }

    public GraphicSizePanel(Application app, int width, int height,
	    boolean keepRatio) {
	//this.app = app;

	setLayout(new FlowLayout(5));
	tfWidth = new JTextField(5);
	tfHeight = new JTextField(5);
	add(new JLabel(app.getPlain("Width") + ":"));
	add(tfWidth);
	add(new JLabel(app.getPlain("Height") + ":"));
	add(tfHeight);
	
	tfWidth.setHorizontalAlignment(JTextField.RIGHT);
	tfHeight.setHorizontalAlignment(JTextField.RIGHT);

	setValues(width, height, keepRatio);

	tfWidth.addActionListener(this);
	tfHeight.addActionListener(this);
	tfWidth.addFocusListener(this);
	tfHeight.addFocusListener(this);
    }

    public void setValues(int width, int height, boolean keepRatio) {
	this.width = width;
	this.height = height;
	this.keepRatio = keepRatio;
	ratio = (double) width / height;

	tfWidth.setText("" + width);
	tfHeight.setText("" + height);
    }

    public int getSelectedWidth() {
	return width;
    }

    public int getSelectedHeight() {
	return height;
    }

    public void setEnabled(boolean flag) {
	Object[] comp = getComponents();
	for (int i = 0; i < comp.length; i++) {
	    ((JComponent) comp[i]).setEnabled(flag);
	}
    }

    private void updateValues(Object src) {
	if (src == tfWidth) {
	    try {
		int newValue = Integer.parseInt(tfWidth.getText());
		if (MIN <= newValue && newValue <= MAX) {
		    width = newValue;
		}
	    } catch (Exception e) {
	    }
	    tfWidth.setText("" + width);
	    if (keepRatio) {
		height = (int) Math.floor(width / ratio);
		tfHeight.setText("" + height);
	    }
	}
	else if (src == tfHeight) {
	    try {
		int newValue = Integer.parseInt(tfHeight.getText());
		if (MIN <= newValue && newValue <= MAX) {
		    height = newValue;
		}
	    } catch (Exception e) {
	    }
	    tfHeight.setText("" + height);
	    if (keepRatio) {
		width = (int) Math.floor(height * ratio);
		tfWidth.setText("" + width);
	    }
	}
    }

    public void actionPerformed(ActionEvent ev) {
	updateValues(ev.getSource());
    }

    public void focusGained(FocusEvent ev) {
    }

    public void focusLost(FocusEvent ev) {
	updateValues(ev.getSource());
    }

}

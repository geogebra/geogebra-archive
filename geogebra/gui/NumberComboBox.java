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
import geogebra.kernel.Kernel;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JComboBox;

public class NumberComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_FRAC_DIGITS = 5;
	private static final String PI_STRING = "\u03c0";
	
	private NumberFormat nf;
	private Application app;
	private Kernel kernel;
	
	public NumberComboBox(final Application app) {
		this.app = app;		
		kernel = app.getKernel();
		
		addItem("1"); //pi
		addItem(PI_STRING); //pi
		addItem(PI_STRING + "/2"); //pi/2
		setEditable(true);
		setSelectedItem(null);
		
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(MAX_FRAC_DIGITS);
		
		final Dimension dim = getPreferredSize();
		dim.width = app.getPlainFont().getSize() * (MAX_FRAC_DIGITS+3);
		setPreferredSize(dim);
	}
	
	public void setValue(final double val) {	
		setSelectedItem(kernel.formatPi(val, nf));			
	}
	
	public double getValue() {
		final Object ob = getSelectedItem();
		if (ob == null) return Double.NaN;
		
		final String text = ob.toString().trim();
		if (text.equals("")) return Double.NaN;
		return app.getAlgebraController().evaluateToDouble(text);			
	}


}

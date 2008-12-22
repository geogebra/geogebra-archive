/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgoText extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // input
	private GeoBoolean substituteVars; // optional input
	private GeoPoint startPoint, startPointCopy; // optional input
	private GeoText text;     // output              

	public AlgoText(Construction cons, String label, GeoElement geo) {
		this(cons, label, geo, null, null);
	}   

	public AlgoText(Construction cons, String label, GeoElement geo, GeoBoolean substituteVars) {
		this(cons, label, geo, null, substituteVars);
	}   
	
	public AlgoText(Construction cons, String label, GeoElement geo, GeoPoint p) {
		this(cons, label, geo, p, null);
	}   

	public AlgoText(Construction cons, String label, GeoElement geo, GeoPoint p, GeoBoolean substituteVars) {
		super(cons);
		this.geo = geo;
		this.startPoint = p;
		this.substituteVars = substituteVars;

		text = new GeoText(cons);
		text.setIsCommand(true); // stop editing as text
		
		// set startpoint
		if (startPoint != null) {
			startPointCopy = (GeoPoint) startPoint.copyInternal(cons);
			
			try {
				text.setStartPoint(startPointCopy);
			}
			catch (CircularDefinitionException e) {
				e.printStackTrace();				
			}
			text.setAlwaysFixed(true); // disable dragging if p != null
		}
		
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();      
		text.setLabel(label);		
	}

	protected String getClassName() {
		return "AlgoText";
	}

	// for AlgoElement
	protected void setInputOutput() {

		int inputs = 1;
		if (startPoint != null) inputs++;
		if (substituteVars != null) inputs++;

		int i=0;
		input = new GeoElement[inputs];
		input[i++] = geo;
		if (startPoint != null) input[i++] = startPoint;
		if (substituteVars != null) input[i++] = substituteVars;

		output = new GeoElement[1];        
		output[0] = text;        
		setDependencies(); // done by AlgoElement
	}    

	public GeoText getGeoText() { return text; }

	protected final void compute() {    
		
		// undefined text
		if (!geo.isDefined() || 
				(startPoint != null && !startPoint.isDefined()) ||
				(substituteVars != null && !substituteVars.isDefined())) 
		{
			text.setUndefined();
			return;
		}
		
		if (text.useSignificantFigures()) {
			kernel.setTemporaryPrintFigures(text.getPrintFigures());
		} else {
			kernel.setTemporaryPrintDecimals(text.getPrintDecimals());
		}
					
		// standard case: set text
		boolean bool = substituteVars == null ? true : substituteVars.getBoolean();
		text.setTextString(geo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, bool));	
		
		kernel.restorePrintAccuracy();
		
		// update startpoint position of text
		if (startPointCopy != null) {
			startPointCopy.setCoords(startPoint);		
		}
	}         
}

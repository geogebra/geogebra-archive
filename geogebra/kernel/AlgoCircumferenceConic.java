/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.kernel;

import geogebra.kernel.integration.EllipticArcLength;

/**
 * Algorithm to compute the circumference of a
 * {@link geogebra.kernel.GeoConic GeoConic}.
 * 
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 * @author Markus Hohenwarter
 */
public class AlgoCircumferenceConic extends AlgoElement {

	// Take a conic as input
	private GeoConic conic;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric circum;

	// Helper to calculate circumference for ellipse
	private EllipticArcLength ellipticArcLength = null;

	AlgoCircumferenceConic(Construction cons, String label, GeoConic conic) {
		this(cons, conic);
		circum.setLabel(label);
	}

	AlgoCircumferenceConic(Construction cons, GeoConic conic) {
		super(cons);
		this.conic = conic;

		circum = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	protected String getClassName() {
		return "AlgoCircumferenceConic";
	}

	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = conic;

		output = new GeoElement[1];
		output[0] = circum;
		setDependencies();
	}

	/**
	 * Compute circumference. In order to do so we have to distinguishe between
	 * the following cases:
	 * 
	 * <pre>
	 *    a) conic is a circle
	 *    b) conic is an ellipse
	 * </pre>
	 * 
	 * For all other cases circumference is undefined.
	 */
	protected final void compute() {
    	if (!conic.isDefined())
    		circum.setUndefined();
    	
    	// conic type
    	int type = conic.getType();	
		
    	// circumference of sector
    	if (conic.isGeoConicPart()) {
    		GeoConicPart conicPart = (GeoConicPart) conic;
    		int partType = conicPart.getConicPartType();
    		if (type == GeoConic.CONIC_CIRCLE && partType == GeoConicPart.CONIC_PART_SECTOR) {				
				/* value of sector is area:
					area = r*r * paramExtent / 2;
					arclength = r * paramExtent;
					arclength = area * 2/r;
				*/
				double area = conicPart.getValue();
				double r = conic.halfAxes[0]; 
				double arclength = area * 2.0 / r;
				
				// circumference of sector
				circum.setValue(arclength + 2 * r);					
			}
			else if (type == GeoConic.CONIC_CIRCLE && partType == GeoConicPart.CONIC_PART_ARC) {				
				// value of arc is curved length
			double arclength = conicPart.getValue();
			double r = conic.halfAxes[0]; 
			double angle = conicPart.getParameterExtent();
			
			// return circumference of **segment**
			// ie curved + straight
			circum.setValue(arclength + 2.0 * r * Math.sin(angle/2));					
		}
		else 
				// circumference of ellipse sector is undefined
	    		// note: circumference of ellipse sector is simply not implemented yet
				circum.setUndefined();
			
			return;
    	}
    	
    	// standard case: conic			
		switch (type) {
			case GeoConic.CONIC_CIRCLE:
				// r is length of one of the half axes
				double r = conic.halfAxes[0];
				circum.setValue(2 * r * Math.PI);
				break;
				
			case GeoConic.CONIC_ELLIPSE:
				if (ellipticArcLength == null)
					ellipticArcLength = new EllipticArcLength(conic);
				circum.setValue(ellipticArcLength.compute(0, 2 * Math.PI));
				break;
					
			default:			
				circum.setUndefined();			
		}
	}

	/**
	 * Get the GeoConics's circumference.
	 * 
	 * @return circumference
	 */
	GeoNumeric getCircumference() {
		return circum;
	}	
}

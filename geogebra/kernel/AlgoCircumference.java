/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

package geogebra.kernel;


/**
 * Algorithm to compute the circumference of a 
 * {@link geogebra.kernel.GeoPolygon GeoPolygon}.
 * 
 * The following algorithm is used:
 * 
 * <p><blockquote><pre>
 *   u = r * n * sin (180°/n)
 *   
 *   r ... circumradius
 *   n ... count of points
 *     
 *   r = a / 2 * sin (180°/n)
 *   a ... side length
 * </pre></blockquote></p>
 * 
 * <p><b>Note:</b> I am aware of the fact that there is room for
 * improvement for polygons like trigons (triangels) or
 * pentagons which could be computed without the very general
 * formula seen above. However, I don't see any sagnificant
 * advantage especially in respect of speed. Therefore no
 * differentiation was made to tell these cases apart. </p>
 * 
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class AlgoCircumference extends AlgoElement {

    // Either takes a polygon or a conic as input
    private GeoPolygon polygon;

    private GeoConic conic;

    // Output is a GeoNumeric (= a number)
    private GeoNumeric u;

    // -----------------------------
    //  Constructor for GeoPolygon
    // -----------------------------
    AlgoCircumference(Construction cons, String label, GeoPolygon polygon) {
	this(cons, polygon);
	u.setLabel(label);
    }

    AlgoCircumference(Construction cons, GeoPolygon polygon) {
	super(cons);
	this.polygon = polygon;

	u = new GeoNumeric(cons);
	u.setValue(-1); // init u
	setInputOutput();
	compute();
	System.out.println("cd: "+getCommandDescription());
    }

    // -----------------------------
    //  Constructor for GeoConic
    // -----------------------------

    public AlgoCircumference(Construction cons, String label, GeoConic conic) {
	this(cons, conic);
	u.setLabel(label);
    }

    public AlgoCircumference(Construction cons, GeoConic conic) {
	super(cons);
	this.conic = conic;

	u = new GeoNumeric(cons);
	setInputOutput();
	compute();
    }

    String getClassName() {
	return "AlgoCircumference";
    }

    // for AlgoElement
    void setInputOutput() {
	input = new GeoElement[1];
	// for polygon
	if (polygon != null && conic == null) {
	    input[1] = polygon;
	} else if (conic != null && polygon == null) {
	    input[1] = conic;
	}

	output = new GeoElement[1];
	output[0] = u;
	setDependencies(); // done by AlgoElement
    }

    /**
     * Compute circumference.
     * 
     * u = r * n * sin (180°/n)
     * r ... circumradius
     * n ... count of points
     * 
     * r = a / 2 * sin (180°/n)
     * a ... side length
     */
    final void compute() {
	// Compute circumradius
	GeoSegment[] segment = polygon.getSegments();
	double length = 0;
	for (int i = 0; i < segment.length; i++) {
	    length = +segment[i].getLength();
	}
	u.setValue(length);
	//XXX Does this really work?
    }

    /**
     * Get the GeoPolygon's circumference.
     * @return circumference
     */
    GeoNumeric getCircumference() {
	return u;
    }

    final public String toString() {
	/*
	StringBuffer sb = new StringBuffer();

	sb.append(app.getCommand("Circumference"));
	sb.append("[ ");

	if (polygon != null && conic == null) {
	    
	    sb.append(polygon.getLabel());
	    
	} else
	if (conic != null && polygon == null) {
	    
	    sb.append(conic.getLabel());
	    
	}

	sb.append("]");

	return sb.toString(); */
	return super.toString();
    }
}

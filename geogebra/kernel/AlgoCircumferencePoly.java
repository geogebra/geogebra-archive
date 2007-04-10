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
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class AlgoCircumferencePoly extends AlgoElement {

    // Take a polygon as input
    private GeoPolygon polygon;

    // Output is a GeoNumeric (= a number)
    private GeoNumeric circum;

    AlgoCircumferencePoly(Construction cons, String label, GeoPolygon polygon) {
	this(cons, polygon);
	circum.setLabel(label);
    }

    AlgoCircumferencePoly(Construction cons, GeoPolygon polygon) {
	super(cons);
	this.polygon = polygon;

	circum = new GeoNumeric(cons);
	setInputOutput();
	compute();
    }

    String getClassName() {
	return "AlgoCircumference";
    }

    void setInputOutput() {
	input = new GeoElement[1];
	input[0] = polygon;

	output = new GeoElement[1];
	output[0] = circum;
	setDependencies();
    }

    /**
     * Compute circumference by adding up the
     * length of it's segemnts.
     */
    final void compute() {
	GeoSegment[] segment = polygon.getSegments();
	double length = 0;
	for (int i = 0; i < segment.length; i++) {
	    length = length + (segment[i].getLength());
	}
	circum.setValue(length);
    }

    /**
     * Get the GeoPolygon's circumference.
     * @return circumference
     */
    GeoNumeric getCircumference() {
	return circum;
    }

    final public String toString() {
	return super.toString();
    }
}

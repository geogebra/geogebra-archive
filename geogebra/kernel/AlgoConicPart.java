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

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Super class for all algorithms creating
 * conic arcs or sectors.
 */
public abstract class AlgoConicPart extends AlgoElement {

    GeoConic conic; // input
    NumberValue startParam, endParam; // input
    GeoConicPart conicPart; // output   
    
    int type;

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CIRCLE_ARC or 
     * GeoConicPart.CIRCLE_ARC.CIRCLE_SECTOR         
     */
    AlgoConicPart(Construction cons, int type) {
        super(cons);        
        this.type = type;        
    }
    
	String getClassName() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return "AlgoConicArc";
			default:
				return "AlgoConicSector";
		}		
	}

    public GeoConicPart getConicPart() {
        return conicPart;
    }
   
    void compute() {    	    	
    	conicPart.set(conic);
    	conicPart.setParameters(startParam.getDouble(), endParam.getDouble(), true);
    }

    
	public String toString() {
		return getCommandDescription();
	}

}

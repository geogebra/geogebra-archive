/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

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

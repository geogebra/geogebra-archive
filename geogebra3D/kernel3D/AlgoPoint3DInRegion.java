/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Region;


public class AlgoPoint3DInRegion extends AlgoElement3D {

	
	private Region region; // input
    private GeoPoint3D P; // output       

    AlgoPoint3DInRegion(
        Construction cons,
        String label,
        Region region,
        double x,
        double y,
        double z) {
        super(cons);
        this.region = region;
        P = new GeoPoint3D(cons, region);
        
        P.setWillingCoords(x, y, z, 1.0);
        

        
        region.pointChangedForRegion(P);
        

        setInputOutput(); // for AlgoElement

        // compute 
        compute();
        P.setLabel(label);
    }
    
    
    

    protected String getClassName() {
        return "AlgoPoint3DInRegion";
    }

    // for AlgoElement
    protected void setInputOutput() {
    	
    	input = new GeoElement[1];  	
        input[0] = region.toGeoElement();

        output = new GeoElement[1];
        output[0] = P;
        setDependencies(); // done by AlgoElement
        
     }

    GeoPoint3D getP() {
        return P;
    }
    
    Region getRegion() {
        return region;
    }

    protected final void compute() {
    	
    	
    	if (input[0].isDefined()) {	    	
	        region.regionChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    	
    }

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("PointInA",input[0].getLabel()));
        
        return sb.toString();
    }
}

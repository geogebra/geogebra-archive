/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * 
 *
 *  
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;




/**
 *
 * @author  ggb3D
 * @version 
 */
public class AlgoJoinPoints3DPolygon extends AlgoElement3D {

	private static final long serialVersionUID = 1L;
	private GeoPoint3D [] points;  // input
    private GeoPolygon3D poly; // output 


    /** Creates new AlgoJoinPoints */
    AlgoJoinPoints3DPolygon(Construction cons, String label, GeoPoint3D [] points) {
        this(cons, points);
        poly.setLabel(label);
    }

    AlgoJoinPoints3DPolygon(Construction cons, GeoPoint3D [] points) {
    	super(cons);
 
    	this.points=points;
          
        poly = new GeoPolygon3D(cons, points);          
        setInputOutput(); // for AlgoElement
               
        // compute line through P, Q
        compute();
    }   

    protected String getClassName() {
        return "AlgoJoinPoints3DTriangle";
    }

    // for AlgoElement
    protected void setInputOutput() {
  	
    	// points as input (TODO : use list, see AlgoPolygon)
    	input = points;
 	
    	// set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        
    	setOutput();

        // parent of output
        poly.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 

    }
    
    private void setOutput() {
    	
    	output = new GeoElement[1];                       
        output[0] = poly; 
        
    	//TODO use this method for segments (see AlgoPolygon)
    }

    GeoPolygon3D getPoly() {
        return poly;
    }
     

    // recalc the poly
    protected final void compute() {
    	   
    	poly.setCoord(points);
    }

    
  
    final public String toString() {
        StringBuffer sb = new StringBuffer();
  
        sb.append(app.getPlain("Polygon"));
        sb.append(' ');     
        int last = points.length - 1;       
        for (int i = 0; i < last; i++) {
            sb.append(points[i].getLabel());
            sb.append(", ");
        }
        sb.append(points[last].getLabel());        
        
        return  sb.toString();
    }
}

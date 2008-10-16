/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;




/**
 *
 * @author  ggb3D
 * @version 
 */
public abstract class AlgoJoinPoints3DCoordSys1D extends AlgoElement3D {

	private static final long serialVersionUID = 1L;
	protected GeoPoint3D P, Q; // input
    protected GeoCoordSys1D cs; // output 


    /** Creates new AlgoJoinPoints */
    AlgoJoinPoints3DCoordSys1D(
        Construction cons,        
        GeoPoint3D P,
        GeoPoint3D Q) {
    	super(cons);
    	
                  
        this.P = P;
        this.Q = Q;
                
    }   



    // for AlgoElement
    protected void setInputOutput() {
    	GeoElement3D [] efficientInput = new GeoElement3D[2];
    	efficientInput[0] = P;
    	efficientInput[1] = Q;
    	

    	input = new GeoElement3D[2];
    	input[0] = P;
    	input[1] = Q;

    	            	
    	
        output = new GeoElement3D[1];
        output[0] = cs;
          
        //setDependencies();
        setEfficientDependencies(input, efficientInput);
    }

    GeoPoint3D getP() {
        return P;
    }
    GeoPoint3D getQ() {
        return Q;
    }
    

    // recalc the segment joining P and Q    
    protected void compute() {
    	    	   
    	cs.setCoord(P,Q);
    }

    
  
 

}

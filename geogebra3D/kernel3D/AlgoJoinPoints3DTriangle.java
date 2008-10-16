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

import geogebra.Application;
import geogebra.kernel.Construction;




/**
 *
 * @author  ggb3D
 * @version 
 */
public class AlgoJoinPoints3DTriangle extends AlgoElement3D {

	private static final long serialVersionUID = 1L;
	private GeoPoint3D P, Q, R; // input
    private GeoTriangle3D t; // output 


    /** Creates new AlgoJoinPoints */
    AlgoJoinPoints3DTriangle(
        Construction cons,
        String label,
        GeoPoint3D P,
        GeoPoint3D Q,
        GeoPoint3D R) {
        this(cons, P, Q, R);
        t.setLabel(label);
    }

    AlgoJoinPoints3DTriangle(
        Construction cons,        
        GeoPoint3D P,
        GeoPoint3D Q,
        GeoPoint3D R) {
    	super(cons);
    	
                  
        this.P = P;
        this.Q = Q;
        this.R = R;
          
        t = new GeoTriangle3D(cons, P, Q, R);          
        setInputOutput(); // for AlgoElement
               
        // compute line through P, Q
        compute();
    }   

    protected String getClassName() {
        return "AlgoJoinPoints3DTriangle";
    }

    // for AlgoElement
    protected void setInputOutput() {
    	GeoElement3D [] efficientInput = new GeoElement3D[3];
    	efficientInput[0] = P;
    	efficientInput[1] = Q;
    	efficientInput[2] = R;
    	

    	input = new GeoElement3D[3];
    	input[0] = P;
    	input[1] = Q;
    	input[2] = R;

    	            	
    	
        output = new GeoElement3D[1];
        output[0] = t;
          
        //setDependencies();
        setEfficientDependencies(input, efficientInput);
    }

    GeoTriangle3D getTriangle() {
        return t;
    }
    GeoPoint3D getP() {
        return P;
    }
    GeoPoint3D getQ() {
        return Q;
    }
    GeoPoint3D getR() {
        return R;
    }
    

    // recalc the triangle joining P and Q and R    
    protected final void compute() {
    	//Application.debug("compute (triangle)");    	   
    	t.setCoord(P,Q,R);
    }

    
  
 
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(app.getPlain("TriangleABC",P.getLabel(),Q.getLabel(),R.getLabel()));

        return sb.toString();
    }
}

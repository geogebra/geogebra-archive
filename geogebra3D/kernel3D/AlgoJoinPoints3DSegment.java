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
public class AlgoJoinPoints3DSegment extends AlgoElement3D {

	private static final long serialVersionUID = 1L;
	private GeoPoint3D P, Q; // input
    private GeoSegment3D s; // output 


    /** Creates new AlgoJoinPoints */
    AlgoJoinPoints3DSegment(
        Construction cons,
        String label,
        GeoPoint3D P,
        GeoPoint3D Q) {
        this(cons, P, Q);
        s.setLabel(label);
    }

    AlgoJoinPoints3DSegment(
        Construction cons,        
        GeoPoint3D P,
        GeoPoint3D Q) {
    	super(cons);
    	
                  
        this.P = P;
        this.Q = Q;
          
        s = new GeoSegment3D(cons, P, Q);          
        setInputOutput(); // for AlgoElement
               
        // compute line through P, Q
        compute();
    }   

    protected String getClassName() {
        return "AlgoJoinPoints3DSegment";
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
        output[0] = s;
          
        //setDependencies();
        setEfficientDependencies(input, efficientInput);
    }

    GeoSegment3D getSegment() {
        return s;
    }
    GeoPoint3D getP() {
        return P;
    }
    GeoPoint3D getQ() {
        return Q;
    }
    

    // recalc the segment joining P and Q    
    protected final void compute() {
    	    	   
    	s.setCoord(P,Q);
    }

    
  
 
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(app.getPlain("SegmentAB",P.getLabel(),Q.getLabel()));

        return sb.toString();
    }
}

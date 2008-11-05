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
 * 
 * Joins two GeoPoint3Ds in a GeoSegment3D, GeoLine3D, ... regarding to geoClassType
 * 
 */
public class AlgoJoinPoints3D extends AlgoElement3D {

	private static final long serialVersionUID = 1L;
	protected GeoPoint3D P, Q; // input
    protected GeoCoordSys1D cs; // output 
    protected int geoClassType;


    /** Creates new AlgoJoinPoints3D */    
    AlgoJoinPoints3D(Construction cons, String label, GeoPoint3D P, GeoPoint3D Q, int geoClassType) {

    	this(cons,P,Q,geoClassType);
    	cs.setLabel(label);

    }

    AlgoJoinPoints3D(Construction cons, GeoPoint3D P, GeoPoint3D Q, int geoClassType) {
    	super(cons);


    	this.P = P;
    	this.Q = Q;
    	this.geoClassType = geoClassType;

    	switch(geoClassType){
    	case GeoElement3D.GEO_CLASS_SEGMENT3D:
    		cs = new GeoSegment3D(cons, P, Q);
    		break;
    	case GeoElement3D.GEO_CLASS_LINE3D:
    		cs = new GeoLine3D(cons, P, Q); 
    		break;
    	default:
    		cs = null;
    	}
    	  
    	setInputOutput(); // for AlgoElement

    	// compute line through P, Q
    	compute();
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
    
    GeoCoordSys1D getCS(){
    	return cs;
    }
    

    // recalc the segment joining P and Q    
    protected void compute() {
    	    	   
    	cs.setCoord(P,Q);
    }

	protected String getClassName() {
		String s = 	"AlgoJoinPoints3D";
    	switch(geoClassType){
    	case GeoElement3D.GEO_CLASS_SEGMENT3D:
    		s+="Segment";
    		break;
    	case GeoElement3D.GEO_CLASS_LINE3D:
    		s+="Line";
    		break;
    	}		
    	
    	return s;
	}

	
	
	
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        switch(geoClassType){
    	case GeoElement3D.GEO_CLASS_SEGMENT3D:
    		sb.append(app.getPlain("SegmentAB",P.getLabel(),Q.getLabel()));
    		break;
    	case GeoElement3D.GEO_CLASS_LINE3D:
    		sb.append(app.getPlain("LineThroughAB",P.getLabel(),Q.getLabel()));
    		break;
    	}	

        return sb.toString();
    }   
  
 

}

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
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;




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
	protected GeoPointInterface P, Q; // input
	private GeoPolygon3D polygon;
    protected GeoCoordSys1D cs; // output 
    protected int geoClassType;


    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param label name of the segment/line/...
     * @param P first point
     * @param Q second point
     * @param geoClassType type (GeoSegment3D, GeoLine3D, ...) */    
    AlgoJoinPoints3D(Construction cons, String label, GeoPoint3D P, GeoPoint3D Q, int geoClassType) {

    	this(cons,label,P,Q,null,geoClassType);
 
    }
    
    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param label name of the segment/line/...
     * @param P first point
     * @param Q second point
     * @param polygon polygon providing a 2D coord sys (when P and Q are 2D points)
     * @param geoClassType type (GeoSegment3D, GeoLine3D, ...) */    
    AlgoJoinPoints3D(Construction cons, String label, 
    		GeoPointInterface P, GeoPointInterface Q, GeoPolygon3D polygon, int geoClassType) {

    	this(cons,P,Q,polygon,geoClassType);
    	cs.setLabel(label);

    }
    

    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param P first point
     * @param Q second point
     * @param polygon polygon providing a 2D coord sys (when P and Q are 2D points)
     * @param geoClassType type (GeoSegment3D, GeoLine3D, ...) */    
    AlgoJoinPoints3D(Construction cons, 
    		GeoPointInterface P, GeoPointInterface Q, GeoPolygon3D polygon, int geoClassType) {
    	super(cons);


    	this.P = P;
    	this.Q = Q;
    	this.polygon = polygon;
    	this.geoClassType = geoClassType;

    	switch(geoClassType){
    	case GeoElement3D.GEO_CLASS_SEGMENT3D:
    		if (polygon==null)
    			cs = new GeoSegment3D(cons, (GeoPoint3D) P, (GeoPoint3D) Q);
    		else{ //P and Q are two GeoPoints in cs2D coord sys
    			cs = new GeoSegment3D(cons);
     		}
    		break;
    	case GeoElement3D.GEO_CLASS_LINE3D:
    		if (polygon==null)
    			cs = new GeoLine3D(cons, (GeoPoint3D) P, (GeoPoint3D) Q); 
    		break;
    	default:
    		cs = null;
    	}
    	
    	
    	if (polygon==null)
    		setInputOutput(new GeoElement[] {(GeoElement) P,(GeoElement) Q}, new GeoElement[] {cs});
    	else
    		setInputOutput(new GeoElement[] {(GeoElement) P,(GeoElement) Q, polygon}, new GeoElement[] {cs});
    	 
    		
    	/*
    	setInputOutput(); // for AlgoElement

    	// compute line through P, Q
    	compute();
    	*/
    }       



    // for AlgoElement
    /*
    protected void setInputOutput() {
    	GeoElement [] efficientInput = new GeoElement[2];
    	efficientInput[0] = (GeoElement) P;
    	efficientInput[1] = (GeoElement) Q;
    	

    	if (polygon == null) {
    		input = efficientInput;    		
    	} else {
    		input = new GeoElement[3];
    		input[0] = efficientInput[0];
            input[1] = efficientInput[1];
            input[2] = polygon;             
    	}   
    	            	
    	
        output = new GeoElement[1];
        output[0] = cs;
          
        setEfficientDependencies(input, efficientInput);
    }
    */
    
    
    
    
    GeoPoint3D getP() {
        return (GeoPoint3D) P;
    }
    GeoPoint3D getQ() {
        return (GeoPoint3D) Q;
    }
    
    GeoCoordSys1D getCS(){
    	return cs;
    }
    

    // recalc the segment joining P and Q    
    protected void compute() {
    	    	   
		if (polygon==null)
			cs.setCoord((GeoPoint3D) P, (GeoPoint3D) Q);
		else{ //P and Q are two GeoPoints in cs2D coord sys
			if (polygon.isDefined())
				cs.setCoordFromPoints(
						polygon.getCoordSys().getPoint(((GeoPoint) P).inhomX, ((GeoPoint) P).inhomY),
						polygon.getCoordSys().getPoint(((GeoPoint) Q).inhomX, ((GeoPoint) Q).inhomY));
			else
				cs.setUndefined();
		}

    }
    
    
    public void remove() {
        super.remove();
        if (polygon != null)
            polygon.remove();
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
    		sb.append(app.getPlain("SegmentAB",((GeoElement) P).getLabel(),((GeoElement) Q).getLabel()));
    		break;
    	case GeoElement3D.GEO_CLASS_LINE3D:
    		sb.append(app.getPlain("LineThroughAB",((GeoElement) P).getLabel(),((GeoElement) Q).getLabel()));
    		break;
    	}	

        return sb.toString();
    }   
  
 

}

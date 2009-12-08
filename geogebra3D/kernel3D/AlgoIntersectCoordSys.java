/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import geogebra.kernel.Kernel;
import geogebra3D.Matrix.Ggb3DMatrixUtil;
import geogebra3D.Matrix.Ggb3DVector;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectCoordSys extends AlgoElement3D {

	
	//inputs
	/** first coord sys */
	private GeoCoordSys cs1;
	/** second coord sys */
	private GeoCoordSys cs2;
	
	//output
	/** point intersection */
	private GeoPoint3D p;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectCoordSys(Construction cons, String label, GeoCoordSys cs1, GeoCoordSys cs2) {

    	this(cons,cs1,cs2);
    	p.setLabel(label);
 
    }
    
 

    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     * */
    AlgoIntersectCoordSys(Construction cons, 
    		GeoCoordSys cs1, GeoCoordSys cs2) {
    	super(cons);


    	this.cs1 = cs1;
    	this.cs2 = cs2;
    	
    	p = new GeoPoint3D(cons);
  
    	setInputOutput(new GeoElement[] {cs1,cs2}, new GeoElement[] {p});
 
    }       



    
    
    
    
    /**
     * return the first coord sys
     * @return the first coord sys
     */
    GeoCoordSys getCS1() {
        return cs1;
    }
    
    /**
     * return the second coord sys
     * @return the second coord sys
     */   
    GeoCoordSys getCS2() {
        return cs2;
    }
    
    
    /**
     * return the point
     * @return the point
     */   
    GeoPoint3D getPoint() {
        return p;
    }
   
    
    
    
    ///////////////////////////////////////////////
    // COMPUTE

    protected void compute() {
    	    
    	if (!cs1.isDefined() || !cs2.isDefined()){
    		p.setUndefined();
    		return;
    	}
    		

    	if (cs1 instanceof GeoCoordSys1D){
    		if (cs2 instanceof GeoCoordSys1D)
    			compute1D1D((GeoCoordSys1D) cs1,(GeoCoordSys1D) cs2);
    		else if (cs2 instanceof GeoPlane3D)
    			computeLinePlane(cs1,cs2);
    	}else if (cs1 instanceof GeoPlane3D){
    		if (cs2 instanceof GeoCoordSys1D)
    			computeLinePlane(cs2,cs1);
    	}
     	


    }
    
    
    /** compute the intersection of the line on the plane
     * @param line the line
     * @param plane the plane
     */
    private void computeLinePlane(GeoCoordSys line, GeoCoordSys plane){
    	Ggb3DVector v = Ggb3DMatrixUtil.intersectLinePlane(line.getMatrix(),plane.getMatrix4x4());
    	
    	if (v==null)
    		p.setUndefined(); //TODO infinite point
    	else
    		p.setCoords(v);

    }
    
    
    /** compute the intersection of the two lines
     * @param line1 first line
     * @param line2 second line
     */
    private void compute1D1D(GeoCoordSys1D line1, GeoCoordSys1D line2){
    	
    	Ggb3DVector[] project = Ggb3DMatrixUtil.nearestPointsFromTwoLines(line1.getMatrix(), line2.getMatrix());
    	
    	if (project==null)
    		p.setUndefined(); //TODO infinite point
    	else if (project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){
    		if (line1.isValidCoord(project[2].get(1)) && line2.isValidCoord(project[2].get(2)))
    			p.setCoords(project[0]);
    		else
    			p.setUndefined();
    	}
    	else
    		p.setUndefined();
    	
    }
    
    
    /** set the coords of p regarding project coords.
     * (see {@link AlgoIntersectCoordSys#compute1D1D(GeoCoordSys, GeoCoordSys)})
     * @param project coordinates of p
     */
    private void setCoordsLineLine(Ggb3DVector[] project){
    	p.setCoords(project[0]);
    }
    
    
    
    
    
 
    
    
    
    

	protected String getClassName() {
    	
    	return "AlgoIntersectCoordSys";
	}

	
	
	
    final public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(app.getPlain("Intersection",cs1.getLabel(),cs2.getLabel()));
        
        return sb.toString();
    }   
  
 

}

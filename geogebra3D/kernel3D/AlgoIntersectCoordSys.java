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

import geogebra.Matrix.GgbMatrixUtil;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;




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
	private GeoCoordSysAbstract cs1;
	/** second coord sys */
	private GeoCoordSysAbstract cs2;
	
	//output
	/** point intersection */
	private GeoPoint3D p;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectCoordSys(Construction cons, String label, GeoCoordSysAbstract cs1, GeoCoordSysAbstract cs2) {

    	this(cons,cs1,cs2);
    	p.setLabel(label);
 
    }
    
 

    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     * */
    AlgoIntersectCoordSys(Construction cons, 
    		GeoCoordSysAbstract cs1, GeoCoordSysAbstract cs2) {
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
    GeoCoordSysAbstract getCS1() {
        return cs1;
    }
    
    /**
     * return the second coord sys
     * @return the second coord sys
     */   
    GeoCoordSysAbstract getCS2() {
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
    			computeLinePlane((GeoCoordSys1D) cs1, (GeoCoordSys2D) cs2);
    	}else if (cs1 instanceof GeoPlane3D){
    		if (cs2 instanceof GeoCoordSys1D)
    			computeLinePlane((GeoCoordSys1D) cs2, (GeoCoordSys2D) cs1);
    	}
     	


    }
    
    
    /** compute the intersection of the line on the plane
     * @param line the line
     * @param plane the plane
     */
    private void computeLinePlane(GeoCoordSys1D line, GeoCoordSys2D plane){
    	GgbVector[] project = GgbMatrixUtil.intersectLinePlane(line.getMatrix(),plane.getMatrix4x4());
    	
    	//check if the point is in the line (segment or half-line)
 		if (line.isValidCoord(-project[1].get(3))){
			p.setCoords(project[0]);
		}else
			p.setUndefined();
    	


    }
    
    
    /** compute the intersection of the two lines
     * @param line1 first line
     * @param line2 second line
     */
    private void compute1D1D(GeoCoordSys1D line1, GeoCoordSys1D line2){
    	
    	GgbVector[] project = GgbMatrixUtil.nearestPointsFromTwoLines(line1.getMatrix(), line2.getMatrix());
    	
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
     * (see {@link AlgoIntersectCoordSys#compute1D1D(GeoCoordSysAbstract, GeoCoordSysAbstract)})
     * @param project coordinates of p
     */
    private void setCoordsLineLine(GgbVector[] project){
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

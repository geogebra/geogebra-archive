/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;


/**
 * Compute a circle with point and radius (missing direction)
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoCircle3DPointAxisRadius extends AlgoElement3D {

 
	private GeoPointND point; // input
	private NumberValue radius; // input
	private GeoElement forAxis; // input
	private GeoConic3D circle; // output       
    private CoordSys coordsys;

    /**
     * 
     * @param cons
     * @param label
     * @param point
     * @param forAxis
     * @param radius
     */
    public AlgoCircle3DPointAxisRadius(Construction cons, String label, GeoPointND point, NumberValue radius, GeoElement forAxis) {
        super(cons);
        
        this.point = point;
        this.forAxis = forAxis;
        this.radius=radius;
        circle = new GeoConic3D(cons);
        coordsys = new CoordSys(2);
		circle.setCoordSys(coordsys);
		
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) radius, (GeoElement) forAxis}, new GeoElement[] {circle});

        // compute line 
        compute();
        circle.setLabel(label);
    }
    
    /**
     * 
     * @return center
     */
    protected GeoPointND getPoint(){
    	return point;
    }
    
    /**
     * 
     * @return input used for axis of the circle
     */
    protected GeoElement getForAxis(){
    	return forAxis;
    }

    /**
     * 
     * @return radius
     */
    protected NumberValue getRadius(){
    	return radius;
    }

    /**
     * 
     * @return the circle
     */
    public GeoConic3D getCircle() {
        return circle;
    }
    

    /**
     * 
     * @return direction of axis
     */
    protected abstract Coords getDirection();
    
    protected final void compute() {
    	
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
    	coordsys.addPoint(point.getCoordsInD(3));
    	Coords[] v = getDirection().completeOrthonormal();
		coordsys.addVector(v[0]);
		coordsys.addVector(v[1]);
		
		coordsys.makeOrthoMatrix(false,false);
		
    	
		//set the circle
    	circle.setSphereND(new Coords(0,0), radius.getDouble());

    }

    public String getClassName() {
        return "AlgoCirclePointAxisRadius";
    }

    
}

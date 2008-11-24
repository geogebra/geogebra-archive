/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;




import geogebra.kernel.AlgoPointOnPath;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.Application3D;
import geogebra3D.kernel3D.commands.AlgebraProcessor3D;




/**
 *
 * @author  ggb3D
 * @version 
 */




public class Kernel3D
	extends Kernel{
	
	protected Application3D app3D;
	
	public Kernel3D(Application3D app) {
		
		super(app);
		this.app3D = app;
		
	}
	
	public Application3D getApplication3D(){
		return app3D;
	}
	
	/**
	 * Returns this kernel's algebra processor that handles
	 * all input and commands.
	 */		
	public AlgebraProcessor getAlgebraProcessor() {
    	if (algProcessor == null)
    		algProcessor = new AlgebraProcessor3D(this);
    	return algProcessor;
    }
	
	

	

	
	/***********************************
	 * FACTORY METHODS FOR GeoElements3D
	 ***********************************/

	/** Point3D label with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3D(String label, double x, double y, double z) {
		GeoPoint3D p = new GeoPoint3D(cons);
		p.setCoords(x, y, z, 1.0);
		p.setLabel(label); // invokes add()                
		return p;
	}
	
	
	
	/** Point3D on a 1D path with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3D(String label, PathOn path, double x, double y, double z) {
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, x, y, z);
		GeoPoint3D p = algo.getP();		
		p.setLabel(label);               
		return p;
	}	
	
	/** Point3D on a 1D path without cartesian coordinates   */
	final public GeoPoint3D Point3D(String label, PathOn path) {
		// try (0,0,0)
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, 0, 0, 0);
		GeoPoint3D p = algo.getP(); 
		
		/* TODO below
		// try (1,0,0) 
		if (!p.isDefined()) {
			p.setCoords(1,0,1);
			algo.update();
		}
		
		// try (random(),0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(),0,1);
			algo.update();
		}
		*/
		
		return p;
	}	
	
	/** Point3D on a 2D path with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3D(String label, PathIn path, double x, double y, double z) {
		AlgoPoint3DInPath algo = new AlgoPoint3DInPath(cons, label, path, x, y, z);
		GeoPoint3D p = algo.getP();		
		p.setLabel(label);               
		return p;
	}		
	
	/** Segment3D label linking points v1 and v2   */
	final public GeoSegment3D Segment3D(String label, GgbVector v1, GgbVector v2){
		GeoSegment3D s = new GeoSegment3D(cons,v1,v2);
		s.setLabel(label);
		return s;
	}
	
	/** Segment3D label linking points P1 and P2   */
	final public GeoSegment3D Segment3D(String label, GeoPoint3D P1, GeoPoint3D P2){
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2, GeoElement3D.GEO_CLASS_SEGMENT3D);
		GeoSegment3D s = (GeoSegment3D) algo.getCS();
		return s;
	}	
	
	
	/** Line3D label linking points P1 and P2   */	
	final public GeoLine3D Line3D(String label, GeoPoint3D P1, GeoPoint3D P2){
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2, GeoElement3D.GEO_CLASS_LINE3D);
		GeoLine3D l = (GeoLine3D) algo.getCS();
		return l;
	}	
	
	

	/** Triangle3D label linking points P1 and P2 and P3  */
	final public GeoTriangle3D Polygon3D(String label, GeoPoint3D[] points){
		AlgoJoinPoints3DPolygon algo = new AlgoJoinPoints3DPolygon(cons, label, points);
		GeoTriangle3D t = algo.getPoly();
		return t;
	}	
	
	
	
	/** Plane3D label linking with (o,v1,v2) coord sys   */
	final public GeoPlane3D Plane3D(String label, GgbVector o, GgbVector v1, GgbVector v2){
		GeoPlane3D p=new GeoPlane3D(cons,o,v1,v2,-2.25,2.25,-2.25,2.25);
		p.setLabel(label);
		return p;
	}	

	
	
	
	
	/** 3D element on coord sys 2D to 2D element    */	
	final public GeoElement From3Dto2D(String label, GeoElement3D geo3D, GeoCoordSys2D cs){
		Algo3Dto2D algo = new Algo3Dto2D(cons, label, geo3D, cs);
		return algo.getGeo();
	}
	
	
}
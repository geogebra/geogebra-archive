/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;




import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.KernelInterface;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.kernel3D.commands.AlgebraProcessor3D;




/**
 * 
 * Class used for (3D) calculations
 *
 * <h3> How to add a method for creating a {@link GeoElement3D} </h3>
 *
   <ul>
   <li> simply call the element's constructor
   <p>
   <code>
   final public GeoNew3D New3D(String label, ???) { <br> &nbsp;&nbsp;
       GeoNew3D ret = new GeoNew3D(cons, ???); <br> &nbsp;&nbsp;
       // stuff <br> &nbsp;&nbsp;
       ret.setLabel(label); <br> &nbsp;&nbsp;           
       return ret; <br> 
   }
   </code>
   </li>
   <li> use an {@link AlgoElement3D}
   <p>
   <code>
   final public GeoNew3D New3D(String label, ???) { <br> &nbsp;&nbsp;
     AlgoNew3D algo = new AlgoNew3D(cons, label, ???); <br> &nbsp;&nbsp;
	 return algo.getGeo(); <br> 
   }
   </code>
   </li>
   </ul>

 *
 * @author  ggb3D
 * 
 */




public class Kernel3D
	extends Kernel {
	
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
		p.setObjColor(ConstructionDefaults.colPoint);
		return p;
	}
	
	final public GeoVector3D Vector3D(String label, double x, double y, double z) {
		GeoVector3D v = new GeoVector3D(cons, x, y, z);
		v.setLabel(label); // invokes add()                
		return v;
	}
	
	
	
	/** Point3D on a 1D path with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3D(String label, Path3D path, double x, double y, double z) {
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, x, y, z);
		GeoPoint3D p = algo.getP();		
		p.setLabel(label);
		p.setObjColor(ConstructionDefaults.colPathPoint);
		return p;
	}	
	
	/** Point3D on a 1D path without cartesian coordinates   */
	final public GeoPoint3D Point3D(String label, Path3D path) {
		// try (0,0,0)
		//AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, 0, 0, 0);
		//GeoPoint3D p = algo.getP(); 
		GeoPoint3D p = Point3D(label,path,0,0,0);
			
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
	
	
	/** Segment3D label linking points v1 and v2   */
	final public GeoSegment3D Segment3D(String label, Ggb3DVector v1, Ggb3DVector v2){
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
	
	
	/** Ray3D label linking points P1 and P2   */	
	final public GeoRay3D Ray3D(String label, GeoPoint3D P1, GeoPoint3D P2){
		Application.debug("Kernel3D : Ray3D");
		//AlgoJoinPointsRay3D algo = new AlgoJoinPointsRay3D(cons, label, P1, P2);
		//GeoRay3D l = algo.getRay3D();
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2, GeoElement3D.GEO_CLASS_RAY3D);
		GeoRay3D l = (GeoRay3D) algo.getCS();
		return l;
	}	
	


	
	/** Polygon3D linking points P1, P2, ...  
	 * @param label name of the polygon
	 * @param points vertices of the polygon
	 * @return the polygon */
	//TODO final public GeoPolygon3D Polygon3D(String[] label, GeoPoint3D[] points){
    final public GeoPolygon3D Polygon3D(String label, GeoPoint3D[] points){
		
    	/*
		//creates the 2D coord sys to have 2D points
		AlgoCoordSys2D algoCS = new AlgoCoordSys2D(cons,null,points,true);
		
		//creates the 3D polygon linked to the coord sys and the 2D points
		AlgoPolygon3D algo = new AlgoPolygon3D(cons,null,algoCS);
		
		return (GeoPolygon3D) algo.getOutput()[0];
		*/
    	
    	AlgoPolygon3D algo = new AlgoPolygon3D(cons,null,points);
    	
    	return (GeoPolygon3D) algo.getOutput()[0];
		
	}	
	
	
	
	/** Plane3D label linking with (o,v1,v2) coord sys   */
	final public GeoPlane3D Plane3D(String label, Ggb3DVector o, Ggb3DVector v1, Ggb3DVector v2){
		GeoPlane3D p=new GeoPlane3D(cons,o,v1,v2,-2.25,2.25,-2.25,2.25);
		p.setLabel(label);
		return p;
	}	

	
	
	/** Sphere label linking with center o and radius r   */
	final public GeoQuadric Sphere(String label, GeoPoint3D center, GeoNumeric radius){
		AlgoSphere algo = new AlgoSphere(cons,label,center,radius);
		return algo.getQuadric();
	}	

	
	
	
	
	
	
	
	
	/** 3D element on coord sys 2D to 2D element    */	
	final public GeoElement From3Dto2D(String label, GeoElement3D geo3D, GeoCoordSys2D cs){
		Algo3Dto2D algo = new Algo3Dto2D(cons, label, geo3D, cs);
		return algo.getGeo();
	}
	
	
	
	
	
	
	
	
	
	
	/** Conic label with equation ax² + bxy + cy² + dx + ey + f = 0  */
	final public GeoConic3D Conic3D(
		String label,
		double a,
		double b,
		double c,
		double d,
		double e,
		double f,
		GeoCoordSys2D cs) {
		double[] coeffs = { a, b, c, d, e, f };
		GeoConic3D conic = new GeoConic3D(cons, label, coeffs, cs);
		return conic;
	}
	
	
	
	/** 
	 * circle with through points A, B, C
	 */
	final public GeoConic3D Circle3D(
		String label,
		GeoPoint3D A,
		GeoPoint3D B,
		GeoPoint3D C) {
		AlgoCircleThreePoints algo = new AlgoCircle3DThreePoints(cons, label, A, B, C);
		GeoConic3D circle = (GeoConic3D) algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}
	
	
}
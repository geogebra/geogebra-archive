/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;




import geogebra.io.MyXMLHandler;
import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.AlgoVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.Application3D;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.io.MyXMLHandler3D;
import geogebra3D.kernel3D.arithmetic.ExpressionNodeEvaluator3D;
import geogebra3D.kernel3D.commands.AlgebraProcessor3D;

import java.util.LinkedHashMap;
import java.util.TreeSet;




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
	
	public GeoAxis3D getXAxis3D(){
		return ((Construction3D) cons).getXAxis3D();
	}
	public GeoAxis3D getYAxis3D(){
		return ((Construction3D) cons).getYAxis3D();
	}
	public GeoAxis3D getZAxis3D(){
		return ((Construction3D) cons).getZAxis3D();
	}
	public GeoPlane3DConstant getXOYPlane(){
		return ((Construction3D) cons).getXOYPlane();
	}

	
	/**
	 * creates the 3D construction cons
	 */
	protected void newConstruction(){
		cons = new Construction3D(this);	
	}	
	
	
	/**
	 * creates a new MyXMLHandler3D
	 * @param cons construction used in MyXMLHandler constructor
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Construction cons){
		return new MyXMLHandler3D(this, cons);		
	}
	
	
	/**
	 * creates the Evaluator for ExpressionNode
	 */
	protected void newExpressionNodeEvaluator(){
		expressionNodeEvaluator = new ExpressionNodeEvaluator3D();
	}
	
	
	
	
	
	public Application3D getApplication3D(){
		return app3D;
	}
	
	/**
	 * Returns this kernel's algebra processor that handles
	 * all input and commands.
	 */		
	public AlgebraProcessor getAlgebraProcessor() {
		
		//Application.debug("hop-3d");
		
    	if (algProcessor == null)
    		algProcessor = new AlgebraProcessor3D(this);
    	return algProcessor;
    }
	
	
	
	
	
	
	
	/** return all points of the current construction */
	public TreeSet getPointSet(){
		TreeSet t3d = getConstruction().getGeoSetLabelOrder(GeoElement3D.GEO_CLASS_POINT3D);
		//TODO add super.getPointSet()
		return t3d;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
     * Creates a new GeoElement object for the given type string.
     * @param type: String as produced by GeoElement.getXMLtypeString()
     */
    public GeoElement createGeoElement(Construction cons, String type) throws MyError {    
    	
    	//Application.debug(type);
    	//Application.printStacktrace("");
    	
    	switch (type.charAt(0)) {
   		case 'p': // point, polygon
			if (type.equals("point3d")){
				Application.debug("ici");
				return new GeoPoint3D(cons);
			}
			else if (type.equals("polygon3D"))
				return new GeoPolygon3D(cons, null);
		case 's': // segment 
			if (type.equals("segment3D"))
				return new GeoSegment3D(cons, null, null);	 
			
    	}
    	

    	
    	return super.createGeoElement(cons,type);

    }

	
	/* *******************************************
	 *  Methods for MyXMLHandler
	 * ********************************************/
	public boolean handleCoords(GeoElement geo, LinkedHashMap<String, String> attrs) {
		
		/*
		Application.debug("attrs =\n"+attrs);		
		Application.debug("attrs(x) = "+attrs.get("x"));
		Application.debug("attrs(y) = "+attrs.get("y"));
		Application.debug("attrs(z) = "+attrs.get("z"));
		Application.debug("attrs(w) = "+attrs.get("w"));
		*/
		
		if (!(geo instanceof GeoVec4D)) {
			return super.handleCoords(geo, attrs);
		}
		
		
		GeoVec4D v = (GeoVec4D) geo;
		//Application.debug("GeoVec4D : "+v.getLabel()+", type = "+geo.getGeoClassType());
		

		try {
			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			double z = Double.parseDouble((String) attrs.get("z"));
			double w = Double.parseDouble((String) attrs.get("w"));
			((GeoVec4D) geo).setCoords(x, y, z, w);
			//Application.debug(geo.getLabel()+": x="+x+", y="+y+", z="+z+", w="+w);
			return true;
		} catch (Exception e) {
			//Application.debug("erreur : "+e);
			return false;
		}
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
	
	/** Point dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. P = (4t, 2s)
	 */
	final public GeoPoint3D DependentPoint3D(
			String label,
			ExpressionNode root) {
			AlgoDependentPoint3D algo = new AlgoDependentPoint3D(cons, label, root);
			GeoPoint3D P = algo.getPoint3D();
			return P;
		}

	final public GeoVector3D DependentVector3D(
			String label,
			ExpressionNode root) {
			AlgoDependentVector3D algo = new AlgoDependentVector3D(cons, label, root);
			GeoVector3D P = algo.getVector3D();
			return P;
		}

	final public GeoVector3D Vector3D(String label, double x, double y, double z) {
		GeoVector3D v = new GeoVector3D(cons, x, y, z);
		v.setLabel(label); // invokes add()                
		return v;
	}
	
	/** 
	 * Vector named label from Point P to Q
	 */
	final public GeoVector3D Vector3D(
		String label,
		GeoPoint3D P,
		GeoPoint3D Q) {
		AlgoVector3D algo = new AlgoVector3D(cons, label, P, Q);
		GeoVector3D v = (GeoVector3D) algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		notifyUpdate(v);
		return v;
	}
	
	
	/** Point in region with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3DIn(String label, Region region, double x, double y, double z) {
		//Application.debug("Point3DIn - \n x="+x+"\n y="+y+"\n z="+z);
		AlgoPoint3DInRegion algo = new AlgoPoint3DInRegion(cons, label, region, x, y, z);
		GeoPoint3D p = algo.getP();    
		return p;
	}
	
	/** Point in region */
	final public GeoPoint3D Point3DIn(String label, Region region) {  
		return Point3DIn(label,region,0,0,0); //TODO do as for paths
	}	
	
	
	
	
	/** Point3D on a 1D path with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3D(String label, Path path, double x, double y, double z) {
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, x, y, z);
		GeoPoint3D p = algo.getP();		
		//p.setLabel(label);
		//p.setObjColor(ConstructionDefaults.colPathPoint);
		return p;
	}	
	
	/** Point3D on a 1D path without cartesian coordinates   */
	final public GeoPoint3D Point3D(String label, Path path) {
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
	/*
	final public GeoSegment3D Segment3D(String label, Ggb3DVector v1, Ggb3DVector v2){
		GeoSegment3D s = new GeoSegment3D(cons,v1,v2);
		s.setLabel(label);
		return s;
	}
	*/
	
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
    final public GeoElement [] Polygon3D(String[] label, GeoPoint3D[] points){
		
    	
    	AlgoPolygon3D algo = new AlgoPolygon3D(cons,label,points,null);
    	
    	return algo.getOutput();
		
	}	
	
    /** Polyhedron with vertices and faces description
     * @param label name
     * @param points vertices
     * @param faces faces description
     * @return the polyhedron
     */
    final public GeoElement [] Polyhedron(String label, GeoPoint3D[] points, int[][] faces){
		
    	
    	AlgoPolyhedron algo = new AlgoPolyhedron(cons,null,points,faces);
    	
    	return algo.getOutput();
		
	}	
    
    /** Polyhedron "pseudo-prismatic" with vertices 
     * @param label name
     * @param points vertices
     * @return the polyhedron
     */
    final public GeoElement [] Polyhedron(String label, GeoPoint3D[] points){
		
    	
    	AlgoPolyhedron algo = new AlgoPolyhedron(cons,null,points,GeoPolyhedron.TYPE_PSEUDO_PRISM);
    	
    	return algo.getOutput();
		
	}	
    
    /** Polyhedron with vertices (last one as apex)
     * @param label name
     * @param points vertices
     * @return the polyhedron
     */
    final public GeoElement [] Pyramid(String label, GeoPoint3D[] points){
		    	
    	AlgoPolyhedron algo = new AlgoPolyhedron(cons,null,points, GeoPolyhedron.TYPE_PYRAMID);
    	
    	return algo.getOutput();
		
	}
	
	/** Plane3D label linking with (o,v1,v2) coord sys   */
	final public GeoPlane3D Plane3D(String label, Ggb3DVector o, Ggb3DVector v1, Ggb3DVector v2){
		GeoPlane3D p=new GeoPlane3D(cons,o,v1,v2,-2.25,2.25,-2.25,2.25);
		p.setLabel(label);
		return p;
	}	

	/** Axis3D label linking with (o,v) coord sys   */
	/*
	final public GeoAxis3D Axis3D(String label, Ggb3DVector o, Ggb3DVector v){
		GeoAxis3D a=new GeoAxis3D(cons,o,v);
		a.setLabel(label);
		return a;
	}	
	*/
	
	
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
	 * circle through points A, B, C
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
	
	
	
	/** 
	 * plane through points A, B, C
	 */
	final public GeoPlane3D Plane3D(
		String label,
		GeoPoint3D A,
		GeoPoint3D B,
		GeoPoint3D C) {
		AlgoPlane algo = new AlgoPlane(cons, label, A,B,C);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}
	
	
	
	
	////////////////////////////////////////////////
	// INTERSECTION (POINTS)
	
	
	/** Calculate the intersection of two coord sys (eg line and plane).
	 * @param label name of the point
	 * @param cs1 first coord sys
	 * @param cs2 second coord sys
	 * @return point intersection
	 */
	final public GeoPoint3D Intersect(
			String label,
			GeoCoordSys cs1,
			GeoCoordSys cs2) {
		
		AlgoIntersectCoordSys algo = new AlgoIntersectCoordSys(cons,label,cs1,cs2);
		GeoPoint3D p = algo.getPoint();
		return p;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
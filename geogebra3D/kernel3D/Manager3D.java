package geogebra3D.kernel3D;

import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.AlgoLinePointLine;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.Manager3DInterface;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;

/**
 * Class that for manage all 3D methods in Kernel.
 * 
 * @author mathieu
 *
 */
public class Manager3D implements Manager3DInterface {

	private Kernel kernel;
	private Construction cons;

	/**
	 * @param kernel
	 */
	public Manager3D(Kernel kernel){
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
	}


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
			GeoPointND P,
			GeoPointND Q) {
		AlgoVector3D algo = new AlgoVector3D(cons, label, P, Q);
		GeoVector3D v = (GeoVector3D) algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		kernel.notifyUpdate(v);
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
	final public GeoSegment3D Segment3D(String label, GeoPointND P1, GeoPointND P2){
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2, GeoElement3D.GEO_CLASS_SEGMENT3D);
		GeoSegment3D s = (GeoSegment3D) algo.getCS();
		return s;
	}	


	/** Line3D label linking points P1 and P2   */	
	final public GeoLine3D Line3D(String label, GeoPointND P1, GeoPointND P2){
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2, GeoElement3D.GEO_CLASS_LINE3D);
		GeoLine3D l = (GeoLine3D) algo.getCS();
		return l;
	}	
	
	final public GeoLineND Line3D(String label, GeoPointND P, GeoLineND l) {
		AlgoLinePointLine3D algo = new AlgoLinePointLine3D(cons, label, P, l);
		GeoLineND g = algo.getLine();
		return g;
	}

	final public GeoLineND Line3D(String label, GeoPointND P, GeoVectorND v) {
		AlgoLinePointVector3D algo = new AlgoLinePointVector3D(cons, label, P, v);
		GeoLineND g = algo.getLine();
		return g;
	}

	/** Ray3D label linking points P1 and P2   */	
	final public GeoRay3D Ray3D(String label, GeoPointND P1, GeoPointND P2){
		//Application.debug("Kernel3D : Ray3D");
		//AlgoJoinPointsRay3D algo = new AlgoJoinPointsRay3D(cons, label, P1, P2);
		//GeoRay3D l = algo.getRay3D();
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, label, P1, P2, GeoElement3D.GEO_CLASS_RAY3D);
		GeoRay3D l = (GeoRay3D) algo.getCS();
		return l;
	}	

	 public GeoLineND OrthogonalLine3D(String label, GeoPointND point, GeoCoordSys2D cs){
		 AlgoOrthoLinePointPlane algo = new AlgoOrthoLinePointPlane(cons, label, point, cs);
		 return algo.getLine();
	 }

	 public GeoLineND OrthogonalLine3D(String label, GeoPointND point, GeoLineND line){
		 AlgoOrthoLinePointLine3D algo = new AlgoOrthoLinePointLine3D(cons, label, point, line);
		 return algo.getLine();
	 }
	 
	 public GeoVectorND OrthogonalVector3D(String label, GeoCoordSys2D plane){
		 AlgoOrthoVectorPlane algo = new AlgoOrthoVectorPlane(cons, label, plane);
		 return algo.getVector();
	 }
	 


	/** Polygon3D linking points P1, P2, ...  
	 * @param label name of the polygon
	 * @param points vertices of the polygon
	 * @return the polygon */
	final public GeoElement [] Polygon3D(String[] label, GeoPointND[] points){


		AlgoPolygon3D algo = new AlgoPolygon3D(cons,label,points,null);

		return algo.getOutput();

	}	

	/** Polyhedron with vertices and faces description
	 * @param label name
	 * @param faces faces description
	 * @return the polyhedron
	 */
	final public GeoElement[] Polyhedron(String[] labels, GeoList faces){


		AlgoPolyhedron algo = new AlgoPolyhedron(cons,labels,faces);

		return algo.getOutput();

	}	

	/** Prism with vertices (last one is first vertex of second parallel face)
	 * @param label name
	 * @param points vertices
	 * @return the polyhedron
	 */
	final public GeoElement [] Prism(String[] labels, GeoPointND[] points){


		AlgoPolyhedron algo = new AlgoPolyhedron(cons,labels,points,GeoPolyhedron.TYPE_PRISM);

		return algo.getOutput();

	}	
	
	
	 final public GeoElement [] Prism(String[] labels, GeoPolygon polygon, GeoPointND point){
		 
		AlgoPolyhedron algo = new AlgoPolyhedron(cons,labels,polygon,point,GeoPolyhedron.TYPE_PRISM);

		return algo.getOutput();

	 }

	 final public GeoElement [] Prism(String[] labels, GeoPolygon polygon, NumberValue height){

		 AlgoPolyhedron algo = new AlgoPolyhedron(cons,labels,polygon,height,GeoPolyhedron.TYPE_PRISM);

		 return algo.getOutput();

	 }

	/** Pyramid with vertices (last one as apex)
	 * @param label name
	 * @param points vertices
	 * @return the polyhedron
	 */
	final public GeoElement [] Pyramid(String[] labels, GeoPointND[] points){

		AlgoPolyhedron algo = new AlgoPolyhedron(cons,labels, points, GeoPolyhedron.TYPE_PYRAMID);

		return algo.getOutput();

	}



	/** Line a x + b y + c z + d = 0 named label */
	final public GeoPlane3D Plane3D(
			String label,
			double a,
			double b,
			double c,
			double d) {
		GeoPlane3D plane = new GeoPlane3D(cons, label, a, b, c, d);
		return plane;
	}


	/** Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 */
	final public GeoPlane3D DependentPlane3D(String label, Equation equ) {
		AlgoDependentPlane3D algo = new AlgoDependentPlane3D(cons, label, equ);
		return algo.getPlane();
	}


	final public GeoPlane3D Plane3D(String label, GeoPointND point, GeoLineND line) {
		AlgoPlaneThroughPointAndLine algo = new AlgoPlaneThroughPointAndLine(cons, label, point, line);
		return algo.getPlane();
	}

	final public GeoPlane3D Plane3D(String label, GeoPointND point, GeoCoordSys2D cs) {
		AlgoPlaneThroughPointAndPlane algo = new AlgoPlaneThroughPointAndPlane(cons, label, point, (GeoCoordSys2D) cs);
		return algo.getPlane();
	}

	/** 
	 * Line named label through Point P orthogonal to line l
	 */
	final public GeoPlane3D OrthogonalPlane3D(
			String label,
			GeoPointND point,
			GeoLineND line) {

		return new AlgoOrthoPlanePoint(cons, label, point, line).getPlane();
	}









	/** Sphere label linking with center o and radius r   */
	final public GeoQuadric3D Sphere(
			String label, 
			GeoPointND M, 
			NumberValue r){
		AlgoSpherePointRadius algo = new AlgoSpherePointRadius(cons, label, M, r);
		return algo.getSphere();
	}	

	/** 
	 * Sphere with midpoint M through point P
	 */
	final public GeoQuadric3D Sphere(String label, GeoPointND M, GeoPointND P) {
		AlgoSphereTwoPoints algo = new AlgoSphereTwoPoints(cons, label, M, P);
		return algo.getSphere();
	}


	/** 
	 * Cone
	 */
	final public GeoQuadric3D Cone(String label, GeoPointND origin, GeoVectorND direction, NumberValue angle) {
		AlgoConePointVectorAngle algo = new AlgoConePointVectorAngle(cons, label, origin, direction, angle);
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cone(String label, GeoPointND origin, GeoPointND secondPoint, NumberValue angle) {
		AlgoConePointPointAngle algo = new AlgoConePointPointAngle(cons, label, origin, secondPoint, angle);
		return algo.getQuadric();
	}
	
	final public GeoElement Cone(String label, GeoPointND origin, GeoLineND axis, NumberValue angle){
		AlgoConePointLineAngle algo = new AlgoConePointLineAngle(cons, label, origin, axis, angle);
		return algo.getQuadric();
	}
	
	/** 
	 * Cylinder
	 */
	final public GeoQuadric3D Cylinder(String label, GeoPointND origin, GeoVectorND direction, NumberValue r) {
		AlgoCylinderPointVectorRadius algo = new AlgoCylinderPointVectorRadius(cons, label, origin, direction, r);
		return algo.getQuadric();
	}


	final public GeoQuadric3D Cylinder(String label, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		AlgoCylinderPointPointRadius algo = new AlgoCylinderPointPointRadius(cons, label, origin, secondPoint, r);
		return algo.getQuadric();
	}


	final public GeoQuadric3D Cylinder(String label, GeoLineND axis, NumberValue r) {
		AlgoCylinderAxisRadius algo = new AlgoCylinderAxisRadius(cons, label, axis, r);
		return algo.getQuadric();
	}










	/** 
	 * circle through points A, B, C
	 */
	final public GeoConic3D Circle3D(
			String label,
			GeoPointND A,
			GeoPointND B,
			GeoPointND C) {
		AlgoCircleThreePoints algo = new AlgoCircle3DThreePoints(cons, label, A, B, C);
		GeoConic3D circle = (GeoConic3D) algo.getCircle();
		//circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;
	}

	public GeoConic3D Circle3D(
			String label,
			GeoLineND axis,
			GeoPointND A) {
		AlgoCircle3DAxisPoint algo = new AlgoCircle3DAxisPoint(cons, label, axis, A);
		GeoConic3D circle = algo.getCircle();
		//circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;
	}

	/** 
	 * plane through points A, B, C
	 */
	final public GeoPlane3D Plane3D(
			String label,
			GeoPointND A,
			GeoPointND B,
			GeoPointND C) {
		AlgoPlane algo = new AlgoPlane(cons, label, A, B, C);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}




	////////////////////////////////////////////////
	// INTERSECTION (POINTS)


	/** Calculate the intersection of two coord sys (eg line and plane)
	 * or the intersection of two 2D coord sys (eg two planes).
	 * 
	 * @param label name of the point
	 * @param cs1 first coord sys
	 * @param cs2 second coord sys
	 * @return point intersection
	 */
	final public GeoElement Intersect(
			String label,
			GeoElement cs1,
			GeoElement cs2) {

		AlgoIntersectCoordSys algo = null;

		if (cs1 instanceof GeoLineND){
			if (cs2 instanceof GeoLineND)
				algo = new AlgoIntersectCS1D1D(cons,label,
						(GeoLineND) cs1,(GeoLineND) cs2);
			else if (cs2 instanceof GeoCoordSys2D)
				algo = new AlgoIntersectCS1D2D(cons,label, 
						 cs1, cs2);
		}else if (cs1 instanceof GeoCoordSys2D){
			if (cs2 instanceof GeoLineND)
				algo = new AlgoIntersectCS1D2D(cons,label, 
						cs1, cs2);
			else
				algo = new AlgoIntersectCS2D2D(cons,label, (GeoCoordSys2D) cs1, (GeoCoordSys2D) cs2);
		}

		return algo.getIntersection();
	}


	
	public GeoConic3D Intersect(
			 String label,
			 GeoPlaneND plane,
			 GeoQuadricND quadric){
		
		
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons, label, (GeoPlane3D) plane, (GeoQuadric3D) quadric);
		
		return algo.getConic();
	}
			
	////////////////////////////////////////////////
	// FUNCTIONS (2 VARS)

	final public GeoFunctionNVar Function2Var(
			String label, 
			NumberValue zcoord, 
			GeoNumeric localVarU, NumberValue Ufrom, NumberValue Uto, 
			GeoNumeric localVarV, NumberValue Vfrom, NumberValue Vto 
	){

		AlgoFunctionNVarND algo = new AlgoFunctionNVarND(cons, label, 
				new NumberValue[] {zcoord}, 
				new GeoNumeric[] {localVarU, localVarV},
				new NumberValue[] {Ufrom, Vfrom}, 
				new NumberValue[] {Uto, Vto}
		);


		return algo.getFunction();

	}

	final public GeoFunctionNVar Function2Var(
			String label, 
			GeoFunctionNVar f, 
			NumberValue xFrom, NumberValue xTo, 
			NumberValue yFrom, NumberValue yTo 		
	){

		AlgoFunctionNVarND algo = new AlgoFunctionNVarND(cons, label, 
				f, 
				new NumberValue[] {xFrom, yFrom}, 
				new NumberValue[] {xTo, yTo});


		return algo.getFunction();

	}


	////////////////////////////////////////////////
	// 3D CURVE (2 VARS)

	/** 
	 * 3D Cartesian curve command:
	 * Curve[ <expression x-coord>, <expression y-coord>,  <expression z-coord>, <number-var>, <from>, <to> ]  
	 */
	final public GeoCurveCartesian3D CurveCartesian3D(String label, 
			NumberValue xcoord, NumberValue ycoord, NumberValue zcoord, 
			GeoNumeric localVar, NumberValue from, NumberValue to) 
	{									
		AlgoCurveCartesian3D algo = new AlgoCurveCartesian3D(cons, label, 
				new NumberValue[] {xcoord, ycoord, zcoord} , localVar, from, to);
		return (GeoCurveCartesian3D) algo.getCurve();		
	}	




}

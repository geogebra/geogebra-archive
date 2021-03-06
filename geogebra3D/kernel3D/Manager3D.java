package geogebra3D.kernel3D;


import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSurfaceFinite;
import geogebra.kernel.Kernel;
import geogebra.kernel.Manager3DInterface;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;

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
	final public GeoPoint3D Point3DIn(String label, Region region, Coords coords, boolean addToConstruction) {
		boolean oldMacroMode = false;
		
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		//Application.debug("Point3DIn - \n x="+x+"\n y="+y+"\n z="+z);
		AlgoPoint3DInRegion algo = new AlgoPoint3DInRegion(cons, label, region, coords);
		GeoPoint3D p = algo.getP();    
		
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}
	
	
	/** Point in region with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3DIn(Region region, Coords coords) {
		AlgoPoint3DInRegion algo = new AlgoPoint3DInRegion(cons, region, coords);
		GeoPoint3D p = algo.getP();    
		return p;
	}

	/** Point in region */
	final public GeoPoint3D Point3DIn(String label, Region region) {  
		return Point3DIn(label,region, null, true); 
	}	




	/** Point3D on a 1D path with cartesian coordinates (x,y,z)   */
	final public GeoPoint3D Point3D(String label, Path path, double x, double y, double z, boolean addToConstruction) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, x, y, z);
		GeoPoint3D p = algo.getP();	
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}	

	/** Point3D on a 1D path without cartesian coordinates   */
	final public GeoPoint3D Point3D(String label, Path path) {
		// try (0,0,0)
		//AlgoPoint3DOnPath algo = new AlgoPoint3DOnPath(cons, label, path, 0, 0, 0);
		//GeoPoint3D p = algo.getP(); 
		GeoPoint3D p = Point3D(label,path,0,0,0,true);

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
	
	/** 
	 * Midpoint M = (P + Q)/2
	 */
	final public GeoPoint3D Midpoint(
		String label,
		GeoPointND P,
		GeoPointND Q) {
		AlgoMidpoint3D algo = new AlgoMidpoint3D(cons, label, P, Q);
		GeoPoint3D M = algo.getPoint();
		return M;
	}
	
	public GeoPointND Midpoint(
			 String label,
			 GeoSegmentND segment){
		
		AlgoMidpoint3D algo = new AlgoMidpointSegment3D(cons, label, segment);
		GeoPoint3D M = algo.getPoint();
		return M;
		
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
	 
	 public GeoLineND OrthogonalLine3D(String label, GeoPointND point, GeoLineND line, GeoDirectionND direction){
		 AlgoOrthoLineLinePointPlane algo = new AlgoOrthoLineLinePointPlane(cons, label, point, line, direction);
		 return algo.getLine();
	 }
	 
	 
	 public GeoLineND OrthogonalLine3D(String label, GeoLineND line1, GeoLineND line2){
		 AlgoOrthoLineLineLine algo = new AlgoOrthoLineLineLine(cons, label, line1, line2);
		 return algo.getLine();
	 } 
	 
	 public GeoVectorND OrthogonalVector3D(String label, GeoCoordSys2D plane){
		 AlgoOrthoVectorPlane algo = new AlgoOrthoVectorPlane(cons, label, plane);
		 return algo.getVector();
	 }
	 
	 public GeoVectorND UnitOrthogonalVector3D(String label, GeoCoordSys2D plane){
		 AlgoUnitOrthoVectorPlane algo = new AlgoUnitOrthoVectorPlane(cons, label, plane);
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
	
	final public GeoElement [] Polygon3D(String[] label, GeoPointND[] points, GeoDirectionND direction){
		AlgoPolygon algo = new AlgoPolygon3DDirection(cons,label,points,direction);

		return algo.getOutput();

	}


	final public GeoElement [] PolyLine3D(String [] labels, GeoPointND [] P) {
		AlgoPolyLine3D algo = new AlgoPolyLine3D(cons, labels, P);
		return algo.getOutput();
	}
	
	final public GeoElement [] PolyLine3D(String [] labels, GeoList pointList) {
		AlgoPolyLine3D algo = new AlgoPolyLine3D(cons, labels, pointList);
		return algo.getOutput();
	}
	

	/** Prism with vertices (last one is first vertex of second parallel face)
	 * @param label name
	 * @param points vertices
	 * @return the polyhedron
	 */
	final public GeoElement [] Prism(String[] labels, GeoPointND[] points){


		AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,labels,points);

		return algo.getOutput();

	}	
	
	
	 final public GeoElement [] Prism(String[] labels, GeoPolygon polygon, GeoPointND point){
		 
		 AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,labels,polygon,point);

		return algo.getOutput();

	 }

	 final public GeoElement [] Prism(String[] labels, GeoPolygon polygon, NumberValue height){

		 AlgoPolyhedronPointsPrism algo = new AlgoPolyhedronPointsPrism(cons,labels,polygon,height);

		 return algo.getOutput();

	 }

	/** Pyramid with vertices (last one as apex)
	 * @param label name
	 * @param points vertices
	 * @return the polyhedron
	 */
	final public GeoElement [] Pyramid(String[] labels, GeoPointND[] points){

		AlgoPolyhedronPointsPyramid algo = new AlgoPolyhedronPointsPyramid(cons,labels, points);

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
	 * Plane named label through Point P orthogonal to line l
	 */
	final public GeoPlane3D OrthogonalPlane3D(
			String label,
			GeoPointND point,
			GeoLineND line) {

		return new AlgoOrthoPlanePointLine(cons, label, point, line).getPlane();
	}

	/** 
	 * Plane named label through Point P orthogonal to line l
	 */
	final public GeoPlane3D OrthogonalPlane3D(
			String label,
			GeoPointND point,
			GeoVectorND vector) {

		return new AlgoOrthoPlanePointVector(cons, label, point, vector).getPlane();
	}


	
	final public GeoPlane3D PlaneBisector(
			String label,
			GeoPointND point1,
			GeoPointND point2) {

		return new AlgoOrthoPlaneBisectorPointPoint(cons, label, point1, point2).getPlane();
	}


	final public GeoPlane3D PlaneBisector(
			String label,
			GeoSegmentND segment) {

		return new AlgoOrthoPlaneBisectorSegment(cons, label, segment).getPlane();
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
		AlgoQuadric algo = new AlgoQuadricPointVectorNumber(cons, label, origin, direction, angle, new AlgoQuadricComputerCone());
		return algo.getQuadric();
	}

	final public GeoQuadric3D Cone(String label, GeoPointND origin, GeoPointND secondPoint, NumberValue angle) {
		AlgoQuadric algo = new AlgoQuadricPointPointNumber(cons, label, origin, secondPoint, angle, new AlgoQuadricComputerCone());
		return algo.getQuadric();
	}
	
	final public GeoQuadric3D Cone(String label, GeoPointND origin, GeoLineND axis, NumberValue angle){
		AlgoConePointLineAngle algo = new AlgoConePointLineAngle(cons, label, origin, axis, angle);
		return algo.getQuadric();
	}
	

	final public GeoElement[] ConeLimited(String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		AlgoQuadricLimitedPointPointRadius algo = new AlgoQuadricLimitedPointPointRadiusCone(cons, labels, origin, secondPoint, r);
		return algo.getOutput();
	}


	
	/** 
	 * Cylinder
	 */
	final public GeoQuadric3D Cylinder(String label, GeoPointND origin, GeoVectorND direction, NumberValue r) {
		AlgoQuadric algo = new AlgoQuadricPointVectorNumber(cons, label, origin, direction, r, new AlgoQuadricComputerCylinder());
		return algo.getQuadric();
	}


	final public GeoQuadric3D Cylinder(String label, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		AlgoQuadric algo = new AlgoQuadricPointPointNumber(cons, label, origin, secondPoint, r, new AlgoQuadricComputerCylinder());
		return algo.getQuadric();
	}


	final public GeoQuadric3D Cylinder(String label, GeoLineND axis, NumberValue r) {
		AlgoQuadric algo = new AlgoCylinderAxisRadius(cons, label, axis, r);
		return algo.getQuadric();
	}


	final public GeoElement[] CylinderLimited(String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		AlgoQuadricLimitedPointPointRadius algo = new AlgoQuadricLimitedPointPointRadiusCylinder(cons, labels, origin, secondPoint, r);
		algo.update();//ensure volume is correctly computed
		return algo.getOutput();
	}



	final public GeoQuadric3DPart  QuadricSide(String label, GeoQuadricND quadric){
		AlgoQuadric algo = new AlgoQuadricSide(cons, label, (GeoQuadric3DLimited) quadric);
		return (GeoQuadric3DPart) algo.getQuadric();
	}
	
	final public GeoConic3D  QuadricBottom(String label, GeoQuadricND quadric){
		AlgoQuadricEnd algo = new AlgoQuadricEndBottom(cons, label, (GeoQuadric3DLimited) quadric);
		return algo.getSection();
	}
	
	final public GeoConic3D  QuadricTop(String label, GeoQuadricND quadric){
		AlgoQuadricEnd algo = new AlgoQuadricEndTop(cons, label, (GeoQuadric3DLimited) quadric);
		return algo.getSection();
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

	public GeoConicND Circle3D(
			 String label,
			 GeoPointND A,
			 NumberValue radius,
			 GeoDirectionND axis){
		
		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointRadiusDirection(cons, label, A, radius, axis);
		GeoConic3D circle = algo.getCircle();
		//circle.setToSpecific();
		circle.update();
		kernel.notifyUpdate(circle);
		return circle;

		
	}

	public GeoConicND Circle3D(
			 String label,
			 GeoPointND A,
			 GeoPointND B,
			 GeoDirectionND axis){
		
		AlgoCircle3DPointDirection algo = new AlgoCircle3DPointPointDirection(cons, label, A, B, axis);
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
		AlgoPlaneThreePoints algo = new AlgoPlaneThreePoints(cons, label, A, B, C);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}


	final public GeoPlane3D Plane3D(String label,GeoCoordSys2D cs2D){
		AlgoPlaneCS2D algo = new AlgoPlaneCS2D(cons, label, cs2D);
		GeoPlane3D plane = (GeoPlane3D) algo.getCoordSys();
		return plane;
	}

	final public GeoPlane3D Plane3D(GeoCoordSys2D cs2D){
		AlgoPlaneCS2D algo = new AlgoPlaneCS2D(cons, cs2D);
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
				algo = new AlgoIntersectCS1D2D(cons,label,cs1, cs2);
		}else if (cs1 instanceof GeoCoordSys2D){
			if (cs2 instanceof GeoLineND)
				algo = new AlgoIntersectCS1D2D(cons,label, 
						cs1, cs2);
			//else
				//algo = new AlgoIntersectCS2D2D(cons,label, (GeoCoordSys2D) cs1, (GeoCoordSys2D) cs2);
		}

		return algo.getIntersection();
	}

	 public GeoElement[] IntersectionPoint(
				String[] labels,
				GeoLineND g, GeoSurfaceFinite p) {
			
		 	if (p instanceof GeoPolygon) {
		 		AlgoElement algo;
		 		//check if line g is contained in the plane of p
		 		if (AlgoIntersectCS1D2D.getConfigLinePlane(g, ((GeoCoordSys2D) p)) == AlgoIntersectCS1D2D.RESULTCATEGORY_CONTAINED)
		 			algo = new AlgoIntersectLinePolygon3D(cons, labels, g, (GeoPolygon)p);
		 		else
		 			algo = new AlgoIntersectLinePolygonalRegion3D(cons, labels, g, (GeoPolygon)p);
		 		return algo.getOutput();
		 	} else {
		 		return null;
		 	}
	 }
	 
	 public GeoElement[] IntersectionPoint(
				String[] labels,
				GeoPlaneND plane, GeoSurfaceFinite s) {
		 	
		 	if (s instanceof GeoPolygon) {
		 		AlgoIntersectPlanePolygon algo = new AlgoIntersectPlanePolygon(cons, labels, (GeoPlane3D) plane, (GeoPolygon)s);
		 		return algo.getOutput();
		 	} else {
		 		return null;
		 	}
	 }
	
	 
	public GeoElement[] IntersectionSegment(
			String[] labels,
			GeoLineND g, GeoSurfaceFinite p){
		
		AlgoIntersectLinePolygonalRegion3D algo;
		if (p instanceof GeoPolygon) {
		  algo = new AlgoIntersectLinePolygonalRegion3D(cons, labels, g, (GeoPolygon)p);
		  //Application.debug(algo);
		  return algo.getOutput();
		}
		return null;
		
	}

	
	 public GeoElement[] IntersectionSegment(
				String[] labels,
				GeoPlaneND plane, GeoSurfaceFinite s) {
		 	
		 	if (s instanceof GeoPolygon) {
		 		AlgoIntersectPlanePolygonalRegion algo = new AlgoIntersectPlanePolygonalRegion(cons, labels, (GeoPlane3D) plane, (GeoPolygon)s);
		 		return algo.getOutput();
		 	} else {
		 		return null;
		 	}
	 }
	
	public GeoConic3D Intersect(
			 String label,
			 GeoPlaneND plane,
			 GeoQuadricND quadric){
		
		
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons, label, (GeoPlane3D) plane, (GeoQuadric3D) quadric);
		
		return algo.getConic();
	}
	
	public GeoConic3D Intersect(
			 GeoPlaneND plane,
			 GeoQuadricND quadric){
		
		
		AlgoIntersectPlaneQuadric algo = new AlgoIntersectPlaneQuadric(cons, (GeoPlane3D) plane, (GeoQuadric3D) quadric);
		
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
	// 3D CURVE (1 VAR)

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

	
	 ////////////////////////////////////////////////
	 // 3D SURFACE (2 VARS)


	public GeoElement SurfaceCartesian3D(String label, 
			NumberValue xcoord, NumberValue ycoord, NumberValue zcoord, 
			GeoNumeric uVar, NumberValue uFrom, NumberValue uTo,
			GeoNumeric vVar, NumberValue vFrom, NumberValue vTo){

		AlgoSurfaceCartesian3D algo = new AlgoSurfaceCartesian3D(cons, label, 
				new NumberValue[] {xcoord, ycoord, zcoord} , 
				new GeoNumeric[] {uVar, vVar}, 
				new NumberValue[] {uFrom, vFrom},
				new NumberValue[] {uTo, vTo});
		return (GeoSurfaceCartesian3D) algo.getSurface();		

	}

	////////////////////////////////////////////////
	// intersection algos
	
	/**
	 * intersect line and conic
	 */
	private AlgoIntersectLineConic3D getIntersectionAlgorithm(GeoLineND g, GeoConicND c) {
		AlgoElement existingAlgo = kernel.findExistingIntersectionAlgorithm((GeoElement) g, c);
		if (existingAlgo != null) return (AlgoIntersectLineConic3D) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic3D algo = new AlgoIntersectLineConic3D(cons, g, c);
		algo.setPrintedInXML(false);
		kernel.addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	 }
	
	/** 
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 */
	final public GeoPoint3D[] IntersectLineConic(
		String[] labels,
		GeoLineND g,
		GeoConicND c) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();		
		GeoElement.setLabels(labels, points);	
		return points;
	}

	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW) 
	 */
	final public GeoPoint3D IntersectLineConicSingle(
		String label,
		GeoLineND g,
		GeoConicND c, double xRW, double yRW, CoordMatrix4x4 mat) {
		
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);

		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two conics
	 * choice depends on command input
	 */
	final public GeoPoint3D IntersectLineConicSingle(
			String label, GeoLineND g, GeoConicND c, NumberValue index) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);		// index - 1 to start at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, (int) index.getDouble() - 1);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two conics, near to refPoint
	 */
	public GeoPoint3D IntersectLineConicSingle(
			String label, GeoLineND g, GeoConicND c, GeoPointND refPoint) {
		AlgoIntersectLineConic3D algo = getIntersectionAlgorithm(g, c);		// index - 1 to start at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, refPoint);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	
	
	/**
	 * intersect conics
	 */
	private AlgoIntersectConics3D getIntersectionAlgorithm(GeoConicND A, GeoConicND B) {
		AlgoElement existingAlgo = kernel.findExistingIntersectionAlgorithm(A, B);
		if (existingAlgo != null) return (AlgoIntersectConics3D) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics3D algo = new AlgoIntersectConics3D(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	 }

	
	
	/** 
	 * IntersectConics3D yields intersection points named label1, label2
	 * of conics A and B
	 */
	final public GeoPoint3D[] IntersectConics(
		String[] labels,
		GeoConicND A,
		GeoConicND B) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();		
		GeoElement.setLabels(labels, points);	
		return points;
	}
	final public GeoPoint3D IntersectConicsSingle(
		String label, GeoConicND A, GeoConicND B, 
		double xRW, double yRW, CoordMatrix4x4 mat) {
		
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	final public GeoPoint3D IntersectConicsSingle(
			String label, GeoConicND A, GeoConicND B, NumberValue index) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B);		// index - 1 to start at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, (int) index.getDouble() - 1);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	final public GeoPoint3D IntersectConicsSingle(
			String label, GeoConicND A, GeoConicND B, GeoPointND refPoint) {
		AlgoIntersectConics3D algo = getIntersectionAlgorithm(A, B);		// index - 1 to start at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, refPoint);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}

	/**
	 * intersect line/quadric
	 */
	private AlgoIntersectLineQuadric3D getIntersectionAlgorithm(GeoLineND A, GeoQuadricND B) {
		AlgoElement existingAlgo = kernel.findExistingIntersectionAlgorithm((GeoElement)A, B);
		if (existingAlgo != null) return (AlgoIntersectLineQuadric3D) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineQuadric3D algo = new AlgoIntersectLineQuadric3D(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	 }
	public GeoPointND[] IntersectLineQuadric(String[] labels, GeoLineND A,
			GeoQuadricND B) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(A,(GeoQuadric3D) B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();	
		GeoElement.setLabels(labels, points);			
		return points;
	}
	/** 
	 * get only one intersection point of line and quadric
	 * choice depends on command input
	 */
	final public GeoPoint3D IntersectLineQuadricSingle(
			String label, GeoLineND g, GeoQuadricND q, NumberValue index) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q);		// index - 1 to start at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, (int) index.getDouble() - 1);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW) 
	 */
	final public GeoPoint3D IntersectLineQuadricSingle(
		String label, GeoLineND g, GeoQuadricND q, 
		double xRW, double yRW, CoordMatrix4x4 mat) {
		
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q);

		int index = algo.getClosestPointIndex(xRW, yRW, mat);
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, index);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	

	final public GeoPoint3D IntersectLineQuadricSingle(
			String label, GeoLineND g, GeoQuadricND q, GeoPointND refPoint) {
		AlgoIntersectLineQuadric3D algo = getIntersectionAlgorithm(g, q);		// index - 1 to start at 0
		AlgoIntersectSingle3D salgo = new AlgoIntersectSingle3D(label, algo, refPoint);
		GeoPoint3D point = salgo.getPoint();
		return point;
	}
	
	
	/**
	 * intersect plane/conic
	 */
	private AlgoIntersectPlaneConic getIntersectionAlgorithm(GeoCoordSys2D A, GeoConicND B) {
		AlgoElement existingAlgo = kernel.findExistingIntersectionAlgorithm((GeoElement)A, B);
		if (existingAlgo != null) return (AlgoIntersectPlaneConic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPlaneConic algo = new AlgoIntersectPlaneConic(cons, A, B);
		algo.setPrintedInXML(false);
		kernel.addIntersectionAlgorithm(algo); // remember this algorithm
		return algo;
	 }
	public GeoPointND[] IntersectPlaneConic(String[] labels, GeoCoordSys2D A,
			GeoConicND B) {
		AlgoIntersectPlaneConic algo = getIntersectionAlgorithm(A, B);
		algo.setPrintedInXML(true);
		GeoPoint3D[] points = algo.getIntersectionPoints();	
		GeoElement.setLabels(labels, points);			
		return points;
	}


	final public GeoElement IntersectPlanes(
			String label,
			GeoCoordSys2D cs1,
			GeoCoordSys2D cs2) {
		
		AlgoIntersectCS2D2D algo = new AlgoIntersectCS2D2D(cons,label, cs1, cs2);
		return algo.getIntersection();
	}
	
	final public GeoElement IntersectPlanes(
			GeoCoordSys2D cs1,
			GeoCoordSys2D cs2) {
		
		AlgoIntersectCS2D2D algo = new AlgoIntersectCS2D2D(cons, cs1, cs2);
		return algo.getIntersection();
	}
}

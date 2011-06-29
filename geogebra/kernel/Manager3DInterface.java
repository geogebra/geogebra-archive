package geogebra.kernel;

import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
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
import geogebra3D.kernel3D.AlgoPolyLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;

/**
 * Interface for managing all 3D methods in Kernel.
 * 
 * @author mathieu
 *
 */
public interface Manager3DInterface {
	

	/** Point3D label with cartesian coordinates (x,y,z)   */
	public GeoElement Point3D(String label, double x, double y, double z);
		
		/** Point dependent on arithmetic expression with variables,
		 * represented by a tree. e.g. P = (4t, 2s)
		 */
		 public GeoElement DependentPoint3D(
				String label,
				ExpressionNode root) ;
		 
		 
		 
			

			 public GeoElement DependentVector3D(
					String label,
					ExpressionNode root);

			 public GeoElement Vector3D(String label, double x, double y, double z);

			/** 
			 * Vector named label from Point P to Q
			 */
			 public GeoElement Vector3D(
					String label,
					GeoPointND P,
					GeoPointND Q) ;


			/** Point in region with cartesian coordinates (x,y,z)   */
			 public GeoPointND Point3DIn(String label, Region region, double x, double y, double z);
		
			 public GeoPointND Point3DIn(Region region, double x, double y, double z);

			/** Point in region */
			 public GeoPointND Point3DIn(String label, Region region) ;




			/** Point3D on a 1D path with cartesian coordinates (x,y,z)   */
			 public GeoPointND Point3D(String label, Path path, double x, double y, double z, boolean addToConstruction) ;

			/** Point3D on a 1D path without cartesian coordinates   */
			 public GeoPointND Point3D(String label, Path path) ;
			 


			 /** 
			  * Midpoint M = (P + Q)/2
			 * @param label 
			 * @param P 
			 * @param Q 
			 * @return midpoint
			  */
			 public GeoPointND Midpoint(
					 String label,
					 GeoPointND P,
					 GeoPointND Q);
			 
			 /** 
			  * Midpoint of segment
			 * @param label 
			 * @param segment 
			 * @return midpoint
			  */
			 public GeoPointND Midpoint(
					 String label,
					 GeoSegmentND segment);			 

			/** Segment3D label linking points v1 and v2   */
			/*
				 public GeoSegment3D Segment3D(String label, Ggb3DVector v1, Ggb3DVector v2){
					GeoSegment3D s = new GeoSegment3D(cons,v1,v2);
					s.setLabel(label);
					return s;
				}
			 */

			/** Segment3D label linking points P1 and P2   */
			 public GeoElement Segment3D(String label, GeoPointND P1, GeoPointND P2);

			/** Line3D label linking points P1 and P2   */	
			 public GeoElement Line3D(String label, GeoPointND P1, GeoPointND P2);

			 /** Line3D label through point P and parallel to line l  */
			 public GeoLineND Line3D(String label, GeoPointND P, GeoLineND l);
			 
			 /** Line3D label through point P and parallel to vector v  */
			 public GeoLineND Line3D(String label, GeoPointND P, GeoVectorND v);

			 
			/** Ray3D label linking points P1 and P2   */	
			 public GeoElement Ray3D(String label, GeoPointND P1, GeoPointND P2);


		    /** Line3D through point orthogonal to plane   */	
			 public GeoLineND OrthogonalLine3D(String label, GeoPointND point, GeoCoordSys2D plane);


			 /** Line3D through point orthogonal to line   */	
			 public GeoLineND OrthogonalLine3D(String label, GeoPointND point, GeoLineND line);
			 
			 /** Line3D through point orthogonal to line and direction   */	
			 public GeoLineND OrthogonalLine3D(String label, GeoPointND point, GeoLineND line, GeoDirectionND direction);
			 
			 /** Line3D orthogonal two lines   */	
			 public GeoLineND OrthogonalLine3D(String label, GeoLineND line1, GeoLineND line2);

			 /** Vector3D orthogonal to plane   */	
			 public GeoVectorND OrthogonalVector3D(String label, GeoCoordSys2D plane);

			 /** Vector3D unit orthogonal to plane   */	
			 public GeoVectorND UnitOrthogonalVector3D(String label, GeoCoordSys2D plane);


			 /** Polygon3D linking points P1, P2, ...  
			  * @param label name of the polygon
			  * @param points vertices of the polygon
			  * @return the polygon */
			 public GeoElement [] Polygon3D(String[] label, GeoPointND[] points);

			 /** Polygon3D linking points P1, P2, ...  
			  * @param label name of the polygon
			  * @param points vertices of the polygon
			  * @return the polygon */
			 public GeoElement [] Polygon3D(String[] label, GeoPointND[] points, GeoDirectionND direction);


			 public GeoElement [] PolyLine3D(String [] labels, GeoPointND [] P);
				
			 public GeoElement [] PolyLine3D(String [] labels, GeoList pointList);
				

			/** Prism with vertices (last one is first vertex of second parallel face)
			 * @param label name
			 * @param points vertices
			 * @return the polyhedron
			 */
			 public GeoElement [] Prism(String[] labels, GeoPointND[] points);

			 /** Prism with basis and first vertex of second parallel face
			  * @param labels 
			  * @param polygon 
			  * @param point 
			  * @return the polyhedron
			  */
			 public GeoElement [] Prism(String[] labels, GeoPolygon polygon, GeoPointND point);

			 /** Right prism with basis and height
			  * @param labels 
			  * @param polygon 
			  * @param height 
			  * @return the polyhedron
			  */	 
			 public GeoElement [] Prism(String[] labels, GeoPolygon polygon, NumberValue height);

			/** Pyramid with vertices (last one as apex)
			 * @param label name
			 * @param points vertices
			 * @return the polyhedron
			 */
			 public GeoElement [] Pyramid(String[] labels, GeoPointND[] points);


			/** Line a x + b y + c z + d = 0 named label */
			 public GeoPlaneND Plane3D(
					String label,
					double a,
					double b,
					double c,
					double d);

			/** Line dependent on coefficients of arithmetic expressions with variables,
			 * represented by trees.
			 */
			 public GeoPlaneND DependentPlane3D(String label, Equation equ) ;
			 
			/** 
			 * Plane named label through point and line
			 */
			 public GeoPlaneND Plane3D(String label, GeoPointND point, GeoLineND line);
			 
			 /** 
			  * Plane named label through point parallel to plane
			  */
			 public GeoPlaneND Plane3D(String label, GeoPointND point, GeoCoordSys2D cs);

			/** 
			 * Plane named label through Point P orthogonal to line l
			 */
			 public GeoPlaneND OrthogonalPlane3D(
					String label,
					GeoPointND point,
					GeoLineND line) ;
			 
			 public GeoPlaneND OrthogonalPlane3D(
						String label,
						GeoPointND point,
						GeoVectorND vector);
			 
			 public GeoPlaneND PlaneBisector(
						String label,
						GeoPointND point1,
						GeoPointND point2) ;

			 public GeoPlaneND PlaneBisector(
						String label,
						GeoSegmentND segment) ;









			/** Sphere label linking with center o and radius r   */
			 public GeoElement Sphere(
					String label, 
					GeoPointND M, 
					NumberValue r);

			/** 
			 * Sphere with midpoint M through point P
			 */
			 public GeoElement Sphere(String label, GeoPointND M, GeoPointND P) ;


			/** 
			 * Cone
			 */
			 public GeoQuadricND Cone(String label, GeoPointND origin, GeoVectorND direction, NumberValue angle) ;

			 public GeoQuadricND Cone(String label, GeoPointND origin, GeoPointND secondPoint, NumberValue angle);
	
			 public GeoQuadricND Cone(String label, GeoPointND origin, GeoLineND axis, NumberValue angle);

			 public GeoElement[] ConeLimited(String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) ;

			 
			/** 
			 * Cylinder
			 */
			 public GeoQuadricND Cylinder(String label, GeoPointND origin, GeoVectorND direction, NumberValue r) ;

			 public GeoQuadricND Cylinder(String label, GeoPointND origin, GeoPointND secondPoint, NumberValue r) ;


			 public GeoQuadricND Cylinder(String label, GeoLineND axis, NumberValue r);


			 public GeoElement[] CylinderLimited(String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) ;

			 



				/** 
				 * Limited quadrics
				 */
			 public GeoQuadricND QuadricSide(String label, GeoQuadricND quadric) ;

			 public GeoConicND QuadricBottom(String label, GeoQuadricND quadric);
			 
			 public GeoConicND QuadricTop(String label, GeoQuadricND quadric);




			 /** 
			  * circle through points A, B, C
			  */
			 public GeoConicND Circle3D(
					 String label,
					 GeoPointND A,
					 GeoPointND B,
					 GeoPointND C) ;

			 /** 
			  * circle with axis through point 
			  */
			 public GeoConicND Circle3D(
					 String label,
					 GeoLineND axis,
					 GeoPointND A) ;

			 /** 
			  * circle with point, radius, axis
			  */
			 public GeoConicND Circle3D(
					 String label,
					 GeoPointND A,
					 NumberValue radius,
					 GeoDirectionND axis) ;

			 public GeoConicND Circle3D(
					 String label,
					 GeoPointND A,
					 GeoPointND B,
					 GeoDirectionND axis);



			/** 
			 * plane through points A, B, C
			 */
			 public GeoElement Plane3D(
					String label,
					GeoPointND A,
					GeoPointND B,
					GeoPointND C) ;


			 /**
			  * 
			  * @param label
			  * @param cs2D
			  * @return plane containing the 2D coord sys
			  */
			 public GeoPlaneND Plane3D(String label,GeoCoordSys2D cs2D);

			 public GeoPlaneND Plane3D(GeoCoordSys2D cs2D);


			////////////////////////////////////////////////
			// INTERSECTION (POINTS)


			/** Calculate the intersection of two coord sys (eg line and plane).
			 * @param label name of the point
			 * @param cs1 first coord sys
			 * @param cs2 second coord sys
			 * @return point intersection
			 */
			 public GeoElement Intersect(
					String label,
					GeoElement cs1,
					GeoElement cs2) ;

			 /**
			  * Calculate the intersection of the line g with the region of p
			  * @param label
			  * @param g
			  * @param p
			  * @return
			  */
			 
			 public GeoElement[] IntersectionPoint(
						String[] labels,
						GeoLineND g, GeoSurfaceFinite s) ;	
			 
			 public GeoElement[] IntersectionSegment(
						String[] labels,
						GeoLineND g, GeoSurfaceFinite s) ;
			 
			 public GeoElement[] IntersectionPoint(
						String[] labels,
						GeoPlane3D p, GeoSurfaceFinite s) ;	
			 
			 public GeoElement[] IntersectionSegment(
						String[] labels,
						GeoPlane3D p, GeoSurfaceFinite s) ;		 
			 /** Calculate the intersection of plane and quadric
			  * @param label name of the point
			  * @param plane
			  * @param quadric
			  * @return conic intersection
			  */
			 public GeoConicND Intersect(
					 String label,
					 GeoPlaneND plane,
					 GeoQuadricND quadric) ;

			

			////////////////////////////////////////////////
			// FUNCTIONS (2 VARS)

			 public GeoFunctionNVar Function2Var(
					String label, 
					NumberValue zcoord, 
					GeoNumeric localVarU, NumberValue Ufrom, NumberValue Uto, 
					GeoNumeric localVarV, NumberValue Vfrom, NumberValue Vto 
			);

			 public GeoFunctionNVar Function2Var(
					String label, 
					GeoFunctionNVar f, 
					NumberValue xFrom, NumberValue xTo, 
					NumberValue yFrom, NumberValue yTo 		
			);


			////////////////////////////////////////////////
			// 3D CURVE (1 VAR)

			/** 
			 * 3D Cartesian curve command:
			 * Curve[ <expression x-coord>, <expression y-coord>,  <expression z-coord>, <number-var>, <from>, <to> ]  
			 */
			 public GeoElement CurveCartesian3D(String label, 
					NumberValue xcoord, NumberValue ycoord, NumberValue zcoord, 
					GeoNumeric localVar, NumberValue from, NumberValue to) ;
			 
			 

			 ////////////////////////////////////////////////
			 // 3D SURFACE (2 VARS)

			 /** 
			  * 3D Cartesian surface command:
			  * Surface[ <expression x-coord>, <expression y-coord>,  <expression z-coord>, <u-var>, <u-from>, <u-to>, <v-var>, <v-from>, <v-to> ]  
			  */
			 public GeoElement SurfaceCartesian3D(String label, 
					 NumberValue xcoord, NumberValue ycoord, NumberValue zcoord, 
					 GeoNumeric uVar, NumberValue uFrom, NumberValue uTo,
					 GeoNumeric vVar, NumberValue vFrom, NumberValue vTo) ;


			 ////////////////////////////////////////////////
			 // intersection algos


			 /**
			  * intersection between 3D line and conic
			  * @param labels
			  * @param g
			  * @param c
			  * @return two intersection points
			  */
			 public GeoPointND[] IntersectLineConic(
					 String[] labels,
					 GeoLineND g,
					 GeoConicND c);
			 
			/** 
			 * get only one intersection point of two conics that is near to the given
			 * location (xRW, yRW)
			 */
			public GeoPoint3D IntersectLineConicSingle(
				String label,
				GeoLineND g,
				GeoConicND c, double xRW, double yRW, CoordMatrix4x4 mat);
			 
			 /**
			  * intersection between two 3D conics
			  * @param labels
			  * @param A
			  * @param B
			  * @return 4 intersection points
			  */
			 public GeoPointND[] IntersectConics(
						String[] labels,
						GeoConicND A,
						GeoConicND B);

}

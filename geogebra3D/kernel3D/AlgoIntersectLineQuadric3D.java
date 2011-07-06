/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.AlgoIntersectAbstract;
import geogebra.kernel.AlgoIntersectLineConic;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.AlgoIntersectND;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.main.Application;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLineQuadric3D extends AlgoIntersect3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLineND g;  // input
    private GeoQuadricND q;  // input
    private GeoPoint3D [] D;     // D: old points; Q: new points, not yet permuted
    protected GeoPoint3D [] P, Q; //output -- P is a permutation of Q according to D
    private int intersectionType;

    /**
     * 
     * @param cons
     * @param label
     * @param g
     * @param c
     */
    AlgoIntersectLineQuadric3D(Construction cons, String label, GeoLineND g, GeoQuadricND q) {
        this(cons, g,q);
        GeoElement.setLabels(label, Q);            //TODO change to P
    }
    
    /**
     * 
     * @param cons
     * @param labels
     * @param g
     * @param c
     */
    AlgoIntersectLineQuadric3D(Construction cons, String [] labels, GeoLineND g, GeoQuadricND q) {
        this(cons, g,q);
        GeoElement.setLabels(labels, Q);       //TODO change to P      
    }
    
    public String getClassName() {
        return "AlgoIntersectLineQuadric3D";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }

    
    /**
     * 
     * @param cons
     * @param g
     * @param c
     */
    AlgoIntersectLineQuadric3D(Construction cons, GeoLineND g, GeoQuadricND q) {
        super(cons);
        this.g = g;
        this.q = q;                
        

        P  = new GeoPoint3D[2];
        Q  = new GeoPoint3D[2];
        D  = new GeoPoint3D[2];
        
        
       
        for (int i=0; i < 2; i++) {
            P[i] = new GeoPoint3D(cons);
            Q[i] = new GeoPoint3D(cons);    
            D[i] = new GeoPoint3D(cons);    
        }

        setInputOutput(); // for AlgoElement
        

        initForNearToRelationship();
        compute();        

    }   
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) g;
        input[1] = q;
        
        output = Q;    //TODO  change to P         
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
    

    protected final GeoPoint3D [] getIntersectionPoints() {
        return Q;   //TODO  change to P 
    }
    
    protected GeoPoint3D [] getLastDefinedIntersectionPoints() {
        return D;
    }
    
    
    /**
     * 
     * @return line input
     */
    GeoLineND getLine() { return g; }
    
    /**
     * 
     * @return conic input
     */
    GeoQuadricND getQuadric() { return q; }
    
    
    public final String toString() {
    	return app.getPlain("IntersectionPointOfAB",q.getLabel(),((GeoElement) g).getLabel());
    }

    // INTERSECTION TYPES
    public static final int INTERSECTION_PRODUCING_LINE = 1;
    public static final int INTERSECTION_ASYMPTOTIC_LINE = 2;
    public static final int INTERSECTION_MEETING_LINE = 3;
    public static final int INTERSECTION_TANGENT_LINE = 4;
    public static final int INTERSECTION_SECANT_LINE = 5;
    public static final int INTERSECTION_PASSING_LINE = 6;
    

	protected void compute() {
		
		Coords o = g.getStartInhomCoords();
		Coords d = g.getDirectionInD3();
		
		// g: X' = p + tv   (X' is inhom coords)
		// q: XAX = 0  (the second X is transposed; X = (X',1) is hom coords)
        // we have to solve  
		// u t^2 + 2b t + w = 0  
		// where
        //      u = v.S.v           
        //      b = p.S.v + a.v
        //      w = evaluate(p)
        
        // precalc S.v for u and b
		double[] m = q.matrix;
		double v1 = g.getDirectionInD3().getX();
		double v2 = g.getDirectionInD3().getY();
		double v3 = g.getDirectionInD3().getZ();
        double Sv1 = m[0] * v1 + m[4] * v2 + m[5] * v3;
        double Sv2 = m[4] * v1 + m[1] * v2 + m[6] * v3;
        double Sv3 = m[5] * v1 + m[6] * v2 + m[2] * v3;

		double p1 = g.getStartInhomCoords().getX();
		double p2 = g.getStartInhomCoords().getY();
		double p3 = g.getStartInhomCoords().getZ();
		double u = v1 * Sv1 + v2 * Sv2 + v3 * Sv3;
        double b = g.getStartInhomCoords().getX() * Sv1 
        + g.getStartInhomCoords().getY() * Sv2 
        + g.getStartInhomCoords().getZ() * Sv3
        + m[7] * v1 + m[8] * v2 + m[9] * v3;
        
        double w = p1 * (m[0] * p1 + m[4] * p2 + m[5] * p3 + m[7])
        + p2 * (m[4] * p1 + m[1] * p2 + m[6] * p3 + m[8])
        + p3 * (m[5] * p1 + m[6] * p2 + m[2] * p3 + m[9])
        + m[7] * p1 + m[8] * p2 + m[9] * p3 + m[3];
        
        Kernel kernel = q.getKernel();
        if (kernel.isZero(u)) {//no quadratic term
            if (kernel.isZero(b)) {//no linear term: 0 t = -w
                if (kernel.isZero(w)) { // whole line is contained in q
                    Q[0].setUndefined();
                    Q[1].setUndefined();
                    intersectionType = INTERSECTION_PRODUCING_LINE;
                }
                else { // w != 0, Asymptote
                    Q[0].setUndefined();
                    Q[1].setUndefined();                    
                    intersectionType =  INTERSECTION_ASYMPTOTIC_LINE;
                }
            }
            else { // b != 0, t = -w/ (2b)
                double t1 = -w / (2.0 * b);
                Q[0].setCoords(g.getPointInD(3, t1));
                Q[1].setUndefined();
                intersectionType =  INTERSECTION_MEETING_LINE;
            }            
        }
        else { // u != 0
            double dis = b * b - u * w;
            if (Kernel.isZero(dis)) {// Tangent
                double t1 = -b / u;
                Q[0].setCoords(g.getPointInD(3, t1));
                Q[1].setCoords(Q[0].getCoords());
                intersectionType =  INTERSECTION_TANGENT_LINE;
            }  else { //two solutions
                if (dis > 0) {
                    dis = Math.sqrt(dis);
                    // For accuracy, if b > 0 then we choose
                    //     t1 = -(b+dis) / u
                    //     t2 = (-b + dis) / u  = w / -(b+dis)
                    // if b < 0 then we choose
                    //     t1 = (-b - dis) / u  = w / (-b+dis) = w / -(b-dis)
                    //     t2 = -(b-dis) / u
                    
                    boolean swap = b < 0.0;
                    if (swap) {
                        dis = -dis;
                    }
                    double n = -(b + dis);
                    double t1 = swap ? w / n : n / u;
                    double t2 = swap ? n / u : w / n;
                                        
                    Q[0].setCoords(g.getPointInD(3, t1));
                    Q[1].setCoords(g.getPointInD(3, t2));
                    
                    intersectionType =  INTERSECTION_SECANT_LINE;
                }
                else { // dis < 0, no solution
                    Q[0].setUndefined();
                    Q[1].setUndefined();                    
                    intersectionType =  INTERSECTION_PASSING_LINE;                    
                }                
            }
        }        
		
				
	}
	
	private void checkIsOnLine(GeoPoint3D p){
		if (!p.isDefined())
			return;
		if (!g.respectLimitedPath(p.getCoords(),Kernel.MIN_PRECISION))
			p.setUndefined();
	}
	
	
	
	private void setPointsUndefined(){
        for (int i=0; i < 2; i++) 
            Q[i].setUndefined();  //TODO change to Q
        
	}
	
    /**
     * Returns the index in output[] of the intersection point
     * that is closest to the coordinates (xRW, yRW)
     * TODO: move to an interface
     */
    /*int getClosestPointIndex(double xRW, double yRW, CoordMatrix4x4 mat) {
        GeoPoint3D[] P = getIntersectionPoints();
        double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < P.length; i++) {
        	Coords toSceneInhomCoords = mat.mul(P[i].getCoords().getCoordsLast1()).getInhomCoords();
        	x = (toSceneInhomCoords.getX() - xRW);
            y = (toSceneInhomCoords.getY() - yRW);
            lengthSqr = x * x + y * y;
            if (lengthSqr < mindist) {
                mindist = lengthSqr;
                minIndex = i;
            }
        }

        return minIndex;
    }*/
	
    protected final void initForNearToRelationship() {   
//TODO
    }
}

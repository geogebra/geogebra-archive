/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLineConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.main.Application;

import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLineCubic extends AlgoIntersect {    

	private static final long serialVersionUID = 1L;
	private GeoLine g;  // input
    private GeoCubic c;

    private GeoPoint [] P, D, Q;     // points  
    private int age[]; // of defined points D
    private int permutation[]; // of computed intersection points Q to output points P
    private double [][] distTable;
    private boolean isQonPath []; // for every new intersection point Q: is it on both paths?
    
    //  for every resulting point P: has it ever been defined, i.e. is it alive?
    private boolean isPalive [];     
    
    private int i;
    private boolean isDefinedAsTangent;
    private boolean firstIntersection = true;
    private boolean isPermutationNeeded = true;
    private GeoPoint tangentPoint;
    
    private PointPairList pointList = new PointPairList();
    
    // for segments, rays and conic parts we need to check the
    // intersection points at the end of compute()
    private boolean isLimitedPathSituation;              
    private boolean possibleSpecialCase = false;
    private int specialCasePointOnCircleIndex = 0; // index of point on line and conic
    
    protected String getClassName() {
        return "AlgoIntersectLineCubic";
    }

    
    AlgoIntersectLineCubic(Construction cons, GeoLine g, GeoCubic c) {
        super(cons);
        this.g = g;
        this.c = c;  
        
        isLimitedPathSituation = g.isLimitedPath() || c.isLimitedPath();        
        
             P  = new GeoPoint[4];
            D  = new GeoPoint[4];
            Q  = new GeoPoint[4];
            distTable = new double[4][4];                       
            age = new int[4];
            permutation= new int[4];
            isQonPath = new boolean[4];
            isPalive = new boolean[4];
            
            for (i=0; i < 4; i++) {
                Q[i] = new GeoPoint(cons);
                P[i] = new GeoPoint(cons); 
                D[i] = new GeoPoint(cons);                     
            }
            
        	// check possible special case
            possibleSpecialCase = handleSpecialCase();

        
        setInputOutput(); // for AlgoElement     
        initForNearToRelationship();
        compute();                      
    }
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = c;
        input[1] = g;
        
        output = P;        
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
    
    final GeoPoint [] getIntersectionPoints() {
        return P;
    }
    
    GeoLine getLine() { return g; }
    GeoCubic getCubic() { return c; }
    GeoPoint [] getLastDefinedIntersectionPoints() {
        return D;
    }
    
    public boolean isNearToAlgorithm() {
    	return true;
    }
    	 
    final void initForNearToRelationship() {   
    	if (isDefinedAsTangent) return;
    	    	
    	isPermutationNeeded = true; // for non-continuous intersections    	
    	for (int i=0; i < P.length; i++) {        	 	
    	 	 age[i] = 0; 
             isQonPath[i] = true;
             isPalive[i] = false;             
        }
    }
    
    // calc intersections of conic c and line g
    protected final void compute() {  
        // g is defined as tangent of c
        if (isDefinedAsTangent) {
            P[0].setCoords(tangentPoint);
            return;
        }              
        
        // check for special case of line through point on conic
        if (possibleSpecialCase) {
            if (handleSpecialCase()) return;
        }   
        
        // continous: use near-to-heuristic between old and new intersection points
        // non-continous: use computeContinous() to init a permutation and then
        //                always use this permutation
        boolean continous = isPermutationNeeded || kernel.isContinuous();   
        if (continous) {
        	computeContinous();        	        	        	        	
        } else {
        	computeNonContinous();
        }        
              
        avoidDoubleTangentPoint();
    }           
            
    /**
     * There is an important special case we handle separately:
     * The conic section c is intersected with a line passing through a point A on c.
     * In this case the first intersection point should always be A. 
     * @return true if this special case was handled.
     */
    private boolean handleSpecialCase() {
    	// check if startpoint or endpoint of line is on conic
    	GeoPoint pointOnConic = null;    	
    	if (g.startPoint != null && c.isOnPath(g.startPoint, Kernel.MIN_PRECISION)) {    		
    		pointOnConic = g.startPoint;    		
    	} 
    	else if (g.endPoint != null && c.isOnPath(g.endPoint, Kernel.MIN_PRECISION)) {    		
    		pointOnConic = g.endPoint;    		
    	}     	 
    	else {
    		// get points on conic and see if one of them is on line g
    		ArrayList pointsOnConic = c.getPointsOnConic();
    		if (pointsOnConic != null) {
    			int size = pointsOnConic.size();
    			for (int i=0; i < size; i++) {
    				GeoPoint p = (GeoPoint) pointsOnConic.get(i);
    				if (g.isOnPath(p, Kernel.MIN_PRECISION)) {
    					pointOnConic = p;
    					break;
    				}
    			}
    		}
    	}
    	if (pointOnConic == null) return false;
    		    
    	// calc new intersection points Q
        intersect(c, g, Q);    
        
        // pointOnConic should be first intersection point
        // Note: if the first intersection point was already set when a file
        //       was loaded, then we need to make sure that we don't lose this information
        int firstIndex = specialCasePointOnCircleIndex;
        int secondIndex = (firstIndex + 1) % 2;
                
        if (firstIntersection && didSetIntersectionPoint(firstIndex)) {           
        	if (!P[firstIndex].isEqual(pointOnConic)) {
            	// pointOnConic is NOT equal to the loaded intersection point:
        		// we need to swap the indices
        		int temp = firstIndex;
        		firstIndex = secondIndex;
        		secondIndex = temp;
        		
        		specialCasePointOnCircleIndex = firstIndex;        		     
        	}  
        	firstIntersection = false;
        } 
        
        // pointOnConic should be first intersection point
        P[firstIndex].setCoords(pointOnConic);        
        
        // the other intersection point should be the second one
        boolean didSetP1 = false;
        for (int i=0; i < 4; i++) {  
	   		if (!Q[i].isEqual(P[firstIndex])) {
	   			P[secondIndex].setCoords(Q[i]);
	   			didSetP1 = true;
	   			break;
	   		}
	    }   
        if (!didSetP1) // this happens when both intersection points are equal
        	P[secondIndex].setCoords(pointOnConic); 
	   	 
	   	if (isLimitedPathSituation) {
	   		// make sure the points are on a limited path
	   		for (int i=0; i < 4; i++) {  
	   			if (!pointLiesOnBothPaths(P[i]))
	   				P[i].setUndefined();    			          
	   	    }     	 
	   	}	   	
                          
	   	return true;
    }
    
    /**
     * Use the current permutation to set output points P from computed points Q.        
     */  
     private void computeNonContinous() {    	     	 
    	 // calc new intersection points Q
         intersect(c, g, Q);    
                           
         // use fixed permutation to set output points P
    	 for (int i=0; i < P.length; i++) {        	
         	P[i].setCoords(Q[permutation[i]]);         	
         }   
    	 
    	 if (isLimitedPathSituation) {
        	 // make sure the points are on a limited path
    		 for (int i=0; i < P.length; i++) {  
    			 if (!pointLiesOnBothPaths(P[i]))
    				 P[i].setUndefined();    			          
    	     }     	 
    	 }
     }
    
    /**
    * We want to find a permutation of Q, so that the 
    * distances between old points Di and new points Qi are minimal.         
    */  
    private void computeContinous() {
    	 /* 
         * D ... old defined points
         * P ... current points
         * Q ... new points
         *
         * We want to find a permutation of Q, so that the 
         * distances between old point Di and new Point Qi are minimal.         
         */        
         
        // remember the defined points D, so that Di = Pi if Pi is defined        
        // and set age                
        boolean noSingularity = !P[0].isEqual(P[1]); // singularity check        
        for (i=0; i < 4; i++) {        	
        	boolean finite = P[i].isFinite();
        	
        	// don't do this if P[0] = P[1]        
	        if (noSingularity && finite)  { 
                D[i].setCoords(P[i]);   
                age[i] = 0;                               
            } else {                
                age[i]++;
            }        	    
	        
	        // update alive state
	        isPalive[i] = isPalive[i] || finite || P[i].labelSet;
        }   
       
           
        // calc new intersection points Q
        intersect(c, g, Q);                         
        
        // for limited paths we have to distinguish between intersection points Q
        // that lie on both limited paths or not. This is important for choosing
        // the right permutation in setNearTo()
        if (isLimitedPathSituation) {
        	updateQonPath();
        }        
        
        if (firstIntersection) {           	
        	// init points in order P[0], P[1]
            int count=0;
            for (i=0; i < Q.length; i++) {
            	// make sure interesection points lie on limited paths   
                if (Q[i].isDefined() && pointLiesOnBothPaths(Q[i])) {
                    P[count].setCoords(Q[i]);
                    D[count].setCoords(P[count]);
                    firstIntersection = false;
                    count++;
                }
            }                                    
            return;
        }
        
        // calc distance table
        AlgoIntersectConics.distanceTable(D, age, Q, distTable);                   
        
        // find permutation and calculate new mean distances         
        AlgoIntersectConics.setNearTo(P, isPalive, Q, isQonPath, distTable, pointList, permutation);          
        isPermutationNeeded = false;
        
        /*
    	System.out.print("permutation: ");
    	for (int i=0; i < permutation.length; i++) {
    		System.out.print(permutation[i] + " ");
    	}
    	Application.debug();
        */
        
        // make sure interesection points lie on limited paths
        if (isLimitedPathSituation) 
        	handleLimitedPaths();       
    }
    
        
    /**
     * Checks whether the computed intersection points really lie on
     * the limited paths. Note: points D[] and P[] may be changed here.     
     */
    private void handleLimitedPaths() {
    	//  singularity check        
    	boolean noSingularity = !P[0].isEqual(P[1]);    	
        
    	for (i=0; i < P.length; i++) {
            if (P[i].isDefined()) {
            	if (!pointLiesOnBothPaths(P[i])) {
            		// the intersection point should be undefined as it doesn't lie
            		// on both (limited) paths. However, we want to keep the information
            		// of P[i]'s position for our near-to-approach to achieve continous movements.
            		// That's why we remember D[i] now                		                   		                	    
            	    if (noSingularity && P[i].isFinite())  { 
            	    	D[i].setCoords(P[i]);   
            	    	// the age will be increased by 1 at the
            	    	// next call of compute() as P[i] will be undefined
                        age[i] = -1;            	    	
                    }    
            	    P[i].setUndefined(); 
            	}
            }
        }
    }
    
    /**
     * Checks wether Q[i] lies on g and c and sets isQonPath[] accordingly. 
     */
    private void updateQonPath() {
    	for (int i=0; i < Q.length; i++) {
    		isQonPath[i] = pointLiesOnBothPaths(Q[i]);
    	}
    }
    
    private boolean pointLiesOnBothPaths(GeoPoint P) {        	
    	return g.isIntersectionPointIncident(P, Kernel.MIN_PRECISION) &&
			   c.isIntersectionPointIncident(P, Kernel.MIN_PRECISION);
    }
        
    
    // INTERSECTION TYPES
    public static final int INTERSECTION_PRODUCING_LINE = 1;
    public static final int INTERSECTION_ASYMPTOTIC_LINE = 2;
    public static final int INTERSECTION_MEETING_LINE = 3;
    public static final int INTERSECTION_TANGENT_LINE = 4;
    public static final int INTERSECTION_SECANT_LINE = 5;
    public static final int INTERSECTION_PASSING_LINE = 6;
                
    /**
     * Intersects conic c with line g and always sets two GeoPoints (sol).
     * If there are no real intersections, the coords of GeoPoints are
     * set to Double.NaN. 
     * @returns type of intersection
     */
    private int intersect(GeoCubic c, GeoLine g, GeoPoint [] sol) {                                        	
    	
    	boolean ok = false;    	
    	int ret = INTERSECTION_PASSING_LINE;        
        
    	if (c.isDefined() && g.isDefined()) {    	
	    	double epsilon = Kernel.STANDARD_PRECISION;           
	        while (epsilon <= Kernel.MIN_PRECISION) {
	            ret = intersectLineCubic(g, c, sol, cons);    	            	            
	            
	            // TODO
	            //if (ok = testPoints(g, c, sol, Kernel.MIN_PRECISION)) break;
	            ok = true;
	            epsilon *= 10.0;
	            kernel.setEpsilon(epsilon);
	        }
	        kernel.resetPrecision();                
    	}
    	
        // intersection failed
        if (!ok) {     
            //Application.debug("INTERSECT LINE CONIC FAILED: epsilon = " + epsilon);
            for (int i=0; i < 4; i++) sol[i].setUndefined();                      
        }    
        return ret;
    }
        
    // do the actual computations
    final static int intersectLineCubic(GeoLine g, GeoCubic c, GeoPoint [] sol, Construction cons) { 
        double [] A = c.getCoeffs();
        
        
        double eqn[] = new double[5];
        double sols[] = new double[4];
        
        double r = g.x;
        double s = g.y;
        double t = g.z;
        
        double x, x2, x3, y, y2, y3;
        
        EquationSolver eqnSolver = cons.getEquationSolver();
        
        if (r == 0) { // horizontal line
        	y = -t / s;
        	y2 = y * y;
        	y3 = y * y2;
        	eqn[0] = A[15] + y * A[11] + y2 * A[7] + y3 * A[3]; // coeff of 1
        	eqn[1] = A[14] + y * A[10] + y2 * A[6] + y3 * A[2]; // coeff of x
        	eqn[2] = A[13] + y * A[9] + y2 * A[5] + y3 * A[1]; // coeff of x^2
        	eqn[3] = A[11] + y * A[8] + y2 * A[4] + y3 * A[0]; // coeff of x^3
        	
        	int N = eqnSolver.solveCubic(eqn, sols);
        	
        	for (int i = 0 ; i < 4 ; i++) {
        		if (i < N)
        			sol[i].setCoords(sols[i], y, 1.0); else sol[i].setUndefined();
        			//Application.debug(N+ ": "+sols[0]);
        	}
        	return N;
        }
        
        if (s == 0) { // vertical line
        	x = -t / r;
        	x2 = x * x;
        	x3 = x * x2;
        	eqn[0] = A[15] + x * A[14] + x2 * A[13] + x3 * A[12]; // coeff of 1
        	eqn[1] = A[11] + x * A[10] + x2 * A[9] + x3 * A[8]; // coeff of y
        	eqn[2] = A[7] + x * A[6] + x2 * A[5] + x3 * A[4]; // coeff of y^2
        	eqn[3] = A[3] + x * A[2] + x2 * A[1] + x3 * A[0]; // coeff of y^3
        	
        	int N = eqnSolver.solveCubic(eqn, sols);
        	
        	for (int i = 0 ; i < 4 ; i++) {
        		if (i < N)
        			sol[i].setCoords(x, sols[i], 1.0); else sol[i].setUndefined();
        			//Application.debug(N+ ": "+sols[0]);
        	}
        	return N;
        }
        
        // if coefficients of x^3y^3, x^2y^3 or x^3y^2 are non-zero
        // we get a quintic / sextic
        // TODO try numerical solve?
        if (A[0] != 0 || A[1] != 0 || A[4] != 0) {
        	sol[0].setUndefined();
        	sol[1].setUndefined();
        	sol[2].setUndefined();
        	sol[3].setUndefined();
        	return -1;
        }

        double rr = r * r;
        double rrr = rr * r;
        double ss = s * s;
        double sss = ss * s;
        double tt = t*t;
        double ttt = tt * t;
        
        // substitute x = (-sy-t)/r into equation of curve then solve for y
        //Expand(y^3*(c*((-s*y-t)/r)+d)+y^2*(f*((-s*y-t)/r)^2+g*((-s*y-t)/r)+h)+y*(i*((-s*y-t)/r)^3+j*((-s*y-t)/r)^2+k*((-s*y-t)/r)+l)+m*((-s*y-t)/r)^3+n*((-s*y-t)/r)^2+o*((-s*y-t)/r)+p)

        //((((((((((((((((((((((((((((((y)^(4) * (s)^(2) * (r)^(14) * f) - ((y)^(4) * (s)^(3) * (r)^(13) * i)) - ((y)^(4) * c * s * (r)^(15))) + ((y)^(3) * d * (r)^(16))) + ((y)^(3) * (s)^(2) * (r)^(14) * j)) - (3 * (y)^(3) * (s)^(2) * t * (r)^(13) * i)) - ((y)^(3) * (s)^(3) * (r)^(13) * m)) + (2 * (y)^(3) * s * t * (r)^(14) * f)) - ((y)^(3) * s * (r)^(15) * g)) - ((y)^(3) * c * t * (r)^(15))) + ((y)^(2) * (s)^(2) * (r)^(14) * n)) - (3 * (y)^(2) * (s)^(2) * t * (r)^(13) * m)) + (2 * (y)^(2) * s * t * (r)^(14) * j)) - (3 * (y)^(2) * s * (t)^(2) * (r)^(13) * i)) - ((y)^(2) * s * (r)^(15) * k)) + ((y)^(2) * (t)^(2) * (r)^(14) * f)) - ((y)^(2) * t * (r)^(15) * g)) + ((y)^(2) * (r)^(16) * h)) + (2 * y * s * t * (r)^(14) * n)) - (3 * y * s * (t)^(2) * (r)^(13) * m)) - (y * s * (r)^(15) * o)) + (y * (t)^(2) * (r)^(14) * j)) - (y * (t)^(3) * (r)^(13) * i)) - (y * t * (r)^(15) * k)) + (y * (r)^(16) * l)) + ((t)^(2) * (r)^(14) * n)) - ((t)^(3) * (r)^(13) * m)) - (t * (r)^(15) * o)) + ((r)^(16) * p))/((r)^(16))
        // coefficient of y^4:
        eqn[4] = ss * A[5] / rr - sss * A[8] / rrr - A[2] * s / r;
        // coefficient of y^3:
        eqn[3] = A[3] + ss * A[9] / rr - 3 * ss * t * A[8] / rrr - sss * A[12] / rrr + 2 * s * t * A[5] / rr -  s * A[6] / r -  A[2] * t / r;
        // coefficient of y^2:
        eqn[2] = ss * A[13] / rr - 3 * ss * t * A[12] / rrr + 2 * s * t * A[9] / rr - 3 * s * tt * A[8] / rrr - s * A[10] / r + tt * A[5] / rr - t * A[6] / r +  A[7];
        // coefficient of y:
        eqn[1] = 2 * s * t * A[13] / rr - 3 * s * tt * A[12] / rrr - s * A[14] / r + tt * A[9] / rr - ttt * A[8] / rrr - t * A[10] / r +  A[11]; 
        // coefficient of 1:
        eqn[0] = tt * A[13] / rr - ttt * A[12] / rrr - t * A[14] / r + A[15];
    	
    	int N = eqnSolver.solveQuartic(eqn, sols);
    	
    	for (int i = 0 ; i < 4 ; i++) {
    		if (i < N)
    			sol[i].setCoords(-(s * sols[i] + t) / r, sols[i], 1.0); else sol[i].setUndefined();
    			//Application.debug(N+ ": "+sols[0]);
    	}
    	return N;
        
     
    }
    
     /**
     * Tests if at least one point lies on conic c and line g.
     */
    final static private boolean testPoints(GeoLine g, GeoCubic c, GeoPoint[] P, double eps) {
        boolean foundPoint = false;      
        for (int i=0; i < P.length; i++) {
            if (P[i].isDefined()) {                	            	
                if (!(c.isOnFullCubic(P[i], eps) && g.isOnFullLine(P[i], eps)))                 	                
                	P[i].setUndefined();
                else
                	foundPoint = true;
            }            
        }            
        return foundPoint;
    }
   
}
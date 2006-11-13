/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoIntersectLineConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;




/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLineConic extends AlgoIntersect {    

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g;  // input
    private GeoConic c;

    private GeoPoint [] P, D, Q;     // points  
    private int age[]; // of defined points D       
    private double [][] distTable;
    private boolean isQonPath []; // for every new intersection point Q: is it on both paths?
    
    //  for every resulting point P: has it ever been defined, i.e. is it alive?
    private boolean isPalive [];     
    
    private int i;
    private boolean isTangent;
    private boolean firstIntersection = true;
    private GeoPoint tangentPoint;
  
    // mean distance computation
    private double meanDistance[]; // for every point P
    private double distMemory[][]; // for every point P: store old distances
    private int distMemoryIndex;
    private PointPairList pointList = new PointPairList();
    
    // for segments, rays and conic parts we need to check the
    // intersection points at the end of compute()
    private boolean isLimitedPathSituation;   
    
    /*
     * There is an important special case I where my heuristic of the 
     * near-to-relationship fails:
     * Circle[A, B] is intersected with a line passing through A and B.
     * In this case the first intersection point should always be B
     * and the other (2A - B) 
     */    
    //  if the conic is defined by Circle[A, B] then we should check for
    // this special case
     private boolean possibleSpecialCase; 
     private GeoPoint midpoint, radiuspoint;  
    
    String getClassName() {
        return "AlgoIntersectLineConic";
    }

    
    AlgoIntersectLineConic(Construction cons, GeoLine g, GeoConic c) {
        super(cons);
        this.g = g;
        this.c = c;  
        
        isLimitedPathSituation = g.isLimitedPath() || c.isLimitedPath();        
        
        // check special cases
        
        // if g is defined as a tangent of c, we dont't need
        // to compute anything
        if (g.getParentAlgorithm() instanceof AlgoTangentPoint) {
            AlgoTangentPoint algo = (AlgoTangentPoint) g.getParentAlgorithm();
            tangentPoint = algo.getTangentPoint(c, g);
            isTangent = (tangentPoint != null);            
        }
        else if (g.getParentAlgorithm() instanceof AlgoTangentLine) { 
            AlgoTangentLine algo = (AlgoTangentLine) g.getParentAlgorithm();
            tangentPoint = algo.getTangentPoint(c, g);
            isTangent = (tangentPoint != null);            
        }
        
        // possible special case I described above
        if (c.getParentAlgorithm() instanceof AlgoCircleTwoPoints) {
            AlgoCircleTwoPoints algo = (AlgoCircleTwoPoints) c.getParentAlgorithm();
            possibleSpecialCase = true;
            midpoint = algo.getM();
            radiuspoint = algo.getP();
        }
        
        
        // g is defined as tangent of c
        if (isTangent) {
            P  = new GeoPoint[1];
            P[0] = new GeoPoint(cons);
        } 
        // standard case
        else {
            P  = new GeoPoint[2];
            D  = new GeoPoint[2];
            Q  = new GeoPoint[2];
            distTable = new double[2][2];                       
            age = new int[2];
            isQonPath = new boolean[2];
            isPalive = new boolean[2];
            meanDistance = new double[2];
            distMemory = new double[2][AlgoIntersectConics.DIST_MEMORY_SIZE];            
            
            for (i=0; i < 2; i++) {
                Q[i] = new GeoPoint(cons);
                P[i] = new GeoPoint(cons); 
                D[i] = new GeoPoint(cons);                     
            }
        }
        
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
        setDependencies(); // done by AlgoElement
    }    
    
    GeoPoint [] getIntersectionPoints() {
        return P;
    }
    
    GeoLine getLine() { return g; }
    GeoConic getConic() { return c; }
    GeoPoint [] getLastDefinedIntersectionPoints() {
        return D;
    }
    	 
    final void initForNearToRelationship() {   
    	if (isTangent) return;
    	    	
    	distMemoryIndex = 0;
    	for (int i=0; i < P.length; i++) {    
    	 	 meanDistance[i] = 0;
    	 	 for (int j=0; j < AlgoIntersectConics.DIST_MEMORY_SIZE; j++) {
    	 	 	distMemory[i][j] = 0;
    	 	 }
    	 	 age[i] = 0; 
             isQonPath[i] = true;
             isPalive[i] = false;             
        }
    }
    
    // calc intersections of conic c and line g
    final void compute() {  
        // g is defined as tangent of c
        if (isTangent) {
            P[0].setCoords(tangentPoint);
            return;
        }              
        
        if (possibleSpecialCase) {
            // does the line pass through midpoint and radiuspoint?        	
            if (g.isIntersectionPointIncident(midpoint, Kernel.MIN_PRECISION) && 
            	g.isIntersectionPointIncident(radiuspoint, Kernel.MIN_PRECISION)) {
                // now we know the intersection points:
                // radiuspoint and (2* midpoint - radiuspoint)
                P[0].setCoords(radiuspoint.x, radiuspoint.y, radiuspoint.z);
                P[1].setCoords(2* midpoint.inhomX - radiuspoint.inhomX, 
                                        2* midpoint.inhomY - radiuspoint.inhomY,
                                        1.0);
                // make sure interesection points lie on limited paths
                if (isLimitedPathSituation) 
                	handleLimitedPaths();
                return;                                     
            } else {
                // we switch off the special case in the very moment
                // the line does not pass through midpoint and radiuspoint
                // We do this because we don't want to destroy our
                // near-to-realtionship used in the standard case
                possibleSpecialCase = false;
            }
        }   
        
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
        boolean noSingularity = !P[0].equals(P[1]); // singularity check        
        for (i=0; i < 2; i++) {        	
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
       

        // STANDARD CASE
        // calc new points Q
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
        AlgoIntersectConics.distanceTable(D, age, meanDistance, Q, distTable);                   
        
        // find permutation and calculate new mean distances
        distMemoryIndex++;
        if (distMemoryIndex == AlgoIntersectConics.DIST_MEMORY_SIZE) 
        	distMemoryIndex = 0;
        AlgoIntersectConics.setNearTo(P, isPalive, Q, isQonPath, distTable, meanDistance, distMemory, distMemoryIndex, pointList);        
              
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
    	boolean noSingularity = !P[0].equals(P[1]);    	
        
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
    private int intersect(GeoConic c, GeoLine g, GeoPoint [] sol) {                                        	
    	boolean ok = false;    	
    	int ret = INTERSECTION_PASSING_LINE;        
        
    	if (c.isDefined() && g.isDefined()) {    	
	    	double epsilon = Kernel.STANDARD_PRECISION;           
	        while (epsilon <= Kernel.MIN_PRECISION) {
	            ret = intersectLineConic(g, c, sol);    	            	            
	            
	            if (ok = testPoints(g, c, sol, Kernel.MIN_PRECISION)) break;
	            epsilon *= 10.0;
	            kernel.setEpsilon(epsilon);
	        }
	        kernel.resetPrecision();                
    	}
    	
        // intersection failed
        if (!ok) {     
            //System.err.println("INTERSECT LINE CONIC FAILED: epsilon = " + epsilon);
            for (int i=0; i < 2; i++) sol[i].setUndefined();                      
        }    
        return ret;
    }
        
    // do the actual computations
    final static int intersectLineConic(GeoLine g, GeoConic c, GeoPoint [] sol) { 
        double [] A = c.A;
        
        // get arbitrary point of line          
        double px, py;
        if (Math.abs(g.x) > Math.abs(g.y)) {
            px = -g.z / g.x;
            py = 0.0d;
        } else {
            px = 0.0d;
            py = -g.z / g.y;
        } 
   
        // we have to solve   u t� + 2d t + w = 0  
        // to intersect line g: X = p + t v  with conic
        // calc u, d, w:
        //      u = v.S.v           (S is upper left submatrix of A)
        //      d = p.S.v + a.v
        //      w = evaluate(p)
        
        // precalc S.v for u and d
        double SvX = A[0] * g.y - A[3] * g.x;
        double SvY = A[3] * g.y - A[1] * g.x;
        double u = g.y * SvX - g.x * SvY;
        double d = px * SvX + py * SvY + A[4] * g.y - A[5] * g.x;
        double w = c.evaluate(px, py);
        
        Kernel kernel = g.kernel;
        // Erzeugende, Asymptote oder Treffgerade
        if (kernel.isZero(u)) {
            // Erzeugende oder Asymptote
            if (kernel.isZero(d)) {
                // Erzeugende
                if (kernel.isZero(w)) {
                    sol[0].setUndefined();
                    sol[1].setUndefined();
                    return INTERSECTION_PRODUCING_LINE;
                }
                // Asymptote
                else { // w != 0
                    sol[0].setUndefined();
                    sol[1].setUndefined();                    
                    return INTERSECTION_ASYMPTOTIC_LINE;
                }
            }
            // Treffgerade
            else { // d != 0
                double t1 = -w / (2.0 * d);
                sol[0].setCoords(px + t1 * g.y, py - t1 * g.x, 1.0d);
                sol[1].setUndefined();
                return INTERSECTION_MEETING_LINE;
            }            
        }
        // Tangente, Sekante, Passante
        else { // u != 0
            double dis = d * d - u * w;
            // Tangente
            if (kernel.isZero(dis)) {
                double t1 = -d / u;
                sol[0].setCoords(px + t1 * g.y,  py - t1 * g.x, 1.0);
                sol[1].setCoords(sol[0]);
                return INTERSECTION_TANGENT_LINE;
            }
            // Sekante oder Passante
            else {
                // Sekante
                if (dis > 0) {
                    dis = Math.sqrt(dis);
                    // For accuracy, calculate one root using:
                    //     (-d +/- dis) / u
                    // and the other using:
                    //      w / (-d +/- dis)
                    // Choose the sign of the +/- so that d+dis gets larger in magnitude
                    if (d < 0.0) {
                        dis = -dis;
                    }
                    double q = -(d + dis);
                    double t1 = q / u;
                    double t2 = w / q;
                                        
                    sol[0].setCoords(px + t1 * g.y, py - t1 * g.x, 1.0);
                    sol[1].setCoords(px + t2 * g.y, py - t2 * g.x, 1.0);       
                    return INTERSECTION_SECANT_LINE;
                }
                // Passante
                else { // dis < 0
                    sol[0].setUndefined();
                    sol[1].setUndefined();                    
                    return INTERSECTION_PASSING_LINE;                    
                }                
            }
        }        
    }
    
     /**
     * Tests if at least one point lies on conic c and line g.
     */
    final static private boolean testPoints(GeoLine g, GeoConic c, GeoPoint[] P, double eps) {
        boolean foundPoint = false;      
        for (int i=0; i < P.length; i++) {
            if (P[i].isDefined()) {                	            	
                if (!(c.isOnFullConic(P[i], eps) && g.isOnFullLine(P[i], eps)))                 	                
                	P[i].setUndefined();
                else
                	foundPoint = true;
            }            
        }            
        return foundPoint;
    }
   
}
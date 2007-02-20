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
 * AlgoIntersectConics.java
 *
 * Created on 1. Dezember 2001
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectConics extends AlgoIntersect {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// number of old distances that are used to 
    // compute the mean distance change of one point
    static final int DIST_MEMORY_SIZE = 8;
    
    private GeoConic A, B;
    private GeoPoint [] P, D, Q;     // points  
        
    private GeoConic [] degConic;    
    private int [] age; // for points in D    
    private double [][] distTable;   
    private boolean [] isQonPath;
    private boolean [] isPalive; // has P ever been defined?
    private boolean firstIntersection = true;
    private int i;
    private boolean isLimitedPathSituation;
    
    // mean distance computation
    private double meanDistance[]; // for every point P
    private double distMemory[][]; // for every point P: store old distances
    private int distMemoryIndex = 0;
    private PointPairList pointList = new PointPairList();
    
    private EquationSolver eqnSolver;
        
	String getClassName() {
		return "AlgoIntersectConics";
	}
    
    AlgoIntersectConics(Construction cons, GeoConic A, GeoConic B) {           
    	super(cons);     
    	
		eqnSolver = cons.getEquationSolver();
    	
        this.A = A;
        this.B = B;   
        isLimitedPathSituation = A.isLimitedPath() || B.isLimitedPath();        
        
        // init temp vars        
        P  = new GeoPoint[4]; // output
        D  = new GeoPoint[4];
        Q  = new GeoPoint[4];       
                
        isQonPath = new boolean[4];    
        isPalive = new boolean[4];
        age = new int[4];
        for (i=0; i < 4; i++) {
            P[i] = new GeoPoint(cons);                    
            Q[i] = new GeoPoint(cons);      
            D[i] = new GeoPoint(cons);            
        }                                   
        degConic = new GeoConic[3];
        for (i=0; i < 3; i++) degConic[i] = new GeoConic(cons);                
        distTable = new double[4][4];                      
        meanDistance = new double[4];
        distMemory = new double[4][AlgoIntersectConics.DIST_MEMORY_SIZE];        
        
        setInputOutput(); // for AlgoElement     
        initForNearToRelationship();
        compute();                      
    }
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = B;
        
        output = P;
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
        
	GeoPoint [] getIntersectionPoints() {
		return P;
	}
	
    GeoConic getA() { return A; }
    GeoConic getB() { return B; }
	
	GeoPoint [] getLastDefinedIntersectionPoints() {
		return D;
	}
	
	public boolean isNearToAlgorithm() {
    	return true;
    }
	
	final void initForNearToRelationship() {        	    	
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
    
    // calc intersections of conics A and B
    final void compute() {     
        /* D ... old defined points
         * P ... current points
         * Q ... new points
         *
         * We want to find a permutation of Q, so that the sum of squared 
         * distances between old point Di and new Point Qi is minimal.
         * The distances are weighed by Di's age (i.e. how long it has not
         * been reset by a finit intersection point).      
         */        
    	
    	// if there are only two points P[i] that are defined and equal
    	// we are in a singularity situation     
    	boolean noSingularity = !isSingularitySituation();
                     
		// remember the defined points D, so that Di = Pi if Pi is finite        
		// and set age
		for (i=0; i < 4; i++) {
			boolean finite = P[i].isFinite();
			
			if (noSingularity && finite)  { 
				D[i].setCoords(P[i]);   
				age[i] = 0;
			} else {                
				age[i]++;
			}				
			
			// update alive state
			isPalive[i] = isPalive[i] || finite || P[i].labelSet;
		}                 
                     
        // calc new intersection Points Q        
        intersectConics(A, B, Q);      
        
        // for limited paths we have to distinguish between intersection points Q
        // that lie on both limited paths or not. This is important for choosing
        // the right permutation in setNearTo()
        if (isLimitedPathSituation) {
        	updateQonPath();
        }
                 
        if (firstIntersection) {
        // init points in order P[0], P[1] , ...
            int count=0;
            for (i=0; i < Q.length; i++) {
            	// 	make sure interesection points lie on limited paths   
                if (Q[i].isDefined() && pointLiesOnBothPaths(Q[i])) {              
                    P[count].setCoords(Q[i]);
                    D[count].setCoords(P[count]);
                    firstIntersection = false;
                    count++;
                }
            }            
            return;
        }
        
        // calc distance table of defined points D and new points Q
        distanceTable(D, age,meanDistance, Q, distTable);           
                
        // find permutation and calculate new mean distances
        distMemoryIndex++;
        if (distMemoryIndex == AlgoIntersectConics.DIST_MEMORY_SIZE) 
        	distMemoryIndex = 0;
        setNearTo(P, isPalive, Q, isQonPath, distTable, meanDistance, distMemory, 
        		distMemoryIndex, pointList);
        
        // 	make sure interesection points lie on limited paths
        if (isLimitedPathSituation)   
        	handleLimitedPaths();        
    }    
    
    /**
     * Checks whether the computed intersection points really lie on
     * the limited paths. Note: points D[] and P[] may be changed here.     
     */
    private void handleLimitedPaths() {
    	//  singularity check
        boolean noSingularity = !isSingularitySituation();
                              
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
    	return A.isIntersectionPointIncident(P, Kernel.MIN_PRECISION) 
				&& B.isIntersectionPointIncident(P, Kernel.MIN_PRECISION);
    }
    
	/**
	 * Returns wheter we are in a singularity situation. This is the case whenever
	 *  there are only two points P[i] that are defined and equal.	 
	 */ 
    private boolean isSingularitySituation() {
    	int count = 0;
    	int index[] = new int[P.length];
    	
    	for (int i=0; i < P.length; i++) {
    		if (P[i].isDefined()) {
    			index[count] = i;
    			count++;
    			if (count > 2) return false;
    		}
    	}
    	
    	// we have a singularity if there are two defined points
    	// that are equal
    	return (count == 2 && P[index[0]].equals(P[index[1]]));
    	//System.out.println("Singularity: " + ret);    	
    }
    
    // calc four intersection Points of conics A and B.
    // write result into points
    final void intersectConics(GeoConic conic1, GeoConic conic2, 
                                        GeoPoint[] points) {
    	    	 
    	if (!(conic1.isDefined() && conic2.isDefined())) {
    		 for (int i=0; i < points.length; i++) 
    		 	points[i].setUndefined();
    		 return;
    	}
    	
    	boolean ok = false;
    	int i, solnr = 0;                                     
        // equal conics have no intersection points
        if (conic1.equals(conic2)) {
            for (i=0; i < points.length; i++) {
                points[i].setUndefined();
            }            
            return;
        }                       
        
      // STANDARD PROCEDURE
        double epsilon = Kernel.STANDARD_PRECISION;
       
        while (!ok && epsilon <= Kernel.MIN_PRECISION) { 
            kernel.setEpsilon(epsilon);            
            
            // input is allready degenerate
            if (conic1.isLineConic()) {
                intersectWithDegenerate(conic2, conic1, points);
                ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
            }
            else if (conic2.isLineConic()) {
                intersectWithDegenerate(conic1, conic2, points);
                ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
            }
            // standard case
            else {            
                // find degenerate conics through intersection points
                solnr = calcDegenerates(conic1.A, conic2.A, degConic, epsilon);            
                if (solnr > 0) { // there are solutions
                    // intersect degenerates conic with conic1
                    for (i=0; i < solnr; i++) {                    	
                        intersectWithDegenerate(conic1, degConic[i], points);
                        ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
                        if (ok) break; // solution found -> leaf for-loop

                        // calculated points are not on both conics!
                        // try again with conic2            
                        intersectWithDegenerate(conic2, degConic[i], points);
                        ok = testPoints(conic1, conic2, points, Kernel.MIN_PRECISION);
                        if (ok) break; // solution found -> leaf for loop
                    }                                
                }     
            }            
            // try it with lower precision     
            epsilon *= 10.0;                        
        }            
        kernel.resetPrecision();
        
        // NOTHING WORKED
        if (!ok) {
            //System.err.println("INTERSECTING CONICS FAILED (epsilon = " + epsilon/10.0 + ")");        
            // INTERSECTION FAILED
            for (i=0; i < points.length; i++) points[i].setUndefined();                                       
        }
    }   
    
    /**
     * Intersect conic with degenerate conic degConic. Write result into
     * points.
     */
    final static private void intersectWithDegenerate(GeoConic conic, GeoConic degConic,
                                               GeoPoint [] points) {
        if (degConic.isDefined()) {
            switch (degConic.getType()) {
                case GeoConic.CONIC_INTERSECTING_LINES:
                case GeoConic.CONIC_PARALLEL_LINES:                                    
                    AlgoIntersectLineConic.intersectLineConic(degConic.lines[0], conic, points);
                    points[2].setCoords(points[0]);
                    points[3].setCoords(points[1]);
                    AlgoIntersectLineConic.intersectLineConic(degConic.lines[1], conic, points);
                    return;

				case GeoConic.CONIC_EMPTY: 
					// this shouldn't happen: try it with doubleline conic
					degConic.enforceDoubleLine();
					//System.err.println("intersectWithDegenerate: empty degenerate conic, try double line");	
					//degConic.setToSpecific();
					//System.err.println("degConic: " + degConic);
					
                case GeoConic.CONIC_DOUBLE_LINE:                    
                    AlgoIntersectLineConic.intersectLineConic(degConic.lines[0], conic, points);
                    points[2].setUndefined();
                    points[3].setUndefined();
                    return;
                    
                case GeoConic.CONIC_SINGLE_POINT:                                        
                    //System.err.println("intersectConics: single point: " + p);                    
                    points[0].setCoords(degConic.getSinglePoint());                    
                    points[1].setUndefined();
                    points[2].setUndefined();
                    points[3].setUndefined();
                    return;                                                            
            }        
        }   
                
        // something went wrong: no intersections
        //System.err.println("intersectWithDegenerate: undefined degenerate conic, type:  " + degConic.type);
        for (int i=0; i < 4; i++) points[i].setUndefined();                   
        
       // System.err.println("degConic type" + degConic.getType());
       // for (int i=0; i < 6; i++) 
        //    System.out.println(" A[" + i + "] = " +degConic.A[i]);
    }
    
    /**
     * Tests if at least one point lies on conics A and B.
     */
    final private boolean testPoints(GeoConic A, GeoConic B, GeoPoint[] P, double eps) {
        boolean foundPoint = false;      
        for (int i=0; i < P.length; i++) {
            if (P[i].isDefined()) {                                         
                if (!(A.isOnFullConic(P[i], eps) && B.isOnFullConic(P[i], eps))) 
                	P[i].setUndefined();                
                else
                	foundPoint = true;
            }            
        }            
        return foundPoint;        
    }
    
    /**
     * Solve the cubic equation det(A + x B) = 0 and set degenerate conics C =
     * A + x B for all solutions x.
     * @param deg are the degenerate conics
     * @return number of conics set
     */      
    final private int calcDegenerates(double[] origFlatA, double[] origFlatB, 
                                             GeoConic [] deg, double eps) {                                                                                                                                                                                                            
        int solnr, i, j;
        double [] sol = new double[4];
        double [] flatDeg = new double[6]; // flat matrix of degenerate conic  
        double [] flatA = new double[6]; // flat matrix of conic A
        double [] flatB = new double[6]; // flat matrix of conic B
        //boolean equationExchanged = false;
        
        // normalize flat matrices
        double maxA=0, maxB=0;
        for (i=0; i<6; i++) {
        	flatA[i] = origFlatA[i];
        	double absA = Math.abs(flatA[i]);
        	if (absA > maxA) maxA = absA;
        	
        	flatB[i] = origFlatB[i];
        	double absB = Math.abs(flatB[i]);
        	if (absB > maxB) maxB = absB;
        }
        // divide by max coeff
        for (i=0; i<6; i++) {
        	flatA[i] = flatA[i] / maxA;        	
        	flatB[i] = flatB[i] / maxB;         	
        }
                 
        /*
        // AVOID HIGHLY DEGENERATE CASE:
        // TEST wheter conics A and B have same submatrix S
        // => degnerate is single line        
        if ( (Math.abs(flatA[0] - flatB[0]) <= eps) &&
             (Math.abs(flatA[1] - flatB[1]) <= eps) &&
             (Math.abs(flatA[3] - flatB[3]) <= eps) ) 
        {
            sol[0] = -1.0;            
            // set single line matrix
            flatDeg[0] = 0.0;
            flatDeg[1] = 0.0;
            flatDeg[3] = 0.0;
            flatDeg[2] = flatA[2] - flatB[2];
            flatDeg[4] = flatA[4] - flatB[4];
            flatDeg[5] = flatA[5] - flatB[5];
            // classify degenerate conic
            deg[0].setMatrixFromArray(flatDeg); 
            return 1;            
        }
        */
                                          
        // STANDARD CASE: solve cubic equation        
        // sol[0] + sol[1] x + sol[2] x� + sol[3] x� = 0        
        // constant
        sol[0] =    flatA[2] * (flatA[0] * flatA[1] - flatA[3] * flatA[3])
                  + flatA[4] * (2.0 * flatA[3] * flatA[5] - flatA[1] * flatA[4])                                             
                  - flatA[0] * flatA[5] * flatA[5];        
        // x
        sol[1] =    flatB[0] * (flatA[1] * flatA[2] - flatA[5] * flatA[5])
                  + flatB[1] * (flatA[0] * flatA[2] - flatA[4] * flatA[4])                                  
                  + flatB[2] * (flatA[0] * flatA[1] - flatA[3] * flatA[3])
                  + 2.0 * (
                      flatB[3] * (flatA[4] * flatA[5] - flatA[2] * flatA[3])
                    + flatB[4] * (flatA[3] * flatA[5] - flatA[1] * flatA[4])
                    + flatB[5] * (flatA[3] * flatA[4] - flatA[0] * flatA[5])
                  );                              
        // x�
        sol[2] =    flatA[0] * (flatB[1] * flatB[2] - flatB[5] * flatB[5])              
                  + flatA[1] * (flatB[0] * flatB[2] - flatB[4] * flatB[4])
                  + flatA[2] * (flatB[0] * flatB[1] - flatB[3] * flatB[3])
                  + 2.0 * (                                                                        
                        flatA[3] * (flatB[4] * flatB[5] - flatB[2] * flatB[3])                  
                      + flatA[4] * (flatB[3] * flatB[5] - flatB[1] * flatB[4])                  
                      + flatA[5] * (flatB[3] * flatB[4] - flatB[0] * flatB[5])
                  );                                                      
        // x�
        sol[3] =    flatB[2] * (flatB[0] * flatB[1] - flatB[3] * flatB[3])
                  + flatB[4] * (2.0 * flatB[3] * flatB[5] - flatB[1] * flatB[4]) 
                  - flatB[0] * flatB[5] * flatB[5];               
        
        /*
        // the coefficients of the cubic equation should not get too big      
        double max=0;
        for (i=0; i<4; i++) {
        	double abs = Math.abs(sol[i]);
        	if (abs > max) max = abs;        		        	                                    
        }        
        if (max > 10) {        	
	        for (i=0; i<4; i++) {
	        	sol[i] = sol[i] / max;        	        		        	                                   
	        }
        }
        */
        
        /*
         * I don't think that this code is necessary any more
         * (January 21, 2007)
         * 
        // sol[3] should not be too small or too big
        double abs3 = Math.abs(sol[3]);
        double abs0 = Math.abs(sol[0]);
        if ((abs3 < 1.0d && abs0 > 1.0d) ||
            (abs0 > 1.0d && abs3 > abs0)) 
        { 
            // change equation from {0, 1, 2, 3} to {3, 2, 1, 0}
            // i.e. intersect(A,B) = intersect(B,A)
            
        	// TODO: remove
        	System.out.println("CHANGE EQUATION");            
            double temp = sol[0];
            sol[0] = sol[3];
            sol[3] = temp;
            temp = sol[1];
            sol[1] = sol[2];
            sol[2] = temp;                        
            equationExchanged = true;
        }  */   
                     
      
        //System.out.println(sol[3] + " x^3 + " + sol[2] + " x^2 + " 
        //                 + sol[1] + " x + "  + sol[0] );
         
        // solve cubic equation        
        solnr = eqnSolver.solveCubic(sol, sol);   

       // for (i=0;i<solnr;i++) {
        //    System.out.println("sol[" + i + "] = " + sol[i]);
       // }
        
        // set degenerate conics        
        // C = x A + B    
        /*
        if (equationExchanged) {
            for (i=0; i < solnr; i++) {                
               for (j=0; j < 6; j++) {                                
                    flatDeg[j] = (sol[i] * flatA[j] + flatB[j]);                                  
                }                
                // classify degenerate conic
                deg[i].setMatrixFromArray(flatDeg);                
            }        
        } 
        // C = A + x B
        else { */        
            for (i=0; i < solnr; i++) {               
                for (j=0; j < 6; j++) {   
                    flatDeg[j] = (flatA[j] + sol[i] * flatB[j]);
                }                
                // classify degenerate conic
                deg[i].setMatrixFromArray(flatDeg);
            }
        //}        
        return solnr;
    }
    
    
/***************************************************************
 * NEAREST DISTANCE RELATION
 ***************************************************************/              
    
    /**
     * set tabel[i][j] to square distance between D[i] and Q[j]. 
     * distSqr(D[i], Q[j]) := (D[i] - Q[j])^2 + age[i] + meanDistance[i].
     * age[i] tells for every D[i], how long it has been undefined
     * (old points' distances should be larger).
     * meanDistance[i] is the mean change of the point's location within the
     * last couple of computations (fixed points should stay at their place).
     * Undefined (NaN) or infinite distances are set to max of all defined
     * distances + 1. If there are no defined distances, all distances
     * are set to 0.
     */
    final public static void distanceTable(GeoPoint [] D, int[] age, double[] meanDistance,
                                           GeoPoint [] Q, double[][] table) {
        int i, j;        
        boolean foundUndefined = false;
        double dist, max = -1.0;
        
        // calc all distances and maximum distance (max)
        for (i=0; i < D.length; i++) {
        //	checkFixedPoint = meanDistance[i] == 0.0;
        	for (j=0; j < Q.length; j++) {            
                dist = D[i].distanceSqr(Q[j]) + age[i] + meanDistance[i];
                
                if (Double.isInfinite(dist) || Double.isNaN(dist)) {
                    dist = -1; // mark as undefined
                    foundUndefined = true;
                }          
                else if (dist > max) {                                      
                    max = dist;                    
                }
                table[i][j] = dist;                
            }            
        }
        
        if (foundUndefined) {                                    
            max = max + 1; 
            // set undefined distances to max (marked as -1)
            for (j=0; j < Q.length; j++) {
                for (i=0; i < D.length; i++) {
                // check if entry is marked as undefined (Q[j])
                    if (table[i][j] == -1) {
                        // set all distances to Q[j] to max+1
                        table[i][j] = max;            
                    } 
                }
            }
        }
                
        /*
        for (i=0; i < D.length; i++) {
            System.out.println("D[" + i + "] = " + D[i] +
            "   Q[" + i + "] = " + Q[i]);
        }        
        
        // print table
           for (i=0; i < D.length; i++) {                
                for (j=0; j < Q.length; j++) {             
                // check if entry is marked as undefined (Q[j])                    
                        // set all distances to Q[j] to max+1
                        System.out.print(table[i][j] + "\t");            
                }
                System.out.println();                
            }        
        System.out.println();                
         */
    }
    
    

    
    /**
     * Sets Pi = Qj according to near to heuristic (using the closest
     * pairs of points in ascending distance order).          
     * 
     *  Additionally the new mean distances are computed using the distTable, the
     *  distMemory and the current distMemoryIndex (0..meanDistance.length-1)
     *  Note: distMemory[i][] holds the old distance changes of Pi
     *    
     *  For limitedPaths we also have to make sure that we only use points from Q 
     *  to set P that really lie on both paths.  
     */
    final static void setNearTo(GeoPoint[] P, boolean [] isPalive,
    							GeoPoint[] Q, boolean [] isQonPath,    							    							
    							double[][] distTable, 
    							double [] meanDistance,
								double[][] distMemory, 
								int distMemoryIndex, PointPairList pointList) {
    	int indexP, indexQ;
    	pointList.clear();
    	for (indexP = 0; indexP < P.length; indexP++) {        		
    		for (indexQ = 0; indexQ < Q.length; indexQ++) {    		    		    	        	
        		// sorted inserting
        		pointList.insertPointPair(
        				indexP, 
        				isPalive[indexP],
						indexQ, 						
						isQonPath[indexQ],
        				distTable[indexP][indexQ]);
        	}        	
    	}    	    
    	
    	//System.out.println(pointList.toString());
    	//System.out.flush();
 
    	double newDist;
    	PointPair pair;
        while (!pointList.isEmpty()) {
        	// take first pair from pointList
        	pair = pointList.getHead();	
        	indexP = pair.indexP;
        	indexQ = pair.indexQ;                	
        	
        	// remove all other pairs with P[indexP] or Q[indexQ] from list
        	pointList.removeAllPairs(pair);             	        	
         	
        	// P[indexP] = Q[indexQ]
            P[indexP].setCoords(Q[indexQ]);
            
            // calculate new mean distance of P[indexP]
            if (P[indexP].isDefined()) {
	            newDist =  (distTable[indexP][indexQ] - meanDistance[indexP])
											  / meanDistance.length;
	            meanDistance[indexP] = meanDistance[indexP] 
									- distMemory[indexP][distMemoryIndex]
									+ newDist;
	            if (meanDistance[indexP] < Kernel.STANDARD_PRECISION)
	            	meanDistance[indexP] = 0.0;
	            distMemory[indexP][distMemoryIndex] = newDist;
	            	            
	            //System.out.println("mean distance of "  + P[indexP].label + ": " +
	            //		meanDistance[indexP]);
            }
        }
    }
    
    
}




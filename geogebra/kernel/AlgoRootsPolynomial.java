/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.PolyFunction;
import geogebra.kernel.roots.RealRootFunction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * TODO: extend for rational functions
 * Finds all real roots of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoRootsPolynomial extends AlgoIntersect {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int ROOTS = 0;
    private static final int INTERSECT_POLYNOMIALS = 1;
    private static final int INTERSECT_POLY_LINE = 2;
    private int mode;

    GeoFunction f, g; // input  (g for intersection of polynomials)   
    private GeoLine line; // input (for intersection of polynomial with line)
    private GeoPoint[] rootPoints; // output, inherited from AlgoIntersect 
    //private int rootPointsLength;

    private String[] labels;
    private boolean initLabels, setLabels;
    private EquationSolver eqnSolver;
    double[] curRoots = new double[30]; // current roots
    int curRealRoots;

    Function yValFunction;
    // used for AlgoExtremumPolynomial, see setRootPoints()
    private Function diffFunction; // used for intersection of f and g
    private GeoPoint tempPoint;

    /**
     * Computes all roots of f
     */
    public AlgoRootsPolynomial(
        Construction cons,
        String[] labels,
        GeoFunction f) {
        this(cons, labels, !cons.isSuppressLabelsActive(), f, null, null);
    }

    /**
     * Intersects polynomials f and g.
     */
    AlgoRootsPolynomial(Construction cons, GeoFunction f, GeoFunction g) {
        this(cons, null, false, f, g, null);
    }

    /**
     * Intersects polynomial f and line l.
     */
    AlgoRootsPolynomial(Construction cons, GeoFunction f, GeoLine l) {
        this(cons, null, false, f, null, l);
    }

    private AlgoRootsPolynomial(
        Construction cons,
        String[] labels,
        boolean setLabels,
        GeoFunction f,
        GeoFunction g,
        GeoLine l) {
        super(cons);
        this.f = f;
        this.g = g;
        line = l;

        tempPoint = new GeoPoint(cons);
        
        // set mode
        if (g != null)
            mode = INTERSECT_POLYNOMIALS;
        else if (l != null)   {
            mode = INTERSECT_POLY_LINE;           
        } else
            mode = ROOTS;

        if (mode != ROOTS) { // for intersection of f and g resp. line
            diffFunction = new Function(kernel);
        }
        this.labels = labels;
        this.setLabels = setLabels; // should lables be used?

        eqnSolver = cons.getEquationSolver();

        //  make sure root points is not null
        int number = labels == null ? 1 : Math.max(1, labels.length);
        rootPoints = new GeoPoint[0];
        initRootPoints(number);
        initLabels = true;  
                
        setInputOutput(); // for AlgoElement    
        compute();        
        
        // show at least one root point in algebra view
        // this is enforced here:
        if (!rootPoints[0].isDefined()) {
        	rootPoints[0].setCoords(0,0,1);
        	rootPoints[0].update();
        	rootPoints[0].setUndefined();
        	rootPoints[0].update();
        }
    }

    /**
     * The given labels will be used for the resulting points.   
     */
    public void setLabels(String[] labels) {
        this.labels = labels;
        setLabels = true;

        // make sure that there are at least as many
        // points as labels
        if (labels != null)
            initRootPoints(labels.length);

        update();
    }

    String getClassName() {
        return "AlgoRootsPolynomial";
    }

    // for AlgoElement
    void setInputOutput() {
        switch (mode) {
            case ROOTS : // roots of f
                input = new GeoElement[1];
                input[0] = f;
                break;

            case INTERSECT_POLYNOMIALS : // intersection of f and g
                input = new GeoElement[2];
                input[0] = f;
                input[1] = g;
                break;

            case INTERSECT_POLY_LINE : //   intersection of f and line
                input = new GeoElement[2];
                input[0] = f;
                input[1] = line;
                break;
        }

        output = rootPoints;
        noUndefinedPointsInAlgebraView();
        setDependencies();
    }

    public GeoPoint[] getRootPoints() {
        return rootPoints;
    }

    GeoPoint[] getIntersectionPoints() {
        return rootPoints;
    }

    GeoPoint[] getLastDefinedIntersectionPoints() {
        return null;
    }       

    void compute() {
        switch (mode) {
            case ROOTS :
                // roots of f
                computeRoots();
                break;

            case INTERSECT_POLYNOMIALS :
                //  intersection of f and g
                computePolynomialIntersection();
                break;

            case INTERSECT_POLY_LINE :
                //  intersection of f and line
                computePolyLineIntersection();
                break;
        }

        setRootPoints(curRoots, curRealRoots);
    }

    // roots of f
    private void computeRoots() {
        if (f.isDefined()) {
            Function fun = f.getFunction();
            // get polynomial factors anc calc roots
            calcRoots(fun, 0);
        } else {
            curRealRoots = 0;
        }
    }

    //  intersection of f and g
    private void computePolynomialIntersection() {
        if (f.isDefined() && g.isDefined()) {	         	        
            yValFunction = f.getFunction();
            // get difference f - g
            Function.difference(f.getFunction(), g.getFunction(), diffFunction);                                 
            calcRoots(diffFunction, 0);
                                  
            // check if the intersection points are really on the functions
            // due to interval restrictions this might not be the case            
            for (int i = 0; i < curRealRoots; i++) {
            	if ( !(Math.abs( f.evaluate(curRoots[i]) - g.evaluate(curRoots[i]) ) < Kernel.MIN_PRECISION) ) {
            		removeRoot(i);
            		i--;
            	} 
            	           	               	
            }
            
        } else {
            curRealRoots = 0;
        }
    }

    //  intersection of f and line
    private void computePolyLineIntersection() {    	
        if (f.isDefined() && line.isDefined()) {
            yValFunction = f.getFunction();

            // check for vertical line a*x + c = 0: intersection at x=-c/a 
            if (kernel.isZero(line.y)) {
                double x = -line.z / line.x;
                curRoots[0] = x;
                curRealRoots = 1;
            }
            // standard case
            else {
                //  get difference f - line
                Function.difference(f.getFunction(), line, diffFunction);
                calcRoots(diffFunction, 0);
                
                // check if the intersection points really are on the line
                // this is important for segments and rays
                for (int i = 0; i < curRealRoots; i++) {
                	tempPoint.setCoords(curRoots[i], f.evaluate(curRoots[i]), 1.0);
                	if (! line.isIntersectionPointIncident(tempPoint, Kernel.MIN_PRECISION) ){
                		removeRoot(i);
                		i--;
                	}                	
                }
                
            }
        } else {
            curRealRoots = 0;
        }
    }

    /**
     * Calculates the roots of the given function resp. its derivative,
     * stores them in curRoots and
     * sets curRealRoots to the number of real roots found.
     * @param derivDegree: degree of derivative to compute roots from
     */
    final void calcRoots(Function fun, int derivDegree) {  
    	LinkedList factorList;    	
    	PolyFunction derivPoly = null;// only needed for derivatives
		RealRootFunction evalFunction = null; // needed to remove wrong extrema and inflection points 
    	
    	// get polynomial factors for this function
    	if (derivDegree > 0) {
    		// try to get the factors of the symbolic derivative
    		factorList = fun.getSymbolicPolynomialDerivativeFactors(derivDegree, true);
    		
    		// if this didn't work take the derivative of the numeric
    		// expansion of this function
    		if (factorList == null) {
    			derivPoly = fun.getNumericPolynomialDerivative(derivDegree);  	
    			evalFunction = derivPoly;
    		} else {
    			evalFunction = fun.getDerivative(derivDegree);
    		}
    	} else {
    		// standard case
    		factorList = fun.getPolynomialFactors(true);    		
    	}
    	
        double[] roots;
        int realRoots;
        curRealRoots = 0; // reset curRoots index 
        
    	// we got a list of polynomial factors
        if (factorList != null) { 
        	 // compute the roots of every single factor              
            Iterator it = factorList.iterator();
            while (it.hasNext()) {
            	PolyFunction polyFun = (PolyFunction) it.next();                       
            	
                //  update the current coefficients of polyFun
            	// (this is needed for SymbolicPolyFunction objects)
                if (!polyFun.updateCoeffValues()) {
                    //  current coefficients are not defined
                    curRealRoots = 0;
                    return;
                }

                // now let's compute the roots of this factor           
                //  compute all roots of polynomial polyFun
                roots = polyFun.getCoeffsCopy();   
                realRoots = eqnSolver.polynomialRoots(roots);                           
                addToCurrentRoots(roots, realRoots);                            
            }
        }         
        // we've got one factor, i.e. derivPoly
        else if (derivPoly != null) {          
            //  compute all roots of derivPoly
            roots = derivPoly.getCoeffsCopy();   
            realRoots = eqnSolver.polynomialRoots(roots);                           
            addToCurrentRoots(roots, realRoots);
        } else
			return;                      

        if (curRealRoots > 1) {
            // sort roots and eliminate duplicate ones
            Arrays.sort(curRoots, 0, curRealRoots);

            // eliminate duplicate roots
            double maxRoot = curRoots[0];            
            int maxIndex = 0;
            for (int i = 1; i < curRealRoots; i++) {
                if ((curRoots[i] - maxRoot) >  Kernel.MIN_PRECISION) {
                	maxRoot = curRoots[i];
	                maxIndex++;
	                curRoots[maxIndex] = maxRoot;
                }
            }
            curRealRoots = maxIndex + 1;
        }
        
        // for first or second derivative we only
        // want roots where the signs changed
        // i.e. we only want extrema and inflection points
        if (derivDegree > 0) {        	
        	ensureSignChanged(evalFunction);
        }
    }
    
    // remove roots where the sign of the function's values did not change    
    private void ensureSignChanged(RealRootFunction f) {      	    	
        double left, right, leftEval, rightEval;
        boolean signUnChanged;
        for (int i = 0; i < curRealRoots; i++) {
        	left  = curRoots[i] - DELTA;
        	right = curRoots[i] + DELTA;
        	// ensure we get a non-zero y value to the left
        	int count = 0;
        	while (Math.abs(leftEval = f.evaluate(left)) < DELTA && count++ < 100) 
        		left = left -  DELTA;        	        	
        		
        	// ensure we get a non-zero y value to the right
        	count = 0;
        	while (Math.abs(rightEval = f.evaluate(right)) < DELTA && count++ < 100)
        		right = right + DELTA;

        	//System.out.println("leftEval: " + leftEval + ", left: " + left);
        	//System.out.println("rightEval: " + rightEval + ", right: " + right);
        	
        	// check if the second derivative changed its sign here
        	signUnChanged = leftEval * rightEval > 0;
        	if (signUnChanged) {
        		// remove root[i]
        		removeRoot(i);
        		i--;
        	}			
        }
    }
    private static final double DELTA = Kernel.MIN_PRECISION * 10;

    // add first number of doubles in roots to current roots
    private void addToCurrentRoots(double[] roots, int number) {
        int length = curRealRoots + number;
        if (length >= curRoots.length) { // ensure space
            double[] temp = new double[2 * length];
            for (int i = 0; i < curRealRoots; i++) {
                temp[i] = curRoots[i];
            }
            curRoots = temp;
        }

        // insert new roots
        for (int i = 0; i < number; i++) {
            curRoots[curRealRoots + i] = roots[i];
        }
        curRealRoots += number;
    }

    final void removeRoot(int pos) {    
    	for (int i = pos+1; i < curRealRoots; i++) {
    		curRoots[i-1] = curRoots[i];
    	}    
        curRealRoots--;
    }

    // roots array and number of roots
    final void setRootPoints(double[] roots, int number) {
        initRootPoints(number);

        // now set the new values of the roots
        for (int i = 0; i < number; i++) {
           // System.out.println("root[" + i + "] = " + roots[i]);  
            if (yValFunction == null) {
            	// check if defined
            	//if (Double.isNaN(f.evaluate(roots[i]))) 
            	//	rootPoints[i].setUndefined();
            	//else
            		rootPoints[i].setCoords(roots[i], 0.0, 1.0); // root point
            } else { // extremum or turnal point
                rootPoints[i].setCoords(
                    roots[i],
                    yValFunction.evaluate(roots[i]),
                    1.0);
                
             // TODO: remove
              //  System.out.println("   " + rootPoints[i]);
                
            }
        }

        // all other roots are undefined
        for (int i = number; i < rootPoints.length; i++) {
            rootPoints[i].setUndefined();
        }

        if (setLabels)
            updateLabels(number);
    }

    // number is the number of current roots
    private void updateLabels(int number) {  
    	if (initLabels) {
    		GeoElement.setLabels(labels, rootPoints);
    		initLabels = false;
    	} else {	    
	        for (int i = 0; i < number; i++) {
	            //  check labeling      
	            if (!rootPoints[i].labelSet) {
	                if (labels == null || labels.length <= i) {
	                	String label = rootPoints[i].getIndexLabel(rootPoints[0].label);                    	
						rootPoints[i].setLabel(label);
	                }                        
	                else {
	                	String label = labels[i];                    	
	                    //if (label == null)
	                    //	label = rootPoints[i].getIndexLabel(null);
	                    rootPoints[i].setLabel(label);
	                }
	            }
	        }
    	}
        
        // all other roots are undefined
        for (int i = number; i < rootPoints.length; i++) {
        	rootPoints[i].setUndefined();
        }
    }
    
    /**
     * Removes only one single output element if possible. 
     * If this is not possible the whole algorithm is removed.
     */
    void remove(GeoElement output) {
    	// only single undefined points may be removed       
        for (int i = 0; i < rootPoints.length; i++) {
        	if (rootPoints[i] == output && !rootPoints[i].isDefined()) {
        		removeRootPoint(i);      		
        		return;
        	}            
        }
    	
        // if we get here removing output was not possible
        // so we remove the whole algorithm
        super.remove();
    }

    private void initRootPoints(int number) {
        // make sure that there are enough points   
        if (rootPoints.length < number) {
            GeoPoint[] temp = new GeoPoint[number];
            for (int i = 0; i < rootPoints.length; i++) {
                temp[i] = rootPoints[i];
                temp[i].setCoords(0, 0, 1); // init as defined
            }
            for (int i = rootPoints.length; i < temp.length; i++) {
                temp[i] = new GeoPoint(cons);
                temp[i].setCoords(0, 0, 1); // init as defined
                temp[i].setParentAlgorithm(this);
            }
            rootPoints = temp;
            output = rootPoints;
        }
    }
    
    private void removeRootPoint(int pos) {
    	rootPoints[pos].doRemove();
    	
    	// build new rootPoints array without the removed point
    	GeoPoint[] temp = new GeoPoint[rootPoints.length - 1];
    	int i;
    	for (i=0; i < pos; i++) 
    		temp[i] = rootPoints[i];        		
    	for (i=pos+1; i < rootPoints.length; i++) 
    		temp[i-1] = rootPoints[i];
    	rootPoints = temp;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Root"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
        }
        sb.append(f.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Root"));
        }
        return sb.toString();
    }

}

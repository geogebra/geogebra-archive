/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


import geogebra.kernel.arithmetic.PolyFunction;
import geogebra.kernel.complex.Complex;
import geogebra.kernel.complex.ComplexPoly;
import geogebra.kernel.roots.RealRoot;

public class EquationSolver { 
		
	private static final double LAGUERRE_EPS = 1E-5;
	private static final double BISECT_EPS = 0.1;
	
	private double epsilon = Kernel.STANDARD_PRECISION;
	
	private RealRoot rootPolisher;
	//private ExtremumFinder extrFinder;
	
    public EquationSolver(Kernel kernel) {		
		// we need someone to polish our roots
		rootPolisher = new RealRoot();
		//extrFinder = kernel.getExtremumFinder();
    }
    
    void setEpsilon(double eps) {
    	epsilon = eps;
    }
    
	/**
	 * Computes all roots of a polynomial using Laguerre's method for
	 * degrees > 3.
	 * The roots are polished and only distinct roots are returned.
	 * @param roots: array with the coefficients of the polynomial 
	 * @return number of realRoots found
	 */
	final public int polynomialRoots(double [] roots) {			
		int realRoots;						
		switch (roots.length - 1) { // degree of polynomial
			case 0:
				realRoots = 0;
				break;
				
			case 1:
				roots[0] = -roots[0] / roots[1];
				realRoots = 1;
				break;
			
			case 2:
				realRoots = solveQuadratic(roots, roots);
				break;
			
			case 3: 
				realRoots = solveCubic(roots, roots);
				break;
					
			default:
				realRoots = laguerreAll(roots);		
		}
		 				
		// solveQuadratic and solveCubic may return -1
		return Math.max(0, realRoots);
	}
       
     /**
     * Solves the quadratic whose coefficients are in the <code>eqn</code> 
     * array and places the non-complex roots back into the same array,
     * returning the number of roots.  The quadratic solved is represented
     * by the equation:
     * <pre>
     *     eqn = {C, B, A};
     *     ax^2 + bx + c = 0
     * </pre>
     * A return value of <code>-1</code> is used to distinguish a constant
     * equation, which might be always 0 or never 0, from an equation that
     * has no zeroes.
     * @param equ the array that contains the quadratic coefficients
     * @return the number of roots, or <code>-1</code> if the equation is
     *      a constant
     */
    final public int solveQuadratic(double eqn[]) {
        return solveQuadratic(eqn, eqn);
    }

    /**
     * Solves the quadratic whose coefficients are in the <code>eqn</code> 
     * array and places the non-complex roots into the <code>res</code>
     * array, returning the number of roots.
     * The quadratic solved is represented by the equation:
     * <pre>
     *     eqn = {C, B, A};
     *     ax^2 + bx + c = 0
     * </pre>
     * A return value of <code>-1</code> is used to distinguish a constant
     * equation, which might be always 0 or never 0, from an equation that
     * has no zeroes.
     * @return the number of roots, or <code>-1</code> if the equation is
     *  a constant.
     */
    final public int solveQuadratic(double eqn[], double res[]) {
        double a = eqn[2];
        double b = eqn[1];
        double c = eqn[0];
        int roots = 0;
        if (Math.abs(a) < epsilon) {
            // The quadratic parabola has degenerated to a line.
            if (Math.abs(b) < epsilon)
				// The line has degenerated to a constant.
				return -1; 
            res[roots++] = -c / b;
        } else {
            // From Numerical Recipes, 5.6, Quadratic and Cubic Equations
            double d = b * b - 4.0 * a * c;
            if (Math.abs(d) < epsilon) 
               res[roots++] = - b /(2.0 * a);
            else {
                if (d < 0.0)
					// If d < 0.0, then there are no roots
					return 0;
                d = Math.sqrt(d);
                // For accuracy, calculate one root using:
                //     (-b +/- d) / 2a
                // and the other using:
                //     2c / (-b +/- d)
                // Choose the sign of the +/- so that b+d gets larger in magnitude
                if (b < 0.0) {
                    d = -d;
                }
                double q = (b + d) / -2.0;
                // We already tested a for being 0 above
                res[roots++] = q / a;            
                res[roots++] = c / q;            
            }
        }
        return roots;
    }
      
     /**
     * Solves the cubic whose coefficients are in the <code>eqn</code> 
     * array and places the non-complex roots back into the same array, 
     * returning the number of roots.  The solved cubic is represented 
     * by the equation:
     * <pre>
     *     eqn = {c, b, a, d}
     *     dx^3 + ax^2 + bx + c = 0
     * </pre>
     * A return value of -1 is used to distinguish a constant equation
     * that might be always 0 or never 0 from an equation that has no
     * zeroes.
     * @param eqn an array containing coefficients for a cubic
     * @return the number of roots, or -1 if the equation is a constant.
     */
    final public int solveCubic(double eqn[]) {
        return solveCubic(eqn, eqn);
    }

    /**
     * Solve the cubic whose coefficients are in the <code>eqn</code>
     * array and place the non-complex roots into the <code>res</code>
     * array, returning the number of roots.
     * The cubic solved is represented by the equation:
     *     eqn = {c, b, a, d}
     *     dx^3 + ax^2 + bx + c = 0
     * A return value of -1 is used to distinguish a constant equation,
     * which may be always 0 or never 0, from an equation which has no
     * zeroes.
     * @return the number of roots, or -1 if the equation is a constant
     */
    final public int solveCubic(double eqn[], double res[]) {
    // From Numerical Recipes, 5.6, Quadratic and Cubic Equations
    // case discriminant == 0 added by Markus Hohenwarter, 20.1.2002
        
        double d = eqn[3];
        if (Math.abs(d) < epsilon)
			// The cubic has degenerated to quadratic (or line or ...).
            return solveQuadratic(eqn, res);
        double a = eqn[2] / d;
        double b = eqn[1] / d;
        double c = eqn[0] / d;
        int roots = 0;
        double Q = (a * a - 3.0 * b) / 9.0;
        double R = (2.0 * a * a * a - 9.0 * a * b + 27.0 * c) / 54.0;
        double R2 = R * R;
        double Q3 = Q * Q * Q;
        double discriminant = R2 - Q3;
        a = a / 3.0;

        if (Math.abs(discriminant) < epsilon) {            
            if (Q >= epsilon) {
                // two real solutions
                Q = Math.sqrt(Q);
                if (R < 0) Q = -Q;
                res[roots++] = -2.0 * Q - a;
                res[roots++] = Q - a;
            } 
            else { // Q is zero            
                // one real solution
                res[roots++] = -a;            
            }            
        } else {
            if (R2 < Q3) { // => Q3 > 0.0                
                double theta = Math.acos(R / Math.sqrt(Q3));
                Q = -2.0 * Math.sqrt(Q);
                if (res == eqn) {
	                // Copy the eqn so that we don't clobber it with the
	                // roots.  This is needed so that fixRoots can do its
	                // work with the original equation.
	                eqn = new double[4];
	                System.arraycopy(res, 0, eqn, 0, 4);
                }
                res[roots++] = Q * Math.cos(theta / 3.0) - a;
                res[roots++] = Q * Math.cos((theta - Math.PI * 2.0)/ 3.0) - a;
                res[roots++] = Q * Math.cos((theta + Math.PI * 2.0)/ 3.0) - a;
                fixRoots(res, eqn); 
            } else {                         
                boolean neg = (R < 0.0);
                double S = Math.sqrt(discriminant);
                if (neg) {
                    R = -R;
                }
                double A = Math.pow(R + S, 1.0 / 3.0);
                if (!neg) {
                    A = -A;
                }
                double B = (Math.abs(A) < epsilon) ? 0.0 : (Q / A);
                res[roots++] = (A + B) - a;
            }
        }
        return roots;
    }

    /*
     * This pruning step is necessary since solveCubic uses the
     * cosine function to calculate the roots when there are 3
     * of them.  Since the cosine method can have an error of
     * +/- 1E-14 we need to make sure that we don't make any
     * bad decisions due to an error.
     * 
     * If the root is not near one of the endpoints, then we will
     * only have a slight inaccuracy in calculating the x intercept
     * which will only cause a slightly wrong answer for some
     * points very close to the curve.  While the results in that
     * case are not as accurate as they could be, they are not
     * disastrously inaccurate either.
     * 
     * On the other hand, if the error happens near one end of
     * the curve, then our processing to reject values outside
     * of the t=[0,1] range will fail and the results of that
     * failure will be disastrous since for an entire horizontal
     * range of test points, we will either overcount or undercount
     * the crossings and get a wrong answer for all of them, even
     * when they are clearly and obviously inside or outside the
     * curve.
     * 
     * To work around this problem, we try a couple of Newton-Raphson
     * iterations to see if the true root is closer to the endpoint
     * or further away.  If it is further away, then we can stop
     * since we know we are on the right side of the endpoint.  If
     * we change direction, then either we are now being dragged away
     * from the endpoint in which case the first condition will cause
     * us to stop, or we have passed the endpoint and are headed back.
     * In the second case, we simply evaluate the slope at the
     * endpoint itself and place ourselves on the appropriate side
     * of it or on it depending on that result.
     */
    private static void fixRoots(double res[], double eqn[]) {
        double myEPSILON = 1E-5;  

        for (int i = 0; i < 3; i++) {
            double t = res[i];
            if (Math.abs(t) < myEPSILON) {
            res[i] = findZero(t, 0, eqn);
            } else if (Math.abs(t - 1) < myEPSILON) {
            res[i] = findZero(t, 1, eqn);
            }
        }
    }

    private static double solveEqn(double eqn[], int order, double t) {
        double v = eqn[order];
        while (--order >= 0) {
            v = v * t + eqn[order];
        }
        return v;
    }

    private static double findZero(double t, double target, double eqn[]) {
        double slopeqn[] = {eqn[1], 2*eqn[2], 3*eqn[3]};
        double slope;
        double origdelta = 0;
        double origt = t;
        while (true) {
            slope = solveEqn(slopeqn, 2, t);
            if (slope == 0.0)
				// At a local minima - must return
				return t;
            double y = solveEqn(eqn, 3, t);
            if (y == 0.0)
				// Found it! - return it
				return t;
            // assert(slope != 0 && y != 0);
            double delta = - (y / slope);
            // assert(delta != 0);
            if (origdelta == 0.0) {
            origdelta = delta;
            }
            if (t < target) {
            if (delta < 0) return t;
            } else if (t > target) {
            if (delta > 0) return t;
            } else
				return (delta > 0
				    ? (target + java.lang.Double.MIN_VALUE)
				    : (target - java.lang.Double.MIN_VALUE));
            double newt = t + delta;
            if (t == newt)
				// The deltas are so small that we aren't moving...
				return t;
            if (delta * origdelta < 0) {
            // We have reversed our path.
            int tag = (origt < t
                   ? getTag(target, origt, t)
                   : getTag(target, t, origt));
            if (tag != INSIDE)
				// Local minima found away from target - return the middle
                return (origt + t) / 2;
            // Local minima somewhere near target - move to target
            // and let the slope determine the resulting t.
            t = target;
            } else {
            t = newt;
            }
        }
    }
    
    private static final int BELOW = -2;
    private static final int LOWEDGE = -1;
    private static final int INSIDE = 0;
    private static final int HIGHEDGE = 1;
    private static final int ABOVE = 2;

    /*
     * Determine where coord lies with respect to the range from
     * low to high.  It is assumed that low <= high.  The return
     * value is one of the 5 values BELOW, LOWEDGE, INSIDE, HIGHEDGE,
     * or ABOVE.
     */
    private static int getTag(double coord, double low, double high) {
        if (coord <= low)
			return (coord < low ? BELOW : LOWEDGE);
        if (coord >= high)
			return (coord > high ? ABOVE : HIGHEDGE);
        return INSIDE;
    }
    
/* **************************************************/
    
    private static final double LAGUERRE_START = -1;
    
	/**
	 * Calculates all roots of a polynomial given by eqn using Laguerres method.
	 * Polishes roots found. The roots are stored in eqn again.
	 * @param eqn: coefficients of polynomial	 
	 */	
	private int laguerreAll(double [] eqn) {
		// for fast evaluation of polynomial (used for root polishing)
		PolyFunction polyFunc = new PolyFunction(eqn);
		PolyFunction derivFunc = polyFunc.getDerivative();

		/*
		double estx = 0;		
		try {
			estx = rootPolisher.newtonRaphson(polyFunc, LAGUERRE_START);	
			System.out.println("newton estx: " + estx);
			if (Double.isNaN(estx)) {
				estx = LAGUERRE_START;
				System.out.println("corrected estx: " + estx);
			}
		} catch (Exception e) {}
		*/			
	
		// calc roots with Laguerre method
		ComplexPoly poly = new ComplexPoly(eqn);	
		Complex [] complexRoots = poly.roots(false, new Complex(LAGUERRE_START, 0)); // don't polish here 
	
		// get the roots from Laguerre method
		int realRoots = 0;		
		double root, polish;
		double [] val;		
				
		for (int i=0; i < complexRoots.length; i++) {
			// System.out.println("complexRoot[" + i + "] = " + complexRoots[i]);	
			
			// let's polish all complex roots to get all real roots
			// take (a + b) as a real start value for the complex root (a + ib)			
			root = complexRoots[i].getReal() + complexRoots[i].getImag();
			val = polyFunc.evaluateDerivFunc(root); // function's and first derivative's values
				
			try {
				if (Math.abs(val[1]) > 0.1) {	// f'(root) big enough for Mr. Newton
					polish = rootPolisher.newtonRaphson(polyFunc, root);
					// System.out.println("Polish newtonRaphson: " + polish);	
				} else {					
					// check if root is bounded in intervall [root-eps, root+eps]
					double f_left = polyFunc.evaluate(root - BISECT_EPS);
					double f_right = polyFunc.evaluate(root + BISECT_EPS);
					boolean bounded = f_left * f_right < 0.0; 
					if (bounded) {						
						//	small f'(root): don't go too fare from our laguerre root !	
						polish = rootPolisher.bisectNewtonRaphson(polyFunc, root - BISECT_EPS, root + BISECT_EPS);
						// System.out.println("Polish bisectNewtonRaphson: " + polish);
					} else {
						// the root is not bounded: give Mr. Newton a chance
						polish = rootPolisher.newtonRaphson(polyFunc, root);
						// System.out.println("Polish newtonRaphson: " + polish);
					}					
				}
				root = polish;
				// System.out.println("polished function successfully: " + root);
			} catch (Exception e) {
				// System.out.println("Polish FAILED: ");
				// polishing failed: maybe we have an extremum here
				// try to find a local extremum
				try {					
					polish = rootPolisher.bisectNewtonRaphson(derivFunc, root - 0.1, root + 0.1);	
					root = polish;
					// System.out.println("    find extremum successfull: " + root);
				} catch (Exception ex) {
				}
			}

			//	check if the found root is really ok 												
			val = polyFunc.evaluateDerivFunc(root); // get f(root) and f'(root)
			double error = Math.abs(val[0]); // | f(root) |
			double slope = Math.abs(val[1]);
			
			boolean success;
			if (slope < 1)
				success = error < LAGUERRE_EPS;
			else
				success = error < LAGUERRE_EPS * slope;
			
			if (success) {
				// System.out.println("FOUND ROOT: " + root);
				eqn[realRoots] = root;
				realRoots++;
			} else {
				 //System.out.println("no root: " + root + ", error " + error);
			}
		}			
		return realRoots;
	}
	
	
// avoid too big polynomial coefficients in laguerreAll
//	private static final int MAX_POLYNOMIAL_COEFFICIENT = 1000;
//	
//	// avoid huge coefficients
//	private void checkCoefficients(double [] eqn) {
//		double max = Math.abs(eqn[0]);
//		double temp;
//		for (int i=1; i < eqn.length; i++) {
//			temp = Math.abs(eqn[i]);
//			if (temp > max) max = temp;
//		}
//		if (max > MAX_POLYNOMIAL_COEFFICIENT) {
//			System.out.println("changed coefficients");
//			double factor = MAX_POLYNOMIAL_COEFFICIENT/max;
//			for (int i=0; i < eqn.length; i++) {
//				eqn[i] *= factor;
//			}
//		}
//	}
    
}






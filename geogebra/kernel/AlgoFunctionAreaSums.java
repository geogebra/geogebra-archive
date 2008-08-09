/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.Application;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.optimization.NegativeRealRootFunction;
import geogebra.kernel.roots.RealRootFunction;


/**
 * Superclass for lower/upper sum of function f in interval [a, b] with
 * n intervals
 */
public abstract class AlgoFunctionAreaSums extends AlgoElement
implements EuclidianViewAlgo {
	
	// largest possible number of rectangles
	private static final int MAX_RECTANGLES = 10000;
	
	// find global minimum in an interval with the following heuristic:
	// 1) sample the function for some values of x in [a, b] 
	// 2) get x[i] with minimal f(x[i])
	// 3) use parabolic interpolation and Brent's Method in One Dimension
	//     for interval x[i-1] to x[i+1]
	//     (Numerical Recipes in C++, pp.406)
	
	// the function is sampled view_steps times over the currently visible part of the x-axis
	private static final double VIEW_STEPS = 50;
	private static double xVisibleWidth = 10;
	
	private int type;
	public static final int TYPE_UPPERSUM = 0;
	public static final int TYPE_LOWERSUM = 1;
	public static final int TYPE_BARCHART = 2;
	public static final int TYPE_HISTOGRAM = 3;
	public static final int TYPE_TRAPEZOIDALSUM = 4;
	
	// tolerance for parabolic interpolation
	private static final double TOLERANCE = 1E-7;

	private GeoFunction f; // input	   
	private NumberValue a, b, n; // input
	private GeoList list1, list2; // input
	private GeoElement ageo, bgeo, ngeo;
	private GeoNumeric  sum; // output sum    
	
	private int N; 	
	private double STEP;
	private double [] yval; // y value (= min) in interval 0 <= i < N
	private double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	//private double [] widths;
	
	private ExtremumFinder extrFinder;
		
	public AlgoFunctionAreaSums(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n,
								   int type) {
		
		super(cons);
		
		this.type = type;
		
		extrFinder = cons.getExtremumFinder();
		
		this.f = f;
		this.a = a;
		this.b = b;			
		this.n = n;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();
			
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
	}
	
	// BARCHART
			public AlgoFunctionAreaSums(Construction cons, String label,  
					   NumberValue a, NumberValue b, GeoList list1) {
		
		super(cons);
		
		type = TYPE_BARCHART;
		
		extrFinder = cons.getExtremumFinder();
		
		this.a = a;
		this.b = b;			
		this.list1 = list1;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		}
		
			// HISTOGRAM
			public AlgoFunctionAreaSums(Construction cons, String label,  
					   GeoList list1, GeoList list2) {
		
		super(cons);
		
		type = TYPE_HISTOGRAM;
		
		extrFinder = cons.getExtremumFinder();
		

		this.list1 = list1;
		this.list2 = list2;
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		}

	final public void euclidianViewUpdate() {
		xVisibleWidth = kernel.getXmax() - kernel.getXmin();
		super.euclidianViewUpdate();
	}
	
	final public boolean wantsEuclidianViewUpdate() {
    	return true;
    }
	
	abstract protected String getClassName();

	// for AlgoElement
	protected void setInputOutput() {
		
		switch (type)
		{
		case TYPE_UPPERSUM:
		case TYPE_LOWERSUM:
		case TYPE_TRAPEZOIDALSUM:
			input = new GeoElement[4];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;		
			break;
		case TYPE_BARCHART:
			input = new GeoElement[3];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;		
			break;
		case TYPE_HISTOGRAM:
			input = new GeoElement[2];
			input[0] = list1;		
			input[1] = list2;		
			break;

		}
		output = new GeoElement[1];
		output[0] = sum;
		setDependencies(); // done by AlgoElement
	}

	/**
 	* number of intervals
 	* @return
 	*/
	public int getIntervals() {
		return N;
	}
	
	public double getStep() {
		return STEP;
	}
	
	public double [] getValues() {
		return yval;
	}

	public double [] getLeftBorders() {
		return leftBorder;
	}	
	
	public GeoNumeric getSum() {
		return sum;
	}
	
	public NumberValue getA() {
		return a;
	}
		
	public NumberValue getB() {
		return b;
	}
	
	protected final void compute() {	
		
		switch (type)
		{
		case TYPE_LOWERSUM:
		case TYPE_UPPERSUM:
			
			if (!(f.isDefined() && ageo.isDefined() && bgeo.isDefined() 
					&& ngeo.isDefined())) 
				sum.setUndefined();
					
			RealRootFunction fun = f.getRealRootFunctionY();				
			double ad = a.getDouble();
			double bd = b.getDouble();		 
			 
			double ints = n.getDouble();		
			if (ints < 1) {
				sum.setUndefined();
				return;
			} else if (ints > MAX_RECTANGLES) {
				N = MAX_RECTANGLES;
			} else {
				N = (int) Math.round(ints);
			}
			STEP = (bd - ad) / N;	 		
			
			// calc minimum in every interval		
			if (yval == null || yval.length < N) {
				yval = new double[N]; 			
				leftBorder = new double[N];		
			}				
			RealRootFunction fmin = fun;
			if (type == TYPE_UPPERSUM) fmin = new NegativeRealRootFunction(fun); // use -f to find maximum		
			
			double cumSum = 0;
			double left, right, min;			 	
			
			double subStep = xVisibleWidth / VIEW_STEPS;	
			boolean doSubSamples = Math.abs(STEP) > subStep;	
			boolean positiveStep = 	STEP >= 0; 		
			for (int i=0; i < N ; i++) { 
				leftBorder[i] = ad + i * STEP;	
				
				if (positiveStep) {		
					left = leftBorder[i];
					right = leftBorder[i] + STEP;				
				} else {
					left = leftBorder[i] + STEP;
					right = leftBorder[i];		
				}
					
				min = Double.POSITIVE_INFINITY;
							
				// heuristic: take some samples in this interval
				// and find smallest one									
				if (doSubSamples) { 
					double y, minSample = left;				
					for (double x=left; x < right; x += subStep) {
						y = fmin.evaluate(x);
						if (y < min) { 
							min = y;
							minSample = x;
						} 
					}	
					// if the minimum is on the left border then minSample == left now
					//	check right border too
					y = fmin.evaluate(right);
					if (y < min) minSample = right; 
					
					// investigate only the interval around the minSample
					// make sure we don't get out of our interval!
					left = Math.max(left, minSample - subStep);
					right = Math.min(right, minSample + subStep);
				}	
					
				// find minimum (resp. maximum) over interval
				double x = extrFinder.findMinimum(left, right, fmin, TOLERANCE);
				double y = fmin.evaluate(x);
	
				// one of the evaluated sub-samples could be smaller 
				// e.g. at the border of this interval			 				
				if (y > min) y = min;						 
				
				//	store min/max
				if (type == TYPE_UPPERSUM) y = -y;	
				yval[i] = y; 
		
				// add to sum
				cumSum += y;	
			}							
			
			// calc area of rectangles				
			sum.setValue(cumSum * STEP);	
			break;

		case TYPE_TRAPEZOIDALSUM:

			if (!(f.isDefined() && ageo.isDefined() && bgeo.isDefined() 
					&& ngeo.isDefined())) 
				sum.setUndefined();
					
			fun = f.getRealRootFunctionY();				
			ad = a.getDouble();
			bd = b.getDouble();		 
			 
			ints = n.getDouble();		
			if (ints < 1) {
				sum.setUndefined();
				return;
			} else if (ints > MAX_RECTANGLES) {
				N = MAX_RECTANGLES;
			} else {
				N = (int) Math.round(ints);
			}
			STEP = (bd - ad) / N;	 		
			

			
			// calc minimum in every interval		
			if (yval == null || yval.length < N+1) {// N+1 for trapezoids
				yval = new double[N+1]; 			// N+1 for trapezoids
				leftBorder = new double[N+1];		// N+1 for trapezoids
			}				

		
			cumSum = 0;
			
			for (int i=0; i < N+1 ; i++) { // N+1 for trapezoids
				leftBorder[i] = ad + i * STEP;	
				

				yval[i] = fun.evaluate(leftBorder[i]);
	
				cumSum += yval[i];	
			}							
			
			// calc area of rectangles	
			
			cumSum-=(yval[0] + yval[N])/2;
			//for (int i=0; i < N+1 ; i++) cumSum += yval[i];
			sum.setValue(cumSum * STEP);	
			break;
			
		case TYPE_BARCHART:
			if (!(ageo.isDefined() && bgeo.isDefined() 
					&& list1.isDefined())) 
			{
				sum.setUndefined();
				return;
			}
			
			
			N = list1.size();
							
			ad = a.getDouble();
			bd = b.getDouble();		 
			 
			ints = list1.size();
			if (ints < 1) {
				sum.setUndefined();
				return;
			} else if (ints > MAX_RECTANGLES) {
				N = MAX_RECTANGLES;
			} else {
				N = (int) Math.round(ints);
			}
			STEP = (bd - ad) / N;	 		

			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}				

			cumSum = 0;
			
			for (int i=0; i < N; i++) {
				leftBorder[i] = ad + i * STEP;	
				
				GeoElement geo = list1.get(i);
				if (geo.isGeoNumeric())	yval[i] = ((GeoNumeric)geo).getDouble(); 
				else yval[i]=0;
				
				cumSum += yval[i];
				
			}
			 
							
			
			// calc area of rectangles				
			sum.setValue(cumSum * STEP);	
				
			break;
			
		case TYPE_HISTOGRAM:
			if (!list1.isDefined() || !list2.isDefined()) 
			{
				sum.setUndefined();
				return;
			}
			
			N = list1.size();
			
			if (N < 2 || N-1 != list2.size())
			{
				sum.setUndefined();
				return;
			}
							
		 

			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}				
			
			//cumSum = 0;
			for (int i=0; i < N-1; i++) {
				
				GeoElement geo = list1.get(i);
				if (i == 0) {
					if (geo.isNumberValue()) a = (NumberValue)geo;
					else { sum.setUndefined(); return; }
				}
				//if (i == N-1) b = (NumberValue)geo;
				if (geo.isGeoNumeric())	leftBorder[i] = ((GeoNumeric)geo).getDouble(); 
				else { sum.setUndefined(); return; }
				
				geo = list2.get(i);
				if (geo.isGeoNumeric())	yval[i] = ((GeoNumeric)geo).getDouble(); 
				else { sum.setUndefined(); return; }
				
				//if (i != 0) cumSum += leftBorder[i] * yval[i];
			}
			
			yval[N-1] = yval[N-2];
			
			GeoElement geo = list1.get(N-1);
			if (geo.isNumberValue()) b = (NumberValue)geo;
			else { sum.setUndefined(); return; }
			leftBorder[N-1] = ((GeoNumeric)geo).getDouble(); 


			cumSum = 0;
			for (int i=1; i < N; i++) cumSum += (leftBorder[i] - leftBorder[i-1]) * yval[i-1];
			
			// area of rectangles				
			sum.setValue(cumSum);	
				
			break;
		}
	}
	
	public String toString() {
		return getCommandDescription();
	}
	
	public boolean useTrapeziums() {
		switch (type)
		{
		case TYPE_TRAPEZOIDALSUM:
			return true;
		default :
			return false;
		}
	}
	public boolean isHistogram() {
		switch (type)
		{
		case TYPE_HISTOGRAM:
			return true;
		default :
			return false;
		}
	}
}

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
public abstract class AlgoSumUpperLower extends AlgoElement
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
	private static final int TYPE_UPPERLOWERSUM = 0;
	private static final int TYPE_BARCHART = 1;
	private static final int TYPE_HISTOGRAM = 2;
	
	
	// tolerance for parabolic interpolation
	private static final double TOLERANCE = 1E-7;

	private GeoFunction f; // input	   
	private NumberValue a, b, n; // input
	private GeoList list1, list2; // input
	private GeoElement ageo, bgeo, ngeo;
	private GeoNumeric  sum; // output sum    
	private boolean upperSum;
	
	private int N; 	
	private double STEP;
	private double [] yval; // y value (= min) in interval 0 <= i < N
	private double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	private double [] widths;
	
	private ExtremumFinder extrFinder;
		
	public AlgoSumUpperLower(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n,
								   boolean upperSum) {
		
		super(cons);
		
		type = TYPE_UPPERLOWERSUM;
		
		extrFinder = cons.getExtremumFinder();
		
		this.f = f;
		this.a = a;
		this.b = b;			
		this.n = n;
		this.upperSum = upperSum;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();
			
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
	}
	
	// BARCHART
			public AlgoSumUpperLower(Construction cons, String label,  
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
			public AlgoSumUpperLower(Construction cons, String label,  
					   NumberValue a, NumberValue b, GeoList list1, GeoList list2) {
		
		super(cons);
		
		type = TYPE_HISTOGRAM;
		
		extrFinder = cons.getExtremumFinder();
		
		this.a = a;
		this.b = b;			
		this.list1 = list1;
		this.list2 = list2;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		
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
		case TYPE_UPPERLOWERSUM:
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
			input = new GeoElement[4];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;		
			input[3] = list2;		
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
		case TYPE_UPPERLOWERSUM:

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
			if (upperSum) fmin = new NegativeRealRootFunction(fun); // use -f to find maximum		
			
			double cumSum = 0;
			double left, right, min;			 	
			
			double subStep = xVisibleWidth / VIEW_STEPS;	
			boolean doSubSamples = Math.abs(STEP) > subStep;	
			boolean positiveStep = 	STEP >= 0; 		
			for (int i=0; i < N; i++) {
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
				if (upperSum) y = -y;	
				yval[i] = y; 
		
				// add to sum
				cumSum += y;	
			}							
			
			// calc area of rectangles				
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
			if (!(ageo.isDefined() && bgeo.isDefined() 
					&& list1.isDefined() && list2.isDefined())) 
			{
				sum.setUndefined();
				return;
			}
			
			N = list1.size();
			
			if (N != list2.size())
			{
				sum.setUndefined();
				return;
			}
							
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

			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
				widths = new double[N];
			}				
			
			double width = 0;
			for (int i=0; i < N; i++) {
				GeoElement geo = list2.get(i);
				if (geo.isGeoNumeric())	widths[i] = ((GeoNumeric)geo).getDouble(); 
				else { sum.setUndefined(); return; }
				width += widths[i];
			}


			
			
			leftBorder[0] = ad;
			GeoElement geo = list1.get(0);
			if (geo.isGeoNumeric())	yval[0] = ((GeoNumeric)geo).getDouble(); 
			else yval[0]=0;
			
			cumSum = 0;
			Application.debug(cumSum+"");
			
			for (int i=1; i < N; i++) {
				
				double scaledWidth = widths[i-1]*(bd - ad)/width;
				
				leftBorder[i] = (leftBorder[i-1]) + scaledWidth; 
				
				geo = list1.get(i);
				if (geo.isGeoNumeric())	yval[i] = ((GeoNumeric)geo).getDouble(); 
				else yval[i]=0;
				
				cumSum += yval[i-1] * scaledWidth;
				Application.debug(cumSum+"");
				
			}
			
			cumSum += yval[N-1] * widths[N-1]*(bd - ad)/width;
			 
							
			
			// area of rectangles				
			sum.setValue(cumSum);	
				
			break;
		}
	}
	
	public String toString() {
		return getCommandDescription();
	}

}

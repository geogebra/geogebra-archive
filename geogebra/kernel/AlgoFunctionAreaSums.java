/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.optimization.NegativeRealRootFunction;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.kernel.statistics.AlgoMedian;
import geogebra.kernel.statistics.AlgoQ1;
import geogebra.kernel.statistics.AlgoQ3;
import geogebra.main.Application;


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
	
	
	private int type;
	public static final int TYPE_UPPERSUM = 0;
	public static final int TYPE_LOWERSUM = 1;
	public static final int TYPE_BARCHART = 2;
	public static final int TYPE_BARCHART_RAWDATA = 3;
	public static final int TYPE_BARCHART_FREQUENCY_TABLE = 4;
	public static final int TYPE_BARCHART_FREQUENCY_TABLE_WIDTH = 5;
	public static final int TYPE_HISTOGRAM = 6;
	public static final int TYPE_TRAPEZOIDALSUM = 7;
	public static final int TYPE_BOXPLOT = 8;
	public static final int TYPE_BOXPLOT_RAWDATA = 9;
	
	// tolerance for parabolic interpolation
	private static final double TOLERANCE = 1E-7;

	private GeoFunction f; // input	   
	private NumberValue a, b, n, width; // input
	private GeoList list1, list2; // input
	private GeoList tempList;
	private GeoElement ageo, bgeo, ngeo, minGeo, maxGeo, Q1geo, Q3geo, medianGeo, widthgeo;
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
		sum.setDrawable(true);
	}
	
	// BARCHART
	public AlgoFunctionAreaSums(Construction cons, String label,  
			   NumberValue a, NumberValue b, GeoList list1) {

		super(cons);
		
		type = TYPE_BARCHART;
		
		this.a = a;
		this.b = b;			
		this.list1 = list1;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
	// BarChart [<list of data without repetition>, <frequency of each of these data>]
	public AlgoFunctionAreaSums(Construction cons, String label,  
			   GeoList list1, GeoList list2, boolean dummy) {

		super(cons);
		
		type = TYPE_BARCHART_FREQUENCY_TABLE;
		
		this.list1 = list1;
		this.list2 = list2;
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
	
	// BarChart [<list of data without repetition>, <frequency of each of these data>, <width>]
	public AlgoFunctionAreaSums(Construction cons, String label,  
			   GeoList list1, GeoList list2, NumberValue width) {

		super(cons);
		
		type = TYPE_BARCHART_FREQUENCY_TABLE_WIDTH;
		
		this.list1 = list1;
		this.list2 = list2;
		this.width = width;
		widthgeo = width.toGeoElement();

		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
	
	// BarChart [<list of data>, <width>]
	public AlgoFunctionAreaSums(Construction cons, String label,  
			GeoList list1, GeoNumeric n) {

		super(cons);
		
		type = TYPE_BARCHART_RAWDATA;
		
		
		this.list1 = list1;
		this.n = n;
		ngeo = n.toGeoElement();
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
		
			// HISTOGRAM
			public AlgoFunctionAreaSums(Construction cons, String label,  
					   GeoList list1, GeoList list2) {
		
		super(cons);
		
		type = TYPE_HISTOGRAM;
		
		this.list1 = list1;
		this.list2 = list2;
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
		}
			
			
			// BOXPLOT
			public AlgoFunctionAreaSums(Construction cons, String label,  
					NumberValue min, NumberValue Q1,
					NumberValue median, NumberValue Q3, NumberValue max, NumberValue a, NumberValue b) {
		
		super(cons);
		
		type = TYPE_BOXPLOT;
		
		this.a=a;
		this.b=b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement(); 
		minGeo = min.toGeoElement();
		Q1geo = Q1.toGeoElement();
		medianGeo = median.toGeoElement();
		Q3geo = Q3.toGeoElement();
		maxGeo = max.toGeoElement();

		
		sum = new GeoNumeric(cons); // output
		//sum.setLabelVisible(false);
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
		}

			// BOXPLOT (raw data)
			public AlgoFunctionAreaSums(Construction cons, String label,  
					GeoList list1, NumberValue a, NumberValue b) {
		
		super(cons);
		
		type = TYPE_BOXPLOT_RAWDATA;
		
		this.a=a;
		this.b=b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement(); 
		this.list1 = list1;

		
		sum = new GeoNumeric(cons); // output
		//sum.setLabelVisible(false);
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
		}

	final public void euclidianViewUpdate() {
		compute();
	}
	
	final public boolean wantsEuclidianViewUpdate() {
    	return true;
    }
	
	public abstract String getClassName();

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
		case TYPE_BARCHART_FREQUENCY_TABLE:
			input = new GeoElement[2];
			input[0] = list1;
			input[1] = list2;
			break;
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			input = new GeoElement[3];
			input[0] = list1;
			input[1] = list2;
			input[2] = widthgeo;
			break;
		case TYPE_BARCHART_RAWDATA:
			input = new GeoElement[2];
			input[0] = list1;
			input[1] = ngeo;		
			break;
		case TYPE_HISTOGRAM:
			input = new GeoElement[2];
			input[0] = list1;		
			input[1] = list2;		
			break;
		case TYPE_BOXPLOT:
			input = new GeoElement[7];
			input[0] = ageo;		
			input[1] = bgeo;		
			input[2] = minGeo;		
			input[3] = Q1geo;		
			input[4] = medianGeo;		
			input[5] = Q3geo;		
			input[6] = maxGeo;		
			break;
		case TYPE_BOXPLOT_RAWDATA:
			input = new GeoElement[3];
			input[0] = ageo;		
			input[1] = bgeo;		
			input[2] = list1;		
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
		GeoElement geo; // temporary variable	
		
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
			
            // calulate the min and max x-coords of what actually needs to be drawn
			// do subsampling only in this region
			EuclidianView ev = app.getEuclidianView();
            double visibleMin = Math.max(Math.min(ad, bd), ev.getXmin());
            double visibleMax = Math.min(Math.max(ad, bd), ev.getXmax());	
            
            // subsample every 5 pixels
            double noOfSamples = Math.abs(ev.toScreenCoordXd(visibleMax) - ev.toScreenCoordX(visibleMin)) / 5;
            
			double subStep = Math.abs(visibleMax - visibleMin) / noOfSamples;	
			boolean doSubSamples = !Kernel.isZero(subStep) && Math.abs(STEP) > subStep;	
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
				// subsampling needed in case there are two eg minimums and we get the wrong one with extrFinder.findMinimum()
				//Application.debug(left + " "+ visibleMin+" "+right + " "+visibleMax);
				// subsample visible bit only
				if (doSubSamples && ((STEP > 0 ? left : right) < visibleMax && (STEP > 0 ? right : left) > visibleMin )) { 
					//Application.debug("subsampling from "+left+" to "+right);
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
				
				geo = list1.get(i);
				if (geo.isGeoNumeric())	yval[i] = ((GeoNumeric)geo).getDouble(); 
				else yval[i]=0;
				
				cumSum += yval[i];
				
			}
			 
							
			
			// calc area of rectangles				
			sum.setValue(cumSum * STEP);	
				
			break;
		case TYPE_BARCHART_RAWDATA:
			// BarChart[{1,1,2,3,3,3,4,5,5,5,5,5,5,5,6,8,9,10,11,12},3]
			if (!list1.isDefined() || !ngeo.isDefined()) 
			{
				sum.setUndefined();
				return;
			}
			
			double mini = Double.MAX_VALUE;
			double maxi = Double.MIN_VALUE;
			int minIndex=-1, maxIndex=-1;
			
			double step = n.getDouble();
			
			int rawDataSize = list1.size();
			
			if (step < 0 || kernel.isZero(step) || rawDataSize < 2)
			{
				sum.setUndefined();
				return;
			}
			
			
			// find max and min
			for (int i = 0; i < rawDataSize; i++) {
				geo = list1.get(i);
				if (!geo.isGeoNumeric()) {
					sum.setUndefined();
					return;
				}
				double val = ((GeoNumeric)geo).getDouble();
				
				if (val > maxi) {
					maxi = val;
					maxIndex = i;
				}
				if (val < mini) {
					mini = val;
					minIndex = i;
				}
			}
			
			if (maxi == mini || maxIndex == -1 || minIndex == -1) {
				sum.setUndefined();
				return;
			}
			
			double totalWidth = maxi - mini;
			
			double noOfBars = totalWidth / n.getDouble();
			
			double gap = 0;
			
			/*
			if (kernel.isInteger(noOfBars))
			{
				N = (int)noOfBars + 1;
				a = (NumberValue)list1.get(minIndex);
				b = (NumberValue)list1.get(maxIndex);
			}
			else */
			{
				N = (int)noOfBars + 2;
				gap = ((N-1) * step - totalWidth) / 2.0;
				a = (NumberValue)(new GeoNumeric(cons,mini - gap));
				b = (NumberValue)(new GeoNumeric(cons,maxi + gap));
				//Application.debug("gap = "+gap);
			}
			
			
			//Application.debug("N = "+N+" maxi = "+maxi+" mini = "+mini);
							

				
				
			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}				
			
			// fill in class boundaries
			//double width = (maxi-mini)/(double)(N-2);
			for (int i=0; i < N; i++) {
				leftBorder[i] = mini - gap + step * i;
			}
						
		
			// zero frequencies
			for (int i=0; i < N; i++) yval[i] = 0; 	

			// work out frequencies in each class
			double datum;
			
			for (int i=0; i < list1.size() ; i++) {
				geo = list1.get(i);
				if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
				else { sum.setUndefined(); return; }
				
				// if datum is outside the range, set undefined
				//if (datum < leftBorder[0] || datum > leftBorder[N-1] ) { sum.setUndefined(); return; }
				
				// fudge to make the last boundary eg 10 <= x <= 20
				// all others are 10 <= x < 20
				double oldMaxBorder = leftBorder[N-1];
				leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);

				// check which class this datum is in
				for (int j=1; j < N; j++) {
					//System.out.println("checking "+leftBorder[j]);
					if (datum < leftBorder[j]) 
					{
						//System.out.println(datum+" "+j);
						yval[j-1]++;
						break;
					}
				}
				
				leftBorder[N-1] = oldMaxBorder;

				
				// area of rectangles 
				sum.setValue(list1.size() * step);	

			}
			break;
			
		case TYPE_BARCHART_FREQUENCY_TABLE:
			// BarChart[{11,12,13,14,15},{1,5,0,13,4}]
			if (!list1.isDefined() || !list2.isDefined()) 
			{
				sum.setUndefined();
				return;
			}
			

			N = list1.size() + 1;
			
			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}				
			
			if (N == 2) {
				// special case, 1 bar
				yval = new double[2];
				leftBorder = new double[2];
				yval[0] = ((GeoNumeric)(list2.get(0))).getDouble(); 	
				
				leftBorder[0] = ((GeoNumeric)(list1.get(0))).getDouble() - 0.5;
				leftBorder[1] = leftBorder[0] + 1;
				ageo = new GeoNumeric(cons,leftBorder[0]);
				bgeo = new GeoNumeric(cons,leftBorder[1]);
				a = (NumberValue)ageo;
				b = (NumberValue)bgeo;
				
				sum.setValue(yval[0]);

				return;
			} 
			
			if (list2.size() + 1 != N || N < 3) {
				sum.setUndefined();
				return;
			}
			
			double start = ((GeoNumeric)(list1.get(0))).getDouble();
			double end = ((GeoNumeric)(list1.get(N-2))).getDouble();
			step = ((GeoNumeric)(list1.get(1))).getDouble() - start;
			
			
			
			//Application.debug("N = "+N+" start = "+start+" end = "+end+" width = "+width);

			if (!kernel.isEqual(end - start, step * (N-2)) // check first list is (consistent) with being AP 
					|| step <= 0) {
						sum.setUndefined();
						return;
					}

			ageo = new GeoNumeric(cons,start - step / 2);
			bgeo = new GeoNumeric(cons,end + step / 2);
			a = (NumberValue)ageo;
			b = (NumberValue)bgeo;
			
							
				
				
			
			// fill in class boundaries

			for (int i=0; i < N; i++) {
				leftBorder[i] = start - step/2 + step * i;
			}
						
			double area = 0;
		
			// fill in frequencies
			for (int i=0; i < N-1; i++) {
				geo = list2.get(i);
				if (!geo.isGeoNumeric()) {
					sum.setUndefined();
					return;
				}
				yval[i] = ((GeoNumeric)(list2.get(i))).getDouble(); 	
				
				area += yval[i] * step;
			}

			
			// area of rectangles = total frequency				
			sum.setValue(area);	

			
			break;
			
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			// BarChart[{1,2,3,4,5},{1,5,0,13,4}, 0.5]
			if (!list1.isDefined() || !list2.isDefined()) 
			{
				sum.setUndefined();
				return;
			}
			

			N = list1.size() + 1;
			
			int NN = 2 * N -1;
			
			if (list2.size() + 1 != N || N < 3) {
				sum.setUndefined();
				return;
			}
			
			start = ((GeoNumeric)(list1.get(0))).getDouble();
			end = ((GeoNumeric)(list1.get(N-2))).getDouble();
			step = ((GeoNumeric)(list1.get(1))).getDouble() - start;
			double colWidth = width.getDouble();
			
			//Application.debug("N = "+N+" start = "+start+" end = "+end+" colWidth = "+colWidth);

			if (!kernel.isEqual(end - start, step * (N-2)) // check first list is (consistent) with being AP 
					|| step <= 0) {
						sum.setUndefined();
						return;
					}

			ageo = new GeoNumeric(cons,start - colWidth / 2);
			bgeo = new GeoNumeric(cons,end + colWidth / 2);
			a = (NumberValue)ageo;
			b = (NumberValue)bgeo;
			
							
				
				
			if (yval == null || yval.length < NN-1) {
				yval = new double[NN-1];
				leftBorder = new double[NN-1];
			}				
			
			// fill in class boundaries

			for (int i=0; i < NN-1; i += 2 ) {
				leftBorder[i] = start + step * (i/2) - colWidth / 2.0;
				leftBorder[i+1] = start + step * (i/2) + colWidth / 2.0;
			}
			
			area = 0;
		
			// fill in frequencies
			for (int i=0; i < NN-1; i++) {
				if (i % 2 == 1) {
					// dummy columns, zero height
					yval[i] = 0;
				} else {
					geo = list2.get(i/2);
					if (!geo.isGeoNumeric()) {
						sum.setUndefined();
						return;
					}
					yval[i] = ((GeoNumeric)(list2.get(i/2))).getDouble(); 	
					
					area += yval[i] * colWidth;
				}
			}

			
			// area of rectangles = total frequency				
			sum.setValue(area);	
			
			N = NN - 1;

			
			break;
			
		case TYPE_HISTOGRAM:
			if (!list1.isDefined() || !list2.isDefined()) 
			{
				sum.setUndefined();
				return;
			}
			
			N = list1.size();
			
			if (N < 2)
			{
				sum.setUndefined();
				return;
			}
							
			if (N-1 != list2.size())
			{ // list2 contains raw data
				// eg Histogram[{1,1.5,2,4},{1.0,1.1,1.1,1.2,1.7,1.7,1.8,2.2,2.5,4.0}]
				
				if (yval == null || yval.length < N) {
					yval = new double[N];
					leftBorder = new double[N];
				}				
				
				// fill in class boundaries
				for (int i=0; i < N-1; i++) {
					
					geo = list1.get(i);
					if (i == 0) {
						if (geo.isNumberValue()) a = (NumberValue)geo;
						else { sum.setUndefined(); return; }
					}
					if (geo.isGeoNumeric())	leftBorder[i] = ((GeoNumeric)geo).getDouble(); 
					else { sum.setUndefined(); return; }
					
				}
							
				geo = list1.get(N-1);
				if (geo.isNumberValue()) b = (NumberValue)geo;
				else { sum.setUndefined(); return; }
				leftBorder[N-1] = ((GeoNumeric)geo).getDouble(); 
				
				// zero frequencies
				for (int i=0; i < N; i++) yval[i] = 0; 	
	
				// work out frequencies in each class
				
				for (int i=0; i < list2.size() ; i++) {
					geo = list2.get(i);
					if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
					else { sum.setUndefined(); return; }
					
					// if datum is outside the range, set undefined
					if (datum < leftBorder[0] || datum > leftBorder[N-1] ) { sum.setUndefined(); return; }
					
					// fudge to make the last boundary eg 10 <= x <= 20
					// all others are 10 <= x < 20
					double oldMaxBorder = leftBorder[N-1];
					leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);
					
					// check which class this datum is in
					for (int j=1; j < N; j++) {
						//System.out.println("checking "+leftBorder[j]);
						if (datum < leftBorder[j]) 
						{
							//System.out.println(datum+" "+j);
							yval[j-1]++;
							break;
						}
					}
					
					leftBorder[N-1] = oldMaxBorder;
					
				}
				
			
				// turn frequencies into frequency densities
				for (int i=1; i < N; i++) yval[i-1] /= (leftBorder[i] - leftBorder[i-1]);
				
				// area of rectangles = total frequency				
				sum.setValue(list2.size());	

			}
			else
			{ // list2 contains the heights
	
				if (yval == null || yval.length < N) {
					yval = new double[N];
					leftBorder = new double[N];
				}				
				
				for (int i=0; i < N-1; i++) {
					
					geo = list1.get(i);
					if (i == 0) {
						if (geo.isNumberValue()) a = (NumberValue)geo;
						else { sum.setUndefined(); return; }
					}
					if (geo.isGeoNumeric())	leftBorder[i] = ((GeoNumeric)geo).getDouble(); 
					else { sum.setUndefined(); return; }
					
					geo = list2.get(i);
					if (geo.isGeoNumeric())	yval[i] = ((GeoNumeric)geo).getDouble(); 
					else { sum.setUndefined(); return; }
					
				}
				
				yval[N-1] = yval[N-2];
				
				geo = list1.get(N-1);
				if (geo.isNumberValue()) b = (NumberValue)geo;
				else { sum.setUndefined(); return; }
				leftBorder[N-1] = ((GeoNumeric)geo).getDouble(); 
	
				cumSum = 0;
				for (int i=1; i < N; i++) cumSum += (leftBorder[i] - leftBorder[i-1]) * yval[i-1];
				
				// area of rectangles				
				sum.setValue(cumSum);	
			}	
			

			break;
		case TYPE_BOXPLOT_RAWDATA:
			
			// list1 = rawData
			if (tempList == null) tempList = new GeoList(cons);
			tempList.clear();
			AlgoListMin min2 = new AlgoListMin(cons,list1);
			cons.removeFromConstructionList(min2);
			tempList.add(min2.getMin());
			AlgoQ1 Q1 = new AlgoQ1(cons,list1);
			cons.removeFromConstructionList(Q1);
			tempList.add(Q1.getQ1());
			AlgoMedian median = new AlgoMedian(cons,list1);
			cons.removeFromConstructionList(median);
			tempList.add(median.getMedian());
			AlgoQ3 Q3 = new AlgoQ3(cons,list1);
			cons.removeFromConstructionList(Q3);
			tempList.add(Q3.getQ3());
			AlgoListMax max = new AlgoListMax(cons,list1);
			cons.removeFromConstructionList(max);
			tempList.add(max.getMax());

			N=5;
			
			calcBoxPlot();
				
			break;
			
		case TYPE_BOXPLOT:

		if (tempList == null) tempList = new GeoList(cons);
		tempList.clear();
		tempList.add(minGeo);
		tempList.add(Q1geo);
		tempList.add(medianGeo);
		tempList.add(Q3geo);
		tempList.add(maxGeo);
		
		N=5;

		calcBoxPlot();
		
		break;
}
}
	
	private void calcBoxPlot() {
		if (yval == null || yval.length < N) {
			yval = new double[N];
			leftBorder = new double[N];
		}				
		
		for (int i=0; i < N; i++) {
			
			GeoElement geo = tempList.get(i);
			//if (i == 0) {
			//	if (geo.isNumberValue()) b = (NumberValue)geo; // dummy value, not used
			//	else { sum.setUndefined(); return; }
			//}
			if (geo.isGeoNumeric())	leftBorder[i] = ((GeoNumeric)geo).getDouble(); 
			else { sum.setUndefined(); return; }
			
			yval[i] = 1.0; // dummy value
			
			
		}

		sum.setValue(leftBorder[2]);	 // median
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
	public int getType() {
		return type;
	}
	
	public boolean isBoxPlot() {
		switch (type)
		{
		case TYPE_BOXPLOT:
		case TYPE_BOXPLOT_RAWDATA:
			return true;
		default :
			return false;
		}
	}
}

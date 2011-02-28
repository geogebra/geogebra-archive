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



/**
 * Superclass for lower/upper sum of function f in interval [a, b] with
 * n intervals
 */
public abstract class AlgoFunctionAreaSums extends AlgoElement
implements EuclidianViewAlgo, AlgoDrawInformation{
	
	// largest possible number of rectangles
	private static final int MAX_RECTANGLES = 10000;
	
	// subsample every 5 pixels
	private static final int SAMPLE_PIXELS = 5;

	// find global minimum in an interval with the following heuristic:
	// 1) sample the function for some values of x in [a, b] 
	// 2) get x[i] with minimal f(x[i])
	// 3) use parabolic interpolation and Brent's Method in One Dimension
	//     for interval x[i-1] to x[i+1]
	//     (Numerical Recipes in C++, pp.406)
	
	
	private int type;
	/** Upper Rieeman sum **/
	public static final int TYPE_UPPERSUM = 0;
	/** Lower Rieman sum **/
	public static final int TYPE_LOWERSUM = 1;
	/** Left Rieman sum (Ulven: 09.02.11) **/
	public static final int TYPE_LEFTSUM=11;
	/** Rectangle sum with divider for step interval (Ulven: 09.02.11) **/
	public static final int TYPE_RECTANGLESUM=12;	
	/** Barchart from expression**/
	public static final int TYPE_BARCHART = 2;
	/** Barchart from raw data **/
	public static final int TYPE_BARCHART_RAWDATA = 3;
	/** Barchart from (values,frequencies)**/
	public static final int TYPE_BARCHART_FREQUENCY_TABLE = 4;
	/** Barchart from (values,frequencies) with given width**/
	public static final int TYPE_BARCHART_FREQUENCY_TABLE_WIDTH = 5;
	
	/** Histogram from(class boundaries, raw data) with default density = 1 
	* or Histogram from(class boundaries, frequencies) no density required **/
	public static final int TYPE_HISTOGRAM = 6;
	/** Histogram from(class boundaries, raw data) with given density **/
	public static final int TYPE_HISTOGRAM_DENSITY = 7;
	
	/** Trapezoidal sum**/
	public static final int TYPE_TRAPEZOIDALSUM = 8;
	/** Boxplot**/
	public static final int TYPE_BOXPLOT = 9;
	/** Boxplot from raw data**/
	public static final int TYPE_BOXPLOT_RAWDATA = 10;
	
	
	// tolerance for parabolic interpolation
	private static final double TOLERANCE = 1E-7;

	private GeoFunction f; // input	   
	private NumberValue a, b, n, width, density; // input
	private NumberValue d;  // input: divider for Rectangle sum, 0..1
	private GeoList list1, list2; // input
	private GeoList tempList;
	private GeoElement ageo, bgeo, ngeo, dgeo, minGeo, maxGeo, Q1geo, Q3geo, medianGeo, 
	                   widthGeo, densityGeo, useDensityGeo, isCumulative;
	private GeoNumeric  sum; // output sum    
	
	
	private int N; 	
	private double STEP;
	private double [] yval; // y value (= min) in interval 0 <= i < N
	private double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	//private double [] widths;
	
	private ExtremumFinder extrFinder;

	// maximum frequency of bar chart
	// this is used by stat dialogs when setting window dimensions
	private double freqMax;

	public double getFreqMax() {
		return freqMax;
	}


	/**
	 * Rectangle sum
	 * @param cons
	 * @param label
	 * @param f
	 * @param a
	 * @param b
	 * @param n
	 * @param d
	 * @param type
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n, NumberValue d,
								   int type) {
		
		super(cons);
		
		this.type = type;
		
		
		this.f = f;
		this.a = a;
		this.b = b;			
		this.n = n;
		this.d = d;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();
		dgeo = d.toGeoElement();
			
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}//AlgoFunctionAreaSums(cons,label,func,a,b,n,d,type)
	
	
	/**
	 * Upper o lower sum
	 * @param cons
	 * @param label
	 * @param f
	 * @param a
	 * @param b
	 * @param n
	 * @param type
	 */
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
	
	/**
	 *  BARCHART
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 * @param list1
	 */
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
	/**
	 *  BarChart [<list of data without repetition>, <frequency of each of these data>]
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param dummy
	 */
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
	
	/**
	 *  BarChart [<list of data without repetition>, <frequency of each of these data>, <width>]
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param width
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,  
			   GeoList list1, GeoList list2, NumberValue width) {

		super(cons);
		
		type = TYPE_BARCHART_FREQUENCY_TABLE_WIDTH;
		
		this.list1 = list1;
		this.list2 = list2;
		this.width = width;
		widthGeo = width.toGeoElement();

		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
	
	
	
	
	
	/**
	 *  BarChart [<list of data>, <width>]
	 * @param cons
	 * @param label
	 * @param list1
	 * @param n
	 */
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
		
	/**
	 *  HISTOGRAM[ <list of class boundaries>, <list of heights> ]
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoList list1, GeoList list2) {
		
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
		
	
	
	/**
	 *  Histogram [<list of class boundaries>, <list of raw data>, <useDensity>, <densityFactor>]
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param density
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoBoolean isCumulative,  
			   GeoList list1, GeoList list2,  GeoBoolean useDensity, GeoNumeric density) {

		super(cons);
		
		type = TYPE_HISTOGRAM_DENSITY;
		
		this.isCumulative = isCumulative;
		this.list1 = list1;
		this.list2 = list2;
		this.density = density;
		if(density!=null)
		densityGeo = density.toGeoElement();

		this.useDensityGeo = useDensity;
		
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement	
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
	
	
	
	
			
	/**
	 *  BOXPLOT
	 * @param cons
	 * @param label
	 * @param min
	 * @param Q1
	 * @param median
	 * @param Q3
	 * @param max
	 * @param a
	 * @param b
	 */
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

	/**
	 *  BOXPLOT (raw data)
	 * @param cons
	 * @param label
	 * @param list1
	 * @param a
	 * @param b
	 */
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
	
	public abstract String getClassName();

	// for AlgoElement
	protected void setInputOutput() {
		
		switch (type)
		{
		case TYPE_UPPERSUM:
		case TYPE_LOWERSUM:
		case TYPE_TRAPEZOIDALSUM:
		case TYPE_LEFTSUM:           //Ulven: 09.02.11			
			input = new GeoElement[4];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;		
			break;
		case TYPE_RECTANGLESUM:      //Ulven: 09.02.11
			input = new GeoElement[5];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;
			input[4] = dgeo;
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
			input[2] = widthGeo;
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
		case TYPE_HISTOGRAM_DENSITY:
			if(isCumulative == null){
				if(densityGeo == null){
					input = new GeoElement[3];
					input[0] = list1;		
					input[1] = list2;
					input[2] = useDensityGeo;
				}else{
					input = new GeoElement[4];
					input[0] = list1;		
					input[1] = list2;
					input[2] = useDensityGeo;
					input[3] = densityGeo;
				}
			}else{
				if(densityGeo == null){
					input = new GeoElement[4];
					input[0] = isCumulative;
					input[1] = list1;		
					input[2] = list2;
					input[3] = useDensityGeo;
				}else{
					input = new GeoElement[5];
					input[0] = isCumulative;
					input[1] = list1;		
					input[2] = list2;
					input[3] = useDensityGeo;
					input[4] = densityGeo;
				}
			}
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
		setOutputLength(1);
		setOutput(0,sum);
		setDependencies(); // done by AlgoElement
	}

	/**
 	 * function
 	 * @return function
 	 */
	public GeoFunction getF() {
		return f;
	}
	
	/**
 	 * number of intervals
 	 * @return number of intervals
 	 */
	public int getIntervals() {
		return N;
	}
	
	/**
	 * Returns length of step for sums
	 * @return length of step for sums
	 */
	public double getStep() {
		return STEP;
	}
	
	/**
	 * Returns list of function values
	 * @return list of function values
	 */
	public double [] getValues() {
		return yval;
	}

	/**
	 * Returns list of left borders of columns
	 * @return list of left borders of columns
	 */
	public double [] getLeftBorders() {
		return leftBorder;
	}	
	
	/**
	 * Returns the resulting sum
	 * @return the resulting sum
	 */
	public GeoNumeric getSum() {
		return sum;
	}
	
	/**
	 * Returns lower bound for sums and y-offset for boxplots
	 * @return lower bound for sums and y-offset for boxplots
	 */
	public NumberValue getA() {
		return a;
	}
	
	/**
	 * Returns upper bound for sums and y-scale for boxplots
	 * @return upper bound for sums and y-scale for boxplots
	 */
	public NumberValue getB() {
		return b;
	}
	
	/**
	 * Returns n
	 * @return n
	 */
	public GeoNumeric getN() {
		return (GeoNumeric)ngeo;
	}
	
	/**
	 * Returns d
	 * @return d
	 */
	public GeoNumeric getD() {
		return (GeoNumeric)dgeo;
	}
	
	/**
	 * Returns list of raw data for boxplot
	 * @return list of raw data for boxplot
	 */
	public GeoList getList1(){
		return list1;
	}
	
	/**
	 * Returns list of frequencies for histogram
	 * @return list of frequencies for histogram
	 */
	public GeoList getList2(){
		return list2;
	}
	
	/**
	 * Returns minimum
	 * @return minimum
	 */
	public GeoElement getMinGeo() {
		return minGeo;
	}

	/**
	 * Returns maximum
	 * @return maximum
	 */
	public GeoElement getMaxGeo() {
		return maxGeo;
	}

	/**
	 * Returns Q1
	 * @return Q1
	 */
	public GeoElement getQ1geo() {
		return Q1geo;
	}

	/**
	 * Returns Q3
	 * @return Q3
	 */
	public GeoElement getQ3geo() {
		return Q3geo;
	}

	/**
	 * Returns median
	 * @return median
	 */
	public GeoElement getMedianGeo() {
		return medianGeo;
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
            double noOfSamples = Math.abs(ev.toScreenCoordXd(visibleMax) - ev.toScreenCoordX(visibleMin)) / SAMPLE_PIXELS;
            
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
		case TYPE_RECTANGLESUM:
		case TYPE_LEFTSUM:			

			if (!(f.isDefined() && ageo.isDefined() && bgeo.isDefined() 
					&& ngeo.isDefined())) 
				sum.setUndefined();
			
			/* Rectanglesum needs extra treatment */
			if( ( type==TYPE_RECTANGLESUM) && (!dgeo.isDefined()) ){	//extra parameter
				sum.setUndefined();
			}//if d parameter for rectanglesum
			
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
				
				/* Extra treatment for RectangleSum */
				if(type==TYPE_RECTANGLESUM){
					double dd=d.getDouble();
					if( (0.0<=dd) && (dd<=1.0) ){ 
						yval[i] = fun.evaluate(leftBorder[i]+dd*STEP);  //divider into step-interval
					}else{
						sum.setUndefined();
						return;
					}// if divider ok
				}else{
					yval[i] = fun.evaluate(leftBorder[i]);
				}//if 

	
				cumSum += yval[i];	
			}							
			
			// calc area of rectangles	or trapezoids
			if( (type==TYPE_RECTANGLESUM) || (type==TYPE_LEFTSUM) ){
				cumSum-=yval[N];		//Last right value not needed
			}else{
				cumSum-=(yval[0] + yval[N])/2;
			}//if rectangles or trapezoids			
			

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
			
			double mini = Double.POSITIVE_INFINITY;
			double maxi = Double.NEGATIVE_INFINITY;
			int minIndex=-1, maxIndex=-1;
			
			double step = n.getDouble();
			
			int rawDataSize = list1.size();
			
			if (step < 0 || Kernel.isZero(step) || rawDataSize < 2)
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
			
			// find maximum frequency
			// this is used by the stat dialogs
			freqMax = 0.0;
			for(int k = 0; k < yval.length; ++k){
				if(yval[k] > freqMax)
					freqMax = yval[k];
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
		case TYPE_HISTOGRAM_DENSITY:
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

			// set the density scale factor
			// default is 1; densityFactor == -1 means do not convert from frequency to density
			double densityFactor;
			
			if(useDensityGeo == null){
				densityFactor = 1;
			} 
			else if(!((GeoBoolean)useDensityGeo).getBoolean() )
			{
				densityFactor = -1;
			} 
			else 
			{
				densityFactor = (density != null) ? density.getDouble() : 1;
				if(densityFactor <=0 && densityFactor != -1)
				{
					sum.setUndefined();
					return;
				}
			}

			
			// list2 contains raw data
			// eg Histogram[{1,1.5,2,4},{1.0,1.1,1.1,1.2,1.7,1.7,1.8,2.2,2.5,4.0}]
			// problem: if N-1 = list2.size() then raw data is not assumed
			// fix for now is to check if other parameters are present, then it must be raw data
			if (N-1 != list2.size() || useDensityGeo != null || isCumulative !=null)
			{ 

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
				
				//TODO: finish right histogram option for 2nd case below
				boolean isHistogramRight = false;
				
				for (int i=0; i < list2.size() ; i++) {
					geo = list2.get(i);
					if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
					else { sum.setUndefined(); return; }

					// if datum is outside the range, set undefined
					if (datum < leftBorder[0] || datum > leftBorder[N-1] ) { sum.setUndefined(); return; }

					if(!isHistogramRight){
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

					else
					{
						// fudge to make the first boundary eg 10 <= x <= 20
						// all others are 10 < x <= 20 (HistogramRight)
						double oldMinBorder = leftBorder[0];
						leftBorder[0] += Math.abs(leftBorder[0] / 100000000);

						// check which class this datum is in
						for (int j=1; j <= N; j++) {
							//System.out.println("checking "+leftBorder[j]);
							if (datum < leftBorder[j]) 
							{
								//System.out.println(datum+" "+j);
								yval[j-1]++;
								break;
							}
						}
						leftBorder[N-1] = oldMinBorder;
					}


				}
				
				//convert to cumulative frequencies if cumulative option is set
				if(isCumulative != null && ((GeoBoolean)isCumulative).getBoolean()){
					for (int i=1; i < N; i++)  
						yval[i] += yval[i-1];
				}
				
				
				// turn frequencies into frequency densities
				// if densityFactor = -1 then do not convert frequency to density
				if(densityFactor != -1)
					for (int i=1; i < N; i++)  
						yval[i-1] = densityFactor * yval[i-1] / (leftBorder[i] - leftBorder[i-1]);

				
				// area of rectangles = total frequency	* densityFactor			
				sum.setValue(Math.abs( list2.size() * densityFactor ));	
				
				// find maximum frequency
				// this is used by the stat dialogs
				freqMax = 0.0;
				for(int k = 0; k < yval.length; ++k){
					if(yval[k] > freqMax)
						freqMax = yval[k];
				}
				

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
			

			// find maximum frequency
			// this is used by the stat dialogs
			freqMax = 0.0;
			for(int k = 0; k < yval.length; ++k){
				if(yval[k] > freqMax)
					freqMax = yval[k];
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
	
	/**
	 * Returns true iff this is trapezoidal sum
	 * @return true iff this is trapezoidal sums
	 */
	public boolean useTrapeziums() {
		switch (type)
		{
		case TYPE_TRAPEZOIDALSUM:
			return true;
		default :
			return false;
		}
	}
	
	/**
	 * Returns true iff this is histogram
	 * @return true iff this is histogram
	 */
	public boolean isHistogram() {
		switch (type)
		{
		case TYPE_HISTOGRAM:
		case TYPE_HISTOGRAM_DENSITY:
			return true;
		default :
			return false;
		}
	}
	
	/**
	 * Returns type of the sum, see TYPE_* constants of this class
	 * @return type of the sum
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns true iff this is boxplot
	 * @return true iff this is boxplot
	 */
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

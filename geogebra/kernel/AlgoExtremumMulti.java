/*
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;
import geogebra.euclidian.EuclidianView;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Command:	Extremum[ <function>, left-x, right-x]
 * 
 * Numerically calculates Extremum points for <function> in open interval <left-x,right-x>
 * without being dependent on being able to find the derivate of <function>.
 * 
 * Restrictions for use:
 * 		 <function> should be continuous and be reasonably well-behaved in the interval.
 * 		 (For instance the function should not be wildly oscillating in small x-intervals,
 * 		  and not have several hundred extremums in the interval.)
 * 		 (The interval should be an open interval, extremums should not be on leftx or rightx.)
 * Breaking restrictions will give unpredictable results.
 * 
 * This routine tries to find all extremums visible by eyesight in the graphic screen, 
 * but might oversee more extremums not being visible. (Those might become visible by zooming howeveer.)
 * 
 * Algorithm is:
 * 		-Sample every 5 pixel
 * 		-Find intervals with possible extremums
 * 		-Use Brent's algorithm (see geogebra.kernel.optimization.ExtremumFinder) on the intervals
 * 
 *  
 * @author 	Hans-Petter Ulven
 * @version 2011-02.24
 */

public class AlgoExtremumMulti extends AlgoElement {
	
	// Constants
	private static final long 	serialVersionUID 		= 	1L;
	private static final int	PIXELS_BETWEEN_SAMPLES	=	  5;		// Open for empirical adjustments
    private static final int   	MAX_SAMPLES				=	400;		// -"- (covers a screen up to 2000 pxs...)
    private static final int	MIN_SAMPLES				=	 50;		// -"- (covers up to 50 in a 250 pxs interval...)	;
    
    //Input-Output
   	private GeoFunctionable 	function; 			// input
	private GeoFunction			f;
	private NumberValue     	left;	  			// input
	private GeoElement			geoleft;
	private NumberValue			right;				// input
	private GeoElement			georight;
    private GeoPoint[] 			extremumPoints;		// output  	
    
    //Flags
    private boolean				initLabels;
    private boolean				setLabels;
    
    //Variables
    private GeoPoint			tempPoint;
    private String[]			labels;
    private String				labelprefix;
    private double[]			curXValues;	
    private int					curNumberOfXValues;

    /** Computes "all" Extremums of f in <l,r> */
    public AlgoExtremumMulti(Construction cons, String[] labels, GeoFunctionable function,NumberValue left,NumberValue right) {
    	super(cons);
    	this.function=function;
    	this.f=function.getGeoFunction();
    	this.left=left;
    	this.geoleft=left.toGeoElement();
    	this.right=right;
    	this.georight=right.toGeoElement();
    	
    	tempPoint=new GeoPoint(cons);
    	
    	this.labels=labels;
    	this.setLabels=!cons.isSuppressLabelsActive();
    	
    	//if( (labels!=null) && (labels.length>0)){labelprefix=getLabelPrefix(labels[0]);	}	debug("Label-prefix: "+labelprefix);
    	
    	// make sure root points are not null
    	int number = labels==null ? 1: Math.max(1,labels.length);
    	extremumPoints = new GeoPoint[0];
    	/*
    	extremumPoints = new GeoPoint[1];				//ExtremumPolynomial has 0???
    	extremumPoints[0]=new GeoPoint(cons);			//Have to make a valid point
    	extremumPoints[0].setCoords(0,0,1);
    	extremumPoints[0].update();
    	extremumPoints[0].setUndefined();
    	extremumPoints[0].update();
    	*/
    	initExtremumPoints(number);							//ExtremumPolynomial uses number=>index out of bonds...	
    	initLabels=true;
    	
    	    	
    	setInputOutput();
    	
    	compute();
    	
    	// Show at least one root point in algebra view
    	// Copied from AlgoRootsPolynomial...
    	if(!extremumPoints[0].isDefined() ) {
    		extremumPoints[0].setCoords(0,0,1);
    		extremumPoints[0].update();
    		extremumPoints[0].setUndefined();
    		extremumPoints[0].update();
    	}//if list not defined
    	
    }//constructor

    public String getClassName() {
        return "AlgoExtremumMulti";
    }//getClassName()
    
    public GeoPoint[] getExtremumPoints() {
    	return extremumPoints;
    }//getExtremumPoints()

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = function.toGeoElement();
        input[1] = geoleft;
        input[2] = georight;
        
        //setOutputLength(1);
        //setOutput(0, E);
        
        output=extremumPoints;

        noUndefinedPointsInAlgebraView();
        
        setDependencies(); // done by AlgoElement
    }//setInputOutput()
    

    protected final void compute() {

        

        double		l				=	left.getDouble();
        double		r				=	right.getDouble();
 

    	if (    !function.toGeoElement().isDefined() || !geoleft.isDefined()    ||   
    		    !georight.isDefined() 				// || (right.getDouble()<=left.getDouble() )    
    	) {
    		curNumberOfXValues=0;		//flag
    		//return;
    	}else {
    	 
    	
    		if(l>r){double tmp=l;l=r;r=tmp;}		//correct if negative interval
    	
    		GeoFunction geofunc = function.getGeoFunction();

    		RealRootFunction rrfunc = geofunc.getRealRootFunctionY();
    	
    		/// ---  Algorithm --- ///
    	
    		int n = findNumberOfSamples(l,r);
    		int m = n;
    		try{	//To catch eventual wrong indexes in arrays...
    			//This might be a better strategy than using the screen pixels (?):
    			do{																//debug("doing samples: "+m);
    				curXValues=findExtremums(rrfunc,l,r,m);
    				curNumberOfXValues=curXValues.length;
    				m=curNumberOfXValues*2;
    			}while( (curNumberOfXValues>(m/2)) && (m<MAX_SAMPLES));			//debug("extremums: "+curNumberOfXValues+" samples: "+m);
    			if(m>MAX_SAMPLES) Application.debug("We have probably lost some extremums...");
    			//Could do(?):
    			//m=n
    			//repeat
    			//x[]=findExtremums(rrfunc,l,r,m);
    			//m=x.length*1.?
    			//until x.length<m*0.? or m>MAX_SAMPLES
    		}catch(Exception e){
    			Application.debug("Exception in findExtremums() "+e.toString());
    		}//try-catch
    	              
    		setExtremumPoints(curXValues,curNumberOfXValues);
    	}//if input is ok?
  
    }//compute()
    
    /** Main algorithm, public for eventual use by other commands
     *  ToDo:
     *     	private after testing
     *     	Use <l-deltax,r+deltax> to cover endpoints of interval?
     *     	(Now it might miss extremums very close to interval ends.) 
     */
    @SuppressWarnings("unchecked")
	public final static double[] findExtremums(RealRootFunction rrfunc,double l,double r,int n){
    	double[]	y	=	new double[n+1];					//n+1 y-values
    	boolean[]	grad=	new boolean[n];				// n  gradients, true: f'>=0, false: f'<0
    	ArrayList	xres=	new ArrayList();
    	
    	double		deltax	= (r-l)/n;					//x[i]=l+i*deltax, don't need x-array
    	
    	//cons/kernel unusable in static method: ExtremumFinder extrfinder = cons.getExtremumFinder();
    	ExtremumFinder extrfinder=new ExtremumFinder();
    	
    	for(int i=0;i<=n;i++){							//debug("iteration: "+i);

    		y[i]=rrfunc.evaluate(l+i*deltax);
    		if( i>0){									//grad only from 1 to n-1
    			if(y[i]>=y[i-1]){						//grad positive or zero
    				grad[i-1]=true;
    			}else{									//grad negative
    				grad[i-1]=false;
    			}//if gradient >=0 or <0
    													//debug("grad "+(i-1)+": "+grad[i-1]);
    		}//if gradients can be calculated

    		if(i>1){
    			double xval;
    			if( (grad[i-2]) && (!grad[i-1]) ) {       //change of grad, possible max point
    				xval=extrfinder.findMaximum(l+(i-2)*deltax,l+i*deltax,rrfunc,3.0E-8);
    				if (!Double.isNaN(xval)) xres.add(new Double(xval));						//debug("Extr: "+xval);
    			}else if (  (!grad[i-2])   && (grad[i-1])   ){  
    				xval=extrfinder.findMinimum(l+(i-2)*deltax,l+i*deltax,rrfunc,3.0E-8);
    				if (!Double.isNaN(xval)) xres.add(new Double(xval));						//debug("Extr: "+xval);
    			}else {
    				//debug("did nothing");
    			}//if possible extremum between x[i-2] and x[i]
    		}//if grad analysis possible
    		
    	}//for all n sample points

    	Iterator iter=xres.iterator(); 
    	Double xd;
    	double[] result=new double[xres.size()];
    	/* not sorted
    	int i=0;
    	while(iter.hasNext()){
    		xd=(Double)iter.next();
    		result[i]=xd.doubleValue();
    		i++;
    	}//while xvalues
    	*/
    	for(int i=0;i<xres.size();i++) {
    		xd=(Double)xres.get(i);
    		result[i]=xd.doubleValue();
    	}//for all x
    	return result;
    }//findExtremums(rrfunc,l,r)
    
    /// --- Private methods --- ///
    //  Make all private after testing...
    
    public final int findNumberOfSamples(double l,double r){
    	//Find visible area of graphic screen: xmin,xmax,ymin,ymax
    	//pixels_in_visible_interval=...
    	//n=pixels_in_visible_interval/PIXELS_BETWEEN_SAMPLES;
    	
    	EuclidianView ev	=	app.getEuclidianView();
    	double visiblemax	=	ev.getXmax();
    	double visiblemin	=	ev.getXmin();
    	double visiblepixs	=	ev.toScreenCoordXd(visiblemax)-ev.toScreenCoordXd(visiblemin);
    	//debug("Visible pixels: "+visiblepixs);
    	double pixsininterval=	visiblepixs*(r-l)/(visiblemax-visiblemin);
    	//debug("Pixels in interval: "+pixsininterval);
    	int n=(int)Math.round(Math.max(Math.min(pixsininterval/PIXELS_BETWEEN_SAMPLES,MAX_SAMPLES),MIN_SAMPLES));
    	
    	//debug("Samples: "+n);    
    	return n;
    	
    }//findNumberOfSamples()
    
    //Make points from the x-values
    private final void setExtremumPoints(double[] curXValues,int curNumberOfXValues){
    	//listArray(curXValues);
    	initExtremumPoints(curNumberOfXValues);
    	//listPoints(extremumPoints);
    	for(int i=0;i<curNumberOfXValues;i++) {
    		extremumPoints[i].setCoords(curXValues[i],f.evaluate(curXValues[i]),1.0d);
    	}//for all x-values
    	
    	if(setLabels) updateLabels(curNumberOfXValues);
    	
    }//setExtremumPoints(double[],number)
    
	private final void setLabels(String[] labels){
		this.labels=labels;
		setLabels=true;
		if (labels != null) initExtremumPoints(labels.length);
		update();
	}//setLabels(String[])
	
	private final void updateLabels(int number){
		if(initLabels){
			GeoElement.setLabels(labels,extremumPoints);
			initLabels=false;
		}else{
			for(int i=0;i<number;i++) {
				if(!extremumPoints[i].isLabelSet() ){	//check labeling
					//use user specified label if there is one
					String newLabel=(labels != null && i < labels.length) ? labels[i] : null;
					extremumPoints[i].setLabel(newLabel);
				}//if label not set
			}//for
		}//if initLabels
	}//updateLabels(int)
	
	// Initialize points
	private final void initExtremumPoints(int number){
		int length=extremumPoints.length;
		GeoPoint[] temp=new GeoPoint[number];
		if(length<number) {
			for (int i=0;i<length;i++) {
				temp[i]=extremumPoints[i];
				temp[i].setCoords(0,0,1);	//init as defined
			}//for
			for(int i=length;i<temp.length;i++) {
				temp[i]=new GeoPoint(cons);
				temp[i].setCoords(0,0,1);
				temp[i].setParentAlgorithm(this);
			}//for
		
		}else{	//Some lost because of parameter change or zooming?
			for (int i=0;i<number;i++) {
				temp[i]=extremumPoints[i];
				temp[i].setCoords(0,0,1);
				temp[i].setParentAlgorithm(this);
			}//for all current extrpts
			for(int i=number;i<length;i++){
				//extremumPoints[i].setUndefined();		//remove() removes algo as well...
				//extremumPoints[i].update();
				extremumPoints[i].doRemove();
			}//for the lost ones
		}//if
		extremumPoints=temp;
		//If we want to fix labeling inconsistencies...
		//listLabels(labels);
		//String prefix=getLabelPrefix(labels[0]);
		//GeoElement.setLabels(labelprefix,extremumPoints); //this one might solve the label problem?
		output=extremumPoints;		
	}//initExtremumPoints(number)
	
	//Doesn't seem to work, even if copyied from AlgoExtremumPolynomium?
	private final void noUndefinedPointsInAlgebraView() {
    	 for (int i=1; i < extremumPoints.length; i++) 
    		 extremumPoints[i].showUndefinedInAlgebraView(false);
	}//noUndefinedPointsInAlgebraView()
	
	// If we want to fix some inconsistenciew in labeling?
	private final String getLabelPrefix(String first){
		int pos=first.indexOf("_");
		if(pos<0){
			return first;
		}else{
			String pre=first.substring(0,pos);
			return pre;
		}//if indexed
	}//getLabelPrefix(String)
	

    

// * //--- SNIP (after debugging and testing) -------------------------   
    /// --- Test interface --- ///
    //  Running testcases from external testscript Test_Extremum.bsh from plugin scriptrunner.
    
    //Test constructor:
    public AlgoExtremumMulti(Construction cons){
    	super(cons);
    	
    }//Test constructor

    
	private static final boolean DEBUG	= true;
	
    private final static void debug(String s) {
        if(DEBUG) {
        	Application.debug(s);
        }//if()
    }//debug()       
    
    private final static void listArray(double[] a){
    	int l=a.length;
    	System.out.println("Length: "+l);
    	for(int i=0;i<l;i++){
    		System.out.println("a["+i+"]: "+a[i]);
    	}//for
    }//listArray(a)
    
    private final static void listLabels(String[] a){
    	int l=a.length;
    	System.out.println("Length: "+l);
    	for(int i=0;i<l;i++){
    		System.out.println("Label["+i+"]: "+a[i]);
    	}//for
    }//listLabels(a)
    
    private final static void listPoints(GeoPoint[] gpts) {
    	int n=gpts.length;
    	System.out.println("Length: "+n);
    	for(int i=0;i<n;i++) {
    		System.out.println("Label: "+gpts[i].getLabel()+"     pt["+i+"]: ("+gpts[i].x+","+gpts[i]+")");
    	}//for
    }//listPoints(GeoPoint[])
 
    
    
    
// */ //--- SNIP end ---------------------------------------    
    
}//class AlgoExtremumNumerical


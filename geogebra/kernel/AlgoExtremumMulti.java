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
 * Numerically calculates Extremum points for <function> in  interval <left-x,right-x>
 * without being dependent on being able to find the derivate of <function>.
 * 
 * Restrictions for use:
 * 		 <function> should be continuous and be reasonably well-behaved in the interval.
 * 		 (For instance the function should not be wildly oscillating in small x-intervals,
 * 		  and not have several hundred extremums in the interval.)
 * 		 (The interval should be an open interval, extremums should not be on leftx or rightx.)(?)
 * Breaking restrictions could go well but could also give unpredictable results.
 * 
 * This routine tries to find all extremums visible by eyesight in the graphic screen, 
 * but might oversee more extremums not being visible. (Those might become visible by zooming howeveer.)
 * 
 * Algorithm is:
 * 		-Sample every 5 pixel
 * 		-Find intervals with possible extremums
 * 		-Use Brent's algorithm (see geogebra.kernel.optimization.ExtremumFinder) on the intervals
 * 
 * 01.03.2011:
 * 		-Got rid of false extremums near asymptotes (even if the user should not have them in the interval...)
 *       by testing gradient
 * 07.03.2011:
 * 		Rewrote to extend abstract AlgoGeoPointsFunction which has all the label code.       
 *  
 * @author 	Hans-Petter Ulven
 * @version 2011-03.07
 */

public class AlgoExtremumMulti extends AlgoGeoPointsFunction {
	
	// Constants
	private static final long 	serialVersionUID 		= 	1L;
	private static final int	PIXELS_BETWEEN_SAMPLES	=	  5;		// Open for empirical adjustments
    private static final int   	MAX_SAMPLES				=	400;		// -"- (covers a screen up to 2000 pxs...)
    private static final int	MIN_SAMPLES				=	 50;		// -"- (covers up to 50 in a 250 pxs interval...)	;
    private static final double	MAX_GRADIENT			=	1.0E-4;		// Filter out false extremums near discontinuity 10^8 too large
    
    //Input-Output
	private GeoFunction			f;
	private NumberValue     	left;	  			// input
	private GeoElement			geoleft;
	private NumberValue			right;				// input
	private GeoElement			georight;  	

    /** Computes "all" Extremums of f in <l,r> */
    public AlgoExtremumMulti(Construction cons, String[] labels, GeoFunction function,NumberValue left,NumberValue right) {
    	super(cons,labels,!cons.isSuppressLabelsActive(),function);			//set f,g,l null
    	this.f=function;
    	this.left=left;
    	this.geoleft=left.toGeoElement();
    	this.right=right;
    	this.georight=right.toGeoElement();
    	
    	// make sure root points are not null
    	int number = labels==null ? 1: Math.max(1,labels.length);
    	
    	setInputOutput();
    	
    	compute();
    	    	
    	// Show at least one root point in algebra view
    	// Copied from AlgoRootsPolynomial...
    	GeoPoint[] gpt=getPoints();					//Ancestor
    	if(!gpt[0].isDefined() ) {
    		gpt[0].setCoords(0,0,1);
    		gpt[0].update();
    		gpt[0].setUndefined();
    		gpt[0].update();
    	}//if list not defined
    	
    }//constructor

    public String getClassName() {
        return "AlgoExtremumMulti";
    }//getClassName()
    
    public GeoPoint[] getExtremumPoints() {
    	return getPoints();
    }//getExtremumPoints()

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = f.toGeoElement();
        input[1] = geoleft;
        input[2] = georight;
        
        //setOutputLength(1);
        //setOutput(0, E);
        
        output=getPoints();

        noUndefinedPointsInAlgebraView(getPoints());
        
        setDependencies(); // done by AlgoElement
    }//setInputOutput()
    

    protected final void compute() {

        double[]	extremums		=	new double[0];
        int			numberOfExtremums=	0;

        double		l				=	left.getDouble();
        double		r				=	right.getDouble();
 

    	if (    !f.toGeoElement().isDefined() || !geoleft.isDefined()    ||   
    		    !georight.isDefined() 				// || (right.getDouble()<=left.getDouble() )    
    	) {
    		setPoints(new double[1],0);				//0 flags it
    	}else {

    		if(l>r){double tmp=l;l=r;r=tmp;}		//correct user input

    		RealRootFunction rrfunc = f.getRealRootFunctionY();
    	
    		/// ---  Algorithm --- ///
    	
    		int n = findNumberOfSamples(l,r);
    		int m = n;
    		try{	//To catch eventual wrong indexes in arrays...
    			do{																debug("doing samples: "+m);
    				extremums=findExtremums(rrfunc,l,r,m);
    				if(extremums==null){numberOfExtremums=0;}else{ numberOfExtremums=extremums.length;}
    				
    				if(numberOfExtremums<m/2) {
    					break;
    				}else{
    					m=m*2;
    				}//
    			}while( m<MAX_SAMPLES );
    			if(m>MAX_SAMPLES) Application.debug("We have probably lost some extremums...");
    		}catch(Exception e){
    			Application.debug("Exception in compute() "+e.toString());
    		}//try-catch
    	    if(numberOfExtremums==0){
    	    	setPoints(new double[1],0);
    	    }else{
    	    	setPoints(extremums,numberOfExtremums);
    	    }//if null
    	}//if input is ok?
  
    }//compute()
    
    /** Main algorithm, public for eventual use by other commands
     *  Finds a samplesize depending on screen coordinates
     *  Samples n intervals and collects extremums in intervals
     */
    @SuppressWarnings("unchecked")
	public final static double[] findExtremums(RealRootFunction rrfunc,double l,double r,int samples){
    	double[]	y	=	new double[samples+1];					//n+1 y-values
    	boolean[]	grad=	new boolean[samples];				// n  gradients, true: f'>=0, false: f'<0
    	ArrayList<Double>	xlist=	new ArrayList<Double>();
    	
    	double		deltax	= (r-l)/samples;					//x[i]=l+i*deltax, don't need x-array
    	
    	//cons/kernel unusable in static method: ExtremumFinder extrfinder = cons.getExtremumFinder();
    	ExtremumFinder extrfinder=new ExtremumFinder();
    	
    	for(int i=0;i<=samples;i++){							//debug("iteration: "+i);

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
    			double xval=0.0;
    			double curleft=l+(i-2)*deltax;
    			double curright=curleft+2*deltax;
    			if( 	(grad[i-2]) && (!grad[i-1]) ) {		//max 
    				//if( ((y[i-1]-y[i-2])/deltax)<MAX_GRADIENT) {
    					xval=extrfinder.findMaximum(curleft,curright,rrfunc,3.0E-8);      			debug("Gradient for "+xval+": "+gradient(rrfunc,xval,curleft,curright));
    				if(gradient(rrfunc,xval,curleft,curright)<1.0E-4){
    					xlist.add(new Double(xval));	
    				}//If not too large gradient
    			}else if (   (!grad[i-2]) && (grad[i-1]) ){	    //min
    				//if( ((y[i-2]-y[i-1])/deltax) < MAX_GRADIENT ) {
    					xval=extrfinder.findMinimum(curleft,curright,rrfunc,3.0E-8);    			debug("Gradient for "+xval+": "+gradient(rrfunc,xval,curleft,curright));
    				if(gradient(rrfunc,xval,curleft,curright)<1.0E-4) {
    					xlist.add(new Double(xval));					
    																
    				}//if not too large gradient				
    														
    			}else {
    				//debug("did nothing");
    			}//if possible extremum between x[i-2] and x[i]											
    		}//if grad analysis possible
    																	
    	}//for all n sample points

    	double[] result=new double[xlist.size()];
    	for(int i=0;i<xlist.size();i++) {
    		result[i]=xlist.get(i);
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
    
	
    private final static double gradient(RealRootFunction rrf,double x,double l,double r){
    	double dx=(r-l)/1E8;
    	return Math.abs((rrf.evaluate(x+dx)-rrf.evaluate(x))/dx);
    }//gradient(
    

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


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
import geogebra.main.Application;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.roots.RealRootFunction;



/**
 * Command: Min[<function>,left-x,right-x]
 * 
 * Numerically calculates Extremum point for <function> in closed interval [left-x,right-x]
 * without being dependent on being able to find the derivate of <function>.
 * 
 * Restrictions for use:
 * 		 <function> should be continuous and only have one extremum in the interval [left-x,right-x]
 * 
 * Breaking restrictions will give unpredictable results:
 *    -Will usually find the first minimum if more than one extremums
 *    -Unpredictable results if discontinuous in interval
 * 
 * Uses Brent's algorithm in geogebra.kernel.optimization.ExtremumFinder;
 *  
 * @author 	Hans-Petter Ulven
 * @version 2011-02.20
 */

public class AlgoFunctionMin extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunctionable function; 	//input
	private GeoFunction		f;
	private NumberValue     left;	  	//input
	private GeoElement		geoleft;
	private NumberValue		right;		//input
	private GeoElement		georight;
    private GeoPoint 		E; 			// output
	private ExtremumFinder	extrFinder	=	null;		
    private static double	xres;	//static x for test interface

    /** Constructor for Extremum[f,l,r] */
    public AlgoFunctionMin(Construction cons, String label, GeoFunctionable function,NumberValue left,NumberValue right) {
    	super(cons);
    	this.function=function;
    	this.f=function.getGeoFunction();
    	this.left=left;
    	this.geoleft=left.toGeoElement();
    	this.right=right;
    	this.georight=right.toGeoElement();
    	
    	E=new GeoPoint(cons);					//Put an extremum point in the user interface from the very start
    	E.setCoords(0.0,0.0,1.0);
    	
    	setInputOutput();
    	
    	compute();
    	
    	E.setLabel(label);
    	
    }//constructor

    public String getClassName() {
        return "AlgoFunctionMin";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = function.toGeoElement();
        input[1] = geoleft;
        input[2] = georight;
        
        setOutputLength(1);
        setOutput(0, E);

        setDependencies(); // done by AlgoElement
    }
    
    public GeoPoint getPoint() {
        return E;
    }//getNumericalExtremum()
    

    protected final void compute() {
        double		l				=	left.getDouble();
        double		r				=	right.getDouble();
        double		min				=	0.0d;    	
    	
    	if (    !function.toGeoElement().isDefined() || !geoleft.isDefined()    ||   
    		    !georight.isDefined()         		 || (right.getDouble()<=left.getDouble() )    
    	) {
    		E.setUndefined();
    		return;
    	}//if input is ok? 
    	

    	
    	//Brent's algorithm    	
    	extrFinder = new ExtremumFinder();
		RealRootFunction fun = f.getRealRootFunctionY();    

		min		   = extrFinder.findMinimum(l,r,fun,5.0E-8);


        
        E.setCoords(min,f.evaluate(min),1.0);
        E.updateRepaint();

        

    }//compute()
    
 
    

// * //--- SNIP (after debugging and testing) -------------------------   
    /// --- Test interface --- ///
    //  Running testcases from external testscript Test_Extremum.bsh from plugin scriptrunner.
    
    /** Test constructor */
    public AlgoFunctionMin(Construction cons){
    	super(cons);
    	this.cons=cons;
    	E=new GeoPoint(cons);					//Put an extremum point in the user interface from the very start
    	E.setCoords(0.0,0.0,1.0);    	
    }//test constructor
    
    public final static double getX(){
    	return xres;
    }//getX()
    
	private static final boolean DEBUG	= true;
	
    private final static void debug(String s) {
        if(DEBUG) {
        	Application.debug(s);
        }//if()
    }//debug()       
    
    private String   info(int i,double l,double m,double r){
    	return "Iteration "+i+":\n"+l+"     "+m+"     "+r+"     "+f.evaluate(l)+"     "+f.evaluate(m)+"     "+f.evaluate(r);
    }//info()
    
    
    
// */ //--- SNIP end ---------------------------------------    
    
}//class AlgoEFuntionMin



/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMirrorPointPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.MyDouble;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMirror extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Mirrorable out;   
    private GeoElement geoIn, geoOut; 
    private GeoLine mirrorLine;   
    private GeoPoint mirrorPoint;      
    private GeoConic mirrorConic;      
    private GeoElement mirror;
    
    /**
     * Creates new "mirror at point" algo
     * @param cons
     * @param label
     * @param in
     * @param p
     */
    AlgoMirror(Construction cons, String label,GeoElement in,GeoPoint p) {
    	this(cons, in, null, p, null);  
    	 geoOut.setLabel(label);
    }
    
    /**
     * Creates new "mirror at conic" algo
     * @param cons
     * @param label
     * @param in
     * @param c
     */
    AlgoMirror(Construction cons, String label,GeoElement in,GeoConic c) {
    	this(cons,in,null,null,c);  
    	geoOut.setLabel(label);
    }
    
    /**
     * Creates new "mirror at line" algo 
     * @param cons
     * @param label
     * @param in
     * @param g
     */
    AlgoMirror(Construction cons, String label,GeoElement in,GeoLine g) {
    	
    	this(cons, in, g, null, null);
    	 geoOut.setLabel(label);
    }    
    
    /**
     * Creates new "mirror at *" algo
     * @param cons
     * @param in
     * @param g
     * @param p
     * @param c
     */
    AlgoMirror(Construction cons, GeoElement in, GeoLine g, GeoPoint p, GeoConic c) {
        super(cons);
        //this.in = in;      
        mirrorLine = g;
        mirrorPoint = p;
        mirrorConic = c; // Michael Borcherds 2008-02-10
        
        if (g != null)
        	mirror = g;
		else if (p != null)
			mirror = p;
		else
			mirror = c; // Michael Borcherds 2008-02-10
              
        geoIn = in;
        if(geoIn.isGeoList()){
        	geoOut = new GeoList(cons);
        }
        if (mirror instanceof GeoConic && geoIn instanceof GeoLine){
        	out = new GeoConic(cons);
        	geoOut = (GeoElement)out;
        }
        else if (mirror instanceof GeoConic && geoIn instanceof GeoConic && 
        		(!((GeoConic)geoIn).isCircle()||!((GeoConic)geoIn).keepsType())){
        	out = new GeoCurveCartesian(cons);
        	geoOut = (GeoElement)out;
        }
        else if(geoIn instanceof GeoPolygon || geoIn instanceof GeoPolyLine){
        	out = (Mirrorable) geoIn.copyInternal(cons);               
        	geoOut = out.toGeoElement();
        	
        }
        else if(geoIn instanceof Mirrorable){
        	out = (Mirrorable) geoIn.copy();               
        	geoOut = out.toGeoElement();
        	
        }else if (geoIn instanceof GeoFunction && mirror!= mirrorPoint){
        	out = new GeoCurveCartesian(cons);
        	geoOut = (GeoElement)out;
        }else if (geoIn instanceof GeoFunction && mirror == mirrorPoint){
        	geoOut = geoIn.copy();
        }
        setInputOutput();
              
        cons.registerEuclidianViewAlgo(this);
        
        compute();                                     
    }           
    
    public String getClassName() {
        return "AlgoMirror";
    }
    
    public int getRelatedModeID() { 	   	
		if (mirror.isGeoLine()) {
			return EuclidianConstants.MODE_MIRROR_AT_LINE;
		} else if (mirror.isGeoPoint()) {
			return EuclidianConstants.MODE_MIRROR_AT_POINT;
		} else {
			return EuclidianConstants.MODE_MIRROR_AT_CIRCLE;
		}
    	
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = geoIn; 
        input[1] = mirror;
        
        setOutputLength(1);        
        setOutput(0,geoOut);        
        setDependencies(); // done by AlgoElement
    }           
    
    /**
     * Returns the transformed geo
     * @return transformed geo
     */
    GeoElement getResult() { 
    	return geoOut; 
    }       

    
    protected final void compute() {
    	if(geoIn.isGeoList()){
    		return;
    	}
    	if(mirror instanceof GeoConic && geoIn instanceof GeoLine){
    		((GeoLine)geoIn).toGeoConic((GeoConic)geoOut);    		
    	}
    	else if(mirror instanceof GeoConic && geoIn instanceof GeoConic && geoOut instanceof GeoCurveCartesian){
    		((GeoConic)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut);    		
    	}
    	else if(geoIn instanceof GeoFunction && mirror != mirrorPoint){
    		((GeoFunction)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut);
    	}
    	else geoOut.set(geoIn);
        
        if (mirror == mirrorLine)
        	out.mirror(mirrorLine);
        else if (mirror == mirrorPoint){
        	if(geoOut.isGeoFunction())
        		((GeoFunction)geoOut).dilate(new MyDouble(kernel,-1), mirrorPoint);
        	else
        		out.mirror(mirrorPoint);
        }
        else ((ConicMirrorable)out).mirror(mirrorConic);
    }       
    
    final public String toString() {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("AMirroredAtB",geoIn.getLabel(),mirror.getLabel());

    }
}

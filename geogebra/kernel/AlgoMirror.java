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
    private GeoElement inGeo, outGeo; 
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
    	 outGeo.setLabel(label);
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
    	outGeo.setLabel(label);
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
    	 outGeo.setLabel(label);
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
              
        inGeo = in;
        if(inGeo.isGeoList()){
        	outGeo = new GeoList(cons);
        }
        if (mirror instanceof GeoConic && inGeo instanceof GeoLine){
        	out = new GeoConic(cons);
        	outGeo = (GeoElement)out;
        }
        /*
        else if (mirror instanceof GeoConic && geoIn instanceof GeoConic && 
        		(!((GeoConic)geoIn).isCircle()||!((GeoConic)geoIn).keepsType())){
        	out = new GeoCurveCartesian(cons);
        	geoOut = (GeoElement)out;
        }*/
        else if (mirror instanceof GeoConic && inGeo instanceof GeoConic && 
        		(!((GeoConic)inGeo).isCircle()||!((GeoConic)inGeo).keepsType())){
        	out = new GeoImplicitPoly(cons);
        	outGeo = (GeoElement)out;
        }
        else if(inGeo instanceof GeoPolygon || inGeo instanceof GeoPolyLine){
        	out = (Mirrorable) inGeo.copyInternal(cons);               
        	outGeo = out.toGeoElement();        	
        }
        else if(inGeo instanceof Mirrorable){
        	out = (Mirrorable) inGeo.copy();               
        	outGeo = out.toGeoElement();
        	
        }else if (inGeo instanceof GeoFunction && mirror!= mirrorPoint){
        	out = new GeoCurveCartesian(cons);
        	outGeo = (GeoElement)out;
        }else if (inGeo instanceof GeoFunction && mirror == mirrorPoint){
        	outGeo = inGeo.copy();
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
        input[0] = inGeo; 
        input[1] = mirror;
        
        setOutputLength(1);        
        setOutput(0,outGeo);        
        setDependencies(); // done by AlgoElement
    }           
    
    /**
     * Returns the transformed geo
     * @return transformed geo
     */
    GeoElement getResult() { 
    	return outGeo; 
    }       

    
    protected final void compute() {
    	if(inGeo.isGeoList()){
    		transformList((GeoList)inGeo,(GeoList)outGeo);
    		return;
    	}
    	if(mirror instanceof GeoConic && inGeo instanceof GeoLine){
    		((GeoLine)inGeo).toGeoConic((GeoConic)outGeo);    		
    	}
    	/*
    	else if(mirror instanceof GeoConic && geoIn instanceof GeoConic && geoOut instanceof GeoCurveCartesian){
    		((GeoConic)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut);    		
    	}*/
    	else if(mirror instanceof GeoConic && inGeo instanceof GeoConic && outGeo instanceof GeoImplicitPoly){
    		((GeoConic)inGeo).toGeoImplicitPoly((GeoImplicitPoly)outGeo);    		
    	}
    	else if(inGeo instanceof GeoFunction && mirror != mirrorPoint){
    		((GeoFunction)inGeo).toGeoCurveCartesian((GeoCurveCartesian)outGeo);
    	}
    	else outGeo.set(inGeo);
        
        if (mirror == mirrorLine)
        	out.mirror(mirrorLine);
        else if (mirror == mirrorPoint){
        	if(outGeo.isGeoFunction())
        		((GeoFunction)outGeo).dilate(new MyDouble(kernel,-1), mirrorPoint);
        	else
        		out.mirror(mirrorPoint);
        }
        else ((ConicMirrorable)out).mirror(mirrorConic);
    }       
    
    final public String toString() {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("AMirroredAtB",inGeo.getLabel(),mirror.getLabel());

    }
    @Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if(!(out instanceof GeoList))
			out = (Mirrorable)outGeo;
		
	}
}

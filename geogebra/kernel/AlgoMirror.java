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
    
    AlgoMirror(Construction cons, String label,Mirrorable in,GeoPoint p) {
    	this(cons, in, null, p, null);  
    	 geoOut.setLabel(label);
    }
    
    AlgoMirror(Construction cons, String label,Mirrorable in,GeoConic c) {
    	this(cons, in, null, null, c);  
    	 geoOut.setLabel(label);
    }
    
    AlgoMirror(Construction cons, String label,Mirrorable in,GeoLine g) {
    	this(cons, in, g, null, null);
    	 geoOut.setLabel(label);
    }    
    
    AlgoMirror(Construction cons, Mirrorable in, GeoLine g, GeoPoint p, GeoConic c) {
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
              
        geoIn = in.toGeoElement();
        out = (Mirrorable) geoIn.copy();               
        geoOut = out.toGeoElement();                       
        setInputOutput();
              
        cons.registerEuclidianViewAlgo(this);
        
        compute();                                     
    }           
    
    public String getClassName() {
        return "AlgoMirror";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = geoIn; 
        input[1] = mirror;
        
        output = new GeoElement[1];        
        output[0] = geoOut;        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { 
    	return geoOut; 
    }       

    final public boolean wantsEuclidianViewUpdate() {
        return geoIn.isGeoImage();
    }

    protected final void compute() {
        geoOut.set(geoIn);
        
        if (mirror == mirrorLine)
        	out.mirror(mirrorLine);
        else if (mirror == mirrorPoint)
        	out.mirror(mirrorPoint);
        else if (geoOut instanceof GeoPoint) // invert Point in Circle
        	((GeoPoint)geoOut).mirror(mirrorConic); // Michael Borcherds 2008-02-10
        else // invert circle in circle
        	((GeoConic)geoOut).mirror(mirrorConic); // Michael Borcherds 2010-01-21
    }       
    
    final public String toString() {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("AMirroredAtB",geoIn.getLabel(),mirror.getLabel());

    }
}

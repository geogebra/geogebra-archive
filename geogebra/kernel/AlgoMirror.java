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
import geogebra.kernel.implicit.GeoImplicitPoly;

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
    
    private GeoPoint transformedPoint;
    
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
        outGeo = getResultTemplate(inGeo);
        if(outGeo instanceof Mirrorable)
        	out = (Mirrorable)outGeo;
        setInputOutput();
              
        cons.registerEuclidianViewCE(this);
        transformedPoint = new GeoPoint(cons);
        compute();        
        if(inGeo.isGeoFunction())
        	cons.registerEuclidianViewCE(this);
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
    	else if(inGeo instanceof GeoPolyLineInterface && mirror == mirrorConic){
    		((GeoPolyLineInterface)inGeo).toGeoCurveCartesian((GeoCurveCartesian)outGeo);    		
    	}
    	else outGeo.set(inGeo);
    	
    	if(inGeo.isRegion() && mirror == mirrorConic){
			GeoVec2D v = mirrorConic.getTranslationVector();   
			outGeo.setInverseFill(((Region)inGeo).isInRegion(v.x,v.y) ^ inGeo.isInverseFill());
		}
    	
        
        if (mirror == mirrorLine)
        	out.mirror(mirrorLine);
        else if (mirror == mirrorPoint){
        	if(outGeo.isGeoFunction())
        		((GeoFunction)outGeo).dilate(new MyDouble(kernel,-1), mirrorPoint);
        	else
        		out.mirror(mirrorPoint);
        }
        else ((ConicMirrorable)out).mirror(mirrorConic);
        if(inGeo.isLimitedPath())
        	this.transformLimitedPath(inGeo, outGeo);
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
		if(!(outGeo instanceof GeoList))
			out = (Mirrorable)outGeo;
		
	}
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
    	if((geo instanceof GeoPolyLineInterface) && mirror == mirrorConic)
			return new GeoCurveCartesian(cons);
		if((geo instanceof GeoFunction) && mirror != mirrorPoint)
			return new GeoCurveCartesian(cons);
		if(geo.isLimitedPath() && mirror == mirrorConic)
			return new GeoConicPart(cons, GeoConicPart.CONIC_PART_ARC);
		if (mirror instanceof GeoConic && geo instanceof GeoLine){
        	return new GeoConic(cons);        	
        }
        if (mirror instanceof GeoConic && geo instanceof GeoConic && 
        		(!((GeoConic)geo).isCircle()||!((GeoConic)geo).keepsType()))
        	return new GeoImplicitPoly(cons);
		if(geo instanceof GeoPolyLineInterface  || (geo.isLimitedPath() && mirror!=mirrorConic))
			return geo.copyInternal(cons);		
		if(geo.isGeoList())        	
        	return new GeoList(cons);
		return geo.copy();
	}
    protected void transformLimitedPath(GeoElement a,GeoElement b){
    	if(mirror != mirrorConic){
    		super.transformLimitedPath(a, b);
    	}
    	GeoConicPart arc = (GeoConicPart)b;
    	arc.setParameters(0, 6.28, true);
		if(a instanceof GeoRay){			
			transformedPoint.removePath();
			setTransformedObject(
					((GeoRay)a).getStartPoint(),
					transformedPoint);
			compute();						
			arc.pathChanged(transformedPoint);
			double d = transformedPoint.getPathParameter().getT();
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			arc.pathChanged(transformedPoint);
			double e = transformedPoint.getPathParameter().getT();					
			arc.setParameters(d*Kernel.PI_2, e*Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(
					arc.getPointParam(0.5),
					transformedPoint);			
			compute();			
			if(!((GeoRay)a).isOnPath(transformedPoint, Kernel.EPSILON))
				arc.setParameters(d*Kernel.PI_2, e*Kernel.PI_2, false);
			
			setTransformedObject(a,b);
		}
		else if(a instanceof GeoSegment){
			arc.setParameters(0, Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(
					((GeoSegment)a).getStartPoint(),
					transformedPoint);
			compute();
			
			arc.pathChanged(transformedPoint);
			double d = transformedPoint.getPathParameter().getT();
			
			arc.setParameters(0, Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(
					((GeoSegment)a).getEndPoint(),
					transformedPoint);
			compute();
		
			arc.pathChanged(transformedPoint);
			double e = transformedPoint.getPathParameter().getT();			
			arc.setParameters(d*Kernel.PI_2, e*Kernel.PI_2, true);				
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			if(arc.isOnPath(transformedPoint, Kernel.EPSILON))
				arc.setParameters(d*Kernel.PI_2, e*Kernel.PI_2, false);
			setTransformedObject(a,b);
		}
		if(a instanceof GeoConicPart){			
			transformLimitedConic(a,b);
		}
	}
    protected boolean swapOrientation(boolean positiveOrientation) {		
		return positiveOrientation;
	}
}

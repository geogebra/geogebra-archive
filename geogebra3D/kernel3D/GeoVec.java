/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Traceable;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

/**
 *
 * @author  Markus + Mathieu
 * @version 
 */
public abstract class GeoVec extends GeoElement3D //TODO extends GeoElement
implements Traceable {
       
    public GgbVector v;
	public boolean trace;	 
    
	
    public GeoVec(Construction c) {super(c);}  

    public GeoVec(Construction c, int n) {
    	this(c);
    	v = new GgbVector(n);
    }  

    /** Creates new GeoVec with coordinates coords[] and label */
    public GeoVec(Construction c, double[] coords) {    	
    	super(c); 
    	
    	v = new GgbVector(coords);
    	v.set(Double.NaN);
    }                 
    
    /** Copy constructor */
    public GeoVec(Construction c, GeoVec vec) {   
    	super(c); 	
        set(vec);
    }
    
    /*
    public GeoElement copy() {
        return new GeoVec(this.cons, this);        
    }
    */
    
    public boolean isDefined() {
    	return v.isDefined();        
    }
    
    public void setUndefined() {  
    	if (v!=null)
    		v.set(Double.NaN);       
    }       
    
    protected boolean showInEuclidianView() {     
        return isDefined();
    }
    
    protected boolean showInAlgebraView() {
       // return true;
	   //return isDefined();
    	return true;
    }        
    
    public void set(GeoElement geo) {    
        GeoVec vec = (GeoVec) geo;        
        setCoords(vec.v);        
    }         
    
	public void setCoords(GgbVector v0){
		v.set(v0);
	}
	
	public void setCoords(double[] vals){
		v.set(vals);
	}
	
	//	TODO cast on add, mul methods in GgbVector
	public void setCoords(GgbMatrix v0){		
		v.set(v0);		
	}
	public void setCoords(GeoVec vec){
		v.set(vec.v);
	}
	
	
	public void translate(GgbVector v0){
		
		GgbVector v1 = v.add(v0).getColumn(1);
		setCoords(v1);
		
	}



	
	
	
    final public GgbVector getCoords() {
    	return v;
    }             


    
    
    
    /** Yields true if the coordinates of this vector are equal to
     * those of vector v. 
     */
    final public boolean equals(GeoVec vec) {
    
        kernel.setMinPrecision();
        
        boolean ret = true;
        
        for(int i=1;(i<=v.getLength())&&(ret);i++)
        	ret = ret && kernel.isEqual(v.get(i), vec.v.get(i));             
        kernel.resetPrecision();
        return ret;
    }
    
    
    
    
    
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
    

    
    final public boolean isZero() {
    	boolean ret = true;
        for(int i=1;(i<=v.getLength())&&(ret);i++)
        	ret = ret && kernel.isZero(v.get(i));             
        return ret;
    }
    

    
    
    
    
    
    public String toString() {
		sbToString.setLength(0);
        for(int i=1;i<=v.getLength();i++){
        	sbToString.append('(');
        	sbToString.append(v.get(i));
        	sbToString.append(", ");
        }
		sbToString.append(')');
        return sbToString.toString();
    }
    
    
    
    
    
    
    
	private StringBuffer sbToString = new StringBuffer(50);
	
    /**
     * returns all class-specific xml tags for saveXML
     */
    protected String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        //TODO sb.append(super.getXMLtags());
        
        
        sb.append("\t<coords");
        for(int i=1;i<=v.getLength();i++){
        	sb.append(" x"+i+"=\"" + v.get(i) + "\"");
        }
        sb.append("/>\n");
        
        return sb.toString();
    }
    
	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   
    
}

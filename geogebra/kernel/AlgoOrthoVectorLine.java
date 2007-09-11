/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoVectorLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoOrthoVectorLine extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g; // input
    private GeoVector  n;     // output       
    
    //private GeoPoint startPoint;
        
    /** Creates new AlgoOrthoVectorLine */
    AlgoOrthoVectorLine(Construction cons, String label, GeoLine g) {        
        super(cons);
        this.g = g;                
        n = new GeoVector(cons); 

        GeoPoint possStartPoint = g.getStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            n.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
        
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        n.z = 0.0d;
        compute();      
        n.setLabel(label);
    }   
    
    String getClassName() {
        return "AlgoOrthoVectorLine";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = g;
        
        output = new GeoElement[1];        
        output[0] = n;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoVector getVector() { return n; }    
    GeoLine getg() { return g; }
    
    // line through P normal to v
    final void compute() {        
        n.x = g.x;
        n.y = g.y;        
    }   
    

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
        sb.append(app.getPlain("OrthogonalVectorOf"));
        sb.append(' ');
         }
        sb.append(g.getLabel());
        if(app.isReverseLanguage()){//FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("OrthogonalVectorOf"));
         }
        return sb.toString();
    }
}

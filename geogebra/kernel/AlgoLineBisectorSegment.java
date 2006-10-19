/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;


public class AlgoLineBisectorSegment extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoSegment s;  // input
    private GeoPoint A, B;     
    private GeoLine  g;     // output   
    
    // temp
    private double ax, ay, bx, by;
    private GeoPoint midPoint;
        
    /** Creates new AlgoLineBisector */
    AlgoLineBisectorSegment(Construction cons, String label, GeoSegment s) {
        super(cons);
        this.s = s;
        A = s.getStartPoint();
        B = s.getEndPoint();        
        g = new GeoLine(cons); 
        midPoint = new GeoPoint(cons);
        g.setStartPoint(midPoint);
        setInputOutput(); // for AlgoElement
        
        // compute bisector of A, B
        compute();      
        g.setLabel(label);
    }   
    
    String getClassName() {
        return "AlgoLineBisectorSegment";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = s;        
        
        output = new GeoElement[1];        
        output[0] = g;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoLine getLine() { return g; }
    GeoSegment getSegment() {return s;  }
    GeoPoint getMidPoint() {
        return midPoint;
    }
    
    // line through P normal to v
    final void compute() { 
        // get inhomogenous coords
        ax = A.inhomX;
        ay = A.inhomY;
        bx = B.inhomX;
        by = B.inhomY;
         
        // comput line
        g.x = ax - bx;
        g.y = ay - by;
        midPoint.setCoords( (ax + bx), (ay + by), 2.0);   
        g.z = -(midPoint.x * g.x + midPoint.y * g.y)/2.0;     
    }   
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
        sb.append(app.getPlain("LineBisector"));
        sb.append(' ');
        sb.append(app.getPlain("of"));
        sb.append(' ');
        }
        sb.append(s.getLabel());
         if(app.isReverseLanguage()){//FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("LineBisector"));
        }
        return sb.toString();
    }
}

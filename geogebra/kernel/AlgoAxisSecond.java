/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAxes.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAxisSecond extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoLine axis;     // output          
        
    private GeoVec2D [] eigenvec;    
    private GeoVec2D b;
    private GeoPoint P;
    
    AlgoAxisSecond(Construction cons, String label,GeoConic c) {   
        super(cons);
        this.c = c;                               
        
        eigenvec = c.eigenvec;        
        b = c.b;                
        
        axis = new GeoLine(cons); 
        P = new GeoPoint(cons);
        axis.setStartPoint(P);
                       
        setInputOutput(); // for AlgoElement                
        compute();              
        axis.setLabel(label);            
    }   
    
    protected String getClassName() {
        return "AlgoAxisSecond";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = new GeoElement[1];
        output[0] = axis;
        setDependencies(); // done by AlgoElement
    }    
    
    GeoLine getAxis() { return axis; }    
    GeoConic getConic() { return c; }        
    
    // calc axes
    final void compute() {                        
        // axes are lines with directions of eigenvectors
        // through midpoint b        
        
        axis.x = -eigenvec[1].y;
        axis.y =  eigenvec[1].x;
        axis.z = -(axis.x * b.x + axis.y * b.y);       
        
        P.setCoords(b.x, b.y, 1.0);
    }
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("SecondAxisOfA",c.getLabel()));
        
        /*
        if(app.isReverseLanguage()){//FKH 20040906
          sb.append(c.getLabel());
          sb.append(' ');
          sb.append(app.getPlain("SecondAxisOf"));
        }else{
        sb.append(app.getPlain("SecondAxisOf"));        
        sb.append(' ');
        sb.append(c.getLabel());
        }*/
        
        return sb.toString();
    }
}

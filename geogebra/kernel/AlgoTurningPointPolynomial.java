/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Finds all inflection points of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTurningPointPolynomial extends AlgoRootsPolynomial {
        
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoTurningPointPolynomial(Construction cons, String [] labels, GeoFunction f) {
        super(cons, labels, f);             
    }
    
    protected String getClassName() {
        return "AlgoTurningPointPolynomial";    
    }
            
    public GeoPoint [] getInflectionPoints() {
        return super.getRootPoints();
    }
    
    protected final void compute() {              
        if (f.isDefined()) {
            yValFunction = f.getFunction();                                                                    
            
            // roots of second derivative 
            //(roots without change of sign are removed)
            calcRoots(yValFunction, 2);                                                          
        } else {
            curRealRoots = 0;                           
        }                       
                
        setRootPoints(curRoots, curRealRoots);                  
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("InflectionPointofA",f.getLabel()));
        
        /*
        if(!app.isReverseLanguage()){//FKH 20040906
        sb.append(app.getPlain("TurningPoint"));
        sb.append(' ');
        sb.append(app.getPlain("of"));
        sb.append(' ');
        }
        sb.append(f.getLabel());
        if(app.isReverseLanguage()){//FKH 20040906
        sb.append(' ');
        sb.append(app.getPlain("of"));
        sb.append(' ');
        sb.append(app.getPlain("TurningPoint"));
        }*/
        

        return sb.toString();
    }

}

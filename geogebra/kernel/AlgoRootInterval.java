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

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRoot;
import geogebra.kernel.roots.RealRootUtil;

/**
 * Finds one real root of a function in the given interval. 
 */
public class AlgoRootInterval extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input    
    private NumberValue a, b; // interval bounds
    private GeoPoint rootPoint; // output 

    private GeoElement aGeo, bGeo;
    private RealRoot rootFinder;

    public AlgoRootInterval(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue a,
        NumberValue b) {
        super(cons);
        this.f = f;
        this.a = a;
        this.b = b;
        aGeo = a.toGeoElement();
        bGeo = b.toGeoElement();

        rootFinder = new RealRoot();

        // output
        rootPoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement    
        compute();
        rootPoint.setLabel(label);
    }

    String getClassName() {
        return "AlgoRootInterval";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[3];
        input[0] = f;
        input[1] = aGeo;
        input[2] = bGeo;

        output = new GeoElement[1];
        output[0] = rootPoint;
        setDependencies();
    }

    public GeoPoint getRootPoint() {
        return rootPoint;
    }

    final void compute() {
        rootPoint.setCoords(calcRoot(), 0.0, 1.0);
    }

    final double calcRoot() {
        if (!(f.isDefined() && aGeo.isDefined() && bGeo.isDefined()))
			return Double.NaN;
        
        double root = Double.NaN;
        Function fun = f.getFunction();
        
        try {
        	// Brent's method
            root = rootFinder.brent(fun, a.getDouble(), b.getDouble());            
        } catch (IllegalArgumentException e) {        	
            try {                               	        	
            	// Brent's failed because we left our function's domain
            	// Let's search for a valid domain and try again
            	double [] borders = RealRootUtil.getDefinedInterval(fun, a.getDouble(), b.getDouble());            	
            	root = rootFinder.brent(fun, borders[0], borders[1]);                
            } catch (Exception ex) {            	            
            } 
        }
        
        if (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION)
            return root;
        else
        	return Double.NaN;
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Root"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
        }
        sb.append(f.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("onInterval"));
        sb.append(" [");
        sb.append(aGeo.getLabel());
        sb.append(", ");
        sb.append(bGeo.getLabel());
        sb.append("]");
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Root"));
        }
        return sb.toString();
    }
}

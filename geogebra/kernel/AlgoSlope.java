/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoSlope extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g; // input
    private GeoNumeric slope; // output       

    /** Creates new AlgoDirection */
    AlgoSlope(Construction cons, String label, GeoLine g) {
        super(cons);
        this.g = g;
        slope = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        compute();
        slope.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoSlope";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = g;

        output = new GeoElement[1];
        output[0] = slope;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getSlope() {
        return slope;
    }
    public GeoLine getg() {
        return g;
    }

    // direction vector of g
    protected final void compute() {
        if (g.isDefined())
            slope.setValue(-g.x / g.y);
        else
            slope.setUndefined();
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("SlopeOfA",g.getLabel()));
        
        /*
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("SlopeOf"));
            sb.append(' ');
        }
        sb.append(g.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("SlopeOf"));
        }*/
        

        return sb.toString();
    }
}

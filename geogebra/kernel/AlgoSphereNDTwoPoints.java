/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus + Mathieu
 * @version 
 */
public abstract class AlgoSphereNDTwoPoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointInterface M, P; // input    
    private GeoQuadricND sphereND; // output         

    public AlgoSphereNDTwoPoints(
        Construction cons,
        GeoPointInterface M,
        GeoPointInterface P) {
        super(cons);
        this.M = M;
        this.P = P;
        sphereND = createSphereND(cons);
        setInputOutput(); // for AlgoElement

        compute();
    }
    
    
    abstract protected GeoQuadricND createSphereND(Construction cons);
    
    protected AlgoSphereNDTwoPoints(
            Construction cons,
            String label,
            GeoPointInterface M,
            GeoPointInterface P) {
         this(cons, M, P);
         sphereND.setLabel(label);
    }

    public String getClassName() {
        return "AlgoCircleTwoPoints";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) M;
        input[1] = (GeoElement) P;

        output = new GeoElement[1];
        output[0] = sphereND;
        setDependencies(); // done by AlgoElement
    }

    public GeoQuadricND getSphereND() {
        return sphereND;
    }
    protected GeoPointInterface getM() {
        return M;
    }
    protected GeoPointInterface getP() {
        return P;
    }

    // compute circle with midpoint M and radius r
    protected final void compute() {
        sphereND.setSphereND(M, P);
    }


}

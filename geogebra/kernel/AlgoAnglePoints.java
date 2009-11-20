/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAnglePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAnglePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A, B, C; // input
    private GeoAngle angle; // output           

    private AlgoAnglePolygon algoAnglePoly;

    transient private double bx, by, vx, vy, wx, wy;

    AlgoAnglePoints(
        Construction cons,
        String label,
        GeoPoint A,
        GeoPoint B,
        GeoPoint C) {
        this(cons, A, B, C);
        angle.setLabel(label);
    }

    AlgoAnglePoints(
        Construction cons,
        AlgoAnglePolygon algoAnglePoly,
        GeoPoint A,
        GeoPoint B,
        GeoPoint C) {
        this(cons, A, B, C);
        this.algoAnglePoly = algoAnglePoly;
    }

    protected String getClassName() {
        return "AlgoAnglePoints";
    }

    private AlgoAnglePoints(
        Construction cons,
        GeoPoint A,
        GeoPoint B,
        GeoPoint C) {
        super(cons);
        this.A = A;
        this.B = B;
        this.C = C;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
    }

    void setAlgoAnglePolygon(AlgoAnglePolygon algo) {
        algoAnglePoly = algo;
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = C;

        output = new GeoElement[1];
        output[0] = angle;
        setDependencies(); // done by AlgoElement
    }

    public void remove() {
        if (algoAnglePoly != null)
            algoAnglePoly.remove();
        else
            super.remove();
    }

    public int getConstructionIndex() {
        if (algoAnglePoly != null)
			return algoAnglePoly.getConstructionIndex();
		else
			return super.getConstructionIndex();
    }

    GeoAngle getAngle() {
        return angle;
    }
    public GeoPoint getA() {
        return A;
    }
    public GeoPoint getB() {
        return B;
    }
    public GeoPoint getC() {
        return C;
    }

    // calc angle between vectors A-B and C-B    
    // angle in range [0, pi]
    protected final void compute() {
        if (!A.isFinite() || !B.isFinite() || !C.isFinite()) {
            angle.setUndefined(); // undefined
            return;
        }                
        
        // get vectors v=BA and w=BC                        
        bx = B.inhomX;
        by = B.inhomY;
        vx = A.inhomX - bx;
        vy = A.inhomY - by;
        wx = C.inhomX - bx;
        wy = C.inhomY - by;
                
        if (kernel.isZero(vx) && kernel.isZero(vy) ||
        		kernel.isZero(wx) && kernel.isZero(wy)) {
        	angle.setUndefined();
        	return;
        }
        
       	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = vx * wy - vy * wx;
    	double prod = vx * wx + vy * wy;    	    
    	double value = Math.atan2(det, prod);                  	    	       

        angle.setValue(value);
    }

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        if (algoAnglePoly != null) 
            sb.append(app.getPlain("AngleBetweenABCofD",A.getLabel(),B.getLabel(),C.getLabel(),algoAnglePoly.getPolygon().getNameDescription()));
        else
           	sb.append(app.getPlain("AngleBetweenABC",A.getLabel(),B.getLabel(),C.getLabel()));

        
       
        return sb.toString();
    }
}

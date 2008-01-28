/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



public class AlgoAngleVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoVec3D vec; // input
    private GeoAngle angle; // output          
    
    private double [] coords = new double[2];

    AlgoAngleVector(Construction cons, String label, GeoVec3D vec) {
        super(cons);
        this.vec = vec;
        
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        angle.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoAngleVector";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = vec;

        output = new GeoElement[1];
        output[0] = angle;
        setDependencies(); // done by AlgoElement
    }

    GeoAngle getAngle() {
        return angle;
    }
    
    public GeoVec3D getVec3D() {
    	return vec;
    }
        
    final void compute() {  
    	vec.getInhomCoords(coords);
        angle.setValue(
        		Math.atan2(coords[1], coords[0])
			);
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        if (app.isReverseLanguage()) {
            sb.append(vec.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Angle"));
        } else {
            sb.append(app.getPlain("Angle"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(vec.getLabel());
        }
        return sb.toString();
    }
}

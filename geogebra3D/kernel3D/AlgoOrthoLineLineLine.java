/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Compute a line orthogonal to two lines
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLineLineLine extends AlgoElement3D {

 
	private GeoLineND line1; // input
    private GeoLineND line2; // input
    private GeoLine3D line; // output       


    public AlgoOrthoLineLineLine(Construction cons, String label, GeoLineND line1, GeoLineND line2) {
        super(cons);
        this.line1 = line1;
        this.line2 = line2;
        line = new GeoLine3D(cons);
        
        setInputOutput(new GeoElement[] {(GeoElement) line1, (GeoElement) line2}, new GeoElement[] {line});

        // compute line 
        compute();
        line.setLabel(label);
    }


    public GeoLine3D getLine() {
        return line;
    }


	protected void compute() {
		
		Coords o1 = line1.getPointInD(3, 0);
    	Coords v1 = line1.getPointInD(3, 1).sub(o1);
    	Coords o2 = line2.getPointInD(3, 0);
    	Coords v2 = line2.getPointInD(3, 1).sub(o2);
		Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(o1, v1, o2, v2);
		
		getLine().setCoord(points[0], v1.crossProduct(v2));
		
	}

	public String getClassName() {
        return "AlgoOrthoLineLineLine";
    }
    
    
    


}

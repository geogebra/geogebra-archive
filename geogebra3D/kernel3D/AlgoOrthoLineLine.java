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
public abstract class AlgoOrthoLineLine extends AlgoElement3D {

 
	protected GeoLineND line1; // input
    private GeoLine3D line; // output       


    public AlgoOrthoLineLine(Construction cons, String label, GeoLineND line1) {
        super(cons);
        this.line1 = line1;
        line = new GeoLine3D(cons);
    }
    
    public GeoLine3D getLine() {
        return line;
    }

    
    protected Coords origin, direction2, origin1, direction1;

	protected void compute() {
		
		origin1 = line1.getPointInD(3, 0);
    	direction1 = line1.getPointInD(3, 1).sub(origin1);
    	setOriginAndDirection2();		
		getLine().setCoord(origin, direction1.crossProduct(direction2));
		
	}
	
    protected abstract void setOriginAndDirection2();
    


}

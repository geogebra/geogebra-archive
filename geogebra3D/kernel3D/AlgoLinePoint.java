/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Compute a line through a point and ...
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoLinePoint extends AlgoElement3D {

 
	private GeoPointND point; // input
    private GeoElement inputParallel; // input
    private GeoLine3D line; // output       


    public AlgoLinePoint(Construction cons, String label, GeoPointND point, GeoElement inputParallel) {
        super(cons);
        this.point = point;
        this.inputParallel = inputParallel;
        line = new GeoLine3D(cons);
        
        setInputOutput(new GeoElement[] {(GeoElement) point, inputParallel}, new GeoElement[] {line});

        // compute line 
        compute();
        line.setLabel(label);
    }


    public GeoLine3D getLine() {
        return line;
    }
    
    protected GeoPointND getPoint(){
    	return point;
    }

    protected GeoElement getInputParallel(){
    	return inputParallel;
    }
    
    protected final void compute() {
    	
    	GgbVector v = getDirection();
    	
    	if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION))
    		getLine().setUndefined();
    	else
    		getLine().setCoord(getPoint().getCoordsInD(3), v.normalize());
    }


    abstract protected GgbVector getDirection();
    
    final public String toString() {
    	return app.getPlain("LineThroughAParallelToB",point.getLabel(),inputParallel.getLabel());
    }
}

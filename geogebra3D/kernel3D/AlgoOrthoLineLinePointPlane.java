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
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;


/**
 * Compute a line orthogonal to a line, through a point, and orthogonal to a plane
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLineLinePointPlane extends AlgoOrthoLineLine {


	private GeoPointND point; // input     
	private GeoDirectionND direction; // input     


    public AlgoOrthoLineLinePointPlane(Construction cons, String label, GeoPointND point, GeoLineND line1, GeoDirectionND direction) {
        super(cons,label,line1);
        this.point = point;
        this.direction = direction;
               
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) line1, (GeoElement) direction}, new GeoElement[] {getLine()});

        // compute line 
        compute();
        getLine().setLabel(label);
    }





	public String getClassName() {
        return "AlgoOrthoLineLinePointPlane";
    }




	@Override
	protected void setOriginAndDirection2() {
    	direction2 = direction.getDirectionInD3();
		origin = point.getInhomCoordsInD(3);
	
	}
    
    
    


}

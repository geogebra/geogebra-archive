/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.main.Application;


/**
 * Compute the ends of a limited quadric
 *
 * @author  matthieu
 * @version 
 */
public class AlgoQuadricEnds extends AlgoElement3D {

 
    private GeoQuadricND quadric; // input
	private GeoPointND point; // input
	private GeoPointND pointThrough; // input
    private GeoConic3D section1, section2; // output       
    private CoordSys coordsys1, coordsys2;

    public AlgoQuadricEnds(Construction cons, GeoQuadricND quadric, GeoPointND point, GeoPointND pointThrough) {
        super(cons);
        
        this.quadric = quadric;
        this.point = point;
        this.pointThrough = pointThrough;
        section1 = new GeoConic3D(cons);
        coordsys1 = new CoordSys(2);
		section1.setCoordSys(coordsys1);
        section2 = new GeoConic3D(cons);
        coordsys2 = new CoordSys(2);
		section2.setCoordSys(coordsys2);
		
		//setUpdateAfterAlgo(quadric.getParentAlgorithm());
		
		setInputOutput(new GeoElement[] {(GeoElement) quadric, (GeoElement) point, (GeoElement) pointThrough}, new GeoElement[] {(GeoElement) point, (GeoElement) pointThrough}, new GeoElement[] {section1, section2});

		

    }


    public GeoConic3D getSection1() {
        return section1;
    }
  
    public GeoConic3D getSection2() {
        return section2;
    }


    
    protected final void compute() {
    	
    	
    	if (!point.isDefined() || point.isInfinite() || !pointThrough.isDefined() || pointThrough.isInfinite() || !quadric.isDefined()){
    		section1.setUndefined();
    		section2.setUndefined();
    		return;
    	}

    	section1.setDefined();
    	section2.setDefined();
    	
    	CoordMatrix qm = quadric.getSymetricMatrix();
    	CoordMatrix pm = new CoordMatrix(4,3);
    	Coords o1 = point.getInhomCoordsInD(3);
    	Coords o2 = pointThrough.getInhomCoordsInD(3);
    	pm.setOrigin(o1);
    	Coords[] v = o2.sub(o1).completeOrthonormal();  	
    	pm.setVx(v[0]);
    	pm.setVy(v[1]);
    	CoordMatrix pmt = pm.transposeCopy();
     	
    	//sets the conic matrix from plane and quadric matrix
    	CoordMatrix cm = pmt.mul(qm).mul(pm);
    	
    	coordsys1.resetCoordSys();
      	coordsys1.addPoint(o1);
       	coordsys1.addVector(v[0]);
       	coordsys1.addVector(v[1]);
       	coordsys1.makeOrthoMatrix(false, false);
        	
    	section1.setMatrix(cm);
    	
    	//section2
    	pm.setOrigin(o2);
    	pmt = pm.transposeCopy();
    	
    	cm = pmt.mul(qm).mul(pm);
    	
    	coordsys2.resetCoordSys();
      	coordsys2.addPoint(o2);
       	coordsys2.addVector(v[0]);
       	coordsys2.addVector(v[1]);
       	coordsys2.makeOrthoMatrix(false, false);
        	
    	section2.setMatrix(cm);
    	

    }

    public String getClassName() {
        return "AlgoQuadricEnds";
    }
    
    /*
    final public String toString() {
    	return app.getPlain("EndsOfABetweenBC",((GeoElement) quadric).getLabel(),point.getLabel(),pointThrough.getLabel());
    }
    */
}

/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;




import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbVector;


/**
 *
 * @author  ggb3D
 * @version 
 */
public class Kernel3D
	extends Kernel{
	
	
	public void setConstruction(Construction cons){
		
		this.cons = cons;
	}

	
	/***********************************
	 * FACTORY METHODS FOR GeoElements
	 ***********************************/

	/** Point3D label with cartesian coordinates (x,y)   */
	final public GeoPoint3D Point3D(String label, double x, double y, double z) {
		GeoPoint3D p = new GeoPoint3D(cons);
		p.setCoords(x, y, z, 1.0);
		//p.setObjColor(new Color((float) x,(float) y,(float) z)); //TODO remove this
		p.setLabel(label); // invokes add()                
		return p;
	}
	
	/** Segment3D label linking points v1 and v2   */
	final public GeoSegment3D Segment3D(String label, GgbVector v1, GgbVector v2){
		GeoSegment3D s=new GeoSegment3D(cons,v1,v2);
		s.setLabel(label);
		return s;
	}
	
	/** Segment3D label linking points P1 and P2   */
	final public GeoSegment3D Segment3D(String label, GeoPoint3D P1, GeoPoint3D P2){
		AlgoJoinPoints3DSegment algo = new AlgoJoinPoints3DSegment(cons, label, P1, P2);
		GeoSegment3D s = algo.getSegment();
		/*
		GeoSegment3D s=new GeoSegment3D(cons,P1,P2);
		s.setLabel(label);
		*/
		return s;
	}	
	
	
	
	/** Plane3D label linking with (o,v1,v2) coord sys   */
	final public GeoPlane3D Plane3D(String label, GgbVector o, GgbVector v1, GgbVector v2){
		//GeoPlane3D p=new GeoPlane3D(cons,o,v1,v2,-1.5,1.5,-1.5,1.5);
		GeoPlane3D p=new GeoPlane3D(cons,o,v1,v2,-2.25,2.25,-2.25,2.25);
		p.setLabel(label);
		return p;
	}	
	
}
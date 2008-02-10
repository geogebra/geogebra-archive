/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra.kernel3D;


//import geogebra.Application;
//import geogebra.kernel.Construction;
import java.awt.Color;

import geogebra.kernel.Kernel;



//TODO in Kernel, change private to protected

/**
 *
 * @author  Mathieu
 * @version 
 */
public class Kernel3D
	extends Kernel{

	//Construction cons;
	/********************************************************/

	/** Creates new GeoElement for given construction */
	/*
	public Kernel3D(Application app) {
		super(app);		
	}*/
	
	/*
	public Kernel3D() {
		super();
	}
	*/
	
	/***********************************
	 * FACTORY METHODS FOR GeoElements
	 ***********************************/

	/** Point3D label with cartesian coordinates (x,y)   */
	final public GeoPoint3D Point3D(String label, double x, double y, double z) {
		GeoPoint3D p = new GeoPoint3D(cons);
		p.setCoords(x, y, z, 1.0);
		//p.setMode(COORD_CARTESIAN);
		p.setLabel(label); // invokes add()                
		return p;
	}
	
	
	
	/***********************************
	 * TEST
	 ***********************************/
	
	private void testPoint(int i, int j, int k, float x, float y, float z){
		String s="";
		GeoPoint3D P=null;
		s="M3d"+i+""+j+""+k;
		P=Point3D(s,x,y,z);
		P.setObjColor(new Color(x,y,z));
		cons.addToConstructionList(P,false);			
	}
	
	public void test(Kernel kernel){
		cons=kernel.getConstruction();
		geogebra.kernel.GeoPoint p2d = Point("M2d",2,1);
		cons.addToConstructionList(p2d,false);
		geogebra.kernel.GeoText t = Text("T","Move Mode has changed");
		try{
			t.setStartPoint(p2d);
		}catch(geogebra.kernel.CircularDefinitionException cde){}
		cons.addToConstructionList(t,false);
		
		int i,j,k;
		int n=4;
		for(i=1;i<n;i++){
			j=0;k=n;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			j=n;k=n;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			j=0;k=0;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			j=n;k=0;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
		}
		for(j=1;j<n;j++){
			i=0;k=n;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			i=n;k=n;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			i=0;k=0;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			i=n;k=0;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
		}
		for(k=n;k>=0;k--){
			i=0;j=0;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			i=n;j=0;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			i=0;j=n;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
			i=n;j=n;
			testPoint(i,j,k,(float)i/n,(float)j/n,(float)k/n);			
		}
	}
	
	
	

}
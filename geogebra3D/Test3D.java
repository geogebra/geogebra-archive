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

package geogebra3D;


//import geogebra.Application;
//import geogebra.kernel.Construction;
import java.awt.Color;

import geogebra.kernel.*;
import geogebra3D.kernel3D.*;
import geogebra3D.kernel3D.Linalg.GgbVector;






/**
 *
 * @author  ggb3D
 * @version 
 */
public class Test3D{
	
	Kernel kernel;
	Construction cons;
	Kernel3D kernel3D;


	public Test3D(Kernel kernel){
		
		this.kernel=kernel;
		cons=kernel.getConstruction();
		kernel3D=new Kernel3D();
		kernel3D.setConstruction(cons);
		
	}

	
	/***********************************
	 * TESTS
	 ***********************************/
	
	private void testPoint(int i, int j, int k, float x, float y, float z){
		String s="";
		GeoPoint3D P=null;
		s="M3d"+i+""+j+""+k;
		P=kernel3D.Point3D(s,x,y,z);
		P.setObjColor(new Color(x,y,z));
		P.setLabelVisible(false);
		cons.addToConstructionList(P,false);			
	}
	
	public void testRepere(){
		
		testPoint(0,0,0,0,0,0);
		testPoint(1,0,0,1,0,0);testSegment(0,0,0,1,0,0);
		testPoint(0,1,0,0,1,0);testSegment(0,0,0,0,1,0);
		testPoint(0,0,1,0,0,1);testSegment(0,0,0,0,0,1);
		
	}
	
	public void testPointSegment(){
		
		testPoint(1,0,0,1,0,0);
		//testPoint(0,1,0,0,1,0);
		testSegment(0,0,0,0,1,0);
		
		
	}
	
	public void testSegmentSegment(){
		
		testSegment(0,0,0,1,1,0);
		testSegment(1,0,1,0,1,1);
		
		
	}
	
	
	public void testSegment(){
		GeoSegment3D s=null;
		s=kernel3D.Segment3D("segment",new GgbVector(new double[] {0,0,0,1}),new GgbVector(new double[]{1,1,1,1}));
		cons.addToConstructionList(s, false);
		//testPoint(1,1,1,1,1,1);
	}
	
	public void testSegment(double x1, double y1, double z1, double x2, double y2, double z2){

		GeoSegment3D s=null;
		
		s=kernel3D.Segment3D("segment",
				new GgbVector(new double[] {x1,y1,z1,1}),
				new GgbVector(new double[] {x2,y2,z2,1}));
		s.setObjColor(new Color((float) (x1+x2)/2, (float) (y1+y2)/2, (float) (z1+z2)/2));
		cons.addToConstructionList(s, false);

	}
	
	public void testSegment2(){
		
		testSegment(0,0,0, 1,0,0);
		testSegment(0,1,0, 1,1,0);
		testSegment(0,0,1, 1,0,1);
		testSegment(0,1,1, 1,1,1);
		
		testSegment(0,0,0, 0,1,0);
		testSegment(1,0,0, 1,1,0);
		testSegment(0,0,1, 0,1,1);
		testSegment(1,0,1, 1,1,1);
		
		testSegment(0,0,0, 0,0,1);
		testSegment(1,0,0, 1,0,1);
		testSegment(0,1,0, 0,1,1);
		testSegment(1,1,0, 1,1,1);
		
	}	
	
	/** number of points = n+1 */
	public void test1(int n){
		/*
		geogebra.kernel.GeoPoint p2d = kernel.Point("M2d",-3,4);
		cons.addToConstructionList(p2d,false);
		geogebra.kernel.GeoText t = kernel.Text("T","\nUse shift + left clic\n to turn the cube --->>");
		try{
			t.setStartPoint(p2d);
		}catch(geogebra.kernel.CircularDefinitionException cde){}
		cons.addToConstructionList(t,false);
		*/
		
		int i,j,k;
		//int n=4;
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
	
	
	
	
	public void testPlane(double x0, double y0, double z0, 
			double x1, double y1, double z1, 
			double x2, double y2, double z2){
		
		GeoPlane3D p=null;
		
		p=kernel3D.Plane3D("plane",
				new GgbVector(new double[] {x0,y0,z0,1}),
				new GgbVector(new double[] {x1,y1,z1,0}),
				new GgbVector(new double[] {x2,y2,z2,0}));
		p.setObjColor(new Color((float) (x0+(x1+x2)/2), (float) (y0+(y1+y2)/2), (float) (z0+(z1+z2)/2)));
		cons.addToConstructionList(p, false);
		
	}
	
	
	public void testPlane(){
		testPlane(0, 0, 0,  1, 0, 0,  0, 1, 0);
		testPlane(0, 0, 0,  1, 0, 0,  0, 0, 1);
		testPlane(0, 0, 0,  0, 1, 0,  0, 0, 1);
		
		testPlane(0, 0, 1,  1, 0, 0,  0, 1, 0);
		testPlane(0, 1, 0,  1, 0, 0,  0, 0, 1);
		testPlane(1, 0, 0,  0, 1, 0,  0, 0, 1);
	}
	

	
	

}
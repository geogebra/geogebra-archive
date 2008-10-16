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
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.*;






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
		
		
		//let's test here
		
        //testSegment2();  
        //test1(1);
        //testSegment(0,0,0,1,1,1);test.testSegment(0.3333,-0.25,-0.25,0.3333,1.25,1.25);test.testSegment(0.6667,-0.25,-0.25,0.6667,1.25,1.25);
        //testPlane();
        //testPlane(0, 0, 0,  1, 0, 0,  0, 1, 0);
        testAlgoPyramide();
        testRepere();

		
	}

	
	/***********************************
	 * TESTS
	 ***********************************/
	
	private GeoPoint3D testPoint(int i, int j, int k, float x, float y, float z){
		String s="";
		GeoPoint3D P=null;
		s="M3d"+i+""+j+""+k;
		P=kernel3D.Point3D(s,x,y,z);
		P.setObjColor(new Color(x,y,z));
		P.setLabelVisible(false);
		//cons.addToConstructionList(P,false);	
		return P;
	}
	
	private GeoPoint3D testPoint(float x, float y, float z){
		String s="";
		GeoPoint3D P=null;
		s="M3d";
		P=kernel3D.Point3D(s,x,y,z);
		P.setObjColor(new Color(0f,0f,1f));
		P.setLabelVisible(false);		
		return P;
	}	
	
	public void testRepere(){
		
		
		double longueur = 2.25;
		
		GeoSegment3D s=null;
		
		s=kernel3D.Segment3D("axeX3D",new GgbVector(new double[] {0,0,0,1}),new GgbVector(new double[]{longueur,0,0,1}));
		s.setObjColor(new Color(1f,0f,0f));
		s.setLineThickness(1);
		cons.addToConstructionList(s, false);
		
		s=kernel3D.Segment3D("axeY3D",new GgbVector(new double[] {0,0,0,1}),new GgbVector(new double[]{0,longueur,0,1}));
		s.setObjColor(new Color(0f,1f,0f));
		s.setLineThickness(1);
		cons.addToConstructionList(s, false);
		
		s=kernel3D.Segment3D("axeZ3D",new GgbVector(new double[] {0,0,0,1}),new GgbVector(new double[]{0,0,longueur,1}));
		s.setObjColor(new Color(0f,0f,1f));
		s.setLineThickness(1);
		cons.addToConstructionList(s, false);
		
		
		//xOy plane
		GeoPlane3D plane=kernel3D.Plane3D("xOy",
				new GgbVector(new double[] {0.0,0.0,0.0,1.0}),
				new GgbVector(new double[] {1.0,0.0,0.0,0.0}),
				new GgbVector(new double[] {0.0,1.0,0.0,0.0}));
		plane.setObjColor(new Color(0.5f,0.5f,1f));
		plane.setAlgebraVisible(false); //TODO make it works
		plane.setLabelVisible(false);
		cons.addToConstructionList(plane, false);
		
		
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
	
	
	public void testAlgoPyramide(){
		
		int i;
		
		GeoPoint3D[] P1 = new GeoPoint3D[3];				
		P1[0] = testPoint(0f,0f,0f);
		P1[1] = testPoint(1f,0f,0f);
		P1[2] = testPoint(0f,1f,0f);
		//P1[3] = testPoint(0f,1f,0f);
		
		for(i=0;i<3;i++)
			kernel3D.Segment3D("segment",P1[i],P1[(i+1)%3]);

		GeoPoint3D P2;				
		P2 = testPoint(0f,0f,1f);

		for(i=0;i<3;i++)
			kernel3D.Segment3D("segment",P1[i],P2);
		
		GeoTriangle3D t;
		Color c = new Color(0.5f,0.2f,0.1f);
		t=kernel3D.Triangle3D("triangle",P2,P1[1],P1[2]);
		t.setObjColor(c);
		
		t=kernel3D.Triangle3D("triangle",P2,P1[2],P1[0]);
		t.setObjColor(c);
		t=kernel3D.Triangle3D("triangle",P2,P1[1],P1[0]);
		t.setObjColor(c);
		t=kernel3D.Triangle3D("triangle",P1[1],P1[2],P1[0]);
		t.setObjColor(c);
		
		
		GeoLine3D l=kernel3D.Line3D("line",P1[1],P1[2]);
		l.setObjColor(new Color(1f,0.5f,0f));
		l.setLineThickness(1);
		
		
	}
	

	public void testAlgo(){
		
		int i;
		
		GeoPoint3D[] P1 = new GeoPoint3D[4];				
		P1[0] = testPoint(0f,0f,0f);
		P1[1] = testPoint(1f,0f,0f);
		P1[2] = testPoint(1f,1f,0f);
		P1[3] = testPoint(0f,1f,0f);
		
		for(i=0;i<4;i++)
			kernel3D.Segment3D("segment",P1[i],P1[(i+1)%4]);

		GeoPoint3D[] P2 = new GeoPoint3D[4];				
		P2[0] = testPoint(0f,0f,1f);
		P2[1] = testPoint(1f,0f,1f);
		P2[2] = testPoint(1f,1f,1f);
		P2[3] = testPoint(0f,1f,1f);
		
		for(i=0;i<4;i++)
			kernel3D.Segment3D("segment",P2[i],P2[(i+1)%4]);

		for(i=0;i<4;i++)
			kernel3D.Segment3D("segment",P1[i],P2[i]);
		
		
	}
	
	
	

}
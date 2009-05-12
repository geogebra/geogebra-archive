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

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.*;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.*;






/**
 *
 * @author  ggb3D
 * @version 
 */
public class Test3D{
	
	Construction cons;
	Kernel3D kernel3D;
	EuclidianView view2D;
	
	
	GeoPlane3D xOyPlane;


	public Test3D(Kernel3D kernel3D, EuclidianView view2D, EuclidianView3D view3D){
		
		this.kernel3D=kernel3D;
		cons=kernel3D.getConstruction();
		this.view2D = view2D;

		//view2D.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		view2D.showAxes(true, true);
		view2D.setCoordSystem(100,view2D.getYZero(),view2D.getXscale(),view2D.getYscale());
		
		
        //init 3D view
        view3D.setZZero(-7.0);
        view3D.setRotXY(-Math.PI/6,Math.PI/6,true);
        view3D.setRotXY(-Math.PI/6,Math.PI/8,true);
        //view3D.setRotXY(-Math.PI/6,Math.PI/12,true);
		
		
		
		
		//testRegion();
		
		
		testRepere();
		
		//testConic3D();
		//testPolygon();
		//testPlane();


		//testQuadric();
    	
		//testRay3D();
		//testVector3D();
		//testAlgoPyramide();
		
		//testPolyhedron();
		testTetrahedron();
		
		//testIntersectLinePlane();
		//testIntersectLineLine();
		//testIntersectParallelLines();
	}

	

	/***********************************
	 * TESTS
	 ***********************************/
	
	private GeoPoint3D testPoint(int i, int j, int k, float x, float y, float z){
		//String s="";
		GeoPoint3D P=null;
		//s="M3d"+i+""+j+""+k;
		P=kernel3D.Point3D("A",x,y,z);
		P.setObjColor(new Color(x,y,z));
		//P.setLabelVisible(false);
		//cons.addToConstructionList(P,false);	
		return P;
	}
	
	private GeoPoint3D testPoint(float x, float y, float z){
 	
		return kernel3D.Point3D(null,x,y,z);
	}	
	
	private void testRepere(){
		
		
		double longueur = 2.25;
		
		GeoSegment3D s=null;
		
		s=kernel3D.Segment3D("axeX3D",new Ggb3DVector(new double[] {-longueur,0,0,1}),new Ggb3DVector(new double[]{longueur,0,0,1}));
		s.setObjColor(new Color(1f,0f,0f));
		s.setLineThickness(1);
		cons.addToConstructionList(s, false);
		
		s=kernel3D.Segment3D("axeY3D",new Ggb3DVector(new double[] {0,-longueur,0,1}),new Ggb3DVector(new double[]{0,longueur,0,1}));
		s.setObjColor(new Color(0f,1f,0f));
		s.setLineThickness(1);
		cons.addToConstructionList(s, false);
		
		s=kernel3D.Segment3D("axeZ3D",new Ggb3DVector(new double[] {0,0,0,1}),new Ggb3DVector(new double[]{0,0,longueur,1}));
		s.setObjColor(new Color(0f,0f,1f));
		s.setLineThickness(1);
		cons.addToConstructionList(s, false);
		
		
		//xOy plane
		xOyPlane=kernel3D.Plane3D("xOy",
				new Ggb3DVector(new double[] {0.0,0.0,0.0,1.0}),
				new Ggb3DVector(new double[] {1.0,0.0,0.0,0.0}),
				new Ggb3DVector(new double[] {0.0,1.0,0.0,0.0}));
		xOyPlane.setObjColor(new Color(0.75f,0.75f,0.75f));
		xOyPlane.setAlgebraVisible(false); //TODO make it works
		xOyPlane.setLabelVisible(false);
		cons.addToConstructionList(xOyPlane, false);
		
		
	}
	
	private void testPointSegment(){
		
		testPoint(1,0,0,1,0,0);
		//testPoint(0,1,0,0,1,0);
		testSegment(0,0,0,0,1,0);
		
		
	}
	
	private void testSegmentSegment(){
		
		testSegment(0,0,0,1,1,0);
		testSegment(1,0,1,0,1,1);
		
		
	}
	
	
	private void testSegment(){
		GeoSegment3D s=null;
		s=kernel3D.Segment3D("segment",new Ggb3DVector(new double[] {0,0,0,1}),new Ggb3DVector(new double[]{1,1,1,1}));
		cons.addToConstructionList(s, false);
		//testPoint(1,1,1,1,1,1);
	}
	
	private void testSegment(double x1, double y1, double z1, double x2, double y2, double z2){

		GeoSegment3D s=null;
		
		s=kernel3D.Segment3D("segment",
				new Ggb3DVector(new double[] {x1,y1,z1,1}),
				new Ggb3DVector(new double[] {x2,y2,z2,1}));
		s.setObjColor(new Color((float) (x1+x2)/2, (float) (y1+y2)/2, (float) (z1+z2)/2));
		cons.addToConstructionList(s, false);

	}
	
	private void testSegment2(){
		
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
	private void test1(int n){
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
	
	
	
	
	private void testPlane(double x0, double y0, double z0, 
			double x1, double y1, double z1, 
			double x2, double y2, double z2){
		
		GeoPlane3D p=null;
		
		p=kernel3D.Plane3D("plane",
				new Ggb3DVector(new double[] {x0,y0,z0,1}),
				new Ggb3DVector(new double[] {x1,y1,z1,0}),
				new Ggb3DVector(new double[] {x2,y2,z2,0}));
		p.setObjColor(new Color((float) (x0+(x1+x2)/2), (float) (y0+(y1+y2)/2), (float) (z0+(z1+z2)/2)));
		cons.addToConstructionList(p, false);
		
	}
	
	
	private void testPlane(){
		testPlane(0, 0, 0,  1, 0, 0,  0, 1, 0);
		testPlane(0, 0, 0,  1, 0, 0,  0, 0, 1);
		testPlane(0, 0, 0,  0, 1, 0,  0, 0, 1);
		
		testPlane(0, 0, 1,  1, 0, 0,  0, 1, 0);
		testPlane(0, 1, 0,  1, 0, 0,  0, 0, 1);
		testPlane(1, 0, 0,  0, 1, 0,  0, 0, 1);
	}
	
	
	private void testAlgoPyramide(){
		
		int i;
		
		GeoPoint3D[] P1 = new GeoPoint3D[3];				
		P1[0] = testPoint(1f,0.6f,0f);
		//P1[0].setLabel("Ax");
		P1[1] = testPoint(0f,0f,0f);
		//P1[1].setLabel("A");
		P1[2] = testPoint(0.3f,1f,0.5f);
		//P1[2].setLabel("Ay");
		//P1[3] = testPoint(0f,1f,0f);
		
		/*
		GeoSegment3D s=null;
		for(i=0;i<3;i++)
			s=kernel3D.Segment3D("segment",P1[i],P1[(i+1)%3]);
			*/

		GeoPoint3D P2;				
		P2 = testPoint(0.1f,0.1f,1f);
		//P2.setLabel("Az");

		//RG
		/*
		GeoPlane3D aPlane = xOyPlane;
		for(i=0;i<3;i++)
			kernel3D.From3Dto2D(P1[i].getLabel(), P1[i], aPlane);
		kernel3D.From3Dto2D(P2.getLabel(), P2, aPlane);
		*/
		//finRG

		/*
		for(i=0;i<3;i++)
			kernel3D.Segment3D("segment",P1[i],P2);
			*/
		
		GeoPolygon3D t;
		Color c = new Color(0.5f,0.2f,0.1f);
		t=kernel3D.Polygon3D("triangle",new GeoPoint3D[] {P2,P1[1],P1[2]});
		t.setObjColor(c);
		
		t=kernel3D.Polygon3D("triangle",new GeoPoint3D[] {P2,P1[1],P1[0]});
		t.setObjColor(c);
		t=kernel3D.Polygon3D("triangle",new GeoPoint3D[] {P1[1],P1[2],P1[0]});
		t.setObjColor(c);
		
		t=kernel3D.Polygon3D("triangle",new GeoPoint3D[] {P2,P1[2],P1[0]});
		t.setObjColor(c);
		

		GeoPoint3D A = testPoint(2f,0f,0f);
		A.setLabel("sA");
		GeoPoint3D B = testPoint(0.7f,-1.3f,0f);
		B.setLabel("sB");

		GeoSegment3D s = kernel3D.Segment3D("segment",A,B);
		s.setLabel("s");
		
		GeoPoint3D P=kernel3D.Point3D("ps", s, 0.3, 0, 0);
		P.setObjColor(new Color(1f,1f,0f));
		
		
		GeoLine3D l=kernel3D.Line3D("line",P1[1],P);
		l.setObjColor(new Color(1f,0.5f,0f));
		l.setLineThickness(1);
		
		P=kernel3D.Point3D("pl", l, 2.7, -2, 0);
		P.setObjColor(new Color(1f,0.75f,0f));
		
		
		//P=kernel3D.Point3D("pt", t, 0, 0, 0);
		//P.setObjColor(new Color(1f,0.25f,0f));
		
		//new AlgoTo2D(cons, "essai", s);
		
	}
	

	private void testAlgo(){
		
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
	
	
	
	
	private void testPath(){
		

		view2D.setAxesColor(new Color(0.5f,0.5f,0.5f));
		

		
		GeoPoint3D P1;				
		P1 = testPoint(0.5f,1f,0f);
		P1.setLabel("P1");
		
		GeoPoint3D P2;				
		P2 = testPoint(2f,0.5f,0f);
		P2.setLabel("P2");
		
		GeoSegment3D s = kernel3D.Segment3D("segment3D",P1,P2);
		GeoPoint3D P=kernel3D.Point3D("A3D", s, 1, 0, 0);
		P.setObjColor(new Color(1f,0.5f,0f));
	}
	
	
	private void testPolygon(){

		//GeoPoint3D P = testPoint(-0.5f,-0.5f,0f);P.setLabel("P");		
		//kernel3D.From3Dto2D("truc", P, xOyPlane);
		
		
		GeoPoint3D[] points = new GeoPoint3D[4];
		points[0] = testPoint(1f,-2f,0f);
		points[1] = testPoint(1f,0f,0f);
		points[2] = testPoint(-1f,0f,0f);
		points[3] = testPoint(-1f,-2f,0f);
		
		/*
		points[2] = testPoint(-1.5f,0.5f,0f);
		points[3] = testPoint(-1.5f,0f,0f);
		*/
		
		GeoPolygon3D p=kernel3D.Polygon3D("poly", points);
		p.setObjColor(new Color(0f,0f,1f));
		//p.setAlphaValue(0.5f);
	}

	private void testRay3D(){

		GeoPoint3D P1 = testPoint(1f,1f,1f);
		P1.setLabel("rayP1");
		GeoPoint3D P2 = testPoint(2f,2f,2f);
		P2.setLabel("rayP2");
		
		kernel3D.Ray3D("MonRay", P1, P2);
	}

	

	private void testVector3D() {
		kernel3D.Vector3D("MonVector3D", 1f,1f,1f);
		
	}
	
	
	
	private void testRegion(){
		GeoPoint A=kernel3D.Point("A", 0, 0);
		GeoPoint B=kernel3D.Point("B", 1, 0);
		GeoPoint C=kernel3D.Point("C", 1, 1);
		GeoPoint D=kernel3D.Point("D", 0.5, 1.5);
		GeoPoint E=kernel3D.Point("E", 0, 1);
		GeoPolygon p=(GeoPolygon) kernel3D.Polygon(null, new GeoPoint[] {A,B,C,D,E})[0];

		GeoPoint M= kernel3D.PointIn("M",(Region) p,0.5,0.5);  
	}

	
	
	
	private void testQuadric(){
		
		GeoPoint3D c = testPoint(-1.1f,-0.8f,0f); c.setLabel("center");
		GeoNumeric r = new GeoNumeric(cons, "radius", 1);
		GeoQuadric sphere = kernel3D.Sphere("sphere", c, r);
		sphere.setObjColor(new Color(1f,0f,0f));
	}
	
	
	
	private void testConic3D(){
		
		//GeoConic3D conic = kernel3D.Conic3D("conic", 1, 0, 1, -2, -2, -2, xOyPlane);		
		//conic.setObjColor(new Color(1f,0f,0f));
		
		
		GeoPoint3D[] points = new GeoPoint3D[3];
		points[0] = testPoint(0f,2f,1f);
		points[1] = testPoint(1f,0f,0f);
		points[2] = testPoint(-1f,0f,0f);
		
		GeoConic3D conic2 = kernel3D.Circle3D("circle3points", points[0], points[1], points[2]);		
		conic2.setObjColor(new Color(1f,0.5f,0f));
		
		kernel3D.Plane3D("plane3points", points[0], points[1], points[2]);
		
	}
	
	
	
	

	
	private void testPolyhedron(){

		
		
		GeoPoint3D[] points = new GeoPoint3D[5];
		points[0] = testPoint(1f,-1f,0f);
		points[1] = testPoint(1f,1f,0f);
		points[2] = testPoint(-1f,1f,0f);
		points[3] = testPoint(-1f,-1f,0f);
		points[4] = testPoint(0f,0f,2f);
		
		
		GeoPolyhedron p=kernel3D.Polyhedron("poly", points, 
				new int[][] {{0,1,2,3},{0,1,4},{1,2,4},{2,3,4},{3,0,4}});
	}
	
	private void testTetrahedron(){

		
		
		GeoPoint3D[] points = new GeoPoint3D[4];
		/*
		points[0] = testPoint((float) Math.sqrt(3)-1,-1f,0f);
		points[1] = testPoint((float) Math.sqrt(3)-1,1f,0f);
		points[2] = testPoint(-1f,0f,0f);
		points[3] = testPoint(0f,0f,(float) Math.sqrt(3));
		*/
		points[0] = testPoint(1f,-1f,0f);
		points[1] = testPoint(0.5f,1f,0f);
		points[2] = testPoint(-1f,0f,0f);
		points[3] = testPoint(0f,0f,2f);		
		
		GeoPolyhedron p=kernel3D.Polyhedron("tetrahedron", points, 
				new int[][] {{0,1,2},{0,1,3},{1,2,3},{2,0,3}});
	}

	
	private void testIntersectLinePlane(){
		GeoPoint3D[] points = new GeoPoint3D[2];
		points[0] = testPoint(1f,1f,1f);
		points[1] = testPoint(1f,1f,2f);
		
		GeoLine3D line = kernel3D.Line3D("line", points[0], points[1]);
		
		GeoPoint3D p = kernel3D.Intersect("intersection", line, xOyPlane);
		
	}
	
	
	private void testIntersectLineLine(){
		GeoPoint3D[] points = new GeoPoint3D[4];
		points[0] = testPoint(1f,1f,0f);
		points[1] = testPoint(1f,0f,0f);
		
		points[2] = testPoint(-1f,2f,0f);
		points[3] = testPoint(0f,2f,0f);
		
		GeoLine3D line1 = kernel3D.Line3D("line1", points[0], points[1]);
		GeoLine3D line2 = kernel3D.Line3D("line2", points[2], points[3]);
		
		GeoPoint3D p = kernel3D.Intersect("intersection", line1, line2);
		
	}
	
	private void testIntersectParallelLines(){
		GeoPoint3D[] points = new GeoPoint3D[5];
		points[0] = testPoint(1f,1f,0f);
		points[1] = testPoint(2f,1f,0f);
		
		points[2] = testPoint(1f,2f,0f);
		points[3] = testPoint(2f,2f,0f);
		
		points[4] = testPoint(0f,0f,0f);
		
		GeoLine3D line1 = kernel3D.Line3D("line1", points[0], points[1]);
		GeoLine3D line2 = kernel3D.Line3D("line2", points[2], points[3]);
		
		GeoPoint3D p = kernel3D.Intersect("intersection", line1, line2);
		
		GeoLine3D line3 = kernel3D.Line3D("line2", p, points[4]);
		
		
	}
}
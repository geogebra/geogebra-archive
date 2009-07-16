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

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Region;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.plugin.GgbAPI;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolyhedron;
import geogebra3D.kernel3D.GeoQuadric;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * @author  ggb3D
 */
public class Test3D{
	
	///////////////////////////////////////////////
	// DEMOS
	///////////////////////////////////////////////
	
	private void demos(){				
		
		/* Demo with a cube 
		 * Remove A to remove anything
		 * Use it to create new points, segments, lines, etc. */
		
		testAxisAndPlane();testCube(true);
		
		
		
		/* Demo with a spring and spreadsheet 
		 * Expand-down the second line to create more points and segments 
		 * Animate slider k */
		
		//testAxisAndPlane();testSpring(2,true);
		
		
		
		/* Demo with a cube and spring */
		
		//testCube(true);testSpring(50,false);
		
		
		
		/* Demo with a colored cube and more...
		 * Animate slider k 
		 * You can also create points, segments... */
		
		//testColoredCube(0.75);
		
		
		
		/* Demo with morphing polyhedra 
		 * Michael's demo*/
		
		//testMorphingPolyhedra();
		
		/* Michael's slices through cube */
		//testSlicesThroughCube();
		
		
		
		
	}
	
	
	///////////////////////////////////////////////
	// 
	///////////////////////////////////////////////
	
	
	
	
	
	
	
	
	Construction cons;
	Kernel3D kernel3D;
	EuclidianView view2D;
	Application3D app;
	
	
	GeoPlane3D xOyPlane;


	public Test3D(Kernel3D kernel3D, EuclidianView view2D, EuclidianView3D view3D, Application3D app){
		
		this.kernel3D=kernel3D;
		cons=kernel3D.getConstruction();
		this.view2D = view2D;
		this.app = app;
		
		app.setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY);

		//view2D.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		view2D.showAxes(true, true);
		view2D.setCoordSystem(100,view2D.getYZero(),view2D.getXscale(),view2D.getYscale());
		
		
        //init 3D view
        view3D.setZZero(-0.0);
        view3D.setYZero(-100);
        //view3D.setRotXY(-Math.PI/6,Math.PI/6,true);
        view3D.setRotXY(-Math.PI/6,Math.PI/8,true);
        //view3D.setRotXY(-Math.PI/6,Math.PI/12,true);
        //view3D.setRotXY(0,0,true);
        
		//testAxisAndPlane();
		
		
		//testTetrahedron();
		
		//testSpring();
		
        demos();
		
        //testLine();
        
        
        //testPoint(1,1,1);
        //testSegment3();
        //testPolygon();
        //testSave();	
        //testLoad();
        	
		//testRegion();
		
		
		
		//testConic3D();
		//testPolygon();
		//testPlane();


		//testQuadric();
    	
		//testRay3D();
		//testVector3D();
		//testAlgoPyramide();
		
		//testPolyhedron();
		//testTetrahedron();
		
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
	
	private void testAxisAndPlane(){
		
		
		double longueur = 2.25;
		
		GeoSegment3D s=null;
		
		s=kernel3D.Segment3D("axeX3D",new Ggb3DVector(new double[] {-longueur,0,0,1}),new Ggb3DVector(new double[]{longueur,0,0,1}));
		s.setObjColor(new Color(1f,0f,0f));
		s.setLineThickness(1);
		s.setAuxiliaryObject(true);
		cons.addToConstructionList(s, false);
		
		s=kernel3D.Segment3D("axeY3D",new Ggb3DVector(new double[] {0,-longueur,0,1}),new Ggb3DVector(new double[]{0,longueur,0,1}));
		s.setObjColor(new Color(0f,1f,0f));
		s.setLineThickness(1);
		s.setAuxiliaryObject(true);
		cons.addToConstructionList(s, false);
		
		s=kernel3D.Segment3D("axeZ3D",new Ggb3DVector(new double[] {0,0,0,1}),new Ggb3DVector(new double[]{0,0,longueur,1}));
		s.setObjColor(new Color(0f,0f,1f));
		s.setLineThickness(1);
		s.setAuxiliaryObject(true);
		cons.addToConstructionList(s, false);
		
		
		//xOy plane
		xOyPlane=kernel3D.Plane3D("xOy",
				new Ggb3DVector(new double[] {0.0,0.0,0.0,1.0}),
				new Ggb3DVector(new double[] {1.0,0.0,0.0,0.0}),
				new Ggb3DVector(new double[] {0.0,1.0,0.0,0.0}));
		xOyPlane.setObjColor(new Color(0.75f,0.75f,0.75f));
		xOyPlane.setAlgebraVisible(false); //TODO make it works
		xOyPlane.setLabelVisible(false);
		xOyPlane.setAuxiliaryObject(true);
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
	
	
	private void testSegment3(){

		
		
		GeoPoint3D[] points = new GeoPoint3D[2];
		points[0] = testPoint(1f,-1f,0f);
		points[1] = testPoint(0.5f,1f,0f);	

		
		GeoSegment3D s = kernel3D.Segment3D("segment", points[0], points[1]);
		kernel3D.Point3D("p", s, 0, 0, 0);
		
		
	}
	
	private void testLine(){

		
		
		GeoPoint3D[] points = new GeoPoint3D[2];
		points[0] = testPoint(1f,-1f,-0.5f);
		points[1] = testPoint(1f,1f,-0.5f);	

		
		kernel3D.Line3D("line", points[0], points[1]);
		
		
		
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
		/*
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
		*/
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
		
		
		GeoPoint3D[] points = new GeoPoint3D[3];
		points[0] = testPoint(0f,-1f,0f);
		points[1] = testPoint(1f,1f,0f);
		points[2] = testPoint(-1f,1f,0f);
		//points[3] = testPoint(-1f,-1f,0f);
		

		//kernel3D.Polygon3D(new String[] {"poly","a1","b2","c3","d4"}, points);
		kernel3D.Polygon3D(null, points);

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
		
		/*
		GeoPolyhedron p=kernel3D.Polyhedron("tetrahedron", points, 
				new int[][] {{0,1,2},{0,1,3},{1,2,3},{2,0,3}});
				*/
		
		GeoPolyhedron p=kernel3D.Pyramid("tetrahedron", points);
		
		
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
	
	
	
	private void testSave(){

		//testPoint(1f,-1f,0f);
		//testPoint(1f,1f,0f);
		
		File f = new File("geogebra3D/test3d.ggb");
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		app.saveGeoGebraFile(f);
		
	}
	
	
	private void testLoad(){

        app.loadXML(new File("geogebra3D/test3d.ggb"), false);

		
	}

	
	private void testSpring(int nb, boolean labels){
		try {
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("k=0",false);
			GeoNumeric k = (GeoNumeric) kernel3D.lookupLabel("k");
			k.setIntervalMin(0);k.setIntervalMax(10);k.setValue(0);k.setAnimationSpeed(10);
			k.setEuclidianVisible(true);k.setLabelVisible(true);
			
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("A1=0",false);
			kernel3D.lookupLabel("A1").setAuxiliaryObject(true);
			
			for (int i=1; i<nb; i++){
				kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("A"+(i+1)+"=A"+i+"+1",false);
				kernel3D.lookupLabel("A"+(i+1)).setAuxiliaryObject(true);
			}
			
			for(int i=1; i<=nb; i++){
				kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("B"+i+"=cos(A"+i+"/2)",false);
				kernel3D.lookupLabel("B"+i).setAuxiliaryObject(true);
				kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("C"+i+"=sin(A"+i+"/2)",false);
				kernel3D.lookupLabel("C"+i).setAuxiliaryObject(true);
				kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("D"+i+"=A"+i+"*k/100",false);
				kernel3D.lookupLabel("D"+i).setAuxiliaryObject(true);
				kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("E"+i+"=(B"+i+",C"+i+",D"+i+")",false);
				kernel3D.lookupLabel("E"+i).setAuxiliaryObject(true);
				kernel3D.lookupLabel("E"+i).setLabelVisible(labels);
			}
			
			for (int i=1; i<nb; i++){
				kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("F"+(i+1)+"=Segment[E"+i+",E"+(i+1)+"]",false);
				kernel3D.lookupLabel("F"+(i+1)).setAuxiliaryObject(true);
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	
	private GeoPoint3D[] testMorphingPolyhedra(){
		GeoPoint3D[] points = new GeoPoint3D[6];
		GeoPoint3D[] points2 = new GeoPoint3D[12];
		
		try {
			
			AlgebraProcessor ap = kernel3D.getAlgebraProcessor();
			
			ap.processAlgebraCommandNoExceptionHandling("A=(1,0,0)",false);
			points[0]=(GeoPoint3D) kernel3D.lookupLabel("A");
			ap.processAlgebraCommandNoExceptionHandling("B=(-y(A),x(A),0)",false);
			points[1]=(GeoPoint3D) kernel3D.lookupLabel("B");
			ap.processAlgebraCommandNoExceptionHandling("C=(-x(A),-y(A),0)",false);
			points[2]=(GeoPoint3D) kernel3D.lookupLabel("C");
			ap.processAlgebraCommandNoExceptionHandling("D=(y(A),-x(A),0)",false);
			points[3]=(GeoPoint3D) kernel3D.lookupLabel("D");
			ap.processAlgebraCommandNoExceptionHandling("E=(0,0,sqrt(x(A)^2+y(A)^2))",false);
			points[4]=(GeoPoint3D) kernel3D.lookupLabel("E");
			ap.processAlgebraCommandNoExceptionHandling("F=(0,0,-z(E))",false);
			points[5]=(GeoPoint3D) kernel3D.lookupLabel("F");
			
			ap.processAlgebraCommandNoExceptionHandling("a=0.382",false);
			kernel3D.lookupLabel("a").setEuclidianVisible(true);
			
			ap.processAlgebraCommandNoExceptionHandling("Q0 = a F + (1-a) A",false);
			ap.processAlgebraCommandNoExceptionHandling("Q1 = a F + (1-a) C",false);
			ap.processAlgebraCommandNoExceptionHandling("Q2 = a E + (1-a) A",false);
			ap.processAlgebraCommandNoExceptionHandling("Q3 = a E + (1-a) C",false);
			ap.processAlgebraCommandNoExceptionHandling("Q4 = a A + (1-a) B",false);
			ap.processAlgebraCommandNoExceptionHandling("Q5 = a C + (1-a) B",false);
			ap.processAlgebraCommandNoExceptionHandling("Q6 = a A + (1-a) D",false);
			ap.processAlgebraCommandNoExceptionHandling("Q7 = a C + (1-a) D",false);
			ap.processAlgebraCommandNoExceptionHandling("Q8 = a B + (1-a) E",false);
			ap.processAlgebraCommandNoExceptionHandling("Q9 = a B + (1-a) F",false);
			ap.processAlgebraCommandNoExceptionHandling("Q10 = a D + (1-a) E",false);
			ap.processAlgebraCommandNoExceptionHandling("Q11 = a D + (1-a) F",false);
			
			/*
			ap.processAlgebraCommandNoExceptionHandling("s0=Segment[E,A]",false);
			ap.processAlgebraCommandNoExceptionHandling("s1=Segment[E,B]",false);
			ap.processAlgebraCommandNoExceptionHandling("s2=Segment[E,C]",false);
			ap.processAlgebraCommandNoExceptionHandling("s3=Segment[E,D]",false);
			ap.processAlgebraCommandNoExceptionHandling("s4=Segment[F,A]",false);
			ap.processAlgebraCommandNoExceptionHandling("s5=Segment[F,B]",false);
			ap.processAlgebraCommandNoExceptionHandling("s6=Segment[F,C]",false);
			ap.processAlgebraCommandNoExceptionHandling("s7=Segment[F,D]",false);
			ap.processAlgebraCommandNoExceptionHandling("s8=Segment[A,B]",false);
			ap.processAlgebraCommandNoExceptionHandling("s9=Segment[B,C]",false);
			ap.processAlgebraCommandNoExceptionHandling("s10=Segment[C,D]",false);
			ap.processAlgebraCommandNoExceptionHandling("s11=Segment[D,A]",false);
			*/
			
			for (int i = 0 ; i < 12 ; i++)
			kernel3D.lookupLabel("Q"+i).setLabelVisible(false);
			
			for (int i = 0 ; i < 12 ; i ++) {
				points2[i]=(GeoPoint3D) kernel3D.lookupLabel("Q"+i);
				points2[i].setEuclidianVisible(false);

			}
			
			for (int i = 0 ; i < 6 ; i ++) {
				points[i].setEuclidianVisible(false);

			}

			
			//kernel3D.Polyhedron("octahedron", points, 
			//		new int[][] {
			//		{4,0,1},{4,1,2},{4,2,3},{4,3,0},
			//		{5,0,1},{5,1,2},{5,2,3},{5,3,0}					
			//});
			kernel3D.Polyhedron("polyhedra1", points2, 
					new int[][] {
					{2,6,10},{3,10,7},{3,5,8},{8,2,4},
					{0,6,11},{0,9,4},{9,5,1},{1,7,11},
			});
			//kernel3D.lookupLabel("polyhedra1").setObjColor(new Color(255,255,0));
			
			//kernel3D.Polyhedron("polyhedra2", points2, 
			//		new int[][] {
			//		{3,8,2,10}, {10,6,11,7}, {2,4,0,6},
			//		{11,0,9,1}, {4,8,5,9}, 
			//		{8,2,10}
			//});
			kernel3D.Polyhedron("polyhedra3", points2, 
					new int[][] {
					{8,2,10}, {8,10,3}, {10,6,7}, {11,6,7}, {6,2,0}, {4,2,0}, {3,7,1}, {3,5,1},
					{9,5,4}, {8,5,4}, {0,9,11}, {1,9,11}
			});
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return points;
	}
	
	

	private GeoPoint3D[] testCube(boolean top){
		GeoPoint3D[] points = new GeoPoint3D[8];
		
		try {
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("A=(1,1,0)",false);
			points[0]=(GeoPoint3D) kernel3D.lookupLabel("A");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("B=(-y(A),x(A),z(A))",false);
			points[1]=(GeoPoint3D) kernel3D.lookupLabel("B");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("C=(-x(A),-y(A),z(A))",false);
			points[2]=(GeoPoint3D) kernel3D.lookupLabel("C");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("D=(y(A),-x(A),z(A))",false);
			points[3]=(GeoPoint3D) kernel3D.lookupLabel("D");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("E=(x(A),y(A), z(A)+sqrt((A-B)^2) )",false);
			points[4]=(GeoPoint3D) kernel3D.lookupLabel("E");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("F=(x(B),y(B),z(E))",false);
			points[5]=(GeoPoint3D) kernel3D.lookupLabel("F");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("G=(x(C),y(C),z(E))",false);
			points[6]=(GeoPoint3D) kernel3D.lookupLabel("G");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("H=(x(D),y(D),z(E))",false);
			points[7]=(GeoPoint3D) kernel3D.lookupLabel("H");
			
			/*
			kernel3D.Polyhedron("cube", points, 
					new int[][] {
					{0,1,2,3},
					{4,5,6,7},
					{0,1,5,4},{1,2,6,5},{2,3,7,6},{3,0,4,7}
			});
			*/
			
			kernel3D.Polyhedron("cube", points);
			
			if (!top)
				kernel3D.lookupLabel("faceEFGH",false).setEuclidianVisible(false);
				
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return points;
	}
	
	private GeoPoint3D[] testSlicesThroughCube(){
		GeoPoint3D[] points = new GeoPoint3D[8];
		
		try {
			
			GgbAPI ggbapi = app.getGgbApi();
			
			ggbapi.evalCommand("P_1=(1,1,0)");
			ggbapi.evalCommand("P_2=(-y(P_1),x(P_1),z(P_1))");
			ggbapi.evalCommand("P_3=(-x(P_1),-y(P_1),z(P_1))");
			ggbapi.evalCommand("P_4=(y(P_1),-x(P_1),z(P_1))");
			ggbapi.evalCommand("P_5=(x(P_1),y(P_1), z(P_1)+sqrt((P_1-P_2)^2) )");
			ggbapi.evalCommand("P_6=(x(P_2),y(P_2),z(P_5))");
			ggbapi.evalCommand("P_7=(x(P_3),y(P_3),z(P_5))");
			ggbapi.evalCommand("P_8=(x(P_4),y(P_4),z(P_5))");
			
			ggbapi.evalCommand("a = 0.5");
			
			ggbapi.evalCommand("Segment[P_1,P_2]");
			ggbapi.evalCommand("Segment[P_1,P_4]");
			ggbapi.evalCommand("Segment[P_1,P_5]");

			ggbapi.evalCommand("Segment[P_7,P_3]");
			ggbapi.evalCommand("Segment[P_7,P_6]");
			ggbapi.evalCommand("Segment[P_7,P_8]");
			
			ggbapi.evalCommand("Segment[P_3,P_2]");
			ggbapi.evalCommand("Segment[P_3,P_4]");
			ggbapi.evalCommand("Segment[P_8,P_4]");

			ggbapi.evalCommand("Segment[P_8,P_5]");
			ggbapi.evalCommand("Segment[P_5,P_6]");
			ggbapi.evalCommand("Segment[P_6,P_2]");


			
			ggbapi.evalCommand("Q_1 = a P_2 + (1-a) P_1");
			ggbapi.evalCommand("Q_2 = a P_1 + (1-a) P_4");
			ggbapi.evalCommand("Q_3 = a P_4 + (1-a) P_8");
			ggbapi.evalCommand("Q_4 = a P_8 + (1-a) P_7");
			ggbapi.evalCommand("Q_5 = a P_7 + (1-a) P_6");
			ggbapi.evalCommand("Q_6 = a P_6 + (1-a) P_2");
			
			ggbapi.evalCommand("Polygon[Q_1,Q_2,Q_3,Q_4,Q_5,Q_6]");
			
			for (int i = 0 ; i < 8 ; i++ ) 
				((GeoPoint3D) kernel3D.lookupLabel("P_"+(i+1))).setLabelVisible(false);
			for (int i = 0 ; i < 6 ; i++ ) 
				((GeoPoint3D) kernel3D.lookupLabel("Q_"+(i+1))).setLabelVisible(false);
				
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return points;
	}
	
	
	private void testColoredCube(double a){
		GeoPoint3D[] points = new GeoPoint3D[8];
		
		Color color1 = Color.RED;
		Color color2 = Color.BLUE;
		Color color3 = Color.GREEN;
		
		try {
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("A=("+a+","+a+","+(-a/2)+")",false);
			points[0]=(GeoPoint3D) kernel3D.lookupLabel("A");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("B=(-y(A),x(A),z(A))",false);
			points[1]=(GeoPoint3D) kernel3D.lookupLabel("B");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("C=(-x(A),-y(A),z(A))",false);
			points[2]=(GeoPoint3D) kernel3D.lookupLabel("C");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("D=(y(A),-x(A),z(A))",false);
			points[3]=(GeoPoint3D) kernel3D.lookupLabel("D");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("E=(x(A),y(A), z(A)+sqrt((A-B)^2) )",false);
			points[4]=(GeoPoint3D) kernel3D.lookupLabel("E");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("F=(x(B),y(B),z(E))",false);
			points[5]=(GeoPoint3D) kernel3D.lookupLabel("F");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("G=(x(C),y(C),z(E))",false);
			points[6]=(GeoPoint3D) kernel3D.lookupLabel("G");
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("H=(x(D),y(D),z(E))",false);
			points[7]=(GeoPoint3D) kernel3D.lookupLabel("H");
	
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("k=1.0001",false);
			GeoNumeric k = (GeoNumeric) kernel3D.lookupLabel("k");
			k.setIntervalMin(0.5001);k.setIntervalMax(1.5001);
			k.setAnimationSpeed(10);k.setAnimationStep(0.01);
			k.setEuclidianVisible(true);k.setLabelVisible(true);
			
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"I=(B+C+G+F)/4",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"J=(A+D+H+E)/4",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"I'=k*I+(1-k)*J",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"J'=k*J+(1-k)*I",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"Pyramid[B,C,G,F,I']",false);
			kernel3D.lookupLabel("faceBCGF").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceBCI'").setObjColor(color3);
			kernel3D.lookupLabel("edgeBC").setObjColor(color3);
			kernel3D.lookupLabel("faceGFI'").setObjColor(color3);
			kernel3D.lookupLabel("edgeGF").setObjColor(color3);
			kernel3D.lookupLabel("faceCGI'").setObjColor(color2);
			kernel3D.lookupLabel("edgeCG").setObjColor(color2);
			kernel3D.lookupLabel("faceFBI'").setObjColor(color2);
			kernel3D.lookupLabel("edgeBF").setObjColor(color2);
			kernel3D.lookupLabel("edgeBI'").setObjColor(color1);
			kernel3D.lookupLabel("edgeCI'").setObjColor(color1);
			kernel3D.lookupLabel("edgeGI'").setObjColor(color1);
			kernel3D.lookupLabel("edgeFI'").setObjColor(color1);
			kernel3D.lookupLabel("I'").setObjColor(color1);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"Pyramid[A,D,H,E,J']",false);
			kernel3D.lookupLabel("faceADHE").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceADJ'").setObjColor(color3);
			kernel3D.lookupLabel("edgeAD").setObjColor(color3);
			kernel3D.lookupLabel("faceHEJ'").setObjColor(color3);
			kernel3D.lookupLabel("edgeHE").setObjColor(color3);
			kernel3D.lookupLabel("faceDHJ'").setObjColor(color2);
			kernel3D.lookupLabel("edgeDH").setObjColor(color2);
			kernel3D.lookupLabel("faceEAJ'").setObjColor(color2);
			kernel3D.lookupLabel("edgeAE").setObjColor(color2);
			kernel3D.lookupLabel("edgeAJ'").setObjColor(color1);
			kernel3D.lookupLabel("edgeDJ'").setObjColor(color1);
			kernel3D.lookupLabel("edgeHJ'").setObjColor(color1);
			kernel3D.lookupLabel("edgeEJ'").setObjColor(color1);
			kernel3D.lookupLabel("J'").setObjColor(color1);
			
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"K=(A+B+C+D)/4",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"L=(E+F+G+H)/4",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"K'=k*K+(1-k)*L",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"L'=k*L+(1-k)*K",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"Pyramid[A,B,C,D,K']",false);
			kernel3D.lookupLabel("faceABCD").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceBCK'").setObjColor(color3);
			kernel3D.lookupLabel("edgeBC").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceDAK'").setObjColor(color3);
			kernel3D.lookupLabel("edgeAD").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceABK'").setObjColor(color1);
			kernel3D.lookupLabel("edgeAB").setObjColor(color1);
			kernel3D.lookupLabel("faceCDK'").setObjColor(color1);
			kernel3D.lookupLabel("edgeCD").setObjColor(color1);
			kernel3D.lookupLabel("edgeAK'").setObjColor(color2);
			kernel3D.lookupLabel("edgeBK'").setObjColor(color2);
			kernel3D.lookupLabel("edgeCK'").setObjColor(color2);
			kernel3D.lookupLabel("edgeDK'").setObjColor(color2);
			kernel3D.lookupLabel("K'").setObjColor(color2);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"Pyramid[E,F,G,H,L']",false);
			kernel3D.lookupLabel("faceEFGH").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceFGL'").setObjColor(color3);
			kernel3D.lookupLabel("edgeFG").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceHEL'").setObjColor(color3);
			kernel3D.lookupLabel("edgeEH").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceEFL'").setObjColor(color1);
			kernel3D.lookupLabel("edgeEF").setObjColor(color1);
			kernel3D.lookupLabel("faceGHL'").setObjColor(color1);
			kernel3D.lookupLabel("edgeGH").setObjColor(color1);
			kernel3D.lookupLabel("edgeEL'").setObjColor(color2);
			kernel3D.lookupLabel("edgeFL'").setObjColor(color2);
			kernel3D.lookupLabel("edgeGL'").setObjColor(color2);
			kernel3D.lookupLabel("edgeHL'").setObjColor(color2);
			kernel3D.lookupLabel("L'").setObjColor(color2);

			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"M=(A+B+F+E)/4",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"N=(D+C+G+H)/4",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"M'=k*M+(1-k)*N",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"N'=k*N+(1-k)*M",false);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"Pyramid[A,B,F,E,M']",false);
			kernel3D.lookupLabel("faceABFE").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceABM'").setObjColor(color1);
			kernel3D.lookupLabel("edgeAB").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceFEM'").setObjColor(color1);
			kernel3D.lookupLabel("edgeFE").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceBFM'").setObjColor(color2);
			kernel3D.lookupLabel("edgeBF").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceEAM'").setObjColor(color2);
			kernel3D.lookupLabel("edgeAE").setEuclidianVisible(false);
			kernel3D.lookupLabel("edgeAM'").setObjColor(color3);
			kernel3D.lookupLabel("edgeBM'").setObjColor(color3);
			kernel3D.lookupLabel("edgeFM'").setObjColor(color3);
			kernel3D.lookupLabel("edgeEM'").setObjColor(color3);
			kernel3D.lookupLabel("M'").setObjColor(color3);
			kernel3D.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
					"Pyramid[D,C,G,H,N']",false);
			kernel3D.lookupLabel("faceDCGH").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceDCN'").setObjColor(color1);
			kernel3D.lookupLabel("edgeDC").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceGHN'").setObjColor(color1);
			kernel3D.lookupLabel("edgeGH").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceCGN'").setObjColor(color2);
			kernel3D.lookupLabel("edgeCG").setEuclidianVisible(false);
			kernel3D.lookupLabel("faceHDN'").setObjColor(color2);
			kernel3D.lookupLabel("edgeDH").setEuclidianVisible(false);
			kernel3D.lookupLabel("edgeDN'").setObjColor(color3);
			kernel3D.lookupLabel("edgeCN'").setObjColor(color3);
			kernel3D.lookupLabel("edgeGN'").setObjColor(color3);
			kernel3D.lookupLabel("edgeHN'").setObjColor(color3);
			kernel3D.lookupLabel("N'").setObjColor(color3);
			
			kernel3D.lookupLabel("I").setEuclidianVisible(false);
			kernel3D.lookupLabel("J").setEuclidianVisible(false);
			kernel3D.lookupLabel("K").setEuclidianVisible(false);
			kernel3D.lookupLabel("L").setEuclidianVisible(false);
			kernel3D.lookupLabel("M").setEuclidianVisible(false);
			kernel3D.lookupLabel("N").setEuclidianVisible(false);

			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
	}

	
}

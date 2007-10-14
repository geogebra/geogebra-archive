/*
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

 */

package geogebra.export.pstricks;
import geogebra.Application;
import geogebra.euclidian.DrawAngle;
import geogebra.euclidian.DrawLine;
import geogebra.euclidian.DrawPoint;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.export.pstricks.gui.PstricksPanel;
import geogebra.kernel.AlgoAngleLines;
import geogebra.kernel.AlgoAnglePoints;
import geogebra.kernel.AlgoAngleVector;
import geogebra.kernel.AlgoAngleVectors;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.AlgoSumUpperLower;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoRay;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.arithmetic.Function;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
/**
 * @author Le Coq loïc
 */

public class GeoGebraToPstricks implements ActionListener {
	private final double PRECISION_XRANGE_FUNCTION=0.00001;
	private StringBuffer code,codePoint,codePreamble,codeFilledObject,codeBeginDoc,codeBeginPic;
	private Application app;
    private Kernel kernel;
    private Construction construction;
    private EuclidianView euclidianView;
    private PstricksPanel frame;
    private HashMap CustomColor;
    private double xunit,yunit,xmin,xmax,ymin,ymax;
    private boolean transparency;
    public GeoGebraToPstricks(Application app) {
    	this.app = app;
    	this.kernel = app.getKernel();
    	this.construction = kernel.getConstruction();
    	this.euclidianView = app.getEuclidianView();
    	initGui();
    }
    public Application getApp(){
    	return app;
    }
    private void initGui(){	
    	xunit=1;
    	yunit=1;
    	//Changes to make xmin,xmax,ymin,ymax be defined by the selection rectangle
    	//when this one is defined.
    	Rectangle rect = this.euclidianView.getSelectionRectangle();
    	if( rect != null){
    		xmin=euclidianView.toRealWorldCoordX(rect.getMinX());
        	xmax=euclidianView.toRealWorldCoordX(rect.getMaxX());
        	ymin=euclidianView.toRealWorldCoordY(rect.getMaxY());
        	ymax=euclidianView.toRealWorldCoordY(rect.getMinY());
    	}
    	else{
    		xmin=euclidianView.getXmin();
    		xmax=euclidianView.getXmax();
    		ymin=euclidianView.getYmin();
    		ymax=euclidianView.getYmax();
    	}
    	frame=new PstricksPanel(this);
    }
    //Functions added to access and modify xmin, xmax, ymin and ymax
    //When xmin,xmax,ymin or ymax are changed
    //the selected area is reported accodingly on the euclidianView.
    //This is not visible, on the view, but one may expect that when
    //the selection rectangle is changed it is displayed on the view.
    //This may be implemented by changing the class EuclidianView.
    //Furthermore the definition of a class EuclidianView listerner 
    //which this class would implement would be desirable so that 
    //when the selection is modified by the mouse, this is reported
    //to the values xmin, xmax, ymin and ymax of instances of this class.
    
    private void refreshEuclidianView(){
    	int x=euclidianView.toScreenCoordX(xmin);
    	int y=euclidianView.toScreenCoordY(ymin);
    	int width=euclidianView.toScreenCoordX(xmax)-x;
    	int height=euclidianView.toScreenCoordY(ymax)-y;
    	Rectangle r= new Rectangle(x,y,width,height);
    	this.euclidianView.setSelectionRectangle(r);
    }
    public void setxmin(double xmin){
    	this.xmin=xmin;
    	this.refreshEuclidianView();
    }
    public void setxmax(double xmax){
    	this.xmax=xmax;
    	this.refreshEuclidianView();
    }
    public void setymin(double ymin){
    	this.ymin=ymin;
    	this.refreshEuclidianView();
    }
    public void setymax(double ymax){
    	this.ymax=ymax;
    	this.refreshEuclidianView();
    }
    public double getxmin(){return this.xmin;}
    public double getxmax(){return this.xmax;}
    public double getymin(){return this.ymin;}
    public double getymax(){return this.ymax;}
    //end changes.
    //
    //Adding edition and reading function for xunit and yunit
    //to make the PstricksPanel be only a view of GeoGebraToPstricks
    public void setxunit(double xunit){this.xunit=xunit;}
    public void setyunit(double yunit){this.yunit=yunit;}
    public double getxunit(){return this.xunit;}
    public double getyunit(){return this.yunit;}
    //end addition
   
	 
    public void createPstricks() {
 
    	// init unit variables
    	try{	
    		xunit=frame.getXUnit();
    		yunit=frame.getYUnit();
     	}
    	catch(NullPointerException e2){
    		xunit=1;yunit=1;
    	}
//    	scaleratio=yunit/xunit;
    	// Initialize new StringBuffer for Pstricks code
    	// and CustomColor
    	code=new StringBuffer();
    	codePoint=new StringBuffer();
    	codePreamble=new StringBuffer();
    	codeFilledObject=new StringBuffer();
		codeBeginDoc=new StringBuffer();
		codeBeginPic=new StringBuffer();
		CustomColor=new HashMap();
 		transparency=false;
 		
    	codePreamble.append("\\documentclass[" +
    			frame.getFontSize()+"pt]{article}\n" +
    			"\\usepackage{pstricks,pstricks-add,pst-math,pst-xkey}\n");
     	codeBeginDoc.append("\\begin{document}\n");
     	
    	// Draw Grid
		if (euclidianView.getShowGrid()) drawGrid();
		else {
			initUnitAndVariable();
			// Environment pspicture
			codeBeginPic.append("\\begin{pspicture*}(");
			codeBeginPic.append(kernel.format(xmin));
			codeBeginPic.append(",");
			codeBeginPic.append(kernel.format(ymin));
			codeBeginPic.append(")(");
			codeBeginPic.append(kernel.format(xmax));
			codeBeginPic.append(",");
			codeBeginPic.append(kernel.format(ymax));
			codeBeginPic.append(")\n");
		}
		
		// Draw axis
		if (euclidianView.getShowXaxis() || euclidianView.getShowYaxis()) 
			drawAxis();
		
/*		 get all objects from construction
 *   	 and "draw" them by creating pstricks code*/
        Iterator it = construction.getGeoSetConstructionOrder().iterator();  	 
        while (it.hasNext()) {
        	GeoElement g = (GeoElement) it.next();
           	drawGeoElement(g);
        }
        // add code for Points and Labels
        code.append(codePoint);
        // Close Environment pspicture
		code.append("\\end{pspicture*}\n");
/*		String formatFont=resizeFont(app.getFontSize());
		if (null!=formatFont){
			codeBeginPic.insert(0,formatFont+"\n");
			code.append("}\n");
		}*/
		code.insert(0,codeFilledObject);
		code.insert(0,codeBeginPic);
        code.insert(0,codeBeginDoc);		
        code.insert(0,codePreamble);
		code.append("\\end{document}");		
		frame.write(code);
	}	

    private void drawGeoElement(GeoElement g){
    	if (g.isEuclidianVisible()) {
    		if (g.isGeoPoint()){
				drawGeoPoint((GeoPoint)g);
				drawLabel(g,null);
    		}
		else if (g.isGeoSegment()){
			drawGeoSegment((GeoSegment)g);
	      	drawLabel(g,null);
		}
		else if (g.isGeoRay()){
			drawGeoRay((GeoRay)g);
	      	drawLabel(g,null);
		}
		else if (g.isGeoLine()){
			drawGeoLine((GeoLine)g);
	      	drawLabel(g,null);
		}
		else if (g.isGeoPolygon()) {
			drawPolygon((GeoPolygon)g);
			drawLabel(g,null);
		}
		else if (g.isGeoAngle()) {
        	if (g.isIndependent()) {
        		// independent number may be shown as slider
        		drawSlider((GeoNumeric)g);
        	} else {
        		drawAngle((GeoAngle)g);
//        		String label="$"+Util.toLaTeXString(g.getLabelDescription(),true)+"$";
        		drawLabel(g,euclidianView.getDrawableFor((GeoAngle)g));
        	}        	                                  
     }  
        else if (g.isGeoNumeric()) {
            AlgoElement algo = g.getParentAlgorithm();
            if (algo == null) {
            	// indpendent number may be shown as slider
        		drawSlider((GeoNumeric)g);
            }
            else if (algo instanceof AlgoSlope) {
            	drawSlope((GeoNumeric)g);
            	drawLabel(g,null);
            }       
            else if (algo instanceof AlgoIntegralDefinite) {
                drawIntegral((GeoNumeric) g);  
                drawLabel(g,null);
            } 
            else if (algo instanceof AlgoSumUpperLower) {
              drawSumUpperLower((GeoNumeric)g);
              drawLabel(g,null);
            }
        }
        else if (g.isGeoVector()) {
        	drawGeoVector((GeoVector)g);
	      	drawLabel(g,null);
        } else if (g.isGeoConicPart()) {
        	GeoConicPart geo=(GeoConicPart)g;
        	drawGeoConicPart(geo);
        	if (geo.getConicPartType()==GeoConicPart.CONIC_PART_ARC
        			|| geo.getConicPartType()==GeoConicPart.CONIC_PART_SECTOR)
	      	drawLabel(g,null);
        } else if (g.isGeoConic()) {
			if (isSinglePointConic(g)){
				GeoConic geo=(GeoConic)g;
				GeoPoint point = geo.getSinglePoint();
				point.copyLabel(geo);
				point.objColor = geo.objColor;
				point.labelColor = geo.labelColor;
				point.pointSize = geo.lineThickness;
				point.setLabelOffset(geo.labelOffsetX,geo.labelOffsetY);
				DrawPoint drawPoint = new DrawPoint(euclidianView, point);
				drawPoint.setGeoElement(geo);
				drawGeoPoint(point);
				drawLabel(point,drawPoint);
			}
			else if(isDoubleLineConic(g)){
				GeoConic geo=(GeoConic)g;
				GeoLine[] lines = geo.getLines();
				DrawLine[] drawLines = new DrawLine[2];
				for (int i=0; i < 2; i++) {
					lines[i].copyLabel(geo);					
					lines[i].objColor = geo.objColor;					
					lines[i].labelColor = geo.labelColor;	
					lines[i].lineThickness = geo.lineThickness;
					lines[i].lineType = geo.lineType;
				}
				drawLines[0] = new DrawLine(euclidianView, lines[0]);
				drawLines[1] = new DrawLine(euclidianView, lines[1]);               
				drawLines[0].setGeoElement(geo);
				drawLines[1].setGeoElement(geo);
				drawGeoLine(lines[0]);
				drawGeoLine(lines[1]);
				drawLabel(lines[0],drawLines[0]);
				drawLabel(lines[1],drawLines[1]);
			}
			else if (isEmpty(g)){
			}
			else {
				drawGeoConic((GeoConic)g);
				drawLabel(g,null);
			}			
        } else if (g.isGeoFunction()) {
        	drawFunction((GeoFunction)g);
        	drawLabel(g,null);
        } else if (g.isGeoText()) {
        	drawText((GeoText)g);
        } else if (g.isGeoImage()) {
        	//Image --> export to eps is better and easier!
        }  else if (g.isGeoLocus()) {
        	drawLocus((GeoLocus)g);
        }
	}
    	
    }
    private void drawLocus(GeoLocus g){
    	ArrayList ll=g.getMyPointList();
    	Iterator it=ll.iterator();
    	code.append("\\pscustom{");
    	while(it.hasNext()){
    		MyPoint mp=(MyPoint)it.next();
    		String x=kernel.format(mp.x);
    		String y=kernel.format(mp.y);
    		boolean b=mp.lineTo;
    		if (b) code.append("\\lineto(");
    		else code.append("\\moveto(");
    		code.append(x);
    		code.append(",");
    		code.append(y);
    		code.append(")\n");
    	}
		code.append("}\n");
    }
    
    private void drawSumUpperLower(GeoNumeric geo){
    	AlgoSumUpperLower algo = (AlgoSumUpperLower)geo.getParentAlgorithm();
    	int n=algo.getIntervals();
        double step=algo.getStep();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
        for (int i=0;i<n;i++){
        	codeFilledObject.append("\\psframe");
        	codeFilledObject.append(LineOptionCode(geo,true));
        	codeFilledObject.append("(");
        	codeFilledObject.append(kernel.format(x[i]));
        	codeFilledObject.append(",0)(");
        	codeFilledObject.append(kernel.format(x[i]+step));
        	codeFilledObject.append(",");
        	codeFilledObject.append(kernel.format(y[i]));
        	codeFilledObject.append(")\n");
        }
    }
    private void drawIntegral(GeoNumeric geo){
// command: \pscutom[option]{\pstplot{a}{b}{f(x)}\lineto(b,0)\lineto(a,0)\closepath} 
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo.getParentAlgorithm();
    	// function f
    	GeoFunction f = algo.getFunction();
    	// between a and b
    	String a = kernel.format(algo.getA().getDouble());
        String b = kernel.format(algo.getB().getDouble());    
    	String value=f.toValueString();
    	value=killSpace(Util.toLaTeXString(value,true));
    	codeFilledObject.append("\\pscustom");
    	codeFilledObject.append(LineOptionCode(geo,true));
    	codeFilledObject.append("{\\psplot{");
    	codeFilledObject.append(a);
    	codeFilledObject.append("}{");
    	codeFilledObject.append(b);
    	codeFilledObject.append("}{");
    	codeFilledObject.append(value);
    	codeFilledObject.append("}\\lineto(");
    	codeFilledObject.append(b);
    	codeFilledObject.append(",0)\\lineto(");
    	codeFilledObject.append(a);
    	codeFilledObject.append(",0)\\closepath}\n");

    }
    private void drawSlope(GeoNumeric geo){
       	int slopeTriangleSize = geo.getSlopeTriangleSize();
        double rwHeight = geo.getValue() * slopeTriangleSize;
        double height =  euclidianView.getYscale() * rwHeight;
        double[] coords=new double[2];
        if (Math.abs(height) > Float.MAX_VALUE) {
        	return;
        }
        // get point on line g
        GeoLine g = ((AlgoSlope)geo.getParentAlgorithm()).getg();
        g.getInhomPointOnLine(coords);
        // draw slope triangle       
        float x = (float) coords[0];
        float y = (float) coords[1];
        float xright=x+slopeTriangleSize;
    	codeFilledObject.append("\\pspolygon");
    	codeFilledObject.append(LineOptionCode(geo,true));
    	codeFilledObject.append("(");
		codeFilledObject.append(kernel.format(x));
		codeFilledObject.append(",");
		codeFilledObject.append(kernel.format(y));
		codeFilledObject.append(")");
		codeFilledObject.append("(");
		codeFilledObject.append(kernel.format(xright));
		codeFilledObject.append(",");
		codeFilledObject.append(kernel.format(y));
		codeFilledObject.append(")");
		codeFilledObject.append("(");
		codeFilledObject.append(kernel.format(xright));
		codeFilledObject.append(",");
		codeFilledObject.append(kernel.format(y+rwHeight));
		codeFilledObject.append(")");
    	codeFilledObject.append("\n");	
        // draw Label
    	float xLabelHor = (x + xright) /2;
        float yLabelHor = y -(float)(
        		(euclidianView.getFont().getSize() + 2)/euclidianView.getYscale());
		Color geocolor=geo.getObjectColor();
		codePoint.append("\\rput[bl](");
		codePoint.append(kernel.format(xLabelHor));
		codePoint.append(",");
		codePoint.append(kernel.format(yLabelHor));
		codePoint.append("){");
		if (!geocolor.equals(Color.BLACK)){
			codePoint.append("\\");
			ColorCode(geocolor,codePoint);
			codePoint.append("{");
		}
		codePoint.append(slopeTriangleSize);
		if (!geocolor.equals(Color.BLACK)){
			codePoint.append("}");
		}
		codePoint.append("}\n");	

    }
    private void drawAngle(GeoAngle geo){
    	int arcSize=geo.getArcSize();
    	AlgoElement algo=geo.getParentAlgorithm();
    	GeoPoint vertex,point;
    	GeoVector v;
    	GeoLine line,line2;
     	GeoPoint tempPoint = new GeoPoint(construction);     	
     	tempPoint.setCoords(0.0, 0.0, 1.0);
     	double[] firstVec=new double[2];
     	double[] m=new double[2];
     	// angle defines with three points
     	if (algo instanceof AlgoAnglePoints) {
     		AlgoAnglePoints pa = (AlgoAnglePoints) algo;
    		vertex = pa.getB();
    		point = pa.getA();
	        vertex.getInhomCoords(m);
	        // first vec
		    firstVec[0] = point.inhomX - m[0];
		    firstVec[1] = point.inhomY - m[1];
    	} 
     	// angle between two vectors
		else if (algo instanceof AlgoAngleVectors) {
			AlgoAngleVectors va = (AlgoAngleVectors) algo;
			v = va.getv();
       		// vertex
    		vertex = v.getStartPoint();        		
    		if (vertex == null) vertex = tempPoint;
    		vertex.getInhomCoords(m);
    		// first vec
    		v.getInhomCoords(firstVec);        		
		} 
     	// angle between two lines
		else if (algo instanceof AlgoAngleLines) {
			AlgoAngleLines la = (AlgoAngleLines) algo;
			line = la.getg();
			line2 = la.geth();	
			vertex = tempPoint;
			// intersect lines to get vertex
			GeoVec3D.cross(line, line2, vertex);
			vertex.getInhomCoords(m);
			// first vec
			line.getDirection(firstVec);
		}
		// angle of a single vector or a single point
		else if (algo instanceof AlgoAngleVector) {			
			AlgoAngleVector va = (AlgoAngleVector) algo;
			GeoVec3D vec = va.getVec3D();	
			if (vec instanceof GeoVector) {
				v = (GeoVector) vec;
        		// vertex
        		vertex = v.getStartPoint();        		
        		if (vertex == null) vertex = tempPoint;
        		vertex.getInhomCoords(m);
			} else if (vec instanceof GeoPoint) {
				point = (GeoPoint) vec;				
				vertex = tempPoint;
        		// vertex
		        vertex.getInhomCoords(m);
			}			
			firstVec[0] = 1;
			firstVec[1] = 0;

		}
		double angSt = Math.atan2(firstVec[1], firstVec[0]);
        double angExt = geo.getValue();
        if (angExt>Math.PI*2) angExt-=Math.PI*2;
        if (geo.changedReflexAngle()) {        	
        	angSt = angSt - angExt;
        }
        angExt+=angSt;
		double r = arcSize /euclidianView.getXscale();
		// if angle=90� and decoration=little square
        if (kernel.isEqual(geo.getValue(),Kernel.PI_HALF)&&geo.isEmphasizeRightAngle()&&euclidianView.getRightAngleStyle()==EuclidianView.RIGHT_ANGLE_STYLE_SQUARE){
        	r=r/Math.sqrt(2);
        	double[] x=new double[8];
        	x[0]=m[0]+r*Math.cos(angSt);
        	x[1]=m[1]+r*Math.sin(angSt);
        	x[2]=m[0]+r*Math.sqrt(2)*Math.cos(angSt+Kernel.PI_HALF/2);
        	x[3]=m[1]+r*Math.sqrt(2)*Math.sin(angSt+Kernel.PI_HALF/2);
        	x[4]=m[0]+r*Math.cos(angSt+Kernel.PI_HALF);
        	x[5]=m[1]+r*Math.sin(angSt+Kernel.PI_HALF);
        	x[6]=m[0];
        	x[7]=m[1];
        	
          	// command: \pspolygon[par](x0,y0)....(xn,yn)
        	codeFilledObject.append("\\pspolygon");
        	codeFilledObject.append(LineOptionCode(geo,true));
        	for (int i=0;i<4;i++){
         		codeFilledObject.append("(");
        		codeFilledObject.append(kernel.format(x[2*i]));
        		codeFilledObject.append(",");
        		codeFilledObject.append(kernel.format(x[2*i+1]));
        		codeFilledObject.append(")");
        	}
        	codeFilledObject.append("\n");	
        }
        // draw arc for the angle
        else {	
       	// set arc in real world coords
		code.append("\\pscustom");
		code.append(LineOptionCode(geo,true));
		code.append("{\\parametricplot{");
		code.append(angSt);
		code.append("}{");
		code.append(angExt);
		code.append("}{");
		code.append(kernel.format(r));
		code.append("*cos(t)+");
		code.append(kernel.format(m[0]));
		code.append("|");
		code.append(kernel.format(r));
		code.append("*sin(t)+");
		code.append(kernel.format(m[1]));
		code.append("}");
		code.append("\\lineto(");
		code.append(kernel.format(m[0]));
		code.append(",");
		code.append(kernel.format(m[1]));
		code.append(")\\closepath}\n");
		// draw the dot if angle= 90� and decoration=dot
		if (kernel.isEqual(geo.getValue(),Kernel.PI_HALF)&&geo.isEmphasizeRightAngle()&&euclidianView.getRightAngleStyle()==EuclidianView.RIGHT_ANGLE_STYLE_DOT){
			double diameter = geo.lineThickness/euclidianView.getXscale();
			double radius = arcSize/euclidianView.getXscale()/1.7;
			double labelAngle = (angSt+angExt) / 2.0;
			double x1=m[0] + radius * Math.cos(labelAngle);
			double x2 = m[1] + radius * Math.sin(labelAngle);
			// draw an ellipse
			// command:  \psellipse(0,0)(20.81,-10.81)}
				code.append("\\psellipse*");
				code.append(LineOptionCode(geo,true));
				code.append("(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(kernel.format(x2));
				code.append(")(");
				code.append(kernel.format(diameter));
				code.append(",");
				code.append(kernel.format(diameter));
				code.append(")\n");
			}
        }
   		int deco=geo.decorationType;
		if (deco!=GeoElement.DECORATION_NONE) markAngle(geo,r,m,angSt,angExt);

    }
    private void drawArc(GeoAngle geo,double[] vertex,double angSt, double angEnd,double r ){
		code.append("\\parametricplot");
		code.append(LineOptionCode(geo,false));
		code.append("{");
		code.append(angSt);
		code.append("}{");
		code.append(angEnd);
		code.append("}{");
		code.append(kernel.format(r));
		code.append("*cos(t)+");
		code.append(kernel.format(vertex[0]));
		code.append("|");
		code.append(kernel.format(r));
		code.append("*sin(t)+");
		code.append(kernel.format(vertex[1]));
		code.append("}\n");

    }
	private void drawTick(GeoAngle geo,double[] vertex,double angle){
		angle=-angle;
		double radius=geo.getArcSize();
		double diff= 2.5 + geo.lineThickness / 4d;
		double x1=euclidianView.toRealWorldCoordX(vertex[0]+(radius-diff)*Math.cos(angle));
		double x2=euclidianView.toRealWorldCoordX(vertex[0]+(radius+diff)*Math.cos(angle));
		double y1=euclidianView.toRealWorldCoordY(vertex[1]+(radius-diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		double y2=euclidianView.toRealWorldCoordY(vertex[1]+(radius+diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		code.append("\\psline");
		code.append(LineOptionCode(geo,false));
		code.append("(");
		code.append(kernel.format(x1));
		code.append(",");
		code.append(kernel.format(y1));
		code.append(")(");
		code.append(kernel.format(x2));
		code.append(",");
		code.append(kernel.format(y2));
		code.append(")\n");

		
	}
    private void markAngle(GeoAngle geo,double r, double[] vertex,double  angSt,double angEnd){
    	double rdiff;
    	switch(geo.decorationType){
    		case GeoElement.DECORATION_ANGLE_TWO_ARCS:
    			rdiff = 4 + geo.lineThickness/2d;
    			drawArc(geo,vertex,angSt,angEnd,r);
    			r-=rdiff/euclidianView.getXscale();
    			drawArc(geo,vertex,angSt,angEnd,r);
    		break;
    		case GeoElement.DECORATION_ANGLE_THREE_ARCS:
    			rdiff = 4 + geo.lineThickness/2d;
    			drawArc(geo,vertex,angSt,angEnd,r);
    			r-=rdiff/euclidianView.getXscale();
    			drawArc(geo,vertex,angSt,angEnd,r);
    			r-=rdiff/euclidianView.getXscale();
    			drawArc(geo,vertex,angSt,angEnd,r);
    		break;
    		case GeoElement.DECORATION_ANGLE_ONE_TICK:
    			drawArc(geo,vertex,angSt,angEnd,r);
    			euclidianView.toScreenCoords(vertex);
    			drawTick(geo,vertex,(angSt+angEnd)/2);
    			
    		break;
    		case GeoElement.DECORATION_ANGLE_TWO_TICKS:
    			drawArc(geo,vertex,angSt,angEnd,r);
    			euclidianView.toScreenCoords(vertex);	
    			double angleTick[] =new double[2];
    			angleTick[0]=(2*angSt+3*angEnd)/5;
    			angleTick[1]=(3*angSt+2*angEnd)/5;
				if (Math.abs(angleTick[1]-angleTick[0])>DrawAngle.MAX_TICK_DISTANCE){
					angleTick[0]=(angSt+angEnd)/2-DrawAngle.MAX_TICK_DISTANCE/2;
					angleTick[1]=(angSt+angEnd)/2+DrawAngle.MAX_TICK_DISTANCE/2;
				}

    			drawTick(geo,vertex,angleTick[0]);
    			drawTick(geo,vertex,angleTick[1]);
    		break;
    		case GeoElement.DECORATION_ANGLE_THREE_TICKS:
    			drawArc(geo,vertex,angSt,angEnd,r);
    			euclidianView.toScreenCoords(vertex);
    			angleTick=new double[2];
    			angleTick[0]=(5*angSt+3*angEnd)/8;
    			angleTick[1]=(3*angSt+5*angEnd)/8;
				if (Math.abs(angleTick[1]-angleTick[0])>DrawAngle.MAX_TICK_DISTANCE){
					angleTick[0]=(angSt+angEnd)/2-DrawAngle.MAX_TICK_DISTANCE/2;
					angleTick[1]=(angSt+angEnd)/2+DrawAngle.MAX_TICK_DISTANCE/2;
				}
    			drawTick(geo,vertex,(angSt+angEnd)/2);
    			drawTick(geo,vertex,angleTick[0]);
    			drawTick(geo,vertex,angleTick[1]);
    		break;
    	}
    }
    private void drawSlider(GeoNumeric geo){
    	boolean horizontal=geo.isSliderHorizontal();
    	double max=geo.getIntervalMax();
    	double min=geo.getIntervalMin();
    	double value=geo.getValue();
    	double width=geo.getSliderWidth();
    	double x=geo.getSliderX();
    	double y=geo.getSliderY();
    	
    	// start point of horizontal line for slider
    	if (geo.isAbsoluteScreenLocActive()) {
    		x = euclidianView.toRealWorldCoordX(x);
    		y = euclidianView.toRealWorldCoordY(y);
    		width = horizontal ? width / euclidianView.getXscale() :
    					width / euclidianView.getYscale();
    	}
        // create point for slider
        GeoPoint geoPoint = new GeoPoint(construction);
        geoPoint.objColor=geo.getObjectColor();
        String label="$"+Util.toLaTeXString(geo.getLabelDescription(),true)+"$";
        geoPoint.setLabel(label);
    	double param =  (value - min) / (max - min);
    	geoPoint.pointSize = 2 + (geo.lineThickness+1) / 3;  
    	geoPoint.setLabelVisible(geo.isLabelVisible());
    	if (horizontal) geoPoint.setCoords(x+width*param, y, 1.0);
    	else geoPoint.setCoords(x, y+width* param, 1.0);
        DrawPoint drawPoint = new DrawPoint(euclidianView, geoPoint);
        drawPoint.setGeoElement(geo);
    	if (geo.isLabelVisible()) {
    		if (horizontal){
    			drawPoint.xLabel -= 15;
    			drawPoint.yLabel -= 5;
    		}
    		else {
    			drawPoint.xLabel += 5;
        		drawPoint.yLabel += 2*geoPoint.pointSize + 4;	
    		}
    	}
    	drawGeoPoint(geoPoint);
    	drawLabel(geoPoint,drawPoint);
    	
    	//draw Line dor Slider
    	code.append("\\psline");
    	code.append(LineOptionCode(geo,true));
    	code.append("(");
    	code.append(kernel.format(x));
    	code.append(",");
    	code.append(kernel.format(y));
    	code.append(")(");
    	if (horizontal) x+=width;
    	else y+=width;
    	code.append(kernel.format(x));
    	code.append(",");
    	code.append(kernel.format(y));
    	code.append(")\n");    	
    }
    
    
    private void drawPolygon(GeoPolygon geo){
    	// command: \pspolygon[par](x0,y0)....(xn,yn)
    	float alpha=geo.getAlphaValue();
    	if (alpha==0.0f) return;
    	codeFilledObject.append("\\pspolygon");
    	codeFilledObject.append(LineOptionCode(geo,true));
    	GeoPoint [] points = geo.getPoints();	
    	for (int i=0;i<points.length;i++){
    		double x=points[i].getX();
    		double y=points[i].getY();
    		double z=points[i].getZ();
     		x=x/z;
    		y=y/z;
     		codeFilledObject.append("(");
    		codeFilledObject.append(kernel.format(x));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y));
    		codeFilledObject.append(")");
    	}
    	codeFilledObject.append("\n");	
    }
    
	private void drawText(GeoText geo){
		boolean isLatex=geo.isLaTeX();
		String st=geo.getTextString();
		Color geocolor=geo.getObjectColor();
		int style=geo.getFontStyle();
		int size=geo.getFontSize()+app.getFontSize();
		GeoPoint gp;
		double x,y;
	      // compute location of text		
		if (geo.isAbsoluteScreenLocActive()) {
			x = geo.getAbsoluteScreenLocX();
			y = geo.getAbsoluteScreenLocY(); 
		} else {
			gp = geo.getStartPoint();
	        if (gp == null) {
				x = (int) euclidianView.getXZero();
				y = (int) euclidianView.getYZero();
	        } else {
	        	if (!gp.isDefined()) {
	        		return;
	        	}
				x = euclidianView.toScreenCoordX(gp.inhomX);
				y = euclidianView.toScreenCoordY(gp.inhomY);        	
	        }
	        x += geo.labelOffsetX;
			y += geo.labelOffsetY; 
		}
		x=euclidianView.toRealWorldCoordX(x);
		y=euclidianView.toRealWorldCoordY(y-euclidianView.getFont().getSize());
		int id=st.indexOf("\n");
		// One line
		if (id==-1){
			code.append("\\rput[tl](");
			code.append(kernel.format(x));
			code.append(",");
			code.append(kernel.format(y));
			code.append("){");
			addText(st,isLatex,style,size,geocolor);
			code.append("}\n");
		}
		// MultiLine
		else {
			StringBuffer sb=new StringBuffer();
			StringTokenizer stk=new StringTokenizer(st,"\n");
			int width=0;
			Font font = new Font(geo.isSerifFont() ? "Serif" : "SansSerif", style, size);
			FontMetrics fm=euclidianView.getFontMetrics(font);
			while (stk.hasMoreTokens()){
				String line=stk.nextToken();
				width=Math.max(width,fm.stringWidth(line));		
				sb.append(line);
				if (stk.hasMoreTokens()) sb.append(" \\\\ ");
			}
			code.append("\\rput[lt](");
			code.append(kernel.format(x));
			code.append(",");
			code.append(kernel.format(y));
			code.append("){\\parbox{");
			code.append(kernel.format(width*(xmax-xmin)*xunit/euclidianView.getWidth()+1));
			code.append(" cm}{");
			addText(new String(sb),isLatex,style,size,geocolor);
			code.append("}}\n");	
		}
	}
	private void addText(String st,boolean isLatex,int style,int size,Color geocolor){
		if (isLatex)code.append("$");
		switch(style){
			case 1:
				if (isLatex) code.append("\\mathbf{");
				else code.append("\\textbf{");
			break;
			case 2:
				if (isLatex) code.append("\\mathit{");
				else code.append("\\textit{");
			break;
			case 3:
				if (isLatex) code.append("\\mathit{\\mathbf{");
				else code.append("\\textit{\\textbf{");
			break;
		}
		if (!geocolor.equals(Color.BLACK)){
			code.append("\\");
			ColorCode(geocolor,code);
			code.append("{");
		}
/*
		if (size!=app.getFontSize()) {
			String formatFont=resizeFont(size);
			if (null!=formatFont) code.append(formatFont);
		}*/
		code.append(st);
//		if (size!=app.getFontSize()) code.append("}");
		if (!geocolor.equals(Color.BLACK)){
			code.append("}");
		}
		switch(style){
			case 1:
			case 2:
				code.append("}");
				break;
			case 3:
				code.append("}}");
				break;
		}
		if (isLatex)code.append("$");
	}
	
	private void drawGeoConicPart(GeoConicPart geo){
		double x=geo.getTranslationVector().getX();
		double y=geo.getTranslationVector().getY();
		double r=geo.getHalfAxes()[0];
		double startAngle=geo.getParameterStart();
		double endAngle=geo.getParameterEnd();
		if (xunit==yunit){
			startAngle=Math.toDegrees(startAngle);
			endAngle=Math.toDegrees(endAngle);
		//	Sector command:   \pswedge*[par](x0,y0){radius}{angle1}{angle2}
		//	 Arc command:	\psarc*[par]{arrows}(x,y){radius}{angleA}{angleB}
			if (geo.getConicPartType()==GeoConicPart.CONIC_PART_SECTOR){
				code.append("\\pswedge");
			}
			else if (geo.getConicPartType()==GeoConicPart.CONIC_PART_ARC){
				code.append("\\psarc");
			}
			code.append(LineOptionCode(geo,true));
			code.append("(");
			code.append(kernel.format(x));
			code.append(",");
			code.append(kernel.format(y));
			code.append("){");
			code.append(kernel.format(r*xunit));
			code.append("}{");
			code.append(kernel.format(startAngle));
			code.append("}{");
			code.append(kernel.format(endAngle));
			code.append("}\n");
		}	
		else {
//Sector command: \pscustom[options]{\parametricplot{startAngle}{endAngle}{x+r*cos(t),y+r*sin(t)}\lineto(x,y)\closepath}
			if (geo.getConicPartType()==GeoConicPart.CONIC_PART_SECTOR){
				code.append("\\pscustom");
				code.append(LineOptionCode(geo,true));
				code.append("{\\parametricplot{");
			}
			else if (geo.getConicPartType()==GeoConicPart.CONIC_PART_ARC){
				code.append("\\parametricplot");
				code.append(LineOptionCode(geo,true));
				code.append("{");
			}
			code.append(startAngle);
			code.append("}{");
			code.append(endAngle);
			code.append("}{");
			code.append(kernel.format(r));
			code.append("*cos(t)+");
			code.append(kernel.format(x));
			code.append("|");
			code.append(kernel.format(r));
			code.append("*sin(t)+");
			code.append(kernel.format(y));
			code.append("}");
			if (geo.getConicPartType()==GeoConicPart.CONIC_PART_SECTOR){				
				code.append("\\lineto(");
				code.append(kernel.format(x));
				code.append(",");
				code.append(kernel.format(y));
				code.append(")\\closepath}");
			}
			code.append("\n");
		}
	}
	private void drawFunction(GeoFunction geo){
		Function f=geo.getFunction();
		if (null==f) return;
		String value=f.toValueString();
		value=killSpace(Util.toLaTeXString(value,true));
		double a=xmin;
		double b=xmax;
		if (f.hasInterval()) {
			a=Math.max(a,f.getIntervalMin());
			b=Math.min(b,f.getIntervalMax());
		}
		double xrangemax=a,xrangemin=a;
		while (xrangemax<b){
			xrangemin=firstDefinedValue(f,a,b);
//			System.out.println("xrangemin "+xrangemin);
			if (xrangemin==b) break;
			xrangemax=maxDefinedValue(f,xrangemin,b);
//			System.out.println("xrangemax "+xrangemax);
			code.append("\\psplot");
			code.append(LineOptionCode(geo,true));
			int index=code.lastIndexOf("]");
			if (index==code.length()-1){
				code.deleteCharAt(index);
				code.append(",plotpoints=200]{");
			}
			else code.append("[plotpoints=200]{");
			code.append(xrangemin);
			code.append("}{");
			code.append(xrangemax);
			code.append("}{");
			code.append(value);
			code.append("}\n");
			xrangemax+=PRECISION_XRANGE_FUNCTION;
			a=xrangemax;
		}
	}
/* We have to rewrite the function
 - Kill spaces
 - add character * when needed
 - rename several functions:
 		log(x)  ---> ln(x)
 		ceil(x) ---> ceiling(x)
 		exp(x)  ---> 2.71828^(x)
  
**/
	private String killSpace(String name){
//		2  x +3 ----> 2*x+3
		StringBuffer sb=new StringBuffer();
		boolean operand=false;
		boolean space=false;
		for (int i=0;i<name.length();i++){
			char c=name.charAt(i);
			if ("*/+-".indexOf(c)!=-1){
				sb.append(c);
				operand=true;
				space=false;
			}
			else if (c==' ') {
				if (!operand) space=true;
				else {
					space=false;
					operand=false;
				}
			}
			else {
				if (space) sb.append("*");
				sb.append(c);
				space=false;
				operand=false;
			}
		}
		// rename functions log, ceil and exp
		renameFunc(sb,"log(","ln(");
		renameFunc(sb,"ceil(","ceiling(");
		renameFunc(sb,"exp(","EXP(");
		renameFunc(sb,"atan(","ATAN(");
		renameFunc(sb,"cosh(","COSH(");
		renameFunc(sb,"acosh(","ACOSH(");
		renameFunc(sb,"asinh(","ASINH(");
		renameFunc(sb,"atanh(","ATANH(");
		renameFunc(sb,"sinh(","SINH(");
		renameFunc(sb,"tanh(","TANH(");
		
		return new String(sb);
	}
	private void renameFunc(StringBuffer sb,String nameFunc,String nameNew){
		int ind=sb.indexOf(nameFunc);
		while(ind>-1){
			sb.replace(ind,ind+nameFunc.length(),nameNew);
			ind=sb.indexOf(nameFunc);
		}
	}
	private double maxDefinedValue(Function f,double a,double b){
		double x=a;
		double step=(b-a)/100;
		while(x<=b){
			double y=f.evaluate(x);
			if (Double.isNaN(y)){
				if (step<PRECISION_XRANGE_FUNCTION) return x-step;
				else return maxDefinedValue(f,x-step,x);
			}
			x+=step;
		}
		return b;
	}
	private double firstDefinedValue(Function f,double a,double b){
		double x=a;
		double step=(b-a)/100;
		while(x<=b){
			double y=f.evaluate(x);
			if (!Double.isNaN(y)){
				if (x==a) return a;
				else if (step<PRECISION_XRANGE_FUNCTION) return x;
				else return firstDefinedValue(f,x-step,x);
			}
			x+=step;
		}
		return b;
	}
	private void drawGeoVector(GeoVector geo){
		GeoPoint pointStart=geo.getStartPoint();
		String x1,y1;
		if (null==pointStart){
			x1="0";y1="0";
		}
		else {
			x1=kernel.format(pointStart.getX()/pointStart.getZ());
			y1=kernel.format(pointStart.getY()/pointStart.getZ());
		}
		double[] coord=new double[3];
		geo.getCoords(coord);
		String x2=kernel.format(coord[0]+Double.parseDouble(x1));
		String y2=kernel.format(coord[1]+Double.parseDouble(y1));
	
		code.append("\\psline");
		code.append(LineOptionCode(geo,true));
		code.append("{->}(");
		code.append(x1);
		code.append(",");
		code.append(y1);
		code.append(")(");
		code.append(x2);
		code.append(",");
		code.append(y2);
		code.append(")\n");
	}
	private void drawCircle(GeoConic geo){
		if (xunit==yunit){
	// draw a circle
	//	 command:  \pscircle[options](x_center,y_center){Radius)}
			double x=geo.getTranslationVector().getX();
			double y=geo.getTranslationVector().getY();
			double r=geo.getHalfAxes()[0];
			code.append("\\pscircle");
			code.append(LineOptionCode(geo,true));
			code.append("(");
			code.append(kernel.format(x));
			code.append(",");
			code.append(kernel.format(y));
			code.append("){");
			String tmpr=kernel.format(r*xunit);
			if (Double.parseDouble(tmpr)!=0) code.append(tmpr);
			else code.append(r);
			code.append("}\n");
		}
		else {
		// draw an ellipse
		// command:  \psellipse(0,0)(20.81,-10.81)}
			double x1=geo.getTranslationVector().getX();
			double y1=geo.getTranslationVector().getY();
			double r1=geo.getHalfAxes()[0];
			double r2=geo.getHalfAxes()[1];
			code.append("\\psellipse");
			code.append(LineOptionCode(geo,true));
			code.append("(");
			code.append(kernel.format(x1));
			code.append(",");
			code.append(kernel.format(y1));
			code.append(")(");
			code.append(kernel.format(r1));
			code.append(",");
			code.append(kernel.format(r2));
			code.append(")\n");
		}
	}
	private void drawGeoConic(GeoConic geo){	
		switch(geo.getType()){
		// if conic is a circle
			case GeoConic.CONIC_CIRCLE:
				drawCircle(geo);
			break;
			// if conic is an ellipse
			case GeoConic.CONIC_ELLIPSE:
//	command:  \rput{angle}(x_center,y_center){\psellipse(0,0)(20.81,-10.81)}
				AffineTransform at=geo.getAffineTransform();
				double eigenvecX=at.getScaleX();
				double eigenvecY=at.getShearY();
				double x1=geo.getTranslationVector().getX();
				double y1=geo.getTranslationVector().getY();
				double r1=geo.getHalfAxes()[0];
				double r2=geo.getHalfAxes()[1];
				double angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
				code.append("\\rput{");
				code.append(kernel.format(angle));
				code.append("}(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(kernel.format(y1));
				code.append("){\\psellipse");
				code.append(LineOptionCode(geo,true));
				code.append("(0,0)(");
				code.append(kernel.format(r1));
				code.append(",");
				code.append(kernel.format(r2));
				code.append(")}\n");
			break;
			
		// if conic is a parabola 
			case GeoConic.CONIC_PARABOLA:
// command: \rput{angle_rotation}(x_origin,y_origin){\pstplot{xmin}{xmax}{x^2/2/p}}
				
				// parameter of the parabola
				double p=geo.p;
				at=geo.getAffineTransform();
				// first eigenvec
				eigenvecX=at.getScaleX();
				eigenvecY=at.getShearY();
				// vertex
				x1=geo.getTranslationVector().getX();
				y1=geo.getTranslationVector().getY();
				
				// calculate the x range to draw the parabola
				double x0 = Math.max( Math.abs(x1-xmin), Math.abs(x1-xmax) );
		        x0 = Math.max(x0, Math.abs(y1 - ymin));
				x0 = Math.max(x0, Math.abs(y1 - ymax));
		        /*
		        x0 *= 2.0d;
		        // y� = 2px
		        y0 = Math.sqrt(2*c.p*x0);
		        */
		        
		        // avoid sqrt by choosing x = k*p with         
		        // i = 2*k is quadratic number
		        // make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
		        x0 = 4*x0/p;
		        int i = 4; 
		        int k2 = 16;
		        while (k2 < x0) {
		            i += 2;
		            k2 = i * i;
		        }
		        //x0 = k2/2 * p; // x = k*p
		        x0 = i * p;    // y = sqrt(2k p^2) = i p
				angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX))-90;
				code.append("\\rput{");
				code.append(kernel.format(angle));
				code.append("}(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(kernel.format(y1));
				code.append("){\\psplot");
				code.append(LineOptionCode(geo,true));
				code.append("{");
				code.append(kernel.format(-x0));
				code.append("}{");
				code.append(kernel.format(x0));
				code.append("}");
				code.append("{x^2/2/");
				code.append(kernel.format(p));
				code.append("}}\n");
			break;
			case GeoConic.CONIC_HYPERBOLA:
// command: \rput{angle_rotation}(x_origin,y_origin){\parametric{-1}{1}{a(1+t^2)/(1-t^2)|2bt/(1-t^2)}
				at=geo.getAffineTransform();
				eigenvecX=at.getScaleX();
				eigenvecY=at.getShearY();
				x1=geo.getTranslationVector().getX();
				y1=geo.getTranslationVector().getY();
				r1=geo.getHalfAxes()[0];
				r2=geo.getHalfAxes()[1];
				angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
				code.append("\\rput{");
				code.append(kernel.format(angle));
				code.append("}(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(kernel.format(y1));
				code.append("){\\parametricplot");
				code.append(LineOptionCode(geo,true));
				code.append("{-0.99}{0.99}{");
				code.append(kernel.format(r1));
				code.append("*(1+t^2)/(1-t^2)|");
				code.append(kernel.format(r2));
				code.append("*2*t/(1-t^2)");
				code.append("}}\n");
        		
				code.append("\\rput{");
				code.append(kernel.format(angle));
				code.append("}(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(kernel.format(y1));
				code.append("){\\parametricplot");
				code.append(LineOptionCode(geo,true));
				code.append("{-0.99}{0.99}{");
				code.append(kernel.format(r1));
				code.append("*(-1-t^2)/(1-t^2)|");
				code.append(kernel.format(r2));
				code.append("*(-2)*t/(1-t^2)");
				code.append("}}\n");
				break;
		}	
	}
	
	private void drawGeoPoint(GeoPoint gp){
		if (frame.getExportPointSymbol()){
			double x=gp.getX();
			double y=gp.getY();
			double z=gp.getZ();
			x=x/z;
			y=y/z;
			codePoint.append("\\psdots");
			PointOptionCode(gp);
			codePoint.append("(");
			codePoint.append(kernel.format(x));
			codePoint.append(",");
			codePoint.append(kernel.format(y));
			codePoint.append(")\n");
		}
	}
	private void drawGeoLine(GeoLine geo){
		double x=geo.getX();
		double y=geo.getY();
		double z=geo.getZ();
		if (y!=0)code.append("\\psplot");
		else code.append("\\psline");
		code.append(LineOptionCode(geo,true));
		if (y!=0){	
			code.append("{");
			code.append(kernel.format(xmin));
			code.append("}{");
			code.append(kernel.format(xmax));
			code.append("}{(-");
			code.append(kernel.format(z));
			code.append("-");
			code.append(kernel.format(x));
			code.append("*x)/");
			String tmpy=kernel.format(y);
			if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			else code.append(y);
			code.append("}\n");			
		}
		else {
				String s=kernel.format(-z/x);
				code.append("(");
				code.append(s);
				code.append(",");
				code.append(kernel.format(ymin));
				code.append(")(");
				code.append(s);
				code.append(",");
				code.append(kernel.format(ymax));
				code.append(")\n");
		}
	}
	private void drawGeoSegment(GeoSegment geo){
		double[] A=new double[2];
		double[] B=new double[2];
		GeoPoint pointStart=geo.getStartPoint();
		GeoPoint pointEnd=geo.getEndPoint();
		pointStart.getInhomCoords(A);
		pointEnd.getInhomCoords(B);
		String x1=kernel.format(A[0]);
		String y1=kernel.format(A[1]);
		String x2=kernel.format(B[0]);
		String y2=kernel.format(B[1]);
		code.append("\\psline");
		code.append(LineOptionCode(geo,true));
		code.append("(");
		code.append(x1);
		code.append(",");
		code.append(y1);
		code.append(")(");
		code.append(x2);
		code.append(",");
		code.append(y2);
		code.append(")\n");
		int deco=geo.decorationType;
		if (deco!=GeoElement.DECORATION_NONE) mark(A,B,deco,geo);
	}
	private void drawLine(double x1,double y1,double x2,double y2,GeoElement geo){
		String sx1=kernel.format(x1);
		String sy1=kernel.format(y1);
		String sx2=kernel.format(x2);
		String sy2=kernel.format(y2);
		code.append("\\psline");
		code.append(LineOptionCode(geo,true));
		code.append("(");
		code.append(sx1);
		code.append(",");
		code.append(sy1);
		code.append(")(");
		code.append(sx2);
		code.append(",");
		code.append(sy2);
		code.append(")\n");
		
	}
	private void mark(double[] A, double[] B,int deco,GeoElement geo){
		// calc midpoint (midX, midY) and perpendicular vector (nx, ny)
		euclidianView.toScreenCoords(A);
		euclidianView.toScreenCoords(B);					
		double midX = (A[0] + B[0])/ 2.0;
		double midY = (A[1] + B[1])/ 2.0;			
		double nx = A[1] - B[1]; 			
		double ny = B[0] - A[0];		
		double nLength = GeoVec2D.length(nx, ny);			
		// tick spacing and length.
		double tickSpacing = 2.5 + geo.lineThickness/2d;
		double tickLength =  tickSpacing + 1;	
//		 Michael Borcherds 20071006 start
		double arrowlength = 1.5;
//		 Michael Borcherds 20071006 end
		double vx, vy, factor,x1,x2,y1,y2;
		switch(deco){
		case GeoElement.DECORATION_SEGMENT_ONE_TICK:
			factor = tickLength / nLength;
			nx *= factor/xunit;
			ny *= factor/yunit;
			x1=euclidianView.toRealWorldCoordX(midX - nx);
			y1=euclidianView.toRealWorldCoordY(midY - ny);
			x2=euclidianView.toRealWorldCoordX(midX + nx);
			y2=euclidianView.toRealWorldCoordY(midY + ny);
	 		drawLine(x1,y1,x2,y2,geo);
	 	break;
	 	case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
	 		// vector (vx, vy) to get 2 points around midpoint		
	 		factor = tickSpacing / (2 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX + vx - nx);
	 		x2=euclidianView.toRealWorldCoordX(midX + vx + nx);
	 		y1=euclidianView.toRealWorldCoordY(midY + vy - ny);
	 		y2=euclidianView.toRealWorldCoordY(midY + vy + ny);
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - vx - nx);
	 		x2=euclidianView.toRealWorldCoordX(midX - vx + nx);
	 		y1=euclidianView.toRealWorldCoordY(midY - vy - ny);
	 		y2=euclidianView.toRealWorldCoordY(midY - vy + ny);
	 		drawLine(x1,y1,x2,y2,geo);
	 	break;
	 	case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
	 		// vector (vx, vy) to get 2 points around midpoint				 		
	 		factor = tickSpacing / nLength;		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX + vx - nx);
	 		x2=euclidianView.toRealWorldCoordX(midX + vx + nx);
	 		y1=euclidianView.toRealWorldCoordY(midY + vy - ny);
	 		y2=euclidianView.toRealWorldCoordY(midY + vy + ny);
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - nx);
	 		x2=euclidianView.toRealWorldCoordX(midX + nx);
	 		y1=euclidianView.toRealWorldCoordY(midY - ny);
	 		y2=euclidianView.toRealWorldCoordY(midY + ny);
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - vx - nx);
	 		x2=euclidianView.toRealWorldCoordX(midX - vx + nx);
	 		y1=euclidianView.toRealWorldCoordY(midY - vy - ny);
	 		y2=euclidianView.toRealWorldCoordY(midY - vy + ny);
	 		drawLine(x1,y1,x2,y2,geo);
	 	break;
// Michael Borcherds 20071006 start
		case GeoElement.DECORATION_SEGMENT_ONE_ARROW:
	 		// vector (vx, vy) to get 2 points around midpoint		
	 		factor = tickSpacing / (1.5 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / (1.5 * nLength);
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX - arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - arrowlength*vy);
			x2=euclidianView.toRealWorldCoordX(midX - arrowlength*vx + arrowlength*(nx + vx));
			y2=euclidianView.toRealWorldCoordY(midY - arrowlength*vy + arrowlength*(ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX - arrowlength*vx + arrowlength*(-nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY - arrowlength*vy + arrowlength*(-ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 	break;
	 	case GeoElement.DECORATION_SEGMENT_TWO_ARROWS:
	 		// vector (vx, vy) to get 2 points around midpoint		
	 		factor = tickSpacing / (1.5 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / (1.5 * nLength);
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX - 2*arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - 2*arrowlength*vy);
			x2=euclidianView.toRealWorldCoordX(midX - 2*arrowlength*vx + arrowlength*(nx + vx));
			y2=euclidianView.toRealWorldCoordY(midY - 2*arrowlength*vy + arrowlength*(ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - 2*arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - 2*arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX - 2*arrowlength*vx + arrowlength*(-nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY - 2*arrowlength*vy + arrowlength*(-ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
			
	 		x1=euclidianView.toRealWorldCoordX(midX);
	 		y1=euclidianView.toRealWorldCoordY(midY);
	 		x2=euclidianView.toRealWorldCoordX(midX + arrowlength*(nx + vx));
			y2=euclidianView.toRealWorldCoordY(midY + arrowlength*(ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX);
	 		y1=euclidianView.toRealWorldCoordY(midY);
	 		x2=euclidianView.toRealWorldCoordX(midX + arrowlength*(-nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY + arrowlength*(-ny + vy));
	 		drawLine(x1,y1,x2,y2,geo);
	 	break;
	 	case GeoElement.DECORATION_SEGMENT_THREE_ARROWS:
	 		// vector (vx, vy) to get 2 points around midpoint				 		
	 		factor = tickSpacing / (1.5 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / (1.5 * nLength);
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX - arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - arrowlength*vy);
			x2=euclidianView.toRealWorldCoordX(midX - arrowlength*vx + arrowlength*(nx + vx));
			y2=euclidianView.toRealWorldCoordY(midY - arrowlength*vy + arrowlength*(ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX - arrowlength*vx + arrowlength*(-nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY - arrowlength*vy + arrowlength*(-ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
			
	 		x1=euclidianView.toRealWorldCoordX(midX + arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY + arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX + arrowlength*vx + arrowlength*(nx + vx));
			y2=euclidianView.toRealWorldCoordY(midY + arrowlength*vy + arrowlength*(ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX + arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY + arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX + arrowlength*vx + arrowlength*(-nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY + arrowlength*vy + arrowlength*(-ny + vy));
	 		drawLine(x1,y1,x2,y2,geo);
			
	 		x1=euclidianView.toRealWorldCoordX(midX - 3*arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - 3*arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX - 3*arrowlength*vx + arrowlength*(nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY - 3*arrowlength*vy + arrowlength*(ny + vy));
	 		drawLine(x1,y1,x2,y2,geo);
	 		x1=euclidianView.toRealWorldCoordX(midX - 3*arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordY(midY - 3*arrowlength*vy);
	 		x2=euclidianView.toRealWorldCoordX(midX - 3*arrowlength*vx + arrowlength*(-nx + vx));
	 		y2=euclidianView.toRealWorldCoordY(midY - 3*arrowlength*vy + arrowlength*(-ny + vy));	
	 		drawLine(x1,y1,x2,y2,geo);
	 	break;
//	  Michael Borcherds 20071006 end
	 }
	}
	private void drawGeoRay(GeoRay geo){
		GeoPoint pointStart=geo.getStartPoint();
		
		
		double x1=pointStart.getX();
		double z1=pointStart.getZ();
		x1=x1/z1;
		String y1=kernel.format(pointStart.getY()/z1);
		
		double x=geo.getX();
		double y=geo.getY();
		double z=geo.getZ();

		if (y!=0)code.append("\\psplot");
		else code.append("\\psline");
		code.append(LineOptionCode(geo,true));
		double inf=xmin,sup=xmax;
		if (y>0){
			inf=x1;
		}
		else {
			sup=x1;
		}
		if (y!=0){	
			code.append("{");
			code.append(kernel.format(inf));
			code.append("}{");
			code.append(kernel.format(sup));
			code.append("}{(-");
			code.append(kernel.format(z));
			code.append("-");
			code.append(kernel.format(x));
			code.append("*x)/");
			String tmpy=kernel.format(y);
			if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			else code.append(y);
			code.append("}\n");			
		}
		else {
				if (-x>0) sup=ymax;
				else sup=ymin;
				code.append("(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(y1);
				code.append(")(");
				code.append(kernel.format(x1));
				code.append(",");
				code.append(kernel.format(sup));
				code.append(")\n");
		}
	}
    
    private void initUnitAndVariable(){
		// Initaialze uits, dot style, dot size .... 
		codeBeginPic.append("\\psset{xunit=");
		codeBeginPic.append(sci2dec(xunit));
		codeBeginPic.append("cm,yunit=");
		codeBeginPic.append(sci2dec(yunit));
		codeBeginPic.append("cm,algebraic=true,dotstyle=");
		switch(euclidianView.getPointStyle()){
			case EuclidianView.POINT_STYLE_CIRCLE:
				codeBeginPic.append("o");
			break;
			case EuclidianView.POINT_STYLE_CROSS:
				codeBeginPic.append("+");
			break;
			case EuclidianView.POINT_STYLE_DOT:
				codeBeginPic.append("*");
			break;
		}
		codeBeginPic.append(",dotsize=");
		codeBeginPic.append(EuclidianView.DEFAULT_POINT_SIZE);
		codeBeginPic.append("pt 0");
		codeBeginPic.append(",linewidth=");
		codeBeginPic.append(kernel.format(EuclidianView.DEFAULT_LINE_THICKNESS/2*0.8));
		codeBeginPic.append("pt,arrowsize=3pt 2,arrowinset=0.25}\n");
    }
    
	// if label is Visible, draw it
	private void drawLabel(GeoElement geo,Drawable drawGeo){
		if (geo.isLabelVisible()){
				String name="$"+Util.toLaTeXString(geo.getLabelDescription(),true)+"$";
				if (null==drawGeo) drawGeo=euclidianView.getDrawableFor(geo);
				double xLabel=drawGeo.getxLabel();
				double yLabel=drawGeo.getyLabel();
				xLabel=euclidianView.toRealWorldCoordX(Math.round(xLabel));
				yLabel=euclidianView.toRealWorldCoordY(Math.round(yLabel));
				
				Color geocolor=geo.getObjectColor();
				codePoint.append("\\rput[bl](");
				codePoint.append(kernel.format(xLabel));
				codePoint.append(",");
				codePoint.append(kernel.format(yLabel));
				codePoint.append("){");
				if (!geocolor.equals(Color.BLACK)){
					codePoint.append("\\");
					ColorCode(geocolor,codePoint);
					codePoint.append("{");
				}
				codePoint.append(name);
				if (!geocolor.equals(Color.BLACK)){
					codePoint.append("}");
				}
				codePoint.append("}\n");
			}		
		}	

	private boolean isSinglePointConic(GeoElement geo){
		if (geo.isGeoConic()){
			if (((GeoConic)geo).getType()==GeoConic.CONIC_SINGLE_POINT) 
				return true;
		}
		return false;
	}
	private boolean isDoubleLineConic(GeoElement geo){
		if (geo.isGeoConic()){
			if (((GeoConic)geo).getType()==GeoConic.CONIC_DOUBLE_LINE
					|| ((GeoConic)geo).getType()==GeoConic.CONIC_INTERSECTING_LINES
					|| ((GeoConic)geo).getType()==GeoConic.CONIC_PARALLEL_LINES
					) 
				return true;
		}
		return false;
	}
	private boolean isEmpty(GeoElement geo){
		if (geo.isGeoConic()){
			if (((GeoConic)geo).getType()==GeoConic.CONIC_EMPTY	) 
				return true;
		}
		return false;	
	}
	
	
    // Draw the grid 
	private void drawGrid(){
		Color GridCol=euclidianView.getGridColor();
		double[] GridDist=euclidianView.getGridDistances();
		int GridLine=euclidianView.getGridLineStyle();

		// Set Units for grid
		codeBeginPic.append("\\psset{xunit=");
		codeBeginPic.append(sci2dec(GridDist[0]*xunit));
		codeBeginPic.append("cm,yunit=");
		codeBeginPic.append(sci2dec(GridDist[1]*yunit));
		codeBeginPic.append("cm}\n");
		
		// environment pspicture
		codeBeginPic.append("\\begin{pspicture*}(");
		codeBeginPic.append(kernel.format(xmin/GridDist[0]));
		codeBeginPic.append(",");
		codeBeginPic.append(kernel.format(ymin/GridDist[1]));
		codeBeginPic.append(")(");
		codeBeginPic.append(kernel.format(xmax/GridDist[0]));
		codeBeginPic.append(",");
		codeBeginPic.append(kernel.format(ymax/GridDist[1]));
		codeBeginPic.append(")\n");
		
		// Draw Grid
		codeBeginPic.append("\\psgrid[subgriddiv=0,gridlabels=0,gridcolor=");
		ColorCode(GridCol,codeBeginPic);
		codeBeginPic.append("](0,0)(");
		codeBeginPic.append(kernel.format(xmin/GridDist[0]));
		codeBeginPic.append(",");
		codeBeginPic.append(kernel.format(ymin/GridDist[1]));
		codeBeginPic.append(")(");
		codeBeginPic.append(kernel.format(xmax/GridDist[0]));
		codeBeginPic.append(",");
		codeBeginPic.append(kernel.format(ymax/GridDist[1]));
		codeBeginPic.append(")\n");
		

		
		// Set units for the pspicture
		initUnitAndVariable();
/*		code.append("\\psset{xunit=");
		code.append(xunit);
		code.append("cm,yunit=");
		code.append(yunit);
		code.append("cm}\n");*/

	}
	
	// Draw Axis
	private void drawAxis(){
		boolean xAxis=euclidianView.getShowXaxis();
		boolean yAxis=euclidianView.getShowYaxis();
//		\psaxes[Dx=5,Dy=0.5]{->}(0,0)(-10.5,-0.4)(10.5,1.2)
		double Dx=euclidianView.getAxesNumberingDistances()[0];
		double Dy=euclidianView.getAxesNumberingDistances()[1];
		codeBeginPic.append("\\psaxes[xAxis=");
		codeBeginPic.append(xAxis);
		codeBeginPic.append(",yAxis=");
		codeBeginPic.append(yAxis);
		codeBeginPic.append(',');
		boolean bx=euclidianView.getShowAxesNumbers()[0];
		boolean by=euclidianView.getShowAxesNumbers()[1];
		if (!bx&&!by) codeBeginPic.append("labels=none,");
		else if (bx&&!by) codeBeginPic.append("labels=x,");
		else if (!bx&&by) codeBeginPic.append("labels=y,");
		codeBeginPic.append("Dx=");
		codeBeginPic.append(kernel.format(Dx));
		codeBeginPic.append(",Dy=");
		codeBeginPic.append(kernel.format(Dy));
		codeBeginPic.append(",ticksize=-2pt 0,subticks=2");
		codeBeginPic.append("]{-");
		if (euclidianView.getAxesLineStyle()==EuclidianView.AXES_LINE_TYPE_ARROW)
		codeBeginPic.append(">");
		codeBeginPic.append("}(0,0)(");
		codeBeginPic.append(kernel.format(xmin));
		codeBeginPic.append(",");
		codeBeginPic.append(kernel.format(ymin));
		codeBeginPic.append(")(");
		codeBeginPic.append(kernel.format(xmax));
		codeBeginPic.append(",");
		codeBeginPic.append(kernel.format(ymax));
		codeBeginPic.append(")\n");
	}
	private void PointOptionCode(GeoPoint geo){
		Color dotcolor=geo.getObjectColor();
		int dotsize=geo.getPointSize();
		boolean coma=false;
		boolean bracket=false;
		if (dotsize!=EuclidianView.DEFAULT_POINT_SIZE){
			// coma needed
			coma=true;
			// bracket needed
			bracket=true;
			codePoint.append("[dotsize=");
			codePoint.append(dotsize);
			codePoint.append("pt 0");
		}
		if (!dotcolor.equals(Color.BLACK)){
			if (coma) codePoint.append(",");
			if (!bracket) codePoint.append("[");
			bracket=true;
			codePoint.append("linecolor=");
			ColorCode(dotcolor,codePoint);
		}
		if (bracket) codePoint.append("]");

	
	
	}
	private String LineOptionCode(GeoElement geo,boolean transparency){
		StringBuffer sb=new StringBuffer(); 
		Color linecolor=geo.getObjectColor();
		int linethickness=geo.getLineThickness();
		int linestyle=geo.getLineType();

	boolean coma=false;
	boolean bracket=false;
	if (linethickness!=EuclidianView.DEFAULT_LINE_THICKNESS){
		// coma needed
		coma=true;
		// bracket needed
		bracket=true;
		sb.append("[linewidth=");
		sb.append(kernel.format(linethickness/2.0*0.8));
		sb.append("pt");
	}
	if (linestyle!=EuclidianView.DEFAULT_LINE_TYPE){
		if (coma) sb.append(",");
		else coma=true;
		if (!bracket) sb.append("[");
		bracket=true;
		LinestyleCode(linestyle,sb);
	}
	if (!linecolor.equals(Color.BLACK)){
		if (coma) sb.append(",");
		else coma=true;
		if (!bracket) sb.append("[");
		bracket=true;
		sb.append("linecolor=");
		ColorCode(linecolor,sb);
	}
	if (transparency&&geo.isFillable()&&geo.getAlphaValue()>0.0f){
		defineTransparency();
		if (coma) sb.append(",");
		else coma=true;
		if (!bracket) sb.append("[");
		bracket=true;
		sb.append("fillcolor=");
		ColorCode(linecolor,sb);
		sb.append(",fillstyle=transp,transpalpha=");
		sb.append(geo.getAlphaValue());
	}
	if (bracket) sb.append("]");
	return new String(sb);
	}
	// Append the linestyle to PSTricks code
	private void LinestyleCode(int linestyle,StringBuffer sb){
		switch(linestyle){
			case EuclidianView.LINE_TYPE_DOTTED:
				sb.append("linestyle=dotted");
			break;
			case EuclidianView.LINE_TYPE_DASHED_SHORT:
//				sb.append("linestyle=dashed,dash=4pt 4pt");
				sb.append("linestyle=dashed,dash=");
				int size=resizePt(4);
				sb.append(size);
				sb.append("pt ");
				sb.append(size);
				sb.append("pt");
			break;
			case EuclidianView.LINE_TYPE_DASHED_LONG:
//				sb.append("linestyle=dashed,dash=8pt 8pt");
				sb.append("linestyle=dashed,dash=");
				size=resizePt(8);
				sb.append(size);
				sb.append("pt ");
				sb.append(size);
				sb.append("pt");
			break;
			case EuclidianView.LINE_TYPE_DASHED_DOTTED:
//				sb.append("linestyle=dashed,dash=1pt 4pt 8pt 4pt");
				sb.append("linestyle=dashed,dash=");
				int size1=resizePt(1);
				int size2=resizePt(4);
				int size3=resizePt(8);
				sb.append(size1);
				sb.append("pt ");
				sb.append(size2);
				sb.append("pt ");
				sb.append(size3);
				sb.append("pt ");
				sb.append(size2);
				sb.append("pt ");
			break;
		}
	}
	private int resizePt(int size){
		double height_geogebra=euclidianView.getHeight()/30;
		double height_latex=frame.getLatexHeight();
		double ratio=height_latex/height_geogebra;
		int tmp= (int)Math.round(ratio*size);
		if (tmp!=0) return tmp;
		else return 1;
	}
	// Append the name color to StringBuffer sb 
	public void ColorCode(Color c,StringBuffer sb){
	//	final String suffix="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if (c.equals(Color.BLACK)) sb.append("black");
		else if (c.equals(Color.DARK_GRAY)) sb.append("darkgray");
		else if (c.equals(Color.GRAY)) sb.append("gray");
		else if (c.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
		else if (c.equals(Color.WHITE)) sb.append("white");
		else if (c.equals(Color.RED)) sb.append("red");
		else if (c.equals(Color.GREEN)) sb.append("green");
		else if (c.equals(Color.BLUE)) sb.append("blue");
		else if (c.equals(Color.CYAN)) sb.append("cyan");
		else if (c.equals(Color.MAGENTA)) sb.append("magenta");
		else if (c.equals(Color.YELLOW)) sb.append("yellow");
		else {
			String colorname="";
			if (CustomColor.containsKey(c)){
				colorname=CustomColor.get(c).toString();
			}
			else {
				int red=c.getRed();
				int green=c.getGreen();
				int blue=c.getBlue();
				colorname=createSuffix((int)red,(int)green,(int)blue);
				codeBeginDoc.append("\\newrgbcolor{"+colorname+"}{"
						+kernel.format(red/255d)+" "
						+kernel.format(green/255d)+" "
						+kernel.format(blue/255d)+"}\n");
				CustomColor.put(c,colorname);
			}
			sb.append(colorname);
		}
	}
	String createSuffix(int red,int green,int blue){
		final String suff="qrstuvwxyzabcdef";
		int[] nb={red,green,blue};
		StringBuffer sb=new StringBuffer();
		for (int i=0;i<nb.length;i++){
			int quot=nb[i]/16;
			int reste=nb[i]%16;
			sb.append(suff.charAt(quot));
			sb.append(suff.charAt(reste));
		}
		return new String(sb);
	}
/*	// Resize text 
	// Keep the ratio between font size and picture height
	private String resizeFont(int fontSize){
		int latexFont=frame.getFontSize();
		double height_geogebra=euclidianView.getHeight()/30;
		double height_latex=frame.getLatexHeight();
		double ratio=height_latex/height_geogebra;
		int theoric_size=(int)Math.round(ratio*fontSize);
		String st=null;
		switch(latexFont){
			case 10:
				if (theoric_size<=5) st="\\tiny{";
				else if (theoric_size<=7) st="\\scriptsize{";
				else if (theoric_size<=8) st="\\footnotesize{";
				else if (theoric_size<=9) st="\\small{";
				else if (theoric_size<=10) ;
				else if (theoric_size<=12) st="\\large{";
				else if (theoric_size<=14) st="\\Large{";
				else if (theoric_size<=17) st="\\LARGE{";
				else if (theoric_size<=20) st="\\huge{";
				else  st="\\Huge{";
			break;
			case 11:
				if (theoric_size<=6) st="\\tiny{";
				else if (theoric_size<=8) st="\\scriptsize{";
				else if (theoric_size<=9) st="\\footnotesize{";
				else if (theoric_size<=10) st="\\small{";
				else if (theoric_size<=11) ;
				else if (theoric_size<=12) st="\\large{";
				else if (theoric_size<=14) st="\\Large{";
				else if (theoric_size<=17) st="\\LARGE{";
				else if (theoric_size<=20) st="\\huge{";
				else  st="\\Huge{";
			break;
			case 12:
				if (theoric_size<=6) st="\\tiny{";
				else if (theoric_size<=8) st="\\scriptsize{";
				else if (theoric_size<=10) st="\\footnotesize{";
				else if (theoric_size<=11) st="\\small{";
				else if (theoric_size<=12) ;
				else if (theoric_size<=14) st="\\large{";
				else if (theoric_size<=17) st="\\Large{";
				else if (theoric_size<=20) st="\\LARGE{";
				else if (theoric_size<=25) st="\\huge{";
				else  st="\\Huge{";
			break;
		}
		return st;
	}*/
	private void defineTransparency(){
		String str="\\makeatletter\n\\define@key[psset]{}{transpalpha}{\\pst@checknum{#1}\\pstranspalpha}\n"+
		"\\psset{transpalpha=1}\n"+
		"\\def\\psfs@transp{%\n"+
		"  \\addto@pscode{/Normal .setblendmode \\pstranspalpha .setshapealpha }%\n"+
		"  \\psfs@solid}\n";
		if (!transparency) codePreamble.append(str);
		transparency=true;
	}
	
	
	// When The Button "generate PSTricks" has been clicked
	public void actionPerformed(ActionEvent e){
		this.createPstricks();
	}
	
	// refresh the selection rectangle when values change in TextField
	public void refreshSelectionRectangle(){
		int x1=euclidianView.toScreenCoordX(xmin);
		int x2=euclidianView.toScreenCoordX(xmax);
		int y1=euclidianView.toScreenCoordY(ymin);
		int y2=euclidianView.toScreenCoordY(ymax);
		Rectangle rec=new Rectangle(x1,y2,x2-x1,y1-y2);
	//		System.out.println(x1+" "+x2+" "+y1+" "+y2);
		euclidianView.setSelectionRectangle(rec);
		euclidianView.repaint();
	}
	//	convert a double with engineering noattion to decimal
	// eg: 3E-4 becomes 0.0003 
	private String sci2dec(double d){
		String s=String.valueOf(d).toLowerCase();
		StringTokenizer st=new StringTokenizer(s,"e");
		StringBuffer number;
		if (st.countTokens()==1) return s;
		else {
			String token1=st.nextToken();
			String token2=st.nextToken();
			number=new StringBuffer(token1);
			int exp=Integer.parseInt(token2);
			if (exp>0){
				int id_point=number.indexOf(".");
				if (id_point==-1){
					for (int i=0;i<exp;i++) number.append("0");
				}
				else{
					number.deleteCharAt(id_point);
					int zeros=exp-(number.length()-id_point);
					for (int i=0;i<zeros;i++) number.append("0");
				}
			}
			else {
				exp=-exp;
				int id_point=number.indexOf(".");
				number.deleteCharAt(id_point);
				for (int i=0;i<exp-1;i++) number.insert(0, "0");
				number.insert(0, "0.");
			}
		}
		return number.toString();
	}
}
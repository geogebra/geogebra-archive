/*
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.export.pstricks;
import geogebra.euclidian.DrawPoint;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AlgoAngleLines;
import geogebra.kernel.AlgoAnglePoints;
import geogebra.kernel.AlgoAngleVector;
import geogebra.kernel.AlgoAngleVectors;
import geogebra.kernel.AlgoBoxPlot;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoIntegralFunctions;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
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
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.arithmetic.Function;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
/**
 * @author Le Coq loïc
 */

public class GeoGebraToPstricks extends GeoGebraExport {
	private static final int FORMAT_BEAMER=1;
	private StringBuffer codeBeginPic;
	public GeoGebraToPstricks(Application app) {
    	super(app);

    }
    protected void createFrame(){
    	frame=new PstricksFrame(this);
    }
  
	 
    public void generateAllCode() {
       	format=((ExportFrame)frame).getFormat();
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
 		if (format==GeoGebraToPstricks.FORMAT_BEAMER){
 	    	codePreamble.append("\\documentclass[" +
 	    			frame.getFontSize()+"pt]{beamer}\n");
 		}
 		else{
 			codePreamble.append("\\documentclass[" +
 	    			frame.getFontSize()+"pt]{article}\n");
 		}
 		codePreamble.append("\\usepackage{pstricks-add}\n\\pagestyle{empty}\n");
     	codeBeginDoc.append("\\begin{document}\n");
 		if (format==GeoGebraToPstricks.FORMAT_BEAMER){
 	    	codeBeginDoc.append("\\begin{frame}\n");
 		}
    	// Draw Grid
		if (euclidianView.getShowGrid()) {
			drawGrid();
		}
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
			{
				drawAxis();
			}
		

		
/*		 get all objects from construction
 *   	 and "draw" them by creating pstricks code*/
		
		drawAllElements();
		/*
     	Object [] geos =
     		kernel.getConstruction().getGeoSetConstructionOrder().toArray();
     	for (int i=0;i<geos.length;i++){
        	GeoElement g = (GeoElement)(geos[i]);
           	drawGeoElement(g,false);		
//           	System.out.println(g+" "+beamerSlideNumber);
     	}*/
		
        // add code for Points and Labels
        code.append(codePoint);
        // Close Environment pspicture
		code.append("\\end{pspicture*}\n");
/*		String formatFont=resizeFont(app.getFontSize());
		if (null!=formatFont){
			codeBeginPic.insert(0,formatFont+"\n");
			code.append("}\n");
		}*/
		code.insert(0,codeFilledObject+"");
		code.insert(0,codeBeginPic+"");
        code.insert(0,codeBeginDoc+"");		
        code.insert(0,codePreamble+"");
        if (format==GeoGebraToPstricks.FORMAT_BEAMER){
 	    	code.append("\\end{frame}\n");
 		}
        code.append("\\end{document}");		
		frame.write(code);
	}	

    protected void drawLocus(GeoLocus g){
    	ArrayList ll=g.getMyPointList();
    	Iterator it=ll.iterator();
		startBeamer(code);
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
		endBeamer(code);
    }
    
    protected void drawBoxPlot(GeoNumeric geo){
    	AlgoBoxPlot algo=((AlgoBoxPlot)geo.getParentAlgorithm());
    	double y=algo.getA().getDouble();
    	double height=algo.getB().getDouble();
    	double [] lf=algo.getLeftBorders();
    	double min=lf[0];
    	double q1=lf[1];
    	double med=lf[2];
    	double q3=lf[3];
    	double max=lf[4];
    	startBeamer(code);
    	// Min vertical bar
		drawLine(min,y-height,min,y+height,geo);
		// Max vertical bar
		drawLine(max,y-height,max,y+height,geo);
		// Med vertical bar
		drawLine(med,y-height,med,y+height,geo);
		// Min-q1 horizontal
		drawLine(min,y,q1,y,geo);
		// q3-max
		drawLine(q3,y,max,y,geo);
    	endBeamer(code);
		// Rectangle q1-q3
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\psframe");
		codeFilledObject.append(LineOptionCode(geo,true));
		codeFilledObject.append("(");
		codeFilledObject.append(kernel.format(q1));
		codeFilledObject.append(",");
		codeFilledObject.append(y-height);
		codeFilledObject.append(")(");
		codeFilledObject.append(kernel.format(q3));
		codeFilledObject.append(",");
		codeFilledObject.append(kernel.format(y+height));
		codeFilledObject.append(")\n");
		endBeamer(codeFilledObject);
    }
    protected void drawHistogram(GeoNumeric geo){
    	AlgoFunctionAreaSums algo=(AlgoFunctionAreaSums)geo.getParentAlgorithm();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
		startBeamer(codeFilledObject);
        for (int i=0;i<x.length-1;i++){
    		codeFilledObject.append("\\psframe");
    		codeFilledObject.append(LineOptionCode(geo,true));
    		codeFilledObject.append("(");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",0)(");
    		codeFilledObject.append(kernel.format(x[i+1]));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i]));
    		codeFilledObject.append(")\n");
    		if (i!=x.length-2&&isBeamer) codeFilledObject.append("  ");
        }    	
		endBeamer(codeFilledObject);
    }
    
    protected void drawSumTrapezoidal(GeoNumeric geo){
       	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
    	int n=algo.getIntervals();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
		startBeamer(codeFilledObject);
       	for (int i=0;i<n;i++){
    		codeFilledObject.append("\\pspolygon");
    		codeFilledObject.append(LineOptionCode(geo,true));
    		codeFilledObject.append("(");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",0)(");
    		codeFilledObject.append(kernel.format(x[i+1]));
    		codeFilledObject.append(",0)(");
    		codeFilledObject.append(kernel.format(x[i+1]));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i+1]));
    		codeFilledObject.append(")(");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i]));
    		codeFilledObject.append(")\n");
    		if (i!=n-1&&isBeamer) codeFilledObject.append("  ");
    	}       
		endBeamer(codeFilledObject);
    }
    
    protected void drawSumUpperLower(GeoNumeric geo){
    	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
    	int n=algo.getIntervals();
        double step=algo.getStep();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
		startBeamer(codeFilledObject);
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
        		if (i!=n-1&&isBeamer) codeFilledObject.append("  ");
        }
		endBeamer(codeFilledObject);
    }
    protected void drawIntegralFunctions(GeoNumeric geo){
// command: \pscutom[option]{\pstplot{a}{b}{f(x)}\lineto(b,g(b))\pstplot{b}{a}{g(x)} \lineto(a,f(a))\closepath} 
   	AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo.getParentAlgorithm();
	// function f
	GeoFunction f = algo.getF();
	// function g
	GeoFunction g = algo.getG();
	// double a and b
	double a=algo.getA().getDouble();
	double b=algo.getB().getDouble();
	// String output for a and b
	String sa = kernel.format(a);
    String sb = kernel.format(b);
    // String Expression of f and g
    String valueF=f.toValueString();
	valueF=killSpace(Util.toLaTeXString(valueF,true));
    String valueG=g.toValueString();
	valueG=killSpace(Util.toLaTeXString(valueG,true));
	// String expressions for f(a) and g(b) 
	String fa=kernel.format(f.evaluate(a));
	String gb=kernel.format(g.evaluate(b));
	startBeamer(codeFilledObject);
	codeFilledObject.append("\\pscustom");
	codeFilledObject.append(LineOptionCode(geo,true));
	codeFilledObject.append("{\\psplot{");
	codeFilledObject.append(sa);
	codeFilledObject.append("}{");
	codeFilledObject.append(sb);
	codeFilledObject.append("}{");
	codeFilledObject.append(valueF);
	codeFilledObject.append("}\\lineto(");
	codeFilledObject.append(sb);
	codeFilledObject.append(",");
	codeFilledObject.append(gb);
	codeFilledObject.append(")\\psplot{");
	codeFilledObject.append(sb);
	codeFilledObject.append("}{");
	codeFilledObject.append(sa);
	codeFilledObject.append("}{");
	codeFilledObject.append(valueG);
	codeFilledObject.append("}\\lineto(");
	codeFilledObject.append(sa);
	codeFilledObject.append(",");
	codeFilledObject.append(fa);
	codeFilledObject.append(")\\closepath}\n");
	endBeamer(codeFilledObject);
    }
    
    protected void drawIntegral(GeoNumeric geo){
// command: \pscutom[option]{\pstplot{a}{b}{f(x)}\lineto(b,0)\lineto(a,0)\closepath} 
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo.getParentAlgorithm();
    	// function f
    	GeoFunction f = algo.getFunction();
    	// between a and b
    	String a = kernel.format(algo.getA().getDouble());
        String b = kernel.format(algo.getB().getDouble());    
    	String value=f.toValueString();
    	value=killSpace(Util.toLaTeXString(value,true));
		startBeamer(codeFilledObject);
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
		endBeamer(codeFilledObject);
    }
    protected void drawSlope(GeoNumeric geo){
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
		startBeamer(codeFilledObject);
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
		endBeamer(codeFilledObject);
        // draw Label
    	float xLabelHor = (x + xright) /2;
        float yLabelHor = y -(float)(
        		(euclidianView.getFont().getSize() + 2)/euclidianView.getYscale());
		Color geocolor=geo.getObjectColor();
		startBeamer(codePoint);
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
		endBeamer(codePoint);
    }
    protected void drawAngle(GeoAngle geo){
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
    	tempPoint.remove(); // Michael Borcherds 2008-08-20
    	
		double angSt = Math.atan2(firstVec[1], firstVec[0]);

// Michael Borcherds 2007-10-21 BEGIN
//      double angExt = geo.getValue();
		double angExt = geo.getRawAngle();
		if (angExt>Math.PI*2) angExt-=Math.PI*2;
		
		if (geo.angleStyle()==GeoAngle.ANGLE_ISCLOCKWISE)
		{
			angSt+=angExt;
			angExt=2.0*Math.PI-angExt;
		}
		
		if (geo.angleStyle()==GeoAngle.ANGLE_ISNOTREFLEX)
		{
			if (angExt>Math.PI)
			{
				angSt+=angExt;
				angExt=2.0*Math.PI-angExt;
			}
		}
		
		if (geo.angleStyle()==GeoAngle.ANGLE_ISREFLEX)
		{
			if (angExt<Math.PI)
			{
				angSt+=angExt;
				angExt=2.0*Math.PI-angExt;
			}
		}
        
//        if (geo.changedReflexAngle()) {        	
//        	angSt = angSt - angExt;
//        }
		// Michael Borcherds 2007-10-21 END

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
			startBeamer(codeFilledObject);
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
			endBeamer(codeFilledObject);
        }
        // draw arc for the angle
        else {	
       	// set arc in real world coords
		startBeamer(code);
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
		endBeamer(code);
		// draw the dot if angle= 90 and decoration=dot
		if (kernel.isEqual(geo.getValue(),Kernel.PI_HALF)&&geo.isEmphasizeRightAngle()&&euclidianView.getRightAngleStyle()==EuclidianView.RIGHT_ANGLE_STYLE_DOT){
			double diameter = geo.lineThickness/euclidianView.getXscale();
			double radius = arcSize/euclidianView.getXscale()/1.7;
			double labelAngle = (angSt+angExt) / 2.0;
			double x1=m[0] + radius * Math.cos(labelAngle);
			double x2 = m[1] + radius * Math.sin(labelAngle);
			// draw an ellipse
			// command:  \psellipse(0,0)(20.81,-10.81)}
				startBeamer(code);
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
				endBeamer(code);
			}
        }
   		int deco=geo.decorationType;
		if (deco!=GeoElement.DECORATION_NONE){ 
	   		startBeamer(code);
			markAngle(geo,r,m,angSt,angExt);
			endBeamer(code);
		}
    }
    protected void drawArrowArc(GeoAngle geo,double[] vertex,double angSt, double angEnd,double r, boolean anticlockwise){
    	// The arrow head goes away from the line.
    	// Arrow Winset=0.25, see PStricks spec for arrows
    	double arrowHeight=(geo.lineThickness*0.8+3)*1.4*3/4;
    	double angle=Math.asin(arrowHeight/2/euclidianView.getXscale()/ r);
    	angEnd=angEnd-angle;
		startBeamer(code);
    	code.append("\\psellipticarc");
    	code.append(LineOptionCode(geo,false));
		if (anticlockwise)	code.append("{->}(");
		else  code.append("{<-}(");
		code.append(kernel.format(vertex[0]));
		code.append(",");
		code.append(kernel.format(vertex[1]));
		code.append(")(");
		code.append(kernel.format(r));
		code.append(",");
		code.append(kernel.format(r));
		code.append("){");
		code.append(kernel.format(Math.toDegrees(angSt)));
		code.append("}{");
		code.append(kernel.format(Math.toDegrees(angEnd)));
		code.append("}\n");
		endBeamer(code);
    }
    
    
    
    protected void drawArc(GeoAngle geo,double[] vertex,double angSt, double angEnd,double r ){
    	if (isBeamer) code.append("  ");
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
	protected void drawTick(GeoAngle geo,double[] vertex,double angle){
		angle=-angle;
		double radius=geo.getArcSize();
		double diff= 2.5 + geo.lineThickness / 4d;
		double x1=euclidianView.toRealWorldCoordX(vertex[0]+(radius-diff)*Math.cos(angle));
		double x2=euclidianView.toRealWorldCoordX(vertex[0]+(radius+diff)*Math.cos(angle));
		double y1=euclidianView.toRealWorldCoordY(vertex[1]+(radius-diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		double y2=euclidianView.toRealWorldCoordY(vertex[1]+(radius+diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		if (isBeamer) code.append("  ");
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
    protected void drawSlider(GeoNumeric geo){
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
        geoPoint.setObjColor(geo.getObjectColor());
        String label=Util.toLaTeXString(geo.getLabelDescription(),true);
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
    	
    	geoPoint.remove(); // Michael Borcherds 2008-08-20
		startBeamer(code);
    	//draw Line for Slider
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
		endBeamer(code);
    }
    
    
    protected void drawPolygon(GeoPolygon geo){
    	// command: \pspolygon[par](x0,y0)....(xn,yn)
    	float alpha=geo.getAlphaValue();
    	if (alpha==0.0f) return;
		startBeamer(codeFilledObject);
    	codeFilledObject.append("\\pspolygon[linestyle=none,fillstyle=solid,fillcolor=");
    	ColorCode(geo.getObjectColor(),codeFilledObject);
    	codeFilledObject.append(",opacity=");
    	codeFilledObject.append(geo.getAlphaValue());
    	codeFilledObject.append("]");
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
		endBeamer(codeFilledObject);
    }
    
	protected void drawText(GeoText geo){
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
		startBeamer(code);
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
		endBeamer(code);
	}
	
	protected void drawGeoConicPart(GeoConicPart geo){
		double r1=geo.getHalfAxes()[0];
		double r2=geo.getHalfAxes()[1];
		double startAngle=geo.getParameterStart();
		double endAngle=geo.getParameterEnd();
		// Get all coefficients form the transform matrix
		AffineTransform af=geo.getAffineTransform();
		double m11=af.getScaleX();
		double m22=af.getScaleY();
		double m12=af.getShearX();
		double m21=af.getShearY();
		double tx=af.getTranslateX();
		double ty=af.getTranslateY();
		startBeamer(code);
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
			if (startAngle>endAngle){
				startAngle-=Math.PI*2;
			}
			StringBuffer sb1=new StringBuffer();
			sb1.append(kernel.format(r1));
			sb1.append("*cos(t)");
			StringBuffer sb2=new StringBuffer();
			sb2.append(kernel.format(r2));
			sb2.append("*sin(t)");
			code.append(startAngle);
			code.append("}{");
			code.append(endAngle);
			code.append("}{");
			code.append(kernel.format(m11));
			code.append("*");
			code.append(sb1);
			code.append("+");
			code.append(kernel.format(m12));
			code.append("*");
			code.append(sb2);
			code.append("+");
			code.append(kernel.format(tx));			
			code.append("|");
			code.append(kernel.format(m21));
			code.append("*");
			code.append(sb1);
			code.append("+");
			code.append(kernel.format(m22));
			code.append("*");
			code.append(sb2);
			code.append("+");
			code.append(kernel.format(ty));
			code.append("}");
			if (geo.getConicPartType()==GeoConicPart.CONIC_PART_SECTOR){				
				code.append("\\lineto(");
				code.append(kernel.format(tx));
				code.append(",");
				code.append(kernel.format(ty));
				code.append(")\\closepath}");
			}
			code.append("\n");
		//}
		endBeamer(code);
	}
	protected void drawCurveCartesian (GeoCurveCartesian geo){
//		  \parametricplot[algebraic=true,linecolor=red]  {-3.14}{3.14}{cos(3*t)|sin(2*t)}
		double start=geo.getMinParameter();
		double end=geo.getMaxParameter();
//		boolean isClosed=geo.isClosedPath();
		String fx=geo.getFunX();
		fx=killSpace(Util.toLaTeXString(fx,true));
		String fy=geo.getFunY();
		fy=killSpace(Util.toLaTeXString(fy,true));
		String variable=geo.getVarString();
		boolean warning=!(variable.equals("t"));
		startBeamer(code);
		if(warning) code.append("% WARNING: You have to use the special variable t in parametric plot");
		code.append("\\parametricplot");
		code.append(LineOptionCode(geo,true));
		int index=code.lastIndexOf("]");
		if (index==code.length()-1){
			code.deleteCharAt(index);
			code.append("]{");
		}
		else code.append("{");
		code.append(start);
		code.append("}{");
		code.append(end);
		code.append("}{");
		code.append(fx);
		code.append("|");
		code.append(fy);
		code.append("}\n");
		endBeamer(code);	
	}
	
	
	protected void drawFunction(GeoFunction geo){
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
//			Application.debug("xrangemin "+xrangemin);
			if (xrangemin==b) break;
			xrangemax=maxDefinedValue(f,xrangemin,b);
//			Application.debug("xrangemax "+xrangemax);
			startBeamer(code);
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
			endBeamer(code);
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
		// for exponential in new Geogbra version.
		renameFunc(sb,Kernel.EULER_STRING,"2.718281828");
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
	protected void drawGeoVector(GeoVector geo){
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
		startBeamer(code);
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
		endBeamer(code);
	}
	private void drawCircle(GeoConic geo){
		if (xunit==yunit){
	// draw a circle
	//	 command:  \pscircle[options](x_center,y_center){Radius)}
			double x=geo.getTranslationVector().getX();
			double y=geo.getTranslationVector().getY();
			double r=geo.getHalfAxes()[0];
			startBeamer(code);
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
			endBeamer(code);
		}
		else {
		// draw an ellipse
		// command:  \psellipse(0,0)(20.81,-10.81)}
			double x1=geo.getTranslationVector().getX();
			double y1=geo.getTranslationVector().getY();
			double r1=geo.getHalfAxes()[0];
			double r2=geo.getHalfAxes()[1];
			startBeamer(code);
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
			endBeamer(code);
		}
	}
	protected void drawGeoConic(GeoConic geo){	
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
				startBeamer(code);
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
				endBeamer(code);
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
				startBeamer(code);
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
				endBeamer(code);
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
				startBeamer(code);
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
				endBeamer(code);
				break;
		}	
	}
	
	protected void drawGeoPoint(GeoPoint gp){
		if (frame.getExportPointSymbol()){
			startBeamer(codePoint);
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
			endBeamer(codePoint);
		}
	}
	protected void drawGeoLine(GeoLine geo){
		double x=geo.getX();
		double y=geo.getY();
		double z=geo.getZ();
		startBeamer(code);
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
		endBeamer(code);
	}
	protected void drawGeoSegment(GeoSegment geo){
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
		startBeamer(code);
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
		endBeamer(code);
	}
	protected void drawLine(double x1,double y1,double x2,double y2,GeoElement geo){
		String sx1=kernel.format(x1);
		String sy1=kernel.format(y1);
		String sx2=kernel.format(x2);
		String sy2=kernel.format(y2);
		if (isBeamer) code.append("  ");
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
	protected void drawGeoRay(GeoRay geo){
		GeoPoint pointStart=geo.getStartPoint();
		double x1=pointStart.getX();
		double z1=pointStart.getZ();
		x1=x1/z1;
		String y1=kernel.format(pointStart.getY()/z1);
		
		double x=geo.getX();
		double y=geo.getY();
		double z=geo.getZ();
		startBeamer(code);
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
		endBeamer(code);
	}
    
    private void initUnitAndVariable(){
		// Initaialze uits, dot style, dot size .... 
		codeBeginPic.append("\\psset{xunit=");
		codeBeginPic.append(sci2dec(xunit));
		codeBeginPic.append("cm,yunit=");
		codeBeginPic.append(sci2dec(yunit));
		codeBeginPic.append("cm,algebraic=true,dotstyle=o,dotsize=");
		codeBeginPic.append(EuclidianView.DEFAULT_POINT_SIZE);
		codeBeginPic.append("pt 0");
		codeBeginPic.append(",linewidth=");
		codeBeginPic.append(kernel.format(EuclidianView.DEFAULT_LINE_THICKNESS/2*0.8));
		codeBeginPic.append("pt,arrowsize=3pt 2,arrowinset=0.25}\n");
    }
    
	// if label is Visible, draw it
	protected void drawLabel(GeoElement geo,Drawable drawGeo){
		try{
			if (geo.isLabelVisible()){
				String name;
				if (geo.getLabelMode()==GeoElement.LABEL_CAPTION)
					name=geo.getLabelDescription();
				else name="$"+Util.toLaTeXString(geo.getLabelDescription(),true)+"$";
				if (name.indexOf("\u00b0")!=-1){
					name=name.replaceAll("\u00b0", "\\\\textrm{\\\\degre}");
					if (codePreamble.indexOf("\\degre")==-1)
						codePreamble.append("\\newcommand{\\degre}{\\ensuremath{^\\circ}}\n");
				}
	
				if (null==drawGeo) drawGeo=euclidianView.getDrawableFor(geo);
				double xLabel=drawGeo.getxLabel();
				double yLabel=drawGeo.getyLabel();
				xLabel=euclidianView.toRealWorldCoordX(Math.round(xLabel));
				yLabel=euclidianView.toRealWorldCoordY(Math.round(yLabel));
				
				Color geocolor=geo.getObjectColor();
				startBeamer(codePoint);
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
				endBeamer(codePoint);
			}
		}
		// For GeoElement that don't have a Label
		// For example (created with geoList)
		catch(NullPointerException e){}
		}	

	
	
    // Draw the grid 
	private void drawGrid(){
		Color GridCol=euclidianView.getGridColor();
		double[] GridDist=euclidianView.getGridDistances();
//		int GridLine=euclidianView.getGridLineStyle();

		// Set Units for grid
		codeBeginPic.append("\\psset{xunit=");
//		Application.debug(GridDist[0]*xunit);
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
		String[] label=euclidianView.getAxesLabels();
		codeBeginPic.append("\\psaxes[labelFontSize=\\scriptstyle,xAxis=");
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
		codeBeginPic.append(")");
		if (null!=label[0]||null!=label[1]){
			codeBeginPic.append("[");
			if (null!=label[0]) codeBeginPic.append(label[0]);
			codeBeginPic.append(",140] [");
			if (null!=label[1]) codeBeginPic.append(label[1]);
			codeBeginPic.append(",-40]");
		}
		codeBeginPic.append("\n");
	}
	private void PointOptionCode(GeoPoint geo){
		Color dotcolor=geo.getObjectColor();
		int dotsize=geo.getPointSize();
		int dotstyle=geo.getPointStyle();
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
		
		if (coma) codePoint.append(",");
		if (!bracket) codePoint.append("[");
		coma = true;
		bracket = true;
		codePoint.append("dotstyle=");
		switch(dotstyle){
			case EuclidianView.POINT_STYLE_CIRCLE:
				codePoint.append("o");
			break;
			case EuclidianView.POINT_STYLE_CROSS:
				codePoint.append("x");
			break;
			case EuclidianView.POINT_STYLE_DOT:
				codePoint.append("*");
			break;
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
		if (coma) sb.append(",");
		else coma=true;
		if (!bracket) sb.append("[");
		bracket=true;
		sb.append("fillcolor=");
		ColorCode(linecolor,sb);
		sb.append(",fillstyle=solid,opacity=");
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
	// Append the name color to StringBuffer sb 
	protected void ColorCode(Color c,StringBuffer sb){
		if (frame.isGrayscale()){
			String colorname="";
			int red=c.getRed();
			int green=c.getGreen();
			int blue=c.getBlue();
			int grayscale=(red+green+blue)/3;
			c=new Color(grayscale,grayscale,grayscale);
			if (CustomColor.containsKey(c)){
				colorname=CustomColor.get(c).toString();
			}
			else {
				colorname=createCustomColor(grayscale,grayscale,grayscale);
				codeBeginDoc.append("\\newrgbcolor{"+colorname+"}{"
					+kernel.format(grayscale/255d)+" "
					+kernel.format(grayscale/255d)+" "
					+kernel.format(grayscale/255d)+"}\n");
				CustomColor.put(c,colorname);
			}
			if (c.equals(Color.BLACK)) sb.append("black");
			else if (c.equals(Color.DARK_GRAY)) sb.append("darkgray");
			else if (c.equals(Color.GRAY)) sb.append("gray");
			else if (c.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
			else if (c.equals(Color.WHITE)) sb.append("white");
			else sb.append(colorname);
		}
		else {
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
					colorname=createCustomColor((int)red,(int)green,(int)blue);
					codeBeginDoc.append("\\newrgbcolor{"+colorname+"}{"
						+kernel.format(red/255d)+" "
						+kernel.format(green/255d)+" "
						+kernel.format(blue/255d)+"}\n");
					CustomColor.put(c,colorname);
				}
				sb.append(colorname);
			}
		}
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
/*	private void defineTransparency(){
		String str="\\makeatletter\n\\define@key[psset]{}{transpalpha}{\\pst@checknum{#1}\\pstranspalpha}\n"+
		"\\psset{transpalpha=1}\n"+
		"\\def\\psfs@transp{%\n"+
		"  \\addto@pscode{/Normal .setblendmode \\pstranspalpha .setshapealpha }%\n"+
		"  \\psfs@solid}\n";
		if (!transparency) codePreamble.append(str);
		transparency=true;
	}
	*/
	private void addText(String st,boolean isLatex,int style,int size,Color geocolor){
		if (isLatex)code.append("$");
		// Replace all backslash symbol with \textbackslash
		else {
			st=st.replaceAll("\\\\", "\\\\textbackslash ");
		}
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
	

}
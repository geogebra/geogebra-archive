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
import geogebra.kernel.arithmetic.ExpressionNode;
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
import java.lang.Character; // isUpperCase
import java.io.*; // InputStream
/*
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject; */
/**
 * @author Andy Zhu
 */

public class GeoGebraToAsymptote extends GeoGebraExport {
	 // Use euro symbol
	private boolean eurosym = false;
	 // Use compact code and cse5 code, respectively
	private boolean compact = false, compactcse5=false;
	 // Indexes number of parabolas and hyperbolas and arcs and functions
    private int parabolaCount = 0, hyperbolaCount = 0, arcCount = 0, functionCount = 0,
                fillType = 0, fontsize;
     // Code for beginning of picture, for points, for Colors, and for background fill
	private StringBuilder codeBeginPic, codePointDecl, codeColors, codeEndDoc;
	 // Contains list of points
	private ArrayList<GeoPoint> pointList;
	 // Maps unicode expressions to text equivalents
	private HashMap<String,String> unicodeTable;
	
	/**
	 * @param app
	 */
	public GeoGebraToAsymptote(final Application app) {
    	super(app);
    }
    protected void createFrame(){
    	frame = new AsymptoteFrame(this);
    }
  	 
    public void generateAllCode() {
		int oldCASPrintform = kernel.getCASPrintForm();
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_PSTRICKS);
    	
		// reset global variables
		parabolaCount = 0; 
		hyperbolaCount = 0; 
		arcCount = 0; 
		functionCount = 0;
        fillType = 0;
        pointList = new ArrayList<GeoPoint>();
        unicodeTable = new HashMap<String,String>();
        
		// retrieve flags from frame
       	format = frame.getFormat();
    	compact = frame.getAsyCompact() || frame.getAsyCompactCse5();
       	compactcse5 = frame.getAsyCompactCse5(); 
       	fillType = frame.getFillType();
    	fontsize = frame.getFontSize();
       	
       	// initialize unit variables
    	try{	
    		xunit=frame.getXUnit();
    		yunit=frame.getYUnit();
     	}
    	catch(NullPointerException e2){
    		xunit=1; yunit=1;
    	}
    	// scale ratio = yunit/xunit;
    	// initialize new StringBuilders for Asymptote code
    	code             = new StringBuilder();
    	codePoint        = new StringBuilder();
    	codePreamble     = new StringBuilder();
    	codeFilledObject = new StringBuilder();
		codeBeginDoc     = new StringBuilder();
		codeColors       = new StringBuilder();
		codeBeginPic     = new StringBuilder();
		// codeFills        = new StringBuilder();
		codePointDecl    = new StringBuilder();
		codeEndDoc       = new StringBuilder();
		CustomColor      = new HashMap();
		
		// Generate point list 
		if(compactcse5) {
	    	for (int step = 0; step < construction.steps(); step++){
	    		GeoElement[] geos = construction.getConstructionElement(step).getGeoElements();
	    		for (int j = 0; j < geos.length; j++){
	            	GeoElement g = (GeoElement)(geos[j]);
	            	if (g.isEuclidianVisible() && g.isGeoPoint()) 
	            		pointList.add((GeoPoint) g);
	            }
	    	}
		}
		// In cse5, initialize pair definitions.
    	initPointDeclarations();
    	// Initialize Unicode Table
    	initUnicodeTextTable();
		
		// Write preamble. If compact option unchecked, include liberal documentation.
		if (!compact) {
			codePreamble.append(" /* Geogebra to Asymptote conversion, ");
			codePreamble.append("documentation at userscripts.org/scripts/show/72997 */\n");
		}
		codePreamble.append("import graph; size(");
		codePreamble.append(kernel.format(((ExportFrame) frame).getLatexWidth()));
		codePreamble.append("cm); ");
		initUnitAndVariable();
		
		// Draw Grid
		if (euclidianView.getShowGrid() && ((ExportFrame) frame).getShowAxes())
			drawGrid();
		
		// Draw axis
		if ((euclidianView.getShowXaxis() || euclidianView.getShowYaxis())
		 && ((ExportFrame) frame).getShowAxes()) 
				drawAxis();
		
		// Background color
        if(!euclidianView.getBackground().equals(Color.WHITE)) {
        	if(!compact)
        		codeEndDoc.append("\n");
        	codeEndDoc.append("shipout(bbox(");
        	ColorCode(euclidianView.getBackground(),codeEndDoc);
        	codeEndDoc.append(",Fill)); ");
        }
		
        // get all objects from construction and "draw" by creating Asymptote code */			
		drawAllElements();
		
        // add code for Points and Labels
        code.append("\n");
        if(!compact)
        	code.append(" /* dots and labels */");
		code.append(codePoint);

/*		String formatFont=resizeFont(app.getFontSize());
		if (null!=formatFont){
			codeBeginPic.insert(0,formatFont+"\n");
			code.append("}\n");
		}
*/		// Order:
		// Preamble, BeginDoc, Colors, Points, Fills, Pic, Objects, regular code, EndDoc
		if(!compact)
			code.insert(0,"\n /* draw figures */");
		code.insert(0,codeBeginPic+"");
		code.insert(0,codeFilledObject);
		if(codeFilledObject.length() != 0)
			code.insert(0,"\n");
		// code.insert(0,codeFills+""); // temporary replacement for codeFilledObject
		code.insert(0,codePointDecl+"");
		if(!compact)
			code.insert(0,codeColors+"");
		else if(codeColors.length() != 0) // remove first comma of pen
			code.insert(0,"\npen" + codeColors.substring(1) + "; ");
		code.insert(0,codeBeginDoc+"");		
        code.insert(0,codePreamble+"");
        
        // Clip frame
        code.append("\nclip((xmin,ymin)--(xmin,ymax)--(xmax,ymax)--(xmax,ymin)--cycle); ");
        
        // Background fill
        code.append(codeEndDoc);
        
        // Re-scale
        if(kernel.format(yunit).compareTo(kernel.format(xunit)) != 0) {
        	if(!compact)
	        	code.append("\n /* re-scale y/x */\n");
	        code.append("currentpicture");
	        packSpace("=",code);
	        code.append("yscale(");
	        code.append(kernel.format(yunit/xunit));
	        code.append(")*currentpicture; ");
        }
        if(!compact)
        	code.append("\n /* end of picture */");		

        // code to temporarily remove pi from code
        convertUnicodeToText(code);
        
		kernel.setCASPrintForm(oldCASPrintform);
		frame.write(code);
    }	
    
    protected void drawLocus(GeoLocus geo){
    	ArrayList ll = geo.getMyPointList();
    	Iterator it = ll.iterator();
    	boolean first = true, first2 = true;
    	
    	if(!compact)
    		code.append(" /* locus construction */\n");
    	startDraw();
    	while(it.hasNext()){
    		MyPoint mp = (MyPoint) it.next();
    		if (mp.x > xmin && mp.x < xmax && mp.y > ymin && mp.y < ymax){
        		String x = kernel.format(mp.x), 
        		       y = kernel.format(mp.y);
    			if (first && first2) {
    				code.append("(");
    				first = false; first2 = false;
    			}
    			else if (first) { // don't draw connecting line
    				code.append("^^(");
    				first = false;
    			}
    			else if (mp.lineTo)
    				code.append("--(");
    			else
    				code.append("^^(");
    			code.append(x);
    			code.append(",");
    			code.append(y);
    			code.append(")");
    		}
    		else first = true;
    	}
		endDraw(geo);
    }

    protected void drawBoxPlot(GeoNumeric geo){
    	AlgoBoxPlot algo=((AlgoBoxPlot)geo.getParentAlgorithm());
    	double y = algo.getA().getDouble();
    	double height = algo.getB().getDouble();
    	double[] lf = algo.getLeftBorders();
    	double min = lf[0];
    	double q1  = lf[1];
    	double med = lf[2];
    	double q3  = lf[3];
    	double max = lf[4];

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
		
		// Rectangle q1-q3
    	startTransparentFill(codeFilledObject);
		codeFilledObject.append("box(");
		addPoint(kernel.format(q1),kernel.format(y-height),codeFilledObject);
		codeFilledObject.append(",");
		addPoint(kernel.format(q3),kernel.format(y+height),codeFilledObject);
		codeFilledObject.append(")");
		endTransparentFill(geo, codeFilledObject);
    }

    protected void drawHistogram(GeoNumeric geo){
    	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
        double[] y = algo.getValues();
        double[] x = algo.getLeftBorders();

        for (int i=0; i<x.length-1; i++){
        	startTransparentFill(codeFilledObject);
    		codeFilledObject.append("box((");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",0),(");
    		codeFilledObject.append(kernel.format(x[i+1]));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i]));
    		codeFilledObject.append("))");
    		endTransparentFill(geo,codeFilledObject);
        }    	
    }
    
    protected void drawSumTrapezoidal(GeoNumeric geo){
       	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
    	int n=algo.getIntervals();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
       	for (int i=0;i<n;i++){
        	startTransparentFill(codeFilledObject);
    		codeFilledObject.append("(");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",0)--(");
    		codeFilledObject.append(kernel.format(x[i+1]));
    		codeFilledObject.append(",0)--(");
    		codeFilledObject.append(kernel.format(x[i+1]));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i+1]));
    		codeFilledObject.append(")--(");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i]));
    		codeFilledObject.append(")--cycle");
    		endTransparentFill(geo,codeFilledObject);
    	}       
    }
    
    protected void drawSumUpperLower(GeoNumeric geo){
    	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
    	int n = algo.getIntervals();
        double step = algo.getStep();
        double[] y = algo.getValues();
        double[] x = algo.getLeftBorders();

       	for (int i=0; i<n; i++){
        	startTransparentFill(codeFilledObject);
    		codeFilledObject.append("box((");
    		codeFilledObject.append(kernel.format(x[i]));
    		codeFilledObject.append(",0),(");
    		codeFilledObject.append(kernel.format(x[i]+step));
    		codeFilledObject.append(",");
    		codeFilledObject.append(kernel.format(y[i]));
    		codeFilledObject.append("))");
    		endTransparentFill(geo,codeFilledObject);
        }
    }
    
    protected void drawIntegralFunctions(GeoNumeric geo){
	   	AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo.getParentAlgorithm();		
		GeoFunction f = algo.getF(), // function f
		            g = algo.getG(); // function g
		// double a and b
		double a = algo.getA().getDouble(),
		       b = algo.getB().getDouble();
		// String output for a and b
		String sa = kernel.format(a),
	           sb = kernel.format(b);
	    // String Expression of f and g
	    String valueF = f.toValueString(), valueG = g.toValueString();
		valueF = parseFunction(valueF);
		valueG = parseFunction(valueG);
		// String expressions for f(a) and g(b) 
		// String fa = kernel.format(f.evaluate(a));
		// String gb = kernel.format(g.evaluate(b));

		if(!compact)
			codeFilledObject.append("\n");
		
		// write functions for f and g if they do not already exist.
    	int indexFunc = -1;
		String tempFunctionCountF = "f"+Integer.toString(functionCount+1);
		String returnCode = "(real x){return " + valueF + ";} ";
		// search for previous occurrences of function
		// TODO Hashtable rewrite?
		if(compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if(indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
				tempFunctionCountF = codeFilledObject.substring(indexFuncStart+1,indexFunc);
			}
		} 
		// write function
		if(indexFunc == -1){ 
			functionCount++;
			codeFilledObject.append("real f");
			codeFilledObject.append(functionCount);
			packSpace("(real x)", codeFilledObject);
			codeFilledObject.append("{return ");
	    	codeFilledObject.append(valueF);
	    	codeFilledObject.append(";} ");
	    }

		indexFunc = -1;
		String tempFunctionCountG = "f"+Integer.toString(functionCount+1);
		returnCode = "(real x){return " + valueG + ";} ";
		// search for previous occurrences of function
		if(compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if(indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
				tempFunctionCountG = codeFilledObject.substring(indexFuncStart+1,indexFunc);
			}
		} // write function
		if(indexFunc == -1){ 
			functionCount++;
			codeFilledObject.append("real f");
			codeFilledObject.append(functionCount);
			packSpace("(real x)", codeFilledObject);
			codeFilledObject.append("{return ");
	    	codeFilledObject.append(valueG);
	    	codeFilledObject.append(";} ");
	    }
    	
		// draw graphs of f and g
    	startTransparentFill(codeFilledObject);
		codeFilledObject.append("graph(");
		codeFilledObject.append(tempFunctionCountF);
		codeFilledObject.append(",");
		codeFilledObject.append(sa);
		codeFilledObject.append(",");
		codeFilledObject.append(sb);
		codeFilledObject.append(")--graph(");
		codeFilledObject.append(tempFunctionCountG);
		codeFilledObject.append(",");
		codeFilledObject.append(sb);
		codeFilledObject.append(",");
		codeFilledObject.append(sa);
		codeFilledObject.append(")--cycle");
		endTransparentFill(geo, codeFilledObject);
    }

    protected void drawIntegral(GeoNumeric geo){
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo.getParentAlgorithm();
    	GeoFunction f = algo.getFunction(); // function f between a and b
    	String a = kernel.format(algo.getA().getDouble());
        String b = kernel.format(algo.getB().getDouble());    
    	String value = f.toValueString();
    	value = parseFunction(value);
    	
    	int indexFunc = -1;
		String tempFunctionCount = "f"+Integer.toString(functionCount+1);
		String returnCode = "(real x){return (" + value + ");} ";
		// search for previous occurrences of function
		if(compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if(indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
				tempFunctionCount = codeFilledObject.substring(indexFuncStart+1,indexFunc);
			}
		} // write function
		if(indexFunc == -1){ 
			functionCount++;
	    	if(!compact)
				codeFilledObject.append("\n");
	    	codeFilledObject.append("real f");
			codeFilledObject.append(functionCount);
			packSpace("(real x)", codeFilledObject);
			codeFilledObject.append("{return ");
	    	codeFilledObject.append(value);
	    	codeFilledObject.append(";} ");
		}
		
    	startTransparentFill(codeFilledObject);
    	codeFilledObject.append("graph(");
    	codeFilledObject.append(tempFunctionCount);
		codeFilledObject.append(",");
		codeFilledObject.append(a);
    	codeFilledObject.append(",");
    	codeFilledObject.append(b);
    	codeFilledObject.append(")--");
    	addPoint(b,"0",codeFilledObject);
    	codeFilledObject.append("--");
    	addPoint(a,"0",codeFilledObject);
    	codeFilledObject.append("--cycle");
    	endTransparentFill(geo,codeFilledObject);
    }

    protected void drawSlope(GeoNumeric geo){
       	int slopeTriangleSize = geo.getSlopeTriangleSize();
        double rwHeight = geo.getValue() * slopeTriangleSize;
        double height = euclidianView.getYscale() * rwHeight;
        double[] coords = new double[2];
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

    	startTransparentFill(codeFilledObject);
		addPoint(kernel.format(x),kernel.format(y),codeFilledObject);
		codeFilledObject.append("--");
		addPoint(kernel.format(xright),kernel.format(y),codeFilledObject);
		codeFilledObject.append("--");
		addPoint(kernel.format(xright),kernel.format(y+rwHeight),codeFilledObject);
		codeFilledObject.append("--cycle");
		endTransparentFill(geo,codeFilledObject);
    	
        // draw Label 
    	float xLabelHor = (x + xright) /2;
        float yLabelHor = y - (float)(
        		(euclidianView.getFont().getSize() + 2)/euclidianView.getYscale());
		Color geocolor=geo.getObjectColor();

		if(!compact)
			codePoint.append("\n");
		codePoint.append("label(\"$");
		codePoint.append(slopeTriangleSize);
		codePoint.append("$\",(");
		codePoint.append(kernel.format(xLabelHor));
		codePoint.append(",");
		codePoint.append(kernel.format(yLabelHor));
		codePoint.append("),NE*");
		if(compact)
			codePoint.append("lsf");
		else
			codePoint.append("labelscalefactor");
		if (!geocolor.equals(Color.BLACK)){
			codePoint.append(",");
			ColorCode(geocolor,codePoint);
		}
		codePoint.append("); "); 
    }

    protected void drawAngle(GeoAngle geo){
    	int arcSize = geo.getArcSize();
    	AlgoElement algo = geo.getParentAlgorithm();
    	GeoPoint vertex,point;
    	GeoVector v;
    	GeoLine line,line2;
     	GeoPoint tempPoint = new GeoPoint(construction);     	
     	tempPoint.setCoords(0.0, 0.0, 1.0);
     	double[] firstVec = new double[2];
     	double[] m = new double[2];
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
		// double angExt = geo.getValue();
		double angExt = geo.getRawAngle();
		if (angExt > Math.PI*2) angExt -= Math.PI*2;
		
		if (geo.angleStyle() == GeoAngle.ANGLE_ISCLOCKWISE) {
			angSt += angExt;
			angExt = 2.0*Math.PI-angExt;
		}
		
		if (geo.angleStyle() == GeoAngle.ANGLE_ISNOTREFLEX) {
			if (angExt > Math.PI) {
				angSt += angExt;
				angExt = 2.0*Math.PI-angExt;
			}
		}
		
		if (geo.angleStyle() == GeoAngle.ANGLE_ISREFLEX) {
			if (angExt < Math.PI) {
				angSt += angExt;
				angExt = 2.0*Math.PI-angExt;
			}
		}
		// if (geo.changedReflexAngle()) {        	
		//   	angSt = angSt - angExt;
		// }
		// Michael Borcherds 2007-10-21 END

		angExt += angSt;
		double r = arcSize /euclidianView.getXscale();
		
    	// StringBuilder tempsb = new StringBuilder();
    	startTransparentFill(codeFilledObject);
		// if right angle and decoration is a little square
        if (kernel.isEqual(geo.getValue(),Kernel.PI_HALF) && geo.isEmphasizeRightAngle()
        		&& euclidianView.getRightAngleStyle() == EuclidianView.RIGHT_ANGLE_STYLE_SQUARE){
        	r = r/Math.sqrt(2);
        	double[] x = new double[8];
        	x[0] = m[0]+r*Math.cos(angSt);
        	x[1] = m[1]+r*Math.sin(angSt);
        	x[2] = m[0]+r*Math.sqrt(2)*Math.cos(angSt+Kernel.PI_HALF/2);
        	x[3] = m[1]+r*Math.sqrt(2)*Math.sin(angSt+Kernel.PI_HALF/2);
        	x[4] = m[0]+r*Math.cos(angSt+Kernel.PI_HALF);
        	x[5] = m[1]+r*Math.sin(angSt+Kernel.PI_HALF);
        	x[6] = m[0];
        	x[7] = m[1];
        	
    		for (int i = 0; i < 4; i++){
        		addPoint(kernel.format(x[2*i]),kernel.format(x[2*i+1]),codeFilledObject);
        		codeFilledObject.append("--");
        	}
    		codeFilledObject.append("cycle");
    		
    		// transparent fill options
    		endTransparentFill(geo, codeFilledObject);
        }
        else {	// draw arc for the angle. 
			codeFilledObject.append("arc(");
			addPoint(kernel.format(m[0]),kernel.format(m[1]),codeFilledObject);
			codeFilledObject.append(",");
			codeFilledObject.append(kernel.format(r));
			codeFilledObject.append(",");
			codeFilledObject.append(kernel.format(Math.toDegrees(angSt)));
			codeFilledObject.append(",");
			codeFilledObject.append(kernel.format(Math.toDegrees(angExt)));
			codeFilledObject.append(")--(");
			codeFilledObject.append(kernel.format(m[0]));
			codeFilledObject.append(",");
			codeFilledObject.append(kernel.format(m[1]));
			codeFilledObject.append(")--cycle");
			// transparent fill options
			endTransparentFill(geo,codeFilledObject);
    		
			// draw the [circular?] dot if right angle and decoration is dot
			if (kernel.isEqual(geo.getValue(),Kernel.PI_HALF) && geo.isEmphasizeRightAngle() 
					&& euclidianView.getRightAngleStyle() == EuclidianView.RIGHT_ANGLE_STYLE_DOT){
				double diameter = geo.lineThickness/euclidianView.getXscale();
				double radius = arcSize/euclidianView.getXscale()/1.7;
				double labelAngle = (angSt+angExt) / 2.0;
				double x1 = m[0] + radius * Math.cos(labelAngle); 
				double x2 = m[1] + radius * Math.sin(labelAngle);
				
				startDraw();
				if(compactcse5)
					code.append("CR(");
				else
					code.append("circle(");
				addPoint(kernel.format(x1),kernel.format(x2),code);
				code.append(",");
				code.append(kernel.format(diameter));
				code.append(")");
				endDraw(geo);
			}
        }
		if (geo.decorationType != GeoElement.DECORATION_NONE){ 
			markAngle(geo,r,m,angSt,angExt);
		}
    }
    
    protected void drawArrowArc(GeoAngle geo,double[] vertex,double angSt,double angEnd,double r,boolean anticlockwise){
    	// The arrow head goes away from the line.
    	// Arrow Winset=0.25, see PStricks spec for arrows
    	double arrowHeight = (geo.lineThickness*0.8+3)*1.4*3/4;
    	double angle = Math.asin(arrowHeight/2/euclidianView.getXscale()/ r);
    	angEnd = angEnd-angle;
  	
    	startDraw();
    	code.append("arc(");
    	addPoint(kernel.format(vertex[0]),kernel.format(vertex[1]),code);
		code.append(",");
		code.append(kernel.format(r));
		code.append(",");
		code.append(kernel.format(Math.toDegrees(angSt)));
		code.append(",");
		code.append(kernel.format(Math.toDegrees(angEnd)));
		code.append(")");
		if(LineOptionCode(geo,true) != null) {
			code.append(",");
			if(!compact)
				code.append(" ");
			code.append(LineOptionCode(geo,true));
		} // TODO: resize?
		if (anticlockwise)	code.append(",EndArcArrow(6)");
		else                code.append(",BeginArcArrow(6)");
		code.append("); ");
    }
    
    // angSt, angEnd in degrees. r = radius.
    protected void drawArc(GeoAngle geo,double[] vertex, double angSt, double angEnd, double r){
    	startDraw();
    	code.append("arc(");
    	addPoint(kernel.format(vertex[0]),kernel.format(vertex[1]),code);
		code.append(",");
		code.append(kernel.format(r));
		code.append(",");
		code.append(kernel.format(Math.toDegrees(angSt)));
		code.append(",");
		code.append(kernel.format(Math.toDegrees(angEnd)));
		code.append(")");
		endDraw(geo);
    }
    
	protected void drawTick(GeoAngle geo,double[] vertex,double angle){
		angle=-angle;
		double radius=geo.getArcSize();
		double diff= 2.5 + geo.lineThickness / 4d;
		double x1=euclidianView.toRealWorldCoordX(vertex[0]+(radius-diff)*Math.cos(angle));
		double x2=euclidianView.toRealWorldCoordX(vertex[0]+(radius+diff)*Math.cos(angle));
		double y1=euclidianView.toRealWorldCoordY(vertex[1]+(radius-diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		double y2=euclidianView.toRealWorldCoordY(vertex[1]+(radius+diff)*Math.sin(angle)*euclidianView.getScaleRatio());

		startDraw();
		addPoint(kernel.format(x1),kernel.format(y1),code);
		code.append("--");
		addPoint(kernel.format(x2),kernel.format(y2),code);
		endDraw(geo);
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

    	//draw Line for Slider
    	startDraw();
    	addPoint(kernel.format(x),kernel.format(y),code);
    	code.append("--");
    	if (horizontal) x += width;
    	else            y += width;
    	addPoint(kernel.format(x),kernel.format(y),code);
    	endDraw(geo);
    }
    
    protected void drawPolygon(GeoPolygon geo){
    	GeoPoint [] points = geo.getPoints();
    	// StringBuilder tempsb = new StringBuilder();
    	
    	startTransparentFill(codeFilledObject);
    	for (int i = 0; i < points.length; i++){
    		double x = points[i].getX(),
    		       y = points[i].getY(),
    		       z = points[i].getZ();
     		x = x / z; y = y / z;
     		addPoint(kernel.format(x),kernel.format(y),codeFilledObject);
     		codeFilledObject.append("--");
    	}
    	codeFilledObject.append("cycle");
    	endTransparentFill(geo,codeFilledObject);
    }
    
    protected void drawText(GeoText geo){
		boolean isLatex = geo.isLaTeX();
		String st = geo.getTextString();
		if(isLatex)
			st = Util.toLaTeXString(st, true);
		// try to replace euro symbol
		if (st.indexOf("\u20ac") != -1) {
			st = st.replaceAll("\\u20ac", "\\\\euro{}");
			if (!eurosym) codePreamble.append("usepackage(\"eurosym\"); ");
		}
		Color geocolor=geo.getObjectColor();
		int style=geo.getFontStyle();
		int size=geo.getFontSize()+app.getFontSize();
		GeoPoint gp;
		double x,y;
	      // compute location of text		
		if (geo.isAbsoluteScreenLocActive()) {
			x = geo.getAbsoluteScreenLocX();
			y = geo.getAbsoluteScreenLocY(); 
		} 
		else {
			gp = (GeoPoint) geo.getStartPoint();
	        if (gp == null) {
				x = (int) euclidianView.getXZero();
				y = (int) euclidianView.getYZero();
	        } 
	        else {
	        	if (!gp.isDefined()) {
	        		return;
	        	}
				x = euclidianView.toScreenCoordX(gp.inhomX);
				y = euclidianView.toScreenCoordY(gp.inhomY);        	
	        }
	        x += geo.labelOffsetX;
			y += geo.labelOffsetY; 
		}
		x = euclidianView.toRealWorldCoordX(x);
		y = euclidianView.toRealWorldCoordY(y-euclidianView.getFont().getSize());
		int id = st.indexOf("\n");
		boolean comma = false;

		// One line
		if (id == -1){
			if(!compact)
				code.append("\n");
			code.append("label(\"");
			addText(st,isLatex,style,size,geocolor);
			code.append("\",");
			addPoint(kernel.format(x),kernel.format(y),code);
			code.append(",SE*");
			if(compact)
				code.append("lsf");
			else
				code.append("labelscalefactor");
			if(!geocolor.equals(Color.BLACK)) { // color
				code.append(","); comma = true;
				ColorCode(geocolor,code);
			}
			if(size != app.getFontSize()) { // fontsize
				if(!comma) code.append(",");
				else packSpace("+", code);
				code.append("fontsize(");
				code.append(fontsize+(size-app.getFontSize()));
				code.append(")");
			}
			else if(compactcse5) {	// use default font pen for cse5
				if(!comma) code.append(",");
				else packSpace("+", code);
				code.append("fp");
			}
			code.append("); ");
		}
		// MultiLine
		else {
			StringBuilder sb = new StringBuilder();
			StringTokenizer stk = new StringTokenizer(st,"\n");
			int width = 0;
			Font font = new Font(geo.isSerifFont() ? "Serif" : "SansSerif", style, size);
			FontMetrics fm = euclidianView.getFontMetrics(font);
			while (stk.hasMoreTokens()){
				String line = stk.nextToken();
				width = Math.max(width,fm.stringWidth(line));		
				sb.append(line);
				if (stk.hasMoreTokens()) sb.append(" \\\\ ");
			}
			
			if(!compact)
				code.append("\n");
			code.append("label(\"$");
			code.append("\\parbox{");
			code.append(kernel.format(width*(xmax-xmin)*xunit/euclidianView.getWidth()+1));
			code.append(" cm}{");
			addText(new String(sb),isLatex,style,size,geocolor);
			code.append("}$\",");
			addPoint(kernel.format(x),kernel.format(y),code);
			code.append(",SE*");
			if(compact)
				code.append("lsf");
			else
				code.append("labelscalefactor");
			if(!geocolor.equals(Color.BLACK)) { // color
				code.append(","); comma = true;
				ColorCode(geocolor,code);
			}
			if(size != app.getFontSize()) { // fontsize
				if(!comma) code.append(",");
				else packSpace("+", code);
				code.append("fontsize(");
				code.append(fontsize+(size-app.getFontSize()));
				code.append(")");
			}
			else if(compactcse5) {	// use default font pen for cse5
				if(!comma) code.append(",");
				else packSpace("+", code);
				code.append("fp");
			}
			code.append("); ");
		}
	}
	
	protected void drawGeoConicPart(GeoConicPart geo){
		StringBuilder tempsb = new StringBuilder();
		double r1 = geo.getHalfAxes()[0],
		       r2 = geo.getHalfAxes()[1];
		double startAngle = geo.getParameterStart();
		double endAngle = geo.getParameterEnd();
		// Get all coefficients form the transform matrix
		AffineTransform af = geo.getAffineTransform();
		double m11 = af.getScaleX();
		double m22 = af.getScaleY();
		double m12 = af.getShearX();
		double m21 = af.getShearY();
		double tx = af.getTranslateX();
		double ty = af.getTranslateY();
		
		if (startAngle > endAngle){
			startAngle -= Math.PI*2;
		}
		// Fill if: SECTOR and fill type not set to FILL_NONE
		if(m11 == 1 && m22 == 1 && m12 == 0 && m21 == 0) {
			if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR 
					&& fillType != ExportFrame.FILL_NONE)				
				startTransparentFill(tempsb);
			else
				startDraw(tempsb);
			tempsb.append("shift(");
			addPoint(kernel.format(tx),kernel.format(ty),tempsb);
			tempsb.append(")*xscale(");
			tempsb.append(kernel.format(r1));
			tempsb.append(")*yscale(");
			tempsb.append(kernel.format(r2));
			tempsb.append(")*arc((0,0),1,");
			tempsb.append(kernel.format(Math.toDegrees(startAngle)));
			tempsb.append(",");
			tempsb.append(kernel.format(Math.toDegrees(endAngle)));
			tempsb.append(")");
		}
		else {
			StringBuilder sb1=new StringBuilder(),sb2=new StringBuilder();
			sb1.append(kernel.format(r1));
			sb1.append("*cos(t)");
			sb2.append(kernel.format(r2));
			sb2.append("*sin(t)");
			
			arcCount++;
			if(!compact)
				tempsb.append("\n");
			tempsb.append("pair arc");
			tempsb.append(arcCount);
			packSpace("(real t)",tempsb);
			tempsb.append("{return (");
			tempsb.append(kernel.format(m11));
			tempsb.append("*");
			tempsb.append(sb1);
			tempsb.append("+");
			tempsb.append(kernel.format(m12));
			tempsb.append("*");
			tempsb.append(sb2);
			tempsb.append("+");
			tempsb.append(kernel.format(tx));			
			tempsb.append(",");
			tempsb.append(kernel.format(m21));
			tempsb.append("*");
			tempsb.append(sb1);
			tempsb.append("+");
			tempsb.append(kernel.format(m22));
			tempsb.append("*");
			tempsb.append(sb2);
			tempsb.append("+");
			tempsb.append(kernel.format(ty));
			tempsb.append(");} ");
			
			if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR 
					&& fillType != ExportFrame.FILL_NONE)				
				startTransparentFill(tempsb);
			else
				startDraw(tempsb);
			tempsb.append("graph(arc");
			tempsb.append(arcCount);
			tempsb.append(",");
			tempsb.append(kernel.format(startAngle));
			tempsb.append(",");
			tempsb.append(kernel.format(endAngle));
			tempsb.append(")");
		}
		if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR){				
			tempsb.append("--");
			addPoint(kernel.format(tx),kernel.format(ty),tempsb);
			tempsb.append("--cycle");
			if(fillType == ExportFrame.FILL_NONE)
				endDraw(geo,tempsb);
			else
				endTransparentFill(geo,tempsb);
		}
		else
			endDraw(geo,tempsb);
	
		if (geo.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR 
				&& fillType != ExportFrame.FILL_NONE)				
			codeFilledObject.append(tempsb);
		else
			code.append(tempsb);
	}
	
	protected void drawCurveCartesian (GeoCurveCartesian geo){
		double start = geo.getMinParameter(),
		         end = geo.getMaxParameter();
//		boolean isClosed=geo.isClosedPath();
		String fx = parseFunction(geo.getFunX());
		String fy = parseFunction(geo.getFunY());
		String variable = parseFunction(geo.getVarString());
		// boolean warning=!(variable.equals("t"));
		
		int indexFunc = -1;
		String tempFunctionCount = "f"+Integer.toString(functionCount+1);
		String returnCode = "(real "+variable+"){return (" + fx + "," + fy + ");} ";
		// search for previous occurrences of function
		if(compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if(indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
				tempFunctionCount = codeFilledObject.substring(indexFuncStart+1,indexFunc);
			}
			else if(code.indexOf(returnCode) != -1) {
				indexFunc = code.indexOf(returnCode);
				int indexFuncStart = code.lastIndexOf(" ",indexFunc);
				tempFunctionCount = code.substring(indexFuncStart+1,indexFunc);	
				indexFunc = code.indexOf(returnCode);
			}
		} // write function
		if(indexFunc == -1){ 
			functionCount++;
			if(!compact)
				code.append("\n");
			code.append("pair f");
			code.append(functionCount);
			packSpace("(real " + variable + ")",code);
			code.append("{return (");
			code.append(fx);
			code.append(",");
			code.append(fy);
			code.append(");} ");	
		}
		
		startDraw();
		code.append("graph(");
		code.append(tempFunctionCount);
		code.append(",");
		code.append(kernel.format(start));
		code.append(",");
		code.append(kernel.format(end));
		code.append(")");	
		endDraw(geo);
	}
	
	protected void drawFunction(GeoFunction geo){
		Function f = geo.getFunction();
		if (f == null) return;
		String value = f.toValueString();
		value = parseFunction(value);
		value = value.replaceAll("\\\\pi", "pi");
		double a = xmin;
		double b = xmax;
		if (geo.hasInterval()) {
			a = Math.max(a,geo.getIntervalMin());
			b = Math.min(b,geo.getIntervalMax());
		}
		double xrangemax = a, xrangemin = a;
		while (xrangemax < b){
			xrangemin = firstDefinedValue(geo,a,b);
//			Application.debug("xrangemin "+xrangemin);
			if (xrangemin == b) break;
			xrangemax = maxDefinedValue(geo,xrangemin,b);
//			Application.debug("xrangemax "+xrangemax);

			int indexFunc = -1;
			String tempFunctionCount = "f"+Integer.toString(functionCount+1);
			String returnCode = "(real x){return " + value + ";} ";
			// search for previous occurrences of function
			if(compact) {
				indexFunc = codeFilledObject.indexOf(returnCode);
				if(indexFunc != -1) {
					// retrieve name of previously used function
					int indexFuncStart = codeFilledObject.lastIndexOf(" ",indexFunc);
					tempFunctionCount = codeFilledObject.substring(indexFuncStart+1,indexFunc);
				}
				else if(code.indexOf(returnCode) != -1) {
					indexFunc = code.indexOf(returnCode);
					int indexFuncStart = code.lastIndexOf(" ",indexFunc);
					tempFunctionCount = code.substring(indexFuncStart+1,indexFunc);	
					indexFunc = code.indexOf(returnCode);
				}
			} // write function
			if(indexFunc == -1){ 
				functionCount++;
				if(!compact)
					code.append("\n");
				code.append("real ");
				code.append(tempFunctionCount);
				packSpace("(real x)",code);
				code.append("{return ");
				code.append(value);
				code.append(";} ");		
			}
			
			startDraw();
			code.append("graph(");
			code.append(tempFunctionCount);
			code.append(",");
			// add/subtract 0.01 to prevent 1/x, log(x) undefined behavior
			code.append(kernel.format(xrangemin+0.01));
			code.append(",");
			code.append(kernel.format(xrangemax-0.01));
			code.append(")");
			//? recycled code of sorts? 
			xrangemax += PRECISION_XRANGE_FUNCTION;
			a = xrangemax; 
			endDraw(geo);
		}
	}
/* Rewrite the function:
 - Kill spaces
 - Add character * when needed: 2  x +3 ----> 2*x+3
 - Rename several functions:
 		log(x)  ---> ln(x)
 		ceil(x) ---> ceiling(x)
 		exp(x)  ---> 2.71828^(x)
*/ //  
	private String killSpace(String name){
		StringBuilder sb = new StringBuilder();
		boolean operand = false;
		boolean space = false;
		for (int i = 0; i < name.length(); i++){
			char c = name.charAt(i);
			if ("*/+-".indexOf(c) != -1){
				sb.append(c);
				operand = true;
				space = false;
			}
			else if (c == ' ') {
				if (!operand) space = true;
				else {
					space = false;
					operand = false;
				}
			}
			else {
				if (space) sb.append("*");
				sb.append(c);
				space = false;
				operand = false;
			}
		}
		
		// rename functions log, ceil and exp
		renameFunc(sb,"\\\\pi","pi");
		renameFunc(sb,"EXP(","exp(");
		renameFunc(sb,"ln(","log(");
		// integers
		renameFunc(sb,"ceiling(","ceil(");
		renameFunc(sb,"CEILING(","ceil(");
		renameFunc(sb,"FLOOR(","floor(");
		// trigonometric/hyperbolics
		renameFunc(sb,"SIN(","sin(");
		renameFunc(sb,"COS(","cos(");
		renameFunc(sb,"TAN(","tan(");
		renameFunc(sb,"ASIN(","asin(");
		renameFunc(sb,"ACOS(","acos(");
		renameFunc(sb,"ATAN(","atan(");
		renameFunc(sb,"SINH(","sinh(");
		renameFunc(sb,"COSH(","cosh(");
		renameFunc(sb,"TANH(","tanh(");
		renameFunc(sb,"ASINH(","asinh(");
		renameFunc(sb,"ACOSH(","acosh(");
		renameFunc(sb,"ATANH(","atanh(");
		
		// for exponential in new Geogebra version.
		renameFunc(sb,Kernel.EULER_STRING,"2.718"); /*2.718281828*/
		
		/*
		// upper letter greek symbols
		renameFunc(sb,"\u0393","Gamma");
		renameFunc(sb,"\u0394","Delta");
		renameFunc(sb,"\u0398","Theta");
		renameFunc(sb,"\u039b","Lambda");
		renameFunc(sb,"\u039e","Xi");
		renameFunc(sb,"\u03a0","Pi");
		renameFunc(sb,"\u03a3","Sigma");
		renameFunc(sb,"\u03a6","Phi");
		renameFunc(sb,"\u03a8","Psi");
		renameFunc(sb,"\u03a9","Omega");
		
		// lower letter greek symbols
		renameFunc(sb,"\u03b1","alpha");
		renameFunc(sb,"\u03b2","beta");
		renameFunc(sb,"\u03b3","gamma");
		renameFunc(sb,"\u03b4","delta");
		renameFunc(sb,"\u03b5","epsilon");
		renameFunc(sb,"\u03b6","zeta");
		renameFunc(sb,"\u03b7","eta");
		renameFunc(sb,"\u03b8","theta");
		renameFunc(sb,"\u03b9","iota");
		renameFunc(sb,"\u03ba","kappa");
		renameFunc(sb,"\u03bb","lambda");
		renameFunc(sb,"\u03bc","mu");
		renameFunc(sb,"\u03be","xi");
		renameFunc(sb,"\u03c0","pi");
		renameFunc(sb,"\u03c1","rho");
		renameFunc(sb,"\u03c2","varsigma");
		renameFunc(sb,"\u03c3","sigma");
		renameFunc(sb,"\u03c4","tau");
		renameFunc(sb,"\u03c5","upsilon");
		renameFunc(sb,"\u03c6","varphi");
		renameFunc(sb,"\u03c7","chi");
		renameFunc(sb,"\u03c8","psi");
		renameFunc(sb,"\u03c9","omega");
		*/
		
		// remove greek letter escapes
		String greekalpha[] = {"alpha","beta","gamma","delta","epsilon","zeta","eta","theta",
				"iota","kappa","lambda","mu","xi","pi","rho","varsigma","sigma","tau",
				"upsilon","varphi","chi","psi","omega"};
		for(int i = 0; i < greekalpha.length; i++) {
			renameFunc(sb,"\\"+greekalpha[i],greekalpha[i]); // lower case 
			String temps = Character.toString(Character.toUpperCase(greekalpha[i].charAt(0)))
				+ greekalpha[i].substring(1);
			renameFunc(sb,"\\"+temps,temps); // upper case
		}
		
		return new String(sb);
	}
	
	private void renameFunc(StringBuilder sb,String nameFunc,String nameNew){
		int ind = sb.indexOf(nameFunc);
		while(ind > -1){
			sb.replace(ind,ind+nameFunc.length(),nameNew);
			ind = sb.indexOf(nameFunc);
		}
	}
	
	private double maxDefinedValue(GeoFunction f,double a,double b){
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
	
	private double firstDefinedValue(GeoFunction f,double a,double b){
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
	// draw vector with EndArrow(6)
	protected void drawGeoVector(GeoVector geo){
		GeoPoint pointStart = geo.getStartPoint();
		String x1,y1;
		if (pointStart == null){
			x1 = "0"; y1 = "0";
		}
		else {
			x1 = kernel.format(pointStart.getX()/pointStart.getZ());
			y1 = kernel.format(pointStart.getY()/pointStart.getZ());
		}
		double[] coord = new double[3];
		geo.getCoords(coord);
		String x2 = kernel.format(coord[0]+Double.parseDouble(x1));
		String y2 = kernel.format(coord[1]+Double.parseDouble(y1));
		
		if(!compact)
			code.append("\n");
		if(compactcse5)
			code.append("D(");
		else
			code.append("draw(");
		addPoint(x1,y1,code);
		code.append("--");
		addPoint(x2,y2,code);
		if(LineOptionCode(geo,true) != null) {
			code.append(",");
			if(!compact)
				code.append(" ");
			code.append(LineOptionCode(geo,true));
		}
		code.append(",EndArrow(6)); ");
	}
	
	private void drawCircle(GeoConic geo){
		StringBuilder tempsb = new StringBuilder();
		boolean nofill = geo.getAlphaValue() < 0.05;
		
		if (xunit == yunit){
			// draw a circle
			double x = geo.getTranslationVector().getX();
			double y = geo.getTranslationVector().getY();
			double r = geo.getHalfAxes()[0];
			String tmpr = kernel.format(r); // removed *xunit, unsure of function
			
			if(nofill) {
				if(!compact)
					tempsb.append("\n");
				if(compactcse5)
					tempsb.append("D(CR(");
				else
					tempsb.append("draw(circle(");
			}
			else {
				startTransparentFill(tempsb);
				if(compactcse5)
					tempsb.append("CR(");
				else
					tempsb.append("circle(");
			}
			addPoint(kernel.format(x),kernel.format(y),tempsb);
			tempsb.append(",");
			if (Double.parseDouble(tmpr)!=0) 
				tempsb.append(tmpr);
			else 
				tempsb.append(r);
			tempsb.append(")");
			if(nofill) {
				endDraw(geo,tempsb);
			}
			else
				endTransparentFill(geo,tempsb);
		}
		else {
		// draw an ellipse by scaling a circle
			double x1=geo.getTranslationVector().getX();
			double y1=geo.getTranslationVector().getY();
			double r1=geo.getHalfAxes()[0];
			double r2=geo.getHalfAxes()[1];

			if(nofill) {
				if(!compact)
					tempsb.append("\n");
				if(compactcse5)
					tempsb.append("D(");
				else
					tempsb.append("draw(");
			}
			else
				startTransparentFill(tempsb);
			tempsb.append("shift(");
			addPoint(kernel.format(x1),kernel.format(y1),tempsb);
			tempsb.append(")*xscale(");
			tempsb.append(kernel.format(r1));
			tempsb.append(")*yscale(");
			tempsb.append(kernel.format(r2));
			tempsb.append(")*unitcircle");
			if(nofill) 
				endDraw(geo,tempsb);
			else
				endTransparentFill(geo,tempsb);
		}
		
		if(nofill)
			code.append(tempsb);
		else
			codeFilledObject.append(tempsb);
	}
	
	protected void drawGeoConic(GeoConic geo){	
		switch(geo.getType()){
		// if conic is a circle
			case GeoConic.CONIC_CIRCLE:
				drawCircle(geo);
			break;
		// if conic is an ellipse
			case GeoConic.CONIC_ELLIPSE:
				AffineTransform at=geo.getAffineTransform();
				double eigenvecX=at.getScaleX();
				double eigenvecY=at.getShearY();
				double x1=geo.getTranslationVector().getX();
				double y1=geo.getTranslationVector().getY();
				double r1=geo.getHalfAxes()[0];
				double r2=geo.getHalfAxes()[1];
				double angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
				
				// use scale operator to draw ellipse
				if(compactcse5)
					code.append("D(shift(");
				else
					code.append("draw(shift(");
				addPoint(kernel.format(x1),kernel.format(y1),code);
				code.append(")*rotate(");
				code.append(kernel.format(angle));
				code.append(")*xscale(");
				code.append(kernel.format(r1));
				code.append(")*yscale(");
				code.append(kernel.format(r2));
				code.append(")*unitcircle");
				endDraw(geo);
			break;
			
		// if conic is a parabola 
			case GeoConic.CONIC_PARABOLA:		
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
		        x0 = 4 * x0 / p;
		        int i = 4, k2 = 16;
		        while (k2 < x0) {
		            i += 2;
		            k2 = i * i;
		        }
		        //x0 = k2/2 * p; // x = k*p
		        x0 = i * p;    // y = sqrt(2k p^2) = i p
				angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX))-90;
				
				 // write real parabola (real x) function
				parabolaCount++;
				if(!compact)
					code.append("\n");
				code.append("real p");
				if(!compact)
					code.append("arabola");
				code.append(parabolaCount);
				packSpace("(real x)",code);
				code.append("{return x^2/2/");
				if(compact)
					code.append(kernel.format(p));
				else
					code.append(p);
				code.append(";} ");
				
				 // use graph to plot parabola
				if(!compact)
					code.append("\n");
				if(compactcse5)
					code.append("D(shift(");
				else
					code.append("draw(shift(");
				addPoint(kernel.format(x1),kernel.format(y1),code);
				code.append(")*rotate(");
				code.append(kernel.format(angle));
				code.append(")*graph(p");
				if(!compact)
					code.append("arabola");
				code.append(parabolaCount);
				code.append(",");
				code.append(kernel.format(-x0));
				code.append(",");
				code.append(kernel.format(x0));
				code.append(")");
				endDraw(geo);
				
				if(!compact)
					code.append("/* parabola construction */");
			break;
			
			case GeoConic.CONIC_HYPERBOLA:
// 				parametric: (a(1+t^2)/(1-t^2), 2bt/(1-t^2))
				at=geo.getAffineTransform();
				eigenvecX=at.getScaleX();
				eigenvecY=at.getShearY();
				x1=geo.getTranslationVector().getX();
				y1=geo.getTranslationVector().getY();
				r1=geo.getHalfAxes()[0];
				r2=geo.getHalfAxes()[1];
				angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
				
				hyperbolaCount++;
				if(!compact)
					code.append("\n");
				if(!compact)
					code.append("pair hyperbolaLeft");
				else 
					code.append("pair hl");
				code.append(hyperbolaCount);
				packSpace("(real t)",code);
				code.append("{return (");
				code.append(kernel.format(r1));
				code.append("*(1+t^2)/(1-t^2),");
				code.append(kernel.format(r2));
				code.append("*2*t/(1-t^2));} ");
				if(!compact)
					code.append("pair hyperbolaRight");
				else 
					code.append("pair hr");
				code.append(hyperbolaCount);
				packSpace("(real t)",code);
				code.append("{return (");
				code.append(kernel.format(r1));
				code.append("*(-1-t^2)/(1-t^2),");
				code.append(kernel.format(r2));
				code.append("*(-2)*t/(1-t^2));} ");
				
				 // use graph to plot both halves of hyperbola
				if(!compact)
					code.append("\n");
				if(compactcse5)
					code.append("D(shift(");
				else
					code.append("draw(shift(");
				addPoint(kernel.format(x1),kernel.format(y1),code);
				code.append(")*rotate(");
				code.append(kernel.format(angle));
				if(!compact)
					code.append(")*graph(hyperbolaLeft");
				else 
					code.append(")*graph(hl");
				code.append(hyperbolaCount);
				code.append(",-0.99,0.99)");	// arbitrary to approach (-1,1)
				endDraw(geo);
				
				if(compactcse5)
					code.append("D(shift(");
				else
					code.append("draw(shift(");
				addPoint(kernel.format(x1),kernel.format(y1),code);
				code.append(")*rotate(");
				code.append(kernel.format(angle));
				if(!compact)
					code.append(")*graph(hyperbolaRight");
				else 
					code.append(")*graph(hr");
				code.append(hyperbolaCount);
				code.append(",-0.99,0.99)");
				endDraw(geo);
				
				if(!compact)
					code.append("/* hyperbola construction */");
				
				break;
		}	
	}
	// draws dot
	protected void drawGeoPoint(GeoPoint gp){
		if (frame.getExportPointSymbol()){
			double x = gp.getX(),
			       y = gp.getY(),
			       z = gp.getZ();
			x = x/z;
			y = y/z;
			gp.getNameDescription();
			int dotstyle = gp.getPointStyle();
			if (dotstyle == -1) { // default
				dotstyle = app.getEuclidianView().getPointStyle();
			}
			if(dotstyle != EuclidianView.POINT_STYLE_DOT) {
				drawSpecialPoint(gp);
			}
			else {	
				if(!compact)
					codePoint.append("\n");
				if(compactcse5)
					codePoint.append("D(");
				else
					codePoint.append("dot(");
				addPoint(kernel.format(x),kernel.format(y),codePoint);			
				PointOptionCode(gp,codePoint);
				codePoint.append("); ");
			}	
		}
	}
	/** Draws a point with a special point style (usually uses draw() or filldraw() command).
	 * @param geo GeoPoint with style not equal to the standard dot style.
	 */
	protected void drawSpecialPoint(GeoPoint geo){
		// radius = dotsize (pt) * (2.54 cm)/(72 pt per inch) * XUnit / cm
		double dotsize = (double) geo.getPointSize();
		double radius = dotsize * (2.54/72) * (frame.getXUnit());
		int dotstyle   = geo.getPointStyle();
		if (dotstyle == -1) { // default
			dotstyle = app.getEuclidianView().getPointStyle();
		}
		double x = geo.getX(),
		       y = geo.getY(),
		       z = geo.getZ();
		x = x/z;
		y = y/z;
		Color dotcolor = geo.getObjectColor();
		
		switch(dotstyle){
			case EuclidianView.POINT_STYLE_CROSS:
				startDraw();
				code.append("shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("(expi(pi/4)--expi(5*pi/4)");
				if(compactcse5) // compromise for cse5, does not allow join operator
					code.append("--(0,0)--");
				else
					code.append("^^");
				code.append("expi(3*pi/4)--expi(7*pi/4))");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_CIRCLE:
				// use dot(..,UnFill(0)) command in lieu of filldraw
				if(!compactcse5) {
					codePoint.append("dot(");
					addPoint(kernel.format(x),kernel.format(y),codePoint);
					// 4.0 slightly arbitrary. 6.0 should be corrective factor, but too small. 
					PointOptionCode(geo,codePoint,((double) geo.getPointSize())/4.0);
					codePoint.append(",UnFill(0)); ");
				}
				// use filldraw(CR) for cse5
				else {
					startDraw();
					// if(compactcse5)
						code.append("CR((");
					// else
					//	code.append("circle((");
					code.append(kernel.format(x) + "," + kernel.format(y) + "),");
					code.append(kernel.format(radius));
					code.append(")");
					
					if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
						code.append(",");				
						ColorCode(dotcolor,code);
					}
					code.append("); ");
				}
			break;
			case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:
				startDraw();
				code.append("shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((1,0)--(0,1)--(-1,0)--(0,-1)--cycle)");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_FILLED_DIAMOND:
				if(!compact)
					code.append("\n");
				code.append("fill(shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((1,0)--(0,1)--(-1,0)--(0,-1)--cycle)");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_PLUS:
				startDraw();
				code.append("shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((0,1)--(0,-1)");
				if(compactcse5) // compromise for cse5, does not allow join operator
					code.append("--(0,0)--");
				else
					code.append("^^");
				code.append("(1,0)--(-1,0))");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_TRIANGLE_EAST:
				if(!compact)
					code.append("\n");
				code.append("fill(shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:
				if(!compact)
					code.append("\n");
				code.append("fill(shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("rotate(90)*scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:
				if(!compact)
					code.append("\n");
				code.append("fill(shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("rotate(270)*scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			case EuclidianView.POINT_STYLE_TRIANGLE_WEST:
				if(!compact)
					code.append("\n");
				code.append("fill(shift((" + kernel.format(x) + "," + kernel.format(y) + "))*");
				code.append("rotate(180)*scale(");
				code.append(kernel.format(radius));
				code.append(")*"); 
				code.append("((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
				
				if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
					code.append(",");				
					ColorCode(dotcolor,code);
				}
				code.append("); ");
			break;
			default:
			break;
		}
		if(!compact)
			code.append("/* special point */");
	}
	// draws line
	protected void drawGeoLine(GeoLine geo){
		double x = geo.getX(),
		       y = geo.getY(),
		       z = geo.getZ();
		
		if (y != 0){
			startDraw();
			 // new evaluation: [-x/y]*[xmin or xmax]-(z/y)
			code.append("(xmin,");
			code.append(kernel.format(-x/y));
			code.append("*xmin");
			if(z/y < 0 || kernel.format(-z/y).equals("0")) 
				code.append("+");
			code.append(kernel.format(-z/y));
			code.append(")");
			// String tmpy=kernel.format(y);
			// if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			// else code.append(y);
			code.append("--(xmax,");
			code.append(kernel.format(-x/y));
			code.append("*xmax");
			if(z/y < 0 || kernel.format(-z/y).equals("0")) 
				code.append("+");
			code.append(kernel.format(-z/y));
			// if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			// else code.append(y);
			code.append(")");
			endDraw(geo);	
		}
		else { // vertical line
			if(!compact)
				code.append("\n");
			if(compactcse5)
				code.append("D((");
			else
				code.append("draw((");
			String s=kernel.format(-z/x);
			code.append(s);
			code.append(",ymin)--(");
			code.append(s);
			code.append(",ymax)");
			endDraw(geo);
		}
		if(!compact)
			code.append("/* line */");
	}
	// draws segment
	protected void drawGeoSegment(GeoSegment geo){
		double[] A = new double[2],
		         B = new double[2];
		GeoPoint pointStart = geo.getStartPoint();
		GeoPoint pointEnd = geo.getEndPoint();
		pointStart.getInhomCoords(A);
		pointEnd.getInhomCoords(B);
		String x1 = kernel.format(A[0]),
		       y1 = kernel.format(A[1]),
		       x2 = kernel.format(B[0]),
		       y2 = kernel.format(B[1]);
		int deco = geo.decorationType;
		
		if(!compact)
			code.append("\n");
		if(!compactcse5) 
			code.append("draw(");
		else
			code.append("D(");
		addPoint(x1,y1,code);
		code.append("--");
		addPoint(x2,y2,code);
		endDraw(geo);
		
		if (deco != GeoElement.DECORATION_NONE) mark(A,B,deco,geo);
	}
	
	protected void drawLine(double x1,double y1,double x2,double y2,GeoElement geo){
		String sx1 = kernel.format(x1);
		String sy1 = kernel.format(y1);
		String sx2 = kernel.format(x2);
		String sy2 = kernel.format(y2);

		startDraw();
		code.append("");
		addPoint(sx1,sy1,code);
		code.append("--");
		addPoint(sx2,sy2,code);
		endDraw(geo);
	}
	
	protected void drawGeoRay(GeoRay geo){
		GeoPoint pointStart = geo.getStartPoint();
		double x1 = pointStart.getX();
		double z1 = pointStart.getZ();
		x1 = x1/z1;
		String y1 = kernel.format(pointStart.getY()/z1);
		
		double x = geo.getX();
		double y = geo.getY();
		double z = geo.getZ();
		double yEndpoint; // records explicit endpoint
		// String tmpy = kernel.format(y);
		double inf = xmin, sup = xmax;
		if (y > 0) {
			inf = x1;
			yEndpoint = (-z-x*inf)/y;
		}
		else {
			sup = x1;
			yEndpoint = (-z-x*sup)/y;
		}
	
		 // format: draw((inf,f(inf))--(xmax,f(xmax)));
		 //     OR: draw((xmin,f(xmin))--(sup,f(sup)));
		 // old evaluation: (-(z)-(x)*[inf or sup])/y
		 // new evaluation: [-x/y]*[inf or sup]-(z/y)
		startDraw();
		if (y != 0){	
			if (y > 0) {
				addPoint(kernel.format(inf),kernel.format(yEndpoint),code);
				code.append("--");
				code.append("(xmax");
				code.append(",");
				code.append(kernel.format(-x/y));
				code.append("*xmax");
				if(z/y < 0 || kernel.format(-z/y).equals("0")) 
					code.append("+");
				code.append(kernel.format(-z/y));
				// code.append(")/");
				// if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
				// else code.append(y);
				code.append(")");	
			}
			else {
				addPoint(kernel.format(sup),kernel.format(yEndpoint),code);
				code.append("--");
				code.append("(xmin,");
				code.append(kernel.format(-x/y));
				code.append("*xmin");
				if(z/y < 0 || kernel.format(-z/y).equals("0")) 
					code.append("+");
				code.append(kernel.format(-z/y));
				// code.append("/");
				// if (Double.parseDouble(tmpy) != 0) code.append(tmpy);
				// else code.append(y);
				code.append(")");
			}
			endDraw(geo);
		}
		else {
			addPoint(kernel.format(x1),y1,code);
			code.append("--(");
			code.append(kernel.format(x1));
			code.append(",");
			if (-x>0)
				code.append("ymax");
			else
				code.append("ymin");
			code.append(")");
			endDraw(geo);
		}
		if(!compact)
			code.append("/* ray */");
	}
    
	private void initUnitAndVariable(){
		// Initaialze units, dot style, dot size .... 
		/* codeBeginPic.append("\\psset{xunit=");
		codeBeginPic.append(sci2dec(xunit));
		codeBeginPic.append("cm,yunit=");
		codeBeginPic.append(sci2dec(yunit));
		codeBeginPic.append("cm,algebraic=true,dotstyle=o,dotsize=");
		codeBeginPic.append(EuclidianView.DEFAULT_POINT_SIZE);
		codeBeginPic.append("pt 0");
		codeBeginPic.append(",linewidth=");
		codeBeginPic.append(kernel.format(EuclidianView.DEFAULT_LINE_THICKNESS/2*0.8));
		codeBeginPic.append("pt,arrowsize=3pt 2,arrowinset=0.25}\n"); */
    	
    	if (!compact) {
			codePreamble.append("\nreal labelscalefactor = 0.5; /* changes label-to-point distance */");
		    codePreamble.append("\npen dps = linewidth(0.7) + fontsize(");
		    codePreamble.append(fontsize);
		    codePreamble.append("); defaultpen(dps); /* default pen style */ ");
		    if(!((ExportFrame) frame).getKeepDotColors())
		    	codePreamble.append("\npen dotstyle = black; /* point style */ \n");
		}
		else if (!compactcse5) {
			codePreamble.append("real lsf=0.5; pen dps=linewidth(0.7)+fontsize(");
			codePreamble.append(fontsize);
			codePreamble.append("); defaultpen(dps); ");
			if(!((ExportFrame) frame).getKeepDotColors())
				codePreamble.append("pen ds=black; ");
		}
		else {
			codePreamble.append("real lsf=0.5; pathpen=linewidth(0.7); pointpen=black; pen fp = fontsize(");
			codePreamble.append(fontsize);
			codePreamble.append("); pointfontpen=fp; ");
		}
    	codePreamble.append("real xmin");
    	packSpace("=",codePreamble);
    	codePreamble.append(kernel.format(xmin));
    	codePreamble.append(",xmax");
    	packSpace("=",codePreamble);
    	codePreamble.append(kernel.format(xmax));
    	codePreamble.append(",ymin");
    	packSpace("=",codePreamble);
    	codePreamble.append(kernel.format(ymin));
    	codePreamble.append(",ymax");
    	packSpace("=",codePreamble);
    	codePreamble.append(kernel.format(ymax));
    	codePreamble.append("; ");
    	if(!compact) {
    		codePreamble.append(" /* image dimensions */\n");
    	}
    	else {
    		// codePreamble.append("\n");
    	}
    }
    // Generate list of pairs for cse5 code to use
    private void initPointDeclarations(){
    	if(!compactcse5) return;
    	Iterator<GeoPoint> it = pointList.iterator();
    	boolean comma = false;
    	
    	while(it.hasNext()) {
    		GeoPoint gp = (GeoPoint) it.next();
    		if(gp.getPointStyle() == EuclidianView.POINT_STYLE_DOT
    		|| gp.getPointStyle() == EuclidianView.POINT_STYLE_CIRCLE) {
    			double x = gp.getX(), y = gp.getY(), z = gp.getZ();
    			x /= z; y /= z;
    			String pairString = "(" + kernel.format(x) + "," + kernel.format(y) + ")";
    			String pointName = gp.getLabel();
    			boolean isVariable = true;
    			
    			for(int i = 0; i < pointName.length(); i++) {
    				if(!Character.isLetterOrDigit(pointName.charAt(i)) && pointName.charAt(i) != '_')
    					isVariable = false;
    			}
    			if(codePointDecl.indexOf(pairString) == -1 && isVariable) {
	    			if(comma)
	    				codePointDecl.append(", ");
	    			else
	    				comma = true;
	    			codePointDecl.append(gp.getLabel());
	    			codePointDecl.append("=");
	    			codePointDecl.append(pairString);
    			}
    		}
    	}
    	if(comma) {
    		codePointDecl.insert(0, "\npair ");
    		codePointDecl.append("; ");
    	}
    }
    
	// if label is visible, draw it
	protected void drawLabel(GeoElement geo,Drawable drawGeo){
		try{
			if (geo.isLabelVisible()){
				String name;
				if (geo.getLabelMode() == GeoElement.LABEL_CAPTION)
					name = geo.getLabelDescription();
				else if (compactcse5) 
					name = Util.toLaTeXString(geo.getLabelDescription(),true);
				else
					name = "$"+Util.toLaTeXString(geo.getLabelDescription(),true)+"$";
				name = convertUnicodeToLatex(name);
				if (name.indexOf("\u00b0") != -1){
					name = name.replaceAll("\u00b0", "^\\\\circ");
				}
	
				if (drawGeo == null) 
					drawGeo = euclidianView.getDrawableFor(geo);
				double xLabel = drawGeo.getxLabel();
				double yLabel = drawGeo.getyLabel();
				xLabel = euclidianView.toRealWorldCoordX(Math.round(xLabel));
				yLabel = euclidianView.toRealWorldCoordY(Math.round(yLabel));
				boolean isPointLabel = false;
				
				Color geocolor = geo.getObjectColor();

				if(!compact)
					codePoint.append("\n");
				if(compactcse5 && geo.getLabelMode() != GeoElement.LABEL_CAPTION)
					codePoint.append("MP(\"");
				else
					codePoint.append("label(\""); 
				codePoint.append(name);
				codePoint.append("\",(");
				codePoint.append(kernel.format(xLabel));
				codePoint.append(",");
				codePoint.append(kernel.format(yLabel));
				codePoint.append("),");
				if(!compact)
					codePoint.append(" ");
				codePoint.append("NE");
				packSpace("*",codePoint);
				if(compact)
					codePoint.append("lsf");
				if(!compact)
					codePoint.append("labelscalefactor");
			
				// check if label is of point
				isPointLabel = (geocolor.equals(Color.BLUE) || ColorEquals(geocolor,new Color(124,124,255))) // xdxdff
									// is of the form "A" or "$A$"
				            && ( ((name.length() == 1) && Character.isUpperCase(name.charAt(0)))
				            || ( ((name.length() == 3) && name.charAt(0) == '$' && name.charAt(2) == '$' 
				                && Character.isUpperCase(name.charAt(1)))) ); 
				isPointLabel = isPointLabel || geo.isGeoPoint();
				// replaced with pointfontpen:
				// if(compactcse5) {
				// 	codePoint.append(",fp");
				// }
				if(isPointLabel && !((ExportFrame) frame).getKeepDotColors()) {
					// configurable or default black?
					// temp empty
				}
				else if(!geocolor.equals(Color.BLACK)){
					if(compactcse5)
						codePoint.append(",fp+");
					else
						codePoint.append(",");
					ColorCode(geocolor,codePoint);
				}
				codePoint.append("); ");
			}
		}
		// For GeoElement that don't have a Label
		// For example (created with geoList)
		catch(NullPointerException e){}
	}	
	
	/** Returns whether or not c1 and c2 are equivalent colors, when rounded to the nearest hexadecimal integer.
	 * @param c1 The first Color object.
	 * @param c2 The second Color object to compare with.
	 * @return Whether c1 and c2 are equivalent colors, to rounding.
	 */
	boolean ColorEquals(Color c1, Color c2) {
		return kernel.format(c1.getRed()/255d).equals(kernel.format(c2.getRed()/255d))
		    && kernel.format(c1.getGreen()/255d).equals(kernel.format(c2.getGreen()/255d))
		    && kernel.format(c1.getBlue()/255d).equals(kernel.format(c2.getBlue()/255d));
	}
	
    // Draw the grid 
	private void drawGrid(){
		Color GridCol = euclidianView.getGridColor();
		double[] GridDist = euclidianView.getGridDistances();
		boolean GridBold = euclidianView.getGridIsBold();
		int GridLine = euclidianView.getGridLineStyle();
		
		if(!compact) {
			 // draws grid using Asymptote loops
			codeBeginPic.append("\n /* draw grid of horizontal/vertical lines */");
			codeBeginPic.append("\npen gridstyle = ");
			if(GridBold)
				codeBeginPic.append("linewidth(1.0)");	
			else
				codeBeginPic.append("linewidth(0.7)");	
			codeBeginPic.append(" + ");
			ColorCode(GridCol,codeBeginPic);
			if(GridLine != EuclidianView.LINE_TYPE_FULL) {
				codeBeginPic.append(" + ");
				LinestyleCode(GridLine, codeBeginPic);
			}
			codeBeginPic.append("; real gridx = ");
			codeBeginPic.append(kernel.format(GridDist[0]));
			codeBeginPic.append(", gridy = ");
			codeBeginPic.append(kernel.format(GridDist[1]));
			codeBeginPic.append("; /* grid intervals */" 
					   		  + "\nfor(real i = ceil(xmin/gridx)*gridx; "
			                  + "i <= floor(xmax/gridx)*gridx; i += gridx)");
			codeBeginPic.append("\n draw((i,ymin)--(i,ymax), gridstyle);");			
			codeBeginPic.append("\nfor(real i = ceil(ymin/gridy)*gridy; "
							  + "i <= floor(ymax/gridy)*gridy; i += gridy)");
			codeBeginPic.append("\n draw((xmin,i)--(xmax,i), gridstyle);");			
			codeBeginPic.append("\n /* end grid */ \n");
		}
		else if(!compactcse5) {
			codeBeginPic.append("\n/*grid*/ pen gs=");
			if(GridBold)
				codeBeginPic.append("linewidth(1.0)");	
			else
				codeBeginPic.append("linewidth(0.7)");	
			codeBeginPic.append("+");
			ColorCode(GridCol,codeBeginPic);
			if(GridLine != EuclidianView.LINE_TYPE_FULL) {
				codeBeginPic.append("+");
				LinestyleCode(GridLine, codeBeginPic);
			}
			codeBeginPic.append("; real gx=");
			codeBeginPic.append(kernel.format(GridDist[0]));
			codeBeginPic.append(",gy=");
			codeBeginPic.append(kernel.format(GridDist[1]));
			codeBeginPic.append(";\nfor(real i=ceil(xmin/gx)*gx;"
			                  + "i<=floor(xmax/gx)*gx;i+=gx)");
			codeBeginPic.append(" draw((i,ymin)--(i,ymax),gs);");			
			codeBeginPic.append(" for(real i=ceil(ymin/gy)*gy;"
							  + "i<=floor(ymax/gy)*gy;i+=gy)");
			codeBeginPic.append(" draw((xmin,i)--(xmax,i),gs); ");			
		}
		else { // with cse5 shorthands
			codeBeginPic.append("\npen gs=");
			if(GridBold)
				codeBeginPic.append("linewidth(1.0)");	
			else
				codeBeginPic.append("linewidth(0.7)");	
			codeBeginPic.append("+");
			ColorCode(GridCol,codeBeginPic);
			if(GridLine != EuclidianView.LINE_TYPE_FULL) {
				codeBeginPic.append("+");
				LinestyleCode(GridLine, codeBeginPic);
			}
			codeBeginPic.append("; real gx=");
			codeBeginPic.append(kernel.format(GridDist[0]));
			codeBeginPic.append(",gy=");
			codeBeginPic.append(kernel.format(GridDist[1]));
			codeBeginPic.append(";\nfor(real i=ceil(xmin/gx)*gx;"
			                  + "i<=floor(xmax/gx)*gx;i+=gx)");
			codeBeginPic.append(" D((i,ymin)--(i,ymax),gs);");			
			codeBeginPic.append(" for(real i=ceil(ymin/gy)*gy;"
							  + "i<=floor(ymax/gy)*gy;i+=gy)");
			codeBeginPic.append(" D((xmin,i)--(xmax,i),gs); ");	
		}
	}
	
	// Draws Axis presuming shown
	// note: may shift around relative positions of certain labels. 
	private void drawAxis(){
		boolean xAxis = euclidianView.getShowXaxis();
		boolean yAxis = euclidianView.getShowYaxis();
		boolean bx = euclidianView.getShowAxesNumbers()[0];
		boolean by = euclidianView.getShowAxesNumbers()[1];
		double Dx = euclidianView.getAxesNumberingDistances()[0];
		double Dy = euclidianView.getAxesNumberingDistances()[1];
		String[] label = euclidianView.getAxesLabels();
		String[] units = euclidianView.getAxesUnitLabels();
		Color axisCol = euclidianView.getAxesColor();
		int axisStyle = euclidianView.getAxesLineStyle();
		boolean axisBold= (axisStyle == EuclidianView.AXES_LINE_TYPE_ARROW_BOLD) 
		               || (axisStyle == EuclidianView.AXES_LINE_TYPE_FULL_BOLD);
		boolean axisArrow= (axisStyle == EuclidianView.AXES_LINE_TYPE_ARROW_BOLD) 
                        || (axisStyle == EuclidianView.AXES_LINE_TYPE_ARROW);
		String lx = "", ly = "";
		if(label[0] != null)
			lx = "$"+Util.toLaTeXString(label[0], true)+"$";
		if(label[1] != null)
			ly = "$"+Util.toLaTeXString(label[1], true)+"$";
/* follow format: 		
		void xaxis(picture pic=currentpicture, Label L="", axis axis=YZero,
                real xmin=-infinity, real xmax=infinity, pen p=currentpen, 
                ticks ticks=NoTicks, arrowbar arrow=None, bool above=false);
 */
		
		if(xAxis || yAxis) {
			codeBeginPic.append("\n");
			codeBeginPic.append("Label laxis; laxis.p");
			packSpace("=",codeBeginPic);
			codeBeginPic.append("fontsize(");
			codeBeginPic.append(((ExportFrame) frame).getFontSize());
			codeBeginPic.append("); ");
			if (!bx || !by) { // implement no number shown
				codeBeginPic.append("string blank");
				packSpace("(real x)",codeBeginPic);
				codeBeginPic.append("{return \"\";} ");
			}
			if(bx || by) { // implement unit labels
				if(units[0] != null && !units[0].equals("")) {
					codeBeginPic.append("string ");
					if(compact)
						codeBeginPic.append("xlbl");
					else
						codeBeginPic.append("xaxislabel");
					packSpace("(real x)",codeBeginPic);
					codeBeginPic.append("{");
					
					// unit label is pi: format -1pi, 0pi, 1pi
					if(units[0].equals("\u03c0")) { 
						// create labeling function for special labels if n = -1,0,1
						codeBeginPic.append("int n=round(x/pi); ");
						codeBeginPic.append("if(n==-1) return \"$-\\pi$\"; ");
						codeBeginPic.append("if(n==1) return \"$\\pi$\"; ");
						codeBeginPic.append("if(n==0) return \"$0$\"; ");
					}
					codeBeginPic.append("return \"$\"");
					packSpace("+",codeBeginPic);
					// unit label is pi
					if(units[0].equals("\u03c0")) { 
						codeBeginPic.append("string(n)");
						packSpace("+",codeBeginPic);
						codeBeginPic.append("\"\\pi");
					}
					// unit label is degrees symbol
					else if(units[0].equals("\u00b0")) { 
						codeBeginPic.append("string(x)");
						packSpace("+",codeBeginPic);
						codeBeginPic.append("\"^\\circ");						
					}
					else {
						codeBeginPic.append("string(x)");
						packSpace("+",codeBeginPic);
						codeBeginPic.append("\"\\,\\mathrm{"+units[0]+"}");
					}
					codeBeginPic.append("$\";} ");
				}
				if(units[1] != null && !units[1].equals("")) {
					codeBeginPic.append("string ");
					if(compact)
						codeBeginPic.append("ylbl");
					else
						codeBeginPic.append("yaxislabel");
					packSpace("(real x)",codeBeginPic);
					codeBeginPic.append("{");
					
					// unit label is pi: format -1pi, 0pi, 1pi
					if(units[1].equals("\u03c0")) { 
						codeBeginPic.append("int n=round(x/pi); ");
						codeBeginPic.append("if(n==-1) return \"$-\\pi$\"; ");
						codeBeginPic.append("if(n==1) return \"$\\pi$\"; ");
						codeBeginPic.append("if(n==0) return \"$0$\"; ");
					}
					codeBeginPic.append("return \"$\"");
					packSpace("+",codeBeginPic);
					// unit label is pi
					if(units[1].equals("\u03c0")) { 
						codeBeginPic.append("string(round(x/pi))");
						packSpace("+",codeBeginPic);
						codeBeginPic.append("\"\\pi");
					}
					// unit label is degrees symbol
					else if(units[1].equals("\u00b0")) { 
						codeBeginPic.append("string(x)");
						packSpace("+",codeBeginPic);
						codeBeginPic.append("\"^\\circ");						
					}
					else {
						codeBeginPic.append("string(x)");
						packSpace("+",codeBeginPic);
						// put units in text form
						codeBeginPic.append("\"\\,\\mathrm{"+units[1]+"}");
					}
					codeBeginPic.append("$\";} ");
				}
			}
			codeBeginPic.append("\n");
		}
		if(xAxis) {
			codeBeginPic.append("xaxis(");
			if (label[0] != null) // axis label
				codeBeginPic.append("\""+lx+"\",");
			codeBeginPic.append("xmin,xmax"); // non-fixed axes?
			
			// axis pen style
			if(axisCol != Color.BLACK) {
				codeBeginPic.append(",");
				// catch for other options not changing.
				if(compactcse5)
					codeBeginPic.append("pathpen+");
				else
					codeBeginPic.append("defaultpen+");
				ColorCode(axisCol,codeBeginPic);
				if(axisBold) {
					codeBeginPic.append("+linewidth(1.2)");
				}
			}
			else if(axisBold) {
				codeBeginPic.append(",linewidth(1.2)");
			}
			codeBeginPic.append(",Ticks(laxis,");
			if(!bx) // no tick labels
				codeBeginPic.append("blank,");
			else if(units[0] != null && !units[0].equals("")) {
				if(compact)
					codeBeginPic.append("xlbl,");
				else
					codeBeginPic.append("xaxislabel,");
			}
			codeBeginPic.append("Step=");
			codeBeginPic.append(Dx);
			codeBeginPic.append(",Size=2,NoZero)");
			if(axisArrow)
				codeBeginPic.append(",Arrows(6)");
			codeBeginPic.append(",above=true); ");
		}
		if(yAxis) {
			codeBeginPic.append("yaxis(");
			if (label[1] != null) // axis label
				codeBeginPic.append("\""+ly+"\",");
			codeBeginPic.append("ymin,ymax"); // non-fixed axes?
			
			// axis pen style
			if(axisCol != Color.BLACK) {
				if(compactcse5)
					codeBeginPic.append(",pathpen+");
				else
					codeBeginPic.append(",defaultpen+");
				ColorCode(axisCol,codeBeginPic);
				if(axisBold) {
					codeBeginPic.append("+linewidth(1.2)");
				}
			}
			else if(axisBold) {
				codeBeginPic.append(",linewidth(1.2)");
			}
			codeBeginPic.append(",Ticks(laxis,");
			if(!by) // no tick labels
				codeBeginPic.append("blank,");
			else if(units[1] != null && !units[1].equals("")) {
				if(compact)
					codeBeginPic.append("ylbl,");
				else
					codeBeginPic.append("yaxislabel,");
			}
			codeBeginPic.append("Step=");
			codeBeginPic.append(Dy);
			codeBeginPic.append(",Size=2,NoZero)");
			if(axisArrow)
				codeBeginPic.append(",Arrows(6)");
			codeBeginPic.append(",above=true); ");
		}
		if((xAxis || yAxis) && !compact)
			codeBeginPic.append("/* draws axes; NoZero hides '0' label */ ");
	}
	// Returns point style code with size dotsize. Includes comma.
	private void PointOptionCode(GeoPoint geo, StringBuilder sb, double dotsize){
		Color dotcolor = geo.getObjectColor();
		int dotstyle   = geo.getPointStyle();
		if (dotstyle == -1) { // default
			dotstyle = app.getEuclidianView().getPointStyle();
		}
		boolean comma = false; // add comma
		
		if (dotsize != EuclidianView.DEFAULT_POINT_SIZE){
			// comma needed
			comma=true;
			sb.append(",linewidth(");
			// Note: Asymptote magnifies default dotsizes by a scale of 6 x linewidth,
			// but it does not magnify passed-in arguments. So the dotsize here
			// is approximately of the correct size. 
			sb.append(kernel.format(dotsize));
			sb.append("pt)");
		}
		if (!dotcolor.equals(Color.BLACK) && ((ExportFrame) frame).getKeepDotColors()){
			if (comma) packSpace("+",sb);
			else sb.append(",");
			comma=true;
			
			ColorCode(dotcolor,sb);
		}
		else if (!((ExportFrame) frame).getKeepDotColors() && !compactcse5){
			if (comma) packSpace("+",sb);
			else sb.append(",");
			comma = true;
			
			/* cse5 has pointpen attribute */
			if(!compact)
				sb.append("dotstyle");
			else if(!compactcse5)
				sb.append("ds");
		}
		// catch mistake
		if (dotstyle != EuclidianView.POINT_STYLE_DOT) {
			if (comma) packSpace("+",sb);
			else sb.append(",");
			comma = true;
			sb.append("invisible");
		}
	}
	// Returns point style code. Includes comma.
	private void PointOptionCode(GeoPoint geo, StringBuilder sb){
		PointOptionCode(geo, sb, (double) geo.getPointSize());
	}
	// Line style code; does not include comma.
	private String LineOptionCode(GeoElement geo,boolean transparency){
		StringBuilder sb = new StringBuilder(); 
		Color linecolor = geo.getObjectColor();
		int linethickness = geo.getLineThickness();
		int linestyle = geo.getLineType();

		boolean noPlus = true;
		if (linethickness != EuclidianView.DEFAULT_LINE_THICKNESS){
			// first parameter
			noPlus = false;
			sb.append("linewidth(");
			sb.append(kernel.format(linethickness/2.0*0.8));
			sb.append(")");
		}
		if (linestyle != EuclidianView.DEFAULT_LINE_TYPE){
			if (!noPlus) 
				packSpace("+",sb);
			else noPlus = false;
			LinestyleCode(linestyle,sb);
		}
		if (!linecolor.equals(Color.BLACK)){
			if (!noPlus) 
				packSpace("+",sb);
			else noPlus = false;
			ColorCode(linecolor,sb);
		}
		if (transparency && geo.isFillable() && geo.getAlphaValue() > 0.0f){
			/* TODO: write opacity code?
			if (!noPlus) 
				packSpace("+",sb);
			else noPlus = false;
			sb.append("fillcolor=");
			ColorCode(linecolor,sb);
			sb.append(",fillstyle=solid,opacity=");
			sb.append(geo.getAlphaValue()); 
			*/
		}
		if(noPlus)
			return null;
		return new String(sb);
	}
	
	// Append the linestyle to PSTricks code
	private void LinestyleCode(int linestyle,StringBuilder sb){
		// note: removed 'pt' from linetype commands, seems to work better. 
		switch(linestyle){
			case EuclidianView.LINE_TYPE_DOTTED:
				sb.append("dotted");
			break;
			case EuclidianView.LINE_TYPE_DASHED_SHORT:
				sb.append("linetype(\"");
				//int size=resizePt(3);
				int size = 2;
				sb.append(size);
				sb.append(" ");
				sb.append(size);
				sb.append("\")");
			break;
			case EuclidianView.LINE_TYPE_DASHED_LONG:
				sb.append("linetype(\"");
				// size=resizePt(6);
				size = 4;
				sb.append(size);
				sb.append(" ");
				sb.append(size);
				sb.append("\")");
			break;
			case EuclidianView.LINE_TYPE_DASHED_DOTTED:
				sb.append("linetype(\"");
				//int size1=resizePt(2);
				//int size2=resizePt(8);
				//int size3=resizePt(10);
				int size1 = 0,size2 = 3,size3 = 4;
				sb.append(size1);
				sb.append(" ");
				sb.append(size2);
				sb.append(" ");
				sb.append(size3);
				sb.append(" ");
				sb.append(size2);
				sb.append("\")");
			break;
		}
	}
	
	// Append the name color to StringBuilder sb 
	protected void ColorCode(Color c,StringBuilder sb){
		int red = c.getRed(),
		  green = c.getGreen(),
		   blue = c.getBlue();
		if (frame.isGrayscale()){
			String colorname="";
			int grayscale = (red+green+blue)/3;
			c = new Color(grayscale,grayscale,grayscale);
			if (CustomColor.containsKey(c)){
				colorname = CustomColor.get(c).toString();
			}
			else { 
				// Not compact:
				// "pen XXXXXX = rgb(0,0,0); pen YYYYYY = rgb(1,1,1);"
				// Compact:
				// "pen XXXXXX = rgb(0,0,0), YYYYYY = rgb(1,1,1);"
				colorname=createCustomColor(grayscale,grayscale,grayscale);
				if(!compact)
					codeColors.append("pen ");
				else
					codeColors.append(", ");
				codeColors.append(colorname);
				packSpace("=",codeColors);
				codeColors.append("rgb("
					+kernel.format(grayscale/255d)+","
					+kernel.format(grayscale/255d)+","
					+kernel.format(grayscale/255d)+")");
				if(!compact)
					codeColors.append("; ");
				CustomColor.put(c,colorname);
			}
			if (c.equals(Color.BLACK))       sb.append("black");
			//else if (c.equals(Color.DARK_GRAY)) sb.append("darkgray");
			else if (c.equals(Color.GRAY))   sb.append("gray");
			//else if (c.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
			else if (c.equals(Color.WHITE))  sb.append("white");
			else sb.append(colorname);
		}
		else {
			if (c.equals(Color.BLACK))       sb.append("black");
			//else if (c.equals(Color.DARK_GRAY)) sb.append("darkgray");
			else if (c.equals(Color.GRAY))   sb.append("gray");
			//else if (c.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
			else if (c.equals(Color.WHITE))  sb.append("white");
			else if (c.equals(Color.RED))    sb.append("red");
			else if (c.equals(Color.GREEN))  sb.append("green");
			else if (c.equals(Color.BLUE))   sb.append("blue");
			else if (c.equals(Color.YELLOW)) sb.append("yellow");
			else {
				String colorname = "";
				if (CustomColor.containsKey(c)){
					colorname = CustomColor.get(c).toString();
				}
				else {
					colorname = createCustomColor((int)red,(int)green,(int)blue);
					if(!compact)
						codeColors.append("pen ");
					else
						codeColors.append(", ");
					codeColors.append(colorname);
					packSpace("=",codeColors);
					codeColors.append("rgb("
						+kernel.format(red/255d)+","
						+kernel.format(green/255d)+","
						+kernel.format(blue/255d)+")");
					if(!compact)
						codeColors.append("; ");					
					CustomColor.put(c,colorname);
				}
				sb.append(colorname);
			}
		}
	}
	
	/** Equivalent to ColorCode, but dampens color based upon opacity. Appends the pen to codeColor.
	 * @param c The original color before transparency.
	 * @param opacity Double value from 0 to 1, with 0 being completely transparent.
	 * @param sb StringBuilder to attach code to.
	 */
	protected void ColorLightCode(Color c, double opacity, StringBuilder sb){
		// new Color object so that c is not overriden.
		Color tempc; 
		int red = c.getRed(),
		  green = c.getGreen(),
		   blue = c.getBlue();
		red = (int) (255 * (1-opacity) + red * opacity);
		green = (int) (255 * (1-opacity) + green * opacity);
		blue = (int) (255 * (1-opacity) + blue * opacity);
		if (frame.isGrayscale()){
			String colorname="";
			int grayscale = (red+green+blue)/3;
			tempc = new Color(grayscale,grayscale,grayscale);
			if (CustomColor.containsKey(tempc)){
				colorname = CustomColor.get(tempc).toString();
			}
			else {
				colorname=createCustomColor(grayscale,grayscale,grayscale);
				if(!compact)
					codeColors.append("pen ");
				else
					codeColors.append(", ");
				codeColors.append(colorname);
				packSpace("=",codeColors);
				codeColors.append("rgb("
					+kernel.format(grayscale/255d)+","
					+kernel.format(grayscale/255d)+","
					+kernel.format(grayscale/255d)+")");
				if(!compact)
					codeColors.append("; ");
				CustomColor.put(tempc,colorname);
			}
			if (tempc.equals(Color.BLACK))       sb.append("black");
			//else if (tempc.equals(Color.DARK_GRAY)) sb.append("darkgray");
			else if (tempc.equals(Color.GRAY))   sb.append("gray");
			//else if (tempc.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
			else if (tempc.equals(Color.WHITE))  sb.append("white");
			else sb.append(colorname);
		}
		else {
			tempc = new Color(red,green,blue);
			if (tempc.equals(Color.BLACK))       sb.append("black");
			//else if (tempc.equals(Color.DARK_GRAY)) sb.append("darkgray");
			else if (tempc.equals(Color.GRAY))   sb.append("gray");
			//else if (tempc.equals(Color.LIGHT_GRAY)) sb.append("lightgray");
			else if (tempc.equals(Color.WHITE))  sb.append("white");
			else if (tempc.equals(Color.RED))    sb.append("red");
			else if (tempc.equals(Color.GREEN))  sb.append("green");
			else if (tempc.equals(Color.BLUE))   sb.append("blue");
			else if (tempc.equals(Color.YELLOW)) sb.append("yellow");
			else {
				String colorname = "";
				if (CustomColor.containsKey(tempc)){
					colorname = CustomColor.get(tempc).toString();
				}
				else {
					colorname = createCustomColor((int) red,(int)green,(int)blue);
					if(!compact)
						codeColors.append("pen ");
					else
						codeColors.append(", ");
					codeColors.append(colorname);
					packSpace("=",codeColors);
					codeColors.append("rgb("
						+kernel.format(red/255d)+","
						+kernel.format(green/255d)+","
						+kernel.format(blue/255d)+")");
					if(!compact)
						codeColors.append("; ");
					CustomColor.put(tempc,colorname);
				}
				sb.append(colorname);
			}
		}
	}
	
	/** Returns the LaTeX color command; \color[rgb](XX,YY,ZZ). Does not create a new pen. 
	 * @param c Desired Color object.
	 * @param sb Code to add the command to. 
	 */
	// Adds LaTeX: 
	protected void ColorCode2(Color c,StringBuilder sb){
		int red=c.getRed(), green=c.getGreen(), blue=c.getBlue();
		if (frame.isGrayscale()){
			int grayscale = (red+green+blue)/3;
			c = new Color(grayscale,grayscale,grayscale);
			sb.append("\\color[rgb]{"
				+kernel.format(grayscale/255d)+","
				+kernel.format(grayscale/255d)+","
				+kernel.format(grayscale/255d)+"}");
			if (c.equals(Color.BLACK))       sb.append("black");
			else if (c.equals(Color.GRAY))   sb.append("gray");
			else if (c.equals(Color.WHITE))  sb.append("white");
		}
		else {
			if (c.equals(Color.BLACK))       sb.append("black");
			else if (c.equals(Color.GRAY))   sb.append("gray");
			else if (c.equals(Color.WHITE))  sb.append("white");
			else if (c.equals(Color.RED))    sb.append("red");
			else if (c.equals(Color.GREEN))  sb.append("green");
			else if (c.equals(Color.BLUE))   sb.append("blue");
			else if (c.equals(Color.YELLOW)) sb.append("yellow");
			else {
				sb.append("\\color[rgb]{"
					+kernel.format(red/255d)  +","
					+kernel.format(green/255d)+","
					+kernel.format(blue/255d) +"}");
			}
		}
	}
/*	// Resize text Keep the ratio between font size and picture height
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
//	private void defineTransparency(){}	
	
	private void addText(String st,boolean isLatex,int style,int size,Color geocolor){
		if (isLatex) code.append("$");
		if (isLatex && st.charAt(0) == '$') st = st.substring(1);
		
		// Convert Unicode symbols
		if (isLatex) 
			st = convertUnicodeToLatex(st);
		else {
			st = convertUnicodeToText(st);
			// Strip dollar signs. Questionable!
            st = st.replaceAll("\\$", "dollar ");
			// Replace all backslash symbol with \textbackslash, except for newlines
			st = st.replaceAll("\\\\", "\\\\textbackslash ")
			       .replaceAll("\\\\textbackslash \\\\textbackslash ", "\\\\\\\\ ");
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
		/*if (!geocolor.equals(Color.BLACK)){
			ColorCode2(geocolor,code);
			code.append("{");
		} // Colors moved to drawText()

		if (size!=app.getFontSize()) {
			String formatFont=resizeFont(size);
			if (null!=formatFont) code.append(formatFont);
		}*/
		
		// strip final '$'
		code.append(st.substring(0,st.length()-1));
		if(!isLatex || st.charAt(st.length()-1) != '$')
			code.append(st.charAt(st.length() - 1));
		
		// if (size!=app.getFontSize()) code.append("}");
		// if (!geocolor.equals(Color.BLACK)) code.append("}");
		
		switch(style){
			case 1:
			case 2:
				code.append("}");
				break;
			case 3:
				code.append("}}");
				break;
		}
		if (isLatex) code.append("$");
	}
	
	/** Append spaces about s to sb if not in compact mode.
	 * @param s A string which can have spaces around it.
	 * @param sb The StringBuilder to which s is attached.
	 */
	protected void packSpace(String s,StringBuilder sb){
		if(!compact)
			sb.append(" "+s+" ");
		else
			sb.append(s);
	}
	
	
	
	/** Default version of startDraw, appends the start of a draw() command to StringBuilder code.
	 * 
	 */
	protected void startDraw(){
    	startDraw(code);
	}
	
	/** Appends the opening of a draw() command to sb.
	 * @param sb Code to attach to. 
	 */
	protected void startDraw(StringBuilder sb){
		if(!compact)
			sb.append("\n");
		if(compactcse5)
			sb.append("D(");
		else
			sb.append("draw(");
	}
	/** Appends line style code to end of StringBuilder code. 
	 * @param geo contains line style code. 
	 */
	protected void endDraw(GeoElement geo){
		endDraw(geo, code);
	}

	/** Appends line style code to end of StringBuilder code. 
	 * @param geo contains line style code. 
	 * @param sb code to attach to.
	 */
	protected void endDraw(GeoElement geo, StringBuilder sb){
		if(LineOptionCode(geo,true) != null) {
			sb.append(",");
			if(!compact)
				sb.append(" ");
			sb.append(LineOptionCode(geo,true));
		}
		sb.append("); ");
	}
		
	/** Begins an object drawn by the filldraw() command.
	 * @param sb StringBuilder to which code added.
	 */
	protected void startTransparentFill(StringBuilder sb){
		if(!compact)
    		sb.append("\n");
    	if(fillType != ExportFrame.FILL_NONE) // filldraw
    		sb.append("filldraw(");
    	else if(compactcse5) // normal draw
    		sb.append("D(");
    	else
    		sb.append("draw(");
	}
	
	/** Closes an object drawn by the filldraw() command.
	 * @param geo Object that can be filled.
	 * @param sb StringBuilder to which code added.
	 */
	protected void endTransparentFill(GeoElement geo, StringBuilder sb){
		// transparent fill options
		if(fillType == ExportFrame.FILL_OPAQUE) {
    		sb.append(",");
			if(!compact)
				sb.append(" ");
    		if(geo.getAlphaValue() >= 0.9) 
    			ColorCode(geo.getObjectColor(),sb);
    		else
    			sb.append("invisible");
    	}
    	// use opacity(alpha value) pen
		else if(fillType == ExportFrame.FILL_OPACITY_PEN) {
    		sb.append(",");
			if(!compact)
				sb.append(" ");
    		ColorCode(geo.getObjectColor(),sb);
    		packSpace("+",sb);
    		sb.append("opacity(");
    	    sb.append(geo.getAlphaValue());
    	    sb.append(")");
    	}
    	else if(fillType == ExportFrame.FILL_LAYER) {
    		sb.append(",");
			if(!compact)
				sb.append(" ");
    		ColorLightCode(geo.getObjectColor(),geo.getAlphaValue(),sb);    	
    	}
		if(LineOptionCode(geo,true) != null) {
			sb.append(",");
			if(!compact)
				sb.append(" ");
			sb.append(LineOptionCode(geo,true));
		}
		sb.append("); ");
	}
	/** Adds a point in the format "(s1,s2)" to sb.
	 * @param s1 kernel.format(x-coordinate)
	 * @param s2 kernel.format(y-coordinate)
	 * @param sb StringBuilder object to append code to.
	 */
	protected void addPoint(String s1, String s2, StringBuilder sb){
		if(compactcse5) { 
			// retrieves point name from codePointDecl
			int locPair = codePointDecl.indexOf("(" + s1 + "," + s2 + ")");
			if(locPair != -1) {
				String name = codePointDecl.substring(0,locPair); // temporary re-use
				int locNameStart = name.lastIndexOf(" ")+1;
				int locNameEnd = name.lastIndexOf("=");
				name = codePointDecl.substring(locNameStart,locNameEnd);
				sb.append(name);
				return;
			}
		}
		sb.append("("+s1+","+s2+")");
	}
	
	/** Initializes a Hash Map mapping unicode expressions with plain text equivalents. Reads from file at directory geogebra/export/pstricks/unicodetex.
	 * 
	 */
	protected void initUnicodeTextTable(){
		// read unicode symbols from unicodetex.txt
	    try {
		    BufferedReader br = new BufferedReader(new FileReader("geogebra/export/pstricks/unicodetex"));
		    String st; 
		    
	        while ((st = br.readLine()) != null) {
	        	int indexTab = st.indexOf("\t");
	        	// file format:
	        	// \ uXXXX \t plaintext
	        	unicodeTable.put(st.substring(0,indexTab), 
	        	                 st.substring(indexTab+1,st.length()));
	        }
	    } catch (FileNotFoundException e) {
	    	codePreamble.insert(0,"File unicodetex not found.\n\n");
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	codePreamble.insert(0,"IO error.\n\n");
	    	e.printStackTrace();	 
	    }
	}
	
    /** Converts unicode expressions ("\u03c0") to plain text ("pi").
     * @param sb StringBuilder with code.
     * @return Updated StringBuilder;
     */
    protected StringBuilder convertUnicodeToText(StringBuilder sb){
    	// import unicode;
    	String tempc = sb.toString();
    	tempc = convertUnicodeToText(tempc);
    	// override sb with tempc
        sb.delete(0, sb.length());
        sb.append(tempc);
        return sb;
    }
    
    /** Converts unicode expressions ("\u03c0") to plain text ("pi").
     * @param s Text to convert unicode symbols to text. Is not modified.
     * @return Converted string.
     */
    protected String convertUnicodeToText(String s){
    	// import unicode;
    	String s1 = new String(s);
    	Iterator<String> it = unicodeTable.keySet().iterator();
    	while(it.hasNext()) {
    		String skey = it.next();
    		s1 = s1.replaceAll(skey, unicodeTable.get(skey)+" ");
    	}
        return s1.replaceAll("\u00b0", "o ")	// degree symbol
                 .replaceAll("\u212f", "e ")
                 .replaceAll("\u00b2", "2 ")
                 .replaceAll("\u00b3", "3 ")
                 .replaceAll("pi \\)",  "pi\\)");	// eliminate unsightly spaces
    }
    
    /** Converts unicode expressions ("\u03c0") to LaTeX expressions ("\pi").
     * @param s Text to convert unicode symbols to LaTeX. Is not modified.
     * @return Converted string.
     */
    protected String convertUnicodeToLatex(String s){
    	// import unicode;
    	String s1 = new String(s);
    	Iterator<String> it = unicodeTable.keySet().iterator();
    	// look up unicodeTable conversions and replace with LaTeX commands
    	while(it.hasNext()) {
    		String skey = it.next();
    		s1 = s1.replaceAll(skey, "\\"+unicodeTable.get(skey)+" ");
    	}
    	
    	// strip dollar signs
    	/* int locDollar = 0;
    	while((locDollar = s1.indexOf('$',locDollar+1)) != -1) {
    		if(locDollar != 0 && locDollar != s1.length() && s1.charAt(locDollar-1) != '\\')
    			s1 = s1.substring(0,locDollar) + "\\" + s1.substring(locDollar);
    	} */
    	
    	StringBuilder sb = new StringBuilder();
    	// ignore first and last characters
    	// TODO check if odd number of dollar signs? No catch-all fix ..
    	sb.append(s1.charAt(0));
    	for(int i = 1; i < s1.length() - 1; i++) {
    		if(s1.charAt(i-1) == '\\' && (i == 1 || s1.charAt(i-2) != '\\')) {
    			sb.append(s1.charAt(i));
    			continue;
    		}
    		else if(s1.charAt(i) == '$') 
    			sb.append("\\$");
    		else
    			sb.append(s1.charAt(i));
    	}
    	if(s1.length() > 1)
    		sb.append(s1.charAt(s1.length() - 1));
    	s1 = sb.toString(); 
    	
    	return s1.replaceAll("\u00b0", "^\\\\circ")
    	         .replaceAll("\u212f", "e ")
                 .replaceAll("\u00b2", "^2")
                 .replaceAll("\u00b3", "^3");
    }
    
    /** Formats a function string.
     * @param s Code containing function.
     * @return Parsed function string compatible with programming languages.
     */
    protected String parseFunction(String s){
    	// Unicode?
    	return killSpace(Util.toLaTeXString(s,true));
    }
}
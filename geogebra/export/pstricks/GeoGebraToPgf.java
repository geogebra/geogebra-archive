package geogebra.export.pstricks;

import geogebra.Application;
import geogebra.euclidian.DrawAngle;
import geogebra.euclidian.DrawPoint;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AlgoAngleLines;
import geogebra.kernel.AlgoAnglePoints;
import geogebra.kernel.AlgoAngleVector;
import geogebra.kernel.AlgoAngleVectors;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoIntegralFunctions;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.AlgoSumUpperLower;
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
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class GeoGebraToPgf extends GeoGebraExport {
    public GeoGebraToPgf(Application app) {
    	super(app);
    }
 
    public void generateAllCode() {
    	// init unit variables
    	try{	
    		xunit=frame.getXUnit();
    		yunit=frame.getYUnit();
     	}
    	catch(NullPointerException e2){
    		xunit=1;yunit=1;
    	}
    	// Initialize new StringBuffer for Pstricks code
    	// and CustomColor
    	code=new StringBuffer();
    	codePoint=new StringBuffer();
    	codePreamble=new StringBuffer();
    	codeFilledObject=new StringBuffer();
		codeBeginDoc=new StringBuffer();
		CustomColor=new HashMap();
 		
    	codePreamble.append("\\documentclass[" +
    			frame.getFontSize()+"pt]{article}\n" +
    			"\\usepackage{pgf,tikz}\n\\pagestyle{empty}\n");
     	codeBeginDoc.append("\\begin{document}\n");
     	
    	// Draw Grid
		if (euclidianView.getShowGrid()) drawGrid();

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
		code.append("\\end{tikzpicture}\n");
/*		String formatFont=resizeFont(app.getFontSize());
		if (null!=formatFont){
			codeBeginPic.insert(0,formatFont+"\n");
			code.append("}\n");
		}*/
		codeBeginDoc.append("\\begin{tikzpicture}[x=");
		codeBeginDoc.append(xunit);
		codeBeginDoc.append("cm,y=");
		codeBeginDoc.append(yunit);
		codeBeginDoc.append("cm]\n");
		code.insert(0,codeFilledObject+"");
		code.insert(0,codeBeginDoc+"");		
        code.insert(0,codePreamble+"");
		code.append("\\end{document}");		
		frame.write(code);
	}	    	
    
    protected void drawLocus(GeoLocus g){
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
    
    protected void drawSumUpperLower(GeoNumeric geo){
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
    	
    	//draw Line or Slider
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
    
    
    protected void drawPolygon(GeoPolygon geo){
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
	
	protected void drawGeoConicPart(GeoConicPart geo){
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
	
	/**
	 * This will generate the Tikz code to draw the GeoPoint gp 
	 * into the StringBuffer PointCode
	 * @param gp The choosen GeoPoint
	 */
		
	protected void drawGeoPoint(GeoPoint gp){
		if (frame.getExportPointSymbol()){
			double x=gp.getX();
			double y=gp.getY();
			double z=gp.getZ();
			x=x/z;
			y=y/z;		
			Color dotcolor=gp.getObjectColor();
			double dotsize=gp.getPointSize();
			codePoint.append("\\fill [color=");
			ColorCode(dotcolor,codePoint);
			codePoint.append("] ");
			writePoint(x,y,codePoint);
			codePoint.append(" circle (");
			codePoint.append(dotsize/2);
			codePoint.append("pt);\n");
		}
	}
	protected void drawGeoLine(GeoLine geo){
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
	/**
	 * This will generate the Tikz code to draw the GeoSegment geo 
	 * into the StringBuffer code
	 * @param geo The choosen GeoPoint
	 */
	
	protected void drawGeoSegment(GeoSegment geo){
		double[] A=new double[2];
		double[] B=new double[2];
		GeoPoint pointStart=geo.getStartPoint();
		GeoPoint pointEnd=geo.getEndPoint();
		pointStart.getInhomCoords(A);
		pointEnd.getInhomCoords(B);
		code.append("\\draw ");
		code.append(LineOptionCode(geo,true));
		writePoint(A[0],A[1],code);
		code.append("-- ");
		writePoint(B[0],B[1],code);
		code.append(";\n");
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
	 		factor = tickSpacing / (2 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX - arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordX(midY - arrowlength*vy);
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
	 		factor = tickSpacing / (2 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX - 2*arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordX(midY - 2*arrowlength*vy);
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
	 		x2=euclidianView.toRealWorldCoordY(midX + arrowlength*(nx + vx));
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
	 		factor = tickSpacing / nLength;		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
			nx *= factor;
			ny *= factor;
	 		x1=euclidianView.toRealWorldCoordX(midX - arrowlength*vx);
	 		y1=euclidianView.toRealWorldCoordX(midY - arrowlength*vy);
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
	 		x2=euclidianView.toRealWorldCoordY(midX + arrowlength*vx + arrowlength*(nx + vx));
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
	protected void drawGeoRay(GeoRay geo){
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
	protected void drawLabel(GeoElement geo,Drawable drawGeo){
		if (geo.isLabelVisible()){
				String name="$"+Util.toLaTeXString(geo.getLabelDescription(),true)+"$";
				if (null==drawGeo) drawGeo=euclidianView.getDrawableFor(geo);
				double xLabel=drawGeo.getxLabel();
				double yLabel=drawGeo.getyLabel();
				xLabel=euclidianView.toRealWorldCoordX(Math.round(xLabel));
				yLabel=euclidianView.toRealWorldCoordY(Math.round(yLabel));
				
				Color geocolor=geo.getObjectColor();
				codePoint.append("\\draw[color=");
				ColorCode(geocolor,codePoint);
				codePoint.append("] ");
				writePoint(xLabel,yLabel,codePoint);
				codePoint.append("node[anchor=south west]{");
				codePoint.append(name);
				codePoint.append("};\n");
		}
	}

	
	
    // Draw the grid 
	private void drawGrid(){
		Color gridCol=euclidianView.getGridColor();
		double[] GridDist=euclidianView.getGridDistances();
		int gridLine=euclidianView.getGridLineStyle();
		code.append("\\draw [color=");
		ColorCode(gridCol,code);
		code.append(",");
		LinestyleCode(gridLine,code);
		code.append(", xstep=");
		code.append(sci2dec(GridDist[0]*xunit));
		code.append("cm,ystep=");
		code.append(sci2dec(GridDist[1]*yunit));
		code.append("cm] ");
		writePoint(xmin,ymin,code);
		code.append(" grid ");
		writePoint(xmax,ymax,code);
		code.append(";\n");
	}
	

	private void drawAxis(){
		Color color=euclidianView.getAxesColor();
		// Drawing X Axis
		boolean showAxis=euclidianView.getShowXaxis();
		double spaceTick=euclidianView.getAxesNumberingDistances()[0];
		boolean showNumbers=euclidianView.getShowAxesNumbers()[0];		
		int tickStyle=euclidianView.getAxesTickStyles()[0];

		if (showAxis){
			code.append("\\draw[->,color=");
			ColorCode(color,code);
			code.append("] ");
			writePoint(xmin,0,code);
			code.append(" -- ");
			writePoint(xmax,0,code);
			code.append(";\n");
			int x1=(int)(xmin/spaceTick);
			double xstart=x1*spaceTick;
			StringBuffer tmp=new StringBuffer();
			while(xstart<xmax){
				if (Math.abs(xstart)>0.1) tmp.append(kernel.format(xstart));
				xstart+=spaceTick;
				if (xstart<xmax&&Math.abs(xstart)>0.1) tmp.append(",");
			}
			code.append("\\foreach \\x in {");
			code.append(tmp);
			code.append("}\n");
			code.append("\\draw[shift={(\\x,0)},color=");
			ColorCode(color,code);
			if (tickStyle!=EuclidianView.AXES_TICK_STYLE_NONE)	code.append("] (0pt,2pt) -- (0pt,-2pt)");
			else code.append("] (0pt,-2pt)");
			if (showNumbers) code.append("node[below] {\\x};\n");
		}
		// Drawing Y Axis
		showAxis=euclidianView.getShowYaxis();
		spaceTick=euclidianView.getAxesNumberingDistances()[1];
		showNumbers=euclidianView.getShowAxesNumbers()[1];
		tickStyle=resizePt(euclidianView.getAxesTickStyles()[1]);
		if (showAxis){
			code.append("\\draw[->,color=");
			ColorCode(color,code);
			code.append("] ");
			writePoint(0,ymin,code);
			code.append(" -- ");
			writePoint(0,ymax,code);
			code.append(";\n");
			int y1=(int)(ymin/spaceTick);
			double ystart=y1*spaceTick;
			StringBuffer tmp=new StringBuffer();
			while(ystart<ymax){
				if (Math.abs(ystart)>0.1) tmp.append(kernel.format(ystart));
				ystart+=spaceTick;
				if (ystart<ymax&&Math.abs(ystart)>0.1) tmp.append(",");
			}
			code.append("\\foreach \\y in {");
			code.append(tmp);
			code.append("}\n");
			code.append("\\draw[shift={(0,\\y)},color=");
			ColorCode(color,code);
			if (tickStyle!=EuclidianView.AXES_TICK_STYLE_NONE)	code.append("] (2pt,0pt) -- (-2pt,0pt)");
			else code.append("] (-2pt,0pt)");
			if (showNumbers) code.append("node[left] {\\y};\n");
		}
		// Origin
		if (euclidianView.getShowAxesNumbers()[0]||euclidianView.getShowAxesNumbers()[1]){
			code.append("\\draw[color=");
			ColorCode(color,code);
			code.append("] (0pt,-10pt) node[right] {0};");
		}
	}
	private void writePoint(double x, double y,StringBuffer sb){
		sb.append("(");
		sb.append(kernel.format(x));
		sb.append(",");
		sb.append(kernel.format(y));
		sb.append(")");
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
		sb.append("[line width=");
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
		sb.append("color=");
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
	if (bracket) sb.append("] ");
	return new String(sb);
	}
/**
 * Append the line style parameters to the StringBuffer sb
 */
	private void LinestyleCode(int linestyle,StringBuffer sb){
		switch(linestyle){
			case EuclidianView.LINE_TYPE_DOTTED:
				sb.append("dotted");
			break;
			case EuclidianView.LINE_TYPE_DASHED_SHORT:
//				sb.append("dash pattern=off 4pt on 4pt");
				sb.append("dash pattern=on ");
				int size=resizePt(4);
				sb.append(size);
				sb.append("pt off ");
				sb.append(size);
				sb.append("pt");
			break;
			case EuclidianView.LINE_TYPE_DASHED_LONG:
//				sb.append("dash pattern=off 8pt on 8pt");
				sb.append("dash pattern=on ");
				int size8=resizePt(8);
				sb.append(size8);
				sb.append("pt off ");
				sb.append(size8);
				sb.append("pt");
			break;
			case EuclidianView.LINE_TYPE_DASHED_DOTTED:
//				sb.append("dash pattern=on 1 pt off 4pt on 8pt off 4 pt");
				sb.append("dash pattern=on ");
				int size1=resizePt(1);
				int size4=resizePt(4);
				size8=resizePt(8);
				sb.append(size1);
				sb.append("pt off ");
				sb.append(size4);
				sb.append("pt on ");
				sb.append(size8 );
				sb.append("pt off ");
				sb.append(4);
				sb.append("pt");
			break;
		}
	}

/**
 * 	Append the name color to StringBuffer sb
 * It will create a custom color, if this color hasn't be defined yet
 * @param c The Choosen color
 * @param sb  The stringbuffer where the color has to be added
 */
	public void ColorCode(Color c,StringBuffer sb){
		if (c.equals(Color.BLACK)) {sb.append("black");return;}
		String colorname="";
		if (CustomColor.containsKey(c)){
			colorname=CustomColor.get(c).toString();
		}
		else {
			int red=c.getRed();
			int green=c.getGreen();
			int blue=c.getBlue();
			colorname=createCustomColor((int)red,(int)green,(int)blue);
			// Example: \definecolor{orange}{rgb}{1,0.5,0}
			codeBeginDoc.append("\\definecolor{"+colorname+"}{rgb}{"
					+kernel.format(red/255d)+","
					+kernel.format(green/255d)+","
					+kernel.format(blue/255d)+"}\n");
			CustomColor.put(c,colorname);
		}
		sb.append(colorname);
	}

protected void createFrame() {
	frame=new PgfFrame(this);
}
}

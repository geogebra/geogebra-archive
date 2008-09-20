package geogebra.export.pstricks;

import geogebra.Application;
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
import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.AlgoBoxPlot;
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
	private static final int FORMAT_LATEX=0;
	private static final int FORMAT_PLAIN_TEX=1;
	private static final int FORMAT_CONTEXT=2;
	private int format=0;
	private int functionIdentifier=0;
	private boolean forceGnuplot=false;
	public GeoGebraToPgf(Application app) {
    	super(app);
    }
 
    public void generateAllCode() {
       	format=((PgfFrame)frame).getFormat();
       	forceGnuplot=((PgfFrame)frame).getGnuplot();
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
 		if (format==GeoGebraToPgf.FORMAT_LATEX){
 	    	codePreamble.append("\\documentclass[" +
 	    			frame.getFontSize()+"pt]{article}\n" +
 	    			"\\usepackage{pgf,tikz}\n\\usetikzlibrary{arrows}\n\\pagestyle{empty}\n");
 			codeBeginDoc.append("\\begin{tikzpicture}[>=triangle 45,x=");
 			codeBeginDoc.append(xunit);
 			codeBeginDoc.append("cm,y=");
 			codeBeginDoc.append(yunit);
 			codeBeginDoc.append("cm]\n"); 			
 		}
 		else if (format==GeoGebraToPgf.FORMAT_PLAIN_TEX){
 			codePreamble.append("%Uncomment next line if XeTeX is used\n%\\def\\pgfsysdriver{pgfsys-xetex.def}\n\n");
 	    	codePreamble.append("\\input pgf.tex\n\\input tikz.tex\n");
 	    	codePreamble.append("\\usetikzlibrary{arrows}\n");
 	    	codePreamble.append("\\baselineskip=");
 	    	codePreamble.append(frame.getFontSize());
 	    	codePreamble.append("pt\n\\hsize=6.3truein\n\\vsize=8.7truein\n");

 	    	codeBeginDoc.append("\\tikzpicture[>=triangle 45,x=");
 			codeBeginDoc.append(xunit);
 			codeBeginDoc.append("cm,y=");
 			codeBeginDoc.append(yunit);
 			codeBeginDoc.append("cm]\n"); 		
 		}
 		else if (format==GeoGebraToPgf.FORMAT_CONTEXT){
 			codePreamble.append("\\setupbodyfont["+frame.getFontSize()+"pt]\n");
 			codePreamble.append("\\usemodule[tikz]\n\\usemodule[pgf]\n");
 			codePreamble.append("\\usetikzlibrary[arrows]\n\\setuppagenumbering[location=]\n");
 			
 	    	codeBeginDoc.append("\\starttikzpicture[>=triangle 45,x=");
 			codeBeginDoc.append(xunit);
 			codeBeginDoc.append("cm,y=");
 			codeBeginDoc.append(yunit);
 			codeBeginDoc.append("cm]\n");
 		}
    	// Draw Grid
		if (euclidianView.getShowGrid()) drawGrid();
		// Draw axis
		if (euclidianView.getShowXaxis() || euclidianView.getShowYaxis()) 
			drawAxis();
		// Clipping
     	codeFilledObject.append("\\clip");
     	writePoint(xmin,ymin,codeFilledObject);
     	codeFilledObject.append(" rectangle ");
     	writePoint(xmax,ymax,codeFilledObject);
     	codeFilledObject.append(";\n");

/*		 get all objects from construction
 *   	 and "draw" them by creating PGF code*/
     	Object [] geos =
     		kernel.getConstruction().getGeoSetConstructionOrder().toArray();
     	for (int i=0;i<geos.length;i++){
        	GeoElement g = (GeoElement)(geos[i]);
           	drawGeoElement(g);     		
     	}
        // add code for Points and Labels
        code.append(codePoint);
        // Close Environment tikzpicture
 		if (format==GeoGebraToPgf.FORMAT_LATEX){
 	        code.append("\\end{tikzpicture}\n\\end{document}");
 	     	codeBeginDoc.insert(0,"\\begin{document}\n");
 		}
 		else if (format==GeoGebraToPgf.FORMAT_PLAIN_TEX){
 	        code.append("\\endtikzpicture\n\\bye\n");
 	        
 		}
 		else if (format==GeoGebraToPgf.FORMAT_CONTEXT){
 			code.append("\\stoptikzpicture\n\\stoptext");
 	     	codeBeginDoc.insert(0,"\\starttext\n");
 		}
/*		String formatFont=resizeFont(app.getFontSize());
		if (null!=formatFont){
			codeBeginPic.insert(0,formatFont+"\n");
			code.append("}\n");
		}*/
		code.insert(0,codeFilledObject+"");
		code.insert(0,codeBeginDoc+"");		
        code.insert(0,codePreamble+"");
		frame.write(code);
	}	    	
    
    protected void drawLocus(GeoLocus g){
    	ArrayList ll=g.getMyPointList();
    	Iterator it=ll.iterator();
    	code.append("\\draw");
    	boolean first=true;
    	while(it.hasNext()){
    		MyPoint mp=(MyPoint)it.next();
    		double x=mp.x;
    		double y=mp.y;
    		boolean b=mp.lineTo;
    		if (x>xmin&&x<xmax&&y>ymin&&y<ymax){
    			if (b&&!first) code.append(" -- ");
    			else if (first) first=false;
    			writePoint(x,y,code);
    		}
    	}
		code.append(";\n");
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
		codeFilledObject.append("\\draw ");
		String s=LineOptionCode(geo,true);
		if (s.length()!=0) s="["+s+"] ";
		codeFilledObject.append(s);
		// Min vertical bar
		writePoint(min,y-height,codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(min,y+height,codeFilledObject);
		codeFilledObject.append(" ");
		// Max vertical bar
		writePoint(max,y-height,codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(max,y+height,codeFilledObject);
		codeFilledObject.append(" ");
		// Med vertical bar
		writePoint(med,y-height,codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(med,y+height,codeFilledObject);
		// Min-q1 horizontal
		codeFilledObject.append(" ");
		writePoint(min,y,codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(q1,y,codeFilledObject);
		// q3-max
		codeFilledObject.append(" ");
		writePoint(q3,y,codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(max,y,codeFilledObject);
		codeFilledObject.append(";\n");
		// Rectangle q1-q3
    	codeFilledObject.append("\\draw");
    	if (s.length()!=0) codeFilledObject.append(s);
    	writePoint(q1,y-height,codeFilledObject);
    	codeFilledObject.append(" rectangle ");
    	writePoint(q3,y+height,codeFilledObject);
    	codeFilledObject.append(";\n");
    }
    protected void drawHistogram(GeoNumeric geo){
    	AlgoFunctionAreaSums algo=(AlgoFunctionAreaSums)geo.getParentAlgorithm();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
        for (int i=0;i<x.length-1;i++){
          	codeFilledObject.append("\\draw");
        	String s=LineOptionCode(geo,true);
        	if (s.length()!=0) codeFilledObject.append("["+s+"] ");
        	writePoint(x[i],0,codeFilledObject);
        	codeFilledObject.append(" rectangle ");
        	writePoint(x[i+1],y[i],codeFilledObject);
        	codeFilledObject.append(";\n");
        }        
    }
    
    protected void drawSumTrapezoidal(GeoNumeric geo){
       	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
    	int n=algo.getIntervals();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
        // Trapezoidal sum
    	for (int i=0;i<n;i++){
    		
        	codeFilledObject.append("\\draw");
        	String s=LineOptionCode(geo,true);
        	if (s.length()!=0) codeFilledObject.append("["+s+"] ");
        	writePoint(x[i],0,codeFilledObject);
        	codeFilledObject.append(" -- ");
        	writePoint(x[i+1],0,codeFilledObject);
        	codeFilledObject.append(" -- ");
        	writePoint(x[i+1],y[i+1],codeFilledObject);
        	codeFilledObject.append(" -- ");
        	writePoint(x[i],y[i],codeFilledObject);
        	codeFilledObject.append(" -- cycle;\n");
      	}        
    }
    protected void drawSumUpperLower(GeoNumeric geo){
    	AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums)geo.getParentAlgorithm();
    	int n=algo.getIntervals();
        double step=algo.getStep();
        double[] y=algo.getValues();
        double[] x=algo.getLeftBorders();
        for (int i=0;i<n;i++){     	
        	codeFilledObject.append("\\draw");
        	String s=LineOptionCode(geo,true);
        	if (s.length()!=0) codeFilledObject.append("["+s+"] ");
        	writePoint(x[i],0,codeFilledObject);
        	codeFilledObject.append(" rectangle ");
        	writePoint(x[i]+step,y[i],codeFilledObject);
        	codeFilledObject.append(";\n");
        }
    }
    protected void drawIntegralFunctions(GeoNumeric geo){
// command: \draw[option]{[domain=a:b,samples=..] plot(\x,{f(\x)}) }--(b,g(b)) -- {[domain=b:a,samples=..] plot(\x,{g(\x)}) }--(a,f(a)) --cycle; 
   	AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo.getParentAlgorithm();

	// function f	
	GeoFunction f = algo.getF();
	// function g
	GeoFunction g = algo.getG();

	// between a and b
	double a = algo.getA().getDouble();
    double b = algo.getB().getDouble();

	// values for f(a) and g(b) 
	double fa=f.evaluate(a);
	double gb=g.evaluate(b);
	
    String value=f.toValueString();
	value=killSpace(Util.toLaTeXString(value,true));
	boolean plotWithGnuplot=warningFunc(value,"tan(")||warningFunc(value,"cosh(")||warningFunc(value,"acosh(")
					||warningFunc(value,"asinh(")||warningFunc(value,"atanh(")||warningFunc(value,"sinh(")
					|| warningFunc(value,"tanh(");
	codeFilledObject.append("\\draw");
	String s=LineOptionCode(geo,true);
	if (s.length()!=0){
		codeFilledObject.append("["+s+"] ");
	}
	codeFilledObject.append("{");
	if (plotWithGnuplot){
			codeFilledObject.append(" plot[raw gnuplot, id=func");
			codeFilledObject.append(functionIdentifier);
			functionIdentifier++;
			codeFilledObject.append("] function{set samples 100; set xrange [");
			codeFilledObject.append(a);
			codeFilledObject.append(":");
			codeFilledObject.append(b);
			codeFilledObject.append("]; plot ");
			value=value.replaceAll("\\^", "**");
			codeFilledObject.append(value);
			codeFilledObject.append("}");
		}
		else {
			codeFilledObject.append("[");
			codeFilledObject.append("smooth,samples=50,domain=");
			codeFilledObject.append(a);
			codeFilledObject.append(":");
			codeFilledObject.append(b);
			codeFilledObject.append("] plot");
			codeFilledObject.append("(\\x,{");
			value=replaceX(value,"\\x");
			codeFilledObject.append(value);
			codeFilledObject.append("})");
		}
	codeFilledObject.append("} -- ");
	writePoint(b,gb,codeFilledObject);
	codeFilledObject.append(" {");
    value=g.toValueString();
	value=killSpace(Util.toLaTeXString(value,true));
	plotWithGnuplot=warningFunc(value,"tan(")||warningFunc(value,"cosh(")||warningFunc(value,"acosh(")
					||warningFunc(value,"asinh(")||warningFunc(value,"atanh(")||warningFunc(value,"sinh(")
					|| warningFunc(value,"tanh(");
	if (plotWithGnuplot){
			codeFilledObject.append("-- plot[raw gnuplot, id=func");
			codeFilledObject.append(functionIdentifier);
			functionIdentifier++;
			
			codeFilledObject.append("] function{set parametric ; set samples 100; set trange [");
			codeFilledObject.append(a);
			codeFilledObject.append(":");
			codeFilledObject.append(b);
			codeFilledObject.append("]; plot ");
			String variable=kernel.format(b+a)+"-t";	
			
			codeFilledObject.append(variable);
			codeFilledObject.append(",");
			value=replaceX(value, variable);
			value=value.replaceAll("\\^", "**");
			codeFilledObject.append(value);
			codeFilledObject.append("}");
		}
		else {
			codeFilledObject.append("[");
			codeFilledObject.append("smooth,samples=50,domain=");
			codeFilledObject.append(b);
			codeFilledObject.append(":");
			codeFilledObject.append(a);
			codeFilledObject.append("] -- plot");
			codeFilledObject.append("(\\x,{");
			value=replaceX(value,"\\x");
			codeFilledObject.append(value);
			codeFilledObject.append("})");
		}
	codeFilledObject.append("} -- ");
	writePoint(a,fa,codeFilledObject);
	codeFilledObject.append(" -- cycle;\n");
    }
    
    protected void drawIntegral(GeoNumeric geo){
// command: \plot[option] plot[domain]{\pstplot{a}{b}{f(x)}\lineto(b,0)\lineto(a,0)\closepath} 
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo.getParentAlgorithm();
    	// function f
    	GeoFunction f = algo.getFunction();
    	// between a and b
    	double a = algo.getA().getDouble();
        double b = algo.getB().getDouble();
		String value=f.toValueString();
		value=killSpace(Util.toLaTeXString(value,true));
		boolean plotWithGnuplot=warningFunc(value,"tan(")||warningFunc(value,"cosh(")||warningFunc(value,"acosh(")
						||warningFunc(value,"asinh(")||warningFunc(value,"atanh(")||warningFunc(value,"sinh(")
						|| warningFunc(value,"tanh(");

			codeFilledObject.append("\\draw");
			String s=LineOptionCode(geo,true);
			if (s.length()!=0){
				codeFilledObject.append("[");
				codeFilledObject.append(s);
			}
			
			if (plotWithGnuplot){
				if (s.length()!=0){
					codeFilledObject.append("]");
				}
				codeFilledObject.append(" plot[raw gnuplot, id=func");
				codeFilledObject.append(functionIdentifier);
				functionIdentifier++;
				codeFilledObject.append("] function{set samples 100; set xrange [");
				codeFilledObject.append(a);
				codeFilledObject.append(":");
				codeFilledObject.append(b);
				codeFilledObject.append("]; plot ");
				value=value.replaceAll("\\^", "**");
				codeFilledObject.append(value);
				codeFilledObject.append("}");
			}
			else {
				if (s.length()!=0) codeFilledObject.append(", ");
				else codeFilledObject.append("[");
				codeFilledObject.append("smooth,samples=50,domain=");
				codeFilledObject.append(a);
				codeFilledObject.append(":");
				codeFilledObject.append(b);
				codeFilledObject.append("] plot");
				codeFilledObject.append("(\\x,{");
				value=replaceX(value,"\\x");
				codeFilledObject.append(value);
				codeFilledObject.append("})");
			}
    	codeFilledObject.append(" -- ");
    	writePoint(b,0,codeFilledObject);
    	codeFilledObject.append(" -- ");
    	writePoint(a,0,codeFilledObject);
    	codeFilledObject.append(" -- cycle;\n");
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
    	codeFilledObject.append("\\draw");
    	String s=LineOptionCode(geo,true);
    	if (s.length()!=0){
    		codeFilledObject.append("["+s+"] ");
    	}
    	writePoint(x,y,codeFilledObject);
    	codeFilledObject.append(" -- ");
    	writePoint(xright,y,codeFilledObject);
    	codeFilledObject.append(" -- ");
    	writePoint(xright,y+rwHeight,codeFilledObject);
    	codeFilledObject.append(";\n");	
        // draw Label
    	float xLabelHor = (x + xright) /2;
        float yLabelHor = y -(float)(
        		(euclidianView.getFont().getSize() + 2)/euclidianView.getYscale());
		Color geocolor=geo.getObjectColor();
		codePoint.append("\\draw[color=");
		ColorCode(geocolor,codePoint);
		codePoint.append("] ");
		writePoint(xLabelHor,yLabelHor,codePoint);
		codePoint.append(" node[anchor=south west] {");
		codePoint.append(slopeTriangleSize);
		codePoint.append("};\n");
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
     	// Fix bug with Slider
     	tempPoint.remove(); 
     	/////////////////
     	
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
		// if angle=90 and decoration=little square
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
        	
        	codeFilledObject.append("\\draw");
        	String s=LineOptionCode(geo,true);
        	if (s.length()!=0){
            	codeFilledObject.append("["+s+"] ");        		
        	}
        	for (int i=0;i<4;i++){
        		writePoint(x[2*i],x[2*i+1],codeFilledObject);
        		codeFilledObject.append(" -- ");
        	}
        	codeFilledObject.append("cycle; \n");	
        }
        // draw arc for the angle
        else {	
       	// set arc in real world coords
        	double angStDeg=Math.toDegrees(angSt)%360;
        	double angEndDeg=Math.toDegrees(angExt)%360;
        	if (angStDeg>angEndDeg) angStDeg=angStDeg-360;
        	
        	codeFilledObject.append("\\draw [shift={");
        	writePoint(m[0],m[1],codeFilledObject);
        	codeFilledObject.append("}");
        	String s=LineOptionCode(geo,true);    	
        	if (s.length()!=0){
        		codeFilledObject.append(","+s+"] ");    		
        	}
        	else codeFilledObject.append("] ");
        	codeFilledObject.append("(0,0) -- (");
        	codeFilledObject.append(kernel.format(angStDeg));
        	codeFilledObject.append(":");
    		codeFilledObject.append(kernel.format(r));
    		codeFilledObject.append(") arc (");
    		codeFilledObject.append(kernel.format(angStDeg));
        	codeFilledObject.append(":");
    		codeFilledObject.append(kernel.format(angEndDeg));
        	codeFilledObject.append(":");
    		codeFilledObject.append(kernel.format(r));
        	codeFilledObject.append(") -- cycle;\n");

		// draw the dot if angle= 90 and decoration=dot
		if (kernel.isEqual(geo.getValue(),Kernel.PI_HALF)&&geo.isEmphasizeRightAngle()&&euclidianView.getRightAngleStyle()==EuclidianView.RIGHT_ANGLE_STYLE_DOT){
			double diameter = geo.lineThickness/euclidianView.getXscale();
			double radius = arcSize/euclidianView.getXscale()/1.7;
			double labelAngle = (angSt+angExt) / 2.0;
			double x1=m[0] + radius * Math.cos(labelAngle);
			double x2 = m[1] + radius * Math.sin(labelAngle);
			// draw an ellipse
			// command:  \draw (0,0) circle diameter
				code.append("\\fill");
				s=LineOptionCode(geo,true);
				if (s.length()!=0) code.append("["+s+"] ");
				writePoint(x1,x2,code);
				code.append(" circle ");
				code.append(kernel.format(diameter/2));
				code.append(";\n");
			}
        }
   		int deco=geo.decorationType;
		if (deco!=GeoElement.DECORATION_NONE) markAngle(geo,r,m,angSt,angExt);

    }
    protected void drawArrowArc(GeoAngle geo,double[] vertex,double angSt, double angEnd,double r, boolean anticlockwise){
      	double angStDeg=Math.toDegrees(angSt)%360;
    	double angEndDeg=Math.toDegrees(angEnd)%360;
    	if (angStDeg>angEndDeg) angStDeg-=360;
    	code.append("\\draw [shift={");
    	writePoint(vertex[0],vertex[1],code);
    	code.append("},-");
    	if (anticlockwise) code.append(">");
    	else code.append("<");
    	String s=LineOptionCode(geo,false);    	
    	if (s.length()!=0){
    		code.append(","+s+"] ");    		
    	}
    	else code.append("] ");
    	code.append("(");
    	code.append(kernel.format(angStDeg));
       	code.append(":");    	
		code.append(kernel.format(r));
		code.append(") arc (");
		code.append(kernel.format(angStDeg));
       	code.append(":");    	
		code.append(kernel.format(angEndDeg));
       	code.append(":");    	
		code.append(kernel.format(r));
		code.append(");\n");
        }
    
    protected void drawArc(GeoAngle geo,double[] vertex,double angSt, double angEnd,double r ){
    	double angStDeg=Math.toDegrees(angSt)%360;
    	double angEndDeg=Math.toDegrees(angEnd)%360;
    	if (angStDeg>angEndDeg) angStDeg-=360;
    	code.append("\\draw [shift={");
    	writePoint(vertex[0],vertex[1],code);
    	code.append("}");
    	String s=LineOptionCode(geo,false);    	
    	if (s.length()!=0){
    		code.append(","+s+"] ");    		
    	}
    	else code.append("] ");
    	code.append("(");
    	code.append(kernel.format(angStDeg));
       	code.append(":");    	
		code.append(kernel.format(r));
		code.append(") arc (");
		code.append(kernel.format(angStDeg));
       	code.append(":");    	
		code.append(kernel.format(angEndDeg));
       	code.append(":");    	
		code.append(kernel.format(r));
		code.append(");\n");
    }
	protected void drawTick(GeoAngle geo,double[] vertex,double angle){
		angle=-angle;
		double radius=geo.getArcSize();
		double diff= 2.5 + geo.lineThickness / 4d;
		double x1=euclidianView.toRealWorldCoordX(vertex[0]+(radius-diff)*Math.cos(angle));
		double x2=euclidianView.toRealWorldCoordX(vertex[0]+(radius+diff)*Math.cos(angle));
		double y1=euclidianView.toRealWorldCoordY(vertex[1]+(radius-diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		double y2=euclidianView.toRealWorldCoordY(vertex[1]+(radius+diff)*Math.sin(angle)*euclidianView.getScaleRatio());
		code.append("\\draw");
	   	String s=LineOptionCode(geo,false);    	
    	if (s.length()!=0){
    		code.append("["+s+"] ");    		
    	}
    	writePoint(x1,y1,code);
		code.append(" -- ");
    	writePoint(x2,y2,code);
		code.append(";\n");
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
    	// Bug fixed with Slider
    	geoPoint.remove(); 

    	//draw Line or Slider
    	code.append("\\draw");
    	String s=LineOptionCode(geo,true);
    	if (s.length()!=0){
    		s="["+s+"] ";
        	code.append(s);
    	}
    	writePoint(x,y,code);
    	code.append(" -- ");
    	if (horizontal) x+=width;
    	else y+=width;
    	writePoint(x,y,code);
    	code.append(";\n");    	
    }
    
    
    protected void drawPolygon(GeoPolygon geo){
    	// command: \pspolygon[par](x0,y0)....(xn,yn)
    	float alpha=geo.getAlphaValue();
    	if (alpha==0.0f) return;
    	codeFilledObject.append("\\draw");
    	String s=LineOptionCode(geo,true);
    	if (s.length()!=0){
    		s="["+s+"] ";
    		codeFilledObject.append(s);
    	}
    	GeoPoint [] points = geo.getPoints();	
    	for (int i=0;i<points.length;i++){
    		double x=points[i].getX();
    		double y=points[i].getY();
    		double z=points[i].getZ();
     		x=x/z;
    		y=y/z;
    		writePoint(x,y,codeFilledObject);
    		codeFilledObject.append(" -- ");
    	}
    	codeFilledObject.append("cycle;\n");	
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
			code.append("\\draw ");
			writePoint(x,y,code);
			code.append(" node[anchor=north west] {");
			addText(st,isLatex,style,size,geocolor);
			code.append("};\n");
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
			code.append("\\draw ");
			writePoint(x,y,code);
			code.append(" node[anchor=north west] {");
			code.append("\\parbox{");
			code.append(kernel.format(width*(xmax-xmin)*xunit/euclidianView.getWidth()+1));
			code.append(" cm}{");
			addText(new String(sb),isLatex,style,size,geocolor);			
			code.append("}};\n");			
		}
	}

	protected void drawGeoConicPart(GeoConicPart geo){
		double x=geo.getTranslationVector().getX();
		double y=geo.getTranslationVector().getY();
		double r=geo.getHalfAxes()[0];
		double startAngle=geo.getParameterStart();
		double endAngle=geo.getParameterEnd();
		startAngle=Math.toDegrees(startAngle);
		endAngle=Math.toDegrees(endAngle);
		if (startAngle>endAngle){
			startAngle=startAngle-360;
		}
		//	Sector command:   \draw[shift={(x0,y0)},par] (0,0) -- (startAngle:radius) arc (angleStart:EndAngle:radius) -- cycle
		//	 Arc command:	\draw[shift={(x0,y0)},par] (startAngle:radius) arc (startAngle:endAngle:radius)
		code.append("\\draw [shift={");
		writePoint(x,y,code);
		code.append("}");
		String s=LineOptionCode(geo,true);
		if (s.length()!=0){
			s=","+s+"] ";
			code.append(s);
		}
		else code.append("]");
		if (geo.getConicPartType()==GeoConicPart.CONIC_PART_SECTOR){
			code.append(" (0,0) -- (");
			code.append(kernel.format(startAngle));
			code.append(":");
			
			
			code.append(kernel.format(r*xunit));
			code.append(") arc (");
			code.append(kernel.format(startAngle));
			code.append(":");
			code.append(kernel.format(endAngle));
			code.append(":");
			code.append(kernel.format(r*xunit));
			code.append(") -- cycle ;\n");
		}
		else if (geo.getConicPartType()==GeoConicPart.CONIC_PART_ARC){
			code.append(" (");
			code.append(kernel.format(startAngle));
			code.append(":");
			code.append(kernel.format(r*xunit));
			code.append(") arc (");
			code.append(kernel.format(startAngle));
			code.append(":");
			code.append(kernel.format(endAngle));
			code.append(":");
			code.append(kernel.format(r*xunit));
			code.append(");\n");
		}
			
	}
	private void drawFunction(GeoFunction geo,StringBuffer sb){
		Function f=geo.getFunction();
		if (null==f) return;
		String value=f.toValueString();
		value=killSpace(Util.toLaTeXString(value,true));
		boolean plotWithGnuplot=warningFunc(value,"tan(")||warningFunc(value,"cosh(")||warningFunc(value,"acosh(")
						||warningFunc(value,"asinh(")||warningFunc(value,"atanh(")||warningFunc(value,"sinh(")
						|| warningFunc(value,"tanh(");

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
			sb.append("\\draw");
			String s=LineOptionCode(geo,true);
			if (s.length()!=0){
				sb.append("[");
				sb.append(s);
			}
			
			if (plotWithGnuplot){

				if (s.length()!=0){
					sb.append("]");
				}
				sb.append(" plot[raw gnuplot, id=func");
				sb.append(functionIdentifier);
				functionIdentifier++;
				sb.append("] function{set samples 100; set xrange [");
				sb.append(xrangemin+0.1);
				sb.append(":");
				sb.append(xrangemax-0.1);
				sb.append("]; plot ");
				value=value.replaceAll("\\^", "**");
				sb.append(value);
				sb.append("};\n");
			}
			else {
				if (s.length()!=0) sb.append(", ");
				else sb.append("[");
				sb.append("smooth,samples=100,domain=");
				sb.append(xrangemin);
				sb.append(":");
				sb.append(xrangemax);
				sb.append("] plot");
				sb.append("(\\x,{");
				value=replaceX(value,"\\x");
				sb.append(value);
				sb.append("});\n");
			}
			xrangemax+=PRECISION_XRANGE_FUNCTION;
			a=xrangemax;
		}
		
		
	}
	
	protected void drawFunction(GeoFunction geo){
		drawFunction(geo,code);
	}
	/**
	 * This method replace the letter "x" by the String substitute
	 * @param name The function
	 */
	private String replaceX(String name,String substitute){
		StringBuffer sb=new StringBuffer(name);
		// If the expression starts with minus -
		// Insert a "0" (Bug from TikZ /PGF)
		if (name.startsWith("-")) sb.insert(0,"0");
		int i=0;
		while(i<sb.length()){
			char before='1';
			char after='1';
			char character=sb.charAt(i);
			if (character=='x'){
				if (i>0){
					before=sb.charAt(i-1);
				}
				if (i<sb.length()-1){
					after=sb.charAt(i+1);
				}
				int id1="1234567890^ +-*/%()\t".indexOf(after);
				int id2="1234567890^ +-*/%()\t".indexOf(before);			
				if (id1!=-1&&id2!=-1){
					sb.deleteCharAt(i);
					sb.insert(i, substitute);
					i+=substitute.length()-1;
				}				
			}
			i++;
		}
		return new String(sb);
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
		// rename functions log
		renameFunc(sb,"log(","ln(");
		// for exponential in new Geogebra version.
		renameFunc(sb,Kernel.EULER_STRING,"2.718281828");
		return new String(sb);
	}
	/**
	 * Some Functions are not supported by PGF. This method write a warning in preamble
	 * @param sb The complete Function
	 * @param name The Function unsupported
	 */
	private boolean warningFunc(String sb,String nameFunc){
		if (forceGnuplot) return true;
		int ind=sb.indexOf(nameFunc);
		if (ind!=-1){
			codePreamble.append("% <<<<<<<WARNING>>>>>>>\n");
			codePreamble.append("% PGF/Tikz doesn't support the following mathematical functions:\n");
			codePreamble.append("% tan, cosh, acosh, sinh, asinh, tanh, atanh\n");
			codePreamble.append("% Plotting will be done using GNUPLOT\n");
			codePreamble.append("% GNUPLOT must be installed and you must allow Latex to call external programs by\n");	
			codePreamble.append("% Adding the following option to your compiler\n");
			codePreamble.append("% shell-escape    OR    enable-write18 \n");
			codePreamble.append("% Example: pdflatex --shell-escape file.tex \n");
			return true;
		}
		return false;
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
		double x1,y1;
		if (null==pointStart){
			x1=0;y1=0;
		}
		else {
			x1=pointStart.getX()/pointStart.getZ();
			y1=pointStart.getY()/pointStart.getZ();
		}
		double[] coord=new double[3];
		geo.getCoords(coord);
		double x2=coord[0]+x1;
		double y2=coord[1]+y1;
		code.append("\\draw [->");
		String s=LineOptionCode(geo,true);
		if (s.length()!=0){
			code.append(",");
			code.append(s);
		}
		code.append("] ");
		writePoint(x1,y1,code);
		code.append(" -- ");
		writePoint(x2,y2,code);
		code.append(";\n");
	}
	private void drawCircle(GeoConic geo){
		if (xunit==yunit){
	// draw a circle
	//	 command:  \draw[options](x_center,y_center) circle (R cm)
			double x=geo.getTranslationVector().getX();
			double y=geo.getTranslationVector().getY();
			double r=geo.getHalfAxes()[0];
			code.append("\\draw");
			String s=LineOptionCode(geo,true);
			if (s.length()!=0) s=" ["+s+"] ";
			code.append(s);
			writePoint(x,y,code);
			code.append(" circle (");
			String tmpr=kernel.format(r*xunit);
			if (Double.parseDouble(tmpr)!=0) code.append(tmpr);
			else code.append(r);
			code.append("cm);\n");
		}
		else {
		// draw an ellipse
		// command:  \draw[options](x_center,y_center) ellipse (XRadius cm and YRadius cm)
			double x1=geo.getTranslationVector().getX();
			double y1=geo.getTranslationVector().getY();
			double r1=geo.getHalfAxes()[0];
			double r2=geo.getHalfAxes()[1];
			code.append("\\draw");
			String s=LineOptionCode(geo,true);
			if (s.length()!=0) s=" ["+s+"] ";
			code.append(s);
			writePoint(x1,y1,code);
			code.append(" ellipse (");
			code.append(kernel.format(r1*xunit));
			code.append("cm and ");
			code.append(kernel.format(r2*yunit));
			code.append("cm);\n");
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
//	command:  \draw[rotate around={angle:center},lineOptions](x_center,y_center) ellipse (R1 and R2)
				AffineTransform at=geo.getAffineTransform();
				double eigenvecX=at.getScaleX();
				double eigenvecY=at.getShearY();
				double x1=geo.getTranslationVector().getX();
				double y1=geo.getTranslationVector().getY();
				double r1=geo.getHalfAxes()[0];
				double r2=geo.getHalfAxes()[1];
				double angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
				code.append("\\draw [rotate around={");
				code.append(kernel.format(angle));
				code.append(":");
				writePoint(x1,y1,code);
				code.append("}");
				String s=LineOptionCode(geo,true);
				if (s.length()!=0){
					code.append(",");
					code.append(s);					
				}
				code.append("] ");
				writePoint(x1,y1,code);
				code.append(" ellipse (");
				code.append(kernel.format(r1*xunit));
				code.append("cm and ");
				code.append(kernel.format(r2*yunit));
				code.append("cm);\n");
			break;
			
		// if conic is a parabola 
			case GeoConic.CONIC_PARABOLA:
//command:  \draw[rotate around={angle:center},xshift=x1,yshift=y1,lineOptions] plot(\x,\x^2/2/p);
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
				code.append("\\draw [samples=50,rotate around={");
				code.append(kernel.format(angle));
				code.append(":");
				writePoint(x1,y1,code);
				code.append("},xshift=");
				code.append(kernel.format(x1*xunit));
				code.append("cm,yshift=");
				code.append(kernel.format(y1*yunit));
				code.append("cm");
				s=LineOptionCode(geo,true);
				if (s.length()!=0){
					code.append(",");
					code.append(s);					
				}
				code.append("] plot (\\x,\\x^2/2/");
				code.append(p);
				code.append(");\n");
			break;
			case GeoConic.CONIC_HYPERBOLA:
//command:  \draw[domain=-1:1,rotate around={angle:center},xshift=x1,yshift=y1,lineOptions] 
//				plot({a(1+\x^2)/(1-\x^2)},2b\x/(1-\x^2));
				at=geo.getAffineTransform();
				eigenvecX=at.getScaleX();
				eigenvecY=at.getShearY();
				x1=geo.getTranslationVector().getX();
				y1=geo.getTranslationVector().getY();
				r1=geo.getHalfAxes()[0];
				r2=geo.getHalfAxes()[1];
				angle=Math.toDegrees(Math.atan2(eigenvecY,eigenvecX));
				
				code.append("\\draw [samples=50,domain=-0.99:0.99,rotate around={");
				code.append(kernel.format(angle));
				code.append(":");
				writePoint(x1,y1,code);
				code.append("},xshift=");
				code.append(kernel.format(x1*xunit));
				code.append("cm,yshift=");
				code.append(kernel.format(y1*yunit));
				code.append("cm");
				s=LineOptionCode(geo,true);
				if (s.length()!=0){
					code.append(",");
					code.append(s);					
				}
				code.append("] plot ({");
				code.append(kernel.format(r1));
				code.append("*(1+\\x^2)/(1-\\x^2)},{");
				code.append(kernel.format(r2));
				code.append("*2*\\x/(1-\\x^2)});\n");
				
				
				code.append("\\draw [samples=50,domain=-0.99:0.99,rotate around={");
				code.append(kernel.format(angle));
				code.append(":");
				writePoint(x1,y1,code);
				code.append("},xshift=");
				code.append(kernel.format(x1*xunit));
				code.append("cm,yshift=");
				code.append(kernel.format(y1*yunit));
				code.append("cm");
				s=LineOptionCode(geo,true);
				if (s.length()!=0){
					code.append(",");
					code.append(s);					
				}
				code.append("] plot ({");
				code.append(kernel.format(r1));
				code.append("*(-1-\\x^2)/(1-\\x^2)},{");
				code.append(kernel.format(r2));
				code.append("*(-2)*\\x/(1-\\x^2)});\n");
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
			int dotstyle=gp.getPointStyle();
			if (dotstyle==-1) dotstyle=euclidianView.getPointStyle();
			if (dotstyle==EuclidianView.POINT_STYLE_DOT){
				codePoint.append("\\fill [color=");
				ColorCode(dotcolor,codePoint);
				codePoint.append("] ");
				writePoint(x,y,codePoint);
				codePoint.append(" circle (");
				codePoint.append(dotsize/2);
				codePoint.append("pt);\n");				
			}
			else if (dotstyle==EuclidianView.POINT_STYLE_CIRCLE){
				codePoint.append("\\draw [color=");
				ColorCode(dotcolor,codePoint);
				codePoint.append("] ");
				writePoint(x,y,codePoint);
				codePoint.append(" circle (");
				codePoint.append(dotsize/2);
				codePoint.append("pt);\n");				
			}
			else if (dotstyle==EuclidianView.POINT_STYLE_CROSS){
				codePoint.append("\\draw [color=");
				ColorCode(dotcolor,codePoint);
				codePoint.append("] ");
				writePoint(x,y,codePoint);
				codePoint.append("-- ++(-");
				codePoint.append(dotsize/2);
				codePoint.append("pt,-");
				codePoint.append(dotsize/2);
				codePoint.append("pt) -- ++(");
				codePoint.append(dotsize);
				codePoint.append("pt,");
				codePoint.append(dotsize);
				codePoint.append("pt) ++(-");
				codePoint.append(dotsize);
				codePoint.append("pt,0) -- ++(");
				codePoint.append(dotsize);
				codePoint.append("pt,-");
				codePoint.append(dotsize);
				codePoint.append("pt);\n");
			} 
		}
	}
	/**
	 * Generate the PGF/tikZ code to draw an infinite line
	 */
	
	protected void drawGeoLine(GeoLine geo){
		double a=geo.getX();
		double b=geo.getY();
		double c=geo.getZ();
		if (b!=0){
			code.append("\\draw [");
			String option=LineOptionCode(geo,true);
			if (option.length()!=0){
				code.append(option);
				code.append(",");
			}
			code.append("domain=");
			code.append(kernel.format(xmin));
			code.append(":");
			code.append(kernel.format(xmax));
			code.append("] plot(\\x,{(-");
			code.append(kernel.format(c));
			code.append("-");
			code.append(kernel.format(a));
			code.append("*\\x)/");
			String tmpy=kernel.format(b);
			if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			else code.append(b);
			code.append("});\n");			
		}
		else if (b==0){
			code.append("\\draw ");
			String s=LineOptionCode(geo,true);
			if (s.length()!=0) s="["+s+"] ";
			code.append(s);
			writePoint(-c/a,ymin,code);
			code.append(" -- ");
			writePoint(-c/a,ymax,code);
			code.append(";\n");
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
		String s=LineOptionCode(geo,true);
		if (s.length()!=0) s="["+s+"] ";
		code.append(s);
		writePoint(A[0],A[1],code);
		code.append("-- ");
		writePoint(B[0],B[1],code);
		code.append(";\n");
		int deco=geo.decorationType;
		if (deco!=GeoElement.DECORATION_NONE) mark(A,B,deco,geo);
	}
	protected void drawLine(double x1,double y1,double x2,double y2,GeoElement geo){	
		code.append("\\draw ");
		String s=LineOptionCode(geo,true);
		if (s.length()!=0) s="["+s+"] ";
		code.append(s);
		writePoint(x1,y1,code);
		code.append(" -- ");
		writePoint(x2,y2,code);
		code.append(";\n");
	}

	protected void drawGeoRay(GeoRay geo){
		GeoPoint pointStart=geo.getStartPoint();
		double x1=pointStart.getX();
		double z1=pointStart.getZ();
		x1=x1/z1;
		double y1=pointStart.getY()/z1;
		double a=geo.getX();
		double b=geo.getY();
		double c=geo.getZ();

		double inf=xmin,sup=xmax;
		if (b>0){
			inf=x1;
		}
		else {
			sup=x1;
		}
		if (b!=0){
			code.append("\\draw [");
			String option=LineOptionCode(geo,true);
			if (option.length()!=0){
				code.append(option);
				code.append(",");
			}
			code.append("domain=");
			code.append(inf);
			code.append(":");
			code.append(sup);
			code.append("] plot(\\x,{(-");
			code.append(kernel.format(c));
			code.append("-");
			code.append(kernel.format(a));
			code.append("*\\x)/");
			String tmpy=kernel.format(b);
			if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			else code.append(b);
			code.append("});\n");			
		}
		else if (b==0){
			if (a<0) sup=ymax;
			else sup=ymin;
			code.append("\\draw ");
			String s=LineOptionCode(geo,true);
			if (s.length()!=0) s="["+s+"] ";
			code.append(s);
			writePoint(x1,y1,code);
			code.append(" -- ");
			writePoint(x1,sup,code);
			code.append(";\n");
		}
	}
    
	protected void drawLabel(GeoElement geo,Drawable drawGeo){
		if (geo.isLabelVisible()){
				String name="$"+Util.toLaTeXString(geo.getLabelDescription(),true)+"$";
				if (name.indexOf("°")!=-1){
					name=name.replaceAll("°", "\\\\textrm{\\\\degre}");
					if (codePreamble.indexOf("\\degre")==-1)
						codePreamble.append("\\usepackage[T1]{fontenc}\n\\DeclareTextSymbol{\\degre}{T1}{6}\n");
					}
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
				codePoint.append(" node[anchor=south west] {");
				codePoint.append(name);
				codePoint.append("};\n");
		}
	}

	/**
	 * Generate the PGF/TikZ code for the Grid
	 */
	
	private void drawGrid(){
		Color gridCol=euclidianView.getGridColor();
		double[] GridDist=euclidianView.getGridDistances();
		int gridLine=euclidianView.getGridLineStyle();
		codeBeginDoc.append("\\draw [color=");
		ColorCode(gridCol,codeBeginDoc);
		codeBeginDoc.append(",");
		LinestyleCode(gridLine,codeBeginDoc);
		codeBeginDoc.append(", xstep=");
		codeBeginDoc.append(sci2dec(GridDist[0]*xunit));
		codeBeginDoc.append("cm,ystep=");
		codeBeginDoc.append(sci2dec(GridDist[1]*yunit));
		codeBeginDoc.append("cm] ");
		writePoint(xmin,ymin,codeBeginDoc);
		codeBeginDoc.append(" grid ");
		writePoint(xmax,ymax,codeBeginDoc);
		codeBeginDoc.append(";\n");
	}
	/**
	 * Generate the PGF/TikZ code for Axis drawing
	 */

	private void drawAxis(){
		Color color=euclidianView.getAxesColor();
		// Drawing X Axis
		boolean showAxis=euclidianView.getShowXaxis();
		double spaceTick=euclidianView.getAxesNumberingDistances()[0];
		boolean showNumbers=euclidianView.getShowAxesNumbers()[0];		
		int tickStyle=euclidianView.getAxesTickStyles()[0];
		if (showAxis){
			codeBeginDoc.append("\\draw[->,color=");
			ColorCode(color,codeBeginDoc);
			codeBeginDoc.append("] ");
			writePoint(xmin,0,codeBeginDoc);
			codeBeginDoc.append(" -- ");
			writePoint(xmax,0,codeBeginDoc);
			codeBeginDoc.append(";\n");
			int x1=(int)(xmin/spaceTick);
			double xstart=x1*spaceTick;
			StringBuffer tmp=new StringBuffer();
			while(xstart<xmax){
				if (Math.abs(xstart)>0.1) tmp.append(kernel.format(xstart));
				xstart+=spaceTick;
				if (xstart<xmax&&Math.abs(xstart)>0.1) tmp.append(",");
			}
			codeBeginDoc.append("\\foreach \\x in {");
			codeBeginDoc.append(tmp);
			codeBeginDoc.append("}\n");
			codeBeginDoc.append("\\draw[shift={(\\x,0)},color=");
			ColorCode(color,codeBeginDoc);
			if (tickStyle!=EuclidianView.AXES_TICK_STYLE_NONE)	codeBeginDoc.append("] (0pt,2pt) -- (0pt,-2pt)");
			else codeBeginDoc.append("] (0pt,-2pt)");
			if (showNumbers) {
				codeBeginDoc.append(" node[below] {");
				codeBeginDoc.append(footnotesize("\\x"));
				codeBeginDoc.append("};\n");
			}
		}
		// Drawing Y Axis
		showAxis=euclidianView.getShowYaxis();
		spaceTick=euclidianView.getAxesNumberingDistances()[1];
		showNumbers=euclidianView.getShowAxesNumbers()[1];
		tickStyle=resizePt(euclidianView.getAxesTickStyles()[1]);
		if (showAxis){
			codeBeginDoc.append("\\draw[->,color=");
			ColorCode(color,codeBeginDoc);
			codeBeginDoc.append("] ");
			writePoint(0,ymin,codeBeginDoc);
			codeBeginDoc.append(" -- ");
			writePoint(0,ymax,codeBeginDoc);
			codeBeginDoc.append(";\n");
			int y1=(int)(ymin/spaceTick);
			double ystart=y1*spaceTick;
			StringBuffer tmp=new StringBuffer();
			while(ystart<ymax){
				if (Math.abs(ystart)>0.1) tmp.append(kernel.format(ystart));
				ystart+=spaceTick;
				if (ystart<ymax&&Math.abs(ystart)>0.1) tmp.append(",");
			}
			codeBeginDoc.append("\\foreach \\y in {");
			codeBeginDoc.append(tmp);
			codeBeginDoc.append("}\n");
			codeBeginDoc.append("\\draw[shift={(0,\\y)},color=");
			ColorCode(color,codeBeginDoc);
			if (tickStyle!=EuclidianView.AXES_TICK_STYLE_NONE)	codeBeginDoc.append("] (2pt,0pt) -- (-2pt,0pt)");
			else codeBeginDoc.append("] (-2pt,0pt)");
			if (showNumbers) {
				codeBeginDoc.append(" node[left] {");
				codeBeginDoc.append(footnotesize("\\y"));
				codeBeginDoc.append("};\n");
			}
		}
		// Origin
		if (euclidianView.getShowAxesNumbers()[0]||euclidianView.getShowAxesNumbers()[1]){
			codeBeginDoc.append("\\draw[color=");
			ColorCode(color,codeBeginDoc);
			// append the origin
			codeBeginDoc.append("] (0pt,-10pt) node[right] {");
			codeBeginDoc.append(footnotesize("0"));
			codeBeginDoc.append("};\n");
		}
	}
	
	private String footnotesize(String s){
		if (format==GeoGebraToPgf.FORMAT_LATEX)	return "\\footnotesize "+s;
		else if (format==GeoGebraToPgf.FORMAT_CONTEXT) return "\\tfx "+s;  
		else if (format==GeoGebraToPgf.FORMAT_PLAIN_TEX) return s;
		return s;
		
	}
	
	
	/**
	 * A util method adds point coordinates to a StringBuffer
	 * @param x X point
	 * @param y Y Point
	 * @param sb The Stringbuffer code
	 */
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
	if (linethickness!=EuclidianView.DEFAULT_LINE_THICKNESS){
		// coma needed
		coma=true;
		// bracket needed
		sb.append("line width=");
		sb.append(kernel.format(linethickness/2.0*0.8));
		sb.append("pt");
	}
	if (linestyle!=EuclidianView.DEFAULT_LINE_TYPE){
		if (coma) sb.append(",");
		else coma=true;
		LinestyleCode(linestyle,sb);
	}
	if (!linecolor.equals(Color.BLACK)){
		if (coma) sb.append(",");
		else coma=true;
		sb.append("color=");
		ColorCode(linecolor,sb);
	}
	if (transparency&&geo.isFillable()&&geo.getAlphaValue()>0.0f){
		if (coma) sb.append(",");
		else coma=true;
		sb.append("fill=");
		ColorCode(linecolor,sb);
		sb.append(",fill opacity=");
		sb.append(geo.getAlphaValue());
	}
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
	protected void ColorCode(Color c,StringBuffer sb){
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
			if (format==GeoGebraToPgf.FORMAT_LATEX||format==GeoGebraToPgf.FORMAT_PLAIN_TEX){
				codeBeginDoc.insert(0,"\\definecolor{"+colorname+"}{rgb}{"
					+kernel.format(red/255d)+","
					+kernel.format(green/255d)+","
					+kernel.format(blue/255d)+"}\n");
				CustomColor.put(c,colorname);
			}
			else if (format==GeoGebraToPgf.FORMAT_CONTEXT){
				codeBeginDoc.insert(0,"\\definecolor["+colorname+"][r="
						+kernel.format(red/255d)+",g="
						+kernel.format(green/255d)+",b="
						+kernel.format(blue/255d)+"]\n");
					CustomColor.put(c,colorname);
				
			}
		}
		sb.append(colorname);
	}

protected void createFrame() {
	frame=new PgfFrame(this);
}
}

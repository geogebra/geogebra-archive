package geogebra.export.pstricks;

import geogebra.Application;
import geogebra.euclidian.DrawAngle;
import geogebra.euclidian.DrawLine;
import geogebra.euclidian.DrawPoint;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoIntegralFunctions;
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
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

public abstract class GeoGebraExport implements ActionListener{
	protected final double PRECISION_XRANGE_FUNCTION=0.00001;
	protected StringBuffer code,codePoint,codePreamble,codeFilledObject,codeBeginDoc;
	protected Application app;
    protected Kernel kernel;
    protected Construction construction;
    protected EuclidianView euclidianView;
    protected ExportFrame frame;
    protected HashMap CustomColor;
    protected double xunit,yunit,xmin,xmax,ymin,ymax;
    public GeoGebraExport(Application app) {
    	this.app = app;
    	this.kernel = app.getKernel();
    	this.construction = kernel.getConstruction();
    	this.euclidianView = app.getEuclidianView();
    	initGui();
    }

    public Application getApp(){
    	return app;
    }
    /**
     * Initialize Gui JFrame
     */
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
    	createFrame();
    }
    /**
     * When The Button "generate Code" has been clicked
     */
	public void actionPerformed(ActionEvent e){
		generateAllCode();
	}
	/**
	 * 	This method	converts a double with engineering notation to decimal<br>
	 *	Example: 3E-4 becomes 0.0003 
	 * @param d The double to translate
	 * @return The resulting String
	 */	
	protected String sci2dec(double d){
		String s=String.valueOf(d).toLowerCase(Locale.US);
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
	/**
	 * This method creates the name for all custom colors
	 * @param red The red color part
	 * @param green The green color part
	 * @param blue The blue color part
	 * @return The name for the color
	 * uses hexadecimal decomposition
	 */
	
	String createCustomColor(int red,int green,int blue){
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
	
	
	/**
	 * This method is generic method to generate code according to GeoElement nature
	 * @param g GeoElement g
	 */
    protected void drawGeoElement(GeoElement g){
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
            else if (algo instanceof AlgoIntegralFunctions){
            	drawIntegralFunctions((GeoNumeric) g);
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
				point.setObjColor(geo.getObjectColor());
				point.setLabelColor(geo.getLabelColor());
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
					lines[i].setObjColor(geo.getObjectColor());					
					lines[i].setLabelColor(geo.getLabelColor());	
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
    protected boolean isSinglePointConic(GeoElement geo){
		if (geo.isGeoConic()){
			if (((GeoConic)geo).getType()==GeoConic.CONIC_SINGLE_POINT) 
				return true;
		}
		return false;
}
	protected boolean isDoubleLineConic(GeoElement geo){
		if (geo.isGeoConic()){
			if (((GeoConic)geo).getType()==GeoConic.CONIC_DOUBLE_LINE
					|| ((GeoConic)geo).getType()==GeoConic.CONIC_INTERSECTING_LINES
					|| ((GeoConic)geo).getType()==GeoConic.CONIC_PARALLEL_LINES
					) 
				return true;
		}
		return false;
	}
	protected boolean isEmpty(GeoElement geo){
		if (geo.isGeoConic()){
			if (((GeoConic)geo).getType()==GeoConic.CONIC_EMPTY	) 
				return true;
		}
		return false;	
	}
	protected int resizePt(int size){
		double height_geogebra=euclidianView.getHeight()/30;
		double height_latex=frame.getLatexHeight();
		double ratio=height_latex/height_geogebra;
		int tmp= (int)Math.round(ratio*size);
		if (tmp!=0) return tmp;
		else return 1;
	}
	abstract protected void drawGeoPoint(GeoPoint geo);
	abstract protected void drawGeoLine(GeoLine geo);
	abstract protected void drawGeoRay(GeoRay geo);
	abstract protected void drawGeoSegment(GeoSegment geo);
	abstract protected void drawPolygon(GeoPolygon geo);
	abstract protected void drawSlider(GeoNumeric geo);
	abstract protected void drawSlope(GeoNumeric geo);
	abstract protected void drawIntegral(GeoNumeric geo);
	abstract protected void drawIntegralFunctions(GeoNumeric geo);
	abstract protected void drawSumUpperLower(GeoNumeric geo);
	abstract protected void drawAngle(GeoAngle geo);
	abstract protected void drawGeoVector(GeoVector geo);
	abstract protected void drawGeoConic(GeoConic geo);
	abstract protected void drawGeoConicPart(GeoConicPart geo);
	abstract protected void drawLabel(GeoElement geo,Drawable drawGeo);
	abstract protected void drawFunction(GeoFunction geo);
	abstract protected void drawText(GeoText geo);
	abstract protected void drawLocus(GeoLocus geo);
	abstract protected void drawLine(double x1,double y1,double x2,double y2,GeoElement geo);
	abstract protected void drawArc(GeoAngle geo,double[] vertex,double angSt,double angEnd,double r);
	abstract protected void drawTick(GeoAngle geo,double[] vertex, double angleTick);
    abstract protected void drawArrowArc(GeoAngle geo,double[] vertex,double angSt, double angEnd,double r, boolean clockwise);
	abstract protected void createFrame();
	abstract protected void generateAllCode();
	abstract protected void ColorCode(Color color, StringBuffer sb);
	/**
	 * @return the xmin
	 */
	protected double getXmin() {
		return xmin;
	}

	/**
	 * @param xmin the xmin to set
	 */
	protected void setXmin(double xmin) {
		this.xmin = xmin;
	}

	/**
	 * @return the xmax
	 */
	protected double getXmax() {
		return xmax;
	}

	/**
	 * @param xmax the xmax to set
	 */
	protected void setXmax(double xmax) {
		this.xmax = xmax;
	}

	/**
	 * @return the ymin
	 */
	protected double getYmin() {
		return ymin;
	}

	/**
	 * @param ymin the ymin to set
	 */
	protected void setYmin(double ymin) {
		this.ymin = ymin;
	}

	/**
	 * @return the ymax
	 */
	protected double getYmax() {
		return ymax;
	}

	/**
	 * @param ymax the ymax to set
	 */
	protected void setYmax(double ymax) {
		this.ymax = ymax;
	}

	/**
	 * @return the xunit
	 */
	protected double getXunit() {
		return xunit;
	}

	/**
	 * @param xunit the xunit to set
	 */
	protected void setXunit(double xunit) {
		this.xunit = xunit;
	}

	/**
	 * @return the yunit
	 */
	protected double getYunit() {
		return yunit;
	}

	/**
	 * @param yunit the yunit to set
	 */
	protected void setYunit(double yunit) {
		this.yunit = yunit;
	}
	/**
	 * This method draws decoration on segment using abstract method drawLine
	 * @param A First Point
	 * @param B Second Point
	 * @param deco Decoration type
	 * @param geo GeoElement
	 */
	
	protected void mark(double[] A, double[] B,int deco,GeoElement geo){
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
	 		factor = tickSpacing / (2 * nLength);		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
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
	 		factor = tickSpacing / nLength;		
	 		vx = -ny * factor;
	 		vy =  nx * factor;	
	 		// use perpendicular vector to set ticks			 		
	 		factor = tickLength / nLength;
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
	/**
	 * This Method draws The decoration for GeoAngle geo
	 * @param geo The GeoAngle
	 * @param r The Radius
	 * @param vertex The vertex coordinates
	 * @param angSt Angle Start
	 * @param angEnd Angle End
	 */
    protected void markAngle(GeoAngle geo,double r, double[] vertex,double  angSt,double angEnd){
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
    		case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
    			drawArrowArc(geo,vertex,angSt,angEnd,r,false);
    		break;
    		case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
    			drawArrowArc(geo,vertex,angSt,angEnd,r,true);
   			break;
    	}
    }

	
	protected void addText(String st,boolean isLatex,int style,int size,Color geocolor){
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
	
}

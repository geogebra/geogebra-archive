/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author  Markus
 * @version 
 */
public abstract class GeoElement
	extends ConstructionElement
	implements ExpressionValue {

	// maximum label offset distance
	private static final int MAX_LABEL_OFFSET = 80;

	// private static int geoElementID = Integer.MIN_VALUE;
	
	private static final char[] pointLabels =
		{
			'A',
			'B',
			'C',
			'D',
			'E',
			'F',
			'G',
			'H',
			'I',
			'J',
			'K',
			'L',
			'M',
			'N',
			'O',
			'P',
			'Q',
			'R',
			'S',
			'T',
			'U',
			'V',
			'W',			
			'Z' };	
	
	private static final char[] functionLabels =
	{		
		'f',
		'g',
		'h',
		'p',
		'q',
		'r',
		's',
		't'
	};

	private static final char[] lineLabels =
		{
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'p',
			'q',
			'r',
			's',
			't' };

	private static final char[] vectorLabels =
		{
			'u',
			'v',
			'w',
			'z',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'p',
			'q',
			'r',
			's',
			't' };

	private static final char[] conicLabels =
		{ 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'p', 'q', 'r', 's', 't' };

	private static final char[] lowerCaseLabels =
		{
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'o',
			'p',
			'q',
			'r',
			's',
			't',
			'u',
			'v',
			'w',
			'z' };

	private static final char[] greekLowerCase =
		{
			'\u03b1',
			'\u03b2',
			'\u03b3',
			'\u03b4',
			'\u03b5',
			'\u03b6',
			'\u03b7',
			'\u03b8',
			'\u03b9',
			'\u03ba',
			'\u03bb',
			'\u03bc',
			'\u03bd',
			'\u03be',
			'\u03bf',
			'\u03c1',
			'\u03c3',
			'\u03c4',
			'\u03c5',
			'\u03c6',
			'\u03c7',
			'\u03c8',
			'\u03c9' };
	
	/*
	private static final char[] greekUpperCase =
	{ // Michael Borcherds 2008-02-23
	'\u0391',
	'\u0392',
	'\u0393',
	'\u0394',
	'\u0395',
	'\u0396',
	'\u0397',
	'\u0398',
	'\u0399',
	'\u039a',
	'\u039b',
	'\u039c',
	'\u039d',
	'\u039e',
	'\u039f',
	'\u03a0',
	'\u03a1',
	'\u03a3',
	'\u03a4',
	'\u03a5',
	'\u03a6',
	'\u03a7',
	'\u03a8',
	'\u03a9'};
	*/
	
	// GeoElement types
	public static final int GEO_CLASS_ANGLE = 10;
	public static final int GEO_CLASS_AXIS = 20;
	public static final int GEO_CLASS_BOOLEAN = 30;
	public static final int GEO_CLASS_CONIC = 40;
	public static final int GEO_CLASS_CONICPART = 50;
	public static final int GEO_CLASS_FUNCTION = 60;
	public static final int GEO_CLASS_FUNCTIONCONDITIONAL = 70;
	public static final int GEO_CLASS_IMAGE = 80;
	public static final int GEO_CLASS_LINE = 90;
	public static final int GEO_CLASS_LIST = 100;
	public static final int GEO_CLASS_LOCUS = 110;
	public static final int GEO_CLASS_NUMERIC = 120;
	public static final int GEO_CLASS_POINT = 130;
	public static final int GEO_CLASS_POLYGON = 140;
	public static final int GEO_CLASS_RAY = 150;
	public static final int GEO_CLASS_SEGMENT = 160;
	public static final int GEO_CLASS_TEXT = 170;
	public static final int GEO_CLASS_VECTOR = 180;
	public static final int GEO_CLASS_CURVE_CARTESIAN = 190;
	public static final int GEO_CLASS_CURVE_POLAR = 191;
	
	
	public static final int LABEL_NAME = 0;
	public static final int LABEL_NAME_VALUE = 1;
	public static final int LABEL_VALUE = 2;
	public static final int LABEL_CAPTION = 3; // Michael Borcherds 2008-02-18

	protected String label; // should only be used directly in subclasses
	private String oldLabel; // see doRenameLabel
	private String caption; // only used by GeoBoolean for check boxes at the moment	
	boolean labelWanted = false, labelSet = false, localVarLabelSet = false;
	private boolean euclidianVisible = true;
	protected boolean algebraVisible = true;
	private boolean labelVisible = true;
	private boolean isConsProtBreakpoint; // in construction protocol
	private boolean isAlgoMacroOutput; // is an output object of a macro construction
	private boolean fixed = false;
	private int labelMode = LABEL_NAME;
	protected int toStringMode = Kernel.COORD_CARTESIAN; // cartesian or polar	  
	private Color objColor = Color.black;
	private Color selColor = objColor;
	private Color labelColor = objColor; 
	private Color fillColor = objColor;
	public int layer=0; 	// Michael Borcherds 2008-02-23
	public double animationIncrement = 0.1;
	private double animationSpeed = 1;
	private boolean animating = false;
	
	public final static int ANIMATION_OSCILLATING = 0;
	public final static int ANIMATION_INCREASING = 1;
	public final static int ANIMATION_DECREASING = 2;
	private int animationType = ANIMATION_OSCILLATING;
	private int animationDirection = 1;
	
	public float alphaValue = 0.0f;
	public int labelOffsetX = 0, labelOffsetY = 0;
	private boolean auxiliaryObject = false;	
	// on change: see setVisualValues()

	// spreadsheet specific properties
	private Point spreadsheetCoords, oldSpreadsheetCoords;	 
	private int cellRangeUsers = 0; // number of AlgoCellRange using this cell: don't allow renaming when greater 0
	
	// condition to show object
	private GeoBoolean condShowObject;
	
	// function to determine color
	private GeoList colFunction; // { GeoNumeric red, GeoNumeric Green, GeoNumeric Blue }
	
	private boolean useVisualDefaults = true;
	private boolean isColorSet = false;
	protected boolean highlighted = false;
	private boolean selected = false;		
	private String strAlgebraDescription, strAlgebraDescTextOrHTML, strAlgebraDescriptionHTML,
		strLabelTextOrHTML, strLaTeX;
	private boolean strAlgebraDescriptionNeedsUpdate = true;
	private boolean strAlgebraDescTextOrHTMLneedsUpdate = true;
	private boolean strAlgebraDescriptionHTMLneedsUpdate = true;
	private boolean strLabelTextOrHTMLUpdate = true;
	private boolean strLaTeXneedsUpdate = true;	
	
	// line thickness and line type: s	
	// note: line thickness in Drawable is calculated as lineThickness / 2.0f
	public int lineThickness = EuclidianView.DEFAULT_LINE_THICKNESS;
	public int lineType = EuclidianView.DEFAULT_LINE_TYPE;		
	
	// decoration types
	public int decorationType = DECORATION_NONE;	
	
	// DECORATION
	public static final int DECORATION_NONE = 0;
	// segment decorations
	public static final int DECORATION_SEGMENT_ONE_TICK = 1;
	public static final int DECORATION_SEGMENT_TWO_TICKS = 2;
	public static final int DECORATION_SEGMENT_THREE_TICKS = 3;
// Michael Borcherds 2007-10-06
	public static final int DECORATION_SEGMENT_ONE_ARROW = 4;
	public static final int DECORATION_SEGMENT_TWO_ARROWS = 5;
	public static final int DECORATION_SEGMENT_THREE_ARROWS = 6;
//	 Michael Borcherds 2007-10-06
	// angle decorations
	public static final int DECORATION_ANGLE_TWO_ARCS = 1;
	public static final int DECORATION_ANGLE_THREE_ARCS = 2;
	public static final int DECORATION_ANGLE_ONE_TICK = 3;
	public static final int DECORATION_ANGLE_TWO_TICKS = 4;
	public static final int DECORATION_ANGLE_THREE_TICKS = 5;
//	 Michael Borcherds START 2007-11-19
	public static final int DECORATION_ANGLE_ARROW_ANTICLOCKWISE = 6; //	 Michael Borcherds 2007-10-22
	public static final int DECORATION_ANGLE_ARROW_CLOCKWISE = 7; //	 Michael Borcherds 2007-10-22
//	 Michael Borcherds END 2007-11-19
	
	// public int geoID;    
	//  static private int geoCounter = 0;
	private AlgoElement algoParent = null; // Parent Algorithm
	private ArrayList algorithmList = new ArrayList(50); 	// directly dependent algos
	
	//	set of all dependent algos sorted in topological order    
	private AlgorithmSet algoUpdateSet = new AlgorithmSet();
	//private TreeSet algoUpdateSet = new TreeSet();

	/********************************************************/

	/** Creates new GeoElement for given construction */
	public GeoElement(Construction c) {
		super(c);
		// this.geoID = geoCounter++;                                 
		setConstructionDefaults(); // init visual settings       				
		
		// new elements become breakpoints if only breakpoints are shown
		//isConsProtBreakpoint = cons.showOnlyBreakpoints();
		
		// ensure all new objects are in the top layer
		EuclidianView ev =c.getApplication().getEuclidianView();
		if (ev != null)
			layer = ev.getMaxLayerUsed();
	}

	/* *******************************************************/	
	
	/**
	 * Returns label of GeoElement. If the label is null then 
	 * algoParent.getCommandDescription() or  toValueString() is returned.     
	 */
	public String getLabel() {			
		if (!labelSet && !localVarLabelSet) {
			if (algoParent == null)
				return toOutputValueString();
			else
				return algoParent.getCommandDescription();
		} else
			return label;
	}

	public void copyLabel(GeoElement c) {
		label = c.label;
	}

	public void setLabelMode(int mode) {
		switch (mode) {
			case LABEL_NAME_VALUE :
				labelMode = LABEL_NAME_VALUE;
				break;

			case LABEL_VALUE :
				labelMode = LABEL_VALUE;
				break;
				
			case LABEL_CAPTION : // Michael Borcherds 2008-02-18
				labelMode = LABEL_CAPTION;
				break;

			default :
				labelMode = LABEL_NAME;
		}
	}

	public int getLabelMode() {
		return labelMode;
	}
	
	/** 
	 * Returns the GEO_CLASS_ type integer 
	 */
	public abstract int getGeoClassType();

	/** 
	 * every subclass implements it's own copy method 
	 *  this is needed for assignment copies like:
	 *  a = 2.7
	 *  b = a   (here copy() is needed)
	 * */
	public abstract GeoElement copy();
	
	/** 	
	 * This method always returns a GeoElement of the
	 * SAME CLASS as this GeoElement. Furthermore the resulting geo
	 * is in construction cons.
	 */
	public GeoElement copyInternal(Construction cons) {
		// default implementation: changed in some subclasses
		GeoElement geoCopy = copy();	
		geoCopy.setConstruction(cons);	
		return geoCopy;
	}		
	
	/**
	 * Copies the given points array. The resulting points are part of the given construction.
	 */
	public static GeoPoint [] copyPoints(Construction cons, GeoPoint [] points) {
		GeoPoint [] pointsCopy = new GeoPoint[points.length];
		for (int i=0; i < points.length; i++) {
			pointsCopy[i] = (GeoPoint) points[i].copyInternal(cons);			
			pointsCopy[i].set(points[i]);
		}
		return pointsCopy;
	}
	
	/**
	 * Copies the given segments array. The resulting segments are part of the given construction.
	 *
	public static GeoSegment [] copySegments(Construction cons, GeoSegment [] segments) {
		GeoSegment [] segmentsCopy = new GeoSegment[segments.length];
		for (int i=0; i < segments.length; i++) {
			segmentsCopy[i] = (GeoSegment) segments[i].copyInternal(cons);	
			
		}
		return segmentsCopy;
	}*/
	
	
	public ExpressionValue deepCopy(Kernel kernel) {
		//default implementation: changed in some subclasses
		return copy();				
	}
	
	public void resolveVariables() {     
    }
	

	/** every subclass implements it's own set method */
	public abstract void set(GeoElement geo);

	public abstract boolean isDefined();
	public abstract void setUndefined();
	public abstract String toValueString();	
	
	/**
	 * Sets this object to zero (number = 0, points = (0,0), etc.)
	 */
	public void setZero() {
		
	}
	
	/**
	 * Returns a value string that is saveable in an XML file.
	 * Note: this is needed for texts that need to be quoted
	 * in lists and as command arguments.
	 */
	public String toOutputValueString() {		
		return toValueString();		
	}
	
	public void setConstructionDefaults() {	
		if (useVisualDefaults) {
			ConstructionDefaults consDef = cons.getConstructionDefaults();
			if (consDef != null) 
				consDef.setDefaultVisualStyles(this);
		}
	}

	public void setObjColor(Color color) {
		isColorSet = true;
		
		objColor = color;
		labelColor = color;
		fillColor = color;
		setAlphaValue(alphaValue);

		//selColor = getInverseColor(objColor);
		selColor =
			new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
	}
	
	public boolean isColorSet() {
		return isColorSet;
	}
	
	// Michael Borcherds 2008-04-02
	private Color getRGBFromList(float alpha2)
	{
		if (alpha2 > 1f) alpha2 = 1f;
		if (alpha2 < 0f) alpha2 = 0f;
		
		int alpha = (int)(alpha2*255f);
		return getRGBFromList(alpha);
	}
	
	// Michael Borcherds 2008-04-02
	private Color getRGBFromList(int alpha)	{
		
		if (alpha > 255) alpha = 255;
		if (alpha < 0) alpha = 0;

		double redD = ((GeoNumeric)(colFunction.get(0))).getValue();
		double greenD = ((GeoNumeric)(colFunction.get(1))).getValue();
		double blueD = ((GeoNumeric)(colFunction.get(2))).getValue();
		
		//double epsilon = 0.000001; // 1 - floor(1) = 0 but we want 1.
		
		// make sure the colors are between 0 and 1
		redD = redD/2 - Math.floor(redD/2);
		greenD = greenD/2 - Math.floor(greenD/2);
		blueD = blueD/2 - Math.floor(blueD/2);
		
		
		// step function so
		// [0,1] -> [0,1]
		// [1,2] -> [1,0]
		// [2,3] -> [0,1]
		// [3,4] -> [1,0]
		// [4,5] -> [0,1] etc
		if (redD>0.5) redD=2*(1-redD); else redD=2*redD;
		if (greenD>0.5) greenD=2*(1-greenD); else greenD=2*greenD;
		if (blueD>0.5) blueD=2*(1-blueD); else blueD=2*blueD;
		
		//Application.debug("red"+redD+"green"+greenD+"blue"+blueD);
		
		return new Color((int)(redD*255.0), (int)(greenD*255.0), (int)(blueD*255.0), alpha);		

		/*
		if (red < 0) red = 0;
		if (red > 255) red = 255;
		
		if (green < 0) green = 0;
		if (green > 255) green = 255;
		
		if (blue < 0) blue = 0;
		if (blue > 255) blue = 255;	
		
		return new Color(red, green, blue, alpha);		*/
	}

	// Michael Borcherds 2008-04-02
	public Color getSelColor() {
		if (colFunction == null) return selColor;	
		//else return RGBtoColor((int)colFunction.getValue(),100);
		else return getRGBFromList(100);
	}
	
	// Michael Borcherds 2008-04-02
	public Color getFillColor() {
		if (colFunction == null) return fillColor;	
		//else return RGBtoColor((int)colFunction.getValue(),alphaValue);
		else return getRGBFromList(alphaValue);
	}
	
	/* return black if the color is white, so it can be seen
	 * 
	 */
	public Color getAlgebraColor() {
		Color col = getLabelColor();
		return col.equals(Color.white) ? Color.black : col;
	}

	
	// Michael Borcherds 2008-04-01
	public Color getLabelColor() {
		if (colFunction == null) return labelColor;	
		else return getObjectColor();
	}

	// Michael Borcherds 2008-04-01
	public void setLabelColor(Color color) {
		labelColor = color;
	}
		
	// Michael Borcherds 2008-04-02
	public Color getObjectColor() {
		if (colFunction == null) return objColor;
		//else return RGBtoColor((int)colFunction.getValue(),255);
		else return getRGBFromList(255);
	}

	// Michael Borcherds 2008-03-01
	public void setLayer(int layer){
		if (layer == this.layer) return;
		if (layer > EuclidianView.MAX_LAYERS) layer = EuclidianView.MAX_LAYERS;
		EuclidianView ev =app.getEuclidianView();
		if (ev != null) 
			ev.changeLayer(this,this.layer,layer);
		this.layer=layer;
	}
	
	// Michael Borcherds 2008-02-23
	public int getLayer(){
		return layer;
	}
	
	// Michael Borcherds 2008-02-23	
	public long getDrawingPriority()
	{
		
		long typePriority;
		
		switch (getGeoClassType())
		{
		case  GEO_CLASS_AXIS:
			typePriority = 10; break;
		case  GEO_CLASS_IMAGE:
		case  GEO_CLASS_BOOLEAN:
			typePriority = 20; break;
		case  GEO_CLASS_TEXT:
			typePriority = 30; break;
		case  GEO_CLASS_LIST:
			typePriority = 40; break;
		case  GEO_CLASS_POLYGON :
			typePriority = 50; break;
		case  GEO_CLASS_CONIC:
		case  GEO_CLASS_CONICPART:
			typePriority = 70; break;
		case  GEO_CLASS_ANGLE :
		case  GEO_CLASS_NUMERIC:
			typePriority = 80; break;
		case  GEO_CLASS_FUNCTION:
		case  GEO_CLASS_FUNCTIONCONDITIONAL:
		case  GEO_CLASS_CURVE_CARTESIAN :
		case  GEO_CLASS_CURVE_POLAR:
			typePriority = 90; break;
		case  GEO_CLASS_LINE:
			typePriority = 100; break;
		case  GEO_CLASS_RAY:
		case  GEO_CLASS_SEGMENT:
			typePriority = 110; break;
		case  GEO_CLASS_VECTOR:
			typePriority = 120; break;
		case  GEO_CLASS_LOCUS:
			typePriority = 130; break;
		case  GEO_CLASS_POINT:
			typePriority = 140; break;
		default: // shouldn't occur
			typePriority = 150;
		}
		
		// priority = 100 000 000
		 long ret = (long) (typePriority * 10E9 + getConstructionIndex());
		 
		 //Application.debug("priority: " + ret + ", " + this);
		 return ret;
	}

	public void setAlphaValue(float alpha) {
		if (fillColor == null || alpha < 0.0f || alpha > 1.0f)
			return;
		alphaValue = alpha;

		float[] rgb = new float[3];
		fillColor.getRGBColorComponents(rgb);
		fillColor = new Color(rgb[0], rgb[1], rgb[2], alpha);
	}

	public float getAlphaValue() {
		return alphaValue;
	}
		
	public boolean isLimitedPath() {
		return false;
	}
	
	public boolean isPath() {
		return false;
	}
	
	public boolean isGeoList() {
		return false;
	}		

	/**
	 * Sets all visual values from given GeoElement. 
	 * This will also affect tracing, label location
	 * and the location of texts for example.
	 */
	public void setAllVisualProperties(GeoElement geo) {
		setVisualStyle(geo);
		
		euclidianVisible = geo.euclidianVisible;
		algebraVisible = geo.algebraVisible;	
		labelOffsetX = geo.labelOffsetX;
		labelOffsetY = geo.labelOffsetY;	
		caption = geo.caption;
		
		if (isTraceable() && geo.isTraceable()) {
			((Traceable) this).setTrace(((Traceable) geo).getTrace());
		}
		
		if (isGeoPoint() && geo.isGeoPoint()) {
			((GeoPoint) this).setSpreadsheetTrace(((GeoPoint) geo).getSpreadsheetTrace());
		}
	}
	
	public void setVisualStyle(GeoElement geo) {
		// label style
		labelVisible = geo.labelVisible;
		labelMode = geo.labelMode;
		
		// style of equation, coordinates, ...
		if (getGeoClassType() == geo.getGeoClassType())
			toStringMode = geo.toStringMode;

		// colors
		objColor = geo.objColor;
		selColor = geo.selColor;
		labelColor = geo.labelColor;
		fillColor = geo.fillColor;
		alphaValue = geo.alphaValue;
		
		// line thickness and line type:	
		// note: line thickness in Drawable is calculated as lineThickness / 2.0f
		lineThickness = geo.lineThickness;
		lineType = geo.lineType;	
		decorationType = geo.decorationType;

		// set layer
		setLayer(geo.getLayer());

		// copy color function
		setColorFunction(geo.getColorFunction());
		
		// copy ShowObjectCondition, unless it generates a CirclularDefinitionException
		try { setShowObjectCondition(geo.getShowObjectCondition());}
		catch (Exception e) {}
}

	/**
		 * @return
		 *
	private static Color getInverseColor(Color c) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		 hsb[0] += 0.40;
		 if (hsb[0] > 1)
		  hsb[0]--;
		 hsb[1] = 1;
		 hsb[2] = 0.7f;
		 return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
		 
	}	*/

	/**
	 * Moves label by updating label offset
	 */
	public void setLabelOffset(int x, int y) {	
		double len = GeoVec2D.length(x, y);
		if (len > MAX_LABEL_OFFSET) {
			double factor = MAX_LABEL_OFFSET / len;
			x = (int) Math.round(factor * x);
			y = (int) Math.round(factor * y);
		}		

		labelOffsetX = x;
		labelOffsetY = y;
	}

	/**
	 * object should be visible in at least one view
	 */
	final public boolean isVisible() {
		return isEuclidianVisible() || isAlgebraVisible();
	}

	/**
	 * object should be drawn in euclidian view
	 */
	final public boolean isEuclidianVisible() {
		if (!showInEuclidianView()) return false;
		
		if (condShowObject == null)
			return euclidianVisible;
		else
			return condShowObject.getBoolean();
	}	

	public void setEuclidianVisible(boolean visible) {
		euclidianVisible = visible;		
	}

	public boolean isSetEuclidianVisible() {
		return euclidianVisible;
	}
	
	/**
	 * Returns whether this GeoElement is visible in 
	 * the construction protocol
	 */
	final public boolean isConsProtocolBreakpoint() {
		return isConsProtBreakpoint;
	}	
	
	public void setConsProtocolBreakpoint(boolean flag) {
		/*
		// all siblings need to have same breakpoint information
		GeoElement [] siblings = getSiblings();
		if (siblings != null) {			
			for (int i=0; i < siblings.length; i++) {
				siblings[i].isConsProtBreakpoint = flag;
			}	
		}*/
		
		isConsProtBreakpoint = flag;
	}
	
	/**
	 * Returns the children of the parent algorithm or null.	 
	 */
	public GeoElement [] getSiblings() {
		if (algoParent != null) {
			return algoParent.getOutput();
		}
		else 
			return null;
	}

	public boolean isDrawable() {
		return true;
	}

	public boolean isFillable() {
		return false;
	}

	public boolean isTraceable() {
		return false;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean flag) {
		if (!flag)
			fixed = flag;
		else if (isFixable())
			fixed = flag;
	}

	public boolean isFixable() {
		return isIndependent();
	}
	
	final public boolean isAuxiliaryObject() {
		return auxiliaryObject;
	}
	
	public GeoElement toGeoElement() {
		return this;
	}
	
	public void setAuxiliaryObject(boolean flag) {
		auxiliaryObject = flag;
		notifyUpdateAuxiliaryObject();
	}

	/**
	 * sets wheter the object's label should be drawn in 
	 * an EuclidianView
	 * @param visible
	 */
	public void setLabelVisible(boolean visible) {
		labelVisible = visible;
	}

	/**
	 * Returns whether the label should be shown in 
	 * Euclidian view.
	 */
	public boolean isLabelVisible() {
		return labelVisible;
	}
	
	/**
	 * Returns whether the label can be shown in 
	 * Euclidian view.
	 */
	final public boolean isLabelShowable() {
		return isDrawable() && 
				!(isTextValue() ||
					isGeoImage() ||
					isGeoList());
	}	
	
	/**
	 * Returns whether the value (e.g. equation) should be shown
	 * as part of the label description
	 */
	final public boolean isLabelValueShowable() {
		return !(isGeoLocus() ||
					isGeoBoolean());
	}

	/**
	 * object should be printed in algebra view
	 */
	final public boolean isAlgebraVisible() {
		return algebraVisible && showInAlgebraView();
	}
	
	public boolean showToolTipText() {
		return isAlgebraVisible();
	}

	public void setAlgebraVisible(boolean visible) {
		algebraVisible = visible;
	}

	public boolean isSetAlgebraVisible() {
		return algebraVisible;
	}

	protected abstract boolean showInAlgebraView();
	protected abstract boolean showInEuclidianView();
	
	final public boolean isEuclidianShowable() {
		return showInEuclidianView();
	}
	
	final public boolean isAlgebraShowable() {
		return showInAlgebraView();
	}

	public void setParentAlgorithm(AlgoElement algorithm) {
		algoParent = algorithm;
		if (algorithm != null)
			setConstructionDefaults(); // set colors to dependent colors
	}

	final public AlgoElement getParentAlgorithm() {
		return algoParent;
	}
	
	public ArrayList getAlogrithmList() {
		return algorithmList;
	}

	final public boolean isIndependent() {
		return (algoParent == null);
	}

	/**
	 * Returns whether this GeoElement can be 
	 * changed directly.
	 * Note: for points on lines this is different than isIndependent() 
	 */
	public boolean isChangeable() {
		return !fixed && isIndependent();
	}	
	
	/**
	 * Returns whether this GeoElement is a point
	 * on a path.
	 */
	public boolean isPointOnPath() {
		return false;
	}
	
	/**
	 * Returns whether this object may be redefined
	 */	
	public boolean isRedefineable() {
		return !fixed && app.letRedefine() && !(isTextValue() || isGeoImage()) &&
			(isChangeable() ||	// redefine changeable (independent and not fixed)
			 !isIndependent()); // redefine dependent object
	}

	/**
	 * Returns whether this GeoElement can be 
	 * moved in Euclidian View.
	 * Note: this is needed for texts
	 */
	public boolean isMoveable() {		
		return isChangeable();
	}
	
	/**
	 * Returns whether this (dependent) GeoElement has input points that can be 
	 * moved in Euclidian View.
	 * @return
	 */
	public boolean hasMoveableInputPoints() {
		// allow only moving of certain object types
		switch (getGeoClassType()) {		
			case GEO_CLASS_CONIC:
			case GEO_CLASS_CONICPART:
			case GEO_CLASS_IMAGE:
			case GEO_CLASS_LINE:
			case GEO_CLASS_RAY:
			case GEO_CLASS_SEGMENT:
			case GEO_CLASS_TEXT:
				return hasOnlyFreeInputPoints() && containsOnlyMoveableGeos(getFreeInputPoints());
				
			case GEO_CLASS_POLYGON:
				return containsOnlyMoveableGeos(getFreeInputPoints());
							
		}		
		return false;
	}
	
	/**
	 * Returns all free parent points of this GeoElement.	 
	 */
	public ArrayList getFreeInputPoints() {		
		if (algoParent == null) 
			return null;
		else
			return algoParent.getFreeInputPoints();		
	}
	
	final public boolean hasOnlyFreeInputPoints() {
		if (algoParent == null) 
			return false;
		else
			return algoParent.getFreeInputPoints().size() == algoParent.input.length;	
	}
	
	private static boolean containsOnlyMoveableGeos(ArrayList geos) {
		if (geos == null || geos.size() == 0)
			return false;
				
		for (int i=0; i < geos.size(); i++) {
			GeoElement geo = (GeoElement) geos.get(i);
    		if (!geo.isMoveable())
    			return false;
    	}
		return true;
	}
	
	/**
	 * Returns whether this object's class implements the interface Translateable.	 
	 */
	public boolean isTranslateable() {
		return false;
	}
	
	/**
	 * Returns whether this GeoElement can be 
	 * rotated in Euclidian View.
	 * Note: this is needed for images
	 */
	public boolean isRotateMoveable() {
		return isChangeable() && this instanceof PointRotateable;
	}

	/**
	 * Returns whether this GeoElement has
	 * properties that can be edited in a properties dialog.
	 */
	public boolean hasProperties() {
		//return isDrawable() || isChangeable();
		return true;
	}

	public void setAnimationStep(double s) {
		if (s > 0 && s < 1000)
			animationIncrement = s;
	}

	public double getAnimationStep() {
		return animationIncrement;
	}
	
	final public double getAnimationSpeed() {
		return animationSpeed;
	}
	
	public void setAnimationSpeed(double s) {
		double abs = Math.abs(s);
		if (0.001 <= abs && abs < 10)
			animationSpeed = s;
	}

	final public int getAnimationType() {
		return animationType;
	}
	
	final public void setAnimationType(int type) {
		switch (type) {
			case ANIMATION_INCREASING:
			case ANIMATION_OSCILLATING:
				animationType = type;
				animationDirection = 1;
				break;
			
			case ANIMATION_DECREASING:
				animationType = type;
				animationDirection = -1;
				break;
		}
	}
	
	protected int getAnimationDirection() {
		return animationDirection;
	}
	
	protected void changeAnimationDirection() {
		animationDirection = -animationDirection;
	}
	
	/**
	 * Sets the state of this object to animating on or off. Note that this
	 * 
	 * @see Animatable interface
	 */
	public synchronized void setAnimating(boolean flag) {
		boolean oldValue = animating;
		animating = flag && isAnimatable();
		
		// tell animation manager
		if (oldValue != animating) {
			AnimationManager am = kernel.getAnimatonManager();
			if (animating)
				am.addAnimatedGeo(this);
			else
				am.removeAnimatedGeo(this);		
		}
	}

	final public boolean isAnimating() {		
		return animating;
	}

	public boolean isAnimatable() {
		// over ridden by types that implement Animateable
		return false;
	}

    public String toLaTeXString(boolean symbolic) {
    	if (symbolic)
    		return toString();
    	else
    		return toDefinedValueString();	
    }     

	/* *******************************************************
	 * GeoElementTable Management
	 * Hashtable: String (label) -> GeoElement 
	 ********************************************************/

    public void addCellRangeUser() {
    	++cellRangeUsers;
    }
    
    public void removeCellRangeUser() {
    	if (cellRangeUsers > 0)
    		--cellRangeUsers;
    }
    
    public boolean isRenameable() {
    	// don't allow renaming when this object is used in 
		// cell ranges, see AlgoCellRange
    	return cellRangeUsers == 0;
    }
    
	/**
	 * renames this GeoElement to newLabel.
	 * @param newLabel
	 * @return true if label was changed
	 * @throws MyError: if new label is already in use
	 */
	public boolean rename(String newLabel) {		
		if (!isRenameable())
			return false;
		
		if (newLabel == null)
			return false;
		newLabel = newLabel.trim();
		if (newLabel.length() == 0)
			return false;
		String oldLabel = label;

		if (newLabel.equals(oldLabel))
			return false;
		else if (cons.isFreeLabel(newLabel)) {
			setLabel(newLabel); // now we rename				
			return true;
		} else {
			String str[] = { "NameUsed", newLabel };
			throw new MyError(app, str);
		}				
	}

	/**
	 * Returns whether this object's label has been set and is valid now.
	 * (this is needed for saving: only object's with isLabelSet() == true should
	 * be saved)
	 */
	final public boolean isLabelSet() {
		return labelSet;
	}

	/** 
	 * Sets label of a GeoElement and updates Construction list and GeoElement
	 * tabel (String label, GeoElement geo) in Kernel.
	 * If the old label was null, a new free label is assigned starting with
	 * label as a prefix.
	 * If newLabel is not already used, this object is renamed to newLabel. 
	 * Otherwise nothing is done.
	 */
	public void setLabel(String newLabel) {				
		if (cons.isSuppressLabelsActive())
			return;
		
		
		labelWanted = true;

		// had no label: try to set it
		if (!labelSet) {
			// to avoid wasting of labels, new elements must wait
			// until they are shown in one of the views to get a label            
			if (isVisible()) {
				doSetLabel(getFreeLabel(newLabel));
			} else {
				// remember desired label
				label = newLabel;
			}
		}
		// try to rename
		else {
			if (cons.isFreeLabel(newLabel)) { // rename    
				doRenameLabel(newLabel);
			}
		}
		
	}

//	private StringBuffer sb;
//
//	private String removeDollars(String s) {	
//		if (sb == null)
//			sb = new StringBuffer();
//		sb.setLength(0);
//
//		for (int i = 0; i < s.length(); i++) {
//			char c = s.charAt(i);
//			if (c != '$')
//				sb.append(c);
//		}
//
//		return sb.toString();
//	}	
	
	/**
	 * Sets label of a GeoElement and updates GeoElement table 
	 * (label, GeoElement). This method should only be used by
	 * MyXMLHandler.
	 */
	public void setLoadedLabel(String label) {
		if (labelSet) { // label was set before -> rename
			doRenameLabel(label);
		} else { // no label set so far -> set new label			 
			doSetLabel(getFreeLabel(label));
		}
	}
	
	public boolean setCaption(String caption) {
		if (caption == null 
			|| caption.equals(label)) {
			caption = null;
			return false;
		}
		
		caption = caption.trim();
		
		if (caption.trim().length() == 0) {
			this.caption = null;
			return true;
		}
		
		this.caption = caption.trim();
		return true;
	}		

	public String getCaption() {
		if (caption == null)
			return getLabel();
		else
			return caption;
	}
	
	/**
	 * Sets label of a local variable object. This method should
	 * only be used by Construction.
	 */
	public void setLocalVariableLabel(String label) {
		this.label = label;
		localVarLabelSet = true;
	}

	private void doSetLabel(String label) {		
		// UPDATE KERNEL						
		if (!labelSet && isIndependent()) {
			//	add independent object to list of all Construction Elements
			// dependent objects are represented by their parent algorithm
			cons.addToConstructionList(this, true);			
		}
		
		/*
		if (!cons.isFreeLabel(label)) {
			try {
				throw new Exception("SET LABEL: label: " + label + ", type: " + this.getTypeString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Application.debug("SET LABEL: " + label + ", type: " + this.getTypeString());
		}
		*/	
				
		this.label = label; // set new label
		labelSet = true;
		labelWanted = false; // got a label, no longer wanted					
		
		cons.putLabel(this); // add new table entry			
		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();
		
		notifyAdd();		
	}	
	
	private void updateSpreadsheetCoordinates() {				
		if(labelSet 
			&& Character.isLetter(label.charAt(0)) // starts with letter
			&& Character.isDigit(label.charAt(label.length()-1)))  // ends with digit
		{
			
			// init old and current spreadsheet coords
			if (spreadsheetCoords == null) {	
				oldSpreadsheetCoords = null;		
				spreadsheetCoords = new Point();
			} else {
				if (oldSpreadsheetCoords == null) 
					oldSpreadsheetCoords = new Point();
				oldSpreadsheetCoords.setLocation(spreadsheetCoords);
			}
			
			// we need to also support wrapped GeoElements like
			// $A4 that are implemented as dependent geos (using ExpressionNode)
			Matcher matcher = GeoElement.spreadsheetPattern.matcher(getLabel());				
			int column = getSpreadsheetColumn(matcher);
			int row = getSpreadsheetRow(matcher);
			if (column >= 0 && row >= 0) {			
				spreadsheetCoords.setLocation(column, row);
			} else {
				spreadsheetCoords = null;
			}
    	} else {
    		oldSpreadsheetCoords = spreadsheetCoords;
    		spreadsheetCoords = null;
    	}
		
		
		//Application.debug("update spread sheet coords: " + this + ", " +  spreadsheetCoords + ", old: " + oldSpreadsheetCoords);
	}		
	
	/**
	 * Returns a point with the spreadsheet coordinates of the given inputLabel.
	 * Note that this can also be used for names that include $ signs like "$A1".
	 * @return null for non-spreadsheet names
	 */
	public static Point getSpreadsheetCoordsForLabel(String inputLabel) {
		// we need to also support wrapped GeoElements like
		// $A4 that are implemented as dependent geos (using ExpressionNode)	
		Matcher matcher = GeoElement.spreadsheetPattern.matcher(inputLabel);				
		int column = getSpreadsheetColumn(matcher);
		int row = getSpreadsheetRow(matcher);

//		System.out.println("match: " + inputLabel);
//		for (int i=0; i < matcher.groupCount(); i++) {
//			System.out.println("    group: " + i + ": " + matcher.group(i));
//		}
		
		if (column >= 0 && row >= 0)
			return new Point(column, row);
		else 
			return null;
	}

	// Cong Liu
	public static String getSpreadsheetCellName(int column, int row) {
		++row;
		return getSpreadsheetColumnName(column) + row;
	}
		
	public static String getSpreadsheetColumnName(int i) {
		++ i;		
		String col = "";
		while (i > 0) {
			col = (char)('A' + (i % 26) - 1) + col;
			i /= 26;
		}
		return col;
	}
	
	public static String getSpreadsheetColumnName(String label) {
		Matcher matcher = spreadsheetPattern.matcher(label);
		if (! matcher.matches()) return null;
		return matcher.group(1);
	}
	
	
	 /**
     * Returns the spreadsheet reference name of this GeoElement using $ signs
     * for absolute spreadsheet reference names
     * like A$1 or $A$1.
     */
	public String getSpreadsheetLabelWithDollars(boolean col$, boolean row$) {
		String colName = getSpreadsheetColumnName(spreadsheetCoords.x);
		String rowName = Integer.toString(spreadsheetCoords.y + 1);
		
		StringBuffer sb = new StringBuffer(label.length() + 2);
		if (col$) sb.append('$');
		sb.append(colName);
		if (row$) sb.append('$');
		sb.append(rowName);			
		return sb.toString();
	}
	 	
	// Michael Borcherds
	public static boolean isSpreadsheetLabel(String str) {
		Matcher matcher = spreadsheetPattern.matcher(str);
		if (matcher.matches()) return true;
		else return false;		
	}
	
	// Cong Liu	
	public static final Pattern spreadsheetPattern = 
		Pattern.compile("\\$?([A-Z]+)\\$?([0-9]+)");

	// Cong Liu	
	public static int getSpreadsheetColumn(Matcher matcher) {	
		if (! matcher.matches()) return -1;
						
		String s = matcher.group(1);
		int column = 0;
		while (s.length() > 0) {
			column *= 26;
			column += s.charAt(0) - 'A' + 1;
			s = s.substring(1);
		}
		//Application.debug(column);
		return column - 1;
	}
	    
	// Cong Liu	
	public static int getSpreadsheetRow(Matcher matcher) {		
		if (! matcher.matches()) return -1;
		String s = matcher.group(2);
		return Integer.parseInt(s) - 1;
	}
	
	 private void doRenameLabel(String newLabel) {
		if (newLabel == null || newLabel.equals(label)) 
			return;
		
		/*
		if (!cons.isFreeLabel(newLabel)) {
			try {
				throw new Exception("RENAME ERROR: old: " + label + ", new: " + newLabel + ", type: " + this.getTypeString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Application.debug("RENAME: old: " + label + ", new: " + newLabel + ", type: " + this.getTypeString());
		}
		*/
		
		//	UPDATE KERNEL			
		cons.removeLabel(this); // remove old table entry
		oldLabel = label; // remember old label (for applet to javascript rename)
		label = newLabel; // set new label
		cons.putLabel(this); // add new table entry    
		
		algebraStringsNeedUpdate();
		updateSpreadsheetCoordinates();
				
		kernel.notifyRename(this); // tell views   		
		updateCascade();
		}
	
	/**
	 * Returns the label of this object before rename()
	 * was called.	
	 */
	final public String getOldLabel() {
		return oldLabel;
	}

	/**
	 *  set labels for array of GeoElements with given label prefix.
	 * e.g. labelPrefix = "F", geos.length = 2 sets geo[0].setLabel("F_1")
	 * and geo[0].setLabel("F_2")
	 */
	public static void setLabels(String labelPrefix, GeoElement[] geos) {
		if (geos == null) return; 
		
		int visible = 0;
		for (int i = 0; i < geos.length; i++)
			if (geos[i].isVisible())
				visible++;

		switch (visible) {
			case 0 : // no visible geos: they all get the labelPrefix as suggestion
				for (int i = 0; i < geos.length; i++)
					geos[i].setLabel(labelPrefix);
				break;

			case 1 : //	if there is only one visible geo, don't use indices
				geos[0].setLabel(labelPrefix);
				break;

			default : 
				// is this a spreadsheet label?
				Matcher matcher = GeoElement.spreadsheetPattern.matcher(labelPrefix);								
				if (matcher.matches()) {
					// more than one visible geo and it's a spreadsheet cell
					// use D1, E1, F1, etc as names
					int col = getSpreadsheetColumn(matcher);
					int row = getSpreadsheetRow(matcher);
					for (int i = 0; i < geos.length; i++)
						geos[i].setLabel(geos[i].getFreeLabel(getSpreadsheetCellName(col + i, row)));	
				} else { // more than one visible geo: use indices if we got a prefix
					for (int i = 0; i < geos.length; i++)
						geos[i].setLabel(geos[i].getIndexLabel(labelPrefix));	
				}
		}
	}

	/**
	 * set labels for array of GeoElements pairwise:
	 * geos[i].setLabel(labels[i])
	 */
	static void setLabels(String[] labels, GeoElement[] geos) {
		setLabels(labels, geos, false);
	}
	
	static void setLabels(String[] labels, GeoElement[] geos, boolean indexedOnly) {
		int labelLen = (labels == null) ? 0 : labels.length;

		if (labelLen == 1 && labels[0] != null && !labels[0].equals("")) {
			setLabels(labels[0], geos);
			return;
		}

		String label;		
		for (int i = 0; i < geos.length; i++) {			
			if (i < labelLen) {
				label = labels[i]; 				
			} else {
				label = null;
			}
			
			if (indexedOnly)
				label = geos[i].getIndexLabel(label);
			
			geos[i].setLabel(label);						
		}
	}

	/** Get a free label. Try the suggestedLabel first */
	public String getFreeLabel(String suggestedLabel) {		
		if (suggestedLabel != null) {
			if ("x".equals(suggestedLabel) || "y".equals(suggestedLabel))
				return getDefaultLabel();
			
			if (cons.isFreeLabel(suggestedLabel))
				return suggestedLabel;
			else if (suggestedLabel.length() > 0)
				return getIndexLabel(suggestedLabel);
		}
				
		// standard case: get default label
		return getDefaultLabel(); 
	}
		
	public String getDefaultLabel() {				
		char[] chars;

		if (isGeoPoint()) {
			// Michael Borcherds 2008-02-23
			// use Greek upper case for labeling points if lenguage is Greek (el)
			// TODO decide if we want this as an option, or just uncomment the next line
			// if (app.languageIs(app.getLocale(), "el")) chars=greekUpperCase; else 
				chars = pointLabels;
		} else if (isGeoFunction()) {
			chars = functionLabels;
		} else if (isGeoLine()) {
			chars = lineLabels;
		} else if (isGeoConic()) {
			chars = conicLabels;
		} else if (isGeoVector()) {
			chars = vectorLabels;
		}  else if (isGeoAngle()) {
			chars = greekLowerCase;
		} 
		else if (isGeoPolygon()) {
			int counter = 0;
			String str;
			do {
				counter++;
				str = app.getPlain("Name.polygon") + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		}		
		else if (isGeoText()) {
			int counter = 0;
			String str;
			do {
				counter++;
				str = app.getPlain("Name.text") + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		} else if (isGeoImage()) {
			int counter = 0;			
			String str;
			do {
				counter++;
				str = app.getPlain("Name.picture") + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		} else if (isGeoLocus()) {
			int counter = 0;			
			String str;
			do {
				counter++;
				str = app.getPlain("Name.locus") + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		} else if (isGeoList()) {
			GeoList list = (GeoList) this;
			int counter = 0;			
			String str;
			do {
				counter++;
				str = list.isMatrix() ? app.getPlain("Name.matrix") + counter : app.getPlain("Name.list") + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		}
		else {
			chars = lowerCaseLabels;
		}

		int counter = 0, q, r;
		sbDefaultLabel.setLength(0);
		sbDefaultLabel.append(chars[0]);
		while (!cons.isFreeLabel(sbDefaultLabel.toString())) {
			sbDefaultLabel.setLength(0);
			q = counter / chars.length; // quotient
			r = counter % chars.length; // remainder                    
			sbDefaultLabel.append(chars[r]);						
						
			if (q > 0) {
				// don't use indices
				//sbDefaultLabel.append(q);
				
				// q as index
				if (q < 10) {
					sbDefaultLabel.append('_');
					sbDefaultLabel.append(q);
				} else {
					sbDefaultLabel.append("_{");
					sbDefaultLabel.append(q);
					sbDefaultLabel.append('}');
				}
				
			}
			counter++;					
		}
		return sbDefaultLabel.toString();
	}
	private StringBuffer sbDefaultLabel = new StringBuffer();
	
	public String getIndexLabel(String prefix) {
		if (prefix == null) 
			return getFreeLabel(null) + "_1";
		
		// start numbering with indices using suggestedLabel
		// as prefix
		String pref;
		int pos = prefix.indexOf('_');
		if (pos == -1)
			pref = prefix;
		else
			pref = prefix.substring(0, pos);
		
		sbIndexLabel.setLength(0);
		int n = 0; // index
		do {
			sbIndexLabel.setLength(0);
			sbIndexLabel.append(pref);
			// n as index
			n++;
			if (n < 10) {
				sbIndexLabel.append('_');
				sbIndexLabel.append(n);
			} else {
				sbIndexLabel.append("_{");
				sbIndexLabel.append(n);
				sbIndexLabel.append('}');
			}
		} while (!cons.isFreeLabel(sbIndexLabel.toString()));
		return sbIndexLabel.toString();
	}
	private StringBuffer sbIndexLabel = new StringBuffer();

	/**
	 * Removes this object and all dependent objects from the Kernel.
	 * If this object is not independent, it's parent algorithm is removed too.
	 */
	final public void remove() {
		// dependent object: remove parent algorithm
		if (algoParent != null) {
			algoParent.remove(this);
		} else {
			doRemove();
		}
	}

	// removes this GeoElement and all its dependents
	protected void doRemove() {
		// stop animation of this geo
		setAnimating(false);
		
		// remove this object from List
		if (isIndependent()) 
			cons.removeFromConstructionList(this);

		// remove all dependent algorithms
		AlgoElement algo;
		Object[] algos = algorithmList.toArray();
		for (int i = 0; i < algos.length; i++) {
			algo = (AlgoElement) algos[i];
			algo.remove(this);
		}

		// remove this object from table
		if (isLabelSet()) {
			cons.removeLabel(this);			
		}

		// remove from selection
		app.removeSelectedGeo(this, false);	
				
		// notify views before we change labelSet
		notifyRemove();
		
		labelSet = false;
		labelWanted = false;
	}

	final public void notifyAdd() {
		kernel.notifyAdd(this);

		//		Application.debug("add " + label);
		// printUpdateSets();
	}

	final public void notifyRemove() {
		kernel.notifyRemove(this);
		
		//Application.debug("remove " + label);
		//printUpdateSets();
	}
	
	final public void notifyUpdate() {
		kernel.notifyUpdate(this);

		//	Application.debug("update " + label);
		//	printUpdateSets();
	}
	
	final public void notifyUpdateAuxiliaryObject() {
		kernel.notifyUpdateAuxiliaryObject(this);

		//		Application.debug("add " + label);
		//	printUpdateSets();
	}


/*
	private void printUpdateSets() {		
		Iterator itList = cons.getAllGeoElementsIterator();
		while (itList.hasNext()) {
			GeoElement geo = (GeoElement) itList.next();		
			Application.debug(geo.label + ": " + geo.algoUpdateSet.toString());			
		}    	    	
	}
	*/

	/* *******************************************************
	 * AlgorithmList Management
	 * each GeoElement has a list of dependent algorithms 
	 ********************************************************/
	
	/**
	 * add algorithm to dependency list of this GeoElement
	 */
	public final void addAlgorithm(AlgoElement algorithm) {	
		if (!algorithmList.contains(algorithm))
			algorithmList.add(algorithm);
		addToUpdateSets(algorithm);
	}
	
	/**
	 * Adds the given algorithm to the dependency list of this GeoElement.
	 * The algorithm is NOT added to the updateSet of this GeoElement!
	 * I.e. when updateCascade() is called the given algorithm will
	 * not be updated.
	 */
	final void addToAlgorithmListOnly(AlgoElement algorithm) {
		if (!algorithmList.contains(algorithm))
			algorithmList.add(algorithm);		
	}
	
	/**
	 * Adds the given algorithm to the update set this GeoElement.
	 * Note: the algorithm is NOT added to the algorithm list, 
	 * i.e. the dependency graph of the construction.
	 */
	final void addToUpdateSetOnly(AlgoElement algorithm) {
		addToUpdateSets(algorithm);
	}		

	/**
	 * remove algorithm from dependency list of this GeoElement
	 */
	final void removeAlgorithm(AlgoElement algorithm) {
		algorithmList.remove(algorithm);
		removeFromUpdateSets(algorithm);
	}

	protected AlgorithmSet getAlgoUpdateSet() {
		return algoUpdateSet;
	}		
		
	
	/**
	 * add algorithm to update sets up the construction graph
	 */
	private void addToUpdateSets(AlgoElement algorithm) {				
		if (algoUpdateSet.add(algorithm)) {		
			// propagate up the graph if we didn't do this before			
			if (algoParent != null) {
				GeoElement [] input = algoParent.getInput();
				for (int i = 0; i < input.length; i++) {
					input[i].addToUpdateSets(algorithm);
				}
			}
		}
	}

	/**
	 * remove algorithm from update sets  up the construction graph
	 */
	private void removeFromUpdateSets(AlgoElement algorithm) {
		//	Application.debug(label + " remove from updateSet: " + algorithm.getCommandDescription());
		if (algoUpdateSet.remove(algorithm)) {
			//	propagate up the graph				
			if (algoParent != null) {
				GeoElement [] input = algoParent.getInput();
				for (int i = 0; i < input.length; i++) {
					input[i].removeFromUpdateSets(algorithm);
				}
			}
		}
	}
	
	/**
	 * updates this object and notifies kernel. 
	 * Note: no dependent objects are updated.
	 * @see updateRepaint()
	 */
	public void update() {		
		if (labelWanted && !labelSet) {
			// check if this object's label needs to be set
			if (isVisible())
				setLabel(label);			
		}

		// texts need updates
		algebraStringsNeedUpdate();					    			
		
		kernel.notifyUpdate(this);	        				
	}
	
	private void algebraStringsNeedUpdate() {
		strAlgebraDescriptionNeedsUpdate = true;	
		strAlgebraDescTextOrHTMLneedsUpdate = true;
		strAlgebraDescriptionHTMLneedsUpdate = true;
		strLabelTextOrHTMLUpdate = true;
		strLaTeXneedsUpdate = true;
	}
	
	/**
	 * Updates this object and all dependent ones. Note: no repainting is done afterwards! 
	 * 	 synchronized for animation
	 */
	final public void updateCascade() {
		update();

		// update all algorithms in the algorithm set of this GeoElement        
		algoUpdateSet.updateAll();		
	}
	
	
	/**
	 * Updates all GeoElements in the given ArrayList and all algorithms that depend on free GeoElements in that list.
	 * Note: this method is more efficient than calling updateCascade() for all individual
	 * GeoElements. 
	 */
	final static public synchronized void updateCascade(ArrayList geos) {
		// only one geo: call updateCascade()
		if (geos.size() == 1) {
			GeoElement geo = (GeoElement) geos.get(0);
			geo.updateCascade();
			return;
		}
		
		
		// build update set of all algorithms in construction element order						
		if (algoSetUpdateCascade == null) 
			algoSetUpdateCascade = new TreeSet();
		else
			algoSetUpdateCascade.clear();
		
		int size = geos.size();
		for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement) geos.get(i);
			geo.update();
			
			if (geo.isIndependent()) {
				// add all dependent algos of geo to the overall algorithm set
				geo.algoUpdateSet.addAllToCollection(algoSetUpdateCascade);
			}
		}
		
		// now we have one nice algorithm set that we can update
		if (algoSetUpdateCascade.size() > 0) {
			Iterator it = algoSetUpdateCascade.iterator();
			while (it.hasNext()) {
				AlgoElement algo = (AlgoElement) it.next();
				algo.update();			
			}
		}
	}
	private static TreeSet algoSetUpdateCascade;

	/**
	 * Updates this object and all dependent ones. 
	 * Notifies kernel to repaint views. 
	 */
	final public void updateRepaint() {
		updateCascade();
		kernel.notifyRepaint();
	}
	
	final void updateCascadeParentAlgo() {		
		if (algoParent != null) {
			algoParent.compute();
			for (int i=0; i < algoParent.output.length; i++) {
				algoParent.output[i].updateCascade();
			}
		}
	}

	public String toString() {
		return label;
	}

	/*
	 * implementation of interface ExpressionValue
	 */
	public boolean isConstant() {
		return false;
	}

	public boolean isLeaf() {
		return true;
	}

	public ExpressionValue evaluate() {
		return this;
	}

	public HashSet getVariables() {
		HashSet ret = new HashSet();
		ret.add(this);
		return ret;
	}
	
	
	/**
	 * Returns all predecessors (of type GeoElement) that this object depends on.
	 * The predecessors are sorted topologically.
	 */
	public TreeSet getAllPredecessors() {
		TreeSet set = new TreeSet();
		addPredecessorsToSet(set, false);
		return set;
	}		
	
	/**
	 * Returns all independent predecessors (of type GeoElement) that this object depends on.
	 * The predecessors are sorted topologically. Note: when this method is called
	 * on an independent geo that geo is included in the TreeSet.
	 */
	public TreeSet getAllIndependentPredecessors() {
		TreeSet set = new TreeSet();
		addPredecessorsToSet(set, true);
		return set;
	}

	// adds all predecessors of this object to the given set
	// the set is topologically sorted 
	// @param onlyIndependent: whether only indpendent geos should be added
	final public void addPredecessorsToSet(TreeSet set, boolean onlyIndependent) {
		if (algoParent == null) { // independent geo
			if (onlyIndependent) {
				// OLD: LinkeList implementation
					// remove this geo and  insert it again at the beginning of the list
					// this ensures the topological sorting
					// and it also ensures that each element is only once in the list
					// list.remove(this);
					// list.addFirst(this);				
				
				set.add(this);
			}
		} else { // parent algo
			algoParent.addPredecessorsToSet(set, onlyIndependent);          
		}	
	}
	
	/**
	 * Returns whether geo depends on this object.
	 */
	public boolean isParentOf(GeoElement geo) {
		if (geo == null)
			return false;
		else
			return geo.isChildOf(this);
	}

	
	/**
	 * Returns whether this object is parent of other geos.
	 */
	public boolean hasChildren() {
		return algoUpdateSet.getSize() > 0;
	}

	/**
	 * Returns whether this object is dependent on geo.
	 */
	public boolean isChildOf(GeoElement geo) {				
		if (geo == null || algoParent == null)
			return false;
			
		GeoElement [] input = algoParent.getInput();
		for (int i = 0; i < input.length; i++) {
			if (geo == input[i])
				return true;
			if (input[i].isChildOf(geo))
				return true;
			
			//Application.debug(input[i].getLabel() + " isChildOf: " + geo.getLabel());
		}
		return false;
	}
	
	/**
	 * Returns all children (of type GeoElement) that depend on this object.
	 */
	public TreeSet getAllChildren() {
		TreeSet set = new TreeSet();
		Iterator it = algoUpdateSet.getIterator();
		while (it.hasNext()) {
			AlgoElement algo = (AlgoElement) it.next();
			for (int i = 0; i < algo.output.length; i++) {
				set.add(algo.output[i]);					
			}			
		}
		return set;
	}


	
	/*
	* implementation of abstract methods from ConstructionElement
	*/
	public GeoElement[] getGeoElements() {
		return myGeoElements;
	}
	private GeoElement [] myGeoElements = new GeoElement[] { this };
	
	final public boolean isAlgoElement() {
		return false;
	}

	final public boolean isGeoElement() {
		return true;
	}	
	


	/**
	 * Returns construction index in current construction.
	 * For a dependent object the construction index of its parent algorithm is returned.
	 */
	public int getConstructionIndex() {
		if (algoParent == null)
			return super.getConstructionIndex();
		else
			return algoParent.getConstructionIndex();
	}

	/**
	 * Returns the smallest possible construction index for this object in its construction.
	 * For an independent object 0 is returned.
	 */
	public int getMinConstructionIndex() {
		if (algoParent == null)
			return 0;
		else
			return algoParent.getMinConstructionIndex();
	}

	/**
	 * Returns the largest possible construction index for this object in its construction.
	 */
	public int getMaxConstructionIndex() {
		if (algoParent == null) {
			// independent object:
			// index must be less than every dependent algorithm's index
			int min = cons.steps();
			int size = algorithmList.size();
			for (int i = 0; i < size; ++i) {
				int index =
					((AlgoElement) algorithmList.get(i)).getConstructionIndex();
				if (index < min)
					min = index;
			}
			return min - 1;
		} else
			//	dependent object
			return algoParent.getMaxConstructionIndex();
	}
	
	/**
	 * Returns the label for a free geo and the definition description 
	 * for a dependent geo.
	 * @return
	 */	
	public String getLabelOrCommandDescription() {
		if (algoParent == null)
			return getLabel();
		else
			return algoParent.getCommandDescription();	
    }

	public String getDefinitionDescription() {
		if (algoParent == null)
			return "";
		else
			return algoParent.toString();
	}

	public String getDefinitionDescriptionHTML(boolean addHTMLtag) {
		if (algoParent == null)
			return "";
		else
			return indicesToHTML(algoParent.toString(), addHTMLtag);
	}

	public String getCommandDescription() {
		if (algoParent == null)
			return "";
		else
			return algoParent.getCommandDescription();
	}

	public String getCommandDescriptionHTML(boolean addHTMLtag) {
		if (algoParent == null)
			return "";
		else
			return indicesToHTML(
				algoParent.getCommandDescription(),
				addHTMLtag);
	}
	
	/**
	 * Returns type string of GeoElement. Note: this is
	 * equal to getClassName().substring(3), but faster
	 */
	abstract protected String getTypeString();
	/*{
		// e.g. GeoPoint -> type = Point
		//return getClassName().substring(3);		
	}*/
	
	final public String getObjectType() {
		return getTypeString();
	}
	
	public String translatedTypeString() {
		return app.getPlain(getTypeString());
	}

	/**
	 * returns Type, label and definition information about this GeoElement
	 * (for tooltips and error messages)	 
	 */
	final public String getLongDescription() {
		if (algoParent == null)
			return getNameDescription();
		else {			
			sbLongDesc.setLength(0);
			sbLongDesc.append(getNameDescription());			
			// add dependency information
			sbLongDesc.append(": ");
			sbLongDesc.append(algoParent.toString());			
			return sbLongDesc.toString();
		}
	}
	private StringBuffer sbLongDesc = new StringBuffer();

	/**
	 * returns Type, label and definition information about this GeoElement
	 * as html string.
	 * (for tooltips and error messages)	 
	 */
	final public String getLongDescriptionHTML(
		boolean colored,
		boolean addHTMLtag) {
		if (algoParent == null || isTextValue())
			return getNameDescriptionHTML(colored, addHTMLtag);
		else {
			sbLongDescHTML.setLength(0);
			
			String label = getLabel();
			String typeString = translatedTypeString();			
			
			// html string	
			if (addHTMLtag)
				sbLongDescHTML.append("<html>");
			
			boolean reverseOrder = app.isReverseNameDescriptionLanguage();		
			if (!reverseOrder) {
				//	standard order: "point A"
				sbLongDescHTML.append(typeString);				
				sbLongDescHTML.append(' ');
			}				
						
			if (colored) {
				sbLongDescHTML.append("<b><font color=\"#");
				sbLongDescHTML.append(Util.toHexString(getAlgebraColor()));
				sbLongDescHTML.append("\">");
			}
			sbLongDescHTML.append(indicesToHTML(label, false));
			if (colored)
				sbLongDescHTML.append("</font></b>");
			
			if (reverseOrder) {
				// reverse order: "A point"				
				sbLongDescHTML.append(' ');
				sbLongDescHTML.append(typeString);								
			}

			// add dependency information
			if (algoParent != null) {
				// Guy Hed, 25.8.2008
				// In order to present the text cottectly in Hebrew and Arabic:
				boolean rightToLeft = app.isRightToLeftReadingOrder(); 
				if (rightToLeft) 
					sbLongDescHTML.append("\u200e\u200f: \u200e"); 
				else
					sbLongDescHTML.append(": ");
				sbLongDescHTML.append(indicesToHTML(algoParent.toString(), false));
				if (rightToLeft) 
					sbLongDescHTML.append("\u200e"); 
			}
			if (addHTMLtag)
				sbLongDescHTML.append("</html>");
			return sbLongDescHTML.toString();
		}
	}
	private StringBuffer sbLongDescHTML = new StringBuffer();


	/**
	 * Returns long description for all GeoElements in given array.	 	 
	 */
	final public static String getToolTipDescriptionHTML(
		ArrayList geos,
		boolean colored,
		boolean addHTMLtag) {
		if (geos == null)
			return null;
		
		sbToolTipDesc.setLength(0);		
		if (addHTMLtag)
			sbToolTipDesc.append("<html>");
		int count=0;
		for (int i = 0; i < geos.size(); ++i) {
			GeoElement geo = (GeoElement) geos.get(i);
			if (geo.showToolTipText()) {
				count++;
				sbToolTipDesc.append(geo.getLongDescriptionHTML(colored, false));			
				if (i+1 < geos.size())
					sbToolTipDesc.append("<br>");
			}				
		}
		if (count == 0) return null;
		if (addHTMLtag)
			sbToolTipDesc.append("</html>");
		return sbToolTipDesc.toString();
	}
	private static StringBuffer sbToolTipDesc = new StringBuffer();

	/**
		* Returns the label and/or value of this object for 
		* showing in EuclidianView. This depends on the current
		* setting of labelMode:
		* LABEL_NAME : only label
		* LABEL_NAME_VALUE : label and value
		*/
	public String getLabelDescription() {
		switch (labelMode) {
			case LABEL_NAME_VALUE :
				return getAlgebraDescription();

			case LABEL_VALUE :
				return toDefinedValueString();
				
			case LABEL_CAPTION: // Michael Borcherds 2008-02-18
				return getCaption();
				
			default : // case LABEL_NAME:
				return label;
		}
	}
	
	/**
	 * Returns toValueString() if isDefined() ist true, else
	 * the translation of "undefined" is returne
	 */
	final public String toDefinedValueString() {
		if (isDefined())
			return toValueString();
		else
			return app.getPlain("undefined");
	}

	/**
		* Returns algebraic representation of this GeoElement as Text. If this
		* is not possible (because there are indices in the representation)
		* a HTML string is returned.		
		*/
	final public String getAlgebraDescriptionTextOrHTML() {
		if (strAlgebraDescTextOrHTMLneedsUpdate) {
			String algDesc = getAlgebraDescription();
			// convertion to html is only needed if indices are found
			if (hasIndexLabel()) {
				strAlgebraDescTextOrHTML =
					indicesToHTML(algDesc, true);
			} else {
				strAlgebraDescTextOrHTML = algDesc;
			}
			
			strAlgebraDescTextOrHTMLneedsUpdate = false;						
		}
		
		return strAlgebraDescTextOrHTML;
	}

	final public String getAlgebraDescriptionHTML(boolean addHTMLtag) {
		if (strAlgebraDescriptionHTMLneedsUpdate) {
			strAlgebraDescriptionHTML = indicesToHTML(getAlgebraDescription(), false);
			
			strAlgebraDescriptionHTMLneedsUpdate = false;
		}
		
		return strAlgebraDescriptionHTML;		
	}
	
	/**
	* returns type and label of a GeoElement 
	* (for tooltips and error messages)		
	*/
	final public String getLabelTextOrHTML() {
		if (strLabelTextOrHTMLUpdate) {
			if (hasIndexLabel())
				strLabelTextOrHTML = indicesToHTML(getLabel(), true);
			else
				strLabelTextOrHTML = getLabel();
		} 
		
		return strLabelTextOrHTML;
	}
	
	/**
	 * Returns algebraic representation of this GeoElement.		
	 */
	final public String getAlgebraDescription() {
		if (strAlgebraDescriptionNeedsUpdate) {
			if (isDefined()) {
				strAlgebraDescription = toString();
			} else {				
				sbAlgebraDesc.setLength(0);			
				sbAlgebraDesc.append(label);
				sbAlgebraDesc.append(' ');
				sbAlgebraDesc.append(app.getPlain("undefined"));
				strAlgebraDescription = sbAlgebraDesc.toString();
			}		
			
			strAlgebraDescriptionNeedsUpdate = false;
		}
		
		return strAlgebraDescription;
	}	
	private StringBuffer sbAlgebraDesc = new StringBuffer();
	
	final public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {			
			if (isDefined()) {
				strLaTeX = toLaTeXString(false);
			} else {				
				strLaTeX = getAlgebraDescription();
			}								
		}
		
		return strLaTeX;		
	}
	
	/*
	final public Image getAlgebraImage(Image tempImage) {		
		Graphics2D g2 = (Graphics2D) g;
		GraphicsConfiguration gc = app.getGraphicsConfiguration();
		if (gc != null) {
			bgImage = gc.createCompatibleImage(width, height);
		Point p = drawIndexedString(g2, labelDesc, xLabel, yLabel);
			
		setSize(fontSize, p.x, fontSize + p.y);						
	}
	*/

	/*
	 * replaces all indices (_ and _{}) in str by <sub> tags, all and converts all
	 * special characters in str to HTML
	 * examples:
	 * "a_1" becomes "a<sub>1</sub>"
	 * "s_{AB}" becomes "s<sub>AB</sub>"
	 */
	private static String subBegin = "<sub><font size=\"-1\">";
	private static String subEnd = "</font></sub>";
	public static String indicesToHTML(String str, boolean addHTMLtag) {
		if (str == null) return "";
		
		sbIndicesToHTML.setLength(0);
		if (addHTMLtag)
			sbIndicesToHTML.append("<html>");

		int depth = 0;
		int startPos = 0;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
				case '_' :
					//	write everything before _
					if (i > startPos) {
						sbIndicesToHTML.append(
							Util.toHTMLString(str.substring(startPos, i)));
					}
					startPos = i + 1;
					depth++;

					// check if next character is a '{' (beginning of index with several chars)
					if (startPos < length && str.charAt(startPos) != '{') {
						sbIndicesToHTML.append(subBegin);
						sbIndicesToHTML.append(
							Util.toHTMLString(
								str.substring(startPos, startPos + 1)));
						sbIndicesToHTML.append(subEnd);
						depth--;
					} else {
						sbIndicesToHTML.append(subBegin);
					}
					i++;
					startPos++;
					break;

				case '}' :
					if (depth > 0) {
						if (i > startPos) {
							sbIndicesToHTML.append(
								Util.toHTMLString(str.substring(startPos, i)));
						}
						sbIndicesToHTML.append(subEnd);
						startPos = i + 1;
						depth--;
					}
					break;
			}
		}

		if (startPos < length) {
			sbIndicesToHTML.append(Util.toHTMLString(str.substring(startPos)));
		}
		if (addHTMLtag)
			sbIndicesToHTML.append("</html>");
		return sbIndicesToHTML.toString();
	}
	private static StringBuffer sbIndicesToHTML = new StringBuffer();

	/**
		* returns type and label of a GeoElement 
		* (for tooltips and error messages)		
		*/
	public String getNameDescription() {
		sbNameDescription.setLength(0);
		
		String label = getLabel();
		String typeString = translatedTypeString();
						
		if (app.isReverseNameDescriptionLanguage()) {
			//	reverse order: "A point"
			sbNameDescription.append(label);				
			sbNameDescription.append(' ');			
			sbNameDescription.append(typeString);			
		}	
		else {
			// standard order: "point A"
			sbNameDescription.append(typeString);				
			sbNameDescription.append(' ');
			sbNameDescription.append(label);
		}
				
		return sbNameDescription.toString();
	}
	private StringBuffer sbNameDescription = new StringBuffer();

	/**
		* returns type and label of a GeoElement 
		* (for tooltips and error messages)		
		*/
	final public String getNameDescriptionTextOrHTML() {
		if (hasIndexLabel())
			return getNameDescriptionHTML(false, true);
		else
			return getNameDescription();
	}

	/**
	 * Returns whether the str contains any indices (i.e. '_' chars). 
	 */
	final public boolean hasIndexLabel() {
		if (strHasIndexLabel != label) {
			hasIndexLabel = (label == null || label.indexOf('_') > -1);
			strHasIndexLabel = label;
		}
		
		return hasIndexLabel;
	}
	private String strHasIndexLabel;
	private boolean hasIndexLabel = false;
	

	/**
		* returns type and label of a GeoElement as html string
		* (for tooltips and error messages)		
		*/
	public String getNameDescriptionHTML(
		boolean colored,
		boolean addHTMLtag) {
		sbNameDescriptionHTML.setLength(0);
		if (addHTMLtag)
			sbNameDescriptionHTML.append("<html>");
		
		String label = getLabel();
		String typeString = translatedTypeString();					
			
		boolean reverseOrder = app.isReverseNameDescriptionLanguage();		
		if (!reverseOrder) {
			//	standard order: "point A"
			sbNameDescriptionHTML.append(typeString);				
			sbNameDescriptionHTML.append(' ');
		}						
		
		if (colored) {
			sbNameDescriptionHTML.append(" <b><font color=\"#");
			sbNameDescriptionHTML.append(Util.toHexString(getAlgebraColor()));
			sbNameDescriptionHTML.append("\">");
		}
		sbNameDescriptionHTML.append(indicesToHTML(label, false));
		if (colored)
			sbNameDescriptionHTML.append("</font></b>");
		
		if (reverseOrder) {
			//	reverse order: "A point"
			sbNameDescriptionHTML.append(' ');
			sbNameDescriptionHTML.append(typeString);							
		}
		
		if (addHTMLtag)
			sbNameDescriptionHTML.append("</html>");							
		return sbNameDescriptionHTML.toString();
	}
	private StringBuffer sbNameDescriptionHTML = new StringBuffer();

	/*******************************************************
	 * SAVING
	 *******************************************************/

	final public String getXMLtypeString() {		
		return getClassName().substring(3).toLowerCase(Locale.US);
	}
	
	/**
	 * save object in xml format
	 * GeoGebra File Format
	 */
	public String getXML() {
		String type = getXMLtypeString();
		
		StringBuffer sb = new StringBuffer();
		sb.append("<element");
		sb.append(" type=\"");
		sb.append(type);
		sb.append("\" label=\"");
		sb.append(Util.encodeXML(label));		
		sb.append("\">\n");
		sb.append(getXMLtags());
		
		// caption text
		if (caption != null && caption.length() > 0 && !caption.equals(label)) {
			sb.append("\t<caption val=\"");
			sb.append(Util.encodeXML(caption));
			sb.append("\"/>\n");
		}
		
		sb.append("</element>\n");
				
		return sb.toString();
	}
	
	/**
	 * save object in i2g format
	 * Intergeo File Format (Yves Kreis)
	 */
	public String getI2G(int mode) {
		String type = getXMLtypeString();
		
		StringBuffer sb = new StringBuffer();
		
		if (mode == CONSTRAINTS) {
			if (isIndependent() || isPointOnPath()) {
				sb.append("\t\t<free_");
				sb.append(type);
				sb.append(">\n");

				sb.append("\t\t\t<");
				sb.append(type);
				sb.append(" out=\"true\">");
				sb.append(Util.encodeXML(label));		
				sb.append("</");
				sb.append(type);
				sb.append(">\n");

				sb.append("\t\t</free_");
				sb.append(type);
				sb.append(">\n");
			}
		} else {
			sb.append("\t\t<");
			sb.append(type);
			sb.append(" id=\"");
			sb.append(Util.encodeXML(label));		
			sb.append("\">\n");
			
			if (mode == ELEMENTS) {
				sb.append(getI2Gtags());
			} else if (mode == DISPLAY) {
				// caption text
				if (caption != null && caption.length() > 0 && !caption.equals(label)) {
					sb.append("\t\t\t<label>");
					sb.append(Util.encodeXML(caption));
					sb.append("</label>\n");
				} else {
					return "";
				}
			}

			sb.append("\t\t</");
			sb.append(type);
			sb.append(">\n");
		}
		
		return sb.toString();
	}
	
    final String getAuxiliaryXML() {
		if (auxiliaryObject) {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<auxiliary val=\"");
			sb.append(auxiliaryObject);
			sb.append("\"/>\n");
			return sb.toString();
		} else 
			return "";		
	}

	/**
	 * returns all visual xml tags (like show, objColor, labelOffset, ...)
	 */
	String getXMLvisualTags() {
		return getXMLvisualTags(true);
	}
		
	String getXMLvisualTags(boolean withLabelOffset) {
		StringBuffer sb = new StringBuffer();		
		
		// show object and label  
		sb.append("\t<show");
		sb.append(" object=\"");
		sb.append(euclidianVisible);
		sb.append("\"");
		sb.append(" label=\"");
		sb.append(labelVisible);
		sb.append("\"");
		sb.append("/>\n");
		
		sb.append(getShowObjectConditionXML());
		
		sb.append("\t<objColor");
		sb.append(" r=\"");
		sb.append(objColor.getRed());
		sb.append("\"");
		sb.append(" g=\"");
		sb.append(objColor.getGreen());
		sb.append("\"");
		sb.append(" b=\"");
		sb.append(objColor.getBlue());
		sb.append("\"");
		sb.append(" alpha=\"");
		sb.append(alphaValue);
		sb.append("\"");
		
		if (colFunction!=null)
		{
			sb.append(" dynamicr=\"");
			sb.append(colFunction.get(0).getLabelOrCommandDescription());
			sb.append("\"");
			sb.append(" dynamicg=\"");
			sb.append(colFunction.get(1).getLabelOrCommandDescription());
			sb.append("\"");
			sb.append(" dynamicb=\"");
			sb.append(colFunction.get(2).getLabelOrCommandDescription());
			sb.append("\"");
			
		}
		sb.append("/>\n");

		
		if (this.isGeoPoint())
			{
			GeoPoint p = (GeoPoint)(this);
			GeoList coordinateFunction = p.getCoordinateFunction();
			if (coordinateFunction != null) {
				sb.append("\t<objCoords");
				sb.append(" dynamicx=\"");
				sb.append(coordinateFunction.get(0).getLabelOrCommandDescription());
				sb.append("\"");
				sb.append(" dynamicy=\"");
				sb.append(coordinateFunction.get(1).getLabelOrCommandDescription());
				sb.append("\"");
				sb.append("/>\n");

			}
		}
		
		
		// layer
		// Michael Borcherds 2008-02-26
		sb.append("\t<layer ");
		sb.append("val=\""+layer+"\"");
		sb.append("/>\n");
		
		if (withLabelOffset &&
			(labelOffsetX != 0 || labelOffsetY != 0)) {
			sb.append("\t<labelOffset");
			sb.append(" x=\"");
			sb.append(labelOffsetX);
			sb.append("\"");
			sb.append(" y=\"");
			sb.append(labelOffsetY);
			sb.append("\"");
			sb.append("/>\n");
		}
		
		sb.append("\t<labelMode");
		sb.append(" val=\"");
		sb.append(labelMode);
		sb.append("\"");
		sb.append("/>\n");

		// trace on or off
		if (isTraceable()) {
			Traceable t = (Traceable) this;
			if (t.getTrace()) {
				sb.append("\t<trace val=\"true\"/>\n");
			}
		}
		
		// trace to spreadsheet on or off
		if (isGeoPoint()) {
			GeoPoint p = (GeoPoint) this;
			if (p.getSpreadsheetTrace()) {
				sb.append("\t<spreadsheetTrace val=\"true\"/>\n");
			}
		}
		
		// decoration type
		if (decorationType != DECORATION_NONE) {
			sb.append("\t<decoration");		
			sb.append(" type=\"");
			sb.append(decorationType);
			sb.append("\"/>\n");
		}
		
		return sb.toString();	
	}

	String getXMLanimationTags() {
		// animation step width
		if (isChangeable()) {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<animation");
			sb.append(" step=\""+animationIncrement+"\"");
			sb.append(" speed=\""+animationSpeed+"\"");
			sb.append(" type=\""+animationType+"\"");
			sb.append(" playing=\"");
			sb.append((isAnimating() ? "true" : "false"));
			sb.append("\"");
			sb.append("/>\n");
			return sb.toString();
		}
		return "";
	}

	String getXMLfixedTag() {
		//		is object fixed
		if (isFixable()) {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<fixed val=\"");
			sb.append(fixed);
			sb.append("\"/>\n");
			return sb.toString();
		}
		return "";
	}

	/**
	 * returns all class-specific xml tags for getXML
	 * GeoGebra File Format
	 */
	protected String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		sb.append(getLineStyleXML());
		sb.append(getXMLvisualTags());
		sb.append(getXMLanimationTags());
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		sb.append(getBreakpointXML());		
		return sb.toString();
	}

	/**
	 * returns all class-specific i2g tags for getI2G
	 * Intergeo File Format (Yves Kreis)
	 */
	protected String getI2Gtags() {
		return "";
	}

	/**
	 * Returns line type and line thickness as xml string.
	 * @see getXMLtags() of GeoConic, GeoLine and GeoVector      
	 */
	String getLineStyleXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t<lineStyle");
		sb.append(" thickness=\"");
		sb.append(lineThickness);
		sb.append("\"");
		sb.append(" type=\"");
		sb.append(lineType);
		sb.append("\"");
		sb.append("/>\n");
		return sb.toString();
	}	

	/**
	 * Returns line type and line thickness as xml string.
	 * @see getXMLtags() of GeoConic, GeoLine and GeoVector      
	 */
	String getBreakpointXML() {		
		StringBuffer sb = new StringBuffer();		
		sb.append("\t<breakpoint val=\"");		
		sb.append(isConsProtBreakpoint);
		sb.append("\"/>\n");
		return sb.toString();				
	}
	
	private String getShowObjectConditionXML() {
		if (condShowObject != null) {
			StringBuffer sb = new StringBuffer();		
			sb.append("\t<condition showObject=\"");		
			sb.append(Util.encodeXML(condShowObject.getLabelOrCommandDescription()));
			sb.append("\"/>\n");
			return sb.toString();
		}
		return "";
	}

	/**
	 * @return
	 */
	public int getLineThickness() {
		return lineThickness;
	}

	/**
	 * @return
	 */
	public int getLineType() {
		return lineType;
	}

	/**
	 * @param f
	 */
	public void setLineThickness(int th) {
		lineThickness = th;
	}

	/**
	 * @param i
	 */
	public void setLineType(int i) {
		lineType = i;
	}
	
	public void setDecorationType(int type) {
		decorationType = type;
	}
	
	
	/*
	 *  NOTE: change in GeoElementWrapper too!
	 */
	
  	public boolean isGeoElement3D() {
		return false;
	}
	
	
	public boolean isGeoAngle() {
		return false;
	}
	
	public boolean isGeoBoolean() {
		return false;
	}
	
	public boolean isGeoConic() {
		return false;
	}
	
	public boolean isGeoConicPart() {
		return false;
	}
		
	public boolean isGeoFunction() {
		return false;
	}
	
	public boolean isGeoFunctionConditional() {		
		return false;
	}
	
	public boolean isGeoFunctionable() {
		return false;
	}		
	
	public boolean isGeoImage() {
		return false;
	}
	
	public boolean isGeoLine() {
		return false;
	}
	
	public boolean isGeoLocus() {
		return false;
	}
	
	public boolean isGeoNumeric() {
		return false;
	}
	
	public boolean isGeoPoint() {
		return false;
	}
	
	public boolean isGeoPoint3D() {
		return false;
	}
	
	public boolean isGeoPolygon() {
		return false;
	}
	
	public boolean isGeoRay() {
		return false;
	}
	
	public boolean isGeoSegment() {
		return false;
	}
	
	public boolean isGeoText() {
		return false;
	}
	
	public boolean isGeoVector() {
		return false;
	}
	
	public boolean isGeoCurveCartesian() {
		return false;
	}
	
	public boolean isGeoCurveable() {
		return false;
	}	
	
	public boolean isGeoDeriveable() {
		return false;
	}
	
	final public boolean isExpressionNode() {
		return false;
	}

	final public boolean isVariable() {
		return false;
	}
	
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}
	
	/* ** hightlighting and selecting 
	 * only for internal purpouses, i.e. this is not saved */
	
	final public void setSelected(boolean flag) {
		selected = flag;
	}
	
	final public void setHighlighted(boolean flag) {
		highlighted = flag;
	}
	
	final public boolean doHighlighting() {
		return highlighted || selected;
	}
	
	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}
	
	public boolean isBooleanValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {		
		return false;
	}
	
	public boolean isListValue() {
		return false;
	}

	public boolean isUseVisualDefaults() {
		return useVisualDefaults;
	}

	public void setUseVisualDefaults(boolean useVisualDefaults) {
		this.useVisualDefaults = useVisualDefaults;
	}
   	
	public boolean isAbsoluteScreenLocateable() {
		return false;
	}

	public final GeoBoolean getShowObjectCondition() {
		return condShowObject;
	}

	public void setShowObjectCondition(GeoBoolean cond) 
	throws CircularDefinitionException {
		// check for circular definition
		if (this == cond || isParentOf(cond))
			throw new CircularDefinitionException();	
		
		// unregister old condition
		if (condShowObject != null) {
			condShowObject.unregisterConditionListener(this);
		}
		
		// set new condition
		condShowObject = cond;
		
		// register new condition
		if (condShowObject != null) {
			condShowObject.registerConditionListener(this);
		}		
	}
	
	public final void removeCondition(GeoBoolean bool) {
		if (condShowObject == bool)
			condShowObject = null;
	}
	
	public final GeoList getColorFunction() {		
		return colFunction;
	}

	public void setColorFunction(GeoList col) 
	//throws CircularDefinitionException 
	{
		//Application.debug("setColorFunction"+col.getValue());
		
		// check for circular definition (not needed)
		//if (this == col || isParentOf(col))
		//	throw new CircularDefinitionException();	
		
		// unregister old condition
		if (colFunction != null) {
			colFunction.unregisterConditionListener(this);
		}
		
		// set new condition
		colFunction = col;
		
		// register new condition
		if (colFunction != null) {
			colFunction.registerConditionListener(this);
		}		
	}
	
	public final void removeColorFunction() {
		//Application.debug("removeColorFunction");
		//if (colFunction == col)
			colFunction = null;
	}
	
	
	/**
	 * Translates all GeoElement objects in geos by a vector in real world coordinates or by
	 * (xPixel, yPixel) in screen coordinates. 
	 * @param endPosition may be null
	 */
	public static boolean moveObjects(ArrayList geos, GeoVector rwTransVec, Point2D.Double endPosition) {	
		if (moveObjectsUpdateList == null)
			moveObjectsUpdateList = new ArrayList();
		
		boolean moved = false;
		int size = geos.size();
		moveObjectsUpdateList.clear();
		moveObjectsUpdateList.ensureCapacity(size);
		
		// only use end position for a single point
		Point2D.Double position = size == 1 ? endPosition : null;
		
		for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement) geos.get(i);
			
			moved = geo.moveObject(rwTransVec, position, moveObjectsUpdateList) || moved;		
		}					
				
		// take all independent input objects and build a common updateSet
		// then update all their algos.
		// (don't do updateCascade() on them individually as this could cause 
		//  multiple updates of the same algorithm)
		updateCascade(moveObjectsUpdateList);
		
		return moved;
	}
	private static ArrayList moveObjectsUpdateList;
	
//	/**
//	 * Moves geo by a vector in real world coordinates.
//	 * @return whether actual moving occurred 	 
//	 */
//	final public boolean moveObject(GeoVector rwTransVec, Point2D.Double endPosition) {
//		return moveObject(rwTransVec, endPosition, null);
//	}

	/**
	 * Moves geo by a vector in real world coordinates.
	 * @return whether actual moving occurred 	 
	 */
	private boolean moveObject(GeoVector rwTransVec, Point2D.Double endPosition, ArrayList updateGeos) {
		boolean movedGeo = false;
		
		// moveable geo
		if (isMoveable()) {
			// point
			if (isGeoPoint()) {
				GeoPoint point = (GeoPoint) this;
				if (endPosition != null) {					
					point.setCoords(endPosition.x, endPosition.y, 1);
					movedGeo = true;
				} 
				
				// translate point
				else {	
					double x  = point.inhomX + rwTransVec.x;
					double y =  point.inhomY + rwTransVec.y;
										
					// round to decimal fraction, e.g. 2.800000000001 to 2.8
					if (Math.abs(rwTransVec.x) > Kernel.MIN_PRECISION)
						x  = kernel.checkDecimalFraction(x);
					if (Math.abs(rwTransVec.y) > Kernel.MIN_PRECISION) 
						y = kernel.checkDecimalFraction(y);
						
					// set translated point coords
					point.setCoords(x, y, 1);					
					movedGeo = true;
				}
			}
			
			// translateable
			else if (isTranslateable()) {
				Translateable trans = (Translateable) this;
				trans.translate(rwTransVec);			
				movedGeo = true;
			}
			
			// absolute position on screen
			else if (isAbsoluteScreenLocateable()) {
				AbsoluteScreenLocateable screenLoc = (AbsoluteScreenLocateable) this;
				if (screenLoc.isAbsoluteScreenLocActive()) {					
					int vxPixel = (int) Math.round(kernel.getXscale() * rwTransVec.x);
					int vyPixel = -(int) Math.round(kernel.getYscale() * rwTransVec.y);
					int x = screenLoc.getAbsoluteScreenLocX() + vxPixel;
					int y = screenLoc.getAbsoluteScreenLocY() + vyPixel;
					screenLoc.setAbsoluteScreenLoc(x, y);
					movedGeo = true;
				} 					
				else if (isGeoText()) {
					// check for GeoText with unlabeled start point
					GeoText movedGeoText = (GeoText) this;
					if (movedGeoText.hasAbsoluteLocation()) {
						//	absolute location: change location
						GeoPoint loc = movedGeoText.getStartPoint();
						loc.translate(rwTransVec);
						movedGeo = true;
					}						
				}
			}		
			
			if (movedGeo) {
				if (updateGeos != null)
					updateGeos.add(this);
				else
					updateCascade();
			}			
		}			
		
		// non-moveable geo
		else {
			// point with changeable parent coordinates
			if (isGeoPoint()) {
				GeoPoint point = (GeoPoint) this;
				if (point.hasChangeableCoordParentNumbers()) {
					// translate x and y coordinates by changing the parent coords accordingly
					ArrayList changeableCoordNumbers = point.getCoordParentNumbers();					
					GeoNumeric xvar = (GeoNumeric) changeableCoordNumbers.get(0);
					GeoNumeric yvar = (GeoNumeric) changeableCoordNumbers.get(1);
							
					// polar coords (r; phi)
					if (point.hasPolarParentNumbers()) {
						// radius
						double radius = GeoVec2D.length(endPosition.x, endPosition.y);
						xvar.setValue(radius);
						
						// angle
						double angle = kernel.convertToAngleValue(Math.atan2(endPosition.y, endPosition.x));
						// angle outsid of slider range
						if (yvar.isIntervalMinActive() && yvar.isIntervalMaxActive() &&
						    (angle < yvar.getIntervalMin() || angle > yvar.getIntervalMax())) 
						{
							// use angle value closest to closest border
							double minDiff = Math.abs((angle - yvar.getIntervalMin())) ;
							if (minDiff > Math.PI) minDiff = Kernel.PI_2 - minDiff;
							double maxDiff = Math.abs((angle - yvar.getIntervalMax()));
							if (maxDiff > Math.PI) maxDiff = Kernel.PI_2 - maxDiff;
							
							if (minDiff < maxDiff) 
								angle = angle - Kernel.PI_2;
							else
								angle = angle + Kernel.PI_2;
						}											
						yvar.setValue(angle);
					}
					
					// cartesian coords (xvar + constant, yvar + constant)
					else {
						xvar.setValue( xvar.getValue() - point.inhomX + endPosition.x);
						yvar.setValue( yvar.getValue() - point.inhomY + endPosition.y);
					}
					
		    		if (updateGeos != null) {
		    			// add both variables to update list
		    			updateGeos.add(xvar);
		    			updateGeos.add(yvar);
		    		} else {
		    			// update both variables right now
		    			if (tempMoveObjectList == null)
		    				tempMoveObjectList = new ArrayList();
		    			tempMoveObjectList.add(xvar);
		    			tempMoveObjectList.add(yvar);
		    			updateCascade(tempMoveObjectList);
		    		}
		    				    				    	
		    		movedGeo = true;
				}
			}			
		}
					
		return movedGeo;
	}
	private ArrayList tempMoveObjectList;

	/**
	 * Returns the position of this GeoElement in
	 * GeoGebra's spreadsheet view. 
	 * The x-coordinate of the returned point specifies its
	 * column and the y-coordinate specifies its row location.	
	 * Note that this method
	 * may return null if no position was specified so far.	 
	 */
	public Point getSpreadsheetCoords() {
		if (spreadsheetCoords == null)
			updateSpreadsheetCoordinates();
		return spreadsheetCoords;
	}

	/**
	 * Sets the position of this GeoElement in
	 * GeoGebra's spreadsheet. The x-coordinate specifies its
	 * column and the y-coordinate specifies its row location.	 
	 */
	public void setSpreadsheetCoords(Point spreadsheetCoords) {
		this.spreadsheetCoords = spreadsheetCoords;
	}

	public Point getOldSpreadsheetCoords() {
		return oldSpreadsheetCoords;
	}

	final boolean isAlgoMacroOutput() {
		return isAlgoMacroOutput;
	}

	final void setAlgoMacroOutput(boolean isAlgoMacroOutput) {
		this.isAlgoMacroOutput = isAlgoMacroOutput;
	}

	
	// Michael Borcherds 2008-04-30
	public abstract boolean isEqual(GeoElement Geo);
	
	/**
	 * String getFormulaString(int, boolean substituteNumbers)
	 * substituteNumbers determines (for a function) whether you want
	 * "2*x^2" or "a*x^2"
	 * returns a string representing the formula of the GeoElement in the following formats:
	 * getFormulaString(ExpressionNode.STRING_TYPE_YACAS) eg Sqrt(x)
	 * getFormulaString(ExpressionNode.STRING_TYPE_LATEX) eg \sqrt(x)
	 * getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA) eg sqrt(x)
	 * getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA_XML)
	 * getFormulaString(ExpressionNode.STRING_TYPE_JASYMCA)
	 */
	public String getFormulaString(int ExpressionNodeType, boolean substituteNumbers)
	{
		
		/*
		 * maybe use this
		 * doesn't work on f=Factor[x^2-1] Expand[f]
		if (ExpressionNodeType == ExpressionNode.STRING_TYPE_YACAS
				 || ExpressionNodeType == ExpressionNode.STRING_TYPE_JASYMCA) {
		
			ExpressionValue ev;
			if (!this.isExpressionNode()) 
	            ev = new ExpressionNode(kernel, this);
			else
				ev = this;
			
			String ret = ((ExpressionNode)
					ev).getCASstring(ExpressionNodeType,
					!substituteNumbers);
			Application.debug(ret);
			return ret;
		}
		*/
		
    
		int tempCASPrintForm = kernel.getCASPrintForm();
		kernel.setCASPrintForm(ExpressionNodeType);


		
		if (ExpressionNodeType == ExpressionNode.STRING_TYPE_YACAS
		 || ExpressionNodeType == ExpressionNode.STRING_TYPE_JASYMCA)
			kernel.setTemporaryPrintDecimals(16); // Yacas doesn't like 4E-20 or x^2.0

		
		String ret="";
		if (this.isGeoFunction()) {
			GeoFunction geoFun = (GeoFunction)this;
	 				   
	 		if (geoFun.isIndependent()) {
	 			ret = geoFun.toValueString();
	 		} else {
	 			ret = substituteNumbers ?
	 					geoFun.getFunction().toValueString():
	 					geoFun.getFunction().toString(); 
	 		}
		}
		else 
		{
			ret = substituteNumbers ? this.toValueString()
					: this.getCommandDescription();
		}
		
		if (ret.equals("")) {
			// eg Text[ (1,2), false]
			ret = toOutputValueString();
		}
		
		if (ExpressionNodeType == ExpressionNode.STRING_TYPE_YACAS
		 || ExpressionNodeType == ExpressionNode.STRING_TYPE_JASYMCA)
			kernel.restorePrintAccuracy();
		
		kernel.setCASPrintForm(tempCASPrintForm);
		return ret;
	}

	private int traceColumn1 = -1;
	private double lastTrace1 = Math.random();
	private double lastTrace2 = Math.random();
	
	public void resetTraceColumns() {
		traceColumn1 = -1;
	}
	
	public String getTraceColumn1() {
		if (app.isUsingLayout() && app.getGuiManager().showSpreadsheetView() && traceColumn1 == -1) {
			traceColumn1 = app.getGuiManager().getHighestUsedSpreadsheetColumn() + 1;
		}
		return GeoElement.getSpreadsheetColumnName(traceColumn1);
	}
		
	public String getTraceColumn2() {
		if (app.isUsingLayout() && app.getGuiManager().showSpreadsheetView() && traceColumn1 == -1) {
			traceColumn1 = app.getGuiManager().getHighestUsedSpreadsheetColumn() + 1;
		}
		return GeoElement.getSpreadsheetColumnName(traceColumn1 + 1);
	}
	
	public int getTraceRow() {
		if (traceColumn1 == -1) return -1;
		
		if (!(app.isUsingLayout() && app.getGuiManager().showSpreadsheetView())) return -1;
		
		return app.getGuiManager().getSpreadsheetTraceRow(traceColumn1);
	}
		
	public double getLastTrace1() {
		return lastTrace1;
	}

	public double getLastTrace2() {
		return lastTrace2;
	}
	
	public void setLastTrace1(double val) {
		lastTrace1 = val;
	}

	public void setLastTrace2(double val) {
		lastTrace2 = val;
	}
	
	/*
	 * over-ridden in GeoList
	 */
	public GeoElement getGeoElementForPropertiesDialog() {
		return this;
	}
	
	/*
	 * over-ridden in GeoText
	 */
	public boolean isTextCommand() {
		return false;
	}
	
	boolean inTree = false;
	
	final public boolean isInTree() {
		return inTree;
	}
	
	final public void setInTree(boolean flag) {
		inTree = flag;
	}


}
/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra.kernel;

import geogebra.MyError;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.util.Util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

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
			'Y',
			'Z' };	
	
	private static final char[] functionLabels =
	{		
		'f',
		'g',
		'h'
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

	public static final int LABEL_NAME = 0;
	public static final int LABEL_NAME_VALUE = 1;
	public static final int LABEL_VALUE = 2;

	protected String label; // should only be used directly in subclasses
	boolean labelWanted = false, labelSet = false, localVarLabelSet = false;
	private boolean euclidianVisible = true;
	private boolean algebraVisible = true;
	private boolean labelVisible = true;
	private boolean isConsProtBreakpoint; // in construction protocol
	private boolean fixed = false;
	private int labelMode = LABEL_NAME;
	protected int toStringMode = Kernel.COORD_CARTESIAN; // cartesian or polar	  
	public Color objColor, selColor, labelColor, fillColor;
	public double animationStep = 0.1;
	public float alphaValue = 0.0f;
	public int labelOffsetX = 0, labelOffsetY = 0;
	private boolean auxiliaryObject = false;
	// on change: see setVisualValues()

	private boolean isColorSet = false;
	private boolean highlighted = false;
	private boolean selected = false;
	private String strAlgebraDescription, strAlgebraDescTextOrHTML;
	private StringBuffer sb = new StringBuffer();

	// line thickness and line type: s	
	// note: line thickness in Drawable is calculated as lineThickness / 2.0f
	public int lineThickness = EuclidianView.DEFAULT_LINE_THICKNESS;
	public int lineType = EuclidianView.DEFAULT_LINE_TYPE;		
	
	// decoration types
	public int decorationType;
	
	public static final int DECORATION_NONE = 0;
	// segment decorations
	public static final int DECORATION_SEGMENT_ONE_TICK = 1;
	public static final int DECORATION_SEGMENT_TWO_TICKS = 2;
	public static final int DECORATION_SEGMENT_THREE_TICKS = 3;
	// angle decorations
	public static final int DECORATION_ANGLE_TWO_ARCS = 1;
	public static final int DECORATION_ANGLE_THREE_ARCS = 2;
	public static final int DECORATION_ANGLE_TICK_TWO_ARCS = 4;
	public static final int DECORATION_ANGLE_TICK_THREE_ARCS = 5;			
	
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
		setDefaultColors(); // init colors       
		
		// init label
		initSetLabelVisible();
		
		// new elements become breakpoints if only breakpoints are shown
		isConsProtBreakpoint = cons.showOnlyBreakpoints();
	}

	/* *******************************************************/	
	
	/**
	 * Returns label of GeoElement. If the label is null then 
	 * algoParent.getCommandDescription() or  toValueString() is returned.     
	 */
	public String getLabel() {			
		if (!(labelSet || localVarLabelSet)) {
			if (algoParent == null)
				return toValueString();
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

			default :
				labelMode = LABEL_NAME;
		}
	}

	public int getLabelMode() {
		return labelMode;
	}

	/** 
	 * every subclass implements it's own copy method 
	 *  this is needed for assignment copies like:
	 *  a = 2.7
	 *  b = a   (here copy() is needed)
	 * */
	public abstract GeoElement copy();
	
	/** 
	 * every subclass implements it's own copyInternal method 
	 * This method will always return a GeoElement of the
	 * SAME CLASS as this GeoElement
	 */
	public GeoElement copyInternal() {
		// default implementation: changed in some subclasses
		return copy();
	}

	/** 
	 * every subclass implements it's own set internal method 
	 * This method will only work for GeoElements of the 
	 * SAME CLASS as this GeoElement
	 */
	public void setInternal(GeoElement geo) {
		set(geo);
	}
	
	public ExpressionValue deepCopy() {
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

	
	void initSetLabelVisible() {
		labelVisible =  ! isPath() || app.showAlgebraView();
	}
	
	public void setDefaultColors() {
		Color col;
		if (isIndependent())
			col = EuclidianView.getDefaultColor(this);
		else
			col = EuclidianView.getDependentColor(this);
		
		boolean oldColorSetVal = isColorSet;
		setObjColor(col);
		isColorSet = oldColorSetVal;
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

	public Color getColor() {
		return objColor;
	}

	public void setAlphaValue(float alpha) {
		if (alpha < 0.0f || alpha > 1.0f)
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
		
		try {
			((Traceable) this).setTrace(((Traceable) geo).getTrace());
		} catch (Exception e) {}		
	}
	
	public void setVisualStyle(GeoElement geo) {
		// label style
		labelVisible = geo.labelVisible;
		labelMode = geo.labelMode;
		
		// style of equation, coordinates, ...
		if (getClass().isInstance(geo))
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
		return euclidianVisible && showInEuclidianView();
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
		isConsProtBreakpoint = flag;
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
		if (isFixable())
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

	public boolean isLabelVisible() {
		return labelVisible;
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

	abstract boolean showInAlgebraView();
	abstract boolean showInEuclidianView();

	public void setParentAlgorithm(AlgoElement algorithm) {
		algoParent = algorithm;
		if (algorithm != null)
			setDefaultColors(); // set colors to dependent colors
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
	 * Note: this is needed for points on lines
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
		return isChangeable() ;
	}
	
	/**
	 * Returns whether this GeoElement's parents can be 
	 * moved in Euclidian View.
	 */
	public boolean hasMoveableParents() {		
		AlgoElement algo = getParentAlgorithm();
		boolean ret = false;
		if (algo != null) {
			for (int i=0; i < algo.input.length; i++) {
				GeoElement parent = algo.input[i];
				ret = parent.isEuclidianVisible() &&
							parent.isMoveable() && parent.isTranslateable();
				if (!ret) break;
			}
		}
		return ret;
	}
	
	/**
	 * Checks if all parents of this object are Translateables and
	 * returns them.
	 */
	public Translateable [] getTranslateableParents() {
		AlgoElement algo = getParentAlgorithm();		
		if (algo == null) return null;
		
		Translateable [] ret = new Translateable[algo.input.length];
		for (int i=0; i < algo.input.length; i++) {
			GeoElement parent = algo.input[i];
			if (parent.isTranslateable())
				ret[i] = (Translateable) parent;
			else
				return null;				
		}
		return ret;
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
			animationStep = s;
	}

	public double getAnimationStep() {
		return animationStep;
	}
	
    public String toLaTeXString(boolean symbolic) {
    	if (symbolic)
    		return toString();
    	else
    		return toDefinedValueString();	
    }     

	/********************************************************
	 * GeoElementTable Management
	 * Hashtable: String (label) -> GeoElement 
	 ********************************************************/

	/**
	 * renames this GeoElement to newLabel.
	 * @param newLabel
	 * @return true if label was changed
	 * @throws MyError: if new label is already in use
	 */
	public boolean rename(String newLabel) {
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
		if (cons.isInMacroMode())
			return;
		
		labelWanted = true;

		// had no label: try to set it
		if (!labelSet) {
			// to avoid wasting of lables, new elements must wait
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
		
		this.label = label; // set new label
		labelSet = true;
		labelWanted = false; // got a label, no longer wanted					
		
		cons.putLabel(this); // add new table entry	   		
		setAlgebraDescription();
		notifyAdd();
	}

	private void doRenameLabel(String newLabel) {
		//	UPDATE KERNEL		
		cons.removeLabel(this); // remove old table entry
		label = newLabel; // set new label
		cons.putLabel(this); // add new table entry    
		setAlgebraDescription();
		kernel.notifyRename(this); // tell views   
		
		update();		
	}

	/**
	 *  set labels for array of GeoElements with given label prefix.
	 * e.g. labelPrefix = "F", geos.length = 2 sets geo[0].setLabel("F_1")
	 * and geo[0].setLabel("F_2")
	 */
	static void setLabels(String labelPrefix, GeoElement[] geos) {
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

			default : // more than one visible geo: use indices if we got a prefix
				for (int i = 0; i < geos.length; i++)
					geos[i].setLabel(geos[i].getIndexLabel(labelPrefix));							
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
				str = "poly" + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		}		
		else if (isGeoText()) {
			int counter = 0;
			String str;
			do {
				counter++;
				str = "T" + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		} else if (isGeoImage()) {
			int counter = 0;			
			String str;
			do {
				counter++;
				str = "pic" + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		} else if (isGeoLocus()) {
			int counter = 0;			
			String str;
			do {
				counter++;
				str = "loc" + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		} else if (isGeoList()) {
			int counter = 0;			
			String str;
			do {
				counter++;
				str = "L" + counter;
			} while (!cons.isFreeLabel(str));
			return str;
		}
		else {
			chars = lowerCaseLabels;
		}

		int counter = 0, q, r;
		StringBuffer label = new StringBuffer();
		label.append(chars[0]);
		while (!cons.isFreeLabel(label.toString())) {
			label.setLength(0);
			q = counter / chars.length; // quotient
			r = counter % chars.length; // remainder                    
			label.append(chars[r]);
			if (q > 0) {
				// q as index
				if (q < 10) {
					label.append('_');
					label.append(q);
				} else {
					label.append("_{");
					label.append(q);
					label.append('}');
				}
			}
			counter++;					
		}
		return label.toString();
	}
	
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
		
		StringBuffer label = new StringBuffer();
		int n = 0; // index
		do {
			label.setLength(0);
			label.append(pref);
			// n as index
			n++;
			if (n < 10) {
				label.append('_');
				label.append(n);
			} else {
				label.append("_{");
				label.append(n);
				label.append('}');
			}
		} while (!cons.isFreeLabel(label.toString()));
		return label.toString();
	}

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
	void doRemove() {
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
		if (isLabelSet())
			cons.removeLabel(this);

		// remove from selection
		app.removeSelectedGeo(this, false);
		
		labelSet = false;
		labelWanted = false;
		notifyRemove();
	}

	final public void notifyAdd() {
		kernel.notifyAdd(this);

		//		System.out.println("add " + label);
		// printUpdateSets();
	}

	final public void notifyRemove() {
		kernel.notifyRemove(this);

		//	System.out.println("remove " + label);
		//	printUpdateSets();
	}
	
	final public void notifyUpdate() {
		kernel.notifyUpdate(this);

		//	System.out.println("update " + label);
		//	printUpdateSets();
	}
	
	final public void notifyUpdateAuxiliaryObject() {
		kernel.notifyUpdateAuxiliaryObject(this);

		//		System.out.println("add " + label);
		//	printUpdateSets();
	}


/*
	private void printUpdateSets() {		
		Iterator itList = cons.getAllGeoElementsIterator();
		while (itList.hasNext()) {
			GeoElement geo = (GeoElement) itList.next();		
			System.out.println(geo.label + ": " + geo.algoUpdateSet.toString());			
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
	final void addAlgorithm(AlgoElement algorithm) {
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

	AlgorithmSet getAlgoUpdateSet() {
		return algoUpdateSet;
	}
	
	boolean hasEmptyAlgoUpdateSet() {
		return algoUpdateSet.isEmpty();
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
		//	System.out.println(label + " remove from updateSet: " + algorithm.getCommandDescription());
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

		// update algebra description
		setAlgebraDescription();
		kernel.notifyUpdate(this);	        			   	
	}
	
	/**
	 * Updates this object and all dependent ones. Note: no repainting is done afterwards! 
	 */
	final public void updateCascade() {
		update();

		// update all algorithms in the algorithm set of this GeoElement        
		algoUpdateSet.updateAll();		
	}

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
	 * Returns all children (of type GeoElement) that depend on this object.
	 *
	public Set getAllChildren() {
		HashSet set = new HashSet(3 * algoUpdateSet.getSize());
		Iterator it = algoUpdateSet.getIterator();
		while (it.hasNext()) {
			AlgoElement algo = (AlgoElement) it.next();
			for (int i = 0; i < algo.output.length; i++) {
				// this is wrong, isn't it?     set.add(algo.output[i]);					
			}			
		}
		return set;
	}*/

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
	final void addPredecessorsToSet(TreeSet set, boolean onlyIndependent) {
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
		}
		return false;
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
	 * Returns the smallest possible construction index for this object in it's construction.
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
	abstract String getTypeString();
	/*{
		// e.g. GeoPoint -> type = Point
		//return getClassName().substring(3);		
	}*/
	
	final public String getObjectType() {
		return getTypeString();
	}
	
	private String translatedTypeString() {
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
			sb.delete(0, sb.length());
			sb.append(getNameDescription());			
			// add dependency information
			sb.append(": ");
			sb.append(algoParent.toString());			
			return sb.toString();
		}
	}

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
			StringBuffer sb = new StringBuffer();
			// html string	
			if (addHTMLtag)
				sb.append("<html>");
			
			boolean reverseOrder = app.isReverseNameDescriptionLanguage();		
			if (!reverseOrder) {
				//	standard order: "point A"
				sb.append(translatedTypeString());				
				sb.append(' ');
			}				
						
			if (colored) {
				sb.append("<b><font color=\"#");
				sb.append(Util.toHexString(labelColor));
				sb.append("\">");
			}
			sb.append(indicesToHTML(getLabel(), false));
			if (colored)
				sb.append("</font></b>");
			
			if (reverseOrder) {
				// reverse order: "A point"				
				sb.append(' ');
				sb.append(translatedTypeString());								
			}

			// add dependency information
			if (algoParent != null) {
				sb.append(": ");
				sb.append(indicesToHTML(algoParent.toString(), false));
			}
			if (addHTMLtag)
				sb.append("</html>");
			return sb.toString();
		}
	}

	/**
	 * Returns long description for all GeoElements in given array.	 	 
	 */
	final public static String getToolTipDescriptionHTML(
		ArrayList geos,
		boolean colored,
		boolean addHTMLtag) {
		if (geos == null)
			return null;

		StringBuffer sb = new StringBuffer();
		if (addHTMLtag)
			sb.append("<html>");
		int count=0;
		for (int i = 0; i < geos.size(); ++i) {
			GeoElement geo = (GeoElement) geos.get(i);
			if (geo.showToolTipText()) {
				count++;
				sb.append(geo.getLongDescriptionHTML(colored, false));			
				if (i+1 < geos.size())
					sb.append("<br>");
			}				
		}
		if (count == 0) return null;
		if (addHTMLtag)
			sb.append("</html>");
		return sb.toString();
	}

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
				return strAlgebraDescription;

			case LABEL_VALUE :
				return toDefinedValueString();
				
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
	public String getAlgebraDescriptionTextOrHTML() {
		return strAlgebraDescTextOrHTML;
	}

	public String getAlgebraDescriptionHTML(boolean addHTMLtag) {
		return indicesToHTML(strAlgebraDescription, false);
	}

	/**
		* Returns algebraic representation of this GeoElement.		
		*/
	public String getAlgebraDescription() {
		return strAlgebraDescription;
	}

	private void setAlgebraDescription() {
		if (isDefined()) {
			strAlgebraDescription = toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(label);
			sb.append(' ');
			sb.append(app.getPlain("undefined"));
			strAlgebraDescription = sb.toString();
		}

		// convertion to html is only needed if indices are found
		if (includesIndex(strAlgebraDescription)) {
			strAlgebraDescTextOrHTML =
				indicesToHTML(strAlgebraDescription, true);
		} else {
			strAlgebraDescTextOrHTML = strAlgebraDescription;
		}
	}

	/*
	 * replaces all indices (_ and _{}) in str by <sub> tags, all and converts all
	 * special characters in str to HTML
	 * examples:
	 * "a_1" becomes "a<sub>1</sub>"
	 * "s_{AB}" becomes "s<sub>AB</sub>"
	 */
	private static String subBegin = "<sub><font size=\"-1\">";
	private static String subEnd = "</font></sub>";
	static String indicesToHTML(String str, boolean addHTMLtag) {
		StringBuffer sb = new StringBuffer();
		if (addHTMLtag)
			sb.append("<html>");

		int depth = 0;
		int startPos = 0;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
				case '_' :
					//	write everything before _
					if (i > startPos) {
						sb.append(
							Util.toHTMLString(str.substring(startPos, i)));
					}
					startPos = i + 1;
					depth++;

					// check if next character is a '{' (beginning of index with several chars)
					if (startPos < length && str.charAt(startPos) != '{') {
						sb.append(subBegin);
						sb.append(
							Util.toHTMLString(
								str.substring(startPos, startPos + 1)));
						sb.append(subEnd);
						depth--;
					} else {
						sb.append(subBegin);
					}
					i++;
					startPos++;
					break;

				case '}' :
					if (depth > 0) {
						if (i > startPos) {
							sb.append(
								Util.toHTMLString(str.substring(startPos, i)));
						}
						sb.append(subEnd);
						startPos = i + 1;
						depth--;
					}
					break;
			}
		}

		if (startPos < length) {
			sb.append(Util.toHTMLString(str.substring(startPos)));
		}
		if (addHTMLtag)
			sb.append("</html>");
		return sb.toString();
	}

	/**
		* returns type and label of a GeoElement 
		* (for tooltips and error messages)		
		*/
	public String getNameDescription() {
		StringBuffer sb = new StringBuffer();
						
		if (app.isReverseNameDescriptionLanguage()) {
			//	reverse order: "A point"
			sb.append(getLabel());				
			sb.append(' ');			
			sb.append(translatedTypeString());			
		}	
		else {
			// standard order: "point A"
			sb.append(translatedTypeString());				
			sb.append(' ');
			sb.append(getLabel());
		}
				
		return sb.toString();
	}

	/**
		* returns type and label of a GeoElement 
		* (for tooltips and error messages)		
		*/
	final public String getNameDescriptionTextOrHTML() {
		if (includesIndex(label))
			return getNameDescriptionHTML(false, true);
		else
			return getNameDescription();
	}

	/**
	 * Returns whether the str contains any indices (i.e. '_' chars). 
	 */
	private static boolean includesIndex(String str) {
		if (str == null)
			return false;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			if (str.charAt(i) == '_')
				return true;
		}
		return false;
	}

	/**
		* returns type and label of a GeoElement as html string
		* (for tooltips and error messages)		
		*/
	public String getNameDescriptionHTML(
		boolean colored,
		boolean addHTMLtag) {
		StringBuffer sb = new StringBuffer();
		if (addHTMLtag)
			sb.append("<html>");
			
		boolean reverseOrder = app.isReverseNameDescriptionLanguage();		
		if (!reverseOrder) {
			//	standard order: "point A"
			sb.append(translatedTypeString());				
			sb.append(' ');
		}						
		
		if (colored) {
			sb.append(" <b><font color=\"#");
			sb.append(Util.toHexString(objColor));
			sb.append("\">");
		}
		sb.append(indicesToHTML(getLabel(), false));
		if (colored)
			sb.append("</font></b>");
		
		if (reverseOrder) {
			//	reverse order: "A point"
			sb.append(' ');
			sb.append(translatedTypeString());							
		}
		
		if (addHTMLtag)
			sb.append("</html>");							
		return sb.toString();
	}

	/*******************************************************
	 * SAVING
	 *******************************************************/

	private String getXMLtypeString() {		
		return getClassName().substring(3).toLowerCase();
	}
	
	/**
	 * save object in xml format
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
		sb.append("</element>\n");
				
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

		if (labelMode != LABEL_NAME) {
			sb.append("\t<labelMode");
			sb.append(" val=\"");
			sb.append(labelMode);
			sb.append("\"");
			sb.append("/>\n");
		}

		// trace on or off
		if (isTraceable()) {
			Traceable t = (Traceable) this;
			if (t.getTrace()) {
				sb.append("\t<trace val=\"true\"/>\n");
			}
		}

		return sb.toString();
	}

	String getXMLanimationTags() {
		// animation step width
		if (isChangeable()) {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<animation step=\"");
			sb.append(animationStep);
			sb.append("\"/>\n");
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
	 */
	String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		sb.append(getXMLvisualTags());
		sb.append(getXMLanimationTags());
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		sb.append(getBreakpointXML());
		return sb.toString();
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
	
	
	/*
	 *  NOTE: change in GeoElementWrapper too!
	 */
	
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
   	

}
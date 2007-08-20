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
 * Kernel.java
 *
 * Created on 30. August 2001, 20:12
 */

package geogebra.kernel;

import geogebra.Application;
import geogebra.MyError;
import geogebra.View;
import geogebra.algebra.parser.Parser;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.optimization.ExtremumFinder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class Kernel {

	// standard precision 
	public final static double STANDARD_PRECISION = 1E-8;
	
	// minimum precision
	public final static double MIN_PRECISION = 1E-5;
	
	// current working precision
	private double EPSILON = STANDARD_PRECISION;
	
	 // style of point/vector coordinates
    public static final int COORD_STYLE_DEFAULT = 0;		// A = (3, 2)  and 	B = (3; 90°)
	public static final int COORD_STYLE_AUSTRIAN = 1;		// A(3|2)  		and	B(3; 90°)
	private int coordStyle = 0;

	// STATIC
	final public static int ANGLE_RADIANT = 1;
	final public static int ANGLE_DEGREE = 2;
	final public static int COORD_CARTESIAN = 3;
	final public static int COORD_POLAR = 4;	 
	final public static String EULER_STRING = "\u212f"; // "\u0435";
	final public static String PI_STRING = "\u03c0";	
	final public static double PI_2 = 2.0 * Math.PI;
	final public static double PI_HALF =  Math.PI / 2.0;
	final public static double SQRT_2_HALF =  Math.sqrt(2.0) / 2.0;
	//private static boolean KEEP_LEADING_SIGN = true;
	
	// print precision
	public static final int STANDARD_PRINT_DECIMALS = 2; 
	private double PRINT_PRECISION = 1E-2;
	private NumberFormat nf;
	private int casPrintForm;		
	private String casPrintFormPI; // for pi
	
	// before May 23, 2005 the function acos(), asin() and atan()
	// had an angle as result. Now the result is a number.
	// this flag is used to distinguish the different behaviour
	// depending on the the age of saved construction files
	public boolean arcusFunctionCreatesAngle = false;
	
	private boolean translateCommandName = true;
	private boolean undoActive = true;
	private boolean notifyViewsActive = true, viewReiniting = false;
		
	private double xmin, xmax, ymin, ymax, xscale, yscale;
	
	// Views may register to be informed about 
	// changes to the Kernel
	// (add, remove, update)
	private View[] views = new View[20];
	private int viewCnt = 0;
	
	protected Construction cons;
	protected Application app;	
	private AlgebraProcessor algProcessor;
	private EquationSolver eqnSolver;
	private ExtremumFinder extrFinder;
	private Parser parser;
	private GeoGebraCAS ggbCAS;
	
	// Continuity on or off, default: false since V3.0
	private boolean continuous = false;
	private MacroManager macroManager;
				
	public Kernel(Application app) {
		this();
		this.app = app;
		cons = new Construction(this);			
	}
	
	public Kernel() {
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
	}
	
	/**
	 * Returns this kernel's algebra processor that handles
	 * all input and commands.
	 */	
	final public AlgebraProcessor getAlgebraProcessor() {
    	if (algProcessor == null)
    		algProcessor = new AlgebraProcessor(this);
    	return algProcessor;
    }
	
	
	public GeoElement lookupLabel(String label) {		
		return cons.lookupLabel(label);
	}
	
	final public GeoAxis getXAxis() {
		return cons.getXAxis();
	}
	
	final public GeoAxis getYAxis() {
		return cons.getYAxis();
	}
	
	final public boolean isAxis(GeoElement geo) {
		return (geo == cons.getXAxis() || geo == cons.getYAxis());
	}
	
    public void updateLocalAxesNames() {
    	cons.updateLocalAxesNames();
    }
	
	public Application getApplication() {
		return app;
	}		
	
	public void setShowOnlyBreakpoints(boolean flag) {
		 cons.setShowOnlyBreakpoints(flag);
	}
	
	final public boolean showOnlyBreakpoints() {
		return cons.showOnlyBreakpoints();
	}
	
	final public EquationSolver getEquationSolver() {
		if (eqnSolver == null)
			eqnSolver = new EquationSolver(this);
		return eqnSolver;
	}
	
	final public ExtremumFinder getExtremumFinder() {
		if (extrFinder == null)
			extrFinder = new ExtremumFinder();
		return extrFinder;
	}
	
	final public Parser getParser() {
    	if (parser == null)
    		parser = new Parser(this, cons);
    	return parser;
    }		
	
	/** 
     * Evaluates a YACAS expression and returns the result as a String.
     * e.g. exp = "D(x) (x^2)" returns "2*x"
     * @param expression string
     * @return result string (null possible)
     */ 
	final public String evaluateYACAS(String exp) {
		if (ggbCAS == null) {
			initCAS();		
		}
		
		return ggbCAS.evaluateYACAS(exp);
	}
	
	/** 
     * Evaluates a JASYMCA expression and returns the result as a String.
     * e.g. exp = "diff(x^2,x)" returns "2*x"
     * @param expression string
     * @return result string (null possible)
     */ 
	final public String evaluateJASYMCA(String exp) {
		if (ggbCAS == null) {
			initCAS();		
		}
		
		return ggbCAS.evaluateJASYMCA(exp);
	}
	
	public synchronized void initCAS() {
		if (ggbCAS == null) {
			ggbCAS = new GeoGebraCAS();
		}			
	}
	/**
     * Finds the polynomial coefficients of
     * the given expression and returns it in ascending order. 
     * If exp is not a polynomial null is returned.
     * 
     * example: getPolynomialCoeffs("3*a*x^2 + b"); returns
     * ["0", "b", "3*a"]
     */
    final public String [] getPolynomialCoeffs(String exp, String variable) {
    	if (ggbCAS == null) {
			initCAS();					
		}
    	
    	return ggbCAS.getPolynomialCoeffs(exp, variable);
    }

	final public void setEpsilon(double epsilon) {
		EPSILON = epsilon;
		getEquationSolver().setEpsilon(epsilon);
	}

	final public double getEpsilon() {
		return EPSILON;
	}

	final public void setMinPrecision() {
		EPSILON = MIN_PRECISION;
	}

	final public void resetPrecision() {
		EPSILON = STANDARD_PRECISION;
	}
	
	/**
	 * Tells this kernel about the bounds and the scales for x-Axis and y-Axis used
	 * in EudlidianView. The scale is the number of pixels per unit.
	 * (useful for some algorithms like findminimum). All 
	 */
	final public void setEuclidianViewBounds(double xmin, double xmax, 
			double ymin, double ymax, double xscale, double yscale) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.xscale = xscale;
		this.yscale = yscale;
				
		notifyEuclidianViewAlgos();
	}	
	
	double getXmax() {
		return xmax;
	}
	double getXmin() {
		return xmin;
	}
	double getXscale() {
		return xscale;
	}
	double getYmax() {
		return ymax;
	}
	double getYmin() {
		return ymin;
	}
	double getYscale() {
		return yscale;
	}	
	
	/**
	 * Registers an algorithm that wants to be notified when setEuclidianViewBounds() is called.	 
	 */
	void registerEuclidianViewAlgo(EuclidianViewAlgo algo) {
		if (!euclidianViewAlgos.contains(algo))
			euclidianViewAlgos.add(algo);
	}
	
	void unregisterEuclidianViewAlgo(EuclidianViewAlgo algo) {		
			euclidianViewAlgos.remove(algo);
	}
	private ArrayList euclidianViewAlgos = new ArrayList();
	
	private void notifyEuclidianViewAlgos() {
		int size = euclidianViewAlgos.size();
		for (int i=0; i < size; i++) {
			((EuclidianViewAlgo) euclidianViewAlgos.get(i)).euclidianViewUpdate();
		}
	}		

	final public void setAngleUnit(int unit) {
		cons.angleUnit = unit;
	}

	final public int getAngleUnit() {
		return cons.angleUnit;
	}
	
	final public int getMaximumFractionDigits() {
		return nf.getMaximumFractionDigits();
	}

	final public void setMaximumFractionDigits(int digits) {
		nf.setMaximumFractionDigits(digits);
	}
	
	final public void setCASPrintForm(int type) {
		casPrintForm = type;
		
		switch (casPrintForm) {
			case ExpressionNode.STRING_TYPE_YACAS:
				casPrintFormPI = "Pi";
				
			case ExpressionNode.STRING_TYPE_JASYMCA:
			case ExpressionNode.STRING_TYPE_GEOGEBRA_XML:
				casPrintFormPI = "pi";
		
			default:
				casPrintFormPI = PI_STRING;
		}
	}
	
	final public int getCASPrintForm() {
		return casPrintForm;
	}

	final public void setPrintDecimals(int decimals) {
		if (decimals >= 0 && decimals != nf.getMaximumFractionDigits()) {
			nf.setMaximumFractionDigits(decimals);
			PRINT_PRECISION = Math.pow(10, -decimals);
		}
	}
	
	final public int getPrintDecimals() {
		return nf.getMaximumFractionDigits();
	}
		
	/**
	 * returns 10^(-PrintDecimals)
	 */
	final public double getPrintPrecision() {
		return PRINT_PRECISION;
	}
	
	final public int getCoordStyle() {
		return coordStyle;
	}
	public void setCoordStyle(int coordStlye) {
		coordStyle = coordStlye;		
	}
	
	/*
	 * GeoElement specific
	 */
	
	
	
	/**
     * Creates a new GeoElement object for the given type string.
     * @param type: String as produced by GeoElement.getXMLtypeString()
     */
    final public static GeoElement createGeoElement(Construction cons, String type) throws MyError {    	
    	// the type strings are the classnames in lowercase without the beginning "geo"
    	// due to a bug in GeoGebra 2.6c the type strings for conics
        // in XML may be "ellipse", "hyperbola", ...  
    	    	
    	switch (type.charAt(0)) {
    		case 'a': //angle    			
    			return new GeoAngle(cons);	    			     		    			
    			
    		case 'b': //angle
    			return new GeoBoolean(cons);
    		
    		case 'c': // conic
    			if (type.equals("conic"))
    				return new GeoConic(cons);   
    			else if (type.equals("conicpart"))    					
    				return new GeoConicPart(cons, 0);
    			else if (type.equals("circle")) { // bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			}
    			
    		case 'd': // doubleLine 			// bug in GeoGebra 2.6c
    			return new GeoConic(cons);    			
    			
    		case 'e': // ellipse, emptyset	//  bug in GeoGebra 2.6c
				return new GeoConic(cons);     			
    				
    		case 'f': // function
    			return new GeoFunction(cons);
    		
    		case 'h': // hyperbola			//  bug in GeoGebra 2.6c
				return new GeoConic(cons);     			
    			
    		case 'i': // image
    			if (type.equals("image"))    				
    				return new GeoImage(cons);
    			else if (type.equals("intersectinglines")) //  bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    		
    		case 'l': // line, list, locus
    			if (type.equals("line"))
    				return new GeoLine(cons);
    			else if (type.equals("list"))
    				return new GeoList(cons);    			
    			else 
    				return new GeoLocus(cons);
    		
    		case 'n': // numeric
    			return new GeoNumeric(cons);
    			
    		case 'p': // point, polygon
    			if (type.equals("point")) 
    				return new GeoPoint(cons);
    			else if (type.equals("polygon"))
    				return new GeoPolygon(cons, null);
    			else // parabola, parallelLines, point //  bug in GeoGebra 2.6c
    				return new GeoConic(cons);
    			
    		case 'r': // ray
    			return new GeoRay(cons, null);
    			
    		case 's': // segment    			
    			return new GeoSegment(cons, null, null);	    			    			
    			
    		case 't': // text
    			return new GeoText(cons);
    			
    		case 'v': // vector
				return new GeoVector(cons);
    		
    		default:    			
    			throw new MyError(cons.getApplication(), "Kernel: GeoElement of type "
    		            + type + " could not be created.");		    		
    	}    		    
    }     

	/* *******************************************
	 *  Construction specific methods
	 * ********************************************/

	/**
	 * Returns the ConstructionElement for the given GeoElement.
	 * If geo is independent geo itself is returned. If geo is dependent
	 * it's parent algorithm is returned.	 
	 */
	public static ConstructionElement getConstructionElement(GeoElement geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		if (algo == null)
			return geo;
		else
			return algo;
	}
	
	/**
	 * Returns the Construction object of this kernel.
	 */
	public Construction getConstruction() {
		return cons;
	}

	/**
	 * Returns the ConstructionElement for the given construction index.
	 */
	public ConstructionElement getConstructionElement(int index) {
		return cons.getConstructionElement(index);
	}

	public void setConstructionStep(int step) {
		cons.setStep(step);
	}

	public int getConstructionStep() {
		return cons.getStep();
	}

	public int getLastConstructionStep() {
		return cons.steps() - 1;
	}
	
	/**
	 * Sets construction step to 
	 * first step of construction protocol. 
	 * Note: showOnlyBreakpoints() is important here
	 */
	public void firstStep() {
		int step = 0;
		
		if (showOnlyBreakpoints()) {
			cons.setStep(getNextBreakpoint(step));		
		} else {	
    		cons.setStep(step);
    	}
	}
	
	/**
	 * Sets construction step to 
	 * last step of construction protocol. 
	 * Note: showOnlyBreakpoints() is important here
	 */
	public void lastStep() {
		int step = getLastConstructionStep();
		
		if (showOnlyBreakpoints()) {
			cons.setStep(getPreviousBreakpoint(step));		
		} else {	
    		cons.setStep(step);
    	}
	}
	
	/**
	 * Sets construction step to 
	 * next step of construction protocol. 
	 * Note: showOnlyBreakpoints() is important here
	 */
	public void nextStep() {		
		int step = cons.getStep() + 1;
		
		if (showOnlyBreakpoints()) {
			cons.setStep(getNextBreakpoint(step));		
		} else {	
    		cons.setStep(step);
    	}
	}
	
	private int getNextBreakpoint(int step) {
		int lastStep = getLastConstructionStep();
		// go to next breakpoint
		while (step <= lastStep) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint())
				return step;
			step++;
		}
		return lastStep;
	}

	/**
	 * Sets construction step to 
	 * previous step of construction protocol	
	 * Note: showOnlyBreakpoints() is important here 
	 */
	public void previousStep() {		
		int step = cons.getStep() - 1;
		
		if (showOnlyBreakpoints()) {
			cons.setStep(getPreviousBreakpoint(step));
		}
    	else {		
    		cons.setStep(step);
    	}
	}
	
	private int getPreviousBreakpoint(int step) {
		// go to previous breakpoint
		while (step >= 0) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint())
				return step;				
			step--;
		}
		return -1;
	}
	
	/**
	 * Move object at position from to position to in current construction.
	 */
	public boolean moveInConstructionList(int from, int to) {
		return cons.moveInConstructionList(from, to);
	}

	public void clearConstruction() {		
		if (macroManager != null)
			macroManager.setAllMacrosUnused();
		
		cons.clearConstruction();
		notifyClearView();
		notifyRepaint();
		System.gc();
	}

	public void updateConstruction() {
		cons.updateConstruction();
		notifyRepaint();
	}

	/**
	 * Tests if the current construction has no elements. 
	 * @return true if the current construction has no GeoElements; false otherwise.
	 */
	public boolean isEmpty() {
		return cons.isEmpty();
	}

	/* ******************************
	 * redo / undo for current construction
	 * ******************************/

	public void setUndoActive(boolean flag) {
		undoActive = flag;
		if (undoActive) initUndoInfo();		
	}
	
	public void storeUndoInfo() {
		if (undoActive)
			cons.storeUndoInfo();
	}

	public void restoreCurrentUndoInfo() {
		if (undoActive) cons.restoreCurrentUndoInfo();
	}

	public void initUndoInfo() {
		if (undoActive) cons.initUndoInfo();
	}

	public void redo() {
		if (undoActive){			
			notifyReset();
			cons.redo();	
			notifyReset();
		}
	}

	public void undo() {
		if (undoActive) {			
			notifyReset();
			cons.undo();
			notifyReset();
		}
	}

	public boolean undoPossible() {
		return undoActive && cons.undoPossible();
	}

	public boolean redoPossible() {
		return undoActive && cons.redoPossible();
	}

	/* *******************************************************
	 * methods for view-Pattern (Model-View-Controller)
	 * *******************************************************/

	public void attach(View view) {			
		//System.out.println("attach " + view + ", notifyActive: " + notifyViewsActive);
		
		if (!notifyViewsActive) {			
			viewCnt = oldViewCnt;
		}
		
		// view already attached?
		boolean viewFound = false;
		for (int i = 0; i < viewCnt; i++) {
			if (views[i] == view) {
				viewFound = true;
				break;
			}				
		}
		
		if (!viewFound) {
			// new view
			views[viewCnt++] = view;
		}
				
		/*
		System.out.print("  current views: ");
		for (int i = 0; i < viewCnt; i++) {
			System.out.print(views[i] + ", ");
		}
		System.out.println();
		*/
		
		if (!notifyViewsActive) {
			oldViewCnt = viewCnt;
			viewCnt = 0;
		}
	}

	public void detach(View view) {
		//System.out.println("detach " + view);
		
		if (!notifyViewsActive) {
			viewCnt = oldViewCnt;
		}
		
		int pos = -1;
		for (int i = 0; i < viewCnt; ++i) {
			if (views[i] == view) {
				pos = i;
				views[pos] = null; // delete view
				break;
			}
		}
		
		// view found
		if (pos > -1) {						
			// copy following views
			viewCnt--;		
			for (; pos < viewCnt; ++pos) {
				views[pos] = views[pos + 1];
			}
		}
		
		/*
		System.out.print("  current views: ");
		for (int i = 0; i < viewCnt; i++) {
			System.out.print(views[i] + ", ");
		}
		System.out.println();		
		*/
		
		if (!notifyViewsActive) {
			oldViewCnt = viewCnt;
			viewCnt = 0;
		}
	}	

	final public void notifyAddAll(View view) {
		if (!notifyViewsActive) return;
		
		Iterator it = cons.getGeoSetConstructionOrder().iterator();
		while (it.hasNext()) {
			view.add((GeoElement) it.next());
		}			
	}	
	
	/*
	final public void notifyRemoveAll(View view) {
		Collection geos = cons.getAllGeoElements();
		Iterator it = geos.iterator();
		while (it.hasNext()) {
			view.remove((GeoElement) it.next());
		}
	}*/

	/**
	 * Tells views to update all labeled elements of current construction.
	 *
	final public static void notifyUpdateAll() {
		notifyUpdate(kernelConstruction.getAllGeoElements());
	}*/

	final void notifyAdd(GeoElement geo) {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].add(geo);					
		}
	}

	final void notifyRemove(GeoElement geo) {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].remove(geo);
		}
	}

	final void notifyUpdate(GeoElement geo) {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].update(geo);
		}
	}
	
	final void notifyUpdateAuxiliaryObject(GeoElement geo) {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].updateAuxiliaryObject(geo);
		}
	}

	final  void notifyRename(GeoElement geo) {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].rename(geo);
		}
	}
	
	public void setNotifyViewsActive(boolean flag) {	
		//System.out.println("setNotifyViews: " + flag);
		
		if (flag != notifyViewsActive) {
			notifyViewsActive = flag;
			
			if (flag) {
				//System.out.println("Activate VIEWS");				
				viewReiniting = true;
				
				// "attach" views again
				viewCnt = oldViewCnt;		
				
				// add all geos to all views
				Iterator it = cons.getGeoSetConstructionOrder().iterator();				
				while (it.hasNext()) {	
					GeoElement geo =  (GeoElement) it.next();					
					notifyAdd(geo);									
				}			
				//app.setMoveMode();
				notifyReset();
				
				viewReiniting = false;
			} 
			else {
				//System.out.println("Deactivate VIEWS");

				// "detach" views
				notifyClearView();				
				oldViewCnt = viewCnt;
				viewCnt = 0;								
			}					
		}		
	}
	private int oldViewCnt;
	
	public boolean isNotifyViewsActive() {
		return notifyViewsActive && !viewReiniting;
	}
	
	private boolean notifyRepaint = true;
		
	public void setNotifyRepaintActive(boolean flag) {
		if (flag != notifyRepaint) {
			notifyRepaint = flag;
			if (notifyRepaint)
				notifyRepaint();
		}
	}
		
	public boolean isNotifyRepaintActive() {
		return notifyRepaint;
	}
	
	public final void notifyRepaint() {
		if (notifyRepaint) {
			for (int i = 0; i < viewCnt; ++i) {			
				views[i].repaintView();
			}
		}
	}
	
	final void notifyReset() {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].reset();
		}
	}
	
	final void notifyClearView() {
		for (int i = 0; i < viewCnt; ++i) {
			views[i].clearView();
		}
	}
	
	/* **********************************
	 *   MACRO handling
	 * **********************************/			
	
	/**
	 * Creates a new macro within the kernel. A macro is a user defined
	 * command in GeoGebra.
	 */
	public void addMacro(Macro macro) {
		if (macroManager == null) {
			macroManager = new MacroManager();
		}						
		macroManager.addMacro(macro);				
	}
	
	/**
	 * Removes a macro from the kernel.
	 */
	public void removeMacro(Macro macro) {
		if (macroManager != null)								
			macroManager.removeMacro(macro);			
	}
	
	/**
	 * Removes all macros from the kernel. 
	 */
	public void removeAllMacros() {
		if (macroManager != null) {								
			app.removeMacroCommands();			
			macroManager.removeAllMacros();			
		}
	}
	
	/**
	 * Sets the command name of a macro. Note: if the given name is
	 * already used nothing is done.
	 * @return if the command name was really set
	 */
	public boolean setMacroCommandName(Macro macro, String cmdName) {
		boolean nameUsed = macroManager.getMacro(cmdName) != null;
		if (nameUsed || cmdName == null || cmdName.length() == 0) 
			return false;
		
		macroManager.setMacroCommandName(macro, cmdName);		
		return true;		
	}
	
	/**
	 * Returns the macro object for a given macro name.
	 * Note: null may be returned.
	 */
	public Macro getMacro(String name) {
		return (macroManager == null) ? null : macroManager.getMacro(name);		
	}		
	
	/**
	 * Returns the number of currently registered macros
	 */
	public int getMacroNumber() {
		if (macroManager == null)
			return 0;
		else
			return macroManager.getMacroNumber();
	}
	
	/**
	 * Returns an array with all currently registered macros.
	 */
	public Macro [] getAllMacros() {
		if (macroManager == null)
			return null;
		else
			return macroManager.getAllMacros();
	}
	
	/**
	 * Returns i-th registered macro
	 */
	public Macro getMacro(int i) {
		if (macroManager == null)
			return null;
		else			
			return macroManager.getMacro(i);
	}
	
	/**
	 * Returns the ID of the given macro.
	 */
	public int getMacroID(Macro macro) {
		return (macroManager == null) ? -1 : macroManager.getMacroID(macro);	
	}
	
	/**
	 * Creates a new algorithm that uses the given macro.
	 * @return output of macro algorithm
	 */
	final public GeoElement [] useMacro(String [] labels, Macro macro, GeoElement [] input) {		
		try {
			AlgoMacro algo = new AlgoMacro(cons, labels, macro, input);
			return algo.getOutput();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}						
	}
	
	/**
	 * Returns an XML represenation of the given macros in this kernel.
	 * 
	 * @return
	 */
	public String getMacroXML(Macro [] macros) {
		if (hasMacros())					
			return MacroManager.getMacroXML(macros);
		else
			return "";
	}
	
	/**
	 * Returns whether any macros have been added to this kernel. 
	 */
	public boolean hasMacros() {
		return (macroManager != null && macroManager.getMacroNumber() > 0);
	}
	

	/***********************************
	 * FACTORY METHODS FOR GeoElements
	 ***********************************/

	/** Point label with cartesian coordinates (x,y)   */
	final public GeoPoint Point(String label, double x, double y) {
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, 1.0);
		p.setMode(COORD_CARTESIAN);
		p.setLabel(label); // invokes add()                
		return p;
	}

	/** Vector label with cartesian coordinates (x,y)   */
	final public GeoVector Vector(String label, double x, double y) {
		GeoVector v = new GeoVector(cons);
		v.setCoords(x, y, 0.0);
		v.setMode(COORD_CARTESIAN);
		v.setLabel(label); // invokes add()                
		return v;
	}

	/** Line a x + b y + c = 0 named label */
	final public GeoLine Line(
		String label,
		double a,
		double b,
		double c) {
		GeoLine line = new GeoLine(cons, label, a, b, c);
		return line;
	}

	/** Conic label with equation axï¿½ + bxy + cyï¿½ + dx + ey + f = 0  */
	final public GeoConic Conic(
		String label,
		double a,
		double b,
		double c,
		double d,
		double e,
		double f) {
		double[] coeffs = { a, b, c, d, e, f };
		GeoConic conic = new GeoConic(cons, label, coeffs);
		return conic;
	}

	/** Converts number to angle */
	final public GeoAngle Angle(String label, GeoNumeric num) {
		AlgoAngleNumeric algo = new AlgoAngleNumeric(cons, label, num);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/** Function in x,  e.g. f(x) = 4 xï¿½ + 3 xï¿½
	 */
	final public GeoFunction Function(String label, Function fun) {
		GeoFunction f = new GeoFunction(cons, label, fun);
		return f;
	}
	
	final public GeoText Text(String label, String text) {
		GeoText t = new GeoText(cons);
		t.setTextString(text);
		t.setLabel(label);
		return t;
	}
	
	final public GeoBoolean Boolean(String label, boolean value) {
		GeoBoolean b = new GeoBoolean(cons);
		b.setValue(value);
		b.setLabel(label);
		return b;
	}
		
	/**
	 * Creates a free list object with the given
	 * @param label
	 * @param geoElementList: list of GeoElement objects
	 * @return
	 */
	final public GeoList List(String label, ArrayList geoElementList, boolean isIndependent) {
		if (isIndependent) {
			GeoList list = new GeoList(cons);		
			int size = geoElementList.size();
			for (int i=0; i < size; i++) {
				list.add((GeoElement) geoElementList.get(i));
			}
			list.setLabel(label);
			return list;
		} 
		else {
			AlgoDependentList algoList = new AlgoDependentList(cons, label, geoElementList);
			return algoList.getGeoList();
		}		
	}
	
	/********************
	 * ALGORITHMIC PART *
	 ********************/

	/** 
	 * If-then-else construct.
	 */
	final public GeoElement If(String label, 
			GeoBoolean condition,
			GeoElement geoIf, GeoElement geoElse) {
		
		// check if geoIf and geoElse are of same type
	/*	if (geoElse == null ||
			geoIf.isNumberValue() && geoElse.isNumberValue() ||
			geoIf.getTypeString().equals(geoElse.getTypeString())) 
		{*/
			AlgoIf algo = new AlgoIf(cons, label, condition, geoIf, geoElse);
			return algo.getGeoElement();			
	/*	}
		else {
			// incompatible types
			System.out.println("if incompatible: " + geoIf + ", " + geoElse);
			return null;
		}	*/			
	}
	
	/** 
	 * If-then-else construct for functions. 
	 *  example: If[ x < 2, x^2, x + 2 ]
	 */
	final public GeoFunction If(String label, 
			GeoFunction boolFun,
			GeoFunction ifFun, GeoFunction elseFun) {
		
		AlgoIfFunction algo = new AlgoIfFunction(cons, label, boolFun, ifFun, elseFun);
		return algo.getGeoFunction();
	}	
	
	/** 
	 * Sequence command:
 	 * Sequence[ <expression>, <number-var>, <from>, <to>, <step> ]  
 	 * @return array with GeoList object and its list items
	 */
	final public GeoElement [] Sequence(String label, 
			GeoElement expression, GeoNumeric localVar, 
			NumberValue from, NumberValue to, NumberValue step) {
		
			AlgoSequence algo = new AlgoSequence(cons, label, expression, localVar, from, to, step);
			return algo.getOutput();				
	}	
	
	/** 
	 * Cartesian curve command:
 	 * Curve[ <expression x-coord>, <expression x-coord>, <number-var>, <from>, <to> ]  
	 */
	final public GeoCurveCartesian CurveCartesian(String label, 
			NumberValue xcoord, NumberValue ycoord, 
			GeoNumeric localVar, NumberValue from, NumberValue to) 
	{									
		AlgoCurveCartesian algo = new AlgoCurveCartesian(cons, label, xcoord, ycoord, localVar, from, to);
		return algo.getCurve();		
	}	
	
	/**
	 * Converts a NumberValue object to an ExpressionNode object. 
	 */
	public ExpressionNode convertNumberValueToExpressionNode(NumberValue nv) {
		GeoElement geo = nv.toGeoElement();
		AlgoElement algo = geo.getParentAlgorithm();
		
		if (algo != null && algo instanceof AlgoDependentNumber) {
			AlgoDependentNumber algoDep = (AlgoDependentNumber) algo;
			return algoDep.getExpression().getCopy();
		}
		else {
			return new ExpressionNode(this, geo);
		}		
	}
	
	/** Number dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. t = 6z - 2
	 */
	final public GeoNumeric DependentNumber(
		String label,
		ExpressionNode root,
		boolean isAngle) {
		AlgoDependentNumber algo =
			new AlgoDependentNumber(cons, label, root, isAngle);
		GeoNumeric number = algo.getNumber();
		return number;
	}

	/** Point dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. P = (4t, 2s)
	 */
	final public GeoPoint DependentPoint(
		String label,
		ExpressionNode root) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, label, root);
		GeoPoint P = algo.getPoint();
		return P;
	}

	/** Vector dependent on arithmetic expression with variables,
	 * represented by a tree. e.g. v = u + 3 w
	 */
	final public GeoVector DependentVector(
		String label,
		ExpressionNode root) {
		AlgoDependentVector algo = new AlgoDependentVector(cons, label, root);
		GeoVector v = algo.getVector();
		return v;
	}

	/** Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y = k x + d
	 */
	final public GeoLine DependentLine(String label, Equation equ) {
		AlgoDependentLine algo = new AlgoDependentLine(cons, label, equ);
		GeoLine line = algo.getLine();
		return line;
	}

	/** Conic dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. yï¿½ = 2 p x 
	 */
	final public GeoConic DependentConic(String label, Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic(cons, label, equ);
		GeoConic conic = algo.getConic();
		return conic;
	}

	/** Function dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. f(x) = a xï¿½ + b xï¿½
	 */
	final public GeoFunction DependentFunction(
		String label,
		Function fun) {
		AlgoDependentFunction algo = new AlgoDependentFunction(cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}
	
	/** Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. text = "Radius: " + r
	 */
	final public GeoText DependentText(
		String label,
		ExpressionNode root) {
		AlgoDependentText algo = new AlgoDependentText(cons, label, root);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Name of geo.
	 */
	final public GeoText Name(
		String label,
		GeoElement geo) {
		AlgoName algo = new AlgoName(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. c = a & b
	 */
	final public GeoBoolean DependentBoolean(
		String label,
		ExpressionNode root) {
		AlgoDependentBoolean algo = new AlgoDependentBoolean(cons, label, root);
		return algo.getGeoBoolean();		
	}
	
	/** Point on path with cartesian coordinates (x,y)   */
	final public GeoPoint Point(String label, Path path, double x, double y) {
		AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, x, y);
		GeoPoint p = algo.getP();        
		return p;
	}
	
	/** Point P + v   */
	final public GeoPoint Point(String label, GeoPoint P, GeoVector v) {
		AlgoPointVector algo = new AlgoPointVector(cons, label, P, v);
		GeoPoint p = algo.getQ();        
		return p;
	}

	/** 
	 * Returns the projected point of P on line g. 
	 */
	final public GeoPoint ProjectedPoint(GeoPoint P, GeoLine g) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);		
			GeoLine perp = OrthogonalLine(null, P, g);
			GeoPoint S = IntersectLines(null, perp, g);		
		cons.setSuppressLabelCreation(oldMacroMode);
		return S;
	}

	
	/** 
	 * Midpoint M = (P + Q)/2
	 */
	final public GeoPoint Midpoint(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoMidpoint algo = new AlgoMidpoint(cons, label, P, Q);
		GeoPoint M = algo.getPoint();
		return M;
	}
	
	/** 
	 * Creates Midpoint M = (P + Q)/2 without label (for use as e.g. start point)
	 */
	final public GeoPoint Midpoint(
		GeoPoint P,
		GeoPoint Q) {

		boolean oldValue = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		GeoPoint midPoint = Midpoint(null, P, Q);
		cons.setSuppressLabelCreation(oldValue);
		return midPoint;
	}
	
	/** 
	 * Midpoint of segment
	 */
	final public GeoPoint Midpoint(
		String label,
		GeoSegment s) {
		AlgoMidpointSegment algo = new AlgoMidpointSegment(cons, label, s);
		GeoPoint M = algo.getPoint();
		return M;
	}

	/** 
		* LineSegment named label from Point P to Point Q
		*/
	final public GeoSegment Segment(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons, label, P, Q);
		GeoSegment s = algo.getSegment();
		return s;
	}

	/** 
	 * Line named label through Points P and Q
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPoints algo = new AlgoJoinPoints(cons, label, P, Q);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Line named label through Point P with direction of vector v
	 */
	final public GeoLine Line(String label, GeoPoint P, GeoVector v) {
		AlgoLinePointVector algo = new AlgoLinePointVector(cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 *  Ray named label through Points P and Q
	 */
	final public GeoRay Ray(String label, GeoPoint P, GeoPoint Q) {
		AlgoJoinPointsRay algo = new AlgoJoinPointsRay(cons, label, P, Q);
		return algo.getRay();
	}

	/** 
	 * Ray named label through Point P with direction of vector v
	 */
	final public GeoRay Ray(String label, GeoPoint P, GeoVector v) {
		AlgoRayPointVector algo = new AlgoRayPointVector(cons, label, P, v);
		return algo.getRay();
	}
	
	/** 
	* Line named label through Point P parallel to Line l
	*/
	final public GeoLine Line(String label, GeoPoint P, GeoLine l) {
		AlgoLinePointLine algo = new AlgoLinePointLine(cons, label, P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	* Line named label through Point P orthogonal to vector v
	*/
	final public GeoLine OrthogonalLine(
		String label,
		GeoPoint P,
		GeoVector v) {
		AlgoOrthoLinePointVector algo =
			new AlgoOrthoLinePointVector(cons, label, P, v);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Line named label through Point P orthogonal to line l
	 */
	final public GeoLine OrthogonalLine(
		String label,
		GeoPoint P,
		GeoLine l) {
		AlgoOrthoLinePointLine algo = new AlgoOrthoLinePointLine(cons, label, P, l);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Line bisector of points A, B
	 */
	final public GeoLine LineBisector(
		String label,
		GeoPoint A,
		GeoPoint B) {
		AlgoLineBisector algo = new AlgoLineBisector(cons, label, A, B);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	  * Line bisector of segment s
	  */
	final public GeoLine LineBisector(String label, GeoSegment s) {
		AlgoLineBisectorSegment algo = new AlgoLineBisectorSegment(cons, label, s);
		GeoLine g = algo.getLine();
		return g;		
	}

	/** 
	 * Angular bisector of points A, B, C
	 */
	final public GeoLine AngularBisector(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoAngularBisectorPoints algo =
			new AlgoAngularBisectorPoints(cons, label, A, B, C);
		GeoLine g = algo.getLine();
		return g;
	}

	/** 
	 * Angular bisectors of lines g, h
	 */
	final public GeoLine[] AngularBisector(
		String[] labels,
		GeoLine g,
		GeoLine h) {
		AlgoAngularBisectorLines algo =
			new AlgoAngularBisectorLines(cons, labels, g, h);
		GeoLine[] lines = algo.getLines();
		return lines;
	}

	/** 
	 * Vector named label from Point P to Q
	 */
	final public GeoVector Vector(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoVector algo = new AlgoVector(cons, label, P, Q);
		GeoVector v = algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		notifyUpdate(v);
		return v;
	}

	/** 
	* Vector (0,0) to P
	*/
	final public GeoVector Vector(String label, GeoPoint P) {
		AlgoVectorPoint algo = new AlgoVectorPoint(cons, label, P);
		GeoVector v = algo.getVector();
		v.setEuclidianVisible(true);
		v.update();
		notifyUpdate(v);
		return v;
	}

	/** 
	 * Direction vector of line g
	 */
	final public GeoVector Direction(String label, GeoLine g) {
		AlgoDirection algo = new AlgoDirection(cons, label, g);
		GeoVector v = algo.getVector();
		return v;
	}

	/** 
	 * Slope of line g
	 */
	final public GeoNumeric Slope(String label, GeoLine g) {
		AlgoSlope algo = new AlgoSlope(cons, label, g);
		GeoNumeric slope = algo.getSlope();
		return slope;
	}

	/** 
	 * Slope of function f = derivative of f
	 */
	final public GeoFunction Slope(String label, GeoFunction f) {
		AlgoDerivative algo = new AlgoDerivative(cons, label, f);
		GeoFunction g = (GeoFunction) algo.getDerivative();
		return g;
	}
	
	/** 
	 * UpperSum of function f 
	 */
	final public GeoNumeric UpperSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumUpper algo = new AlgoSumUpper(cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * LowerSum of function f 
	 */
	final public GeoNumeric LowerSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumLower algo = new AlgoSumLower(cons, label, f, a, b, n);
		GeoNumeric sum = algo.getSum();
		return sum;
	}	

	/** 
	 * unit vector of line g
	 */
	final public GeoVector UnitVector(String label, GeoLine g) {
		AlgoUnitVectorLine algo = new AlgoUnitVectorLine(cons, label, g);
		GeoVector v = algo.getVector();
		return v;
	}

	/** 
	 * unit vector of vector v
	 */
	final public GeoVector UnitVector(String label, GeoVector v) {
		AlgoUnitVectorVector algo = new AlgoUnitVectorVector(cons, label, v);
		GeoVector u = algo.getVector();
		return u;
	}

	/** 
	 * orthogonal vector of line g
	 */
	final public GeoVector OrthogonalVector(String label, GeoLine g) {
		AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine(cons, label, g);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * orthogonal vector of vector v
	 */
	final public GeoVector OrthogonalVector(String label, GeoVector v) {
		AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector(cons, label, v);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * unit orthogonal vector of line g
	 */
	final public GeoVector UnitOrthogonalVector(
		String label,
		GeoLine g) {
		AlgoUnitOrthoVectorLine algo = new AlgoUnitOrthoVectorLine(cons, label, g);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * unit orthogonal vector of vector v
	 */
	final public GeoVector UnitOrthogonalVector(
		String label,
		GeoVector v) {
		AlgoUnitOrthoVectorVector algo =
			new AlgoUnitOrthoVectorVector(cons, label, v);
		GeoVector n = algo.getVector();
		return n;
	}

	/** 
	 * Length named label of vector v
	 */
	final public GeoNumeric Length(String label, GeoVec3D v) {
		AlgoLengthVector algo = new AlgoLengthVector(cons, label, v);
		GeoNumeric num = algo.getLength();
		return num;
	}

	/** 
	 * Distance named label between points P and Q
	 */
	final public GeoNumeric Distance(
		String label,
		GeoPoint P,
		GeoPoint Q) {
		AlgoDistancePoints algo = new AlgoDistancePoints(cons, label, P, Q);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/** 
	 * Distance named label between point P and line g
	 */
	final public GeoNumeric Distance(
		String label,
		GeoPoint P,
		GeoLine g) {
		AlgoDistancePointLine algo = new AlgoDistancePointLine(cons, label, P, g);
		GeoNumeric num = algo.getDistance();
		return num;
	}

	/** 
	 * Distance named label between line g and line h
	 */
	final public GeoNumeric Distance(
		String label,
		GeoLine g,
		GeoLine h) {
		AlgoDistanceLineLine algo = new AlgoDistanceLineLine(cons, label, g, h);
		GeoNumeric num = algo.getDistance();
		return num;
	}
	
	/** 
	 * Area named label of  P[0], ..., P[n]
	 */
	final public GeoNumeric Area(String label, GeoPoint [] P) {
		AlgoAreaPoints algo = new AlgoAreaPoints(cons, label, P);
		GeoNumeric num = algo.getArea();
		return num;
	}
	
	/** 
	 * Area named label of  conic
	 */
	final public GeoNumeric Area(String label, GeoConic c) {
		AlgoAreaConic algo = new AlgoAreaConic(cons, label, c);
		GeoNumeric num = algo.getArea();
		return num;
	}
	
	/** 
	 * Mod[a, b]
	 */
	final public GeoNumeric Mod(String label, NumberValue a, NumberValue b) {
		AlgoMod algo = new AlgoMod(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Div[a, b]
	 */
	final public GeoNumeric Div(String label, NumberValue a, NumberValue b) {
		AlgoDiv algo = new AlgoDiv(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Min[a, b]
	 */
	final public GeoNumeric Min(String label, NumberValue a, NumberValue b) {
		AlgoMin algo = new AlgoMin(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Min[list]
	 */
	final public GeoNumeric Min(String label, GeoList list) {
		AlgoListMin algo = new AlgoListMin(cons, label, list);
		GeoNumeric num = algo.getMin();
		return num;
	}
	
	/** 
	 * Max[a, b]
	 */
	final public GeoNumeric Max(String label, NumberValue a, NumberValue b) {
		AlgoMax algo = new AlgoMax(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Max[list]
	 */
	final public GeoNumeric Max(String label, GeoList list) {
		AlgoListMax algo = new AlgoListMax(cons, label, list);
		GeoNumeric num = algo.getMax();
		return num;
	}
	
	/** 
	 * Iteration[ f(x), x0, n ] 
	 */
	final public GeoNumeric Iteration(String label, GeoFunction f, NumberValue start,
			NumberValue n) {
		AlgoIteration algo = new AlgoIteration(cons, label, f, start, n);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * IterationList[ f(x), x0, n ] 
	 */
	final public GeoList IterationList(String label, GeoFunction f, NumberValue start,
			NumberValue n) {
		AlgoIterationList algo = new AlgoIterationList(cons, label, f, start, n);
		return algo.getResult();				
	}
	
	/** 
	 * Element[list, number]
	 */
	final public GeoElement Element(String label, GeoList list, NumberValue n) {
		AlgoListElement algo = new AlgoListElement(cons, label, list, n);
		GeoElement geo = algo.getElement();
		return geo;
	}
	
	/** 
	 * Length[list]
	 */
	final public GeoNumeric Length(String label, GeoList list) {
		AlgoListLength algo = new AlgoListLength(cons, label, list);
		return algo.getLength();
	}
	
	// PhilippWeissenbacher 2007-04-10
	
	/**
	 * Perimeter named label of GeoPolygon
	 */
	final public GeoNumeric Perimeter(String label, GeoPolygon polygon) {
	    AlgoPerimeterPoly algo = new AlgoPerimeterPoly(cons, label, polygon);
	    return algo.getCircumference();
	}
	
	/**
	 * Circumference named label of GeoConic
	 */
	final public GeoNumeric Circumference(String label, GeoConic conic) {
	    AlgoCircumferenceConic algo = new AlgoCircumferenceConic(cons, label, conic);
	    return algo.getCircumference();
	}
	
	// PhilippWeissenbacher 2007-04-10
		
	/** 
	 * polygon P[0], ..., P[n-1]
	 * The labels name the polygon itself and its segments
	 */
	final public GeoElement [] Polygon(String [] labels, GeoPoint [] P) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, P);
		return algo.getOutput();
	}
	
	/** 
	 * Regular polygon with vertices A and B and n total vertices.
	 * The labels name the polygon itself, its segments and points
	 */
	final public GeoElement [] RegularPolygon(String [] labels, GeoPoint A, GeoPoint B, NumberValue n) {
		AlgoPolygonRegular algo = new AlgoPolygonRegular(cons, labels, A, B, n);
		return algo.getOutput();
	}
	
	/** 
	 * Creates new point B with distance n from A and  new segment AB 
	 * The labels[0] is for the segment, labels[1] for the new point	 
	 */
	final public GeoElement [] Segment (String [] labels, GeoPoint A, NumberValue n) {
		// this is actually a macro
		String pointLabel = null, segmentLabel = null;
		if (labels != null) {
			switch (labels.length) {
				case 2:
					pointLabel = labels[1];
					
				case 1:
					segmentLabel = labels[0];
					
				default:
			}
		}
		
		// create a circle around A with radius n
		AlgoCirclePointRadius algoCircle = new AlgoCirclePointRadius(cons, A, n);
		cons.removeFromConstructionList(algoCircle);
		// place the new point on the circle
		AlgoPointOnPath algoPoint = new AlgoPointOnPath(cons, pointLabel, algoCircle.getCircle(), A.inhomX+ n.getDouble(), A.inhomY );
		
		// return segment and new point
		GeoElement [] ret = { Segment(segmentLabel, A, algoPoint.getP()),
											algoPoint.getP() };
		return ret;		
	}
	
	/** 
	 * Creates a new point C by rotating B around A using angle alpha and
	 * a new angle BAC. 
	 * The labels[0] is for the angle, labels[1] for the new point	 
	 */
	final public GeoElement [] Angle (String [] labels, GeoPoint B, GeoPoint A, NumberValue alpha) {
		return Angle(labels, B, A, alpha, true);	
	}
	
	/** 
	 * Creates a new point C by rotating B around A using angle alpha and
	 * a new angle BAC (for positive orientation) resp. angle CAB (for negative orientation). 
	 * The labels[0] is for the angle, labels[1] for the new point	 
	 */
	final public GeoElement [] Angle (String [] labels, GeoPoint B, GeoPoint A, NumberValue alpha, boolean posOrientation) {
		// this is actually a macro
		String pointLabel = null, angleLabel = null;
		if (labels != null) {
			switch (labels.length) {
				case 2:
					pointLabel = labels[1];
					
				case 1:
					angleLabel = labels[0];
					
				default:
			}
		}
		
		// rotate B around A using angle alpha
		GeoPoint C = (GeoPoint) Rotate(pointLabel, B, alpha, A)[0];
		
		// create angle according to orientation
		GeoAngle angle;
		if (posOrientation) {
			angle = Angle(angleLabel, B, A, C);
		} else {
			angle = Angle(angleLabel, C, A, B);
		}
		
		//return angle and new point
		GeoElement [] ret = { angle, C };
		return ret;		
	}

	/** 
	 * Angle named label between line g and line h
	 */
	final public GeoAngle Angle(String label, GeoLine g, GeoLine h) {
		AlgoAngleLines algo = new AlgoAngleLines(cons, label, g, h);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/** 
	 * Angle named label between vector v and vector w
	 */
	final public GeoAngle Angle(
		String label,
		GeoVector v,
		GeoVector w) {
		AlgoAngleVectors algo = new AlgoAngleVectors(cons, label, v, w);
		GeoAngle angle = algo.getAngle();
		return angle;
	}
	
	/** 
	 * Angle named label for a point or a vector
	 */
	final public GeoAngle Angle(
		String label,
		GeoVec3D v) {
		AlgoAngleVector algo = new AlgoAngleVector(cons, label, v);
		GeoAngle angle = algo.getAngle();
		return angle;
	}


	/** 
	 * Angle named label between three points
	 */
	final public GeoAngle Angle(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoAnglePoints algo = new AlgoAnglePoints(cons, label, A, B, C);
		GeoAngle angle = algo.getAngle();
		return angle;
	}
	
	/** 
	 * all angles of given polygon
	 */
	final public GeoAngle [] Angles(String [] labels, GeoPolygon poly) {
		AlgoAnglePolygon algo = new AlgoAnglePolygon(cons, labels, poly);
		GeoAngle [] angles = algo.getAngles();
		//for (int i=0; i < angles.length; i++) {
		//	angles[i].setAlphaValue(0.0f);
		//}
		return angles;
	}

	/** 
	 * IntersectLines yields intersection point named label of lines g, h
	 */
	final public GeoPoint IntersectLines(
		String label,
		GeoLine g,
		GeoLine h) {
		AlgoIntersectLines algo = new AlgoIntersectLines(cons, label, g, h);
		GeoPoint S = algo.getPoint();
		return S;
	}
	
	/** 
	 * Intersects f and g using starting point A (with Newton's root finding)
	 */
	final public GeoPoint IntersectFunctions(
			String label,
			GeoFunction f,
			GeoFunction g, GeoPoint A) {
		AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(cons, label, f, g, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}
	
	/** 
	 * Intersects f and l using starting point A (with Newton's root finding)
	 */
	final public GeoPoint IntersectFunctionLine(
			String label,
			GeoFunction f,
			GeoLine l, GeoPoint A) {
				
		AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(cons, label, f, l, A);
		GeoPoint S = algo.getIntersectionPoint();
		return S;
	}
	
	/*********************************************
	 * CONIC PART
	 *********************************************/

	/** 
	 * circle with midpoint M and radius r
	 */
	final public GeoConic Circle(
		String label,
		GeoPoint M,
		NumberValue r) {
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, M, r);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint M through point P
	 */
	final public GeoConic Circle(String label, GeoPoint M, GeoPoint P) {
		AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, label, M, P);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}
	
	/** 
	 * semicircle with midpoint M through point P
	 */
	final public GeoConicPart Semicircle(String label, GeoPoint M, GeoPoint P) {
		AlgoSemicircle algo = new AlgoSemicircle(cons, label, M, P);
		return algo.getSemicircle();
	}
	
	/** 
	 * locus line for Q dependent on P. Note: P must be a point
	 * on a path.
	 */
	final public GeoLocus Locus(String label, GeoPoint Q, GeoPoint P) {		
		if (P.getPath() == null || 
			Q.getPath() != null || 
			!P.isParentOf(Q)) return null;
		AlgoLocus algo = new AlgoLocus(cons, label, Q, P);
		return algo.getLocus();
	}

	/** 
	 * circle with through points A, B, C
	 */
	final public GeoConic Circle(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoCircleThreePoints algo = new AlgoCircleThreePoints(cons, label, A, B, C);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}
	
	/** 
	 * conic arc from conic and parameters
	 */
	final public GeoConicPart ConicArc(String label, GeoConic conic, NumberValue a, NumberValue b) {
		AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(cons, label, conic, a, b, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * conic sector from conic and points
	 */
	final public GeoConicPart ConicArc(String label, GeoConic conic, GeoPoint P, GeoPoint Q) {
		AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons, label, conic, P, Q, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * conic sector from conic and parameters
	 */
	final public GeoConicPart ConicSector(String label, GeoConic conic, NumberValue a, NumberValue b) {
		AlgoConicPartConicParameters algo = new AlgoConicPartConicParameters(cons, label, conic, a, b, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}
	
	/** 
	 * conic sector from conic and points
	 */
	final public GeoConicPart ConicSector(String label, GeoConic conic, GeoPoint P, GeoPoint Q) {
		AlgoConicPartConicPoints algo = new AlgoConicPartConicPoints(cons, label, conic, P, Q, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}
	
	/** 
	 * circle arc from three points
	 */
	final public GeoConicPart CircumcircleArc(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * circle sector from three points
	 */
	final public GeoConicPart CircumcircleSector(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}
	
	/** 
	 * circle arc from center and twho points on arc
	 */
	final public GeoConicPart CircleArc(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_ARC);
		return algo.getConicPart();
	}
	
	/** 
	 * circle sector from center and twho points on arc
	 */
	final public GeoConicPart CircleSector(String label, GeoPoint A, GeoPoint B, GeoPoint C) {
		AlgoConicPartCircle algo = new AlgoConicPartCircle(cons, label, A,B, C, 															
				GeoConicPart.CONIC_PART_SECTOR);
		return algo.getConicPart();
	}

	/** 
	 * Focuses of conic. returns 2 GeoPoints
	 */
	final public GeoPoint[] Focus(String[] labels, GeoConic c) {
		AlgoFocus algo = new AlgoFocus(cons, labels, c);
		GeoPoint[] focus = algo.getFocus();
		return focus;
	}

	/** 
	 * Vertices of conic. returns 4 GeoPoints
	 */
	final public GeoPoint[] Vertex(String[] labels, GeoConic c) {
		AlgoVertex algo = new AlgoVertex(cons, labels, c);
		GeoPoint[] vertex = algo.getVertex();
		return vertex;
	}

	/** 
	 * Center of conic
	 */
	final public GeoPoint Center(String label, GeoConic c) {
		AlgoCenterConic algo = new AlgoCenterConic(cons, label, c);
		GeoPoint midpoint = algo.getPoint();
		return midpoint;
	}
	
	/** 
	 * Centroid of a 
	 */
	final public GeoPoint Centroid(String label, GeoPolygon p) {
		AlgoCentroidPolygon algo = new AlgoCentroidPolygon(cons, label, p);
		GeoPoint centroid = algo.getPoint();
		return centroid;
	}
	
	/** 
	 * Corner of image
	 */
	final public GeoPoint Corner(String label, GeoImage img, NumberValue number) {
		AlgoImageCorner algo = new AlgoImageCorner(cons, label, img, number);	
		return algo.getCorner();
	}

	/** 
	 * parabola with focus F and line l
	 */
	final public GeoConic Parabola(
		String label,
		GeoPoint F,
		GeoLine l) {
		AlgoParabolaPointLine algo = new AlgoParabolaPointLine(cons, label, F, l);
		GeoConic parabola = algo.getParabola();
		return parabola;
	}

	/** 
	 * ellipse with foci A, B and length of first half axis a
	 */
	final public GeoConic Ellipse(
		String label,
		GeoPoint A,
		GeoPoint B,
		NumberValue a) {
		AlgoEllipseFociLength algo = new AlgoEllipseFociLength(cons, label, A, B, a);
		GeoConic ellipse = algo.getEllipse();
		return ellipse;
	}

	/** 
	 * hyperbola with foci A, B and length of first half axis a
	 */
	final public GeoConic Hyperbola(
		String label,
		GeoPoint A,
		GeoPoint B,
		NumberValue a) {
		AlgoHyperbolaFociLength algo =
			new AlgoHyperbolaFociLength(cons, label, A, B, a);
		GeoConic hyperbola = algo.getHyperbola();
		return hyperbola;
	}

	/** 
	 * conic through five points
	 */
	final public GeoConic Conic(String label, GeoPoint[] points) {
		AlgoConicFivePoints algo = new AlgoConicFivePoints(cons, label, points);
		GeoConic conic = algo.getConic();
		return conic;
	}

	/** 
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 */
	final public GeoPoint[] IntersectLineConic(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();		
		GeoElement.setLabels(labels, points);	
		return points;
	}

	/** 
	 * IntersectConics yields intersection points named label1, label2, label3, label4
	 * of conics c1, c2
	 */
	final public GeoPoint[] IntersectConics(
		String[] labels,
		GeoConic a,
		GeoConic b) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
		algo.setPrintedInXML(true);
		GeoPoint[] points = algo.getIntersectionPoints();
		GeoElement.setLabels(labels, points);
		return points;
	}
	
	/** 
	 * IntersectPolynomials yields all intersection points 
	 * of polynomials a, b
	 */
	final public GeoPoint[] IntersectPolynomials(String[] labels, GeoFunction a, GeoFunction b) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;			
			
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();		
		return points;
	}
	
	/** 
	 * get only one intersection point of two polynomials a, b 
	 * that is near to the given location (xRW, yRW)	 
	 */
	final public GeoPoint IntersectPolynomialsSingle(
		String label, GeoFunction a, GeoFunction b, 
		double xRW, double yRW) 
	{
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;
			
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);		
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two polynomials a, b 
	 * with given index	 
	 */
	final public GeoPoint IntersectPolynomialsSingle(
		String label,
		GeoFunction a,
		GeoFunction b, NumberValue index) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) return null;
		
		AlgoIntersectPolynomials algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * IntersectPolyomialLine yields all intersection points
	 * of polynomial f and line l
	 */
	final public GeoPoint[] IntersectPolynomialLine(
			String[] labels,		
			GeoFunction f,
			GeoLine l) {
				
		if (!f.isPolynomialFunction(false))
			return null;			
			
		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		algo.setPrintedInXML(true);
		algo.setLabels(labels);
		GeoPoint[] points = algo.getIntersectionPoints();		
		return points;
	}
	
	/** 
	 * one intersection point of polynomial f and line l near to (xRW, yRW)
	 */
	final public GeoPoint IntersectPolynomialLineSingle(
			String label,		
			GeoFunction f,
			GeoLine l, double xRW, double yRW) {
		if (!f.isPolynomialFunction(false)) return null;	
			
		AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);
		int index = algo.getClosestPointIndex(xRW, yRW);		
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;		
	}	
	
	/** 
	 * get only one intersection point of a line and a function 
	 */
	final public GeoPoint IntersectPolynomialLineSingle(
			String label,		
		GeoFunction f,
		GeoLine l, NumberValue index) {
			if (!f.isPolynomialFunction(false)) return null;	
			
			AlgoIntersectPolynomialLine algo = getIntersectionAlgorithm(f, l);		
			AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
			GeoPoint point = salgo.getPoint();
			return point;	
	}	
	
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint IntersectLineConicSingle(
		String label,
		GeoLine g,
		GeoConic c, double xRW, double yRW) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);
		int index = algo.getClosestPointIndex(xRW, yRW);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	
	/** 
	 * get only one intersection point of a line and a conic 
	 */
	final public GeoPoint IntersectLineConicSingle(
		String label,
		GeoLine g,
		GeoConic c, NumberValue index) {
		AlgoIntersectLineConic algo = getIntersectionAlgorithm(g, c);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two conics that is near to the given
	 * location (xRW, yRW)
	 */
	final public GeoPoint IntersectConicsSingle(
		String label,
		GeoConic a,
		GeoConic b, double xRW, double yRW) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);
		int index = algo.getClosestPointIndex(xRW, yRW) ; 				
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/** 
	 * get only one intersection point of two conics 
	 */
	final public GeoPoint IntersectConicsSingle(
			String label, GeoConic a, GeoConic b, NumberValue index) {
		AlgoIntersectConics algo = getIntersectionAlgorithm(a, b);		// index - 1 to start at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, (int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	
	/*
	 * to avoid multiple calculations of the intersection points of the same
	 * two objects, we remember all the intersection algorithms created
	 */
	 private ArrayList intersectionAlgos = new ArrayList();
	 
	 // intersect line and conic
	 AlgoIntersectLineConic getIntersectionAlgorithm(GeoLine g, GeoConic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) return (AlgoIntersectLineConic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersect conics
	 AlgoIntersectConics getIntersectionAlgorithm(GeoConic a, GeoConic b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) return (AlgoIntersectConics) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersection of polynomials
	 AlgoIntersectPolynomials getIntersectionAlgorithm(GeoFunction a, GeoFunction b) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, b);
		if (existingAlgo != null) return (AlgoIntersectPolynomials) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomials algo = new AlgoIntersectPolynomials(cons, a, b);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	 
	 // intersection of polynomials
	 AlgoIntersectPolynomialLine getIntersectionAlgorithm(GeoFunction a, GeoLine l) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(a, l);
		if (existingAlgo != null) return (AlgoIntersectPolynomialLine) existingAlgo;
		
		// we didn't find a matching algorithm, so create a new one
		AlgoIntersectPolynomialLine algo = new AlgoIntersectPolynomialLine(cons, a, l);
		algo.setPrintedInXML(false);
		intersectionAlgos.add(algo); // remember this algorithm
		return algo;
	 }
	  
	 private AlgoElement findExistingIntersectionAlgorithm(GeoElement a, GeoElement b) {
		int size = intersectionAlgos.size();
		AlgoElement algo;
		for (int i=0; i < size; i++) {
			algo = (AlgoElement) intersectionAlgos.get(i);
			GeoElement [] input = algo.getInput();
			if (a == input[0] && b == input[1] ||
				 a == input[1] && b == input[0])
				// we found an existing intersection algorithm
				return algo;
		}
		return null;
	 }
	 
	 void removeIntersectionAlgorithm(AlgoIntersect algo) {
		intersectionAlgos.remove(algo);	 
	 }


	/** 
	 * polar line to P relativ to c
	 */
	final public GeoLine PolarLine(
		String label,
		GeoPoint P,
		GeoConic c) {
		AlgoPolarLine algo = new AlgoPolarLine(cons, label, c, P);
		GeoLine polar = algo.getLine();
		return polar;
	}

	/** 
	 * diameter line conjugate to direction of g relative to c
	 */
	final public GeoLine DiameterLine(
		String label,
		GeoLine g,
		GeoConic c) {
		AlgoDiameterLine algo = new AlgoDiameterLine(cons, label, c, g);
		GeoLine diameter = algo.getDiameter();
		return diameter;
	}

	/** 
	 * diameter line conjugate to v relative to c
	 */
	final public GeoLine DiameterLine(
		String label,
		GeoVector v,
		GeoConic c) {
		AlgoDiameterVector algo = new AlgoDiameterVector(cons, label, c, v);
		GeoLine diameter = algo.getDiameter();
		return diameter;
	}

	/** 
	 * tangents to c through P
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoPoint P,
		GeoConic c) {
		AlgoTangentPoint algo = new AlgoTangentPoint(cons, labels, P, c);
		GeoLine[] tangents = algo.getTangents();		
		return tangents;
	}

	/** 
	 * tangents to c parallel to g
	 */
	final public GeoLine[] Tangent(
		String[] labels,
		GeoLine g,
		GeoConic c) {
		AlgoTangentLine algo = new AlgoTangentLine(cons, labels, g, c);
		GeoLine[] tangents = algo.getTangents();
		return tangents;
	}

	/** 
	 * tangent to f in x = a
	 */
	final public GeoLine Tangent(
		String label,
		NumberValue a,
		GeoFunction f) {
		AlgoTangentFunctionNumber algo =
			new AlgoTangentFunctionNumber(cons, label, a, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();   
		notifyUpdate(t);  
		return t;
	}

	/** 
	 * tangent to f in x = x(P)
	 */
	final public GeoLine Tangent(
		String label,
		GeoPoint P,
		GeoFunction f) {
		AlgoTangentFunctionPoint algo =
			new AlgoTangentFunctionPoint(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();     
		notifyUpdate(t);
		return t;
	}
	
	/** 
	 * asymptotes to c
	 */
	final public GeoLine[] Asymptote(String[] labels, GeoConic c) {
		AlgoAsymptote algo = new AlgoAsymptote(cons, labels, c);
		GeoLine[] asymptotes = algo.getAsymptotes();
		return asymptotes;
	}

	/** 
	 * axes of c
	 */
	final public GeoLine[] Axes(String[] labels, GeoConic c) {
		AlgoAxes algo = new AlgoAxes(cons, labels, c);
		GeoLine[] axes = algo.getAxes();
		return axes;
	}

	/** 
	 * first axis of c
	 */
	final public GeoLine FirstAxis(String label, GeoConic c) {
		AlgoAxisFirst algo = new AlgoAxisFirst(cons, label, c);
		GeoLine axis = algo.getAxis();
		return axis;
	}

	/** 
	 * second axis of c
	 */
	final public GeoLine SecondAxis(String label, GeoConic c) {
		AlgoAxisSecond algo = new AlgoAxisSecond(cons, label, c);
		GeoLine axis = algo.getAxis();
		return axis;
	}

	/** 
	 * directrix of c
	 */
	final public GeoLine Directrix(String label, GeoConic c) {
		AlgoDirectrix algo = new AlgoDirectrix(cons, label, c);
		GeoLine directrix = algo.getDirectrix();
		return directrix;
	}

	/** 
	 * excentricity of c
	 */
	final public GeoNumeric Excentricity(String label, GeoConic c) {
		AlgoExcentricity algo = new AlgoExcentricity(cons, label, c);
		GeoNumeric excentricity = algo.getExcentricity();
		return excentricity;
	}

	/** 
	 * first axis' length of c
	 */
	final public GeoNumeric FirstAxisLength(String label, GeoConic c) {
		AlgoAxisFirstLength algo = new AlgoAxisFirstLength(cons, label, c);
		GeoNumeric length = algo.getLength();
		return length;
	}

	/** 
	 * second axis' length of c
	 */
	final public GeoNumeric SecondAxisLength(String label, GeoConic c) {
		AlgoAxisSecondLength algo = new AlgoAxisSecondLength(cons, label, c);
		GeoNumeric length = algo.getLength();
		return length;
	}

	/** 
	 * (parabola) parameter of c
	 */
	final public GeoNumeric Parameter(String label, GeoConic c) {
		AlgoParabolaParameter algo = new AlgoParabolaParameter(cons, label, c);
		GeoNumeric length = algo.getParameter();
		return length;
	}

	/** 
	 * (circle) radius of c
	 */
	final public GeoNumeric Radius(String label, GeoConic c) {
		AlgoRadius algo = new AlgoRadius(cons, label, c);
		GeoNumeric length = algo.getRadius();
		return length;
	}

	/** 
	 * angle of c (angle between first eigenvector and (1,0))
	 */
	final public GeoAngle Angle(String label, GeoConic c) {
		AlgoAngleConic algo = new AlgoAngleConic(cons, label, c);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/********************************************************************
	 * TRANSFORMATIONS
	 ********************************************************************/

	/**
	 * translate geoTrans by vector v
	 */
	final public GeoElement [] Translate(String label, Translateable geoTrans, GeoVector v) {
		if (geoTrans.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoTrans).createTransformedObject(TRANSFORM_TRANSLATE, label, null, null, v, null); 
		
		// standard case
		AlgoTranslate algo = new AlgoTranslate(cons, label, geoTrans, v);			
		GeoElement [] geos = {algo.getResult()};
		return geos;				
	}
	
	/**
	 * translates vector v to point A. The resulting vector is equal
	 * to v and has A as startPoint
	 */
	final public GeoVector Translate(String label, GeoVector v, GeoPoint A) {
		AlgoTranslateVector algo = new AlgoTranslateVector(cons, label, v, A);
		GeoVector vec = algo.getTranslatedVector();
		return vec;
	}	

	/**
	 * rotate geoRot by angle phi around (0,0)
	 */
	final public GeoElement [] Rotate(String label, Rotateable geoRot, NumberValue phi) {
		if (geoRot.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoRot).createTransformedObject(TRANSFORM_ROTATE, label, null, null, null, phi);
		
		// standard case
		AlgoRotate algo = new AlgoRotate(cons, label, geoRot, phi);				
		GeoElement [] geos = {algo.getResult()};
		return geos;					
	}


	/**
	 * rotate geoRot by angle phi around Q
	 */
	final public GeoElement [] Rotate(String label, PointRotateable geoRot, NumberValue phi, GeoPoint Q) {
		if (geoRot.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoRot).createTransformedObject(TRANSFORM_ROTATE_AROUND_POINT, label, Q, null, null, phi);
		
		// standard case
		AlgoRotatePoint algo = new AlgoRotatePoint(cons, label, geoRot, phi, Q);			
		GeoElement [] geos = {algo.getResult()};
		return geos;		
	}
		
	/**
	 * dilate geoRot by r from S
	 */
	final public GeoElement [] Dilate(String label, Dilateable geoRot, NumberValue r, GeoPoint S) {
		if (geoRot.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoRot).createTransformedObject(TRANSFORM_DILATE, label, S, null, null, r);
		
		// standard case
		AlgoDilate algo = new AlgoDilate(cons, label, geoRot, r, S);			
		GeoElement [] geos = {algo.getResult()};
		return geos;	
	}

	/**
	 * mirror geoMir at point Q
	 */
	final public GeoElement [] Mirror(String label, Mirrorable geoMir, GeoPoint Q) {		
		if (geoMir.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoMir).createTransformedObject(TRANSFORM_MIRROR_AT_POINT, label, Q, null, null, null);
		
		// standard case
		AlgoMirror algo = new AlgoMirror(cons, label, geoMir, Q);		
		GeoElement [] geos = {algo.getResult()};
		return geos;	
	}

	/**
	 * mirror geoMir at line g
	 */
	final public GeoElement [] Mirror(String label, Mirrorable geoMir, GeoLine g) {
		if (geoMir.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return  ((LimitedPath) geoMir).createTransformedObject(TRANSFORM_MIRROR_AT_LINE, label, null, g, null, null);
		
		// standard case
		AlgoMirror algo = new AlgoMirror(cons, label, geoMir, g);		
		GeoElement [] geos = {algo.getResult()};
		return geos;				
	}			
	
	/* ******************************
	 * Transformations for polygons 
	 * ******************************/
	
	/**
	 * translate poly by vector v
	 */
	final public GeoElement [] Translate(String label, GeoPolygon poly, GeoVector v) {
		return transformPoly(label, poly, translatePoints(poly.getPoints(), v));
	}	
	
	GeoPoint [] translatePoints(GeoPoint [] points, GeoVector v) {		
		// rotate all points
		GeoPoint [] newPoints = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = points[i].isLabelSet() ? points[i].label + "'" : null;
			newPoints[i] = (GeoPoint) Translate(pointLabel, points[i], v)[0]; 				
		}			
		return newPoints;
	}
	
	/**
	 * rotates poly by angle phi around (0,0)
	 */
	final public GeoElement [] Rotate(String label, GeoPolygon poly, NumberValue phi) {
		return transformPoly(label, poly, rotPoints(poly.getPoints(), phi, null));
	}
	
	/**
	 * rotates poly by angle phi around Q
	 */
	final public GeoElement [] Rotate(String label,	GeoPolygon poly, NumberValue phi, GeoPoint Q) {		
		return transformPoly(label, poly, rotPoints(poly.getPoints(), phi, Q));
	}
				
	GeoPoint [] rotPoints(GeoPoint [] points, NumberValue phi, GeoPoint Q) {		
		// rotate all points
		GeoPoint [] rotPoints = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = points[i].isLabelSet() ? points[i].label + "'" : null;
			if (Q == null)
				rotPoints[i] = (GeoPoint) Rotate(pointLabel, points[i], phi)[0]; 	
			else
				rotPoints[i] = (GeoPoint) Rotate(pointLabel, points[i], phi, Q)[0];
		}			
		return rotPoints;
	}
		
	/**
	 * dilate geoRot by r from S
	 */
	final public GeoElement [] Dilate(String label, GeoPolygon poly, NumberValue r, GeoPoint S) {
		return transformPoly(label, poly, dilatePoints(poly.getPoints(), r, S));
	}
	
	GeoPoint [] dilatePoints(GeoPoint [] points, NumberValue r, GeoPoint S) {		
		// dilate all points
		GeoPoint [] newPoints = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = points[i].isLabelSet() ? points[i].label + "'"  : null;
			newPoints[i] = (GeoPoint) Dilate(pointLabel, points[i], r, S)[0];			
		}			
		return newPoints;
	}	

	/**
	 * mirror geoMir at point Q
	 */
	final public GeoElement [] Mirror(String label, GeoPolygon poly, GeoPoint Q) {
		return transformPoly(label, poly, mirrorPoints(poly.getPoints(), Q, null));	
	}

	/**
	 * mirror geoMir at line g
	 */
	final public GeoElement [] Mirror(String label, GeoPolygon poly, GeoLine g) {
		return transformPoly(label, poly, mirrorPoints(poly.getPoints(), null, g));	
	}	
	
	GeoPoint [] mirrorPoints(GeoPoint [] points, GeoPoint Q, GeoLine g) {		
		// mirror all points
		GeoPoint [] newPoints = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			String pointLabel = points[i].isLabelSet() ? points[i].label + "'" : null;
			if (Q == null)
				newPoints[i] = (GeoPoint) Mirror(pointLabel, points[i], g)[0]; 	
			else
				newPoints[i] = (GeoPoint) Mirror(pointLabel, points[i], Q)[0];
		}			
		return newPoints;
	}		
	
	private GeoElement [] transformPoly(String label, GeoPolygon oldPoly, GeoPoint [] transformedPoints) {
		// get label for polygon
		String [] polyLabel = null;		
		if (label == null) {
			if (oldPoly.isLabelSet()) {		
				polyLabel = new String[1];
				polyLabel[0] = oldPoly.label + "'";
			}			
		} else {
			polyLabel = new String[1];
			polyLabel[0] = label;
		}
	
		// build the polygon from the transformed points
		return Polygon(polyLabel, transformedPoints);
	}			    
	
	static final int TRANSFORM_TRANSLATE = 0;
	static final int TRANSFORM_MIRROR_AT_POINT = 1;
	static final int TRANSFORM_MIRROR_AT_LINE = 2;	
	static final int TRANSFORM_ROTATE = 3;
	static final int TRANSFORM_ROTATE_AROUND_POINT = 4;
	static final int TRANSFORM_DILATE = 5;
	
	GeoPoint [] transformPoints(int type, GeoPoint [] points, GeoPoint Q, GeoLine l, GeoVector vec, NumberValue n) {
		switch (type) {
			case TRANSFORM_TRANSLATE:
				return translatePoints(points, vec);			
			case TRANSFORM_MIRROR_AT_POINT:
				return mirrorPoints(points, Q, null);			
			case TRANSFORM_MIRROR_AT_LINE:	
				return mirrorPoints(points, null, l);		
			case TRANSFORM_ROTATE:
				return rotPoints(points, n, null);							
			case TRANSFORM_ROTATE_AROUND_POINT:
				return rotPoints(points, n, Q);			
			case TRANSFORM_DILATE:
				return dilatePoints(points, n, Q);	
			default:
				return null;
		}
	}	
	
	GeoLine getTransformedLine(int type, GeoLine line, GeoPoint Q, GeoLine l, GeoVector vec, NumberValue n) {
		switch (type) {
			case Kernel.TRANSFORM_TRANSLATE:
				AlgoTranslate algoTrans = new AlgoTranslate(cons, line, vec);			
				return (GeoLine) algoTrans.getResult();							
				
			case Kernel.TRANSFORM_MIRROR_AT_POINT:
			case Kernel.TRANSFORM_MIRROR_AT_LINE:	
				AlgoMirror algoMirror = new AlgoMirror(cons, line, l, Q);			
				return (GeoLine) algoMirror.getResult();			
						
			case Kernel.TRANSFORM_ROTATE:
				AlgoRotate algoRotate = new AlgoRotate(cons, line, n);			
				return (GeoLine) algoRotate.getResult();			
			
			case Kernel.TRANSFORM_ROTATE_AROUND_POINT:
				AlgoRotatePoint algoRotatePoint = new AlgoRotatePoint(cons, line, n, Q);			
				return (GeoLine) algoRotatePoint.getResult();						
			
			case Kernel.TRANSFORM_DILATE:
				AlgoDilate algoDilate = new AlgoDilate(cons, line, n, Q);			
				return (GeoLine) algoDilate.getResult();						
			
			default:
				return null;
		}
	}	
	
	GeoConic getTransformedConic(int type, GeoConic conic, GeoPoint Q, GeoLine l, GeoVector vec, NumberValue n) {
		switch (type) {
			case Kernel.TRANSFORM_TRANSLATE:
				AlgoTranslate algoTrans = new AlgoTranslate(cons, conic, vec);			
				return (GeoConic) algoTrans.getResult();							
				
			case Kernel.TRANSFORM_MIRROR_AT_POINT:
			case Kernel.TRANSFORM_MIRROR_AT_LINE:	
				AlgoMirror algoMirror = new AlgoMirror(cons, conic, l, Q);			
				return (GeoConic) algoMirror.getResult();			
						
			case Kernel.TRANSFORM_ROTATE:
				AlgoRotate algoRotate = new AlgoRotate(cons, conic, n);			
				return (GeoConic) algoRotate.getResult();			
			
			case Kernel.TRANSFORM_ROTATE_AROUND_POINT:
				AlgoRotatePoint algoRotatePoint = new AlgoRotatePoint(cons, conic, n, Q);			
				return (GeoConic) algoRotatePoint.getResult();						
			
			case Kernel.TRANSFORM_DILATE:
				AlgoDilate algoDilate = new AlgoDilate(cons, conic, n, Q);			
				return (GeoConic) algoDilate.getResult();						
			
			default:
				return null;
		}
	}	

	/***********************************
	 * CALCULUS
	 ***********************************/
	
	/** function limited to interval [a, b]
	 */
	final public GeoFunction Function(String label, GeoFunction f, 
										NumberValue a, NumberValue b) {
		AlgoFunctionInterval algo = new AlgoFunctionInterval(cons, label, f, a, b);		
		GeoFunction g = algo.getFunction();
		return g;
	}
	
	/**
	 * first derivative of deriveable f
	 */
	final public GeoElement Derivative(
		String label,
		GeoDeriveable f) {
		
		AlgoDerivative algo = new AlgoDerivative(cons, label, f);
		return algo.getDerivative();				
	}	
	
	/**
	 * n-th derivative of deriveable f
	 */
	final public GeoElement Derivative(
		String label,
		GeoDeriveable f,
		NumberValue n) {
		
		AlgoDerivative algo = new AlgoDerivative(cons, label, f, n);
		return algo.getDerivative();	
	}			
	
	/**
	 * Tries to expand a function f to a polynomial.
	 */
	final public GeoFunction PolynomialFunction(String label, GeoFunction f) {		
		AlgoPolynomialFromFunction algo = new AlgoPolynomialFromFunction(cons, label, f);
		return algo.getPolynomial();			
	}
	
	/**
	 * Taylor series of function f about point x=a of order n
	 */
	final public GeoFunction TaylorSeries(
		String label,
		GeoFunction f,
		NumberValue a, 
		NumberValue n) {
		
		AlgoTaylorSeries algo = new AlgoTaylorSeries(cons, label, f, a, n);
		return algo.getPolynomial();
	}

	/**
	 * Integral of function f
	 */
	final public GeoFunction Integral(String label, GeoFunction f) {
		AlgoIntegral algo = new AlgoIntegral(cons, label, f);
		GeoFunction g = algo.getIntegral();
		return g;
	}
	
	/**
	 * definite Integral of function f from x=a to x=b
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, NumberValue a, NumberValue b) {
		AlgoIntegralDefinite algo = new AlgoIntegralDefinite(cons, label, f, a, b);
		GeoNumeric n = algo.getIntegral();
		return n;
	}

	/** 
	 * definite integral of function (f - g) in interval [a, b]
	 */
	final public GeoNumeric Integral(String label, GeoFunction f, GeoFunction g,
												NumberValue a, NumberValue b) {
		AlgoIntegralFunctions algo = new AlgoIntegralFunctions(cons, label, f, g, a, b);
		GeoNumeric num = algo.getIntegral();
		return num;
	}		
	
	/**
	 * all Roots of polynomial f (works only for polynomials and functions
	 * that can be simplified to factors of polynomials, e.g. sqrt(x) to x)
	 */
	final public GeoPoint [] Root(String [] labels, GeoFunction f) {
		// allow functions that can be simplified to factors of polynomials
		if (!f.isPolynomialFunction(true)) return null;
		
		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	
	/**
	 * Root of a function f to given start value a (works only if first derivative of f exists)
	 */
	final public GeoPoint Root(String label, GeoFunction f, NumberValue a) {			 
		AlgoRootNewton algo = new AlgoRootNewton(cons, label, f, a);
		GeoPoint p = algo.getRootPoint();
		return p;
	}	

	/**
	 * Root of a function f in given interval [a, b]
	 */
	final public GeoPoint Root(String label, GeoFunction f, NumberValue a, NumberValue b) {			 
		AlgoRootInterval algo = new AlgoRootInterval(cons, label, f, a, b);
		GeoPoint p = algo.getRootPoint();
		return p;
	}	

	
	/**
	 * all Extrema of function f (works only for polynomials)
	 */
	final public GeoPoint [] Extremum(String [] labels, GeoFunction f) {
		//	check if this is a polynomial at the moment
		if (!f.isPolynomialFunction(true)) return null;
			 
		AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	
	/**
	 * all Turning points of function f (works only for polynomials)
	 */
	final public GeoPoint [] TurningPoint(String [] labels, GeoFunction f) {
		//	check if this is a polynomial at the moment
		if (!f.isPolynomialFunction(true)) return null;
			 
		AlgoTurningPointPolynomial algo = new AlgoTurningPointPolynomial(cons, labels, f);
		GeoPoint [] g = algo.getRootPoints();
		return g;
	}	
	/**
	 * Victor Franco Espino 18-04-2007: New commands
	 *
	 * Calculate affine ratio: (A,B,C) = (t(C)-t(A)) : (t(C)-t(B)) 
	 */

	final public GeoNumeric AffineRatio(String label, GeoPoint A, GeoPoint B,
			GeoPoint C) {
		AlgoAffineRatio affine = new AlgoAffineRatio(cons, label, A, B, C);
		GeoNumeric M = affine.getResult();
		return M;

	}

	  

	/**
	 * Calculate cross ratio: (A,B,C,D) = affineRatio(A, B, C) / affineRatio(A, B, D)
	 */

	final public GeoNumeric CrossRatio(String label,GeoPoint A,GeoPoint B,GeoPoint C,GeoPoint D){

		  AlgoCrossRatio cross = new AlgoCrossRatio(cons,label,A,B,C,D);
		  GeoNumeric M = cross.getResult();
		  return M;

	}

	

	/**
	 * Calculate Curvature Vector for function: c(x) = (1/T^4)*(-f'*f'',f''), T = sqrt(1+(f')^2)
	 */

	final public GeoVector CurvatureVector(String label,GeoPoint A,GeoFunction f){

		  AlgoCurvatureVector algo = new AlgoCurvatureVector(cons,label,A,f);
		  GeoVector v = algo.getVector();
		  return v;

	}



	/**

	 * Calculate Curvature Vector for curve: c(t) = ((a'(t)b''(t)-a''(t)b'(t))/T^4) * (-b'(t),a'(t))
     *                                       T = sqrt(a'(t)^2+b'(t)^2)
	 */

	final public GeoVector CurvatureVectorCurve(String label,GeoPoint A,GeoCurveCartesian f){

		  AlgoCurvatureVectorCurve algo = new AlgoCurvatureVectorCurve(cons,label,A,f);
		  GeoVector v = algo.getVector();
		  return v;

	}

	

	/**
	 * Calculate Curvature for function: k(x) = f''/T^3, T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric Curvature(String label,GeoPoint A,GeoFunction f){

		  AlgoCurvature algo = new AlgoCurvature(cons,label,A,f);
		  GeoNumeric k = algo.getResult();
		  return k;

	}

		

	/**
	 * Calculate Curvature for Curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
	 */

	final public GeoNumeric CurvatureCurve(String label,GeoPoint A, GeoCurveCartesian f){

		  AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons,label,A,f);
		  GeoNumeric k = algo.getResult();
		  return k;

	}

	

	/**
	 * Osculating Circle of a function f in point A
	 */

	final public GeoConic OsculatingCircle(String label,GeoPoint A,GeoFunction f){

		  AlgoOsculatingCircle algo = new AlgoOsculatingCircle(cons,label,A,f);
		  GeoConic circle = algo.getCircle();
		  return circle;

	}

	

	/**
	 * Osculating Circle of a curve f in point A
	 */

	final public GeoConic OsculatingCircleCurve(String label,GeoPoint A,GeoCurveCartesian f){

		  AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(cons,label,A,f);
		  GeoConic circle = algo.getCircle();
		  return circle;

	}

	

	/**
	 * Calculate Function Length between the numbers A and B: integral from A to B on T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric FunctionLength(String label,GeoFunction f,GeoNumeric A,GeoNumeric B){

		  AlgoLengthFunction algo = new AlgoLengthFunction(cons,label,f,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**
	 * Calculate Function Length between the points A and B: integral from A to B on T = sqrt(1+(f')^2)
	 */

	final public GeoNumeric FunctionLength2Points(String label,GeoFunction f,GeoPoint A,GeoPoint B){

		  AlgoLengthFunction2Points algo = new AlgoLengthFunction2Points(cons,label,f,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**

	 * Calculate Curve Length between the parameters t0 and t1: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)

	 */

	final public GeoNumeric CurveLength(String label, GeoCurveCartesian c, GeoNumeric t0,GeoNumeric t1){

		  AlgoLengthCurve algo = new AlgoLengthCurve(cons,label,c,t0,t1);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/**

	 * Calculate Curve Length between the points A and B: integral from t0 to t1 on T = sqrt(a'(t)^2+b'(t)^2)

	 */

	final public GeoNumeric CurveLength2Points(String label, GeoCurveCartesian c, GeoPoint A,GeoPoint B){

		  AlgoLengthCurve2Points algo = new AlgoLengthCurve2Points(cons,label,c,A,B);
		  GeoNumeric length = algo.getLength();
		  return length;

	}

	

	/** 
	 * tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
	 */

	final public GeoLine Tangent(String label,GeoPoint P,GeoCurveCartesian f) {

		AlgoTangentCurve algo = new AlgoTangentCurve(cons, label, P, f);
		GeoLine t = algo.getTangent();
		t.setToExplicit();
		t.update();     
		notifyUpdate(t);
		return t;

	}

	/**
	 * Victor Franco Espino 18-04-2007: End new commands 
	 */


	

	/***********************************
	 * PACKAGE STUFF
	 ***********************************/

	/** if x is nearly zero, 0.0 is returned,
	 *  else x is returned
	 */
	final public double chop(double x) {
		if (isZero(x))
			return 0.0d;
		else
			return x;
	}

	/** is abs(x) < epsilon ? */
	final public boolean isZero(double x) {
		return -EPSILON < x && x < EPSILON;
	}

	final boolean isZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (!isZero(a[i]))
				return false;
		}
		return true;
	}

	/**
	 * Returns whether x is equal to y	 	 
	 */
	final public boolean isEqual(double x, double y) {	
		return x - EPSILON < y && y < x + EPSILON;
	}
	
	public static boolean isEqual(double x, double y, double eps) {		
		return x - eps < y && y < x + eps;
	}
	
	/**
	 * Returns whether x is greater than y	 	 
	 */
	final public boolean isGreater(double x, double y) {
		return x > y + EPSILON;
	}
	
	/**
	 * Returns whether x is greater than or equal to y	 	 
	 */
	final public boolean isGreaterEqual(double x, double y) {
		return x + EPSILON > y;
	}

	// compares double arrays: 
	// yields true if (isEqual(a[i], b[i]) == true) for all i
	final boolean isEqual(double[] a, double[] b) {
		for (int i = 0; i < a.length; ++i) {
			if (!isEqual(a[i], b[i]))
				return false;
		}
		return true;
	}
	
	final public boolean isInteger(double x) {
		return isEqual(x, Math.round(x));		
	}
	
    final public double convertToAngleValue(double val) {
    	double value = val % PI_2; 
		if (isZero(value)) {
			if (val < 1.0) value = 0.0;
			else value = PI_2; 
		}
    	else if (value < 0.0)  {
    		value += PI_2;
    	} 
    	return value;
    }

    /*
	// calc acos(x). returns 0 for x > 1 and pi for x < -1    
	final static double trimmedAcos(double x) {
		if (Math.abs(x) <= 1.0d)
			return Math.acos(x);
		else if (x > 1.0d)
			return 0.0d;
		else if (x < -1.0d)
			return Math.PI;
		else
			return Double.NaN;
	}*/

	/** returns max of abs(a[i]) */
	final static double maxAbs(double[] a) {
		double temp, max = Math.abs(a[0]);
		for (int i = 1; i < a.length; i++) {
			temp = Math.abs(a[i]);
			if (temp > max)
				max = temp;
		}
		return max;
	}

	// copy array a to array b
	final static void copy(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i];
		}
	}

	// change signs of double array values, write result to array b
	final static void negative(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			b[i] = -a[i];
		}
	}

	// c[] = a[] / b
	final static void divide(double[] a, double b, double[] c) {
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] / b;
		}
	}
	
	// temp for buildEquation    
	private double[] temp = new double[6];

	// lhs of implicit equation without constant coeff
	final private StringBuffer buildImplicitVarPart(		
		double[] numbers,
		String[] vars, 
		boolean KEEP_LEADING_SIGN) {
		int leadingNonZero = -1;
		sbBuildImplicitVarPart.setLength(0);

		for (int i = 0; i < vars.length; i++) {
			if (!isZero(numbers[i])) {
				leadingNonZero = i;
				break;
			}
		}

		// no left hand side        
		if (leadingNonZero == -1) {
			sbBuildImplicitVarPart.append("0");
			return sbBuildImplicitVarPart;
		}
		
		// don't change leading coefficient
		if (KEEP_LEADING_SIGN) {
			copy(numbers, temp);
		} else {
			if (numbers[leadingNonZero] < 0)
				negative(numbers, temp);
			else
				copy(numbers, temp);
		}

		// BUILD EQUATION STRING                              
		// valid left hand side 
		// leading coefficient
		sbBuildImplicitVarPart.append(formatCoeff(temp[leadingNonZero]));
		sbBuildImplicitVarPart.append(vars[leadingNonZero]);

		// other coefficients on lhs
		String sign;
		double abs;
		for (int i = leadingNonZero + 1; i < vars.length; i++) {
			if (temp[i] < 0.0) {
				sign = " - ";
				abs = -temp[i];
			} else {
				sign = " + ";
				abs = temp[i];
			}

			if (abs >= PRINT_PRECISION) {
				sbBuildImplicitVarPart.append(sign);
				sbBuildImplicitVarPart.append(formatCoeff(abs));
				sbBuildImplicitVarPart.append(vars[i]);
			}
		}
		return sbBuildImplicitVarPart;
	}
	private StringBuffer sbBuildImplicitVarPart = new StringBuffer(80);

	final StringBuffer buildImplicitEquation(
		double[] numbers,
		String[] vars,
		boolean KEEP_LEADING_SIGN) {

		sbBuildImplicitEquation.setLength(0);
		sbBuildImplicitEquation.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN));
		sbBuildImplicitEquation.append(" = ");
		// temp is set by buildImplicitVarPart
		sbBuildImplicitEquation.append(format(-temp[vars.length]));

		return sbBuildImplicitEquation;
	}
	private StringBuffer sbBuildImplicitEquation = new StringBuffer(80);

	// lhs of lhs = 0
	final public StringBuffer buildLHS(double[] numbers, String[] vars, boolean KEEP_LEADING_SIGN) {
		sbBuildLHS.setLength(0);
		sbBuildLHS.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN));

		// add constant coeff
		double coeff = temp[vars.length];
		if (Math.abs(coeff) >= PRINT_PRECISION) {
			sbBuildLHS.append(' ');
			sbBuildLHS.append(sign(coeff));
			sbBuildLHS.append(' ');
			sbBuildLHS.append(format(Math.abs(coeff)));
		}
		return sbBuildLHS;
	}
	private StringBuffer sbBuildLHS = new StringBuffer(80);

	// form: yï¿½ = f(x) (coeff of y = 0)
	final StringBuffer buildExplicitConicEquation(
		double[] numbers,
		String[] vars,
		int pos,
		boolean KEEP_LEADING_SIGN) {
		// yï¿½-coeff is 0
		double d, dabs, q = numbers[pos];
		// coeff of yï¿½ is 0 or coeff of y is not 0
		if (isZero(q))
			return buildImplicitEquation(numbers, vars, KEEP_LEADING_SIGN);

		int i, leadingNonZero = numbers.length;
		for (i = 0; i < numbers.length; i++) {
			if (i != pos
				&& // except yï¿½ coefficient                
			Math.abs(numbers[i])
					>= PRINT_PRECISION) {
				leadingNonZero = i;
				break;
			}
		}

		// BUILD EQUATION STRING                              
		sbBuildExplicitConicEquation.setLength(0);
		sbBuildExplicitConicEquation.append(vars[pos]);
		sbBuildExplicitConicEquation.append(" = ");

		if (leadingNonZero == numbers.length) {
			sbBuildExplicitConicEquation.append("0");
			return sbBuildExplicitConicEquation;
		} else if (leadingNonZero == numbers.length - 1) {
			// only constant coeff
			d = -numbers[leadingNonZero] / q;
			sbBuildExplicitConicEquation.append(format(d));
			return sbBuildExplicitConicEquation;
		} else {
			// leading coeff
			d = -numbers[leadingNonZero] / q;
			sbBuildExplicitConicEquation.append(formatCoeff(d));
			sbBuildExplicitConicEquation.append(vars[leadingNonZero]);

			// other coeffs
			for (i = leadingNonZero + 1; i < vars.length; i++) {
				if (i != pos) {
					d = -numbers[i] / q;
					dabs = Math.abs(d);
					if (dabs >= PRINT_PRECISION) {
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(sign(d));
						sbBuildExplicitConicEquation.append(' ');
						sbBuildExplicitConicEquation.append(formatCoeff(dabs));
						sbBuildExplicitConicEquation.append(vars[i]);
					}
				}
			}

			// constant coeff
			d = -numbers[i] / q;
			dabs = Math.abs(d);
			if (dabs >= PRINT_PRECISION) {
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(sign(d));
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(format(dabs));
			}
			return sbBuildExplicitConicEquation;
		}
	}
	private StringBuffer sbBuildExplicitConicEquation = new StringBuffer(80);

	// y = k x + d
	final StringBuffer buildExplicitLineEquation(
		double[] numbers,
		String[] vars) {

		double d, dabs, q = numbers[1];		
		sbBuildExplicitLineEquation.setLength(0);
		
		//	BUILD EQUATION STRING                      
		// special case
		// y-coeff is 0: form x = constant
		if (isZero(q)) {
			sbBuildExplicitLineEquation.append("x = ");
			sbBuildExplicitLineEquation.append(format(-numbers[2] / numbers[0]));
			return sbBuildExplicitLineEquation;
		}

		// standard case: y-coeff not 0
		sbBuildExplicitLineEquation.append("y = ");

		// x coeff
		d = -numbers[0] / q;
		dabs = Math.abs(d);
		if (dabs >= PRINT_PRECISION) {
			sbBuildExplicitLineEquation.append(formatCoeff(d));
			sbBuildExplicitLineEquation.append("x");

			// constant            
			d = -numbers[2] / q;
			dabs = Math.abs(d);
			if (dabs >= PRINT_PRECISION) {
				sbBuildExplicitLineEquation.append(' ');
				sbBuildExplicitLineEquation.append(sign(d));
				sbBuildExplicitLineEquation.append(' ');
				sbBuildExplicitLineEquation.append(format(dabs));
			}
		} else {
			// only constant
			sbBuildExplicitLineEquation.append(format(-numbers[2] / q));
		}
		return sbBuildExplicitLineEquation;
	}
	private StringBuffer sbBuildExplicitLineEquation = new StringBuffer(50);

	/*
	final private String formatAbs(double x) {
		if (isZero(x))
			return "0";
		else
			return nf.format(Math.abs(x));
	}*/

	/** doesn't show 1 or -1 */
	final private String formatCoeff(double x) {
		if (isZero(Math.abs(x) - 1.0)) {
			if (x > 0.0)
				return "";
			else
				return "-";
		} else
			return format(x);
	}

	final public String format(double x) {	
		// ZERO
		if (-MIN_PRECISION < x && x < MIN_PRECISION)
			return "0";			
		
		return nf.format(x);				
	}
	
	final public String formatPiE(double x, NumberFormat nf) {
		// ZERO
		if (-MIN_PRECISION < x && x < MIN_PRECISION)
			return "0";			
		
		/*
		// 	E
		if (x == Math.E) {
			switch (casPrintForm) {
				case ExpressionNode.STRING_TYPE_GEOGEBRA:	
					return EULER_STRING;
				case ExpressionNode.STRING_TYPE_JASYMCA:
					return "exp(1)";
				case ExpressionNode.STRING_TYPE_YACAS:
					return "Exp(1)";
				default:
					return nf.format(Math.E);
			}
		}	
		*/		
		
		// PI
		if (x == Math.PI) {
			return casPrintFormPI;
		}		
				
		// 	MULTIPLES OF PI/2
		// i.e. x = a * pi/2
		double a = 2*x / Math.PI;
		int aint = (int) Math.round(a);
		sbFormat.setLength(0);
		if (isEqual(a, aint)) {	
			switch (aint) {					
				case 1: // pi/2
					sbFormat.append(casPrintFormPI);
					sbFormat.append("/2");
					return sbFormat.toString();
					
				case -1: // -pi/2
					sbFormat.append('-');
					sbFormat.append(casPrintFormPI);
					sbFormat.append("/2");
					return sbFormat.toString();
					
				case 2: // 2pi/2 = pi
					return casPrintFormPI;
					
				case -2: // -2pi/2 = -pi
					sbFormat.append('-');
					sbFormat.append(casPrintFormPI);
					return sbFormat.toString();
				
				default:
					// 	even
					long half = aint / 2;			
					if (aint == 2 * half) {		
						// half * pi
						sbFormat.append(half);
						if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA)
							sbFormat.append("*");
						sbFormat.append(casPrintFormPI);
						return sbFormat.toString();
					}
					// odd
					else {		
						// aint * pi/2
						sbFormat.append(aint);
						if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA)
							sbFormat.append("*");
						sbFormat.append(casPrintFormPI);
						sbFormat.append("/2");
						return sbFormat.toString();
					}
			}									
		}		
		
		// STANDARD CASE
		return nf.format(x);
	}
	private StringBuffer sbFormat = new StringBuffer();


	final public StringBuffer formatSigned(double x) {
		sbFormatSigned.setLength(0);
		if (-MIN_PRECISION < x && x < MIN_PRECISION) {
			sbFormatSigned.append("+ 0");
			return sbFormatSigned;
		}			
		
		if (x > 0.0d) {
			sbFormatSigned.append("+ ");
			sbFormatSigned.append(nf.format(x));
			return sbFormatSigned;
		} else {
			sbFormatSigned.append("- ");
			sbFormatSigned.append(nf.format(-x));
			return sbFormatSigned;
		}
	}
	private StringBuffer sbFormatSigned = new StringBuffer(40);
	

	final public StringBuffer formatAngle(double phi) {
		sbFormatAngle.setLength(0);
		if (Double.isNaN(phi)) {
			sbFormatAngle.append(app.getPlain("undefined"));
			return sbFormatAngle;
		}			
		
		if (cons.angleUnit == ANGLE_DEGREE) {
			if (isZero(phi)) {
				sbFormatAngle.append("0\u00b0");
				return sbFormatAngle;
			}				
			else {
				phi = Math.toDegrees(phi);
				if (phi < 0) phi += 360;				
				sbFormatAngle.append(nf.format(phi));
				sbFormatAngle.append('\u00b0');
				return sbFormatAngle;
			}
		} else {
			if (isZero(phi)) {
				sbFormatAngle.append( "0 rad");
				return sbFormatAngle;
			}
			else {				
				sbFormatAngle.append(nf.format(phi));
				sbFormatAngle.append(" rad");
				return sbFormatAngle;
			}
		}
	}
	private StringBuffer sbFormatAngle = new StringBuffer(40);

	final private static char sign(double x) {
		if (x > 0)
			return '+';
		else
			return '-';
	}

	/**
	 * greatest common divisor
	 */
	final static int gcd(int m, int n) {
		// Return the GCD of positive integers m and n.
		if (m == 0 || n == 0)
			return Math.max(Math.abs(m), Math.abs(n));

		int p = m, q = n;
		while (p % q != 0) {
			int r = p % q;
			p = q;
			q = r;
		}
		return q;
	}

	/**
	 * compute greatest common divisor of given longs
	 */
	final static int gcd(int[] numbers) {
		int gcd = numbers[0];
		for (int i = 0; i < numbers.length; i++) {
			gcd = gcd(numbers[i], gcd);
		}
		return gcd;
	}
	
	/**
	 * Round a double to the given scale
	 * e.g. roundToScale(5.32, 1) = 5.0,
	 * 	  roundToScale(5.32, 0.5) = 5.5,
	 * 	  roundToScale(5.32, 0.25) = 5.25,
	 *  	  roundToScale(5.32, 0.1) = 5.3
	 */
	final public static double roundToScale(double x, double scale) {
		if (scale == 1.0)
			return Math.round(x);
		else
			return Math.round(x / scale) * scale;
	}
	
	/**
	 * Checks if x is very close (1E-8) to an integer. If yes,
	 * the integer value is returned. If no, x is returnd.
	 */	
	final public double checkInteger(double x) {		
		double roundVal = Math.round(x);
		if (Math.abs(x - roundVal) < EPSILON)
			return roundVal;
		else
			return x;
	}
	
	/*******************************************************
	 * SAVING
	 *******************************************************/

	/**
	 * Returns current construction in XML format.
	 */
	public String getConstructionXML() {
		return cons.getXML();
	}
	
	public boolean isTranslateCommandName() {
		return translateCommandName;
	}

	public void setTranslateCommandName(boolean b) {
		translateCommandName = b;
	}

	/**
	 * States whether the continuity heuristic is active.
	 */
	final public boolean isContinuous() {
		return continuous;
	}

	/**
	 * Turns the continuity heuristic on or off.
	 * Note: the macro kernel always turns continuity off. 
	 */
	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

}
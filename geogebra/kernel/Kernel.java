/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Kernel.java
 *
 * Created on 30. August 2001, 20:12
 */

package geogebra.kernel;

import geogebra.cas.GeoGebraCAS;
import geogebra.euclidian.EuclidianView;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.parser.Parser;
import geogebra.kernel.statistics.AlgoCauchy;
import geogebra.kernel.statistics.AlgoChiSquared;
import geogebra.kernel.statistics.AlgoDoubleListCovariance;
import geogebra.kernel.statistics.AlgoDoubleListPMCC;
import geogebra.kernel.statistics.AlgoDoubleListSXX;
import geogebra.kernel.statistics.AlgoDoubleListSXY;
import geogebra.kernel.statistics.AlgoDoubleListSigmaXX;
import geogebra.kernel.statistics.AlgoDoubleListSigmaXY;
import geogebra.kernel.statistics.AlgoDoubleListSigmaYY;
import geogebra.kernel.statistics.AlgoExponential;
import geogebra.kernel.statistics.AlgoFDistribution;
import geogebra.kernel.statistics.AlgoFit;
import geogebra.kernel.statistics.AlgoFitExp;
import geogebra.kernel.statistics.AlgoFitGrowth;
import geogebra.kernel.statistics.AlgoFitLineX;
import geogebra.kernel.statistics.AlgoFitLineY;
import geogebra.kernel.statistics.AlgoFitLog;
import geogebra.kernel.statistics.AlgoFitLogistic;
import geogebra.kernel.statistics.AlgoFitPoly;
import geogebra.kernel.statistics.AlgoFitPow;
import geogebra.kernel.statistics.AlgoFitSin;
import geogebra.kernel.statistics.AlgoGamma;
import geogebra.kernel.statistics.AlgoHyperGeometric;
import geogebra.kernel.statistics.AlgoInverseCauchy;
import geogebra.kernel.statistics.AlgoInverseChiSquared;
import geogebra.kernel.statistics.AlgoInverseExponential;
import geogebra.kernel.statistics.AlgoInverseFDistribution;
import geogebra.kernel.statistics.AlgoInverseGamma;
import geogebra.kernel.statistics.AlgoInverseHyperGeometric;
import geogebra.kernel.statistics.AlgoInverseNormal;
import geogebra.kernel.statistics.AlgoInversePascal;
import geogebra.kernel.statistics.AlgoInverseTDistribution;
import geogebra.kernel.statistics.AlgoInverseWeibull;
import geogebra.kernel.statistics.AlgoInverseZipf;
import geogebra.kernel.statistics.AlgoListCovariance;
import geogebra.kernel.statistics.AlgoListMeanX;
import geogebra.kernel.statistics.AlgoListMeanY;
import geogebra.kernel.statistics.AlgoListPMCC;
import geogebra.kernel.statistics.AlgoListSXX;
import geogebra.kernel.statistics.AlgoListSXY;
import geogebra.kernel.statistics.AlgoListSYY;
import geogebra.kernel.statistics.AlgoListSigmaXX;
import geogebra.kernel.statistics.AlgoListSigmaXY;
import geogebra.kernel.statistics.AlgoListSigmaYY;
import geogebra.kernel.statistics.AlgoMean;
import geogebra.kernel.statistics.AlgoMedian;
import geogebra.kernel.statistics.AlgoMode;
import geogebra.kernel.statistics.AlgoNormal;
import geogebra.kernel.statistics.AlgoPascal;
import geogebra.kernel.statistics.AlgoProduct;
import geogebra.kernel.statistics.AlgoQ1;
import geogebra.kernel.statistics.AlgoQ3;
import geogebra.kernel.statistics.AlgoRandom;
import geogebra.kernel.statistics.AlgoRandomBinomial;
import geogebra.kernel.statistics.AlgoRandomNormal;
import geogebra.kernel.statistics.AlgoRandomPoisson;
import geogebra.kernel.statistics.AlgoRandomUniform;
import geogebra.kernel.statistics.AlgoRank;
import geogebra.kernel.statistics.AlgoSXX;
import geogebra.kernel.statistics.AlgoSampleStandardDeviation;
import geogebra.kernel.statistics.AlgoSampleVariance;
import geogebra.kernel.statistics.AlgoShuffle;
import geogebra.kernel.statistics.AlgoSigmaXX;
import geogebra.kernel.statistics.AlgoStandardDeviation;
import geogebra.kernel.statistics.AlgoSum;
import geogebra.kernel.statistics.AlgoSumSquaredErrors;
import geogebra.kernel.statistics.AlgoTDistribution;
import geogebra.kernel.statistics.AlgoVariance;
import geogebra.kernel.statistics.AlgoWeibull;
import geogebra.kernel.statistics.AlgoZipf;
import geogebra.kernel.statistics.RegressionMath;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.ScientificFormat;
import geogebra.util.Unicode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.math.complex.Complex;


public class Kernel {

	// standard precision 
	public final static double STANDARD_PRECISION = 1E-8;
	
	// minimum precision
	public final static double MIN_PRECISION = 1E-5;
	private final static double INV_MIN_PRECISION = 1E5; 

	// maximum reasonable precision
	public final static double MAX_PRECISION = 1E-12;
	
	// current working precision
	public static double EPSILON = STANDARD_PRECISION;

	// maximum precision of double numbers
	public final static double MAX_DOUBLE_PRECISION = 1E-15;
	public final static double INV_MAX_DOUBLE_PRECISION = 1E15;	
	
	 // style of point/vector coordinates
    public static final int COORD_STYLE_DEFAULT = 0;		// A = (3, 2)  and 	B = (3; 90�)
	public static final int COORD_STYLE_AUSTRIAN = 1;		// A(3|2)  	   and	B(3; 90�)
	public static final int COORD_STYLE_FRENCH = 2;			// A: (3, 2)   and	B: (3; 90�)
	private int coordStyle = 0;

	// STATIC
	final public static int ANGLE_RADIANT = 1;
	final public static int ANGLE_DEGREE = 2;
	final public static int COORD_CARTESIAN = 3;
	final public static int COORD_POLAR = 4;	 
	final public static int COORD_COMPLEX = 5;	 
	final public static String EULER_STRING = "\u212f"; // "\u0435";
	final public static String PI_STRING = "\u03c0";	
	final public static double PI_2 = 2.0 * Math.PI;
	final public static double PI_HALF =  Math.PI / 2.0;
	final public static double SQRT_2_HALF =  Math.sqrt(2.0) / 2.0;
	final public static double PI_180 = Math.PI / 180;
	final public static double CONST_180_PI = 180 / Math.PI;
	//private static boolean KEEP_LEADING_SIGN = true;
	
	//G.Sturr 2009-10-18
	// algebra style 
	final public static int ALGEBRA_STYLE_VALUE = 0;
	final public static int ALGEBRA_STYLE_DEFINITION = 1;
	final public static int ALGEBRA_STYLE_COMMAND = 2;
	private int algebraStyle = Kernel.ALGEBRA_STYLE_VALUE;
	//end G.Sturr
	
	// print precision
	public static final int STANDARD_PRINT_DECIMALS = 2; 
	private double PRINT_PRECISION = 1E-2;
	private NumberFormat nf;
	private ScientificFormat sf;
	public boolean useSignificantFigures = false;
	
	// angle unit: degree, radians
	private int angleUnit = Kernel.ANGLE_DEGREE;
	
	// rounding hack, see format()
	private static final double ROUND_HALF_UP_FACTOR_DEFAULT = 1.0 + 1E-15;
	private double ROUND_HALF_UP_FACTOR = ROUND_HALF_UP_FACTOR_DEFAULT;
	
	// used to store info when rounding is temporarily changed
	private Stack useSignificantFiguresList;
	private Stack noOfSignificantFiguresList;
	private Stack noOfDecimalPlacesList;
	
	/* Significant figures
	 * 
	 * How to do:
	 * 
	 * private ScientificFormat sf;
	 * sf = new ScientificFormat(5, 20, false);
	 * String s = sf.format(double)
	 * 
	 * need to address:
	 * 
	 * PRINT_PRECISION 
	 * setPrintDecimals()
	 * getPrintDecimals()
	 * getMaximumFractionDigits()
	 * setMaximumFractionDigits()
	 * 
	 * how to determine whether to use nf or sf
	 */
	
	private int casPrintForm;		
	private String casPrintFormPI; // for pi
	
	// before May 23, 2005 the function acos(), asin() and atan()
	// had an angle as result. Now the result is a number.
	// this flag is used to distinguish the different behaviour
	// depending on the the age of saved construction files
	public boolean arcusFunctionCreatesAngle = false;
	
	private boolean translateCommandName = true;
	private boolean undoActive = false;
	private boolean notifyViewsActive = true;
	private boolean viewReiniting = false;
	private boolean allowVisibilitySideEffects = true;
	
	// silentMode is used to create helper objects without any side effects	
	// i.e. in silentMode no labels are created and no objects are added to views
	private boolean silentMode = false;
	
	// setResolveUnkownVarsAsDummyGeos
	private boolean resolveUnkownVarsAsDummyGeos = false;
	
	private double xmin, xmax, ymin, ymax, xscale, yscale;
	
	// Views may register to be informed about 
	// changes to the Kernel
	// (add, remove, update)
	private View[] views = new View[20];
	private int viewCnt = 0;
	
	protected Construction cons;
	protected Application app;	
	protected AlgebraProcessor algProcessor;
	private EquationSolver eqnSolver;
	private RegressionMath regMath;
	private ExtremumFinder extrFinder;
	protected Parser parser;
	private Object ggbCAS;
	
	// Continuity on or off, default: false since V3.0
	private boolean continuous = false;
	private MacroManager macroManager;
	
	
	/** Evaluator for ExpressionNode */
	protected ExpressionNodeEvaluator expressionNodeEvaluator;
				
	public Kernel(Application app) {
		this();
		this.app = app;
		
		newConstruction();
		newExpressionNodeEvaluator();
	}
	
	/**
	 * creates the construction cons
	 */
	protected void newConstruction(){
		cons = new Construction(this);	
	}
	
	
	/**
	 * creates a new MyXMLHandler (used for 3D)
	 * @param cons construction used in MyXMLHandler constructor
	 * @return a new MyXMLHandler
	 */
	public MyXMLHandler newMyXMLHandler(Construction cons){
		return new MyXMLHandler(this, cons);		
	}
	
	
	/**
	 * creates the Evaluator for ExpressionNode
	 */
	protected void newExpressionNodeEvaluator(){
		expressionNodeEvaluator = new ExpressionNodeEvaluator();
	}
	
	/** return the Evaluator for ExpressionNode
	 * @return the Evaluator for ExpressionNode
	 */
	public ExpressionNodeEvaluator getExpressionNodeEvaluator(){
		if (expressionNodeEvaluator == null)
			newExpressionNodeEvaluator();
		return expressionNodeEvaluator;
	}
	
	
	public Kernel() {
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		
		sf = new ScientificFormat(5, 16, false);
		
		setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
	}
	
	/**
	 * Returns this kernel's algebra processor that handles
	 * all input and commands.
	 */	
	public AlgebraProcessor getAlgebraProcessor() {
    	if (algProcessor == null)
    		algProcessor = new AlgebraProcessor(this);
    	return algProcessor;
    }
	
	
	
	/**
     * Returns a GeoElement for the given label. 
     * @return may return null
     */
	final public GeoElement lookupLabel(String label) {		
		return lookupLabel(label, false);
	}
	
	final public GeoElement lookupLabel(String label, boolean autoCreate) {	
		GeoElement geo = cons.lookupLabel(label, autoCreate);
				
		if (geo == null && resolveUnkownVarsAsDummyGeos) {
			// resolve unknown variable as dummy geo to keep its name and 
			// avoid an "unknown variable" error message
			geo = new GeoDummyVariable(cons, label);
		}
		
		return geo;
	}
	
	/*
	 * returns GeoElement at (row,col) in spreadsheet
	 * may return null
	 */
	public GeoElement getGeoAt(int col, int row) {
		return lookupLabel(GeoElement.getSpreadsheetCellName(col, row));
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
	
	final public Application getApplication() {
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
	
	final public RegressionMath getRegressionMath() {
		if (regMath == null)
			regMath = new RegressionMath();
		return regMath;
	}
	
	final public Parser getParser() {
    	if (parser == null)
    		parser = new Parser(this, cons);
    	return parser;
    }	
			
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateGeoGebraCAS(String exp) throws Throwable {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ((geogebra.cas.GeoGebraCAS) ggbCAS).evaluateGeoGebraCAS(exp);
	}	
	
	/** 
	 * Evaluates an expression in MathPiper syntax with.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateMathPiper(String exp) {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ((geogebra.cas.GeoGebraCAS) ggbCAS).evaluateMathPiper(exp);
	}	
	
	/** 
	 * Evaluates an expression in Maxima syntax with.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateMaxima(String exp) {
		if (ggbCAS == null) {
			getGeoGebraCAS();		
		}
		
		return ((geogebra.cas.GeoGebraCAS) ggbCAS).evaluateMaxima(exp);
	}	

	/** 
     *  Returns whether var is a defined variable in GeoGebraCAS.
     */
	final public boolean isCASVariableBound(String var) {
		if (ggbCAS == null) {
			return false;		
		} else {
			return ((geogebra.cas.GeoGebraCAS) ggbCAS).isVariableBound(var);
		}
	}	
			
	final public boolean isGeoGebraCASready() {
		return ggbCAS != null;
	}
	
	public static int DEFAULT_CAS = Application.CAS_MATHPIPER; // default

	/*
	 * needed eg change MathPiper -> Maxima
	 */
	final public void setDefaultCAS(int cas) {
		DEFAULT_CAS = cas;
		if (ggbCAS != null) ((geogebra.cas.GeoGebraCAS) ggbCAS).setCurrentCAS(DEFAULT_CAS);
	}
	
	/**
	 * Returns this kernel's GeoGebraCAS object.
	 */
	public synchronized Object getGeoGebraCAS() {
		if (ggbCAS == null) {
			ggbCAS = new geogebra.cas.GeoGebraCAS(this);
		}			
		
		return ggbCAS;
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
    		getGeoGebraCAS();					
		}
    	
    	return ((geogebra.cas.GeoGebraCAS) ggbCAS).getPolynomialCoeffs(exp, variable);
    }

	final public void setEpsilon(double epsilon) {
		EPSILON = epsilon;
	
		if (EPSILON > MIN_PRECISION)
			EPSILON = MIN_PRECISION;
		else if (EPSILON < MAX_PRECISION)
			EPSILON = MAX_PRECISION;
		
		getEquationSolver().setEpsilon(EPSILON);
	}
	
	/**
	 * Sets the working epsilon precision according to the given 
	 * print precision. After this method epsilon will always be
	 * less or equal STANDARD_PRECISION.
	 * @param printPrecision
	 */
	private void setEpsilonForPrintPrecision(double printPrecision) {
		if (printPrecision < STANDARD_PRECISION) {
			setEpsilon(printPrecision);
		} else {
			setEpsilon(STANDARD_PRECISION);
		}
	}

	final public double getEpsilon() {
		return EPSILON;
	}

	final public void setMinPrecision() {
		setEpsilon(MIN_PRECISION);
	}

	final public void resetPrecision() {
		setEpsilon(STANDARD_PRECISION);
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
	
	private void notifyEuclidianViewAlgos() {
		if (macroManager != null) 
			macroManager.notifyEuclidianViewAlgos();
		
		cons.notifyEuclidianViewAlgos();
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
	 * Registers an algorithm that needs to be updated when notifyRename(),
	 * notifyAdd(), or notifyRemove() is called.	 
	 */
	void registerRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos == null) {
			renameListenerAlgos = new ArrayList();
		}
		
		if (!renameListenerAlgos.contains(algo))
			renameListenerAlgos.add(algo);
	}
	
	void unregisterRenameListenerAlgo(AlgoElement algo) {
		if (renameListenerAlgos != null) 
			renameListenerAlgos.remove(algo);
	}
	private ArrayList renameListenerAlgos;
	
	private void notifyRenameListenerAlgos() {
		AlgoElement.updateCascadeAlgos(renameListenerAlgos);
	}	
	
		
	//G.Sturr 2009-10-18
	final public void setAlgebraStyle(int style) {
		algebraStyle = style;
	}

	final public int getAlgebraStyle() {
		return algebraStyle;
	}
	//end G.Sturr
	
	
	final public void setAngleUnit(int unit) {
		angleUnit = unit;
	}

	final public int getAngleUnit() {
		return angleUnit;
	}
	
	final public int getMaximumFractionDigits() {
		return nf.getMaximumFractionDigits();
	}

	final public void setMaximumFractionDigits(int digits) {
		//Application.debug(""+digits);
		useSignificantFigures = false;
		nf.setMaximumFractionDigits(digits);
	}
	
	final public String getPiString() {
		return casPrintFormPI;
	}
	
	final public void setCASPrintForm(int type) {
		casPrintForm = type;
		
		switch (casPrintForm) {
		case ExpressionNode.STRING_TYPE_MATH_PIPER:
			casPrintFormPI = "Pi";
			break;
			
		case ExpressionNode.STRING_TYPE_MAXIMA:
			casPrintFormPI = "%pi";
			break;
			
			case ExpressionNode.STRING_TYPE_JASYMCA:
			case ExpressionNode.STRING_TYPE_GEOGEBRA_XML:
				casPrintFormPI = "pi";
				break;
		
			default:
				casPrintFormPI = PI_STRING;
		}
	}
	
	final public int getCASPrintForm() {
		return casPrintForm;
	}
	
	final public int getCurrentCAS() {
		return ((GeoGebraCAS)getGeoGebraCAS()).currentCAS;
	}

	final public void setPrintDecimals(int decimals) {
		if (decimals >= 0) {
			useSignificantFigures = false;
			nf.setMaximumFractionDigits(decimals);
			ROUND_HALF_UP_FACTOR = decimals < 15 ? ROUND_HALF_UP_FACTOR_DEFAULT : 1;
			
			PRINT_PRECISION = Math.pow(10, -decimals);
			setEpsilonForPrintPrecision(PRINT_PRECISION);
		}
	}
	
	final public int getPrintDecimals() {
		return nf.getMaximumFractionDigits();
	}
		
	final public void setPrintFigures(int figures) {
		if (figures >= 0) {
			useSignificantFigures = true;
			sf.setSigDigits(figures);
			sf.setMaxWidth(16); // for scientific notation
			ROUND_HALF_UP_FACTOR = figures < 15 ? ROUND_HALF_UP_FACTOR_DEFAULT : 1;
			
			PRINT_PRECISION = MAX_PRECISION;
			setEpsilonForPrintPrecision(PRINT_PRECISION);
		}
	}
	
	/**
	 * Sets the print accuracy to at least the given decimals
	 * or significant figures. If the current accuracy is already higher, nothing is changed.
	 * 
	 * @param decimalsOrFigures
	 * @return whether the print accuracy was changed
	 */
	public boolean ensureTemporaryPrintAccuracy(int decimalsOrFigures) {
		if (useSignificantFigures) {
			if (sf.getSigDigits() < decimalsOrFigures) {
				setTemporaryPrintFigures(decimalsOrFigures);
				return true;
			}
		} else {
			// decimals
			if (nf.getMaximumFractionDigits() < decimalsOrFigures) {
				setTemporaryPrintDecimals(decimalsOrFigures);
				return true;
			}
		}
		return false;
	}	
	
	final public void setTemporaryPrintFigures(int figures) {
		storeTemporaryRoundingInfoInList();				
		setPrintFigures(figures);
	}
	
	final public void setTemporaryPrintDecimals(int decimals) {		
		storeTemporaryRoundingInfoInList();		
		setPrintDecimals(decimals);
	}			

	/*
	 * stores information about the current no of decimal places/sig figures used
	 * for when it is (temporarily changed)
	 * needs to be in a list as it can be nested
	 */
	private void storeTemporaryRoundingInfoInList()
	{
		if (useSignificantFiguresList == null) {
			useSignificantFiguresList = new Stack();
			noOfSignificantFiguresList = new Stack();
			noOfDecimalPlacesList = new Stack();
		}
				
		useSignificantFiguresList.push(new Boolean(useSignificantFigures));
		noOfSignificantFiguresList.push(new Integer(sf.getSigDigits()));	
		noOfDecimalPlacesList.push(new Integer(nf.getMaximumFractionDigits()));	
	}
	
	
	final public void restorePrintAccuracy()
	{		
		// get previous values from stacks
		useSignificantFigures = ((Boolean)useSignificantFiguresList.pop()).booleanValue();		
		int sigFigures = ((Integer)(noOfSignificantFiguresList.pop())).intValue();
		int decDigits = ((Integer)(noOfDecimalPlacesList.pop())).intValue();
		
		if (useSignificantFigures)
			setPrintFigures(sigFigures);
		else
			setPrintDecimals(decDigits);	
		
		//Application.debug("list size"+noOfSignificantFiguresList.size());
	}
	
	/*
	 * returns number of significant digits, or -1 if using decimal places
	 */
	final public int getPrintFigures() {
		if (!useSignificantFigures) return -1;
		return sf.getSigDigits();
	}
		
	/**
	 * returns 10^(-PrintDecimals)
	 *
	final public double getPrintPrecision() {
		return PRINT_PRECISION;
	} */
	
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
    public GeoElement createGeoElement(Construction cons, String type) throws MyError {    	
    	// the type strings are the classnames in lowercase without the beginning "geo"
    	// due to a bug in GeoGebra 2.6c the type strings for conics
        // in XML may be "ellipse", "hyperbola", ...  
    	    	
    	switch (type.charAt(0)) {
    		case 'a': //angle    			
    			return new GeoAngle(cons);	    			     		    			
    			
    		case 'b': //angle
    			if (type.equals("boolean"))
    				return new GeoBoolean(cons);
    			else
        			return new GeoButton(cons); // "button"
    		
    		case 'c': // conic
    			if (type.equals("conic"))
    				return new GeoConic(cons);   
    			else if (type.equals("conicpart"))    					
    				return new GeoConicPart(cons, 0);
    			else if (type.equals("cubic"))    					
    				return new GeoCubic(cons);
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
    			
    		case 't': 
    			if (type.equals("text"))
    				return new GeoText(cons); // text
    			else
        			return new GeoTextField(cons); // textfield
   			
    		case 'v': // vector
				return new GeoVector(cons);
    		
    		default:    			
    			throw new MyError(cons.getApplication(), "Kernel: GeoElement of type "
    		            + type + " could not be created.");		    		
    	}    		    
    }  
    
    
    
    
    
    
	/* *******************************************
	 *  Methods for EuclidianView/EuclidianView3D
	 * ********************************************/
    

	public String getModeText(int mode) {
		switch (mode) {
		case EuclidianView.MODE_SELECTION_LISTENER:
			return "Select";

		case EuclidianView.MODE_MOVE:
			return "Move";

		case EuclidianView.MODE_POINT:
			return "Point";
			
		case EuclidianView.MODE_POINT_IN_REGION:
			return "PointInRegion";
			
		case EuclidianView.MODE_JOIN:
			return "Join";

		case EuclidianView.MODE_SEGMENT:
			return "Segment";

		case EuclidianView.MODE_SEGMENT_FIXED:
			return "SegmentFixed";

		case EuclidianView.MODE_RAY:
			return "Ray";

		case EuclidianView.MODE_POLYGON:
			return "Polygon";

		case EuclidianView.MODE_PARALLEL:
			return "Parallel";

		case EuclidianView.MODE_ORTHOGONAL:
			return "Orthogonal";

		case EuclidianView.MODE_INTERSECT:
			return "Intersect";

		case EuclidianView.MODE_LINE_BISECTOR:
			return "LineBisector";

		case EuclidianView.MODE_ANGULAR_BISECTOR:
			return "AngularBisector";

		case EuclidianView.MODE_TANGENTS:
			return "Tangent";

		case EuclidianView.MODE_POLAR_DIAMETER:
			return "PolarDiameter";

		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
			return "Circle2";

		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
			return "Circle3";

		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
			return "Ellipse3";

		case EuclidianView.MODE_PARABOLA:
			return "Parabola";

		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
			return "Hyperbola3";

		// Michael Borcherds 2008-03-13
		case EuclidianView.MODE_COMPASSES:
			return "Compasses";

		case EuclidianView.MODE_CONIC_FIVE_POINTS:
			return "Conic5";

		case EuclidianView.MODE_RELATION:
			return "Relation";

		case EuclidianView.MODE_TRANSLATEVIEW:
			return "TranslateView";

		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			return "ShowHideObject";

		case EuclidianView.MODE_SHOW_HIDE_LABEL:
			return "ShowHideLabel";

		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			return "CopyVisualStyle";

		case EuclidianView.MODE_DELETE:
			return "Delete";

		case EuclidianView.MODE_VECTOR:
			return "Vector";

		case EuclidianView.MODE_TEXT:
			return "Text";

		case EuclidianView.MODE_IMAGE:
			return "Image";

		case EuclidianView.MODE_MIDPOINT:
			return "Midpoint";

		case EuclidianView.MODE_SEMICIRCLE:
			return "Semicircle";

		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
			return "CircleArc3";

		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return "CircleSector3";

		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return "CircumcircleArc3";

		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return "CircumcircleSector3";

		case EuclidianView.MODE_SLIDER:
			return "Slider";

		case EuclidianView.MODE_MIRROR_AT_POINT:
			return "MirrorAtPoint";

		case EuclidianView.MODE_MIRROR_AT_LINE:
			return "MirrorAtLine";

		case EuclidianView.MODE_MIRROR_AT_CIRCLE:
			return "MirrorAtCircle";

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			return "TranslateByVector";

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			return "RotateByAngle";

		case EuclidianView.MODE_DILATE_FROM_POINT:
			return "DilateFromPoint";

		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
			return "CirclePointRadius";

		case EuclidianView.MODE_ANGLE:
			return "Angle";

		case EuclidianView.MODE_ANGLE_FIXED:
			return "AngleFixed";

		case EuclidianView.MODE_VECTOR_FROM_POINT:
			return "VectorFromPoint";

		case EuclidianView.MODE_DISTANCE:
			return "Distance";				

		case EuclidianView.MODE_MOVE_ROTATE:
			return "MoveRotate";

		case EuclidianView.MODE_ZOOM_IN:
			return "ZoomIn";

		case EuclidianView.MODE_ZOOM_OUT:
			return "ZoomOut";

		case EuclidianView.MODE_LOCUS:
			return "Locus";
			
		case EuclidianView.MODE_AREA:
			return "Area";
			
		case EuclidianView.MODE_SLOPE:
			return "Slope";
			
		case EuclidianView.MODE_REGULAR_POLYGON:
			return "RegularPolygon";
			
		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:
			return "ShowCheckBox";
			
		case EuclidianView.MODE_BUTTON_ACTION:
			return "ButtonAction";
			
		case EuclidianView.MODE_TEXTFIELD_ACTION:
			return "TextFieldAction";
			
		case EuclidianView.MODE_PEN:
			return "Pen";
			
		case EuclidianView.MODE_VISUAL_STYLE:
			return "VisualStyle";
			
		case EuclidianView.MODE_FITLINE:
			return "FitLine";

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			return "RecordToSpreadsheet";

		default:
			return "";
		}
	}
    
    
    
    
    
	/* *******************************************
	 *  Methods for MyXMLHandler
	 * ********************************************/
	public boolean handleCoords(GeoElement geo, LinkedHashMap<String, String> attrs) {
		
		if (!(geo instanceof GeoVec3D)) {
			Application.debug("wrong element type for <coords>: "
					+ geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;
		


		try {
			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			double z = Double.parseDouble((String) attrs.get("z"));
			v.setCoords(x, y, z);
			return true;
		} catch (Exception e) {
			return false;
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
		if (cons.getStep() != step) {
			cons.setStep(step);
			app.setUnsaved();
		}
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
			setConstructionStep(getNextBreakpoint(step));		
		} else {	
			setConstructionStep(step);
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
			setConstructionStep(getPreviousBreakpoint(step));		
		} else {	
			setConstructionStep(step);
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
			setConstructionStep(getNextBreakpoint(step));		
		} else {	
			setConstructionStep(step);
    	}
	}
	
	private int getNextBreakpoint(int step) {
		int lastStep = getLastConstructionStep();
		// go to next breakpoint
		while (step <= lastStep) {
			if (cons.getConstructionElement(step).isConsProtocolBreakpoint()) {				
				return step;
			}
				
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
		
		// clear animations
		if (animationManager != null) {
			animationManager.stopAnimation();
			animationManager.clearAnimatedGeos();
		}
				
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
	}
	
	public boolean isUndoActive() {
		return undoActive;
	}
	
	public void storeUndoInfo() {
		if (undoActive) {
			cons.storeUndoInfo();
		}
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
	//	Application.debug("ATTACH " + view + ", notifyActive: " + notifyViewsActive);			
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
				
		//TODO: remove
		System.out.print("  current views:\n");
		for (int i = 0; i < viewCnt; i++) {
			System.out.print(views[i] + "\n");
		}
		System.out.print("\n");
		//Application.debug();
		
		
		if (!notifyViewsActive) {
			oldViewCnt = viewCnt;
			viewCnt = 0;
		}
	}

	public void detach(View view) {    
		// Application.debug("detach " + view);
		
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
		Application.debug();		
		*/
		
		if (!notifyViewsActive) {
			oldViewCnt = viewCnt;
			viewCnt = 0;
		}
	}	

	final public void notifyAddAll(View view) {
		int consStep = cons.getStep();
		notifyAddAll(view, consStep);
	}
		
	final public void notifyAddAll(View view, int consStep) {
		if (!notifyViewsActive) return;
				
		Iterator it = cons.getGeoSetConstructionOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			
			// stop when not visible for current construction step
			if (!geo.isAvailableAtConstructionStep(consStep))
				break;
			
			view.add(geo);
		}			
	}	
	

//	final public void notifyRemoveAll(View view) {
//		Iterator it = cons.getGeoSetConstructionOrder().iterator();
//		while (it.hasNext()) {
//			GeoElement geo = (GeoElement) it.next();
//			view.remove(geo);
//		}	
//	}

	/**
	 * Tells views to update all labeled elements of current construction.
	 *
	final public static void notifyUpdateAll() {
		notifyUpdate(kernelConstruction.getAllGeoElements());
	}*/

	final void notifyAdd(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].add(geo);					
			}
		}
		
		notifyRenameListenerAlgos();
	}

	final void notifyRemove(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].remove(geo);
			}
		}
		
		notifyRenameListenerAlgos();
	}

	protected final void notifyUpdate(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].update(geo);
			}
		}
	}
	
	final void notifyUpdateAuxiliaryObject(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].updateAuxiliaryObject(geo);
			}
		}
	}

	final  void notifyRename(GeoElement geo) {
		if (notifyViewsActive) {
			for (int i = 0; i < viewCnt; ++i) {
				views[i].rename(geo);
			}
		}
		
		notifyRenameListenerAlgos();
	}
	
	public void setNotifyViewsActive(boolean flag) {	
		//Application.debug("setNotifyViews: " + flag);
		
		if (flag != notifyViewsActive) {
			notifyViewsActive = flag;
			
			if (flag) {
				//Application.debug("Activate VIEWS");				
				viewReiniting = true;
				
				// "attach" views again
				viewCnt = oldViewCnt;		
				
				
				// add all geos to all views
				Iterator it = cons.getGeoSetConstructionOrder().iterator();				
				while (it.hasNext()) {	
					GeoElement geo =  (GeoElement) it.next();					
					notifyAdd(geo);									
				}			
				
				/*
				Object [] geos =
					getConstruction().getGeoSetConstructionOrder().toArray();
				for (int i = 0 ; i < geos.length ; i++) {
					GeoElement geo =  (GeoElement) geos[i];					
					notifyAdd(geo);														
				}*/
				
				
				//app.setMoveMode();
				
				notifyEuclidianViewAlgos();
				notifyReset();					
				viewReiniting = false;
			} 
			else {
				//Application.debug("Deactivate VIEWS");

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
		
	public boolean isViewReiniting() {
		return viewReiniting;
	}
		
	private boolean notifyRepaint = true;
		
	public void setNotifyRepaintActive(boolean flag) {
		if (flag != notifyRepaint) {
			notifyRepaint = flag;
			if (notifyRepaint)
				notifyRepaint();
		}
	}
		
	final public boolean isNotifyRepaintActive() {
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
	 * Returns a list with all currently registered macros.
	 */
	public ArrayList getAllMacros() {
		if (macroManager == null)
			return null;
		else
			return macroManager.getAllMacros();
	}
	
	/**
	 * Returns i-th registered macro
	 */
	public Macro getMacro(int i) {
		try {
			return macroManager.getMacro(i);
		} catch (Exception e) {
			return null;
		}		
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
	public String getMacroXML(ArrayList macros) {
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

	/** Point label with cartesian coordinates (x,y)   */
	final public GeoPoint Point(String label, double x, double y, boolean complex) {
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, 1.0);
		if (complex) {
			p.setMode(COORD_COMPLEX);
			// we have to reset the visual style as the constructor
			// did not know that this was a complex number
			p.setConstructionDefaults();
		}
		else
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

	/** Conic label with equation ax� + bxy + cy� + dx + ey + f = 0  */
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

	/** Implicit Cubic  */
	final public GeoCubic Cubic(
		String label,
		double a,
		double b,
		double c,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		double j,
		double k,
		double l,
		double m,
		double n,
		double o,
		double p) {
		double[] coeffs = { a, b, c, d, e, f,g,h,i,j,k,l,m,n,o,p };
		GeoCubic cubic = new GeoCubic(cons, label, coeffs);
		return cubic;
	}

	/** Converts number to angle */
	final public GeoAngle Angle(String label, GeoNumeric num) {
		AlgoAngleNumeric algo = new AlgoAngleNumeric(cons, label, num);
		GeoAngle angle = algo.getAngle();
		return angle;
	}

	/** Function in x,  e.g. f(x) = 4 x� + 3 x�
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
	
	/**
	 * Creates a dependent list object with the given label, 
	 * e.g. {3, 2, 1} + {a, b, 2}	 
	 */
	final public GeoList ListExpression(String label, ExpressionNode root) {
		AlgoDependentListExpression algo =
			new AlgoDependentListExpression(cons, label, root);		
		return algo.getList();
	}
	
	/**
	 * Creates a list object for a range of cells in the spreadsheet. 
	 * e.g. A1:B2
	 */
	final public GeoList CellRange(String label, GeoElement startCell, GeoElement endCell) {
		AlgoCellRange algo =
			new AlgoCellRange(cons, label, startCell, endCell);		
		return algo.getList();
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
			Application.debug("if incompatible: " + geoIf + ", " + geoElse);
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
	 * If-then-else construct for functions. 
	 *  example: If[ x < 2, x^2, x + 2 ]
	 */
	final public GeoNumeric CountIf(String label, 
			GeoFunction boolFun,
			GeoList list) {
		
		AlgoCountIf algo = new AlgoCountIf(cons, label, boolFun, list);
		return algo.getResult();
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
		AlgoCurveCartesian algo = new AlgoCurveCartesian(cons, label, new NumberValue[] {xcoord, ycoord} , localVar, from, to);
		return (GeoCurveCartesian) algo.getCurve();		
	}	
	
	/**
	 * Converts a NumberValue object to an ExpressionNode object. 
	 */
	public ExpressionNode convertNumberValueToExpressionNode(NumberValue nv) {
		GeoElement geo = nv.toGeoElement();
		AlgoElement algo = geo.getParentAlgorithm();
		
		if (algo != null && algo instanceof AlgoDependentNumber) {
			AlgoDependentNumber algoDep = (AlgoDependentNumber) algo;
			return algoDep.getExpression().getCopy(this);
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
		ExpressionNode root, boolean complex) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, label, root, complex);
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
	 * represented by trees. e.g. y� = 2 p x 
	 */
	final public GeoConic DependentConic(String label, Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic(cons, label, equ);
		GeoConic conic = algo.getConic();
		return conic;
	}

	/** Implicit cubic eg x^3+y^3=1
	 */
	final public GeoCubic DependentCubic(String label, Equation equ) {
		AlgoDependentCubic algo = new AlgoDependentCubic(cons, label, equ);
		GeoCubic cubic = algo.getCubic();
		return cubic;
	}

	/** Function dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. f(x) = a x� + b x�
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
	 * Creates a dependent copy of origGeo with label
	 */
	final public GeoElement DependentGeoCopy(String label, ExpressionNode origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, label, origGeoNode);
		return algo.getGeo();
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
	 * Object from name
	 */
	final public GeoElement Object(
		String label,
		GeoText text) {
		AlgoObject algo = new AlgoObject(cons, label, text);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Spreadsheet Object from coords
	 */
	final public GeoElement Cell(
		String label,
		NumberValue a, NumberValue b) {
		AlgoCell algo = new AlgoCell(cons, label, a, b);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ColumnName[]
	 */
	final public GeoText ColumnName(
		String label,
		GeoElement geo) {
		AlgoColumnName algo = new AlgoColumnName(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}		
	
	/** 
	 * LaTeX of geo.
	 */
	final public GeoText LaTeX(
		String label,
		GeoElement geo, GeoBoolean substituteVars) {
		AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * LaTeX of geo.
	 */
	final public GeoText LaTeX(
		String label,
		GeoElement geo) {
		AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo) {
		AlgoText algo = new AlgoText(cons, label, geo);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoBoolean substituteVars) {
		AlgoText algo = new AlgoText(cons, label, geo, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint p, GeoBoolean substituteVars) {
		AlgoText algo = new AlgoText(cons, label, geo, p, substituteVars);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint p, GeoBoolean substituteVars, GeoBoolean latex) {
		AlgoText algo = new AlgoText(cons, label, geo, p, substituteVars, latex);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Text of geo.
	 */
	final public GeoText Text(
		String label,
		GeoElement geo, GeoPoint p) {
		AlgoText algo = new AlgoText(cons, label, geo, p);
		GeoText t = algo.getGeoText();
		return t;
	}
	
	/** 
	 * Row of geo.
	 */
	final public GeoNumeric Row(
		String label,
		GeoElement geo) {
		AlgoRow algo = new AlgoRow(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Column of geo.
	 */
	final public GeoNumeric Column(
		String label,
		GeoElement geo) {
		AlgoColumn algo = new AlgoColumn(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToNumber
	 */
	final public GeoNumeric LetterToUnicode(
		String label,
		GeoText geo) {
		AlgoLetterToUnicode algo = new AlgoLetterToUnicode(cons, label, geo);
		GeoNumeric ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToNumbers
	 */
	final public GeoList TextToUnicode(
		String label,
		GeoText geo) {
		AlgoTextToUnicode algo = new AlgoTextToUnicode(cons, label, geo);
		GeoList ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * ToText(number)
	 */
	final public GeoText UnicodeToLetter(String label, NumberValue a) {
		AlgoUnicodeToLetter algo = new AlgoUnicodeToLetter(cons, label, a);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * ToText(list)
	 */
	final public GeoText UnicodeToText(
		String label,
		GeoList geo) {
		AlgoUnicodeToText algo = new AlgoUnicodeToText(cons, label, geo);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * returns the current x-axis step
	 * Michael Borcherds 
	 */
	final public GeoNumeric AxisStepX(
		String label) {
		AlgoAxisStepX algo = new AlgoAxisStepX(cons, label);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * returns the current y-axis step
	 * Michael Borcherds 
	 */
	final public GeoNumeric AxisStepY(
		String label) {
		AlgoAxisStepY algo = new AlgoAxisStepY(cons, label);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * returns the current construction protocol step
	 * Michael Borcherds 2008-05-15
	 */
	final public GeoNumeric ConstructionStep(
		String label) {
		AlgoConstructionStep algo = new AlgoConstructionStep(cons, label);
		GeoNumeric t = algo.getResult();
		return t;
	}
	
	/** 
	 * returns  current construction protocol step for an object
	 * Michael Borcherds 2008-05-15
	 */
	final public GeoNumeric ConstructionStep(
		String label, GeoElement geo) {
		AlgoStepObject algo = new AlgoStepObject(cons, label, geo);
		GeoNumeric t = algo.getResult();
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
	final public GeoPoint Point(String label, Path path, double x, double y, boolean addToConstruction) {
		boolean oldMacroMode = false;
		if (!addToConstruction) {
			oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);		

		}
		AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, x, y);
		GeoPoint p = algo.getP();        
		if (!addToConstruction) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}
	
	/** Point anywhere on path with    */
	final public GeoPoint Point(String label, Path path) {						
		// try (0,0)
		AlgoPointOnPath algo = new AlgoPointOnPath(cons, label, path, 0, 0);
		GeoPoint p = algo.getP(); 
		
		// try (1,0) 
		if (!p.isDefined()) {
			p.setCoords(1,0,1);
			algo.update();
		}
		
		// try (random(),0)
		if (!p.isDefined()) {
			p.setCoords(Math.random(),0,1);
			algo.update();
		}
				
		return p;
	}
	
	
	/** Point in region with cartesian coordinates (x,y)   */
	final public GeoPoint PointIn(String label, Region region, double x, double y) {
		AlgoPointInRegion algo = new AlgoPointInRegion(cons, label, region, x, y);
		Application.debug("PointIn - \n x="+x+"\n y="+y);
		GeoPoint p = algo.getP();    
		return p;
	}
	
	/** Point in region */
	final public GeoPoint PointIn(String label, Region region) {  
		return PointIn(label,region,0,0); //TODO do as for paths
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
		GeoVector v = (GeoVector) algo.getVector();
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
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
					NumberValue a, NumberValue b, GeoList list) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, a, b, list);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
			GeoList list1, GeoList list2) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, list1, list2);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
			GeoList list1, GeoList list2, NumberValue width) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, list1, list2, width);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
			GeoList list, GeoNumeric a) {
		AlgoBarChart algo = new AlgoBarChart(cons, label, list, a);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BarChart	
	 */
	final public GeoNumeric BarChart(String label, 
					NumberValue a, NumberValue b, GeoElement expression, GeoNumeric localVar, 
					NumberValue from, NumberValue to, NumberValue step) {
		
		AlgoSequence seq = new AlgoSequence(cons, expression, localVar, from, to, step);
		cons.removeFromConstructionList(seq);
		
		AlgoBarChart algo = new AlgoBarChart(cons, label, a, b, (GeoList)seq.getOutput()[0]);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BoxPlot
	 */
	final public GeoNumeric BoxPlot(String label, 
			NumberValue a, NumberValue b, GeoList rawData) {
		
		/*
		AlgoListMin min = new AlgoListMin(cons,rawData);
		cons.removeFromConstructionList(min);
		AlgoQ1 Q1 = new AlgoQ1(cons,rawData);
		cons.removeFromConstructionList(Q1);
		AlgoMedian median = new AlgoMedian(cons,rawData);
		cons.removeFromConstructionList(median);
		AlgoQ3 Q3 = new AlgoQ3(cons,rawData);
		cons.removeFromConstructionList(Q3);
		AlgoListMax max = new AlgoListMax(cons,rawData);
		cons.removeFromConstructionList(max);
	
		AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, (NumberValue)(min.getMin()),
				(NumberValue)(Q1.getQ1()), (NumberValue)(median.getMedian()), (NumberValue)(Q3.getQ3()), (NumberValue)(max.getMax()));
		*/
		
		AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, rawData);
		
		
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * BoxPlot
	 */
	final public GeoNumeric BoxPlot(String label, 
			NumberValue a, NumberValue b, NumberValue min, NumberValue Q1,
			NumberValue median, NumberValue Q3, NumberValue max) {
		AlgoBoxPlot algo = new AlgoBoxPlot(cons, label, a, b, min, Q1, median, Q3, max);
		GeoNumeric sum = algo.getSum();
		return sum;
	}
	
	/** 
	 * Histogram
	 */
	final public GeoNumeric Histogram(String label, 
					GeoList list1, GeoList list2) {
		AlgoHistogram algo = new AlgoHistogram(cons, label, list1, list2);
		GeoNumeric sum = algo.getSum();
		return sum;
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
	 * TrapezoidalSum of function f 
	 */
	final public GeoNumeric TrapezoidalSum(String label, GeoFunction f, 
					NumberValue a, NumberValue b, NumberValue n) {
		AlgoSumTrapezoidal algo = new AlgoSumTrapezoidal(cons, label, f, a, b, n);
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
	 * SumSquaredErrors[<List of Points>,<Function>]
	 * Hans-Petter Ulven
	 * 2010-02-22
	 */
	final public GeoNumeric SumSquaredErrors(String label, GeoList list, GeoFunction function) {
		AlgoSumSquaredErrors algo = new AlgoSumSquaredErrors(cons, label, list, function);
		GeoNumeric sse=algo.getsse();
		return sse;
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
	 * Mod[a, b] Polynomial remainder
	 */
	final public GeoFunction Mod(String label, GeoFunction a, GeoFunction b) {
		AlgoPolynomialMod algo = new AlgoPolynomialMod(cons, label, a, b);
		GeoFunction f = algo.getResult();
		return f;
	}
	
	/** 
	 * Div[a, b] Polynomial Division
	 */
	final public GeoFunction Div(String label, GeoFunction a, GeoFunction b) {
		AlgoPolynomialDiv algo = new AlgoPolynomialDiv(cons, label, a, b);
		GeoFunction f = algo.getResult();
		return f;
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
	 * LCM[a, b]
	 * Michael Borcherds
	 */
	final public GeoNumeric LCM(String label, NumberValue a, NumberValue b) {
		AlgoLCM algo = new AlgoLCM(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * LCM[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric LCM(String label, GeoList list) {
		AlgoListLCM algo = new AlgoListLCM(cons, label, list);
		GeoNumeric num = algo.getLCM();
		return num;
	}
	
	/** 
	 * GCD[a, b]
	 * Michael Borcherds
	 */
	final public GeoNumeric GCD(String label, NumberValue a, NumberValue b) {
		AlgoGCD algo = new AlgoGCD(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * GCD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric GCD(String label, GeoList list) {
		AlgoListGCD algo = new AlgoListGCD(cons, label, list);
		GeoNumeric num = algo.getGCD();
		return num;
	}
	
	/** 
	 * SigmaXY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXY(String label, GeoList list) {
		AlgoListSigmaXY algo = new AlgoListSigmaXY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaYY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaYY(String label, GeoList list) {
		AlgoListSigmaYY algo = new AlgoListSigmaYY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Covariance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Covariance(String label, GeoList list) {
		AlgoListCovariance algo = new AlgoListCovariance(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SXX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXX(String label, GeoList list) {
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo.isNumberValue())
		{  // list of numbers
			AlgoSXX algo = new AlgoSXX(cons, label, list);
			num = algo.getResult();
		}
		else
		{  // (probably) list of points
			AlgoListSXX algo = new AlgoListSXX(cons, label, list);			
			num = algo.getResult();
		}
		return num;
	}
	
	
	/** 
	 * SXY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXY(String label, GeoList list) {
		AlgoListSXY algo = new AlgoListSXY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SYY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SYY(String label, GeoList list) {
		AlgoListSYY algo = new AlgoListSYY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * MeanX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric MeanX(String label, GeoList list) {
		AlgoListMeanX algo = new AlgoListMeanX(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * MeanY[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric MeanY(String label, GeoList list) {
		AlgoListMeanY algo = new AlgoListMeanY(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * PMCC[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric PMCC(String label, GeoList list) {
		AlgoListPMCC algo = new AlgoListPMCC(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaXY algo = new AlgoDoubleListSigmaXY(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXX[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXX(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaXX algo = new AlgoDoubleListSigmaXX(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaYY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaYY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSigmaYY algo = new AlgoDoubleListSigmaYY(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Covariance[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Covariance(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListCovariance algo = new AlgoDoubleListCovariance(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SXX[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXX(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSXX algo = new AlgoDoubleListSXX(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SXY[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SXY(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListSXY algo = new AlgoDoubleListSXY(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * PMCC[list,list]
	 * Michael Borcherds
	 */
	final public GeoNumeric PMCC(String label, GeoList listX, GeoList listY) {
		AlgoDoubleListPMCC algo = new AlgoDoubleListPMCC(cons, label, listX, listY);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * FitLineY[list of coords]
	 * Michael Borcherds
	 */
	final public GeoLine FitLineY(String label, GeoList list) {
		AlgoFitLineY algo = new AlgoFitLineY(cons, label, list);
		GeoLine line = algo.getFitLineY();
		return line;
	}
	
	/** 
	 * FitLineX[list of coords]
	 * Michael Borcherds
	 */
	final public GeoLine FitLineX(String label, GeoList list) {
		AlgoFitLineX algo = new AlgoFitLineX(cons, label, list);
		GeoLine line = algo.getFitLineX();
		return line;
	}
	
	/** 
	 * FitPoly[list of coords,degree]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitPoly(String label, GeoList list, NumberValue degree) {
		AlgoFitPoly algo = new AlgoFitPoly(cons, label, list, degree);
		GeoFunction function = algo.getFitPoly();
		return function;
	}

	/** 
	 * FitExp[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitExp(String label, GeoList list) {
		AlgoFitExp algo = new AlgoFitExp(cons, label, list);
		GeoFunction function = algo.getFitExp();
		return function;
	}
   
	/** 
	 * FitLog[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitLog(String label, GeoList list) {
		AlgoFitLog algo = new AlgoFitLog(cons, label, list);
		GeoFunction function = algo.getFitLog();
		return function;
	}
	/** 
	 * FitPow[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitPow(String label, GeoList list) {
		AlgoFitPow algo = new AlgoFitPow(cons, label, list);
		GeoFunction function = algo.getFitPow();
		return function;
	}

	/** 
	 * FitSin[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitSin(String label, GeoList list) {
		AlgoFitSin algo = new AlgoFitSin(cons, label, list);
		GeoFunction function = algo.getFitSin();
		return function;
	}
	
	/** 
	 * FitLogistic[list of coords]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitLogistic(String label, GeoList list) {
		AlgoFitLogistic algo = new AlgoFitLogistic(cons, label, list);
		GeoFunction function = algo.getFitLogistic();
		return function;
	}	
	
	/** 
	 * Fit[list of points,list of functions]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction Fit(String label, GeoList ptslist,GeoList funclist) {
		AlgoFit algo = new AlgoFit(cons, label, ptslist,funclist);
		GeoFunction function = algo.getFit();
		return function;
	}	

	/**
	 * 'FitGrowth[<List of Points>]
	 * Hans-Petter Ulven
	 */
	final public GeoFunction FitGrowth(String label, GeoList list) {
		AlgoFitGrowth algo = new AlgoFitGrowth(cons, label, list);
		GeoFunction function=algo.getFitGrowth();
		return function;
	}

	/** 
	 * Binomial[n,r]
	 * Michael Borcherds
	 */
	final public GeoNumeric Binomial(String label, NumberValue a, NumberValue b) {
		AlgoBinomial algo = new AlgoBinomial(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomNormal[mean,variance]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomNormal(String label, NumberValue a, NumberValue b) {
		AlgoRandomNormal algo = new AlgoRandomNormal(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Random[max,min]
	 * Michael Borcherds
	 */
	final public GeoNumeric Random(String label, NumberValue a, NumberValue b) {
		AlgoRandom algo = new AlgoRandom(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomUniform[max,min]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomUniform(String label, NumberValue a, NumberValue b) {
		AlgoRandomUniform algo = new AlgoRandomUniform(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomBinomial[n,p]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomBinomial(String label, NumberValue a, NumberValue b) {
		AlgoRandomBinomial algo = new AlgoRandomBinomial(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * RandomPoisson[lambda]
	 * Michael Borcherds
	 */
	final public GeoNumeric RandomPoisson(String label, NumberValue a) {
		AlgoRandomPoisson algo = new AlgoRandomPoisson(cons, label, a);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * InverseNormal[mean,variance,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric InverseNormal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseNormal algo = new AlgoInverseNormal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Normal[mean,variance,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric Normal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoNormal algo = new AlgoNormal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * TDistribution[degrees of freedom,x]
	 * Michael Borcherds
	 */
	final public GeoNumeric TDistribution(String label, NumberValue a, NumberValue b) {
		AlgoTDistribution algo = new AlgoTDistribution(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseTDistribution(String label, NumberValue a, NumberValue b) {
		AlgoInverseTDistribution algo = new AlgoInverseTDistribution(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	final public GeoNumeric ChiSquared(String label, NumberValue a, NumberValue b) {
		AlgoChiSquared algo = new AlgoChiSquared(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseChiSquared(String label, NumberValue a, NumberValue b) {
		AlgoInverseChiSquared algo = new AlgoInverseChiSquared(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	final public GeoNumeric Exponential(String label, NumberValue a, NumberValue b) {
		AlgoExponential algo = new AlgoExponential(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseExponential(String label, NumberValue a, NumberValue b) {
		AlgoInverseExponential algo = new AlgoInverseExponential(cons, label, a, b);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric FDistribution(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoFDistribution algo = new AlgoFDistribution(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseFDistribution(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseFDistribution algo = new AlgoInverseFDistribution(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Gamma(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoGamma algo = new AlgoGamma(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseGamma(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseGamma algo = new AlgoInverseGamma(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Cauchy(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoCauchy algo = new AlgoCauchy(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseCauchy(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseCauchy algo = new AlgoInverseCauchy(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Weibull(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoWeibull algo = new AlgoWeibull(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseWeibull(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseWeibull algo = new AlgoInverseWeibull(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Zipf(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoZipf algo = new AlgoZipf(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseZipf(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInverseZipf algo = new AlgoInverseZipf(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric Pascal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoPascal algo = new AlgoPascal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InversePascal(String label, NumberValue a, NumberValue b, NumberValue c) {
		AlgoInversePascal algo = new AlgoInversePascal(cons, label, a, b, c);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric HyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d) {
		AlgoHyperGeometric algo = new AlgoHyperGeometric(cons, label, a, b, c, d);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	final public GeoNumeric InverseHyperGeometric(String label, NumberValue a, NumberValue b, NumberValue c, NumberValue d) {
		AlgoInverseHyperGeometric algo = new AlgoInverseHyperGeometric(cons, label, a, b, c, d);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	
	/** 
	 * Sort[list]
	 * Michael Borcherds
	 */
	final public GeoList Sort(String label, GeoList list) {
		AlgoSort algo = new AlgoSort(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Rank[list]
	 * Michael Borcherds
	 */
	final public GeoList Rank(String label, GeoList list) {
		AlgoRank algo = new AlgoRank(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Shuffle[list]
	 * Michael Borcherds
	 */
	final public GeoList Shuffle(String label, GeoList list) {
		AlgoShuffle algo = new AlgoShuffle(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * PointList[list]
	 * Michael Borcherds
	 */
	final public GeoList PointList(String label, GeoList list) {
		AlgoPointList algo = new AlgoPointList(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * RootList[list]
	 * Michael Borcherds
	 */
	final public GeoList RootList(String label, GeoList list) {
		AlgoRootList algo = new AlgoRootList(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * First[list,n]
	 * Michael Borcherds
	 */
	final public GeoList First(String label, GeoList list, GeoNumeric n) {
		AlgoFirst algo = new AlgoFirst(cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Last[list,n]
	 * Michael Borcherds
	 */
	final public GeoList Last(String label, GeoList list, GeoNumeric n) {
		AlgoLast algo = new AlgoLast(cons, label, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Take[list,m,n]
	 * Michael Borcherds
	 */
	final public GeoList Take(String label, GeoList list, GeoNumeric m, GeoNumeric n) {
		AlgoTake algo = new AlgoTake(cons, label, list, m, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Append[list,object]
	 * Michael Borcherds
	 */
	final public GeoList Append(String label, GeoList list, GeoElement geo) {
		AlgoAppend algo = new AlgoAppend(cons, label, list, geo);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Append[object,list]
	 * Michael Borcherds
	 */
	final public GeoList Append(String label, GeoElement geo, GeoList list) {
		AlgoAppend algo = new AlgoAppend(cons, label, geo, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Join[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Join(String label, GeoList list) {
		AlgoJoin algo = new AlgoJoin(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Union[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Union(String label, GeoList list, GeoList list1) {
		AlgoUnion algo = new AlgoUnion(cons, label, list, list1);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Intersection[list,list]
	 * Michael Borcherds
	 */
	final public GeoList Intersection(String label, GeoList list, GeoList list1) {
		AlgoIntersection algo = new AlgoIntersection(cons, label, list, list1);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Insert[list,list,n]
	 * Michael Borcherds
	 */
	final public GeoList Insert(String label, GeoElement geo, GeoList list, GeoNumeric n) {
		AlgoInsert algo = new AlgoInsert(cons, label, geo, list, n);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * RemoveUndefined[list]
	 * Michael Borcherds
	 */
	final public GeoList RemoveUndefined(String label, GeoList list) {
		AlgoRemoveUndefined algo = new AlgoRemoveUndefined(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Keep[boolean condition, list]
	 * Michael Borcherds
	 */
	final public GeoList KeepIf(String label, GeoFunction boolFun, GeoList list) {
		AlgoKeepIf algo = new AlgoKeepIf(cons, label, boolFun, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Defined[object]
	 * Michael Borcherds
	 */
	final public GeoBoolean Defined(String label, GeoElement geo) {
		AlgoDefined algo = new AlgoDefined(cons, label, geo);
		GeoBoolean result = algo.getResult();
		return result;
	}
	
	/** 
	 * IsInteger[number]
	 * Michael Borcherds
	 */
	final public GeoBoolean IsInteger(String label, GeoNumeric geo) {
		AlgoIsInteger algo = new AlgoIsInteger(cons, label, geo);
		GeoBoolean result = algo.getResult();
		return result;
	}
	
	/** 
	 * Mode[list]
	 * Michael Borcherds
	 */
	final public GeoList Mode(String label, GeoList list) {
		AlgoMode algo = new AlgoMode(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Invert[matrix]
	 * Michael Borcherds
	 */
	final public GeoList Invert(String label, GeoList list) {
		AlgoInvert algo = new AlgoInvert(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoList Transpose(String label, GeoList list) {
		AlgoTranspose algo = new AlgoTranspose(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoList ReducedRowEchelonForm(String label, GeoList list) {
		AlgoReducedRowEchelonForm algo = new AlgoReducedRowEchelonForm(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Transpose[matrix]
	 * Michael Borcherds
	 */
	final public GeoNumeric Determinant(String label, GeoList list) {
		AlgoDeterminant algo = new AlgoDeterminant(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Reverse[list]
	 * Michael Borcherds
	 */
	final public GeoList Reverse(String label, GeoList list) {
		AlgoReverse algo = new AlgoReverse(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}
	
	/** 
	 * Product[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Product(String label, GeoList list) {
		AlgoProduct algo = new AlgoProduct(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Sum[list]
	 * Michael Borcherds
	 */
	final public GeoElement Sum(String label, GeoList list) {
		AlgoSum algo = new AlgoSum(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list,n]
	 * Michael Borcherds
	 */
	final public GeoElement Sum(String label, GeoList list, GeoNumeric n) {
		AlgoSum algo = new AlgoSum(cons, label, list, n);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of functions]
	 * Michael Borcherds
	 */
	final public GeoElement SumFunctions(String label, GeoList list) {
		AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of functions,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumFunctions(String label, GeoList list, GeoNumeric num) {
		AlgoSumFunctions algo = new AlgoSumFunctions(cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points]
	 * Michael Borcherds
	 */
	final public GeoElement SumPoints(String label, GeoList list) {
		AlgoSumPoints algo = new AlgoSumPoints(cons, label, list);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumPoints(String label, GeoList list, GeoNumeric num) {
		AlgoSumPoints algo = new AlgoSumPoints(cons, label, list, num);
		GeoElement ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of points]
	 * Michael Borcherds
	 */
	final public GeoElement SumText(String label, GeoList list) {
		AlgoSumText algo = new AlgoSumText(cons, label, list);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Sum[list of text,n]
	 * Michael Borcherds
	 */
	final public GeoElement SumText(String label, GeoList list, GeoNumeric num) {
		AlgoSumText algo = new AlgoSumText(cons, label, list, num);
		GeoText ret = algo.getResult();
		return ret;
	}
	
	/** 
	 * Table[list]
	 * Michael Borcherds
	 */
	final public GeoText TableText(String label, GeoList list, GeoText args) {
		AlgoTableText algo = new AlgoTableText(cons, label, list, args);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * ToFraction[number]
	 * Michael Borcherds
	 */
	final public GeoText FractionText(String label, GeoNumeric num) {
		AlgoFractionText algo = new AlgoFractionText(cons, label, num);
		GeoText text = algo.getResult();
		return text;
	}
	
	/** 
	 * Mean[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Mean(String label, GeoList list) {
		AlgoMean algo = new AlgoMean(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * Variance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Variance(String label, GeoList list) {
		AlgoVariance algo = new AlgoVariance(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleVariance[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SampleVariance(String label, GeoList list) {
		AlgoSampleVariance algo = new AlgoSampleVariance(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric StandardDeviation(String label, GeoList list) {
		AlgoStandardDeviation algo = new AlgoStandardDeviation(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SampleSD[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SampleStandardDeviation(String label, GeoList list) {
		AlgoSampleStandardDeviation algo = new AlgoSampleStandardDeviation(cons, label, list);
		GeoNumeric num = algo.getResult();
		return num;
	}
	
	/** 
	 * SigmaXX[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric SigmaXX(String label, GeoList list) {
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo.isNumberValue())
		{  // list of numbers
			AlgoSigmaXX algo = new AlgoSigmaXX(cons, label, list);
			num = algo.getResult();
		}
		else
		{  // (probably) list of points
			AlgoListSigmaXX algo = new AlgoListSigmaXX(cons, label, list);			
			num = algo.getResult();
		}
		return num;
	}
	
	/** 
	 * Median[list]
	 * Michael Borcherds
	 */
	final public GeoNumeric Median(String label, GeoList list) {
		AlgoMedian algo = new AlgoMedian(cons, label, list);
		GeoNumeric num = algo.getMedian();
		return num;
	}
	
	/** 
	 * Q1[list] lower quartile
	 * Michael Borcherds
	 */
	final public GeoNumeric Q1(String label, GeoList list) {
		AlgoQ1 algo = new AlgoQ1(cons, label, list);
		GeoNumeric num = algo.getQ1();
		return num;
	}
	
	/** 
	 * Q3[list] upper quartile
	 * Michael Borcherds
	 */
	final public GeoNumeric Q3(String label, GeoList list) {
		AlgoQ3 algo = new AlgoQ3(cons, label, list);
		GeoNumeric num = algo.getQ3();
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
	 * Element[list, number, number]
	 */
	final public GeoElement Element(String label, GeoList list, NumberValue n, NumberValue m) {
		AlgoListElement algo = new AlgoListElement(cons, label, list, n, m);
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
	
	/** 
	 * Element[text, number]
	 */
	final public GeoElement Element(String label, GeoText text, NumberValue n) {
		AlgoTextElement algo = new AlgoTextElement(cons, label, text, n);
		GeoElement geo = algo.getText();
		return geo;
	}		
	
	/** 
	 * Length[text]
	 */
	final public GeoNumeric Length(String label, GeoText text) {
		AlgoTextLength algo = new AlgoTextLength(cons, label, text);
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
	
	//G.Sturr 2010-3-14
	/** 
	 * Polygon with vertices from geolist 
	 * Only the polygon is labeled, segments are not labeled
	 */
	final public GeoElement [] Polygon(String [] labels, GeoList pointList) {
		AlgoPolygon algo = new AlgoPolygon(cons, labels, pointList);
		return algo.getOutput();
	}
	//END G.Sturr
	
	
	/** 
	 * Regular polygon with vertices A and B and n total vertices.
	 * The labels name the polygon itself, its segments and points
	 */
	final public GeoElement [] RegularPolygon(String [] labels, GeoPoint A, GeoPoint B, NumberValue n) {
		AlgoPolygonRegular algo = new AlgoPolygonRegular(cons, labels, A, B, n);
		return algo.getOutput();
	}
	
	
	//G.Sturr 2010-3-14
	/** 
	 * Polygon formed by operation on two input polygons.
	 * Possible operations: addition, subtraction or intersection
     * The labels name the polygon itself, its segments and points
	 */	
	final public GeoElement [] PolygonOperation(String [] labels, GeoPolygon A, GeoPolygon B, NumberValue n) {
		AlgoPolygonOperation algo = new AlgoPolygonOperation(cons, labels, A, B,n);
		return algo.getOutput();
	}
	//END G.Sturr
	
	
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
	 * circle with midpoint M and radius BC
	 * Michael Borcherds 2008-03-14
	 */
	final public GeoConic Circle(
			// this is actually a macro
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C, boolean dummy) {

		AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, B, C, null);
		cons.removeFromConstructionList(algoSegment);
		
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A, algoSegment.getSegment(),true);
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint A and radius the same as circle
	 * Michael Borcherds 2008-03-14
	 */
	final public GeoConic Circle(
			// this is actually a macro
		String label,
		GeoPoint A,
		GeoConic c) {

		AlgoRadius radius = new AlgoRadius(cons, c);
		cons.removeFromConstructionList(radius);
		
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A, radius.getRadius());
		GeoConic circle = algo.getCircle();
		circle.setToSpecific();
		circle.update();
		notifyUpdate(circle);
		return circle;
	}

	/** 
	 * circle with midpoint M and radius segment
	 * Michael Borcherds 2008-03-15
	 */
	final public GeoConic Circle(
		String label,
		GeoPoint A,
		GeoSegment segment) {

		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, label, A, segment, true);
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
		GeoConic circle = (GeoConic) algo.getCircle();
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
	 * Corner of text Michael Borcherds 2007-11-26
	 */
	final public GeoPoint Corner(String label, GeoText txt, NumberValue number) {
		AlgoTextCorner algo = new AlgoTextCorner(cons, label, txt, number);	
		return algo.getCorner();
	}

	/** 
	 * Corner of Drawing Pad Michael Borcherds 2008-05-10
	 */
	final public GeoPoint CornerOfDrawingPad(String label, NumberValue number) {
		AlgoDrawingPadCorner algo = new AlgoDrawingPadCorner(cons, label, number);	
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
		GeoConic ellipse = algo.getConic();
		return ellipse;
	}

	/** 
	 * ellipse with foci A, B passing thorugh C
	 * Michael Borcherds 2008-04-06
	 */
	final public GeoConic Ellipse(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoEllipseFociPoint algo = new AlgoEllipseFociPoint(cons, label, A, B, C);
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
		GeoConic hyperbola = algo.getConic();
		return hyperbola;
	}

	/** 
	 * hyperbola with foci A, B passing thorugh C
	 * Michael Borcherds 2008-04-06
	 */
	final public GeoConic Hyperbola(
		String label,
		GeoPoint A,
		GeoPoint B,
		GeoPoint C) {
		AlgoHyperbolaFociPoint algo =
			new AlgoHyperbolaFociPoint(cons, label, A, B, C);
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
	 * IntersectLineConic yields intersection points named label1, label2
	 * of line g and conic c
	 */
	final public GeoPoint[] IntersectLineCubic(
		String[] labels,
		GeoLine g,
		GeoCubic c) {
		AlgoIntersectLineCubic algo = getIntersectionAlgorithm(g, c);
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
		
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false)) {
			
			// dummy point 
			GeoPoint A = new GeoPoint(cons);
			A.setZero();

			AlgoIntersectFunctionsNewton algo = new AlgoIntersectFunctionsNewton(cons, labels[0], a, b, A);
			GeoPoint[] ret = {algo.getIntersectionPoint()};
			return ret;
		}
			
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
				
		if (!f.isPolynomialFunction(false)) {
			
			// dummy point 
			GeoPoint A = new GeoPoint(cons);
			A.setZero();

			AlgoIntersectFunctionLineNewton algo = new AlgoIntersectFunctionLineNewton(cons, labels[0], f, l, A);
			GeoPoint[] ret = {algo.getIntersectionPoint()};
			return ret;

		}

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
	 
	 // intersect line and cubic
	 AlgoIntersectLineCubic getIntersectionAlgorithm(GeoLine g, GeoCubic c) {
		AlgoElement existingAlgo = findExistingIntersectionAlgorithm(g, c);
		if (existingAlgo != null) return (AlgoIntersectLineCubic) existingAlgo;
			
	 	// we didn't find a matching algorithm, so create a new one
		AlgoIntersectLineCubic algo = new AlgoIntersectLineCubic(cons, g, c);
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
	 * linear eccentricity of c
	 */
	final public GeoNumeric Excentricity(String label, GeoConic c) {
		AlgoExcentricity algo = new AlgoExcentricity(cons, label, c);
		GeoNumeric linearEccentricity = algo.getLinearEccentricity();
		return linearEccentricity;
	}

	/** 
	 * eccentricity of c
	 */
	final public GeoNumeric Eccentricity(String label, GeoConic c) {
		AlgoEccentricity algo = new AlgoEccentricity(cons, label, c);
		GeoNumeric eccentricity = algo.getEccentricity();
		return eccentricity;
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
		
		if (label == null)
			label = transformedGeoLabel(geoTrans.toGeoElement());

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
		if (label == null)
			label = transformedGeoLabel(geoRot.toGeoElement());
		
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
		if (label == null)
			label = transformedGeoLabel(geoRot.toGeoElement());
		
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
		if (label == null)
			label = transformedGeoLabel(geoRot.toGeoElement());
		
		if (geoRot.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoRot).createTransformedObject(TRANSFORM_DILATE, label, S, null, null, r);
		
		// standard case
		AlgoDilate algo = new AlgoDilate(cons, label, geoRot, r, S);
		GeoElement ret = algo.getResult();
		ret.setVisualStyleForTransformations((GeoElement) geoRot);
		GeoElement[] geos = { ret };
		return geos;
	}

	/**
	 * mirror geoMir at point Q
	 */
	final public GeoElement [] Mirror(String label, Mirrorable geoMir, GeoPoint Q) {	
		if (label == null)
			label = transformedGeoLabel(geoMir.toGeoElement());
		
		if (geoMir.toGeoElement().isLimitedPath())
			// handle segments, rays and arcs separately
			return ((LimitedPath) geoMir).createTransformedObject(TRANSFORM_MIRROR_AT_POINT, label, Q, null, null, null);
		
		// standard case
		AlgoMirror algo = new AlgoMirror(cons, label, geoMir, Q);
		GeoElement ret = algo.getResult();
		ret.setVisualStyleForTransformations((GeoElement) geoMir);
		GeoElement[] geos = { ret };
		return geos;
	}

	/**
	 * mirror (invert) point Q in circle 
	 * Michael Borcherds 2008-02-10
	 */
	final public GeoElement [] Mirror(String label, GeoPoint Q, GeoConic conic) {	
		if (label == null)
			label = transformedGeoLabel(Q);
	
		AlgoMirror algo = new AlgoMirror(cons, label, Q, conic);
		GeoElement ret = algo.getResult();
		ret.setVisualStyleForTransformations((GeoElement) Q);
		GeoElement[] geos = { ret };
		return geos;
	}

	/**
	 * apply matrix 
	 * Michael Borcherds 2010-05-27
	 */
	final public GeoElement [] ApplyMatrix(String label, MatrixTransformable Q, GeoList matrix) {	
		if (label == null)
			label = transformedGeoLabel((GeoElement)Q);
	
		AlgoApplyMatrix algo = new AlgoApplyMatrix(cons, label, Q, matrix);
		GeoElement ret = algo.getResult();
		ret.setVisualStyleForTransformations((GeoElement) Q);
		GeoElement[] geos = { ret };
		return geos;
	}

	/**
	 * mirror (invert) circle conic0 in circle conic1 
	 * Michael Borcherds 2008-02-10
	 */
	final public GeoElement [] Mirror(String label, GeoConic conic0, GeoConic conic1) {	
		if (label == null)
			label = transformedGeoLabel(conic0);
	
		AlgoMirror algo = new AlgoMirror(cons, label, conic0, conic1);		
		GeoElement ret = algo.getResult();
		ret.setVisualStyleForTransformations((GeoElement) conic0);
		GeoElement[] geos = { ret };
		return geos;	
	}

	/**
	 * mirror geoMir at line g
	 */
	final public GeoElement [] Mirror(String label, Mirrorable geoMir, GeoLine g) {
		if (label == null)
			label = transformedGeoLabel(geoMir.toGeoElement());
		
		if (geoMir.toGeoElement().isLimitedPath()) {
			// handle segments, rays and arcs separately
			GeoElement [] geos =  ((LimitedPath) geoMir).createTransformedObject(TRANSFORM_MIRROR_AT_LINE, label, null, g, null, null);
			
//			if (geos[0] instanceof Orientable && geoMir instanceof Orientable)
//				((Orientable)geos[0]).setOppositeOrientation( (Orientable)geoMir);
			
			return geos;
		}
		// standard case
		AlgoMirror algo = new AlgoMirror(cons, label, geoMir, g);
		GeoElement ret = algo.getResult();
		ret.setVisualStyleForTransformations((GeoElement) geoMir);
		GeoElement[] geos = { ret };
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
			newPoints[i] = (GeoPoint) Translate(transformedGeoLabel(points[i]), points[i], v)[0]; 				
			newPoints[i].setVisualStyleForTransformations(points[i]);
		}			
		return newPoints;
	}
	
	private static String transformedGeoLabel(GeoElement geo) {
		if (geo.isLabelSet() && !geo.hasIndexLabel() && !geo.label.endsWith("'''")) {
			return geo.label + "'";
		} else {
			return null;
		}
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
			String pointLabel = transformedGeoLabel(points[i]);
			if (Q == null)
				rotPoints[i] = (GeoPoint) Rotate(pointLabel, points[i], phi)[0]; 	
			else
				rotPoints[i] = (GeoPoint) Rotate(pointLabel, points[i], phi, Q)[0];
			rotPoints[i].setVisualStyleForTransformations(points[i]);
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
			String pointLabel = transformedGeoLabel(points[i]);
			newPoints[i] = (GeoPoint) Dilate(pointLabel, points[i], r, S)[0];			
			newPoints[i].setVisualStyleForTransformations(points[i]);
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
			String pointLabel = transformedGeoLabel(points[i]);
			if (Q == null)
				newPoints[i] = (GeoPoint) Mirror(pointLabel, points[i], g)[0]; 	
			else
				newPoints[i] = (GeoPoint) Mirror(pointLabel, points[i], Q)[0];
			newPoints[i].setVisualStyleForTransformations(points[i]);
		}			
		return newPoints;
	}		
	
	private GeoElement [] transformPoly(String label, GeoPolygon oldPoly, GeoPoint [] transformedPoints) {
		// get label for polygon
		String [] polyLabel = null;		
		if (label == null) {							
			if (oldPoly.isLabelSet()) {		
				polyLabel = new String[1];
				polyLabel[0] = transformedGeoLabel(oldPoly);
			}			
		} else {
			polyLabel = new String[1];
			polyLabel[0] = label;
		}
		
		// use visibility of points for transformed points
		GeoPoint [] oldPoints = oldPoly.getPoints();
		for (int i=0; i < oldPoints.length; i++) {
			transformedPoints[i].setEuclidianVisible(oldPoints[i].isSetEuclidianVisible());			
			transformedPoints[i].setVisualStyleForTransformations(oldPoints[i]);
			notifyUpdate(transformedPoints[i]);
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
	
	public static boolean keepOrientationForTransformation(int transformationType) {
		switch (transformationType) {
			case TRANSFORM_MIRROR_AT_LINE:	
				return false;
			
			default:
				return true;									
		}
	}
	
	GeoPoint [] transformPoints(int type, GeoPoint [] points, GeoPoint Q, GeoLine l, GeoVector vec, NumberValue n) {
		GeoPoint [] result = null;
		
		switch (type) {
			case TRANSFORM_TRANSLATE:
				result = translatePoints(points, vec);	
				break;
				
			case TRANSFORM_MIRROR_AT_POINT:
				result = mirrorPoints(points, Q, null);	
				break;
				
			case TRANSFORM_MIRROR_AT_LINE:	
				result = mirrorPoints(points, null, l);	
				break;
				
			case TRANSFORM_ROTATE:
				result = rotPoints(points, n, null);		
				break;
				
			case TRANSFORM_ROTATE_AROUND_POINT:
				result = rotPoints(points, n, Q);	
				break;
				
			case TRANSFORM_DILATE:
				result = dilatePoints(points, n, Q);	
				break;
				
			default:
				return null;			
		}
				
		// use visibility of points for transformed points
		for (int i=0; i < points.length; i++) {
			result[i].setEuclidianVisible(points[i].isSetEuclidianVisible());			
			result[i].setVisualStyleForTransformations(points[i]);
			notifyUpdate(result[i]);
		}
		return result;
	}	
	
	GeoLine getTransformedLine(int type, GeoLine line, GeoPoint Q, GeoLine l,
			GeoVector vec, NumberValue n) {

		GeoLine ret = null;

		switch (type) {
		case Kernel.TRANSFORM_TRANSLATE:
			AlgoTranslate algoTrans = new AlgoTranslate(cons, line, vec);
			ret = (GeoLine) algoTrans.getResult();
			break;

		case Kernel.TRANSFORM_MIRROR_AT_POINT:
		case Kernel.TRANSFORM_MIRROR_AT_LINE:
			AlgoMirror algoMirror = new AlgoMirror(cons, line, l, Q, null);
			ret = (GeoLine) algoMirror.getResult();
			break;

		case Kernel.TRANSFORM_ROTATE:
			AlgoRotate algoRotate = new AlgoRotate(cons, line, n);
			ret = (GeoLine) algoRotate.getResult();
			break;

		case Kernel.TRANSFORM_ROTATE_AROUND_POINT:
			AlgoRotatePoint algoRotatePoint = new AlgoRotatePoint(cons, line,
					n, Q);
			ret = (GeoLine) algoRotatePoint.getResult();
			break;

		case Kernel.TRANSFORM_DILATE:
			AlgoDilate algoDilate = new AlgoDilate(cons, line, n, Q);
			ret = (GeoLine) algoDilate.getResult();
			break;

		default:
			return null;
		}
		ret.setVisualStyleForTransformations(line);
		return ret;
	}

	GeoConic getTransformedConic(int type, GeoConic conic, GeoPoint Q,
			GeoLine l, GeoVector vec, NumberValue n) {

		GeoConic ret;

		switch (type) {
		case Kernel.TRANSFORM_TRANSLATE:
			AlgoTranslate algoTrans = new AlgoTranslate(cons, conic, vec);
			ret = (GeoConic) algoTrans.getResult();
			break;

		case Kernel.TRANSFORM_MIRROR_AT_POINT:
		case Kernel.TRANSFORM_MIRROR_AT_LINE:
			AlgoMirror algoMirror = new AlgoMirror(cons, conic, l, Q, null);
			ret = (GeoConic) algoMirror.getResult();
			break;

		case Kernel.TRANSFORM_ROTATE:
			AlgoRotate algoRotate = new AlgoRotate(cons, conic, n);
			ret = (GeoConic) algoRotate.getResult();
			break;

		case Kernel.TRANSFORM_ROTATE_AROUND_POINT:
			AlgoRotatePoint algoRotatePoint = new AlgoRotatePoint(cons, conic,
					n, Q);
			ret = (GeoConic) algoRotatePoint.getResult();
			break;

		case Kernel.TRANSFORM_DILATE:
			AlgoDilate algoDilate = new AlgoDilate(cons, conic, n, Q);
			ret = (GeoConic) algoDilate.getResult();
			break;

		default:
			return null;
		}

		ret.setVisualStyleForTransformations(conic);
		return ret;
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
	 * Fits a polynomial exactly to a list of coordinates
	 * Michael Borcherds 2008-01-22
	 */
	final public GeoFunction PolynomialFunction(String label, GeoList list) {		
		AlgoPolynomialFromCoordinates algo = new AlgoPolynomialFromCoordinates(cons, label, list);
		return algo.getPolynomial();			
	}
	
	/**
	 * Expand function expression
	 * @author Michael Borcherds 2008-04-04
	 */
	final public GeoElement Expand(String label, GeoFunction func) {		
		AlgoExpand algo = new AlgoExpand(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Simplify function expression
	 * @author Michael Borcherds 2008-04-04
	 */
	final public GeoElement Simplify(String label, GeoFunction func) {		
		AlgoSimplify algo = new AlgoSimplify(cons, label, func);
		return algo.getResult();			
	}
	
	final public GeoElement DynamicCoordinates(String label, GeoPoint geoPoint,
			NumberValue num1, NumberValue num2) {
		AlgoDynamicCoordinates algo = new AlgoDynamicCoordinates(cons, label, geoPoint, num1, num2);
		return algo.getPoint();
	}

	/**
	 * Factor
	 * Michael Borcherds 2008-04-04
	 */
	final public GeoFunction Factor(String label, GeoFunction func) {		
		AlgoFactor algo = new AlgoFactor(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Factors
	 * Michael Borcherds 
	 */
	final public GeoList Factors(String label, GeoFunction func) {		
		AlgoFactors algo = new AlgoFactors(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Asymptotes
	 * Michael Borcherds 
	 */
	final public GeoList AsymptoteFunction(String label, GeoFunction func) {		
		AlgoAsymptoteFunction algo = new AlgoAsymptoteFunction(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Numerator
	 * Michael Borcherds 
	 */
	final public GeoFunction Numerator(String label, GeoFunction func) {		
		AlgoNumerator algo = new AlgoNumerator(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Denominator
	 * Michael Borcherds 
	 */
	final public GeoFunction Denominator(String label, GeoFunction func) {		
		AlgoDenominator algo = new AlgoDenominator(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Degree
	 * Michael Borcherds 
	 */
	final public GeoNumeric Degree(String label, GeoFunction func) {		
		AlgoDegree algo = new AlgoDegree(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Limit
	 * Michael Borcherds 
	 */
	final public GeoNumeric Limit(String label, GeoFunction func, NumberValue num) {		
		AlgoLimit algo = new AlgoLimit(cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * LimitBelow
	 * Michael Borcherds 
	 */
	final public GeoNumeric LimitBelow(String label, GeoFunction func, NumberValue num) {		
		AlgoLimitBelow algo = new AlgoLimitBelow(cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * LimitAbove
	 * Michael Borcherds 
	 */
	final public GeoNumeric LimitAbove(String label, GeoFunction func, NumberValue num) {		
		AlgoLimitAbove algo = new AlgoLimitAbove(cons, label, func, num);
		return algo.getResult();			
	}
	
	/**
	 * Partial Fractions
	 * Michael Borcherds 
	 */
	final public GeoFunction PartialFractions(String label, GeoFunction func) {		
		AlgoPartialFractions algo = new AlgoPartialFractions(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Coefficients
	 * Michael Borcherds 2008-04-04
	 */
	final public GeoList Coefficients(String label, GeoFunction func) {		
		AlgoCoefficients algo = new AlgoCoefficients(cons, label, func);
		return algo.getResult();			
	}
	
	/**
	 * Coefficients
	 * Michael Borcherds 2008-04-04
	 */
	final public GeoList Coefficients(String label, GeoConic func) {		
		AlgoConicCoefficients algo = new AlgoConicCoefficients(cons, label, func);
		return algo.getResult();			
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
	 * 
	 */
	final public GeoPoint [] PointsFromList(String [] labels, GeoList list) {
		
		AlgoPointsFromList algo = new AlgoPointsFromList(cons, labels, true, list);
		GeoPoint [] g = algo.getPoints();
		return g;
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
	
	final public boolean isReal(Complex c) {
		return isZero(c.getImaginary());
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

	final public boolean isInteger(double x) {
		if (x > 1E17)
			return true;
		else
			return isEqual(x, Math.round(x));		
	}

	/**
	 * Returns whether x is equal to y	 
	 * infinity == infinity returns true eg 1/0	 
	 * -infinity == infinity returns false	 eg -1/0
	 * -infinity == -infinity returns true
	 * undefined == undefined returns false eg 0/0	 
	 */
	final public boolean isEqual(double x, double y) {	
		if (x == y) // handles infinity and NaN cases
			return true;
		else
			return x - EPSILON <= y && y <= x + EPSILON;
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
	
    final public double convertToAngleValue(double val) {
		if (val > EPSILON && val < PI_2) return val;
		
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
	private double[] temp;// = new double[6];

	// lhs of implicit equation without constant coeff
	final private StringBuilder buildImplicitVarPart(		
		double[] numbers,
		String[] vars, 
		boolean KEEP_LEADING_SIGN,
		boolean CANCEL_DOWN) {
		
		temp = new double[numbers.length];
			
		int leadingNonZero = -1;
		sbBuildImplicitVarPart.setLength(0);

		for (int i = 0; i < vars.length; i++) {
			if (!isZero(numbers[i])) {
				leadingNonZero = i;
				break;
			}
		}
		
		if (CANCEL_DOWN) {
			// check if integers and divide through gcd
			boolean allIntegers = true;
			for (int i = 0; i < numbers.length; i++) {
				allIntegers = allIntegers && isInteger(numbers[i]);			
			}		
			if (allIntegers) {
				// divide by greates common divisor
				divide(numbers, gcd(numbers), numbers);
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
		String strCoeff = formatCoeff(temp[leadingNonZero]);
		sbBuildImplicitVarPart.append(strCoeff);
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

			if (abs >= PRINT_PRECISION || useSignificantFigures) {
				sbBuildImplicitVarPart.append(sign);
				sbBuildImplicitVarPart.append(formatCoeff(abs));
				sbBuildImplicitVarPart.append(vars[i]);
			}
		}
		return sbBuildImplicitVarPart;
	}
	private StringBuilder sbBuildImplicitVarPart = new StringBuilder(80);

	public final StringBuilder buildImplicitEquation(
		double[] numbers,
		String[] vars,
		boolean KEEP_LEADING_SIGN,
		boolean CANCEL_DOWN) {

		sbBuildImplicitEquation.setLength(0);
		sbBuildImplicitEquation.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN, CANCEL_DOWN));
		if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) 
			sbBuildImplicitEquation.append(" == ");
		else
			sbBuildImplicitEquation.append(" = ");
		
		// temp is set by buildImplicitVarPart
		sbBuildImplicitEquation.append(format(-temp[vars.length]));

		return sbBuildImplicitEquation;
	}
	private StringBuilder sbBuildImplicitEquation = new StringBuilder(80);

	// lhs of lhs = 0
	final public StringBuilder buildLHS(double[] numbers, String[] vars, boolean KEEP_LEADING_SIGN, boolean CANCEL_DOWN) {
		sbBuildLHS.setLength(0);
		sbBuildLHS.append(buildImplicitVarPart(numbers, vars, KEEP_LEADING_SIGN, CANCEL_DOWN));

		// add constant coeff
		double coeff = temp[vars.length];
		if (Math.abs(coeff) >= PRINT_PRECISION || useSignificantFigures) {
			sbBuildLHS.append(' ');
			sbBuildLHS.append(sign(coeff));
			sbBuildLHS.append(' ');
			sbBuildLHS.append(format(Math.abs(coeff)));
		}
		return sbBuildLHS;
	}
	private StringBuilder sbBuildLHS = new StringBuilder(80);

	// form: y� = f(x) (coeff of y = 0)
	final StringBuilder buildExplicitConicEquation(
		double[] numbers,
		String[] vars,
		int pos,
		boolean KEEP_LEADING_SIGN) {
		// y�-coeff is 0
		double d, dabs, q = numbers[pos];
		// coeff of y� is 0 or coeff of y is not 0
		if (isZero(q))
			return buildImplicitEquation(numbers, vars, KEEP_LEADING_SIGN, true);

		int i, leadingNonZero = numbers.length;
		for (i = 0; i < numbers.length; i++) {
			if (i != pos
				&& // except y� coefficient                
				(Math.abs(numbers[i]) >= PRINT_PRECISION || useSignificantFigures)) {
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
					if (dabs >= PRINT_PRECISION || useSignificantFigures) {
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
			if (dabs >= PRINT_PRECISION || useSignificantFigures) {
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(sign(d));
				sbBuildExplicitConicEquation.append(' ');
				sbBuildExplicitConicEquation.append(format(dabs));
			}
			
			//Application.debug(sbBuildExplicitConicEquation.toString());
			
			return sbBuildExplicitConicEquation;
		}
	}
	private StringBuilder sbBuildExplicitConicEquation = new StringBuilder(80);

	// y = k x + d
	final StringBuilder buildExplicitLineEquation(
		double[] numbers,
		String[] vars) {

		double d, dabs, q = numbers[1];		
		sbBuildExplicitLineEquation.setLength(0);
		
		//	BUILD EQUATION STRING                      
		// special case
		// y-coeff is 0: form x = constant
		if (isZero(q)) {
			sbBuildExplicitLineEquation.append("x");
						
			if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) 
				sbBuildExplicitLineEquation.append(" == ");
			else
				sbBuildExplicitLineEquation.append(" = ");
			
			sbBuildExplicitLineEquation.append(format(-numbers[2] / numbers[0]));
			return sbBuildExplicitLineEquation;
		}

		// standard case: y-coeff not 0
		sbBuildExplicitLineEquation.append("y");
		if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) 
			sbBuildExplicitLineEquation.append(" == ");
		else
			sbBuildExplicitLineEquation.append(" = ");

		// x coeff
		d = -numbers[0] / q;
		dabs = Math.abs(d);
		if (dabs >= PRINT_PRECISION || useSignificantFigures) {
			sbBuildExplicitLineEquation.append(formatCoeff(d));
			sbBuildExplicitLineEquation.append('x');

			// constant            
			d = -numbers[2] / q;
			dabs = Math.abs(d);
			if (dabs >= PRINT_PRECISION || useSignificantFigures) {
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
	private StringBuilder sbBuildExplicitLineEquation = new StringBuilder(50);

	/*
	final private String formatAbs(double x) {
		if (isZero(x))
			return "0";
		else
			return formatNF(Math.abs(x));
	}*/

	/** doesn't show 1 or -1 */
	final private String formatCoeff(double x) {
		if (Math.abs(x) == 1.0) {
			if (x > 0.0)
				return "";
			else
				return "-";
		} else {
			String numberStr = format(x);
			if (casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) 
				return numberStr + "*";
			else {
				// standard case
				return numberStr;
			}
		}
	}
	
	
	
	////////////////////////////////////////////////
	// FORMAT FOR NUMBERS
	////////////////////////////////////////////////
	
	public double axisNumberDistance(double units, DecimalFormat numberFormat){

		// calc number of digits
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp, getPrintDecimals());
		
		// format the numbers
		numberFormat.applyPattern("###0.##");	
		numberFormat.setMaximumFractionDigits(maxFractionDigtis);
		
		// calc the distance
		double pot = Math.pow(10, exp);
		double n = units / pot;
		double distance;

		if (n > 5) {
			distance = 5 * pot;
		} else if (n > 2) {
			distance = 2 * pot;
		} else {
			distance = pot;
		}
		
		return distance;
	}
	
	
	private StringBuilder formatSB;
	
	/**
	 * Formats the value of x using the currently set
	 * NumberFormat or ScientificFormat. This method also
	 * takes getCasPrintForm() into account.
	 * 
	 * converts to localised digits if appropriate
	 */
	final public String format(double x) {	
		if (Application.unicodeZero != '0') {
			
			String num = formatRaw(x);
			
			return internationalizeDigits(num);
			
			
		} else return formatRaw(x);
		
	}
	
	/*
	 * swaps the digits in num to the current locale's
	 */
	public String internationalizeDigits(String num) {
		if (formatSB == null) formatSB = new StringBuilder(17);
		else formatSB.setLength(0);
		
		boolean reverseOrder = app.isRightToLeftDigits();
		
		int length = num.length();
		
		for (int i = 0 ; i < num.length() ; i++) {
			char c = num.charAt(i);
			//char c = reverseOrder ? num.charAt(length - 1 - i) : num.charAt(i);
			if (c == '.') c = Application.unicodeDecimalPoint;
			else if (c >= '0' && c <= '9') {
				
				c += Application.unicodeZero - '0'; // convert to eg Arabic Numeral
				
			}

			// make sure the minus is treated as part of the number in eg Arabic
			if ( reverseOrder && c=='-'){
				formatSB.append(Unicode.RightToLeftMark);
				formatSB.append(c);
				formatSB.append(Unicode.RightToLeftMark);				
			} 
			else
				formatSB.append(c);
		}
		
		return formatSB.toString();
		
	}

		

	/**
	 * Formats the value of x using the currently set
	 * NumberFormat or ScientificFormat. This method also
	 * takes getCasPrintForm() into account.
	 */
	final public String formatRaw(double x) {		
		switch (casPrintForm) {
			// number formatting for XML string output
			case ExpressionNode.STRING_TYPE_GEOGEBRA_XML:
				return Double.toString(x);		
		
			// number formatting for CAS
			case ExpressionNode.STRING_TYPE_MATH_PIPER:				
			case ExpressionNode.STRING_TYPE_JASYMCA:		
			case ExpressionNode.STRING_TYPE_MAXIMA:		
				if (Double.isNaN(x))
					return " 1/0 ";	
				else if (Double.isInfinite(x)) {
					if (casPrintForm == ExpressionNode.STRING_TYPE_MAXIMA) return (x<0) ? "-infinity" : "infinity";
					return Double.toString(x); // "Infinity" or "-Infinity"
 				}
				else {			
					double abs = Math.abs(x);
					// number small enough that Double.toString() won't create E notation
					if (abs >= 10E-3 && abs < 10E7) {
						long round = Math.round(x);
						if (x == round) {
							return Long.toString(round);
						} else {
							return Double.toString(x);	
						}
					}
					// number would produce E notation with Double.toString()
					else {						
						// convert scientific notation 1.0E-20 to 1*10^(-20) 
						String scientificStr = Double.toString(x);
						StringBuilder sb = new StringBuilder(scientificStr.length() * 2);
						boolean Efound = false;
						for (int i=0; i < scientificStr.length(); i++) {
							char ch = scientificStr.charAt(i);
							if (ch == 'E') {
								sb.append("*10^(");
								Efound = true;
							} else {
								sb.append(ch);
							}
						}
						if (Efound)
							sb.append(")");
						
						// TODO: remove
						//Application.printStacktrace(sb.toString());
						
						return sb.toString();
					}					
				}
								
			// number formatting for screen output
			default:
				if (Double.isNaN(x))
					return "?";	
				else if (Double.isInfinite(x)) {
					return (x > 0) ? "\u221e" : "-\u221e"; // infinity
				}
				else if (x == Math.PI) {
					return casPrintFormPI;
				}	
					
			// ROUNDING hack							
			// NumberFormat and SignificantFigures use ROUND_HALF_EVEN as 
			// default which is not changeable, so we need to hack this 
			// to get ROUND_HALF_UP like in schools: increase abs(x) slightly
			//    x = x * ROUND_HALF_UP_FACTOR;
			// We don't do this for large numbers as 
				double abs = Math.abs(x);
				if (abs < 10E7) {
					// increase abs(x) slightly to round up
					x = x * ROUND_HALF_UP_FACTOR;
				}
	
				if (useSignificantFigures) {	
					return formatSF(x);
				} else {				
					return formatNF(x);
				}
			}								
	}
	
	
	/**
	 * Uses current NumberFormat nf to format a number.
	 */
	final private String formatNF(double x) {
		// "<=" catches -0.0000000000000005
		// should be rounded to -0.000000000000001 (15 d.p.)
		// but nf.format(x) returns "-0" 
		if (-PRINT_PRECISION / 2 <= x && x < PRINT_PRECISION / 2) {
			// avoid output of "-0" for eg -0.0004
			return "0";
		} else {
			// standard case
			return nf.format(x);
		}
	}

	/**
	 * Uses current ScientificFormat sf to format a number. Makes sure ".123" is
	 * returned as "0.123".
	 */
	final private String formatSF(double x) {
		if (sbFormatSF == null)
			sbFormatSF = new StringBuilder();
		else
			sbFormatSF.setLength(0);

		// get scientific format
		String absStr;
		if (x == 0) {
			// avoid output of "-0.00"
			absStr = sf.format(0);
		}
		else if (x > 0) {
			absStr = sf.format(x);
		} 
		else {
			sbFormatSF.append('-');
			absStr = sf.format(-x);
		}

		// make sure ".123" is returned as "0.123".
		if (absStr.charAt(0) == '.')
			sbFormatSF.append('0');
		sbFormatSF.append(absStr);

		return sbFormatSF.toString();
	}
	private StringBuilder sbFormatSF;
	
	/**
	 * calls formatPiERaw() and converts to localised digits if appropriate
	 */
	final public String formatPiE(double x, NumberFormat numF) {	
		if (Application.unicodeZero != '0') {
			
			String num = formatPiERaw(x, numF);
			
			return internationalizeDigits(num);
			
			
		} else return formatPiERaw(x, numF);
		
	}

	final public String formatPiERaw(double x, NumberFormat numF) {		
		// PI
		if (x == Math.PI) {
			return casPrintFormPI;
		}
				
		// 	MULTIPLES OF PI/2
		// i.e. x = a * pi/2
		double a = 2*x / Math.PI;
		int aint = (int) Math.round(a);
		if (sbFormat == null)
			sbFormat = new StringBuilder();
		sbFormat.setLength(0);
		if (isEqual(a, aint, STANDARD_PRECISION)) {	
			switch (aint) {		
				case 0:
					return "0";		
					
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
		// use numberformat to get number string
		// checkDecimalFraction() added to avoid 2.19999999999999 when set to 15dp
		String str = numF.format(checkDecimalFraction(x));
		sbFormat.append(str);	
		// if number is in scientific notation and ends with "E0", remove this
		if (str.endsWith("E0"))
			sbFormat.setLength(sbFormat.length() - 2);
		return sbFormat.toString();
	}
	private StringBuilder sbFormat;


	final public String formatSignedCoefficient(double x) {
		if (x == -1.0)
			return "- ";
		if (x == 1.0)
			return "+ ";

		return formatSigned(x).toString();
	}
		
	final public StringBuilder formatSigned(double x) {
		sbFormatSigned.setLength(0);		
		
		if (x >= 0.0d) {
			sbFormatSigned.append("+ ");
			sbFormatSigned.append( format(x));
			return sbFormatSigned;
		} else {
			sbFormatSigned.append("- ");
			sbFormatSigned.append( format(-x));
			return sbFormatSigned;
		}
	}
	private StringBuilder sbFormatSigned = new StringBuilder(40);

	final public StringBuilder formatAngle(double phi) {
		// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
		return formatAngle(phi, 10);
	}
	
	final public StringBuilder formatAngle(double phi, double precision) {
		sbFormatAngle.setLength(0);
		switch (casPrintForm) {
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
			case ExpressionNode.STRING_TYPE_JASYMCA:
				if (angleUnit == ANGLE_DEGREE) {
					sbFormatAngle.append("(");
					// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
					sbFormatAngle.append(format(checkDecimalFraction(Math.toDegrees(phi), precision)));
					sbFormatAngle.append("*");
					sbFormatAngle.append("\u00b0");
					sbFormatAngle.append(")");
				} else {
					sbFormatAngle.append(format(phi));					
				}
				return sbFormatAngle;				
				
			default:
				// STRING_TYPE_GEOGEBRA_XML
				// STRING_TYPE_GEOGEBRA

				if (Double.isNaN(phi)) {
					sbFormatAngle.append("?");
					return sbFormatAngle;
				}		
				
				if (angleUnit == ANGLE_DEGREE) {
					phi = Math.toDegrees(phi);
					if (phi < 0) 
						phi += 360;	
					else if (phi > 360)
						phi = phi % 360;
					// STANDARD_PRECISION * 10 as we need a little leeway as we've converted from radians
					sbFormatAngle.append(format(checkDecimalFraction(phi, precision)));
					
					if (casPrintForm == ExpressionNode.STRING_TYPE_GEOGEBRA_XML) {
						sbFormatAngle.append("*");
					}
					sbFormatAngle.append('\u00b0');
					return sbFormatAngle;
				} 
				else {
					// RADIANS
					sbFormatAngle.append(format(phi));
					
					if (casPrintForm != ExpressionNode.STRING_TYPE_GEOGEBRA_XML) {
						sbFormatAngle.append(" rad");
					}
					return sbFormatAngle;
				}
		}
		
		
	}
	private StringBuilder sbFormatAngle = new StringBuilder(40);

	final private static char sign(double x) {
		if (x > 0)
			return '+';
		else
			return '-';
	}

	/**
	 * greatest common divisor
	 */
	final public static long gcd(long m, long n) {
		// Return the GCD of positive integers m and n.
		if (m == 0 || n == 0)
			return Math.max(Math.abs(m), Math.abs(n));

		long p = m, q = n;
		while (p % q != 0) {
			long r = p % q;
			p = q;
			q = r;
		}
		return q;
	}

	/**
	 * Compute greatest common divisor of given doubles.
	 * Note: all double values are cast to long.
	 */
	final public static double gcd(double[] numbers) {
		long gcd = (long) numbers[0];
		for (int i = 0; i < numbers.length; i++) {
			gcd = gcd((long) numbers[i], gcd);
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
		else {
			return Math.round(x / scale) * scale;					
		}				
	}
	
	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction,  
	 * eg 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned, 
	 * otherwise x is returned.
	 */	
	/**
	 * Checks if x is close (Kernel.MIN_PRECISION) to a decimal fraction, eg
	 * 2.800000000000001. If it is, the decimal fraction eg 2.8 is returned,
	 * otherwise x is returned.
	 */
	final public double checkDecimalFraction(double x, double precision) {
		
		//Application.debug(precision+" ");
		precision = Math.pow(10, Math.floor(Math.log(Math.abs(precision))/Math.log(10)));
		
		double fracVal = x * INV_MIN_PRECISION;
		double roundVal = Math.round(fracVal);
		//Application.debug(precision+" "+x+" "+fracVal+" "+roundVal+" "+isEqual(fracVal, roundVal, precision)+" "+roundVal / INV_MIN_PRECISION);
		if (isEqual(fracVal, roundVal, STANDARD_PRECISION * precision))
			return roundVal / INV_MIN_PRECISION;
		else
			return x;
	}
	
	final public double checkDecimalFraction(double x) {
		return checkDecimalFraction(x, 1);
	}

	/**
	 * Checks if x is very close (1E-8) to an integer. If it is,
	 * the integer value is returned, otherwise x is returnd.
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

	private boolean isSaving;
	
	public synchronized boolean isSaving() {
		return isSaving;
	}
	
	public synchronized void setSaving(boolean saving) {
		isSaving = saving;
	}
	
	/**
	 * Returns the kernel settings in XML format.
	 */
	public void getKernelXML(StringBuilder sb) {
	
		// kernel settings
		sb.append("<kernel>\n");
	
		// continuity: true or false, since V3.0
		sb.append("\t<continuous val=\"");
		sb.append(isContinuous());
		sb.append("\"/>\n");
		
		if (useSignificantFigures) {
			// significant figures
			sb.append("\t<significantfigures val=\"");
			sb.append(getPrintFigures());
			sb.append("\"/>\n");			
		}
		else
		{
			// decimal places
			sb.append("\t<decimals val=\"");
			sb.append(getPrintDecimals());
			sb.append("\"/>\n");
		}
		
		// angle unit
		sb.append("\t<angleUnit val=\"");
		sb.append(angleUnit == Kernel.ANGLE_RADIANT ? "radiant" : "degree");
		sb.append("\"/>\n");
		
		// algebra style
		sb.append("\t<algebraStyle val=\"");
		sb.append(algebraStyle);
		sb.append("\"/>\n");
		
		// coord style
		sb.append("\t<coordStyle val=\"");
		sb.append(getCoordStyle());
		sb.append("\"/>\n");
		
		// animation
		if (isAnimationRunning()) {
			sb.append("\t<startAnimation val=\"");
			sb.append(isAnimationRunning());
			sb.append("\"/>\n");
		}
	
		sb.append("</kernel>\n");
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

	public final boolean isAllowVisibilitySideEffects() {
		return allowVisibilitySideEffects;
	}

	public final void setAllowVisibilitySideEffects(
			boolean allowVisibilitySideEffects) {
		this.allowVisibilitySideEffects = allowVisibilitySideEffects;
	}

	public boolean isMacroKernel() {
		return false;
	}
	
	private AnimationManager animationManager;
	
	final public AnimationManager getAnimatonManager() {		
		if (animationManager == null) {
			animationManager = new AnimationManager(this);			
		}
		return animationManager;		
	}
	
	final public boolean isAnimationRunning() {
		return animationManager != null && animationManager.isRunning();
	}
	
	final public boolean isAnimationPaused() {
		return animationManager != null && animationManager.isPaused();
	}
	
	final public boolean needToShowAnimationButton() {
		return animationManager != null && animationManager.needToShowAnimationButton();		
	}
	
	final public void udpateNeedToShowAnimationButton() {
		if (animationManager != null)
			animationManager.updateNeedToShowAnimationButton();		
		
	}	

	/**
	 * Turns silent mode on (true) or off (false). In silent mode, commands can
	 * be used to create objects without any side effects, i.e.
	 * no labels are created, algorithms are not added to the construction
	 * list and the views are not notified about new objects. 
	 */
	public final void setSilentMode(boolean silentMode) {
		
		
		this.silentMode = silentMode;
		
		// no new labels, no adding to construction list
		cons.setSuppressLabelCreation(silentMode);
		
		// no notifying of views
		//ggb3D - 2009-07-17
		//removing :
		//notifyViewsActive = !silentMode;
		//(seems not to work with loading files)


		
		//Application.printStacktrace(""+silentMode);
		
	}
	

	/**
	 * Returns whether silent mode is turned on.
	 * @see setSilentMode()
	 */
	public final boolean isSilentMode() {
		return silentMode;
	}
	
	/**
	 * Sets whether unknown variables should be resolved as GeoDummyVariable objects. 
	 */
	public final void setResolveUnkownVarsAsDummyGeos(boolean resolveUnkownVarsAsDummyGeos) {
		this.resolveUnkownVarsAsDummyGeos = resolveUnkownVarsAsDummyGeos;				
	}
	

	/**
	 * Returns whether unkown variables are resolved as GeoDummyVariable objects.
	 * @see setSilentMode()
	 */
	public final boolean isResolveUnkownVarsAsDummyGeos() {
		return resolveUnkownVarsAsDummyGeos;
	}
	
	final public static String defaultLibraryJavaScript = "function ggbOnInit() {}";
	
	String libraryJavaScript = defaultLibraryJavaScript;
	
	public void setLibraryJavaScript(String str) {
		Application.debug(str);
		libraryJavaScript = str;
		
		//libraryJavaScript = "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');ggbApplet.registerObjectUpdateListener('A','listener');}function listener() {//java.lang.System.out.println('add listener called'); var x = ggbApplet.getXcoord('A');var y = ggbApplet.getYcoord('A');var len = Math.sqrt(x*x + y*y);if (len > 5) { x=x*5/len; y=y*5/len; }ggbApplet.unregisterObjectUpdateListener('A');ggbApplet.setCoords('A',x,y);ggbApplet.registerObjectUpdateListener('A','listener');}";
		//libraryJavaScript = "function ggbOnInit() {ggbApplet.evalCommand('A=(1,2)');}";
	}
	
	//public String getLibraryJavaScriptXML() {
	//	return Util.encodeXML(libraryJavaScript);
	//}
	
	public String getLibraryJavaScript() {
		return libraryJavaScript;
	}
	
	
	
	/** return all points of the current construction */
	public TreeSet getPointSet(){
		return getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
	}
	
	/**
	 * test kernel
	 */
	public static void mainx(String [] args) {
		// create kernel with null application for testing
		Kernel kernel = new Kernel(null);
		Construction cons = kernel.getConstruction();
		
		// create points A and B
		GeoPoint A = new GeoPoint(cons, "A", 0, 1, 1);
		GeoPoint B = new GeoPoint(cons, "B", 3, 4, 1);
		
		// create line g through points A and B
		GeoLine g = kernel.Line("g", A, B);
		
		// print current objects
		System.out.println(A);
		System.out.println(B);
		System.out.println(g);
		
		// change B
		B.setCoords(3, 2, 1);
		B.updateCascade();
		
		// print current objects
		System.out.println("changed " +B);
		System.out.println(g);
	}
	
	
	final public GeoNumeric convertIndexToNumber(String str) {
		//Application.debug(str.substring(3, str.length() - 1)); 
		MyDouble md = new MyDouble(this, str.substring(3, str.length() - 1)); // strip off eg "sin" at start, "(" at end
		GeoNumeric num = new GeoNumeric(getConstruction(), md.getDouble());
		return num;

	}
	
	final public ExpressionNode handleTrigPower(String image, ExpressionNode en, int type) {
		
		// sin^(-1)(x) -> ArcSin(x)
		if (image.indexOf(Unicode.Superscript_Minus) > -1) {
			//String check = ""+Unicode.Superscript_Minus + Unicode.Superscript_1 + '(';
			if (image.substring(3, 6).equals(Unicode.superscriptMinusOneBracket))
				return new ExpressionNode(this, en, ExpressionNode.ARCSIN, null);
			else throw new Error("Bad index for trig function"); // eg sin^-2(x)
		}
		
		return new ExpressionNode(this, new ExpressionNode(this, en, type, null), ExpressionNode.POWER, convertIndexToNumber(image));
	}
}

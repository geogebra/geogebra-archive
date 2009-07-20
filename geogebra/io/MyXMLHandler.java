/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyXMLHandler.java
 *
 * Created on 14. Juni 2003, 12:04
 */

package geogebra.io;

import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.io.layout.DockPanelXml;
import geogebra.io.layout.DockSplitPaneXml;
import geogebra.io.layout.Perspective;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoJavaScriptButton;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.Kernel;
import geogebra.kernel.LimitedPath;
import geogebra.kernel.Locateable;
import geogebra.kernel.Macro;
import geogebra.kernel.MacroKernel;
import geogebra.kernel.PointProperties;
import geogebra.kernel.TextProperties;
import geogebra.kernel.Traceable;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.parser.Parser;
import geogebra.main.Application;
import geogebra.main.CasManager;
import geogebra.main.MyError;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.xml.sax.SAXException;

/**
 * 
 * @author Markus Hohenwarter
 */
// public class MyXMLHandler extends DefaultHandler {
public class MyXMLHandler implements DocHandler {

	private static final double FORMAT = Double.parseDouble(GeoGebra.XML_FILE_FORMAT);

	private static final int MODE_INVALID = -1;
	private static final int MODE_GEOGEBRA = 1;
	private static final int MODE_MACRO = 50;
	private static final int MODE_EUCLIDIAN_VIEW = 100;
	protected static final int MODE_EUCLIDIAN_VIEW3D = 101; //only for 3D
	private static final int MODE_SPREADSHEET_VIEW = 150;
	private static final int MODE_CAS_VIEW = 160;
	private static final int MODE_CAS_SESSION = 161;
	private static final int MODE_CAS_CELL_PAIR = 162;
	private static final int MODE_CAS_INPUT_CELL = 163;
	private static final int MODE_CAS_OUTPUT_CELL = 164;
	private static final int MODE_KERNEL = 200;
	private static final int MODE_CONSTRUCTION = 300;
	private static final int MODE_CONST_GEO_ELEMENT = 301;
	private static final int MODE_CONST_COMMAND = 302;
	
	private static final int MODE_GUI = 400;
	private static final int MODE_GUI_PERSPECTIVES = 401; // <perspectives>
	private static final int MODE_GUI_PERSPECTIVE = 402; // <perspective>
	private static final int MODE_GUI_PERSPECTIVE_PANES = 403; // <perspective> <panes /> </perspective>
	private static final int MODE_GUI_PERSPECTIVE_VIEWS = 404; // <perspective> <views /> </perspective>

	private int mode;
	private int constMode; // submode for <construction>
	private int casSessionMode; // submode for <casSession>

	// avoid import geogebra.cas.view.CASTableCellValue as this is in cas jar
	// file
	private Object casTableCellValueElement;

	private GeoElement geo;
	private Command cmd;
	private Macro macro;
	protected Application app;
	
	private String[] macroInputLabels, macroOutputLabels;
	private GeoElement[] cmdOutput;
	private boolean startAnimation;
	
	/**
	 * The point style of the document, for versions < 3.3
	 */
	private int docPointStyle; 

	// for macros we need to change the kernel, so remember the original kernel
	// too
	private Kernel kernel, origKernel;
	private Construction cons, origCons;
	private Parser parser, origParser;

	// List of LocateableExpPair objects
	// for setting the start points at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList startPointList = new LinkedList();

	// List of cell pairs in cas session
	private LinkedList cellPairList = new LinkedList();	

	// List of GeoExpPair condition objects
	// for setting the conditions at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList showObjectConditionList = new LinkedList();
	private LinkedList dynamicColorList = new LinkedList();
	private LinkedList animationSpeedList = new LinkedList();

	private class GeoExpPair {
		GeoElement geo;
		String exp;

		GeoExpPair(GeoElement g, String exp) {
			geo = g;
			this.exp = exp;
		}
	}
	
	private class LocateableExpPair {
		Locateable locateable;
		String exp; // String with expression to create point 
		GeoPoint point; // free point
		int number; // number of startPoint

		LocateableExpPair(Locateable g, String s, int n) {
			locateable = g;
			exp = s;
			number = n;
		}
		
		LocateableExpPair(Locateable g, GeoPoint p, int n) {
			locateable = g;
			point = p;
			number = n;
		}
	}

	// construction step stored in <consProtNavigation> : handled after parsing
	private int consStep;

	private double ggbFileFormat;

	/**
	 * The storage container for all GUI related information of the current document.
	 */
	private Perspective tmp_perspective;
	
	/**
	 * A vector with all perspectives we have read in this document.
	 */
	private ArrayList tmp_perspectives = new ArrayList();
	
	/**
	 * Array lists to store temporary panes and views of a perspective.
	 */
	private ArrayList tmp_panes, tmp_views;
	
	/**
	 * Backward compatibility for version < 3.03 where no layout component was used.
	 * Temporary storage for the split divider location of the split panes #1/#2. 
	 */
	private int tmp_sp1, tmp_sp2;
	
	/**
	 * If the split divider is horizontal. (version < 3.03)
	 */
	private boolean tmp_spHorizontal;
	
	/**
	 * If the algebra or spreadsheet view is visible. (version < 3.03)
	 */
	private boolean tmp_showAlgebra, tmp_showSpreadsheet;

	/** Creates a new instance of MyXMLHandler */
	public MyXMLHandler(Kernel kernel, Construction cons) {
		origKernel = kernel;
		origCons = cons;
		origParser = new Parser(origKernel, origCons);
		app = origKernel.getApplication();
		initKernelVars();

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;

		casSessionMode = MODE_CAS_SESSION;
	}

	private void reset(boolean start) {
		startPointList.clear();
		showObjectConditionList.clear();
		dynamicColorList.clear();
		if (start) consStep = -2;

		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;

		initKernelVars();
	}

	private void initKernelVars() {
		this.kernel = origKernel;
		this.parser = origParser;
		this.cons = origKernel.getConstruction();
	}

	public int getConsStep() {
		return consStep;
	}

	// ===============================================
	// SAX ContentHandler methods
	// ===============================================

	final public void text(String str) throws SAXException {
	}

	final public void startDocument() throws SAXException {
		reset(true);
	}

	final public void endDocument() throws SAXException {				
		if (mode == MODE_INVALID)
			throw new SAXException(app.getPlain("XMLTagANotFound","<geogebra>"));
	}

	final public void startElement(String eName, LinkedHashMap<String, String> attrs)
			throws SAXException {
		// final public void startElement(
		// String namespaceURI,
		// String sName,
		// String qName,
		// LinkedHashMap<String, String> attrs)
		// throws SAXException {
		// String eName = qName;

		switch (mode) {
		case MODE_GEOGEBRA: // top level mode
			startGeoGebraElement(eName, attrs);
			break;

		case MODE_EUCLIDIAN_VIEW:
			startEuclidianViewElement(eName, attrs);
			break;
			
		case MODE_EUCLIDIAN_VIEW3D:
			startEuclidianView3DElement(eName, attrs);
			break;

		case MODE_SPREADSHEET_VIEW:
			startSpreadsheetViewElement(eName, attrs);
			break;

		case MODE_CAS_VIEW:
			startCASViewElement(eName, attrs);
			break;

		case MODE_CAS_SESSION:
			startCASSessionElement(eName, attrs);
			break;

		case MODE_KERNEL:
			startKernelElement(eName, attrs);
			break;

		case MODE_MACRO:
			startMacroElement(eName, attrs);
			break;

		case MODE_CONSTRUCTION:
			startConstructionElement(eName, attrs);
			break;

		case MODE_GUI:
			startGuiElement(eName, attrs);
			break;
			
		case MODE_GUI_PERSPECTIVES:
			startGuiPerspectivesElement(eName, attrs);
			break;
		
		case MODE_GUI_PERSPECTIVE:
			startGuiPerspectiveElement(eName, attrs);
			break;
		
		case MODE_GUI_PERSPECTIVE_PANES:
			startGuiPanesElement(eName, attrs);
			break;
			
		case MODE_GUI_PERSPECTIVE_VIEWS:
			startGuiViewsElement(eName, attrs);
			break;

		case MODE_INVALID:
			// is this a geogebra file?
			if (eName.equals("geogebra")) {
				mode = MODE_GEOGEBRA;
				// check file format version
				try {
					ggbFileFormat = Double.parseDouble((String) attrs
							.get("format"));
					
					ggbFileFormat = kernel.checkDecimalFraction(ggbFileFormat);

					if (ggbFileFormat > FORMAT) {
						System.err.println(app.getError("FileFormatNewer")
								+ ": " + ggbFileFormat); // Michael
						// Borcherds
					}

					// removed - doesn't work over an undo
					// fileFormat dependent settings for downward compatibility
					//if (ggbFileFormat < 2.6) {
					//	kernel.arcusFunctionCreatesAngle = true;
					//}
					
					
					if (ggbFileFormat < 3.0) {
						// before V3.0 the kernel had continuity always on
						if (!(kernel instanceof MacroKernel))
							kernel.setContinuous(true);

						// before V3.0 the automaticGridDistanceFactor was 0.5
						EuclidianView.automaticGridDistanceFactor = 0.5;
					}

				} catch (Exception e) {
					throw new MyError(app, "FileFormatUnknown");
				}
			}
			break;

		default:
			System.err.println("unknown mode: " + mode);
		}
	}

	// set mode back to geogebra mode
	final public void endElement(String eName)
	// public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException {
		// String eName = qName;
		switch (mode) {
		case MODE_EUCLIDIAN_VIEW:
			if (eName.equals("euclidianView"))
				mode = MODE_GEOGEBRA;
			break;
		case MODE_EUCLIDIAN_VIEW3D:
			if (eName.equals("euclidianView3D"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_SPREADSHEET_VIEW:
			if (eName.equals("spreadsheetView"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_CAS_VIEW:
			if (eName.equals("casView"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_CAS_SESSION:
			endCASSessionElement(eName);
			break;

		case MODE_KERNEL:
			if (eName.equals("kernel"))
				mode = MODE_GEOGEBRA;
			break;

		case MODE_GUI:
			if (eName.equals("gui"))
				mode = MODE_GEOGEBRA;
				endGuiElement();
			break;
			
		case MODE_GUI_PERSPECTIVES:
			if(eName.equals("perspectives"))
				mode = MODE_GUI;
				endGuiPerspectivesElement(); // save all perspectives
			break;
			
		case MODE_GUI_PERSPECTIVE:
			if(eName.equals("perspective"))
				mode = MODE_GUI_PERSPECTIVES;
				endGuiPerspectiveElement(); // save views & panes of the perspective
			break;
			
		case MODE_GUI_PERSPECTIVE_PANES:
			if(eName.equals("panes"))
				mode = MODE_GUI_PERSPECTIVE;
			break;
			
		case MODE_GUI_PERSPECTIVE_VIEWS:
			if(eName.equals("views"))
				mode = MODE_GUI_PERSPECTIVE;
			break;

		case MODE_CONSTRUCTION:
			endConstructionElement(eName);
			break;

		case MODE_MACRO:
			if (eName.equals("macro")) {
				endMacro();
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_GEOGEBRA:
			if (eName.equals("geogebra")) {
				
				// start animation if necessary
				if (startAnimation)
					kernel.getAnimatonManager().startAnimation();
							}
		}
	}

	private void endCASSessionElement(String eName) {
		switch (casSessionMode) {
		case MODE_CAS_SESSION:
			if (eName.equals("casSession")) {
				// process start points at end of construction
				processCellPairList();
				mode = MODE_GEOGEBRA;
			}
			break;

		case MODE_CAS_CELL_PAIR:
			if (eName.equals("cellPair"))
				casSessionMode = MODE_CAS_SESSION;
			break;

		case MODE_CAS_INPUT_CELL:
			if (eName.equals("inputCell"))
				casSessionMode = MODE_CAS_CELL_PAIR;
			break;

		case MODE_CAS_OUTPUT_CELL:
			if (eName.equals("outputCell"))
				casSessionMode = MODE_CAS_CELL_PAIR;
			break;

		default:
			casSessionMode = MODE_CAS_SESSION; // set back mode
			System.err.println("unknown cas session mode:" + constMode);
		}

	}

	private void processCellPairList() {
		try {
			app.getCasView().initCellPairs(cellPairList);
			cellPairList.clear();
		} catch (Exception e) {
			cellPairList.clear();
			e.printStackTrace();
			throw new MyError(app, "processCellPairList: " + e.toString());
		}
	}

	// ====================================
	// <geogebra>
	// ====================================
	private void startGeoGebraElement(String eName, LinkedHashMap<String, String> attrs) {
		if (eName.equals("euclidianView")) {
			mode = MODE_EUCLIDIAN_VIEW;
		} else if (eName.equals("euclidianView3D")) {
			mode = MODE_EUCLIDIAN_VIEW3D;
		} else if (eName.equals("kernel")) {
				mode = MODE_KERNEL;
		} else if (eName.equals("spreadsheetView")) {
			mode = MODE_SPREADSHEET_VIEW;
		} else if (eName.equals("casView")) {
			mode = MODE_CAS_VIEW;
		} else if (eName.equals("casSession")) {
			mode = MODE_CAS_SESSION;
		} else if (eName.equals("gui")) {
			mode = MODE_GUI;
			
			if(ggbFileFormat < 3.3)
				tmp_perspective = new Perspective("tmp");
		} else if (eName.equals("macro")) {
			mode = MODE_MACRO;
			initMacro(attrs);
		} else if (eName.equals("construction")) {
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
		} else {
			System.err.println("unknown tag in <geogebra>: " + eName);
		}
	}

	private void startMacroElement(String eName, LinkedHashMap<String, String> attrs) {
		if (eName.equals("macroInput")) {
			macroInputLabels = getAttributeStrings(attrs);
		} else if (eName.equals("macroOutput")) {
			macroOutputLabels = getAttributeStrings(attrs);
		} else if (eName.equals("construction")) {
			mode = MODE_CONSTRUCTION;
			handleConstruction(attrs);
		} else {
			System.err.println("unknown tag in <macro>: " + eName);
		}
	}
	
	
	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/** only used in MyXMLHandler3D
	 * @param eName
	 * @param attrs
	 */
	protected void startEuclidianView3DElement(String eName, LinkedHashMap<String, String> attrs) {
		Application.debug("TODO : warn that it's a 3D file");
	}
	
	

	// ====================================
	// <euclidianView>
	// ====================================
	private void startEuclidianViewElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		EuclidianView ev = app.getEuclidianView();

		switch (eName.charAt(0)) {
		case 'a':
			if (eName.equals("axesColor")) {
				ok = handleAxesColor(ev, attrs);
				break;
			} else if (eName.equals("axis")) {
				ok = handleAxis(ev, attrs);
				break;
			}

		case 'b':
			if (eName.equals("bgColor")) {
				ok = handleBgColor(ev, attrs);
				break;
			}

		case 'c':
			if (eName.equals("coordSystem")) {
				ok = handleCoordSystem(ev, attrs);
				break;
			}

		case 'e':
			if (eName.equals("evSettings")) {
				ok = handleEvSettings(ev, attrs);
				break;
			}

		case 'g':
			if (eName.equals("grid")) {
				ok = handleGrid(ev, attrs);
				break;
			} else if (eName.equals("gridColor")) {
				ok = handleGridColor(ev, attrs);
				break;
			}
		case 'l':
			if (eName.equals("lineStyle")) {
				ok = handleLineStyle(ev, attrs);
				break;
			}

		case 's':
			if (eName.equals("size")) {
				ok = handleEvSize(ev, attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <euclidianView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <euclidianView>: " + eName);
	}

	// ====================================
	// <SpreadsheetView>
	// ====================================
	private void startSpreadsheetViewElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (eName.charAt(0)) {
		case 's':
			if (eName.equals("size")) {
				ok = handleSpreadsheetSize(app.getGuiManager()
						.getSpreadsheetView(), attrs);
				break;
			} else
			if (eName.equals("spreadsheetColumn")) {
				ok = handleSpreadsheetColumn(app.getGuiManager()
						.getSpreadsheetView(), attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <spreadsheetView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <spreadsheetView>: " + eName);
	}

	// ====================================
	// <CASView>
	// ====================================
	private void startCASViewElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;

		switch (eName.charAt(0)) {
		case 's':
			if (eName.equals("size")) {
				ok = handleCASSize(app.getCasView(), attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <casView>: " + eName);
		}

		if (!ok)
			System.err.println("error in <casView>: " + eName);

	}

	private boolean handleCoordSystem(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		try {
			double xZero = Double.parseDouble((String) attrs.get("xZero"));
			double yZero = Double.parseDouble((String) attrs.get("yZero"));
			double scale = Double.parseDouble((String) attrs.get("scale"));

			// new since version 2.5
			double yscale = scale;
			String strYscale = (String) attrs.get("yscale");
			if (strYscale != null) {
				yscale = Double.parseDouble(strYscale);
			}
			ev.setCoordSystem(xZero, yZero, scale, yscale, false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEvSettings(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		try {
			// axes attribute was removed with V3.0, see handleAxis()
			// this code is for downward compatibility
			String strAxes = (String) attrs.get("axes");
			if (strAxes != null) {
				boolean showAxes = parseBoolean(strAxes);
				ev.showAxes(showAxes, showAxes);
			}

			ev.showGrid(parseBoolean((String) attrs.get("grid")));

			try {
				ev
						.setGridIsBold(parseBoolean((String) attrs
								.get("gridIsBold"))); // Michael Borcherds
				// 2008-04-11
			} catch (Exception e) {
			}

			try {
				ev
						.setGridType(Integer.parseInt((String) attrs
								.get("gridType"))); // Michael Borcherds
				// 2008-04-30
			} catch (Exception e) {
			}

			String str = (String) attrs.get("pointCapturing");
			if (str != null) {
				// before GeoGebra 2.7 pointCapturing was either "true" or
				// "false"
				// now pointCapturing holds an int value
				int pointCapturingMode;
				if (str.equals("false"))
					pointCapturingMode = 0;
				else if (str.equals("true"))
					pointCapturingMode = 1;
				else
					// int value
					pointCapturingMode = Integer.parseInt(str);
				ev.setPointCapturing(pointCapturingMode);
			}
			
			// if there is a point style given save it
			if(ggbFileFormat < 3.3) {
				String strPointStyle = (String) attrs.get("pointStyle");
				if (strPointStyle != null) {
					docPointStyle = Integer.parseInt(strPointStyle);
				} else {
					docPointStyle = EuclidianView.POINT_STYLE_DOT;
				}
				
				// TODO save as default construction (F.S.)
			} else {
				docPointStyle = -1;
			}

			// Michael Borcherds 2008-05-12
			// size of checkbox
			String strBooleanSize = (String) attrs.get("checkboxSize");
			if (strBooleanSize != null)
				ev.setBooleanSize(Integer.parseInt(strBooleanSize));

			// v3.0: appearance of right angle
			String strRightAngleStyle = (String) attrs.get("rightAngleStyle");
			if (strRightAngleStyle == null)
				// before v3.0 the default was a dot to show a right angle
				ev.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
			else
				ev.setRightAngleStyle(Integer.parseInt(strRightAngleStyle));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEvSize(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		if (app.isApplet())
			return true;

		try {
			int width = Integer.parseInt((String) attrs.get("width"));
			int height = Integer.parseInt((String) attrs.get("height"));
			ev.setPreferredSize(new Dimension(width, height));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetSize(Object spreadsheetView,
			LinkedHashMap<String, String> attrs) {
		if (app.isApplet())
			return true;

		try {
			int width = Integer.parseInt((String) attrs.get("width"));
			int height = Integer.parseInt((String) attrs.get("height"));
			
			app.getGuiManager().getSpreadsheetView().setPreferredSize(new Dimension(width, height));
			//((geogebra.gui.view.spreadsheet.SpreadsheetView) spreadsheetView)
			//		.setPreferredSize(new Dimension(width+118, height));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetColumn(Object spreadsheetView,
			LinkedHashMap<String, String> attrs) {

		try {
			int col = Integer.parseInt((String) attrs.get("id"));
			int width = Integer.parseInt((String) attrs.get("width"));
			app.getGuiManager().setColumnWidth(col, width);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCASSize(CasManager casView, LinkedHashMap<String, String> attrs) {
		if (app.isApplet())
			return true;

		try {
			int width = Integer.parseInt((String) attrs.get("width"));
			int height = Integer.parseInt((String) attrs.get("height"));

			// it seems that this statement does not work, because now cas use
			// its own frame. --Quan Yuan
			((JComponent) app.getCasView()).setPreferredSize(new Dimension(
					width, height));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleBgColor(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setBackground(col);
		return true;
	}

	private boolean handleAxesColor(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setAxesColor(col);
		return true;
	}

	private boolean handleGridColor(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setGridColor(col);
		return true;
	}

	private boolean handleLineStyle(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		try {
			ev.setAxesLineStyle(Integer.parseInt((String) attrs.get("axes")));
			ev.setGridLineStyle(Integer.parseInt((String) attrs.get("grid")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleGrid(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		// <grid distX="2.0" distY="4.0"/>
		try {
			double[] dists = new double[2];
			dists[0] = Double.parseDouble((String) attrs.get("distX"));
			dists[1] = Double.parseDouble((String) attrs.get("distY"));
			ev.setGridDistances(dists);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAxis(EuclidianView ev, LinkedHashMap<String, String> attrs) {
		// <axis id="0" label="x" unitLabel="x" showNumbers="true"
		// tickDistance="2"/>
		try {
			int axis = Integer.parseInt((String) attrs.get("id"));
			String strShowAxis = (String) attrs.get("show");
			String label = (String) attrs.get("label");
			String unitLabel = (String) attrs.get("unitLabel");
			boolean showNumbers = parseBoolean((String) attrs
					.get("showNumbers"));

			// show this axis
			if (strShowAxis != null) {
				boolean showAxis = parseBoolean(strShowAxis);
				if (axis == 0) { // xaxis
					ev.showAxes(showAxis, ev.getShowYaxis());
				} else if (axis == 1) { // yaxis
					ev.showAxes(ev.getShowXaxis(), showAxis);
				}
			}

			// set label
			if (label != null && label.length() > 0) {
				String[] labels = ev.getAxesLabels();
				labels[axis] = label;
				ev.setAxesLabels(labels);
			}

			// set unitlabel
			if (unitLabel != null && unitLabel.length() > 0) {
				String[] unitLabels = ev.getAxesUnitLabels();
				unitLabels[axis] = unitLabel;
				ev.setAxesUnitLabels(unitLabels);
			}

			// set showNumbers
			boolean showNums[] = ev.getShowAxesNumbers();
			showNums[axis] = showNumbers;
			ev.setShowAxesNumbers(showNums);

			// check if tickDistance is given
			String strTickDist = (String) attrs.get("tickDistance");
			if (strTickDist != null) {
				double tickDist = Double.parseDouble(strTickDist);
				ev.setAxesNumberingDistance(tickDist, axis);
			}

			// tick style
			String strTickStyle = (String) attrs.get("tickStyle");
			if (strTickStyle != null) {
				int tickStyle = Integer.parseInt(strTickStyle);
				ev.getAxesTickStyles()[axis] = tickStyle;
			} else {
				// before v3.0 the default tickStyle was MAJOR_MINOR
				ev.getAxesTickStyles()[axis] = EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <kernel>
	// ====================================
	private void startKernelElement(String eName, LinkedHashMap<String, String> attrs) {
		if (eName.equals("angleUnit")) {
			handleAngleUnit(attrs);
		} else if (eName.equals("coordStyle")) {
			handleKernelCoordStyle(attrs);
		} else if (eName.equals("continuous")) {
			handleKernelContinuous(attrs);
		} else if (eName.equals("decimals")) {
			handleKernelDecimals(attrs);
		} else if (eName.equals("significantfigures")) {
			handleKernelFigures(attrs);
		} else if (eName.equals("startAnimation")) {
			handleKernelStartAnimation(attrs);
		} else
			System.err.println("unknown tag in <kernel>: " + eName);
	}

	private boolean handleAngleUnit(LinkedHashMap<String, String> attrs) {
		if (attrs == null)
			return false;
		String angleUnit = (String) attrs.get("val");
		if (angleUnit == null)
			return false;

		if (angleUnit.equals("degree"))
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
		else if (angleUnit.equals("radiant"))
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
		else
			return false;
		return true;
	}

	private boolean handleKernelCoordStyle(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setCoordStyle(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelDecimals(LinkedHashMap<String, String> attrs) {
		try {
			kernel
					.setPrintDecimals(Integer.parseInt((String) attrs
							.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleKernelStartAnimation(LinkedHashMap<String, String> attrs) {
		try {
			startAnimation = parseBoolean((String) attrs.get("val"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelFigures(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setPrintFigures(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKernelContinuous(LinkedHashMap<String, String> attrs) {
		try {
			kernel.setContinuous(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <gui>
	// ====================================
	private void startGuiElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'c':
			if (eName.equals("consProtColumns")) {
				ok = handleConsProtColumns(app, attrs);
				break;
			} else if (eName.equals("consProtocol")) {
				ok = handleConsProtocol(app, attrs);
				break;
			} else if (eName.equals("consProtNavigationBar")) {
				ok = handleConsProtNavigationBar(app, attrs);
				break;
			}

		case 'f':
			if (eName.equals("font")) {
				ok = handleFont(app, attrs);
				break;
			}

		case 'l':
			if (eName.equals("labelingStyle")) {
				ok = handleLabelingStyle(app, attrs);
				break;
			}
			
		case 'p':
			if(eName.equals("perspectives")) {
				mode = MODE_GUI_PERSPECTIVES;
				break;
			}
			
		case 's':
			if (eName.equals("show")) {
				ok = handleGuiShow(app, attrs);
				break;
			} else if (eName.equals("splitDivider")) {
				ok = handleSplitDivider(app, attrs);
				break;
			} else if (eName.equals("settings")) {
				ok = handleGuiSettings(app, attrs);
				break;
			}
			
		case 't':
			if (eName.equals("toolbar")) {
				ok = handleToolbar(app, attrs);
				break;
			}
			
		case 'w':
			if(eName.equals("window")) {
				ok = handleWindowSize(app, attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <gui>: " + eName);
		}

		if (!ok)
			System.err.println("error in <gui>: " + eName);
	}
	
	/**
	 * Take care of backward compatibility for the dynamic layout component
	 */
	private void endGuiElement() {
		// construct default xml data in case we're using an old version which didn't
		// store the layout xml.
		if(!(ggbFileFormat > 3.02)) {
			DockPanelXml[] dpXml = new DockPanelXml[] {
				new DockPanelXml(Application.VIEW_EUCLIDIAN, true, false, new Rectangle(400, 400), "1,3", 400),
				new DockPanelXml(Application.VIEW_ALGEBRA, tmp_showAlgebra, false, new Rectangle(200, 400), "3", 400),
				new DockPanelXml(Application.VIEW_SPREADSHEET, tmp_showSpreadsheet, false, new Rectangle(400, 400), "1,1", 400)
			};
			tmp_perspective.setDockPanelInfo(dpXml);
			
			// if we just have one split pane we need to set a proper definition string for the euclidian view
			if(!tmp_showSpreadsheet) {
				dpXml[0].setEmbeddedDef("1");
			}			
			
			DockSplitPaneXml[] spXml; 
			
			int splitOrientation = tmp_spHorizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;
			
			// use two split panes in case the spreadsheet is visible
			// TODO take split pane size into consideration (F.S.)
			if(tmp_showSpreadsheet) {
				spXml = new DockSplitPaneXml[] {
					new DockSplitPaneXml("", 0.2, splitOrientation),
					new DockSplitPaneXml("1", 0.5, splitOrientation)
				}; 
			} else {
				spXml = new DockSplitPaneXml[] {
					new DockSplitPaneXml("", 0.2, splitOrientation)
				};
			}
			
			tmp_perspective.setSplitPaneInfo(spXml);
			
			tmp_perspectives = new ArrayList();
			tmp_perspectives.add(tmp_perspective);
			app.setTmpPerspectives(tmp_perspectives);
			
			// TODO calculate window size in old versions (F.S.)
		}
	}

	private boolean handleConsProtColumns(Application app, LinkedHashMap<String, String> attrs) {
		try {
			// TODO: set visible state of columns in consProt
			/*
			 * Iterator it = attrs.keySet().iterator(); while (it.hasNext()) {
			 * Object ob = attrs.get(it.next());
			 * 
			 * boolean isVisible = parseBoolean((String) ob); }
			 */

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleConsProtocol(Application app, LinkedHashMap<String, String> attrs) {
		try {
			// boolean useColors = parseBoolean((String)
			// attrs.get("useColors"));
			// TODO: set useColors for consProt

			boolean showOnlyBreakpoints = parseBoolean((String) attrs
					.get("showOnlyBreakpoints"));
			kernel.setShowOnlyBreakpoints(showOnlyBreakpoints);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleConsProtNavigationBar(Application app,
			LinkedHashMap<String, String> attrs) {
		try {
			boolean show = parseBoolean((String) attrs.get("show"));
			boolean playButton = parseBoolean((String) attrs.get("playButton"));
			double playDelay = Double.parseDouble((String) attrs
					.get("playDelay"));
			boolean showProtButton = parseBoolean((String) attrs
					.get("protButton"));
			
			app.setShowConstructionProtocolNavigation(show);
			if (show) {
				app.getGuiManager().setShowConstructionProtocolNavigation(show,
					playButton, playDelay, showProtButton);
			}

			// construction step: handled at end of parsing
			String strConsStep = (String) attrs.get("consStep");
			if (strConsStep != null)
				consStep = Integer.parseInt(strConsStep);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Backward compatibility for version < 3.3
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleGuiShow(Application app, LinkedHashMap<String, String> attrs) {
		try {
			// backward compatibility to versions without the layout component
			if(ggbFileFormat < 3.3) {
				tmp_showAlgebra = parseBoolean((String) attrs
						.get("algebraView"));

				// Michael Borcherds 2008-04-25
				tmp_showSpreadsheet = parseBoolean((String) attrs
						.get("spreadsheetView"));
			}

			String str = (String) attrs.get("auxiliaryObjects");
			boolean auxiliaryObjects = (str != null && str.equals("true"));
			app.setShowAuxiliaryObjects(auxiliaryObjects);

			str = (String) attrs.get("algebraInput");
			boolean algebraInput = (str == null || str.equals("true"));
			tmp_perspective.setShowInputPanel(algebraInput);

			str = (String) attrs.get("cmdList");
			boolean cmdList = (str == null || str.equals("true"));
			tmp_perspective.setShowInputPanelCommands(cmdList);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	/**
	 * Settings of the user, not saved in the file XML but for preferences XML.
	 * 
	 * <settings ignoreDocument=".." showTitleBar=".." />
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleGuiSettings(Application app, LinkedHashMap<String, String> attrs) {
		try {
			boolean ignoreDocument = !((String)attrs.get("ignoreDocument")).equals("false");
			app.setIgnoreDocumentPerspective(ignoreDocument);
			
			boolean showTitleBar = !((String)attrs.get("showTitleBar")).equals("false");
			app.setViewTitleBarVisible(showTitleBar);
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	/**
	 * Kept for backward compatibility with version < 3.3
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleSplitDivider(Application app, LinkedHashMap<String, String> attrs) {
		try {
			tmp_spHorizontal = !"false".equals((String) attrs.get("horizontal"));
			
			if(tmp_spHorizontal) {
				tmp_sp1 = Integer.parseInt((String) attrs.get("loc"));
				tmp_sp2 = Integer.parseInt((String) attrs.get("loc2"));
			} else {
				String strLocVert = (String) attrs.get("locVertical");
				if (strLocVert != null) {
					tmp_sp1 = Integer.parseInt(strLocVert);
				} else {
					tmp_sp1 = Integer.parseInt((String) attrs.get("loc"));
				}
				
				String strLocVert2 = (String) attrs.get("locVertical2");
				if (strLocVert2 != null) {
					tmp_sp2 = Integer.parseInt(strLocVert2);
				} else {
					tmp_sp2 = Integer.parseInt((String) attrs.get("loc2"));
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleToolbar(Application app, LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective.setToolbarDefinition((String) attrs.get("str"));
			return true;
		} catch (Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	/**
	 * Handle the window size:
	 * <window width=".." height=".." />
	 * 
	 * @param app
	 * @param attrs
	 * @return
	 */
	private boolean handleWindowSize(Application app, LinkedHashMap<String, String> attrs) {
		try {
			Dimension size = new Dimension(
				Integer.parseInt((String)attrs.get("width")),
				Integer.parseInt((String)attrs.get("height"))
			);
			app.setPreferredSize(size);
			return true;
		} catch (Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	private boolean handleFont(Application app, LinkedHashMap<String, String> attrs) {
		try {			
			int guiSize = Integer.parseInt((String) attrs.get("size"));			

			// old versions do just have a single font size and derive the font size for
			// the axes / euclidian view from this single size
			if(ggbFileFormat < 3.3) {
				app.setEuclidianFontSize(guiSize, false);
				app.setAxesFontSize(guiSize - 2, false); // always 2 points smaller than the default size
			} else {
				int axesSize = Integer.parseInt((String) attrs.get("axesSize"));
				app.setAxesFontSize(axesSize, false);
				
				int euclidianSize = Integer.parseInt((String) attrs.get("euclidianSize"));
				app.setEuclidianFontSize(euclidianSize, false);
			}

			app.setGUIFontSize(guiSize); // set gui font size and update all fonts
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelingStyle(Application app, LinkedHashMap<String, String> attrs) {
		try {
			int style = Integer.parseInt((String) attrs.get("val"));
			app.setLabelingStyle(style);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <perspectives>
	// ====================================
	private void startGuiPerspectivesElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if(eName.equals("perspective"))
			ok = handlePerspective(attrs);
		else 
			Application.debug("unknown tag in <perspectives>: " + eName);

		if (!ok)
			Application.debug("error in <perspectives>: " + eName);
	}
	
	/**
	 * Create a new temporary perspective for the current <perspective> element
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handlePerspective(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective = new Perspective((String)attrs.get("id"));
			tmp_perspectives.add(tmp_perspective);
			tmp_panes = new ArrayList();
			tmp_views = new ArrayList();
			mode = MODE_GUI_PERSPECTIVE;
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	/**
	 * Save all perspectives in the application.
	 */
	private void endGuiPerspectivesElement() {
		app.setTmpPerspectives(tmp_perspectives);
	}

	// ====================================
	// <perspective>
	// ====================================
	private void startGuiPerspectiveElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'i':
			if(eName.equals("input")) {
				ok = handleAlgebraInput(attrs);
				break;
			}
			
		case 'p':
			if(eName.equals("panes")) {
				mode = MODE_GUI_PERSPECTIVE_PANES;
				break;
			}
			
		case 's':
			if(eName.equals("show")) {
				ok = handleGuiShow(app, attrs);
				break;
			}
			
		case 't':
			if(eName.equals("toolbar")) {
				ok = handleToolbar(app, attrs);
				break;
			}
			
		case 'v':
			if(eName.equals("views")) {
				mode = MODE_GUI_PERSPECTIVE_VIEWS;
				break;
			}

		default:
			Application.debug("unknown tag in <perspective>: " + eName);
		}

		if (!ok)
			Application.debug("error in <perspective>: " + eName);
	}
	
	private boolean handleAlgebraInput(LinkedHashMap<String, String> attrs) {
		try {
			tmp_perspective.setShowInputPanel(!((String)attrs.get("show")).equals("false"));
			tmp_perspective.setShowInputPanelCommands(!((String)attrs.get("cmd")).equals("false"));
			tmp_perspective.setShowInputPanelOnTop(!((String)attrs.get("top")).equals("false"));
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}
	
	private void endGuiPerspectiveElement() {
		DockPanelXml[] dpInfo = new DockPanelXml[tmp_views.size()];
		DockSplitPaneXml[] spInfo = new DockSplitPaneXml[tmp_panes.size()];
		tmp_perspective.setDockPanelInfo((DockPanelXml[])tmp_views.toArray(dpInfo));
		tmp_perspective.setSplitPaneInfo((DockSplitPaneXml[])tmp_panes.toArray(spInfo));
	}

	// ====================================
	// <views>
	// ====================================
	private void startGuiViewsElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if(eName.equals("view"))
			ok = handleView(attrs);
		else 
			Application.debug("unknown tag in <views>: " + eName);

		if (!ok)
			Application.debug("error in <views>: " + eName);
	}
	
	/**
	 * Handle a view.
	 * <view id=".." visible=".." inframe=".." window=".." location=".." size=".." />
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handleView(LinkedHashMap<String, String> attrs) {
		try {
			int viewId = Integer.parseInt((String)attrs.get("id"));
			boolean isVisible = !((String)attrs.get("visible")).equals("false");
			boolean openInFrame = !((String)attrs.get("inframe")).equals("false");
			
			// the window rectangle is given in the format "x,y,width,height"
			String[] window = ((String)attrs.get("window")).split(",");
			Rectangle windowRect = new Rectangle(
				Integer.parseInt(window[0]),
				Integer.parseInt(window[1]),
				Integer.parseInt(window[2]),
				Integer.parseInt(window[3])
			);
			
			String embeddedDef = (String)attrs.get("location");
			int embeddedSize = Integer.parseInt((String)attrs.get("size"));
			
			tmp_views.add(new DockPanelXml(viewId, isVisible, openInFrame, windowRect, embeddedDef, embeddedSize));
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <panes>
	// ====================================
	private void startGuiPanesElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		
		if(eName.equals("pane"))
			ok = handlePane(attrs);
		else 
			Application.debug("unknown tag in <panes>: " + eName);

		if (!ok)
			Application.debug("error in <panes>: " + eName);
	}
	
	/**
	 * Handle a pane.
	 * <pane location".." divider=".." orientation=".." />
	 * 
	 * @param attrs
	 * @return
	 */
	private boolean handlePane(LinkedHashMap<String, String> attrs) {
		try {
			String location = (String)attrs.get("location");
			double dividerLocation = Double.parseDouble((String)attrs.get("divider"));
			int orientation = Integer.parseInt((String)attrs.get("orientation"));
			
			tmp_panes.add(new DockSplitPaneXml(location, dividerLocation, orientation));
			
			return true;
		} catch(Exception e) {
			Application.debug(e.getMessage() + ": " + e.getCause());
			return false;
		}
	}

	// ====================================
	// <construction>
	// ====================================
	private void handleConstruction(LinkedHashMap<String, String> attrs) {
		try {
			String title = (String) attrs.get("title");
			String author = (String) attrs.get("author");
			String date = (String) attrs.get("date");
			if (title != null)
				cons.setTitle(title);
			if (author != null)
				cons.setAuthor(author);
			if (date != null)
				cons.setDate(date);
		} catch (Exception e) {
			System.err.println("error in <construction>");
		}
	}

	private void initMacro(LinkedHashMap<String, String> attrs) {
		try {
			String cmdName = (String) attrs.get("cmdName");
			String toolName = (String) attrs.get("toolName");
			String toolHelp = (String) attrs.get("toolHelp");
			String iconFile = (String) attrs.get("iconFile");
			String strShowInToolBar = (String) attrs.get("showInToolBar");

			// Make sure we don't have a macro with the same name in kernel.
			// This can happen when a macro file (ggt) is loaded because
			// the previous macros are not cleared in this case.
			int n = 0;
			String myCmdName = cmdName;
			while (kernel.getMacro(myCmdName) != null) {
				n++;
				myCmdName = cmdName + n;
			}

			// create macro and a kernel for it
			macro = new Macro(kernel, myCmdName);
			macro.setToolName(toolName);
			macro.setToolHelp(toolHelp);
			macro.setIconFileName(iconFile);
			boolean showTool = strShowInToolBar == null ? true
					: parseBoolean(strShowInToolBar);
			macro.setShowInToolBar(showTool);

			MacroKernel macroKernel = new MacroKernel(kernel);
			macroKernel.setContinuous(false);

			// we have to change the construction object temporarily so
			// everything
			// is done in the macro construction from now on
			kernel = macroKernel;
			cons = macroKernel.getConstruction();
			parser = new Parser(macroKernel, cons);

		} catch (Exception e) {
			System.err.println("error in <macro>");
		}
	}

	private void endMacro() {
		// cons now holds a reference to the macroConstruction
		macro.initMacro(cons, macroInputLabels, macroOutputLabels);
		// ad the newly built macro to the kernel
		origKernel.addMacro(macro);

		// set kernel and construction back to the original values
		initKernelVars();
	}

	/*
	 * <worksheetText above="blabla" below="morebla" />
	 */
	private void handleWorksheetText(LinkedHashMap<String, String> attrs) {
		String above = (String) attrs.get("above");
		String below = (String) attrs.get("below");
		cons.setWorksheetText(above, 0);
		cons.setWorksheetText(below, 1);
	}

	// ====================================
	// <CAS Session>
	// ====================================
	private void startCASSessionElement(String eName, LinkedHashMap<String, String> attrs) {
		// handle cas session mode
		switch (casSessionMode) {
		case MODE_CAS_SESSION:
			if (eName.equals("cellPair")) {
				casSessionMode = MODE_CAS_CELL_PAIR;
//				app.loadCASJar();
//				casTableCellValueElement = new geogebra.cas.view.CASTableCellValue();
				casTableCellValueElement = app.getCasView().createCellValue();
			} else {
				System.err.println("unknown tag in <cellPair>: " + eName);
			}
			break;

		case MODE_CAS_CELL_PAIR:
			if (eName.equals("inputCell")) {
				casSessionMode = MODE_CAS_INPUT_CELL;
			} else if (eName.equals("outputCell")) {
				casSessionMode = MODE_CAS_OUTPUT_CELL;
			} else {
				System.err.println("unknown tag in <cellPair>: " + eName);
			}
			break;

		case MODE_CAS_INPUT_CELL:
			startCellInputElement(eName, attrs);
			break;

		case MODE_CAS_OUTPUT_CELL:
			startCellOutputElement(eName, attrs);
			break;

		default:
			System.err.println("unknown cas session mode:" + constMode);
		}
	}

	private void startCellOutputElement(String eName, LinkedHashMap<String, String> attrs) {
		if (casTableCellValueElement == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'e':
			if (eName.equals("expression")) {
				ok = handleOutExpression(attrs);
				break;
			}

		case 'c':
			if (eName.equals("color")) {
				ok = handleCASPairColor(attrs);
				if (ok) {
					cellPairList.removeLast();
					cellPairList.add(casTableCellValueElement);

				}
				break;
			}

		default:
			System.err.println("unknown tag in <outputCell>: " + eName);
		}

		if (!ok)
			System.err.println("error in <outputCell>: " + eName);

	}

	private void startCellInputElement(String eName, LinkedHashMap<String, String> attrs) {
		if (casTableCellValueElement == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'e':
			if (eName.equals("expression")) {
				ok = handleInputExpression(attrs);
				break;
			}

		case 'c':
			if (eName.equals("color")) {
				ok = handleCASPairColor(attrs);
				if (ok)
					cellPairList.add(casTableCellValueElement);
				break;
			}

		default:
			System.err.println("unknown tag in <inputCell>: " + eName);
		}

		if (!ok)
			System.err.println("error in <inputCell>: " + eName);
	}

	private void startConstructionElement(String eName, LinkedHashMap<String, String> attrs) {
		// handle construction mode
		
		//Application.debug("constMode = "+constMode+", eName = "+eName);
		
		switch (constMode) {
		case MODE_CONSTRUCTION:
			if (eName.equals("element")) {
				constMode = MODE_CONST_GEO_ELEMENT;
				geo = getGeoElement(attrs);
			} else if (eName.equals("command")) {
				constMode = MODE_CONST_COMMAND;
				cmd = getCommand(attrs);
			} else if (eName.equals("expression")) {
				startExpressionElement(eName, attrs);
			} else if (eName.equals("worksheetText")) {
				handleWorksheetText(attrs);
			} else {
				System.err.println("unknown tag in <construction>: " + eName);
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			startGeoElement(eName, attrs);
			break;

		case MODE_CONST_COMMAND:
			startCommandElement(eName, attrs);
			break;

		default:
			System.err.println("unknown construction mode:" + constMode);
		}
	}

	private void endConstructionElement(String eName) {
		switch (constMode) {
		case MODE_CONSTRUCTION:
			if (eName.equals("construction")) {
				// process start points at end of construction
				processStartPointList();
				processShowObjectConditionList();
				processDynamicColorList();
				//processDynamicCoordinatesList();

				if (kernel == origKernel) {
					mode = MODE_GEOGEBRA;
				} else {
					// macro construction
					mode = MODE_MACRO;
				}
			}
			break;

		case MODE_CONST_GEO_ELEMENT:
			if (eName.equals("element"))
				constMode = MODE_CONSTRUCTION;
			break;

		case MODE_CONST_COMMAND:
			if (eName.equals("command"))
				constMode = MODE_CONSTRUCTION;
			break;

		default:
			constMode = MODE_CONSTRUCTION; // set back mode
			System.err.println("unknown construction mode:" + constMode);
		}
	}

	// ====================================
	// <element>
	// ====================================

	// called when <element> is encountered
	// e.g. for <element type="point" label="P">
	private GeoElement getGeoElement(LinkedHashMap<String, String> attrs) {
		GeoElement geo = null;
		String label = (String) attrs.get("label");
		String type = (String) attrs.get("type");
		if (label == null || type == null) {
			System.err.println("attributes missing in <element>");
			return geo;
		}

		// does a geo element with this label exist?
		geo = kernel.lookupLabel(label);
		
		if (geo == null) {
			geo = kernel.createGeoElement(cons, type);
			geo.setLoadedLabel(label);

			// independent GeoElements should be hidden by default
			// (as older versions of this file format did not
			// store show/hide information for all kinds of objects,
			// e.g. GeoNumeric)
			geo.setEuclidianVisible(false);
		}
		
		// use default point style on points
		if(geo.getGeoClassType() == GeoElement.GEO_CLASS_POINT && ggbFileFormat < 3.3) {
			((PointProperties)geo).setPointStyle(docPointStyle);
		}

		// for downward compatibility
		if (geo.isLimitedPath()) {
			LimitedPath lp = (LimitedPath) geo;
			// old default value for intersections of segments, ...
			// V2.5: default of "allow outlying intersections" is now false
			lp.setAllowOutlyingIntersections(true);

			// old default value for geometric transforms of segments, ...
			// V2.6: default of "keep type on geometric transform" is now true
			lp.setKeepTypeOnGeometricTransform(false);
		}

		return geo;
	}

	private void startGeoElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'a':
			if (eName.equals("auxiliary")) {
				ok = handleAuxiliary(attrs);
				break;
			} else if (eName.equals("animation")) {
				ok = handleAnimation(attrs);
				break;
			} else if (eName.equals("arcSize")) {
				ok = handleArcSize(attrs);
				break;
			} else if (eName.equals("allowReflexAngle")) {
				ok = handleAllowReflexAngle(attrs);
				break; 
			} else if (eName.equals("absoluteScreenLocation")) {
				ok = handleAbsoluteScreenLocation(attrs);
				break;
			}

		case 'b':
			if (eName.equals("breakpoint")) {
				ok = handleBreakpoint(attrs);
				break;
			}

		case 'c':
			if (eName.equals("coords")) {
				ok = handleCoords(attrs);
				break;
			} else if (eName.equals("coordStyle")) {
				ok = handleCoordStyle(attrs);
				break;
			} else if (eName.equals("caption")) {
				ok = handleCaption(attrs);
				break;
			} else if (eName.equals("condition")) {
				ok = handleCondition(attrs);
				break;
			} else if (eName.equals("checkbox")) {
				ok = handleCheckbox(attrs);
				break;
			}

		case 'd':
			if (eName.equals("decoration")) {
				ok = handleDecoration(attrs);
				break;
			} else if (eName.equals("decimals")) {
				ok = handleTextDecimals(attrs);
				break;
			}

		case 'e':
			if (eName.equals("eqnStyle")) {
				ok = handleEqnStyle(attrs);
				break;
			} else if (eName.equals("eigenvectors")) {
				ok = handleEigenvectors(attrs);
				break;
			} else if (eName.equals("emphasizeRightAngle")) {
				ok = handleEmphasizeRightAngle(attrs);
				break; 
			} 

		case 'f':
			if (eName.equals("fixed")) {
				ok = handleFixed(attrs);
				break;
			} else if (eName.equals("file")) {
				ok = handleFile(attrs);
				break;
			} else if (eName.equals("font")) {
				ok = handleTextFont(attrs);
				break;
			}
			// Michael Borcherds 2007-11-19
			else if (eName.equals("forceReflexAngle")) {
				ok = handleForceReflexAngle(attrs);
				break;
			}
			// Michael Borcherds 2007-11-19

		case 'i':
			if (eName.equals("isLaTeX")) {
				ok = handleIsLaTeX(attrs);
				break;
			} else if (eName.equals("inBackground")) {
				ok = handleInBackground(attrs);
				break;
			}

		case 'k':
			if (eName.equals("keepTypeOnTransform")) {
				ok = handleKeepTypeOnTransform(attrs);
				break;
			}

		case 'l':
			if (eName.equals("lineStyle")) {
				ok = handleLineStyle(attrs);
				break;
			} else if (eName.equals("labelOffset")) {
				ok = handleLabelOffset(attrs);
				break;
			} else if (eName.equals("labelMode")) {
				ok = handleLabelMode(attrs);
				break;
			} else if (eName.equals("layer")) {
				ok = handleLayer(attrs);
				break;
			}

		case 'm':
			if (eName.equals("matrix")) {
				ok = handleMatrix(attrs);
				break;
			}

		case 'o':
			if (eName.equals("objColor")) {
				ok = handleObjColor(attrs);
				break;
			} else if (eName.equals("outlyingIntersections")) {
				ok = handleOutlyingIntersections(attrs);
				break;
			} /*else if (eName.equals("objCoords")) {
				ok = handleObjCoords(attrs);
				break;
			}*/

		case 'p':
			if (eName.equals("pointSize")) {
				ok = handlePointSize(attrs);
				break;
			}

			// Florian Sonner 2008-07-17
			else if (eName.equals("pointStyle")) {
				ok = handlePointStyle(attrs);
				break;
			}
			/*
			 * should not be needed else if (eName.equals("pathParameter")) { ok =
			 * handlePathParameter(attrs); break; }
			 */

		case 's':
			if (eName.equals("show")) {
				ok = handleShow(attrs);
				break;
			} else if (eName.equals("startPoint")) {
				ok = handleStartPoint(attrs);
				break;
			} else if (eName.equals("slider")) {
				ok = handleSlider(attrs);
				break;
			} else if (eName.equals("slopeTriangleSize")) {
				ok = handleSlopeTriangleSize(attrs);
				break;
			} else if (eName.equals("significantfigures")) {
				ok = handleTextFigures(attrs);
				break;
			} else if (eName.equals("spreadsheetTrace")) {
				ok = handleSpreadsheetTrace(attrs);
				break;
			}

		case 't':
			if (eName.equals("trace")) {
				ok = handleTrace(attrs);
				break;
			}

		case 'v':
			if (eName.equals("value")) {
				ok = handleValue(attrs);
				break;
			}

		default:
			System.err.println("unknown tag in <element>: " + eName);
		}

		if (!ok)
			System.err.println("error in <element>: " + eName);
	}

	private boolean handleShow(LinkedHashMap<String, String> attrs) {
		try {
			geo.setEuclidianVisible(parseBoolean((String) attrs.get("object")));
			geo.setLabelVisible(parseBoolean((String) attrs.get("label")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleObjColor(LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		geo.setObjColor(col);

		// Dynamic colors
		// Michael Borcherds 2008-04-02
		String red = "";
		String green = "";
		String blue = "";
		red = (String) attrs.get("dynamicr");
		green = (String) attrs.get("dynamicg");
		blue = (String) attrs.get("dynamicb");

		if (red != null && green != null && blue != null)
			try {
				if (!red.equals("") || !green.equals("") || !blue.equals("")) {
					if (red.equals(""))
						red = "0";
					if (green.equals(""))
						green = "0";
					if (blue.equals(""))
						blue = "0";

					// geo.setColorFunction(kernel.getAlgebraProcessor().evaluateToList("{"+red
					// + ","+green+","+blue+"}"));
					// need to to this at end of construction (dependencies!)
					dynamicColorList.add(new GeoExpPair(geo, "{" + red + ","
							+ green + "," + blue + "}"));

				}
			} catch (Exception e) {
				System.err.println("Error loading Dynamic Colors");
			}

		String alpha = (String) attrs.get("alpha");
		if (alpha != null
				&& (!geo.isGeoList() || ggbFileFormat > 3.19)) // ignore alpha value for lists prior to GeoGebra 3.2
			geo.setAlphaValue(Float.parseFloat(alpha));
		return true;
	}
	
	/*
	private boolean handleObjCoords(LinkedHashMap<String, String> attrs) {

		// Dynamic coordinates
		// Michael Borcherds 2008-11-30
		String x = "";
		String y = "";
		x = (String) attrs.get("dynamicx");
		y = (String) attrs.get("dynamicy");


		if (x != null && y != null)
			try {
				if (!x.equals("") || !y.equals("")) {
					if (x.equals(""))
						x = "0";
					if (y.equals(""))
						y = "0";
					
					// geo.setColorFunction(kernel.getAlgebraProcessor().evaluateToList("{"+red
					// + ","+green+","+blue+"}"));
					// need to to this at end of construction (dependencies!)
					dynamicCoordinatesList.add(new GeoExpPair(geo, "{" + x + ","
							+ y +"}"));

				}
			} catch (Exception e) {
				Application.debug("Error loading Dynamic Coordinates");
			}

			return true;
	}*/

	/*
	 * expects r, g, b attributes to build a colo
	 */
	private Color handleColorAttrs(LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt((String) attrs.get("r"));
			int green = Integer.parseInt((String) attrs.get("g"));
			int blue = Integer.parseInt((String) attrs.get("b"));
			return new Color(red, green, blue);
		} catch (Exception e) {
			return null;
		}
	}

	private boolean handleLineStyle(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLineType(Integer.parseInt((String) attrs.get("type")));			
			geo.setLineThickness(Integer.parseInt((String) attrs.get("thickness")));						
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleDecoration(LinkedHashMap<String, String> attrs) {
		try {
			geo.setDecorationType(Integer.parseInt((String) attrs.get("type")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEqnStyle(LinkedHashMap<String, String> attrs) {
		// line
		if (geo.isGeoLine()) {
			GeoLine line = (GeoLine) geo;
			String style = (String) attrs.get("style");
			if (style.equals("implicit")) {
				line.setToImplicit();
			} else if (style.equals("explicit")) {
				line.setToExplicit();
			} else if (style.equals("parametric")) {
				String parameter = (String) attrs.get("parameter");
				line.setToParametric(parameter);
			} else {
				System.err.println("unknown style for line in <eqnStyle>: "
						+ style);
				return false;
			}
		}
		// conic
		else if (geo.isGeoConic()) {
			GeoConic conic = (GeoConic) geo;
			String style = (String) attrs.get("style");
			if (style.equals("implicit")) {
				conic.setToImplicit();
			} else if (style.equals("specific")) {
				conic.setToSpecific();
			} else if (style.equals("explicit")) {
				conic.setToExplicit();
			} else {
				System.err.println("unknown style for conic in <eqnStyle>: "
						+ style);
				return false;
			}
		} else {
			System.err.println("wrong element type for <eqnStyle>: "
					+ geo.getClass());
			return false;
		}
		return true;
	}

	private boolean handleCoords(LinkedHashMap<String, String> attrs) {
		
		return kernel.handleCoords(geo, attrs);
		
		/*
		if (!(geo instanceof GeoVec3D)) {
			System.err.println("wrong element type for <coords>: "
					+ geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;
		


		try {
			// for points: make path parameter invalid to force update in setCoords
			if (v.isGeoPoint()) {
				GeoPoint p = (GeoPoint) v;
				if (p.hasPath()) 
					p.clearPathParameter();
			}
			
			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			double z = Double.parseDouble((String) attrs.get("z"));
			v.setCoords(x, y, z);						
			return true;
		} catch (Exception e) {
			return false;
		}
		*/
	}

	// for point or vector
	private boolean handleCoordStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoPoint() || geo.isGeoVector())) {
			System.err.println("wrong element type for <coordStyle>: "
					+ geo.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geo;
		String style = (String) attrs.get("style");
		if (style.equals("cartesian")) {
			v.setCartesian();
		} else if (style.equals("polar")) {
			v.setPolar();
		} else if (style.equals("complex")) {
			v.setComplex();
		} else {
			System.err.println("unknown style in <coordStyle>: " + style);
			return false;
		}
		return true;
	}

	private boolean handleCaption(LinkedHashMap<String, String> attrs) {
		try {
			geo.setCaption((String) attrs.get("val"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCondition(LinkedHashMap<String, String> attrs) {
		try {
			// condition for visibility of object
			String strShowObjectCond = (String) attrs.get("showObject");
			if (strShowObjectCond != null) {
				// store (geo, epxression) values
				// they will be processed in processShowObjectConditionList()
				// later
				showObjectConditionList.add(new GeoExpPair(geo,
						strShowObjectCond));
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCheckbox(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoBoolean())) {
			System.err.println("wrong element type for <checkbox>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoBoolean bool = (GeoBoolean) geo;
			bool.setCheckboxFixed(parseBoolean((String) attrs.get("fixed")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleValue(LinkedHashMap<String, String> attrs) {
		boolean isBoolean = geo.isGeoBoolean();
		boolean isNumber = geo.isGeoNumeric();
		boolean isButton = geo.isGeoJavaScriptButton();

		if (!(isNumber || isBoolean || isButton)) {
			Application.debug("wrong element type for <value>: "
					+ geo.getClass());
			return false;
		}

		try {
			String strVal = (String) attrs.get("val");
			if (isNumber) {
				GeoNumeric n = (GeoNumeric) geo;
				n.setValue(Double.parseDouble(strVal));
			} else if (isBoolean) {
				GeoBoolean bool = (GeoBoolean) geo;
				bool.setValue(parseBoolean(strVal));
			} else if (isButton) {
				GeoJavaScriptButton button = (GeoJavaScriptButton)geo;
				button.setScript(strVal);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handlePointSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			Application.debug("wrong element type for <pointSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			p.setPointSize(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Florian Sonner 2008-07-17
	private boolean handlePointStyle(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof PointProperties)) {
			Application.debug("wrong element type for <pointStyle>: "
					+ geo.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geo;
			
			int style = Integer.parseInt((String) attrs.get("val"));
			
			if(style == -1) {
				style = docPointStyle;
			}
			p.setPointStyle(style);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Michael Borcherds 2008-02-26
	private boolean handleLayer(LinkedHashMap<String, String> attrs) {

		try {
			geo.setLayer(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleInputExpression(LinkedHashMap<String, String> attrs) {
		try {
//			app.loadCASJar();
//			((geogebra.cas.view.CASTableCellValue) casTableCellValueElement)
//					.setCommand((String) attrs.get("value"));

			casTableCellValueElement = app.getCasView().setInputExpression(casTableCellValueElement,
					(String) attrs.get("value"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleOutExpression(LinkedHashMap<String, String> attrs) {
		try {
			String ggbString = (String) attrs.get("value");
			String latexString = (String) attrs.get("latex");
			casTableCellValueElement = app.getCasView().setOutputExpression(casTableCellValueElement,
					ggbString);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCASPairColor(LinkedHashMap<String, String> attrs) {
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		// geo.setObjColor(col);

		return true;
	}

	/*
	 * this should not be needed private boolean
	 * handlePathParameter(LinkedHashMap<String, String> attrs) { if (!(geo.isGeoPoint())) {
	 * Application.debug( "wrong element type for <handlePathParameter>: " +
	 * geo.getClass()); return false; }
	 * 
	 * try { GeoPoint p = (GeoPoint) geo; PathParameter param = new
	 * PathParameter(); double t = Double.parseDouble((String)
	 * attrs.get("val")); param.setT(t);
	 * 
	 * String strBranch = (String) attrs.get("branch"); if (strBranch != null) {
	 * param.setBranch(Integer.parseInt(strBranch)); }
	 * 
	 * String strType = (String) attrs.get("type"); if (strType != null) {
	 * param.setPathType(Integer.parseInt(strType)); }
	 * 
	 * p.initPathParameter(param); return true; } catch (Exception e) { return
	 * false; } }
	 */

	private boolean handleSlider(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			System.err.println("wrong element type for <slider>: "
					+ geo.getClass());
			return false;
		}

		try {
			// don't create sliders in macro construction
			if (geo.getKernel().isMacroKernel())
				return true;

			GeoNumeric num = (GeoNumeric) geo;

			String str = (String) attrs.get("min");
			if (str != null) {
				num.setIntervalMin(Double.parseDouble(str));
			}

			str = (String) attrs.get("max");
			if (str != null) {
				num.setIntervalMax(Double.parseDouble(str));
			}

			str = (String) attrs.get("absoluteScreenLocation");
			if (str != null) {
				num.setAbsoluteScreenLocActive(parseBoolean(str));
			} else {
				num.setAbsoluteScreenLocActive(false);
			}

			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			num.setSliderLocation(x, y);
			num.setSliderWidth(Double.parseDouble((String) attrs.get("width")));
			num.setSliderFixed(parseBoolean((String) attrs.get("fixed")));
			num.setSliderHorizontal(parseBoolean((String) attrs
					.get("horizontal")));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTrace(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof Traceable)) {
			System.err.println("wrong element type for <trace>: "
					+ geo.getClass());
			return false;
		}

		try {
			Traceable t = (Traceable) geo;
			t.setTrace(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSpreadsheetTrace(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoPoint)) {
			System.err.println("wrong element type for <trace>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoPoint p = (GeoPoint) geo;
			p.setSpreadsheetTrace(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAnimation(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAnimationStep(Double.parseDouble((String) attrs.get("step")));
			
			String strSpeed = (String) attrs.get("speed");
			if (strSpeed != null) {
				// store speed expression to be processed later
				animationSpeedList.add(new GeoExpPair(geo, strSpeed));			
			}
				
			String type = (String) attrs.get("type");
			if (type != null)
				geo.setAnimationType(Integer.parseInt(type));
			
			
			geo.setAnimating(parseBoolean((String) attrs.get("playing")));
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFixed(LinkedHashMap<String, String> attrs) {
		try {
			geo.setFixed(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleBreakpoint(LinkedHashMap<String, String> attrs) {
		try {
			geo.setConsProtocolBreakpoint(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFile(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			System.err.println("wrong element type for <file>: "
					+ geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setFileName((String) attrs.get("name"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// <font serif="false" size="12" style="0">
	private boolean handleTextFont(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			System.err.println("wrong element type for <font>: "
					+ geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setSerifFont(parseBoolean((String) attrs.get("serif")));
			text.setFontSize(Integer.parseInt((String) attrs.get("size")));
			text.setFontStyle(Integer.parseInt((String) attrs.get("style")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTextDecimals(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			System.err.println("wrong element type for <decimals>: "
					+ geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintDecimals(Integer.parseInt((String) attrs.get("val")), true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTextFigures(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof TextProperties)) {
			System.err.println("wrong element type for <decimals>: "
					+ geo.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geo;
			text.setPrintFigures(Integer.parseInt((String) attrs.get("val")), true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleInBackground(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoImage())) {
			System.err.println("wrong element type for <inBackground>: "
					+ geo.getClass());
			return false;
		}

		try {
			((GeoImage) geo).setInBackground(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAuxiliary(LinkedHashMap<String, String> attrs) {
		try {
			geo.setAuxiliaryObject(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleIsLaTeX(LinkedHashMap<String, String> attrs) {
		try {


			((GeoText) geo).setLaTeX(parseBoolean((String) attrs.get("val")),
					false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleArcSize(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof GeoAngle)) {
			System.err.println("wrong element type for <arcSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setArcSize(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAbsoluteScreenLocation(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof AbsoluteScreenLocateable)) {
			Application
					.debug("wrong element type for <absoluteScreenLocation>: "
							+ geo.getClass());
			return false;
		}

		try {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			int x = Integer.parseInt((String) attrs.get("x"));
			int y = Integer.parseInt((String) attrs.get("y"));
			absLoc.setAbsoluteScreenLoc(x, y);
			absLoc.setAbsoluteScreenLocActive(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAllowReflexAngle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoAngle())) {
			System.err.println("wrong element type for <allowReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setAllowReflexAngle(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}
		
	private boolean handleEmphasizeRightAngle(LinkedHashMap attrs) {
		if (!(geo.isGeoAngle())) {
			System.err.println("wrong element type for <emphasizeRightAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setEmphasizeRightAngle(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	// Michael Borcherds 2007-11-19
	private boolean handleForceReflexAngle(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoAngle())) {
			System.err.println("wrong element type for <forceReflexAngle>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geo;
			angle.setForceReflexAngle(parseBoolean((String) attrs.get("val")));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	// Michael Borcherds 2007-11-19

	private boolean handleOutlyingIntersections(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Application
					.debug("wrong element type for <outlyingIntersections>: "
							+ geo.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setAllowOutlyingIntersections(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKeepTypeOnTransform(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof LimitedPath)) {
			Application
					.debug("wrong element type for <outlyingIntersections>: "
							+ geo.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geo;
			lpath.setKeepTypeOnGeometricTransform(parseBoolean((String) attrs
					.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleSlopeTriangleSize(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoNumeric())) {
			System.err.println("wrong element type for <slopeTriangleSize>: "
					+ geo.getClass());
			return false;
		}

		try {
			GeoNumeric num = (GeoNumeric) geo;
			num.setSlopeTriangleSize(Integer
					.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Start Points have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, startpoint expression) pairs and process them at
	 * the end of the construction.
	 * 
	 * @see processStartPointList
	 */
	private boolean handleStartPoint(LinkedHashMap<String, String> attrs) {
		if (!(geo instanceof Locateable)) {
			System.err.println("wrong element type for <startPoint>: "
					+ geo.getClass());
			return false;
		}
		Locateable loc = (Locateable) geo;

		// relative start point (expression or label expected)
		String exp = (String) attrs.get("exp");
		if (exp == null) // try deprecated attribute
			exp = (String) attrs.get("label");

		// for corners a number of the startPoint is given
		int number = 0;
		try {
			number = Integer.parseInt((String) attrs.get("number"));
		} catch (Exception e) {
		}

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processStartPoints() later
			startPointList.add(new LocateableExpPair(loc, exp, number));	
			loc.setWaitForStartPoint();
		}
		else {
			// absolute start point (coords expected)
			try {
				double x = Double.parseDouble((String) attrs.get("x"));
				double y = Double.parseDouble((String) attrs.get("y"));
				double z = Double.parseDouble((String) attrs.get("z"));
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(x, y, z);
				
				if (number == 0) {
					// set first start point right away
					loc.setStartPoint(p);
				} else {
					// set other start points later
					// store (geo, point, number) values
					// they will be processed in processStartPoints() later
					startPointList.add(new LocateableExpPair(loc, p, number));	
					loc.setWaitForStartPoint();
				}				
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}

	private void processStartPointList() {
		try {
			Iterator it = startPointList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				LocateableExpPair pair = (LocateableExpPair) it.next();
				GeoPoint P = pair.point != null ? pair.point : 
								algProc.evaluateToPoint(pair.exp);
				pair.locateable.setStartPoint(P, pair.number);
			}
		} catch (Exception e) {
			startPointList.clear();
			e.printStackTrace();
			throw new MyError(app, "processStartPointList: " + e.toString());
		}
		startPointList.clear();
	}

	private void processShowObjectConditionList() {
		try {
			Iterator it = showObjectConditionList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				GeoBoolean condition = algProc.evaluateToBoolean(pair.exp);
				pair.geo.setShowObjectCondition(condition);
			}
		} catch (Exception e) {
			showObjectConditionList.clear();
			e.printStackTrace();
			throw new MyError(app, "processShowObjectConditionList: "
					+ e.toString());
		}
		showObjectConditionList.clear();
	}
	
	private void processAnimationSpeedList() {
		try {
			Iterator it = animationSpeedList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp);
				pair.geo.setAnimationSpeedObject(num);
			}
		} catch (Exception e) {
			animationSpeedList.clear();
			e.printStackTrace();
			throw new MyError(app, "processAnimationSpeedList: " + e.toString());
		}
		animationSpeedList.clear();
	}
	


	// Michael Borcherds 2008-05-18
	private void processDynamicColorList() {
		try {
			Iterator it = dynamicColorList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				pair.geo.setColorFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (Exception e) {
			dynamicColorList.clear();
			e.printStackTrace();
			throw new MyError(app, "dynamicColorList: " + e.toString());
		}
		dynamicColorList.clear();
	}

	/*
	private void processDynamicCoordinatesList() {
		try {

			Iterator it = dynamicCoordinatesList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				((GeoPoint)(pair.geo)).setCoordinateFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (Exception e) {
			dynamicCoordinatesList.clear();
			e.printStackTrace();
			throw new MyError(app, "dynamicCoordinatesList: " + e.toString());
		}
		dynamicCoordinatesList.clear();
	}*/

	private boolean handleEigenvectors(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoConic())) {
			System.err.println("wrong element type for <eigenvectors>: "
					+ geo.getClass());
			return false;
		}
		try {
			GeoConic conic = (GeoConic) geo;
			// set eigenvectors, but don't classify conic now
			// classifyConic() will be called in handleMatrix() by
			// conic.setMatrix()
			conic.setEigenvectors(Double.parseDouble((String) attrs.get("x0")),
					Double.parseDouble((String) attrs.get("y0")), Double
							.parseDouble((String) attrs.get("z0")), Double
							.parseDouble((String) attrs.get("x1")), Double
							.parseDouble((String) attrs.get("y1")), Double
							.parseDouble((String) attrs.get("z1")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleMatrix(LinkedHashMap<String, String> attrs) {
		if (!(geo.isGeoConic())) {
			System.err.println("wrong element type for <matrix>: "
					+ geo.getClass());
			return false;
		}
		try {
			GeoConic conic = (GeoConic) geo;
			// set matrix and classify conic now
			// <eigenvectors> should have been set earlier
			double[] matrix = { Double.parseDouble((String) attrs.get("A0")),
					Double.parseDouble((String) attrs.get("A1")),
					Double.parseDouble((String) attrs.get("A2")),
					Double.parseDouble((String) attrs.get("A3")),
					Double.parseDouble((String) attrs.get("A4")),
					Double.parseDouble((String) attrs.get("A5")) };
			conic.setMatrix(matrix);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelOffset(LinkedHashMap<String, String> attrs) {
		try {
			geo.labelOffsetX = Integer.parseInt((String) attrs.get("x"));
			geo.labelOffsetY = Integer.parseInt((String) attrs.get("y"));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelMode(LinkedHashMap<String, String> attrs) {
		try {
			geo.setLabelMode(Integer.parseInt((String) attrs.get("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// ====================================
	// <command>
	// ====================================

	// called when <command> is encountered
	// e.g. for <command name="Intersect">
	private Command getCommand(LinkedHashMap<String, String> attrs) {
		Command cmd = null;
		String name = (String) attrs.get("name");

		//Application.debug(name);
		if (name != null)
			cmd = new Command(kernel, name, false); // do not translate name
		else
			throw new MyError(app, "name missing in <command>");
		return cmd;
	}

	private void startCommandElement(String eName, LinkedHashMap<String, String> attrs) {
		boolean ok = true;
		if (eName.equals("input")) {
			if (cmd == null)
				throw new MyError(app, "no command set for <input>");
			ok = handleCmdInput(attrs);
		} else if (eName.equals("output")) {
			ok = handleCmdOutput(attrs);
		} else
			System.err.println("unknown tag in <command>: " + eName);

		if (!ok)
			System.err.println("error in <command>: " + eName);
	}

	private boolean handleCmdInput(LinkedHashMap<String, String> attrs) {
		GeoElement geo;
		ExpressionNode en;
		String arg = null;

		Collection values = attrs.values();
		Iterator it = values.iterator();
		while (it.hasNext()) {
			// parse argument expressions
			try {
				arg = (String) it.next();

				// for downward compatibility: lookup label first
				// as this could be some weird name that can't be parsed
				// e.g. "1/2_{a,b}" could be a label name
				geo = kernel.lookupLabel(arg);
				
				//Application.debug("input : "+geo.getLabel());

				// arg is a label and does not conatin $ signs (e.g. $A1 in
				// spreadsheet)
				if (geo != null && arg.indexOf('$') < 0) {
					en = new ExpressionNode(kernel, geo);
				} else {
					// parse argument expressions
					en = parser.parseCmdExpression(arg);
				}
				cmd.addArgument(en);
			} catch (Exception e) {
				e.printStackTrace();
				throw new MyError(app, "unknown command input: " + arg);
			} catch (Error e) {
				e.printStackTrace();
				throw new MyError(app, "unknown command input: " + arg);
			}
		}
		return true;
	}

	private boolean handleCmdOutput(LinkedHashMap<String, String> attrs) {
		try {
			// set labels for command processing
			String label;
			Collection values = attrs.values();
			Iterator it = values.iterator();
			int countLabels = 0;
			while (it.hasNext()) {
				label = (String) it.next();
				if ("".equals(label))
					label = null;
				else
					countLabels++;
				cmd.addLabel(label);
			}

			// it is possible that we get a command that has been saved
			// where NONE of its output objects had a label
			// (e.g. intersection that never produced any points).
			// Such a command should not be processed as it might
			// use up labels that are needed later on.
			// For example, since v3.0 every intersection command shows
			// at least one labeled (and possibly undefined) point
			// whereas in v2.7 the label was not set before an intersection
			// point became defined for the first time.
			// THUS: let's not process commands with no labels for their output
			if (countLabels == 0)
				return true;

			// process the command
			//String s ="";
			//ExpressionNode[] en = cmd.getArguments();
			//for(int i=0;i<en.length;i++)
			//	s+=en[i].toString()+",";
			//Application.debug("process the command : "+cmd.getLabel()+" = "+cmd.getName()+"["+s+"]");
			
			cmdOutput = kernel.getAlgebraProcessor().processCommand(cmd, true);
			
			//Application.debug(cmdOutput[0].getLabel());
			
			String cmdName = cmd.getName();
			if (cmdOutput == null)
				throw new MyError(app, "processing of command " + cmdName
						+ " failed");
			cmd = null;

			// ensure that labels are set for invisible objects too
			if (attrs.size() != cmdOutput.length) {
				Application
						.debug("error in <output>: wrong number of labels for command "
								+ cmdName);
				System.err.println("   cmdOutput.length = " + cmdOutput.length
						+ ", labels = " + attrs.size());
				return false;
			}
			// enforce setting of labels
			// (important for invisible objects like intersection points)
			it = values.iterator();
			int i = 0;
			while (it.hasNext()) {
				label = (String) it.next();
				if ("".equals(label))
					label = null;

				if (label != null) {
					cmdOutput[i].setLoadedLabel(label);
					//Application.debug("cmdOutput["+i+"].setLoadedLabel("+label+")");
				}
				i++;
			}
			return true;
		} catch (MyError e) {
			throw e;
		} catch (Exception e) {
			throw new MyError(app, "processing of command: " + e.getMessage());
		}
	}

	/**
	 * Reads all attributes into a String array.
	 * 
	 * @param attrs
	 * @return
	 */
	private String[] getAttributeStrings(LinkedHashMap<String, String> attrs) {
		Collection values = attrs.values();
		Iterator it = values.iterator();

		String[] ret = new String[values.size()];
		int i = 0;

		while (it.hasNext()) {
			ret[i] = (String) it.next();
			i++;
		}
		return ret;
	}

	// ====================================
	// <expression>
	// ====================================
	private void startExpressionElement(String eName, LinkedHashMap<String, String> attrs) {
		String label = (String) attrs.get("label");
		String exp = (String) attrs.get("exp");
		if (exp == null)
			throw new MyError(app, "exp missing in <expression>");

		// type may be vector or point, this is important to distinguish between
		// them
		String type = (String) attrs.get("type");

		// parse expression and process it
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(exp);
			if (label != null)
				ve.setLabel(label);

			// enforce point or vector type if it was given in attribute type
			if (type != null) {
				if (type.equals("point")) {
					((ExpressionNode) ve).setForcePoint();
				} else if (type.equals("vector")) {
					((ExpressionNode) ve).setForceVector();
				}
			}

			GeoElement[] result = kernel.getAlgebraProcessor()
					.processValidExpression(ve);

			// ensure that labels are set for invisible objects too
			if (result != null && label != null && result.length == 1) {
				result[0].setLoadedLabel(label);
			} else {
				System.err.println("error in <expression>: " + exp + ", label: "
						+ label);
			}

		} catch (Exception e) {
			String msg = "error in <expression>: label=" + label + ", exp= "
					+ exp;
			System.err.println(msg);
			e.printStackTrace();
			throw new MyError(app, msg);
		} catch (Error e) {
			String msg = "error in <expression>: label = " + label + ", exp = "
					+ exp;
			System.err.println(msg);
			e.printStackTrace();
			throw new MyError(app, msg);
		}
	}

	// ====================================
	// UTILS
	// ====================================

	private boolean parseBoolean(String str) throws Exception {
		return "true".equals(str);
	}
}

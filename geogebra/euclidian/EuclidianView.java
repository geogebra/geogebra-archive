/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.euclidian;

import geogebra.Application;
import geogebra.View;
import geogebra.euclidian.DrawableList.DrawableIterator;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoIntegralFunctions;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.AlgoSumUpperLower;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoRay;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.ParametricCurve;
import geogebra.util.FastHashMapKeyless;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class EuclidianView extends JPanel implements View, Printable {

	protected static final long serialVersionUID = 1L;

	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;
	
	protected static final String PI_STRING = "\u03c0";

	// pixel per centimeter (at 72dpi)
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;

	public static final double MODE_ZOOM_FACTOR = 1.5;

	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;

	public static final double SCALE_STANDARD = 50;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	public static final double XZERO_STANDARD = 215;

	public static final double YZERO_STANDARD = 315;

	public static final int LINE_TYPE_FULL = 0;

	public static final int LINE_TYPE_DASHED_SHORT = 10;

	public static final int LINE_TYPE_DASHED_LONG = 15;

	public static final int LINE_TYPE_DOTTED = 20;

	public static final int LINE_TYPE_DASHED_DOTTED = 30;

	public static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(LINE_TYPE_FULL),
				new Integer(LINE_TYPE_DASHED_LONG),
				new Integer(LINE_TYPE_DASHED_SHORT),
				new Integer(LINE_TYPE_DOTTED),
				new Integer(LINE_TYPE_DASHED_DOTTED) };
		return ret;
	}

	public static final int AXES_LINE_TYPE_FULL = 0;

	public static final int AXES_LINE_TYPE_ARROW = 1;

	public static final int AXES_LINE_TYPE_FULL_BOLD = 2;

	public static final int AXES_LINE_TYPE_ARROW_BOLD = 3;

	public static final int AXES_TICK_STYLE_MAJOR_MINOR = 0;

	public static final int AXES_TICK_STYLE_MAJOR = 1;

	public static final int AXES_TICK_STYLE_NONE = 2;

	public static final int POINT_STYLE_DOT = 0;

	public static final int POINT_STYLE_CROSS = 1;

	public static final int POINT_STYLE_CIRCLE = 2;

	public static final int RIGHT_ANGLE_STYLE_NONE = 0;

	public static final int RIGHT_ANGLE_STYLE_SQUARE = 1;

	public static final int RIGHT_ANGLE_STYLE_DOT = 2;

	public static final int DEFAULT_POINT_SIZE = 3;

	public static final int DEFAULT_LINE_THICKNESS = 2;

	public static final int DEFAULT_ANGLE_SIZE = 30;

	public static final int DEFAULT_LINE_TYPE = LINE_TYPE_FULL;

	public static final float SELECTION_ADD = 2.0f;

	public static final int MODE_MOVE = 0;

	public static final int MODE_POINT = 1;

	public static final int MODE_JOIN = 2;

	public static final int MODE_PARALLEL = 3;

	public static final int MODE_ORTHOGONAL = 4;

	public static final int MODE_INTERSECT = 5;

	public static final int MODE_DELETE = 6;

	public static final int MODE_VECTOR = 7;

	public static final int MODE_LINE_BISECTOR = 8;

	public static final int MODE_ANGULAR_BISECTOR = 9;

	public static final int MODE_CIRCLE_TWO_POINTS = 10;

	public static final int MODE_CIRCLE_THREE_POINTS = 11;

	public static final int MODE_CONIC_FIVE_POINTS = 12;

	public static final int MODE_TANGENTS = 13;

	public static final int MODE_RELATION = 14;

	public static final int MODE_SEGMENT = 15;

	public static final int MODE_POLYGON = 16;

	public static final int MODE_TEXT = 17;

	public static final int MODE_RAY = 18;

	public static final int MODE_MIDPOINT = 19;

	public static final int MODE_CIRCLE_ARC_THREE_POINTS = 20;

	public static final int MODE_CIRCLE_SECTOR_THREE_POINTS = 21;

	public static final int MODE_CIRCUMCIRCLE_ARC_THREE_POINTS = 22;

	public static final int MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS = 23;

	public static final int MODE_SEMICIRCLE = 24;

	public static final int MODE_SLIDER = 25;

	public static final int MODE_IMAGE = 26;

	public static final int MODE_SHOW_HIDE_OBJECT = 27;

	public static final int MODE_SHOW_HIDE_LABEL = 28;

	public static final int MODE_MIRROR_AT_POINT = 29;

	public static final int MODE_MIRROR_AT_LINE = 30;

	public static final int MODE_TRANSLATE_BY_VECTOR = 31;

	public static final int MODE_ROTATE_BY_ANGLE = 32;

	public static final int MODE_DILATE_FROM_POINT = 33;

	public static final int MODE_CIRCLE_POINT_RADIUS = 34;

	public static final int MODE_COPY_VISUAL_STYLE = 35;

	public static final int MODE_ANGLE = 36;

	public static final int MODE_VECTOR_FROM_POINT = 37;

	public static final int MODE_DISTANCE = 38;

	public static final int MODE_MOVE_ROTATE = 39;

	public static final int MODE_TRANSLATEVIEW = 40;

	public static final int MODE_ZOOM_IN = 41;

	public static final int MODE_ZOOM_OUT = 42;

	public static final int MODE_ALGEBRA_INPUT = 43;

	public static final int MODE_POLAR_DIAMETER = 44;

	public static final int MODE_SEGMENT_FIXED = 45;

	public static final int MODE_ANGLE_FIXED = 46;

	public static final int MODE_LOCUS = 47;

	public static final int MODE_MACRO = 48;
	
	public static final int MODE_AREA = 49;
	
	public static final int MODE_SLOPE = 50;
	
	public static final int MODE_REGULAR_POLYGON = 51;
	
	public static final int MODE_SHOW_HIDE_CHECKBOX = 52;

	public static final int MACRO_MODE_ID_OFFSET = 1001;
	
	public static final int POINT_CAPTURING_OFF = 0;
	public static final int POINT_CAPTURING_ON = 1;
	public static final int POINT_CAPTURING_ON_GRID = 2;
	public static final int POINT_CAPTURING_AUTOMATIC = 3;
	

	// zoom rectangle colors
	protected static final Color colZoomRectangle = new Color(200, 200, 230);
	protected static final Color colZoomRectangleFill = new Color(200, 200, 230, 50);

	// STROKES
	protected static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	protected static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + SELECTION_ADD);

	// protected static MyBasicStroke thinStroke = new MyBasicStroke(1.0f);

	// axes strokes
	protected static BasicStroke defAxesStroke = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	protected static BasicStroke boldAxesStroke = new BasicStroke(1.8f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	// axes and grid stroke
	protected BasicStroke axesStroke, tickStroke, gridStroke;

	protected Line2D.Double tempLine = new Line2D.Double();

	protected static RenderingHints defRenderingHints = new RenderingHints(null);
	{
		defRenderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		defRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		defRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);

		// needed for nice antialiasing of GeneralPath objects:
		// defRenderingHints.put(RenderingHints.KEY_STROKE_CONTROL,
		// RenderingHints.VALUE_STROKE_PURE);
	}

	// FONTS
	public Font fontPoint, fontLine, fontVector, fontConic, fontCoords,
			fontAxes, fontAngle;

	int fontSize;

	// member variables
	protected Application app;

	protected Kernel kernel;

	protected EuclidianController euclidianController;

	AffineTransform coordTransform = new AffineTransform();

	int width, height;

	protected NumberFormat[] axesNumberFormat;

	protected NumberFormat printScaleNF;

	double xmin, xmax, ymin, ymax, invXscale, invYscale, xZero, yZero, xscale,
			yscale, scaleRatio = 1.0; // ratio yscale / xscale

	protected double[] AxesTickInterval = { 1, 1 }; // for axes =

	// axesNumberingDistances /
	// 2

	boolean showGrid = false;

	protected boolean antiAliasing = true;

	boolean showMouseCoords = false;
	boolean showAxesRatio = false;

	protected int pointCapturingMode; // snap to grid points

	// added by Loïc BEGIN
	// right angle
	int rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;

	// END

	int pointStyle = POINT_STYLE_DOT;

	int mode = MODE_MOVE;

	protected boolean[] showAxes = { true, true };

	protected boolean[] showAxesNumbers = { true, true };

	protected String[] axesLabels = { null, null };

	protected String[] axesUnitLabels = { null, null };

	protected boolean[] piAxisUnit = { false, false };

	protected int[] axesTickStyles = { AXES_TICK_STYLE_MAJOR,
			AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances = { 2, 2 };

	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

	double[] gridDistances = { 2, 2 };

	protected int gridLineStyle, axesLineType;

	// colors: axes, grid, background
	protected Color axesColor, gridColor, bgColor;

	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	protected FastHashMapKeyless DrawableMap = new FastHashMapKeyless(500);

	protected DrawableList allDrawableList = new DrawableList();

	protected DrawableList drawBooleanList = new DrawableList();
	protected DrawableList drawPointList = new DrawableList();

	protected DrawableList drawLineList = new DrawableList();

	protected DrawableList drawSegmentList = new DrawableList();

	protected DrawableList drawVectorList = new DrawableList();

	protected DrawableList drawConicList = new DrawableList();

	protected DrawableList drawFunctionList = new DrawableList();

	protected DrawableList drawTextList = new DrawableList();		

	protected DrawableList drawImageList = new DrawableList();

	protected DrawableList drawLocusList = new DrawableList();

	protected DrawableList drawPolygonList = new DrawableList();

	protected DrawableList drawNumericList = new DrawableList();
	
	protected DrawableList drawListList = new DrawableList();

	// on add: change resetLists()

	protected DrawableList bgImageList = new DrawableList();

	Previewable previewDrawable;

	protected Rectangle selectionRectangle;

	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	// or use volatile image
	// protected int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
	protected BufferedImage bgImage;
	protected Graphics2D bgGraphics; // g2d of bgImage
	protected Image resetImage;
	
	// temp image
	protected Graphics2D g2Dtemp = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB).createGraphics();

	protected StringBuffer sb = new StringBuffer();

	protected Cursor defaultCursor;

	/**
	 * Creates EuclidianView
	 */
	public EuclidianView(EuclidianController ec, boolean[] showAxes,
			boolean showGrid) {
		euclidianController = ec;
		kernel = ec.getKernel();
		app = ec.getApplication();
		resetImage = app.getInternalImage("view-refresh.png");
		this.showAxes[0] = showAxes[0];
		this.showAxes[1] = showAxes[1];
		this.showGrid = showGrid;

		axesNumberFormat = new NumberFormat[2];
		axesNumberFormat[0] = NumberFormat.getInstance(Locale.ENGLISH);
		axesNumberFormat[1] = NumberFormat.getInstance(Locale.ENGLISH);
		axesNumberFormat[0].setGroupingUsed(false);
		axesNumberFormat[1].setGroupingUsed(false);

		printScaleNF = NumberFormat.getInstance(Locale.ENGLISH);
		printScaleNF.setGroupingUsed(false);
		printScaleNF.setMaximumFractionDigits(5);

		// algebra controller will take care of our key events
		setFocusable(true);
		addKeyListener(app.getAlgebraController());

		setLayout(null);
		setMinimumSize(new Dimension(20, 20));
		euclidianController.setView(this);

		attachView();

		// register Listener
		addMouseMotionListener(euclidianController);
		addMouseListener(euclidianController);
		addMouseWheelListener(euclidianController);
		addComponentListener(euclidianController);
			
		// no repaint
		initView(false);
		
		updateRightAngleStyle(app.getLocale());
	}
	
	public Application getApplication() {
		return app;
	}
	
	public void updateRightAngleStyle(Locale locale) {				
		// change rightAngleStyle for German to
        // EuclidianView.RIGHT_ANGLE_STYLE_DOT
        if (getRightAngleStyle() != RIGHT_ANGLE_STYLE_NONE) {
	        if (locale.getLanguage().equals("de")) {
	        	setRightAngleStyle(RIGHT_ANGLE_STYLE_DOT);
	        } else {
	        	setRightAngleStyle(RIGHT_ANGLE_STYLE_SQUARE);
	        }
        }
	}

	protected void initView(boolean repaint) {
		// preferred size
		setPreferredSize(null);
		
		// init grid's line type
		setGridLineStyle(LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(AXES_LINE_TYPE_ARROW);
		setAxesColor(Color.black); // Michael Borcherds 2008-01-26 was darkgray
		setGridColor(Color.lightGray);
		setBackground(Color.white);

		// showAxes = true;
		// showGrid = false;
		pointCapturingMode = POINT_CAPTURING_AUTOMATIC;
		pointStyle = POINT_STYLE_DOT;

		// added by Loïc BEGIN
		rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;
		// END
			
		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;
		axesTickStyles[0] = AXES_TICK_STYLE_MAJOR;
		axesTickStyles[1] = AXES_TICK_STYLE_MAJOR;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;

		// distances between grid lines
		automaticGridDistance = true;
		
		setCoordSystem(XZERO_STANDARD, YZERO_STANDARD, SCALE_STANDARD,
				SCALE_STANDARD, repaint);
	}
	
	public boolean hasPreferredSize() {
		Dimension prefSize = getPreferredSize();
		
		return prefSize != null &&
			prefSize.width > MIN_WIDTH &&
			prefSize.height > MIN_HEIGHT;
	}

	protected void resetLists() {
		DrawableMap.clear();
		allDrawableList.clear();
		drawPointList.clear();
		drawLineList.clear();
		drawSegmentList.clear();
		drawVectorList.clear();
		drawConicList.clear();
		drawFunctionList.clear();
		drawTextList.clear();		
		drawImageList.clear();
		drawLocusList.clear();
		drawPolygonList.clear();
		drawNumericList.clear();
		drawBooleanList.clear();
		drawListList.clear();

		bgImageList.clear();

		setToolTipText(null);
	}

	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	/*
	 * public void detachView() { kernel.detach(this); clearView();
	 * //kernel.notifyRemoveAll(this); }
	 */

	Kernel getKernel() {
		return kernel;
	}

	/**
	 * Returns point capturing mode.
	 */
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 */
	public void setPointCapturing(int mode) {
		pointCapturingMode = mode;
	}

	/**
	 * Sets the global style for point drawing.
	 */
	public void setPointStyle(int style) {
		switch (style) {
		case 1:
		case 2:
			pointStyle = style;
			break;

		default:
			pointStyle = POINT_STYLE_DOT;
		}
		updateAllDrawables(true);
	}

	final public int getPointStyle() {
		return pointStyle;
	}

	// added by Loïc BEGIN
	/**
	 * Sets the global style for rightAngle drawing.
	 */
	public void setRightAngleStyle(int style) {
		rightAngleStyle = style;
		updateAllDrawables(true);
	}

	final public int getRightAngleStyle() {
		return rightAngleStyle;
	}

	// END
	final void addBackgroundImage(DrawImage img) {
		bgImageList.addUnique(img);
		drawImageList.remove(img);
	}

	final void removeBackgroundImage(DrawImage img) {
		bgImageList.remove(img);
		drawImageList.add(img);
	}

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}

	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 */
	public static BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianView.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianView.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke
				.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	public void updateFonts() {
		fontSize = app.getFontSize();

		fontPoint = app.getPlainFont();
		fontAngle = fontPoint;
		fontLine = fontPoint;
		fontVector = fontPoint;
		fontConic = fontPoint;
		fontCoords = fontPoint.deriveFont(Font.PLAIN, fontSize - 2);
		fontAxes = fontCoords;
		updateDrawableFontSize();
	}

	public void setAntialiasing(boolean flag) {
		if (flag == antiAliasing)
			return;
		antiAliasing = flag;
		repaint();
	}

	public boolean getAntialiasing() {
		return antiAliasing;
	}

	void setDragCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public void setMoveCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	void setHitCursor() {
		if (defaultCursor == null)
			setCursor(Cursor.getDefaultCursor());
		else
			setCursor(defaultCursor);
	}

	void setDefaultCursor() {
		if (defaultCursor == null)
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else
			setCursor(defaultCursor);
	}

	protected void initCursor() {
		defaultCursor = null;

		switch (mode) {	
		case EuclidianView.MODE_ZOOM_IN:
			defaultCursor = getCursorForImage(app
					.getInternalImage("cursor_zoomin.gif"));
			break;

		case EuclidianView.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage(app
					.getInternalImage("cursor_zoomout.gif"));
			break;	
		}

		setDefaultCursor();
	}

	protected Cursor getCursorForImage(Image image) {
		if (image == null)
			return null;

		// Query for custom cursor support
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getBestCursorSize(32, 32);
		int colors = tk.getMaximumCursorColors();
		if (!d.equals(new Dimension(0, 0)) && (colors != 0)) {
			// load cursor image
			if (image != null) {
				try {
					// Create custom cursor from the image
					Cursor cursor = tk.createCustomCursor(image, new Point(16,
							16), "custom cursor");
					return cursor;
				} catch (Exception exc) {
					// Catch exceptions so that we don't try to set a null
					// cursor
					System.err.println("Unable to create custom cursor.");
				}
			}
		}
		return null;
	}

	public void setMode(int mode) {
		this.mode = mode;
		initCursor();
		euclidianController.setMode(mode);
		setSelectionRectangle(null);
	}

	public int getMode() {
		return mode;
	}

	/**
	 * clears all selections and highlighting
	 */
	public void resetMode() {
		setMode(mode);
	}

	void setPreview(Previewable p) {
		if (previewDrawable != null)
			previewDrawable.disposePreview();
		previewDrawable = p;
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return
	 */
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(xZero + xRW * xscale);
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return
	 */
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(yZero - yRW * yscale);
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return
	 */
	final public double toScreenCoordXd(double xRW) {
		return xZero + xRW * xscale;
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return
	 */
	final public double toScreenCoordYd(double yRW) {
		return yZero - yRW * yscale;
	}

	/**
	 * convert real world coordinate x to screen coordinate x. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param xRW
	 * @return
	 */
	final public int toClippedScreenCoordX(double xRW) {
		if (xRW > xmax)
			return width + 1;
		else if (xRW < xmin)
			return -1;
		else
			return toScreenCoordX(xRW);
	}

	/**
	 * convert real world coordinate y to screen coordinate y. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param yRW
	 * @return
	 */
	final public int toClippedScreenCoordY(double yRW) {
		if (yRW > ymax)
			return -1;
		else if (yRW < ymin)
			return height + 1;
		else
			return toScreenCoordY(yRW);
	}

	/**
	 * Converts real world coordinates to screen coordinates.
	 * 
	 * @param inOut:
	 *            input and output array with x and y coords
	 */
	final public void toScreenCoords(double[] inOut) {
		inOut[0] = xZero + inOut[0] * xscale;
		inOut[1] = yZero - inOut[1] * yscale;

		// java drawing crashes for huge coord values
		if (Math.abs(inOut[0]) > MAX_SCREEN_COORD_VAL
				|| Math.abs(inOut[0]) > MAX_SCREEN_COORD_VAL) {
			inOut[0] = Double.NaN;
			inOut[1] = Double.NaN;
		}
	}

	public static final double MAX_SCREEN_COORD_VAL = 1E6;

	/**
	 * Converts real world coordinates to screen coordinates. If a coord value
	 * is outside the screen it is clipped to a rectangle with border
	 * PIXEL_OFFSET around the screen.
	 * 
	 * @param inOut:
	 *            input and output array with x and y coords
	 * @return true iff resulting coords are on screen, note: Double.NaN is NOT
	 *         checked
	 */
	final public boolean toClippedScreenCoords(double[] inOut, int PIXEL_OFFSET) {
		inOut[0] = xZero + inOut[0] * xscale;
		inOut[1] = yZero - inOut[1] * yscale;

		boolean onScreen = true;

		// x-coord on screen?
		if (inOut[0] < 0) {
			inOut[0] = Math.max(inOut[0], -PIXEL_OFFSET);
			onScreen = false;
		} else if (inOut[0] > width) {
			inOut[0] = Math.min(inOut[0], width + PIXEL_OFFSET);
			onScreen = false;
		}

		// y-coord on screen?
		if (inOut[1] < 0) {
			inOut[1] = Math.max(inOut[1], -PIXEL_OFFSET);
			onScreen = false;
		} else if (inOut[1] > height) {
			inOut[1] = Math.min(inOut[1], height + PIXEL_OFFSET);
			onScreen = false;
		}

		return onScreen;
	}

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 * @return
	 */
	final public double toRealWorldCoordX(double x) {
		return (x - xZero) * invXscale;
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 * @return
	 */
	final public double toRealWorldCoordY(double y) {
		return (yZero - y) * invYscale;
	}

	/**
	 * Sets real world coord system, where zero point has screen coords (xZero,
	 * yZero) and one unit is xscale pixels wide on the x-Axis and yscale pixels
	 * heigh on the y-Axis.
	 */
	final public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		setCoordSystem(xZero, yZero, xscale, yscale, true);
	}

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	final public void setRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		double calcXscale = width / (xmax - xmin);
		double calcYscale = height / (ymax - ymin);
		double calcXzero = -calcXscale * xmin;
		double calcYzero = calcYscale * ymax;

		setCoordSystem(calcXzero, calcYzero, calcXscale, calcYscale);
	}

	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (xscale < Kernel.MIN_PRECISION || xscale > 1E8)
			return;
		if (yscale < Kernel.MIN_PRECISION || yscale > 1E8)
			return;

		this.xZero = xZero;
		this.yZero = yZero;
		this.xscale = xscale;
		this.yscale = yscale;
		scaleRatio = yscale / xscale;
		invXscale = 1.0d / xscale;
		invYscale = 1.0d / yscale;

		// real world values
		setRealWorldBounds();

		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			updateBackgroundImage();
			updateAllDrawables(repaint);
			//app.updateStatusLabelAxesRatio();
		}
	}

	public void updateSize() {
		width = getWidth();
		height = getHeight();
		if (width <= 0 || height <= 0)
			return;
		
		// real world values
		setRealWorldBounds();

		GraphicsConfiguration gc = getGraphicsConfiguration();
		if (gc != null) {
			bgImage = gc.createCompatibleImage(width, height);
			bgGraphics = bgImage.createGraphics();
			if (antiAliasing) {
				setAntialiasing(bgGraphics);
			}
		}

		updateBackgroundImage();
		updateAllDrawables(true);
	}

	// move view:
	/*
	 * protected void setDrawMode(int mode) { if (mode != drawMode) { drawMode =
	 * mode; if (mode == DRAW_MODE_BACKGROUND_IMAGE) updateBackgroundImage(); } }
	 */

	final protected void setRealWorldBounds() {
		xmin = -xZero * invXscale;
		xmax = (width - xZero) * invXscale;
		ymax = yZero * invYscale;
		ymin = (yZero - height) * invYscale;

		// tell kernel
		kernel.setEuclidianViewBounds(xmin, xmax, ymin, ymax, xscale, yscale);

		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		calcPrintingScale();
	}

	protected void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / xscale;
		int exp = (int) Math.round(Math.log(unitPerCM) / Math.log(10));
		printingScale = Math.pow(10, -exp);
	}

	// axis: 0 for x-axis, 1 for y-axis
	protected void setAxesIntervals(double scale, int axis) {
		double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / scale;
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));

		if (automaticAxesNumberingDistances[axis]) {
			if (piAxisUnit[axis]) {
				axesNumberingDistances[axis] = Math.PI;
			} else {
				double pot = Math.pow(10, exp);
				double n = units * Math.pow(10, -exp);

				if (n > 5) {
					axesNumberingDistances[axis] = 5 * pot;
				} else if (n > 2) {
					axesNumberingDistances[axis] = 2 * pot;
				} else {
					axesNumberingDistances[axis] = pot;
				}
			}
		}
		AxesTickInterval[axis] = axesNumberingDistances[axis] / 2.0;
		axesNumberFormat[axis].setMaximumFractionDigits(Math.max(-exp, kernel
				.getPrintDecimals()));

		if (automaticGridDistance) {			
			gridDistances[axis] = axesNumberingDistances[axis] * automaticGridDistanceFactor;
		}
	}

	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	public double getYscale() {
		return yscale;
	}

	/**
	 * Returns the ratio yscale / xscale of this view. The scale is the number
	 * of pixels in screen space that represent one unit in user space.
	 */
	public double getScaleRatio() {
		return yscale / xscale;
	}

	protected String getXYscaleRatioString() {
		StringBuffer sb = new StringBuffer();
		sb.append("x : y = ");
		if (xscale >= yscale) {
			sb.append("1 : ");
			sb.append(printScaleNF.format(xscale / yscale));
		} else {
			sb.append(printScaleNF.format(yscale / xscale));
			sb.append(" : 1");
		}
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	public double getXZero() {
		return xZero;
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return yZero;
	}

	public void showAxes(boolean xAxis, boolean yAxis) {
		if (xAxis == showAxes[0] && yAxis == showAxes[1])
			return;

		showAxes[0] = xAxis;
		showAxes[1] = yAxis;
		updateBackgroundImage();
	}
	
	final boolean isGridOrAxesShown() {
		return showAxes[0] || showAxes[1] || showGrid;
	}	

	public boolean getShowXaxis() {
		return showAxes[0];
	}

	public boolean getShowYaxis() {
		return showAxes[1];
	}

	public void showGrid(boolean show) {
		if (show == showGrid)
			return;
		showGrid = show;
		updateBackgroundImage();
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	final public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHints(defRenderingHints);
		// g2.setClip(0, 0, width, height);

		// BACKGROUND
		// draw background image (with axes and/or grid)
		if (bgImage == null)
			updateSize();
		else
			g2.drawImage(bgImage, 0, 0, null);

		/*
		 * switch (drawMode) { case DRAW_MODE_BACKGROUND_IMAGE: // draw
		 * background image (with axes and/or grid) if (bgImage == null)
		 * updateSize(); else g2.drawImage(bgImage, 0,0, null); break;
		 * 
		 * default: // DRAW_MODE_DIRECT_DRAW: drawBackground(g2, true); }
		 */

		// FOREGROUND
		if (antiAliasing)
			setAntialiasing(g2);		
		
		// draw equations, checkboxes and all geo objects
		drawObjects(g2);			

		if (selectionRectangle != null) {
			drawZoomRectangle(g2);
		}

		if (showMouseCoords && (showAxes[0] || showAxes[1] || showGrid))
			drawMouseCoords(g2);
		if (showAxesRatio)
			drawAxesRatio(g2);
	}

	protected void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	protected void drawZoomRectangle(Graphics2D g2) {
		g2.setColor(colZoomRectangleFill);
		g2.fill(selectionRectangle);
		g2.setColor(colZoomRectangle);
		g2.draw(selectionRectangle);
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {		
		if (pageIndex > 0)
			return (NO_SUCH_PAGE);
		else {
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldTransform = g2d.getTransform();

			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());

			// construction title
			int y = 0;
			Construction cons = kernel.getConstruction();
			String title = cons.getTitle();
			if (!title.equals("")) {
				Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
						app.getBoldFont().getSize() + 2);
				g2d.setFont(titleFont);
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getAscent();
				g2d.drawString(title, 0, y);
			}

			// construction author and date
			String author = cons.getAuthor();
			String date = cons.getDate();
			String line = null;
			if (!author.equals("")) {
				line = author;
			}
			if (!date.equals("")) {
				if (line == null)
					line = date;
				else
					line = line + " - " + date;
			}

			// scale string:
			// Scale in cm: 1:1 (x), 1:2 (y)
			String scaleString = null;
			if (app.isPrintScaleString()) {
				StringBuffer sb = new StringBuffer(app
						.getPlain("ScaleInCentimeter"));
				if (printingScale <= 1) {
					sb.append(": 1:");
					sb.append(printScaleNF.format(1 / printingScale));
				} else {
					sb.append(": ");
					sb.append(printScaleNF.format(printingScale));
					sb.append(":1");
				}
	
				// add yAxis scale too?
				if (scaleRatio != 1.0) {
					sb.append(" (x), ");
					double yPrintScale = printingScale * yscale / xscale;
					if (yPrintScale < 1) {
						sb.append("1:");
						sb.append(printScaleNF.format(1 / yPrintScale));
					} else {
						sb.append(printScaleNF.format(yPrintScale));
						sb.append(":1");
					}
					sb.append(" (y)");
				}
				scaleString = sb.toString();
			}

			if (scaleString != null) {
				if (line == null)
					line = scaleString;
				else
					line = line + " - " + scaleString;
			}

			if (line != null) {
				g2d.setFont(app.getPlainFont());
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getHeight();
				g2d.drawString(line, 0, y);
			}
			if (y > 0) {
				g2d.translate(0, y + 20); // space between title and drawing
			}

			double scale = PRINTER_PIXEL_PER_CM / xscale * printingScale;
			exportPaint(g2d, scale);

			// clear page margins at bottom and right
			double pagewidth = pageFormat.getWidth();
			double pageheight = pageFormat.getHeight();
			double xmargin = pageFormat.getImageableX();
			double ymargin = pageFormat.getImageableY();

			g2d.setTransform(oldTransform);
			g2d.setClip(null);
			g2d.setPaint(Color.white);

			Rectangle2D.Double rect = new Rectangle2D.Double();
			rect.setFrame(0, pageheight - ymargin, pagewidth, ymargin);
			g2d.fill(rect);
			rect.setFrame(pagewidth - xmargin, 0, xmargin, pageheight);
			g2d.fill(rect);

			System.gc();
			return (PAGE_EXISTS);
		}
	}

	/**
	 * Scales construction and draws it to g2d.
	 * 
	 * @param forEPS:
	 *            states if export should be optimized for eps. Note: if this is
	 *            set to true, no traces are drawn.
	 * 
	 */
	public void exportPaint(Graphics2D g2d, double scale) {
		g2d.scale(scale, scale);	
		
		// clipping on selection rectangle
		if (selectionRectangle != null) {
			Rectangle rect = selectionRectangle;
			g2d.setClip(0,0, rect.width, rect.height);
			g2d.translate(-rect.x, -rect.y);					
		} else {
			// or take full euclidian view
			g2d.setClip(0, 0, width, height);	
		}											

		// DRAWING
		if (isTracing() || hasBackgroundImages()) {
			// draw background image to get the traces
			g2d.drawImage(bgImage, 0, 0, this);
		} else {
			drawBackground(g2d, true);
		}

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		setAntialiasing(g2d);
		drawObjects(g2d);
	}		

	/**
	 * Tells if there are any traces in the background image.
	 */
	protected boolean isTracing() {
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			if (it.next().isTracing)
				return true;
		}
		return false;
	}

	/**
	 * Tells if there are any images in the background.
	 */
	protected boolean hasBackgroundImages() {
		return bgImageList.size() > 0;
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 */
	public BufferedImage getExportImage(double scale) throws OutOfMemoryError {
		int width = (int) Math.floor(getSelectedWidth() * scale);
		int height = (int) Math.floor(getSelectedHeight() * scale);		
		BufferedImage img = createBufferedImage(width, height);
		exportPaint(img.createGraphics(), scale);
		img.flush();
		return img;
	}

	protected BufferedImage createBufferedImage(int width, int height)
			throws OutOfMemoryError {
		// this image might be too big for our memory
		BufferedImage img = null;
		try {
			System.gc();
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		} catch (OutOfMemoryError e) {
			System.err.println(e.getMessage() + ": BufferedImage.TYPE_INT_RGB");
			try {
				System.gc();
				img = new BufferedImage(width, height,
						BufferedImage.TYPE_3BYTE_BGR);
			} catch (OutOfMemoryError e2) {
				System.err.println(e2.getMessage()
						+ ": BufferedImage.TYPE_3BYTE_BGR");
				System.gc();
				img = new BufferedImage(width, height,
						BufferedImage.TYPE_BYTE_INDEXED);
			}
		}
		return img;
	}

	final public Graphics2D getBackgroundGraphics() {
		return bgGraphics;
	}

	final public void updateBackground() {
		updateBackgroundImage();
		updateAllDrawables(true);
		// repaint();
	}

	final protected void updateBackgroundImage() {
		if (bgGraphics != null) {
			clearBackground(bgGraphics);
			bgImageList.drawAll(bgGraphics);

			drawBackground(bgGraphics, false);
		}
	}

	final protected void drawBackground(Graphics2D g, boolean clear) {
		if (clear) {
			clearBackground(g);
		}

		setAntialiasing(g);
		if (showGrid)
			drawGrid(g);
		if (showAxes[0] || showAxes[1])
			drawAxes(g);

		if (app.showResetIcon()) {
			g.drawImage(resetImage, width - 18, 2, null);
		}
	}		

	final protected void clearBackground(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, width, height);
	}

	protected static int SCREEN_BORDER = 10;

	final void drawAxes(Graphics2D g2) {
		// for axes ticks
		double yZeroTick = yZero;
		double xZeroTick = xZero;
		double yBig = yZero + 4;
		double xBig = xZero - 4;
		double ySmall1 = yZero + 2;
		double ySmall2 = yZero + 3;
		double xSmall1 = xZero - 2;
		double xSmall2 = xZero - 3;
		int xoffset, yoffset;
		boolean bold = axesLineType == AXES_LINE_TYPE_FULL_BOLD
						|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD;
		boolean drawArrows = axesLineType == AXES_LINE_TYPE_ARROW
								|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD;

		// AXES_TICK_STYLE_MAJOR_MINOR = 0;
		// AXES_TICK_STYLE_MAJOR = 1;
		// AXES_TICK_STYLE_NONE = 2;
		boolean[] drawMajorTicks = { axesTickStyles[0] <= 1,
				axesTickStyles[1] <= 1 };
		boolean[] drawMinorTicks = { axesTickStyles[0] == 0,
				axesTickStyles[1] == 0 };

		FontRenderContext frc = g2.getFontRenderContext();
		g2.setFont(fontAxes);
		int fontsize = fontAxes.getSize();
		int arrowSize = fontsize / 3;
		g2.setPaint(axesColor);

		if (bold) {
			axesStroke = boldAxesStroke;
			tickStroke = boldAxesStroke;
			ySmall2++;
			xSmall2--;
			arrowSize += 1;
		} else {
			axesStroke = defAxesStroke;
			tickStroke = defAxesStroke;
		}

		// turn antialiasing off
//		Object antiAliasValue = g2
//				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);	
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_OFF);

		// X - AXIS
		if (showAxes[0]) {
			if (showGrid) {
				yoffset = fontsize + 4;
				xoffset = 10;
			} else {
				yoffset = fontsize + 4;
				xoffset = 1;
			}

			// label of x axis
			if (axesLabels[0] != null) {
				TextLayout layout = new TextLayout(axesLabels[0], fontLine, frc);
				g2.drawString(axesLabels[0], (int) (width - 10 - layout
						.getAdvance()), (int) (yZero - 4));
			}

			// numbers
			double rw = xmin - (xmin % axesNumberingDistances[0]);
			double pix = xZero + rw * xscale;
			double axesStep = xscale * axesNumberingDistances[0]; // pixelstep
			double smallTickPix;
			double tickStep = axesStep / 2;
			if (pix < SCREEN_BORDER) {
				// big tick
				if (drawMajorTicks[0]) {
					g2.setStroke(tickStroke);					
					tempLine.setLine(pix, yZeroTick, pix, yBig);
					g2.draw(tempLine);
				}
				pix += axesStep;
				rw += axesNumberingDistances[0];
			}
			int maxX = width - SCREEN_BORDER;
			for (; pix < width; rw += axesNumberingDistances[0], pix += axesStep) {
				if (pix <= maxX) {
					if (showAxesNumbers[0]) {
						String strNum = kernel.formatPiE(rw,
								axesNumberFormat[0]);
						boolean zero = strNum.equals("0");

						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[0] != null && !piAxisUnit[0])
							sb.append(axesUnitLabels[0]);

						TextLayout layout = new TextLayout(sb.toString(),
								fontAxes, frc);
						int x, y = (int) (yZero + yoffset);
						if (zero && showAxes[1]) {
							x = (int) (pix + 6);
						} else {
							x = (int) (pix + xoffset - layout.getAdvance() / 2);
						}
						g2.drawString(sb.toString(), x, y);
					}

					// big tick
					if (drawMajorTicks[0]) {
						g2.setStroke(tickStroke);
						tempLine.setLine(pix, yZeroTick, pix, yBig);
						g2.draw(tempLine);
					}
				} else if (drawMajorTicks[0] && !drawArrows) {
					// draw last tick if there is no arrow
					tempLine.setLine(pix, yZeroTick, pix, yBig);
					g2.draw(tempLine);
				}

				// small tick
				smallTickPix = pix - tickStep;
				if (drawMinorTicks[0]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(smallTickPix, ySmall1, smallTickPix,
							ySmall2);
					g2.draw(tempLine);
				}
			}
			// last small tick
			smallTickPix = pix - tickStep;
			if (drawMinorTicks[0] && (!drawArrows || smallTickPix <= maxX)) {
				g2.setStroke(tickStroke);
				tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
				g2.draw(tempLine);
			}

			// x-Axis
			g2.setStroke(axesStroke);
			tempLine.setLine(0, yZero, width, yZero);
			g2.draw(tempLine);

			if (drawArrows) {
				// tur antialiasing on
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						antiAliasValue);

				// draw arrow for x-axis
				tempLine.setLine(width - 1, yZero, width - 1 - arrowSize, yZero
						- arrowSize);
				g2.draw(tempLine);
				tempLine.setLine(width - 1, yZero, width - 1 - arrowSize, yZero
						+ arrowSize);
				g2.draw(tempLine);

				//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				//		RenderingHints.VALUE_ANTIALIAS_OFF);
			}
		}

		// Y-AXIS
		if (showAxes[1]) {
			if (showGrid) {
				xoffset = -2 - fontsize / 4;
				yoffset = -2;
			} else {
				xoffset = -4 - fontsize / 4;
				yoffset = fontsize / 2 - 1;
			}

			// label of y axis
			if (axesLabels[1] != null) {
				TextLayout layout = new TextLayout(axesLabels[1], fontLine, frc);
				g2.drawString(axesLabels[1], (int) (xZero + 5),
						(int) (5 + layout.getAscent()));
			}

			// numbers
			double rw = ymax - (ymax % axesNumberingDistances[1]);
			double pix = yZero - rw * yscale;
			double axesStep = yscale * axesNumberingDistances[1]; // pixelstep
			double tickStep = axesStep / 2;

			// first small tick
			double smallTickPix = pix - tickStep;
			if (drawMinorTicks[1]
					&& (!drawArrows || smallTickPix > SCREEN_BORDER)) {
				g2.setStroke(tickStroke);
				tempLine.setLine(xSmall1, smallTickPix, xSmall2, smallTickPix);
				g2.draw(tempLine);
			}

			// don't get too near to the top of the screen
			if (pix < SCREEN_BORDER) {
				if (drawMajorTicks[1] && !drawArrows) {
					// draw tick if there is no arrow
					g2.setStroke(tickStroke);
					tempLine.setLine(xBig, pix, xZeroTick, pix);
					g2.draw(tempLine);
				}
				smallTickPix = pix + tickStep;
				if (drawMinorTicks[1] && smallTickPix > SCREEN_BORDER) {
					g2.setStroke(tickStroke);
					tempLine.setLine(xSmall1, smallTickPix, xSmall2,
							smallTickPix);
					g2.draw(tempLine);
				}
				pix += axesStep;
				rw -= axesNumberingDistances[1];
			}
			int maxY = height - SCREEN_BORDER;
			for (; pix <= height; rw -= axesNumberingDistances[1], pix += axesStep) {
				if (pix <= maxY) {
					if (showAxesNumbers[1]) {
						String strNum = kernel.formatPiE(rw,
								axesNumberFormat[1]);
						boolean zero = strNum.equals("0");

						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[1] != null && !piAxisUnit[1])
							sb.append(axesUnitLabels[1]);

						TextLayout layout = new TextLayout(sb.toString(),
								fontAxes, frc);
						int x = (int) (xZero + xoffset - layout.getAdvance());
						int y;
						if (zero && showAxes[0]) {
							y = (int) (yZero - 2);
						} else {
							y = (int) (pix + yoffset);
						}
						g2.drawString(sb.toString(), x, y);
					}
				}

				// big tick
				if (drawMajorTicks[1]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(xBig, pix, xZeroTick, pix);
					g2.draw(tempLine);
				}

				smallTickPix = pix + tickStep;
				if (drawMinorTicks[1]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(xSmall1, smallTickPix, xSmall2,
							smallTickPix);
					g2.draw(tempLine);
				}
			}

			// y-Axis
			tempLine.setLine(xZero, 0, xZero, height);
			g2.draw(tempLine);

			if (drawArrows) {
				// draw arrow for y-axis
				tempLine.setLine(xZero, 0, xZero - arrowSize, arrowSize);
				g2.draw(tempLine);
				tempLine.setLine(xZero, 0, xZero + arrowSize, arrowSize);
				g2.draw(tempLine);
			}								
		}
		
	
		// if one of the axes is not visible, show upper left and lower right corner coords
		if (xmin > 0 || xmax < 0 || ymin > 0 || ymax < 0) {
			// uper left corner								
			sb.setLength(0);
			sb.append('(');
			sb.append(axesNumberFormat[0].format(xmin));
			sb.append(", ");
			sb.append(axesNumberFormat[1].format(ymax));
			sb.append(')');
			
			int textHeight = 2 + fontAxes.getSize();
			g2.setFont(fontAxes);			
			g2.drawString(sb.toString(), 5, textHeight);
			
			// lower right corner
			sb.setLength(0);
			sb.append('(');
			sb.append(axesNumberFormat[0].format(xmax));
			sb.append(", ");
			sb.append(axesNumberFormat[1].format(ymin));
			sb.append(')');
			
			TextLayout layout = new TextLayout(sb.toString(), fontAxes, frc);	
			layout.draw(g2, (int) (width - 5 - layout.getAdvance()), 
									height - 5);					
		}	
	}

	final void drawGrid(Graphics2D g2) {
		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		// vertical grid lines
		double tickStep = xscale * gridDistances[0];
		double start = xZero % tickStep;
		double pix = start;		
		for (int i=0; pix <= width; i++) {			
			//int val = (int) Math.round(i);
			//g2.drawLine(val, 0, val, height);
			tempLine.setLine(pix, 0, pix, height);
			g2.draw(tempLine);
			pix = start + i * tickStep;
		}

		// horizontal grid lines
		tickStep = yscale * gridDistances[1];
		start = yZero % tickStep;
		pix = start;
		for (int j=0; pix <= height; j++) {
			//int val = (int) Math.round(j);
			//g2.drawLine(0, val, width, val);
			tempLine.setLine(0, pix, width, pix);
			g2.draw(tempLine);
			pix = start + j * tickStep;			
		}						
	}

	final protected void drawMouseCoords(Graphics2D g2) {
		Point pos = euclidianController.mouseLoc;
		if (pos == null)
			return;

		sb.setLength(0);
		sb.append('(');
		sb.append(kernel.format(euclidianController.xRW));
		if (kernel.getCoordStyle() == Kernel.COORD_STYLE_AUSTRIAN)
			sb.append(" | ");
		else
			sb.append(", ");
		sb.append(kernel.format(euclidianController.yRW));
		sb.append(')');

		g2.setColor(Color.darkGray);
		g2.setFont(fontCoords);
		g2.drawString(sb.toString(), pos.x + 15, pos.y + 15);
	}
	
	final protected void drawAxesRatio(Graphics2D g2) {
		Point pos = euclidianController.mouseLoc;
		if (pos == null)
			return;						

		g2.setColor(Color.darkGray);
		g2.setFont(fontLine);
		g2.drawString(getXYscaleRatioString(), pos.x + 15, pos.y + 30);
	}
	
	protected void drawObjects(Graphics2D g2) {		
		// draw images
		drawImageList.drawAll(g2);
		
		// draw HotEquations
		paintChildren(g2);
		
		// draw Geometric objects
		drawGeometricObjects(g2);
	}

	/**
	 * Draws all GeoElements except images.
	 */
	protected void drawGeometricObjects(Graphics2D g2) {	

		if (previewDrawable != null) {
			previewDrawable.drawPreview(g2);
		}		
		
		// draw lists of objects
		drawListList.drawAll(g2);

		// draw polygons
		drawPolygonList.drawAll(g2);

		// draw conics
		drawConicList.drawAll(g2);

		// draw angles and numbers
		drawNumericList.drawAll(g2);

		// draw functions
		drawFunctionList.drawAll(g2);

		// draw lines
		drawLineList.drawAll(g2);

		// draw segments
		drawSegmentList.drawAll(g2);

		// draw vectors
		drawVectorList.drawAll(g2);

		// draw locus
		drawLocusList.drawAll(g2);

		// draw points
		drawPointList.drawAll(g2);

		// draw text
		drawTextList.drawAll(g2);
		
		// boolean are not drawn as they are JToggleButtons and children of the view
	}

	// for use in AlgebraController
	final public void mouseMovedOver(GeoElement geo) {
		ArrayList geos = null;
		if (geo != null) {
			tempArrayList.clear();
			tempArrayList.add(geo);
			geos = tempArrayList;
		}
		boolean repaintNeeded = euclidianController.refreshHighlighting(geos);
		if (repaintNeeded)
			kernel.notifyRepaint();
	}

	protected ArrayList tempArrayList = new ArrayList();

	// for use in AlgebraController
	final public void clickedGeo(GeoElement geo, MouseEvent e) {
		if (geo == null)
			return;

		tempArrayList.clear();
		tempArrayList.add(geo);
		boolean changedKernel = euclidianController.processMode(tempArrayList,
				e);
		if (changedKernel)
			app.storeUndoInfo();
		kernel.notifyRepaint();
	}

	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	final public GeoElement getLabelHit(Point p) {
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible())
					return geo;
			}
		}
		return null;
	}
	
	/**
	 * Returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 */
	final public ArrayList getHits(Point p) {
		return getHits(p, false);
	}
	
	/**
	 * Returns hits that are suitable for new point mode.
	 * A polygon is only kept if one of its sides is also in
	 * hits.
	 */
	final public ArrayList getHitsForNewPointMode(ArrayList hits) {	
		if (hits == null) return null;
		
		Iterator it = hits.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (geo.isGeoPolygon()) {
				boolean sidePresent = false;
				GeoSegment [] sides = ((GeoPolygon) geo).getSegments();
				for (int k=0; k < sides.length; k++) {
					if (hits.contains(sides[k])) {
						sidePresent = true;
						break;
					}
				}
				
				if (!sidePresent)
					it.remove();					
			}				
		}				
		
		return hits;
	}

	/**
	 * returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 */
	final public ArrayList getHits(Point p, boolean includePolygons) {
		foundHits.clear();

		// count lists, images and Polygons
		int listCount = 0;
		int polyCount = 0;
		int imageCount = 0;

		// get anything but a polygon
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();

				if (geo.isEuclidianVisible()) {
					if (geo.isGeoList()) {
						listCount++;
					} else if (geo.isGeoImage()) {
						imageCount++;
					} else if (geo.isGeoPolygon()) {
						polyCount++;
					}
					foundHits.add(geo);
				}
			}
		}

		// look for axis
		if (imageCount == 0) {
			if (showAxes[0] && Math.abs(yZero - p.y) < 3) {
				foundHits.add(kernel.getXAxis());
			}
			if (showAxes[1] && Math.abs(xZero - p.x) < 3) {
				foundHits.add(kernel.getYAxis());
			}
		}

		int size = foundHits.size();
		if (size == 0)
			return null;

		// remove all lists, images and polygons if there are other objects too
		if (size - (listCount + imageCount + polyCount) > 0) {
			for (int i = 0; i < foundHits.size(); ++i) {
				GeoElement geo = (GeoElement) foundHits.get(i);
				if (geo.isGeoList() || geo.isGeoImage() || (!includePolygons && geo.isGeoPolygon()))
					foundHits.remove(i);
			}
		}

		return foundHits;
	}
	protected ArrayList foundHits = new ArrayList();
	
	/**
	 * Returns array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	final public ArrayList getHits(Rectangle rect) {
		foundHits.clear();		
		
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect)) {				
				foundHits.add(geo);
			}
		}
		return foundHits;
	}

	/**
	 * returns array of independent GeoElements whose visual representation is
	 * at streen coords (x,y). order: points, vectors, lines, conics
	 */
	final public ArrayList getMoveableHits(Point p) {
		return getMoveableHits(getHits(p));
	}

	/**
	 * returns array of changeable GeoElements out of hits
	 */
	final public ArrayList getMoveableHits(ArrayList hits) {
		return getMoveables(hits, TEST_MOVEABLE, null);
	}

	/**
	 * returns array of changeable GeoElements out of hits that implement
	 * PointRotateable
	 */
	final public ArrayList getPointRotateableHits(ArrayList hits,
			GeoPoint rotCenter) {
		return getMoveables(hits, TEST_ROTATEMOVEABLE, rotCenter);
	}

	protected final int TEST_MOVEABLE = 1;

	protected final int TEST_ROTATEMOVEABLE = 2;

	protected ArrayList getMoveables(ArrayList hits, int test, GeoPoint rotCenter) {
		if (hits == null)
			return null;

		GeoElement geo;
		moveableList.clear();
		for (int i = 0; i < hits.size(); ++i) {
			geo = (GeoElement) hits.get(i);
			switch (test) {
			case TEST_MOVEABLE:
				if (geo.isMoveable() || geo.hasOnlyMoveableInputPoints())
					moveableList.add(geo);
				break;

			case TEST_ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if (rotCenter == null || !geo.isParentOf(rotCenter))
						moveableList.add(geo);
				}

				break;
			}
		}
		if (moveableList.size() == 0)
			return null;
		else
			return moveableList;
	}

	protected ArrayList moveableList = new ArrayList();

	/**
	 * returns array of GeoElements of type geoclass whose visual representation
	 * is at streen coords (x,y). order: points, vectors, lines, conics
	 */
	final public ArrayList getHits(Point p, Class geoclass, ArrayList result) {
		return getHits(getHits(p), geoclass, false, result);
	}

	/**
	 * returns array of GeoElements NOT of type geoclass out of hits
	 */
	final public ArrayList getOtherHits(ArrayList hits, Class geoclass,
			ArrayList result) {
		return getHits(hits, geoclass, true, result);
	}

	final public ArrayList getHits(ArrayList hits, Class geoclass,
			ArrayList result) {
		return getHits(hits, geoclass, false, result);
	}

	/**
	 * Returns array of polygons with n points out of hits.
	 * 
	 * @return
	 *
	final public ArrayList getPolygons(ArrayList hits, int n, ArrayList polygons) {
		// search for polygons in hits that exactly have the needed number of
		// points
		polygons.clear();
		getHits(hits, GeoPolygon.class, polygons);
		for (int k = polygons.size() - 1; k > 0; k--) {
			GeoPolygon poly = (GeoPolygon) polygons.get(k);
			// remove poly with wrong number of points
			if (n != poly.getPoints().length)
				polygons.remove(k);
		}
		return polygons;
	}*/

	/**
	 * Stores all GeoElements of type geoclass to result list.
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT of type geoclass out of
	 *            hits.
	 */
	final protected ArrayList getHits(ArrayList hits, Class geoclass,
			boolean other, ArrayList result) {
		if (hits == null)
			return null;

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			boolean success = geoclass.isInstance(hits.get(i));
			if (other)
				success = !success;
			if (success)
				result.add(hits.get(i));
		}
		return result.size() == 0 ? null : result;
	}

	/**
	 * returns array of GeoElements whose visual representation is on top of
	 * screen coords of Point p. If there are points at location p only the
	 * points are returned. Otherwise all GeoElements are returned.
	 * 
	 * @see EuclidianController: mousePressed(), mouseMoved()
	 */
	final public ArrayList getTopHits(Point p) {
		return getTopHits(getHits(p));
	}

	/**
	 * if there are GeoPoints in hits, all these points are returned. Otherwise
	 * hits is returned.
	 * 
	 * @see EuclidianController: mousePressed(), mouseMoved()
	 */
	final public ArrayList getTopHits(ArrayList hits) {
		if (hits == null)
			return null;

		// point in there?
		if (containsGeoPoint(hits)) {
			getHits(hits, GeoPoint.class, false, topHitsList);
			return topHitsList;
		} else
			return hits;
	}

	protected ArrayList topHitsList = new ArrayList();

	final public boolean containsGeoPoint(ArrayList hits) {
		if (hits == null)
			return false;

		for (int i = 0; i < hits.size(); i++) {
			if (((GeoElement) hits.get(i)).isGeoPoint())
				return true;
		}
		return false;
	}

	/**
	 * Returns the drawable for the given GeoElement.
	 */
	final Drawable getDrawable(GeoElement geo) {
		return (Drawable) DrawableMap.get(geo);
	}

	/*
	 * interface View implementation
	 */

	/**
	 * adds a GeoElement to this view
	 */
	public void add(GeoElement geo) {
		// check if there is already a drawable for geo
		Drawable d = getDrawable(geo);
		if (d != null)
			return;

		d = createDrawable(geo);
		if (d != null) {
			addToDrawableLists(d);
			repaint();			
		}
	}

	/**
	 * adds a GeoElement to this view
	 */
	protected Drawable createDrawable(GeoElement geo) {
		Drawable d = null;

		switch (geo.getGeoClassType()) {
		case GeoElement.GEO_CLASS_BOOLEAN:
			d = new DrawBoolean(this, (GeoBoolean) geo);			
			break;
		
		case GeoElement.GEO_CLASS_POINT:
			d = new DrawPoint(this, (GeoPoint) geo);
			break;					

		case GeoElement.GEO_CLASS_SEGMENT:
			d = new DrawSegment(this, (GeoSegment) geo);
			break;

		case GeoElement.GEO_CLASS_RAY:
			d = new DrawRay(this, (GeoRay) geo);
			break;

		case GeoElement.GEO_CLASS_LINE:
			d = new DrawLine(this, (GeoLine) geo);
			break;

		case GeoElement.GEO_CLASS_POLYGON:
			d = new DrawPolygon(this, (GeoPolygon) geo);
			break;

		case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else {
				d = new DrawAngle(this, (GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						Color col = geo.getConstruction()
								.getConstructionDefaults().getDefaultGeo(
										ConstructionDefaults.DEFAULT_ANGLE)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;

		case GeoElement.GEO_CLASS_NUMERIC:
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo == null) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoSlope) {
				d = new DrawSlope(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralDefinite) {
				d = new DrawIntegral(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralFunctions) {
				d = new DrawIntegralFunctions(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoSumUpperLower) {
				d = new DrawUpperLowerSum(this, (GeoNumeric) geo);
			}
			if (d != null) {
				if (!geo.isColorSet()) {
					ConstructionDefaults consDef = geo.getConstruction()
							.getConstructionDefaults();
					if (geo.isIndependent()) {
						Color col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_NUMBER).getObjectColor();
						geo.setObjColor(col);
					} else {
						Color col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_POLYGON)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}			
			}
			break;

		case GeoElement.GEO_CLASS_VECTOR:
			d = new DrawVector(this, (GeoVector) geo);
			break;

		case GeoElement.GEO_CLASS_CONICPART:
			d = new DrawConicPart(this, (GeoConicPart) geo);
			break;

		case GeoElement.GEO_CLASS_CONIC:
			d = new DrawConic(this, (GeoConic) geo);
			break;

		case GeoElement.GEO_CLASS_FUNCTION:
		case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
			d = new DrawParametricCurve(this, (ParametricCurve) geo);
			break;

		case GeoElement.GEO_CLASS_TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(this, text);
			// we may need to propagate the update of the bounding box of the text
			if (text.isNeedsUpdatedBoundingBox()) {
				text.updateCascade();
			}			
			break;

		case GeoElement.GEO_CLASS_IMAGE:
			d = new DrawImage(this, (GeoImage) geo);
			break;

		case GeoElement.GEO_CLASS_LOCUS:
			d = new DrawLocus(this, (GeoLocus) geo);
			break;

		case GeoElement.GEO_CLASS_CURVE_CARTESIAN:
			d = new DrawParametricCurve(this, (GeoCurveCartesian) geo);
			break;

		case GeoElement.GEO_CLASS_LIST:
			d = new DrawList(this, (GeoList) geo);
			break;
		}
		
		if (d != null) {			
			DrawableMap.put(geo, d);
		}

		return d;
	}	
	
	/**
	 * adds a GeoElement to this view
	 */
	protected void addToDrawableLists(Drawable d) {
		if (d == null) return;
		
		GeoElement geo = d.getGeoElement();

		switch (geo.getGeoClassType()) {
		case GeoElement.GEO_CLASS_BOOLEAN:			
			drawBooleanList.add(d);
			break;
		
		case GeoElement.GEO_CLASS_POINT:
			drawPointList.add(d);
			break;					

		case GeoElement.GEO_CLASS_SEGMENT:
			drawSegmentList.add(d);
			break;

		case GeoElement.GEO_CLASS_RAY:
			drawSegmentList.add(d);
			break;

		case GeoElement.GEO_CLASS_LINE:
			drawLineList.add(d);
			break;

		case GeoElement.GEO_CLASS_POLYGON:
			drawPolygonList.add(d);					
			break;

		case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {				
				drawNumericList.add(d);
			} else {				
				if (geo.isDrawable()) {					
					drawNumericList.add(d);
				} 
				else 
					d = null;
			}
			break;

		case GeoElement.GEO_CLASS_NUMERIC:			
			drawNumericList.add(d);
			break;

		case GeoElement.GEO_CLASS_VECTOR:			
			drawVectorList.add(d);
			break;

		case GeoElement.GEO_CLASS_CONICPART:
			drawConicList.add(d);
			break;

		case GeoElement.GEO_CLASS_CONIC:
			drawConicList.add(d);
			break;

		case GeoElement.GEO_CLASS_FUNCTION:
		case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
			drawFunctionList.add(d);
			break;

		case GeoElement.GEO_CLASS_TEXT:
			drawTextList.add(d);
			break;

		case GeoElement.GEO_CLASS_IMAGE:
			if (!bgImageList.contains(d))
				drawImageList.add(d);
			break;

		case GeoElement.GEO_CLASS_LOCUS:
			drawLocusList.add(d);
			break;

		case GeoElement.GEO_CLASS_CURVE_CARTESIAN:
			drawFunctionList.add(d);
			break;

		case GeoElement.GEO_CLASS_LIST:
			drawListList.add(d);			
			break;
		}

		if (d != null) {
			allDrawableList.add(d);			
		}
	}
	

	/**
	 * removes a GeoElement from this view
	 */
	final public void remove(GeoElement geo) {
		Drawable d = (Drawable) DrawableMap.get(geo);

		if (d != null) {
			switch (geo.getGeoClassType()) {
			case GeoElement.GEO_CLASS_BOOLEAN:
				drawBooleanList.remove(d);
				// remove checkbox
				((DrawBoolean) d).remove();
				break;
			
			case GeoElement.GEO_CLASS_POINT:
				drawPointList.remove(d);
				break;

			case GeoElement.GEO_CLASS_SEGMENT:
			case GeoElement.GEO_CLASS_RAY:
				drawSegmentList.remove(d);
				break;

			case GeoElement.GEO_CLASS_LINE:
				drawLineList.remove(d);
				break;

			case GeoElement.GEO_CLASS_POLYGON:
				drawPolygonList.remove(d);
				break;

			case GeoElement.GEO_CLASS_ANGLE:
			case GeoElement.GEO_CLASS_NUMERIC:
				drawNumericList.remove(d);
				break;

			case GeoElement.GEO_CLASS_VECTOR:
				drawVectorList.remove(d);
				break;

			case GeoElement.GEO_CLASS_CONICPART:
				drawConicList.remove(d);
				break;

			case GeoElement.GEO_CLASS_CONIC:
				drawConicList.remove(d);
				break;

			case GeoElement.GEO_CLASS_FUNCTION:
			case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
				drawFunctionList.remove(d);
				break;

			case GeoElement.GEO_CLASS_TEXT:
				drawTextList.remove(d);
				// remove HotEqn
				((DrawText) d).remove();
				break;

			case GeoElement.GEO_CLASS_IMAGE:
				drawImageList.remove(d);
				break;

			case GeoElement.GEO_CLASS_LOCUS:
				drawLocusList.remove(d);
				break;

			case GeoElement.GEO_CLASS_CURVE_CARTESIAN:
				drawFunctionList.remove(d);
				break;

			case GeoElement.GEO_CLASS_LIST:
				drawListList.remove(d);
				break;
			}

			allDrawableList.remove(d);
			DrawableMap.remove(geo);
			repaint();
		}
	}		

	/**
	 * renames an element
	 */
	public void rename(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
			repaint();
		}
	}

	final public void update(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
		}
	}

	final public Drawable getDrawableFor(GeoElement geo) {
		return (Drawable) DrawableMap.get(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		// repaint();
	}

	final protected void updateAllDrawables(boolean repaint) {
		allDrawableList.updateAll();
		if (repaint)
			repaint();
	}

	final protected void updateDrawableFontSize() {
		allDrawableList.updateFontSizeAll();
		repaint();
	}

	public void reset() {
		resetMode();
		updateBackgroundImage();
	}

	public void clearView() {		
		removeAll(); // remove hotEqns
		resetLists();
		initView(false);
		updateBackgroundImage(); // clear traces and images
		// resetMode();
	}

	final public void repaintView() {
		repaint();
	}

	/**
	 * returns settings in XML format
	 */
	public String getXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<euclidianView>\n");
		
		if (width > MIN_WIDTH && height > MIN_HEIGHT) {
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
			sb.append("/>\n");
		}

		sb.append("\t<coordSystem");
		sb.append(" xZero=\"");
		sb.append(xZero);
		sb.append("\"");
		sb.append(" yZero=\"");
		sb.append(yZero);
		sb.append("\"");
		sb.append(" scale=\"");
		sb.append(xscale);
		sb.append("\"");
		sb.append(" yscale=\"");
		sb.append(yscale);
		sb.append("\"");
		sb.append("/>\n");

		// NOTE: the attribute "axes" for the visibility state of
		//  both axes is no longer needed since V3.0.
		//  Now there are special axis tags, see below.
		sb.append("\t<evSettings axes=\"");
		sb.append(showAxes[0] || showAxes[1]);
		sb.append("\" grid=\"");		
		sb.append(showGrid);
		sb.append("\" pointCapturing=\"");
		sb.append(pointCapturingMode);
		sb.append("\" pointStyle=\"");
		sb.append(pointStyle);
		sb.append("\" rightAngleStyle=\"");
		sb.append(rightAngleStyle);
		sb.append("\"/>\n");

		// background color
		sb.append("\t<bgColor r=\"");
		sb.append(bgColor.getRed());
		sb.append("\" g=\"");
		sb.append(bgColor.getGreen());
		sb.append("\" b=\"");
		sb.append(bgColor.getBlue());
		sb.append("\"/>\n");

		// axes color
		sb.append("\t<axesColor r=\"");
		sb.append(axesColor.getRed());
		sb.append("\" g=\"");
		sb.append(axesColor.getGreen());
		sb.append("\" b=\"");
		sb.append(axesColor.getBlue());
		sb.append("\"/>\n");

		// grid color
		sb.append("\t<gridColor r=\"");
		sb.append(gridColor.getRed());
		sb.append("\" g=\"");
		sb.append(gridColor.getGreen());
		sb.append("\" b=\"");
		sb.append(gridColor.getBlue());
		sb.append("\"/>\n");

		// axes line style
		sb.append("\t<lineStyle axes=\"");
		sb.append(axesLineType);
		sb.append("\" grid=\"");
		sb.append(gridLineStyle);
		sb.append("\"/>\n");

		// axis settings
		for (int i = 0; i < 2; i++) {
			sb.append("\t<axis id=\"");
			sb.append(i);
			sb.append("\" show=\"");
			sb.append(showAxes[i]);
			sb.append("\" label=\"");
			sb.append(axesLabels[i] == null ? "" : axesLabels[i]);
			sb.append("\" unitLabel=\"");
			sb.append(axesUnitLabels[i] == null ? "" : axesUnitLabels[i]);
			sb.append("\" tickStyle=\"");
			sb.append(axesTickStyles[i]);
			sb.append("\" showNumbers=\"");
			sb.append(showAxesNumbers[i]);

			// the tick distance should only be saved if
			// it isn't calculated automatically
			if (!automaticAxesNumberingDistances[i]) {
				sb.append("\" tickDistance=\"");
				sb.append(axesNumberingDistances[i]);
			}

			sb.append("\"/>\n");
		}

		// grid distances
		if (!automaticGridDistance || 
				// compatibility to v2.7:
			automaticGridDistanceFactor != DEFAULT_GRID_DIST_FACTOR) 
		{
			sb.append("\t<grid distX=\"");
			sb.append(gridDistances[0]);
			sb.append("\" distY=\"");
			sb.append(gridDistances[1]);
			sb.append("\"/>\n");
		}

		sb.append("</euclidianView>\n");
		return sb.toString();
	}

	/***************************************************************************
	 * ANIMATED ZOOMING
	 **************************************************************************/

	/**
	 * Zooms around fixed point (px, py)
	 */
	public final void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		if (zoomer == null)
			zoomer = new MyZoomer();
		zoomer.init(px, py, zoomFactor, steps, storeUndo);
		zoomer.startAnimation();
	}

	protected MyZoomer zoomer;

	/**
	 * Zooms towards the given axes scale ratio. Note: Only the y-axis is
	 * changed here. ratio = yscale / xscale;
	 */
	public final void zoomAxesRatio(double newRatio, boolean storeUndo) {
		if (axesRatioZoomer == null)
			axesRatioZoomer = new MyAxesRatioZoomer();
		axesRatioZoomer.init(newRatio, storeUndo);
		axesRatioZoomer.startAnimation();
	}

	protected MyAxesRatioZoomer axesRatioZoomer;

	public final void setStandardView(boolean storeUndo) {
		if (scaleRatio != 1.0) {
			// set axes ratio back to 1
			if (axesRatioZoomer == null)
				axesRatioZoomer = new MyAxesRatioZoomer();
			axesRatioZoomer.init(1, false);

			Thread waiter = new Thread() {
				public void run() {
					// wait until zoomer has finished
					axesRatioZoomer.startAnimation();
					while (axesRatioZoomer.isRunning()) {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					}
					// set the xscale and axes origin
					setAnimatedCoordSystem(XZERO_STANDARD, YZERO_STANDARD,
							SCALE_STANDARD, 15, false);
				}
			};
			waiter.start();
		} else {
			// set the xscale and axes origin
			setAnimatedCoordSystem(XZERO_STANDARD, YZERO_STANDARD,
					SCALE_STANDARD, 15, false);
		}
		if (storeUndo)
			app.storeUndoInfo();
	}

	/**
	 * Sets coord system of this view. Just like setCoordSystem but with
	 * previous animation.
	 * 
	 * @param ox:
	 *            x coord of new origin
	 * @param oy:
	 *            y coord of new origin
	 * @param newscale
	 */
	final void setAnimatedCoordSystem(double ox, double oy, double newScale,
			int steps, boolean storeUndo) {
		if (!kernel.isEqual(xscale, newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / xscale;
			zoom((ox - xZero * factor) / (1.0 - factor), (oy - yZero * factor)
					/ (1.0 - factor), factor, steps, storeUndo);
		} else {
			// same scales: translate view to standard origin
			// do this with the following action listener
			if (mover == null)
				mover = new MyMover();
			mover.init(ox, oy, storeUndo);
			mover.startAnimation();
		}
	}

	protected MyMover mover;

	protected class MyZoomer implements ActionListener {
		static final int MAX_STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		protected Timer timer; // for animation

		protected double px, py; // zoom point

		protected double factor;

		protected int counter, steps;

		protected double oldScale, newScale, add, dx, dy;

		protected long startTime;

		protected boolean storeUndo;

		public MyZoomer() {
			timer = new Timer(DELAY, this);
		}

		public void init(double px, double py, double zoomFactor, int steps,
				boolean storeUndo) {
			this.px = px;
			this.py = py;
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			oldScale = xscale;
			newScale = xscale * zoomFactor;
			this.steps = Math.min(MAX_STEPS, steps);
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / steps;
			dx = xZero - px;
			dy = yZero - py;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			factor = newScale / oldScale;
			setCoordSystem(px + dx * factor, py + dy * factor, newScale,
					newScale * scaleRatio);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == steps || time > MAX_TIME) { // end of animation
				stopAnimation();
			} else {
				factor = 1.0 + (counter * add) / oldScale;
				setCoordSystem(px + dx * factor, py + dy * factor, oldScale
						* factor, oldScale * factor * scaleRatio);
			}
		}
	}

	// changes the scale of the y-Axis continously to reach
	// the given scale ratio yscale / xscale
	protected class MyAxesRatioZoomer implements ActionListener {

		protected Timer timer; // for animation

		protected double factor;

		protected int counter;

		protected double oldScale, newScale, add;

		protected long startTime;

		protected boolean storeUndo;

		public MyAxesRatioZoomer() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ratio, boolean storeUndo) {
			// this.ratio = ratio;
			this.storeUndo = storeUndo;

			// zoomFactor = ratio / scaleRatio;
			oldScale = yscale;
			newScale = xscale * ratio; // new yscale
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(xZero, yZero, xscale, newScale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.MAX_STEPS || time > MyZoomer.MAX_TIME) { // end
				// of
				// animation
				stopAnimation();
			} else {
				factor = 1.0 + (counter * add) / oldScale;
				setCoordSystem(xZero, yZero, xscale, oldScale * factor);
			}
		}

		final synchronized boolean isRunning() {
			return timer.isRunning();
		}
	}

	// used for animated moving of euclidian view to standard origin
	protected class MyMover implements ActionListener {
		protected double dx, dy, add;

		protected int counter;

		protected double ox, oy; // new origin

		protected Timer timer;

		protected long startTime;

		protected boolean storeUndo;

		public MyMover() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ox, double oy, boolean storeUndo) {
			this.ox = ox;
			this.oy = oy;
			this.storeUndo = storeUndo;
		}

		public synchronized void startAnimation() {
			dx = xZero - ox;
			dy = yZero - oy;
			if (kernel.isZero(dx) && kernel.isZero(dy))
				return;

			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = 1.0 / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(ox, oy, xscale, yscale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.MAX_STEPS || time > MyZoomer.MAX_TIME) { // end
				// of
				// animation
				stopAnimation();
			} else {
				double factor = 1.0 - counter * add;
				setCoordSystem(ox + dx * factor, oy + dy * factor, xscale,
						yscale);
			}
		}
	}

	public final double getPrintingScale() {
		return printingScale;
	}

	public final void setPrintingScale(double printingScale) {
		this.printingScale = printingScale;
	}

	/**
	 * @return Returns the xmax.
	 */
	public double getXmax() {
		return xmax;
	}

	/**
	 * @return Returns the xmin.
	 */
	public double getXmin() {
		return xmin;
	}

	/**
	 * @return Returns the ymax.
	 */
	public double getYmax() {
		return ymax;
	}

	/**
	 * @return Returns the ymin.
	 */
	public double getYmin() {
		return ymin;
	}

	public Color getAxesColor() {
		return axesColor;
	}

	public void setAxesColor(Color axesColor) {
		if (axesColor != null)
			this.axesColor = axesColor;
	}

	public String[] getAxesLabels() {
		return axesLabels;
	}

	public void setAxesLabels(String[] axesLabels) {
		this.axesLabels = axesLabels;
		for (int i = 0; i < 2; i++) {
			if (axesLabels[i] != null && axesLabels[i].length() == 0) {
				axesLabels[i] = null;
			}
		}
	}

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		if (axis == 0)
			setAxesIntervals(xscale, 0);
		else
			setAxesIntervals(yscale, 1);
	}

	public boolean[] isAutomaticAxesNumberingDistance() {
		return automaticAxesNumberingDistances;
	}

	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	/**
	 * 
	 * @param x
	 * @param axis:
	 *            0 for xAxis, 1 for yAxis
	 */
	public void setAxesNumberingDistance(double dist, int axis) {
		axesNumberingDistances[axis] = dist;
		setAutomaticAxesNumberingDistance(false, axis);
	}

	public Color getBackground() {
		return bgColor;
	}

	public void setBackground(Color bgColor) {
		if (bgColor != null)
			this.bgColor = bgColor;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		if (gridColor != null)
			this.gridColor = gridColor;
	}

	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
	}

	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}

	public double[] getGridDistances() {
		return gridDistances;
	}

	public void setGridDistances(double[] dist) {
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}

	public int getGridLineStyle() {
		return gridLineStyle;
	}

	public void setGridLineStyle(int gridLineStyle) {
		this.gridLineStyle = gridLineStyle;
		gridStroke = getStroke(1f, gridLineStyle);
	}

	public int getAxesLineStyle() {
		return axesLineType;
	}

	public void setAxesLineStyle(int axesLineStyle) {
		this.axesLineType = axesLineStyle;
	}

	public boolean[] getShowAxesNumbers() {
		return showAxesNumbers;
	}

	public void setShowAxesNumbers(boolean[] showAxesNumbers) {
		this.showAxesNumbers = showAxesNumbers;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	public void setAxesUnitLabels(String[] axesUnitLabels) {
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 2; i++) {
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(PI_STRING);
		}
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}

	public void setAxesTickStyles(int[] axesTickStyles) {
		this.axesTickStyles = axesTickStyles;
	}

	public static String getModeText(int mode) {
		switch (mode) {
		case EuclidianView.MODE_ALGEBRA_INPUT:
			return "Select";

		case EuclidianView.MODE_MOVE:
			return "Move";

		case EuclidianView.MODE_POINT:
			return "Point";

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
			
		case MODE_AREA:
			return "Area";
			
		case MODE_SLOPE:
			return "Slope";
			
		case MODE_REGULAR_POLYGON:
			return "RegularPolygon";
			
		case MODE_SHOW_HIDE_CHECKBOX:
			return "ShowCheckBox";

		default:
			return "";
		}
	}
	

	public int getSelectedWidth() {
		if (selectionRectangle == null)
			return getWidth();
		else
			return selectionRectangle.width;
	}
	
	public int getSelectedHeight() {
		if (selectionRectangle == null)
			return getHeight();
		else
			return selectionRectangle.height;
	}		
	
	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	public void setSelectionRectangle(Rectangle selectionRectangle) {
		this.selectionRectangle = selectionRectangle;		
	}

	public EuclidianController getEuclidianController() {
		return euclidianController;
	}
	
	final public Graphics2D getTempGraphics2D() {
		return g2Dtemp;
	}
}
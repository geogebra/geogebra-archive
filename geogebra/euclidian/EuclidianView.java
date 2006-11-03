
/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
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
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
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
import geogebra.util.FastHashMapKeyless;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * 
 * @author Markus
 * @version
 */
public final class EuclidianView extends JPanel implements View, Printable,
		Transferable, ClipboardOwner {

	private static final long serialVersionUID = 1L;

	// pixel per centimeter (at 72dpi)
	private static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;

	public static final double MODE_ZOOM_FACTOR = 1.5;

	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;

	public static final double SCALE_STANDARD = 30;

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

	public static final int POINT_STYLE_DOT = 0;

	public static final int POINT_STYLE_CROSS = 1;

	public static final int POINT_STYLE_CIRCLE = 2;

	public static final int RIGHT_ANGLE_STYLE_DOT = 0;

	public static final int RIGHT_ANGLE_STYLE_NONE = 1;

	public static final int RIGHT_ANGLE_STYLE_SQUARE = 2;

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

	public static final int POINT_CAPTURING_OFF = 0;

	public static final int POINT_CAPTURING_ON = 1;

	public static final int POINT_CAPTURING_ON_GRID = 2;

	// DEFAULTS

	// points
	private static final Color colPoint = Color.blue;

	private static final Color colDepPoint = Color.darkGray;

	private static final Color colPathPoint = new Color(125, 125, 255);

	// lines
	private static final Color colDepLine = Color.black;

	private static final Color colLine = Color.black;

	// segments
	private static final Color colDepSegment = Color.black;

	private static final Color colSegment = Color.black;

	// vectors
	private static final Color colDepVector = Color.black;

	private static final Color colVector = Color.black;

	// conics
	private static final Color colDepConic = Color.black;

	private static final Color colConic = Color.black;

	// polygons
	private static final Color colDepPolygon = new Color(153, 51, 0);

	private static final Color colPolygon = colDepPolygon;

	public static final float DEFAULT_POLYGON_ALPHA = 0.1f;

	// angles
	private static final Color colDrawAngle = new Color(0, 100, 0);

	public static final float DEFAULT_ANGLE_ALPHA = 0.1f;

	// locus lines
	private static final Color colDepLocus = Color.darkGray;

	// numbers (slope, definite integral)
	private static final Color colNumber = Color.black;

	private static final Color colDepNumber = new Color(153, 51, 0);

	// preview colors
	public static final Color colPreview = Color.darkGray;

	public static final Color colPreviewFill = new Color(
			colDepPolygon.getRed(), colDepPolygon.getGreen(), colDepPolygon
					.getBlue(), (int) (DEFAULT_POLYGON_ALPHA * 255));

	// zoom rectangle colors
	private static final Color colZoomRectangle = new Color(200, 200, 250);

	private static final Color colZoomRectangleFill = new Color(200, 200, 250,
			70);

	// STROKES
	private static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	private static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + SELECTION_ADD);

	private static MyBasicStroke thinStroke = new MyBasicStroke(1.0f);

	// private static MyBasicStroke thickStroke = new MyBasicStroke(1.5f);

	// axes and grid stroke
	private BasicStroke axesStroke = thinStroke, gridStroke;

	private Line2D.Double tempLine = new Line2D.Double();

	private static RenderingHints defRenderingHints = new RenderingHints(null);
	{
		defRenderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		defRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		defRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
	}

	// FONTS
	public Font fontPoint, fontLine, fontVector, fontConic, fontCoords,
			fontAxes, fontAngle;

	int fontSize;

	// member variables
	private Application app;

	private Kernel kernel;

	private EuclidianController euclidianController;

	AffineTransform coordTransform = new AffineTransform();

	int width, height;

	private NumberFormat[] axesNumberFormat;

	private NumberFormat printScaleNF;

	double xmin, xmax, ymin, ymax, invXscale, invYscale, xZero, yZero, xscale,
			yscale, scaleRatio = 1.0; // ratio yscale / xscale

	private double[] AxesTickInterval = { 1, 1 }; // for axes =

	// axesNumberingDistances /
	// 2

	boolean showAxes = true;

	boolean showGrid = false;

	private boolean antiAliasing = true;

	boolean showMouseCoords = false;

	private int pointCapturingMode = POINT_CAPTURING_OFF; // round point to
	
	// added by Loïc BEGIN
	// right angle
	int rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_DOT;
	//END
	
	int pointStyle = POINT_STYLE_DOT;

	int mode = MODE_MOVE;

	private boolean[] showAxesNumbers = { true, true };

	private String[] axesLabels = { null, null };

	private String[] axesUnitLabels = { null, null };

	private boolean[] piAxisUnit = { false, false };

	// for axes labeling with numbers
	private boolean[] automaticAxesNumberingDistances = { true, true };

	private double[] axesNumberingDistances = { 2, 2 };

	// distances between grid lines
	private boolean automaticGridDistance = true;

	double[] gridDistances = { 2, 2 };

	private int gridLineType, axesLineType;

	// colors: axes, grid, background
	private Color axesColor, gridColor, bgColor;

	private double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	private FastHashMapKeyless DrawableMap = new FastHashMapKeyless(500);

	private DrawableList allDrawableList = new DrawableList();

	private DrawableList drawPointList = new DrawableList();

	private DrawableList drawLineList = new DrawableList();

	private DrawableList drawSegmentList = new DrawableList();

	private DrawableList drawVectorList = new DrawableList();

	private DrawableList drawConicList = new DrawableList();

	private DrawableList drawFunctionList = new DrawableList();

	private DrawableList drawTextList = new DrawableList();

	private DrawableList drawImageList = new DrawableList();

	private DrawableList drawLocusList = new DrawableList();

	private DrawableList drawPolygonList = new DrawableList();

	private DrawableList drawNumericList = new DrawableList();

	// on add: change resetLists()

	private DrawableList bgImageList = new DrawableList();

	Previewable previewDrawable;

	Rectangle zoomRectangle;

	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	// or use volatile image
	// private int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
	private BufferedImage bgImage;

	private Graphics2D bgGraphics; // g2d of bgImage

	private Image resetImage;

	private StringBuffer sb = new StringBuffer();

	private Cursor defaultCursor;

	/**
	 * Creates EuclidianView
	 */
	public EuclidianView(EuclidianController ec, boolean showAxes,
			boolean showGrid) {
		euclidianController = ec;
		kernel = ec.getKernel();
		app = ec.getApplication();
		resetImage = app.getInternalImage("geogebra22.gif");
		this.showAxes = showAxes;
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
	}

	private void initView(boolean repaint) {
		// init grid's line type
		setGridLineStyle(LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(AXES_LINE_TYPE_ARROW);
		setAxesColor(Color.darkGray);
		setGridColor(Color.lightGray);
		setBackground(Color.white);

		// showAxes = true;
		// showGrid = false;
		pointCapturingMode = POINT_CAPTURING_ON;
		pointStyle = POINT_STYLE_DOT;
		
		// added by Loïc BEGIN
		rightAngleStyle=EuclidianView.RIGHT_ANGLE_STYLE_DOT;
		//END

		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;

		// distances between grid lines
		automaticGridDistance = true;

		setCoordSystem(XZERO_STANDARD, YZERO_STANDARD, SCALE_STANDARD,
				SCALE_STANDARD, repaint);
	}

	private void resetLists() {
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
		rightAngleStyle=style;
		updateAllDrawables(true);
	}
	final public int getRightAngleStyle(){
		return rightAngleStyle;
	}
	//END
	final void addBackgroundImage(DrawImage img) {
		bgImageList.addUnique(img);
		drawImageList.remove(img);
	}

	final void removeBackgroundImage(DrawImage img) {
		bgImageList.remove(img);
		drawImageList.add(img);
	}

	/**
	 * get default color for geo
	 * 
	 * @param geo
	 */
	static public Color getDefaultColor(GeoElement geo) {
		Color objcol = Color.black;
		if (geo.isGeoPoint())
			objcol = colPoint;
		else if (geo.isGeoSegment())
			objcol = colSegment;
		else if (geo.isGeoLine())
			objcol = colLine;
		else if (geo.isGeoVector())
			objcol = colVector;
		else if (geo.isGeoConic())
			objcol = colConic;
		else if (geo.isGeoPolygon())
			objcol = colPolygon;
		return objcol;
	}

	/**
	 * get default dependent-color for geo
	 * 
	 * @param geo
	 */
	static public Color getDependentColor(GeoElement geo) {
		Color objcol = Color.black;
		if (geo.isGeoPoint()) {
			GeoPoint p = (GeoPoint) geo;
			if (p.hasPath())
				objcol = colPathPoint;
			else
				objcol = colDepPoint;
		} else if (geo.isGeoSegment())
			objcol = colDepSegment;
		else if (geo.isGeoLine())
			objcol = colDepLine;
		else if (geo.isGeoVector())
			objcol = colDepVector;
		else if (geo.isGeoConic())
			objcol = colDepConic;
		else if (geo.isGeoPolygon())
			objcol = colDepPolygon;
		else if (geo.isGeoLocus())
			objcol = colDepLocus;
		return objcol;
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

	void setMoveCursor() {
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

	private void initCursor() {
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

	private Cursor getCursorForImage(Image image) {
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
			app.updateStatusLabelAxesRatio();
		}
	}

	public void updateSize() {
		width = getWidth();
		height = getHeight();
		if (width <= 0 || height <= 0)
			return;

		// real world values
		setRealWorldBounds();

		bgImage = getGraphicsConfiguration().createCompatibleImage(width,
				height);
		bgGraphics = bgImage.createGraphics();
		if (antiAliasing) {
			bgGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		updateBackgroundImage();
		updateAllDrawables(true);
	}

	// move view:
	/*
	 * private void setDrawMode(int mode) { if (mode != drawMode) { drawMode =
	 * mode; if (mode == DRAW_MODE_BACKGROUND_IMAGE) updateBackgroundImage(); } }
	 */

	final private void setRealWorldBounds() {
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

	private void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / xscale;
		int exp = (int) Math.round(Math.log(unitPerCM) / Math.log(10));
		printingScale = Math.pow(10, -exp);
	}

	// axis: 0 for x-axis, 1 for y-axis
	private void setAxesIntervals(double scale, int axis) {
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
			gridDistances[axis] = axesNumberingDistances[axis] / 2.0;
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

	public String getXYscaleRatioString() {
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

	public void showAxes(boolean show) {
		if (show == showAxes)
			return;
		showAxes = show;
		updateBackgroundImage();
	}

	public boolean getShowAxes() {
		return showAxes;
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

		// paint HotEquations
		paintChildren(g2);

		// draw all Drawables
		drawGeometricObjects(g2);

		if (zoomRectangle != null) {
			drawZoomRectangle(g2);
		}

		if (showMouseCoords && (showAxes || showGrid))
			drawMouseCoords(g2);
	}

	private void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private void drawZoomRectangle(Graphics2D g2) {
		g2.setColor(colZoomRectangleFill);
		g2.fill(zoomRectangle);
		g2.setColor(colZoomRectangle);
		g2.draw(zoomRectangle);
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

			if (line == null)
				line = sb.toString();
			else
				line = line + " - " + sb.toString();

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

		g2d.setClip(0, 0, width, height);

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

		// draw HotEquations
		paintChildren(g2d);

		drawGeometricObjects(g2d);
	}

	/**
	 * Tells if there are any traces in the background image.
	 */
	private boolean isTracing() {
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
	private boolean hasBackgroundImages() {
		return bgImageList.size() > 0;
	}

	/**
	 * Returns image of drawing pad with specified width. The height of the
	 * image is width * getHeight() / getWidth().
	 * 
	 * @param width
	 * @return
	 */
	public BufferedImage getExportImage(int width) throws OutOfMemoryError {
		double scale = width / (double) getWidth();
		return getExportImage(scale);
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 */
	public BufferedImage getExportImage(double scale) throws OutOfMemoryError {
		int height = (int) Math.floor(getHeight() * scale);
		int width = (int) Math.floor(getWidth() * scale);
		BufferedImage img = createBufferedImage(width, height);
		exportPaint(img.createGraphics(), scale);
		img.flush();
		return img;
	}

	private BufferedImage createBufferedImage(int width, int height)
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

	final Graphics2D getBackgroundGraphics() {
		return bgGraphics;
	}

	final public void updateBackground() {
		updateBackgroundImage();
		updateAllDrawables(true);
		// repaint();
	}

	final void updateBackgroundImage() {
		if (bgGraphics != null) {
			clearBackground(bgGraphics);
			bgImageList.drawAll(bgGraphics);

			drawBackground(bgGraphics, false);
		}
	}

	final private void drawBackground(Graphics2D g, boolean clear) {
		if (clear) {
			clearBackground(g);
		}

		setAntialiasing(g);
		if (showGrid)
			drawGrid(g);
		if (showAxes)
			drawAxes(g);

		if (app.showResetIcon())
			g.drawImage(resetImage, width - 24, 2, null);
	}

	final private void clearBackground(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, width, height);
	}

	private static int SCREEN_BORDER = 10;

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
		boolean drawArrows = axesLineType == AXES_LINE_TYPE_ARROW;

		FontRenderContext frc = g2.getFontRenderContext();

		g2.setPaint(axesColor);
		g2.setStroke(thinStroke);
		g2.setFont(fontAxes);
		int fontsize = fontAxes.getSize();

		// X - AXIS
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
			tempLine.setLine(pix, yZeroTick, pix, yBig);
			g2.draw(tempLine);
			pix += axesStep;
			rw += axesNumberingDistances[0];
		}
		int maxX = width - SCREEN_BORDER;
		for (; pix < width; rw += axesNumberingDistances[0], pix += axesStep) {
			if (pix <= maxX) {
				if (showAxesNumbers[0]) {
					sb.setLength(0);
					String strNum = axesNumberFormat[0].format(rw);
					boolean zero = strNum.equals("0") || strNum.equals("-0");
					if (axesUnitLabels[0] != null) {
						if (piAxisUnit[0]) {
							if (zero) {
								sb.append(strNum);
							} else {
								double piUnits = rw / Math.PI;
								strNum = axesNumberFormat[0].format(piUnits);
								if (strNum.equals("-1")) {
									sb.append("-");
								} else if (!strNum.equals("1")) {
									sb.append(strNum);
								}
								sb.append("\u03c0"); // pi
							}
						} else {
							sb.append(strNum);
							sb.append(axesUnitLabels[0]);
						}
					} else {
						sb.append(strNum);
					}
					TextLayout layout = new TextLayout(sb.toString(), fontAxes,
							frc);
					int x, y = (int) (yZero + yoffset);
					if (zero) {
						x = (int) (pix + 6);
					} else {
						x = (int) (pix + xoffset - layout.getAdvance() / 2);
					}
					g2.drawString(sb.toString(), x, y);
				}

				// big tick
				tempLine.setLine(pix, yZeroTick, pix, yBig);
				g2.draw(tempLine);
			} else if (!drawArrows) {
				// draw last tick if there is no arrow
				tempLine.setLine(pix, yZeroTick, pix, yBig);
				g2.draw(tempLine);
			}

			// small tick
			smallTickPix = pix - tickStep;
			tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
			g2.draw(tempLine);
		}
		// last small tick
		smallTickPix = pix - tickStep;
		if (!drawArrows || smallTickPix <= maxX) {
			tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
			g2.draw(tempLine);
		}

		// y-Axis
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
			g2.drawString(axesLabels[1], (int) (xZero + 5), (int) (5 + layout
					.getAscent()));
		}

		// numbers
		rw = ymax - (ymax % axesNumberingDistances[1]);
		pix = yZero - rw * yscale;
		axesStep = yscale * axesNumberingDistances[1]; // pixelstep
		tickStep = axesStep / 2;

		// first small tick
		smallTickPix = pix - tickStep;
		if (!drawArrows || smallTickPix > SCREEN_BORDER) {
			tempLine.setLine(xSmall1, smallTickPix, xSmall2, smallTickPix);
			g2.draw(tempLine);
		}

		// don't get too near to the top of the screen
		if (pix < SCREEN_BORDER) {
			if (!drawArrows) {
				// draw tick if there is no arrow
				tempLine.setLine(xBig, pix, xZeroTick, pix);
				g2.draw(tempLine);
			}
			smallTickPix = pix + tickStep;
			if (smallTickPix > SCREEN_BORDER) {
				tempLine.setLine(xSmall1, smallTickPix, xSmall2, smallTickPix);
				g2.draw(tempLine);
			}
			pix += axesStep;
			rw -= axesNumberingDistances[1];
		}
		int maxY = height - SCREEN_BORDER;
		for (; pix <= height; rw -= axesNumberingDistances[1], pix += axesStep) {
			if (pix <= maxY) {
				if (showAxesNumbers[1]) {
					sb.setLength(0);
					String strNum = axesNumberFormat[1].format(rw);
					boolean zero = strNum.equals("0") || strNum.equals("-0");
					if (axesUnitLabels[1] != null) {
						if (piAxisUnit[1]) {
							if (zero) {
								sb.append(strNum);
							} else {
								strNum = axesNumberFormat[1].format(rw
										/ Math.PI);
								if (strNum.equals("-1")) {
									sb.append("-");
								} else if (!strNum.equals("1")) {
									sb.append(strNum);
								}
								sb.append("\u03c0"); // pi
							}
						} else {
							sb.append(strNum);
							sb.append(axesUnitLabels[1]);
						}
					} else {
						sb.append(strNum);
					}

					TextLayout layout = new TextLayout(sb.toString(), fontAxes,
							frc);
					int x = (int) (xZero + xoffset - layout.getAdvance());
					int y;
					if (zero) {
						y = (int) (yZero - 2);
					} else {
						y = (int) (pix + yoffset);
					}
					g2.drawString(sb.toString(), x, y);
				}
			}

			tempLine.setLine(xBig, pix, xZeroTick, pix);
			g2.draw(tempLine);

			smallTickPix = pix + tickStep;
			tempLine.setLine(xSmall1, smallTickPix, xSmall2, smallTickPix);
			g2.draw(tempLine);
		}

		// x-Axis
		g2.setStroke(axesStroke);
		tempLine.setLine(0, yZero, width, yZero);
		g2.draw(tempLine);

		// y-Axis
		tempLine.setLine(xZero, 0, xZero, height);
		g2.draw(tempLine);

		if (drawArrows) {
			// draw arrows
			// x
			int size = 4;
			tempLine.setLine(width - 1, yZero, width - 1 - size, yZero - size);
			g2.draw(tempLine);
			tempLine.setLine(width - 1, yZero, width - 1 - size, yZero + size);
			g2.draw(tempLine);

			// y
			tempLine.setLine(xZero, 0, xZero - size, size);
			g2.draw(tempLine);
			tempLine.setLine(xZero, 0, xZero + size, size);
			g2.draw(tempLine);
		}
	}

	final void drawGrid(Graphics2D g2) {
		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		// vertical grid lines
		double tickStep = xscale * gridDistances[0];
		for (double i = xZero % tickStep; i <= width; i += tickStep) {
			tempLine.setLine(i, 0, i, height);
			g2.draw(tempLine);
		}

		// horizontal grid lines
		tickStep = yscale * gridDistances[1];
		for (double j = yZero % tickStep; j <= height; j += tickStep) {
			tempLine.setLine(0, j, width, j);
			g2.draw(tempLine);
		}
	}

	final private void drawMouseCoords(Graphics2D g2) {
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

	final private void drawGeometricObjects(Graphics2D g2) {
		// draw images
		drawImageList.drawAll(g2);

		if (previewDrawable != null) {
			previewDrawable.drawPreview(g2);
		}

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

		// draw lines
		drawSegmentList.drawAll(g2);

		// draw vectors
		drawVectorList.drawAll(g2);

		// draw locus
		drawLocusList.drawAll(g2);

		// draw points
		drawPointList.drawAll(g2);

		// draw text
		drawTextList.drawAll(g2);
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

	private ArrayList tempArrayList = new ArrayList();

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
	 * returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 */
	final public ArrayList getHits(Point p) {
		foundHits.clear();

		// count images and Polygons
		int polyCount = 0;
		int imageCount = 0;

		// get anything but a polygon
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();

				if (geo.isEuclidianVisible()) {
					if (geo.isGeoImage()) {
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
			if (showAxes) {
				if (Math.abs(yZero - p.y) < 3)
					foundHits.add(kernel.getXAxis());
				if (Math.abs(xZero - p.x) < 3)
					foundHits.add(kernel.getYAxis());
			}
		}

		int size = foundHits.size();
		if (size == 0)
			return null;

		// remove all images and polygons if there are other objects too
		if (size - (imageCount + polyCount) > 0) {
			for (int i = 0; i < foundHits.size(); ++i) {
				GeoElement geo = (GeoElement) foundHits.get(i);
				if (geo.isGeoImage() || geo.isGeoPolygon())
					foundHits.remove(i);
			}
		}

		return foundHits;
	}

	private ArrayList foundHits = new ArrayList();

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

	private final int TEST_MOVEABLE = 1;

	private final int TEST_ROTATEMOVEABLE = 2;

	private ArrayList getMoveables(ArrayList hits, int test, GeoPoint rotCenter) {
		if (hits == null)
			return null;

		GeoElement geo;
		moveableList.clear();
		for (int i = 0; i < hits.size(); ++i) {
			geo = (GeoElement) hits.get(i);
			switch (test) {
			case TEST_MOVEABLE:
				if (geo.isMoveable() || geo.hasMoveableParents())
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

	private ArrayList moveableList = new ArrayList();

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
	 * Stores all GeoElements of type geoclass to result list.
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT of type geoclass out of
	 *            hits.
	 */
	final private ArrayList getHits(ArrayList hits, Class geoclass,
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

	private ArrayList topHitsList = new ArrayList();

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
	final public void add(GeoElement geo) {
		// check if there is already a drawable for geo
		Drawable d = getDrawable(geo);
		if (d != null)
			return;

		d = createDrawable(geo);
		if (d != null)
			repaint();
	}

	/**
	 * adds a GeoElement to this view
	 */
	final Drawable createDrawable(GeoElement geo) {
		Drawable d = null;

		if (geo.isGeoPoint()) {
			d = new DrawPoint(this, (GeoPoint) geo);
			drawPointList.add(d);
		} else if (geo.isGeoSegment()) {
			d = new DrawSegment(this, (GeoSegment) geo);
			drawSegmentList.add(d);
		} else if (geo.isGeoRay()) {
			d = new DrawRay(this, (GeoRay) geo);
			drawSegmentList.add(d);
		} else if (geo.isGeoLine()) {
			d = new DrawLine(this, (GeoLine) geo);
			drawLineList.add(d);
		} else if (geo.isGeoPolygon()) {
			d = new DrawPolygon(this, (GeoPolygon) geo);
			drawPolygonList.add(d);
		} else if (geo.isGeoAngle()) {
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
				drawNumericList.add(d);
			} else {
				d = new DrawAngle(this, (GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet())
						geo.setObjColor(colDrawAngle);
					drawNumericList.add(d);
				}
			}
		} else if (geo.isGeoNumeric()) {
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo == null) {
				// indpendent number may be shown as slider
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
					if (geo.isIndependent())
						geo.setObjColor(colNumber);
					else
						geo.setObjColor(colDepNumber);
				}
				drawNumericList.add(d);
			}
		} else if (geo.isGeoVector()) {
			d = new DrawVector(this, (GeoVector) geo);
			drawVectorList.add(d);
		} else if (geo.isGeoConicPart()) {
			d = new DrawConicPart(this, (GeoConicPart) geo);
			drawConicList.add(d);
		} else if (geo.isGeoConic()) {
			d = new DrawConic(this, (GeoConic) geo);
			drawConicList.add(d);
		} else if (geo.isGeoFunction()) {
			d = new DrawFunction(this, (GeoFunction) geo);
			drawFunctionList.add(d);
		} else if (geo.isGeoText()) {
			d = new DrawText(this, (GeoText) geo);
			drawTextList.add(d);
		} else if (geo.isGeoImage()) {
			d = new DrawImage(this, (GeoImage) geo);
			if (!bgImageList.contains(d))
				drawImageList.add(d);
		} else if (geo.isGeoLocus()) {
			d = new DrawLocus(this, (GeoLocus) geo);
			drawLocusList.add(d);
		}

		else if (geo.isGeoList()) {
			// the geolist adds all its items in its update() method
			d = new DrawList(this, (GeoList) geo);
		}

		if (d != null) {
			allDrawableList.add(d);
			DrawableMap.put(geo, d);
		}
		return d;
	}

	/**
	 * removes a GeoElement from this view
	 */
	final public void remove(GeoElement geo) {
		Drawable d = (Drawable) DrawableMap.get(geo);

		if (d != null) {
			if (geo.isGeoPoint()) {
				drawPointList.remove(d);
			} else if (geo.isGeoSegment()) {
				drawSegmentList.remove(d);
			} else if (geo.isGeoRay()) {
				drawSegmentList.remove(d);
			} else if (geo.isGeoLine()) {
				drawLineList.remove(d);
			} else if (geo.isGeoPolygon()) {
				drawPolygonList.remove(d);
			} else if (geo.isGeoNumeric()) {
				drawNumericList.remove(d);
			} else if (geo.isGeoVector()) {
				drawVectorList.remove(d);
			} else if (geo.isGeoConic()) {
				drawConicList.remove(d);
			} else if (geo.isGeoFunction()) {
				drawFunctionList.remove(d);
			} else if (geo.isGeoText()) {
				drawTextList.remove(d);
				// remove HotEqn
				((DrawText) d).remove();
			} else if (geo.isGeoImage()) {
				drawImageList.remove(d);
			} else if (geo.isGeoLocus()) {
				drawLocusList.remove(d);
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

	final private void updateAllDrawables(boolean repaint) {
		allDrawableList.updateAll();
		if (repaint)
			repaint();
	}

	final private void updateDrawableFontSize() {
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

	/**
	 * returns settings in XML format
	 */
	public String getXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<euclidianView>\n");

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

		sb.append("\t<evSettings axes=\"");
		sb.append(showAxes);
		sb.append("\" grid=\"");
		sb.append(showGrid);
		sb.append("\" pointCapturing=\"");
		sb.append(pointCapturingMode);
		sb.append("\" pointStyle=\"");
		sb.append(pointStyle);
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
		sb.append(gridLineType);
		sb.append("\"/>\n");

		// axis settings
		for (int i = 0; i < 2; i++) {
			sb.append("\t<axis id=\"");
			sb.append(i);
			sb.append("\" label=\"");
			sb.append(axesLabels[i] == null ? "" : axesLabels[i]);
			sb.append("\" unitLabel=\"");
			sb.append(axesUnitLabels[i] == null ? "" : axesUnitLabels[i]);
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
		if (!automaticGridDistance) {
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
	public final void zoom(double px, double py, double zoomFactor,
			boolean storeUndo) {
		if (zoomer == null)
			zoomer = new MyZoomer();
		zoomer.init(px, py, zoomFactor, storeUndo);
		zoomer.startAnimation();
	}

	private MyZoomer zoomer;

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

	private MyAxesRatioZoomer axesRatioZoomer;

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
							SCALE_STANDARD, false);
				}
			};
			waiter.start();
		} else {
			// set the xscale and axes origin
			setAnimatedCoordSystem(XZERO_STANDARD, YZERO_STANDARD,
					SCALE_STANDARD, false);
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
			boolean storeUndo) {
		if (!kernel.isEqual(xscale, newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / xscale;
			zoom((ox - xZero * factor) / (1.0 - factor), (oy - yZero * factor)
					/ (1.0 - factor), factor, storeUndo);
		} else {
			// same scales: translate view to standard origin
			// do this with the following action listener
			if (mover == null)
				mover = new MyMover();
			mover.init(ox, oy, storeUndo);
			mover.startAnimation();
		}
	}

	private MyMover mover;

	private class MyZoomer implements ActionListener {
		static final int STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		private Timer timer; // for animation

		private double px, py; // zoom point

		private double factor;

		private int counter;

		private double oldScale, newScale, add, dx, dy;

		private long startTime;

		private boolean storeUndo;

		public MyZoomer() {
			timer = new Timer(DELAY, this);
		}

		public void init(double px, double py, double zoomFactor,
				boolean storeUndo) {
			this.px = px;
			this.py = py;
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			oldScale = xscale;
			newScale = xscale * zoomFactor;
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / STEPS;
			dx = xZero - px;
			dy = yZero - py;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		private synchronized void stopAnimation() {
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
			if (counter == STEPS || time > MAX_TIME) { // end of animation
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
	private class MyAxesRatioZoomer implements ActionListener {

		private Timer timer; // for animation

		private double factor;

		private int counter;

		private double oldScale, newScale, add;

		private long startTime;

		private boolean storeUndo;

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
			add = (newScale - oldScale) / MyZoomer.STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		private synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(xZero, yZero, xscale, newScale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.STEPS || time > MyZoomer.MAX_TIME) { // end
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
	private class MyMover implements ActionListener {
		private double dx, dy, add;

		private int counter;

		private double ox, oy; // new origin

		private Timer timer;

		private long startTime;

		private boolean storeUndo;

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
			add = 1.0 / MyZoomer.STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		private synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(ox, oy, xscale, yscale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.STEPS || time > MyZoomer.MAX_TIME) { // end
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

	/*
	 * Transferable implementation
	 */
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = { DataFlavor.imageFlavor };
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor == DataFlavor.imageFlavor)
			return true;
		else
			return false;
	}

	public Object getTransferData(DataFlavor flavor) {
		if (flavor == DataFlavor.imageFlavor) {
			BufferedImage img = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_RGB);
			paint(img.createGraphics());
			img.flush();
			return img;
		}
		// Otherwise, return generic object
		return (new Object());
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
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

	public int getGridLineType() {
		return gridLineType;
	}

	public void setGridLineStyle(int gridLineType) {
		this.gridLineType = gridLineType;
		gridStroke = getStroke(1f, gridLineType);
	}

	public int getAxesLineType() {
		return axesLineType;
	}

	public void setAxesLineStyle(int axesLineType) {
		this.axesLineType = axesLineType;
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
					&& axesUnitLabels[i].equals("\u03c0");
		}
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
	}
}
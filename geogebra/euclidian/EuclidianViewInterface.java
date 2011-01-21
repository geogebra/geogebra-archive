package geogebra.euclidian;

import geogebra.kernel.GeoElement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


/**
 * 
 * Interface between EuclidianView (2D or 3D) and EuclidianController (2D or 3D)
 * 
 * (TODO) see EuclidianView for detail of methods
 * 
 */

public interface EuclidianViewInterface {

	
	/** reference to x axis*/
	public static final int AXIS_X = 0; 
	/** reference to y axis*/	
	public static final int AXIS_Y = 1; 

	
	
	public void updateSize();
	public void repaintEuclidianView();


	/**
	 * Zooms around fixed point (px, py)
	 */
	public void zoom(double px, double py, double zoomFactor, int steps, boolean storeUndo);



	// ??
	boolean hitAnimationButton(MouseEvent e);
	void setPreview(Previewable previewDrawable);
	public Drawable getDrawableFor(GeoElement geo);
	public DrawableND getDrawableND(GeoElement geo);
	public DrawableND createDrawableND(GeoElement geo);
	void setToolTipText(String plain);

	/**
	 * Updates highlighting of animation buttons. 
	 * @return whether status was changed
	 */
	boolean setAnimationButtonsHighlighted(boolean hitAnimationButton);

	/**
	 * Returns point capturing mode.
	 */
	int getPointCapturingMode();

	
	// selection rectangle
	public void setSelectionRectangle(Rectangle selectionRectangle);
	public Rectangle getSelectionRectangle();

	
	
	
	// cursor
	void setMoveCursor();
	void setDragCursor();
	void setDefaultCursor();
	void setHitCursor();
	

	
	
	// mode
	/**
	 * clears all selections and highlighting
	 */
	void resetMode();
	void setMode(int modeMove);

	
	// screen coordinate to real world coordinate
	/** convert screen coordinate x to real world coordinate x */
	public double toRealWorldCoordX(double minX);
	/** convert screen coordinate y to real world coordinate y */	
	public double toRealWorldCoordY(double maxY);

	public int toScreenCoordX(double minX);
	public int toScreenCoordY(double maxY);
	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo);





	
	
	
	
	//hits	
	/**get the hits recorded */
	Hits getHits();
	/** set the hits regarding to the mouse location */
	void setHits(Point p);
	
	
	/**
	 * sets array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	public void setHits(Rectangle rect);	
	
	GeoElement getLabelHit(Point mouseLoc);
	

	
	//////////////////////////////////////////////////////
	// AXIS, GRID, ETC.
	//////////////////////////////////////////////////////	
	
	
	/** sets the visibility of x and y axis
	 * @param showXaxis 
	 * @param showYaxis
	 * @deprecated use {@link EuclidianViewInterface#setShowAxes(boolean, boolean)} 
	 * or {@link EuclidianViewInterface#setShowAxis(int, boolean, boolean)} instead
	 */
	//void showAxes(boolean showXaxis, boolean showYaxis);
	
	
	
	
	boolean getShowXaxis();
	boolean getShowYaxis();
	
	
	
	boolean isGridOrAxesShown();
	int getGridType();
	void setCoordSystem(double x, double y, double xscale, double yscale);
	
	/**
	 * sets showing flag of the axis
	 * @param axis id of the axis
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */
	public void setShowAxis(int axis, boolean flag, boolean update);
	
	/**
	 * sets showing flag of all axes
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */	
	public void setShowAxes(boolean flag, boolean update);
	
	
	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel);
	
	
	/** sets if numbers are shown on this axis
	 * @param axis
	 * @param showAxisNumbers
	 */
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers);
	
	
	/** sets the tickstyle of this axis
	 * @param axis
	 * @param tickStyle
	 */
	public void setAxisTickStyle(int axis, int tickStyle);
	
	
	/** sets the axis crossing value
	 * @param axis
	 * @param cross
	 */
	public void setAxisCross(int axis, double cross);
	
	
	/** sets if the axis is drawn in the positive direction only
	 * @param axis
	 * @param isPositive
	 */
	public void setPositiveAxis(int axis, boolean isPositive);
	
	
	
	
	
	/** Sets coord system from mouse move */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode);
	void setAnimatedCoordSystem(double ox, double oy, double newScale,int steps, boolean storeUndo);


	//setters and getters	
	public void setShowMouseCoords(boolean b);
	public boolean getShowMouseCoords();
	double getXZero();
	double getYZero();
	public double getInvXscale();
	public double getInvYscale();
	double getXscale();
	double getYscale();
	public void setShowAxesRatio(boolean b);
	public Previewable getPreviewDrawable();
	public int getViewWidth();
	public int getViewHeight();
	public double getGridDistances(int i);
	public String[] getAxesLabels();
	public void setAxesLabels(String[] labels);
	public String[] getAxesUnitLabels();
	public void setShowAxesNumbers(boolean[] showNums);
	public void setAxesUnitLabels(String[] unitLabels);
	public boolean[] getShowAxesNumbers();
	public void setAxesNumberingDistance(double tickDist, int axis);
	public int[] getAxesTickStyles();
	
	public double[] getAxesCross() ;
	public void setAxesCross(double[] axisCross); 
	
	public boolean[] getPositiveAxes(); 
	public void setPositiveAxes(boolean[] positiveAxis); 


	
	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins();
	
	
	

	/////////////////////////////////////////
	// previewables

	/**
	 * create a previewable for line construction
	 * @param selectedPoints points
	 * @return the line previewable
	 */
	public Previewable createPreviewLine(ArrayList selectedPoints);
	
	/**
	 * create a previewable for segment construction
	 * @param selectedPoints points
	 * @return the segment previewable
	 */	
	public Previewable createPreviewSegment(ArrayList selectedPoints);
	
	
	/**
	 * create a previewable for ray construction
	 * @param selectedPoints points
	 * @return the ray previewable
	 */	
	public Previewable createPreviewRay(ArrayList selectedPoints);

	
	/**
	 * create a previewable for vector construction
	 * @param selectedPoints points
	 * @return the ray previewable
	 */	
	public Previewable createPreviewVector(ArrayList selectedPoints);

	/**
	 * create a previewable for polygon construction
	 * @param selectedPoints points
	 * @return the polygon previewable
	 */		
	public Previewable createPreviewPolygon(ArrayList selectedPoints);
	

	/**
	 * create a previewable for polyline construction
	 * @param selectedPoints points
	 * @return the polygon previewable
	 */		
	public Previewable createPreviewPolyLine(ArrayList selectedPoints);
	
	/**
	 * create a previewable for conic construction
	 * @param mode 
	 * @param selectedPoints points
	 * @return the conic previewable
	 */		
	public Previewable createPreviewConic(int mode, ArrayList selectedPoints);
	


	public void updatePreviewable();
	
	
	public void mouseEntered();
	public void mouseExited();
	public Previewable createPreviewParallelLine(ArrayList selectedPoints,
			ArrayList selectedLines);
	public Previewable createPreviewPerpendicularLine(ArrayList selectedPoints,
			ArrayList selectedLines);
	public Previewable createPreviewPerpendicularBisector(ArrayList selectedPoints);
	public Previewable createPreviewAngleBisector(ArrayList selectedPoints);
	
	
	
	//options
	public Color getBackground();
	public Color getAxesColor();
	public Color getGridColor();
	public boolean getShowGrid();
	public boolean getGridIsBold();
	public boolean getAllowShowMouseCoords();
	public double getXmin();
	public double getXmax();
	public double getYmin();
	public double getYmax();
	public int getAxesLineStyle();
	public int getGridLineStyle();
	public boolean isAutomaticGridDistance();
	public double[] getGridDistances();
	public void setBackground(Color showColorChooser);
	public void setAxesColor(Color showColorChooser);
	public void setGridColor(Color showColorChooser);
	public void showGrid(boolean selected);
	public void setGridIsBold(boolean selected);
	public void setAllowShowMouseCoords(boolean selected);
	public void setGridType(int selectedIndex);
	public void setAxesLineStyle(int selectedIndex);
	public void setGridLineStyle(int type);
	public void setAutomaticGridDistance(boolean b);
	public void setRealWorldCoordSystem(double min, double max, double ymin,
			double ymax);
	public void updateBackground();
	public void setGridDistances(double[] ticks);
	public void setAutomaticAxesNumberingDistance(boolean b, int axis);
	public void setAxesTickStyles(int[] styles);
	public boolean[] getDrawBorderAxes();
	public void setDrawBorderAxes(boolean[] border);
	public boolean[] isAutomaticAxesNumberingDistance();
	public double[] getAxesNumberingDistances();
	
	// for AlgebraView
	public int getMode();
	public void clickedGeo(GeoElement geo, MouseEvent e);
	public void mouseMovedOver(GeoElement geo);


	

	
}

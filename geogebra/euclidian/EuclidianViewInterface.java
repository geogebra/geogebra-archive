package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Path;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


/**
 * 
 * Interface between EuclidianView (2D or 3D) and EuclidianController (2D or 3D)
 * 
 * see EuclidianView for detail of methods
 * 
 */

public interface EuclidianViewInterface {


	
	
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

	boolean containsGeoPoint(ArrayList hits);
	
	
	
	
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
	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo);





	
	
	
	
	//hits
	
	ArrayList getHits(ArrayList hits, Class geoclass, ArrayList tempArrayList);
	ArrayList getHits(Point mouseLoc, Class geoClass, ArrayList tempArrayList);
	ArrayList getHits(Rectangle selectionRectangle);
	ArrayList getHits(Point mouseLoc);
	ArrayList getHits(Point mouseLoc, boolean b);
	
	
	ArrayList getTopHits(Point mouseLoc);
	ArrayList getTopHits(ArrayList moveableList);
	
	
	ArrayList getHitsForNewPointMode(ArrayList hits);

	ArrayList getOtherHits(ArrayList hits, Class geoclass, ArrayList tempArrayList);

	ArrayList getPointRotateableHits(ArrayList hits, GeoPoint rotationCenter);
	ArrayList getPointVectorNumericHits(Point mouseLoc);
	
	
	GeoElement getLabelHit(Point mouseLoc);

	ArrayList getMoveableHits(Point mouseLoc);
	ArrayList getMoveableHits(ArrayList hits);

	
	
	// axis, grid, etc.
	boolean getShowYaxis();
	boolean getShowXaxis();
	void showAxes(boolean b, boolean showYaxis);
	boolean isGridOrAxesShown();
	int getGridType();
	void setCoordSystem(double x, double y, double xscale, double yscale);
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
	
	
	








	
	

	
}

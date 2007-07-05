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
 * EuclidianController.java
 *
 * Created on 16. Oktober 2001, 15:41
 */

package geogebra.euclidian;

import geogebra.Application;
import geogebra.gui.AngleInputDialog;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Mirrorable;
import geogebra.kernel.Path;
import geogebra.kernel.PointRotateable;
import geogebra.kernel.Translateable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ToolTipManager;

final public class EuclidianController implements MouseListener,
		MouseMotionListener, MouseWheelListener, ComponentListener {

	private static final int MOVE_NONE = 101;

	private static final int MOVE_POINT = 102;

	private static final int MOVE_LINE = 103;

	private static final int MOVE_CONIC = 104;

	private static final int MOVE_VECTOR = 105;

	private static final int MOVE_VECTOR_STARTPOINT = 205;

	private static final int MOVE_VIEW = 106;
	
	private static final int MOVE_FUNCTION = 107;

	private static final int MOVE_LABEL = 108;

	private static final int MOVE_TEXT = 109;
	
	private static final int MOVE_NUMERIC = 110; // for number on slider
	
	private static final int MOVE_SLIDER = 111; // for slider itself
	
	private static final int MOVE_IMAGE = 112;
	
	private static final int MOVE_ROTATE = 113;
	
	private static final int MOVE_DEPENDENT = 114;
	
	private static final int MOVE_MULTIPLE_OBJECTS = 115; // for multiple objects
	
	private static final int MOVE_X_AXIS = 116;
	private static final int MOVE_Y_AXIS = 117;
	
	private static final int MOVE_BOOLEAN = 118; // for checkbox moving
	

	private Application app;

	private Kernel kernel;

	private EuclidianView view;

	Point startLoc, mouseLoc, lastMouseLoc; // current mouse location

	private double xZeroOld, yZeroOld, xTemp, yTemp;

	private Point oldLoc = new Point();

	double xRW, yRW, // real world coords of mouse location
			xRWold = Double.NEGATIVE_INFINITY, yRWold = xRWold, temp;

	// for moving conics:
	private Point2D.Double startPoint = new Point2D.Double();

	private Point selectionStartPoint = new Point();

	private GeoConic tempConic;

	private GeoFunction tempFunction;

	// private GeoVec2D b;

	private GeoPoint movedGeoPoint;

	private GeoLine movedGeoLine;

	//private GeoSegment movedGeoSegment;

	private GeoConic movedGeoConic;

	private GeoVector movedGeoVector;

	private GeoText movedGeoText;
	
	private GeoImage oldImage, movedGeoImage;	

	private GeoFunction movedGeoFunction;
	
	private GeoNumeric movedGeoNumeric;
	
	private GeoBoolean movedGeoBoolean;

	private GeoElement movedLabelGeoElement;

	private GeoElement movedGeoElement;	
	
	private GeoElement rotGeoElement, rotStartGeo;
	private GeoPoint rotationCenter;
	private MyDouble tempNum;
	private double rotStartAngle;
	private Translateable [] translateableGeos;
	private GeoVector translationVec;

	private ArrayList tempArrayList = new ArrayList();
	private ArrayList selectedPoints = new ArrayList();

	private ArrayList selectedLines = new ArrayList();

	private ArrayList selectedSegments = new ArrayList();

	private ArrayList selectedConics = new ArrayList();

	private ArrayList selectedFunctions = new ArrayList();
	private ArrayList selectedCurves = new ArrayList();

	private ArrayList selectedVectors = new ArrayList();
	
	private ArrayList selectedPolygons = new ArrayList();

	private ArrayList selectedGeos = new ArrayList();

	private LinkedList highlightedGeos = new LinkedList();

	private boolean selectionPreview = false;

	private boolean RIGHT_CLICK = false;

	private boolean QUICK_TRANSLATEVIEW = false;

	private boolean DRAGGING_OCCURED = false; // for moving objects

	private boolean POINT_CREATED = false;
	
	private boolean moveModeSelectionHandled;

	//private MyPopupMenu popupMenu;

	private int mode, oldMode, moveMode = MOVE_NONE;
	private Macro macro;
	private Class [] macroInput;

	private int DEFAULT_INITIAL_DELAY;
	
	private boolean toggleModeChangedKernel = false;

	/** Creates new EuclidianController */
	public EuclidianController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();

		// for tooltip manager
		DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance()
				.getInitialDelay();
		
		tempNum = new MyDouble(kernel);
	}

	Application getApplication() {
		return app;
	}

	Kernel getKernel() {
		return kernel;
	}

	void setView(EuclidianView view) {
		this.view = view;
	}

	void setMode(int newMode) {
		endOfMode(mode);
		app.clearSelectedGeos(false);
		initNewMode(newMode);
		kernel.notifyRepaint();
	}
	
	private void endOfMode(int mode) {
		switch (mode) {
			case EuclidianView.MODE_SHOW_HIDE_OBJECT:				
				// take all selected objects and hide them
				Collection coll = 	app.getSelectedGeos();				
				Iterator it = coll.iterator();
				while (it.hasNext()) {
					GeoElement geo = (GeoElement) it.next();					
					geo.setEuclidianVisible(false);
					geo.updateRepaint();								
				}				
				break;
		}
		
		if (toggleModeChangedKernel)
			app.storeUndoInfo();
	}
	
	private void initNewMode(int mode) {
		this.mode = mode;
		initShowMouseCoords();
		clearSelections();
		moveMode = MOVE_NONE;	

		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode) {
		case EuclidianView.MODE_JOIN: // line through two points
			previewDrawable = new DrawLine(view, selectedPoints);
			break;

		case EuclidianView.MODE_SEGMENT:
			previewDrawable = new DrawSegment(view, selectedPoints);
			break;

		case EuclidianView.MODE_RAY:
			previewDrawable = new DrawRay(view, selectedPoints);
			break;

		case EuclidianView.MODE_VECTOR:
			previewDrawable = new DrawVector(view, selectedPoints);
			break;

		case EuclidianView.MODE_POLYGON:
			previewDrawable = new DrawPolygon(view, selectedPoints);
			break;

		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
			previewDrawable = new DrawConic(view, mode, selectedPoints);
			break;
			
		// preview for arcs and sectors
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart(view, mode, selectedPoints);
			break;										
			
		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			// select all hidden objects			
			Iterator it = kernel.getConstruction().getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible() && 
					!(
					   (geo.isNumberValue() || geo.isBooleanValue()) && geo.isIndependent())
					 ) 
				{
					app.addSelectedGeo(geo);
					geo.setEuclidianVisible(true);					
					geo.updateRepaint();										
				}
			}	
			break;
			
		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			movedGeoElement = null; // this will be the active geo template
			break;
			
		case EuclidianView.MODE_MOVE_ROTATE:
			rotationCenter = null; // this will be the active geo template
			break;
			
		default:
			previewDrawable = null;

			// macro mode?
			if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
				// get ID of macro
				int macroID = mode - EuclidianView.MACRO_MODE_ID_OFFSET;
				macro = kernel.getMacro(macroID);
				macroInput = macro.getInputTypes();
				this.mode = EuclidianView.MODE_MACRO;								
			}		
			break;
		}
		
		view.setPreview(previewDrawable);	
		toggleModeChangedKernel = false;
	}	
	
	

	private void initShowMouseCoords() {
		view.showMouseCoords = (mode == EuclidianView.MODE_POINT);
	}

	void clearSelections() {
		clearSelection(selectedPoints);
		clearSelection(selectedLines);
		clearSelection(selectedSegments);
		clearSelection(selectedConics);
		clearSelection(selectedVectors);
		clearSelection(selectedPolygons);
		clearSelection(selectedGeos);
		clearSelection(selectedFunctions);		
		clearSelection(selectedCurves);

		// clear highlighting
		refreshHighlighting(null);
	}

	final public void mouseClicked(MouseEvent e) {	
		ArrayList hits;
		//GeoElement geo;
		
		if (mode != EuclidianView.MODE_ALGEBRA_INPUT)
			view.requestFocusInWindow();
		
		if (RIGHT_CLICK) return;
		setMouseLocation(e);		
		
		switch (mode) {
		case EuclidianView.MODE_MOVE:								
			switch (e.getClickCount()) {
			case 1:			
				// handle selection click
				if (mode == EuclidianView.MODE_MOVE) {			
					handleSelectClick(view.getTopHits(mouseLoc), e.isControlDown());
				}
				break;
			
			//	open properties dialog on double click
			case 2:
				if (app.isApplet())
					return;
				app.clearSelectedGeos();
				hits = view.getTopHits(mouseLoc);
				if (hits != null)
					app.showPropertiesDialog(hits);
				break;
			}
			break;
			
		case EuclidianView.MODE_ZOOM_IN:
			view.zoom(mouseLoc.x, mouseLoc.y, EuclidianView.MODE_ZOOM_FACTOR, 15,  false);
			toggleModeChangedKernel = true;
			break;
			
		case EuclidianView.MODE_ZOOM_OUT:
			view.zoom(mouseLoc.x, mouseLoc.y, 1d/EuclidianView.MODE_ZOOM_FACTOR, 15, false);
			toggleModeChangedKernel = true;
			break;
		}
	}
	
	private void handleSelectClick(ArrayList geos, boolean ctrlDown) {		
		if (geos == null) {			
			app.clearSelectedGeos();
		} else {					
			if (ctrlDown) {				
				app.toggleSelectedGeo( chooseGeo(geos) ); 
			} else {								
				if (!moveModeSelectionHandled) {					
					GeoElement geo = chooseGeo(geos);
					if (geo != null) {
						app.clearSelectedGeos(false);
						app.addSelectedGeo(geo);
					}
				}				
			}			
		}
	}

	final public void mousePressed(MouseEvent e) {
		//GeoElement geo;
		ArrayList hits;
		setMouseLocation(e);
		transformCoords();			
		
		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;			
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);	
		
		if (hitResetIcon()) {				
			// see mouseReleased
			return;
		}

		if (e.isPopupTrigger() || e.isMetaDown()) {
			if (!app.isRightClickEnabled()) return;
			RIGHT_CLICK = true;				
			return;
		} 
		else if (e.isShiftDown() // All Platforms: Shift key
				|| e.isControlDown() // old Windows key: Ctrl key 
				) 
		{
			QUICK_TRANSLATEVIEW = true;
			oldMode = mode; // remember current mode			
			//view.setMode(EuclidianView.MODE_TRANSLATEVIEW);
			mode = EuclidianView.MODE_TRANSLATEVIEW;	
		} 		
		RIGHT_CLICK = false;

		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianView.MODE_POINT:
			hits = view.getHits(mouseLoc, true);
			createNewPoint(hits, true, true, true);
			break;
			
		case EuclidianView.MODE_SEGMENT:
		case EuclidianView.MODE_SEGMENT_FIXED:		
		case EuclidianView.MODE_JOIN:
		case EuclidianView.MODE_RAY:
		case EuclidianView.MODE_VECTOR:
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
		case EuclidianView.MODE_POLYGON:
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, true, true, true);
			break;
		
		case EuclidianView.MODE_PARALLEL:
		case EuclidianView.MODE_ORTHOGONAL:
		case EuclidianView.MODE_LINE_BISECTOR:
		case EuclidianView.MODE_ANGULAR_BISECTOR:
		case EuclidianView.MODE_TANGENTS:		
		case EuclidianView.MODE_POLAR_DIAMETER:
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, false, true, true);
			break;					
			
		case EuclidianView.MODE_ANGLE:
			hits = view.getTopHits(mouseLoc);
 		 	// check if we got a polygon
			if (hits == null || !((GeoElement) hits.get(0)).isGeoPolygon()) {
				createNewPoint(hits, false, false, true);			
			}			
			break;
			
		case EuclidianView.MODE_ANGLE_FIXED:
		case EuclidianView.MODE_MIDPOINT:
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, false, false, true);			
			break;
			
		case EuclidianView.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode();
			break;

		// move an object
		case EuclidianView.MODE_MOVE:		
			handleMousePressedForMoveMode(e);			
			break;

		// move drawing pad or axis
		case EuclidianView.MODE_TRANSLATEVIEW:			
			// check if axis is hit
			hits = view.getHits(mouseLoc);
			if (hits != null && hits.size() == 1) {
				Object hit0 = hits.get(0);
				if (hit0 == kernel.getXAxis())
					moveMode = MOVE_X_AXIS;
				else if (hit0 == kernel.getYAxis())
					moveMode = MOVE_Y_AXIS;
				else
					moveMode = MOVE_VIEW;
			} else {						
				moveMode = MOVE_VIEW;
			}						
			
			startLoc = mouseLoc; 
			if (!QUICK_TRANSLATEVIEW) {
				if (moveMode == MOVE_VIEW)
					view.setMoveCursor();
				else
					view.setDragCursor();
			}
			xZeroOld = view.xZero;
			yZeroOld = view.yZero;		
			xTemp = xRW;
			yTemp = yRW;
			view.showAxesRatio = (moveMode == MOVE_X_AXIS) || (moveMode == MOVE_Y_AXIS);
			//view.setDrawMode(EuclidianView.DRAW_MODE_DIRECT_DRAW);
			break;		
				
		default:
			moveMode = MOVE_NONE;				 
		}
	}
	
	private void handleMousePressedForRotateMode() {	
		GeoElement geo;
		ArrayList hits;
		
		// we need the center of the rotation
		if (rotationCenter == null) {
			rotationCenter = (GeoPoint) chooseGeo(view.getHits(mouseLoc, GeoPoint.class, tempArrayList));
			app.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		}
		else {	
			hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (hits != null && hits.contains(rotationCenter)) {
				app.removeSelectedGeo(rotationCenter);
				rotationCenter = null;
				moveMode = MOVE_NONE;
				return;
			}
							
			moveModeSelectionHandled = true;
			
			// find and set rotGeoElement
			hits = view.getPointRotateableHits(hits, rotationCenter);					
			
			// object was chosen before, take it now!
			if (hits != null && hits.contains(rotGeoElement))
				geo = rotGeoElement;
			else {
				geo = chooseGeo(hits);				
				app.addSelectedGeo(geo);				
			}			
			rotGeoElement = geo;						
			
			if (geo != null) {							
				doSingleHighlighting(rotGeoElement);						
				//rotGeoElement.setHighlighted(true);
				
				// init values needed for rotation
				rotStartGeo = rotGeoElement.copy();
				rotStartAngle = Math.atan2(yRW - rotationCenter.inhomY, 
											xRW - rotationCenter.inhomX);
				moveMode = MOVE_ROTATE;
			} else {
				moveMode = MOVE_NONE;
			}
		}			
	}
	
	private void handleMousePressedForMoveMode(MouseEvent e) {
		// move label?
		GeoElement geo = view.getLabelHit(mouseLoc);
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			view.setDragCursor();
			return;
		}

		// find and set movedGeoElement
		ArrayList moveableList = view.getMoveableHits(mouseLoc);
		ArrayList hits = view.getTopHits(moveableList);	
		
		ArrayList selGeos = app.getSelectedGeos();
		// if object was chosen before, take it now!
		if (selGeos.size() == 1 && 
				hits != null && hits.contains(selGeos.get(0))) 
		{
			// object was chosen before: take it			
			geo = (GeoElement) selGeos.get(0);			
		} else {
			// choose out of hits			
			geo = chooseGeo(hits);
			if (!selGeos.contains(geo)) {
				app.clearSelectedGeos();
				app.addSelectedGeo(geo);
			}
		}				
		
		if (geo != null) {		
			moveModeSelectionHandled = true;														
		} else {
			// no geo clicked at
			moveMode = MOVE_NONE;			
			return;
		}				
		
		movedGeoElement = geo;
		//doSingleHighlighting(movedGeoElement);				
				
		/*
		// if object was chosen before, take it now!
		ArrayList selGeos = app.getSelectedGeos();
		if (selGeos.size() == 1 && hits != null && hits.contains(selGeos.get(0))) {
			// object was chosen before: take it
			geo = (GeoElement) selGeos.get(0);			
		} else {
			geo = chooseGeo(hits);			
		}		
				
		if (geo != null) {
			app.clearSelectedGeos(false);
			app.addSelectedGeo(geo);
			moveModeSelectionHandled = true;			
		}						
		
		movedGeoElement = geo;
		doSingleHighlighting(movedGeoElement);	
		*/	
				
		
		// multiple geos selected
		if (movedGeoElement != null && selGeos.size() > 1) {									
			moveMode = MOVE_MULTIPLE_OBJECTS;
			startPoint.setLocation(xRW, yRW);	
			startLoc = mouseLoc;
			view.setDragCursor();
			if (translationVec == null)
				translationVec = new GeoVector(kernel.getConstruction());
		}	
			// dependent object: moveable parents?
		else if (!movedGeoElement.isMoveable()) {				
				translateableGeos = movedGeoElement.getTranslateableParents();
				if (translateableGeos != null) {					
					moveMode = MOVE_DEPENDENT;
					startPoint.setLocation(xRW, yRW);					
					view.setDragCursor();
					if (translationVec == null)
						translationVec = new GeoVector(kernel.getConstruction());
				} else {
					moveMode = MOVE_NONE;
				}				
			} 
			else if (movedGeoElement.isGeoPoint()) {
				moveMode = MOVE_POINT;
				movedGeoPoint = (GeoPoint) movedGeoElement;
				view.showMouseCoords = !app.isApplet()
						&& !movedGeoPoint.hasPath();
				view.setDragCursor();
			} 			
			else if (movedGeoElement.isGeoLine()) {
				moveMode = MOVE_LINE;
				movedGeoLine = (GeoLine) movedGeoElement;
				view.showMouseCoords = true;
				view.setDragCursor();
			} 
			else if (movedGeoElement.isGeoVector()) {
				movedGeoVector = (GeoVector) movedGeoElement;

				// change vector itself or move only startpoint?
				// if vector is dependent or
				// mouseLoc is closer to the startpoint than to the end
				// point
				// then move the startpoint of the vector
				if (movedGeoVector.hasAbsoluteLocation()) {
					GeoPoint sP = movedGeoVector.getStartPoint();
					double sx = 0;
					double sy = 0;
					if (sP != null) {
						sx = sP.inhomX;
						sy = sP.inhomY;
					}
					//	if |mouse - startpoint| < 1/2 * |vec| then move
					// startpoint
					if (2d * GeoVec2D.length(xRW - sx, yRW - sy) < GeoVec2D
							.length(movedGeoVector.x, movedGeoVector.y)) { // take
																		   // startPoint
						moveMode = MOVE_VECTOR_STARTPOINT;
						if (sP == null) {
							sP = new GeoPoint(kernel.getConstruction());
							sP.setCoords(xRW, xRW, 1.0);
							try {
								movedGeoVector.setStartPoint(sP);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} else
						moveMode = MOVE_VECTOR;
				} else {
					moveMode = MOVE_VECTOR;
				}

				view.showMouseCoords = true;
				view.setDragCursor();
			} 
			else if (movedGeoElement.isGeoText()) {
				moveMode = MOVE_TEXT;
				movedGeoText = (GeoText) movedGeoElement;
				view.showMouseCoords = false;
				view.setDragCursor();	
				
				if (movedGeoText.isAbsoluteScreenLocActive()) {
					oldLoc.setLocation(movedGeoText.getAbsoluteScreenLocX(),
										movedGeoText.getAbsoluteScreenLocY());
					startLoc = mouseLoc;
				}
				else if (movedGeoText.hasAbsoluteLocation()) {
					//	absolute location: change location
					GeoPoint loc = movedGeoText.getStartPoint();
					if (loc == null) {
						loc = new GeoPoint(kernel.getConstruction());
						loc.setCoords(0, 0, 1.0);
						try {
							movedGeoText.setStartPoint(loc);
						} catch (Exception ex) {
						}
						startPoint.setLocation(xRW, yRW);
					} else {
						startPoint.setLocation(xRW - loc.inhomX, yRW
								- loc.inhomY);
					}
				} else {
					// for relative locations label has to be moved
					oldLoc.setLocation(movedGeoText.labelOffsetX,
							movedGeoText.labelOffsetY);
					startLoc = mouseLoc;
				}
			} else if (movedGeoElement.isGeoConic()) {
				moveMode = MOVE_CONIC;
				movedGeoConic = (GeoConic) movedGeoElement;
				view.showMouseCoords = false;
				view.setDragCursor();

				startPoint.setLocation(xRW, yRW);
				if (tempConic == null) {
					tempConic = new GeoConic(kernel.getConstruction());
				}
				tempConic.set(movedGeoConic);
			} 
			else if (movedGeoElement.isGeoFunction()) {
				moveMode = MOVE_FUNCTION;
				movedGeoFunction = (GeoFunction) movedGeoElement;
				view.showMouseCoords = false;
				view.setDragCursor();

				startPoint.setLocation(xRW, yRW);
				if (tempFunction == null) {
					tempFunction = new GeoFunction(kernel.getConstruction());
				}
				tempFunction.set(movedGeoFunction);
			} 
			else if (movedGeoElement.isGeoNumeric()) {															
				movedGeoNumeric = (GeoNumeric) movedGeoElement;
				moveMode = MOVE_NUMERIC;
				
				Drawable d = view.getDrawableFor(movedGeoNumeric);
				if (d instanceof DrawSlider) {
					// should we move the slider 
					// or the point on the slider, i.e. change the number
					DrawSlider ds = (DrawSlider) d;
					if (!ds.hitPoint(mouseLoc.x, mouseLoc.y) &&
						 ds.hitSlider(mouseLoc.x, mouseLoc.y)) {
						moveMode = MOVE_SLIDER;
						if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
							oldLoc.setLocation(movedGeoNumeric.getAbsoluteScreenLocX(),
												movedGeoNumeric.getAbsoluteScreenLocY());
							startLoc = mouseLoc;
						} else {
							startPoint.setLocation(xRW - movedGeoNumeric.getRealWorldLocX(),
													yRW - movedGeoNumeric.getRealWorldLocY());
						}
					}	
					else {						
						startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
					}
				} 						
				
				view.showMouseCoords = false;
				view.setDragCursor();					
			}  
			else if (movedGeoElement.isGeoBoolean()) {
				movedGeoBoolean = (GeoBoolean) movedGeoElement;
				// move checkbox
				moveMode = MOVE_BOOLEAN;					
				startLoc = mouseLoc;
				oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
				oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();
				
				view.showMouseCoords = false;
				view.setDragCursor();			
			}
			else if (movedGeoElement.isGeoImage()) {
				moveMode = MOVE_IMAGE;
				movedGeoImage = (GeoImage) movedGeoElement;
				view.showMouseCoords = false;
				view.setDragCursor();
				
				if (movedGeoImage.isAbsoluteScreenLocActive()) {
					oldLoc.setLocation(movedGeoImage.getAbsoluteScreenLocX(),
										movedGeoImage.getAbsoluteScreenLocY());
					startLoc = mouseLoc;
				} 
				else if (movedGeoImage.hasAbsoluteLocation()) {
					startPoint.setLocation(xRW, yRW);
					oldImage = new GeoImage(movedGeoImage);
				} 				
			}
			else {
				moveMode = MOVE_NONE;
			}

			view.repaint();												
	}

	final public void mouseDragged(MouseEvent e) {
		if (!DRAGGING_OCCURED) {
			DRAGGING_OCCURED = true;			
					
			if (mode == EuclidianView.MODE_MOVE_ROTATE) {
				app.clearSelectedGeos(false);
				app.addSelectedGeo(rotationCenter, false);						
			}
		}
		lastMouseLoc = mouseLoc;
		setMouseLocation(e);				
		transformCoords();

		// zoom rectangle (right drag) or selection rectangle (left drag)
		if (RIGHT_CLICK || allowSelectionRectangle()) {
			// set zoom rectangle's size
			updateSelectionRectangle(RIGHT_CLICK);
			view.repaint();
			return;
		}		

		// update previewable
		if (view.previewDrawable != null) {
			view.previewDrawable.updateMousePos(mouseLoc.x, mouseLoc.y);
		}		
		
		/*
		 * Conintuity handling
		 * 
		 * If the mouse is moved wildly we take intermediate steps to
		 * get a more continous behaviour
		 */		 		
		if (kernel.isContinuous() && lastMouseLoc != null) {
			double dx = mouseLoc.x - lastMouseLoc.x;
			double dy = mouseLoc.y - lastMouseLoc.y;			
			double distsq = dx*dx + dy*dy;		
			if (distsq > MOUSE_DRAG_MAX_DIST_SQUARE) {										
				double factor = Math.sqrt(MOUSE_DRAG_MAX_DIST_SQUARE / distsq);				
				dx *= factor;
				dy *= factor;
				int steps = (int) (1.0 / factor);
				int mx = mouseLoc.x;
				int my = mouseLoc.y;

				// System.out.println("BIG drag dist: " + Math.sqrt(distsq) + ", steps: " + steps  );
				for (int i=1; i <= steps; i++) {			
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + i * dx);
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + i * dy);
					calcRWcoords();
									
					handleMouseDragged(false);							
				}
				
				// set endpoint of mouse movement if we are not already there
				if (mouseLoc.x != mx || mouseLoc.y != my) {	
					mouseLoc.x = mx;
					mouseLoc.y = my;
					calcRWcoords();	
				}				
			} 
		}
		
		handleMouseDragged(true);								
	}	
	
	private boolean allowSelectionRectangle() {
		return mode == EuclidianView.MODE_MOVE && moveMode == MOVE_NONE ||
			mode == EuclidianView.MODE_ALGEBRA_INPUT && app.getCurrentSelectionListener() != null;			
	}
	
	
	
	
	// square of maximum allowed pixel distance 
	// for continous mouse movements
	private static double MOUSE_DRAG_MAX_DIST_SQUARE = 16; 	
	
	private void handleMouseDragged(boolean repaint) {
		// moveMode was set in mousePressed()
		switch (moveMode) {
			case MOVE_ROTATE:
				rotateObject(repaint);
				break;
				
			case MOVE_POINT:
				movePoint(repaint);
				break;
	
			case MOVE_LINE:
				moveLine(repaint);
				break;
	
			case MOVE_VECTOR:
				moveVector(repaint);
				break;
	
			case MOVE_VECTOR_STARTPOINT:
				moveVectorStartPoint(repaint);
				break;
	
			case MOVE_CONIC:
				moveConic(repaint);
				break;
	
			case MOVE_FUNCTION:
				moveFunction(repaint);
				break;
	
			case MOVE_LABEL:
				moveLabel();
				break;
	
			case MOVE_TEXT:
				moveText(repaint);
				break;
	
			case MOVE_IMAGE:
				moveImage(repaint);
				break;
				
			case MOVE_NUMERIC:
				moveNumeric(repaint);
				break;
				
			case MOVE_SLIDER:
				moveSlider(repaint);
				break;
				
			case MOVE_BOOLEAN:
				moveBoolean(repaint);
				break;
				
			case MOVE_DEPENDENT:
				moveDependent(repaint);
				break;
				
			case MOVE_MULTIPLE_OBJECTS:
				moveMultipleObjects(repaint);
				break;
				
			case MOVE_VIEW:
				if (repaint) {
					if (QUICK_TRANSLATEVIEW) view.setMoveCursor();
					view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x, yZeroOld
							+ mouseLoc.y - startLoc.y, view.xscale, view.yscale);
				}
				break;	
								
			case MOVE_X_AXIS:
				if (repaint) {
					if (QUICK_TRANSLATEVIEW) view.setDragCursor();
										
					// take care when we get close to the origin
					if (Math.abs(mouseLoc.x - view.xZero) < 2) {
						mouseLoc.x = (int) Math.round(mouseLoc.x > view.xZero ?  view.xZero + 2 : view.xZero - 2);						
					}											
					double xscale = (mouseLoc.x - view.xZero) / xTemp;					
					view.setCoordSystem(view.xZero, view.yZero, xscale, view.yscale);
				}
				break;	
				
			case MOVE_Y_AXIS:
				if (repaint) {
					if (QUICK_TRANSLATEVIEW) view.setDragCursor();
					
					// take care when we get close to the origin
					if (Math.abs(mouseLoc.y - view.yZero) < 2) {
						mouseLoc.y = (int) Math.round(mouseLoc.y > view.yZero ?  view.yZero + 2 : view.yZero - 2);						
					}											
					double yscale = (view.yZero - mouseLoc.y) / yTemp;					
					view.setCoordSystem(view.xZero, view.yZero, view.xscale, yscale);					
				}
				break;	
	
			default: // do nothing
		}
	}		

	private void updateSelectionRectangle(boolean keepScreenRatio) {
		if (view.getSelectionRectangle() == null)
			 view.setSelectionRectangle(new Rectangle());
				
		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		int dxabs = Math.abs(dx);
		int dyabs = Math.abs(dy);

		int width = dx;
		int height = dy;
		
		// the zoom rectangle should have the same aspect ratio as the view
		if (keepScreenRatio) {
			double ratio = (double) view.width / (double) view.height;
			if (dxabs >= dyabs * ratio) {		
				height = (int) (Math.round(dxabs / ratio));
				if (dy < 0)
					height = -height;
			} else {
				width = (int) Math.round(dyabs * ratio);
				if (dx < 0)
					width = -width;			
			}
		}

		Rectangle rect = view.getSelectionRectangle();
		if (height >= 0) {			
			if (width >= 0) {
				rect.setLocation(selectionStartPoint);
				rect.setSize(width, height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width, selectionStartPoint.y);
				rect.setSize(-width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				rect.setLocation(selectionStartPoint.x,
						selectionStartPoint.y + height);
				rect.setSize(width, -height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y + height);
				rect.setSize(-width, -height);
			}
		}
	}

	final public void mouseReleased(MouseEvent e) {	
		view.requestFocusInWindow();
		setMouseLocation(e);
		transformCoords();
		ArrayList hits = null;
		GeoElement geo;

		if (hitResetIcon()) {				
			app.reset();
			return;
		}
				
		if (RIGHT_CLICK) {									
			if (processZoomRectangle()) return;
			
			// get selected GeoElements						
			// show popup menu after right click
			hits = view.getTopHits(mouseLoc);
			if (hits == null) {
				// no hits
				if (app.selectedGeosSize() > 0) {
					// there are selected geos: show them
					app.showPropertiesDialog(app.getSelectedGeos());
				}
				else {
					// there are no selected geos: show drawing pad popup menu
					app.showDrawingPadPopup(view, mouseLoc);
				}
			} else {		
				// there are hits
				if (app.selectedGeosSize() > 0) {	
					// selected geos: add first hit to selection and show properties
					app.addSelectedGeo((GeoElement) hits.get(0));
					app.showPropertiesDialog(app.getSelectedGeos());				
				}
				else {
					// no selected geos: choose geo and show popup menu
					geo = chooseGeo(hits);
					if (geo != null)
						app.showPopupMenu(geo, view, mouseLoc);
				}																										
			}				
			return;
		}

		// handle moving
		boolean changedKernel = POINT_CREATED;		
		if (DRAGGING_OCCURED) {			
			changedKernel = (moveMode != MOVE_NONE);			
			movedGeoElement = null;
			rotGeoElement = null;	
			
			if (allowSelectionRectangle()) {
				processSelectionRectangle();				
				return;
			}
		} 

		// remember helper point, see createNewPoint()
		if (changedKernel)
			app.storeUndoInfo();

		// now handle current mode
		hits = view.getHits(mouseLoc);
		if (QUICK_TRANSLATEVIEW) {
			QUICK_TRANSLATEVIEW = false;
			//view.setMode(oldMode);
			mode = oldMode;
		} 
		
		// grid capturing on: newly created point should be taken
		if (hits == null && POINT_CREATED) {			
			hits = new ArrayList();
			hits.add(movedGeoPoint);				
		}
		POINT_CREATED = false;		
		
		changedKernel = processMode(hits, e);
		if (changedKernel)
			app.storeUndoInfo();

		if (hits != null)
			view.setDefaultCursor();		
		else
			view.setHitCursor();

		refreshHighlighting(null);
		
		// reinit vars
		//view.setDrawMode(EuclidianView.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();	
		view.showAxesRatio = false;
		kernel.notifyRepaint();					
	}
	
	private boolean hitResetIcon() {
		return app.showResetIcon() &&
		  (mouseLoc.y < 18 && mouseLoc.x > view.width - 18);
	}

	// return if we really did zoom
	private boolean processZoomRectangle() {
		Rectangle rect = view.getSelectionRectangle();
		if (rect == null) 
			return false;
		
		if (rect.width < 30 || rect.height < 30) {
			view.setSelectionRectangle(null);
			view.repaint();
			return false;
		}

		view.resetMode();
		// zoom zoomRectangle to EuclidianView's size
		double factor = (double) view.width / (double) rect.width;
		Point p = rect.getLocation();
		view.setSelectionRectangle(null);
		view.setAnimatedCoordSystem((view.xZero - p.x) * factor,
				(view.yZero - p.y) * factor, view.xscale * factor, 15, true);
		return true;
	}
	
	// select all geos in selection rectangle 
	private void processSelectionRectangle() {		
		ArrayList hits = view.getHits(view.getSelectionRectangle());		
		app.setSelectedGeos(hits.toArray());					
		view.repaint();		
	}

	final public void mouseMoved(MouseEvent e) {
		setMouseLocation(e);
		ArrayList hits = null;
		boolean noHighlighting = false;
		
		if (hitResetIcon()) {
			view.setToolTipText(app.getPlain("resetConstruction"));
			view.setHitCursor();
			return;
		} 

		// label hit in move mode: block all other hits
		if (mode == EuclidianView.MODE_MOVE) {
			GeoElement geo = view.getLabelHit(mouseLoc);
			if (geo != null) {				
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;				
			}
		}
		else if (mode == EuclidianView.MODE_POINT) {
			// include polygons in hits
			hits = view.getHits(mouseLoc, true);
		}

		if (hits == null)
			hits = view.getHits(mouseLoc);
		if (hits == null) {
			view.setToolTipText(null);
			view.setDefaultCursor();	
		}			
		else
			view.setHitCursor();

		//	manage highlighting
		boolean repaintNeeded = noHighlighting ? 
				  refreshHighlighting(null)
				: refreshHighlighting(hits);

		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		if (app.showAlgebraView()) {
			hits = view.getTopHits(hits);
			if (hits != null) {
				String text = GeoElement.getToolTipDescriptionHTML(hits,
						true, true);				
				view.setToolTipText(text);
			} else
				view.setToolTipText(null);
		}

		// update previewable
		if (view.previewDrawable != null) {
			view.previewDrawable.updateMousePos(mouseLoc.x, mouseLoc.y);
			repaintNeeded = true;
		}

		// show Mouse coordinates
		if (view.showMouseCoords) {
			transformCoords();
			repaintNeeded = true;
		}		

		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}


	private void doSingleHighlighting(GeoElement geo) {
		if (geo == null) return;
		
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
		}		
		
		highlightedGeos.add(geo);
		geo.setHighlighted(true); 
		kernel.notifyRepaint();					
	}

	// mode specific highlighting of selectable objects
	// returns wheter repaint is necessary
	final boolean refreshHighlighting(ArrayList hits) {
		boolean repaintNeeded = false;
		
		 //	clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
			repaintNeeded = true;
		}
		
		// find new objects to highlight
		highlightedGeos.clear();	
		selectionPreview = true; // only preview selection, see also
								 // mouseReleased()
		processMode(hits, null); // build highlightedGeos List
		selectionPreview = false; // reactivate selection in mouseReleased()
		
		// set highlighted objects
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(true); 
			repaintNeeded = true;
		}		
		return repaintNeeded;
	}

	//	set highlighted state of all highlighted geos without repainting
	private final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = (GeoElement) it.next();
			geo.setHighlighted(highlight);
		}
	}

	// process mode and return wheter kernel was changed
	final boolean processMode(ArrayList hits, MouseEvent e) {
		boolean changedKernel = false;

		switch (mode) {
		case EuclidianView.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {			
				move(view.getTopHits(hits));				
			} else {
				if (DRAGGING_OCCURED && app.selectedGeosSize() == 1)
					app.clearSelectedGeos();
			}
			break;			
			
		case EuclidianView.MODE_MOVE_ROTATE:
			// moveRotate() is a dummy function for highlighting only
			if (selectionPreview) {
				moveRotate(view.getTopHits(hits));
			}
			break;
			
		case EuclidianView.MODE_POINT:
			// point() is dummy function for highlighting only
			if (selectionPreview) {
				point(hits);
			}
			break;

		// copy geo to algebra input
		case EuclidianView.MODE_ALGEBRA_INPUT:
			boolean addToSelection = e != null && e.isControlDown();
			geoElementSelected(view.getTopHits(hits), addToSelection);
			break;

		// new line through two points
		case EuclidianView.MODE_JOIN:
			changedKernel = join(hits);
			break;

		// new segment through two points
		case EuclidianView.MODE_SEGMENT:
			changedKernel = segment(hits);
			break;
			
		// segment for point and number
		case EuclidianView.MODE_SEGMENT_FIXED:
			changedKernel = segmentFixed(hits);
			break;
		
		//	angle for two points and number
		case EuclidianView.MODE_ANGLE_FIXED:
			changedKernel = angleFixed(hits);
			break;

		case EuclidianView.MODE_MIDPOINT:
			changedKernel = midpoint(hits);
			break;

		// new ray through two points or point and vector
		case EuclidianView.MODE_RAY:
			changedKernel = ray(hits);
			break;

		// new polygon through points
		case EuclidianView.MODE_POLYGON:
			changedKernel = polygon(hits);
			break;

		// new vector between two points
		case EuclidianView.MODE_VECTOR:
			changedKernel = vector(hits);
			break;

		// intersect two objects
		case EuclidianView.MODE_INTERSECT:
			changedKernel = intersect(hits);
			break;

		// new line through point with direction of vector or line
		case EuclidianView.MODE_PARALLEL:
			changedKernel = parallel(hits);
			break;

		// new line through point orthogonal to vector or line
		case EuclidianView.MODE_ORTHOGONAL:
			changedKernel = orthogonal(hits);
			break;

		// new line bisector
		case EuclidianView.MODE_LINE_BISECTOR:
			changedKernel = lineBisector(hits);
			break;

		// new angular bisector
		case EuclidianView.MODE_ANGULAR_BISECTOR:
			changedKernel = angularBisector(hits);
			break;

		// new circle (2 points)
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		// new semicircle (2 points)
		case EuclidianView.MODE_SEMICIRCLE:
			changedKernel = circle2(hits, mode);
			break;
			
		case EuclidianView.MODE_LOCUS:
			changedKernel = locus(hits);
			break;

		// new circle (3 points)
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			changedKernel = threePoints(hits, mode);
			break;

		// new conic (5 points)
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
			changedKernel = conic5(hits);
			break;

		// relation query
		case EuclidianView.MODE_RELATION:
			relation(view.getTopHits(hits));			
			break;

		// new tangents
		case EuclidianView.MODE_TANGENTS:
			changedKernel = tangents(view.getTopHits(hits));
			break;
			
		case EuclidianView.MODE_POLAR_DIAMETER:
			changedKernel = polarLine(view.getTopHits(hits));
			break;

		// delete selected object
		case EuclidianView.MODE_DELETE:
			changedKernel = delete(view.getTopHits(hits));
			break;
		
		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			if (showHideObject(view.getTopHits(hits)))
				toggleModeChangedKernel = true;
			break;
			
		case EuclidianView.MODE_SHOW_HIDE_LABEL:
			if (showHideLabel(view.getTopHits(hits)))
				toggleModeChangedKernel = true;
			break;
			
		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			if (copyVisualStyle(view.getTopHits(hits)))
				toggleModeChangedKernel = true;
			break;
			
		//  new text or image
		case EuclidianView.MODE_TEXT:
		case EuclidianView.MODE_IMAGE:
			changedKernel = textImage(view.getOtherHits(hits, GeoImage.class, tempArrayList), mode);
			break;
			
		// new slider
		case EuclidianView.MODE_SLIDER:
			changedKernel = slider();
			break;			
			
		case EuclidianView.MODE_MIRROR_AT_POINT:
			changedKernel = mirrorAtPoint(hits);
			break;
			
		case EuclidianView.MODE_MIRROR_AT_LINE:
			changedKernel = mirrorAtLine(hits);
			break;
			
		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			changedKernel = translateByVector(hits);
			break;
						
		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			changedKernel = rotateByAngle(hits);
			break;
			
		case EuclidianView.MODE_DILATE_FROM_POINT:
			changedKernel = dilateFromPoint(hits);
			break;
			
		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
			changedKernel = circlePointRadius(hits);
			break;				
			
		case EuclidianView.MODE_ANGLE:
			changedKernel = angle(view.getTopHits(hits));
			break;
			
		case EuclidianView.MODE_VECTOR_FROM_POINT:
			changedKernel = vectorFromPoint(hits);
			break;
			
		case EuclidianView.MODE_DISTANCE:
			changedKernel = distance(hits, e);
			break;	
			
		case EuclidianView.MODE_MACRO:			
			changedKernel = macro(hits);
			break;
			
		case EuclidianView.MODE_AREA:
			changedKernel = area(hits, e);
			break;	
			
		case EuclidianView.MODE_SLOPE:
			changedKernel = slope(hits);
			break;
			
		case EuclidianView.MODE_REGULAR_POLYGON:
			changedKernel = regularPolygon(hits);
			break;
			
		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:
			changedKernel = showCheckBox(hits);
			break;

		default:
		// do nothing
		}

		// update preview
		if (view.previewDrawable != null) {
			view.previewDrawable.updatePreview();
			if (mouseLoc != null)
				view.previewDrawable.updateMousePos(mouseLoc.x, mouseLoc.y);			
			view.repaint();
		}

		return changedKernel;
	}

	final public void mouseEntered(MouseEvent e) {
		initToolTipManager();
		initShowMouseCoords();
	}

	final public void mouseExited(MouseEvent e) {
		refreshHighlighting(null);
		resetToolTipManager();
		view.showMouseCoords = false;
		mouseLoc = null;
		view.repaint();
	}

	/*
	public void focusGained(FocusEvent e) {				
		initToolTipManager();
	}

	public void focusLost(FocusEvent e) {
		resetToolTipManager();
	}*/

	private void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(true);
	}

	private void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */
	
	final private void rotateObject(boolean repaint) {
		double angle = Math.atan2(yRW - rotationCenter.inhomY, 
								xRW - rotationCenter.inhomX)
						- rotStartAngle;
		
		tempNum.set(angle);
		rotGeoElement.set(rotStartGeo);	
		((PointRotateable) rotGeoElement).rotate(tempNum, rotationCenter);
		
		if (repaint)
			rotGeoElement.updateRepaint();
		else
			rotGeoElement.updateCascade();
	}

	final private void moveLabel() {
		movedLabelGeoElement.setLabelOffset(oldLoc.x + mouseLoc.x
				- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
		movedLabelGeoElement.update();  // no update cascade needed
		kernel.notifyRepaint();
	}

	final private void movePoint(boolean repaint) {
		movedGeoPoint.setCoords(xRW, yRW, 1.0);
		if (repaint)
			movedGeoPoint.updateRepaint();
		else
			movedGeoPoint.updateCascade();		
	}

	final private void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setCoords(movedGeoLine.x, movedGeoLine.y, -(movedGeoLine.x
				* xRW + movedGeoLine.y * yRW));		
		if (repaint)
			movedGeoLine.updateRepaint();
		else
			movedGeoLine.updateCascade();	
	}

	final private void moveVector(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		if (P == null) {
			movedGeoVector.setCoords(xRW, yRW, 0.0);
		} else {
			movedGeoVector.setCoords(xRW - P.inhomX, yRW - P.inhomY, 0.0);
		}
		
		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final private void moveVectorStartPoint(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		P.setCoords(xRW, yRW, 1.0);
		
		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final private void moveText(boolean repaint) {
		if (movedGeoText.isAbsoluteScreenLocActive()) {
			movedGeoText.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);			
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = movedGeoText.getStartPoint();
				loc.setCoords(xRW - startPoint.x, yRW - startPoint.y, 1.0);
			} else {
				// relative location: move label (change label offset)
				movedGeoText.setLabelOffset(oldLoc.x + mouseLoc.x
						- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
			}
		}				
		
		if (repaint)
			movedGeoText.updateRepaint();
		else
			movedGeoText.updateCascade();	
	}
	
	final private void moveImage(boolean repaint) {	
		if (movedGeoImage.isAbsoluteScreenLocActive()) {
			movedGeoImage.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
												oldLoc.y + mouseLoc.y-startLoc.y);			

			if (repaint)
				movedGeoImage.updateRepaint();
			else
				movedGeoImage.updateCascade();
		} else {
			if (movedGeoImage.hasAbsoluteLocation()) {
				//	absolute location: translate all defined corners
				double vx = xRW - startPoint.x;
				double vy = yRW - startPoint.y;
				movedGeoImage.set(oldImage);
				for (int i=0; i < 3; i++) {
					GeoPoint corner = movedGeoImage.getCorner(i);
					if (corner != null) {
						corner.setCoords(corner.inhomX + vx, corner.inhomY + vy, 1.0);
					}
				}
				
				if (repaint)
					movedGeoImage.updateRepaint();
				else
					movedGeoImage.updateCascade();
			} 	
		}
	}

	final private void moveConic(boolean repaint) {
		movedGeoConic.set(tempConic);
		movedGeoConic.translate(xRW - startPoint.x, yRW - startPoint.y);		
		
		if (repaint)
			movedGeoConic.updateRepaint();
		else
			movedGeoConic.updateCascade();
	}

	final private void moveFunction(boolean repaint) {
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);		
		
		if (repaint)
			movedGeoFunction.updateRepaint();
		else
			movedGeoFunction.updateCascade();
	}
	
	final private void moveBoolean(boolean repaint) {
		movedGeoBoolean.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
				oldLoc.y + mouseLoc.y-startLoc.y);
			
		if (repaint)
			movedGeoBoolean.updateRepaint();
		else
			movedGeoBoolean.updateCascade();
	}
	
	final private void moveNumeric(boolean repaint) {
		double min = movedGeoNumeric.getIntervalMin();
		double max = movedGeoNumeric.getIntervalMax();
		
		double param;
		if (movedGeoNumeric.isSliderHorizontal()) {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {				
				param = mouseLoc.x - startPoint.x;
			} else {
				param = xRW - startPoint.x;
			}
		}			
		else {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
				param = startPoint.y - mouseLoc.y ;
			} else {
				param = yRW - startPoint.y;
			}
		}							
		param = param * (max - min) / movedGeoNumeric.getSliderWidth();					
				
		// round to animation step scale				
		param = Kernel.roundToScale(param, movedGeoNumeric.animationStep);
		
		double val = min + param;
		if (movedGeoNumeric.isGeoAngle()) {
			if (val < 0) 
				val = 0;
			else if (val > Kernel.PI_2)
				val = Kernel.PI_2;
		}
		
		val = kernel.checkInteger(val);	
		
		// do not set value unless it really changed!
		if (movedGeoNumeric.getValue() == val)
			return;
		
		movedGeoNumeric.setValue(val);						
		if (repaint)
			movedGeoNumeric.updateRepaint();
		else
			movedGeoNumeric.updateCascade();
	}
	
	final private void moveSlider(boolean repaint) {
		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			movedGeoNumeric.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
												oldLoc.y + mouseLoc.y-startLoc.y);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - startPoint.x, yRW - startPoint.y);
		}		
		
		if (repaint)
			movedGeoNumeric.updateRepaint();
		else
			movedGeoNumeric.updateCascade();				
	}
	
	final private void moveDependent(boolean repaint) {
		translationVec.setCoords(xRW - startPoint.x, yRW - startPoint.y, 0.0);
		for (int i=0; i < translateableGeos.length; i++) {						
			translateableGeos[i].translate(translationVec);			
			translateableGeos[i].toGeoElement().updateCascade();
		}				
		if (repaint)
			kernel.notifyRepaint();		
		startPoint.setLocation(xRW, yRW);		
	}
	
	private void moveMultipleObjects(boolean repaint) {		
		translationVec.setCoords(xRW - startPoint.x, yRW - startPoint.y, 0.0);
	
		// move all selected geos that are moveable and translateable
		ArrayList sel = app.getSelectedGeos();		
		for (int i=0; i < sel.size(); i++) {
			GeoElement geo = (GeoElement) sel.get(i);
			boolean movedGeo = false;
			if (geo.isMoveable()) {
				if (geo.isTranslateable()) {
					Translateable trans = (Translateable) geo;
					trans.translate(translationVec);			
					movedGeo = true;
				}
				else if (geo.isAbsoluteScreenLocateable()) {
					AbsoluteScreenLocateable screenLoc = (AbsoluteScreenLocateable) geo;
					if (screenLoc.isAbsoluteScreenLocActive()) {
						int x = screenLoc.getAbsoluteScreenLocX() + mouseLoc.x - startLoc.x;
						int y = screenLoc.getAbsoluteScreenLocY() + mouseLoc.y - startLoc.y;
						screenLoc.setAbsoluteScreenLoc(x, y);
						movedGeo = true;
					} 					
					else if (geo.isGeoText()) {
						// check for GeoText with unlabeled start point
						GeoText movedGeoText = (GeoText) geo;
						if (movedGeoText.hasAbsoluteLocation()) {
							//	absolute location: change location
							GeoPoint loc = movedGeoText.getStartPoint();
							loc.translate(translationVec);
							movedGeo = true;
						}						
					}
				}								
			}											
			
			if (movedGeo)
				geo.updateCascade();			
		}				
		
		if (repaint)
			kernel.notifyRepaint();	
		
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;
	}	
	

	/**
	 * COORD TRANSFORM SCREEN -> REAL WORLD
	 * 
	 * real world coords -> screen coords 
	 *     ( xscale 0 xZero ) 
	 * T = ( 0 -yscale yZero ) 
	 *     ( 0 0 1 )
	 * 
	 * screen coords -> real world coords 
	 *          ( 1/xscale 0 -xZero/xscale ) 
	 * T^(-1) = ( 0 -1/yscale yZero/yscale ) 
	 *          ( 0 0 1 )
	 */
	
	private void transformCoords() {
		transformCoords(false);
	}

	final private void transformCoords(boolean usePointCapturing) {
		// calc real world coords
		calcRWcoords();
		
		boolean doPointCapturing =
			usePointCapturing ||
			moveMode == MOVE_POINT ||
			moveMode == MOVE_FUNCTION;
		
		if (doPointCapturing) {
			//	point capturing to grid
			double pointCapturingPercentage = 1;
			switch (view.getPointCapturingMode()) {			
				case EuclidianView.POINT_CAPTURING_ON:
					pointCapturingPercentage = 0.125;
				
				case EuclidianView.POINT_CAPTURING_ON_GRID:
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW, view.gridDistances[0]);
					double y = Kernel.roundToScale(yRW, view.gridDistances[1]);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW);
					double b = Math.abs(y - yRW);
					if (a < view.gridDistances[0] * pointCapturingPercentage
						&& b < view.gridDistances[1] *  pointCapturingPercentage) {
						xRW = x;
						yRW = y;
						mouseLoc.x = view.toScreenCoordX(xRW);
						mouseLoc.y = view.toScreenCoordY(yRW);
					}
				
				default:
					// point capturing off
			}
		}
	}
	
	private void calcRWcoords() {
		xRW = (mouseLoc.x - view.xZero) * view.invXscale;
		yRW = (view.yZero - mouseLoc.y) * view.invYscale;
	}

	final private void setMouseLocation(MouseEvent e) {
		mouseLoc = e.getPoint();

		if (mouseLoc.x < 0)
			mouseLoc.x = 0;
		else if (mouseLoc.x > view.width)
			mouseLoc.x = view.width;
		if (mouseLoc.y < 0)
			mouseLoc.y = 0;
		else if (mouseLoc.y > view.height)
			mouseLoc.y = view.height;
	}

	/***************************************************************************
	 * mode implementations
	 * 
	 * the following methods return true if a factory method of the kernel was
	 * called
	 **************************************************************************/

	// create new point at current position if hits is null
	// or on path
	// or intersection point
	// returns wether new point was created or not
	final private boolean createNewPoint(ArrayList hits,
			boolean onPathPossible, boolean intersectPossible, boolean doSingleHighlighting) {
		Path path = null;		
		boolean createPoint = !view.containsGeoPoint(hits);
		GeoPoint point = null;

		//	try to get an intersection point
		if (createPoint && intersectPossible) {
			point = getSingleIntersectionPoint(hits);
			if (point != null) {
				// we don't use an undefined or infinite
				// intersection point
				if (!point.showInEuclidianView()) {
					point.remove();
				} else
					createPoint = false;
			}
		}

		//	check if point lies on path and if we are allowed to place a point
		// on a path
		if (createPoint) {
			ArrayList pathHits = view.getHits(hits, Path.class, tempArrayList);
			if (pathHits != null) {
				if (onPathPossible) {
					path = (Path) chooseGeo(pathHits);
					createPoint = path != null;
				} else {
					createPoint = false;
				}
			}
		}

		if (createPoint) {
			transformCoords(true); // use point capturing if on
			if (path == null) {
				point = kernel.Point(null, xRW, yRW);
				view.showMouseCoords = true;
			} else {
				point = kernel.Point(null, path, xRW, yRW);
			}
		}

		if (point != null) {
			movedGeoPoint = point;
			movedGeoElement = movedGeoPoint;
			moveMode = MOVE_POINT;
			view.setDragCursor();
			if (doSingleHighlighting)
				doSingleHighlighting(movedGeoPoint);
			POINT_CREATED = true;
			return true;
		} else {
			moveMode = MOVE_NONE;
			POINT_CREATED = false;
			return false;
		}
	}

	// get two points and create line through them
	final private boolean join(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Line(null, points[0], points[1]);
			return true;
		}
		return false;
	}

	//	get two points and create line through them
	final private boolean segment(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Segment(null, points[0], points[1]);
			return true;
		}
		return false;
	}

	// get two points and create vector between them
	final private boolean vector(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Vector(null, points[0], points[1]);
			return true;
		}
		return false;
	}

	//	get two points and create ray with them
	final private boolean ray(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Ray(null, points[0], points[1]);
			return true;
		}

		return false;
	}

	//	get at least 3 points and create polygon with them
	final private boolean polygon(ArrayList hits) {
		if (hits == null)
			return false;

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
					&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				kernel.Polygon(null, getSelectedPoints());
				return true;
			}
		}

		// points needed
		addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false);
		return false;
	}

	// get two objects (lines or conics) and create intersection point
	final private boolean intersect(ArrayList hits) {
		if (hits == null)
			return false;				

		// when two objects are selected at once then only one single
		// intersection point should be created
		boolean singlePointWanted = selGeos() == 0;
							
		// check how many interesting hits we have
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			ArrayList goodHits = new ArrayList();
			//goodHits.add(selectedGeos);
			view.getHits(hits, GeoLine.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			view.getHits(hits, GeoConic.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			view.getHits(hits, GeoFunction.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			
			if (goodHits.size() > 2 - selGeos()) {
				//  choose one geo, and select only this one
				GeoElement geo = chooseGeo(goodHits);
				hits.clear();
				hits.add(geo);				
			} else {
				hits = goodHits;
			}
		}			
		
		// get lines, conics and functions
		addSelectedLine(hits, 2, true);
		addSelectedConic(hits, 2, true);
		addSelectedFunction(hits, 2, true);				
		
		singlePointWanted = singlePointWanted && selGeos() == 2;
		
		if (selGeos() > 2)
			return false;

		// two lines
		if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			kernel.IntersectLines(null, lines[0], lines[1]);
			return true;
		}
		// two conics
		else if (selConics() == 2) {
			GeoConic[] conics = getSelectedConics();
			if (singlePointWanted)
				kernel.IntersectConicsSingle(null, conics[0], conics[1], xRW,
						yRW);
			else
				kernel.IntersectConics(null, conics[0], conics[1]);
			return true;
		} else if (selFunctions() == 2) {
			GeoFunction[] fun = getSelectedFunctions();
			boolean polynomials = fun[0].isPolynomialFunction(false)
					&& fun[1].isPolynomialFunction(false);
			if (!polynomials) {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				kernel.IntersectFunctions(null, fun[0], fun[1], startPoint);
			} else {
				// polynomials
				if (singlePointWanted) {
					kernel.IntersectPolynomialsSingle(null, fun[0], fun[1],
							xRW, yRW);
				} else {
					kernel.IntersectPolynomials(null, fun[0], fun[1]);
				}
			}
		}
		// one line and one conic
		else if (selLines() == 1 && selConics() == 1) {
			GeoConic[] conic = getSelectedConics();
			GeoLine[] line = getSelectedLines();
			if (singlePointWanted)
				kernel.IntersectLineConicSingle(null, line[0], conic[0], xRW,
						yRW);
			else
				kernel.IntersectLineConic(null, line[0], conic[0]);

			return true;
		}
		// line and function
		else if (selLines() == 1 && selFunctions() == 1) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			if (fun[0].isPolynomialFunction(false)) {
				if (singlePointWanted)
					kernel.IntersectPolynomialLineSingle(null, fun[0], line[0],
							xRW, yRW);
				else
					kernel.IntersectPolynomialLine(null, fun[0], line[0]);
			} else {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				kernel.IntersectFunctionLine(null, fun[0], line[0], startPoint);
			}
			return true;
		}
		return false;
	}

	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	final private GeoPoint getSingleIntersectionPoint(ArrayList hits) {
		if (hits == null || hits.size() != 2)
			return null;

		GeoElement a = (GeoElement) hits.get(0);
		GeoElement b = (GeoElement) hits.get(1);

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine())
				if (!((GeoLine) a).linDep((GeoLine) b))
					return kernel.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				else 
					return null;
			else if (b.isGeoConic())
				return kernel.IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, xRW, yRW);
			else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) a,
							startPoint);
				}
			} else
				return null;
		}
		//	first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine())
				return kernel.IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, xRW, yRW);
			else if (b.isGeoConic())
				return kernel.IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, xRW, yRW);
			else
				return null;
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = (GeoFunction) a;
			if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false) && bFun.isPolynomialFunction(false))
					return kernel.IntersectPolynomialsSingle(null, aFun, bFun,
							xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctions(null, aFun, bFun,
							startPoint);
				}
			} else if (b.isGeoLine()) {
				// line and function
				GeoFunction f = (GeoFunction) a;
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) b, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) b,
							startPoint);
				}
			} else
				return null;
		} else
			return null;
	}

	// get point and line or vector;
	// create line through point parallel to line or vector
	final private boolean parallel(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoVector[] vectors = getSelectedVectors();
				// create new line
				kernel.Line(null, points[0], vectors[0]);
				return true;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.Line(null, points[0], lines[0]);
				return true;
			}
		}
		return false;
	}

	// get point and line or vector;
	// create line through point orthogonal to line or vector
	final private boolean orthogonal(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoVector[] vectors = getSelectedVectors();
				// create new line
				kernel.OrthogonalLine(null, points[0], vectors[0]);
				return true;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.OrthogonalLine(null, points[0], lines[0]);
				return true;
			}
		}
		return false;
	}

	// get two points, line segment or conic
	// and create midpoint/center for them/it
	final private boolean midpoint(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		//if (selSegments() == 0)
		//	hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0) {
			addSelectedSegment(hits, 1, false); // segment needed
			if (selSegments() == 0)
				addSelectedConic(hits, 1, false); // conic needed
		}

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Midpoint(null, points[0], points[1]);
			return true;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			kernel.Midpoint(null, segments[0]);
			return true;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConic[] conics = getSelectedConics();
			kernel.Center(null, conics[0]);
			return true;
		}
		return false;
	}

	// get two points and create line bisector for them
	// or get line segment and create line bisector for it
	final private boolean lineBisector(ArrayList hits) {
		if (hits == null)
			return false;
		boolean hitPoint = false;

		if (selSegments() == 0)
			hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0)
			addSelectedSegment(hits, 1, false); // segment needed

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.LineBisector(null, points[0], points[1]);
			return true;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			kernel.LineBisector(null, segments[0]);
			return true;
		}
		return false;
	}

	// get three points and create angular bisector for them
	// or bisector for two lines
	final private boolean angularBisector(ArrayList hits) {
		if (hits == null)
			return false;
		boolean hitPoint = false;

		if (selLines() == 0) {
			hitPoint = (addSelectedPoint(hits, 3, false) != 0);
		}
		if (!hitPoint && selPoints() == 0) {
			addSelectedLine(hits, 2, false);
		}

		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.AngularBisector(null, points[0], points[1], points[2]);
			return true;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLine[] lines = getSelectedLines();
			kernel.AngularBisector(null, lines[0], lines[1]);
			return true;
		}
		return false;
	}

	// get 3 points
	final private boolean threePoints(ArrayList hits, int mode) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 3, false);
		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			switch (mode) {
			case EuclidianView.MODE_CIRCLE_THREE_POINTS:
				kernel.Circle(null, points[0], points[1], points[2]);
				break;
				
			case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
				kernel.CircumcircleArc(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
				kernel.CircumcircleSector(null, points[0], points[1], points[2]);
				break;
				
			case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
				kernel.CircleArc(null, points[0], points[1], points[2]);
				break;
				
			case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
				kernel.CircleSector(null, points[0], points[1], points[2]);
				break;												

			default:
				return false;
			}
			return true;
		}
		return false;
	}
	
	// get 2 lines, 2 vectors or 3 points
	final private boolean angle(ArrayList hits) {
		if (hits == null)
			return false;				
		
		int count = 0;
		if (selPoints() == 0) {
			if (selVectors() == 0)
				count = addSelectedLine(hits, 2, true);
			if (selLines() == 0) 
				count = addSelectedVector(hits, 2, true);			
		}		
		if (count == 0)
			count = addSelectedPoint(hits, 3, false);	
		
		// try polygon too
		boolean polyFound = false;
		if (count == 0)	{		
			polyFound = 1 == addSelectedGeo(view.getHits(hits, GeoPolygon.class, tempArrayList), 
					1, false);
		}
					
		GeoAngle angle = null;
		GeoAngle [] angles = null;
		if (selPoints() == 3) {
			GeoPoint[] points = getSelectedPoints();
			angle = kernel.Angle(null, points[0], points[1], points[2]);					
		} else if (selVectors() == 2) {
			GeoVector[] vecs = getSelectedVectors();
			angle = kernel.Angle(null, vecs[0], vecs[1]);				
		} else if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			angle = createLineAngle(lines);			
		} else if (polyFound && selGeos() == 1) {
			angles = kernel.Angles(null,(GeoPolygon) getSelectedGeos()[0]);	
		}
		
		if (angle != null) {
			// commented in V3.0:
			// angle.setAllowReflexAngle(false);
			angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			angle.setLabelVisible(true);
			angle.updateRepaint();
			return true;
		} 
		else if (angles != null) {
			for (int i=0; i < angles.length; i++) {
				angles[i].setLabelMode(GeoElement.LABEL_NAME_VALUE);
				angles[i].setLabelVisible(true);
				angles[i].updateRepaint();
			}
			return true;
		} else
			return false;
	}
	
	// build angle between two lines
	private GeoAngle createLineAngle(GeoLine [] lines) {
		GeoAngle angle = null;
		
		// did we get two segments?
		if (lines[0] instanceof GeoSegment && 
			lines[1] instanceof GeoSegment) {
			// check if the segments have one point in common
			GeoSegment a = (GeoSegment) lines[0];
			GeoSegment b = (GeoSegment) lines[1];
			// get endpoints
			GeoPoint a1 = a.getStartPoint();
			GeoPoint a2 = a.getEndPoint();
			GeoPoint b1 = b.getStartPoint();
			GeoPoint b2 = b.getEndPoint();
			
			if (a1 == b1) {
				angle = kernel.Angle(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = kernel.Angle(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = kernel.Angle(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = kernel.Angle(null, a1, a2, b1);
			}			
		}
		
		if (angle == null)
			angle = kernel.Angle(null, lines[0], lines[1]);
		
		return angle;
	}
	
	// get 2 points
	final private boolean circle2(ArrayList hits, int mode) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			if (mode == EuclidianView.MODE_SEMICIRCLE)
				kernel.Semicircle(null, points[0], points[1]);
			else
				kernel.Circle(null, points[0], points[1]);
			return true;
		}
		return false;
	}
	
	// get 2 points for locus
	// first point 
	final private boolean locus(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			GeoLocus locus;
			if (points[0].getPath() == null) {
				locus = kernel.Locus(null, points[0], points[1]);
			} else {
				locus = kernel.Locus(null, points[1], points[0]);
			}				
			return locus != null;
		}
		return false;
	}

	// get 5 points
	final private boolean conic5(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 5, false);
		if (selPoints() == 5) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Conic(null, points);
			return true;
		}
		return false;
	}

	// get 2 GeoElements
	final private boolean relation(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedGeo(hits, 2, false);
		if (selGeos() == 2) {
			// fetch the three selected points
			GeoElement[] geos = getSelectedGeos();
			app.showRelation(geos[0], geos[1]);
			return true;
		}
		return false;
	}
	
	// get 2 points, 2 lines or 1 point and 1 line
	final private boolean distance(ArrayList hits, MouseEvent e) {
		if (hits == null)
			return false;
		
		int count = addSelectedPoint(hits, 2, false);
		if (count == 0) {
			addSelectedLine(hits, 2, false);
		}
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}
		if (count == 0) {
			addSelectedPolygon(hits, 2, false);
		}
		if (count == 0) {
			addSelectedSegment(hits, 2, false);
		}			
		
		// TWO POINTS
		if (selPoints() == 2) {			
			// length
			GeoPoint[] points = getSelectedPoints();
			GeoNumeric length = kernel.Distance(null, points[0], points[1]);								
		
			// set startpoint of text to midpoint of two points
			GeoPoint midPoint = kernel.Midpoint(points[0], points[1]);
			createDistanceText(points[0], points[1], midPoint, length);			
		} 
		
		// SEGMENT
		else if (selSegments() == 1) {
			// length
			GeoSegment[] segments = getSelectedSegments();
			
			// length			
			segments[0].setLabelMode(GeoElement.LABEL_NAME_VALUE);
			segments[0].setLabelVisible(true);
			segments[0].updateRepaint();
			return true;
		}
		
		// TWO LINES
		else if (selLines() == 2) {			
			GeoLine[] lines = getSelectedLines();
			kernel.Distance(null, lines[0], lines[1]);
			return true;
		}
		
		// POINT AND LINE
		else if (selPoints() == 1 && selLines() == 1) {	
			GeoPoint[] points = getSelectedPoints();
			GeoLine[] lines = getSelectedLines();
			GeoNumeric length = kernel.Distance(null, points[0], lines[0]);						
			
			// set startpoint of text to midpoint between point and line
			GeoPoint midPoint = kernel.Midpoint(points[0], kernel.ProjectedPoint(points[0], lines[0]));
			createDistanceText(points[0],lines[0], midPoint, length);		
		}
		
		// circumference of CONIC
		else if (selConics() == 1) {			
			GeoConic conic = getSelectedConics()[0];
			if (conic.isGeoConicPart()) {
				// length of arc
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					// conic part
					conic.setLabelMode(GeoElement.LABEL_NAME_VALUE);
					conic.updateRepaint();
					return true;
				}				
			} 
			
			// standard case: conic
			GeoNumeric circumFerence = kernel.Circumference(null, conic);
			
			// text			
			GeoText text = createDynamicText(app.getCommand("Circumference"), circumFerence, e.getPoint());			
			if (conic.isLabelSet()) {
				circumFerence.setLabel(app.getCommand("Circumference").toLowerCase() + conic.getLabel() );							
				text.setLabel(app.getPlain("Text") + conic.getLabel());				
			}			
			return true;
		}
		
		// perimeter of CONIC
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();
			GeoNumeric perimeter = kernel.Perimeter(null, poly[0]);
			
			// text			
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Perimeter"), poly[0]), 
									perimeter, e.getPoint());
			
			if (poly[0].isLabelSet()) {
				perimeter.setLabel(app.getCommand("Perimeter").toLowerCase() + poly[0].getLabel() );							
				text.setLabel(app.getPlain("Text") + poly[0].getLabel());				
			} 
			return true;
		}
		
		return false;
	}	
	
	/**
	 * Creates a text that shows the distance length between geoA and geoB at the given startpoint.
	 */
	private GeoText createDistanceText(GeoElement geoA, GeoElement geoB, 
			GeoPoint startPoint, GeoNumeric length) {
		// create text that shows length
		try {				
			String strText = "";
			boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
			if (useLabels) {		
				length.setLabel(app.getCommand("Distance").toLowerCase() + geoA.getLabel() + geoB.getLabel());
				strText = "\"\\overline{\" + Name["+ geoA.getLabel() 
							+ "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
							+ length.getLabel();			
				geoA.setLabelVisible(true);				
				geoB.setLabelVisible(true);
				geoA.updateRepaint();
				geoB.updateRepaint();
			}
			else {
				length.setLabel(app.getCommand("Distance").toLowerCase());					
				strText = "\"\"" + length.getLabel();
			}
							
			// create dynamic text
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText, true);
			if (useLabels) {
				text.setLabel(app.getPlain("Text") + geoA.getLabel() + geoB.getLabel() );	
				text.setLaTeX(useLabels, true);
			}			
							
			text.setStartPoint(startPoint);
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * Creates a text that shows a number value of geo at the current mouse position.
	 */
	private GeoText createDynamicText(String descText, GeoElement value, Point loc) {
		// create text that shows length
		try {
			// create dynamic text
			String dynText = "\"" + descText + " = \" + " + value.getLabel();
			
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(dynText, true);									
			text.setAbsoluteScreenLocActive(true);
			text.setAbsoluteScreenLoc(loc.x, loc.y);			
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	private boolean area(ArrayList hits,  MouseEvent e) {
		if (hits == null)
			return false;
		
		int count = addSelectedPolygon(hits, 1, false);
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}				
		
		// area of CONIC
		if (selConics() == 1) {			
			GeoConic conic = getSelectedConics()[0];			
			
			//  check if arc
			if (conic.isGeoConicPart()) {				
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					clearSelections();
					return false;
				}				
			} 
			
			// standard case: conic
			GeoNumeric area = kernel.Area(null, conic);
			
			// text			
			GeoText text = createDynamicText(app.getCommand("Area"), area, e.getPoint());			
			if (conic.isLabelSet()) {				
				area.setLabel(app.getCommand("Area").toLowerCase() + conic.getLabel());							
				text.setLabel(app.getPlain("Text") + conic.getLabel());				
			}			
			return true;
		}
		
		// area of polygon
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();	
					
									
			// dynamic text with polygon's area
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Area"), poly[0]), poly[0], e.getPoint());			
			if (poly[0].isLabelSet()) {					
				text.setLabel(app.getPlain("Text") + poly[0].getLabel());				
			} 
			return true;
		}
		
		return false;
	}
	
	private String descriptionPoints(String prefix, GeoPolygon poly) {
		// build description text including point labels	
		String descText = prefix;
		
		// use points for polygon with static points (i.e. no list of points)
		GeoPoint [] points = null;
		if (poly.getParentAlgorithm() instanceof AlgoPolygon) {
			points = ((AlgoPolygon) poly.getParentAlgorithm()).getPoints();
		}	
		
		if (points != null) {
			descText = descText + " \"";
			boolean allLabelsSet = true;
			for (int i=0; i < points.length; i++) {
				if (points[i].isLabelSet()) 
					descText = descText + " + Name[" + points[i].getLabel() + "]";
				else {
					allLabelsSet = false;
					i = points.length;
				}
			}
			
			if (allLabelsSet) {
				descText = descText + " + \"";
				for (int i=0; i < points.length; i++) {
					points[i].setLabelVisible(true);
					points[i].updateRepaint();
				}
			} else
				descText = app.getCommand("Area");
		}
		return descText;
	}
	
	private boolean slope(ArrayList hits) {
		if (hits == null)
			return false;
		
		addSelectedLine(hits, 1, false);			
		
		if (selLines() == 1) {			
			GeoLine line = getSelectedLines()[0];						
									
			String strLocale = app.getLocale().toString();
			if (strLocale.equals("de_AT")) {
				kernel.Slope("k", line);
			} else {
				kernel.Slope("m", line);
			}			
			return true;
		}
		return false;
	}
	
	private boolean regularPolygon(ArrayList hits) {
		// TODO: implement regularPolygon()
		return false;
	}
	
	private boolean showCheckBox(ArrayList hits) {
		// TODO: implement showCheckBox()
		return false;
	}

	// get (point or line) and (conic or function or curve)
	final private boolean tangents(ArrayList hits) {
		if (hits == null)
			return false;
		
		boolean found=false;
		found = addSelectedConic(hits, 1, false) != 0;
		if (!found)
			found = addSelectedFunction(hits, 1, false) != 0;
		if (!found)
			found = addSelectedCurve(hits, 1, false) != 0;		
		
		if (!found) {
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selConics() == 1) {
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				kernel.Tangent(null, points[0], conics[0]);
				return true;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.Tangent(null, lines[0], conics[0]);
				return true;
			}
		} 
		else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				kernel.Tangent(null, points[0], functions[0]);
				return true;
			}
		}
		else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPoint [] points = getSelectedPoints();
				// create new tangents
				kernel.Tangent(null, points[0], curves[0]);
				return true;
			}
		}
		return false;
	}
	
	// get (point or line or vector) and conic
	final private boolean polarLine(ArrayList hits) {
		if (hits == null)
			return false;
		boolean hitConic = false;

		hitConic = (addSelectedConic(hits, 1, false) != 0);
	
		if (!hitConic ) {
			if (selVectors() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false);
			}			
		}

		if (selConics() == 1) {
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				kernel.PolarLine(null, points[0], conics[0]);
				return true;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.DiameterLine(null, lines[0], conics[0]);
				return true;
			}  else if (selVectors() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoVector[] vecs = getSelectedVectors();
				// create new line
				kernel.DiameterLine(null, vecs[0], conics[0]);
				return true;
			}
		}
		return false;
	}

	final private boolean delete(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			// delete this object
			GeoElement[] geos = getSelectedGeos();
			geos[0].remove();
			return true;
		}
		return false;
	}
	
	final private boolean showHideObject(ArrayList hits) {
		if (hits == null)
			return false;
		
		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}
				
		GeoElement geo = chooseGeo(hits);
		if (geo != null) {
			// hide axis
			if (geo instanceof GeoAxis)	{
				switch (((GeoAxis) geo).getType()) {
					case GeoAxis.X_AXIS:
						view.showAxes(false, view.getShowYaxis());
						break;
						
					case GeoAxis.Y_AXIS:
						view.showAxes(view.getShowXaxis(), false);
						break;
				}				
				app.updateMenubar();
			} else {
				app.toggleSelectedGeo(geo);
			}
			return true;
		}						
		return false;
	}
	
	final private boolean showHideLabel(ArrayList hits) {
		if (hits == null)
			return false;
		
		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}
				
		GeoElement geo = chooseGeo(view.getOtherHits(hits, GeoAxis.class, tempArrayList));
		if (geo != null) {			
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateRepaint();
			return true;
		}						
		return false;
	}
	
	final private boolean copyVisualStyle(ArrayList hits) {
		if (hits == null)
			return false;
		
		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}
				
		GeoElement geo = chooseGeo(view.getOtherHits(hits, GeoAxis.class, tempArrayList));
		if (geo == null) return false;
		
		// movedGeoElement is the active geo
		if (movedGeoElement == null) {
			movedGeoElement = geo;
			app.addSelectedGeo(geo);
		} else {
			if (geo == movedGeoElement) {
				// deselect
				app.removeSelectedGeo(geo);
				movedGeoElement = null;
				if (toggleModeChangedKernel)
					app.storeUndoInfo();
				toggleModeChangedKernel = false;
			} else {
				// standard case: copy visual properties
				geo.setVisualStyle(movedGeoElement);
				geo.updateRepaint();
				return true;
			}
		}					
		return false;
	}
	
	
	// get mirrorable and point
	final private boolean mirrorAtPoint(ArrayList hits) {
		if (hits == null)
			return false;

		// mirrorable		
		ArrayList mirAbles = view.getHits(hits, Mirrorable.class, tempArrayList);
		int	count = addSelectedGeo(mirAbles, 1, false);
		
		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}
		
		// point = mirror
		if (count == 0) {
			count = addSelectedPoint(hits, 1, false);
		}					
		
		// we got the mirror point
		if (selPoints() == 1) {							
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				kernel.Mirror(null,  polys[0], points[0]);
			} else {
				Mirrorable mirAble = (Mirrorable) getFirstSelectedInstance(Mirrorable.class);
				if (mirAble == null) return false;				
				GeoPoint[] points = getSelectedPoints();
				kernel.Mirror(null,  mirAble, points[0]);
			}			
			return true;
		}
		return false;
	}
	
	// get mirrorable and line
	final private boolean mirrorAtLine(ArrayList hits) {
		if (hits == null)
			return false;

		// mirrorable		
		ArrayList mirAbles = view.getHits(hits, Mirrorable.class, tempArrayList);
		int count =addSelectedGeo(mirAbles, 1, false);
		
		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}
		
		// line = mirror
		if (count == 0) {
			addSelectedLine(hits, 1, false);
		}					
		
		// we got the mirror point
		if (selLines() == 1) {	
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoLine[] lines = getSelectedLines();	
				kernel.Mirror(null,  polys[0], lines[0]);
			} else {
				Mirrorable mirAble = (Mirrorable) getFirstSelectedInstance(Mirrorable.class);
				if (mirAble == null) return false;
				GeoLine[] lines = getSelectedLines();		
				kernel.Mirror(null, mirAble, lines[0]);
			}
			return true;
		}
		return false;
	}
	
	// get translateable and vector
	final private boolean translateByVector(ArrayList hits) {
		if (hits == null)
			return false;

		// translateable		
		ArrayList transAbles = view.getHits(hits, Translateable.class, tempArrayList);
		int count = addSelectedGeo(transAbles, 1, false);
		
		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}	
		
		if (selGeos() == 1) {
			// translation vector
			if (count == 0) {
				addSelectedVector(hits, 1, false);
			}
		}
		
		// we got the mirror point
		if (selVectors() == 1) {		
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoVector[] vecs = getSelectedVectors();	
				kernel.Translate(null,  polys[0], vecs[0]);
			} else {
				Translateable transAble = (Translateable) getFirstSelectedInstance(Translateable.class);
				if (transAble == null) return false;
				GeoVector[] vecs = getSelectedVectors();			
				kernel.Translate(null, transAble, vecs[0]);
			}
			return true;
		}
		return false;
	}
	
	// get rotateable object, point and angle
	final private boolean rotateByAngle(ArrayList hits) {
		if (hits == null)
			return false;

		// translateable
		ArrayList rotAbles = view.getHits(hits, PointRotateable.class, tempArrayList);
		int count = addSelectedGeo(rotAbles, 1, false);
		
//		 polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}
		
		// rotation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}
		
		// we got the rotation center point
		if (selPoints() == 1) {					
			Object [] ob = app.showAngleInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
														app.getPlain("Angle"), "45\u00b0");
			NumberValue num = (NumberValue) ob[0];											
			
			if (num == null) {
				view.resetMode();
				return false;
			}
			
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				kernel.Rotate(null,  polys[0], num, points[0]);
			} else {
				PointRotateable rotAble = (PointRotateable) getFirstSelectedInstance(PointRotateable.class);
				if (rotAble == null) return false;
				GeoPoint[] points = getSelectedPoints();		
				kernel.Rotate(null, rotAble, num, points[0]);
			}
			return true;
		}
		return false;
	}		
	
	// get dilateable object, point and number
	final private boolean dilateFromPoint(ArrayList hits) {
		if (hits == null)
			return false;

		// dilateable
		ArrayList dilAbles = view.getHits(hits, Dilateable.class, tempArrayList);
		int count =	addSelectedGeo(dilAbles, 1, false);
		
//		 polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}
		
		// dilation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}
		
		// we got the mirror point
		if (selPoints() == 1) {		
			NumberValue num = app.showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
														app.getPlain("Numeric"), null);			
			if (num == null) {
				view.resetMode();
				return false;
			}
			
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				kernel.Dilate(null,  polys[0], num, points[0]);
			} else {
				Dilateable dilAble = (Dilateable) getFirstSelectedInstance(Dilateable.class);
				if (dilAble == null) return false;
				GeoPoint[] points = getSelectedPoints();		
				kernel.Dilate(null, dilAble, num, points[0]);
			}
			return true;
		}
		return false;
	}
	
	// get point and number
	final private boolean segmentFixed(ArrayList hits) {
		if (hits == null)
			return false;
		
		// dilation center
		addSelectedPoint(hits, 1, false);
		
		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			NumberValue num = app.showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
														app.getPlain("Length"), null);		
			
			if (num == null) {
				view.resetMode();
				return false;
			}
										
			GeoPoint[] points = getSelectedPoints();		
			kernel.Segment(null, points[0], num);
			return true;
		}
		return false;
	}	
	
	// get two points and number
	final private boolean angleFixed(ArrayList hits) {
		if (hits == null)
			return false;
						
		// dilation center
		int count = addSelectedPoint(hits, 2, false);
		
		if (count == 0) {
			addSelectedSegment(hits, 1, false);
		}				
		
		// we got the points		
		if (selPoints() == 2 || selSegments() == 1) {
			// get angle			
			Object [] ob = app.showAngleInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
														app.getPlain("Angle"), "45\u00b0");
			NumberValue num = (NumberValue) ob[0];
			AngleInputDialog aDialog = (AngleInputDialog) ob[1]; 			
			
			if (num == null) {
				view.resetMode();
				return false;
			}
						
			GeoAngle angle;
			boolean posOrientation = aDialog.isCounterClockWise();
			if (selPoints() == 2) {
				GeoPoint[] points = getSelectedPoints();		
				angle = (GeoAngle) kernel.Angle(null, points[0], points[1], num, posOrientation)[0];			
			} else {
				GeoSegment[] segment = getSelectedSegments();		
				angle = (GeoAngle) kernel.Angle(null, segment[0].getEndPoint(), segment[0].getStartPoint(), num, posOrientation)[0];
			}			
			angle.updateRepaint();					
			return true;
		}
		return false;
	}	
		
	// get center point and number
	final private boolean circlePointRadius(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedPoint(hits, 1, false);		
		
		// we got the center point
		if (selPoints() == 1) {	
			NumberValue num = app.showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
														app.getPlain("Radius"), null);

			if (num == null) {
				view.resetMode();
				return false;
			}

			GeoPoint[] points = getSelectedPoints();	
						
			kernel.Circle(null, points[0], num);
			return true;
		}
		return false;
	}	
	
	// get point and vector
	final private boolean vectorFromPoint(ArrayList hits) {
		if (hits == null)
			return false;

		// point	
		int count = addSelectedPoint(hits, 1, false);
			
		// vector
		if (count == 0) {
			addSelectedVector(hits, 1, false);
		}
		
		if (selPoints() == 1 && selVectors() == 1) {			
			GeoVector[] vecs = getSelectedVectors();			
			GeoPoint[] points = getSelectedPoints();
			GeoPoint endPoint = (GeoPoint) kernel.Translate(null, points[0], vecs[0])[0];
			kernel.Vector(null, points[0], endPoint);
			return true;
		}
		return false;
	}
	
	/**
	 * Handles selected objects for a macro
	 * @param hits
	 * @return
	 */
	final private boolean macro(ArrayList hits) {		
		// try to get next needed type of macroInput
		int index = selGeos();
		
		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == 
			handleAddSelected(hits, macroInput.length, false, selectedGeos, macroInput[index]);			
		
		/*
		// POLYGON instead of points special case:
		// if no object was found maybe we need points
		// in this case let's try to use a polygon's points 
		int neededPoints = 0;				
		if (!objectFound) {
			// how many points do we need?						
			for (int k = index; k < macroInput.length; k++) {
				if (macroInput[k] == GeoPoint.class) 
					++neededPoints;					
				else 
					break;				
			}
						
			// several points needed: look for polygons with this number of points
			if (neededPoints > 2) {				
				if (macroPolySearchList == null)
					macroPolySearchList = new ArrayList();
				// get polygons with needed number of points
				view.getPolygons(hits, neededPoints, macroPolySearchList);
											
				if (selectionPreview) {
					addToHighlightedList(selectedGeos, macroPolySearchList , macroInput.length);
					return false;
				}
					
				// now we only have polygons with the right number of points: choose one 
				GeoPolygon poly = (GeoPolygon) chooseGeo(macroPolySearchList);
				if (poly != null) {					
					// success: let's take the points from the polygon
					GeoPoint [] points = poly.getPoints();					
					for (int k=0; k < neededPoints; k++) {
						selectedGeos.add(points[k]);
						app.toggleSelectedGeo(points[k]);
					}										
					index = index + neededPoints - 1;	
					objectFound = true;
				}
			}									
		}		
		*/
		
		// we're done if in selection preview
		if (selectionPreview) 
			return false; 	
		
		
		// only one point needed: try to create it
		if (!objectFound && macroInput[index] == GeoPoint.class) {
			if (createNewPoint(hits, true, true, false)) {				
				// take movedGeoPoint which is the newly created point								
				selectedGeos.add(movedGeoPoint);
				app.addSelectedGeo(movedGeoPoint);
				objectFound = true;
				POINT_CREATED = false;
			}
		}
				
		// object found in handleAddSelected()
		if (objectFound) { 
			// look ahead if we need a number or an angle next			
			while (++index < macroInput.length) {				
				// maybe we need a number
				if (macroInput[index] == GeoNumeric.class) {									
					NumberValue num = app.showNumberInputDialog(macro.getToolOrCommandName(),
													app.getPlain("Numeric" ), null);									
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our number
						selectedGeos.add(num);
					}
				}	
				
				// maybe we need an angle
				else if (macroInput[index] == GeoAngle.class) {									
					Object [] ob = app.showAngleInputDialog(macro.getToolOrCommandName(),
										app.getPlain("Angle"), "45\u00b0");
					NumberValue num = (NumberValue) ob[0];						
					
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our angle
						selectedGeos.add(num);
					}
				}	
				
				else // other type needed, so leave loop 
					break;				
			}			
		}
		
								
		// TODO: remove
		//System.out.println("index: " + index + ", needed type: " + macroInput[index]);
		
		// do we have everything we need?
		if (selGeos() == macroInput.length) {						
			kernel.useMacro(null, macro, getSelectedGeos())	;		
			return true;
		} 		
		return false;
	}
			
	final private boolean geoElementSelected(ArrayList hits, boolean addToSelection) {
		if (hits == null)
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			GeoElement[] geos = getSelectedGeos();			
			app.geoElementSelected(geos[0], addToSelection);
		}
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final private boolean move(ArrayList hits) {		
		addSelectedGeo(view.getMoveableHits(hits), 1, false);		
		return false;
	}
	
	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final private boolean moveRotate(ArrayList hits) {				
		addSelectedGeo(view.getPointRotateableHits(hits, rotationCenter), 1, false);
		return false;
	}
	
	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final private boolean point(ArrayList hits) {
		addSelectedGeo(view.getHits(hits, Path.class, tempArrayList), 1, false);
		return false;
	}

	final private boolean textImage(ArrayList hits, int mode) {
		GeoPoint loc = null; // location

		if (hits == null) {
			if (selectionPreview)
				return false;
			else {
				// create new Point
				loc = new GeoPoint(kernel.getConstruction());			
				loc.setCoords(xRW, yRW, 1.0);	
			}
		} else {
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() == 1) {
				// fetch the selected point
				GeoPoint[] points = getSelectedPoints();
				loc = points[0];
			}
		}		

		// got location
		if (loc != null) {
			switch (mode) {
				case EuclidianView.MODE_TEXT:				
					app.showTextCreationDialog(loc);
					break;
				
				case EuclidianView.MODE_IMAGE:				
					app.showImageCreationDialog(loc);
					break;
			}			
			return true;
		}

		return false;
	}
	

	// new slider
	final private boolean slider() {		
		return !selectionPreview && mouseLoc != null && app.showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
	}		

	/***************************************************************************
	 * helper functions for selection sets
	 **************************************************************************/

	/*
	final private boolean isSelected(GeoElement geo) {
		return selectedGeos.contains(geo);
	}*/
	
	final private GeoElement getFirstSelectedInstance(Class myclass) {
		Iterator it = selectedGeos.iterator();		
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (myclass.isInstance(geo))
				return geo;
		}
		return null;
	}

	final private GeoElement[] getSelectedGeos() {
		GeoElement[] ret = new GeoElement[selectedGeos.size()];
		int i = 0;
		Iterator it = selectedGeos.iterator();
		while (it.hasNext()) {
			ret[i] = (GeoElement) it.next();
			i++;
		}
		clearSelection(selectedGeos);
		return ret;
	}	

	final private GeoPoint[] getSelectedPoints() {				
		GeoPoint[] ret = new GeoPoint[selectedPoints.size()];
		for (int i = 0; i < selectedPoints.size(); i++) {		
			ret[i] = (GeoPoint) selectedPoints.get(i);
		}
		clearSelection(selectedPoints);
		return ret;
	}
	
	final private GeoPolygon[] getSelectedPolygons() {				
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons.size()];
		for (int i = 0; i < selectedPolygons.size(); i++) {		
			ret[i] = (GeoPolygon) selectedPolygons.get(i);
		}
		clearSelection(selectedPolygons);
		return ret;
	}

	final private GeoLine[] getSelectedLines() {
		GeoLine[] lines = new GeoLine[selectedLines.size()];
		int i = 0;
		Iterator it = selectedLines.iterator();
		while (it.hasNext()) {
			lines[i] = (GeoLine) it.next();
			i++;
		}
		clearSelection(selectedLines);
		return lines;
	}

	final private GeoSegment[] getSelectedSegments() {
		GeoSegment[] segments = new GeoSegment[selectedSegments.size()];
		int i = 0;
		Iterator it = selectedSegments.iterator();
		while (it.hasNext()) {
			segments[i] = (GeoSegment) it.next();
			i++;
		}
		clearSelection(selectedSegments);
		return segments;
	}

	final private GeoVector[] getSelectedVectors() {
		GeoVector[] vectors = new GeoVector[selectedVectors.size()];
		int i = 0;
		Iterator it = selectedVectors.iterator();
		while (it.hasNext()) {
			vectors[i] = (GeoVector) it.next();
			i++;
		}
		clearSelection(selectedVectors);
		return vectors;
	}

	final private GeoConic[] getSelectedConics() {
		GeoConic[] conics = new GeoConic[selectedConics.size()];
		int i = 0;
		Iterator it = selectedConics.iterator();
		while (it.hasNext()) {
			conics[i] = (GeoConic) it.next();
			i++;
		}
		clearSelection(selectedConics);
		return conics;
	}

	final private GeoFunction[] getSelectedFunctions() {
		GeoFunction[] functions = new GeoFunction[selectedFunctions.size()];
		int i = 0;
		Iterator it = selectedFunctions.iterator();
		while (it.hasNext()) {
			functions[i] = (GeoFunction) it.next();
			i++;
		}
		clearSelection(selectedFunctions);
		return functions;
	}


	final private GeoCurveCartesian [] getSelectedCurves() {
		GeoCurveCartesian [] curves = new GeoCurveCartesian[selectedCurves.size()];
		int i = 0;
		Iterator it = selectedCurves.iterator();
		while (it.hasNext()) {
			curves[i] = (GeoCurveCartesian) it.next();
			i++;
		}
		clearSelection(selectedCurves);
		return curves;
	}	

	final private void clearSelection(ArrayList selectionList) {
		// unselect
		selectionList.clear();
		selectedGeos.clear();
		app.clearSelectedGeos();	
		view.repaint();
	}

	final private int addSelectedGeo(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedGeos, GeoElement.class);
	}		
	
	private int handleAddSelected(ArrayList hits, int max, boolean addMore, ArrayList list, Class geoClass) {		
		if (selectionPreview)
			return addToHighlightedList(list, view.getHits(hits, geoClass, handleAddSelectedArrayList) , max);
		else
			return addToSelectionList(list, view.getHits(hits, geoClass, handleAddSelectedArrayList), max, addMore);
	}
	private ArrayList handleAddSelectedArrayList = new ArrayList();

	final private int addSelectedPoint(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPoints, GeoPoint.class);
	}

	final private int addSelectedLine(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLines, GeoLine.class);
	}

	final private int addSelectedSegment(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedSegments, GeoSegment.class);
	}

	final private int addSelectedVector(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedVectors, GeoVector.class);
	}

	final private int addSelectedConic(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedConics, GeoConic.class);
	}

	final private int addSelectedFunction(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedFunctions, GeoFunction.class);
	}
	
	final private int addSelectedCurve(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCurves, GeoCurveCartesian.class);
	}
	
	final private int addSelectedPolygon(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolygons, GeoPolygon.class);
	}

	final int selGeos() {
		return selectedGeos.size();
	}

	final int selPoints() {
		return selectedPoints.size();
	}
	
	final int selPolygons() {
		return selectedPolygons.size();
	}

	final int selLines() {
		return selectedLines.size();
	}

	final int selSegments() {
		return selectedSegments.size();
	}

	final int selVectors() {
		return selectedVectors.size();
	}

	final int selConics() {
		return selectedConics.size();
	}

	final int selFunctions() {
		return selectedFunctions.size();
	}
	
	final int selCurves() {
		return selectedCurves.size();
	}

	// selectionList may only contain max objects
	// a choose dialog will be shown if not all objects can be added
	// @param addMoreThanOneAllowed: it's possible to add several objects
	// without choosing
	final private int addToSelectionList(ArrayList selectionList,
			ArrayList geos, int max, boolean addMoreThanOneAllowed) {
		if (geos == null)
			return 0;
		//GeoElement geo;

		// ONLY ONE ELEMENT
		if (geos.size() == 1)
			return addToSelectionList(selectionList, (GeoElement) geos.get(0), max);

		//	SEVERAL ELEMENTS
		// here nothing should be removed
		//  too many objects -> choose one
		if (!addMoreThanOneAllowed || geos.size() + selectionList.size() > max)
			return addToSelectionList(selectionList, chooseGeo(geos), max);

		// already selected objects -> choose one
		boolean contained = false;
		for (int i = 0; i < geos.size(); i++) {
			if (selectionList.contains(geos.get(i)))
				contained = true;
		}
		if (contained)
			return addToSelectionList(selectionList, chooseGeo(geos), max);

		// add all objects to list
		int count = 0;
		for (int i = 0; i < geos.size(); i++) {
			count += addToSelectionList(selectionList, (GeoElement) geos.get(i), max);
		}
		return count;
	}

	//	selectionList may only contain max objects
	// an already selected objects is deselected
	final private int addToSelectionList(ArrayList selectionList,
			GeoElement geo, int max) {
		if (geo == null)
			return 0;
		
		int ret = 0;
		if (selectionList.contains(geo)) { // remove from selection
			selectionList.remove(geo);
			if (selectionList != selectedGeos)
				selectedGeos.remove(geo);
			ret =  -1;
		} else { // new element: add to selection
			if (selectionList.size() < max) {
				selectionList.add(geo);
				if (selectionList != selectedGeos)
					selectedGeos.add(geo);
				ret = 1;
			} 
		}
		if (ret != 0) app.toggleSelectedGeo(geo);
		return ret;
	}

	// selectionList may only contain max objects
	final private int addToHighlightedList(ArrayList selectionList,
			ArrayList geos, int max) {
		if (geos == null)
			return 0;

		Object geo;
		int ret = 0;
		for (int i = 0; i < geos.size(); i++) {
			geo = geos.get(i);
			if (selectionList.contains(geo)) {
				ret = (ret == 1) ? 1 : -1;
			} else {
				if (selectionList.size() < max) {
					highlightedGeos.add(geo); // add hit
					ret = 1;
				}
			}
		}
		return ret;
	}

	/*
	//	show dialog to choose one object out of hits[] that is an instance of
	// specified class
	// (note: subclasses are included)
	final private GeoElement chooseGeo(ArrayList hits, Class geoclass) {
		return chooseGeo(view.getHits(hits, geoclass, tempArrayList));
	}*/

	final private GeoElement chooseGeo(ArrayList geos) {
		if (geos == null)
		return null;

		GeoElement ret = null;
		switch (geos.size()) {
		case 0:
			ret =  null;
			break;

		case 1:
			ret =  (GeoElement) geos.get(0);
			break;

		default:		
			ToolTipManager ttm = ToolTipManager.sharedInstance();		
			ttm.setEnabled(false);			
			ListDialog dialog = new ListDialog(view, geos, null);
			ret = dialog.showDialog(view, mouseLoc);			
			ttm.setEnabled(true);				
		}				
		return ret;	
	}

	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		view.updateSize();
	}
	
	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	/**
	 * Zooms in or out using mouse wheel
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
			setMouseLocation(e);
			
			//double px = view.width / 2d;
			//double py = view.height / 2d;
			double px = mouseLoc.x;
			double py = mouseLoc.y;
			double dx = view.xZero - px;
			double dy = view.yZero - py;
			
	        double factor = (e.getWheelRotation() > 0) ?
	        		EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR :
	        		1d / 	EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;
			
	        // make zooming a little bit smoother by having some steps
	        	       
			view.setAnimatedCoordSystem(
		                px + dx * factor,
		                py + dy * factor,
		                view.xscale * factor, 4, false);
						//view.yscale * factor);
			app.setUnsaved();
				
	}
}
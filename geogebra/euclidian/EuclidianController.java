/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.Matrix.Coords;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.AlgoDynamicCoordinates;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoImplicitPoly;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolyLine;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Path;
import geogebra.kernel.PointProperties;
import geogebra.kernel.PointRotateable;
import geogebra.kernel.Region;
import geogebra.kernel.Transformable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;

/**
 * EuclidianController.java
 *
 * Created on 16. October 2001, 15:41
 */
public class EuclidianController implements MouseListener,
MouseMotionListener, MouseWheelListener, ComponentListener, PropertiesPanelMiniListener {

	private int mx, my; // keep track of mouse position
	
	protected static final int MOVE_NONE = 101;

	protected static final int MOVE_POINT = 102;

	protected static final int MOVE_LINE = 103;

	protected static final int MOVE_CONIC = 104;

	protected static final int MOVE_VECTOR = 105;

	protected static final int MOVE_VECTOR_STARTPOINT = 205;

	public static final int MOVE_VIEW = 106;

	protected static final int MOVE_FUNCTION = 107;

	protected static final int MOVE_LABEL = 108;

	protected static final int MOVE_TEXT = 109;

	protected static final int MOVE_NUMERIC = 110; // for number on slider

	protected static final int MOVE_SLIDER = 111; // for slider itself

	protected static final int MOVE_IMAGE = 112;

	protected static final int MOVE_ROTATE = 113;

	protected static final int MOVE_DEPENDENT = 114;

	protected static final int MOVE_MULTIPLE_OBJECTS = 115; // for multiple objects

	protected static final int MOVE_X_AXIS = 116;
	protected static final int MOVE_Y_AXIS = 117;

	protected static final int MOVE_BOOLEAN = 118; // for checkbox moving
	protected static final int MOVE_BUTTON = 119; 

	public static final int MOVE_ROTATE_VIEW = 120; // for 3D 

	protected Application app;

	protected Kernel kernel;

	protected EuclidianViewInterface view;
	//protected EuclidianView view;

	protected Point startLoc, mouseLoc, lastMouseLoc; // current mouse location

	//protected double xZeroOld, yZeroOld;
	protected double xTemp, yTemp;

	protected Point oldLoc = new Point();

	protected double xRW;

	protected double yRW;

	double xRWold = Double.NEGATIVE_INFINITY, yRWold = xRWold, temp;

	// for moving conics:
	protected Point2D.Double startPoint = new Point2D.Double();

	protected boolean useLineEndPoint = false;
	protected Point2D.Double lineEndPoint = null;

	protected Point selectionStartPoint = new Point();

	protected GeoConic tempConic;

	protected GeoFunction tempFunction;

	// protected GeoVec2D b;

	protected GeoPointND movedGeoPoint;
	protected boolean movedGeoPointDragged = false;

	protected GeoLine movedGeoLine;

	//protected GeoSegment movedGeoSegment;

	protected GeoConic movedGeoConic;

	protected GeoVector movedGeoVector;

	protected GeoText movedGeoText;

	protected GeoImage oldImage, movedGeoImage;	

	protected GeoFunction movedGeoFunction;

	protected GeoNumeric movedGeoNumeric;
	protected boolean movedGeoNumericDragged = false;

	protected GeoBoolean movedGeoBoolean;

	protected GeoButton movedGeoButton;

	protected GeoElement movedLabelGeoElement;

	protected GeoElement movedGeoElement;	

	protected GeoElement rotGeoElement, rotStartGeo;
	protected GeoPoint rotationCenter;
	public GeoElement recordObject;
	protected MyDouble tempNum;
	protected double rotStartAngle;
	protected ArrayList translateableGeos;
	protected Coords translationVec;

	protected Hits tempArrayList = new Hits();
	protected Hits tempArrayList2 = new Hits();
	protected Hits tempArrayList3 = new Hits();
	protected ArrayList selectedPoints = new ArrayList();

	protected ArrayList selectedNumbers = new ArrayList();
	protected ArrayList selectedNumberValues = new ArrayList();

	protected ArrayList selectedLines = new ArrayList();

	protected ArrayList selectedSegments = new ArrayList();

	protected ArrayList selectedRegions = new ArrayList();
	protected ArrayList selectedPaths = new ArrayList();
	protected ArrayList selectedConics = new ArrayList();
	protected ArrayList selectedImplicitpoly = new ArrayList();

	protected ArrayList selectedFunctions = new ArrayList();
	protected ArrayList selectedCurves = new ArrayList();

	protected ArrayList selectedVectors = new ArrayList();

	protected ArrayList selectedPolygons = new ArrayList();

	protected ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	protected ArrayList selectedLists = new ArrayList();

	//protected LinkedList highlightedGeos = new LinkedList();
	protected Hits highlightedGeos = new Hits();

	protected boolean selectionPreview = false;

	protected boolean TEMPORARY_MODE = false; // changed from QUICK_TRANSLATEVIEW Michael Borcherds 2007-10-08

	protected boolean DONT_CLEAR_SELECTION = false; // Michael Borcherds 2007-12-08

	protected boolean DRAGGING_OCCURED = false; // for moving objects

	protected boolean POINT_CREATED = false;

	protected boolean moveModeSelectionHandled;

	//protected MyPopupMenu popupMenu;

	protected int mode, oldMode, moveMode = MOVE_NONE;
	protected Macro macro;
	protected Class [] macroInput;

	protected int DEFAULT_INITIAL_DELAY;

	protected boolean toggleModeChangedKernel = false;

	boolean altDown=false;
	
	boolean polygonRigid = false;

	// used for gridlock when dragging polygons, segments etc
	double[] transformCoordsOffset = new double[2];

	
	
	
	/*********************************************** 
	 * Creates new EuclidianController 
	 **********************************************/
	public EuclidianController(Kernel kernel) {
		setKernel(kernel);
		setApplication(kernel.getApplication());

		// for tooltip manager
		DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance()
		.getInitialDelay();

		tempNum = new MyDouble(kernel);
	}


	public void setApplication(Application app) {
		this.app=app;
	}

	public Application getApplication() {
		return app;
	}

	public void setKernel(Kernel kernel) {
		this.kernel=kernel;
	}

	public Kernel getKernel() {
		return kernel;
	}

	protected void setView(EuclidianViewInterface view) {
		//void setView(EuclidianView view) {
		this.view = view;
	}

	
	
	//==============================================
	//  Pen 
	
	private EuclidianPen pen;
	public void setPen(EuclidianPen pen){
		this.pen = pen;
	}
	public EuclidianPen getPen(){
		return pen;
	}
	
	
	
	public int getMode(){
		return mode;
	}

	public void setMode(int newMode) {

		if(newMode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS
				|| newMode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				|| newMode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS
				|| newMode == EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST)
			return;


		endOfMode(mode);

		if (EuclidianView.usesSelectionRectangleAsInput(newMode) && view.getSelectionRectangle() != null)
		{
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view)
				processSelectionRectangle(null);
		}
		else if (EuclidianView.usesSelectionAsInput(newMode))
		{
			initNewMode(newMode);
			if (app.getActiveEuclidianView() == view)
				processSelection();
		}
		else
		{
			if (!TEMPORARY_MODE) app.clearSelectedGeos(false);
			initNewMode(newMode);
		}

		kernel.notifyRepaint();
	}

	protected void endOfMode(int mode) {
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
			
		case EuclidianView.MODE_PEN:
			pen.resetPenOffsets();
			
			view.setSelectionRectangle(null);
			break;
		}

		if (recordObject != null) {
			recordObject.setSelected(false);
			app.getGuiManager().removeSpreadsheetTrace(recordObject);
			recordObject = null;
		}
		
		if (toggleModeChangedKernel)
			app.storeUndoInfo();
	}
	
	
	
	
	protected void initNewMode(int mode) {
		this.mode = mode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		//clearSelections();
		if (!TEMPORARY_MODE && !EuclidianView.usesSelectionRectangleAsInput(mode)) clearSelections();
		//		 Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;
		
		closeMiniPropertiesPanel();



		view.setPreview(switchPreviewableForInitNewMode(mode));	
		toggleModeChangedKernel = false;
	}	
	
	
	protected Previewable switchPreviewableForInitNewMode(int mode){
		
		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode) {
		
		case EuclidianView.MODE_PEN:
			
			app.zoomAxesRatio(1);
			
			ArrayList selection = app.getSelectedGeos();
			if (selection.size() == 1) {
				GeoElement geo = (GeoElement)selection.get(0);
				// getCorner(1) == null as we can't write to transformed images
				if (geo.isGeoImage()) {
					GeoPoint c1 = ((GeoImage)geo).getCorner(0);
					GeoPoint c2 = ((GeoImage)geo).getCorner(1);
					GeoPoint c3 = ((GeoImage)geo).getCorner(2);
								
					if (c3 == null &&
							(c2 == null // c2 = null -> not transformed							
						// or c1 and c2 are the correct spacing for the image not to be transformed
						// (ie image was probably created by the Pen Tool)
						|| (c1 != null && c2 != null && c1.inhomY == c2.inhomY && view.toScreenCoordX(c2.inhomX) - view.toScreenCoordX(c1.inhomX) == ((GeoImage)geo).getFillImage().getWidth() ))) 
						pen.setPenGeo( (GeoImage)geo); 
					else 
						pen.setPenGeo(null);
					
					pen.setPenWritingToExistingImage(pen.getPenGeo() != null);
				}
			}
			
			// no break;
			
		case EuclidianView.MODE_VISUAL_STYLE:
			
		//	openMiniPropertiesPanel();
			
			break;



		case EuclidianView.MODE_PROBABILITY_CALCULATOR:
			
			if(app.getGuiManager().getSpreadsheetView() != null)
				((SpreadsheetView) app.getGuiManager().getSpreadsheetView()).showProbabilityCalculator();	
			break;
		
			
		case EuclidianView.MODE_PARALLEL:
			previewDrawable = view.createPreviewParallelLine(selectedPoints, selectedLines);
			break;

		case EuclidianView.MODE_ANGULAR_BISECTOR:
			previewDrawable = view.createPreviewAngleBisector(selectedPoints);
			break;

		case EuclidianView.MODE_ORTHOGONAL:
			previewDrawable = view.createPreviewPerpendicularLine(selectedPoints, selectedLines);
			break;

		case EuclidianView.MODE_LINE_BISECTOR:
			previewDrawable = view.createPreviewPerpendicularBisector(selectedPoints);
			break;

		case EuclidianView.MODE_JOIN: // line through two points
					useLineEndPoint = false;
			previewDrawable = view.createPreviewLine(selectedPoints);
			break;

		case EuclidianView.MODE_SEGMENT:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewSegment(selectedPoints);
			break;

		case EuclidianView.MODE_RAY:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewRay(selectedPoints);
			break;

		case EuclidianView.MODE_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(selectedPoints);
			break;

		case EuclidianView.MODE_POLYGON:
		case EuclidianView.MODE_RIGID_POLYGON:
			previewDrawable = view.createPreviewPolygon(selectedPoints);
			break;

		case EuclidianView.MODE_POLYLINE:
			previewDrawable = view.createPreviewPolyLine(selectedPoints);
			break;

		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:		
			previewDrawable = view.createPreviewConic(mode, selectedPoints);
			break;

			// preview for compass: radius first
		case EuclidianView.MODE_COMPASSES:
			previewDrawable = new DrawConic((EuclidianView) view, mode, selectedPoints, selectedSegments, selectedConics);
			break;

			// preview for arcs and sectors
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart((EuclidianView) view, mode, selectedPoints);
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
					geo.setEuclidianVisible(true);					
					app.addSelectedGeo(geo);
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

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			
			//G.Sturr 2010-5-14
			if(recordObject != null)
				app.getGuiManager().removeSpreadsheetTrace(recordObject);
			//END G.Sturr
			
			recordObject = null;
	
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
		
		return previewDrawable;
	}



	protected void initShowMouseCoords() {
		view.setShowMouseCoords(mode == EuclidianView.MODE_POINT || mode == EuclidianView.MODE_MOVE);	}

	public void clearSelections() {

		clearSelection(selectedNumbers);
		clearSelection(selectedNumberValues);
		clearSelection(selectedPoints);
		clearSelection(selectedLines);
		clearSelection(selectedSegments);
		clearSelection(selectedConics);
		clearSelection(selectedVectors);
		clearSelection(selectedPolygons);
		clearSelection(selectedGeos);
		clearSelection(selectedFunctions);		
		clearSelection(selectedCurves);
		clearSelection(selectedLists);

		app.clearSelectedGeos();

		// clear highlighting
		refreshHighlighting(null);		
	}


	public Point getMouseLoc(){
		return mouseLoc;
	}

	public void mouseClicked(MouseEvent e) {	
		
		if (mode == EuclidianView.MODE_PEN)
			return;
		
		Hits hits;
		//GeoElement geo;

		altDown=e.isAltDown();

		if (mode != EuclidianView.MODE_SELECTION_LISTENER)
			((JPanel) view).requestFocusInWindow();

		if (Application.isRightClick(e)) return;
		setMouseLocation(e);		


		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (e.getClickCount() == 2) { 
			if (app.isApplet() || Application.isControlDown(e))
				return;

			app.clearSelectedGeos();
			//hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) {
				view.setMode(EuclidianView.MODE_MOVE);
				GeoElement geo0 = (GeoElement)hits.get(0);
				
				if (geo0.isGeoNumeric() && ((GeoNumeric)geo0).isSlider()) {
					// double-click slider -> Object Properties
					app.getGuiManager().showPropertiesDialog(hits);
				} else
				if (!geo0.isFixed() && !(geo0.isGeoBoolean() && geo0.isIndependent()) &&
						!(geo0.isGeoImage() && geo0.isIndependent())
						&& !geo0.isGeoButton())
					app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0), true);
			}

		}

		mouseClickedMode(e,mode);

		// Alt click: copy definition to input field
		if (e.isAltDown() && app.showAlgebraInput()) {
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			hits.removePolygons();
			if (hits != null && hits.size() > 0) {
				GeoElement geo = (GeoElement) hits.get(0);

				// F3 key: copy definition to input bar
				if (mode != EuclidianView.MODE_ATTACH_DETACH)
					app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);

				moveMode = MOVE_NONE;	
				return;
			}					
		}
	}
	
	
	
	protected void mouseClickedMode(MouseEvent e, int mode){
		

		switch (mode) {
		case EuclidianView.MODE_VISUAL_STYLE:								
		case EuclidianView.MODE_MOVE:								
		case EuclidianView.MODE_SELECTION_LISTENER:
			switch (e.getClickCount()) {
			case 1:			
				// handle selection click	
				view.setHits(mouseLoc);
				handleSelectClick(view.getHits().getTopHits(),//view.getTopHits(mouseLoc), 
						Application.isControlDown(e));
				break;
				/*
			//	open properties dialog on double click
			case 2:
				if (app.isApplet())
					return;

				app.clearSelectedGeos();
				hits = view.getTopHits(mouseLoc);
				if (hits != null && mode == EuclidianView.MODE_MOVE) {
					GeoElement geo0 = (GeoElement)hits.get(0);
					if (!geo0.isFixed() && !(geo0.isGeoImage() && geo0.isIndependent()))
						app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0));
				}
				break;*/
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

	protected void handleSelectClick(ArrayList geos, boolean ctrlDown) {	
		if (geos == null) {			
			app.clearSelectedGeos();
		} else {					
			if (ctrlDown) {							
				GeoElement geo = chooseGeo(geos, true);
				//boolean selected = geo.is
				app.toggleSelectedGeo( chooseGeo(geos, true) ); 
				//app.geoElementSelected(geo, true); // copy definiton to input bar
			} else {								
				if (!moveModeSelectionHandled) {					
					GeoElement geo = chooseGeo(geos, true);
					if (geo != null) {
						app.clearSelectedGeos(false);
						app.addSelectedGeo(geo);




					}
				}				
			}			
		}
	}






	protected void mousePressedTranslatedView(MouseEvent e){

		Hits hits;

		// check if axis is hit
		//hits = view.getHits(mouseLoc);
		view.setHits(mouseLoc);
		hits = view.getHits();
		hits.removePolygons();
		//Application.debug("MODE_TRANSLATEVIEW - "+hits.toString());

		/*
		if (!hits.isEmpty() && hits.size() == 1) {
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
		*/

		moveMode = MOVE_VIEW;	
		if (!hits.isEmpty() ) 
			for(Object hit:hits){
				if (hit == kernel.getXAxis())
					moveMode = MOVE_X_AXIS;
				if (hit == kernel.getYAxis())
					moveMode = MOVE_Y_AXIS;
			}	

		startLoc = mouseLoc; 
		if (!TEMPORARY_MODE) {
			if (moveMode == MOVE_VIEW)
				view.setMoveCursor();
			else
				view.setDragCursor();
		}

		//xZeroOld = view.getXZero();
		//yZeroOld = view.getYZero();
		view.rememberOrigins();
		xTemp = xRW;
		yTemp = yRW;
		view.setShowAxesRatio((moveMode == MOVE_X_AXIS) || (moveMode == MOVE_Y_AXIS));
		//view.setDrawMode(EuclidianView.DRAW_MODE_DIRECT_DRAW);

	}
	
	
	/**
	 * 
	 * @return true if a view button has been pressed (see 3D)
	 */
	protected boolean handleMousePressedForViewButtons(){
		return false;
	}

	public void mousePressed(MouseEvent e) {
		// determine parent panel to change focus
		EuclidianDockPanelAbstract panel = (EuclidianDockPanelAbstract)SwingUtilities.getAncestorOfClass(EuclidianDockPanelAbstract.class, (Component)e.getSource());
		
		if(panel != null) {
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(panel);
		}
		
		setMouseLocation(e);
		
		if (handleMousePressedForViewButtons())
			return;
		
		Hits hits;
		
		if (mode == EuclidianView.MODE_PEN) {
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removeAllButImages();
			pen.handleMousePressedForPenMode(e, hits);
			return;
		}

		//GeoElement geo;
		transformCoords();	

		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;			
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);	

		if (hitResetIcon() || view.hitAnimationButton(e)) {				
			// see mouseReleased
			return;
		}

		if (Application.isRightClick(e)) {			
			//ggb3D - for 3D rotation
			processRightPressFor3D();

			return;
		} 
		else if (
				app.isShiftDragZoomEnabled() && 
				(
						// MacOS: shift-cmd-drag is zoom
						(e.isShiftDown() && !Application.isControlDown(e)) // All Platforms: Shift key
						|| 
						e.isControlDown() && Application.WINDOWS // old Windows key: Ctrl key 
						||
						Application.isMiddleClick(e)
				)) 
		{
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl		


			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			if (!hits.isEmpty()) // bugfix 2008-02-19 removed this:&& ((GeoElement) hits.get(0)).isGeoPoint())
			{
				DONT_CLEAR_SELECTION=true;
			}
			//			 Michael Borcherds 2007-12-08 END
			TEMPORARY_MODE = true;
			oldMode = mode; // remember current mode	
			view.setMode(EuclidianView.MODE_TRANSLATEVIEW);				
		} 	
		
		switchModeForMousePressed(e);
		
	}
	
	protected void createNewPointForModePoint(Hits hits){
		if (mode==EuclidianView.MODE_POINT){//remove polygons : point inside a polygon is created free, as in v3.2
			//Application.debug("avant:"+hits);
			hits.removeAllPolygons();
			//Application.debug("après:"+hits);
			createNewPoint(hits, true, false, true, true);
		}else {// if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in a region
			createNewPoint(hits, true, true,  true, true);
		}
	}
	
	protected void createNewPointForModeOther(Hits hits){
		createNewPoint(hits, true, false, true, true);
	}
	
	protected void switchModeForMousePressed(MouseEvent e){

		Hits hits;
		
		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianView.MODE_POINT:
		case EuclidianView.MODE_POINT_ON_OBJECT:				
			view.setHits(mouseLoc);
			hits = view.getHits();
			// if mode==EuclidianView.MODE_POINT_ON_OBJECT, point can be in a region
			createNewPointForModePoint(hits); 
			break;

		case EuclidianView.MODE_SEGMENT:
		case EuclidianView.MODE_SEGMENT_FIXED:		
		case EuclidianView.MODE_JOIN:
		case EuclidianView.MODE_RAY:
		case EuclidianView.MODE_VECTOR:
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
		case EuclidianView.MODE_POLYGON:
		case EuclidianView.MODE_POLYLINE:
		case EuclidianView.MODE_REGULAR_POLYGON:	
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			createNewPointForModeOther(hits);
			break;

		case EuclidianView.MODE_RIGID_POLYGON:
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			createNewPoint(hits, false, false, false, false); 
			break;
			
		case EuclidianView.MODE_PARALLEL:
		case EuclidianView.MODE_PARABOLA: // Michael Borcherds 2008-04-08
		case EuclidianView.MODE_ORTHOGONAL:
		case EuclidianView.MODE_LINE_BISECTOR:
		case EuclidianView.MODE_ANGULAR_BISECTOR:
		case EuclidianView.MODE_TANGENTS:		
		case EuclidianView.MODE_POLAR_DIAMETER:
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0)
				createNewPoint(hits, false, true, true);
			break;		

		case EuclidianView.MODE_COMPASSES:		// Michael Borcherds 2008-03-13	
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty()) 
				createNewPoint(hits, false, true, true);
			break;		

		case EuclidianView.MODE_ANGLE:
			//hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			// check if we got a polygon
			if (hits.isEmpty()) {
				createNewPoint(hits, false, false, true);			
			}			
			break;

		case EuclidianView.MODE_ANGLE_FIXED:
		case EuclidianView.MODE_MIDPOINT:
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.isEmpty() || (!((GeoElement) hits.get(0)).isGeoSegment() && !((GeoElement) hits.get(0)).isGeoConic())) 
				createNewPoint(hits, false, false, true);			
			break;

		case EuclidianView.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode();
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			handleMousePressedForRecordToSpreadsheetMode(e);
			break;

			// move an object
		case EuclidianView.MODE_MOVE:	
		case EuclidianView.MODE_VISUAL_STYLE:	
			handleMousePressedForMoveMode(e, false);			
			break;

			// move drawing pad or axis
		case EuclidianView.MODE_TRANSLATEVIEW:		

			mousePressedTranslatedView(e);

			break;								

		default:
			moveMode = MOVE_NONE;			 
		}
	}

	
	
	
	protected void handleMousePressedForRotateMode() {	
		GeoElement geo;
		Hits hits;

		// we need the center of the rotation
		if (rotationCenter == null) {
			view.setHits(mouseLoc);
			rotationCenter = (GeoPoint) chooseGeo(view.getHits().getHits(GeoPoint.class, tempArrayList), true);
			app.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		}
		else {	
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			//hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (!hits.isEmpty() && hits.contains(rotationCenter)) {
				app.removeSelectedGeo(rotationCenter);
				rotationCenter = null;
				moveMode = MOVE_NONE;
				return;
			}

			moveModeSelectionHandled = true;

			// find and set rotGeoElement
			hits = hits.getPointRotateableHits(rotationCenter);
			if (!hits.isEmpty() && hits.contains(rotGeoElement))
				geo = rotGeoElement;
			else {
				geo = chooseGeo(hits, true);				
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
	
	protected void handleMousePressedForMoveMode(MouseEvent e, boolean drag) {

		//long t0 = System.currentTimeMillis();


		//Application.debug("start");



		//view.resetTraceRow(); // for trace/spreadsheet

		// fix for meta-click to work on Mac/Linux
		if (Application.isControlDown(e)) return;

		// move label?
		GeoElement geo = view.getLabelHit(mouseLoc);
		//Application.debug("label("+(System.currentTimeMillis()-t0)+")");
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			view.setDragCursor();
			return;
		}


		//Application.debug("laps("+(System.currentTimeMillis()-t0)+")");

		// find and set movedGeoElement
		view.setHits(mouseLoc); 
		Hits moveableList;		

		// if we just click (no drag) on eg an intersection, we want it selected
		// not a popup with just the lines in
		if (drag)
			moveableList = view.getHits().getMoveableHits();
		else
			moveableList = view.getHits();

		Hits hits = moveableList.getTopHits();

		//Application.debug("end("+(System.currentTimeMillis()-t0)+")");

		ArrayList selGeos = app.getSelectedGeos();
		
		// if object was chosen before, take it now!
		if (selGeos.size() == 1 && 
				!hits.isEmpty() && hits.contains(selGeos.get(0))) 
		{
			// object was chosen before: take it			
			geo = (GeoElement) selGeos.get(0);			
		} else {
			// choose out of hits			
			geo = chooseGeo(hits, false);

			if (!selGeos.contains(geo)) {
				app.clearSelectedGeos();
				app.addSelectedGeo(geo);
				//app.geoElementSelected(geo, false); // copy definiton to input bar
			}
		}				

		if (geo != null && !geo.isFixed()) {		
			moveModeSelectionHandled = true;														
		} else {
			// no geo clicked at
			moveMode = MOVE_NONE;	
			resetMovedGeoPoint();
			return;
		}				

		movedGeoElement = geo;



		resetMovedGeoPoint();


		// multiple geos selected
		if (movedGeoElement != null && selGeos.size() > 1) {									
			moveMode = MOVE_MULTIPLE_OBJECTS;
			startPoint.setLocation(xRW, yRW);	
			startLoc = mouseLoc;
			view.setDragCursor();
			if (translationVec == null)
				translationVec = new Coords(2);
		}	

		// DEPENDENT object: changeable parents?
		// move free parent points (e.g. for segments)
		else if (!movedGeoElement.isMoveable()) {
			
			translateableGeos = null;

			// point with changeable coord parent numbers
			if (movedGeoElement.hasChangeableCoordParentNumbers()) {
				movedGeoElement.recordChangeableCoordParentNumbers();
				translateableGeos = new ArrayList();
				translateableGeos.add(movedGeoElement);
			}

			// STANDARD case: get free input points of dependent movedGeoElement
			else if (movedGeoElement.hasMoveableInputPoints()) {
				// allow only moving of the following object types
				if (movedGeoElement.isGeoLine() || 
						movedGeoElement.isGeoPolygon() ||					 
						movedGeoElement instanceof GeoPolyLine ||					 
						movedGeoElement.isGeoConic() || 
						movedGeoElement.isGeoImage() || 
						movedGeoElement.isGeoList() || 
						movedGeoElement.isGeoVector()) 
				{		
					translateableGeos = movedGeoElement.getFreeInputPoints();
				}
			}

			// init move dependent mode if we have something to move ;-)
			if (translateableGeos != null) {
				moveMode = MOVE_DEPENDENT;

				if (translateableGeos.get(0) instanceof GeoPoint){
					GeoPoint point = ((GeoPoint)translateableGeos.get(0));
					if (point.getParentAlgorithm() != null) {
						// make sure snap-to-grid works for dragging (a + x(A), b + x(B))
						transformCoordsOffset[0] = 0;
						transformCoordsOffset[1] = 0;

					} else {
						// snap to grid when dragging polygons, segments, images etc
						// use first point
						point.getInhomCoords(transformCoordsOffset);
						transformCoordsOffset[0]-=xRW;
						transformCoordsOffset[1]-=yRW;
					}
				}
				
				setStartPointLocation();

				view.setDragCursor();
				if (translationVec == null)
					translationVec = new Coords(2);
			} else {
				moveMode = MOVE_NONE;
			}				
		} 

		// free point
		else if (movedGeoElement.isGeoPoint()) {
			moveMode = MOVE_POINT;
			setMovedGeoPoint(movedGeoElement);
		} 			

		// free line
		else if (movedGeoElement.isGeoLine()) {
			moveMode = MOVE_LINE;
			movedGeoLine = (GeoLine) movedGeoElement;
			view.setShowMouseCoords(true);
			view.setDragCursor();
		} 

		// free vector
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

			view.setShowMouseCoords(true);
			view.setDragCursor();
		} 

		// free text
		else if (movedGeoElement.isGeoText()) {
			moveMode = MOVE_TEXT;
			movedGeoText = (GeoText) movedGeoElement;
			view.setShowMouseCoords(false);
			view.setDragCursor();	

			if (movedGeoText.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoText.getAbsoluteScreenLocX(),
						movedGeoText.getAbsoluteScreenLocY());
				startLoc = mouseLoc;
			}
			else if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
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
		} 

		// free conic
		else if (movedGeoElement.isGeoConic()) {
			moveMode = MOVE_CONIC;
			movedGeoConic = (GeoConic) movedGeoElement;
			view.setShowMouseCoords(false);
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
			view.setShowMouseCoords(false);
			view.setDragCursor();

			startPoint.setLocation(xRW, yRW);
			if (tempFunction == null) {
				tempFunction = new GeoFunction(kernel.getConstruction());
			}
			tempFunction.set(movedGeoFunction);
		} 

		// free number
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

			view.setShowMouseCoords(false);
			view.setDragCursor();					
		}  

		//  checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;
			
			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards
			if (movedGeoBoolean.isCheckboxFixed()) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());
				app.removeSelectedGeo(movedGeoBoolean); // make sure doesn't get selected
				movedGeoBoolean.updateCascade();

			} 
			
			// move checkbox
			moveMode = MOVE_BOOLEAN;					
			startLoc = mouseLoc;
			oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();

			view.setShowMouseCoords(false);
			view.setDragCursor();			

		}

		//  button
		else if (movedGeoElement.isGeoButton()) {
			movedGeoButton = (GeoButton) movedGeoElement;
			// move checkbox
			moveMode = MOVE_BUTTON;					
			startLoc = mouseLoc;
			oldLoc.x = movedGeoButton.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoButton.getAbsoluteScreenLocY();

			view.setShowMouseCoords(false);
			view.setDragCursor();			
		}

		// image
		else if (movedGeoElement.isGeoImage()) {
			moveMode = MOVE_IMAGE;
			movedGeoImage = (GeoImage) movedGeoElement;
			view.setShowMouseCoords(false);
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

		view.repaintEuclidianView();												
	}



	////////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...
	public void setMovedGeoPoint(GeoElement geo){
		movedGeoPoint = (GeoPointND) movedGeoElement;

		AlgoElement algo = ((GeoElement) movedGeoPoint).getParentAlgorithm();
		if (algo != null && algo instanceof AlgoDynamicCoordinates) {
			movedGeoPoint = ((AlgoDynamicCoordinates)algo).getParentPoint();
		}

		view.setShowMouseCoords(!app.isApplet()
				&& !movedGeoPoint.hasPath());
		view.setDragCursor();
	}
	

	public void setStartPointLocation(){
		startPoint.setLocation(xRW, yRW);	
	}

	public void resetMovedGeoPoint(){
		movedGeoPoint = null;
	}




	public void mouseDragged(MouseEvent e) {

		if (textfieldHasFocus) return;

		if (mode == EuclidianView.MODE_PEN) {
			pen.handleMousePressedForPenMode(e, null);
			return;
		}

		if (!DRAGGING_OCCURED) {
			
			DRAGGING_OCCURED = true;			

			// Michael Borcherds 2007-10-07 allow right mouse button to drag points
			// mathieu : also if it's mode point, we can drag the point
			if (Application.isRightClick(e) 
					|| mode == EuclidianView.MODE_POINT 
					|| mode == EuclidianView.MODE_POINT_ON_OBJECT
			){
				view.setHits(mouseLoc);
				if (!(view.getHits().isEmpty())) 
				{
					TEMPORARY_MODE = true;
					oldMode = mode; // remember current mode			
					view.setMode(EuclidianView.MODE_MOVE);
					handleMousePressedForMoveMode(e, true);	

					// make sure that dragging doesn't deselect the geos
					DONT_CLEAR_SELECTION=true;

					return;
				}
				
			}
			if (!app.isRightClickEnabled()) return;
			//			 Michael Borcherds 2007-10-07


			if (mode == EuclidianView.MODE_MOVE_ROTATE) {
				app.clearSelectedGeos(false);
				app.addSelectedGeo(rotationCenter, false);						
			}
		}
		lastMouseLoc = mouseLoc;
		setMouseLocation(e);				
		transformCoords();


		//ggb3D - only for 3D view
		if (moveMode==MOVE_ROTATE_VIEW)
			if (processRotate3DView())
				return;
		
		
		if (Application.isRightClick(e)){
			// if there's no hit, or if first hit is not moveable, do 3D view rotation
			if ((!TEMPORARY_MODE) || view.getHits().size() == 0 
					|| !((GeoElement) view.getHits().get(0)).isMoveable()
					|| ( !((GeoElement) view.getHits().get(0)).isGeoPoint() && ((GeoElement) view.getHits().get(0)).hasDrawable3D() )
					)
				if (processRotate3DView()){ //in 2D view, return false
					if (TEMPORARY_MODE){
						TEMPORARY_MODE = false;
						mode = oldMode;
						view.setMode(mode);
					}
					return;
				}
		}


		// dragging eg a fixed point shouldn't start the selection rectangle
		if (view.getHits().isEmpty())

		// zoom rectangle (right drag) or selection rectangle (left drag)
		// Michael Borcherds 2007-10-07 allow dragging with right mouse button
		if (((Application.isRightClick(e)) || allowSelectionRectangle()) && !TEMPORARY_MODE) {
			//			 Michael Borcherds 2007-10-07 
			// set zoom rectangle's size
			// right-drag: zoom
			// Shift-right-drag: zoom without preserving aspect ratio
			updateSelectionRectangle((Application.isRightClick(e) && !e.isShiftDown())
					// MACOS:
					// Cmd-left-drag: zoom
					// Cmd-shift-left-drag: zoom without preserving aspect ratio
					|| (Application.MAC_OS && Application.isControlDown(e) && !e.isShiftDown() && !Application.isRightClick(e)));
			view.repaintEuclidianView();
			return;
		}		

		// update previewable
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updateMousePos(view.toRealWorldCoordX(mouseLoc.x), view.toRealWorldCoordY(mouseLoc.y));
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

				// number of continuity steps <= MAX_CONTINUITY_STEPS
				int steps = Math.min((int) (1.0 / factor), MAX_CONTINUITY_STEPS);
				int mx = mouseLoc.x;
				int my = mouseLoc.y;

				// Application.debug("BIG drag dist: " + Math.sqrt(distsq) + ", steps: " + steps  );
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







	protected boolean allowSelectionRectangle() {
		switch (mode) {
		// move objects
		case EuclidianView.MODE_MOVE:
			return moveMode == MOVE_NONE;

			// move rotate objects
		case EuclidianView.MODE_MOVE_ROTATE:
			return selPoints() > 0; // need rotation center

			// object selection mode
		case EuclidianView.MODE_SELECTION_LISTENER:
			GeoElementSelectionListener sel = app.getCurrentSelectionListener();
			if (sel == null) return false;
			if (app.hasFullGui()) {
				return !app.getGuiManager().isInputFieldSelectionListener();
			}
			else
				return sel != null;

			// transformations
		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianView.MODE_DILATE_FROM_POINT:	
		case EuclidianView.MODE_MIRROR_AT_POINT:
		case EuclidianView.MODE_MIRROR_AT_LINE:
		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
		case EuclidianView.MODE_ROTATE_BY_ANGLE:
		case EuclidianView.MODE_FITLINE:
		case EuclidianView.MODE_VISUAL_STYLE:
			return true;

			// checkbox, button
		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:			
		case EuclidianView.MODE_BUTTON_ACTION:
		case EuclidianView.MODE_TEXTFIELD_ACTION:
			return true;

		default:
			return false;
		}		
	}




	// square of maximum allowed pixel distance 
	// for continuous mouse movements
	protected static double MOUSE_DRAG_MAX_DIST_SQUARE = 36; 
	protected static int MAX_CONTINUITY_STEPS = 4; 

	protected void handleMouseDragged(boolean repaint) {
		// moveMode was set in mousePressed()
		switch (moveMode) {
		case MOVE_ROTATE:
			rotateObject(repaint);
			break;

		case MOVE_POINT:

			//view.incrementTraceRow(); // for spreadsheet/trace

			
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
			//view.incrementTraceRow(); // for spreadsheet/trace

			moveNumeric(repaint);
			break;

		case MOVE_SLIDER:
			moveSlider(repaint);
			break;

		case MOVE_BOOLEAN:
			moveBoolean(repaint);
			break;

		case MOVE_BUTTON:
			moveButton(repaint);
			break;

		case MOVE_DEPENDENT:
			moveDependent(repaint);
			break;

		case MOVE_MULTIPLE_OBJECTS:
			moveMultipleObjects(repaint);
			break;

		case MOVE_VIEW:
			if (repaint) {
				if (TEMPORARY_MODE) view.setMoveCursor();
				/*
					view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x, yZeroOld
							+ mouseLoc.y - startLoc.y, view.getXscale(), view.getYscale());
				 */
				view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x, mouseLoc.y - startLoc.y, MOVE_VIEW);
			}
			break;	

		case MOVE_X_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) view.setResizeXAxisCursor();

				// take care when we get close to the origin
				if (Math.abs(mouseLoc.x - view.getXZero()) < 2) {
					mouseLoc.x = (int) Math.round(mouseLoc.x > view.getXZero() ?  view.getXZero() + 2 : view.getXZero() - 2);						
				}											
				double xscale = (mouseLoc.x - view.getXZero()) / xTemp;					
				view.setCoordSystem(view.getXZero(), view.getYZero(), xscale, view.getYscale());
			}
			break;	

		case MOVE_Y_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) view.setResizeYAxisCursor();
				// take care when we get close to the origin
				if (Math.abs(mouseLoc.y - view.getYZero()) < 2) {
					mouseLoc.y = (int) Math.round(mouseLoc.y > view.getYZero() ?  view.getYZero() + 2 : view.getYZero() - 2);						
				}											
				double yscale = (view.getYZero() - mouseLoc.y) / yTemp;					
				view.setCoordSystem(view.getXZero(), view.getYZero(), view.getXscale(), yscale);					
			}
			break;	

		default: // do nothing
		}
	}		

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
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
			double ratio = (double) view.getViewWidth() / (double) view.getViewHeight();
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

	// used for 3D
	protected void processReleaseForMovedGeoPoint(MouseEvent e){
		
		// deselect point after drag, but not on click
		// outdated - we want to leave the point selected after drag now
		//if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);

		if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) getMovedGeoPoint().resetTraceColumns();

	}
	
	public void showDrawingPadPopup(Point mouseLoc){
		app.getGuiManager().showDrawingPadPopup((JPanel) view, mouseLoc);		
	}

	public void mouseReleased(MouseEvent e) {	
		if (e != null) {
			mx = e.getX();
			my = e.getY();
		}
		// reset
		transformCoordsOffset[0] = 0;
		transformCoordsOffset[1] = 0;

		if (textfieldHasFocus) return;

		if (mode == EuclidianView.MODE_PEN) {
			pen.handleMouseReleasedForPenMode(e);
			return;
		}


		//if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) view.resetTraceRow(); // for trace/spreadsheet
		if (getMovedGeoPoint() != null){

			processReleaseForMovedGeoPoint(e);
			/*
			// deselect point after drag, but not on click
			if (movedGeoPointDragged) getMovedGeoPoint().setSelected(false);

			if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) getMovedGeoPoint().resetTraceColumns();
			 */
		}
		if (movedGeoNumeric != null) {

			// deselect slider after drag, but not on click
			if (movedGeoNumericDragged) movedGeoNumeric.setSelected(false);

			if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) movedGeoNumeric.resetTraceColumns();
		}

		movedGeoPointDragged = false;
		movedGeoNumericDragged = false;

		((JPanel) view).requestFocusInWindow();
		setMouseLocation(e);

		altDown=e.isAltDown();

		transformCoords();
		Hits hits = null;
		GeoElement geo;

		if (hitResetIcon()) {				
			app.reset();
			return;
		}
		else if (view.hitAnimationButton(e)) {		
			if (kernel.isAnimationRunning())
				kernel.getAnimatonManager().stopAnimation();
			else
				kernel.getAnimatonManager().startAnimation();			
			view.repaintEuclidianView();
			app.setUnsaved();
			return;
		}



		// Michael Borcherds 2007-10-08 allow drag with right mouse button
		if ((Application.isRightClick(e) || Application.isControlDown(e)))// && !TEMPORARY_MODE)
		{		
			if (processRightReleaseFor3D()) return;
			if (!TEMPORARY_MODE){
				if (!app.isRightClickEnabled()) return;
				if (processZoomRectangle()) return;
				//			 Michael Borcherds 2007-10-08

				// make sure cmd-click selects multiple points (not open properties)
				if (Application.MAC_OS && Application.isControlDown(e) 
						|| !Application.isRightClick(e))
					return;

				// get selected GeoElements						
				// show popup menu after right click
				view.setHits(mouseLoc);
				hits = view.getHits().getTopHits();
				if (hits.isEmpty()) {
					// no hits
					if (app.selectedGeosSize() > 0) {
						//GeoElement selGeo = (GeoElement) app.getSelectedGeos().get(0);
						app.getGuiManager().showPopupMenu(app.getSelectedGeos(), (JPanel) view, mouseLoc);
					}
					else {
						showDrawingPadPopup(mouseLoc);
					}
				} else {		
					// there are hits
					if (app.selectedGeosSize() > 0) {	

						// right click on already selected geos -> show menu for them
						// right click on object(s) not selected -> clear selection
						//     and show menu just for new objects
						if (!app.getSelectedGeos().contains(hits.get(0))) {
							app.clearSelectedGeos();
							app.addSelectedGeos(hits, true);
						} else {					
							app.addSelectedGeo((GeoElement) hits.get(0));
						}
						
						app.getGuiManager().showPopupMenu(app.getSelectedGeos(), (JPanel) view, mouseLoc);
					}
					else {
						// no selected geos: choose geo and show popup menu
						geo = chooseGeo(hits, false);
						if (geo!=null) {
							ArrayList<GeoElement> geos = new ArrayList();
							geos.add(geo);
							app.getGuiManager().showPopupMenu(geos,(JPanel) view, mouseLoc);
						}
						else
							//for 3D : if the geo hitted is xOyPlane, then chooseGeo return null
							//app.getGuiManager().showDrawingPadPopup((JPanel) view, mouseLoc);
							showDrawingPadPopup(mouseLoc);
					}																										
				}				
				return;
			}
		}

		// handle moving
		boolean changedKernel = false;		
		if (DRAGGING_OCCURED) {			

			DRAGGING_OCCURED = false;
			//			// copy value into input bar
			//			if (mode == EuclidianView.MODE_MOVE && movedGeoElement != null) {
			//				app.geoElementSelected(movedGeoElement,false);
			//			}


			changedKernel = (moveMode != MOVE_NONE);			
			movedGeoElement = null;
			rotGeoElement = null;	

			// Michael Borcherds 2007-10-08 allow dragging with right mouse button
			if (!TEMPORARY_MODE)
				// Michael Borcherds 2007-10-08
				if (allowSelectionRectangle()) {
					processSelectionRectangle(e);	

					return;
				}
		} else {	
			// no hits: release mouse button creates a point
			// for the transformation tools
			// (note: this cannot be done in mousePressed because
			// we want to be able to select multiple objects using the selection rectangle)
			
			changedKernel = switchModeForMouseReleased(mode, hits, changedKernel);
		}

		// remember helper point, see createNewPoint()
		if (changedKernel)
			app.storeUndoInfo();

		// make sure that when alt is pressed for creating a segment or line
		// it works if the endpoint is on a path
		if (useLineEndPoint && lineEndPoint != null) {
			mouseLoc.x = view.toScreenCoordX(lineEndPoint.x);
			mouseLoc.y = view.toScreenCoordY(lineEndPoint.y);
			useLineEndPoint = false;	
		}

		// now handle current mode
		view.setHits(mouseLoc);
		hits = view.getHits();
		switchModeForRemovePolygons(hits);
		//Application.debug(hits.toString());


		// Michael Borcherds 2007-12-08 BEGIN moved up a few lines (bugfix: Tools eg Line Segment weren't working with grid on)
		// grid capturing on: newly created point should be taken
		//Application.debug("POINT_CREATED="+POINT_CREATED+"\nhits=\n"+hits+"\ngetMovedGeoPoint()="+getMovedGeoPoint());
		if (POINT_CREATED) 			
			hits=addPointCreatedForMouseReleased(hits);		
		POINT_CREATED = false;		
		//		 Michael Borcherds 2007-12-08 END	




		if (TEMPORARY_MODE) {

			//			Michael Borcherds 2007-10-13 BEGIN
			view.setMode(oldMode);
			TEMPORARY_MODE = false;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select multiple points with Ctrl
			if (DONT_CLEAR_SELECTION==false)
				clearSelections();	
			DONT_CLEAR_SELECTION=false;
			//			 Michael Borcherds 2007-12-08 END
			//mode = oldMode;
			//			Michael Borcherds 2007-10-13 END
		} 
		//		 Michael Borcherds 2007-10-12 bugfix: ctrl-click on a point does the original mode's command at end of drag if a point was clicked on
		//  also needed for right-drag
		else
		{			
			if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET){ 
				changedKernel = processMode(hits, e);
			}
			if (changedKernel)
				app.storeUndoInfo();
		}
		//Michael Borcherds 2007-10-12



		//		Michael Borcherds 2007-10-12
		//      moved up a few lines
		//		changedKernel = processMode(hits, e);
		//		if (changedKernel)
		//			app.storeUndoInfo();
		//		Michael Borcherds 2007-10-12


		if (!hits.isEmpty()){
			//Application.debug("hits ="+hits);
			view.setDefaultCursor();		
		}else
			view.setHitCursor();

		refreshHighlighting(null);

		// reinit vars
		//view.setDrawMode(EuclidianView.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();	
		view.setShowAxesRatio(false);
		kernel.notifyRepaint();					
	}
	
	
	protected Hits addPointCreatedForMouseReleased(Hits hits){
		
		if (hits.isEmpty()) {			
			hits = new Hits();
			hits.add(getMovedGeoPoint());			
		}
		
		return hits;
	}
	
	
	
	
	
	
	/**
	 * for some modes, polygons are not to be removed
	 * @param hits
	 */
	protected void switchModeForRemovePolygons(Hits hits){
		hits.removePolygons();
	}
	
	
	protected boolean switchModeForMouseReleased(int mode, Hits hits, boolean changedKernel){
		switch (mode) {
		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianView.MODE_DILATE_FROM_POINT:	
		case EuclidianView.MODE_MIRROR_AT_POINT:
		case EuclidianView.MODE_MIRROR_AT_LINE:
		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			//hits = view.getHits(mouseLoc);
			if (hits.isEmpty()) { 
				POINT_CREATED = createNewPoint(hits, false, false, true);					
			}
			changedKernel = POINT_CREATED;
			break;

		case EuclidianView.MODE_BUTTON_ACTION:
		case EuclidianView.MODE_TEXTFIELD_ACTION:
			// make sure script not triggered
			break;

		default:

			// change checkbox (boolean) state on mouse up only if there's been no drag
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			//hits = view.getTopHits(mouseLoc);
			if (!hits.isEmpty()) {
				GeoElement hit = (GeoElement)hits.get(0);
				if (hit != null && hit.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean)(hits.get(0));
					if (!bool.isCheckboxFixed()) { // otherwise changed on mouse down
						bool.setValue(!bool.getBoolean());
						app.removeSelectedGeo(bool); // make sure doesn't get selected
						bool.updateCascade();
					}
				} else if (hit != null) {
					GeoElement geo1 = chooseGeo(hits, true);
					//ggb3D : geo1 may be null if it's axes or xOy plane
					if (geo1!=null)
						geo1.runScripts(null);						
				}
			}
		}

		return changedKernel;
	}
	
	
	

	protected boolean hitResetIcon() {
		return app.showResetIcon() &&
		(mouseLoc.y < 18 && mouseLoc.x > view.getViewWidth() - 18);
	}

	// return if we really did zoom
	protected boolean processZoomRectangle() {
		Rectangle rect = view.getSelectionRectangle();
		if (rect == null) 
			return false;

		if (rect.width < 30 || rect.height < 30
				|| !app.isShiftDragZoomEnabled() // Michael Borcherds 2007-12-11		
		) {
			view.setSelectionRectangle(null);
			view.repaintEuclidianView();
			return false;
		}

		view.resetMode();
		// zoom zoomRectangle to EuclidianView's size
		//double factor = (double) view.width / (double) rect.width;
		//Point p = rect.getLocation();
		view.setSelectionRectangle(null);
		//view.setAnimatedCoordSystem((view.xZero - p.x) * factor,
		//		(view.yZero - p.y) * factor, view.xscale * factor, 15, true);

		// zoom without (necessarily) preserving the aspect ratio
		view.setAnimatedRealWorldCoordSystem(
				view.toRealWorldCoordX(rect.getMinX()),
				view.toRealWorldCoordX(rect.getMaxX()),
				view.toRealWorldCoordY(rect.getMaxY()),
				view.toRealWorldCoordY(rect.getMinY()),
				15 ,true);
		return true;
	}

	// select all geos in selection rectangle 
	protected void processSelectionRectangle(MouseEvent e) {
		clearSelections();
		view.setHits(view.getSelectionRectangle());
		Hits hits = view.getHits();

		switch (mode) {
		case EuclidianView.MODE_SELECTION_LISTENER:
			// tell properties dialog
			if (hits.size() > 0 &&
					app.hasFullGui() &&
					app.getGuiManager().isPropertiesDialogSelectionListener()) 
			{
				GeoElement geo = (GeoElement) hits.get(0);
				app.geoElementSelected(geo, false);
				for (int i=1; i < hits.size(); i++) {
					app.geoElementSelected((GeoElement) hits.get(i), true);
				}
			} 
			break;

		case EuclidianView.MODE_MIRROR_AT_POINT:	
		case EuclidianView.MODE_MIRROR_AT_LINE:
		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;		

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;	

		case EuclidianView.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Dilateable.class);									
			break;	

		case EuclidianView.MODE_FITLINE:
			for (int i=0; i < hits.size(); i++) {
				GeoElement geo = (GeoElement) hits.get(i);
				if (!(GeoPoint.class.isInstance(geo))) {
					hits.remove(i);
				}
			}
			// Fit line makes sense only for more than 2 points
			if (hits.size() < 3) { 
				hits.clear();
			} else {
				removeParentPoints(hits);				
				selectedGeos.addAll(hits);
				app.setSelectedGeos(hits);
				processMode(hits, e);
				view.setSelectionRectangle(null);
			}
			break;

		default:
			// STANDARD CASE
			app.setSelectedGeos(hits);

			// if alt pressed, create list of objects as string and copy to input bar
			if (hits != null && hits.size() > 0 && e != null && e.isAltDown() && app.hasFullGui() && app.showAlgebraInput()) {

				JTextComponent textComponent = app.getGuiManager().getAlgebraInputTextField();				

				StringBuilder sb = new StringBuilder();
				sb.append(" {");
				for (int i = 0 ; i < hits.size() ; i++ ) {
					sb.append(((GeoElement)hits.get(i)).getLabel());
					if (i < hits.size() - 1 ) sb.append(", ");
				}
				sb.append("} ");
				//Application.debug(sb+"");
				textComponent.replaceSelection(sb.toString());
			}
			break;
		}

		kernel.notifyRepaint();
	}

	protected void processSelectionRectangleForTransformations(Hits hits, Class transformationInterface) {
		for (int i=0; i < hits.size(); i++) {
			GeoElement geo = (GeoElement) hits.get(i);
			if (!(transformationInterface.isInstance(geo) 
				//	|| geo.isGeoPolygon()
					)) {
				hits.remove(i);
			}
		}
		removeParentPoints(hits);
		selectedGeos.addAll(hits);
		app.setSelectedGeos(hits);
	}

	protected void processSelection() {
		Hits hits = new Hits();
		hits.addAll(Arrays.asList(getSelectedGeos()));

		switch (mode) {
		case EuclidianView.MODE_MIRROR_AT_POINT:	
		case EuclidianView.MODE_MIRROR_AT_LINE:
		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;		

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits, Transformable.class);									
			break;	

		case EuclidianView.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Dilateable.class);
			break;

		case EuclidianView.MODE_FITLINE:
			for (int i=0; i < hits.size(); i++) {
				GeoElement geo = (GeoElement) hits.get(i);
				if (!(GeoPoint.class.isInstance(geo))) {
					hits.remove(i);
				}
			}
			// Fit line makes sense only for more than 2 points
			if (hits.size() < 3) { 
				hits.clear();
			} else {
				removeParentPoints(hits);				
				selectedGeos.addAll(hits);
				app.setSelectedGeos(hits);
				processMode(hits, null);
				view.setSelectionRectangle(null);
			}
			break;

		default:
			break;
		}

		kernel.notifyRepaint();
	}



	public void mouseMoved(MouseEvent e) {		

		if (textfieldHasFocus) return;

		setMouseLocation(e);
		processMouseMoved(e);
	}

	
	private boolean mouseIsOverLabel = false;
	
	/**
	 * 
	 * @return true if the mouse is over a label
	 */
	public boolean mouseIsOverLabel(){
		return mouseIsOverLabel;
	}


	protected void processMouseMoved(MouseEvent e) {	
		
		boolean repaintNeeded;

		// reset icon
		if (hitResetIcon()) {
			view.setToolTipText(app.getPlainTooltip("resetConstruction"));
			view.setHitCursor();
			return;
		} 

		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(e);
		repaintNeeded = view.setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused())
				view.setToolTipText(app.getPlainTooltip("Play"));
			else 
				view.setToolTipText(app.getPlainTooltip("Pause"));		
			view.setHitCursor();
			view.repaintEuclidianView();
			return;
		}			

		// standard handling
		Hits hits = new Hits();
		boolean noHighlighting = false;		
		altDown=e.isAltDown();		

		// label hit
		GeoElement geo = view.getLabelHit(mouseLoc);
		if (geo != null)
			mouseIsOverLabel = true;
		else
			mouseIsOverLabel = false;
		if (moveMode(mode)) {	// label hit in move mode: block all other hits		
			if (geo != null) {				
				//Application.debug("hop");
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;						
			}
		}
		else if (mode == EuclidianView.MODE_POINT || mode == EuclidianView.MODE_POINT_ON_OBJECT) {
			// include polygons in hits
			view.setHits(mouseLoc);
			hits = view.getHits();
		}

		if (hits.isEmpty()){
			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
		}

		if (hits.isEmpty()) {
			view.setToolTipText(null);
			view.setDefaultCursor();	
		}			
		else {
			if (e.isShiftDown() && hits.size() == 1 && hits.get(0) instanceof GeoAxis)
				if (((GeoAxis)hits.get(0)).getType() == GeoAxis.X_AXIS)
					view.setResizeXAxisCursor();
				else view.setResizeYAxisCursor();
			else
				view.setHitCursor();
		}



		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		//if (app.isUsingLayout() && app.getGuiManager().showAlgebraView()) {
			//hits = view.getTopHits(hits);
			hits = hits.getTopHits();
			if (!hits.isEmpty()) {
				String text = GeoElement.getToolTipDescriptionHTML(hits,
						true, true);				
				view.setToolTipText(text);
			} else
				view.setToolTipText(null);
		//}

		// update previewable
		if (view.getPreviewDrawable() != null) {	
			view.updatePreviewable();
			repaintNeeded = true;
		}

		// show Mouse coordinates, manage alt -> multiple of 15 degrees
		if (view.getShowMouseCoords()) {
			transformCoords();
			repaintNeeded = true;
		}		

		//	manage highlighting & "snap to object"
		//Application.debug("noHighlighting = "+noHighlighting);
		//Application.debug("hits = "+hits.toString());		
		repaintNeeded = noHighlighting ?  refreshHighlighting(null) : refreshHighlighting(hits) 
				|| repaintNeeded;

		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}


	protected void doSingleHighlighting(GeoElement geo) {
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
	public final boolean refreshHighlighting(Hits hits) {
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
	protected final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = (GeoElement) it.next();
			geo.setHighlighted(highlight);
		}
	}
	
	
	
	
	protected boolean switchModeForProcessMode(Hits hits, MouseEvent e){
		
		Boolean changedKernel = false;
		GeoElement[] ret = null; 
		
		switch (mode) {
		case EuclidianView.MODE_VISUAL_STYLE:
		case EuclidianView.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {	
				move(hits.getTopHits());				
			} else {
				if (DRAGGING_OCCURED && app.selectedGeosSize() == 1)
					app.clearSelectedGeos();

			}
			break;			

		case EuclidianView.MODE_MOVE_ROTATE:
			// moveRotate() is a dummy function for highlighting only
			if (selectionPreview) {				
				moveRotate(hits.getTopHits());
			}
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			//if (selectionPreview) 
		{
			changedKernel = record(hits.getTopHits(), e);
		}
		break;

		case EuclidianView.MODE_POINT:
		case EuclidianView.MODE_POINT_ON_OBJECT:
			// point() is dummy function for highlighting only
			if (selectionPreview) {
				if (mode==EuclidianView.MODE_POINT)
					hits.keepOnlyHitsForNewPointMode();

				point(hits);
			} else {
				GeoElement[] ret0 = { null };
				ret0[0] = hits.getFirstHit(GeoPoint.class);
				ret = ret0;
				clearSelection(selectedPoints);
			}
			break;

			// copy geo to algebra input
		case EuclidianView.MODE_SELECTION_LISTENER:
			boolean addToSelection = e != null && (Application.isControlDown(e));
			geoElementSelected(hits.getTopHits(), addToSelection);
			break;

			// new line through two points
		case EuclidianView.MODE_JOIN:
			ret = join(hits);
			break;

			// new segment through two points
		case EuclidianView.MODE_SEGMENT:
			ret = segment(hits);
			break;

			// segment for point and number
		case EuclidianView.MODE_SEGMENT_FIXED:
			changedKernel = segmentFixed(hits);
			break;

			//	angle for two points and number
		case EuclidianView.MODE_ANGLE_FIXED:
			ret = angleFixed(hits);
			break;

		case EuclidianView.MODE_MIDPOINT:
			ret = midpoint(hits);
			break;

			// new ray through two points or point and vector
		case EuclidianView.MODE_RAY:
			ret = ray(hits);
			break;

			
		case EuclidianView.MODE_POLYLINE:
			ret = polyline(hits);
			break;
			
			// new polygon through points
		case EuclidianView.MODE_POLYGON:
			polygonRigid = false;
			ret = polygon(hits);
			break;

			// new polygon through points
		case EuclidianView.MODE_RIGID_POLYGON:
			polygonRigid = true;
			ret = polygon(hits);
			break;

			// new vector between two points
		case EuclidianView.MODE_VECTOR:
			ret = vector(hits);
			break;

			// intersect two objects
		case EuclidianView.MODE_INTERSECT:
			ret = intersect(hits);
			break;

			// new line through point with direction of vector or line
		case EuclidianView.MODE_PARALLEL:
			ret = parallel(hits);
			break;

			// Michael Borcherds 2008-04-08
		case EuclidianView.MODE_PARABOLA:
			ret = parabola(hits);
			break;

			// new line through point orthogonal to vector or line
		case EuclidianView.MODE_ORTHOGONAL:
			ret = orthogonal(hits);
			break;

			// new line bisector
		case EuclidianView.MODE_LINE_BISECTOR:
			ret = lineBisector(hits);
			break;

			// new angular bisector
		case EuclidianView.MODE_ANGULAR_BISECTOR:
			ret = angularBisector(hits);
			break;

			// new circle (2 points)
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
			// new semicircle (2 points)
		case EuclidianView.MODE_SEMICIRCLE:
			ret = circleOrSphere2(hits, mode);
			break;

		case EuclidianView.MODE_LOCUS:
			ret = locus(hits);
			break;

			// new circle (3 points)
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret = threePoints(hits, mode);
			break;

			// new conic (5 points)
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
			ret = conic5(hits);
			break;

			// relation query
		case EuclidianView.MODE_RELATION:
			relation(hits.getTopHits());			
			break;

			// new tangents
		case EuclidianView.MODE_TANGENTS:
			ret = tangents(hits.getTopHits());
			break;

		case EuclidianView.MODE_POLAR_DIAMETER:
			ret = polarLine(hits.getTopHits());
			break;

			// delete selected object
		case EuclidianView.MODE_DELETE:
			changedKernel = delete(hits.getTopHits());
			break;

		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			if (showHideObject(hits.getTopHits()))
				toggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_SHOW_HIDE_LABEL:
			if (showHideLabel(hits.getTopHits()))
				toggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			if (copyVisualStyle(hits.getTopHits()))
				toggleModeChangedKernel = true;
			break;

			//  new text or image
		case EuclidianView.MODE_TEXT:
		case EuclidianView.MODE_IMAGE:
			changedKernel = textImage(hits.getOtherHits(GeoImage.class, tempArrayList), mode, altDown); //e.isAltDown());
			break;

			// new slider
		case EuclidianView.MODE_SLIDER:
			changedKernel = slider();
			break;			

		case EuclidianView.MODE_MIRROR_AT_POINT:
			ret = mirrorAtPoint(hits.getTopHits());
			break;

		case EuclidianView.MODE_MIRROR_AT_LINE:
			ret = mirrorAtLine(hits.getTopHits());
			break;

		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			ret = mirrorAtCircle(hits.getTopHits());
			break;

		case EuclidianView.MODE_ATTACH_DETACH: // Michael Borcherds 2008-03-23
			changedKernel = attachDetach(hits.getTopHits(), e);
			break;

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			ret = translateByVector(hits.getTopHits());
			break;

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			ret = rotateByAngle(hits.getTopHits());
			break;

		case EuclidianView.MODE_DILATE_FROM_POINT:
			ret = dilateFromPoint(hits.getTopHits());
			break;

		case EuclidianView.MODE_FITLINE:
			ret = fitLine(hits);
			break;

		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
			changedKernel = circleOrSpherePointRadius(hits);
			break;				

		case EuclidianView.MODE_ANGLE:
			ret = angle(hits.getTopHits());
			break;

		case EuclidianView.MODE_VECTOR_FROM_POINT:
			ret = vectorFromPoint(hits);
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
			ret = slope(hits);
			break;

		case EuclidianView.MODE_REGULAR_POLYGON:
			changedKernel = regularPolygon(hits);
			break;

		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:
			changedKernel = showCheckBox(hits);
			break;

		case EuclidianView.MODE_BUTTON_ACTION:
			changedKernel = button(false);
			break;

		case EuclidianView.MODE_TEXTFIELD_ACTION:
			changedKernel = button(true);
			break;

		case EuclidianView.MODE_PEN:
			changedKernel = pen();
			break;

			// Michael Borcherds 2008-03-13	
		case EuclidianView.MODE_COMPASSES:
			ret = compasses(hits);
			break;
			
			
		case EuclidianView.MODE_FUNCTION_INSPECTOR:
			changedKernel = functionInspector(hits);
			break;

		default:
			// do nothing
		}

		if (ret != null)
			selectGeos(ret);

		if (!changedKernel)
			return ret != null;

		return changedKernel;
	}
	
	public void selectGeos(GeoElement[] geos) {
		for (int i = 0; i < geos.length; i++)
		{
			selectedGeos.add(geos[i]);
			app.addSelectedGeo(geos[i], false);
		}
	}

	public void selectGeos(ArrayList<GeoElement> geos) {
		selectedGeos.addAll(geos);
		app.addSelectedGeos(geos, false);
	}

	// process mode and return whether kernel was changed
	public final boolean processMode(Hits hits, MouseEvent e) {
		boolean changedKernel = false;

		if (hits==null)
			hits = new Hits();

		changedKernel = switchModeForProcessMode(hits, e);

		// update preview
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updatePreview();
			if (mouseLoc != null) {
				xRW = view.toRealWorldCoordX(mouseLoc.x);
				yRW = view.toRealWorldCoordY(mouseLoc.y);
				
				processModeLock();
						
				view.getPreviewDrawable().updateMousePos(xRW, yRW);			
			}
			view.repaintEuclidianView();
		}

		return changedKernel;
	}
	
	
	public void processModeLock(){
		
		// make previewable "lock" onto points & paths
		// priority for highlighted geos (points)
		Hits getTopHits = highlightedGeos.getTopHits();
		// nothing highlighted, look at eg circles, lines
		if (getTopHits.size() == 0) getTopHits = view.getHits().getTopHits();

		
		if (getTopHits.size() > 0 ) {
			GeoElement geo = (GeoElement)getTopHits.get(0);
			if (geo.isPath()) {
				Path path = (Path)geo;
				GeoPoint p = kernel.Point(null, path, xRW, yRW, false);
				p.update();
				xRW = p.inhomX;
				yRW = p.inhomY;
			} else if (geo.isGeoPoint()) {
				Coords coords = ((GeoPointND) geo).getInhomCoordsInD(2);
				xRW = coords.getX();
				yRW = coords.getY();
			} else transformCoords(); // grid lock
		} else transformCoords(); // grid lock
	}
	
	

	public void mouseEntered(MouseEvent e) {

		if (textfieldHasFocus) return;

		initToolTipManager();
		initShowMouseCoords();
		view.mouseEntered();
	}

	public void mouseExited(MouseEvent e) {

		if (textfieldHasFocus) return;

		refreshHighlighting(null);
		resetToolTipManager();
		view.setAnimationButtonsHighlighted(false);
		view.setShowMouseCoords(false);
		mouseLoc = null;		
		view.repaintEuclidianView();
		view.mouseExited();
	}

	/*
	public void focusGained(FocusEvent e) {				
		initToolTipManager();
	}

	public void focusLost(FocusEvent e) {
		resetToolTipManager();
	}*/

	protected void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(app.getAllowToolTips());
	}

	protected void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */

	final protected void rotateObject(boolean repaint) {
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

	final protected void moveLabel() {
		movedLabelGeoElement.setLabelOffset(oldLoc.x + mouseLoc.x
				- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();  
		kernel.notifyRepaint();
	}

	protected void movePoint(boolean repaint) {
		movedGeoPoint.setCoords(xRW, yRW, 1.0);
		((GeoElement) movedGeoPoint).updateCascade();	
		movedGeoPointDragged = true;

		if (repaint)
			kernel.notifyRepaint();
	}

	final protected void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setCoords(movedGeoLine.x, movedGeoLine.y, 
				-(movedGeoLine.x * xRW + movedGeoLine.y * yRW));		
		if (repaint)
			movedGeoLine.updateRepaint();
		else
			movedGeoLine.updateCascade();	
	}

	final protected void moveVector(boolean repaint) {
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

	final protected void moveVectorStartPoint(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		P.setCoords(xRW, yRW, 1.0);

		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final protected void moveText(boolean repaint) {
		if (movedGeoText.isAbsoluteScreenLocActive()) {
			movedGeoText.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);			
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = (GeoPoint) movedGeoText.getStartPoint();
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

	final protected void moveImage(boolean repaint) {	
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

	final protected void moveConic(boolean repaint) {
		movedGeoConic.set(tempConic);
		movedGeoConic.translate(xRW - startPoint.x, yRW - startPoint.y);		

		if (repaint)
			movedGeoConic.updateRepaint();
		else
			movedGeoConic.updateCascade();
	}

	final protected void moveFunction(boolean repaint) {
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);		

		if (repaint)
			movedGeoFunction.updateRepaint();
		else
			movedGeoFunction.updateCascade();
	}

	final protected void moveBoolean(boolean repaint) {
		movedGeoBoolean.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
				oldLoc.y + mouseLoc.y-startLoc.y);

		if (repaint)
			movedGeoBoolean.updateRepaint();
		else
			movedGeoBoolean.updateCascade();
	}

	final protected void moveButton(boolean repaint) {
		movedGeoButton.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
				oldLoc.y + mouseLoc.y-startLoc.y);

		if (repaint)
			movedGeoButton.updateRepaint();
		else
			movedGeoButton.updateCascade();
	}

	final protected void moveNumeric(boolean repaint) {
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
		param = Kernel.roundToScale(param, movedGeoNumeric.getAnimationStep());
		double val = min + param;	

		if (movedGeoNumeric.getAnimationStep() > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = kernel.checkDecimalFraction(val);
		}

		if (movedGeoNumeric.isGeoAngle()) {
			if (val < 0) 
				val = 0;
			else if (val > Kernel.PI_2)
				val = Kernel.PI_2;
			
			val = kernel.checkDecimalFraction(val * Kernel.CONST_180_PI) / Kernel.CONST_180_PI;

		}

		// do not set value unless it really changed!
		if (movedGeoNumeric.getValue() == val)
			return;

		movedGeoNumeric.setValue(val);		
		movedGeoNumericDragged = true;

		//movedGeoNumeric.setAnimating(false); // stop animation if slider dragged

		//if (repaint)
		movedGeoNumeric.updateRepaint();
		//else
		//	movedGeoNumeric.updateCascade();
	}

	final protected void moveSlider(boolean repaint) {
		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			movedGeoNumeric.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - startPoint.x, yRW - startPoint.y);
		}		

		// don't cascade, only position of the slider has changed
		movedGeoNumeric.update();

		if (repaint)
			kernel.notifyRepaint();					
	}

	protected void moveDependent(boolean repaint) {

		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);

		startPoint.setLocation(xRW, yRW);

		// we don't specify screen coords for translation as all objects are Transformables
		GeoElement.moveObjects(translateableGeos, translationVec, new Coords(xRW, yRW, 0), null);		
		if (repaint)
			kernel.notifyRepaint();						
	}

	protected void moveMultipleObjects(boolean repaint) {		
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;

		// move all selected geos
		GeoElement.moveObjects(app.getSelectedGeos(), translationVec, new Coords(xRW, yRW, 0), null);									

		if (repaint)
			kernel.notifyRepaint();					
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

	/*
	protected void transformCoords() {
		transformCoords(false);
	} */

	protected void transformCoords() {
		// calc real world coords
		calcRWcoords();

		// if alt pressed, make sure slope is a multiple of 15 degrees
		if ((mode == EuclidianView.MODE_JOIN || mode == EuclidianView.MODE_SEGMENT
				|| mode == EuclidianView.MODE_RAY || mode == EuclidianView.MODE_VECTOR
				|| mode == EuclidianView.MODE_POLYGON || mode == EuclidianView.MODE_POLYLINE)
				&& useLineEndPoint && lineEndPoint != null) {
			xRW = lineEndPoint.x;
			yRW = lineEndPoint.y;
			return;
		}

		if (mode == EuclidianView.MODE_MOVE && 
				moveMode == MOVE_NUMERIC) return; // Michael Borcherds 2008-03-24 bugfix: don't want grid on

		//	point capturing to grid
		double pointCapturingPercentage = 1;
		switch (view.getPointCapturingMode()) {	
		case EuclidianView.POINT_CAPTURING_AUTOMATIC:				
			if (!view.isGridOrAxesShown())break;

		case EuclidianView.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;

		case EuclidianView.POINT_CAPTURING_ON_GRID:
			
			xRW += transformCoordsOffset[0];
			yRW += transformCoordsOffset[1];

			switch (view.getGridType()) {
			case EuclidianView.GRID_ISOMETRIC:

				// isometric Michael Borcherds 2008-04-28
				// iso grid is effectively two rectangular grids overlayed (offset)
				// so first we decide which one we're on (oddOrEvenRow)
				// then compress the grid by a scale factor of root3 horizontally to make it square.

				double root3=Math.sqrt(3.0);
				double isoGrid=view.getGridDistances(0);
				int oddOrEvenRow = (int)Math.round(2.0*Math.abs(yRW - Kernel.roundToScale(yRW, isoGrid))/isoGrid);

				//Application.debug(oddOrEvenRow);

				if (oddOrEvenRow == 0)
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3, isoGrid);
					double y = Kernel.roundToScale(yRW, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW/root3);
					double b = Math.abs(y - yRW);
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = x*root3 - transformCoordsOffset[0];
						yRW = y - transformCoordsOffset[1];
					} else {
						xRW -= transformCoordsOffset[0];
						yRW -= transformCoordsOffset[1];
					}

				}
				else
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3- view.getGridDistances(0)/2, isoGrid);
					double y = Kernel.roundToScale(yRW- isoGrid/2, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - (xRW/root3- isoGrid/2));
					double b = Math.abs(y - (yRW-isoGrid/2));
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = (x+ isoGrid/2)*root3 - transformCoordsOffset[0];
						yRW = y+ isoGrid/2 - transformCoordsOffset[1];
					} else {
						xRW -= transformCoordsOffset[0];
						yRW -= transformCoordsOffset[1];
					}

				}
				break;

			case EuclidianView.GRID_CARTESIAN:

				// X = (x, y) ... next grid point
				
				double x = Kernel.roundToScale(xRW, view.getGridDistances(0));
				double y = Kernel.roundToScale(yRW, view.getGridDistances(1));
				
				
				
				
				// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);

				if (a < view.getGridDistances(0) * pointCapturingPercentage
						&& b < view.getGridDistances(1) *  pointCapturingPercentage) {
					xRW = x - transformCoordsOffset[0];
					yRW = y - transformCoordsOffset[1];
				} else {
					xRW -= transformCoordsOffset[0];
					yRW -= transformCoordsOffset[1];
				}
				break;
		
			
		
		case EuclidianView.GRID_POLAR:
				
			// r = get nearest grid circle radius
			double r = GeoVec2D.length(xRW, yRW);
			double r2 = Kernel.roundToScale(r, view.getGridDistances(0));
			
			// get nearest radial gridline angle
			double angle = Math.atan2(yRW, xRW);
			double angleOffset = angle % view.getGridDistances(2);
			if(angleOffset < view.getGridDistances(2)/2)
				angle = angle - angleOffset;
			else
				angle = angle - angleOffset + view.getGridDistances(2);

			// get grid point
			double x1 = r2*Math.cos(angle);
			double y1 = r2*Math.sin(angle);
			
			// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
			double a1 = Math.abs(x1 - xRW);
			double b1 = Math.abs(y1 - yRW);

			if (a1 < view.getGridDistances(0) * pointCapturingPercentage
					&& b1 < view.getGridDistances(1) *  pointCapturingPercentage) {
				xRW = x1 - transformCoordsOffset[0];
				yRW = y1 - transformCoordsOffset[1];
			} else {
				xRW -= transformCoordsOffset[0];
				yRW -= transformCoordsOffset[1];
			}
			break;
		}
			
			

		default:
		}
	}

	/*
	final protected void transformCoords(boolean usePointCapturing) {
		// calc real world coords
		calcRWcoords();

		if (usePointCapturing) {	
			double pointCapturingPercentage = 1;
			switch (view.getPointCapturingMode()) {		
				case EuclidianView.POINT_CAPTURING_AUTOMATIC:
					if (!view.isGridOrAxesShown())break;

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
	 */

	protected void calcRWcoords() {
		xRW = (mouseLoc.x - view.getXZero()) * view.getInvXscale();
		yRW = (view.getYZero() - mouseLoc.y) * view.getInvYscale();
	}

	protected void setMouseLocation(MouseEvent e) {
		mouseLoc = e.getPoint();

		altDown = e.isAltDown();

		if (mouseLoc.x < 0)
			mouseLoc.x = 0;
		else if (mouseLoc.x > view.getViewWidth())
			mouseLoc.x = view.getViewWidth();
		if (mouseLoc.y < 0)
			mouseLoc.y = 0;
		else if (mouseLoc.y > view.getViewHeight())
			mouseLoc.y = view.getViewHeight();
	}

	/***************************************************************************
	 * mode implementations
	 * 
	 * the following methods return true if a factory method of the kernel was
	 * called
	 **************************************************************************/

	private boolean allowPointCreation() {
		return  mode == EuclidianView.MODE_POINT || 
		mode == EuclidianView.MODE_POINT_ON_OBJECT ||
		app.isOnTheFlyPointCreationActive(); 
	}

	final protected boolean createNewPoint(Hits hits,
			boolean onPathPossible, boolean intersectPossible, boolean doSingleHighlighting) {

		// inRegionpossible must be false so that the Segment Tool creates a point on the edge of a circle
		return createNewPoint(hits,onPathPossible, false, intersectPossible,  doSingleHighlighting);		
	}

	// create new point at current position if hits is null
	// or on path
	// or intersection point
	// returns wether new point was created or not
	final protected boolean createNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, boolean doSingleHighlighting) {

		if (!allowPointCreation()) 
			return false;



		GeoPointND point = getNewPoint(hits, onPathPossible, inRegionPossible, intersectPossible, doSingleHighlighting);

		if (point != null) {

			updateMovedGeoPoint(point);

			movedGeoElement = getMovedGeoPoint();
			moveMode = MOVE_POINT;
			view.setDragCursor();
			if (doSingleHighlighting)
				doSingleHighlighting(getMovedGeoPoint());
			POINT_CREATED = true;

			return true;
		} else {
			moveMode = MOVE_NONE;
			POINT_CREATED = false;
			return false;
		}
	}


	// creates or get the new point (used for 3D)
	protected GeoPointND getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			boolean doSingleHighlighting) {

		return updateNewPoint(false, hits, 
				onPathPossible, inRegionPossible, intersectPossible, 
				doSingleHighlighting, true);
	}
	
	protected Hits getRegionHits(Hits hits){
		return hits.getHits(Region.class, tempArrayList);
	}

		
	// update the new point (used for preview in 3D)
	public GeoPointND updateNewPoint(
			boolean forPreviewable,
			Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean chooseGeo) {

		// create hits for region
		Hits regionHits = getRegionHits(hits);//hits.getHits(Region.class, tempArrayList);

		// only keep polygon in hits if one side of polygon is in hits too
		// removed: Point Tool creates Point on edge of Polygon
		if (mode != EuclidianView.MODE_POINT
				&& mode != EuclidianView.MODE_POINT_ON_OBJECT
				&& !hits.isEmpty())
			hits.keepOnlyHitsForNewPointMode();

		Path path = null;	
		Region region = null;
		boolean createPoint = true;
		if (hits.containsGeoPoint()){
			createPoint = false;
			if (forPreviewable){
				createNewPoint((GeoPointND) hits.getHits(GeoPointND.class, tempArrayList).get(0));				
			}
		}


		GeoPointND point = null;

		
		//	try to get an intersection point
		if (createPoint && intersectPossible) {
			GeoPointND intersectPoint = getSingleIntersectionPoint(hits);
			if (intersectPoint != null) {
				if (!forPreviewable){
					point = intersectPoint;
					// we don't use an undefined or infinite
					// intersection point
					if (!point.showInEuclidianView()) {
						point.remove();
					} else
						createPoint = false;
				}else{
					createNewPointIntersection(intersectPoint);
					createPoint = false;
				}
			}
		}



		// check for paths and regions
		if (createPoint) {

			// check if point lies in a region and if we are allowed to place a point
			// in a region
			if (!regionHits.isEmpty()) {
				if (inRegionPossible) {
					if (chooseGeo)
						region = (Region) chooseGeo(regionHits, true);
					else
						region = (Region) regionHits.get(0);
					if (region != null){
						if (((GeoElement) region).isGeoPolygon()){
							GeoSegmentND [] sides = ((GeoPolygon) region).getSegments();
							boolean sideInHits = false;
							for (int k=0; k < sides.length; k++) 
								sideInHits = sideInHits || hits.remove(sides[k]);
							if (!sideInHits)
								createPoint = true;	
							else{
								createPoint = false;	
								region = null;
							}
						}
					}else
						createPoint = true;
				} else {
					createPoint = true;
					// if inRegionPossible is false, the point is created as a free point
				}

			}


			//check if point lies on path and if we are allowed to place a point
			// on a path			
			Hits pathHits = hits.getHits(Path.class, tempArrayList);
			if (!pathHits.isEmpty()) {
				if (onPathPossible) {
					if (chooseGeo)
						path = (Path) chooseGeo(pathHits, true);
					else
						path = (Path) pathHits.get(0);
					createPoint = path != null;
				} else {
					createPoint = true; // create as free point if onPathPossible=false
					//Application.printStacktrace("");
				}
			}

		}



		//Application.debug("createPoint 3 = "+createPoint);

		if (createPoint) {
			transformCoords(); // use point capturing if on
			//branches reordered to prefer region if possible (Z.Konecny, 2010-07-30)
			if (region == null || !inRegionPossible) {
				if (path == null){
					point = createNewPoint(forPreviewable);
					view.setShowMouseCoords(true);
				} else {
					point = createNewPoint(forPreviewable, path);
				}
			} else {
				point = createNewPoint(forPreviewable, region);
			}
		}

		return point;
	}

	/** only used in 3D */
	protected void createNewPoint(GeoPointND sourcePoint){


	}

	/** only used in 3D */
	protected void createNewPointIntersection(GeoPointND intersectionPoint){
	}
	

	protected GeoPointND createNewPoint(boolean forPreviewable){
		GeoPointND ret = kernel.Point(null, xRW, yRW);
		return ret;
	}

	protected GeoPointND createNewPoint(boolean forPreviewable, Path path){
		return createNewPoint(forPreviewable, path, xRW, yRW);
	}
	
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path, double x, double y){
		
		if (((GeoElement) path).isGeoElement3D()) //check if the path is not a 3D element
			return (GeoPointND) kernel.getManager3D().Point3D(null, path, x, y, 0);
		else
			return createNewPoint2D(forPreviewable, path, x, y);

	}
	
	protected GeoPointND createNewPoint2D(boolean forPreviewable, Path path, double x, double y){
		return kernel.Point(null, path, x, y, true);
	}
	
	
	
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region){
		return createNewPoint(forPreviewable, region, xRW, yRW);
	}
	

	protected GeoPointND createNewPoint(boolean forPreviewable, Region region, double x, double y){
		
		if (((GeoElement) region).isGeoElement3D()) //check if the path is not a 3D element
			return (GeoPointND) kernel.getManager3D().Point3DIn(null, region, x, y, 0);
		else
			return createNewPoint2D(forPreviewable, region, x, y);

	}
	

	protected GeoPointND createNewPoint2D(boolean forPreviewable, Region region, double x, double y){
		GeoPointND ret = kernel.PointIn(null, region, x, y, true);
		return ret;
	}
	
	protected void updateMovedGeoPoint(GeoPointND point){
		movedGeoPoint = point;
	}







	// get two points and create line through them
	final protected GeoElement[] join(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		//Application.debug("addSelectedPoint : "+hits+"\nselectedPoints = "+selectedPoints);
		if (selPoints() == 2) {
			// fetch the two selected points
			return join();
		}
		return null;
	}

	// fetch the two selected points
	/*
	protected void join(){
		GeoPoint[] points = getSelectedPoints();
		GeoLine line = kernel.Line(null, points[0], points[1]);
	}
	*/
	
	protected GeoElement[] join(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
			ret[0] = getKernel().getManager3D().Line3D(null,points[0], points[1]);
		else
			ret[0] = getKernel().Line(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}

	private void recordSingleObjectToSpreadSheet(GeoElement geo) {
		int i = 1;
		while (kernel.lookupLabel("A"+i) != null) i++;

		kernel.getApplication().getGuiManager().setScrollToShow(true);
		if (geo.isGeoPoint()) {

			// find first empty pair of cells in columns A, B
			while (kernel.lookupLabel("A"+i) != null || kernel.lookupLabel("B"+i) != null) i++;

			GeoPoint p = (GeoPoint)geo;

			GeoNumeric num = new GeoNumeric(kernel.getConstruction(), "A"+i, p.inhomX);
			num.setAuxiliaryObject(true);
			num = new GeoNumeric(kernel.getConstruction(), "B"+i, p.inhomY);
			num.setAuxiliaryObject(true);				
		} else if (geo.isGeoVector()) {
			// find first empty pair of cells in columns A, B
			while (kernel.lookupLabel("A"+i) != null || kernel.lookupLabel("B"+i) != null) i++;

			GeoVector v = (GeoVector)geo;

			double [] coords = new double[2];
			v.getInhomCoords(coords);

			GeoNumeric num = new GeoNumeric(kernel.getConstruction(), "A"+i, coords[0]);
			num.setAuxiliaryObject(true);				
			num = new GeoNumeric(kernel.getConstruction(), "B"+i, coords[1]);
			num.setAuxiliaryObject(true);				

		} else if (geo.isGeoNumeric()) {

			GeoNumeric num = new GeoNumeric(kernel.getConstruction(), "A"+i, ((NumberValue)geo).getDouble());
			num.setAuxiliaryObject(true);
		}
		kernel.getApplication().getGuiManager().setScrollToShow(false);

	}	

	protected void handleMousePressedForRecordToSpreadsheetMode(MouseEvent e) {	
		GeoElement geo;
		Hits hits;

		view.setHits(mouseLoc);
		Hits viewHits= view.getHits();
		Hits pointHits = viewHits.getHits(GeoPoint.class, tempArrayList);
		Hits vectorHits = viewHits.getHits(GeoVector.class, tempArrayList2);
		Hits numberHits = viewHits.getHits(GeoNumeric.class, tempArrayList3);
		//Application.debug(pointHits.size()+"");
		// we need the object to record
		if (recordObject == null) {

			// alt-click on object: special mode, just put it straight in the spreadsheet in column A (and B for Points)		
			if (e.isAltDown()) {

				if (pointHits != null)
					recordSingleObjectToSpreadSheet((GeoPoint)pointHits.get(0));
				else if (vectorHits != null)
					recordSingleObjectToSpreadSheet((GeoVector)vectorHits.get(0));
				else if (pointHits != null)
					recordSingleObjectToSpreadSheet((GeoNumeric)numberHits.get(0));

				return;

			}


			if (!pointHits.isEmpty()) {
				recordObject = (GeoPoint)pointHits.get(0);
				//app.addSelectedGeo(recordObject);
				recordObject.setSelected(true);
				resetSpreadsheetRecording();
			} else if (!vectorHits.isEmpty()) {
				recordObject = (GeoVector)vectorHits.get(0);
				//app.addSelectedGeo(recordObject);
				recordObject.setSelected(true);
				resetSpreadsheetRecording();
			}
		}
		else {	// recordObject != null
			hits = viewHits.getPointVectorNumericHits();
			// got recordObject again: deselect
			if (!hits.isEmpty() && hits.contains(recordObject) &&
					// if you drag a point at the end of a vector, we don't want to deselect the vector:
					(!recordObject.isGeoVector() || (recordObject.isGeoVector() && noPointsIn(hits)))		
			) {
				//app.removeSelectedGeo(recordObject);
				recordObject.setSelected(false);
				app.getGuiManager().removeSpreadsheetTrace(recordObject);
				recordObject = null;
				moveMode = MOVE_NONE;
				return;
			}

			//moveModeSelectionHandled = true;

			if (getMovedGeoPoint() != null) getMovedGeoPoint().setSelected(false);
			if (movedGeoNumeric != null) movedGeoNumeric.setSelected(false);

			//hits = view.getHits(hits, GeoPoint.class, tempArrayList);
			if (!pointHits.isEmpty() && pointHits.contains(getMovedGeoPoint()))
			{
				getMovedGeoPoint().setSelected(true);
				moveMode = MOVE_POINT;
			}
			else if (!pointHits.isEmpty()) {
				movedGeoPoint = (GeoPointND)pointHits.get(0);
				((GeoElement) movedGeoPoint).setSelected(true);
				moveMode = MOVE_POINT;
			}
			else if (!numberHits.isEmpty() && numberHits.contains(movedGeoNumeric))
			{
				movedGeoNumeric.setSelected(true);
				moveMode = MOVE_NUMERIC;
				startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
			}
			else if (!numberHits.isEmpty()) {
				movedGeoNumeric = (GeoNumeric)numberHits.get(0);
				movedGeoNumeric.setSelected(true);
				moveMode = MOVE_NUMERIC;
				startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
			}
			else {
				moveMode = MOVE_NONE;
			}

		}					
	}

	private boolean noPointsIn(Hits hits)
	{
		for (int i = 0 ; i < hits.size(); i++) {
			if ( ((GeoElement)(hits.get(i))).isGeoPoint()) return false;
		}
		return true;
	}

	final protected boolean record(Hits hits, MouseEvent e) {
		if (hits.isEmpty())
			return false;

		// check how many interesting hits we have
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			Hits goodHits = new Hits();
			//goodHits.add(selectedGeos);
			hits.getHits(GeoPoint.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			hits.getHits(GeoNumeric.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			hits.getHits(GeoVector.class, tempArrayList);
			goodHits.addAll(tempArrayList);

			if (goodHits.size() > 2 - selGeos()) {
				//  choose one geo, and select only this one
				GeoElement geo = chooseGeo(goodHits, true);
				hits.clear();
				hits.add(geo);				
			} else {
				hits = goodHits;
			}
		}			

		addSelectedPoint(hits, 1, true);
		addSelectedNumeric(hits, 1, true);
		addSelectedVector(hits, 1, true);	

		/*
		if (recordObject != null && selPoints() == 1 && selPoints() == 1 && points[0] == recordObject)
		{
			recordObject.setSelected(false);
			recordObject = null;
			return true;
		}*/

		if (recordObject == null) {
			if (selPoints() == 1) {
				GeoPoint[] points = getSelectedPoints();

				if (e.isAltDown()) {
					recordSingleObjectToSpreadSheet(points[0]);
				}
				else
				{
					recordObject = points[0];
					resetSpreadsheetRecording();
				}
			}
			else if (selNumbers() == 1) {
				GeoNumeric[] nums = getSelectedNumbers();
				if (e.isAltDown()) {
					recordSingleObjectToSpreadSheet(nums[0]);
				}
				else {
					recordObject = nums[0];
					resetSpreadsheetRecording();
				}
			}
			else if (selVectors() == 1) {
				GeoVector[] vecs = getSelectedVectors();
				if (e.isAltDown()) {
					recordSingleObjectToSpreadSheet(vecs[0]);
				}
				else {
					recordObject = vecs[0];
					resetSpreadsheetRecording();
				}
			}
			if (recordObject != null) recordObject.setSelected(true);

			//return true;
		} else { // recordObject != null
			if (selPoints() == 1)
			{
				GeoPoint[] points = getSelectedPoints();
				if (points[0] == recordObject) {
					recordObject.setSelected(false);
				//	app.getGuiManager().removeSpreadsheetTrace(recordObject);
					recordObject = null;
					resetSpreadsheetRecording();
				}
			}
			else if (selNumbers() == 1) {
				GeoNumeric[] nums = getSelectedNumbers();
				if (recordObject == nums[0]) {
					recordObject.setSelected(false);
				//	app.getGuiManager().removeSpreadsheetTrace(recordObject);
					recordObject = null;
					resetSpreadsheetRecording();
				}
			}
			else if (selVectors() == 1) {
				GeoVector[] vecs = getSelectedVectors();
				if (recordObject == vecs[0]) {
					recordObject.setSelected(false);
				//	app.getGuiManager().removeSpreadsheetTrace(recordObject);
					recordObject = null;
					resetSpreadsheetRecording();
				}
			}
			if (recordObject != null) recordObject.setSelected(true);
			//return true;
		}


		if (selGeos() > 1)
			return false;

		return false;
	}

	private void resetSpreadsheetRecording() {
		moveMode = MOVE_NONE;
		if (recordObject != null) {
			recordObject.resetTraceColumns();
			
			//G.Sturr 2010-4-22
			app.getGuiManager().addSpreadsheetTrace(recordObject);
			
			recordObject.updateRepaint(); // force repaint to put first point in spreadsheet
		}
		movedGeoPoint = null;
		movedGeoNumeric = null;
		//view.resetTraceRow();	
		
		//G.Sturr 2010-4-22
		if (recordObject == null) {
			app.getGuiManager().removeSpreadsheetTrace(recordObject);
		}
		
		
	}

	//	get two points and create line through them
	final protected GeoElement[] segment(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			return segment();
			/*
			GeoPoint[] points = getSelectedPoints();
			kernel.Segment(null, points[0], points[1]);
			 */
		}
		return null;
	}


	// fetch the two selected points for segment
	protected GeoElement[] segment(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
			ret[0] = getKernel().getManager3D().Segment3D(null,points[0], points[1]);
		else
			ret[0] = getKernel().Segment(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}

	// get two points and create vector between them
	final protected GeoElement[] vector(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			/*
			GeoPoint[] points = getSelectedPoints();
			kernel.Vector(null, points[0], points[1]);
			*/
			return vector();
		}
		return null;
	}
	
	// fetch the two selected points for vector
	protected GeoElement[] vector(){
		GeoPoint[] points = getSelectedPoints();
		GeoElement[] ret = { null };
		ret[0] = kernel.Vector(null, points[0], points[1]);
		return ret;
	}

	//	get two points and create ray with them
	final protected GeoElement[] ray(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			/*
			GeoPoint[] points = getSelectedPoints();
			kernel.Ray(null, points[0], points[1]);
			 */
			return ray();
		}

		return null;
	}


	// fetch the two selected points for ray
	protected GeoElement[] ray(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
			ret[0] = getKernel().getManager3D().Ray3D(null,points[0], points[1]);
		else
			ret[0] = getKernel().Ray(null, (GeoPoint) points[0], (GeoPoint) points[1]);
		return ret;
	}


	//	get at least 3 points and create polygon with them
	final protected GeoElement[] polygon(Hits hits) {
		if (hits.isEmpty())
			return null;

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
			&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				return polygon();
				//kernel.Polygon(null, getSelectedPoints());
			}
		}

		// points needed
		if (polygonRigid && selPoints() > 0) { // only want free points withput children for rigid polys (apart from first)
			GeoElement geo = chooseGeo(hits, false);
			if (geo == null || !geo.isGeoPoint() || !geo.isIndependent() || geo.hasChildren()) {
				//addToSelectionList(selectedPoints, geo, GeoPolygon.POLYGON_MAX_POINTS);
				return null;
			}
		}
		addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false);
		return null;
	}

	//	get at least 3 points and create polygon with them
	final protected GeoElement[] polyline(Hits hits) {
		if (hits.isEmpty())
			return null;

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
			&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				return kernel.PolyLine(null, getSelectedPoints());
			}
		}

		// points needed
		addSelectedPoint(hits, GeoPolyLine.POLYLINE_MAX_POINTS, false);
		return null;
	}


	// build polygon	
	protected GeoElement[] polygon(){
		if (polygonRigid)
			return kernel.RigidPolygon(null, getSelectedPoints());
		else{
			//check if there is a 3D point
			GeoPointND[] pointsND = getSelectedPointsND();
			GeoPoint[] points = new GeoPoint[pointsND.length];
			boolean point3D = false;
			for (int i=0; i<pointsND.length && !point3D; i++)
				if (((GeoElement) pointsND[i]).isGeoElement3D())
					point3D=true;
				else 
					points[i]=(GeoPoint) pointsND[i];
			if (point3D)
				return kernel.getManager3D().Polygon3D(null, pointsND);
			else
				return kernel.Polygon(null, points);
		}
	}

	// get two objects (lines or conics) and create intersection point
	protected GeoElement[] intersect(Hits hits) {
		if (hits.isEmpty())
			return null;

		// when two objects are selected at once then only one single
		// intersection point should be created
		boolean singlePointWanted = selGeos() == 0;

		// check how many interesting hits we have
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			Hits goodHits = new Hits();
			//goodHits.add(selectedGeos);
			hits.getHits(GeoLine.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			hits.getHits(GeoConic.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			hits.getHits(GeoFunction.class, tempArrayList);
			goodHits.addAll(tempArrayList);

			//if (goodHits.size() > 2 - selGeos()) {
			//	//  choose one geo, and select only this one
			//	GeoElement geo = chooseGeo(goodHits, true);
			//	hits.clear();
			//	hits.add(geo);				
			//} else {
				hits = goodHits;
			//}
		}			

		// get lines, conics and functions
		// now there's no popup chooser, when we use the intersect Tool where multiple objects intersect
		// just choose any 2
		addSelectedLine(hits, 10, true);
		addSelectedConic(hits, 10, true);
		addSelectedFunction(hits, 10, true);	
		addSelectedImplicitpoly(hits, 10, true);
		
		singlePointWanted = singlePointWanted && selGeos() == 2;

		//if (selGeos() > 2)
		//	return false;

		// two lines
		if (selLines() >= 2) {
			GeoLine[] lines = getSelectedLines();
			GeoElement[] ret = { null };
			ret[0] = kernel.IntersectLines(null, lines[0], lines[1]);
			return ret;
		}
		// two conics
		else if (selConics() >= 2) {
			GeoConic[] conics = getSelectedConics();
			GeoElement[] ret = { null };
			if (singlePointWanted)
				ret[0] = kernel.IntersectConicsSingle(null, conics[0], conics[1], xRW,
						yRW);
			else
				ret = kernel.IntersectConics(null, conics[0], conics[1]);
			return ret;
		} else if (selFunctions() >= 2) {
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
		else if (selLines() >= 1 && selConics() >= 1) {
			GeoConic[] conic = getSelectedConics();
			GeoLine[] line = getSelectedLines();
			GeoElement[] ret = { null };
			if (singlePointWanted)
				ret[0] = kernel.IntersectLineConicSingle(null, line[0], conic[0], xRW,
						yRW);
			else
				ret = kernel.IntersectLineConic(null, line[0], conic[0]);

			return ret;
		}
		// line and function
		else if (selLines() >= 1 && selFunctions() >= 1) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			GeoElement[] ret = { null };
			if (fun[0].isPolynomialFunction(false)) {
				if (singlePointWanted)
					ret[0] = kernel.IntersectPolynomialLineSingle(null, fun[0], line[0],
							xRW, yRW);
				else
					ret = kernel.IntersectPolynomialLine(null, fun[0], line[0]);
			} else {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				ret[0] = kernel.IntersectFunctionLine(null, fun[0], line[0], startPoint);
			}
			return ret;
		// function and conic
		}else if (selFunctions() >= 1 && selConics() >= 1){
			GeoConic[] conic = getSelectedConics();
			GeoFunction[] fun = getSelectedFunctions();
			if (fun[0].isPolynomialFunction(false)){
				if (singlePointWanted)
					kernel.IntersectPolynomialConicSingle(null, fun[0], conic[0],
							xRW, yRW);
				else
					kernel.IntersectPolynomialConic(null, fun[0], conic[0]);
			}else{
				return null;
			}
		}else if (selImplicitpoly() >= 1){
			if (selFunctions() >=1 ){
				GeoImplicitPoly p=getSelectedImplicitpoly()[0];
				GeoFunction fun=getSelectedFunctions()[0];
				if (fun.isPolynomialFunction(false)){
					if (singlePointWanted){
						kernel.IntersectImplicitpolyPolynomialSingle(null, p, fun, xRW, yRW);
					}else
						kernel.IntersectImplicitpolyPolynomial(null, p, fun);
				}else
					return null;
			}else if (selLines() >= 1){
				GeoImplicitPoly p=getSelectedImplicitpoly()[0];
				GeoLine l=getSelectedLines()[0];
				if (singlePointWanted){
					kernel.IntersectImplicitpolyLineSingle(null, p, l, xRW, yRW);
				}else
					kernel.IntersectImplicitpolyLine(null, p, l);
			}else if (selConics() >= 1){
				GeoImplicitPoly p=getSelectedImplicitpoly()[0];
				GeoConic c=getSelectedConics()[0];
				if (singlePointWanted){
					kernel.IntersectImplicitpolyConicSingle(null, p, c, xRW, yRW);
				}else
					kernel.IntersectImplicitpolyConic(null, p, c);
			}else
				return null;
		}else if (selImplicitpoly() >= 2){
			GeoImplicitPoly[] p=getSelectedImplicitpoly();
			if (singlePointWanted){
				kernel.IntersectImplicitpolysSingle(null, p[0], p[1], xRW, yRW);
			}else
				kernel.IntersectImplicitpolys(null, p[0], p[1]);
		}
		return null;
	}

	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		if (hits.isEmpty() || hits.size() < 2)
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
			else if (b.isGeoConic() && !a.isEqual(b)) {
				return kernel.IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, xRW, yRW);
			}
			else
				return null;
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = ((GeoFunctionable) a).getGeoFunction();
		    if (b.isGeoLine()) {
				// line and function
				if (aFun.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, aFun,
							(GeoLine) b, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, aFun, (GeoLine) b,
							startPoint);
				}
			} 
		    else if (b.isGeoFunctionable()) {
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
			} 		    
		    else
				return null;
		} else
			return null;
	}

	// get point and line or vector;
	// create line through point parallel to line or vector
	final protected GeoElement[] parallel(Hits hits) {
		if (hits.isEmpty())
			return null;

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
			GeoElement[] ret = { null };
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new line
				if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) vectors[0]).isGeoElement3D())
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(null,points[0], vectors[0]);
				else
					ret[0] = kernel.Line(null, (GeoPoint) points[0], (GeoVector) vectors[0]);
				return ret;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) lines[0]).isGeoElement3D())
					ret[0] = (GeoElement) getKernel().getManager3D().Line3D(null,points[0], lines[0]);
				else
					ret[0] = getKernel().Line(null, (GeoPoint) points[0], (GeoLine) lines[0]);
				return ret;
			}
		}
		return null;
	}

	// get point and line 
	// create parabola (focus and directrix)
	// Michael Borcherds 2008-04-08
	final protected GeoElement[] parabola(Hits hits) {
		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			addSelectedLine(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new parabola
				GeoElement[] ret = { null };
				ret[0] = kernel.Parabola(null, points[0], lines[0]);
				return ret;
			}
		}
		return null;
	}

	// get point and line or vector;
	// create line through point orthogonal to line or vector
	final protected GeoElement[] orthogonal(Hits hits) {
		if (hits.isEmpty())
			return null;
		
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false, GeoVector.class);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new line
				GeoElement[] ret = { null };
				//no defined line through a point and orthogonal to a vector in 3D
				if (((GeoElement) points[0]).isGeoElement3D())// || ((GeoElement) vectors[0]).isGeoElement3D())
					return null;
				else
					ret[0] = kernel.OrthogonalLine(null, (GeoPoint) points[0], (GeoVector) vectors[0]);
				return ret;

			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				GeoElement[] ret = { null };
				if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) lines[0]).isGeoElement3D())
					ret[0] = (GeoElement) getKernel().getManager3D().OrthogonalLine3D(null,points[0], lines[0]);
				else
					ret[0] = getKernel().OrthogonalLine(null, (GeoPoint) points[0], (GeoLine) lines[0]);
				return ret;
			}
		}
		return null;
	}
	
	
	// get two points, line segment or conic
	// and create midpoint/center for them/it
	final protected GeoElement[] midpoint(Hits hits) {
		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0) {
			addSelectedSegment(hits, 1, false); // segment needed
			if (selSegments() == 0)
				addSelectedConic(hits, 1, false); // conic needed
		}

		GeoElement[] ret = { null };
		
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPointND[] points = getSelectedPointsND();
			if (((GeoElement) points[0]).isGeoElement3D() || ((GeoElement) points[1]).isGeoElement3D())
				ret[0] = (GeoElement) kernel.getManager3D().Midpoint(null, points[0], points[1]);
			else
				ret[0] = kernel.Midpoint(null, (GeoPoint) points[0], (GeoPoint) points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			ret[0] = kernel.Midpoint(null, segments[0]);
			return ret;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConic[] conics = getSelectedConics();
			ret[0] = kernel.Center(null, conics[0]);
			return ret;
		}
		return null;
	}

	
	
	// get a function and open the function inspector
	final protected boolean functionInspector(Hits hits) {
		if (hits.isEmpty())
			return false;

		boolean hitFunction = (addSelectedFunction(hits, 1, false) != 0);

		if(selFunctions() == 1){
			GeoFunction[] functions = getSelectedFunctions();
			
			app.getGuiManager().showFunctionInspector(functions[0]);
			app.setMoveMode();
		}
		
		
		return false;
	}
	
	
	
	// get two points and create line bisector for them
	// or get line segment and create line bisector for it
	final protected GeoElement[] lineBisector(Hits hits) {
		if (hits.isEmpty())
			return null;
		boolean hitPoint = false;

		if (selSegments() == 0)
			hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0)
			addSelectedSegment(hits, 1, false); // segment needed

		GeoElement[] ret = { null };
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			ret[0] = kernel.LineBisector(null, points[0], points[1]);
			return ret;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			ret[0] = kernel.LineBisector(null, segments[0]);
			return ret;
		}
		return null;
	}

	// get three points and create angular bisector for them
	// or bisector for two lines
	final protected GeoElement[] angularBisector(Hits hits) {
		if (hits.isEmpty())
			return null;
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
			GeoElement[] ret = { null };
			ret[0] = kernel.AngularBisector(null, points[0], points[1], points[2]);
			return ret;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLine[] lines = getSelectedLines();
			return kernel.AngularBisector(null, lines[0], lines[1]);
		}
		return null;
	}

	// get 3 points
	final protected GeoElement[] threePoints(Hits hits, int mode) {
		
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 3, false);
		if (selPoints() == 3) {
			return switchModeForThreePoints();
		}
		return null;
	}
	
	
	protected GeoElement[] switchModeForThreePoints(){
		// fetch the three selected points
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		switch (mode) {
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
			if ( ((GeoElement) points[0]).isGeoElement3D() 
					|| ((GeoElement) points[1]).isGeoElement3D() 
					|| ((GeoElement) points[2]).isGeoElement3D() )
				ret[0] = kernel.getManager3D().Circle3D(null, points[0], points[1], points[2]);
			else
				ret[0] = kernel.Circle(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
			ret[0] = kernel.Ellipse(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
			ret[0] = kernel.Hyperbola(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			ret[0] = kernel.CircumcircleArc(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			ret[0] = kernel.CircumcircleSector(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
			ret[0] = kernel.CircleArc(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;

		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
			ret[0] = kernel.CircleSector(null, (GeoPoint) points[0], (GeoPoint) points[1], (GeoPoint) points[2]);
			break;												

		default:
			return null;
		}
		
		return ret;

	}

	// get 2 lines, 2 vectors or 3 points
	final protected GeoElement[] angle(Hits hits) {
		if (hits.isEmpty())
			return null;

		int count = 0;
		if (selPoints() == 0) {
			if (selVectors() == 0)
				count = addSelectedLine(hits, 2, false);
			if (selLines() == 0) 
				count = addSelectedVector(hits, 2, false);			
		}		
		if (count == 0)
			count = addSelectedPoint(hits, 3, false);	

		// try polygon too
		boolean polyFound = false;
		if (count == 0)	{		
			polyFound = 1 == addSelectedGeo(hits.getHits(GeoPolygon.class, tempArrayList), 
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
			// 	make sure that we show angle value
			if (angle.isLabelVisible()) 
				angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				angle.setLabelMode(GeoElement.LABEL_VALUE);
			angle.setLabelVisible(true);
			angle.updateRepaint();
			GeoElement[] ret = { angle };
			return ret;
		} 
		else if (angles != null) {
			for (int i=0; i < angles.length; i++) {
				//	make sure that we show angle value
				if (angles[i].isLabelVisible()) 
					angles[i].setLabelMode(GeoElement.LABEL_NAME_VALUE);
				else 
					angles[i].setLabelMode(GeoElement.LABEL_VALUE);
				angles[i].setLabelVisible(true);
				angles[i].updateRepaint();
			}
			return angles;
		} else
			return null;
	}

	// build angle between two lines
	protected GeoAngle createLineAngle(GeoLine [] lines) {
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
	final protected GeoElement[] circleOrSphere2(Hits hits, int mode) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the three selected points
			return switchModeForCircleOrSphere2(mode);
		}
		return null;
	}
	
	protected GeoElement[] switchModeForCircleOrSphere2(int mode){
		GeoPoint[] points = getSelectedPoints();
		GeoElement[] ret = { null };
		if (mode == EuclidianView.MODE_SEMICIRCLE)
			ret[0] = kernel.Semicircle(null, points[0], points[1]);
		else
			ret[0] = kernel.Circle(null, points[0], points[1]);
		return ret;
	}

	// get 2 points for locus
	// first point 
	final protected GeoElement[] locus(Hits hits) {
		if (hits.isEmpty())
			return null;

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
			GeoElement[] ret = { null };
			ret[0] = locus;
			return ret;
		}
		return null;
	}

	// get 5 points
	final protected GeoElement[] conic5(Hits hits) {
		if (hits.isEmpty())
			return null;

		// points needed
		addSelectedPoint(hits, 5, false);
		if (selPoints() == 5) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			GeoElement[] ret = { null };
			ret[0] = kernel.Conic(null, points);
			return ret;
		}
		return null;
	}

	// get 2 GeoElements
	final protected boolean relation(Hits hits) {
		if (hits.isEmpty())
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
	final protected boolean distance(Hits hits, MouseEvent e) {
		if (hits.isEmpty())
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
			GeoNumeric length = kernel.Distance(null, (GeoPointND) points[0], (GeoPointND) points[1]);								

			// set startpoint of text to midpoint of two points
			GeoPoint midPoint = kernel.Midpoint(points[0], points[1]);
			createDistanceText(points[0], points[1], midPoint, length);			
			return true;
		} 

		// SEGMENT
		else if (selSegments() == 1) {
			// length
			GeoSegment[] segments = getSelectedSegments();

			// length			
			if (segments[0].isLabelVisible()) 
				segments[0].setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				segments[0].setLabelMode(GeoElement.LABEL_VALUE);
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
			return true;
		}

		// circumference of CONIC
		else if (selConics() == 1) {			
			GeoConic conic = getSelectedConics()[0];
			if (conic.isGeoConicPart()) {
				// length of arc
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					// arc length
					if (conic.isLabelVisible()) 
						conic.setLabelMode(GeoElement.LABEL_NAME_VALUE);
					else 
						conic.setLabelMode(GeoElement.LABEL_VALUE);
					conic.updateRepaint();
					return true;
				}				
			} 

			// standard case: conic
			GeoNumeric circumFerence = kernel.Circumference(null, conic);

			// text			
			GeoText text = createDynamicText(app.getCommand("Circumference"), circumFerence, e.getPoint());			
			if (conic.isLabelSet()) {
				circumFerence.setLabel(removeUnderscores(app.getCommand("Circumference").toLowerCase(Locale.US) + conic.getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + conic.getLabel()));				
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
				perimeter.setLabel(removeUnderscores(app.getCommand("Perimeter").toLowerCase(Locale.US) + poly[0].getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + poly[0].getLabel()));				
			} 
			return true;
		}

		return false;
	}	

	/**
	 * Creates a text that shows the distance length between geoA and geoB at the given startpoint.
	 */
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB, 
			GeoPoint startPoint, GeoNumeric length) {
		// create text that shows length
		try {				
			String strText = "";
			boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
			if (useLabels) {		
				length.setLabel(removeUnderscores(app.getCommand("Distance").toLowerCase(Locale.US) + geoA.getLabel() + geoB.getLabel()));
				//strText = "\"\\overline{\" + Name["+ geoA.getLabel() 
				//	+ "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
				//	+ length.getLabel();	

				//DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
				// or
				//DistanceAB=%0+%1+" \\, = \\, "+%2
				strText = app.getPlain("DistanceAB.LaTeX","Name["+ geoA.getLabel()+"]","Name["+ geoB.getLabel()+"]",length.getLabel());			
				//Application.debug(strText);
				geoA.setLabelVisible(true);				
				geoB.setLabelVisible(true);
				geoA.updateRepaint();
				geoB.updateRepaint();
			}
			else {
				length.setLabel(removeUnderscores(app.getCommand("Distance").toLowerCase(Locale.US)));					
				strText = "\"\"" + length.getLabel();
			}

			// create dynamic text
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText, true);
			if (useLabels) {
				text.setLabel(removeUnderscores(app.getPlain("Text") + geoA.getLabel() + geoB.getLabel()));	
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
	protected GeoText createDynamicText(String descText, GeoElement value, Point loc) {
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

	protected String removeUnderscores(String label) {	
		// remove all indices
		return label.replaceAll("_", "");			
	}

	protected boolean area(Hits hits,  MouseEvent e) {
		if (hits.isEmpty())
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
				area.setLabel(removeUnderscores(app.getCommand("Area").toLowerCase(Locale.US) + conic.getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + conic.getLabel()));				
			}			
			return true;
		}

		// area of polygon
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();						

			// dynamic text with polygon's area
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Area"), poly[0]), poly[0], e.getPoint());			
			if (poly[0].isLabelSet()) {					
				text.setLabel(removeUnderscores(app.getPlain("Text") + poly[0].getLabel()));				
			} 
			return true;
		}

		return false;
	}



	protected String descriptionPoints(String prefix, GeoPolygon poly) {
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

	protected GeoElement[] slope(Hits hits) {
		if (hits.isEmpty())
			return null;

		addSelectedLine(hits, 1, false);			

		if (selLines() == 1) {			
			GeoLine line = getSelectedLines()[0];						

			String strLocale = app.getLocale().toString();
			GeoNumeric slope;
			/*
			if (strLocale.equals("de_AT")) {
				slope = kernel.Slope("k", line);
			} else {
				slope = kernel.Slope("m", line);
			}			*/
			
			String label = app.getPlain("ExplicitLineGradient");
			
			// make sure automatic naming goes m, m_1, m_2, ..., m_{10}, m_{11} etc
			if (kernel.lookupLabel(label) != null) {
				int i = 1;
				while (kernel.lookupLabel(i > 9 ? label+"_{"+i+"}" : label+"_"+i) != null) i++;
				label = i > 9 ? label+"_{"+i+"}" : label+"_"+i;
			}
			
			slope = kernel.Slope(label, line);

			// show value
			if (slope.isLabelVisible()) {
				slope.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			} else {
				slope.setLabelMode(GeoElement.LABEL_VALUE);
			}
			slope.setLabelVisible(true);
			slope.updateRepaint();
			GeoElement[] ret = { slope };
			return ret;
		}
		return null;
	}

	protected boolean regularPolygon(Hits hits) {
		if (hits.isEmpty())
			return false;

		// need two points
		addSelectedPoint(hits, 2, false);

		if (selPoints() == 2) {					
			GeoPoint [] points = getSelectedPoints();
			app.getGuiManager().showNumberInputDialogRegularPolygon(app.getMenu(getKernel().getModeText(mode)),
					points[0], points[1]);
			return true;
		}
		return false;
	}

	protected boolean showCheckBox(Hits hits) {
		if (selectionPreview)
			return false;

		app.getGuiManager().showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	// get (point or line) and (conic or function or curve or implicitpoly)
	final protected GeoElement[] tangents(Hits hits) {
		if (hits.isEmpty())
			return null;

		boolean found=false;
		found = addSelectedConic(hits, 1, false) != 0;
		if (!found)
			found = addSelectedFunction(hits, 1, false) != 0;
		if (!found)
			found = addSelectedCurve(hits, 1, false) != 0;		
		if (!found)
			found = addSelectedImplicitpoly(hits, 1, false) != 0;	

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
				return kernel.Tangent(null, points[0], conics[0]);
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				return kernel.Tangent(null, lines[0], conics[0]);
			}
		} 
		else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				ret[0] = kernel.Tangent(null, points[0], functions[0]);
				return ret;
			}
		}
		else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPoint [] points = getSelectedPoints();
				// create new tangents
				GeoElement[] ret = { null };
				ret[0] = kernel.Tangent(null, points[0], curves[0]);
				return ret;
			}
		}
		else if (selImplicitpoly() == 1) {
			if (selPoints() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				return kernel.Tangent(null, points[0], implicitPoly);
			} else if (selLines() == 1) {
				GeoImplicitPoly implicitPoly = getSelectedImplicitpoly()[0];
				GeoLine[] lines = getSelectedLines();
				// create new line
				return kernel.Tangent(null, lines[0], implicitPoly);
			}
		} 
		return null;
	}

	// get (point or line or vector) and conic
	final protected GeoElement[] polarLine(Hits hits) {
		if (hits.isEmpty())
			return null;
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
			GeoElement[] ret = { null };
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				ret[0] = kernel.PolarLine(null, points[0], conics[0]);
				return ret;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				ret[0] = kernel.DiameterLine(null, lines[0], conics[0]);
				return ret;
			}  else if (selVectors() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoVector[] vecs = getSelectedVectors();
				// create new line
				ret[0] = kernel.DiameterLine(null, vecs[0], conics[0]);
				return ret;
			}
		}
		return null;
	}

	final protected boolean delete(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			// delete this object
			GeoElement[] geos = getSelectedGeos();
			geos[0].removeOrSetUndefinedIfHasFixedDescendent();
			return true;
		}
		return false;
	}

	final protected boolean showHideObject(Hits hits) {
		if (hits.isEmpty())
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits, true);
		if (geo != null) {
			// hide axis
			if (geo instanceof GeoAxis)	{
				switch (((GeoAxis) geo).getType()) {
				case GeoAxis.X_AXIS:
					//view.showAxes(false, view.getShowYaxis());
					view.setShowAxis(EuclidianViewInterface.AXIS_X, false, true);
					break;

				case GeoAxis.Y_AXIS:
					//view.showAxes(view.getShowXaxis(), false);
					view.setShowAxis(EuclidianViewInterface.AXIS_Y, false, true);
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

	final protected boolean showHideLabel(Hits hits) {
		if (hits.isEmpty())
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits.getOtherHits(GeoAxis.class, tempArrayList), true);
		if (geo != null) {			
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateRepaint();
			return true;
		}						
		return false;
	}

	final protected boolean copyVisualStyle(Hits hits) {
		if (hits.isEmpty())
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits.getOtherHits(GeoAxis.class, tempArrayList), true);
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
				geo.setAdvancedVisualStyle(movedGeoElement);
				geo.updateRepaint();
				return true;
			}
		}					
		return false;
	}


	// get Transformables and point
	final protected GeoElement[] mirrorAtPoint(Hits hits) {	
		if (hits.isEmpty())
			return null;

		// try to get one Transformable	
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Transformable.class, tempArrayList);		
			count = addSelectedGeo(mirAbles, 1, false);	
		}

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
				return kernel.Mirror(null,  polys[0], points[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.Mirror(null,  geos[i], point)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null, (GeoPolygon) geos[i], point)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	/**
	 * Removes parent points of segments, rays, polygons, etc. from selGeos
	 * that are not necessary for transformations of these objects.
	 */
	protected void removeParentPoints(ArrayList selGeos) {
		tempArrayList.clear();	
		tempArrayList.addAll(selGeos);
		
		// remove parent points
		for (int i=0; i < selGeos.size(); i++) {
			GeoElement geo = (GeoElement) selGeos.get(i);

			switch (geo.getGeoClassType()) {			
			case GeoElement.GEO_CLASS_SEGMENT:
			case GeoElement.GEO_CLASS_RAY:
				// remove start and end point of segment
				GeoLine line = (GeoLine) geo;						
				tempArrayList.remove(line.getStartPoint());
				tempArrayList.remove(line.getEndPoint());									
				break;

			case GeoElement.GEO_CLASS_CONICPART:
				GeoConicPart cp = (GeoConicPart) geo;
				ArrayList ip = cp.getParentAlgorithm().getInputPoints();
				tempArrayList.removeAll(ip);
				break;

			case GeoElement.GEO_CLASS_POLYGON:
				// remove points and segments of poly
				GeoPolygon poly = (GeoPolygon) geo;
				GeoPoint [] points = poly.getPoints();
				for (int k=0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				GeoSegmentND [] segs = poly.getSegments();
				for (int k=0; k < segs.length; k++) {
					tempArrayList.remove(segs[k]);
				}
				break;					

			case GeoElement.GEO_CLASS_POLYLINE:
				// remove points and segments of poly
				GeoPolyLine polyl = (GeoPolyLine) geo;
				points = polyl.getPoints();
				for (int k=0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				break;					
			}				
		}		

		selGeos.clear();
		selGeos.addAll(tempArrayList);						
	}

	// get Transformable and line
	final protected GeoElement[] mirrorAtLine(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable	
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Transformable.class, tempArrayList);
			count =addSelectedGeo(mirAbles, 1, false);
		}

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
				return kernel.Mirror(null,  polys[0], lines[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoLine line = getSelectedLines()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != line) {
						if (geos[i] instanceof Transformable)
							ret.addAll(Arrays.asList(kernel.Mirror(null,  geos[i], line)));
						else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Mirror(null, (GeoPolygon) geos[i], line)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}


	// Michael Borcherds 2008-03-23
	final protected GeoElement[] mirrorAtCircle(Hits hits) {
		if (hits.isEmpty())
			return null;


		// remove conics that aren't circles
		for (int i=0 ; i<hits.size(); i++)
		{
			GeoElement geo = (GeoElement) hits.get(i);
			if (geo.isGeoConic())
				if (!((GeoConic)geo).isCircle()) hits.remove(i);
		}

		addSelectedConic(hits, 1, false);

		addSelectedPoint(hits, 1, false);

		if (selConics() == 1 && selPoints() == 1) {
			GeoConic[] conics = getSelectedConics();
			GeoPoint[] points = getSelectedPoints();
			//if (((GeoConic)conics[0]).getTypeString()!="Circle") return false;
			if (!((GeoConic)conics[0]).isCircle()) return null;
			return kernel.Mirror(null, points[0], conics[0]);

		} 
		return null;
	}

	final protected boolean attachDetach(Hits hits, MouseEvent e) {
		if (hits.isEmpty())
			return false;
		
		addSelectedRegion(hits, 1, false);
		
		addSelectedPath(hits, 1, false);

		addSelectedPoint(hits, 1, false);

		if (selectedPoints.size() == 1) {
			
			GeoPoint p = (GeoPoint)selectedPoints.get(0);
			
			
			if (p.isPointOnPath() || p.isPointInRegion()) {
				
				getSelectedPoints();
				getSelectedRegions();
				getSelectedPaths();
				
				// move point (20,20) pixels when detached
				double x = view.toScreenCoordX(p.inhomX) + 20;
				double y = view.toScreenCoordY(p.inhomY) + 20;
				
				try {
					Construction cons = kernel.getConstruction();
					boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint newPoint = new GeoPoint(kernel.getConstruction(),null, view.toRealWorldCoordX(x),view.toRealWorldCoordY(y),1.0);
					cons.setSuppressLabelCreation(oldLabelCreationFlag);
					cons.replace(p, newPoint);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				return true;
			}			
		}

		if (selPoints() == 1) {
			if (selPaths() == 1 && !altDown) { // press alt to force region (ie inside) not path (edge)
				Path paths[] = getSelectedPaths();
				GeoPoint[] points = getSelectedPoints();
				
				if (!((GeoElement)paths[0]).isChildOf(points[0])) {
					
					try {
						Construction cons = kernel.getConstruction();
						boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
						cons.setSuppressLabelCreation(true);
						GeoPoint newPoint = kernel.Point(null, paths[0], view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my), false);
						cons.setSuppressLabelCreation(oldLabelCreationFlag);
						kernel.getConstruction().replace(points[0], newPoint);
					} catch (Exception e1) {
						e1.printStackTrace();
						return false;
					}

					return true;
				}
				
			} else if (selRegions() == 1) {
				Region regions[] = getSelectedRegions();
				GeoPoint[] points = getSelectedPoints();

				if (!((GeoElement)regions[0]).isChildOf(points[0])) {
					try {
						Construction cons = kernel.getConstruction();
						boolean oldLabelCreationFlag = cons.isSuppressLabelsActive();
						cons.setSuppressLabelCreation(true);
						GeoPoint newPoint = kernel.PointIn(null, regions[0], view.toRealWorldCoordX(mx), view.toRealWorldCoordY(my), false);
						cons.setSuppressLabelCreation(oldLabelCreationFlag);
						kernel.getConstruction().replace(points[0], newPoint);
					} catch (Exception e1) {
						e1.printStackTrace();
						return false;
					}
					return true;
				}
				
			} 
		} 
		return false;
	}

	// get Transformable and vector
	final protected GeoElement[] translateByVector(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits transAbles = hits.getHits(Transformable.class, tempArrayList);
			count = addSelectedGeo(transAbles, 1, false);
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}	

		// translation vector
		if (count == 0) {
			addSelectedVector(hits, 1, false);
		}				

		// we got the mirror point
		if (selVectors() == 1) {		
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoVector[] vecs = getSelectedVectors();	
				return kernel.Translate(null,  polys[0], vecs[0]);
			}
			else if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoVector vec = getSelectedVectors()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {
					if (geos[i] != vec) {
						if (geos[i] instanceof Transformable || geos[i].isGeoPolygon())
							ret.addAll(Arrays.asList(kernel.Translate(null,   geos[i], vec)));
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	// get rotateable object, point and angle
	final protected GeoElement[] rotateByAngle(Hits hits) {
		if (hits.isEmpty())
			return null;

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits rotAbles = hits.getHits(Transformable.class, tempArrayList);
			count = addSelectedGeo(rotAbles, 1, false);
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}

		// rotation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}

		// we got the rotation center point
		if (selPoints() == 1 && selGeos() > 0) {	
			
			GeoElement[] selGeos = getSelectedGeos();
			
			app.getGuiManager().showNumberInputDialogRotate(app.getMenu(getKernel().getModeText(mode)), getSelectedPolygons(), getSelectedPoints(), selGeos);
			
			return null;

			/*
			Object [] ob = app.getGuiManager().showAngleInputDialog(app.getMenu(getKernel().getModeText(mode)),
					app.getPlain("Angle"), defaultRotateAngle);

			NumberValue num = (NumberValue) ob[0];
			geogebra.gui.AngleInputDialog dialog = (geogebra.gui.AngleInputDialog) ob[1];
			String angleText = dialog.getText();

			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0") && dialog.success==true) defaultRotateAngle = angleText;
			else defaultRotateAngle = "45"+"\u00b0";

			if (num == null) {
				view.resetMode();
				return null;
			}

			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				return kernel.Rotate(null,  polys[0], num, points[0]);
			} else {	
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.Rotate(null,   geos[i], num, point)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.Rotate(null, (GeoPolygon) geos[i], num, point)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}*/
			
		}

		return null;
	}		

	// get dilateable object, point and number
	final protected GeoElement[] dilateFromPoint(Hits hits) {
		if (hits.isEmpty())
			return null;

		// dilateable
		int count = 0;
		if (selGeos() == 0) {
			Hits dilAbles = hits.getHits(Dilateable.class, tempArrayList);
			count =	addSelectedGeo(dilAbles, 1, false);
		}

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
			NumberValue num = app.getGuiManager().showNumberInputDialog(app.getMenu(getKernel().getModeText(mode)),
					app.getPlain("Numeric"), null);			
			if (num == null) {
				view.resetMode();
				return null;
			}

			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				return kernel.Dilate(null,  polys[0], num, points[0]);
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Dilateable || geos[i].isGeoPolygon())
							ret.addAll(Arrays.asList(kernel.Dilate(null,  geos[i], num, point)));
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}		
		}
		return null;
	}


	// get point and number
	final protected boolean segmentFixed(Hits hits) {
		if (hits.isEmpty())
			return false;

		// dilation center
		addSelectedPoint(hits, 1, false);

		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			app.getGuiManager().showNumberInputDialogSegmentFixed(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPoints()[0]);

			return true;
		}
		return false;
	}



	final protected GeoElement[] fitLine(Hits hits) {

		GeoList list;

		addSelectedList(hits,1,false);

		GeoElement[] ret = { null };
		if (selLists() > 0)
		{
			list = getSelectedLists()[0];
			if (list != null) {
				ret[0] = kernel.FitLineY(null, list);
				return ret;   
			}
		}
		else
		{
			addSelectedPoint(hits, 999, true);

			if (selPoints() > 1) 
			{					
				GeoPoint[] points = getSelectedPoints();
				list = geogebra.kernel.commands.CommandProcessor.wrapInList(kernel,points, points.length, GeoElement.GEO_CLASS_POINT);
				if (list != null) {
					ret[0] = kernel.FitLineY(null, list);
					return ret;             	     	 
				} 
			}
		}
		return null;
	}





	// Michael Borcherds 2008-03-14	
	// Markus 2008-07-30: added support for two identical input points (center *2 and point on edge)
	final protected GeoElement[] compasses(Hits hits) {
		if (hits.isEmpty())
			return null;

		// we already have two points that define the radius
		if (selPoints() == 2) {			
			GeoPoint [] points = new GeoPoint[2];
			points[0] = (GeoPoint) selectedPoints.get(0);
			points[1] = (GeoPoint) selectedPoints.get(1);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				else {
					// three points: center, distance between two points		
					GeoElement circle = kernel.Circle(null, centerPoint, points[0], points[1],true);
					GeoElement[] ret = { circle };
					clearSelections();
					return ret;
				}
			}
		} 

		// we already have a circle that defines the radius
		else if (selConics() == 1) {
			GeoConic circle =  (GeoConic) selectedConics.get(0);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {	
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				else {
					// center point and circle which defines radius
					GeoElement circlel = kernel.Circle(null, centerPoint, circle);
					GeoElement ret[] = { circlel };
					clearSelections();
					return ret;
				}
			}
		}
		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegment segment =  (GeoSegment) selectedSegments.get(0);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {	
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return null;
				}
				else {
					// center point and segment
					GeoElement circlel = kernel.Circle(null, centerPoint, segment);
					GeoElement[] ret = { circlel };
					clearSelections();
					return ret;
				}
			}
		}


		// don't have radius yet: need two points or segment		
		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);
		if (!hitPoint && selPoints() != 2 ) {
			addSelectedSegment(hits, 1, false);
			addSelectedConic(hits, 1, false);

			// don't allow conics other than circles to be selected
			if (selectedConics.size() > 0) {
				GeoConic c = (GeoConic)selectedConics.get(0);
				if (!c.isCircle()) {
					selectedConics.remove(0); 
					clearSelections();
				}
			}
		}

		return null;
	}

	// get two points and number
	final protected GeoElement[] angleFixed(Hits hits) {
		if (hits.isEmpty())
			return null;

		// dilation center
		int count = addSelectedPoint(hits, 2, false);

		if (count == 0) {
			addSelectedSegment(hits, 1, false);
		}				

		// we got the points		
		if (selPoints() == 2 || selSegments() == 1) {
			// get angle			
			Object [] ob = app.getGuiManager().showAngleInputDialog(app.getMenu(getKernel().getModeText(mode)),
					app.getPlain("Angle"), "45\u00b0");
			NumberValue num = (NumberValue) ob[0];
			geogebra.gui.AngleInputDialog aDialog = (geogebra.gui.AngleInputDialog) ob[1]; 			

			if (num == null) {
				view.resetMode();
				return null;
			}

			GeoAngle angle = null;
			boolean posOrientation = aDialog.isCounterClockWise();
			if (selPoints() == 2) {
				GeoPoint[] points = getSelectedPoints();		
				angle = (GeoAngle) kernel.Angle(null, points[0], points[1], num, posOrientation)[0];			
			} else {
				GeoSegment[] segment = getSelectedSegments();		
				angle = (GeoAngle) kernel.Angle(null, segment[0].getEndPoint(), segment[0].getStartPoint(), num, posOrientation)[0];
			}			

			// make sure that we show angle value
			if (angle.isLabelVisible()) 
				angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				angle.setLabelMode(GeoElement.LABEL_VALUE);
			angle.setLabelVisible(true);		
			angle.updateRepaint();

			GeoElement[] ret = { null };
			ret[0] = angle;
			return ret;
		}
		return null;
	}	

	// get center point and number
	final protected boolean circleOrSpherePointRadius(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 1, false);		

		// we got the center point
		if (selPoints() == 1) {	
			app.getGuiManager().showNumberInputDialogCirclePointRadius(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPoints()[0]);
			return true;
		}
		return false;
	}	

	// get point and vector
	final protected GeoElement[] vectorFromPoint(Hits hits) {
		if (hits.isEmpty())
			return null;

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
			GeoElement[] ret = { null };
			ret[0] = kernel.Vector(null, points[0], endPoint);
			return ret;
		}
		return null;
	}

	/**
	 * Handles selected objects for a macro
	 * @param hits
	 * @return
	 */
	final protected boolean macro(Hits hits) {		
		// try to get next needed type of macroInput
		int index = selGeos();

		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == 
			handleAddSelected(hits, macroInput.length, false, selectedGeos, macroInput[index]);			

		//some old code for polygon removed in [6779]

		// we're done if in selection preview
		if (selectionPreview) 
			return false;


		// only one point needed: try to create it
		if (!objectFound && macroInput[index] == GeoPoint.class) {
			if (createNewPoint(hits, true, true, false)) {
				// take movedGeoPoint which is the newly created point								
				selectedGeos.add(getMovedGeoPoint());
				app.addSelectedGeo(getMovedGeoPoint());
				objectFound = true;
				POINT_CREATED = false;
			}
		}


		// object found in handleAddSelected()
		if (objectFound || macroInput[index] == GeoNumeric.class || macroInput[index] == GeoAngle.class) {
			if(!objectFound)index--;
			// look ahead if we need a number or an angle next			
			while (++index < macroInput.length) {				
				// maybe we need a number
				if (macroInput[index] == GeoNumeric.class) {									
					NumberValue num = app.getGuiManager().showNumberInputDialog(macro.getToolOrCommandName(),
							app.getPlain("Numeric" ), null);									
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our number
						if (num.isGeoElement())
							selectedGeos.add(num.toGeoElement());
					}
				}	

				// maybe we need an angle
				else if (macroInput[index] == GeoAngle.class) {									
					Object [] ob = app.getGuiManager().showAngleInputDialog(macro.getToolOrCommandName(),
							app.getPlain("Angle"), "45\u00b0");
					NumberValue num = (NumberValue) ob[0];						

					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our angle
						if (num.isGeoElement())
							selectedGeos.add(num.toGeoElement());
					}
				}	

				else // other type needed, so leave loop 
					break;				
			}			
		}

		//Application.debug("index: " + index + ", needed type: " + macroInput[index]);

		// do we have everything we need?
		if (selGeos() == macroInput.length) {						
			kernel.useMacro(null, macro, getSelectedGeos());
			return true;
		} 		
		return false;
	}

	final protected boolean geoElementSelected(Hits hits, boolean addToSelection) {
		if (hits.isEmpty())
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
	protected boolean move(Hits hits) {	
		addSelectedGeo(hits.getMoveableHits(), 1, false);	
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean moveRotate(Hits hits) {				
		addSelectedGeo(hits.getPointRotateableHits(rotationCenter), 1, false);
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean point(Hits hits) {
		addSelectedGeo(hits.getHits(Path.class, tempArrayList), 1, false);
		return false;
	}

	final protected boolean textImage(Hits hits, int mode, boolean altDown) {
		GeoPoint loc = null; // location

		if (hits.isEmpty()) {
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
				app.getGuiManager().showTextCreationDialog(loc);
				break;

			case EuclidianView.MODE_IMAGE:	
				app.getGuiManager().loadImage(loc, altDown);
				break;
			}			
			return true;
		}

		return false;
	}


	// new slider
	final protected boolean slider() {		
		if (!selectionPreview && mouseLoc != null) app.getGuiManager().showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
		return false;
	}		

	// new button
	final protected boolean button(boolean textfield) {	
		if (!selectionPreview && mouseLoc != null) app.getGuiManager().showButtonCreationDialog(mouseLoc.x, mouseLoc.y, textfield);
		return false;
	}		

	final protected boolean pen() {	
		//Application.debug(app.getEuclidianView().getHeight()+" "+app.getEuclidianView().getWidth());
		return false;
	}		

	/***************************************************************************
	 * helper functions for selection sets
	 **************************************************************************/

	/*
	final protected boolean isSelected(GeoElement geo) {
		return selectedGeos.contains(geo);
	}*/

	//	final protected GeoElement getFirstSelectedInstance(Class myclass) {
	//		Iterator it = selectedGeos.iterator();		
	//		while (it.hasNext()) {
	//			GeoElement geo = (GeoElement) it.next();
	//			if (myclass.isInstance(geo))
	//				return geo;
	//		}
	//		return null;
	//	}

	final protected GeoElement[] getSelectedGeos() {
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

	final protected void getSelectedPointsND(GeoPointND[] result) {	

		for (int i = 0; i < selectedPoints.size(); i++) {		
			result[i] = (GeoPointND) selectedPoints.get(i);
		}
		clearSelection(selectedPoints);

	}
	

	/** return selected points as ND points
	 * @return selected points
	 */
	final protected GeoPointND[] getSelectedPointsND() {		

		GeoPointND[] ret = new GeoPointND[selectedPoints.size()];
		getSelectedPointsND(ret);
		
		return ret;	
	}


	final protected GeoPoint[] getSelectedPoints() {		

		GeoPoint[] ret = new GeoPoint[selectedPoints.size()];
		getSelectedPointsND(ret);

		return ret;

	}

	final protected GeoNumeric[] getSelectedNumbers() {				
		GeoNumeric[] ret = new GeoNumeric[selectedNumbers.size()];
		for (int i = 0; i < selectedNumbers.size(); i++) {		
			ret[i] = (GeoNumeric) selectedNumbers.get(i);
		}
		clearSelection(selectedNumbers);
		return ret;
	}
	
	final protected NumberValue[] getSelectedNumberValues() {				
		NumberValue[] ret = new NumberValue[selectedNumberValues.size()];
		for (int i = 0; i < selectedNumberValues.size(); i++) {		
			ret[i] = (NumberValue) selectedNumberValues.get(i);
		}
		clearSelection(selectedNumberValues);
		return ret;
	}
	
	final protected GeoList[] getSelectedLists() {				
		GeoList[] ret = new GeoList[selectedLists.size()];
		for (int i = 0; i < selectedLists.size(); i++) {		
			ret[i] = (GeoList) selectedLists.get(i);
		}
		clearSelection(selectedLists);
		return ret;
	}

	final protected GeoPolygon[] getSelectedPolygons() {				
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons.size()];
		for (int i = 0; i < selectedPolygons.size(); i++) {		
			ret[i] = (GeoPolygon) selectedPolygons.get(i);
		}
		clearSelection(selectedPolygons);
		return ret;
	}

	

	
	protected final void getSelectedLinesND(GeoLineND[] lines) {
		int i = 0;
		Iterator it = selectedLines.iterator();
		while (it.hasNext()) {
			lines[i] = (GeoLineND) it.next();
			i++;
		}
		clearSelection(selectedLines);
	}
	
	final protected GeoLineND[] getSelectedLinesND() {
		GeoLineND[] lines = new GeoLineND[selectedLines.size()];
		getSelectedLinesND(lines);

		return lines;
	}
	
	final protected GeoLine[] getSelectedLines() {
		GeoLine[] lines = new GeoLine[selectedLines.size()];
		getSelectedLinesND(lines);

		return lines;
	}
	
	final protected GeoSegment[] getSelectedSegments() {
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

	
	
	protected final void getSelectedVectorsND(GeoVectorND[] vectors) {
		int i = 0;
		Iterator it = selectedVectors.iterator();
		while (it.hasNext()) {
			vectors[i] = (GeoVectorND) it.next();
			i++;
		}
		clearSelection(selectedVectors);
	}
	
	final protected GeoVectorND[] getSelectedVectorsND() {
		GeoVectorND[] vectors = new GeoVectorND[selectedVectors.size()];
		getSelectedVectorsND(vectors);

		return vectors;
	}
	
	final protected GeoVector[] getSelectedVectors() {
		GeoVector[] vectors = new GeoVector[selectedVectors.size()];
		getSelectedVectorsND(vectors);

		return vectors;
	}

	final protected GeoConic[] getSelectedConics() {
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
	
	final protected Region[] getSelectedRegions() {
		Region[] regions = new Region[selectedRegions.size()];
		int i = 0;
		Iterator it = selectedRegions.iterator();
		while (it.hasNext()) {
			regions[i] = (Region) it.next();
			i++;
		}
		clearSelection(selectedRegions);
		return regions;
	}
	
	final protected Path[] getSelectedPaths() {
		Path[] paths = new Path[selectedPaths.size()];
		int i = 0;
		Iterator it = selectedPaths.iterator();
		while (it.hasNext()) {
			paths[i] = (Path) it.next();
			i++;
		}
		clearSelection(selectedPaths);
		return paths;
	}
	
	final protected GeoImplicitPoly[] getSelectedImplicitpoly() {
		GeoImplicitPoly[] implicitPoly = new GeoImplicitPoly[selectedImplicitpoly.size()];
		int i = 0;
		Iterator it = selectedImplicitpoly.iterator();
		while (it.hasNext()) {
			implicitPoly[i] = (GeoImplicitPoly) it.next();
			i++;
		}
		clearSelection(selectedImplicitpoly);
		return implicitPoly;
	}

	final protected GeoFunction[] getSelectedFunctions() {
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


	final protected GeoCurveCartesian [] getSelectedCurves() {
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

	final protected void clearSelection(ArrayList selectionList) {
		// unselect
		selectionList.clear();
		selectedGeos.clear();
		app.clearSelectedGeos();	
		view.repaintEuclidianView();
	}

	final protected int addSelectedGeo(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedGeos, GeoElement.class);
	}		
	

	protected int handleAddSelected(Hits hits, int max, boolean addMore, ArrayList list, Class geoClass) {	
		
		
		if (selectionPreview)
			return addToHighlightedList(list, hits.getHits(geoClass, handleAddSelectedArrayList) , max);
		else
			return addToSelectionList(list, hits.getHits(geoClass, handleAddSelectedArrayList), max, addMore);
	}
	protected Hits handleAddSelectedArrayList = new Hits();

	final protected int addSelectedPoint(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPoints, GeoPointND.class);
	}

	public final int addSelectedNumeric(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedNumbers, GeoNumeric.class);
	}
	
	public final int addSelectedNumberValue(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedNumberValues, NumberValue.class);
	}

	final protected int addSelectedLine(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLines, GeoLineND.class);
	}

	final protected int addSelectedSegment(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedSegments, GeoSegment.class);
	}

	final protected int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return addSelectedVector(hits, max, addMoreThanOneAllowed, GeoVectorND.class);
	}
	
	final protected int addSelectedVector(Hits hits, int max,
			boolean addMoreThanOneAllowed, Class geoClass) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedVectors, geoClass);
	}

	final protected int addSelectedConic(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedConics, GeoConic.class);
	}
	
	final protected int addSelectedPath(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPaths, Path.class);
	}
	
	final protected int addSelectedRegion(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedRegions, Region.class);
	}
	
	final protected int addSelectedImplicitpoly(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedImplicitpoly, GeoImplicitPoly.class);
	}

	final protected int addSelectedFunction(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedFunctions, GeoFunction.class);
	}

	final protected int addSelectedCurve(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCurves, GeoCurveCartesian.class);
	}

	final protected int addSelectedPolygon(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolygons, GeoPolygon.class);
	}

	final protected int addSelectedList(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLists, GeoList.class);
	}

	protected final int selGeos() {
		return selectedGeos.size();
	}

	protected final int selPoints() {
		return selectedPoints.size();
	}

	protected final int selNumbers() {
		return selectedNumbers.size();
	}
	
	protected final int selNumberValues() {
		return selectedNumberValues.size();
	}
	
	final int selLists() {
		return selectedLists.size();
	}

	protected final int selPolygons() {
		return selectedPolygons.size();
	}

	protected final int selLines() {
		return selectedLines.size();
	}

	final int selSegments() {
		return selectedSegments.size();
	}

	protected final int selVectors() {
		return selectedVectors.size();
	}

	final int selConics() {
		return selectedConics.size();
	}
	
	final int selPaths() {
		return selectedPaths.size();
	}
	
	final int selRegions() {
		return selectedRegions.size();
	}
	
	final int selImplicitpoly() {
		return selectedImplicitpoly.size();
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
	final protected int addToSelectionList(ArrayList selectionList,
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
		if (!addMoreThanOneAllowed || geos.size() + selectionList.size() > max){
			//Application.printStacktrace(geos.toString());
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);
		}

		// already selected objects -> choose one
		boolean contained = false;
		for (int i = 0; i < geos.size(); i++) {
			if (selectionList.contains(geos.get(i)))
				contained = true;
		}
		if (contained)
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);

		// add all objects to list
		int count = 0;
		for (int i = 0; i < geos.size(); i++) {
			count += addToSelectionList(selectionList, (GeoElement) geos.get(i), max);
		}
		return count;
	}

	//	selectionList may only contain max objects
	// an already selected objects is deselected
	final protected int addToSelectionList(ArrayList selectionList,
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
	final protected int addToHighlightedList(ArrayList selectionList,
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

	/**
	 * Shows dialog to choose one object out of hits[] that is an instance of
	   specified class (note: subclasses are included)
	 * 
	 */
	private GeoElement chooseGeo(Hits hits, Class geoclass) {
		return chooseGeo(hits.getHits(geoclass, tempArrayList), true);
	}

	protected GeoElement chooseGeo(ArrayList<GeoElement> geos, boolean includeFixed) {
		if (geos == null)
			return null;

		GeoElement ret = null;
		GeoElement retFree = null;
		GeoElement retPath = null;
		GeoElement retIndex = null;
		GeoElement retSegment = null;

		switch (geos.size()) {
		case 0:
			ret =  null;
			break;

		case 1:
			ret =  (GeoElement) geos.get(0);
			
			if (!includeFixed && ret.isFixed()) return null;
			
			break;

		default:	

			int maxLayer = -1;

			int layerCount = 0;

			// work out max layer, and
			// count no of objects in max layer
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				int layer = geo.getLayer();

				if (layer > maxLayer && (includeFixed || !geo.isFixed())) {
					maxLayer = layer;
					layerCount = 1;
					ret = geo;
				}
				else if (layer == maxLayer)
				{
					layerCount ++;
				}

			}

			//Application.debug("maxLayer"+maxLayer);
			//Application.debug("layerCount"+layerCount);

			// only one object in top layer, return it.
			if (layerCount == 1) return ret;


			int pointCount = 0;
			int freePointCount = 0;
			int pointOnPathCount = 0;
			int segmentCount = 0;
			//int polygonCount = 0;
			int maxIndex = -1;

			// count no of points in top layer
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				
				if (geo.isGeoPoint() && geo.getLayer() == maxLayer
						&& (includeFixed || !geo.isFixed())) {
					pointCount ++;
					ret = geo;

					// find point with the highest construction index
					int index = geo.getConstructionIndex();
					if (index > maxIndex) {
						maxIndex = index;
						retIndex = geo;
					}

					// find point-on-path/region with the highest construction index
					if (((GeoPointND)geo).isPointOnPath() || ((GeoPointND)geo).isPointInRegion()) {
						pointOnPathCount ++;
						if (retPath == null) {
							retPath = geo;
						}
						else
						{
							if (geo.getConstructionIndex() > retPath.getConstructionIndex()) retPath = geo;
						}
					}

					// find free point with the highest construction index
					if (geo.isIndependent()) {
						freePointCount ++;
						if (retFree == null) {
							retFree = geo;
						}
						else
						{
							if (geo.getConstructionIndex() > retFree.getConstructionIndex()) retFree = geo;						
						}
					}
				}
			}
			//Application.debug("pointOnPathCount"+pointOnPathCount);
			//Application.debug("freePointCount"+freePointCount);
			//Application.debug("pointCount"+pointCount);

			// return point-on-path with highest index
			if (pointOnPathCount > 0) return retPath;

			// return free-point with highest index
			if (freePointCount > 0) return retFree;

			// only one point in top layer, return it
			if (pointCount == 1) return ret;

			// just return the most recently created point
			if (pointCount > 1) return retIndex;

			/*
			try {
				throw new Exception("choose");
			} catch (Exception e) {
				e.printStackTrace();

			}
			 */

			boolean allFixed = false;

			// remove fixed objects (if there are some not fixed)
			if (!includeFixed && geos.size() > 1) {

				allFixed = true;
				for (int i = 0 ; i < geos.size() ; i++)
					if (!((GeoElement)geos.get(i)).isFixed())
						allFixed = false;

				if (!allFixed)
					for (int i = geos.size() - 1 ; i >= 0 ; i--) {
						GeoElement geo = (GeoElement)geos.get(i);
						if (geo.isFixed()) geos.remove(i);		
					}

				if (geos.size() == 1)
					return (GeoElement)geos.get(0);
			}
			
			//int maxPolygonLayer = 0;
			// count segments and polygons
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				
				if (geo.isGeoSegment()) {
					segmentCount++;
					if (retSegment == null) {
						retSegment = geo;
					} else {
						
						// select Segment with lowest layer (& construction index)
						if (retSegment.getLayer() < geo.getLayer()||
						(retSegment.getLayer() == geo.getLayer() && retSegment.getConstructionIndex() > geo.getConstructionIndex())) {
							retSegment = geo;
							
						}
					}
				//} else if (geo.isGeoPolygon()) {
				//	polygonCount++;
				//	if (geo.getLayer() > maxPolygonLayer) maxPolygonLayer = geo.getLayer();
				}				
			}
			
			// check for edge of polygon being selected (priority over polygon itself)
			//if (segmentCount == 1 && (segmentCount + polygonCount == geos.size())) {
			//	if (retSegment.getLayer() >= maxPolygonLayer) return retSegment;
			//}
			
			// give segments priority over eg Polygons, Lines
			// that they might be drawn on top of
			if (segmentCount > 0) return retSegment;
			
			// don't want a popup in this case
			// eg multiple fixed images from Pen Tool
			if (!includeFixed && allFixed) return null; 
			
			/* no points selected, multiple objects selected
			// popup a menu to choose from
			ToolTipManager ttm = ToolTipManager.sharedInstance();		
			ttm.setEnabled(false);			
			ListDialog dialog = new ListDialog((JPanel) view, geos, null);
			if (app.areChooserPopupsEnabled()) ret = dialog.showDialog((JPanel) view, mouseLoc);			
			ttm.setEnabled(true);			*/
			
			// popup removed, just return something at this stage
			ret = geos.get(0);
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

		if (textfieldHasFocus) return;

		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = 
			!app.isApplet() ||
			mode == EuclidianView.MODE_ZOOM_IN ||
			mode == EuclidianView.MODE_ZOOM_OUT ||
			(app.isShiftDragZoomEnabled() && 
					(e.isControlDown() || e.isMetaDown() || e.isShiftDown()));			
		if (!allowMouseWheel)
			return;

		setMouseLocation(e);

		//double px = view.width / 2d;
		//double py = view.height / 2d;
		double px = mouseLoc.x;
		double py = mouseLoc.y;
		//double dx = view.getXZero() - px;
		//double dy = view.getYZero() - py;

		double xFactor = 1;
		if (e.isAltDown())
			xFactor = 1.5;
		
		double reverse = app.isMouseWheelReversed() ? -1 : 1;


		double factor = (e.getWheelRotation() * reverse > 0) ?
				EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor:
					1d / (EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);


		// make zooming a little bit smoother by having some steps

		view.setAnimatedCoordSystem(
				//px + dx * factor,
				//py + dy * factor,
				px, py, factor,
				view.getXscale() * factor, 4, false);
		//view.yscale * factor);
		app.setUnsaved();
	}

	public void zoomInOut(KeyEvent event) {
		boolean allowZoom = 
			!app.isApplet() ||
			mode == EuclidianView.MODE_ZOOM_IN ||
			mode == EuclidianView.MODE_ZOOM_OUT ||
			(app.isShiftDragZoomEnabled());			
		if (!allowZoom)
			return;

		double px = mouseLoc.x;
		double py = mouseLoc.y;
		//double dx = view.getXZero() - px;
		//double dy = view.getYZero() - py;
		
		double factor = event.getKeyCode() == KeyEvent.VK_MINUS ? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (event.isAltDown())
			factor *= event.getKeyCode() == KeyEvent.VK_MINUS ? 2d/3d: 1.5;

		// make zooming a little bit smoother by having some steps
		view.setAnimatedCoordSystem(
				//px + dx * factor,
				//py + dy * factor,
				px,py,factor,
				view.getXscale() * factor, 4, false);
		//view.yscale * factor);
		app.setUnsaved();


	}

	/*
	 * when drawing a line, this is used when alt is down
	 * to set the angle to be a multiple of 15 degrees
	 */
	public void setLineEndPoint(Point2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}

	///////////////////////////////////////////
	// moved GeoElements



	/** return the current movedGeoPoint */
	public GeoElement getMovedGeoPoint(){
		return ((GeoElement) movedGeoPoint);
	}

	private boolean textfieldHasFocus = false;

	public void textfieldHasFocus(boolean hasFocus) {
		textfieldHasFocus = hasFocus;
	}







	///////////////////////////////////////////
	// EMPTY METHODS USED FOR EuclidianView3D


	/** right-press the mouse makes start 3D rotation */
	protected void processRightPressFor3D(){

	}


	/** right-drag the mouse makes 3D rotation 
	 * @return false */
	protected boolean processRotate3DView(){
		return false;
	}



	/** right-release the mouse makes stop 3D rotation 
	 * @return false */
	protected boolean processRightReleaseFor3D(){
		return false;
	}

	
	

	
	//******************************
	// PropertiesPanelMini Listeners
	//******************************
	int penLineStyle = 0;
	public void setLineStyle(int lineStyle) {
		penLineStyle = lineStyle;
		
		//if (mode == EuclidianView.MODE_VISUAL_STYLE) {
			ArrayList geos = app.getSelectedGeos();
			
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				geo.setLineType(lineStyle);
				geo.updateRepaint();
				
			}
	//	}
	}

		
	
	final private static int eraserSize = 12;
	private boolean erasing = false;
	private int penSize = 3;
	
	public void setSize(int size) {
		penSize = size;
		
	//	if (mode == EuclidianView.MODE_VISUAL_STYLE) {
			ArrayList geos = app.getSelectedGeos();
			
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				if (geo instanceof PointProperties) {
					((PointProperties)geo).setPointSize(size);
					geo.updateRepaint();
				} else {
					geo.setLineThickness(size);
					geo.updateRepaint();
				}
			}
	//	}
		
	}
	
	Color penColor = Color.black;
	public void setColor(Color color) {
		penColor = color;
		
//		if (mode == EuclidianView.MODE_VISUAL_STYLE) {
			ArrayList geos = app.getSelectedGeos();
			
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				geo.setObjColor(color);
				geo.updateRepaint();
			}
	//	}

	}
	
	
	public void setAlpha(float alpha) {	
			ArrayList geos = app.getSelectedGeos();		
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)geos.get(i);
				geo.setAlphaValue(alpha);
				geo.updateRepaint();
			}
	}
	
	
	private void openMiniPropertiesPanel() {
		if (!app.hasFullGui()) return;
		if (app.isMiniPropertiesActive())
			app.getGuiManager().toggleMiniProperties(true);

	}

	private void closeMiniPropertiesPanel() {
		if (!app.hasFullGui()) return;
		app.getGuiManager().toggleMiniProperties(false);

	}
	
	protected boolean moveMode(int mode) {
		if (mode == EuclidianView.MODE_MOVE ||
				mode == EuclidianView.MODE_VISUAL_STYLE)
			return true;
		
		else return false;
	}

}
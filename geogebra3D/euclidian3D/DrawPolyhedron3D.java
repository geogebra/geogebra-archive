package geogebra3D.euclidian3D;




import geogebra.Matrix.GgbVector;
import geogebra.euclidian.HandleAction;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.AlgoPolyhedron;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoElement3DInterface;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoPolyhedron;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;






/**
 * Class for drawing 3D polygons.
 * @author matthieu
 *
 */
public class DrawPolyhedron3D extends Drawable3DSurfaces implements Previewable, HandleAction {


	
	
	

	
	//drawing

	public void drawGeometry(Renderer renderer) {

	}
	
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	public int getType(){
		return DRAW_TYPE_CLOSED_SURFACES;
	}

	
	
	
	protected boolean updateForItSelf(){
		return true;
		
	}
	

	
	
	
	////////////////////////////////
	// Previewable interface 

	
	
	@SuppressWarnings("rawtypes")
	/** basis */
	private ArrayList selectedPolygons;
	
	private GeoPolygon basis;
	
	/** segments of the polygon preview */
	private ArrayList<DrawSegment3D> segments;
	
	@SuppressWarnings("rawtypes")
	private ArrayList<ArrayList> segmentsPoints;
	
	private AlgoPolyhedron algo;
	
	private GeoNumeric height;
	

	/**
	 * Constructor for previewable
	 * @param a_view3D
	 * @param selectedPolygon
	 */
	@SuppressWarnings("rawtypes")
	public DrawPolyhedron3D(EuclidianView3D a_view3D, ArrayList selectedPolygons){
		
		super(a_view3D);
		
		
		this.selectedPolygons = selectedPolygons;
		
		newHeight();

		updatePreview();
		
	}	

	


	private void newHeight(){
		height = new GeoNumeric(getView3D().getKernel().getConstruction(), 1);
	}






	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}

	
	private boolean algoShown = false;

	private void createAlgo(){
		algo = new AlgoPolyhedron(getView3D().getKernel().getConstruction(),
				null, basis, height, GeoPolyhedron.TYPE_PRISM);
		algo.removeOutputFromAlgebraView();
		algo.removeOutputFromPicking();
		algo.setOutputPointsEuclidianVisible(false);
		algo.notifyUpdateOutputPoints();
		
		
	}
	
	private void removeAlgo(){
		algo.remove();
		algoShown=false;
	}
	
	private void hideAlgo(){
		algo.setOutputSegmentsAndPolygonsEuclidianVisible(false);
		algo.notifyUpdateOutputSegmentsAndPolygons();
		algoShown=false;

		
			
	}


	public void updatePreview() {
		
		//Application.debug(selectedPolygons.size());
		
		if (selectedPolygons.size()==0){
			if (algo!=null && algoShown){
				hideAlgo();
				getView3D().setButtonsVisible(false);
			}
		}else if (selectedPolygons.size()==1){
			if (algo==null){
				basis = (GeoPolygon) selectedPolygons.get(0);
				createAlgo();
				updateMainDirection();
			}else if (basis!=selectedPolygons.get(0)){
				basis = (GeoPolygon) selectedPolygons.get(0);
				removeAlgo();
				createAlgo();
				updateMainDirection();
			}
			algo.setOutputSegmentsAndPolygonsEuclidianVisible(true);
			algo.notifyUpdateOutputSegmentsAndPolygons();
			algoShown=true;
			
			getView3D().setButtonsVisible(true);
			
			updateButtonsPosition();
			
				
		}
	}
	
	
	private void updateButtonsPosition(){
		
		getView3D().updateButtonsPosition(algo.getTopMiddlePoint(),getMainDirection());
	}
	
	
	public void disposePreview() {
		super.disposePreview();
		
		if (algo!=null)
			removeAlgo();
		
		getView3D().setButtonsVisible(false);
		
	}
	
	public boolean handleOK(){
		if (algoShown){
			//height.setLabel(null);
			Hits hits = new Hits();
			hits.add(height);
			((EuclidianController3D) getView3D().getEuclidianController()).rightPrism(hits);
			disposePreview();
			getView3D().getEuclidianController().clearSelections();
			algo=null;
			basis=null;
			newHeight();
			return true;
		}else
			return false;
	}
	
	
	public boolean handleCancel(){
		if (algoShown){
			hideAlgo();
			getView3D().getEuclidianController().clearSelections();
			getView3D().setButtonsVisible(false);
			return true;
		}else
			return false;
	}

	public boolean handleKey(KeyEvent event) {

		switch (event.getKeyCode()) {
		
		case KeyEvent.VK_RIGHT: //augment the height
		case KeyEvent.VK_UP:
		case KeyEvent.VK_PAGE_UP:
			if (algoShown){
				height.setValue(height.getValue()+0.1);
				height.updateCascade();
				updateButtonsPosition();
				return true;
			}
			return false;
			
		case KeyEvent.VK_LEFT: //reduce the height
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_PAGE_DOWN:
			if (algoShown){
				height.setValue(height.getValue()-0.1);
				height.updateCascade();
				updateButtonsPosition();
				return true;
			}
			return false;
			
			
		default:
			return false;
		}
	}

	
	private GgbVector startPos;
	private double startHeight;
	
	public void handleStartPosition(GgbVector pos){
		startPos = pos;
		startHeight = height.getValue();
	}
	
	
	public void handlePosition(GgbVector pos){
		//Application.debug(startPos.toString()+"\n--\n"+pos.toString());
		
		height.setValue(startHeight+pos.sub(startPos).dotproduct(getMainDirection()));
		height.updateCascade();
		updateButtonsPosition();
	}

	public GgbVector getMainDirection(){
		return mainDirection;
	}
	
	private GgbVector mainDirection;
	
	private void updateMainDirection(){
		mainDirection = basis.getMainDirection().normalized();
	}
}

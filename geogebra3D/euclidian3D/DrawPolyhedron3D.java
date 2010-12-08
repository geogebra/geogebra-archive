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
		
		//getView3D().setButtonHandleColor(ConstructionDefaults3D.colPolygon3D);

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
		
		//tells the handled top
		getView3D().setHandledDrawable(algo.getTopFace());
		
		
	}
	
	private void removeAlgo(){
		algo.remove();
		algo=null;
		algoShown=false;
		
		getView3D().removeHandledDrawable();
	}
	
	/**
	 * sets the height regarding view
	 */
	private void setHeight(){
		
		
		GgbVector o = getView3D().getToScreenMatrix().mul(bottomMiddlePoint);
		GgbVector v = getView3D().getToScreenMatrix().mul(getMainDirection());

		
		double[] minmax = 
			getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o,v, false);
		
		double m = Math.max(Math.abs(minmax[0]), Math.abs(minmax[1]));
		int exp = (int) Math.floor(Math.log(m) / Math.log(10));
		double h = Math.pow(10, exp);
		
		//the height has the same number of digits than minmax, e.g. if minmax=[-0.5,3.7], then h=1
		
		//Application.debug("minmax="+minmax[0]+", "+minmax[1]+"\nexp="+exp+", h="+h);
		
		if (mainDirection.dotproduct(getView3D().getViewDirection())<0)
			height.setValue(-h);
		else
			height.setValue(h);
	}
	

	public void updatePreview() {
		
		//Application.debug(selectedPolygons.size());
		
		if (selectedPolygons.size()==0){
			if (algo!=null){
				removeAlgo();
				getView3D().setButtonsVisible(false);
			}
		}else if (selectedPolygons.size()==1){


			if (algo==null){
				updateBasis((GeoPolygon) selectedPolygons.get(0));
				setHeight();
				createAlgo();
			}else if (basis!=selectedPolygons.get(0)){
				updateBasis((GeoPolygon) selectedPolygons.get(0));
				setHeight();
				removeAlgo();			
				createAlgo();
			}
			 

			algo.setOutputSegmentsAndPolygonsEuclidianVisible(true);
			algo.notifyUpdateOutputSegmentsAndPolygons();
			algoShown=true;

			getView3D().setButtonsVisible(true);

			updateButtonsPosition();

		
			/*
			basis = (GeoPolygon) selectedPolygons.get(0);

			updateMainDirection();
			setHeight();
			createAlgo();
			
			algo.setOutputSegmentsAndPolygonsEuclidianVisible(true);
			algo.notifyUpdateOutputSegmentsAndPolygons();
			algoShown=true;
			
			getView3D().setButtonsVisible(true);
			
			updateButtonsPosition();
			
				*/
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
			removeAlgo();
		
			Hits hits = new Hits();
			hits.add(height);
			((EuclidianController3D) getView3D().getEuclidianController()).rightPrism(hits);
			
			basis=null;
			newHeight();

			getView3D().getEuclidianController().clearSelections();
			
			disposePreview();
			return true;
		}else
			return false;
	}
	
	
	public boolean handleCancel(){
		
		boolean shown = algoShown;
		
		if (algo!=null)
			removeAlgo();
		
		if (shown){
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
	
	private GgbVector bottomMiddlePoint;
	
	private void updateBasis(GeoPolygon basis){
		this.basis = basis;
		mainDirection = basis.getMainDirection().normalized();
		
		
		
		// bottom middle points
		GgbVector ret = new GgbVector(4);
		for (int i=0; i<basis.getPointsLength(); i++)
			ret = ret.add(basis.getPointND(i).getCoordsInD(3));
		bottomMiddlePoint =  ret.mul((double) 1/basis.getPointsLength());
		
	}
}

package geogebra3D.euclidian3D;




import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPolygon;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.AlgoPolyhedron;
import geogebra3D.kernel3D.GeoPolyhedron;

import java.util.ArrayList;






/**
 * Class for drawing 3D polygons.
 * @author matthieu
 *
 */
public class DrawPolyhedron3D extends Drawable3DSurfaces implements Previewable{


	
	
	

	
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
		
		updatePreview();
		
		
		
	}	

	








	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}

	
	


	public void updatePreview() {
		
		//Application.debug(selectedPolygons.size()+", algo==null:"+(algo==null));
		
		if (selectedPolygons.size()==1 && algo==null){
				
		
			//create the height
			height = new GeoNumeric(getView3D().getKernel().getConstruction(), 0.0001);
			
			//create the algo
			algo = new AlgoPolyhedron(getView3D().getKernel().getConstruction(),
					null, 
					(GeoPolygon) selectedPolygons.get(0), 
					height, 
					GeoPolyhedron.TYPE_PRISM);
			algo.removeOutputFromAlgebraView();
			algo.removeOutputFromPicking();
			algo.setOutputPointsEuclidianVisible(false);
			algo.notifyUpdateOutputPoints();
			
			//sets the top face to be handled
			getView3D().getEuclidianController().setHandledGeo(algo.getTopFace());


			//ensure correct drawing of visible parts of the previewable
			algo.setOutputSegmentsAndPolygonsEuclidianVisible(true);
			algo.notifyUpdateOutputSegmentsAndPolygons();

		}
	}
	
	
	
	
	public void disposePreview() {
		super.disposePreview();
		
		getView3D().getEuclidianController().setHandledGeo(null);
		
		if (algo!=null){

			//remove the algo
			algo.remove();
			algo=null;
			
			//clear current selections : remove basis polygon from selections
			getView3D().getEuclidianController().clearSelections();
			
			//add current height to selected numeric (will be used on next EuclidianView3D::rightPrism() call)
			Hits hits = new Hits();hits.add(height);
			getView3D().getEuclidianController().addSelectedNumeric(hits, 1, false);

		}
	}
	
	
	

}

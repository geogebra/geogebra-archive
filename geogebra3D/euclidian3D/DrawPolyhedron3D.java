package geogebra3D.euclidian3D;




import geogebra.Matrix.GgbVector;
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

import java.util.ArrayList;
import java.util.Iterator;






/**
 * Class for drawing 3D polygons.
 * @author matthieu
 *
 */
public class DrawPolyhedron3D extends Drawable3DSurfaces implements Previewable {


	
	
	

	
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
		
		Kernel3D kernel = (Kernel3D) getView3D().getKernel();

		/*
		setGeoElement(new GeoPolyhedron(kernel.getConstruction()));
		
		getGeoElement().setObjColor(ConstructionDefaults3D.colPolygon3D);
		getGeoElement().setAlphaValue(ConstructionDefaults3D.DEFAULT_POLYGON3D_ALPHA);
		getGeoElement().setIsPickable(false);
		*/
		
		this.selectedPolygons = selectedPolygons;
		
		height = new GeoNumeric(kernel.getConstruction(), 1);
		
		/*
		segments = new ArrayList<DrawSegment3D>();
		segmentsPoints = new ArrayList<ArrayList>();
		*/

		updatePreview();
		
	}	

	
	








	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}


	private void createAlgo(){
		algo = new AlgoPolyhedron(getView3D().getKernel().getConstruction(),
				null, basis, height, GeoPolyhedron.TYPE_PRISM);
		algo.removeOutputFromAlgebraView();
		algo.removeOutputFromPicking();
		algo.setOutputPointsInvisible(false);
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updatePreview() {
		
		//Application.debug(selectedPolygons.size());
		
		if (selectedPolygons.size()==0){
			if (algo!=null){
				algo.getPolyhedron().setEuclidianVisible(false);
				basis.setEuclidianVisible(true);
				getView3D().setWaitForUpdate();
			}
		}else if (selectedPolygons.size()==1){
			if (algo==null){
				basis = (GeoPolygon) selectedPolygons.get(0);
				createAlgo();
			}else if (basis!=selectedPolygons.get(0)){
				basis = (GeoPolygon) selectedPolygons.get(0);
				algo.remove();
				createAlgo();
			}
			algo.getPolyhedron().setEuclidianVisible(true);
			
			getView3D().setWaitForUpdate();
				
		}
	}
	
	public void disposePreview() {
		super.disposePreview();
		
		if (algo!=null)
			algo.remove();
		
		/*
		// dispose segments
		for (Iterator<DrawSegment3D> s = segments.iterator(); s.hasNext();)
			s.next().disposePreview();
*/
		
	}


	

}

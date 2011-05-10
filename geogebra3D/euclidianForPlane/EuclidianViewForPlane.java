package geogebra3D.euclidianForPlane;

import java.awt.geom.AffineTransform;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.kernel3D.GeoPlane3D;


/**
 * 2D view for plane.
 * 
 * @author matthieu
 *
 */
public class EuclidianViewForPlane extends EuclidianView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeoCoordSys2D plane;

	/**
	 * 
	 * @param ec
	 * @param plane 
	 */
	public EuclidianViewForPlane(EuclidianController ec, GeoCoordSys2D plane) {
		super(ec, new boolean[]{ true, true }, true);
		
		this.plane = plane;
	}
	
	
	public boolean isVisibleInThisView(GeoElement geo){

		// prevent not implemented type to be displayed (TODO remove)
		switch (geo.getGeoClassType()){
		case GeoElement.GEO_CLASS_POINT:
		case GeoElement.GEO_CLASS_POINT3D:
		case GeoElement.GEO_CLASS_SEGMENT:
		case GeoElement.GEO_CLASS_SEGMENT3D:
		case GeoElement.GEO_CLASS_LINE:
		case GeoElement.GEO_CLASS_LINE3D:
		case GeoElement.GEO_CLASS_RAY:
		case GeoElement.GEO_CLASS_RAY3D:
		case GeoElement.GEO_CLASS_POLYGON:
		case GeoElement.GEO_CLASS_POLYGON3D:
		case GeoElement.GEO_CLASS_CONIC:
		case GeoElement.GEO_CLASS_CONIC3D:
			break;
		default:
			return false;
		}
		
		return geo.isVisibleInView3D();
	}
	
	public void attachView() {
		kernel.attach(this);
	}


	/**
	 * add all existing geos to this view
	 */
	public void addExistingGeos(){

		kernel.notifyAddAll(this);
	}
	
	
	
	
	
	
	
	public Coords getCoordsForView(Coords coords){
		return coords.projectPlane(getPlaneMatrix())[1];
	}
	
	public CoordMatrix getPlaneMatrix(){
		return plane.getCoordSys().getMatrixOrthonormal();
		//return plane.getCoordSys().getDrawingMatrix();
	}

	public AffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){

		//use already computed for this view middlepoint M and eigen vecs ev
		AffineTransform transform = new AffineTransform();			
		transform.setTransform(
				ev[0].getX(),
				ev[0].getY(),
				ev[1].getX(),
				ev[1].getY(),
				M.getX(),
				M.getY());

		return transform;
	}
	
	public String getFromPlaneString(){
		return ((GeoElement) plane).getLabel();
	}

	public String getTranslatedFromPlaneString(){
		if (plane instanceof GeoPlaneND)
			return app.getPlain("PlaneA",((GeoElement) plane).getLabel());
		else
			return app.getPlain("PlaneFromA",((GeoElement) plane).getLabel());
	}
}

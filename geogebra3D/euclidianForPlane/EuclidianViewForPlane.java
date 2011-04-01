package geogebra3D.euclidianForPlane;

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
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
	
	
	
	

	public boolean contains(GeoElement geo){
		switch(geo.getGeoClassType()){
		case GeoElement.GEO_CLASS_POINT:
		case GeoElement.GEO_CLASS_POINT3D:
			return plane.isInRegion((GeoPointND) geo);
		default:
			return false;
		}
	}
	
	
	public void getInhomCoords(GeoPointND P, double[] ret){
		CoordSys coordSys = plane.getCoordSys();
		Coords coords = coordSys.getNormalProjectionForDrawing(P.getInhomCoordsInD(3))[1];
		for (int i=0; i<ret.length; i++)
    		ret[i]=coords.get(i+1);
	}
	
}
